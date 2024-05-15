package com.jisungin.domain.user.repository;

import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByOauthId(OauthId oauthId);

}
