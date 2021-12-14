package nts.uk.ctx.at.aggregation.app.find.schedulecounter.wkplaborcostandtime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
