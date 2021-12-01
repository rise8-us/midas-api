package mil.af.abms.midas.api.measure;

import org.springframework.stereotype.Repository;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;

@Repository
public interface MeasureRepository extends RepositoryInterface<Measure, MeasureDTO> {
}
