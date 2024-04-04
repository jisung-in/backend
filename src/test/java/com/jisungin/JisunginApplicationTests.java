package com.jisungin;

import com.jisungin.infra.s3.S3FileManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class JisunginApplicationTests {

    @MockBean
    private S3FileManager s3FileManager;

    @Test
    void contextLoads() {
    }

}
