package mil.af.abms.midas.api.init.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Getter;

import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.FeedbackRating;
import mil.af.abms.midas.enums.ProgressionStatus;
import mil.af.abms.midas.enums.ProjectJourneyMap;
import mil.af.abms.midas.enums.RoadmapStatus;
import mil.af.abms.midas.enums.RoadmapType;
import mil.af.abms.midas.enums.Roles;
import mil.af.abms.midas.enums.SonarqubeMaintainability;
import mil.af.abms.midas.enums.SonarqubeReliability;
import mil.af.abms.midas.enums.SonarqubeSecurity;
import mil.af.abms.midas.enums.TagType;

@Getter
public class InitDTO implements Serializable {
    public InitDTO(String classificationString, String caveat, UserDTO userDTO, List<AnnouncementDTO> announcementDTOs, Set<Long> productIds) {
        this.classification = new ClassificationDTO(classificationString, caveat);
        this.userLoggedIn = userDTO;
        this.unseenAnnouncements = announcementDTOs;
        this.productIdsForLoggedInUser = productIds;
    }

    private final ClassificationDTO classification;
    private final UserDTO userLoggedIn;
    private final List<AnnouncementDTO> unseenAnnouncements;
    private final Set<Long> productIdsForLoggedInUser;
    private final List<RoleDTO> roles = Roles.toDTO();
    private final List<ProjectJourneyMapDTO> projectJourneyMap = ProjectJourneyMap.toDTO();
    private final List<ProgressionStatusDTO> assertionStatus = ProgressionStatus.toDTO();
    private final List<SonarqubeDTO> sonarqubeReliability = SonarqubeReliability.toDTO();
    private final List<SonarqubeDTO> sonarqubeMaintainability = SonarqubeMaintainability.toDTO();
    private final List<SonarqubeDTO> sonarqubeSecurity = SonarqubeSecurity.toDTO();
    private final TagType[] tagTypes = TagType.values();
    private final List<RoadmapTypeDTO> roadmapType = RoadmapType.toDTO();
    private final List<RoadmapStatusDTO> roadmapStatus = RoadmapStatus.toDTO();
    private final List<CompletionTypeDTO> completionType = CompletionType.toDTO();
    private final List<FeedbackRatingDTO> feedbackRating = FeedbackRating.toDTO();

}
