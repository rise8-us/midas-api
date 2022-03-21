package mil.af.abms.midas.api.metrics.metricspageview;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.metrics.AbstractMetricsService;
import mil.af.abms.midas.api.metrics.dtos.MetricsPageViewDTO;
import mil.af.abms.midas.api.metrics.metricspageview.dto.CreateOrUpdatePageViewsDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;

@Service
public class MetricsPageViewService extends AbstractMetricsService<MetricsPageView, MetricsPageViewDTO, MetricsPageViewRepository> {

    private UserService userService;

    public MetricsPageViewService(MetricsPageViewRepository repository) {
        super(repository, MetricsPageView.class, MetricsPageViewDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Transactional
    public MetricsPageView create(LocalDate id, CreateOrUpdatePageViewsDTO dto) {
        User user = userService.getUserBySecContext();
        Map<String, Set<Long>> pageViews = new HashMap<>();
        pageViews.put(dto.getPathname(), Set.of(user.getId()));

        MetricsPageView newMetricsPageView = Builder.build(MetricsPageView.class)
                .with(a -> a.setId(id))
                .with(a -> a.setPageViews(pageViews))
                .get();
        return repository.save(newMetricsPageView);
    }

    @Transactional
    public MetricsPageView updateById(LocalDate id, CreateOrUpdatePageViewsDTO dto) {
        User user = userService.getUserBySecContext();
        MetricsPageView metricsPageViewToUpdate = findById(id);
        Map<String, Set<Long>> pageViews = new HashMap<>(metricsPageViewToUpdate.getPageViews());

        String currentPath = dto.getPathname();
        Set<Long> currentSet = new HashSet<>(pageViews.getOrDefault(currentPath, new HashSet<>()));

        currentSet.add((user.getId()));
        pageViews.put(currentPath, currentSet);

        MetricsPageView newMetricsPageView = Builder.build(MetricsPageView.class)
                .with(a -> a.setId(id))
                .with(a -> a.setPageViews(pageViews))
                .get();
        return repository.save(newMetricsPageView);
    }

    @Transactional
    public MetricsPageView determineCreateOrUpdate(CreateOrUpdatePageViewsDTO dto) {
        LocalDate id = LocalDate.now();
        MetricsPageView metricsPageViewToCreateOrUpdate = findByIdOrNull(id);
        return metricsPageViewToCreateOrUpdate == null ? create(id, dto) : updateById(id, dto);
    }
}
