package br.com.github.kalilventura.eventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventosApplication {

	public static void main(String[] args) {
		System.out.println("Initializing system");
		SpringApplication.run(EventosApplication.class, args);
		System.out.println("Initialized");
	}

}
