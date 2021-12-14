package nts.uk.file.at.app.schedule.export;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author anhnm
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkPlaceScheDataSource {
    public List<String> employeeIDs;
    
    public String startDate;
    
    public String endDate;

}
