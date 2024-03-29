package mil.af.abms.midas.api;

import java.util.Set;

import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.enums.ProgressionStatus;

public interface Commentable {

    public Set<Comment> getComments();
    public void setComments(Set<Comment> comments);
    public AbstractDTO toDto();
    public String getLowercaseClassName();
    public ProgressionStatus getStatus();
    public void setStatus(ProgressionStatus status);

}
