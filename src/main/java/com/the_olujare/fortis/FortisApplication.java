package com.the_olujare.fortis;

import com.the_olujare.fortis.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class FortisApplication {
	public static void main(String[] args) {
		SpringApplication.run(FortisApplication.class, args);
	}
}