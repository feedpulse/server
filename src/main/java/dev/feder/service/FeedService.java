package dev.feder.service;


import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import dev.feder.exceptions.InvalidUuidException;
import dev.feder.exceptions.MalformedFeedException;
import dev.feder.exceptions.NoSuchFeedException;
import dev.feder.model.Feed;
import dev.feder.model.User;
import dev.feder.repository.FeedRepository;
import dev.feder.util.FetchUtil;
import dev.feder.util.UuidUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserService userService;
    private final EntryService entryService;
    private final FeedFetchService feedFetchService;

    public FeedService(FeedRepository feedRepository, EntryService entryService, UserService userService , FeedFetchService feedFetchService) {
        this.feedRepository = feedRepository;
        this.entryService = entryService;
        this.userService = userService;
        this.feedFetchService = feedFetchService;
    }

    // WARNING: this method is only for internal use and should not be exposed to the frontend
    public List<Feed> getFeeds() {
        return feedRepository.findAll();
    }

    public List<Feed> getFeeds(Integer limit, Integer offset, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var sort = sortOrder ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, limit, sort, "pubDate");
        return feedRepository.findFeedsByUsersId(user.getId(), pageable).toList();
    }

    public Feed getFeed(String uuidString) throws InvalidUuidException, NoSuchFeedException{
        UUID uuid = UuidUtil.fromString(uuidString);
        User user = userService.getCurrentUser();
        Optional<Feed> feed = feedRepository.findFeedByUuidAndUsersId(uuid, user.getId());
        if (feed.isEmpty()) {
            throw new NoSuchFeedException(uuidString);
        }
        return feed.get();
    }

    public Feed addFeed(String feedUrl) throws MalformedFeedException {
        User user = userService.getCurrentUser();
        Optional<Feed> existingFeed = feedRepository.findByFeedUrl(feedUrl);
        if (existingFeed.isPresent()) {
            // if the feed already exists, add it to the user's feeds
            if (!user.getFeeds().contains(existingFeed.get())) {
                // if the user does not already have the feed, add it
                user.getFeeds().add(existingFeed.get());
            }
            userService.saveUser(user);
            return existingFeed.get();
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
        return feed;
    }

    public void deleteFeed(Feed feed) {
        feedRepository.delete(feed);
    }


}
