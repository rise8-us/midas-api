package mil.af.abms.midas.api.roadmap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.dtos.IsHiddenDTO;
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
            .with(p -> p.setProduct(product))
            .with(p -> p.setStartDate(LocalDate.of(2021, 10, 10)))
            .with(p -> p.setDueDate(LocalDate.of(2021, 11, 10)))
            .get();
    UpdateRoadmapDTO updateRoadmapDTO = new UpdateRoadmapDTO(
            "Home One", "dev roadmap", RoadmapStatus.COMPLETE, 1L, "2021-11-10", "2021-11-10"
    );

    @Test
    void should_create_roadmap() {
        CreateRoadmapDTO createRoadmapDTO = new CreateRoadmapDTO(
                "MIDAS", "dev roadmap", 4L, RoadmapStatus.IN_PROGRESS, "2021-10-10", "2021-11-10"
        );

        when(productService.findById(createRoadmapDTO.getProductId())).thenReturn(product);
        when(roadmapRepository.save(roadmap)).thenReturn(new Roadmap());

        roadmapService.create(createRoadmapDTO);

        verify(roadmapRepository, times(1)).save(roadmapCaptor.capture());
        Roadmap roadmapSaved = roadmapCaptor.getValue();

        assertThat(roadmapSaved.getTitle()).isEqualTo(createRoadmapDTO.getTitle());
        assertThat(roadmapSaved.getStatus()).isEqualTo(createRoadmapDTO.getStatus());
        assertThat(roadmapSaved.getDescription()).isEqualTo(createRoadmapDTO.getDescription());
        assertThat(roadmapSaved.getProduct().getId()).isEqualTo(product.getId());
        assertThat(roadmapSaved.getStartDate()).isEqualTo(roadmap.getStartDate());
        assertThat(roadmapSaved.getDueDate()).isEqualTo(roadmap.getDueDate());
        assertThat(roadmapSaved.getCompletedAt()).isEqualTo(roadmap.getCompletedAt());
    }

    @Test
    void should_bulk_update_roadmap() {
        doReturn(roadmap).when(roadmapService).updateById(1L, updateRoadmapDTO);

        roadmapService.bulkUpdate(List.of(updateRoadmapDTO));
        verify(roadmapService, times(1)).updateById(1L, updateRoadmapDTO);

    }

    @Test
    void should_update_roadmap_by_id() {
        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));
        when(roadmapRepository.save(roadmap)).thenReturn(roadmap);

        roadmapService.updateById(1L, updateRoadmapDTO);

        verify(roadmapRepository, times(1)).save(roadmapCaptor.capture());
        Roadmap roadmapSaved = roadmapCaptor.getValue();

        assertThat(roadmapSaved.getTitle()).isEqualTo(updateRoadmapDTO.getTitle());
        assertThat(roadmapSaved.getStatus()).isEqualTo(updateRoadmapDTO.getStatus());
        assertThat(roadmapSaved.getDescription()).isEqualTo(updateRoadmapDTO.getDescription());
        assertThat(roadmapSaved.getStartDate()).isEqualTo(roadmap.getStartDate());
        assertThat(roadmapSaved.getDueDate()).isEqualTo(roadmap.getDueDate());
        assertThat(roadmapSaved.getCompletedAt()).isEqualTo(roadmap.getCompletedAt());
    }

    @Test
    void should_not_update_completed_at() {
        Roadmap roadmap1 = new Roadmap();
        BeanUtils.copyProperties(roadmap, roadmap1);
        roadmap1.setStatus(RoadmapStatus.COMPLETE);
        roadmap1.setCompletedAt(LocalDateTime.of(2021, 11, 10, 10, 10));

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap1));
        when(roadmapRepository.save(roadmap1)).thenReturn(roadmap1);

        roadmapService.updateById(1L, updateRoadmapDTO);

        verify(roadmapRepository, times(1)).save(roadmapCaptor.capture());
        Roadmap roadmapSaved = roadmapCaptor.getValue();

        assertThat(roadmapSaved.getCompletedAt()).isEqualTo(roadmap1.getCompletedAt());
    }

    @Test
    void should_update_isHidden() {
        IsHiddenDTO isHiddenDTO = new IsHiddenDTO(true);

        when(roadmapRepository.findById(roadmap.getId())).thenReturn(Optional.of(roadmap));
        when(roadmapRepository.save(roadmap)).thenReturn(roadmap);

        roadmapService.updateIsHidden(roadmap.getId(), isHiddenDTO);

        verify(roadmapRepository, times(1)).save(roadmapCaptor.capture());
        var roadmapSaved = roadmapCaptor.getValue();

        assertThat(roadmapSaved.getIsHidden()).isEqualTo(isHiddenDTO.getIsHidden());
    }

}
