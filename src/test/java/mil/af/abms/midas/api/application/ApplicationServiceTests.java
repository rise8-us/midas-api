package mil.af.abms.midas.api.application;

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

import mil.af.abms.midas.api.application.dto.CreateApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationIsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(mil.af.abms.midas.api.application.ApplicationService.class)
public class ApplicationServiceTests {

    @Autowired
    mil.af.abms.midas.api.application.ApplicationService applicationService;
    @MockBean
    UserService userService;
    @MockBean
    ProjectService projectService;
    @MockBean
    TagService tagService;
    @MockBean
    ApplicationRepository applicationRepository;
    @MockBean
    PortfolioService portfolioService;
    @Captor
    ArgumentCaptor<Application> applicationCaptor;

    User user = Builder.build(User.class)
            .with(u -> u.setId(3L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("Lambo")).get();
    Project project = Builder.build(Project.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setName("backend")).get();
    Application application = Builder.build(Application.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas")).get();

    @Test
    public void should_create_application() {
        CreateApplicationDTO createApplicationDTO = new CreateApplicationDTO("homeOne", 3L, "new name",
                Set.of(4L), Set.of(3L), 1L);

        when(userService.findByIdOrNull(3L)).thenReturn(user);
        when(projectService.getObject(anyLong())).thenReturn(project);
        when(applicationRepository.save(application)).thenReturn(new Application());

        applicationService.create(createApplicationDTO);

        verify(applicationRepository, times(1)).save(applicationCaptor.capture());
        Application applicationSaved = applicationCaptor.getValue();

        assertThat(applicationSaved.getName()).isEqualTo(createApplicationDTO.getName());
        assertThat(applicationSaved.getProductManager().getId()).isEqualTo(createApplicationDTO.getProductManagerId());
        assertThat(applicationSaved.getDescription()).isEqualTo(createApplicationDTO.getDescription());
        assertFalse(applicationSaved.getIsArchived());
    }

    @Test
    public void should_find_by_name() {
        when(applicationRepository.findByName("Midas")).thenReturn(Optional.of(application));

        assertThat(applicationService.findByName("Midas")).isEqualTo(application);
    }

    @Test
    public void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                applicationService.findByName("buffet"));
    }

    @Test
    public void should_update_application_by_id() {
        UpdateApplicationDTO updateApplicationDTO = new UpdateApplicationDTO("oneHome", user.getId(), "taxable",
                Set.of(project.getId()), Set.of(3L), 1L);

        when(userService.findByIdOrNull(user.getId())).thenReturn(user);
        when(projectService.getObject(anyLong())).thenReturn(project);
        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(applicationRepository.save(application)).thenReturn(application);

        applicationService.updateById(5L, updateApplicationDTO);

        verify(applicationRepository, times(1)).save(applicationCaptor.capture());
        Application applicationSaved = applicationCaptor.getValue();

        assertThat(applicationSaved.getName()).isEqualTo(updateApplicationDTO.getName());
        assertThat(applicationSaved.getProductManager().getId()).isEqualTo(updateApplicationDTO.getProductManagerId());
        assertThat(applicationSaved.getDescription()).isEqualTo(updateApplicationDTO.getDescription());
        assertThat(applicationSaved.getProjects()).isEqualTo(Set.of(project));

    }

    @Test
    public void should_update_is_archived_by_id() {
        UpdateApplicationIsArchivedDTO updateDTO = Builder.build(UpdateApplicationIsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(applicationRepository.save(any())).thenReturn(application);

        applicationService.updateIsArchivedById(5L, updateDTO);

        verify(applicationRepository, times(1)).save(applicationCaptor.capture());
        Application applicationSaved = applicationCaptor.getValue();

        assertTrue(applicationSaved.getIsArchived());
    }

    @Test
    public void should_create_application_with_null_product_manager_and_null_portfolio_id() {
        CreateApplicationDTO createDTO = new CreateApplicationDTO("name", null, "description",
                Set.of(1L), Set.of(1L), null);

        when(userService.findByIdOrNull(anyLong())).thenReturn(null);
        when(portfolioService.findByIdOrNull(anyLong())).thenReturn(null);
        when(projectService.getObject(anyLong())).thenReturn(project);
        when(applicationRepository.save(application)).thenReturn(new Application());

        applicationService.create(createDTO);

        verify(applicationRepository, times(1)).save(applicationCaptor.capture());
        Application applicationSaved = applicationCaptor.getValue();

        assertThat(applicationSaved.getProductManager()).isEqualTo(null);
        assertThat(applicationSaved.getPortfolio()).isEqualTo(null);
    }

    @Test
    public void should_update_application_with_null_product_manager_and_null_portfolio_id() {
        UpdateApplicationDTO updateDTO = new UpdateApplicationDTO("name", null, "description",
                Set.of(1L), Set.of(1L), null);

        when(userService.getObject(anyLong())).thenReturn(null);
        when(portfolioService.findByIdOrNull(anyLong())).thenReturn(null);
        when(projectService.getObject(anyLong())).thenReturn(project);
        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(applicationRepository.save(application)).thenReturn(application);

        applicationService.updateById(5L, updateDTO);

        verify(applicationRepository, times(1)).save(applicationCaptor.capture());
        Application applicationSaved = applicationCaptor.getValue();

        assertThat(applicationSaved.getProductManager()).isEqualTo(null);
        assertThat(applicationSaved.getPortfolio()).isEqualTo(null);

    }

}
