package io.feedpulse.dto.response;

import io.feedpulse.model.Feed;

import java.io.Serializable;

public record FeedDTO(
        String uuid,
        String feedUrl,
        String title,
        String description,
        String link,
        String author,
        String pubDate
) implements Serializable {

    static public FeedDTO of(Feed feed) {
        return new FeedDTO(
                feed.getUuid().toString(),
                feed.getFeedUrl(),
                feed.getTitle(),
                feed.getDescription(),
                feed.getLink(),
                feed.getAuthor(),
                feed.getPubDate().toString()
        );
    }
}
