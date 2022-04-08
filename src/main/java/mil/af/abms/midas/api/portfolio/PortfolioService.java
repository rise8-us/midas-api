package mil.af.abms.midas.api.portfolio;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioInterfaceDTO;
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
    private CapabilityService capabilityService;
    private final SimpMessageSendingOperations websocket;

    public PortfolioService(PortfolioRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Portfolio.class, PortfolioDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setSourceControlService(SourceControlService sourceControlService) { this.sourceControlService = sourceControlService; }

    @Autowired
    public void setPersonnelService(PersonnelService personnelService) { this.personnelService = personnelService; }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    @Autowired
    public void setCapabilityService(CapabilityService capabilityService) { this.capabilityService = capabilityService; }

    @Transactional
    public Portfolio create(CreatePortfolioDTO dto) {
        CreatePersonnelDTO createPersonnelDTO = Optional.ofNullable(dto.getPersonnel()).isPresent() ?
                dto.getPersonnel() : new CreatePersonnelDTO();
        Personnel personnel = personnelService.create(createPersonnelDTO);

        Portfolio newPortfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setPersonnel(personnel))
                .get();

        updateCommonFields(dto, newPortfolio);

        return repository.save(newPortfolio);
    }

    @Transactional
    public Portfolio updateById(Long id, UpdatePortfolioDTO dto) {
        Portfolio foundPortfolio = findById(id);

        foundPortfolio.setName(dto.getName());
        Optional.ofNullable(dto.getPersonnel()).ifPresent(updatePersonnelDTO -> {
            Personnel personnel = personnelService.updateById(foundPortfolio.getPersonnel().getId(), dto.getPersonnel());
            foundPortfolio.setPersonnel(personnel);
        });

        updateCommonFields(dto, foundPortfolio);

        return repository.save(foundPortfolio);
    }

    protected void updateCommonFields(PortfolioInterfaceDTO dto, Portfolio portfolio) {
        portfolio.setDescription(dto.getDescription());
        portfolio.setGitlabGroupId(dto.getGitlabGroupId());
        portfolio.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId()));
        portfolio.setVision(dto.getVision());
        portfolio.setMission(dto.getMission());
        portfolio.setProblemStatement(dto.getProblemStatement());

        Optional.ofNullable(dto.getProductIds()).ifPresent(productIds ->
                portfolio.setProducts(productIds.stream().map(productService::findById).collect(Collectors.toSet())));
        Optional.ofNullable(dto.getCapabilityIds()).ifPresent(capabilityIds ->
                portfolio.setCapabilities(capabilityIds.stream().map(capabilityService::findById).collect(Collectors.toSet())));
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
