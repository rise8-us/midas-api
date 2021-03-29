package mil.af.abms.midas.api.tag;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.tag.dto.TagDTO;

public interface TagRepository extends RepositoryInterface<Tag, TagDTO> {
    Optional<Tag> findByLabel(String label);
}
