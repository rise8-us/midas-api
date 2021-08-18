package mil.af.abms.midas.api.feature;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.feature.dto.CreateFeatureDTO;
import mil.af.abms.midas.api.feature.dto.FeatureDTO;
import mil.af.abms.midas.api.feature.dto.UpdateFeatureDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.ProductService;

@Service
public class FeatureService extends AbstractCRUDService<Feature, FeatureDTO, FeatureRepository> {

    private ProductService productService;

    public FeatureService(FeatureRepository repository) {
        super(repository, Feature.class, FeatureDTO.class);
    }

    @Autowired
    public void setUserService(ProductService productService) { this.productService = productService; }

    @Transactional
    public List<Feature> bulkUpdate(List<UpdateFeatureDTO> dtos) {
        return dtos.stream().map(r -> updateById(r.getId(), r)).collect(Collectors.toList());
    }

    @Transactional
    public Feature create(CreateFeatureDTO dto) {
        Feature newFeature = Builder.build(Feature.class)
                .with(p -> p.setTitle(dto.getTitle()))
                .with(p -> p.setDescription(dto.getDescription()))
                .with(p -> p.setPosition(dto.getIndex()))
                .with(p -> p.setProduct(productService.findById(dto.getProductId())))
                .get();

        return repository.save(newFeature);
    }

    @Transactional
    public Feature updateById(Long id, UpdateFeatureDTO dto) {
        Feature foundFeature = findById(id);
        foundFeature.setTitle(dto.getTitle());
        foundFeature.setDescription(dto.getDescription());
        foundFeature.setPosition(dto.getIndex());

        return repository.save(foundFeature);
    }

}
