package com.jisungin.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.oauth.AuthArgumentResolver;
import com.jisungin.api.oauth.AuthContext;
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

    protected AuthContext authContext;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        authContext = Mockito.mock(AuthContext.class);

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .setCustomArgumentResolvers(new AuthArgumentResolver(authContext))
                .apply(documentationConfiguration(provider))
                .build();
    }

    protected abstract Object initController();

}
