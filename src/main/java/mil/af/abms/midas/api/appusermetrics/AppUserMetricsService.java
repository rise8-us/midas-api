package mil.af.abms.midas.api.appusermetrics;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class AppUserMetricsService {

    private final AppUserMetricsRepository repository;

    public AppUserMetricsService(AppUserMetricsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void create(LocalDate id) {
        AppUserMetrics newAppUserMetrics = Builder.build(AppUserMetrics.class)
                .with(a -> a.setId(id))
                .with(a -> a.setUniqueLogins(1L))
                .get();
        repository.save(newAppUserMetrics);
    }

    @Transactional
    public void updateById(LocalDate id) {
        var appUserMetricsToUpdate = findById(id);
        appUserMetricsToUpdate.setUniqueLogins(appUserMetricsToUpdate.getUniqueLogins() + 1);
        repository.save(appUserMetricsToUpdate);

    }

    @Transactional
    public void determineUpdateOrCreate(LocalDate id) {
        var appUserMetricsToUpdateOrCreate = findByIdOrNull(id);
        if (appUserMetricsToUpdateOrCreate == null) {
            create(id);
        } else {
            updateById(id);
        }
    }

    public Optional<AppUserMetrics> getById(LocalDate id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    @Transactional
    public AppUserMetrics findByIdOrNull(LocalDate id) { return getById(id).orElse(null); }

    @Transactional
    public AppUserMetrics findById(LocalDate id) {
        return getById(id).orElseThrow(() -> new EntityNotFoundException(AppUserMetrics.class.getSimpleName()));
    }

    @Transactional
    public Page<AppUserMetrics> search(Specification<AppUserMetrics> specs, Integer page, Integer size, String sortBy, String orderBy) {
        List<String> sortOptions = List.of("ASC", "DESC");
        page = Optional.ofNullable(page).orElse(0);
        size = Optional.ofNullable(size).orElse(Integer.MAX_VALUE);
        sortBy = Optional.ofNullable(sortBy).orElse("id");
        orderBy = Optional.ofNullable(orderBy).orElse("ASC");
        Sort.Direction direction = sortOptions.contains(orderBy.toUpperCase()) ? Sort.Direction.valueOf(orderBy) : Sort.Direction.ASC;

        var pageRequest = PageRequest.of(page, size, direction, sortBy);
        return repository.findAll(Specification.where(specs), pageRequest);
    }

    @Transactional
    public List<AppUserMetricsDTO> preparePageResponse(Page<AppUserMetrics> page, HttpServletResponse response) {
        response.addHeader("X-Total-Pages", String.valueOf(page.getTotalPages()));
        return toDTOs(page.getContent());
    }

    protected List<AppUserMetricsDTO> toDTOs(List<AppUserMetrics> entities) {
        return entities.stream().map(AppUserMetrics::toDto).collect(Collectors.toList());
    }

}
