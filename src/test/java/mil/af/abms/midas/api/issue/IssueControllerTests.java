package mil.af.abms.midas.api.issue;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.dtos.AddGitLabIssueWithProductDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;

@WebMvcTest({IssueController.class})
class IssueControllerTests extends ControllerTestHarness {

    @MockBean
    private IssueService issueService;

    @MockBean
    private ProjectService projectService;

    SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setBaseUrl("fake_url"))
            .with(sc -> sc.setToken("fake_token"))
            .get();

    Project project = Builder.build(Project.class)
            .with(p -> p.setName("name"))
            .with(p -> p.setId(2L))
            .with(p -> p.setSourceControl(sourceControl))
            .get();

    Issue issue = Builder.build(Issue.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(LocalDateTime.now()))
            .with(e -> e.setProject(project))
            .with(e -> e.setTitle("title"))
            .get();

    @BeforeEach
    void init() {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void throw_should_create_issue_gitLab_not_found() throws Exception {
        when(projectService.findById(any())).thenReturn(project);
        when(issueService.createOrUpdate(any(AddGitLabIssueWithProductDTO.class))).thenReturn(issue);

        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabIssueWithProductDTO(1, 2L)))
                )
                .andExpect(status().is(400))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Validation failed. 1 error(s)"))
                .andExpect(jsonPath("$.errors[0]").value("GitLab issue does not exist or cannot be found"));
    }

    @Test
    void should_create_issue() throws Exception {
        when(projectService.findById(any())).thenReturn(project);
        when(issueService.canAddIssue(any(), any())).thenReturn(true);
        when(issueService.createOrUpdate(any(AddGitLabIssueWithProductDTO.class))).thenReturn(issue);

        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabIssueWithProductDTO(1, 2L)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.projectId").value(project.getId()));
    }

    @Test
    void should_update_issue_by_id() throws Exception {
        when(issueService.updateById(anyLong())).thenReturn(issue);

        mockMvc.perform(get("/api/issues/sync/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.projectId").value(project.getId()));
    }

    @Test
    void should_sync_all_issues_by_project_id() throws Exception {
        when(issueService.gitlabIssueSync(anyLong())).thenReturn(List.of(issue));

        mockMvc.perform(get("/api/issues/sync/project/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_get_all_issues_by_project_id() throws Exception {
        when(issueService.getAllIssuesByProjectId(any())).thenReturn(List.of(issue));

        mockMvc.perform(get("/api/issues/project/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_get_all_issues_by_portfolioId_and_date_range() throws Exception {
        when(issueService.getAllIssuesByPortfolioIdAndDateRange(any(), any(), any())).thenReturn(List.of(issue));

        mockMvc.perform(get("/api/issues/portfolio/1/2000-01-01/2000-02-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_get_all_issues_by_product_id() throws Exception {
        when(issueService.getAllIssuesByProductId(any())).thenReturn(List.of(issue));

        mockMvc.perform(get("/api/issues/product/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_sync_all_issues_by_product_id() throws Exception {
        when(issueService.syncGitlabIssueForProduct(any())).thenReturn(Set.of(issue));

        mockMvc.perform(get("/api/issues/sync/product/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
