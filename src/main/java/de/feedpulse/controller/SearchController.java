package de.feedpulse.controller;

import de.feedpulse.dto.response.EntryDTO;
import de.feedpulse.dto.response.FeedWithoutEntriesDTO;
import de.feedpulse.dto.response.PageableDTO;
import de.feedpulse.model.SpringUserDetails;
import de.feedpulse.service.SearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @PageableDefault(sort = {"pubDate"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails) {
        return searchService.searchEntries(searchString, pageable, userDetails);
    }

    @RequestMapping("/feeds")
    public PageableDTO<FeedWithoutEntriesDTO> searchFeeds(
            @RequestParam() String searchString,
            @PageableDefault(sort = {"pubDate"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails) {
        return searchService.searchFeeds(searchString, pageable, userDetails);
    }

    @RequestMapping("/feeds/{feedId}")
    public PageableDTO<EntryDTO> searchFeed(
            @PathVariable String feedId,
            @RequestParam() String searchString,
            @PageableDefault(sort = {"pubDate"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails) {
        return searchService.searchFeedEntries(feedId, searchString, pageable, userDetails);
    }

    @RequestMapping("/bookmarks")
    public PageableDTO<EntryDTO> bookmarksSearch(
            @RequestParam() String searchString,
            @PageableDefault(sort = {"pubDate"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails) {
        return searchService.searchBookmarkedEntries(searchString, pageable, userDetails);
    }

    @RequestMapping("/favorites")
    public PageableDTO<EntryDTO> favoritesSearch(
            @RequestParam() String searchString,
            @PageableDefault(sort = {"pubDate"}, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails) {
        return searchService.searchFavoriteEntries(searchString, pageable, userDetails);
    }

}
