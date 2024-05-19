package com.jisungin.domain.comment.repository;

import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.commentlike.QCommentLike.commentLike;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.application.comment.response.QCommentQueryResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentQueryResponse> findAllComments(Long talkRoomId) {
        return queryFactory.select(new QCommentQueryResponse(
                        comment.id.as("commentId"),
                        user.name.as("userName"),
                        user.profileImage,
                        comment.content,
                        commentLike.count().as("commentLiKeCount"),
                        comment.registeredDateTime
                ))
                .from(comment)
                .join(comment.talkRoom, talkRoom)
                .join(comment.user, user)
                .leftJoin(commentLike).on(comment.eq(commentLike.comment))
                .groupBy(comment.id)
                .where(comment.talkRoom.id.eq(talkRoomId))
                .orderBy(comment.createDateTime.desc())
                .fetch();
    }

    @Override
    public Long commentTotalCount(Long talkRoomId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.talkRoom, talkRoom)
                .join(comment.user, user)
                .where(comment.talkRoom.id.eq(talkRoomId))
                .fetchOne();
    }

}
