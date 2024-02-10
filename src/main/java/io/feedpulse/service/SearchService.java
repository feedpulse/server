package io.feedpulse.service;

import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.FeedDTO;
import io.feedpulse.dto.response.PageableDTO;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final EntryService entryService;
    private final FeedService feedService;

    public SearchService(EntryService entryService, FeedService feedService) {
        this.entryService = entryService;
        this.feedService = feedService;
    }

    public PageableDTO<EntryDTO> searchEntries(String searchString, Integer size, Integer page, Boolean sortOrder) {
        return entryService.searchEntries(searchString, size, page, sortOrder);
    }

    public PageableDTO<EntryDTO> searchFeedEntries(String feedId, String searchString, Integer size, Integer page, Boolean sortOrder) {
        return entryService.searchFeedEntries(feedId, searchString, size, page, sortOrder);
    }

    public PageableDTO<EntryDTO> searchBookmarkedEntries(String searchString, Integer size, Integer page, Boolean sortOrder) {
        return entryService.searchBookmarkedEntries(searchString, size, page, sortOrder);
    }

    public PageableDTO<EntryDTO> searchFavoriteEntries(String searchString, Integer size, Integer page, Boolean sortOrder) {
        return entryService.searchFavoriteEntries(searchString, size, page, sortOrder);
    }

    public PageableDTO<FeedDTO> searchFeeds(String searchString, Integer size, Integer page, Boolean sortOrder) {
        return feedService.searchFeeds(searchString, size, page, sortOrder);
    }


}
