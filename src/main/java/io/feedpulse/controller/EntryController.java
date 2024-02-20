package io.feedpulse.controller;

import io.feedpulse.dto.request.EntryInteractionUpdateDTO;
import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.exceptions.InvalidUuidException;
import io.feedpulse.exceptions.NoSuchEntryException;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.service.EntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/entries", produces = "application/json")
public class EntryController {

    private static final Logger log = LoggerFactory.getLogger(EntryController.class);

    private final EntryService entryService;

    public EntryController(@NonNull EntryService entryService) {
        this.entryService = entryService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PageableDTO<EntryDTO> getEntries(
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        log.info("Getting entries with size: {}, page: {}, sortOrder: {}", size, page, sortOrder);
        return entryService.getFeedEntries(size, page, sortOrder, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{uuid}")
    public EntryDTO getEntry(@PathVariable String uuid, @AuthenticationPrincipal SpringUserDetails springUserDetails) throws InvalidUuidException, NoSuchEntryException {
        return entryService.getEntry(uuid, springUserDetails);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{uuid}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public void updateEntry(@PathVariable String uuid, @RequestBody EntryInteractionUpdateDTO entry, @AuthenticationPrincipal SpringUserDetails springUserDetails) throws InvalidUuidException, NoSuchEntryException {
        entryService.updateEntry(uuid, entry.isRead(), entry.isFavorite(), entry.isBookmark(), springUserDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/favorites")
    public PageableDTO<EntryDTO> getFavoriteEntries(
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        return entryService.getFavoriteEntries(size, page, sortOrder, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/bookmarks")
    public PageableDTO<EntryDTO> getBookmarkedEntries(
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder,
            @AuthenticationPrincipal SpringUserDetails userDetails
    ) {
        return entryService.getBookmarkedEntries(size, page, sortOrder, userDetails);
    }

}
