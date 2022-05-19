package mil.af.abms.midas.api.gantt.win;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.gantt.GanttInterfaceDTO;
import mil.af.abms.midas.api.gantt.win.dto.CreateWinDTO;
import mil.af.abms.midas.api.gantt.win.dto.UpdateWinDTO;
import mil.af.abms.midas.api.gantt.win.dto.WinDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@Service
public class WinService extends AbstractCRUDService<Win, WinDTO, WinRepository> {

    private PortfolioService portfolioService;

    public WinService(WinRepository repository) {
        super(repository, Win.class, WinDTO.class);
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Transactional
    public Win create(CreateWinDTO dto) {
        Win newWin = Builder.build(Win.class)
                .with(m -> m.setPortfolio(portfolioService.findById(dto.getPortfolioId())))
                .get();

        updateCommonFields(dto, newWin);

        return repository.save(newWin);
    }

    @Transactional
    public Win updateById(Long id, UpdateWinDTO dto) {
        Win foundWin = findById(id);

        updateCommonFields(dto, foundWin);

        return repository.save(foundWin);
    }

    protected void updateCommonFields(GanttInterfaceDTO dto, Win win) {
        win.setDueDate(dto.getDueDate());
        win.setTitle(dto.getTitle());
        win.setDescription(dto.getDescription());
    }
}
