package nts.uk.ctx.at.aggregation.dom.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import lombok.SneakyThrows;
import lombok.val;
import mockit.Injectable;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.interval.IntervalTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.RaiseSalaryTimeOfDailyPerfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.temporarytime.TemporaryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.StayingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;

/**
 * [予実集計]日別勤怠(Work)に関するHelper
 * ※集計処理に特化しています
 * @author kumiko_otake
 */
public class IntegrationOfDailyHelperInAggregation {

	@Injectable private static WorkInfoOfDailyAttendance workInfo = new WorkInfoOfDailyAttendance();
	@Injectable private static BreakTimeOfDailyAttd breakTime = new BreakTimeOfDailyAttd();
	@Injectable private static AffiliationInforOfDailyAttd affInfo = new AffiliationInforOfDailyAttd();


	/**
	 * フィールドに値を設定する
	 * @param instance
	 * @param name
	 * @param value
	 */
	@SneakyThrows
	protected static <T> void setValue(Object instance, String name, T value) {
		Field field = instance.getClass().getDeclaredField( name );
		field.setAccessible( true );
		field.set( instance, value );
	}

	/**
	 * システム日付の年月から日付を作成する
	 * @param day 日付
	 * @return
	 */
	public static GeneralDate createDate(int day) {

		return GeneralDate.ymd(
						GeneralDate.today().year()
					,	GeneralDate.today().month()
					,	day
				);

	}

	/**
	 * 社員IDを作成する
	 * @param seed
	 * @return
	 */
	public static EmployeeId createEmpId(int seed) {
		return new EmployeeId( String.format("EmpId%d", seed) );
	}


	/**
	 * 日別勤怠(Work)を作成する
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param workInfo 勤務情報
	 * @param affInfo 所属情報
	 * @param breakTime 休憩時間帯
	 * @return
	 */
	private static IntegrationOfDaily create(String employeeId, GeneralDate ymd
			, WorkInfoOfDailyAttendance workInfo
			, AffiliationInforOfDailyAttd affInfo
			, BreakTimeOfDailyAttd breakTime
	) {

		return new IntegrationOfDaily( employeeId, ymd
					, workInfo			// 勤務情報
					, CalAttrOfDailyAttd.defaultData()
					, affInfo			// 所属情報
					, Optional.empty()	// PCログオン情報
					, new ArrayList<>()	// エラーアラーム
					, Optional.empty()	// 外出時間帯
					, breakTime			// 休憩時間帯
					, Optional.empty()	// 勤怠時間
					, Optional.empty()	// 出退勤
					, Optional.empty()	// 短時間勤務時間帯
					, Optional.empty()	// 特定日区分
					, Optional.empty()	// 入退門
					, Optional.empty()	// 任意項目
					, new ArrayList<>()	// 編集状態
					, Optional.empty()	// 臨時出退勤
					, new ArrayList<>()	// 備考
					, new ArrayList<>()	// 応援作業時間
					, new ArrayList<>()	// 応援作業時間帯
					, Optional.empty()	// スナップショット
				);

	}
	

	/**
	 * 日別勤怠の勤務情報、日別勤怠の出退勤、日別勤怠の勤怠時間を使って日別勤怠（work)を作成する
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param workInfo 日別勤怠の勤務情報
	 * @param attendanceLeave 日別勤怠の出退勤
	 * @param attendanceTime 日別勤怠の勤怠時間
	 * @return 日別勤怠(Work)
	 */
	public static IntegrationOfDaily create(
			String employeeId, 
			GeneralDate ymd, 
			WorkInfoOfDailyAttendance workInfo,
			Optional<TimeLeavingOfDailyAttd> attendanceLeave,
			Optional<AttendanceTimeOfDailyAttendance> attendanceTime
			) {
		return new IntegrationOfDaily( employeeId, ymd
				, workInfo			// 勤務情報
				, CalAttrOfDailyAttd.defaultData()
				, AffInfoHelper.createDummuy()	// 所属情報
				, Optional.empty()	// PCログオン情報
				, new ArrayList<>()	// エラーアラーム
				, Optional.empty()	// 外出時間帯
				, breakTime			// 休憩時間帯
				, attendanceTime	// 勤怠時間
				, attendanceLeave	// 出退勤
				, Optional.empty()	// 短時間勤務時間帯
				, Optional.empty()	// 特定日区分
				, Optional.empty()	// 入退門
				, Optional.empty()	// 任意項目
				, new ArrayList<>()	// 編集状態
				, Optional.empty()	// 臨時出退勤
				, new ArrayList<>()	// 備考
				, new ArrayList<>()	
				, new ArrayList<>()	
				, Optional.empty()	// スナップショット
			);
	}

	/**
	 * 日別勤怠(Work)を作成する
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @return 日別勤怠(Work)
	 */
	public static IntegrationOfDaily createDummy(String employeeId, GeneralDate ymd) {
		return IntegrationOfDailyHelperInAggregation.create( employeeId, ymd, workInfo, AffInfoHelper.createDummuy(), breakTime );
	}

