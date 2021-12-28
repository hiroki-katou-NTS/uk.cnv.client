package nts.uk.screen.at.app.dailyperformance.correction.dto;

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
@NoArgsConstructor
@AllArgsConstructor
public class GetWkpIDParam {
    
    public String companyId;
    
    public String wkpCode;
    
    public String baseDate;

}
