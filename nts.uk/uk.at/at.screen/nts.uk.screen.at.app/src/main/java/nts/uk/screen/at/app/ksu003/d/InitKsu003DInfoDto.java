package nts.uk.screen.at.app.ksu003.d;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.screen.at.app.kmk.kmk017.worktimeworkplace.WorkManagementMultipleDto;
import nts.uk.screen.at.app.ksu003.d.dto.ScheFuncControlDto;

@AllArgsConstructor
@Data
public class InitKsu003DInfoDto {
    WorkManagementMultipleDto workManagementMulti;
    ScheFuncControlDto scheFuncControl;
}
