package com.jisungin.application.talkroomlike;

import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroomlike.TalkRoomLike;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TalkRoomLikeService {

    private final TalkRoomLikeRepository talkRoomLikeRepository;
    private final TalkRoomRepository talkRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likeTalkRoom(Long talkRoomId, Long userId) {
        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (talkRoomLikeRepository.findByTalkRoomIdAndUserId(talkRoom.getId(), user.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.LIKE_EXIST);
        }

        TalkRoomLike talkRoomLike = TalkRoomLike.likeTalkRoom(user, talkRoom);

        talkRoomLikeRepository.save(talkRoomLike);
    }


    @Transactional
    public void unLikeTalkRoom(Long talkRoomId, Long userId) {
        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        TalkRoomLike talkRoomLike = talkRoomLikeRepository.findByTalkRoomIdAndUserId(talkRoom.getId(), user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_LIKE_NOT_FOUND));

        talkRoomLikeRepository.delete(talkRoomLike);
    }

}
