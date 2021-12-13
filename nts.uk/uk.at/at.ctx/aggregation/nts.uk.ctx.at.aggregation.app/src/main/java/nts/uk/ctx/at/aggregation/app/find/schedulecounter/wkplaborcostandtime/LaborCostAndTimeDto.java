package nts.uk.ctx.at.aggregation.app.find.schedulecounter.wkplaborcostandtime;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.LaborCostAndTime;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborCostAndTimeDto {

    //利用区分
    private int useClassification;

    //時間
    private int time;

    //人件費
    private int laborCost;

    //予算
    private Integer budget;
    
    
    public LaborCostAndTime toDomain() {
    	
    	if (Optional.ofNullable(budget).isPresent()) {
    		
    		return LaborCostAndTime.createWithBudget(
    				NotUseAtr.valueOf(useClassification),
    				NotUseAtr.valueOf(time),
    				NotUseAtr.valueOf(laborCost),
    				NotUseAtr.valueOf(budget));
    	} else {
    		
    		return LaborCostAndTime.createWithoutBudget(
    				NotUseAtr.valueOf(useClassification),
    				NotUseAtr.valueOf(time),
    				NotUseAtr.valueOf(laborCost)
    				);
    	}
    }
}
