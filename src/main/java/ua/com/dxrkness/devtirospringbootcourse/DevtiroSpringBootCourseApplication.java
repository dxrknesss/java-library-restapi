package ua.com.dxrkness.devtirospringbootcourse;

import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
@Log
public class DevtiroSpringBootCourseApplication implements CommandLineRunner {

    private final DataSource dataSource;

    public DevtiroSpringBootCourseApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(DevtiroSpringBootCourseApplication.class, args);
    }


    @Override
    public void run(String... args) {
        log.info("Datasource: " + dataSource);
        final var jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("select 1");
    }
}
