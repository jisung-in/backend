package com.jisungin.domain.talkroom;

import com.jisungin.domain.ReadingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class TalkRoomRole {

    @Id
    @Column(name = "talk_room_role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_room_id")
    private TalkRoom talkRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "talk_room_reading_status")
    private ReadingStatus readingStatus;

    @Builder
    private TalkRoomRole(TalkRoom talkRoom, ReadingStatus readingStatus) {
        this.talkRoom = talkRoom;
        this.readingStatus = readingStatus;
    }

    public static TalkRoomRole roleCreate(TalkRoom talkRoom, ReadingStatus status) {
        return TalkRoomRole.builder()
                .talkRoom(talkRoom)
                .readingStatus(status)
                .build();
    }
}
