package mil.af.abms.midas.api.problem.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UpdateProblemIsCurrentDTO implements Serializable {
    private Boolean isCurrent;
}
