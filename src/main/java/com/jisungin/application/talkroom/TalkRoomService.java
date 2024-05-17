package com.jisungin.application.talkroom;

import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomPageResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
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
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private final TalkRoomLikeRepository talkRoomLikeRepository;

    @Transactional
    public TalkRoomFindOneResponse createTalkRoom(TalkRoomCreateServiceRequest request, Long userId,
                                                  LocalDateTime registeredDateTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        TalkRoom talkRoom = TalkRoom.create(request.getTitle(), request.getContent(), book, user, registeredDateTime);
        talkRoomRepository.save(talkRoom);

        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(request.getReadingStatus());

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            request.getImageUrls().stream()
                    .map(url -> TalkRoomImage.createImages(talkRoom, url))
                    .forEach(talkRoomImageRepository::save);
        }

        List<String> imageUrls = talkRoomImageRepository.findByTalkRoomIdWithImageUrl(
                talkRoom.getId());

        return TalkRoomFindOneResponse.create(talkRoom.getId(), user.getProfileImage(), user.getName(),
                talkRoom.getTitle(), talkRoom.getContent(), book.getTitle(), book.getAuthors(), book.getThumbnail(),
                readingStatus,
                talkRoom.getRegisteredDateTime(), imageUrls);
    }

    public TalkRoomPageResponse findAllTalkRoom(long offset, Integer size, String order, String search, String day,
                                                Long userId, LocalDateTime now) {
        List<TalkRoomQueryResponse> talkRooms = talkRoomRepository.findAllTalkRoom(offset,
                size, order, search, day, now);

        List<Long> talkRoomIds = talkRooms.stream().map(TalkRoomQueryResponse::getId).toList();

        Map<Long, List<ReadingStatus>> talkRoomRoleMap = talkRoomRoleRepository.findTalkRoomRoleByIds(talkRoomIds);

        Long totalCount = talkRoomRepository.countTalkRooms(day, now);

        List<TalkRoomFindAllResponse> response = TalkRoomFindAllResponse.create(talkRooms,
                talkRoomRoleMap);

        List<Long> likeTalkRoomIds = (userId != null)
                ? talkRoomLikeRepository.findLikeTalkRoomIdsByUserId(userId, talkRoomIds)
                : Collections.emptyList();

        return TalkRoomPageResponse.of(PageResponse.of(talkRooms.size(), totalCount, response), likeTalkRoomIds);
    }

    public TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId, Long userId) {
        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        TalkRoomQueryResponse queryTalkRoom = talkRoomRepository.findOneTalkRoom(talkRoom.getId());

        List<ReadingStatus> readingStatuses = talkRoomRoleRepository.findTalkRoomRoleByTalkRoomId(
                queryTalkRoom.getId());

        List<String> images = talkRoomImageRepository.findTalkRoomImages(talkRoom.getId());

        boolean exists = (userId != null)
                ? talkRoomLikeRepository.existsTalkRoomLike(talkRoom.getId(), userId)
                : false;

        return TalkRoomFindOneResponse.create(queryTalkRoom, readingStatuses, images, exists);
    }

    @Transactional
    public void editTalkRoom(TalkRoomEditServiceRequest request, Long userId) {
        User user = userRepository.findById(userId)
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
            request.getRemoveImage().stream()
                    .map(url -> talkRoomImageRepository.findByTalkRoomAndImageUrl(talkRoom, url))
                    .forEach(talkRoomImageRepository::deleteAll);
        }
    }

    @Transactional
    public void deleteTalkRoom(Long talkRoomId, Long userId) {
        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!talkRoom.isTalkRoomOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        commentRepository.findByTalkRoom(talkRoom).ifPresent(commentRepository::delete);

        List<TalkRoomImage> images = talkRoomImageRepository.findByTalkRoom(talkRoom);
        if (images != null && !images.isEmpty()) {
            talkRoomImageRepository.deleteAll(images);
        }
        talkRoomRoleRepository.deleteAllByTalkRoom(talkRoom);
        talkRoomRepository.delete(talkRoom);
    }

}
