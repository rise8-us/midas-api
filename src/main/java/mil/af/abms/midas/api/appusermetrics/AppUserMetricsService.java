package mil.af.abms.midas.api.appusermetrics;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.Roles;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class AppUserMetricsService {

    private final AppUserMetricsRepository repository;

    public AppUserMetricsService(AppUserMetricsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void create(LocalDate id, User user) {
        AppUserMetrics newAppUserMetrics = Builder.build(AppUserMetrics.class)
                .with(a -> a.setId(id))
                .with(a -> a.setUniqueLogins(0L))
                .get();
        repository.save(newAppUserMetrics);
        updateUniqueRoles(id, user);
    }

    @Transactional
    public void incrementUniqueLogins(LocalDate id) {
        AppUserMetrics appUserMetricsToUpdate = findById(id);
        appUserMetricsToUpdate.setUniqueLogins(appUserMetricsToUpdate.getUniqueLogins() + 1);
    }

    @Transactional
    public void updateById(LocalDate id, User user) {
        AppUserMetrics appUserMetricsToUpdate = findById(id);
        updateUniqueRoles(id, user);
        repository.save(appUserMetricsToUpdate);
    }

    @Transactional
    public void determineCreateOrUpdate(LocalDate id, User user) {
        AppUserMetrics appUserMetricsToUpdateOrCreate = findByIdOrNull(id);
        if (appUserMetricsToUpdateOrCreate == null) {
            create(id, user);
        } else {
            updateById(id, user);
        }
    }

    protected void updateUniqueRoles(LocalDate id, User user) {
        AppUserMetrics uniqueLoginEntry = findById(id);
        Map<String, Set<Object>> uniqueRoleCounts = uniqueLoginEntry.getUniqueRoleMetrics();
        updateRoleCountByEnum(uniqueRoleCounts, user);
    }

    protected void updateRoleCountByEnum(Map<String, Set<Object>> uniqueRoleCounts, User user) {
        Map<Roles, Boolean> mapOfUserRoles = Roles.getRoles(user.getRoles());

        if (user.getRoles() > 0) {
            for (Roles role : mapOfUserRoles.keySet()) {
                Set<Object> listOfUsersWithRole = new HashSet<>(uniqueRoleCounts.getOrDefault(role.getName(), new HashSet<>()));
                if (mapOfUserRoles.get(role)) {
                    listOfUsersWithRole.add(user.getId().intValue());
                } else {
                    listOfUsersWithRole.remove(user.getId().intValue());
                }
                uniqueRoleCounts.put(role.getName(), listOfUsersWithRole);
            }
            updateUnassignedRole(uniqueRoleCounts, user.getId(), false);
        } else {
            updateUnassignedRole(uniqueRoleCounts, user.getId(), true);
        }
    }

    protected void updateUnassignedRole(Map<String, Set<Object>> uniqueRoleCounts, Long userId, Boolean unassigned) {
        Set<Object> listOfUsersWithRole = new HashSet<>(uniqueRoleCounts.getOrDefault("UNASSIGNED", new HashSet<>()));
        if (unassigned) listOfUsersWithRole.add(userId.intValue());
        else listOfUsersWithRole.remove(userId.intValue());
        uniqueRoleCounts.put("UNASSIGNED", listOfUsersWithRole);
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

        PageRequest pageRequest = PageRequest.of(page, size, direction, sortBy);
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
