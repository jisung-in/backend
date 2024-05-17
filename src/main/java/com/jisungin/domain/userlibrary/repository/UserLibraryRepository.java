package com.jisungin.domain.userlibrary.repository;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.userlibrary.UserLibrary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long>, UserLibraryRepositoryCustom {

    @Query(
            "SELECT ul.status FROM UserLibrary ul JOIN ul.user u WHERE u.id = :id"
    )
    Optional<ReadingStatus> findByUserId(@Param("id") Long userId);

    @Query(
            "SELECT ul FROM UserLibrary ul "
                    + "JOIN FETCH ul.book "
                    + "JOIN FETCH ul.user "
                    + "WHERE ul.id = :id"
    )
    Optional<UserLibrary> findByIdWithBookAndUser(@Param("id") Long id);

    @Query(
            "SELECT ul FROM UserLibrary ul "
                    + "JOIN FETCH ul.book "
                    + "JOIN FETCH ul.user "
                    + "WHERE ul.user.id = :userId AND "
                    + "ul.book.isbn = :bookIsbn"
    )
    Optional<UserLibrary> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookIsbn") String bookIsbn);

}
