package io.feedpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Feed implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    @JsonIgnore
    @ManyToMany(mappedBy = "feeds") // it tells Hibernate that User is the owner of the relationship
    private List<User> users;

    @Builder
    public Feed(@NonNull String feedUrl, @NonNull String title, @Nullable String description, @NonNull String link, @Nullable String author, @Nullable Date pubDate) {
        this.feedUrl = feedUrl;
        this.title = title;
        this.description = description;
        this.link = link;
        this.author = author;
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

}
