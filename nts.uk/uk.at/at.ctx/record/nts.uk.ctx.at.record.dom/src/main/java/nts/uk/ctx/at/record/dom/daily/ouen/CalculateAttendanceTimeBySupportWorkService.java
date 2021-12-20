package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * DS: 	応援作業別勤怠時間を計算する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.応援作業別勤怠時間を計算する
 * @author ThanhPV
 */

public class CalculateAttendanceTimeBySupportWorkService {
	
//■Public
	/**
	 * 	[prv-1] 編集状態を作成する
	 * @input require
	 * @input empId 社員ID	
	 * @input ymd 年月日
	 * @input ouenWorkTimeSheetOfDailyAttendance 作業時間帯
	 * @output 計算結果 	Optional<日別勤怠(Work)>
	 */
	public static Optional<IntegrationOfDaily> calculate(Require require, String empId, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailyAttendance) {
		//	$日別勤怠 = require.日別勤怠(Work)を取得する(社員ID,年月日)	
		Optional<IntegrationOfDaily> integrationOfDaily = require.get(empId, new DatePeriod(ymd, ymd));
		//	if $日別勤怠.isPresent
		if(integrationOfDaily.isPresent()){
			// $新日別勤怠 = [prv-1] 退勤時刻をセットする($日別勤怠)
			IntegrationOfDaily integrationOfDailyNew = setTheLeaveTime(integrationOfDaily.get());
			//$新日別勤怠.応援時刻 = 作業時間帯
			integrationOfDailyNew.setOuenTimeSheet(ouenWorkTimeSheetOfDailyAttendance);
			//$計算結果 = require.計算する($新日別勤怠, 実行区分.通常実行)
			IntegrationOfDaily calculationResult = require.calculationIntegrationOfDaily(integrationOfDailyNew, ExecutionType.NORMAL_EXECUTION);
			//	return $計算結果	
			return Optional.of(calculationResult);
		}
		return Optional.empty();
	}
	
//■Private
	
	/**
	 * @name	[prv-1] 退勤時刻をセットする
	 * @input integrationOfDaily  日別勤怠(Work)
	 */
	private static IntegrationOfDaily setTheLeaveTime(IntegrationOfDaily integrationOfDaily) {
		//	if 日別勤怠(Work).年月日 == 年月日#今日()	
		if(integrationOfDaily.getYmd().equals(GeneralDate.today())) {
			//	$出退勤 = 日別勤怠(Work).出退勤.出退勤:															
				// filter $.勤務NO = 1
			Optional<TimeLeavingWork> timeLeavingWorks = integrationOfDaily.getAttendanceLeave().map(c->{
				return c.getTimeLeavingWorks().stream().filter(e -> e.getWorkNo().v() == 1).findAny();
			}).orElse(Optional.empty());
			//if $出退勤.出勤.isPresent　AND　$出退勤.退勤.isEmpty		
			if(timeLeavingWorks.isPresent() && timeLeavingWorks.get().getLeaveTime().isPresent() && !timeLeavingWorks.get().getLeaveStamp().isPresent()) {
				//$出退勤.退勤.打刻.時刻.時刻変更理由.時刻変更手段	時刻変更手段.手修正(本人)		
				//$出退勤.退勤.打刻.時刻.時刻変更理由.打刻方法	Optional.empty
				//$出退勤.退勤.打刻.時刻.時刻	日時#今()
				//$出退勤.退勤.打刻.場所コード	Optional.empty
				//$出退勤.退勤.実打刻	Optional.empty
				//$出退勤.退勤.時間外の申告	Optional.empty
				//$出退勤.退勤.時間休暇時間帯	Optional.empty
				//$出退勤.退勤.打刻反映回数	1
				TimeLeavingWork timeLeavingWorkNew = timeLeavingWorks.get(); 
				timeLeavingWorkNew.setLeaveStamp(Optional.of(new TimeActualStamp(
						Optional.empty(), 
						Optional.of(
							new WorkStamp(
								new WorkTimeInformation(
										new ReasonTimeChange(
												TimeChangeMeans.HAND_CORRECTION_PERSON, 
											Optional.empty()), 
											TimeWithDayAttr.hourMinute(GeneralDateTime.now().hours(), GeneralDateTime.now().minutes())), 
							Optional.empty())), 
						1, 
						Optional.empty(), 
						Optional.empty())));
				//	$新の日別勤怠(Work) = 日別勤怠(Work).出退勤.出退勤:											
					//except $.勤務NO = 1
				IntegrationOfDaily integrationOfDailyNew = integrationOfDaily;
				integrationOfDailyNew.getAttendanceLeave().ifPresent(c-> c.getTimeLeavingWorks().removeIf(e -> e.getWorkNo().v() == 1));
				//$新の日別勤怠(Work) .日別勤怠(Work).出退勤.出退勤.追加($出退勤)
				integrationOfDailyNew.getAttendanceLeave().get().getTimeLeavingWorks().add(timeLeavingWorkNew);
				//	return $新の日別勤怠(Work)
				return integrationOfDailyNew;
			}
		}
		return integrationOfDaily;
	}
	
//■Require
	public static interface Require {
		//[R-2] 日別勤怠(Work)を取得する		
		//日別勤怠(Work)を取得する(社員ID,期間)
		Optional<IntegrationOfDaily> get(String employeeId, DatePeriod date);
		//	[R-1] 日別勤怠(Work)を取得する		
		//アルゴリズム.日別実績の修正からの計算(日別実績(Work),実行種別)	
		IntegrationOfDaily calculationIntegrationOfDaily(IntegrationOfDaily integrationOfDaily, ExecutionType executionType);
	}

}
