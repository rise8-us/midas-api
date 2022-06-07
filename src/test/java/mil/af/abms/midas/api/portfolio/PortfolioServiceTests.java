package mil.af.abms.midas.api.portfolio;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(PortfolioService.class)
public class PortfolioServiceTests {

    @SpyBean
    PortfolioService portfolioService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    PersonnelService personnelService;
    @MockBean
    ProductService productService;
    @MockBean
    CapabilityService capabilityService;
    @MockBean
    SourceControlService sourceControlService;
    @MockBean
    PortfolioRepository portfolioRepository;
    @Captor
    ArgumentCaptor<Portfolio> portfolioCaptor;

    private final LocalDateTime today = LocalDateTime.now();
    private final User user = Builder.build(User.class).with(u -> u.setId(1L)).get();
    private final User user2 = Builder.build(User.class).with(u -> u.setId(10L)).get();
    private final Product product = Builder.build(Product.class).with(p -> p.setId(2L)).get();
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(3L))
            .with(p -> p.setPersonnel(new Personnel()))
            .with(p -> p.setName("ABMS"))
            .with(p -> p.setGanttNote("Gantt Note"))
            .with(p -> p.setGanttNoteModifiedAt(today))
            .with(p -> p.setGanttNoteModifiedBy(user))
            .get();
    private final Personnel personnel = Builder.build(Personnel.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setOwner(user))
            .with(p -> p.setAdmins(Set.of()))
            .with(p -> p.setPortfolios(portfolio))
            .with(p -> p.setProduct(null))
            .with(p -> p.setTeams(Set.of()))
            .get();
    private final Capability capability = Builder.build(Capability.class)
            .with(c -> c.setId(5L))
            .with(c -> c.setTitle("title"))
            .with(c -> c.setReferenceId(0))
            .get();

    @Test
    void should_create_portfolio_no_personnel() {
        CreatePortfolioDTO createPortfolioDTO = Builder.build(CreatePortfolioDTO.class)
                .with(d -> d.setName("ABMS"))
                .with(d -> d.setProductIds(Set.of(2L)))
                .with(d -> d.setCapabilityIds(Set.of(5L)))
                .get();

        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(new Personnel());
        when(productService.findById(anyLong())).thenReturn(product);
        when(capabilityService.findById(anyLong())).thenReturn(capability);

        portfolioService.create(createPortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getPersonnel()).isEqualTo(new Personnel());
        assertThat(portfolioSaved.getProducts()).isEqualTo(Set.of(product));
        assertThat(portfolioSaved.getName()).isEqualTo(createPortfolioDTO.getName());
        assertThat(portfolioSaved.getDescription()).isEqualTo(createPortfolioDTO.getDescription());
        assertThat(portfolioSaved.getGitlabGroupId()).isEqualTo(createPortfolioDTO.getGitlabGroupId());
        assertThat(portfolioSaved.getVision()).isEqualTo(createPortfolioDTO.getVision());
        assertThat(portfolioSaved.getMission()).isEqualTo(createPortfolioDTO.getMission());
        assertThat(portfolioSaved.getProblemStatement()).isEqualTo(createPortfolioDTO.getProblemStatement());
        assertThat(portfolioSaved.getSourceControl()).isEqualTo(null);
        assertThat(portfolioSaved.getCapabilities()).isEqualTo(Set.of(capability));

    }
    @Test
    void should_create_portfolio_with_personnel() {
        CreatePersonnelDTO createPersonnelDTO = Builder.build(CreatePersonnelDTO.class)
                .with(d -> d.setOwnerId(1L))
                .get();
        CreatePortfolioDTO createPortfolioDTO = Builder.build(CreatePortfolioDTO.class)
                .with(d -> d.setName("ABMS"))
                .with(d -> d.setPersonnel(createPersonnelDTO))
                .get();
        portfolio.setPersonnel(personnel);

        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(personnel);
        when(productService.findById(anyLong())).thenReturn(product);

        portfolioService.create(createPortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getPersonnel()).isEqualTo(personnel);
    }

    @Test
    void should_update_portfolio_by_id() {
        UpdatePersonnelDTO updatePersonnelDTO = Builder.build(UpdatePersonnelDTO.class)
                .with(d -> d.setOwnerId(10L))
                .get();
        UpdatePortfolioDTO updatePortfolioDTO = Builder.build(UpdatePortfolioDTO.class)
                .with(p -> p.setName("new name"))
                .with(p -> p.setDescription("new description"))
                .with(p -> p.setPersonnel(updatePersonnelDTO))
                .with(p -> p.setProductIds(Set.of(4L)))
                .with(p -> p.setCapabilityIds(Set.of(5L)))
                .with(p -> p.setGanttNote("Test Gantt Note"))
                .get();
        personnel.setOwner(user2);
        portfolio.setPersonnel(personnel);

        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(productService.findById(anyLong())).thenReturn(product);
        when(capabilityService.findById(anyLong())).thenReturn(capability);
        when(personnelService.updateById(anyLong(), any(UpdatePersonnelDTO.class))).thenReturn(personnel);
        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        portfolioService.updateById(3L, updatePortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getName()).isEqualTo(updatePortfolioDTO.getName());
        assertThat(portfolioSaved.getDescription()).isEqualTo(updatePortfolioDTO.getDescription());
        assertThat(portfolioSaved.getPersonnel()).isEqualTo(personnel);
        assertThat(portfolioSaved.getProducts()).isEqualTo(Set.of(product));
        assertThat(portfolioSaved.getVision()).isEqualTo(updatePortfolioDTO.getVision());
        assertThat(portfolioSaved.getMission()).isEqualTo(updatePortfolioDTO.getMission());
        assertThat(portfolioSaved.getProblemStatement()).isEqualTo(updatePortfolioDTO.getProblemStatement());
        assertThat(portfolioSaved.getCapabilities()).isEqualTo((Set.of(capability)));
        assertThat(portfolioSaved.getGanttNote()).isEqualTo(updatePortfolioDTO.getGanttNote());
    }

    @Test
    void should_update_portfolio_by_id_without_gantt_note_change() {
        UpdatePersonnelDTO updatePersonnelDTO = Builder.build(UpdatePersonnelDTO.class)
                .with(d -> d.setOwnerId(10L))
                .get();
        UpdatePortfolioDTO updatePortfolioDTO = Builder.build(UpdatePortfolioDTO.class)
                .with(p -> p.setName("new name"))
                .with(p -> p.setDescription("new description"))
                .with(p -> p.setPersonnel(updatePersonnelDTO))
                .with(p -> p.setProductIds(Set.of(4L)))
                .with(p -> p.setCapabilityIds(Set.of(5L)))
                .with(p -> p.setGanttNote("Gantt Note"))
                .get();
        personnel.setOwner(user2);
        portfolio.setPersonnel(personnel);

        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(productService.findById(anyLong())).thenReturn(product);
        when(capabilityService.findById(anyLong())).thenReturn(capability);
        when(personnelService.updateById(anyLong(), any(UpdatePersonnelDTO.class))).thenReturn(personnel);
        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        portfolioService.updateById(3L, updatePortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getGanttNote()).isEqualTo("Gantt Note");
    }

    @Test
    void should_update_is_archived_by_id() {
        Portfolio portfolioWithProduct = new Portfolio();
        BeanUtils.copyProperties(portfolio, portfolioWithProduct);
        portfolioWithProduct.setProducts(Set.of(product));

        IsArchivedDTO updateDTO = Builder.build(IsArchivedDTO.class).with(d -> d.setIsArchived(true)).get();

        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolioWithProduct));
        when(portfolioRepository.save(any())).thenReturn(portfolioWithProduct);
        when(productService.updateIsArchivedById(any(), any())).thenReturn(product);

        portfolioService.updateIsArchivedById(5L, updateDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertTrue(portfolioSaved.getIsArchived());
    }

    @Test
    void should_find_by_name() {
        when(portfolioRepository.findByName("ABMS")).thenReturn(Optional.of(portfolio));

        assertThat(portfolioService.findByName("ABMS")).isEqualTo(portfolio);
    }

    @Test
    void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () -> portfolioService.findByName("buffet"));
    }
}
