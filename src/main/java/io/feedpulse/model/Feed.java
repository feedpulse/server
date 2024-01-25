package io.feedpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Feed implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    @NonNull
    private UUID uuid;

    @NonNull
    private String feedUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "feed")
    private List<Entry> entries;

    @NonNull
    private String title;

    @Nullable
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @NonNull
    private String link;

    @Nullable
    private String author;

    @Nullable
    private Date pubDate;

    @ManyToMany(mappedBy = "feeds") // it tells Hibernate that User is the owner of the relationship
    private List<User> users;

    public Feed(@NonNull String feedUrl, @NonNull String title, @Nullable String description, @NonNull String link, @Nullable String author, @Nullable Date pubDate) {
        this.feedUrl = feedUrl;
        this.title = title;
        this.description = description;
        this.link = link;
        this.author = author;
        this.pubDate = pubDate;
    }

    protected Feed() {
    }

    @NonNull
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Nullable
    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(@Nullable Date pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "uuid=" + uuid +
                ", feedUrl='" + feedUrl + '\'' +
//                ", entries=" + entries +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", author='" + author + '\'' +
                ", pubDate=" + pubDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feed)) return false;
        return Objects.equals(uuid, ((Feed) o).uuid) && Objects.equals(feedUrl, ((Feed) o).feedUrl) && Objects.equals(entries, ((Feed) o).entries) && Objects.equals(title, ((Feed) o).title) && Objects.equals(description, ((Feed) o).description) && Objects.equals(link, ((Feed) o).link) && Objects.equals(author, ((Feed) o).author) && Objects.equals(pubDate, ((Feed) o).pubDate);
    }

    public static class FeedBuilder {
        @NonNull
        private String feedUrl;
        @NonNull
        private String title;
        @Nullable
        private String description;
        @NonNull
        private String link;
        @Nullable
        private String author;
        @Nullable
        private Date pubDate;

        public FeedBuilder setFeedUrl(@NonNull String feedUrl) {
            this.feedUrl = feedUrl;
            return this;
        }

        public FeedBuilder setTitle(@NonNull String title) {
            this.title = title;
            return this;
        }

        public FeedBuilder setDescription(@Nullable String description) {
            this.description = description;
            return this;
        }

        public FeedBuilder setLink(@NonNull String link) {
            this.link = link;
            return this;
        }

        public FeedBuilder setAuthor(@Nullable String author) {
            this.author = author;
            return this;
        }

        public FeedBuilder setPubDate(@Nullable Date pubDate) {
            this.pubDate = pubDate;
            return this;
        }

        public Feed createFeed() {
            return new Feed(feedUrl, title, description, link, author, pubDate);
        }
    }
}
