package mil.af.abms.midas.api.portfolio;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class PortfolioService extends AbstractCRUDService<Portfolio, PortfolioDTO, PortfolioRepository> {

    private PersonnelService personnelService;
    private ProductService productService;
    private SourceControlService sourceControlService;

    public PortfolioService(PortfolioRepository repository) {
        super(repository, Portfolio.class, PortfolioDTO.class);
    }

    @Autowired
    public void setSourceControlService(SourceControlService sourceControlService) { this.sourceControlService = sourceControlService; }

    @Autowired
    public void setPersonnelService(PersonnelService personnelService) { this.personnelService = personnelService; }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    @Transactional
    public Portfolio create(CreatePortfolioDTO dto) {
        CreatePersonnelDTO createPersonnelDTO = Optional.ofNullable(dto.getPersonnel()).isPresent() ?
                dto.getPersonnel() : new CreatePersonnelDTO();
        Personnel personnel = personnelService.create(createPersonnelDTO);

        Portfolio newPortfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setDescription(dto.getDescription()))
                .with(p -> p.setPersonnel(personnel))
                .with(p -> p.setGitlabGroupId(dto.getGitlabGroupId()))
                .with(p -> p.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId())))
                .with(p -> p.setVision(dto.getVision()))
                .with(p -> p.setMission(dto.getMission()))
                .with(p -> p.setProblemStatement(dto.getProblemStatement()))
                .get();

        Optional.ofNullable(dto.getProductIds()).ifPresent(productIds ->
                newPortfolio.setProducts(productIds.stream().map(productService::findById).collect(Collectors.toSet())));

        return repository.save(newPortfolio);
    }

    @Transactional
    public Portfolio updateById(Long id, UpdatePortfolioDTO dto) {
        Portfolio foundPortfolio = findById(id);

        foundPortfolio.setName(dto.getName());
        foundPortfolio.setDescription(dto.getDescription());
        foundPortfolio.setGitlabGroupId(dto.getGitlabGroupId());
        foundPortfolio.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId()));
        foundPortfolio.setVision(dto.getVision());
        foundPortfolio.setMission(dto.getMission());
        foundPortfolio.setProblemStatement(dto.getProblemStatement());
        Optional.ofNullable(dto.getProductIds()).ifPresent(productIds ->
                foundPortfolio.setProducts(productIds.stream().map(productService::findById).collect(Collectors.toSet())));
        Optional.ofNullable(dto.getPersonnel()).ifPresent(updatePersonnelDTO -> {
            Personnel personnel = personnelService.updateById(foundPortfolio.getPersonnel().getId(), dto.getPersonnel());
            foundPortfolio.setPersonnel(personnel);
        });
        return repository.save(foundPortfolio);
    }

    @Transactional
    public Portfolio updateIsArchivedById(Long id, IsArchivedDTO isArchivedDTO) {
        Portfolio portfolio = findById(id);

        portfolio.setIsArchived(isArchivedDTO.getIsArchived());

        return repository.save(portfolio);
    }

    public Portfolio findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Product.class.getSimpleName(), "name", name));
    }
}
