package com.jisungin.application.talkroom;

import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SearchServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomimage.TalkRoomImage;
import com.jisungin.domain.talkroomimage.repository.TalkRoomImageRepository;
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
    private final CommentRepository commentRepository;
    private final TalkRoomImageRepository talkRoomImageRepository;

    @Transactional
    public TalkRoomResponse createTalkRoom(TalkRoomCreateServiceRequest request, AuthContext authContext) {
        User user = userRepository.findById(authContext.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        TalkRoom talkRoom = TalkRoom.create(request.getTitle(), request.getContent(), book, user);
        talkRoomRepository.save(talkRoom);

        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(request.getReadingStatus());

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);

        TalkRoomResponse response = TalkRoomResponse.of(user.getName(), talkRoom.getTitle(),
                talkRoom.getContent(), readingStatus,
                book.getImageUrl(), book.getTitle());

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            request.getImageUrls().stream()
                    .map(url -> TalkRoomImage.createImages(talkRoom, url))
                    .forEach(talkRoomImageRepository::save);

            List<String> imageUrls = talkRoomImageRepository.findByTalkRoomIdWithImageUrl(
                    talkRoom.getId());

            response.addTalkRoomImages(imageUrls);
        }

        return response;
    }

    public PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(SearchServiceRequest search) {
        return talkRoomRepository.findAllTalkRoom(search.getOffset(), search.getSize(), search.getOrder(),
                search.getQuery());
    }

    public TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId) {
        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        return talkRoomRepository.findOneTalkRoom(talkRoom.getId());
    }

    @Transactional
    public TalkRoomResponse editTalkRoom(TalkRoomEditServiceRequest request, AuthContext authContext) {
        User user = userRepository.findById(authContext.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TalkRoom talkRoom = talkRoomRepository.findByIdWithUserAndBook(request.getId());

        if (!talkRoom.isTalkRoomOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        talkRoom.edit(request.getTitle(), request.getContent());

        talkRoomRoleRepository.deleteAllByTalkRoom(talkRoom);

        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(request.getReadingStatus());

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);

        if (request.getNewImage() != null && !request.getNewImage().isEmpty()) {
            request.getNewImage().stream().map(url -> TalkRoomImage.createImages(talkRoom, url))
                    .forEach(talkRoomImageRepository::save);
        }

        if (request.getRemoveImage() != null && !request.getRemoveImage().isEmpty()) {
            request.getRemoveImage().stream().map(s -> talkRoomImageRepository.findByTalkRoomAndImageUrl(talkRoom, s))
                    .forEach(talkRoomImageRepository::deleteAll);
        }

        TalkRoomResponse response = TalkRoomResponse.of(user.getName(), talkRoom.getTitle(),
                talkRoom.getContent(), readingStatus,
                talkRoom.getBook().getImageUrl(), talkRoom.getBook().getTitle());

        List<String> images = talkRoomImageRepository.findByTalkRoomIdWithImageUrl(talkRoom.getId());
        response.addTalkRoomImages(images);

        return response;
    }

    @Transactional
    public void deleteTalkRoom(Long talkRoomId, AuthContext authContext) {
        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        User user = userRepository.findById(authContext.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!talkRoom.isTalkRoomOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        commentRepository.findByTalkRoom(talkRoom).ifPresent(commentRepository::delete);

        List<TalkRoomImage> images = talkRoomImageRepository.findByTalkRoom(talkRoom);
        if (images != null) {
            talkRoomImageRepository.deleteAll(images);
        }
        talkRoomRoleRepository.deleteAllByTalkRoom(talkRoom);
        talkRoomRepository.delete(talkRoom);
    }

}
