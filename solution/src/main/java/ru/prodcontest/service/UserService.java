package ru.prodcontest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.prodcontest.models.Friend;
import ru.prodcontest.models.Posts;
import ru.prodcontest.models.UserModel;
import ru.prodcontest.repositories.CountriesRepositories;
import ru.prodcontest.repositories.PostsRepositories;
import ru.prodcontest.repositories.UserRepositories;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepositories userRepositories;
    private final PasswordEncoder passwordEncoder;
    private final CountriesRepositories countriesRepositories;
    private final PostsRepositories postsRepositories;

    public int createUser(String login, String email, String password, String countryCode, Boolean isPublic, String phone, String image) {
        if (areRequiredFieldsEmpty(login, email, password, countryCode, isPublic)) {
            return 2;
        }

        if (!isPasswordValid(password)) {
            return 5; // Код для неправильного формата пароля
        }

        if (!isLoginValid(login) || isLoginTooLong(login)) {
            return 6; // Код для неправильного формата логина
        }

        if (isEmailValid(email)) {
            return 7; // Код для превышения максимальной длины логина
        }

        if (!isCountryCodeValid(countryCode)) {
            return 8; // Неправильный формат countryCode
        }
        if(countriesRepositories.findByAlpha2(countryCode.toUpperCase()) == null){
            return 8;
        }
        if(phone != null){
            if (!isPhoneValid(phone)) {
                return 9; // Неправильный формат номера телефона
            }
        }

        if(image != null){
            if (isImageTooLong(image)) {
                return 10; // Слишком длинная ссылка на изображение
            }
        }

        if (isUserAlreadyExists(email, login, phone)) {
            return 3;
        }

        UserModel user = new UserModel();
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCountryCode(countryCode);
        user.setIsPublic(isPublic);
        if(phone != null){
            user.setPhone(phone);
        }
        if(image != null){
            user.setImage(image);
        }
        user.setLastPasswordUpdate(LocalDateTime.now());
        userRepositories.save(user);

        return 0;
    }

    private boolean areRequiredFieldsEmpty(String login, String email, String password, String countryCode, Boolean isPublic) {
        return login == null || email == null || password == null || countryCode == null || isPublic == null || email.isEmpty() || login.isEmpty() || countryCode.isEmpty();
    }

    private boolean isPasswordValid(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        return passwordMatcher.matches();
    }

    private boolean isLoginValid(String login) {
        Pattern loginPattern = Pattern.compile("[a-zA-Z0-9-]+");
        Matcher loginMatcher = loginPattern.matcher(login);
        return loginMatcher.matches();
    }

    private boolean isLoginTooLong(String login) {
        return login.length() > 30;
    }

    private boolean isCountryCodeValid(String countryCode) {
        return countryCode.matches("[a-zA-Z]{2}");
    }

    private boolean isPhoneValid(String phone) {
        return phone.matches("\\+\\d+") && phone.length() <= 20;
    }

    private boolean isImageTooLong(String image) {
        return image.length() > 200 || image.isEmpty();
    }

    private boolean isEmailValid(String email){
        return email.length() > 50;
    }

    private boolean isUserAlreadyExists(String email, String login, String phone) {
        boolean userWithEmailExists = userRepositories.findByEmail(email) != null;
        boolean userWithLoginExists = userRepositories.findByLogin(login) != null;
        boolean userWithPhoneExists = false;
        if (phone != null){
            userWithPhoneExists = userRepositories.findByPhone(phone) != null;
        }

        return userWithEmailExists || userWithLoginExists || userWithPhoneExists;
    }

    public String getStoredPassword(String login){
        UserModel user = userRepositories.findByLogin(login);
        return user.getPassword();
    }

    public UserModel getUserByLogin(String login){
        return userRepositories.findByLogin(login);
    }

    public int updateUserInfo(String countryCode, Boolean isPublic, String phone, String image, UserModel user){
        System.out.println(image);
        if(countryCode != null){
            if(!isCountryCodeValid(countryCode)){
                return 1;
            }else{
                if(countriesRepositories.findByAlpha2(countryCode.toUpperCase()) == null){
                    return 1;
                }
                user.setCountryCode(countryCode);
            }
        }
        if(phone != null){
            if(!isPhoneValid(phone)){
                return 2;
            }else {
                if (userRepositories.findByPhone(phone) != null && userRepositories.findByPhone(phone) != user){
                    return 4;
                }
                user.setPhone(phone);
            }
        }
        if(image != null){
            if(isImageTooLong(image)){
                return 3;
            }else {
                user.setImage(image);
            }
        }
        if(isPublic != null){
            user.setIsPublic(isPublic);
        }
        userRepositories.save(user);
        return 0;
    }

    public UserModel getUserProfileByLogin(String login, UserModel currentUser){
        UserModel user = userRepositories.findByLogin(login);
        if(user.getIsPublic() || user == currentUser || isFriend(currentUser.getLogin(), user)){
            return user;
        }else{
            return null;
        }
    }

    public int updatePassword(String oldPassword, String newPassword, UserModel userModel){
        String storedPassword = userModel.getPassword();
        if(passwordEncoder.matches(oldPassword, storedPassword)){
            if(isPasswordValid(newPassword)){
                userModel.setPassword(passwordEncoder.encode(newPassword));
                userModel.setLastPasswordUpdate(LocalDateTime.now());
                userRepositories.save(userModel);
                return 0;
            }
            return 2;
        }
        return 1;
    }

    public Boolean addFriend(String login, UserModel user){
        if(userRepositories.findByLogin(login) != null){
            if(isFriend(login, user) || userRepositories.findByLogin(login) == user){
                return true;
            }
            user.addFriend(login);
            userRepositories.save(user);
            return true;
        }
        return false;
    }

    public Boolean removeFriend(String login, UserModel user){
        if(userRepositories.findByLogin(login) != null){
            if(!isFriend(login, user)){
                return true;
            }
            user.removeFriend(login);
            userRepositories.save(user);
            return true;
        }
        return false;
    }

    public List<Friend> getUserFriendsWithPagination(UserModel user, int limit, int offset) {
        List<Friend> result = new ArrayList<>();

        Set<Friend> friends = user.getFriends();

        List<Friend> sortedFriends = new ArrayList<>(friends);
        sortedFriends.sort(Comparator.comparing(Friend::getAddedAt).reversed());

        int startIndex = Math.min(offset, sortedFriends.size());
        int endIndex = Math.min(startIndex + limit, sortedFriends.size());

        for (int i = startIndex; i < endIndex; i++) {
            result.add(sortedFriends.get(i));
        }

        return result;
    }

    private Boolean isFriend(String login, UserModel user){
        Set<Friend> friends = user.getFriends();
        for (Friend friend : friends) {
            if (friend.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }
    public Posts createPost(UserModel user, String content, List<String> tags){
        if(content.length() > 1000){
            return null;
        }
        for (String tag : tags) {
            if (tag.length() > 20) {
                return null;
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String currentTime = ZonedDateTime.now().format(formatter);
        Posts posts = new Posts();
        while (true){
            UUID postsId = UUID.randomUUID();
            String tempId = postsId.toString();
            if(tempId.length() > 100){
                tempId = tempId.substring(0, 100);
            }
            if(postsRepositories.findById(tempId) == null){
                posts.setId(tempId);
                posts.setAuthor(user.getLogin());
                posts.setCreatedAt(ZonedDateTime.parse(currentTime, formatter));
                posts.setContent(content);
                posts.setTags(tags);
                posts.setDislikesCount(0);
                posts.setLikesCount(0);
                user.newPost(posts);
                userRepositories.save(user);
                return posts;
            }
        }
    }

    public Posts getPostById(String postId, UserModel user){
        Posts post = postsRepositories.findById(postId);
        if(post == null){
            return null;
        }
        UserModel author = userRepositories.findByLogin(post.getAuthor());
        if(author.getIsPublic()){
            return post;
        }
        if(isFriend(user.getLogin(), author)){
            return post;
        }
        return null;
    }

    public List<Posts> getAllUserPosts(UserModel user, int limit, int offset){
        List<Posts> result = new ArrayList<>();

        List<Posts> userPosts = user.getPosts();
        userPosts.sort(Comparator.comparing(Posts::getCreatedAt).reversed());

        int startIndex = Math.min(offset, userPosts.size());
        int endIndex = Math.min(startIndex + limit, userPosts.size());

        for (int i = startIndex; i < endIndex; i++) {
            result.add(userPosts.get(i));
        }

        return result;
    }

    public List<Posts> getAllUserPosts(String login, UserModel user, int limit, int offset){
        UserModel author = userRepositories.findByLogin(login);
        if(author.getIsPublic() || isFriend(user.getLogin(), author)){
            List<Posts> result = new ArrayList<>();

            List<Posts> userPosts = author.getPosts();
            userPosts.sort(Comparator.comparing(Posts::getCreatedAt).reversed());

            int startIndex = Math.min(offset, userPosts.size());
            int endIndex = Math.min(startIndex + limit, userPosts.size());

            for (int i = startIndex; i < endIndex; i++) {
                result.add(userPosts.get(i));
            }

            return result;
        }

        throw new RuntimeException("Пользователь не найден или у вас нет прав на просмотр его постов.");
    }

    public Posts likePost(UserModel user, String postId){
        Posts post = postsRepositories.findById(postId);
        if(post == null){
            return null;
        }
        UserModel author = userRepositories.findByLogin(post.getAuthor());
        if(author.getIsPublic() || isFriend(user.getLogin(), author)){
            if(!user.getLikePosts().contains(postId)){
                post.setLikesCount(post.getLikesCount() + 1);
                user.getLikePosts().add(postId);
                if(user.getDislikePosts().contains(postId)){
                    post.setDislikesCount(post.getDislikesCount() - 1);
                    user.getDislikePosts().remove(postId);
                }
                userRepositories.save(user);
                postsRepositories.save(post);
            }
            return post;
        }
        return null;
    }

    public Posts dislikePost(UserModel user, String postId){
        Posts post = postsRepositories.findById(postId);
        if(post == null){
            return null;
        }
        UserModel author = userRepositories.findByLogin(post.getAuthor());
        if(author.getIsPublic() || isFriend(user.getLogin(), author)){
            if(!user.getDislikePosts().contains(postId)){
                post.setDislikesCount(post.getDislikesCount() + 1);
                user.getDislikePosts().add(postId);
                if(user.getLikePosts().contains(postId)){
                    post.setLikesCount(post.getLikesCount() - 1);
                    user.getLikePosts().remove(postId);
                }
                userRepositories.save(user);
                postsRepositories.save(post);
            }
            return post;
        }
        return null;
    }
}
