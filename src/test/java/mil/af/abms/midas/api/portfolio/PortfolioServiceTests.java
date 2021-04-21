package mil.af.abms.midas.api.portfolio;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioIsArchivedDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(PortfolioService.class)
public class PortfolioServiceTests {

    @Autowired
    PortfolioService portfolioService;
    @MockBean
    UserService userService;
    @MockBean
    ProductService productService;
    @MockBean
    PortfolioRepository portfolioRepository;
    @Captor
    ArgumentCaptor<Portfolio> portfolioCaptor;

    User user = Builder.build(User.class)
            .with(u -> u.setId(3L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("Lambo")).get();
    Product product = Builder.build(Product.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setName("backend")).get();
    Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas")).get();

    @Test
    public void should_create_portfolio() {
        CreatePortfolioDTO createPortfolioDTO = new CreatePortfolioDTO("homeOne", 3L, "new name",
                Set.of(4L));

        when(userService.findByIdOrNull(3L)).thenReturn(user);
        when(productService.getObject(anyLong())).thenReturn(product);
        when(portfolioRepository.save(portfolio)).thenReturn(new Portfolio());

        portfolioService.create(createPortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getName()).isEqualTo(createPortfolioDTO.getName());
        assertThat(portfolioSaved.getPortfolioManager().getId()).isEqualTo(createPortfolioDTO.getPortfolioManagerId());
        assertThat(portfolioSaved.getDescription()).isEqualTo(createPortfolioDTO.getDescription());
        assertThat(portfolioSaved.getProducts()).isEqualTo(Set.of(product));
        assertFalse(portfolioSaved.getIsArchived());
    }

    @Test
    public void should_find_by_name() {
        when(portfolioRepository.findByName("Midas")).thenReturn(Optional.of(portfolio));

        assertThat(portfolioService.findByName("Midas")).isEqualTo(portfolio);
    }

    @Test
    public void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                portfolioService.findByName("buffet"));
    }

    @Test
    public void should_update_portfolio_by_id() {
        UpdatePortfolioDTO updatePortfolioDTO = new UpdatePortfolioDTO("oneHome", user.getId(), "taxable",
                Set.of(product.getId()));

        when(userService.findByIdOrNull(user.getId())).thenReturn(user);
        when(productService.getObject(anyLong())).thenReturn(product);
        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        portfolioService.updateById(5L, updatePortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getName()).isEqualTo(updatePortfolioDTO.getName());
        assertThat(portfolioSaved.getPortfolioManager().getId()).isEqualTo(updatePortfolioDTO.getPortfolioManagerId());
        assertThat(portfolioSaved.getDescription()).isEqualTo(updatePortfolioDTO.getDescription());
        assertThat(portfolioSaved.getProducts()).isEqualTo(Set.of(product));

    }

    @Test
    public void should_update_is_archived_by_id() {
        UpdatePortfolioIsArchivedDTO updateDTO = Builder.build(UpdatePortfolioIsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(any())).thenReturn(portfolio);

        portfolioService.updateIsArchivedById(5L, updateDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertTrue(portfolioSaved.getIsArchived());
    }

    @Test
    public void should_create_portfolio_with_null_portfolio_manager() {
        CreatePortfolioDTO createPortfolioDTO = new CreatePortfolioDTO("homeOne", null, "new name",
                Set.of(4L));

        portfolioService.create(createPortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getPortfolioManager()).isEqualTo(null);

    }

    @Test
    public void should_update_portfolio_with_null_portfolio_manager() {
        UpdatePortfolioDTO updatePortfolioDTO = new UpdatePortfolioDTO("oneHome", null, "taxable",
                Set.of(product.getId()));

        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        portfolioService.updateById(5L, updatePortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getPortfolioManager()).isEqualTo(null);

    }

}
