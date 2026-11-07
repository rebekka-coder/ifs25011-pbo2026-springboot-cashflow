package org.delcom.ifs25011pbo2026springbootcashflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point aplikasi Cashflow. Memuat file .env terlebih dahulu
 * agar APP_PORT tersedia sebelum Spring Boot dijalankan.
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) throws Exception {
        DotEnv.load();
        SpringApplication.run(App.class, args);
    }
}
