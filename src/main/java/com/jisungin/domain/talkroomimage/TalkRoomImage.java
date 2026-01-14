package com.jisungin.domain.talkroomimage;

import com.jisungin.domain.talkroom.TalkRoom;
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
public class TalkRoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_room_id")
    private TalkRoom talkRoom;

    @Builder
    private TalkRoomImage(Long id, String imageUrl, TalkRoom talkRoom) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.talkRoom = talkRoom;
    }

    public static TalkRoomImage createImages(TalkRoom talkRoom, String imageUrl) {
        return TalkRoomImage.builder()
                .talkRoom(talkRoom)
                .imageUrl(imageUrl)
                .build();
    }

}
