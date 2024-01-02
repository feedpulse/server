package dev.feder.service;


import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import dev.feder.exceptions.InvalidUuidException;
import dev.feder.exceptions.MalformedFeedException;
import dev.feder.exceptions.NoSuchFeedException;
import dev.feder.model.Feed;
import dev.feder.repository.FeedRepository;
import dev.feder.util.FeedUtil;
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
    private final EntryService entryService;

    public FeedService(FeedRepository feedRepository, EntryService entryService) {
        this.feedRepository = feedRepository;
        this.entryService = entryService;
    }

    // WARNING: this method is only for internal use and should not be exposed to the frontend
    public List<Feed> getFeeds() {
        return feedRepository.findAll();
    }

    public List<Feed> getFeeds(Integer limit, Integer offset, Boolean sortOrder) {
        var sort = sortOrder ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, limit, sort, "pubDate");
        return feedRepository.findAll(pageable).toList();
    }

    public Feed getFeed(String uuidString) throws InvalidUuidException, NoSuchFeedException{
        UUID uuid = UuidUtil.fromString(uuidString);
        Optional<Feed> feed = feedRepository.findById(uuid);
        if (feed.isEmpty()) {
            throw new NoSuchFeedException(uuidString);
        }
        return feed.get();
    }

    public UUID addFeed(String feedUrl) throws MalformedFeedException {
        Optional<Feed> existingFeed = feedRepository.findByFeedUrl(feedUrl);
        if (existingFeed.isPresent()) return existingFeed.get().getUuid();
        Feed.FeedBuilder feedBuilder = new Feed.FeedBuilder();
        try {
            SyndFeed syndFeed = FeedUtil.fetchFeed(feedUrl);
            feedBuilder
                    .setTitle(syndFeed.getTitle())
                    .setDescription(syndFeed.getDescription())
                    .setLink(syndFeed.getLink())
                    .setAuthor(syndFeed.getAuthor())
                    .setPubDate(syndFeed.getPublishedDate());
        } catch (FeedException | MalformedURLException e) {
            throw new MalformedFeedException(feedUrl);
        }
        feedBuilder.setFeedUrl(feedUrl);
        Feed feed = feedBuilder.createFeed();
        feed = feedRepository.save(feed);
        return feed.getUuid();
    }

    public void deleteFeed(Feed feed) {
        feedRepository.delete(feed);
    }


}
