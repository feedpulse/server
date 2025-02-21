package de.feedpulse.service;


import com.rometools.rome.feed.synd.SyndFeed;
import de.feedpulse.dto.response.FeedWithEntriesDTO;
import de.feedpulse.dto.response.FeedWithoutEntriesDTO;
import de.feedpulse.dto.response.PageableDTO;
import de.feedpulse.exceptions.BaseException;
import de.feedpulse.exceptions.common.InvalidUuidException;
import de.feedpulse.exceptions.entity.FeedNotFoundException;
import de.feedpulse.exceptions.parsing.MissingFeedEntriesException;
import de.feedpulse.model.*;
import de.feedpulse.repository.EntryRepository;
import de.feedpulse.repository.FeedRepository;
import de.feedpulse.repository.UserEntryInteractionRepository;
import de.feedpulse.validation.UuidValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserService userService;
    private final FeedFetchService feedFetchService;
    private final UserEntryInteractionService userEntryInteractionService;
    private final PagedResourcesAssembler<Feed> pagedResourcesAssembler;
    private final EntryRepository entryRepository;
    private final UserEntryInteractionRepository userEntryInteractionRepository;


    public FeedService(FeedRepository feedRepository, UserService userService, FeedFetchService feedFetchService, UserEntryInteractionService userEntryInteractionService, PagedResourcesAssembler<Feed> pagedResourcesAssembler, EntryRepository entryRepository, UserEntryInteractionRepository userEntryInteractionRepository) {
        this.feedRepository = feedRepository;
        this.userService = userService;
        this.feedFetchService = feedFetchService;
        this.userEntryInteractionService = userEntryInteractionService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.entryRepository = entryRepository;
        this.userEntryInteractionRepository = userEntryInteractionRepository;
    }

    // WARNING: this method is only for internal use and should not be exposed to the frontend
    public List<Feed> getFeeds() {
        return feedRepository.findAll();
    }


    public PageableDTO<FeedWithoutEntriesDTO> getFeeds(Pageable pageRequest, SpringUserDetails springUserDetails) {
        Page<Feed> feeds = feedRepository.findFeedsByUsersUuid(springUserDetails.getUuid(), pageRequest);
        PagedModel<EntityModel<Feed>> pagedModel = pagedResourcesAssembler.toModel(feeds);
        List<FeedWithoutEntriesDTO> feedWithoutEntriesDTOS = convertToFeedDTO(pagedModel, springUserDetails);
        return PageableDTO.of(pagedModel, feedWithoutEntriesDTOS);
    }

    public FeedWithEntriesDTO getFeed(String uuidString, SpringUserDetails userDetails) {
        if (!UuidValidator.isValid(uuidString)) throw new InvalidUuidException(uuidString);
        UUID uuid = UUID.fromString(uuidString);
        Optional<Feed> feed = feedRepository.findFeedByUuidAndUsersUuid(uuid, userDetails.getUuid());
        if (feed.isEmpty()) {
            throw new FeedNotFoundException(uuidString);
        }
        Integer unreadCount = getUnreadFeedEntries(uuidString, userDetails);

        return FeedWithEntriesDTO.of(feed.get(), unreadCount);
    }

    private Integer getUnreadFeedEntries(String uuidString, SpringUserDetails userDetails) {
        if (!UuidValidator.isValid(uuidString)) throw new InvalidUuidException(uuidString);
        UUID feedUuid = UUID.fromString(uuidString);
        Integer unreadCount = feedRepository.countUnreadFeedEntries(userDetails.getUuid(), feedUuid);
        return unreadCount;
    }

    @Transactional(noRollbackFor = BaseException.class)
    public void setReadFeedEntries(String uuidString, SpringUserDetails userDetails) {
        if (!UuidValidator.isValid(uuidString)) throw new InvalidUuidException(uuidString);
        UUID feedUuid = UUID.fromString(uuidString);
        PageRequest pageRequest = PageRequest.of(0, 100);
        Page<UUID> feedEntriesUuid =  feedRepository.getUuidOfUnreadFeedEntries(userDetails.getUuid(), feedUuid, pageRequest);

        /// Base condition: loop until there are no more pages to process
        while (feedEntriesUuid != null && !feedEntriesUuid.isEmpty()) {
            Set<UserEntryInteraction> ueiList = new HashSet<>();

            // Iterate over the current page of UUIDs
            for (UUID uuid : feedEntriesUuid) {
                Entry entry = entryRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("Entry not found"));
                UserEntryInteraction uei = new UserEntryInteraction(userService.getUserByUuid(userDetails.getUuid()), entry);
                uei.setRead(true);
                ueiList.add(uei);
            }

            /**
             * Check if there are more pages to process
             * If the check is done after saving the interactions, the pagination will fail and the loop will terminate prematurely
             */
            boolean hasNextPage = feedEntriesUuid.hasNext();

            // Save collected interactions in the current batch
            if (!ueiList.isEmpty()) {
                userEntryInteractionRepository.saveAll(ueiList);
            }

            // Fetch the next page of unread entries
            if (hasNextPage) {
                feedEntriesUuid = feedRepository.getUuidOfUnreadFeedEntries(userDetails.getUuid(), feedUuid, pageRequest);
                feedEntriesUuid.forEach(System.out::println);
            } else {
                // If no next page, terminate the loop
                break;
            }
        }
    }

    @Transactional(noRollbackFor = BaseException.class)
    public FeedWithoutEntriesDTO addFeed(String feedUrl, SpringUserDetails userDetails) {
        User user = userService.getUserByUuid(userDetails.getUuid());
        Optional<Feed> existingFeed = feedRepository.findByFeedUrl(feedUrl);
        if (existingFeed.isPresent()) {
            // if the feed already exists, add it to the user's feeds
            if (!user.getFeeds().contains(existingFeed.get())) {
                // if the user does not already have the feed, add it
                user.getFeeds().add(existingFeed.get());
            }
            userService.saveUser(user);
            return FeedWithoutEntriesDTO.of(existingFeed.get(), getUnreadFeedEntries(existingFeed.get().getUuid().toString(), userDetails));
        }

        Feed.FeedBuilder feedBuilder = Feed.builder();
        try {
            SyndFeed syndFeed = feedFetchService.fetchFeed(feedUrl);
            feedBuilder
                    .title(syndFeed.getTitle())
                    .description(syndFeed.getDescription())
                    .link(syndFeed.getLink())
                    .author(syndFeed.getAuthor())
                    .pubDate(syndFeed.getPublishedDate());
        } catch (Exception e) {
            e.printStackTrace();
//            throw new MalformedFeedException(feedUrl);
        }
        feedBuilder.feedUrl(feedUrl);
        Feed feed = feedBuilder.build();
        feed = feedRepository.save(feed);
        user.getFeeds().add(feed);
        userService.saveUser(user);
        feedFetchService.fetchFeed(feed);
        return FeedWithoutEntriesDTO.of(feed, 0);
    }

    @Transactional(noRollbackFor = BaseException.class)
    public void deleteFeedForUser(String uuid, SpringUserDetails userDetails) {
        User user = userService.getUserByUuid(userDetails.getUuid());
        if (!UuidValidator.isValid(uuid)) throw new InvalidUuidException(uuid);
        Optional<Feed> feed = feedRepository.findFeedByUuidAndUsersUuid(UUID.fromString(uuid), userDetails.getUuid());
        if (feed.isEmpty()) {
            throw new FeedNotFoundException(uuid);
        }
        // delete all user entry interactions for the feed
        userEntryInteractionService.deleteAllUserEntryInteractionsForFeed(user.getId(), feed.get());
        // remove the feed from the user's feeds
        user.getFeeds().remove(feed.get());
        userService.saveUser(user);

        /**
         * Should we delete the feed from the database if no users have it?
         */
    }

    public void deleteFeedForUser(Feed feed) {
        feedRepository.delete(feed);
    }

    /**
     * Validates a given feed URL.
     *
     * @param feedUrl The URL of the feed to be validated.
     */
    public void validateUrl(String feedUrl) {
        SyndFeed syndFeed = feedFetchService.fetchFeed(feedUrl);
        if (syndFeed.getEntries().isEmpty()) {
            throw new MissingFeedEntriesException(feedUrl);
        }
        feedFetchService.parsePageContent(syndFeed.getEntries().get(0));
    }

    public PageableDTO<FeedWithoutEntriesDTO> searchFeeds(String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Feed> feedList = feedRepository.searchFeedsByUserUuid(userDetails.getUuid(), searchString, pageRequest);
        PagedModel<EntityModel<Feed>> pagedModel = pagedResourcesAssembler.toModel(feedList);
        List<FeedWithoutEntriesDTO> feedWithoutEntriesDTOList = convertToFeedDTO(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, feedWithoutEntriesDTOList);
    }

    private List<FeedWithoutEntriesDTO> convertToFeedDTO(PagedModel<EntityModel<Feed>> pagedModel, SpringUserDetails userDetails) {
        return pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(feed -> FeedWithoutEntriesDTO.of(feed, getUnreadFeedEntries(feed.getUuid().toString(), userDetails)))
                .toList();
    }

}
