package com.jisungin.domain.commentimage.repository;

import static com.jisungin.domain.commentimage.QCommentImage.commentImage;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.jisungin.domain.commentimage.CommentImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentImageRepositoryImpl implements CommentImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, List<CommentImage>> findCommentImageByIds(List<Long> commentIds) {
        return queryFactory.select(commentImage.comment.id, commentImage.imageUrl)
                .from(commentImage)
                .where(commentImage.comment.id.in(commentIds))
                .transform(groupBy(commentImage.comment.id).as(list(commentImage)));
    }

}
