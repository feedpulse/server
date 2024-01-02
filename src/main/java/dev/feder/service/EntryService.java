package dev.feder.service;

import com.rometools.rome.feed.synd.SyndEntry;
import dev.feder.exceptions.InvalidUuidException;
import dev.feder.exceptions.NoSuchEntryException;
import dev.feder.model.Entry;
import dev.feder.model.Feed;
import dev.feder.repository.EntryRepository;
import dev.feder.util.UuidUtil;
import io.github.cdimascio.essence.EssenceResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EntryService {

    private final EntryRepository entryRepository;

    public EntryService(@NonNull EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public void addEntry(SyndEntry entry, Feed feed, EssenceResult data) {
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
                .setFeed(feed)
                .createEntry();
        newEntry = entryRepository.save(newEntry);
    }

    public void deleteEntry(Entry entry) {
        entryRepository.delete(entry);
    }

    public List<Entry> getEntries(String feedUuidString, Integer limit, Integer offset, Boolean sortOrder) throws InvalidUuidException{
        UUID feedUuid = UuidUtil.fromString(feedUuidString);
        var sort = sortOrder ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, limit, sort, "pubDate");
        return entryRepository.findAllByFeedUuid(feedUuid, pageable).toList();
    }

    public List<Entry> getEntries(Integer limit, Integer offset, Boolean sortOrder) {
        var sort = sortOrder ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, limit, sort, "pubDate");
        return entryRepository.findAll(pageable).toList();
    }

    public @NonNull Entry getEntry(@Nullable String uuidString) throws InvalidUuidException, NoSuchEntryException {
        UUID uuid = UuidUtil.fromString(uuidString);
        Optional<Entry> entry = entryRepository.findById(uuid);
        if (entry.isEmpty()) {
            throw new NoSuchEntryException("No entry found with UUID " + uuidString);
        }
        return entry.get();
    }

    public Optional<Entry> getEntryByLink(String link) {
        return entryRepository.findByLink(link);
    }
}
