package com.jisungin.docs;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.support.AuthArgumentResolver;
import com.jisungin.api.support.GuestOrAuthArgumentResolver;
import com.jisungin.api.support.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected HttpSession httpSession;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        httpSession = Mockito.mock(HttpSession.class);

        given(httpSession.getAttribute(anyString())).willReturn(createSessionUser());

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .setCustomArgumentResolvers(new AuthArgumentResolver(httpSession),
                        new GuestOrAuthArgumentResolver(httpSession))
                .apply(documentationConfiguration(provider))
                .build();
    }

    protected abstract Object initController();

    private SessionUser createSessionUser() {
        return SessionUser.builder()
                .id(1L)
                .build();
    }

}
