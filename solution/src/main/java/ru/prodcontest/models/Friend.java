package ru.prodcontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "friend")
public class Friend {
    @Column(name = "friend_login")
    private String login;

    @Column(name = "date_added")
    private ZonedDateTime addedAt;
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public Friend() {
    }

    public Friend(String login, ZonedDateTime dateAdded) {
        this.login = login;
        this.addedAt = dateAdded;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public ZonedDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(ZonedDateTime dateAdded) {
        this.addedAt = dateAdded;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}