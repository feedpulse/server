package io.feedpulse.controller;


import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.FeedWithEntriesDTO;
import io.feedpulse.dto.response.FeedWithoutEntriesDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.service.EntryService;
import io.feedpulse.service.FeedService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public PageableDTO<FeedWithoutEntriesDTO> getFeed(
            @PageableDefault(sort = {"pubDate"}, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        return feedService.getFeeds(pageable, userDetails);
    }

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    // TODO: maybe without entries?
    public FeedWithEntriesDTO getFeed(@PathVariable String uuid, @AuthenticationPrincipal SpringUserDetails userDetails) {
        System.out.println("getFeed");
        return feedService.getFeed(uuid, userDetails);
    }

    // batch edit
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{uuid}/read", method = {RequestMethod.PATCH})
    public void readFeedEntries(@PathVariable String uuid, @AuthenticationPrincipal SpringUserDetails springUserDetails) {
        feedService.setReadFeedEntries(uuid, springUserDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{uuid}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageableDTO<EntryDTO> getFeedEntries(
            @PathVariable String uuid,
            @RequestParam(required = false, defaultValue = "false") boolean onlyUnread,
            @PageableDefault(sort = {"pubDate"}, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails) {
        return entryService.getFeedEntries(uuid, onlyUnread, pageable, userDetails);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public FeedWithoutEntriesDTO addFeed(@RequestParam String feedUrl, @AuthenticationPrincipal SpringUserDetails userDetails) {
        return feedService.addFeed(feedUrl, userDetails);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeed(@PathVariable String uuid, @AuthenticationPrincipal SpringUserDetails userDetails) {
        feedService.deleteFeedForUser(uuid, userDetails);
    }

    @GetMapping(value = "/validate-feed-url", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void validateUrl(@RequestParam String feedUrl) {
        // exception will be thrown if the url is not valid
        // so see the response status code
        feedService.validateUrl(feedUrl);
    }
}
