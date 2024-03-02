package io.feedpulse.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Data
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    private String password;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @Column(nullable = false)
    private LocalDate dateCreated = LocalDate.now();

    @NonNull
    @Column(nullable = false)
    private LocalDate dateUpdated = LocalDate.now();

    @NonNull
    @Column(nullable = false)
    private LocalDate dateLastLogin = LocalDate.now();

    private boolean isUserEnabled = false;

    private boolean isUserLocked = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_feeds",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "feed_id"))
    private List<Feed> feeds = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserEntryInteraction> userEntryInteractions = new ArrayList<>();

    protected User() {}

    @Builder
    public User(@NonNull String username, @NonNull String password, @NonNull String email, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    @Builder
    public User(@NonNull String username, @NonNull String password, @NonNull String email, Set<Role> roles, List<Feed> feeds, List<UserEntryInteraction> userEntryInteractions) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.feeds = feeds;
        this.userEntryInteractions = userEntryInteractions;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                ", feeds='" + feeds + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id.equals(user.id);
    }
}
