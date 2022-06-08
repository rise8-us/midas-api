package mil.af.abms.midas.api;

import mil.af.abms.midas.api.sourcecontrol.SourceControl;

public interface AppGroup {
    Long getId();
    String getVision();
    String getMission();
    String getProblemStatement();
    String getName();
    String getDescription();
    Boolean getIsArchived();
    Integer getGitlabGroupId();
    SourceControl getSourceControl();
}
