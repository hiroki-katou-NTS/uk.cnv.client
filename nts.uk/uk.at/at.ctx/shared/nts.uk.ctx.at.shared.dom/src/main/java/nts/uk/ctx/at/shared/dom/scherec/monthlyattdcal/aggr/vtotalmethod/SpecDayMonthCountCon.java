package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.enums.EnumAdaptor;

/**
 * 月別実績集計の特定日カウント条件
 * @author HoangNDH
 *
 */
@Data
@AllArgsConstructor
public class SpecDayMonthCountCon {
	// 勤務種類
	WorkTypeClassification workType;
	// 特定日としてカウントする
	boolean useCountSpecDay;
	
	public SpecDayMonthCountCon() {
		workType = WorkTypeClassification.Attendance;
		useCountSpecDay = false;
	}
	
	public static SpecDayMonthCountCon createFromJavaType(int workType, boolean useCountSpecDay) {
		return new SpecDayMonthCountCon(EnumAdaptor.valueOf(workType, WorkTypeClassification.class), useCountSpecDay);
	}
}
