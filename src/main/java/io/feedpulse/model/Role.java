package io.feedpulse.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "roles")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private io.feedpulse.model.enums.Role name;

    public Role() {
    }

    public Role(io.feedpulse.model.enums.Role name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public io.feedpulse.model.enums.Role getName() {
        return name;
    }

    public void setName(io.feedpulse.model.enums.Role name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
