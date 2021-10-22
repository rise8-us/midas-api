package mil.af.abms.midas.api.backupandrestore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.backupandrestore.dto.BackupDTO;
import mil.af.abms.midas.clients.MySQLClient;

@WebMvcTest({BackupAndRestoreController.class})
class BackupAndRestoreControllerTests extends ControllerTestHarness {

    @MockBean
    MySQLClient mySQLClient;

    @MockBean
    BackupAndRestoreService service;

    private final Set<String> tableNames = Set.of("foo");

    private final BackupDTO backupDTO = new BackupDTO(
            "mockSQLDump"
    );

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_list_table_names() throws Exception {

        when(mySQLClient.getTableNames()).thenReturn(tableNames);

        mockMvc.perform(get("/api/dbActions/tableNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(tableNames.toArray()[0]));

    }

    @Test
    void should_create_backup_JSON() throws Exception {

        when(mySQLClient.exportToSql()).thenReturn(backupDTO.getMysqlDump());

        mockMvc.perform(get("/api/dbActions/backupJSON"))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$.mysqlDump").value(backupDTO.getMysqlDump()));

    }

    @Test
    void should_create_backup_string() throws Exception {

        when(mySQLClient.exportToSql()).thenReturn(backupDTO.getMysqlDump());

        mockMvc.perform(get("/api/dbActions/backupString"))
                .andExpect(status().isOk())
                .andExpect(content().string(backupDTO.getMysqlDump()));

    }

    @Test
    void should_restore_backup_JSON() throws Exception {

        when(mySQLClient.restore(backupDTO.getMysqlDump())).thenReturn(true);

        mockMvc.perform(post("/api/dbActions/restoreJSON")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(backupDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(content().string("true"));
    }

    @Test
    void should_restore_backup_string() throws Exception {

        when(mySQLClient.restore(backupDTO.getMysqlDump())).thenReturn(true);

        mockMvc.perform(post("/api/dbActions/restoreString")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(backupDTO.getMysqlDump())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(content().string("true"));
    }

}
