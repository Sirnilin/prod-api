package ru.prodcontest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prodcontest.models.Posts;

@Repository
public interface PostsRepositories extends JpaRepository<Posts, Long> {
    Posts findById(String postsId);
}
