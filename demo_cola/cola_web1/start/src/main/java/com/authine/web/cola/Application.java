package com.authine.web.cola;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Starter
 *
 * COLA framework initialization is configured in {@link com.authine.web.cola.config.ColaConfig}
 *
 * @author Frank Zhang
 */
@SpringBootApplication(scanBasePackages = {"com.authine.web.cola","com.alibaba.cola"})
@MapperScan("com.authine.web.cola.repository")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
