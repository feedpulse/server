package io.feedpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class UserEntryInteraction implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_uuid")
    private Entry entry;

    @Column(name = "is_bookmark")
    private Boolean bookmark = false;

    @Column(name = "is_favorite")
    private Boolean favorite = false;

    @Column(name = "is_read") // Renamed from `read` to `is_read`
    private Boolean read = false;

    public UserEntryInteraction() {
    }

    public UserEntryInteraction(User user, Entry entry) {
        this.user = user;
        this.entry = entry;
    }

    public UserEntryInteraction(User user, Entry entry, boolean read, boolean favorite, boolean bookmark) {
        this.user = user;
        this.entry = entry;
        this.read = read;
        this.favorite = favorite;
        this.bookmark = bookmark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public String toString() {
        return "UserEntryInteraction{" +
                "id=" + id +
                ", user=" + user +
                ", entry=" + entry +
                ", read=" + read +
                ", favorite=" + favorite +
                ", bookmark=" + bookmark +
                '}';
    }

}
