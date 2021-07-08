package mil.af.abms.midas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import lombok.Getter;

@Getter
@ConfigurationProperties("custom")
@ConstructorBinding
public class CustomProperty {

    private final String version;
    private final String classification;
    private final String caveat;
    private final String mattermostToken;
    private final String mattermostUrl;
    private final String environment;
    private final String jwtAdminGroup;
    private final String key;
    private final String salt;

    public CustomProperty(
            @DefaultValue("0.0.0") String version,
            @DefaultValue("UNCLASS") String classification,
            @DefaultValue("IL2") String caveat,
            @DefaultValue("other") String environment,
            @DefaultValue("midas-IL2-admin") String jwtAdminGroup,
            String mattermostToken,
            String mattermostUrl,
            String key,
            String salt
    ) {
        this.version = version;
        this.classification = classification;
        this.caveat = caveat;
        this.mattermostToken = mattermostToken;
        this.mattermostUrl = mattermostUrl;
        this.environment = environment;
        this.jwtAdminGroup = jwtAdminGroup;
        this.key = key;
        this.salt = salt;
    }

}
