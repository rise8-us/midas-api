package mil.af.abms.midas.api.portfolio;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.application.ApplicationService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioIsArchivedDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class PortfolioService extends AbstractCRUDService<Portfolio, PortfolioDTO, PortfolioRepository> {

    UserService userService;
    ApplicationService applicationService;

    public PortfolioService(PortfolioRepository repository) {
        super(repository, Portfolio.class, PortfolioDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setApplicationService(ApplicationService applicationService) { this.applicationService = applicationService; }

    @Transactional
    public Portfolio create(CreatePortfolioDTO createPortfolioDTO) {
        User user = userService.getObject(createPortfolioDTO.getPortfolioManagerId());

        Portfolio newPortfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName(createPortfolioDTO.getName()))
                .with(p -> p.setPortfolioManager(user))
                .with(p -> p.setDescription(createPortfolioDTO.getDescription()))
                .with(p -> p.setApplications(createPortfolioDTO.getApplicationIds().stream().map(applicationService::getObject)
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
        User user = userService.getObject(updatePortfolioDTO.getPortfolioManagerId());

        Portfolio portfolio = getObject(id);
        portfolio.setName(updatePortfolioDTO.getName());
        portfolio.setPortfolioManager(user);
        portfolio.setDescription(updatePortfolioDTO.getDescription());
        portfolio.setApplications(updatePortfolioDTO.getApplicationIds().stream()
                .map(applicationService::getObject).collect(Collectors.toSet()));

        return repository.save(portfolio);
    }
    
    @Transactional
    public Portfolio updateIsArchivedById(Long id, UpdatePortfolioIsArchivedDTO updatePortfolioIsArchivedDTO) {
        Portfolio portfolio = getObject(id);
        portfolio.setIsArchived(updatePortfolioIsArchivedDTO.getIsArchived());
        
        return repository.save(portfolio);
    }
}
