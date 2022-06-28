package mil.af.abms.midas.api.portfolio.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.capability.dto.CapabilityDTO;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.user.dto.BasicUserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDTO implements AbstractDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private List<Long> productIds;
    private Integer gitlabGroupId;
    private Long sourceControlId;
    private PersonnelDTO personnel;
    private String vision;
    private String mission;
    private String problemStatement;
    private List<CapabilityDTO> capabilities;
    private String ganttNote;
    private LocalDateTime ganttNoteModifiedAt;
    private BasicUserDTO ganttNoteModifiedBy;
    private LocalDate sprintStartDate;
    private Integer sprintDurationInDays;
}
