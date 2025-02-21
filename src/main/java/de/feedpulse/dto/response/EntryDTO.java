package de.feedpulse.dto.response;

import de.feedpulse.model.Entry;
import de.feedpulse.model.Keyword;
import de.feedpulse.model.UserEntryInteraction;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record EntryDTO(
        UUID uuid,
        UUID feedUuid,
        String title,
        String link,
        String imageUrl,
        String description,
        String text,
        String author,
        String pubDate,
        String[] keywords,
        boolean read,
        boolean favorite,
        boolean bookmark) {

    static public EntryDTO of(Entry entry, @Nullable UserEntryInteraction userEntryInteraction) {
        if (userEntryInteraction == null) return EntryDTO.of(entry);
        return new EntryDTO(
                entry.getUuid(),
                entry.getFeed().getUuid(),
                entry.getTitle(),
                entry.getLink(),
                entry.getImageUrl(),
                entry.getDescription(),
                entry.getText(),
                entry.getAuthor(),
                entry.getPubDate().toString(),
                entry.getKeywords().stream().map(Keyword::getKeyword).toArray(String[]::new),
                userEntryInteraction.isRead(),
                userEntryInteraction.isFavorite(),
                userEntryInteraction.isBookmark()
        );
    }

    static public EntryDTO of(Entry entry) {
        return new EntryDTO(
                entry.getUuid(),
                entry.getFeed().getUuid(),
                entry.getTitle(),
                entry.getLink(),
                entry.getImageUrl(),
                entry.getDescription(),
                entry.getText(),
                entry.getAuthor(),
                entry.getPubDate().toString(),
                entry.getKeywords().stream().map(Keyword::getKeyword).toArray(String[]::new),
                false,
                false,
                false
        );
    }
}
