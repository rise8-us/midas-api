package mil.af.abms.midas.api.gantt.target;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.gantt.target.dto.CreateTargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.TargetInterfaceDTO;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@Service
public class TargetService extends AbstractCRUDService<Target, TargetDTO, TargetRepository> {

    private PortfolioService portfolioService;

    public TargetService(TargetRepository repository) {
        super(repository, Target.class, TargetDTO.class);
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Transactional
    public Target create(CreateTargetDTO dto) {
        Target newTarget = Builder.build(Target.class)
                .with(t -> t.setPortfolio(portfolioService.findById(dto.getPortfolioId())))
                .get();

        updateCommonFields(dto, newTarget);

        return repository.save(newTarget);
    }

    @Transactional
    public Target updateById(Long id, UpdateTargetDTO dto) {
        Target foundTarget = findById(id);

        updateCommonFields(dto, foundTarget);

        return repository.save(foundTarget);
    }

    protected void updateCommonFields(TargetInterfaceDTO dto, Target target) {
        target.setStartDate(dto.getStartDate());
        target.setDueDate(dto.getDueDate());
        target.setTitle(dto.getTitle());
        target.setDescription(dto.getDescription());
    }

}
