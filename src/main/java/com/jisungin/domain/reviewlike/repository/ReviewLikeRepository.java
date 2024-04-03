package com.jisungin.domain.reviewlike.repository;

import com.jisungin.domain.review.Review;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    Optional<ReviewLike> findByUserAndReview(User user, Review review);

}
