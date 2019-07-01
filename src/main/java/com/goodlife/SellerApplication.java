package com.goodlife;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.goodlife.service.KeywordTrackerService;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = { "com.goodlife", "com.goodlife.api", "com.goodlife.model" })
public class SellerApplication {

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
//		Thread keywordTrackerThread = new Thread(new KeywordTrackerService ());
//		keywordTrackerThread.start();	
	}
	
	private static Class<SellerApplication> applicationClass = SellerApplication.class;

	class ExitException extends RuntimeException implements ExitCodeGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public int getExitCode() {
			return 10;
		}

	}
}
