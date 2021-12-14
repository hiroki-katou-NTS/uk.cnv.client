package nts.uk.file.at.app.schedule.filemanagement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkPlaceScheCheckFileParam {
    
    private String fileID;
    
    private String captureSheet;
    
    private boolean captureCheckSheet;
    
    private String captureCell;
    
    private boolean captureCheckCell;
    
    private boolean overwrite;

}
