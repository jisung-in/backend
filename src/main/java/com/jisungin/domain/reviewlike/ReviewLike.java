package com.jisungin.domain.reviewlike;

import com.jisungin.domain.review.Review;
import com.jisungin.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    private ReviewLike(User user, Review review) {
        this.user = user;
        this.review = review;
    }

    public static ReviewLike likeReview(User user, Review review) {
        return ReviewLike.builder()
                .user(user)
                .review(review)
                .build();
    }

}
