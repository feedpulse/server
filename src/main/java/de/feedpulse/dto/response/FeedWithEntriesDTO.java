package de.feedpulse.dto.response;

import de.feedpulse.model.Entry;
import de.feedpulse.model.Feed;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

public record FeedWithEntriesDTO(
        String uuid,
        String feedUrl,
        Set<Entry> entries,
        Integer unreadCount,
        String title,
        String description,
        String link,
        String author,
        String pubDate
) implements Serializable {

    static public FeedWithEntriesDTO of(Feed feed, Integer unreadCount) {
        return new FeedWithEntriesDTO(
                feed.getUuid().toString(),
                feed.getFeedUrl(),
                feed.getEntries().stream().collect(Collectors.toSet()), // TODO: change entries to a set in the Feed model
                unreadCount,
                feed.getTitle(),
                feed.getDescription(),
                feed.getLink(),
                feed.getAuthor(),
                feed.getPubDate().toString()
        );
    }
}
