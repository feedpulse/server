package io.feedpulse.service;

import com.rometools.rome.feed.synd.SyndEntry;
import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.exceptions.InvalidUuidException;
import io.feedpulse.exceptions.NoSuchEntryException;
import io.feedpulse.model.*;
import io.feedpulse.repository.EntryRepository;
import io.feedpulse.repository.UserEntryInteractionRepository;
import io.feedpulse.util.UuidUtil;
import io.github.cdimascio.essence.EssenceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntryService {

    private static final Logger log = LoggerFactory.getLogger(EntryService.class);

    private final EntryRepository entryRepository;
    private final UserService userService;
    private final KeywordService keywordService;
    private final UserEntryInteractionRepository userEntryInteractionRepository;
    private final PagedResourcesAssembler<Entry> pagedResourcesAssembler;

    public EntryService(@NonNull EntryRepository entryRepository, @NonNull UserService userService, KeywordService keywordService, UserEntryInteractionRepository userEntryInteractionRepository, PagedResourcesAssembler<Entry> pagedResourcesAssembler) {
        this.entryRepository = entryRepository;
        this.userService = userService;
        this.keywordService = keywordService;
        this.userEntryInteractionRepository = userEntryInteractionRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    public void addIfNotExists(Feed feed, SyndEntry syndEntry, Set<Keyword> keywords, EssenceResult data) {
        Optional<Entry> existingEntry = getEntryByLink(syndEntry.getLink());
        if (existingEntry.isPresent()) return; // TODO: maybe update entry

        Entry.EntryBuilder entryBuilder = new Entry.EntryBuilder();
        entryBuilder
                .setTitle(syndEntry.getTitle())
                .setDescription(data.getDescription())
                .setText(data.getText())
                .setLink(syndEntry.getLink())
                .setAuthor(syndEntry.getAuthor())
                .setImageUrl(data.getImage())
                .setLanguage(data.getLanguage())
                .setKeywords(keywords)
                .setFeed(feed);
        if (syndEntry.getPublishedDate() == null) {
            entryBuilder.setPubDate(new Date());
        } else {
            entryBuilder.setPubDate(syndEntry.getPublishedDate());
        }

        try {
            Entry newEntry = entryRepository.save(entryBuilder.createEntry());
        } catch (Exception e) {
            log.warn("Failed to save entry: {}", entryBuilder.createEntry());
            e.printStackTrace();
        }

    }

    public void deleteEntry(Entry entry) {
        entryRepository.delete(entry);
    }

    private Page<Entry> getEntriesAsPage(String feedUuidString, Integer limit, Integer offset, Boolean sortOrder) throws InvalidUuidException {
        UUID feedUuid = UuidUtil.fromString(feedUuidString);
        var sort = sortOrder ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, limit, sort, "pubDate");
        return entryRepository.findEntriesByFeedUuidAndUsersId(feedUuid, userService.getCurrentUser().getId(), pageable);
    }

    private PagedModel<EntityModel<Entry>> getEntriesAsPagedModel(String feedUuidString, Integer limit, Integer offset, Boolean sortOrder) throws InvalidUuidException {
        Page<Entry> entryList = getEntriesAsPage(feedUuidString, limit, offset, sortOrder);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        return pagedModel;
    }

    public PageableDTO<EntryDTO> getEntries(String feedUuidString, Integer limit, Integer offset, Boolean sortOrder) throws InvalidUuidException {
        PagedModel<EntityModel<Entry>> pagedModel = getEntriesAsPagedModel(feedUuidString, limit, offset, sortOrder);
        List<EntryDTO> entryDTOs = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(entry -> toEntryDTO(entry, userService.getCurrentUser()))
                .toList();
        return PageableDTO.of(pagedModel, entryDTOs);
    }

    private Page<Entry> getEntriesAsPage(Integer size, Integer page, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return entryRepository.findEntriesByUsersId(user.getId(), pageable);
//        return entryRepository.findEntriesByUsersId(user.getId(), pageable).map(entry -> toEntryDTO(entry, user));
    }

    private PagedModel<EntityModel<Entry>> getEntriesAsPagedModel(Integer size, Integer page, Boolean sortOrder) {
        Page<Entry> entryList = getEntriesAsPage(size, page, sortOrder);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        return pagedModel;
    }

    public PageableDTO<EntryDTO> getEntries(Integer size, Integer page, Boolean sortOrder) {
        PagedModel<EntityModel<Entry>> pagedModel = getEntriesAsPagedModel(size, page, sortOrder);
        List<EntryDTO> entryList = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(entry -> toEntryDTO(entry, userService.getCurrentUser()))
                .toList();
        return PageableDTO.of(pagedModel, entryList);
    }

    public @NonNull Entry getEntry(@Nullable String uuidString) throws InvalidUuidException, NoSuchEntryException {
        User user = userService.getCurrentUser();
        UUID uuid = UuidUtil.fromString(uuidString);
        Optional<Entry> entry = entryRepository.findEntryByUuidAndUsersId(uuid, user.getId());
        if (entry.isEmpty()) {
            throw new NoSuchEntryException("No entry found with UUID " + uuidString);
        }
        return entry.get();
    }

    public Optional<Entry> getUserEntryByLink(String link) {
        User user = userService.getCurrentUser();
        return entryRepository.findEntryByLinkAndUsersId(link, user.getId());
    }

    public Optional<Entry> getEntryByLink(String link) {
        return entryRepository.findByLink(link);
    }

    public void updateEntry(String entryUuid, @Nullable Boolean read, @Nullable Boolean favorite, @Nullable Boolean bookmark) throws InvalidUuidException, NoSuchEntryException {
        User user = userService.getCurrentUser();
        Entry entry = getEntry(entryUuid);
        UserEntryInteraction userEntryInteraction = userEntryInteractionRepository
                .findByUserIdAndEntryUuid(user.getId(), entry.getUuid())
                .orElse(new UserEntryInteraction(user, entry));
        if (read != null) userEntryInteraction.setRead(read);
        if (favorite != null) userEntryInteraction.setFavorite(favorite);
        if (bookmark != null) userEntryInteraction.setBookmark(bookmark);
        userEntryInteractionRepository.save(userEntryInteraction);
    }

    public List<EntryDTO> toEntryDTOs(List<Entry> entries, @Nullable User user) {
        if (user == null) user = userService.getCurrentUser();
        List<UserEntryInteraction> userEntryInteractions = userEntryInteractionRepository.findByUserIdForEntries(user.getId(), entries);
        List<EntryDTO> entryDTOs = new ArrayList<>();
        for (UserEntryInteraction userEntryInteraction : userEntryInteractions) {
            entryDTOs.add(toEntryDTO(userEntryInteraction.getEntry(), user, userEntryInteraction));
        }
        return entryDTOs;
    }

    public EntryDTO toEntryDTO(Entry entry, @Nullable User user) {
        return toEntryDTO(entry, user, null);
    }

    public EntryDTO toEntryDTO(@Nullable Entry entry) {
        return toEntryDTO(entry, null, null);
    }

    public EntryDTO toEntryDTO(Entry entry, @Nullable User user, @Nullable UserEntryInteraction userEntryInteraction) {
        if (user == null) user = userService.getCurrentUser();
        if (userEntryInteraction == null) {
            userEntryInteraction = userEntryInteractionRepository
                    .findByUserIdAndEntryUuid(user.getId(), entry.getUuid())
                    .orElse(new UserEntryInteraction(user, entry));
        }
        return EntryDTO.of(entry, userEntryInteraction);
    }

    public void saveEntry(Entry entry) {
        entryRepository.save(entry);
    }

    private Page<Entry> getFavoriteEntriesAsPage(Integer size, Integer page, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return entryRepository.findFavoriteEntriesByUsersId(user.getId(), pageable);
    }

    private PagedModel<EntityModel<Entry>> getFavoriteEntriesAsPagedModel(Integer size, Integer page, Boolean sortOrder) {
        Page<Entry> entryList = getFavoriteEntriesAsPage(size, page, sortOrder);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        return pagedModel;
    }

    public PageableDTO<EntryDTO> getFavoriteEntries(Integer size, Integer page, Boolean sortOrder) {
        PagedModel<EntityModel<Entry>> pagedModel = getFavoriteEntriesAsPagedModel(size, page, sortOrder);
        List<EntryDTO> entryList = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(entry -> toEntryDTO(entry, userService.getCurrentUser()))
                .toList();
        return PageableDTO.of(pagedModel, entryList);
    }

    private Page<Entry> getBookmarkedEntriesAsPage(Integer size, Integer page, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return entryRepository.findBookmarkedEntriesByUsersId(user.getId(), pageable);
    }

    private PagedModel<EntityModel<Entry>> getBookmarkedEntriesAsPagedModel(Integer size, Integer page, Boolean sortOrder) {
        Page<Entry> entryList = getBookmarkedEntriesAsPage(size, page, sortOrder);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        return pagedModel;
    }

    public PageableDTO<EntryDTO> getBookmarkedEntries(Integer size, Integer page, Boolean sortOrder) {
        PagedModel<EntityModel<Entry>> pagedModel = getBookmarkedEntriesAsPagedModel(size, page, sortOrder);
        List<EntryDTO> entryList = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(entry -> toEntryDTO(entry, userService.getCurrentUser()))
                .toList();
        return PageableDTO.of(pagedModel, entryList);
    }
}
