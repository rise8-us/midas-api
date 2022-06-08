package mil.af.abms.midas.api.portfolio;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class PortfolioService extends AbstractCRUDService<Portfolio, PortfolioDTO, PortfolioRepository> {

    private PersonnelService personnelService;
    private ProductService productService;
    private SourceControlService sourceControlService;
    private CapabilityService capabilityService;
    private UserService userService;

    public PortfolioService(PortfolioRepository repository) {
        super(repository, Portfolio.class, PortfolioDTO.class);
    }

    @Autowired
    public void setSourceControlService(SourceControlService sourceControlService) { this.sourceControlService = sourceControlService; }

    @Autowired
    public void setPersonnelService(PersonnelService personnelService) { this.personnelService = personnelService; }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    @Autowired
    public void setCapabilityService(CapabilityService capabilityService) { this.capabilityService = capabilityService; }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Transactional
    public Portfolio create(CreatePortfolioDTO dto) {
        CreatePersonnelDTO createPersonnelDTO = Optional.ofNullable(dto.getPersonnel()).isPresent() ?
                dto.getPersonnel() : new CreatePersonnelDTO();
        Personnel personnel = personnelService.create(createPersonnelDTO);

        Portfolio newPortfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setPersonnel(personnel))
                .with(p -> p.setSprintStartDate(Optional.ofNullable(dto.getSprintStartDate()).orElse(LocalDate.now())))
                .with(p -> p.setSprintDurationInDays(Optional.ofNullable(dto.getSprintDurationInDays()).orElse(7)))
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

        Optional.ofNullable(dto.getGanttNote()).ifPresent(ganttNote -> {
            if (!Objects.equals(ganttNote, foundPortfolio.getGanttNote())) {
                foundPortfolio.setGanttNote(ganttNote);
                foundPortfolio.setGanttNoteModifiedAt(LocalDateTime.now());
                foundPortfolio.setGanttNoteModifiedBy(userService.getUserBySecContext());
            }
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
        Optional.ofNullable(dto.getSprintStartDate()).ifPresent(portfolio::setSprintStartDate);
        Optional.ofNullable(dto.getSprintDurationInDays()).ifPresent(portfolio::setSprintDurationInDays);

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

    public List<Long> getAllPortfolioIds() {
        return repository.findAll().stream().map(Portfolio::getId).collect(Collectors.toList());
    }

}
