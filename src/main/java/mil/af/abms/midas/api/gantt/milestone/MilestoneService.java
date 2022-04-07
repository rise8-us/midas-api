package mil.af.abms.midas.api.gantt.milestone;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.gantt.GanttInterfaceDTO;
import mil.af.abms.midas.api.gantt.milestone.dto.CreateMilestoneDTO;
import mil.af.abms.midas.api.gantt.milestone.dto.MilestoneDTO;
import mil.af.abms.midas.api.gantt.milestone.dto.UpdateMilestoneDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@Service
public class MilestoneService extends AbstractCRUDService<Milestone, MilestoneDTO, MilestoneRepository> {

    private PortfolioService portfolioService;

    public MilestoneService(MilestoneRepository repository) {
        super(repository, Milestone.class, MilestoneDTO.class);
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Transactional
    public Milestone create(CreateMilestoneDTO dto) {
        Milestone newMilestone = Builder.build(Milestone.class)
                .with(m -> m.setPortfolio(portfolioService.findById(dto.getPortfolioId())))
                .get();

        updateCommonFields(dto, newMilestone);

        return repository.save(newMilestone);
    }

    @Transactional
    public Milestone updateById(Long id, UpdateMilestoneDTO dto) {
        Milestone foundMilestone = findById(id);

        updateCommonFields(dto, foundMilestone);

        return repository.save(foundMilestone);
    }

    protected void updateCommonFields(GanttInterfaceDTO dto, Milestone milestone) {
        milestone.setDueDate(dto.getDueDate());
        milestone.setTitle(dto.getTitle());
        milestone.setDescription(dto.getDescription());
    }
}
