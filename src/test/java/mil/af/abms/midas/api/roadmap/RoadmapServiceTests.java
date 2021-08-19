package mil.af.abms.midas.api.roadmap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.roadmap.dto.CreateRoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.UpdateRoadmapDTO;
import mil.af.abms.midas.enums.RoadmapStatus;

@ExtendWith(SpringExtension.class)
@Import(RoadmapService.class)
class RoadmapServiceTests {
   
    @SpyBean
    RoadmapService roadmapService;
    @MockBean
    ProductService productService;
    @MockBean
    RoadmapRepository roadmapRepository;
    @Captor
    ArgumentCaptor<Roadmap> roadmapCaptor;

    Product product = Builder.build(Product.class)
            .with(p -> p.setId(4L))
            .get();
    Roadmap roadmap = Builder.build(Roadmap.class)
            .with(p -> p.setTitle("MIDAS"))
            .with(p -> p.setStatus(RoadmapStatus.FUTURE))
            .with(p -> p.setDescription("dev roadmap"))
            .with(p -> p.setId(1L))
            .with(p -> p.setPosition(0))
            .with(p -> p.setProduct(product))
            .with(p -> p.setTargetDate(LocalDateTime.of(2021, 10, 10, 0, 0)))
            .get();

    @Test
    void should_create_roadmap() {
        CreateRoadmapDTO createRoadmapDTO = new CreateRoadmapDTO(
                "MIDAS", "dev roadmap", 4L, RoadmapStatus.IN_PROGRESS, 0, "2021-10-10"
        );

        when(productService.findById(createRoadmapDTO.getProductId())).thenReturn(product);
        when(roadmapRepository.save(roadmap)).thenReturn(new Roadmap());

        roadmapService.create(createRoadmapDTO);

        verify(roadmapRepository, times(1)).save(roadmapCaptor.capture());
        Roadmap roadmapSaved = roadmapCaptor.getValue();

        assertThat(roadmapSaved.getTitle()).isEqualTo(createRoadmapDTO.getTitle());
        assertThat(roadmapSaved.getStatus()).isEqualTo(createRoadmapDTO.getStatus());
        assertThat(roadmapSaved.getDescription()).isEqualTo(createRoadmapDTO.getDescription());
        assertThat(roadmapSaved.getPosition()).isEqualTo(createRoadmapDTO.getIndex());
        assertThat(roadmapSaved.getProduct().getId()).isEqualTo(product.getId());
        assertThat(roadmapSaved.getTargetDate()).isEqualTo(roadmap.getTargetDate());
    }

    @Test
    void should_bulk_update_roadmap() {
        UpdateRoadmapDTO updateRoadmapDTO = new UpdateRoadmapDTO(
                "Home One", "dev roadmap", RoadmapStatus.COMPLETE, 1, 1L, "2021-10-10"
        );

        doReturn(roadmap).when(roadmapService).updateById(1L, updateRoadmapDTO);

        roadmapService.bulkUpdate(List.of(updateRoadmapDTO));
        verify(roadmapService, times(1)).updateById(1L, updateRoadmapDTO);

    }

    @Test
    void should_update_roadmap_by_id() {
        UpdateRoadmapDTO updateRoadmapDTO = new UpdateRoadmapDTO(
                "Home One", "dev roadmap", RoadmapStatus.COMPLETE, 1, 1L, "2021-11-10"
        );

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));
        when(roadmapRepository.save(roadmap)).thenReturn(roadmap);

        roadmapService.updateById(1L, updateRoadmapDTO);

        verify(roadmapRepository, times(1)).save(roadmapCaptor.capture());
        Roadmap roadmapSaved = roadmapCaptor.getValue();

        assertThat(roadmapSaved.getTitle()).isEqualTo(updateRoadmapDTO.getTitle());
        assertThat(roadmapSaved.getStatus()).isEqualTo(updateRoadmapDTO.getStatus());
        assertThat(roadmapSaved.getDescription()).isEqualTo(updateRoadmapDTO.getDescription());
        assertThat(roadmapSaved.getPosition()).isEqualTo(updateRoadmapDTO.getIndex());
        assertThat(roadmapSaved.getTargetDate()).isEqualTo(LocalDateTime.of(2021, 11, 10, 0, 0));
    }

}
