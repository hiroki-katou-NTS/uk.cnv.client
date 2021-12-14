package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceItemOfMonthly;

/**
 * 36協定時間内訳
 * @author shuichi_ishida
 */
@Getter
public class AgreementTimeBreakdown {

	/** 残業時間 */
	protected AttendanceTimeMonth overTime;
	/** 振替残業時間 */
	protected AttendanceTimeMonth transferOverTime;
	/** 法定内休出時間 */
	protected AttendanceTimeMonth legalHolidayWorkTime;
	/** 法定内振替時間 */
	protected AttendanceTimeMonth legalTransferTime;
	/** 法定外休出時間 */
	protected AttendanceTimeMonth illegalHolidayWorkTime;
	/** 法定外振替時間 */
	protected AttendanceTimeMonth illegaltransferTime;
	/** 法定内フレックス超過時間 */
	protected AttendanceTimeMonth flexLegalTime;
	/** 法定外フレックス超過時間 */
	protected AttendanceTimeMonth flexIllegalTime;
	/** 所定内割増時間 */
	protected AttendanceTimeMonth withinPrescribedPremiumTime;
	/** 週割増合計時間 */
	protected AttendanceTimeMonth weeklyPremiumTime;
	/** 月間割増合計時間 */
	protected AttendanceTimeMonth monthlyPremiumTime;
	/** 臨時時間 */
	protected AttendanceTimeMonth temporaryTime;
	
	/**
	 * コンストラクタ
	 */
	public AgreementTimeBreakdown(){
		init();
	}

	private void init() {
		this.overTime = new AttendanceTimeMonth(0);
		this.transferOverTime = new AttendanceTimeMonth(0);
		this.illegalHolidayWorkTime = new AttendanceTimeMonth(0);
		this.illegaltransferTime = new AttendanceTimeMonth(0);
		this.flexLegalTime = new AttendanceTimeMonth(0);
		this.flexIllegalTime = new AttendanceTimeMonth(0);
		this.withinPrescribedPremiumTime = new AttendanceTimeMonth(0);
		this.weeklyPremiumTime = new AttendanceTimeMonth(0);
		this.monthlyPremiumTime = new AttendanceTimeMonth(0);
		this.temporaryTime = new AttendanceTimeMonth(0);
		this.legalHolidayWorkTime = new AttendanceTimeMonth(0);
		this.legalTransferTime = new AttendanceTimeMonth(0);
	}
	
	/**
	 * ファクトリー
	 * @param overTime 残業時間
	 * @param transferOverTime 振替残業時間
	 * @param holidayWorkTime 休出時間
	 * @param transferTime 振替時間
	 * @param flexLegalTime フレックス法定内時間
	 * @param flexIllegalTime フレックス法定外時間
	 * @param withinPrescribedPremiumTime 所定内割増時間
	 * @param weeklyPremiumTime 週割増時間
	 * @param monthlyPremiumTime 月割増時間
	 * @param temporaryTime 月割増時間
	 * @param legalHolidayWorkTime 月割増時間
	 * @param legalTransferTime 月割増時間
	 * @return 36協定時間内訳
	 */
	public static AgreementTimeBreakdown of(
			AttendanceTimeMonth overTime,
			AttendanceTimeMonth transferOverTime,
			AttendanceTimeMonth holidayWorkTime,
			AttendanceTimeMonth transferTime,
			AttendanceTimeMonth flexLegalTime,
			AttendanceTimeMonth flexIllegalTime,
			AttendanceTimeMonth withinPrescribedPremiumTime,
			AttendanceTimeMonth weeklyPremiumTime,
			AttendanceTimeMonth monthlyPremiumTime,
			AttendanceTimeMonth temporaryTime,
			AttendanceTimeMonth legalHolidayWorkTime,
			AttendanceTimeMonth legalTransferTime){
		
		AgreementTimeBreakdown domain = new AgreementTimeBreakdown();
		domain.overTime = overTime;
		domain.transferOverTime = transferOverTime;
		domain.illegalHolidayWorkTime = holidayWorkTime;
		domain.illegaltransferTime = transferTime;
		domain.flexLegalTime = flexLegalTime;
		domain.flexIllegalTime = flexIllegalTime;
		domain.withinPrescribedPremiumTime = withinPrescribedPremiumTime;
		domain.weeklyPremiumTime = weeklyPremiumTime;
		domain.monthlyPremiumTime = monthlyPremiumTime;
		domain.temporaryTime = temporaryTime;
		domain.legalHolidayWorkTime = legalHolidayWorkTime;
		domain.legalTransferTime = legalTransferTime;
		return domain;
	}
	
