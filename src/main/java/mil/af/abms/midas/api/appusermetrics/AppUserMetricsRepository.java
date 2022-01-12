package mil.af.abms.midas.api.appusermetrics;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserMetricsRepository extends JpaRepository<AppUserMetrics, LocalDate>, JpaSpecificationExecutor<AppUserMetrics> {
}
