package com.jisungin.domain.comment.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.comment.response.CommentQueryResponse;

public interface CommentRepositoryCustom {

    PageResponse<CommentQueryResponse> findAllComments(Long talkRoomId);

}
