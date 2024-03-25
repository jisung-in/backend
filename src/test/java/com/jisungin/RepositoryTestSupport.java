package com.jisungin;

import com.jisungin.config.QueryDslConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
public abstract class RepositoryTestSupport {

}
