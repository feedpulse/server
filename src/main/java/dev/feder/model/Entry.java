package dev.feder.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Entry implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    @NonNull
    private UUID uuid;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "feed_uuid", nullable = false)
    private Feed feed;

    @NonNull
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    @Nullable
    private String description;

    @Nullable
    @Column(columnDefinition = "LONGTEXT")
    private String text;

    @NonNull
    private String link;

    @Nullable
    private String author;

    @Nullable
    private String imageUrl;

    @Nullable
    private String language;

    @Nullable
    private Date pubDate;

    public Entry(@NonNull String title, @NonNull Feed feed, @Nullable String description, @Nullable String text, @NonNull String link, @Nullable String author, @Nullable String imageUrl, @Nullable String language) {
        this.title = title;
        this.feed = feed;
        this.description = description;
        this.text = text;
        this.link = link;
        this.author = author;
        this.imageUrl = imageUrl;
        this.language = language;
    }

    protected Entry() {
    }

    @NonNull
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        assert uuid != null;
        this.uuid = uuid;
    }

    @Nullable
    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @NonNull
    public String getLink() {
        return link;
    }

    public void setLink(@NonNull String link) {
        this.link = link;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@Nullable String author) {
        this.author = author;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@Nullable String language) {
        this.language = language;
    }

    public void setPubDate(@Nullable Date pubDate) {
        this.pubDate = pubDate;
    }

    @Nullable
    public Date getPubDate() {
        return pubDate;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "uuid=" + uuid +
                ", feed=" + feed +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", text='" + text + '\'' +
                ", link='" + link + '\'' +
                ", author='" + author + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", language='" + language + '\'' +
                ", pubDate=" + pubDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entry entry)) return false;
        return Objects.equals(uuid, entry.uuid) && Objects.equals(feed, entry.feed) && Objects.equals(title, entry.title) && Objects.equals(description, entry.description) && Objects.equals(text, entry.text) && Objects.equals(link, entry.link) && Objects.equals(author, entry.author) && Objects.equals(imageUrl, entry.imageUrl) && Objects.equals(language, entry.language) && Objects.equals(pubDate, entry.pubDate);
    }

    public static class EntryBuilder {
        @NonNull
        private String title;
        @Nullable
        private String description;
        @Nullable
        private String text;
        @NonNull
        private String link;
        @Nullable
        private String author;
        @Nullable
        private String imageUrl;
        @Nullable
        private String language;
        @Nullable
        private Date pubDate;
        @NonNull
        private Feed feed;

        public EntryBuilder setTitle(@NonNull String title) {
            this.title = title;
            return this;
        }

        public EntryBuilder setDescription(@Nullable String description) {
            this.description = description;
            return this;
        }

        public EntryBuilder setText(@Nullable String text) {
            this.text = text;
            return this;
        }

        public EntryBuilder setLink(@NonNull String link) {
            this.link = link;
            return this;
        }

        public EntryBuilder setAuthor(@Nullable String author) {
            this.author = author;
            return this;
        }

        public EntryBuilder setImageUrl(@Nullable String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public EntryBuilder setLanguage(@Nullable String language) {
            this.language = language;
            return this;
        }

        public EntryBuilder setPubDate(@Nullable Date pubDate) {
            this.pubDate = pubDate;
            return this;
        }

        public EntryBuilder setFeed(@NonNull Feed feed) {
            this.feed = feed;
            return this;
        }

        public Entry createEntry() {
            return new Entry(title, feed, description, text, link, author, imageUrl, language);
        }
    }
}
