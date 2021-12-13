package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.daily.ouen.TimeZone;
import nts.uk.ctx.at.record.dom.daily.ouen.WorkDetailsParam;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.日別実績データを取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetDailyPerformanceData {

	@Inject
	private IntegrationOfDailyGetter getter;

	/**
	 * 
	 * @param sId    社員ID
	 * @param period
	 * @return
	 */
	public List<WorkRecordDetail> get(String sId, DatePeriod period) {
		List<WorkRecordDetail> lstWorkRecordDetail = new ArrayList<>();

		// 1: get(社員ID,期間)
		List<IntegrationOfDaily> lstIntegrationOfDaily = getter.getIntegrationOfDaily(sId, period);

		for (IntegrationOfDaily interDaily : lstIntegrationOfDaily) {

				// 作業実績詳細
				WorkRecordDetail detail = new WorkRecordDetail();

				// 年月日
				detail.setDate(interDaily.getYmd());
				// 社員ID
				detail.setSID(interDaily.getEmployeeId());

				// 作業詳細リスト
				List<WorkDetailsParam> lstworkDetailsParam = new ArrayList<>();

				for (OuenWorkTimeSheetOfDailyAttendance ouenSheet : interDaily.getOuenTimeSheet()) {
					
					Optional<OuenWorkTimeOfDailyAttendance> ouentime = interDaily.getOuenTime().stream()
							.filter(x -> x.getWorkNo() == ouenSheet.getWorkNo()).findFirst();
					
					WorkDetailsParam workDetailsParam = new WorkDetailsParam(
							new SupportFrameNo(ouenSheet.getWorkNo()),
							new TimeZone(ouenSheet.getTimeSheet().getStart().get(),
									ouenSheet.getTimeSheet().getEnd().get(),
									Optional.ofNullable(
											ouentime.map(x -> x.getWorkTime().getTotalTime()).orElse(null))),
							ouenSheet.getWorkContent().getWork(), ouenSheet.getWorkContent().getWorkRemarks(),
							ouenSheet.getWorkContent().getWorkplace().getWorkLocationCD());
					lstworkDetailsParam.add(workDetailsParam);
					
				}

				// 実績内容
				ActualContent actualContent = new ActualContent();

				// 休憩リスト
				actualContent.setBreakTimeSheets(interDaily.getBreakTime().getBreakTimeSheets());
				// 休憩時間
				actualContent.setBreakHours(Optional.ofNullable(interDaily.getAttendanceTimeOfDailyPerformance().map(
						x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily().getToRecordTotalTime().getTotalTime().getTime())
						.orElse(null)));
				// 総労働時間
				actualContent.setTotalWorkingHours(Optional.ofNullable(interDaily.getAttendanceTimeOfDailyPerformance()
						.map(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getTotalTime()).orElse(null)));

				// 実績内容
				detail.setActualContent(Optional.of(actualContent));

				// 作業詳細リスト
				detail.setLstworkDetailsParam(Optional.of(lstworkDetailsParam));

				lstWorkRecordDetail.add(detail);
				
				
			Optional<TimeLeavingWork> timeLeavingWork = interDaily.getAttendanceLeave()
					.map(x -> x.getTimeLeavingWorks()).orElse(Collections.emptyList()).stream()
					.filter(x -> x.getWorkNo().v() == 1).findFirst();
				
				if (timeLeavingWork.isPresent()) {
					// 開始時刻
					actualContent.setStart(Optional.ofNullable(timeLeavingWork.get().getAttendanceStamp()
							.map(x -> x.getStamp().map(y -> y.getTimeDay()).orElse(null)).orElse(null)));

					// 終了時刻
					actualContent.setEnd(Optional.ofNullable(timeLeavingWork.get().getLeaveStamp()
							.map(x -> x.getStamp().map(y -> y.getTimeDay()).orElse(null)).orElse(null)));
				}
		}

		return lstWorkRecordDetail;
	}

}