	/**
	 * 日別勤怠(Work)を作成する
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param affInfo 日別勤怠の所属情報
	 * @return 日別勤怠(Work)
	 */
	public static IntegrationOfDaily createWithAffInfo(String employeeId, GeneralDate ymd, AffiliationInforOfDailyAttd affInfo) {
		return IntegrationOfDailyHelperInAggregation.create( employeeId, ymd, workInfo, affInfo, breakTime );
	}

	/**
	 * 日別勤怠(Work)を作成する
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param workInfo 日別勤怠の勤務情報
	 * @return 日別勤怠(Work)
	 */
	public static IntegrationOfDaily createWithWorkInfo(String employeeId, GeneralDate ymd, WorkInfoOfDailyAttendance workInfo) {
		return IntegrationOfDailyHelperInAggregation.create(employeeId, ymd, workInfo, affInfo, breakTime);
	}

	/** 日別勤怠の所属情報に関するHelper **/
	public static class AffInfoHelper {

		/**
		 * 日別勤怠の所属情報を作成する
		 * @return 日別勤怠の所属情報(dummy)
		 */
		public static AffiliationInforOfDailyAttd createDummuy() {
			return AffInfoHelper.createWithRequired("EmpCd", "JttId", "WkpId", "ClsCd");
		}


		/**
		 * 日別勤怠の所属情報を作成する
		 * @param employmentCode 雇用コード
		 * @param jobTitleId 職位ID
		 * @param workPlaceId 職場ID
		 * @param classCode 分類コード
		 * @return 日別勤怠の所属情報
		 */
		public static AffiliationInforOfDailyAttd createWithRequired(String employmentCode, String jobTitleId, String workPlaceId, String classCode) {
			return new AffiliationInforOfDailyAttd(
					new EmploymentCode(employmentCode)	// 雇用コード
				,	jobTitleId							// 職位ID
				,	workPlaceId							// 職場ID
				,	new ClassificationCode(classCode)	// 分類コード
				,	Optional.empty()					// 勤務種別コード
				,	Optional.empty()					// 加給コード
			);
		}

		/**
		 * 日別勤怠の所属情報を作成する
		 * @param employmentCode 雇用コード
		 * @param jobTitleId 職位ID
		 * @param workPlaceId 職場ID
		 * @param classCode 分類コード
		 * @param businessTypeCode 勤務種別コード
		 * @return 日別勤怠の所属情報
		 */
		public static AffiliationInforOfDailyAttd createWithoutBonusPay(String employmentCode, String jobTitleId, String workPlaceId, String classCode, String businessTypeCode) {
			val affInfo = AffInfoHelper.createWithRequired(employmentCode, jobTitleId, workPlaceId, classCode);
			affInfo.setBusinessTypeCode(Optional.of(new BusinessTypeCode(businessTypeCode)));
			return affInfo;
		}

	}

	/** 日別勤怠の勤務情報に関するHelper **/
	public static class WorkInfoHelper {

		/**
		 * 日別勤怠の勤務情報を作成する
		 * @return 日別勤怠の勤務情報(dummy)
		 */
		public static WorkInfoOfDailyAttendance createDummuy() {
			return WorkInfoHelper.createWithCode("WorkTypeCd", Optional.of("WorkTimeCd"));
		}

		/**
		 * 日別勤怠の勤務情報を作成する
		 * @param workTypeCode 勤務種類コード
		 * @param workTimeCode 就業時間帯コード
		 * @return 日別勤怠の勤務情報
		 */
		public static WorkInfoOfDailyAttendance createWithCode(String workTypeCode, Optional<String> workTimeCode) {
			return WorkInfoHelper.createWithRequired(
							workTypeCode, workTimeCode
						,	workTimeCode.isPresent() ? DayOfWeek.MONDAY : DayOfWeek.SUNDAY
						,	CalculationState.Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use
					);
		}

		/**
		 * 日別勤怠の勤務情報を作成する
		 * @return 日別勤怠の勤務情報
		 */
		public static WorkInfoOfDailyAttendance createWithRequired(String workTypeCode, Optional<String> workTimeCode, DayOfWeek dayOfWeek, CalculationState calcStatus, NotUseAttribute isGoStraight, NotUseAttribute isBackStraight) {
			return new WorkInfoOfDailyAttendance(
							WorkInfoHelper.createWorkInformation(workTypeCode, workTimeCode)
						,	calcStatus, isGoStraight, isBackStraight, dayOfWeek
						,	Collections.emptyList(), Optional.empty()
					);
		}


		/**
		 * 勤務情報を作成する
		 * @param workTypeCode 勤務種類コード
		 * @param workTimeCode 就業時間帯コード
		 * @return 勤務情報
		 */
		private static WorkInformation createWorkInformation(String workTypeCode, Optional<String> workTimeCode) {
			return new WorkInformation( workTypeCode, workTimeCode.orElse(null) );
		}

	}

