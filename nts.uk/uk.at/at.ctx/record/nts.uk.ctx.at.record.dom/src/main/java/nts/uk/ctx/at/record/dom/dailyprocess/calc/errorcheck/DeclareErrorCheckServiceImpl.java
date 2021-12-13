package nts.uk.ctx.at.record.dom.dailyprocess.calc.errorcheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.SystemFixedErrorAlarm;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareAttdLeave;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareSetRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareTimeFrameError;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 実装：申告エラーチェックサービス
 * @author shuichi_ishida
 */
@Stateless
public class DeclareErrorCheckServiceImpl implements DeclareErrorCheckService {

	/** 申告設定 */
	@Inject
	private DeclareSetRepository declareSetRepo;
	/** 勤務種類 */
	@Inject
	private WorkTypeRepository workTypeRepo;
	/** 就業時間帯の設定 */
	@Inject
	private WorkTimeSettingRepository workTimeSetRepo;
	/** 所定時間設定 */
	@Inject
	private PredetemineTimeSettingRepository predTimeSetRepo;
	
	/** 申告エラーチェック */
	@Override
	public List<EmployeeDailyPerError> errorCheck(
			IntegrationOfDaily integrationOfDaily,
			SystemFixedErrorAlarm fixedErrorAlarmCode) {
		
		List<EmployeeDailyPerError> result = new ArrayList<>();
//		AttendanceItemDictionaryForCalc attdIdDic = AttendanceItemDictionaryForCalc.setDictionaryValue();
		String companyId = AppContexts.user().companyId();
		String employeeId = integrationOfDaily.getEmployeeId();
		GeneralDate date = integrationOfDaily.getYmd();
		
		// 申告設定を取得する
		Optional<DeclareSet> declareSetOpt = this.declareSetRepo.find(companyId);
		if (!declareSetOpt.isPresent()) return result;
		DeclareSet declareSet = declareSetOpt.get();
		// 勤務種類を取得する
		String workTypeCode = integrationOfDaily.getWorkInformation().getRecordInfo().getWorkTypeCode().v();
		Optional<WorkType> workTypeOpt = this.workTypeRepo.findByPK(companyId, workTypeCode);
		if (!workTypeOpt.isPresent()) return result;
		WorkType workType = workTypeOpt.get();
		// 就業時間帯の設定を取得する
		String workTimeCode = integrationOfDaily.getWorkInformation().getRecordInfo().getWorkTimeCode().v();
		Optional<WorkTimeSetting> workTimeSetOpt = this.workTimeSetRepo.findByCode(companyId, workTimeCode);
		if (!workTimeSetOpt.isPresent()) return result;
		WorkTimeSetting workTimeSet = workTimeSetOpt.get();
		// 所定時間設定を取得する
		Optional<PredetemineTimeSetting> predTimeSetOpt = this.predTimeSetRepo.findByWorkTimeCode(companyId, workTimeCode);
		// 日別実績の出退勤を確認する
		if (!integrationOfDaily.getAttendanceLeave().isPresent()) return result;
		TimeLeavingOfDailyAttd attendanceLeave = integrationOfDaily.getAttendanceLeave().get();
		// 申告出退勤を作成する
		DeclareAttdLeave declareAttdLeave = DeclareAttdLeave.create(
				workTimeSet, predTimeSetOpt, attendanceLeave, declareSet);
		
		// 申告設定残業枠エラーチェック
		declareSet.getOvertimeFrame().checkErrorOvertimeFrame();
		// 申告設定休出枠エラーチェック
		declareSet.getHolidayWorkFrame().checkErrorHolidayWorkFrame();
		// 申告設定深夜枠エラーチェック
		declareSet.checkErrorMidnightFrame();
		// 申告時間枠エラーチェック
		List<DeclareTimeFrameError> frameErrors = declareSet.checkErrorFrame(
				workType.isHolidayWork(), declareAttdLeave);
		if (frameErrors.size() > 0){
			result.add(new EmployeeDailyPerError(
					AppContexts.user().companyCode(), employeeId, date,
					new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), new ArrayList<>()));
		}
		// 【参考】　勤怠項目IDを設定する場合
//		boolean outOvertime = false;
//		boolean outOvertimeMn = false;
//		for (DeclareTimeFrameError frameError : frameErrors){
//			// 社員の日別実績のエラーを作成する
//			switch(frameError){
//			case EARLY_OT:
//			case OVERTIME:
//				if (outOvertime) break;
//				outOvertime = true;
//				if (integrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()){
//					AttendanceTimeOfDailyAttendance attdTime =
//							integrationOfDaily.getAttendanceTimeOfDailyPerformance().get();
//					TotalWorkingTime totalWorkTime = attdTime.getActualWorkingTimeOfDaily().getTotalWorkingTime();
//					ExcessOfStatutoryTimeOfDaily notStatTime = totalWorkTime.getExcessOfStatutoryTimeOfDaily();
//					if (notStatTime.getOverTimeWork().isPresent()){
//						OverTimeOfDaily overTime = notStatTime.getOverTimeWork().get();
//						for (OverTimeFrameTime frameTime : overTime.getOverTimeWorkFrameTime()){
//							attdIdDic.findId("残業時間" + frameTime.getOverWorkFrameNo().v())
//								.ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//									AppContexts.user().companyCode(), employeeId, date,
//									new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//							attdIdDic.findId("振替残業時間" + frameTime.getOverWorkFrameNo().v())
//								.ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//									AppContexts.user().companyCode(), employeeId, date,
//									new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//						}
//					}
//				}
//				break;
//				
//			case EARLY_OT_MN:
//			case OVERTIME_MN:
//				if (outOvertimeMn) break;
//				outOvertimeMn = true;
//				attdIdDic.findId("就外残業深夜時間").ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//						AppContexts.user().companyCode(), employeeId, date,
//						new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//				break;
//				
//			case HOLIDAYWORK:
//				if (integrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()){
//					AttendanceTimeOfDailyAttendance attdTime = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get();
//					TotalWorkingTime totalWorkTime = attdTime.getActualWorkingTimeOfDaily().getTotalWorkingTime();
//					ExcessOfStatutoryTimeOfDaily notStatTime = totalWorkTime.getExcessOfStatutoryTimeOfDaily();
//					if (notStatTime.getWorkHolidayTime().isPresent()){
//						HolidayWorkTimeOfDaily holidayWork = notStatTime.getWorkHolidayTime().get();
//						for (HolidayWorkFrameTime frameTime : holidayWork.getHolidayWorkFrameTime()){
//							attdIdDic.findId("休出時間" + frameTime.getHolidayFrameNo().v())
//								.ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//									AppContexts.user().companyCode(), employeeId, date,
//									new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//							attdIdDic.findId("振替時間" + frameTime.getHolidayFrameNo().v())
//								.ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//									AppContexts.user().companyCode(), employeeId, date,
//									new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//						}
//					}
//				}
//				break;
//				
//			case HOLIDAYWORK_MN:
//				attdIdDic.findId("法内休出外深夜").ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//						AppContexts.user().companyCode(), employeeId, date,
//						new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//				attdIdDic.findId("法外休出外深夜").ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//						AppContexts.user().companyCode(), employeeId, date,
//						new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//				attdIdDic.findId("就外法外祝日深夜").ifPresent(itemId -> result.add(new EmployeeDailyPerError(
//						AppContexts.user().companyCode(), employeeId, date,
//						new ErrorAlarmWorkRecordCode(fixedErrorAlarmCode.value), itemId)));
//				break;
//			}
//		}
		return result;
	}
}
