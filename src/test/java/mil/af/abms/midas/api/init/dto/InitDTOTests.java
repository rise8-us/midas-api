package mil.af.abms.midas.api.init.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.user.dto.UserDTO;
 
class InitDTOTests {
    
    @Test
    void should_get_fields() {
        InitDTO dto = new InitDTO("unclass", "cui", new UserDTO(), List.of(), Set.of());
        
        assertThat(dto.getRoles().size()).isEqualTo(8);
        assertThat(dto.getProjectJourneyMap().size()).isEqualTo(4);
        assertThat(dto.getAssertionStatus().size()).isEqualTo(5);
        assertThat(dto.getSonarqubeReliability().size()).isEqualTo(6);
        assertThat(dto.getSonarqubeMaintainability().size()).isEqualTo(6);
        assertThat(dto.getSonarqubeSecurity().size()).isEqualTo(6);
        assertThat(dto.getRoadmapType().size()).isEqualTo(2);
        assertThat(dto.getRoadmapStatus().size()).isEqualTo(3);
        assertThat(dto.getCompletionType().size()).isEqualTo(5);
        assertThat(dto.getFeedbackRating().size()).isEqualTo(5);
        assertThat(dto.getUserType().size()).isEqualTo(4);

    }

}