	/** 日別勤怠の勤怠時間に関するHelper **/
	public static class AtdTimeHelper {


		/* 共通 */
		@Injectable private static AttendanceTime atdTime;
		/* 日別勤怠の勤怠時間 */
		@Injectable private static WorkScheduleTimeOfDaily schedule;
		@Injectable private static StayingTimeOfDaily stay;
		@Injectable private static AttendanceTimeOfExistMinus budget;
		@Injectable private static AttendanceTimeOfExistMinus unEmploy;
		/* 日別勤怠の勤務実績時間 */
		@Injectable private static ConstraintTime constTime;
		@Injectable private static DivergenceTimeOfDaily divTime;
		/* 日別勤怠の総労働時間 */
		@Injectable private static ExcessOfStatutoryTimeOfDaily excess;
		@Injectable private static BreakTimeOfDaily breakTime;
		@Injectable private static RaiseSalaryTimeOfDailyPerfor raise;
		@Injectable private static WorkTimes workTimes;
		@Injectable private static TemporaryTimeOfDaily temporaly;
		@Injectable private static ShortWorkTimeOfDaily shorttime;
		@Injectable private static HolidayOfDaily holiday;
		@Injectable private static IntervalTimeOfDaily interval;
		/* 日別勤怠の所定内時間 */
		@Injectable private static WithinStatutoryMidNightTime withinMidnite;



		/**
		 * 日別勤怠の勤怠時間を作成する
		 * @param withinTime 就業時間
		 * @param withinAmount 就業時間金額
		 * @param premiumTime 割増労働時間合計
		 * @param premiumAmount 割増金額合計
		 * @return 日別勤怠の勤怠時間
		 */
		public static AttendanceTimeOfDailyAttendance createWithTimeAndAmount(
				int withinTime, int withinAmount
			,	int premiumTime, int premiumAmount
		) {
			return new AttendanceTimeOfDailyAttendance(
							schedule
						,	new ActualWorkingTimeOfDaily(
									atdTime, constTime, atdTime
								,	new TotalWorkingTime(
												atdTime, atdTime, atdTime, AtdTimeHelper.createWithinOfDaily(withinTime, withinAmount), excess
											,	Collections.emptyList(), Collections.emptyList(), breakTime, Collections.emptyList()
											,	raise, workTimes, temporaly, shorttime, holiday, interval
										)
								, divTime, AtdTimeHelper.createPremiumOfDaily(premiumTime, premiumAmount) )
						,	stay, budget, unEmploy
					);
		}

		/**
		 * 日別勤怠の勤怠時間を作成する
		 * @param within 就業時間
		 * @param premium 割増労働時間合計
		 * @return 日別勤怠の勤怠時間
		 */
		public static AttendanceTimeOfDailyAttendance createWithTime(int within, int premium) {
			return AtdTimeHelper.createWithTimeAndAmount( within, 0, premium, 0 );
		}

		/**
		 * 日別勤怠の勤怠時間を作成する
		 * @param within 就業時間
		 * @param premium 割増労働時間合計
		 * @return 日別勤怠の勤怠時間
		 */
		public static AttendanceTimeOfDailyAttendance createWithAmount(int within, int premium) {
			return AtdTimeHelper.createWithTimeAndAmount( 480, within, 0, premium );
		}

		/**
		 * 日別勤怠の所定内時間を作成する
		 * @param time 就業時間
		 * @param amount 就業時間金額
		 * @return 日別勤怠の所定内時間
		 */
		protected static WithinStatutoryTimeOfDaily createWithinOfDaily(int time, int amount) {

			val within = new WithinStatutoryTimeOfDaily(atdTime, atdTime, atdTime, withinMidnite);

			// 就業時間
			IntegrationOfDailyHelperInAggregation.setValue( within , "workTime", new AttendanceTime( time ) );
			// 就業時間金額
			IntegrationOfDailyHelperInAggregation.setValue( within , "withinWorkTimeAmount", new AttendanceAmountDaily( amount ) );

			return within;

		}


		/**
		 * 日別勤怠の割増時間を作成する
		 * @param time 割増労働時間合計
		 * @param amount 割増金額合計
		 * @return 日別勤怠の割増時間
		 */
		protected static PremiumTimeOfDailyPerformance createPremiumOfDaily(int time, int amount) {

			val premium = new PremiumTimeOfDailyPerformance(Collections.emptyList());

			// 割増労働時間合計
			IntegrationOfDailyHelperInAggregation.setValue( premium , "totalWorkingTime", new AttendanceTime( time ) );
			// 割増金額合計
			IntegrationOfDailyHelperInAggregation.setValue( premium , "totalAmount", new AttendanceAmountDaily( amount ) );


			return premium;

		}
	}

}
