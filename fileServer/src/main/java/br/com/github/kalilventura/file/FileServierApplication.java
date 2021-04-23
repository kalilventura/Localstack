package br.com.github.kalilventura.eventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileServierApplication {

	public static void main(String[] args) {
		System.out.println("Trying to initialize system ...");
		SpringApplication.run(FileServierApplication.class, args);
		System.out.println("Initialized");
	}

}
