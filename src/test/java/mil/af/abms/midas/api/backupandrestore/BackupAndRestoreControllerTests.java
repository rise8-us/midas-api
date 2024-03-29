package mil.af.abms.midas.api.backupandrestore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.backupandrestore.dto.BackupRestoreDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.GzipHelper;

@WebMvcTest({BackupAndRestoreController.class})
class BackupAndRestoreControllerTests extends ControllerTestHarness {

    @MockBean
    BackupAndRestoreService service;

    private final BackupRestoreDTO backupRestoreDTO = new BackupRestoreDTO("FileName", false, false);
    private final List<String> s3Files = List.of("foo", "bar", "fooBar");
    private final Set<String> tableNames = Set.of("foo");

    S3Object object = Builder.build(S3Object.class)
            .with(o -> o.setBucketName("test bucket"))
            .with(o -> o.setKey("test key"))
            .with(o -> o.setObjectContent(GzipHelper.compressStringToInputStream("string")))
            .get();


    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_list_table_names() throws Exception {

        when(service.getTableNames()).thenReturn(tableNames);

        mockMvc.perform(get("/api/dbActions/tableNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(tableNames.toArray()[0]));
    }

    @Test
    void should_list_s3_file_names() throws Exception {

        when(service.getBackupFileNames()).thenReturn(s3Files);

        mockMvc.perform(get("/api/dbActions/fileNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(s3Files.get(0)));
    }

    @Test
    void should_create_backup() throws Exception {

        doNothing().when(service).backupToS3(anyString());

        mockMvc.perform(post("/api/dbActions/backup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(backupRestoreDTO))
                )
                .andExpect(status().isOk());
    }

    @Test
    void should_restore_when_given_s3_file_name() throws Exception {

        doNothing().when(service).restore(backupRestoreDTO);

        mockMvc.perform(post("/api/dbActions/restore")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(backupRestoreDTO))
                )
                .andExpect(status().isOk());
    }

    @Test
    void should_restore_when_clear_tokens_is_true() throws Exception {
        var restoreDTO = new BackupRestoreDTO("FileName", true, true);
        doNothing().when(service).restore(restoreDTO);

        mockMvc.perform(post("/api/dbActions/restore")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(restoreDTO))
                )
                .andExpect(status().isOk());
    }

    @Test
    void should_download_file() throws Exception {
        var stream = new ByteArrayResource(IOUtils.toByteArray(object.getObjectContent()));

        when(service.getFile(backupRestoreDTO.getFileName())).thenReturn(stream);

        mockMvc.perform(post("/api/dbActions/download")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(backupRestoreDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

}
