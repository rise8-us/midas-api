package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.product.validation.TeamExists;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateProductTeamDTO {
    @NotNull
    @TeamExists
    private Long teamId;
}
