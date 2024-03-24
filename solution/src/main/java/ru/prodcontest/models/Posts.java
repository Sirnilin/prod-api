package ru.prodcontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Posts {
    @Column(name = "postsId", unique = true, length = 100)
    private String id;
    @Column(name = "content")
    private String content;
    @Column(name = "author")
    private String author;
    @Column(name = "tags")
    @ElementCollection
    @CollectionTable(name="post_tags", joinColumns=@JoinColumn(name="post_id"))
    private List<String> tags;
    @Column(name = "createdAt")
    private ZonedDateTime createdAt;
    @Column(name = "likesCount")
    private Integer likesCount;
    @Column(name = "dislikesCount")
    private Integer dislikesCount;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "id")
    private Long hidden_id;


    public Posts(String postsId, String content, String author, List<String> tags, ZonedDateTime createdAt, Integer likesCount, Integer dislikesCount) {
        this.id = postsId;
        this.content = content;
        this.author = author;
        this.tags = tags;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
    }

    public Posts() {
    }

    public String getId() {
        return id;
    }

    public void setId(String postsId) {
        this.id = postsId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(Integer dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public void setHidden_id(Long hiddenId) {
        this.hidden_id = hiddenId;
    }

    public Long getHidden_id() {
        return hidden_id;
    }
}
