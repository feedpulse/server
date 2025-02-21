package de.feedpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
public class UserEntryInteraction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    private boolean bookmark = false;

    @Column(name = "is_favorite")
    private boolean favorite = false;

    @Column(name = "is_read")
    private boolean read = false;


    protected UserEntryInteraction() {}

    @Builder
    public UserEntryInteraction(User user, Entry entry) {
        this.user = user;
        this.entry = entry;
    }

    @Builder
    public UserEntryInteraction(User user, Entry entry, boolean read, boolean favorite, boolean bookmark) {
        this.user = user;
        this.entry = entry;
        this.read = read;
        this.favorite = favorite;
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
