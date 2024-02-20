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

    public PageableDTO<EntryDTO> getFeedEntries(String feedUuidString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) throws InvalidUuidException {
        UUID feedUuid = UuidUtil.fromString(feedUuidString);
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> pageOfEntries = entryRepository.findEntriesByFeedUuidAndUsersId(feedUuid, userDetails.getId(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(pageOfEntries);
        List<EntryDTO> entryDTOs = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryDTOs);
    }

    public PageableDTO<EntryDTO> getFeedEntries(Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> entryList = entryRepository.findEntriesByUsersId(userDetails.getId(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryDTOs = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryDTOs);
    }

    private @NonNull Entry getEntryFromDB(@Nullable String uuidString, SpringUserDetails springUserDetails) throws InvalidUuidException, NoSuchEntryException {
        UUID uuid = UuidUtil.fromString(uuidString);
        Optional<Entry> entry = entryRepository.findEntryByUuidAndUsersId(uuid, springUserDetails.getId());
        if (entry.isEmpty()) {
            throw new NoSuchEntryException("No entry found with UUID " + uuidString);
        }
        return entry.get();
    }

    public @NonNull EntryDTO getEntry(@Nullable String uuidString, SpringUserDetails springUserDetails) throws InvalidUuidException, NoSuchEntryException {
        Entry entry = getEntryFromDB(uuidString, springUserDetails);
        UserEntryInteraction userEntryInteraction = userEntryInteractionRepository
                .findByUserIdAndEntryUuid(springUserDetails.getId(), UuidUtil.fromString(uuidString))
                .orElse(null);
        return EntryDTO.of( entry, userEntryInteraction);
    }


    public Optional<Entry> getUserEntryByLink(String link, SpringUserDetails springUserDetails) {
        return entryRepository.findEntryByLinkAndUsersId(link, springUserDetails.getId());
    }

    public Optional<Entry> getEntryByLink(String link) {
        return entryRepository.findByLink(link);
    }

    public void updateEntry(String entryUuid, @Nullable Boolean read, @Nullable Boolean favorite, @Nullable Boolean bookmark, SpringUserDetails springUserDetails) throws InvalidUuidException, NoSuchEntryException {
        User user = userService.getUserById(springUserDetails.getId());
        Entry entry = getEntryFromDB(entryUuid, springUserDetails);
        UserEntryInteraction userEntryInteraction = userEntryInteractionRepository
                .findByUserIdAndEntryUuid(springUserDetails.getId(), entry.getUuid())
                .orElse(new UserEntryInteraction(user, entry));
        if (read != null) userEntryInteraction.setRead(read);
        if (favorite != null) userEntryInteraction.setFavorite(favorite);
        if (bookmark != null) userEntryInteraction.setBookmark(bookmark);
        userEntryInteractionRepository.save(userEntryInteraction);
    }

    public EntryDTO toEntryDTO(@NonNull Entry entry, @NonNull Long userId) {
        return toEntryDTO(entry, userId, null);
    }

    public EntryDTO toEntryDTO(@NonNull Entry entry, @NonNull Long userId, @Nullable UserEntryInteraction userEntryInteraction) {
        if (userEntryInteraction == null) {
            userEntryInteraction = userEntryInteractionRepository
                    .findByUserIdAndEntryUuid(userId, entry.getUuid())
                    .orElse(null);
        }
        return EntryDTO.of(entry, userEntryInteraction);
    }

    public void saveEntry(Entry entry) {
        entryRepository.save(entry);
    }

    public PageableDTO<EntryDTO> getFavoriteEntries(Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> entryList = entryRepository.findFavoriteEntriesByUsersId(userDetails.getId(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> getBookmarkedEntries(Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> entryList = entryRepository.findBookmarkedEntriesByUsersId(userDetails.getId(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchEntries(String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> entryList = entryRepository.searchEntriesByUsersId(userDetails.getId(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchFeedEntries(String feedId, String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        UUID feedUuid = UuidUtil.fromString(feedId);
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> entryList = entryRepository.searchEntriesByFeedUuidAndUsersId(feedUuid, userDetails.getId(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchBookmarkedEntries(String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> entryList = entryRepository.searchBookmarkedEntriesByUsersId(userDetails.getId(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchFavoriteEntries(String searchString, Integer size, Integer page, Boolean sortOrder, SpringUserDetails userDetails) {
        Pageable pageRequest = createPageRequest(size, page, sortOrder);
        Page<Entry> entryList = entryRepository.searchFavoriteEntriesByUsersId(userDetails.getId(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    private Pageable createPageRequest(Integer size, Integer page, Boolean sortOrder) {
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        return PageRequest.of(page, size, sort);
    }

    private List<EntryDTO> convertToEntryDTOs(PagedModel<EntityModel<Entry>> pagedModel, SpringUserDetails userDetails) {
        return pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(entry -> toEntryDTO(entry, userDetails.getId()))
                .toList();
    }
}
