package io.feedpulse.service;

import com.rometools.rome.feed.synd.SyndEntry;
import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.exceptions.common.InvalidUuidException;
import io.feedpulse.exceptions.entity.EntryNotFoundException;
import io.feedpulse.model.*;
import io.feedpulse.repository.EntryRepository;
import io.feedpulse.repository.UserEntryInteractionRepository;
import io.feedpulse.validation.UuidValidator;
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

        Entry.EntryBuilder entryBuilder = Entry.builder();
        entryBuilder
                .title(syndEntry.getTitle())
                .description(data.getDescription())
                .text(data.getText())
                .link(syndEntry.getLink())
                .author(syndEntry.getAuthor())
                .imageUrl(data.getImage())
                .language(data.getLanguage())
                .keywords(keywords)
                .feed(feed);
        if (syndEntry.getPublishedDate() == null) {
            entryBuilder.pubDate(new Date());
        } else {
            entryBuilder.pubDate(syndEntry.getPublishedDate());
        }

        Entry newEntry = entryRepository.save(entryBuilder.build());
        try {
            newEntry = entryRepository.save(newEntry);
        } catch (Exception e) {
            log.warn("Failed to save entry: {}", newEntry);
            e.printStackTrace();
        }

    }

    public void deleteEntry(Entry entry) {
        entryRepository.delete(entry);
    }

    public PageableDTO<EntryDTO> getFeedEntries(String feedUuidString, Pageable pageRequest, SpringUserDetails userDetails) {
        if (!UuidValidator.isValid(feedUuidString)) throw new InvalidUuidException(feedUuidString);
        UUID feedUuid = UUID.fromString(feedUuidString);
        Page<Entry> pageOfEntries = entryRepository.findEntriesByFeedUuidAndUsersUuid(feedUuid, userDetails.getUuid(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(pageOfEntries);
        List<EntryDTO> entryDTOs = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryDTOs);
    }

    public PageableDTO<EntryDTO> getFeedEntries(Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Entry> entryList = entryRepository.findEntriesByUsersUuid(userDetails.getUuid(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryDTOs = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryDTOs);
    }

    private @NonNull Entry getEntryFromDB(@Nullable String uuidString, SpringUserDetails springUserDetails) {
        if (!UuidValidator.isValid(uuidString)) throw new InvalidUuidException(uuidString);
        UUID uuid = UUID.fromString(uuidString);
        Optional<Entry> entry = entryRepository.findEntryByUuidAndUsersUuid(uuid, springUserDetails.getUuid());
        if (entry.isEmpty()) {
            throw new EntryNotFoundException(uuidString);
        }
        return entry.get();
    }

    public @NonNull EntryDTO getEntry(@Nullable String uuidString, SpringUserDetails springUserDetails) {
        Entry entry = getEntryFromDB(uuidString, springUserDetails);
        UserEntryInteraction userEntryInteraction = userEntryInteractionRepository
                .findByUserUuidAndEntryUuid(springUserDetails.getUuid(), UUID.fromString(uuidString))
                .orElse(null);
        return EntryDTO.of(entry, userEntryInteraction);
    }


    public Optional<Entry> getUserEntryByLink(String link, SpringUserDetails springUserDetails) {
        return entryRepository.findEntryByLinkAndUsersUuid(link, springUserDetails.getUuid());
    }

    public Optional<Entry> getEntryByLink(String link) {
        return entryRepository.findByLink(link);
    }

    public void updateEntry(String entryUuid, @Nullable Boolean read, @Nullable Boolean favorite, @Nullable Boolean bookmark, SpringUserDetails springUserDetails) {
        User user = userService.getUserByUuid(springUserDetails.getUuid());
        Entry entry = getEntryFromDB(entryUuid, springUserDetails);
        UserEntryInteraction userEntryInteraction = userEntryInteractionRepository
                .findByUserUuidAndEntryUuid(springUserDetails.getUuid(), entry.getUuid())
                .orElse(new UserEntryInteraction(user, entry));
        if (read != null) userEntryInteraction.setRead(read);
        if (favorite != null) userEntryInteraction.setFavorite(favorite);
        if (bookmark != null) userEntryInteraction.setBookmark(bookmark);
        userEntryInteractionRepository.save(userEntryInteraction);
    }

    public EntryDTO toEntryDTO(@NonNull Entry entry, @NonNull UUID userUuid) {
        return toEntryDTO(entry, userUuid, null);
    }

    public EntryDTO toEntryDTO(@NonNull Entry entry, @NonNull UUID userUuid, @Nullable UserEntryInteraction userEntryInteraction) {
        if (userEntryInteraction == null) {
            userEntryInteraction = userEntryInteractionRepository
                    .findByUserUuidAndEntryUuid(userUuid, entry.getUuid())
                    .orElse(null);
        }
        return EntryDTO.of(entry, userEntryInteraction);
    }

    public void saveEntry(Entry entry) {
        entryRepository.save(entry);
    }

    public PageableDTO<EntryDTO> getFavoriteEntries(Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Entry> entryList = entryRepository.findFavoriteEntriesByUsersUuid(userDetails.getUuid(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> getBookmarkedEntries(Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Entry> entryList = entryRepository.findBookmarkedEntriesByUsersUuid(userDetails.getUuid(), pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchEntries(String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Entry> entryList = entryRepository.searchEntriesByUsersUuid(userDetails.getUuid(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchFeedEntries(String feedId, String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        if (!UuidValidator.isValid(feedId)) throw new InvalidUuidException(feedId);
        UUID feedUuid = UUID.fromString(feedId);
        Page<Entry> entryList = entryRepository.searchEntriesByFeedUuidAndUsersUuid(feedUuid, userDetails.getUuid(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchBookmarkedEntries(String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Entry> entryList = entryRepository.searchBookmarkedEntriesByUsersUuid(userDetails.getUuid(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    public PageableDTO<EntryDTO> searchFavoriteEntries(String searchString, Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Entry> entryList = entryRepository.searchFavoriteEntriesByUsersUuid(userDetails.getUuid(), searchString, pageRequest);
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<EntryDTO> entryListDTO = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryListDTO);
    }

    private List<EntryDTO> convertToEntryDTOs(PagedModel<EntityModel<Entry>> pagedModel, SpringUserDetails userDetails) {
        return pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(entry -> toEntryDTO(entry, userDetails.getUuid()))
                .toList();
    }
}
