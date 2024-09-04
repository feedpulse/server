package io.feedpulse.service;


import com.rometools.rome.feed.synd.SyndFeed;
import io.feedpulse.dto.response.FeedWithEntriesDTO;
import io.feedpulse.dto.response.FeedWithoutEntriesDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.exceptions.BaseException;
import io.feedpulse.exceptions.common.InvalidUuidException;
import io.feedpulse.exceptions.entity.FeedNotFoundException;
import io.feedpulse.exceptions.parsing.MissingFeedEntriesException;
import io.feedpulse.model.Feed;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.model.User;
import io.feedpulse.repository.FeedRepository;
import io.feedpulse.validation.UuidValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserService userService;
    private final FeedFetchService feedFetchService;
    private final UserEntryInteractionService userEntryInteractionService;
    private final PagedResourcesAssembler<Feed> pagedResourcesAssembler;


    public FeedService(FeedRepository feedRepository, UserService userService, FeedFetchService feedFetchService, UserEntryInteractionService userEntryInteractionService, PagedResourcesAssembler<Feed> pagedResourcesAssembler) {
        this.feedRepository = feedRepository;
        this.userService = userService;
        this.feedFetchService = feedFetchService;
        this.userEntryInteractionService = userEntryInteractionService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
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

    private Integer getUnreadFeedEntries(@Valid String uuidString, SpringUserDetails userDetails) {
        if (!UuidValidator.isValid(uuidString)) throw new InvalidUuidException(uuidString);
        UUID feedUuid = UUID.fromString(uuidString);
        Integer unreadCount = feedRepository.countUnreadFeedEntries(userDetails.getUuid(), feedUuid);
        System.out.println("\n\n\n\n\n");
        System.out.println("Unread count: " + unreadCount);
        return unreadCount;
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
