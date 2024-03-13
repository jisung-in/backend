package com.jisungin.domain.comment;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.user.User;
import jakarta.persistence.*;
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

}
