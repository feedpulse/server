package io.feedpulse.controller;

import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.FeedDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.service.SearchService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping("/entries")
    public PageableDTO<EntryDTO> search(
            @RequestParam() String searchString,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder) {
        return searchService.searchEntries(searchString, size, page, sortOrder);
    }

    @RequestMapping("/feeds")
    public PageableDTO<FeedDTO> searchFeeds(
            @RequestParam() String searchString,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder) {
        return searchService.searchFeeds(searchString, size, page, sortOrder);
    }

    @RequestMapping("/feeds/{feedId}")
    public PageableDTO<EntryDTO> searchFeed(
            @PathVariable String feedId,
            @RequestParam() String searchString,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder) {
        return searchService.searchFeedEntries(feedId, searchString, size, page, sortOrder);
    }

    @RequestMapping("/bookmarks")
    public PageableDTO<EntryDTO> bookmarksSearch(
            @RequestParam() String searchString,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder) {
        return searchService.searchBookmarkedEntries(searchString, size, page, sortOrder);
    }

    @RequestMapping("/favorites")
    public PageableDTO<EntryDTO> favoritesSearch(
            @RequestParam() String searchString,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder) {
        return searchService.searchFavoriteEntries(searchString, size, page, sortOrder);
    }

}
