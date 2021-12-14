package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.care.CareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.ChildcareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 戻り値：ドメインサービス：月別実績を集計する．集計処理
 * @author shuichi_ishida
 */
@Getter
public class AggregateMonthlyRecordValue {

	/** 月別実績の勤怠時間 */
	@Setter
	private Optional<AttendanceTimeOfMonthly> attendanceTime;
	/** 週別実績の勤怠時間 */
	private List<AttendanceTimeOfWeekly> attendanceTimeWeeks;
	/** 月別実績の所属情報 */
	@Setter
	private Optional<AffiliationInfoOfMonthly> affiliationInfo;
	/** 月別実績の任意項目 */
	@Setter
	private List<AnyItemOfMonthly> anyItemList;
	/** 管理時間の36協定時間 */
	@Setter
	private Optional<AgreementTimeOfManagePeriod> agreementTime;
	@Setter
	private Optional<AgreementTimeOfManagePeriod> prevAgreementTime;
	/** 年休月別残数データ */
	private List<AnnLeaRemNumEachMonth> annLeaRemNumEachMonthList;
	/** 積立年休月別残数データ */
	private List<RsvLeaRemNumEachMonth> rsvLeaRemNumEachMonthList;
	/** 振休月別残数データ */
	private List<AbsenceLeaveRemainData> absenceLeaveRemainList;
	/** 代休月別残数データ */
	private List<MonthlyDayoffRemainData> monthlyDayoffRemainList;
	/** 特別休暇月別残数データ */
	private List<SpecialHolidayRemainData> specialLeaveRemainList;
	/** 介護休暇月別残数データ */
	@Setter
	private List<CareRemNumEachMonth> careHdRemainList;
	/** 子の看護休暇月別残数データ */
	@Setter
	private List<ChildcareRemNumEachMonth> childHdRemainList;
	/** 公休月別残数データ */
	@Setter
	private List<PublicHolidayRemNumEachMonth> publicRemainList;

//	/** 年休積立年休の集計結果 */
//	@Setter
//	private AggrResultOfAnnAndRsvLeave aggrResultOfAnnAndRsvLeave;
//	/** 振休振出の集計結果 */
//	@Setter
//	private Optional<AbsRecRemainMngOfInPeriod> absRecRemainMngOfInPeriodOpt;
//	/** 代休の集計結果 */
//	@Setter
//	private Optional<BreakDayOffRemainMngOfInPeriod> breakDayOffRemainMngOfInPeriodOpt;
//	/** 特別休暇の集計結果 */
//	private Map<Integer, InPeriodOfSpecialLeaveResultInfor> inPeriodOfSpecialLeaveResultInforMap;
	/** エラー情報 */
	private Map<String, MonthlyAggregationErrorInfo> errorInfos;
	/** 社員の月別実績エラー一覧 */
	private List<EmployeeMonthlyPerError> perErrors;
	/** 中断フラグ */
	@Setter
	private boolean interruption;

	/*
	 * コンストラクタ
	 */
	public AggregateMonthlyRecordValue(){

		this.attendanceTime = Optional.empty();
		this.attendanceTimeWeeks = new ArrayList<>();
		this.affiliationInfo = Optional.empty();
		this.anyItemList = new ArrayList<>();
		this.agreementTime = Optional.empty();
		this.annLeaRemNumEachMonthList = new ArrayList<>();
		this.rsvLeaRemNumEachMonthList = new ArrayList<>();
		this.absenceLeaveRemainList = new ArrayList<>();
		this.monthlyDayoffRemainList = new ArrayList<>();
		this.specialLeaveRemainList = new ArrayList<>();
		this.careHdRemainList = new ArrayList<>();
		this.childHdRemainList = new ArrayList<>();
		this.publicRemainList = new ArrayList<>();
		
//		this.aggrResultOfAnnAndRsvLeave = new AggrResultOfAnnAndRsvLeave();
//		this.absRecRemainMngOfInPeriodOpt = Optional.empty();
//		this.breakDayOffRemainMngOfInPeriodOpt = Optional.empty();
//		this.inPeriodOfSpecialLeaveResultInforMap = new HashMap<>();
		this.errorInfos = new HashMap<>();
		this.perErrors = new ArrayList<>();
		this.interruption = false;
	}

	/**
	 * エラー情報を追加する
	 * @param resourceId リソースID
	 * @param message エラーメッセージ
	 */
	public void addErrorInfos(String resourceId, ErrMessageContent message){
		this.errorInfos.putIfAbsent(resourceId, new MonthlyAggregationErrorInfo(resourceId, message));
	}

	/**
	 * エラー情報に指定のリソースIDがあるかどうか
	 * @param resourceId リソースID
	 * @return true：ある、false：ない
	 */
	public boolean existErrorResource(String resourceId){
		return this.errorInfos.containsKey(resourceId);
	}

	/**
	 * 最大の週Noを確認する
	 * @return 最大の週No
	 */
	public int getMaxWeekNo(){
		int maxNo = 0;
		for (val attendanceTimeWeek : this.attendanceTimeWeeks){
			int weekNo = attendanceTimeWeek.getWeekNo();
			if (maxNo < weekNo) maxNo = weekNo;
		}
		return maxNo;
	}

