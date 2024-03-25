package com.jisungin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.comment.CommentController;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.api.review.ReviewController;
import com.jisungin.api.talkroom.TalkRoomController;
import com.jisungin.application.comment.CommentService;
import com.jisungin.application.review.ReviewService;
import com.jisungin.application.talkroom.TalkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        TalkRoomController.class,
        CommentController.class,
        ReviewController.class
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

}
