package mil.af.abms.midas.api;

import java.util.Set;

import mil.af.abms.midas.api.comment.Comment;

public interface Commentable {

    public Set<Comment> getComments();

    public AbstractDTO toDto();

    public String getLowercaseClassName();

}
