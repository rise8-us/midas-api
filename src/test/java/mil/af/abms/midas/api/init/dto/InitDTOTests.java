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
        
        assertThat(dto.getRoles().size()).isEqualTo(6);
        assertThat(dto.getProjectJourneyMap().size()).isEqualTo(4);
        assertThat(dto.getAssertionStatus().size()).isEqualTo(5);
        assertThat(dto.getSonarqubeMaintainability().size()).isEqualTo(6);
        assertThat(dto.getSonarqubeReliability().size()).isEqualTo(6);
        assertThat(dto.getSonarqubeSecurity().size()).isEqualTo(6);

    }

}
