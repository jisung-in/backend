package com.jisungin.application.talkroom;

import com.jisungin.application.response.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
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

    @Transactional
    public TalkRoomResponse createTalkRoom(TalkRoomCreateServiceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        TalkRoom talkRoom = TalkRoom.create(request.getTitle(), request.getContent(), book, user);
        talkRoomRepository.save(talkRoom);

        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(request.getReadingStatus());

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);

        return TalkRoomResponse.of(user.getName(), talkRoom.getTitle(), talkRoom.getContent(), readingStatus,
                book.getImageUrl(), book.getTitle());
    }

    public PageResponse<TalkRoomQueryResponse> getTalkRooms(TalkRoomSearchServiceRequest search) {
        return talkRoomRepository.getTalkRooms(search);
    }

    @Transactional
    public TalkRoomResponse editTalkRoom(TalkRoomEditServiceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        TalkRoom talkRoom = talkRoomRepository.findByIdWithUserAndBook(request.getId());

        if (!talkRoom.isTalkRoomOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        talkRoom.edit(request);

        talkRoomRoleRepository.deleteAllByTalkRoom(talkRoom);

        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(request.getReadingStatus());

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);

        return TalkRoomResponse.of(user.getName(), talkRoom.getTitle(), talkRoom.getContent(), readingStatus,
                talkRoom.getBook().getImageUrl(), talkRoom.getBook().getTitle());
    }

}
