package mil.af.abms.midas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(getApiInfo());
    }

    @Bean
    public Docket initApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("init")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/init/**"))
                .build()
                .apiInfo(getApiInfo());
    }

    @Bean
    public Docket actuatorApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("actuator")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/actuator/**"))
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Template for building a Spring Rest Api")
                .version("1.0.0")
                .description("Configured for PlatformOne with some bells and whistles")
                .contact(new Contact("Jeffrey Wills", "https://rise8.us", "jwills@rise8.us"))
                .build();
    }
}
