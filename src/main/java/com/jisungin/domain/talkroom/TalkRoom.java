package com.jisungin.domain.talkroom;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.book.Book;
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
public class TalkRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talk_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "talk_room_title", length = 20)
    private String title;

    @Lob
    @Column(name = "talk_room_content", length = 2000)
    private String content;

    @Builder
    private TalkRoom(Book book, User user, String title, String content) {
        this.book = book;
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public static TalkRoom create(String title, String content, Book book, User user) {
        return TalkRoom.builder()
                .book(book)
                .user(user)
                .title(title)
                .content(content)
                .build();
    }

    public void edit(String requestTitle, String requestContent) {
        this.title = requestTitle != null ? requestTitle : title;
        this.content = requestContent != null ? requestContent : content;
    }

    public boolean isTalkRoomOwner(Long userId) {
        return user.isMe(userId);
    }

}
