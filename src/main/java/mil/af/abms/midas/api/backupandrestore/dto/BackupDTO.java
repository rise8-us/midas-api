package mil.af.abms.midas.api.backupandrestore.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupDTO implements Serializable {

    String mysqlDump;

}
