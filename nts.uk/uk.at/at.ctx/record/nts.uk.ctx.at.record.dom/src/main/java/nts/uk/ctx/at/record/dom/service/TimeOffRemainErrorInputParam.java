package nts.uk.ctx.at.record.dom.service;

//import java.sql.Date;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimeOffRemainErrorInputParam {
	/**
	 * 会社ID
	 */
	private String cid;
	/**
	 * 社員ID
	 */
	private String sid;
	/**
	 * 集計期間
	 */
	private DatePeriod aggDate;
	/**
	 * 対象期間
	 */
	private DatePeriod objDate;
	/**
	 * 時間代休利用: True:利用, False:　未使用 
	 */
	private boolean useDayoff;
	/**
	 * 日別実績
	 */
	private List<IntegrationOfDaily> lstDomainDaily;
	/**
	 * 月別実績（Work）
	 */
	private Optional<AttendanceTimeOfMonthly> optMonthlyData;
	
}
