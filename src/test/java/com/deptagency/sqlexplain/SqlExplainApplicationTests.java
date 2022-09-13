package com.deptagency.sqlexplain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SqlExplainApplicationTests {

	@Test
	void contextLoads() {
	}

	@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class})
	static class TestConfiguration {
	}

}
