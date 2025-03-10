package de.feedpulse.service;

import com.rometools.rome.feed.synd.SyndEntry;
import de.feedpulse.controller.EntryController;
import de.feedpulse.dto.response.EntryDTO;
import de.feedpulse.dto.response.PageableDTO;
import de.feedpulse.dto.response.PageableDataDTO;
import de.feedpulse.exceptions.common.InvalidUuidException;
import de.feedpulse.exceptions.entity.EntryNotFoundException;
import de.feedpulse.model.*;
import de.feedpulse.repository.EntryRepository;
import de.feedpulse.repository.UserEntryInteractionRepository;
import de.feedpulse.validation.UuidValidator;
import io.github.cdimascio.essence.EssenceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

    public PageableDTO<EntryDTO> getFeedEntries(String feedUuidString, boolean onlyUnread, Pageable pageRequest, SpringUserDetails userDetails) {
        if (!UuidValidator.isValid(feedUuidString)) throw new InvalidUuidException(feedUuidString);
        UUID feedUuid = UUID.fromString(feedUuidString);
        Page<Entry> pageOfEntries;
                if (onlyUnread) {
                    pageOfEntries = entryRepository.findUnreadEntriesByFeedUuidAndUsersUuid(feedUuid, userDetails.getUuid(), pageRequest);
                } else {
                    pageOfEntries = entryRepository.findEntriesByFeedUuidAndUsersUuid(feedUuid, userDetails.getUuid(), pageRequest);
                }
        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(pageOfEntries);
        List<Link> links = updateLinks(pagedModel, onlyUnread, pageRequest);
        pagedModel.add(links);

        List<EntryDTO> entryDTOs = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryDTOs);
    }

    public PageableDataDTO<EntryDTO> getFeedEntries(boolean onlyUnread, Pageable pageRequest, SpringUserDetails userDetails) {
        Page<Entry> entryList;
        if (onlyUnread) {
            entryList = entryRepository.findUnreadEntriesByUsersUuid(userDetails.getUuid(), pageRequest);
        } else {
            entryList = entryRepository.findEntriesByUsersUuid(userDetails.getUuid(), pageRequest);
        }

        PagedModel<EntityModel<Entry>> pagedModel = pagedResourcesAssembler.toModel(entryList);
        List<Link> links = updateLinks(pagedModel, onlyUnread, pageRequest);
        pagedModel.add(links);

        List<EntryDTO> entryDTOs = convertToEntryDTOs(pagedModel, userDetails);
        return PageableDTO.of(pagedModel, entryDTOs);

    }


    /**
     * Update the links in the paged model to point to the correct endpoint
     * @param pagedModel the paged model to update
     * @param onlyUnread whether only unread entries are requested
     * @param pageRequest the page request
     * @return the updated links
     */
    private List<Link> updateLinks(PagedModel<EntityModel<Entry>> pagedModel, boolean onlyUnread, Pageable pageRequest) {
        List<Link> links = new ArrayList<>();
        pagedModel.getLinks().forEach(link -> {
            String updatedHref = WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(EntryController.class).getEntries(onlyUnread, pageRequest, null))
                    .toUri().toString();
            links.add(Link.of(updatedHref, link.getRel()));
        });
        return links;
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

    private @NonNull List<Entry> getEntriesFromDB(@Nullable List<UUID> uuids, SpringUserDetails springUserDetails) {
//        uuidString.forEach(uuid -> {
//            if (!UuidValidator.isValid(uuid)) throw new InvalidUuidException(uuid);
//        });
//        List<UUID> uuids = uuidString.stream().map(UUID::fromString).toList();
        List<Entry> entries = entryRepository.findEntriesByUuidAndUsersUuid(uuids, springUserDetails.getUuid());
        return entries;
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

    public void updateEntries(List<String> uuidStrings, SpringUserDetails springUserDetails) {
        User user = userService.getUserByUuid(springUserDetails.getUuid());
        List<UUID> uuids = Collections.emptyList();
        uuidStrings.forEach(uuid -> {
            if (!UuidValidator.isValid(uuid)) throw new InvalidUuidException(uuid);
            uuids.add(UUID.fromString(uuid));
        });
        List<Entry> entries = getEntriesFromDB(uuids, springUserDetails);

        // TODO: performance increase with raw SQL
        entries.forEach(entry -> {
            UserEntryInteraction userEntryInteraction = userEntryInteractionRepository
                    .findByUserUuidAndEntryUuid(springUserDetails.getUuid(), entry.getUuid())
                    .orElse(new UserEntryInteraction(user, entry));
            userEntryInteraction.setRead(true);
            userEntryInteractionRepository.save(userEntryInteraction);
        });
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
