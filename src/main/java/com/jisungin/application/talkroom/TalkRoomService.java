package com.jisungin.application.talkroom;

import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TalkRoomService {

    private final TalkRoomRepository talkRoomRepository;
    private final TalkRoomRoleRepository talkRoomRoleRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // TODO. 토큰 정보를 가져오는 기능을 구현하면 변경할 예정
    @Transactional
    public TalkRoomResponse createTalkRoom(TalkRoomCreateServiceRequest request, String userEmail) {
        User user = userRepository.findByName(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        TalkRoom talkRoom = TalkRoom.create(request, book, user);
        talkRoomRepository.save(talkRoom);

        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(request.getReadingStatus());

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);

        return TalkRoomResponse.of(talkRoom, readingStatus);
    }

}
