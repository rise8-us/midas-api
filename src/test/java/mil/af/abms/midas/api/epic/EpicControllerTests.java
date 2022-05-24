package mil.af.abms.midas.api.epic;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithPortfolioDTO;
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithProductDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;

@WebMvcTest({EpicController.class})
public class EpicControllerTests extends ControllerTestHarness {

    @MockBean
    private EpicService epicService;

    @MockBean
    private ProductService productService;
    @MockBean
    private PortfolioService portfolioService;

    SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setBaseUrl("fake_url"))
            .with(sc -> sc.setToken("fake_token"))
            .get();

    Product product = Builder.build(Product.class)
            .with(p -> p.setName("name"))
            .with(p -> p.setId(2L))
            .with(p -> p.setSourceControl(sourceControl))
            .get();

    Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setName("name"))
            .with(p -> p.setId(2L))
            .with(p -> p.setSourceControl(sourceControl))
            .get();

    Epic epicWithProduct = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(LocalDateTime.now()))
            .with(e -> e.setProduct(product))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(1))
            .get();

    Epic epicWithPortfolio = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(LocalDateTime.now()))
            .with(e -> e.setPortfolio(portfolio))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(1))
            .get();

    @BeforeEach
    void init() {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void throw_should_create_epic_gitLab_not_found_for_product() throws Exception {
        when(productService.findById(any())).thenReturn(product);
        when(epicService.createForProduct(any(AddGitLabEpicWithProductDTO.class))).thenReturn(epicWithProduct);

        mockMvc.perform(post("/api/epics/product")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabEpicWithProductDTO(1, 2L)))
                )
                .andExpect(status().is(400))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Validation failed. 1 error(s)"))
                .andExpect(jsonPath("$.errors[0]").value("GitLab epic does not exist or cannot be found"));
    }

    @Test
    void throw_should_create_epic_gitLab_not_found_for_portfolio() throws Exception {
        when(portfolioService.findById(any())).thenReturn(portfolio);
        when(epicService.createForPortfolio(any(AddGitLabEpicWithPortfolioDTO.class))).thenReturn(epicWithPortfolio);

        mockMvc.perform(post("/api/epics/portfolio")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabEpicWithPortfolioDTO(1, 2L)))
                )
                .andExpect(status().is(400))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Validation failed. 1 error(s)"))
                .andExpect(jsonPath("$.errors[0]").value("GitLab epic does not exist or cannot be found"));
    }

    @Test
    void should_create_epic_for_product() throws Exception {
        when(productService.findById(any())).thenReturn(product);
        when(epicService.canAddEpicWithProduct(any(), any())).thenReturn(true);
        when(epicService.createForProduct(any(AddGitLabEpicWithProductDTO.class))).thenReturn(epicWithProduct);

        mockMvc.perform(post("/api/epics/product")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabEpicWithProductDTO(1, 2L)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.productId").value(product.getId()));
    }

    @Test
    void should_create_epic_for_portfolio() throws Exception {
        when(portfolioService.findById(any())).thenReturn(portfolio);
        when(epicService.canAddEpicWithPortfolio(any(), any())).thenReturn(true);
        when(epicService.createForPortfolio(any(AddGitLabEpicWithPortfolioDTO.class))).thenReturn(epicWithPortfolio);

        mockMvc.perform(post("/api/epics/portfolio")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabEpicWithPortfolioDTO(1, 2L)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.portfolioId").value(portfolio.getId()));
    }

    @Test
    void should_update_epic_by_id_for_product() throws Exception {
        when(epicService.updateByIdForProduct(anyLong())).thenReturn(epicWithProduct);

        mockMvc.perform(get("/api/epics/sync/product/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.productId").value(product.getId()));
    }

    @Test
    void should_update_epic_by_id_for_portfolio() throws Exception {
        when(epicService.updateByIdForPortfolio(anyLong())).thenReturn(epicWithPortfolio);

        mockMvc.perform(get("/api/epics/sync/portfolio/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.portfolioId").value(portfolio.getId()));
    }

    @Test
    public void should_update_epic_is_hidden_true() throws Exception {
        Epic hidden = new Epic();
        BeanUtils.copyProperties(epicWithProduct, hidden);
        hidden.setIsHidden(true);

        IsHiddenDTO hiddenDTO = new IsHiddenDTO(true);

        when(epicService.updateIsHidden(1L, hiddenDTO)).thenReturn(hidden);

        mockMvc.perform(put("/api/epics/1/hide")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(hiddenDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isHidden").value(true));
    }

    @Test
    void should_get_all_epics_by_product_id() throws Exception {
        Set<Epic> epics = Set.of(epicWithProduct);
        when(epicService.getAllGitlabEpicsForProduct(anyLong())).thenReturn(epics);

        mockMvc.perform(get("/api/epics/all/product/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_get_all_epics_by_portfolio_id() throws Exception {
        Set<Epic> epics = Set.of(epicWithPortfolio);
        when(epicService.getAllGitlabEpicsForPortfolio(anyLong())).thenReturn(epics);

        mockMvc.perform(get("/api/epics/all/portfolio/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
