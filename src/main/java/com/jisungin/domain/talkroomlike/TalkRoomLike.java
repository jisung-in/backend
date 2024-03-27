package com.jisungin.domain.talkroomlike;

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
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TalkRoomLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talk_room_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_room_id")
    private TalkRoom talkRoom;

    @Builder
    private TalkRoomLike(User user, TalkRoom talkRoom) {
        this.user = user;
        this.talkRoom = talkRoom;
    }

    public static TalkRoomLike likeTalkRoom(User user, TalkRoom talkRoom) {
        return TalkRoomLike.builder()
                .user(user)
                .talkRoom(talkRoom)
                .build();
    }

}
