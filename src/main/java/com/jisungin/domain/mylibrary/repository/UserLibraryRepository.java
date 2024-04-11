package com.jisungin.domain.mylibrary.repository;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.mylibrary.UserLibrary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {

    @Query(
            "SELECT ul.status FROM UserLibrary ul JOIN ul.user u WHERE u.id = :id"
    )
    ReadingStatus findByUserId(@Param("id") Long userId);

    @Query(
            "SELECT ul FROM UserLibrary ul "
                    + "JOIN FETCH ul.book "
                    + "JOIN FETCH ul.user "
                    + "WHERE ul.id = :id"
    )
    Optional<UserLibrary> findByIdWithBookAndUser(@Param("id") Long id);

}
