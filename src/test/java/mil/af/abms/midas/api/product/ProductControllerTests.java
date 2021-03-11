package mil.af.abms.midas.api.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductTeamDTO;
import mil.af.abms.midas.api.team.TeamEntity;
import mil.af.abms.midas.api.team.TeamRepository;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({ProductController.class})
public class ProductControllerTests extends ControllerTestHarness {

    @MockBean
    private ProductService productService;
    @MockBean
    private TeamRepository teamRepository;

    private final static Long ID = 1L;
    private final static String NAME = "MIDAS";
    private final static String DESCRIPTION = "MIDAS Project";
    private final static Boolean IS_ARCHIVED = false;
    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final static Long TEAM_ID = 3L;
    private final static Long GITLAB_PROJECT_ID = 2L;
    private final static Long GITLAB_GROUP_ID = 3L;

    private final UpdateProductTeamDTO updateProductTeamDTO = new UpdateProductTeamDTO(2L);

    private final TeamEntity team = Builder.build(TeamEntity.class)
            .with(t -> t.setId(TEAM_ID))
            .with(t -> t.setName("MIDAS_TEAM"))
            .with(t -> t.setCreationDate(CREATION_DATE))
            .with(t -> t.setGitlabGroupId(GITLAB_GROUP_ID)).get();
    private final ProductEntity product = Builder.build(ProductEntity.class)
            .with(p -> p.setId(ID))
            .with(p -> p.setName(NAME))
            .with(p -> p.setGitlabProjectId(GITLAB_PROJECT_ID))
            .with(p -> p.setTeam(team))
            .with(p -> p.setDescription(DESCRIPTION))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setIsArchived(IS_ARCHIVED)).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_product() throws Exception {
        CreateProductDTO createProductDTO = new CreateProductDTO(NAME, DESCRIPTION, GITLAB_PROJECT_ID);

        when(productService.findByName(NAME)).thenThrow(EntityNotFoundException.class);
        when(productService.create(any(CreateProductDTO.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createProductDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(NAME));
    }

    @Test
    public void should_update_product() throws Exception {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO(NAME, "", false, 0L);

        when(productService.findByName(NAME)).thenReturn(product);
        when(productService.updateById(anyLong(), any(UpdateProductDTO.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProductDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @Test
    public void should_update_product_team_by_team_id() throws Exception {
        TeamEntity newTeam = new TeamEntity();
        BeanUtils.copyProperties(team, newTeam);

        ProductEntity updatedProduct = new ProductEntity();
        BeanUtils.copyProperties(product, updatedProduct);

        newTeam.setId(4L);
        updatedProduct.setTeam(newTeam);

        when(teamRepository.existsById(any())).thenReturn(true);
        when(productService.findByName(NAME)).thenReturn(product);
        when(productService.updateProductTeamByTeamId(anyLong(), any(UpdateProductTeamDTO.class))).thenReturn(updatedProduct);


        mockMvc.perform(put("/api/products/1/team")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProductTeamDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.teamId").value(updatedProduct.getTeam().getId()));
    }

    @Test
    public void should_throw_null_error_on_update_product_team_by_team_id() throws Exception {
        mockMvc.perform(put("/api/products/1/team")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new UpdateProductTeamDTO()))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Validation failed. 2 error(s)"));
    }

    @Test
    public void should_throw_team_exists_exception_on_update_product_team() throws Exception {
        String expectedMessage = "team does not exists";

        when(teamRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(put("/api/products/1/team")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProductTeamDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }
}
