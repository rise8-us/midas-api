package mil.af.abms.midas.api.portfolio;

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
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioIsArchivedDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({PortfolioController.class})
public class PortfolioControllerTests extends ControllerTestHarness {

    @MockBean
    PortfolioService portfolioService;
    @MockBean
    ProductService productService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UpdatePortfolioDTO updatePortfolioDTO = new UpdatePortfolioDTO("Midas", 3L, "Full Stack",
            Set.of(3L));
    private final CreatePortfolioDTO createPortfolioDTO = new CreatePortfolioDTO("Midas", 1L, "backend",
            Set.of(1L));
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setPortfolioManager(new User()))
            .with(p -> p.setDescription("stack full"))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProducts(Set.of(new Product()))).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_portfolio() throws Exception {
        when(portfolioService.findByName(createPortfolioDTO.getName())).thenThrow(EntityNotFoundException.class);
        when(portfolioService.create(any(CreatePortfolioDTO.class))).thenReturn(portfolio);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(productService.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/api/portfolios")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createPortfolioDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Midas"));
    }

    @Test
    public void should_update_portfolio_by_id() throws Exception {
        when(portfolioService.findByName(updatePortfolioDTO.getName())).thenReturn(portfolio);
        when(portfolioService.updateById(anyLong(), any(UpdatePortfolioDTO.class))).thenReturn(portfolio);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(productService.existsById(3L)).thenReturn(true);

        mockMvc.perform(put("/api/portfolios/5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updatePortfolioDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Midas"));
    }

    @Test
    public void should_throw_unique_name_validation_on_create() throws Exception {
        String errorMessage = "Portfolio name already exists";

        when(portfolioService.findByName(updatePortfolioDTO.getName())).thenReturn(portfolio);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(productService.existsById(3L)).thenReturn(true);

        mockMvc.perform(post("/api/portfolios")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updatePortfolioDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    public void should_throw_unique_name_validation_error_update_portfolio_by_id() throws Exception {
        String errorMessage = "Portfolio name already exists";
        Portfolio existingPortfolio = new Portfolio();
        BeanUtils.copyProperties(portfolio, existingPortfolio);
        existingPortfolio.setId(10L);

        when(portfolioService.findByName(updatePortfolioDTO.getName())).thenReturn(existingPortfolio);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(productService.existsById(3L)).thenReturn(true);

        mockMvc.perform(put("/api/portfolios/5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updatePortfolioDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    public void should_toggle_portfolio_is_archived() throws Exception {
        UpdatePortfolioIsArchivedDTO archivedDTO = Builder.build(UpdatePortfolioIsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();
        portfolio.setIsArchived(true);

        when(portfolioService.updateIsArchivedById(5L, archivedDTO)).thenReturn(portfolio);

        mockMvc.perform(put("/api/portfolios/5/archive")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(archivedDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(portfolio.getIsArchived()));
    }

}
