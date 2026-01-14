package com.jisungin.domain.comment.repository;

import com.jisungin.application.comment.response.CommentQueryResponse;
import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentQueryResponse> findAllComments(Long talkRoomId);

    Long commentTotalCount(Long talkRoomId);
}
