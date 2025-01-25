package com.brandyodhiambo.bibleApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class BibleApiApplication {

	public static void main(String[] args) {
		//SpringApplication.run(BibleApiApplication.class, args);
		SpringApplication app = new SpringApplication(BibleApiApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port","8090"));
		app.run(args);
	}

}
