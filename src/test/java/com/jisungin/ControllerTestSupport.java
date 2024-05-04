package com.jisungin;

import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.book.BookController;
import com.jisungin.api.comment.CommentController;
import com.jisungin.api.commentlike.CommentLikeController;
import com.jisungin.api.image.ImageController;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.api.review.ReviewController;
import com.jisungin.api.reviewlike.ReviewLikeController;
import com.jisungin.api.search.SearchController;
import com.jisungin.api.talkroom.TalkRoomController;
import com.jisungin.api.talkroomlike.TalkRoomLikeController;
import com.jisungin.api.user.UserController;
import com.jisungin.api.userlibrary.UserLibraryController;
import com.jisungin.application.book.BestSellerService;
import com.jisungin.application.book.BookService;
import com.jisungin.application.comment.CommentService;
import com.jisungin.application.commentlike.CommentLikeService;
import com.jisungin.application.image.ImageService;
import com.jisungin.application.review.ReviewService;
import com.jisungin.application.reviewlike.ReviewLikeService;
import com.jisungin.application.search.SearchService;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroomlike.TalkRoomLikeService;
import com.jisungin.application.user.UserService;
import com.jisungin.application.userlibrary.UserLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = {
        TalkRoomController.class,
        CommentController.class,
        ReviewController.class,
        TalkRoomLikeController.class,
        CommentLikeController.class,
        UserController.class,
        BookController.class,
        ReviewLikeController.class,
        ImageController.class,
        SearchController.class,
        UserLibraryController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthContext authContext;

    @Autowired
    protected WebApplicationContext context;

    @MockBean
    protected MockHttpSession session;

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

    @MockBean
    protected UserService userService;

    @MockBean
    protected BookService bookService;

    @MockBean
    protected BestSellerService bestSellerService;

    @MockBean
    protected ReviewLikeService reviewLikeService;

    @MockBean
    protected ImageService imageService;

    @MockBean
    protected SearchService searchService;

    @MockBean
    protected UserLibraryService userLibraryService;

    @BeforeEach
    void setUp() {
        authContext = Mockito.mock(AuthContext.class);

        BDDMockito.given(session.getAttribute(anyString())).willReturn("1L");

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

}
