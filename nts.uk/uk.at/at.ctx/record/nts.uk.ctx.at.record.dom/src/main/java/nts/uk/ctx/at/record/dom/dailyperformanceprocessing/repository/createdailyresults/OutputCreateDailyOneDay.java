package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;

/**
 * 
 * @author tutk
 *
 *         日別実績の作成結果
 */
@Getter
@Setter
@NoArgsConstructor
public class OutputCreateDailyOneDay {

	//エラーメッセージ
	private List<ErrorMessageInfo> listErrorMessageInfo = new ArrayList<>();

	//日別勤怠(Work)
	private IntegrationOfDaily integrationOfDaily;

	//打刻
	private List<Stamp> listStamp = new ArrayList<>();

	// ・日別勤怠の何が変更されたか
	private ChangeDailyAttendance changeDailyAttendance;

	public OutputCreateDailyOneDay(List<ErrorMessageInfo> listErrorMessageInfo, IntegrationOfDaily integrationOfDaily,
			List<Stamp> listStamp, ChangeDailyAttendance changeDailyAttendance) {
		super();
		this.listErrorMessageInfo = listErrorMessageInfo;
		this.integrationOfDaily = integrationOfDaily;
		this.listStamp = listStamp;
		this.changeDailyAttendance = changeDailyAttendance;
	}

	public OutputCreateDailyOneDay(List<ErrorMessageInfo> listErrorMessageInfo, IntegrationOfDaily integrationOfDaily,
			List<Stamp> listStamp) {
		super();
		this.listErrorMessageInfo = listErrorMessageInfo;
		this.integrationOfDaily = integrationOfDaily;
		this.listStamp = listStamp;
		this.changeDailyAttendance = ChangeDailyAttendance.createDefault(ScheduleRecordClassifi.RECORD);
	}

}
