/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave;

import java.io.Serializable;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.LimitedHalfHdCnt;

/**
 * 半日年休上限回数
 */
@IntegerRange(min = 0, max = 99)
public class AnnualNumberDay extends IntegerPrimitiveValue<AnnualNumberDay> implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Instantiates a new annual number day.
     *
     * @param rawValue the raw value
     */
    public AnnualNumberDay(Integer rawValue) {
        super(rawValue);
    }
    
    public LimitedHalfHdCnt toLimitedTimeHdDays(){
    	return new LimitedHalfHdCnt(this.v());
    }
   
}
