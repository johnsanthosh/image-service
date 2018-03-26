import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableAutoConfiguration
@ComponentScan(basePackages = {"controller", "service", "dao", "pool", "model", "listener"})
@EnableMongoRepositories({"repository"})
public class Application {
    public static void main(String args[]) {
        System.setProperty("log.name", "image-service-logs");
        SpringApplication.run(Application.class, args);
    }
}
