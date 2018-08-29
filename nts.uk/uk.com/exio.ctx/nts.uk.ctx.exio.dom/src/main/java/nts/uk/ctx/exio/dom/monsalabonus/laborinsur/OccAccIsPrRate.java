package nts.uk.ctx.exio.dom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

/**
* 労災保険料率
*/
@AllArgsConstructor
@Getter
public class OccAccIsPrRate extends AggregateRoot
{
    
    /**
    * 
    */
    private String ocAcIsPrRtId;
    
    /**
    * 履歴ID
    */
    private String hisId;
    
    /**
    * 労災保険事業No
    */
    private int occAccInsurBusNo;
    
    /**
    * 端数区分
    */
    private int fracClass;
    
    /**
    * 事業主負担率
    */
    private String empConRatio;
    
    
}
