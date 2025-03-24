package de.uni_leipzig.swtp.borna_lecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BornaLeckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BornaLeckerApplication.class, args);
    }
}
