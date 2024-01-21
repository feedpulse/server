package dev.feder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    private String password;

    @NonNull
    @Column(unique = true)
    private String email;

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

    public User(@NonNull String username, @NonNull String password, @NonNull String email, Set<Role> roles, List<Feed> feeds, List<UserEntryInteraction> userEntryInteractions) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.feeds = feeds;
        this.userEntryInteractions = userEntryInteractions;
    }

    protected User() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public List<UserEntryInteraction> getUserEntryInteractions() {
        return userEntryInteractions;
    }

    public void setUserEntryInteractions(List<UserEntryInteraction> userEntryInteractions) {
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

    public static class UserBuilder {
        @NonNull
        private String username;
        @NonNull
        private String password;
        @NonNull
        private String email;
        @NonNull
        private Set<Role> roles;
        @NonNull
        private List<Feed> feeds;
        @NonNull
        private List<UserEntryInteraction> userEntryInteractions;

        public UserBuilder setUsername(@NonNull String username) {
            this.username = username;
            return this;
        }

        public UserBuilder setPassword(@NonNull String password) {
            this.password = password;
            return this;
        }

        public UserBuilder setEmail(@NonNull String email) {
            this.email = email;
            return this;
        }

        public UserBuilder setRoles(@NonNull Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public UserBuilder addRole(@NonNull Role role) {
            this.roles.add(role);
            return this;
        }

        public UserBuilder setFeeds(@NonNull List<Feed> feeds) {
            this.feeds = feeds;
            return this;
        }

        public UserBuilder setUserEntryInteractions(@NonNull List<UserEntryInteraction> userEntryInteractions) {
            this.userEntryInteractions = userEntryInteractions;
            return this;
        }

        public User build() {
            return new User(username, password, email, roles, feeds, userEntryInteractions);
        }
    }
}
