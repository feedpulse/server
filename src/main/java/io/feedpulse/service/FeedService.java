package io.feedpulse.service;


import com.rometools.rome.feed.synd.SyndFeed;
import io.feedpulse.dto.response.FeedDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.exceptions.*;
import io.feedpulse.model.Feed;
import io.feedpulse.model.User;
import io.feedpulse.repository.FeedRepository;
import io.feedpulse.util.UuidUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserService userService;
    private final EntryService entryService;
    private final FeedFetchService feedFetchService;
    private final UserEntryInteractionService userEntryInteractionService;

    private final PagedResourcesAssembler<Feed> pagedResourcesAssembler;


    public FeedService(FeedRepository feedRepository, EntryService entryService, UserService userService, FeedFetchService feedFetchService, UserEntryInteractionService userEntryInteractionService, PagedResourcesAssembler<Feed> pagedResourcesAssembler) {
        this.feedRepository = feedRepository;
        this.entryService = entryService;
        this.userService = userService;
        this.feedFetchService = feedFetchService;
        this.userEntryInteractionService = userEntryInteractionService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    // WARNING: this method is only for internal use and should not be exposed to the frontend
    public List<Feed> getFeeds() {
        return feedRepository.findAll();
    }

    private Page<Feed> getFeedsAsPage(Integer size, Integer page, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return feedRepository.findFeedsByUsersId(user.getId(), pageable);
    }

    private PagedModel<EntityModel<Feed>> getFeedsAsPagedModel(Integer size, Integer page, Boolean sortOrder) {
        Page<Feed> feeds = getFeedsAsPage(size, page, sortOrder);
        PagedModel<EntityModel<Feed>> pagedModel = pagedResourcesAssembler.toModel(feeds);
        return pagedModel;
    }

    public PageableDTO<FeedDTO> getFeeds(Integer size, Integer page, Boolean sortOrder) {
        PagedModel<EntityModel<Feed>> pagedModel = getFeedsAsPagedModel(size, page, sortOrder);
        List<FeedDTO> feedDTOs = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(FeedDTO::of)
                .toList();
        return PageableDTO.of(pagedModel, feedDTOs);
    }

    public Feed getFeed(String uuidString) throws InvalidUuidException, NoSuchFeedException {
        UUID uuid = UuidUtil.fromString(uuidString);
        User user = userService.getCurrentUser();
        Optional<Feed> feed = feedRepository.findFeedByUuidAndUsersId(uuid, user.getId());
        if (feed.isEmpty()) {
            throw new NoSuchFeedException(uuidString);
        }
        return feed.get();
    }

    public FeedDTO addFeed(String feedUrl) throws MalformedFeedException {
        User user = userService.getCurrentUser();
        Optional<Feed> existingFeed = feedRepository.findByFeedUrl(feedUrl);
        if (existingFeed.isPresent()) {
            // if the feed already exists, add it to the user's feeds
            if (!user.getFeeds().contains(existingFeed.get())) {
                // if the user does not already have the feed, add it
                user.getFeeds().add(existingFeed.get());
            }
            userService.saveUser(user);
            return FeedDTO.of(existingFeed.get());
        }

        Feed.FeedBuilder feedBuilder = new Feed.FeedBuilder();
        try {
            SyndFeed syndFeed = feedFetchService.fetchFeed(feedUrl);
            feedBuilder
                    .setTitle(syndFeed.getTitle())
                    .setDescription(syndFeed.getDescription())
                    .setLink(syndFeed.getLink())
                    .setAuthor(syndFeed.getAuthor())
                    .setPubDate(syndFeed.getPublishedDate());
        } catch (Exception e) {
            e.printStackTrace();
//            throw new MalformedFeedException(feedUrl);
        }
        feedBuilder.setFeedUrl(feedUrl);
        Feed feed = feedBuilder.createFeed();
        feed = feedRepository.save(feed);
        user.getFeeds().add(feed);
        userService.saveUser(user);
        feedFetchService.fetchFeed(feed);
        return FeedDTO.of(feed);
    }

    @Transactional
    public void deleteFeedForUser(String uuid) {
        User user = userService.getCurrentUser();
        Optional<Feed> feed = feedRepository.findFeedByUuidAndUsersId(UuidUtil.fromString(uuid), user.getId());
        if (feed.isEmpty()) {
            throw new NoSuchFeedException(uuid);
        }
        // delete all user entry interactions for the feed
        userEntryInteractionService.deleteAllUserEntryInteractionsForFeed(user, feed.get());
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
     * @throws RomeFeedParseException If an error occurs while parsing or generating the feed.
     * @throws NoFeedEntriesFoundException If no feed entries are found.
     * @throws HtmlNotParsableException If there is an error while parsing the HTML content of the feed entry.
     */
    public void validateUrl(String feedUrl) throws RomeFeedParseException, NoFeedEntriesFoundException, HtmlNotParsableException {
        SyndFeed syndFeed= feedFetchService.fetchFeed(feedUrl);
        if (syndFeed.getEntries().isEmpty()) {
            throw new NoFeedEntriesFoundException(feedUrl);
        }
        feedFetchService.parsePageContent(syndFeed.getEntries().get(0));
    }
}
