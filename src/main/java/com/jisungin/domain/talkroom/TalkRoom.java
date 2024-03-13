package com.jisungin.domain.talkroom;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TalkRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talk_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Lob
    @Column(name = "talk_room_content", length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "talk_room_reading_status")
    private ReadingStatus status;

    @Builder
    private TalkRoom(Book book, String content, ReadingStatus status) {
        this.book = book;
        this.content = content;
        this.status = status;
    }

}
