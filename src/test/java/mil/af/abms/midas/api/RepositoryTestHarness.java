package mil.af.abms.midas.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.AttributeEncryptor;
import mil.af.abms.midas.config.CustomProperty;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:customPropertyTest.properties")
@EnableConfigurationProperties(CustomProperty.class)
@Import({ AttributeEncryptor.class })
@DataJpaTest
public abstract class RepositoryTestHarness {

    @Autowired
    protected CustomProperty property;
    @Autowired
    protected AttributeEncryptor encryptor;
    @Autowired
    protected TestEntityManager entityManager;

}
