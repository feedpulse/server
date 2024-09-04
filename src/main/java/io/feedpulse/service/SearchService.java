package io.feedpulse.service;

import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.FeedWithoutEntriesDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.model.SpringUserDetails;
import org.springframework.data.domain.Pageable;
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

    public PageableDTO<EntryDTO> searchEntries(String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        return entryService.searchEntries(searchString, pageRequest, userDetails);
    }

    public PageableDTO<EntryDTO> searchFeedEntries(String feedId, String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        return entryService.searchFeedEntries(feedId, searchString, pageRequest, userDetails);
    }

    public PageableDTO<EntryDTO> searchBookmarkedEntries(String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        return entryService.searchBookmarkedEntries(searchString, pageRequest, userDetails);
    }

    public PageableDTO<EntryDTO> searchFavoriteEntries(String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        return entryService.searchFavoriteEntries(searchString, pageRequest, userDetails);
    }

    public PageableDTO<FeedWithoutEntriesDTO> searchFeeds(String searchString, Pageable pageRequest, @AuthenticationPrincipal SpringUserDetails userDetails) {
        return feedService.searchFeeds(searchString, pageRequest, userDetails);
    }


}
