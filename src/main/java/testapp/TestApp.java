package testapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by adam on 3/6/2017.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class TestApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApp.class, args);
    }
}
