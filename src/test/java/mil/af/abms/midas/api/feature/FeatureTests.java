package mil.af.abms.midas.api.feature;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.feature.dto.FeatureDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

class FeatureTests {
    private final Product product = Builder.build(Product.class).with(u -> u.setId(4L)).get();
    private final Feature feature = Builder.build(Feature.class)
            .with(f -> f.setId(1L))
            .with(f -> f.setTitle("title"))
            .with(f -> f.setDescription("dev feature"))
            .with(f -> f.setProduct(product))
            .get();
    private final FeatureDTO featureDTOExpected = Builder.build(FeatureDTO.class)
            .with(f -> f.setId(feature.getId()))
            .with(f -> f.setTitle(feature.getTitle()))
            .with(f -> f.setDescription(feature.getDescription()))
            .with(f -> f.setCreationDate(feature.getCreationDate()))
            .with(f -> f.setProductId(product.getId()))
            .get();

    @Test
    void should_have_all_featureDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Feature.class, fields::add);

        assertThat(fields.size()).isEqualTo(FeatureDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Feature feature2 = new Feature();
        BeanUtils.copyProperties(feature, feature2);

        assertTrue(feature.equals(feature));
        assertFalse(feature.equals(null));
        assertFalse(feature.equals(new User()));
        assertFalse(feature.equals(new Feature()));
        assertTrue(feature.equals(feature2));
    }

    @Test
    void should_get_properties() {
        assertThat(feature.getId()).isEqualTo(1L);
        assertThat(feature.getTitle()).isEqualTo("title");
        assertThat(feature.getDescription()).isEqualTo("dev feature");
    }

    @Test
    void can_return_dto() {
        assertThat(feature.toDto()).isEqualTo(featureDTOExpected);
    }
}
