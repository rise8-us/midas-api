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
import java.util.Set;

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
import mil.af.abms.midas.api.product.dto.UpdateProductIsArchivedDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProductType;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({ProductController.class})
public class ProductControllerTests extends ControllerTestHarness {
    
    @MockBean
    ProductService productService;
    @MockBean
    TagService tagService;
    @MockBean
    ProjectService projectService;
    @MockBean
    TeamService teamService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UpdateProductDTO updateProductDTO = new UpdateProductDTO("Midas", "Full Stack",
            3L, 1L, Set.of(3L), Set.of(3L), Set.of(), ProductType.PRODUCT, null, null);
    private final CreateProductDTO createProductDTO = new CreateProductDTO("Midas", "backend",
            1L, 1L, Set.of(3L), Set.of(3L), Set.of(), ProductType.PRODUCT, null, null);
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setProductManager(new User()))
            .with(p -> p.setDescription("stack full"))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProjects(Set.of(new Project()))).get();

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
        UpdateProductIsArchivedDTO archivedDTO = Builder.build(UpdateProductIsArchivedDTO.class)
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

}
