package mil.af.abms.midas.api.metrics;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import mil.af.abms.midas.api.AbstractDTO;

@NoRepositoryBean
public interface MetricsCRUDRepository<
        E extends AbstractMetricsEntity<D>, D extends AbstractDTO> extends JpaRepository<E, LocalDate>, JpaSpecificationExecutor<E> {

}
