package io.feedpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
public class Entry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    @Column(nullable = false, length = 500)
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
    @Column(columnDefinition = "VARCHAR(500)")
    private String imageUrl;

    @Nullable
    private String language;

    @Nullable
    private Date pubDate;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_uuid")
    private Set<UserEntryInteraction> userEntryInteractions;

    @NonNull
    @ManyToMany
    @JsonBackReference
    @JoinTable(name = "entry_keyword", joinColumns = @JoinColumn(name = "entry_uuid"), inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keyword> keywords = Set.of();

    protected Entry() {}

    @Builder
    public Entry(@NonNull Feed feed, @NonNull String title, @Nullable String description, @Nullable String text, @NonNull String link, @Nullable String author, @Nullable String imageUrl, @Nullable String language, @Nullable Date pubDate, @NonNull Set<Keyword> keywords) {
        this.feed = feed;
        this.title = title;
        this.description = description;
        this.text = text;
        this.link = link;
        this.author = author;
        this.imageUrl = imageUrl;
        this.language = language;
        this.pubDate = pubDate;
        this.keywords = keywords;
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
                ", keywords=" + keywords +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entry entry)) return false;
        return Objects.equals(uuid, entry.uuid) && Objects.equals(feed, entry.feed) && Objects.equals(title, entry.title) && Objects.equals(description, entry.description) && Objects.equals(text, entry.text) && Objects.equals(link, entry.link) && Objects.equals(author, entry.author) && Objects.equals(imageUrl, entry.imageUrl) && Objects.equals(language, entry.language) && Objects.equals(pubDate, entry.pubDate) && Objects.equals(keywords, entry.keywords);
    }
}
