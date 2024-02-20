package io.feedpulse.dto.response;

import io.feedpulse.model.Keyword;
import io.feedpulse.model.UserEntryInteraction;
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

    static public EntryDTO of(io.feedpulse.model.Entry entry, @Nullable UserEntryInteraction userEntryInteraction) {
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

    static public EntryDTO of(io.feedpulse.model.Entry entry) {
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
