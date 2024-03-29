package mil.af.abms.midas.api.sourcecontrol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.sourcecontrol.dto.SourceControlDTO;
import mil.af.abms.midas.api.user.User;

class SourceControlTests {

    @MockBean
    SimpMessageSendingOperations websocket;

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(1L))
            .with(g -> g.setToken("foobarbaz"))
            .with(g -> g.setName("bar"))
            .with(g -> g.setDescription("foo"))
            .with(g -> g.setBaseUrl("http://foo.bar"))
            .get();
    private final SourceControlDTO sourceControlDTO = Builder.build(SourceControlDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setName("bar"))
            .with(d -> d.setDescription("foo"))
            .with(d -> d.setBaseUrl("http://foo.bar"))
            .with(d -> d.setCreationDate(sourceControl.getCreationDate()))
            .get();

    @Test
    void should_have_all_sourceControl_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(SourceControl.class, fields::add);

        assertThat(fields).hasSize(SourceControlDTO.class.getDeclaredFields().length + 1);
    }

    @Test
    void should_set_and_get_properties() {
        assertThat(sourceControl.getId()).isEqualTo(1L);
        assertThat(sourceControl.getToken()).isEqualTo("foobarbaz");
        assertThat(sourceControl.getName()).isEqualTo("bar");
        assertThat(sourceControl.getBaseUrl()).isEqualTo("http://foo.bar");
        assertThat(sourceControl.getDescription()).isEqualTo("foo");
    }

    @Test
    void should_return_dto() {
        assertThat(sourceControl.toDto()).isEqualTo(sourceControlDTO);
    }

    @Test
    void should_be_equal() {
        var sourceControl2 = new SourceControl();
        BeanUtils.copyProperties(sourceControl, sourceControl2);

        assertEquals(sourceControl, sourceControl);
        assertNotEquals(sourceControl, null);
        assertNotEquals(sourceControl, new User());
        assertNotEquals(sourceControl, new SourceControl());
        assertEquals(sourceControl, sourceControl2);

        sourceControl2.setBaseUrl("http://fizz.bang");
        assertNotEquals(sourceControl, sourceControl2);
    }

}