	/**
	 * 36協定時間の対象項目を取得
	 * @param aggregateAtr 集計区分
	 * @param monthlyCalculation 月別実績の月の計算
	 */
//	public void getTargetItemOfAgreement(
//			MonthlyAggregateAtr aggregateAtr,
//			MonthlyCalculation monthlyCalculation){
//		
//		// 集計結果　初期化
//		init();
//		
//		// 丸め設定取得
//		RoundingSetOfMonthly roundingSet = monthlyCalculation.getCompanySets().getRoundingSet();
//		
//		// 「時間外超過設定」を取得
//		OutsideOTSetting outsideOTSet = monthlyCalculation.getCompanySets().getOutsideOverTimeSet();
//		for (val attendanceItemId : outsideOTSet.getAllAttendanceItemIds()){
//			
//			// 対象項目の時間を取得　と　丸め処理
//			val targetItemTime = monthlyCalculation.getTimeOfAttendanceItemId(attendanceItemId, roundingSet, true);
//			
//			// 勤怠項目IDに対応する時間を加算する
//			this.addTimeByAttendanceItemId(attendanceItemId, targetItemTime);
//		}
//	}
	
	/**
	 * 36協定時間の対象項目を取得　（週用）
	 * @param aggregateAtr 集計区分
	 * @param weeklyCalculation 月別実績の月の計算
	 * @param companySets 月別集計で必要な会社別設定
	 */
//	public void getTargetItemOfAgreementForWeek(
//			MonthlyAggregateAtr aggregateAtr,
//			WeeklyCalculation weeklyCalculation,
//			MonAggrCompanySettings companySets){
//		
//		// 集計結果　初期化
//		init();
//		
//		// 丸め設定取得
//		RoundingSetOfMonthly roundingSet = companySets.getRoundingSet();
//		
//		// 「時間帯超過設定」を取得
//		val outsideOTSet = companySets.getOutsideOverTimeSet();
//		if (outsideOTSet == null) return;
//		for (val attendanceItemId : outsideOTSet.getAllAttendanceItemIds()){
//			
//			// 対象項目の時間を取得　と　丸め処理
//			val targetItemTime = weeklyCalculation.getTimeOfAttendanceItemId(attendanceItemId, roundingSet, true);
//			
//			// 勤怠項目IDに対応する時間を加算する
//			this.addTimeByAttendanceItemId(attendanceItemId, targetItemTime);
//		}
//	}
	
	/**
	 * 勤怠項目IDに対応する時間を加算する
	 * @param attendanceItemId 勤怠項目ID
	 * @param targetItemTime 対象項目の時間
	 */
	public void addTimeByAttendanceItemId(int attendanceItemId, AttendanceTimeMonth targetItemTime){
		
		// 残業時間
		if (attendanceItemId >= AttendanceItemOfMonthly.OVER_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.OVER_TIME_10.value){
			this.overTime = this.overTime.addMinutes(targetItemTime.v());
		}
		
		// 計算残業時間
		if (attendanceItemId >= AttendanceItemOfMonthly.CALC_OVER_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.CALC_OVER_TIME_10.value){
			this.overTime = this.overTime.addMinutes(targetItemTime.v());
		}
		
		// 振替残業時間
		if (attendanceItemId >= AttendanceItemOfMonthly.TRANSFER_OVER_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.TRANSFER_OVER_TIME_10.value){
			this.transferOverTime = this.transferOverTime.addMinutes(targetItemTime.v());
		}
		
		// 計算振替残業時間
		if (attendanceItemId >= AttendanceItemOfMonthly.CALC_TRANSFER_OVER_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.CALC_TRANSFER_OVER_TIME_10.value){
			this.transferOverTime = this.transferOverTime.addMinutes(targetItemTime.v());
		}
		
		// 休出時間
		if (attendanceItemId >= AttendanceItemOfMonthly.HOLIDAY_WORK_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.HOLIDAY_WORK_TIME_10.value){
			this.illegalHolidayWorkTime = this.illegalHolidayWorkTime.addMinutes(targetItemTime.v());
		}
		
		// 計算休出時間
		if (attendanceItemId >= AttendanceItemOfMonthly.CALC_HOLIDAY_WORK_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.CALC_HOLIDAY_WORK_TIME_10.value){
			this.illegalHolidayWorkTime = this.illegalHolidayWorkTime.addMinutes(targetItemTime.v());
		}
		
		// 振替時間
		if (attendanceItemId >= AttendanceItemOfMonthly.TRANSFER_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.TRANSFER_TIME_10.value){
			this.illegaltransferTime = this.illegaltransferTime.addMinutes(targetItemTime.v());
		}
		
		// 計算振替時間
		if (attendanceItemId >= AttendanceItemOfMonthly.CALC_TRANSFER_TIME_01.value &&
			attendanceItemId <= AttendanceItemOfMonthly.CALC_TRANSFER_TIME_10.value){
			this.illegaltransferTime = this.illegaltransferTime.addMinutes(targetItemTime.v());
		}
		
		// フレックス法定内時間
		if (attendanceItemId == AttendanceItemOfMonthly.FLEX_LEGAL_TIME.value){
			this.flexLegalTime = this.flexLegalTime.addMinutes(targetItemTime.v());
		}
		
		// フレックス法定外時間
		if (attendanceItemId == AttendanceItemOfMonthly.FLEX_ILLEGAL_TIME.value){
			this.flexIllegalTime = this.flexIllegalTime.addMinutes(targetItemTime.v());
		}
		
		// 所定内割増時間
		if (attendanceItemId == AttendanceItemOfMonthly.WITHIN_PRESCRIBED_PREMIUM_TIME.value){
			this.withinPrescribedPremiumTime = this.withinPrescribedPremiumTime.addMinutes(targetItemTime.v());
		}
		
		// 週割増合計時間
		if (attendanceItemId == AttendanceItemOfMonthly.WEEKLY_TOTAL_PREMIUM_TIME.value){
			this.weeklyPremiumTime = this.weeklyPremiumTime.addMinutes(targetItemTime.v());
		}
		
		// 月割増合計時間
		if (attendanceItemId == AttendanceItemOfMonthly.MONTHLY_TOTAL_PREMIUM_TIME.value){
			this.monthlyPremiumTime = this.monthlyPremiumTime.addMinutes(targetItemTime.v());
		}
		
		/** 法定内休出時間 */
		if (attendanceItemId >= AttendanceItemOfMonthly.LEGAL_HOL_WORK_TIME_01.value &&
				attendanceItemId <= AttendanceItemOfMonthly.LEGAL_HOL_WORK_TIME_10.value){
			this.legalHolidayWorkTime = this.legalHolidayWorkTime.addMinutes(targetItemTime.v());
		}
		
		/** 法定内振替休出時間 */
		if (attendanceItemId >= AttendanceItemOfMonthly.LEGAL_HOL_TRANSFER_WORK_TIME_01.value &&
				attendanceItemId <= AttendanceItemOfMonthly.LEGAL_HOL_TRANSFER_WORK_TIME_10.value){
			this.legalTransferTime = this.legalTransferTime.addMinutes(targetItemTime.v());
		}
		
		/** 月割増合計時間 */
		if (attendanceItemId == AttendanceItemOfMonthly.TEMPORARY_TIME.value){
			this.temporaryTime = this.temporaryTime.addMinutes(targetItemTime.v());
		}
	}
	
