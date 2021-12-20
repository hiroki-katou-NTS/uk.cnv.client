package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.pastmonth;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;

/** 過去月集計結果 */
@Getter
@Builder
public class AggregatePastMonthResult {

	/** 月別実績の勤怠時間: 月別実績の勤怠時間 */
	private AttendanceTimeOfMonthly monthlyAttdTime;
	
	/** 月別実績の任意項目: 月別実績の任意項目 */
	private List<AnyItemOfMonthly> monthlyAnyItem;
	
	/** 週別実績の勤怠時間: 週別実績の勤怠時間 */
	private List<AttendanceTimeOfWeekly> weeklyAttdTime;
	
	/** 管理期間の36協定時間: 管理期間の36協定時間 */
	private List<AgreementTimeOfManagePeriod> agreementTime;
}
