package de.feedpulse.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
public class Keyword implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String keyword;

    @JsonManagedReference
    @ManyToMany(mappedBy = "keywords")
    private Set<Entry> entry;

    protected Keyword() {}

    @Builder
    public Keyword(String keyword) {
        this.keyword = keyword;
    }

    @Builder
    public Keyword(String keyword, Set<Entry> entry) {
        this.keyword = keyword;
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "keyword='" + keyword + '\'' +
                '}';
    }

}
