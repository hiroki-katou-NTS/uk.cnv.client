/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.data.CareManagementDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.NursingCareLeaveRemainingInfo;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.workingcondition.LaborContractTime;
import nts.uk.shr.com.time.calendar.MonthDay;

/**
 * The Class NursingLeaveSetting.
 * 介護看護休暇設定
 */
@Getter
@Setter
public class NursingLeaveSetting extends AggregateRoot {

	/** 会社ID */
	private String companyId;

	/** 管理区分 */
	private ManageDistinct manageType;

	/** 介護看護区分 */
	private NursingCategory nursingCategory;

	/** 起算日 */
	private MonthDay startMonthDay;

	/** 上限人数設定 */
	private List<MaxPersonSetting> maxPersonSetting;

	/** 特別休暇枠NO */
	private Optional<Integer> specialHolidayFrame;

	/** 欠勤枠NO */
	private Optional<Integer> workAbsence;

	/** 時間介護看護設定 */
	private TimeCareNursingSet timeCareNursingSetting;

	/**
	 * Checks if is managed.
	 *
	 * @return true, if is managed
	 */
	public boolean isManaged() {
		return this.manageType.equals(ManageDistinct.YES);
	}

	/**
	 * Instantiates a new nursing vacation setting.
	 *
	 * @param memento
	 *            the memento
	 */
	public NursingLeaveSetting(NursingLeaveSettingGetMemento memento) {
		this.companyId = memento.getCompanyId();
		this.manageType = memento.getManageType();
		this.nursingCategory = memento.getNursingCategory();
		this.startMonthDay = memento.getStartMonthDay();
		this.maxPersonSetting = memento.getMaxPersonSetting();
		this.specialHolidayFrame = memento.getHdspFrameNo();
		this.workAbsence = memento.getAbsenceFrameNo();
		this.timeCareNursingSetting = memento.getTimeCareNursingSet();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(NursingLeaveSettingSetMemento memento) {
		memento.setCompanyId(this.companyId);
		memento.setManageType(this.manageType);
		memento.setNursingCategory(this.nursingCategory);
		memento.setStartMonthDay(this.startMonthDay);
		memento.setMaxPersonSetting(this.maxPersonSetting);
		memento.setHdspFrameNo(this.specialHolidayFrame);
		memento.setAbsenceFrameNo(this.workAbsence);
		memento.setTimeCareNursingSet(this.timeCareNursingSetting);
		memento.setNumPer1(1);
		memento.setNumPer2(2);	
	}

	/**
	 * コンストラクタ
	 */
	public NursingLeaveSetting(){

		this.companyId = "";
		this.manageType = ManageDistinct.NO;
		this.nursingCategory = NursingCategory.Nursing;
		this.startMonthDay = new MonthDay(0, 0);
		this.maxPersonSetting = new ArrayList<>();
		this.specialHolidayFrame = Optional.empty();
		this.workAbsence = Optional.empty();
	}

	/**
	 * ファクトリー
	 * @param companyId 会社ID
	 * @param provisionalDate  管理区分
	 * @param nursingCategory 介護看護区分
	 * @param YearAtr 起算日
	 * @param AggrResultOfChildCareNurse 上限人数設定
	 * @param specialHolidayFrame 特別休暇枠NO
	 * @param workAbsence 欠勤枠NO
	 * @return  介護看護休暇設定
	 */
	public static NursingLeaveSetting of(
			String companyId,
			ManageDistinct manageType,
			NursingCategory nursingCategory,
			MonthDay startMonthDay,
			List<MaxPersonSetting> maxPersonSetting,
			Optional<Integer> specialHolidayFrame,
			Optional<Integer> workAbsence){

		NursingLeaveSetting domain = new NursingLeaveSetting();
	domain.companyId = companyId;
	domain.manageType = manageType;
	domain.nursingCategory = nursingCategory;
	domain.startMonthDay = startMonthDay;
	domain.maxPersonSetting = maxPersonSetting;
	domain.specialHolidayFrame = specialHolidayFrame;
	domain.workAbsence = workAbsence;
	return domain;
	}

	/**
	 * 家族情報から対象人数を履歴で求める
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param criteriaDate 基準日
	 * @param Require｛
	 * 								社員IDが一致する家族情報を取得（社員ID）
	 * 								介護対象管理データ（家族ID）
	 * 							｝
	 * @return 上限日数分割日（List）
	 */
	public List<ChildCareNurseUpperLimitSplit> getHistoryCountFromFamilyInfo(
			String employeeId, DatePeriod period,
			GeneralDate criteriaDate,
			RequireM7 require) {

		List<ChildCareTargetChanged> childCareTargetChanged = new ArrayList<>();

		// 子の看護か介護か
		if(nursingCategory == NursingCategory.ChildNursing) {
			// 子の看護
			childCareTargetChanged = getCountChildCareWithinPeriod(employeeId, period, criteriaDate, require);
		}else {
			// 介護
			childCareTargetChanged = childCareTargetChanged(employeeId, period, require);
		}

		// 上限日数分割日を求める
		List<ChildCareNurseUpperLimitSplit> splitDateList = upperLimitSplit(childCareTargetChanged);

		// 「上限日数分割日（List）」を返す
		return splitDateList;
	}

	/**
	 * 介護対象人数を履歴で求める
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param Require｛
	 * 								社員IDが一致する家族情報を取得（社員ID）
	 * 								介護対象管理データ（家族ID）
	 * 							｝
	 * @return 看護介護対象人数（List）
	 */
	public List<ChildCareTargetChanged> childCareTargetChanged(String employeeId, DatePeriod period, RequireM7 require){

		// 介護対象期間
		CareTargetPeriod careTargetPeriod = new CareTargetPeriod();
		// 看護介護対象人数変更日
		List<ChildCareTargetChanged> childCareTargetChanged = new ArrayList<>();

		// INPUT．Require．社員IDが一致する家族情報を取得
		List<FamilyInfo> familyInfo = require.familyInfo(employeeId); //一時対応

		// 期間開始日で処理履歴分割日を作成する
		childCareTargetChanged.add(ChildCareTargetChanged.of(new NumberOfCaregivers(0), period.start()));

		// 取得した「家族」の件数ループ
		for (int idx = 0; idx < familyInfo.size(); idx++) {
			val currentDayProcess = familyInfo.get(idx);

			// Require．INPUT．介護対象管理データを取得する
			Optional<CareManagementDate> careData = require.careData(currentDayProcess.getFamilyID());
			if(!careData.isPresent())
				continue;

			// 介護対象期間を確認
			careData.get().careTargetPeriodWork(period, currentDayProcess.getDeadYmd(), childCareTargetChanged);

			// 介護期間に含まれる介護人数変更日リストを求める
			childCareTargetChanged = careTargetPeriod.childCareTargetChanged(childCareTargetChanged);
		}

		// 「看護介護対象人数（List）」を返す
		return childCareTargetChanged;
	}

	/**
	 * 期間の子の看護対象人数を求める
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param GeneralDate 基準日
	 * @param Require｛
	 * 								社員IDが一致する家族情報を取得（社員ID）
	 * 							｝
	 * @return 看護介護対象人数変更日（List）
	 */
	public List<ChildCareTargetChanged> getCountChildCareWithinPeriod(
			String employeeId, DatePeriod period, GeneralDate criteriaDate, RequireM7 require) {

		// 子の看護本年と翌年の対象人数を求める
		ChildCareNurseTargetCountWork targetCount = targetCountWork(employeeId, period.start(),require);

		// 次回起算日を求める
		GeneralDate nextStartMonthDay= getNextStartMonthDay(criteriaDate);

		// 期間の対象人数を求める
		List<ChildCareTargetChanged> changeDateList = getCountWithinPeriod(period, targetCount, nextStartMonthDay);

		// 「看護介護対象人数変更日（List）」を返す
		return changeDateList;
	}

	/**
	 * 子の看護本年と翌年の対象人数を求める
	 * @param employeeId 社員ID
	 * @param criteriaDate 基準日
	 * @param Require 社員IDが一致する家族情報を取得（社員ID）
	 * @return 看護介護対象人数変更日（List）
	 */
	public ChildCareNurseTargetCountWork targetCountWork(String employeeId, GeneralDate criteriaDate, RequireM7 require) {

		// INPUT．Require．社員IDが一致する家族情報を取得
		List<FamilyInfo> familyInfo = require.familyInfo(employeeId);
		// 本年と次回の対象人数
		ChildCareNurseTargetCountWork targetCountWork = new ChildCareNurseTargetCountWork();

		// 取得した家族の件数分家族の生年月日を作成
		// ===家族の生年月日.生年月日←<imported>家族情報.生年月日
		 List<FamilyBirthday> familyBirthday = familyInfo.stream().map(c -> FamilyBirthday.of(c.getBirthday())).collect(Collectors.toList());

		// 本年起算日を求める
		GeneralDate thisYearStartMonthDay = getThisYearStartMonthDay(criteriaDate);

		// 次回起算日を求める
		GeneralDate nextStartMonthDay= getNextStartMonthDay(criteriaDate);

		for(int idx = 0; idx < familyBirthday.size(); idx++) {

			val currentDayProcess = familyBirthday.get(idx);

			// 子の看護対象判定
			ChildCareAtr childCareAtr = currentDayProcess.childCareAtr(thisYearStartMonthDay, nextStartMonthDay);

			if (childCareAtr.isThisAtr()) {
				// 本年対象人数に1人加算
				targetCountWork.addThisYearOnePerson();
			}
			if (childCareAtr.isNextAtr()) {
				// 次回対象人数に1人加算
				targetCountWork.addNextYearOnePerson();
			}
		}

		// 本年と翌年の対象人数を返す
		return targetCountWork;
	}

	/**
	 * 期間の対象人数を求める
	 * @param period 期間
	 * @param numOfPeople 本年と次回の対象人数
	 * @param nextStartMonthDay 次回起算日
	 * @return 看護介護対象人数変更日（List）
	 */
	public List<ChildCareTargetChanged> getCountWithinPeriod(
			DatePeriod period,
			ChildCareNurseTargetCountWork numOfPeople,
			GeneralDate nextStartMonthDay) {

		// 看護介護対象人数変更日
		List<ChildCareTargetChanged> childCareTargetChanged = new ArrayList<>();

		// 期間に次回起算日があるか
		// ===期間．開始日＜＝　パラメータ「次回起算日」　＜＝期間．終了日
		if(period.contains(nextStartMonthDay)){

			// 本年と次回の対象人数を設定
			childCareTargetChanged.add(ChildCareTargetChanged.of(numOfPeople.getThisYearTargetCount(), period.start()));
			childCareTargetChanged.add(ChildCareTargetChanged.of(numOfPeople.getNextYearTargetCount(), nextStartMonthDay));
		} else {
			//本年の対象人数を設定
			childCareTargetChanged.add(ChildCareTargetChanged.of(numOfPeople.getThisYearTargetCount(), period.start()));
		}
		// 看護介護対象人数変更日（List）
		return childCareTargetChanged;
	}

	/**
	 * 次回起算日を求める
	 * @param criteriaDate 基準日
	 * @return 次回起算日
	 */
	public  GeneralDate getNextStartMonthDay(GeneralDate criteriaDate) {

		// 基準日の月日と起算日の月日を比較
		if(criteriaDate.beforeOrEquals(this.startMonthDay.toDate(criteriaDate.year()))) { //基準日．月日　＜＝　起算日
			// 基準日の年で次回起算日を求める
			// --- 次回起算日 =｛年：基準日．年、月：起算日．月、日：起算日．日｝
			return GeneralDate.ymd(criteriaDate.year(), this.startMonthDay.getMonth(), this.startMonthDay.getDay());
		} else {
			// 基準日の年に＋１年し次回起算日を求める
			// --- 次回起算日 =｛年：基準日．年　＋　１、月：起算日．月、日：起算日．日｝
			return GeneralDate.ymd(criteriaDate.year() + 1, this.startMonthDay.getMonth(), this.startMonthDay.getDay());
		}
	}
	/**
	 * 本年起算日を求める
	 * @param criteriaDate 基準日
	 * @return 本年起算日
	 */
	public  GeneralDate getThisYearStartMonthDay(GeneralDate criteriaDate) {

		// 「本年起算日」を求める
		GeneralDate thisYearStartMonthDay = null;

		// 基準日の月日と起算日の月日を比較
		if(criteriaDate.beforeOrEquals(this.startMonthDay.toDate(criteriaDate.year()))) { //基準日．月日　＜＝　起算日

			// 基準日の年に-1年して本年起算日を求める
			// ---本年起算日 =｛年：基準日．年　-　１、月：起算日．月、日：起算日．日｝
			thisYearStartMonthDay = GeneralDate.ymd(criteriaDate.year() - 1, this.startMonthDay.getMonth(), this.startMonthDay.getDay());
		} else {
			// 基準日の年で本年起算日を求める
			// --- 本年起算日 =｛年：基準日．年、月：起算日．月、日：起算日．日｝
			thisYearStartMonthDay = GeneralDate.ymd(criteriaDate.year(), this.startMonthDay.getMonth(), this.startMonthDay.getDay());
		}
		// 本年起算日を返す
		return thisYearStartMonthDay;
	}

	/**
	 * 上限日数を求める
	 * @param numPerson 人数
	 * @return 介護看護休暇日数
	 */
    public ChildCareNurseUpperLimit nursingNumberPerson(NumberOfCaregivers numPerson) {

        // 何人か
        if (numPerson.v() == 0) {
        	// 0日を返す
        	return new ChildCareNurseUpperLimit(0);

        } else if (numPerson.v() == 1) {
        	// 要介護看護人数1人の介護看護休暇日数を返す
        	// ===介護看護休暇上限人数設定．要介護看護人数　＝　1
        	// ===条件に当てはまる「介護看護休暇上限人数設定．介護看護休暇日数」を返す
        	return maxPersonSetting.stream().filter(c -> c.getNursingNumberPerson().v() == 1).findFirst().get().getNursingNumberLeaveDay();
        } else {
        	// 要介護看護人数2人の介護看護休暇日数を返す
        	// ===介護看護休暇上限人数設定．要介護看護人数　＝　2
        	// ===条件に当てはまる「介護看護休暇上限人数設定．介護看護休暇日数」を返す
        	return maxPersonSetting.stream().filter(c -> c.getNursingNumberPerson().v() ==2).findFirst().get().getNursingNumberLeaveDay();
        }
    }


	/**
	 * 上限日数分割日を求める
	 * @param childCareTargetChanged 看護介護対象人数変更日List
	 * @return 上限日数分割日
	 */
    public List<ChildCareNurseUpperLimitSplit> upperLimitSplit(List<ChildCareTargetChanged> childCareTargetChanged) {

    	// 上限日数分割日
    	List<ChildCareNurseUpperLimitSplit> childCareNurseUpperLimitSplit = new ArrayList<>();;

    	// 看護介護対象人数変更日の件数ループ
		for (int idx = 0; idx < childCareTargetChanged.size(); idx++) {

			val currentDayProcess = childCareTargetChanged.get(idx);

	    	// 上限日数を求める
			ChildCareNurseUpperLimit nursingNumberPerson = nursingNumberPerson(currentDayProcess.getNumPerson());

	    	// 年月日が一番大きい上限日数分割日の上限日数と比較
    		// ==nursingNumberPersonとChildCareNurseUpperLimitSplitの上限日数を比較
    		// ===true：年月日が一番大きい上限日数分割日の上限日数と求めた上限日数が違う or 上限日数分割日が0件
    		// ===false：年月日が一番大きい上限日数分割日の上限日数と求めた上限日数が同じ
			Optional<ChildCareNurseUpperLimitSplit> largest = childCareNurseUpperLimitSplit.stream().filter(c -> c.getLimitDays().equals(nursingNumberPerson)).findFirst();

    		if(!largest.isPresent()) {
    	    	// 上限日数分割日を作成
    	    	// ===年月日←処理中「看護介護対象人数変更日．変更日」
    	    	// ===上限日数←求めた上限日数
				childCareNurseUpperLimitSplit.add(ChildCareNurseUpperLimitSplit.of(nursingNumberPerson, currentDayProcess.getYmd()));
    		}
    	}
		return childCareNurseUpperLimitSplit;
    }
    
    /**
     * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.休暇.子の看護休暇.介護看護休暇設定.残数の集計期間を求める.残数の集計期間を求める
     * 残数の集計期間を求める
     * @param date
     */
    public DatePeriod findPeriodForRemainNumber(GeneralDate date) {
        // 基準日の月日と起算日の月日を比較
        // 基準日．月日　＜＝　起算日
        if (date.beforeOrEquals(this.startMonthDay.toDate(date.year()))) {
            // 基準日の年に-1年し集計期間を求める
            GeneralDate startDate = GeneralDate.ymd(date.year() - 1, startMonthDay.getMonth(), startMonthDay.getDay());
            GeneralDate endDate = startDate.addYears(1).addDays(-1);
            
            return new DatePeriod(startDate, endDate);
        }
        
        // 基準日．月日　＞　起算日
        // 基準日の年で集計期間を求める
        GeneralDate starDate = GeneralDate.ymd(date.year(),  startMonthDay.getMonth(), startMonthDay.getDay());
        GeneralDate endDate = starDate.addYears(1).addDays(-1);
        
        return new DatePeriod(starDate, endDate);
    }


	// Require
	public static interface RequireM8 {

		// 社員IDが一致する家族情報を取得（社員ID）
		List<FamilyInfo> familyInfo(String employeeId);
	}

	public static interface RequireM7{

		// 介護看護休暇設定を取得する（会社ID、介護看護区分）
		NursingLeaveSetting nursingLeaveSetting(String companyId, NursingCategory nursingCategory);

		// 社員IDが一致する家族情報を取得（社員ID）
		List<FamilyInfo> familyInfo(String employeeId);

		// 介護対象管理データ（家族ID）
		Optional<CareManagementDate> careData(String familyID);
	}

	public static interface Require {

		// 介護看護休暇設定を取得する（会社ID、介護看護区分）
		NursingLeaveSetting nursingLeaveSetting(String companyId, NursingCategory nursingCategory);

		// 子の看護・介護休暇基本情報を取得する（社員ID）
		NursingCareLeaveRemainingInfo employeeInfo(String employeeId);

		// 年休の契約時間を取得する（社員ID、基準日）
		LaborContractTime contractTime(String employeeId, GeneralDate criteriaDate);

		// 社員IDが一致する家族情報を取得（社員ID）
		List<FamilyInfo> familyInfo(String employeeId);

//		// 介護対象管理データ（家族ID）
		Optional<CareManagementDate> careData(String familyID);

		// 期間の上限日数取得する（会社ID、社員ID、期間、介護看護区分）
		NursingCareLeaveRemainingInfo upperLimitPeriod (String companyId, String employeeId, DatePeriod period, NursingCategory nursingCategory);
	}
}