package mil.af.abms.midas.api.filemanager.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileManagerDTO implements Serializable {
    String fileName;
    MultipartFile file;
}
