package mil.af.abms.midas.api;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.sourcecontrol.SourceControl;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractProductPortfolio<D extends AbstractDTO> extends AbstractEntity<D> {

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

}
