package com.hackaton.cheetah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.hackaton.cheetah.*")
@ComponentScan("com.hackaton.cheetah.*")
@EntityScan("com.hackaton.cheetah.*")
public class CheetahServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheetahServerApplication.class, args);
	}

}
