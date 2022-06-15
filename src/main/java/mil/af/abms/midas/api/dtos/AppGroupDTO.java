package mil.af.abms.midas.api.dtos;

import java.io.Serializable;

public interface AppGroupDTO extends Serializable {
    String getName();
    Integer getGitlabGroupId();
    Long getSourceControlId();
}
