/**
 * 9:17:22 AM Jun 6, 2017
 */
package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.HTCalSettingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.OTCalSettingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.TimeItemTypeAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.WTCalSettingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;

/**
 * @author hungnm
 *
 */
@AllArgsConstructor
@Getter
public class BPTimeItemSetting extends AggregateRoot {

	private String companyId;

	private int timeItemNo;

	private HTCalSettingAtr holidayCalSettingAtr;

	private OTCalSettingAtr overtimeCalSettingAtr;

	private WTCalSettingAtr worktimeCalSettingAtr;
	
	private TimeItemTypeAtr timeItemTypeAtr;

	public static BPTimeItemSetting createFromJavaType(String companyId, int timeItemNo, int holidayCalSettingAtr,
			int overtimeCalSettingAtr, int worktimeCalSettingAtr, int timeItemTypeAtr) {
		return new BPTimeItemSetting(companyId, 
				timeItemNo,
				EnumAdaptor.valueOf(holidayCalSettingAtr, HTCalSettingAtr.class),
				EnumAdaptor.valueOf(overtimeCalSettingAtr, OTCalSettingAtr.class),
				EnumAdaptor.valueOf(worktimeCalSettingAtr, WTCalSettingAtr.class),
				EnumAdaptor.valueOf(timeItemTypeAtr, TimeItemTypeAtr.class));
	}

	/**
	 * 計算するか
	 * @param actualAtr 実働時間帯区分
	 * @param calcAtrOfDaily 日別勤怠の計算区分
	 * @return true:計算する、false:計算しない
	 */
	public boolean isCalc(ActualWorkTimeSheetAtr actualAtr, CalAttrOfDailyAttd calcAtrOfDaily) {
		if(!calcAtrOfDaily.getRasingSalarySetting().isCalc(this.timeItemTypeAtr)) {
			return false;
		}
		if(actualAtr.isOverTimeWork()) {
			return this.overtimeCalSettingAtr.isCalc(calcAtrOfDaily.getOvertimeSetting().getNormalOtTime()); //普通残業
		}
		if(actualAtr.isEarlyWork()) {
			return this.overtimeCalSettingAtr.isCalc(calcAtrOfDaily.getOvertimeSetting().getEarlyOtTime()); //早出残業
		}
		if(actualAtr.isStatutoryOverTimeWork()) {
			return this.overtimeCalSettingAtr.isCalc(calcAtrOfDaily.getOvertimeSetting().getLegalOtTime()); //法定内残業
		}
		if(actualAtr.isHolidayWork()) {
			return this.holidayCalSettingAtr.isCalc(calcAtrOfDaily.getHolidayTimeSetting().getRestTime()); //休出
		}
		return worktimeCalSettingAtr.isCalculation(); // 就業時間内
	}
}
