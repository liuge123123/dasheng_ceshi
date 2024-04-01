package com.tcc.gen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tcc.gen.dao")
public class WTDPGENApplication {

	public static void main(String[] args) {
		SpringApplication.run(WTDPGENApplication.class, args);
	}
}
