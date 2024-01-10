package dev.feder.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import dev.feder.dto.EntryDTO;
import dev.feder.dto.request.EntryInteractionUpdateDTO;
import dev.feder.exceptions.InvalidUuidException;
import dev.feder.exceptions.NoSuchEntryException;
import dev.feder.model.*;
import dev.feder.repository.EntryRepository;
import dev.feder.repository.UserEntryInteractionRepository;
import dev.feder.util.UuidUtil;
import io.github.cdimascio.essence.EssenceResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public void addEntry(SyndEntry entry, Feed feed, EssenceResult data) {
        Set<String> keywordss = entry.getCategories().stream().map(SyndCategory::getName).collect(Collectors.toSet());
        Set<Keyword> keywords = keywordService.addMissingKeywords(keywordss);
        System.out.println("\nkeywords: ");
        keywords.forEach(keyword -> System.out.println(keyword.getKeyword()));

        Entry.EntryBuilder entryBuilder = new Entry.EntryBuilder();
        Entry newEntry = entryBuilder
                .setTitle(entry.getTitle())
                .setDescription(data.getDescription())
                .setText(data.getText())
                .setLink(entry.getLink())
                .setAuthor(entry.getAuthor())
                .setImageUrl(data.getImage())
                .setLanguage(data.getLanguage())
                .setPubDate(entry.getPublishedDate())
                .setKeywords(keywords)
                .setFeed(feed)
                .createEntry();
        newEntry = entryRepository.save(newEntry);
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

    public List<Entry> getEntries(Integer limit, Integer offset, Boolean sortOrder) {
        User user = userService.getCurrentUser();
        var sort = sortOrder ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, limit, sort, "pubDate");
        return entryRepository.findEntriesByUsersId(user.getId(), pageable).toList();
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
}
