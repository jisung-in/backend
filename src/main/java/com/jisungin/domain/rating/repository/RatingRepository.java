package com.jisungin.domain.rating.repository;

import com.jisungin.domain.rating.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long>, RatingRepositoryCustom {

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.book.isbn = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") String bookId);

}