	/**
	 * 月別実績の任意項目を取得
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日
	 * @param anyItemId 任意項目ID
	 * @return 月別実績の任意項目
	 */
	public Optional<AnyItemOfMonthly> getAnyItem(String employeeId, YearMonth yearMonth,
			ClosureId closureId, ClosureDate closureDate, int anyItemId){

		for (val anyItem : this.anyItemList){
			if (anyItem.getEmployeeId() == employeeId &&
				anyItem.getYearMonth().equals(yearMonth) &&
				anyItem.getClosureId() == closureId &&
				anyItem.getClosureDate().getClosureDay().equals(closureDate.getClosureDay()) &&
				anyItem.getClosureDate().getLastDayOfMonth() == closureDate.getLastDayOfMonth() &&
				anyItem.getAnyItemId() == anyItemId){

				return Optional.of(anyItem);
			}
		}
		return Optional.empty();
	}

	/**
	 * 月別実績の任意項目の追加または更新
	 * @param putAnyItem 月別実績の任意項目
	 */
	public void putAnyItemOrUpdate(AnyItemOfMonthly putAnyItem){

		val itrAnyItem = this.anyItemList.iterator();
		while (itrAnyItem.hasNext()){
			val anyItem = itrAnyItem.next();
			if (anyItem.getEmployeeId() == putAnyItem.getEmployeeId() &&
				anyItem.getYearMonth().equals(putAnyItem.getYearMonth()) &&
				anyItem.getClosureId() == putAnyItem.getClosureId() &&
				anyItem.getClosureDate().getClosureDay().equals(putAnyItem.getClosureDate().getClosureDay()) &&
				anyItem.getClosureDate().getLastDayOfMonth() == putAnyItem.getClosureDate().getLastDayOfMonth() &&
				anyItem.getAnyItemId() == putAnyItem.getAnyItemId()){

				itrAnyItem.remove();
				break;
			}
		}
		this.anyItemList.add(putAnyItem);
	}

	/**
	 * 月別実績の任意項目の合算
	 * @param sumAnyItem 月別実績の任意項目
	 */
	public void sumAnyItem(AnyItemOfMonthly sumAnyItem){

		val itrAnyItem = this.anyItemList.iterator();
		while (itrAnyItem.hasNext()){
			val anyItem = itrAnyItem.next();
			if (anyItem.getEmployeeId() == sumAnyItem.getEmployeeId() &&
				anyItem.getYearMonth().equals(sumAnyItem.getYearMonth()) &&
				anyItem.getClosureId() == sumAnyItem.getClosureId() &&
				anyItem.getClosureDate().getClosureDay().equals(sumAnyItem.getClosureDate().getClosureDay()) &&
				anyItem.getClosureDate().getLastDayOfMonth() == sumAnyItem.getClosureDate().getLastDayOfMonth() &&
				anyItem.getAnyItemId() == sumAnyItem.getAnyItemId()){

				anyItem.sum(sumAnyItem);
				return;
			}
		}
		this.anyItemList.add(sumAnyItem);
	}

	/**
	 * 月別実績(Work)の取得
	 * @return 月別実績(Work)
	 */
	public IntegrationOfMonthly getIntegration(){

		IntegrationOfMonthly result = new IntegrationOfMonthly();
		result.setAttendanceTime(this.attendanceTime);
		result.setAffiliationInfo(this.affiliationInfo);
		result.getAnyItemList().addAll(this.anyItemList);
		result.setAgreementTime(this.agreementTime);
		AnnLeaRemNumEachMonth annualLeaveRemain = null;
		if (this.annLeaRemNumEachMonthList.size() > 0) annualLeaveRemain = this.annLeaRemNumEachMonthList.get(0);
		result.setAnnualLeaveRemain(Optional.ofNullable(annualLeaveRemain));
		RsvLeaRemNumEachMonth reserveLeaveRemain = null;
		if (this.rsvLeaRemNumEachMonthList.size() > 0) reserveLeaveRemain = this.rsvLeaRemNumEachMonthList.get(0);
		result.setReserveLeaveRemain(Optional.ofNullable(reserveLeaveRemain));
		AbsenceLeaveRemainData absenceLeaveRemain = null;
		if (this.absenceLeaveRemainList.size() > 0) absenceLeaveRemain = this.absenceLeaveRemainList.get(0);
		result.setAbsenceLeaveRemain(Optional.ofNullable(absenceLeaveRemain));
		MonthlyDayoffRemainData monthlyDayoffRemain = null;
		if (this.monthlyDayoffRemainList.size() > 0) monthlyDayoffRemain = this.monthlyDayoffRemainList.get(0);
		result.setMonthlyDayoffRemain(Optional.ofNullable(monthlyDayoffRemain));
		result.getSpecialLeaveRemain().addAll(this.specialLeaveRemainList);
		result.getAttendanceTimeOfWeek().addAll(this.attendanceTimeWeeks);
		result.getEmployeeMonthlyPerError().addAll(this.perErrors);
		CareRemNumEachMonth careRemain = null;
		if (this.careHdRemainList.size() > 0) careRemain = this.careHdRemainList.get(0);
		result.setCare(Optional.ofNullable(careRemain));
		ChildcareRemNumEachMonth childCareRemain = null;
		if (this.childHdRemainList.size() > 0) childCareRemain = this.childHdRemainList.get(0);
		result.setChildCare(Optional.ofNullable(childCareRemain));
		PublicHolidayRemNumEachMonth publicHolidayRemain = null;
		if (this.publicRemainList.size() > 0) publicHolidayRemain = this.publicRemainList.get(0);
		result.setPublicHolidayLeaveRemain(Optional.ofNullable(publicHolidayRemain));
		
		return result;
	}
}
