package mil.af.abms.midas.api.portfolio;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioIsArchivedDTO;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class PortfolioService extends AbstractCRUDService<Portfolio, PortfolioDTO, PortfolioRepository> {

    UserService userService;
    ProjectService projectService;

    public PortfolioService(PortfolioRepository repository) {
        super(repository, Portfolio.class, PortfolioDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setProjectService(ProjectService projectService) { this.projectService = projectService; }

    @Transactional
    public Portfolio create(CreatePortfolioDTO createPortfolioDTO) {
        User user = userService.getObject(createPortfolioDTO.getLeadId());

        Portfolio newPortfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName(createPortfolioDTO.getName()))
                .with(p -> p.setLead(user))
                .with(p -> p.setDescription(createPortfolioDTO.getDescription()))
                .with(p -> p.setProjects(createPortfolioDTO.getProjectsIds().stream().map(projectService::getObject)
                        .collect(Collectors.toSet()))).get();

        return repository.save(newPortfolio);
    }

    @Transactional
    public Portfolio findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Portfolio.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Portfolio updateById(Long id, UpdatePortfolioDTO updatePortfolioDTO) {
        User user = userService.getObject(updatePortfolioDTO.getLeadId());

        Portfolio portfolio = getObject(id);
        portfolio.setName(updatePortfolioDTO.getName());
        portfolio.setLead(user);
        portfolio.setDescription(updatePortfolioDTO.getDescription());
        portfolio.setProjects(updatePortfolioDTO.getProjectIds().stream()
                .map(projectService::getObject).collect(Collectors.toSet()));

        return repository.save(portfolio);
    }
    
    @Transactional
    public Portfolio updateIsArchivedById(Long id, UpdatePortfolioIsArchivedDTO updatePortfolioIsArchivedDTO) {
        Portfolio portfolio = getObject(id);
        portfolio.setIsArchived(updatePortfolioIsArchivedDTO.getIsArchived());
        
        return repository.save(portfolio);
    }
}
