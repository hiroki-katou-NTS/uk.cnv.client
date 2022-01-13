/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.scherec.totaltimes;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.memento.TotalConditionGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.memento.TotalConditionSetMemento;

/**
 * 回数集計条件
 */
@Getter
@Setter
public class TotalCondition {

	/** 下限設定区分 */
	private UseAtr lowerLimitSettingAtr;

	/** 上限設定区分 */
	private UseAtr upperLimitSettingAtr;

	/** 閾値下限 */
	private Optional<ConditionThresholdLimit> thresoldLowerLimit;

	/** 閾値上限 */
	private Optional<ConditionThresholdLimit> thresoldUpperLimit;
	
	/** 勤怠項目ID */
	private Optional<Integer> atdItemId;
	
	public TotalCondition(TotalConditionGetMemento memento) {
		this.upperLimitSettingAtr = memento.getUpperLimitSettingAtr();
		this.lowerLimitSettingAtr = memento.getLowerLimitSettingAtr();
		this.thresoldUpperLimit = memento.getThresoldUpperLimit();
		this.thresoldLowerLimit = memento.getThresoldLowerLimit();
		this.atdItemId = memento.getAttendanceItemId();


		if (UseAtr.Use.equals(upperLimitSettingAtr) && UseAtr.Use.equals(lowerLimitSettingAtr)
				&& thresoldUpperLimit.get().lessThanOrEqualTo(thresoldLowerLimit.get())) {
			throw new BusinessException("Msg_210");
		}
		
		if ((UseAtr.Use.equals(upperLimitSettingAtr) || UseAtr.Use.equals(lowerLimitSettingAtr)) 
				&& !this.atdItemId.isPresent()) {
			throw new BusinessException("Msg_362");
		}
	}

	public void saveToMemento(TotalConditionSetMemento memento) {
		memento.setLowerLimitSettingAtr(this.lowerLimitSettingAtr);
		memento.setThresoldLowerLimit(this.thresoldLowerLimit);
		memento.setThresoldUpperLimit(this.thresoldUpperLimit);
		memento.setUpperLimitSettingAtr(this.upperLimitSettingAtr);
		memento.setAttendanceItemId(this.atdItemId);
	}
	
	/** ○勤務時間の判断 */
	public boolean checkWorkTime(RequireM1 require, IntegrationOfDaily dailyWork) {
		
		if (this.lowerLimitSettingAtr == UseAtr.NotUse
				&& this.upperLimitSettingAtr == UseAtr.NotUse) {
			return true;
		}
		
		/** ○勤怠項目IDから時間を取得 */
		val time = getTime(require, dailyWork);
		
		/** ○下限チェック */
		if (this.lowerLimitSettingAtr == UseAtr.Use) {
			if (this.thresoldLowerLimit.get().v() > time) {
				return false;
			}
		}

		/** ○上限チェック */
		if (this.upperLimitSettingAtr == UseAtr.Use) {
			if (this.thresoldUpperLimit.get().v() < time) {
				return false;
			}
		}
		
		return true;
	}

	private Integer getTime(RequireM1 require, IntegrationOfDaily dailyWork) {
		val converter = require.createDailyConverter();
		converter.setData(dailyWork);
		if (!this.atdItemId.isPresent()) return 0;
		Optional<ItemValue> timeOpt = converter.convert(this.atdItemId.get());
		if (!timeOpt.isPresent()) return 0;
		ItemValue time = timeOpt.get();
		return time.getValueType().isInteger() ? 
				time.intOrDefault() : time.doubleOrDefault().intValue();
	}
	
	public static interface RequireM1 {
		
		DailyRecordToAttendanceItemConverter createDailyConverter();
	}
}
