package de.feedpulse.dto.response;

import de.feedpulse.model.Feed;

import java.io.Serializable;

public record FeedWithoutEntriesDTO(
        String uuid,
        String feedUrl,
        String title,
        Integer unreadCount,
        String description,
        String link,
        String author,
        String pubDate
) implements Serializable {

    static public FeedWithoutEntriesDTO of(Feed feed, Integer unreadCount) {
        return new FeedWithoutEntriesDTO(
                feed.getUuid().toString(),
                feed.getFeedUrl(),
                feed.getTitle(),
                unreadCount,
                feed.getDescription(),
                feed.getLink(),
                feed.getAuthor(),
                feed.getPubDate().toString()
        );
    }
}
