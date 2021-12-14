package nts.uk.file.at.ws.schedule.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.file.at.app.schedule.filemanagement.CapturedRawDataDto;

@Getter
@Setter
@AllArgsConstructor
public class CaptureDataInput {

    private CapturedRawDataDto data;
    
    private boolean overwrite;
}
