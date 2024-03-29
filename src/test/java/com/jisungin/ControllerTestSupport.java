package com.jisungin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.comment.CommentController;
import com.jisungin.api.commentlike.CommentLikeController;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.api.review.ReviewController;
import com.jisungin.api.talkroom.TalkRoomController;
import com.jisungin.api.talkroomlike.TalkRoomLikeController;
import com.jisungin.application.comment.CommentService;
import com.jisungin.application.commentlike.CommentLikeService;
import com.jisungin.application.review.ReviewService;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroomlike.TalkRoomLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        TalkRoomController.class,
        CommentController.class,
        ReviewController.class,
        TalkRoomLikeController.class,
        CommentLikeController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthContext authContext;

    @MockBean
    protected TalkRoomService talkRoomService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected ReviewService reviewService;

    @MockBean
    protected TalkRoomLikeService talkRoomLikeService;

    @MockBean
    protected CommentLikeService commentLikeService;

}
