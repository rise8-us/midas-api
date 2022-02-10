package mil.af.abms.midas.api.issue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.issue.dto.IssueDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.user.User;

public class IssueTests {

    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(1L))
            .get();

    private final Issue issue = Builder.build(Issue.class)
            .with(i -> i.setId(1L))
            .with(i -> i.setCreationDate(LocalDateTime.now()))
            .with(i -> i.setProject(project))
            .get();

    private final IssueDTO expectedDTO = Builder.build(IssueDTO.class)
            .with(i -> i.setId(1L))
            .with(i -> i.setCreationDate(issue.getCreationDate()))
            .with(i -> i.setProjectId(project.getId()))
            .get();

    @Test
    void should_have_all_issueDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Issue.class, fields::add);
        assertThat(fields.size()).isEqualTo(IssueDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Issue issue2 = new Issue();
        BeanUtils.copyProperties(issue, issue2);

        assertEquals(issue, issue);
        assertNotEquals(null, issue);
        assertNotEquals(issue, new User());
        assertNotEquals(issue, new Issue());
        assertEquals(issue, issue2);
        assertFalse(issue.equals(null));
    }

    @Test
    void should_get_properties() {
        assertThat(issue.getId()).isEqualTo(1L);
    }

    @Test
    void can_return_dto() {
        assertThat(issue.toDto()).isEqualTo(expectedDTO);
    }
}
