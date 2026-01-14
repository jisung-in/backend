package com.jisungin.domain.commentimage;

import com.jisungin.domain.comment.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    private CommentImage(String imageUrl, Comment comment) {
        this.imageUrl = imageUrl;
        this.comment = comment;
    }

    public static CommentImage createImages(Comment comment, String imageUrl) {
        return CommentImage.builder()
                .comment(comment)
                .imageUrl(imageUrl)
                .build();
    }
}
