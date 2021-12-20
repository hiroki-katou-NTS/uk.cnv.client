package nts.uk.file.at.app.export.specialholiday;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeBaseYear;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.UseAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYear;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYearMonthTbl;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTbl;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantElapseYearMonth;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.TypeTime;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.GrantDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.TimeLimitSpecification;
import nts.uk.ctx.bs.employee.dom.classification.Classification;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.calendar.MonthDay;

/**
 * 特別休暇の付与設定
 * @author huylq
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecialHolidayExportDataSource implements Comparable<SpecialHolidayExportDataSource> {
	//	コード
	private String code;
	//	名称
	private String name;
	//	対象項目
	private String targetItem;
	//	自動で付与する
	private String grantAuto;
	//	メモ
	private String memo;
	//	付与方法
	private String grantMethod;
	//	付与基準日
	private String grantDate;
	//	付与月日 -  指定日
	private String designatedDate;
	//	付与周期日数
	private String grantDays;
	//	使用可能期限開始月
	private String expirationStartMonth;
	//	使用可能期限開始日
	private String expirationStartDay;
	//	使用可能期限終了月
	private String expirationEndMonth;
	//	使用可能期限終了日
	private String expirationEndDay;
	//	連続で取得する
	private String continuousAtr;
	//	付与コード
	private String grantCode;
	//	付与名
	private String grantName;
	//	既定のテーブル
	private String defaultTable;
	//	付与の設定
	private String grantSetting;
	//	付与年数
	private String grantYears;
	//	付与月数
	private String grantMonths;
	//	上限日数
	private String maxDays;
	//	以降毎年固定日数を付与する
	private String grantFixedDaysEachYear;
	//	固定周期(年） - 付与周期（年）
	private String grantCycleYear;
	//	固定周期(月） - 付与周期（月）	
	private String grantCycleMonth;
	//	固定付与日数
	private String fixedGrantDays;
	//	有効期限
	private String expirationDate;
	//	有効期限年
	private String expirationYear;
	//	有効期限月
	private String expirationMonth;
	//	蓄積上限日数
	private String maxAccumulationDays;
	//	利用条件性別
	private String genderRestUseAtr;
	//	性別
	private String gender;
	//	雇用
	private String employment;
	//	対象雇用
	private String targetEmployment;
	//	分類
	private String type;
	//	対象分類
	private String targetType;
	//	年齢
	private String age;
	//	下限年齢
	private String minAge;
	//	上限年齢
	private String maxAge;
	//	年齢基準
	private String ageStandard;
	//	年齢基準月
	private String ageBaseMonth;
	//	年齢基準日
	private String ageBaseDate;	
	
	public static List<SpecialHolidayExportDataSource> convertToDatasource(
			SpecialHoliday specialHoliday, 
			List<GrantDateTbl> grantDateTblList, 
			ElapseYear elapseYear, 
			List<Employment> empList,
			List<Classification> clsList) {
		List<SpecialHolidayExportDataSource> dataList = new ArrayList<SpecialHolidayExportDataSource>();
		SpecialHolidayExportDataSource data = new SpecialHolidayExportDataSource();
		dataList.add(data);
		TypeTime typeTime = specialHoliday.getGrantRegular().getTypeTime();
		//A7_1
		data.setCode(specialHoliday.getSpecialHolidayCode().toString());
		//A7_2
		data.setName(specialHoliday.getSpecialHolidayName().v());
		//A7_3
		data.setTargetItem(
				SpecialHolidayExportDataSource.joinTargetItem(
						specialHoliday.getTargetItem().getFrameNo(), 
						specialHoliday.getTargetItem().getAbsenceFrameNo()
						)
				);
		//A7_38
		if(!data.getCode().isEmpty()) {
			data.setGrantAuto(specialHoliday.getAutoGrant() == NotUseAtr.USE ? "〇" : "");
		}
		//A7_4
		data.setMemo(specialHoliday.getMemo().v());
		//A7_6
		data.setGrantMethod(SpecialHolidayExportDataSource.getTypeTimeText(typeTime));
		//A7_5
		if(SpecialHolidayExportDataSource.visibleCondition("4.3", specialHoliday)) {
			data.setGrantDate(SpecialHolidayUtils.getGrantDateOptions(specialHoliday.getGrantRegular().getGrantDate().get().value));
		}
		//A7_39
		if(SpecialHolidayExportDataSource.visibleCondition("4.4", specialHoliday)) {
			MonthDay monthDay = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().get();
			data.setDesignatedDate(monthDay.getMonth() + "/" + monthDay.getDay());
		}
		//A7_8
		if(SpecialHolidayExportDataSource.visibleCondition("4.1", specialHoliday)) {
			if(typeTime == TypeTime.GRANT_SPECIFY_DATE) {
				data.setGrantDays(specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantDays().getGrantDays().toString() 
						+ TextResource.localize("KMF004_157"));
			}
			if(typeTime == TypeTime.GRANT_PERIOD) {
				data.setGrantDays(specialHoliday.getGrantRegular().getPeriodGrantDate().get().getGrantDays().getGrantDays().toString() 
						+ TextResource.localize("KMF004_157"));
			}
		}
		//A7_22 A7_23 A7_24 A7_25 
		if(SpecialHolidayExportDataSource.visibleCondition("6.3", specialHoliday)) {
			data.setExpirationStartMonth(specialHoliday.getGrantRegular().getPeriodGrantDate().get().getPeriod().start().month() 
					+ TextResource.localize("KMF004_159"));
			data.setExpirationStartDay(specialHoliday.getGrantRegular().getPeriodGrantDate().get().getPeriod().start().day() 
					+ TextResource.localize("KMF004_157"));
			data.setExpirationEndMonth(specialHoliday.getGrantRegular().getPeriodGrantDate().get().getPeriod().end().month() 
					+ TextResource.localize("KMF004_159"));
			data.setExpirationEndDay(specialHoliday.getGrantRegular().getPeriodGrantDate().get().getPeriod().end().day() 
					+ TextResource.localize("KMF004_157"));
		}
		//A7_42
		if(!data.getCode().isEmpty()) {
			data.setContinuousAtr(specialHoliday.getContinuousAcquisition() == NotUseAtr.USE ? "〇" : "");
		}
		
		//A7_9 -> A7_17
		if(SpecialHolidayExportDataSource.visibleCondition("4.2", specialHoliday)) {
			List<SpecialHolidayExportDataSource> grantDateTblAndElapseYearData = 
					data.setGrantDateTblAndElapseYear(specialHoliday, grantDateTblList, elapseYear);
			
			if(grantDateTblAndElapseYearData.size() > 0) {
				SpecialHolidayExportDataSource firstData = grantDateTblAndElapseYearData.get(0);
				data.setGrantCode(firstData.getGrantCode());
				data.setGrantName(firstData.getGrantName());
				data.setDefaultTable(firstData.getDefaultTable());
				data.setGrantSetting(firstData.getGrantSetting());
				data.setGrantYears(firstData.getGrantYears());
				data.setGrantMonths(firstData.getGrantMonths());
				data.setMaxDays(firstData.getMaxDays());
				data.setGrantFixedDaysEachYear(firstData.getGrantFixedDaysEachYear());
				data.setGrantCycleYear(firstData.getGrantCycleYear());
				data.setGrantCycleMonth(firstData.getGrantCycleMonth());
				data.setFixedGrantDays(firstData.getFixedGrantDays());
				grantDateTblAndElapseYearData.remove(0);
				
				dataList.addAll(grantDateTblAndElapseYearData);
			}
		}
		
		//A7_18 A7_19
		if(SpecialHolidayExportDataSource.visibleCondition("6.1", specialHoliday)) {
			if(typeTime == TypeTime.GRANT_SPECIFY_DATE) {
				data.setExpirationDate(SpecialHolidayUtils.getTimeMethod(
						specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getTimeSpecifyMethod().value));
				if(specialHoliday.getGrantRegular().getFixGrantDate().get()
						.getGrantPeriodic().getLimitAccumulationDays().isPresent()) {
					data.setMaxAccumulationDays(specialHoliday.getGrantRegular().getFixGrantDate().get()
							.getGrantPeriodic().getLimitAccumulationDays().get().getLimitCarryoverDays().get().v() 
							+ TextResource.localize("KMF004_157"));
				}
			}
			if(typeTime == TypeTime.REFER_GRANT_DATE_TBL) {
				data.setExpirationDate(SpecialHolidayUtils.getTimeMethod(
						specialHoliday.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getTimeSpecifyMethod().value));
				if(specialHoliday.getGrantRegular().getGrantPeriodic().get()
						.getGrantDeadline().getLimitAccumulationDays().isPresent()) {
					data.setMaxAccumulationDays(specialHoliday.getGrantRegular().getGrantPeriodic().get()
							.getGrantDeadline().getLimitAccumulationDays().get().getLimitCarryoverDays().get().v() 
							+ TextResource.localize("KMF004_157"));
				}
			}
		}
		//A7_20 A7_21
		if(SpecialHolidayExportDataSource.visibleCondition("6.2", specialHoliday)) {
			if(typeTime == TypeTime.GRANT_SPECIFY_DATE) {
				data.setExpirationYear(specialHoliday.getGrantRegular().getFixGrantDate().get()
						.getGrantPeriodic().getExpirationDate().get().getYears().v() 
						+ TextResource.localize("KMF004_156"));
				data.setExpirationMonth(specialHoliday.getGrantRegular().getFixGrantDate().get()
						.getGrantPeriodic().getExpirationDate().get().getMonths().v() 
						+ TextResource.localize("KMF004_158"));
			}
			if(typeTime == TypeTime.REFER_GRANT_DATE_TBL) {
				data.setExpirationYear(specialHoliday.getGrantRegular().getGrantPeriodic().get()
						.getGrantDeadline().getExpirationDate().get().getYears().v() 
						+ TextResource.localize("KMF004_156"));
				data.setExpirationMonth(specialHoliday.getGrantRegular().getGrantPeriodic().get()
						.getGrantDeadline().getExpirationDate().get().getMonths().v() 
						+ TextResource.localize("KMF004_158"));
			}
		}
		//A7_26
		data.setGenderRestUseAtr(specialHoliday.getSpecialLeaveRestriction().getGenderRest() == UseAtr.USE ? "〇" : "ー");
		//A7_27
		if(SpecialHolidayExportDataSource.visibleCondition("5.1", specialHoliday)) {
			data.setGender(SpecialHolidayUtils.getGender(specialHoliday.getSpecialLeaveRestriction().getGender().value - 1));
		}
		//A7_28
		data.setEmployment(specialHoliday.getSpecialLeaveRestriction().getRestEmp() == UseAtr.USE ? "〇" : "ー");
		//A7_29
		if(SpecialHolidayExportDataSource.visibleCondition("5.2", specialHoliday)) {
			data.setTargetEmployment(SpecialHolidayExportDataSource.joinTargetEmployment(empList));
		}
		//A7_30
		data.setType(specialHoliday.getSpecialLeaveRestriction().getRestrictionCls() == UseAtr.USE ? "〇" : "ー");
		//A7_31
		if(SpecialHolidayExportDataSource.visibleCondition("5.3", specialHoliday)) {
			data.setTargetType(SpecialHolidayExportDataSource.joinTargetType(clsList));
		}
		//A7_32
		data.setAge(specialHoliday.getSpecialLeaveRestriction().getAgeLimit() == UseAtr.USE ? "〇" : "ー");
		//A7_33 A7_34 A7_35 A7_36 A7_37
		if(SpecialHolidayExportDataSource.visibleCondition("5.4", specialHoliday)) {
			data.setMinAge(specialHoliday.getSpecialLeaveRestriction().getAgeRange().getAgeLowerLimit().v() 
					+ TextResource.localize("KMF004_160"));
			data.setMaxAge(specialHoliday.getSpecialLeaveRestriction().getAgeRange().getAgeHigherLimit().v() 
					+ TextResource.localize("KMF004_160"));
			data.setAgeStandard(
					SpecialHolidayExportDataSource.getAgeBaseYearText(
							specialHoliday.getSpecialLeaveRestriction().getAgeStandard().getAgeCriteriaCls()));
			data.setAgeBaseMonth(specialHoliday.getSpecialLeaveRestriction().getAgeStandard().getAgeBaseDate().getMonth() 
					+ TextResource.localize("KMF004_159"));
			data.setAgeBaseDate(specialHoliday.getSpecialLeaveRestriction().getAgeStandard().getAgeBaseDate().getDay() 
					+ TextResource.localize("KMF004_157"));
		}
		return dataList;
	}
	
	private List<SpecialHolidayExportDataSource> setGrantDateTblAndElapseYear(SpecialHoliday specialHoliday, 
			List<GrantDateTbl> grantDateTblList, 
			ElapseYear elapseYear) {
		List<SpecialHolidayExportDataSource> dataList = new ArrayList<SpecialHolidayExportDataSource>();
		
		grantDateTblList.stream().forEach(grantDateTbl -> {
			SpecialHolidayExportDataSource dataGrantDateTbl = new SpecialHolidayExportDataSource();
			dataList.add(dataGrantDateTbl);
			
			//A7_9 A7_10 A7_11 
			dataGrantDateTbl.setGrantCode(grantDateTbl.getGrantDateCode().v());
			dataGrantDateTbl.setGrantName(grantDateTbl.getGrantDateName().v());
			dataGrantDateTbl.setDefaultTable(grantDateTbl.isSpecified() ? "〇" : "");
			
			//A7_12 A7_13 A7_14 A7_15
			if(elapseYear != null) {
				Comparator<ElapseYearMonthTbl> sortByGrantCnt = 
						(ElapseYearMonthTbl el1, ElapseYearMonthTbl el2) -> Integer.valueOf(el1.getGrantCnt()).compareTo(Integer.valueOf(el2.getGrantCnt()));
				Collections.sort(elapseYear.getElapseYearMonthTblList(), sortByGrantCnt);
				
				for(int i = 0; i < elapseYear.getElapseYearMonthTblList().size(); i++) {
					ElapseYearMonthTbl elapseYearMonthTbl = elapseYear.getElapseYearMonthTblList().get(i);
					Optional<GrantElapseYearMonth> grantElapseYearMonth = grantDateTbl.getElapseYear().stream()
							.filter(grantElapseYear -> grantElapseYear.getElapseNo() == elapseYearMonthTbl.getGrantCnt())
							.findAny();
					
					if(i == 0) {
						dataGrantDateTbl.setGrantSetting(String.valueOf(elapseYearMonthTbl.getGrantCnt()));
						dataGrantDateTbl.setGrantYears(elapseYearMonthTbl.getElapseYearMonth().getYear() 
								+ TextResource.localize("KMF004_156"));
						dataGrantDateTbl.setGrantMonths(elapseYearMonthTbl.getElapseYearMonth().getMonth() 
								+ TextResource.localize("KMF004_158"));
						dataGrantDateTbl.setMaxDays(grantElapseYearMonth.isPresent() ?
								grantElapseYearMonth.get().getGrantedDays().v() + TextResource.localize("KMF004_157") :
									"");
					} else {
						SpecialHolidayExportDataSource dataElapseYearMonthTbl = new SpecialHolidayExportDataSource();

						dataElapseYearMonthTbl.setGrantSetting(String.valueOf(elapseYearMonthTbl.getGrantCnt()));
						dataElapseYearMonthTbl.setGrantYears(elapseYearMonthTbl.getElapseYearMonth().getYear() 
								+ TextResource.localize("KMF004_156"));
						dataElapseYearMonthTbl.setGrantMonths(elapseYearMonthTbl.getElapseYearMonth().getMonth() 
								+ TextResource.localize("KMF004_158"));
						dataElapseYearMonthTbl.setMaxDays(grantElapseYearMonth.isPresent() ?
								grantElapseYearMonth.get().getGrantedDays().v() + TextResource.localize("KMF004_157") :
									"");
						dataList.add(dataElapseYearMonthTbl);
					}
				}
			}

			if(!dataGrantDateTbl.getGrantCode().isEmpty() && elapseYear != null) {
				//A7_16
				dataGrantDateTbl.setGrantFixedDaysEachYear(elapseYear.isFixedAssign() ? "〇" : "");
				
				//A7_40 A7_41 A7_17
				//※4.2.1
				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.REFER_GRANT_DATE_TBL) {
					if(elapseYear.isFixedAssign()) {
						dataGrantDateTbl.setGrantCycleYear(elapseYear.getGrantCycleAfterTbl().get().getElapseYearMonth().getYear() 
								+ TextResource.localize("KMF004_156"));
						dataGrantDateTbl.setGrantCycleMonth(elapseYear.getGrantCycleAfterTbl().get().getElapseYearMonth().getMonth() 
								+ TextResource.localize("KMF004_158"));
						dataGrantDateTbl.setFixedGrantDays(grantDateTbl.getGrantedDays().get().v() 
								+ TextResource.localize("KMF004_157"));
					}
				}
			}
		});
		return dataList;
	}
	
	//※1
	public static String joinTargetItem(List<Integer> frameNoList, List<Integer> absenceFrameNoList) {
		Collections.sort(frameNoList);
		Collections.sort(absenceFrameNoList);
		String targetItem = "";
		String frameNoString = frameNoList.stream().map(frameNo -> "特別休暇枠" + frameNo).collect(Collectors.joining(", "));
		String absenceFrameString = absenceFrameNoList.stream().map(absenceFrameNo -> "欠勤枠" + absenceFrameNo).collect(Collectors.joining(", "));
		if(frameNoString.length() > 0) {
			targetItem += frameNoString;
			if(absenceFrameString.length() > 0) {
				targetItem += ", " + absenceFrameString;
			}
		} else {
			if(absenceFrameString.length() > 0) {
				targetItem += absenceFrameString;
			}
		}
		return targetItem;
	}
	
	//※2
	public static String joinTargetEmployment(List<Employment> empList) {
		Comparator<Employment> compareByCodes = 
				(Employment emp1, Employment emp2) -> emp1.getEmploymentCode().compareTo(emp2.getEmploymentCode());
		Collections.sort(empList, compareByCodes);
		String targetEmployment = "";
		targetEmployment += empList.stream()
				.map(emp -> emp.getEmploymentCode().v() + emp.getEmploymentName().v()).collect(Collectors.joining(", "));
		return targetEmployment;
	}
	
	//※3
	public static String joinTargetType(List<Classification> clsList) {
		Comparator<Classification> compareByCodes = 
				(Classification cls1, Classification cls2) -> cls1.getClassificationCode().compareTo(cls2.getClassificationCode());
		Collections.sort(clsList, compareByCodes);
		String targetType = "";
		targetType += clsList.stream()
				.map(cls -> cls.getClassificationCode().v() + cls.getClassificationName().v()).collect(Collectors.joining(", "));
		return targetType;
	}
	
	//※4.1 -> ※6.3
	public static boolean visibleCondition(String condition, SpecialHoliday specialHoliday) {
		switch(condition) {
			case "4.1":
				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.REFER_GRANT_DATE_TBL) {
					return false;
				} else {
					return true;
				}
			case "4.2":
				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.REFER_GRANT_DATE_TBL) {
					return true;
				} else {
					return false;
				}
//			case "4.2.1":
//				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.REFER_GRANT_DATE_TBL) {
//					return true;
//				} else {
//					return elapseYear.isFixedAssign();
//				}
			case "4.3":
				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.REFER_GRANT_DATE_TBL) {
					return true;
				} else if (specialHoliday.getGrantRegular().getTypeTime() == TypeTime.GRANT_PERIOD) {
					return false;
				} else {
					return specialHoliday.getGrantRegular().getGrantDate().isPresent();
				}
			case "4.4":
				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.GRANT_SPECIFY_DATE) {
					return specialHoliday.getGrantRegular().getFixGrantDate().isPresent() ? 
							specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().isPresent() : false;
				} else {
					return false;
				}
			case "5.1":
				if(specialHoliday.getSpecialLeaveRestriction().getGenderRest() == UseAtr.NOT_USE) {
					return false;
				} else {
					return true;
				}
			case "5.2":
				if(specialHoliday.getSpecialLeaveRestriction().getRestEmp() == UseAtr.NOT_USE) {
					return false;
				} else {
					return true;
				}
			case "5.3":
				if(specialHoliday.getSpecialLeaveRestriction().getRestrictionCls() == UseAtr.NOT_USE) {
					return false;
				} else {
					return true;
				}
			case "5.4":
				if(specialHoliday.getSpecialLeaveRestriction().getAgeLimit() == UseAtr.NOT_USE) {
					return false;
				} else {
					return true;
				}
			case "6.1":
				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.GRANT_PERIOD) {
					return false;
				} else {
					return true;
				}
			case "6.2":
				if(specialHoliday.getGrantRegular().getGrantPeriodic().isPresent()) {
					GrantDeadline grantDeadline = specialHoliday.getGrantRegular().getGrantPeriodic().get().getGrantDeadline();
					if(grantDeadline.getTimeSpecifyMethod() == TimeLimitSpecification.AVAILABLE_GRANT_DATE_DESIGNATE) {
						return true;
					} else {
						return false;
					}
				} else if(specialHoliday.getGrantRegular().getFixGrantDate().isPresent()) {
					GrantDeadline grantDeadline = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic();
					if(grantDeadline.getTimeSpecifyMethod() == TimeLimitSpecification.AVAILABLE_GRANT_DATE_DESIGNATE) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			case "6.3":
				if(specialHoliday.getGrantRegular().getTypeTime() == TypeTime.GRANT_PERIOD) {
					return true;
				} else {
					return false;
				}
			default:
				return false;
		}
	}

	//	並び順
	@Override
	public int compareTo(SpecialHolidayExportDataSource other) {
		int compareCode = 0;
		int compareGrantCode = 0;
		int compareGrantSetting = 0;
		if(this.code != null){
			compareCode = new Integer(this.code).compareTo(new Integer(other.getCode()));
		} else {
			return 0;
		}
		if(compareCode != 0) {
			return compareCode;
		} else if(this.grantCode != null){
			compareGrantCode = new Integer(this.grantCode).compareTo(new Integer(other.getGrantCode()));
		} else {
			return 0;
		}
		if(compareGrantCode != 0) {
			return compareGrantCode;
		} else if(this.grantSetting != null){
			compareGrantSetting = new Integer(this.grantSetting).compareTo(new Integer(other.getGrantSetting()));
		} else {
			return 0;
		}
		return compareGrantSetting;
	}
	
	/**
	 * 付与するタイミングの種類
	 */
	public static String getTypeTimeText(TypeTime typeTime) {
		switch (typeTime){
	    	case REFER_GRANT_DATE_TBL:
	    		return TextResource.localize("KMF004_173");
	    	case GRANT_SPECIFY_DATE:
	    		return TextResource.localize("KMF004_171");
	    	case GRANT_PERIOD:
	    		return TextResource.localize("KMF004_172");
	    	default: 
	    		return null;
    	}
	}
	
	/**
	 * 年齢基準年区分
	 */
	public static String getAgeBaseYearText(AgeBaseYear ageBaseYear) {
		switch (ageBaseYear){
	    	case THIS_YEAR:
	    		return "当年";
	    	case NEXT_YEAR:
	    		return "翌年";
	    	default: 
	    		return null;
    	}
	}
}
