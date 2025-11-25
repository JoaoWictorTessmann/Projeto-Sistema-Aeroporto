package sistema.aeroporto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AeroportoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AeroportoApplication.class, args);
		System.out.println("Aeroporto Application started successfully.");
		System.out.println("Access aplication at: http://localhost:8080");
	}

}
