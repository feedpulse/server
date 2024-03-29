package io.feedpulse.controller;


import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.FeedDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.model.Feed;
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
    public PageableDTO<FeedDTO> getFeed(
            @PageableDefault(sort = {"pubDate"}, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        return feedService.getFeeds(pageable, userDetails);
    }

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Feed getFeed(@PathVariable String uuid, @AuthenticationPrincipal SpringUserDetails userDetails) {
        return feedService.getFeed(uuid, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{uuid}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageableDTO<EntryDTO> getFeedEntries(
            @PathVariable String uuid,
            @PageableDefault(sort = {"pubDate"}, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails) {
        return entryService.getFeedEntries(uuid, pageable, userDetails);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public FeedDTO addFeed(@RequestParam String feedUrl, @AuthenticationPrincipal SpringUserDetails userDetails) {
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
