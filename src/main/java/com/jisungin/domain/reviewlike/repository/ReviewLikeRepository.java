package com.jisungin.domain.reviewlike.repository;

import com.jisungin.domain.review.Review;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    Optional<ReviewLike> findByUserAndReview(User user, Review review);

    @Query("select rl.review.id from ReviewLike rl where rl.user.id = :userId")
    List<Long> findReviewIdsByUserId(@Param("userId") Long userId);

    @Query("select rl.review.id from ReviewLike rl where rl.review.id in :reviewIds and rl.user.id = :userId")
    List<Long> findLikeReviewByReviewId(Long userId, List<Long> reviewIds);

}
