package mil.af.abms.midas.api.portfolio;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioIsArchivedDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class PortfolioService extends AbstractCRUDService<Portfolio, PortfolioDTO, PortfolioRepository> {

    UserService userService;
    ProductService productService;

    public PortfolioService(PortfolioRepository repository) {
        super(repository, Portfolio.class, PortfolioDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    @Transactional
    public Portfolio create(CreatePortfolioDTO createPortfolioDTO) {
        Portfolio newPortfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName(createPortfolioDTO.getName()))
                .with(p -> p.setDescription(createPortfolioDTO.getDescription()))
                .with(p -> p.setPortfolioManager(userService.findByIdOrNull(createPortfolioDTO.getPortfolioManagerId())))
                .with(p -> p.setProducts(createPortfolioDTO.getProductIds().stream().map(productService::getObject)
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
        Portfolio portfolio = getObject(id);
        portfolio.setName(updatePortfolioDTO.getName());
        portfolio.setPortfolioManager(userService.findByIdOrNull(updatePortfolioDTO.getPortfolioManagerId()));
        portfolio.setDescription(updatePortfolioDTO.getDescription());
        portfolio.setProducts(updatePortfolioDTO.getProductIds().stream()
                .map(productService::getObject).collect(Collectors.toSet()));

        return repository.save(portfolio);
    }
    
    @Transactional
    public Portfolio updateIsArchivedById(Long id, UpdatePortfolioIsArchivedDTO updatePortfolioIsArchivedDTO) {
        Portfolio portfolio = getObject(id);
        portfolio.setIsArchived(updatePortfolioIsArchivedDTO.getIsArchived());
        
        return repository.save(portfolio);
    }
}
