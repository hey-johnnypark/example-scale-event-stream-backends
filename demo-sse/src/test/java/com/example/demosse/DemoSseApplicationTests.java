package com.example.demosse;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;


@SpringBootTest
class DemoSseApplicationTests {

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, "sse");


	@Test
	void contextLoads() throws InterruptedException {
		Thread.sleep(10000);
	}

}
