package mil.af.abms.midas.api.gantt.milestone;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.gantt.milestone.dto.CreateMilestoneDTO;
import mil.af.abms.midas.api.gantt.milestone.dto.UpdateMilestoneDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@ExtendWith(SpringExtension.class)
@Import(MilestoneService.class)
public class MilestoneServiceTests {

    @SpyBean
    private MilestoneService milestoneService;
    @MockBean
    private MilestoneRepository repository;
    @MockBean
    private PortfolioService portfolioService;

    @Captor
    private ArgumentCaptor<Milestone> milestoneArgumentCaptor;
    @Captor
    private ArgumentCaptor<Portfolio> portfolioArgumentCaptor;
    @Captor
    private  ArgumentCaptor<Long> longArgumentCaptor;

    private LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");
    private LocalDate DUE_DATE2 = TimeConversion.getLocalDateOrNullFromObject("2022-07-09");
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("portfolio name"))
            .with(p -> p.setDescription("description"))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setGitlabGroupId(2))
            .with(p -> p.setPersonnel(new Personnel()))
            .with(p -> p.setProducts(Set.of()))
            .with(p -> p.setVision("vision"))
            .with(p -> p.setMission("mission"))
            .with(p -> p.setProblemStatement("problem"))
            .get();
    private final Milestone milestone = Builder.build(Milestone.class)
            .with(m -> m.setId(2L))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setTitle("milestoneTitle"))
            .with(m -> m.setDescription("milestoneDescription"))
            .with(m -> m.setPortfolio(portfolio))
            .get();
    private final CreateMilestoneDTO createMilestoneDTO = Builder.build(CreateMilestoneDTO.class)
            .with(m -> m.setTitle("milestoneTitle"))
            .with(m -> m.setDescription("milestoneDescription"))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setPortfolioId(milestone.getPortfolio().getId()))
            .get();

    private final UpdateMilestoneDTO updateMilestoneDTO = Builder.build(UpdateMilestoneDTO.class)
            .with(m -> m.setTitle("newTitle"))
            .with(m -> m.setDescription("newDescription"))
            .with(m -> m.setDueDate(DUE_DATE2))
            .get();

    @Test
    void should_create_milestone() {
        milestoneService.create(createMilestoneDTO);

        verify(repository, times(1)).save(any());
        verify(milestoneService, times(1)).updateCommonFields(any(), any());
    }

    @Test
    void should_update_milestone_by_id() {
        doReturn(milestone).when(milestoneService).findById(2L);

        milestoneService.updateById(2L, updateMilestoneDTO);

        verify(repository, times(1)).save(milestoneArgumentCaptor.capture());
        Milestone milestoneSaved = milestoneArgumentCaptor.getValue();

        assertThat(milestoneSaved.getTitle()).isEqualTo("newTitle");
        assertThat(milestoneSaved.getDescription()).isEqualTo("newDescription");
    }

    @Test
    void should_set_due_date() {
        doReturn(milestone).when(milestoneService).findById(anyLong());

        milestoneService.updateById(milestone.getId(), updateMilestoneDTO);

        verify(repository, times(1)).save(milestoneArgumentCaptor.capture());
        Milestone milestoneSaved = milestoneArgumentCaptor.getValue();

        AssertionsForClassTypes.assertThat(milestoneSaved.getDueDate()).isEqualTo(DUE_DATE2);
    }


}
