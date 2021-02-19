package us.rise8.mixer.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepositoryInterface<
        E extends AbstractEntity<D>, D extends AbstractDTO> extends JpaRepository<E, Long>, JpaSpecificationExecutor<E> {
}
