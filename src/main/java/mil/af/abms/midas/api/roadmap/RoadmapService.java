package mil.af.abms.midas.api.roadmap;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.roadmap.dto.CreateRoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.RoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.UpdateRoadmapDTO;

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
                .with(r -> r.setStatus(dto.getStatus()))
                .with(r -> r.setPosition(dto.getIndex()))
                .with(r -> r.setTargetDate(TimeConversion.getTime(dto.getTargetDate())))
                .with(r -> r.setProduct(productService.findById(dto.getProductId())))
                .get();

        return repository.save(newRoadmap);
    }

    @Transactional
    public Roadmap updateById(Long id, UpdateRoadmapDTO dto) {
        Roadmap foundRoadmap = findById(id);
        foundRoadmap.setTitle(dto.getTitle());
        foundRoadmap.setDescription(dto.getDescription());
        foundRoadmap.setPosition(dto.getIndex());
        foundRoadmap.setStatus(dto.getStatus());
        foundRoadmap.setTargetDate(TimeConversion.getTime(dto.getTargetDate()));

        return repository.save(foundRoadmap);
    }

}
