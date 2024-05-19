package com.jisungin.domain.commentimage.repository;

import com.jisungin.domain.commentimage.CommentImage;
import java.util.List;
import java.util.Map;

public interface CommentImageRepositoryCustom {

    Map<Long, List<CommentImage>> findCommentImageByIds(List<Long> commentIds);

}
