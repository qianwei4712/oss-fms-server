package cn.shiva;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.shiva.mapper")
public class OssFmsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OssFmsServerApplication.class, args);
	}

}
