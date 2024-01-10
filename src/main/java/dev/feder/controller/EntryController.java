package dev.feder.controller;

import dev.feder.dto.request.EntryInteractionUpdateDTO;
import dev.feder.exceptions.InvalidUuidException;
import dev.feder.exceptions.NoSuchEntryException;
import dev.feder.model.Entry;
import dev.feder.service.EntryService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entries")
public class EntryController {

    private final EntryService entryService;

    public EntryController(@NonNull EntryService entryService) {
        this.entryService = entryService;
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Entry> getEntries(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "true") Boolean sortOrder
    ) {
        return entryService.getEntries(limit, offset, sortOrder);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{uuid}")
    public Entry getEntry(@PathVariable String uuid) throws InvalidUuidException, NoSuchEntryException {
        return entryService.getEntry(uuid);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{uuid}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public void updateEntry(@PathVariable String uuid, @RequestBody EntryInteractionUpdateDTO entry) throws InvalidUuidException, NoSuchEntryException {
        entryService.updateEntry(uuid, entry.isRead(), entry.isFavorite(), entry.isBookmark());
    }

}
