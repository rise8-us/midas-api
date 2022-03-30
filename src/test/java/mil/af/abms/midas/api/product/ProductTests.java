package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.RoadmapType;

class ProductTests {

    private final Set<Project> projects = Set.of(Builder.build(Project.class).with(p -> p.setId(3L)).get());
    private final PersonnelDTO personnelDTO = Builder.build(PersonnelDTO.class)
            .with(d -> d.setTeamIds(Set.of()))
            .with(d -> d.setAdminIds(Set.of()))
            .get();

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setId(4L))
            .get();

    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("test product"))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setProjects(projects))
            .with(p -> p.setPersonnel(new Personnel()))
            .with(p -> p.setPortfolio(new Portfolio()))
            .with(p -> p.setRoadmapType(RoadmapType.GITLAB))
            .with(p -> p.setVision("vision"))
            .with(p -> p.setMission("mission"))
            .with(p -> p.setProblemStatement("problem"))
            .get();
    private final ProductDTO productDTO = Builder.build(ProductDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setName("Midas"))
            .with(d -> d.setDescription("test product"))
            .with(d -> d.setCreationDate(product.getCreationDate()))
            .with(d -> d.setIsArchived(false))
            .with(d -> d.setTags(new HashSet<>()))
            .with(d -> d.setProjectIds(Set.of(3L)))
            .with(d -> d.setSourceControlId(4L))
            .with(d -> d.setRoadmapType(RoadmapType.GITLAB))
            .with(d -> d.setPersonnel(personnelDTO))
            .with(d -> d.setPortfolioId(null))
            .with(d -> d.setVision("vision"))
            .with(d -> d.setMission("mission"))
            .with(d -> d.setProblemStatement("problem"))
            .get();

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Product.class, fields::add);

        assertThat(fields.size()).isEqualTo(ProductDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Product product2 = new Product();
        BeanUtils.copyProperties(product, product2);

        assertEquals(product, product);
        assertNotEquals(product, null);
        assertNotEquals(product, new User());
        assertNotEquals(product, new Product());
        assertEquals(product, product2);
    }

    @Test
    void should_get_properties() {
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Midas");
        assertThat(product.getDescription()).isEqualTo("test product");
        assertFalse(product.getIsArchived());
        assertThat(product.getProjects()).isEqualTo(projects);
    }

    @Test
    void can_return_dto() {
        assertThat(product.toDto()).isEqualTo(productDTO);
    }

}
