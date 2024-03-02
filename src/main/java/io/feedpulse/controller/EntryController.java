package io.feedpulse.controller;

import io.feedpulse.dto.request.EntryInteractionUpdateDTO;
import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.service.EntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/entries", produces = "application/json")
public class EntryController{

    private static final Logger log = LoggerFactory.getLogger(EntryController.class);

    private final EntryService entryService;

    public EntryController(@NonNull EntryService entryService) {
        this.entryService = entryService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PageableDTO<EntryDTO> getEntries(
            // TODO(V1): re-set sort to pubDate in the service layer, so that the user can sort by other fields
            @PageableDefault(sort = {"pubDate"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        return entryService.getFeedEntries(pageable, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{uuid}")
    public EntryDTO getEntry(@PathVariable String uuid, @AuthenticationPrincipal SpringUserDetails springUserDetails) {
        return entryService.getEntry(uuid, springUserDetails);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{uuid}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public void updateEntry(@PathVariable String uuid, @RequestBody EntryInteractionUpdateDTO entry, @AuthenticationPrincipal SpringUserDetails springUserDetails) {
        entryService.updateEntry(uuid, entry.isRead(), entry.isFavorite(), entry.isBookmark(), springUserDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/favorites")
    public PageableDTO<EntryDTO> getFavoriteEntries(
            @PageableDefault(sort = {"pubDate"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        return entryService.getFavoriteEntries(pageable, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/bookmarks")
    public PageableDTO<EntryDTO> getBookmarkedEntries(
            @PageableDefault(sort = {"pubDate"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        return entryService.getBookmarkedEntries(pageable, userDetails);
    }

}
