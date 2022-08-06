package com.github.GypsyJR777;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class EveryDayHelperApplication {

	public static void main(String[] args) {
		Arrays.stream(args).forEach(System.out::println);

		SpringApplication.run(EveryDayHelperApplication.class, args);
	}

}
