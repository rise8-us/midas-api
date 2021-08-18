package mil.af.abms.midas.api.feature;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.feature.dto.CreateFeatureDTO;
import mil.af.abms.midas.api.feature.dto.UpdateFeatureDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;

@ExtendWith(SpringExtension.class)
@Import(FeatureService.class)
class FeatureServiceTests {

    @SpyBean
    FeatureService featureService;
    @MockBean
    ProductService productService;
    @MockBean
    FeatureRepository featureRepository;
    @Captor
    ArgumentCaptor<Feature> featureCaptor;

    Product product = Builder.build(Product.class)
            .with(p -> p.setId(4L))
            .get();
    Feature feature = Builder.build(Feature.class)
            .with(p -> p.setTitle("MIDAS"))
            .with(p -> p.setDescription("dev feature"))
            .with(p -> p.setId(1L))
            .with(p -> p.setPosition(0))
            .with(p -> p.setProduct(product))
            .get();

    @Test
    void should_create_feature() {
        CreateFeatureDTO createFeatureDTO = new CreateFeatureDTO("MIDAS", "dev feature", 4L, 0);

        when(productService.findById(createFeatureDTO.getProductId())).thenReturn(product);
        when(featureRepository.save(feature)).thenReturn(new Feature());

        featureService.create(createFeatureDTO);

        verify(featureRepository, times(1)).save(featureCaptor.capture());
        Feature featureSaved = featureCaptor.getValue();

        assertThat(featureSaved.getTitle()).isEqualTo(createFeatureDTO.getTitle());
        assertThat(featureSaved.getDescription()).isEqualTo(createFeatureDTO.getDescription());
        assertThat(featureSaved.getPosition()).isEqualTo(createFeatureDTO.getIndex());
        assertThat(featureSaved.getProduct().getId()).isEqualTo(product.getId());
    }

    @Test
    void should_update_feature_by_id() {
        UpdateFeatureDTO updateFeatureDTO = new UpdateFeatureDTO("Home One", "dev feature", 1, null);

        when(featureRepository.findById(1L)).thenReturn(Optional.of(feature));
        when(featureRepository.save(feature)).thenReturn(feature);

        featureService.updateById(1L, updateFeatureDTO);

        verify(featureRepository, times(1)).save(featureCaptor.capture());
        Feature featureSaved = featureCaptor.getValue();

        assertThat(featureSaved.getTitle()).isEqualTo(updateFeatureDTO.getTitle());
        assertThat(featureSaved.getDescription()).isEqualTo(updateFeatureDTO.getDescription());
        assertThat(featureSaved.getPosition()).isEqualTo(updateFeatureDTO.getIndex());
    }

    @Test
    void should_bulk_update_feature() {
        UpdateFeatureDTO updateFeatureDTO = new UpdateFeatureDTO(
                "Home One", "dev feature",  1, 1L
        );

        doReturn(feature).when(featureService).updateById(1L, updateFeatureDTO);

        featureService.bulkUpdate(List.of(updateFeatureDTO));
        verify(featureService, times(1)).updateById(1L, updateFeatureDTO);

    }

}