	/** 36協定対象時間を計算 */ 
	public AttendanceTimeMonth calcAgreementTime() {
		
		/** 属性の内、
			・法定内休出時間
			・法定内振替時間
			以外を合計する。 */
		/** 36協定対象となる項目を合計する */
		return this.overTime.addMinutes(this.transferOverTime.v())
				.addMinutes(this.illegalHolidayWorkTime.v())
				.addMinutes(this.illegaltransferTime.v())
				.addMinutes(this.flexLegalTime.v())
				.addMinutes(this.flexIllegalTime.v())
				.addMinutes(this.withinPrescribedPremiumTime.v())
				.addMinutes(this.weeklyPremiumTime.v())
				.addMinutes(this.monthlyPremiumTime.v())
				.addMinutes(this.temporaryTime.v());
	}
	
	/**
	 * 法定上限時間を計算
	 * @return 合計時間
	 */
	public AttendanceTimeMonth calcLegalLimitTime() {
		
		/** 全ての属性を合計する */
		/** 法定上限対象となる項目を合計する */
		return calcAgreementTime()
				.addMinutes(this.legalHolidayWorkTime.v())
				.addMinutes(this.legalTransferTime.v());
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(AgreementTimeBreakdown target){
		this.overTime = this.overTime.addMinutes(target.overTime.v());
		this.transferOverTime = this.transferOverTime.addMinutes(target.transferOverTime.v());
		this.illegalHolidayWorkTime = this.illegalHolidayWorkTime.addMinutes(target.illegalHolidayWorkTime.v());
		this.illegaltransferTime = this.illegaltransferTime.addMinutes(target.illegaltransferTime.v());
		this.flexLegalTime = this.flexLegalTime.addMinutes(target.flexLegalTime.v());
		this.flexIllegalTime = this.flexIllegalTime.addMinutes(target.flexIllegalTime.v());
		this.withinPrescribedPremiumTime = this.withinPrescribedPremiumTime.addMinutes(target.withinPrescribedPremiumTime.v());
		this.weeklyPremiumTime = this.weeklyPremiumTime.addMinutes(target.weeklyPremiumTime.v());
		this.monthlyPremiumTime = this.monthlyPremiumTime.addMinutes(target.monthlyPremiumTime.v());
		this.temporaryTime = this.temporaryTime.addMinutes(target.temporaryTime.v());
		this.legalHolidayWorkTime = this.legalHolidayWorkTime.addMinutes(target.legalHolidayWorkTime.v());
		this.legalTransferTime = this.legalTransferTime.addMinutes(target.legalTransferTime.v());
	}
}