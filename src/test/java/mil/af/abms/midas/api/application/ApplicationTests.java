package mil.af.abms.midas.api.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.application.dto.ApplicationDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.user.User;

public class ApplicationTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final User lead = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private Set<Project> projects = Set.of(Builder.build(Project.class).with(u -> u.setId(3L)).get());
    private final Application application = Builder.build(Application.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("test application"))
            .with(p -> p.setCreationDate(TEST_TIME))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProductManager(lead))
            .with(p -> p.setProjects(projects)).get();
    private final ApplicationDTO applicationDTO = Builder.build(ApplicationDTO.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("test application"))
            .with(p -> p.setCreationDate(TEST_TIME))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProductManagerId(lead.getId()))
            .with(p -> p.setTagIds(new HashSet<>()))
            .with(p -> p.setProjectIds(Set.of(3L))).get();

    @Test
    public void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Application.class, fields::add);

        assertThat(fields.size()).isEqualTo(ApplicationDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_be_equal() {
        Application application2 = new Application();
        BeanUtils.copyProperties(application, application2);

        assertEquals(application, application);
        assertNotEquals(null, application);
        assertNotEquals(application, new User());
        assertNotEquals(application, new Application());
        assertEquals(application, application2);
    }

    @Test
    public void should_get_properties() {
        assertThat(application.getId()).isEqualTo(1L);
        assertThat(application.getName()).isEqualTo("Midas");
        assertThat(application.getDescription()).isEqualTo("test application");
        assertThat(application.getCreationDate()).isEqualTo(TEST_TIME);
        assertFalse(application.getIsArchived());
        assertThat(application.getProductManager()).isEqualTo(lead);
        assertThat(application.getProjects()).isEqualTo(projects);
    }

    @Test
    public void can_return_dto() {
        assertThat(application.toDto()).isEqualTo(applicationDTO);
    }

    @Test
    public void should_return_null_lead() {
        Application nullLead = new Application();
        BeanUtils.copyProperties(application, nullLead);
        nullLead.setProductManager(null);

        assertThat(nullLead.toDto().getProductManagerId()).isEqualTo(null);
    }
}
