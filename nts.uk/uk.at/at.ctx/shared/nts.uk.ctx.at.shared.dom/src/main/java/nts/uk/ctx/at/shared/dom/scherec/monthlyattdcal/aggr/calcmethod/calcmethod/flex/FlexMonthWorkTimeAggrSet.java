package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex;

import java.io.Serializable;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

@Getter
/** フレックス勤務基本設定 */
public abstract class FlexMonthWorkTimeAggrSet extends AggregateRoot implements Serializable{

	/** Serializable */
	protected static final long serialVersionUID = 1L;

	/** 会社ID */
	protected String comId;

	/** 集計方法 */
	protected FlexAggregateMethod aggrMethod;

	/** 不足設定 */
	protected ShortageFlexSetting insufficSet;

	/** 法定内集計設定 */
	protected AggregateTimeSetting legalAggrSet;

	/** フレックス時間の扱い */
	protected FlexTimeHandle flexTimeHandle;
	
	protected FlexMonthWorkTimeAggrSet () {}

	/** 清算期間が複数月か判断する */
	public boolean isMultiMonthSettlePeriod() {
		return insufficSet.getSettlePeriod() == SettlePeriod.MULTI_MONTHS;
	}
}
