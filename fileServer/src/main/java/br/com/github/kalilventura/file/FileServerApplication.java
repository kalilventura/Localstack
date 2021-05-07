package br.com.github.kalilventura.file;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initialized");
    }
}
