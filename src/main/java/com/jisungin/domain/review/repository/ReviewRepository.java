package com.jisungin.domain.review.repository;

import com.jisungin.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.isbn = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") String bookId);

}
