package mil.af.abms.midas;

import java.util.TimeZone;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MidasApi {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        context = SpringApplication.run(MidasApi.class, args);
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            context = SpringApplication.run(MidasApi.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }
}
