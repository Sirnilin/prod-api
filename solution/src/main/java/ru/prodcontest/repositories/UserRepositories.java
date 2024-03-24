package ru.prodcontest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prodcontest.models.UserModel;

@Repository
public interface UserRepositories extends JpaRepository<UserModel, Long> {
    UserModel findByLogin(String login);
    UserModel findByEmail(String email);
    UserModel findByPhone(String phone);
}
