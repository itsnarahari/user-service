package com.user;

import com.user.config.AuditLoggingInterceptor;
import jakarta.persistence.EntityListeners;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@Slf4j
@EntityListeners({AuditLoggingInterceptor.class})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
		System.out.println(TimeZone.getDefault());
	}

}
