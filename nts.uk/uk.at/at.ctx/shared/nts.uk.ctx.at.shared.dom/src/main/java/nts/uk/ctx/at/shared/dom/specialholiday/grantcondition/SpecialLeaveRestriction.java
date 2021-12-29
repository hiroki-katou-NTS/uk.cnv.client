package nts.uk.ctx.at.shared.dom.specialholiday.grantcondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import lombok.AllArgsConstructor;
//import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.dom.DomainObject;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonthDayHolder.Difference;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeRecordImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.SClsHistImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.ConditionFlg;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;

/**
 * 特別休暇利用条件
 * 
 * @author tanlv
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpecialLeaveRestriction extends DomainObject {
	
	/** 会社ID */
	private String companyId;

	/** 特別休暇コード */
	private SpecialHolidayCode specialHolidayCode;

	/** 分類条件 */
	private UseAtr restrictionCls;

	/** 年齢条件 */
	private UseAtr ageLimit;

	/** 性別条件 */
	private UseAtr genderRest;

	/** 雇用条件 */
	private UseAtr restEmp;

	/** 分類一覧 */
	private List<String> listCls = new ArrayList<>();

	/** 年齢基準 */
	private AgeStandard ageStandard;

	/** 年齢範囲 */
	private AgeRange ageRange;

	/** 性別 */
	private GenderCls gender;

	/** 雇用一覧 */
	private List<String> listEmp = new ArrayList<>();

	@Override
	public void validate() {
		super.validate();
	}
	
	/** 分類条件を使用するときはTrueを返す */
	public boolean isRestrictionCls(){
		return this.getRestrictionCls().equals(UseAtr.USE);
	}
	
	/** 年齢条件を使用するときはTrueを返す */
	public boolean isAgeLimit(){
		return this.getAgeLimit().equals(UseAtr.USE);
	}
	
	/** 性別条件を使用するときはTrueを返す */
	public boolean isGenderRest(){
		return this.getGenderRest().equals(UseAtr.USE);
	}
	
	/** 雇用条件を使用するときはTrueを返す */
	public boolean isRestEmp(){
		return this.getRestEmp().equals(UseAtr.USE);
	}
	
	/**
	 * Validate input data
	 */
	public List<String> validateInput() {
		List<String> errors = new ArrayList<>();
		
		// 雇用条件を使用する場合は、雇用一覧を１件以上登録する事。
		if (this.restEmp == UseAtr.USE && CollectionUtil.isEmpty(this.listEmp)) {
			errors.add("Msg_105");
		}

		// 分類条件を使用する場合は、分類一覧を１件以上登録する事。
		if (this.restrictionCls == UseAtr.USE && CollectionUtil.isEmpty(this.listCls)) {
			errors.add("Msg_108");
		}

		// 年齢条件を使用する場合は、年齢範囲を登録する事。
		if (this.ageLimit == UseAtr.USE) {
            Integer lower = this.ageRange != null ? this.ageRange.getAgeLowerLimit().v() : 0;
            Integer higer = this.ageRange != null ? this.ageRange.getAgeHigherLimit().v() : 0;
			
			// 年齢下限 <= 年齢上限
			if (lower > higer) {
				errors.add("Msg_119");
			}

			// 0<=年齢上限<=99
			// 0<=年齢下限<=99
			if ((lower > 99 || lower < 0) || (higer > 99 || higer < 0)) {
				errors.add("Msg_366");
			}
		}
		
		return errors;
	}

	public static SpecialLeaveRestriction createFromJavaType(String companyId, int specialHolidayCode,
			int restrictionCls, int ageLimit, int genderRest, int restEmp, List<String> listCls,
			AgeStandard ageStandard, AgeRange ageRange, int gender, List<String> listEmp) {
		return new SpecialLeaveRestriction(companyId, new SpecialHolidayCode(specialHolidayCode),
				EnumAdaptor.valueOf(restrictionCls, UseAtr.class), EnumAdaptor.valueOf(ageLimit, UseAtr.class),
				EnumAdaptor.valueOf(genderRest, UseAtr.class), EnumAdaptor.valueOf(restEmp, UseAtr.class), listCls,
				ageStandard, ageRange, EnumAdaptor.valueOf(gender, GenderCls.class), listEmp);
	}

	public static SpecialLeaveRestriction createFromJavaType(String companyId, int specialHolidayCode,
			int restrictionCls, int ageLimit, int genderRest, int restEmp,
			AgeStandard ageStandard, AgeRange ageRange, int gender) {
		return new SpecialLeaveRestriction(companyId, new SpecialHolidayCode(specialHolidayCode),
				EnumAdaptor.valueOf(restrictionCls, UseAtr.class), EnumAdaptor.valueOf(ageLimit, UseAtr.class),
				EnumAdaptor.valueOf(genderRest, UseAtr.class), EnumAdaptor.valueOf(restEmp, UseAtr.class), ageStandard,
				ageRange, EnumAdaptor.valueOf(gender, GenderCls.class));
	}

	public SpecialLeaveRestriction(String companyId, SpecialHolidayCode specialHolidayCode,
			UseAtr restrictionCls, UseAtr ageLimit, UseAtr genderRest, UseAtr restEmp, AgeStandard ageStandard,
			AgeRange ageRange, GenderCls gender) {
		super();
		this.companyId = companyId;
		this.specialHolidayCode = specialHolidayCode;
		this.restrictionCls = restrictionCls;
		this.ageLimit = ageLimit;
		this.genderRest = genderRest;
		this.restEmp = restEmp;
		this.ageStandard = ageStandard;
		this.ageRange = ageRange;
		this.gender = gender;
	}
	
	
	/**
	 * 利用可能か判断する
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param spLeaveCD 特別休暇コード
	 * @param 基準日
	 * @return true：利用可能、false：利用不可
	 */
	public boolean checkUseCondition(
			Require require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			int spLeaveCD,
			GeneralDate ymd) {

		// Imported(就業)「社員」を取得する
		EmployeeRecordImport empInfor = require.employeeFullInfo(cacheCarrier, employeeId);

		boolean genderFlg = true;
		
		//「特別休暇利用条件．性別条件」をチェックする
		if(this.isGenderRest()){ // 利用するとき
			genderFlg = canUseByGenderCondition(empInfor);
		}

		boolean employmentFlg = true;
		
		//「特別休暇利用条件．雇用条件」をチェックする
		if(this.isRestEmp()){ // 利用するとき
			employmentFlg = canUseByEmploymentCondition(require, cacheCarrier,companyId, empInfor, ymd);
		}
		
		boolean classFlg = true;

		//「特別休暇利用条件.分類条件」をチェックする
		if(this.isRestrictionCls()){ // 利用するとき
			classFlg = canUseByClsCondition(require, cacheCarrier,companyId, empInfor, ymd);
		}

		boolean ageFlg = true;
		
		//「特別休暇利用条件．年齢条件」をチェックする
		if(this.isAgeLimit()){ // 利用するとき

			ageFlg = canUseByAgeCondition(empInfor,ymd);
		}
		return new ConditionFlg(employmentFlg, genderFlg, ageFlg, classFlg).canUse();
	}

	/**
	 * 性別条件から利用可能か判断する
	 * @param empInfor
	 * @return
	 */
	private boolean canUseByGenderCondition(EmployeeRecordImport empInfor){
		
		// 性別設定と一致するかチェックする
		return (empInfor.getGender() == this.getGender().value);
	}
	
	
	/**
	 * 雇用条件から利用可能か判断する
	 * @param require
	 * @param cacheCarrier
	 * @param companyId
	 * @param employeeId
	 * @param ymd
	 * @return
	 */
	private boolean canUseByEmploymentCondition(Require require, CacheCarrier cacheCarrier, String companyId,
			EmployeeRecordImport empInfor,GeneralDate ymd) {
		// アルゴリズム「社員所属雇用履歴を取得」を実行する
		Optional<BsEmploymentHistoryImport> employmentHistory = require.employmentHistory(cacheCarrier, companyId,
				empInfor.getEmployeeId(), ymd);

		if (!employmentHistory.isPresent()) {
			return false;
		} else {
			if (this.getListEmp() == null && this.getListEmp().isEmpty()) {
				return false;
			}
			
			// 取得した雇用コードが「特別休暇利用条件．雇用一覧」に存在するかチェックする
			return (this.getListEmp().contains(employmentHistory.get().getEmploymentCode())) ;
		}
	}
	
	/**
	 * 分類条件から利用可能か判断する
	 * @param require
	 * @param cacheCarrier
	 * @param companyId
	 * @param employeeId
	 * @param ymd
	 * @return
	 */
	private boolean canUseByClsCondition(Require require, CacheCarrier cacheCarrier, String companyId, EmployeeRecordImport empInfor,
			GeneralDate ymd){
		
		// アルゴリズム「社員所属分類履歴を取得」を実行する
		List<SClsHistImport> clsHistList = require.employeeClassificationHistoires(
				cacheCarrier, companyId, Arrays.asList(empInfor.getEmployeeId()), new DatePeriod(ymd, ymd));
		
		Optional<SClsHistImport> clsHist = clsHistList.stream()
				.filter(x->x.getEmployeeId().equals(empInfor.getEmployeeId()))
				.findFirst();
		
		if(!clsHist.isPresent()) {
			return false;
		}
		
		
        if(this.getListCls() == null && this.getListCls().isEmpty()) {
			return false;
		}
		
		// 取得した分類コードが「特別休暇利用条件．分類一覧」に存在するかチェックする
		return (this.getListCls().contains(clsHist.get().getClassificationCode()));
	}
	
	/**
	 * 年齢条件から利用可能か判断する
	 * @param empInfor
	 * @param ymd
	 * @return
	 */
	private boolean canUseByAgeCondition(EmployeeRecordImport empInfor, GeneralDate ymd){
		GeneralDate ageBase = ymd;
		
		// 年齢基準日を求める
		nts.uk.shr.com.time.calendar.MonthDay ageBaseDate
			= this.getAgeStandard().getAgeBaseDate();

		int year = 0;

		// 「特別休暇利用条件．年齢基準．年齢基準年区分」＝　「当年」の場合
		if(this.getAgeStandard().getAgeCriteriaCls() == AgeBaseYear.THIS_YEAR) {
			// 年齢基準日 = パラメータ「基準日．年」 + 「特別休暇利用条件．年齢基準．年齢基準日」
            year = ageBaseDate != null ? ageBase.year() : 0;
		} else
		// 「特別休暇利用条件．年齢基準．年齢基準年区分」＝　「翌年」の場合
		if(this.getAgeStandard().getAgeCriteriaCls() == AgeBaseYear.NEXT_YEAR) {
			// 年齢基準日 = パラメータ「基準日．年」 の翌年 + 「特別休暇利用条件．年齢基準．年齢基準日」
            year = ageBaseDate != null ? ageBase.year() + 1 : 0;
		}

		if(year != 0
			&& ageBaseDate.getMonth() != 0
			&& ageBaseDate.getDay() != 0) {
			ageBase = GeneralDate.ymd(year, ageBaseDate.getMonth(), ageBaseDate.getDay());
		}

		// 求めた「年齢基準日」時点の年齢を求める
		Difference difYMD = ageBase.differenceFrom(empInfor.getBirthDay());

		//求めた「年齢」が年齢条件に一致するかチェックする
		return(this.getAgeRange().getAgeLowerLimit().v() <= difYMD.years()
				&& this.getAgeRange().getAgeHigherLimit().v() >= difYMD.years());

	}
	
	
	
	public static interface Require {

		EmployeeRecordImport employeeFullInfo(CacheCarrier cacheCarrier, String empId);
		Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId,
				String employeeId, GeneralDate baseDate);
		List<SClsHistImport> employeeClassificationHistoires(CacheCarrier cacheCarrier, String companyId,
				List<String> employeeIds, DatePeriod datePeriod);
	}
}
