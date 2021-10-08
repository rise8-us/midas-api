package mil.af.abms.midas.api.missionthread;

import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.missionthread.dto.CreateMissionThreadDTO;
import mil.af.abms.midas.api.missionthread.dto.MissionThreadDTO;
import mil.af.abms.midas.api.missionthread.dto.UpdateMissionThreadDTO;

@Service
public class MissionThreadService extends AbstractCRUDService<MissionThread, MissionThreadDTO, MissionThreadRepository> {

    public MissionThreadService(MissionThreadRepository repository) {
        super(repository, MissionThread.class, MissionThreadDTO.class);
    }

    public MissionThread create(CreateMissionThreadDTO dto) {
        MissionThread newMissionThread = Builder.build(MissionThread.class)
                .with(d -> d.setTitle(dto.getTitle()))
                .get();

        return repository.save(newMissionThread);
    }

    public MissionThread updateById(Long id, UpdateMissionThreadDTO dto) {
        MissionThread missionThread = findById(id);

        missionThread.setTitle(dto.getTitle());

        return repository.save(missionThread);
    }

    public MissionThread updateIsArchived(Long id, IsArchivedDTO dto) {
        MissionThread missionThread = findById(id);

        missionThread.setIsArchived(dto.getIsArchived());

        return repository.save(missionThread);
    }

}
