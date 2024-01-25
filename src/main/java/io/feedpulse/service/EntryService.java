package io.feedpulse.service;

import com.rometools.rome.feed.synd.SyndEntry;
import io.feedpulse.dto.response.EntryDTO;
import io.feedpulse.exceptions.InvalidUuidException;
import io.feedpulse.exceptions.NoSuchEntryException;
import io.feedpulse.model.*;
import io.feedpulse.repository.EntryRepository;
import io.feedpulse.repository.UserEntryInteractionRepository;
import io.feedpulse.util.UuidUtil;
import io.github.cdimascio.essence.EssenceResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final UserService userService;
    private final KeywordService keywordService;
    private final UserEntryInteractionRepository userEntryInteractionRepository;

    public EntryService(@NonNull EntryRepository entryRepository, @NonNull UserService userService, KeywordService keywordService, UserEntryInteractionRepository userEntryInteractionRepository) {
        this.entryRepository = entryRepository;
        this.userService = userService;
        this.keywordService = keywordService;
        this.userEntryInteractionRepository = userEntryInteractionRepository;
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

        Entry newEntry = entryRepository.save(entryBuilder.createEntry());

    }

    public void deleteEntry(Entry entry) {
        entryRepository.delete(entry);
    }

    public List<Entry> getEntries(String feedUuidString, Integer limit, Integer offset, Boolean sortOrder) throws InvalidUuidException {
        UUID feedUuid = UuidUtil.fromString(feedUuidString);
        var sort = sortOrder ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, limit, sort, "pubDate");
        return entryRepository.findEntriesByFeedUuidAndUsersId(feedUuid, userService.getCurrentUser().getId(), pageable).toList();
    }

    public List<EntryDTO> getEntries(Integer limit, Integer offset, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        Pageable pageable = PageRequest.of(offset, limit, sort);
        return entryRepository.findEntriesByUsersId(user.getId(), pageable).map(entry -> toEntryDTO(entry, user)).toList();
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

    public EntryDTO toEntryDTO(Entry entry) {
        return toEntryDTO(entry, null, null);
    }

    public EntryDTO toEntryDTO(Entry entry, @Nullable User user, @Nullable UserEntryInteraction userEntryInteraction) {
        if (user == null) user = userService.getCurrentUser();
        if (userEntryInteraction == null) {
            userEntryInteraction = userEntryInteractionRepository
                    .findByUserIdAndEntryUuid(user.getId(), entry.getUuid())
                    .orElse(new UserEntryInteraction(user, entry));
        }
        EntryDTO.EntryDtoBuilder entryDtoBuilder = new EntryDTO.EntryDtoBuilder();
        entryDtoBuilder
                .setUuid(entry.getUuid())
                .setFeedUuid(entry.getFeed().getUuid())
                .setTitle(entry.getTitle())
                .setUrl(entry.getLink())
                .setImageUrl(entry.getImageUrl())
                .setDescription(entry.getDescription())
                .setText(entry.getText())
                .setAuthor(entry.getAuthor())
                .setPublishedDate(entry.getPubDate().toString())
                .setKeywords(entry.getKeywords().stream().map(Keyword::getKeyword).toArray(String[]::new))
                .setRead(userEntryInteraction.isRead())
                .setBookmark(userEntryInteraction.isBookmark())
                .setFavorite(userEntryInteraction.isFavorite());

        return entryDtoBuilder.createEntryDTO();

    }

    public void saveEntry(Entry entry) {
        entryRepository.save(entry);
    }


    public List<EntryDTO> getFavoriteEntries(Integer limit, Integer offset, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        Pageable pageable = PageRequest.of(offset, limit, sort);
        return entryRepository.findFavoriteEntriesByUsersId(user.getId(), pageable).map(entry -> toEntryDTO(entry, user)).toList();
    }

    public List<EntryDTO> getBookmarkedEntries(Integer limit, Integer offset, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var by = Sort.by("pubDate");
        var sort = sortOrder ? by.ascending() : by.descending();
        Pageable pageable = PageRequest.of(offset, limit, sort);
        return entryRepository.findBookmarkedEntriesByUsersId(user.getId(), pageable).map(entry -> toEntryDTO(entry, user)).toList();
    }
}
