package com.jisungin;

import com.jisungin.infra.s3.S3FileManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@SpringBootTest
public abstract class ServiceTestSupport {

    @MockBean
    protected S3FileManager s3FileManager;

    @MockBean
    protected ClientRegistrationRepository clientRegistrationRepository;
}
