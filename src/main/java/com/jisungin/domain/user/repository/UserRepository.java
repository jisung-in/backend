package com.jisungin.domain.user.repository;

import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.user.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String userEmail);

    Optional<User> findByOauthId(OauthId oauthId);

}
