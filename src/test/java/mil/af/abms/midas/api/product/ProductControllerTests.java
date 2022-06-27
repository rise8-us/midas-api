package mil.af.abms.midas.api.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.enums.RoadmapType;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({ProductController.class})
class ProductControllerTests extends ControllerTestHarness {
    
    @MockBean
    ProductService productService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    TagService tagService;
    @MockBean
    ProjectService projectService;
    @MockBean
    TeamService teamService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UpdateProductDTO updateProductDTO = new UpdateProductDTO(
            "Midas",
            "Full Stack",
            Set.of(3L),
            Set.of(3L),
            null,
            null,
            RoadmapType.GITLAB,
            new UpdatePersonnelDTO(),
            null,
            null,
            null
    );
    private final CreateProductDTO createProductDTO = new CreateProductDTO(
            "Midas",
            "Full Stack",
            Set.of(3L),
            Set.of(3L),
            null,
            null,
            RoadmapType.GITLAB,
            new CreatePersonnelDTO(),
            null,
            null,
            null
    );
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("stack full"))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProjects(Set.of(new Project())))
            .with(p -> p.setRoadmapType(RoadmapType.GITLAB))
            .with(p -> p.setPersonnel(new Personnel()))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_product() throws Exception {
        when(productService.findByName(createProductDTO.getName())).thenThrow(EntityNotFoundException.class);
        when(productService.create(any(CreateProductDTO.class))).thenReturn(product);
        when(productService.existsById(anyLong())).thenReturn(true);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.findById(anyLong())).thenReturn(new Project());
        when(tagService.existsById(any())).thenReturn(true);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createProductDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Midas"));
    }

    @Test
    void should_update_product_by_id() throws Exception {
        when(productService.findByName(updateProductDTO.getName())).thenReturn(product);
        when(productService.updateById(anyLong(), any(UpdateProductDTO.class))).thenReturn(product);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.findById(anyLong())).thenReturn(new Project());
        when(tagService.existsById(any())).thenReturn(true);

        mockMvc.perform(put("/api/products/5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProductDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Midas"));
    }

    @Test
    void should_throw_unique_name_validation_on_create() throws Exception {
        String errorMessage = "product name already exists";

        when(productService.findByName(createProductDTO.getName())).thenReturn(product);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(any())).thenReturn(true);
        when(productService.existsById(any())).thenReturn(true);
        when(projectService.existsById(any())).thenReturn(true);
        when(projectService.findById(anyLong())).thenReturn(new Project());

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createProductDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    void should_throw_unique_name_validation_error_update_product_by_id() throws Exception {
        String errorMessage = "product name already exists";
        Product existingProduct = new Product();
        BeanUtils.copyProperties(product, existingProduct);
        existingProduct.setId(10L);

        when(productService.findByName(updateProductDTO.getName())).thenReturn(existingProduct);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(any())).thenReturn(true);
        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.findById(anyLong())).thenReturn(new Project());

        mockMvc.perform(put("/api/products/5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProductDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    void should_toggle_product_is_archived() throws Exception {
        IsArchivedDTO archivedDTO = Builder.build(IsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();
        product.setIsArchived(true);

        when(productService.updateIsArchivedById(5L, archivedDTO)).thenReturn(product);

        mockMvc.perform(put("/api/products/5/archive")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(archivedDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(product.getIsArchived()));
    }

    @Test
    public void should_get_sprint_metrics() throws Exception {
        SprintProductMetricsDTO dto = Builder.build(SprintProductMetricsDTO.class)
                .with(d -> d.setDate(LocalDate.parse("2022-06-16")))
                .with(d -> d.setDeliveredPoints(100L))
                .with(d -> d.setDeliveredStories(60))
                .get();
        List<SprintProductMetricsDTO> metricsList = List.of(dto);

        when(productService.getSprintMetrics(any(), any(), any(), any())).thenReturn(metricsList);

        mockMvc.perform(get("/api/products/1/sprint-metrics/2022-06-16?duration=14&sprints=1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(metricsList))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0]['date']").value("2022-06-16"));
    }

}
