package co.plany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// ELIMINADO: Ya no importa Bean ni BCryptPasswordEncoder aquí
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder; // Ya no necesario aquí

@SpringBootApplication
public class PlanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanyApplication.class, args);
    }

}
