package mil.af.abms.midas.api.ogsm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.ogsm.dto.CreateOgsmDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

@WebMvcTest({OgsmController.class})
public class OgsmControllerTests extends ControllerTestHarness {

    @MockBean
    private OgsmService ogsmService;
    @MockBean
    private OgsmRepository ogsmRepository;

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(2L))
            .get();
    private final Assertion objective = Builder.build(Assertion.class)
            .with(a -> a.setId(3L))
            .get();
    private final Ogsm ogsm = Builder.build(Ogsm.class)
            .with(t -> t.setId(4L))
            .with(o -> o.setCreatedBy(user))
            .with(o -> o.setProduct(product))
            .with(o -> o.setAssertions(Set.of(objective)))
            .get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_ogsm() throws Exception {
        CreateOgsmDTO createOgsmDTO = new CreateOgsmDTO(1L, Set.of());

        when(ogsmService.create(any())).thenReturn(ogsm);

        mockMvc.perform(post("/api/ogsms")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createOgsmDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.productId").value(2))
                .andExpect(jsonPath("$.assertions[0].id").value(objective.getId()));;
    }

}
