package io.feedpulse.controller;


import io.feedpulse.exceptions.MalformedFeedException;
import io.feedpulse.model.Entry;
import io.feedpulse.model.Feed;
import io.feedpulse.service.EntryService;
import io.feedpulse.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feeds")
@ResponseStatus(HttpStatus.OK)
public class FeedController {

    private final FeedService feedService;
    private final EntryService entryService;

    public FeedController(FeedService feedService, EntryService entryService) {
        this.feedService = feedService;
        this.entryService = entryService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<Feed> getFeed(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder
    ) {
        return feedService.getFeeds(limit, offset, sortOrder);
    }

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Feed getFeed(@PathVariable String uuid) {
        return feedService.getFeed(uuid);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{uuid}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Entry> getFeedEntries(@PathVariable String uuid,
                                      @RequestParam(defaultValue = "20") Integer limit,
                                      @RequestParam(defaultValue = "0") Integer offset,
                                      @RequestParam(required = false, defaultValue = "true") Boolean sortOrder) {
        return entryService.getEntries(uuid, limit, offset, sortOrder);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Feed addFeed(@RequestParam String feedUrl) throws MalformedFeedException {
        Feed feed = feedService.addFeed(feedUrl);
        return feed;
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeed(@PathVariable String uuid) {
        feedService.deleteFeedForUser(uuid);
    }
}