package nts.uk.ctx.at.shared.dom.calculation.holiday.roundingmonth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingTimeUnit;

@AllArgsConstructor
@Getter
public class RoundingMonth extends AggregateRoot{
	/** 会社ID */
	private String companyId;
	
	/**勤怠項目ID*/
	private TimeItemId timeItemId;
	
	/** 丸め単位*/
	public RoundingTimeUnit unit;
	
	/** 端数処理 */
	public Rounding rounding;
	
	public static RoundingMonth createFromJavaType(String companyId, String timeItemId, int unit, int rounding){
		return new RoundingMonth(companyId,new TimeItemId(timeItemId), EnumAdaptor.valueOf(unit, RoundingTimeUnit.class), EnumAdaptor.valueOf(rounding, Rounding.class));
	}
}
