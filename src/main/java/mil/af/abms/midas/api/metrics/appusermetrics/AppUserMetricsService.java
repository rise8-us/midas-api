package mil.af.abms.midas.api.metrics.appusermetrics;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.metrics.AbstractMetricsService;
import mil.af.abms.midas.api.metrics.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.Roles;

@Service
public class AppUserMetricsService extends AbstractMetricsService<AppUserMetrics, AppUserMetricsDTO, AppUserMetricsRepository> {

    public AppUserMetricsService(AppUserMetricsRepository repository) {
        super(repository, AppUserMetrics.class, AppUserMetricsDTO.class);
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

    @Transactional
    public void incrementUniqueLogins(LocalDate id) {
        AppUserMetrics appUserMetricsToUpdate = findById(id);
        appUserMetricsToUpdate.setUniqueLogins(appUserMetricsToUpdate.getUniqueLogins() + 1);
    }

    protected void updateUniqueRoles(LocalDate id, User user) {
        AppUserMetrics uniqueLoginEntry = findById(id);
        Map<String, Set<Long>> uniqueRoleCounts = uniqueLoginEntry.getUniqueRoleMetrics();
        updateRoleCountByEnum(uniqueRoleCounts, user);
    }

    protected void updateRoleCountByEnum(Map<String, Set<Long>> uniqueRoleCounts, User user) {

        Map<Roles, Boolean> mapOfUserRoles = Roles.getRoles(user.getRoles());

        if (user.getRoles() > 0) {
            for (Map.Entry<Roles, Boolean> role : mapOfUserRoles.entrySet()) {
                Set<Long> listOfUsersWithRole = new HashSet<>(uniqueRoleCounts.getOrDefault(role.getKey().getName(), new HashSet<>()));
                if (mapOfUserRoles.get(role.getKey())) {
                    listOfUsersWithRole.add(user.getId());
                } else {
                    listOfUsersWithRole.remove(user.getId());
                }
                uniqueRoleCounts.put(role.getKey().getName(), listOfUsersWithRole);
            }
            updateUnassignedRole(uniqueRoleCounts, user.getId(), false);
        } else {
            updateUnassignedRole(uniqueRoleCounts, user.getId(), true);
        }
    }

    protected void updateUnassignedRole(Map<String, Set<Long>> uniqueRoleCounts, Long userId, Boolean unassigned) {
        Set<Long> listOfUsersWithRole = new HashSet<>(uniqueRoleCounts.getOrDefault("UNASSIGNED", new HashSet<>()));
        if (unassigned) listOfUsersWithRole.add(userId);
        else listOfUsersWithRole.remove(userId);
        uniqueRoleCounts.put("UNASSIGNED", listOfUsersWithRole);
    }

}
