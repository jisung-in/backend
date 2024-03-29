package com.jisungin.domain.comment;

import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_room_id")
    private TalkRoom talkRoom;

    @Lob
    @Column(name = "comment_content")
    private String content;

    @Builder
    private Comment(User user, TalkRoom talkRoom, String content) {
        this.user = user;
        this.talkRoom = talkRoom;
        this.content = content;
    }

    public static Comment create(CommentCreateServiceRequest request, User user, TalkRoom talkRoom) {
        return Comment.builder()
                .content(request.getContent())
                .user(user)
                .talkRoom(talkRoom)
                .build();
    }

    public boolean isCommentOwner(Long userId) {
        return user.isMe(userId);
    }

    public void edit(String requestContent) {
        this.content = requestContent != null ? requestContent : content;
    }

}
