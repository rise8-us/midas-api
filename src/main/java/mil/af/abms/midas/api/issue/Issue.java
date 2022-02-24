package mil.af.abms.midas.api.issue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractTimeConstrainedEntity;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.issue.dto.IssueDTO;
import mil.af.abms.midas.api.project.Project;

@Entity @Setter @Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "issue")
public class Issue extends AbstractTimeConstrainedEntity<IssueDTO> {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "INT", nullable = false)
    private Integer issueIid;

    @Column(columnDefinition = "VARCHAR(255)")
    private String issueUid;

    @Column(columnDefinition = "TEXT")
    private String state;

    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime syncedAt;

    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "BIGINT DEFAULT 1", nullable = false)
    private Long weight;

    @Column(columnDefinition = "TEXT")
    private String webUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany
    @JoinTable(
            name = "completion_gitlab_issue",
            joinColumns = @JoinColumn(name = "completion_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id", referencedColumnName = "id"))
    private Set<Completion> completions;

    public IssueDTO toDto() {
        return new IssueDTO(
                id,
                title,
                description,
                creationDate,
                startDate,
                dueDate,
                completedAt,
                updatedAt,
                syncedAt,
                issueIid,
                issueUid,
                state,
                webUrl,
                weight,
                getIdOrNull(project)
        );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(creationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue that = (Issue) o;
        return this.hashCode() == that.hashCode();
    }

}
