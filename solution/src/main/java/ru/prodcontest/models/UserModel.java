package ru.prodcontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;
    @Column(name = "login", unique = true)
    private String login;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password", length = 1000)
    @JsonIgnore
    private String password;
    @Column(name = "countryCode")
    private String countryCode;
    @Column(name = "isPublic")
    private Boolean isPublic;
    @Column(name = "phone", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String phone;
    @Column(name = "image", length = 200)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String image;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Set<Friend> friends = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<Posts> posts;
    @Column(name = "likePosts")
    @ElementCollection
    @JsonIgnore
    private List<String> likePosts;
    @Column(name = "dislikePosts")
    @ElementCollection
    @JsonIgnore
    private List<String> dislikePosts;
    @Column(name = "LastPasswordUpdate")
    @JsonIgnore
    private LocalDateTime LastPasswordUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void addFriend(String login){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String currentTime = ZonedDateTime.now().format(formatter);
        Friend friend = new Friend(login, ZonedDateTime.parse(currentTime, formatter));
        this.friends.add(friend);
    }

    public void removeFriend(String login){
        Friend friendToRemove = null;
        for (Friend friend : this.friends) {
            if (friend.getLogin().equals(login)) {
                friendToRemove = friend;
                break;
            }
        }
        if (friendToRemove != null) {
            this.friends.remove(friendToRemove);
        }
    }

    public void newPost(Posts posts){
        this.posts.add(posts);
    }

    public Set<Friend> getFriends() {
        return friends;
    }

    public List<Posts> getPosts() {
        return posts;
    }

    public void setPosts(List<Posts> posts) {
        this.posts = posts;
    }

    public List<String> getLikePosts() {
        return likePosts;
    }

    public void setLikePosts(List<String> likePosts) {
        this.likePosts = likePosts;
    }

    public List<String> getDislikePosts() {
        return dislikePosts;
    }

    public void setDislikePosts(List<String> dislikePosts) {
        this.dislikePosts = dislikePosts;
    }
    @JsonIgnore
    public LocalDateTime getLastPasswordUpdate() {
        return LastPasswordUpdate;
    }

    public void setLastPasswordUpdate(LocalDateTime lastPasswordUpdate) {
        LastPasswordUpdate = lastPasswordUpdate;
    }
}

