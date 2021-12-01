package mil.af.abms.midas.api.roadmap;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.roadmap.dto.CreateRoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.RoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.UpdateRoadmapDTO;
import mil.af.abms.midas.enums.RoadmapStatus;

@Service
public class RoadmapService extends AbstractCRUDService<Roadmap, RoadmapDTO, RoadmapRepository> {

    private ProductService productService;

    public RoadmapService(RoadmapRepository repository) {
        super(repository, Roadmap.class, RoadmapDTO.class);
    }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    @Transactional
    public List<Roadmap> bulkUpdate(List<UpdateRoadmapDTO> dtos) {
        return dtos.stream().map(r -> updateById(r.getId(), r)).collect(Collectors.toList());
    }

    @Transactional
    public Roadmap create(CreateRoadmapDTO dto) {
        Roadmap newRoadmap = Builder.build(Roadmap.class)
                .with(r -> r.setTitle(dto.getTitle()))
                .with(r -> r.setDescription(dto.getDescription()))
                .with(r -> r.setIsHidden(false))
                .with(r -> r.setStatus(dto.getStatus()))
                .with(r -> r.setStartDate(TimeConversion.getLocalDateOrNullFromObject(dto.getStartDate())))
                .with(r -> r.setDueDate(TimeConversion.getLocalDateOrNullFromObject(dto.getDueDate())))
                .with(r -> r.setCompletedAt(getDateTimeIfComplete(dto.getStatus(), null)))
                .with(r -> r.setProduct(productService.findById(dto.getProductId())))
                .get();

        return repository.save(newRoadmap);
    }

    @Transactional
    public Roadmap updateById(Long id, UpdateRoadmapDTO dto) {
        Roadmap foundRoadmap = findById(id);
        foundRoadmap.setTitle(dto.getTitle());
        foundRoadmap.setDescription(dto.getDescription());
        foundRoadmap.setStatus(dto.getStatus());
        foundRoadmap.setStartDate(TimeConversion.getLocalDateOrNullFromObject(dto.getStartDate()));
        foundRoadmap.setDueDate(TimeConversion.getLocalDateOrNullFromObject(dto.getDueDate()));
        foundRoadmap.setCompletedAt(getDateTimeIfComplete(dto.getStatus(), foundRoadmap.getCompletedAt()));

        return repository.save(foundRoadmap);
    }

    public Roadmap updateIsHidden(Long id, IsHiddenDTO dto) {
        var roadmap = findById(id);

        roadmap.setIsHidden(dto.getIsHidden());

        return repository.save(roadmap);
    }

    private LocalDateTime getDateTimeIfComplete(RoadmapStatus status, LocalDateTime completedAt) {
        if (completedAt != null) {
            return completedAt;
        } else if (status.equals(RoadmapStatus.COMPLETE)) {
            return LocalDateTime.now();
        } else {
            return null;
        }
    }

}
