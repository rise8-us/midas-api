package mil.af.abms.midas.api;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Setter;

import mil.af.abms.midas.api.sourcecontrol.SourceControl;

@Setter
@MappedSuperclass
public abstract class AbstractProductPortfolio<D extends AbstractDTO> extends AbstractEntity<D> implements AppGroup {

    @Column(columnDefinition = "TEXT")
    protected String vision;

    @Column(columnDefinition = "TEXT")
    protected String mission;

    @Column(columnDefinition = "TEXT")
    protected String problemStatement;

    @Column(columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
    protected String name;

    @Column(columnDefinition = "TEXT")
    protected String description;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    protected Boolean isArchived = false;

    @Column(columnDefinition = "INT")
    protected Integer gitlabGroupId;

    @ManyToOne
    protected SourceControl sourceControl;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getVision() {
        return this.vision;
    }

    @Override
    public String getMission() {
        return this.mission;
    }

    @Override
    public String getProblemStatement() {
        return this.problemStatement;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Boolean getIsArchived() {
        return this.isArchived;
    }

    @Override
    public Integer getGitlabGroupId() {
        return this.gitlabGroupId;
    }

    @Override
    public SourceControl getSourceControl() {
        return this.sourceControl;
    }
}
