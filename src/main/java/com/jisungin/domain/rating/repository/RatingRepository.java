package com.jisungin.domain.rating.repository;

import com.jisungin.domain.book.Book;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long>, RatingRepositoryCustom {

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.book.isbn = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") String bookId);

    Optional<Rating> findRatingByUserAndBook(User user, Book book);

    boolean existsByUserAndBook(User user, Book book);

}
