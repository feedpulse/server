package io.feedpulse.service;

import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.FeedDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.model.SpringUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final EntryService entryService;
    private final FeedService feedService;

    public SearchService(EntryService entryService, FeedService feedService) {
        this.entryService = entryService;
        this.feedService = feedService;
    }

    public PageableDTO<EntryDTO> searchEntries(String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        return entryService.searchEntries(searchString, size, page, sortOrder, userDetails);
    }

    public PageableDTO<EntryDTO> searchFeedEntries(String feedId, String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        return entryService.searchFeedEntries(feedId, searchString, size, page, sortOrder, userDetails);
    }

    public PageableDTO<EntryDTO> searchBookmarkedEntries(String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        return entryService.searchBookmarkedEntries(searchString, size, page, sortOrder, userDetails);
    }

    public PageableDTO<EntryDTO> searchFavoriteEntries(String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        return entryService.searchFavoriteEntries(searchString, size, page, sortOrder, userDetails);
    }

    public PageableDTO<FeedDTO> searchFeeds(String searchString, Integer size, Integer page, Boolean sortOrder, @AuthenticationPrincipal SpringUserDetails userDetails) {
        return feedService.searchFeeds(searchString, size, page, sortOrder, userDetails);
    }


}
