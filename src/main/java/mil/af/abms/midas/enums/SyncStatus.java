package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SyncStatus {
    SYNC_ERROR, SYNCING, SYNCED
}
