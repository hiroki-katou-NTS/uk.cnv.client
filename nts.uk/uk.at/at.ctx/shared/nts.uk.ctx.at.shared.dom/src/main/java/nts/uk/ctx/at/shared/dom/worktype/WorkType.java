/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;

/**
 * The Class WorkType.
 */
// 勤務種類
@Getter
@NoArgsConstructor
public class  WorkType extends AggregateRoot implements Cloneable, Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** The company id. */
	/* 会社ID */
	private String companyId;

	/** The work type code. */
	/* 勤務種類コード */
	private WorkTypeCode workTypeCode;

	/** The symbolic name. */
	/* 勤務種類記号名 */
	private WorkTypeSymbolicName symbolicName;

	/** The name. */
	/* 勤務種類名称 */
	private WorkTypeName name;

	/** The abbreviation name. */
	/* 勤務種類略名 */
	private WorkTypeAbbreviationName abbreviationName;

	/** The memo. */
	/* 勤務種類備考 */
	private WorkTypeMemo memo;

	/** The daily work. */
	// 1日の勤務
	private DailyWork dailyWork;

	/** The deprecate. */
	// 廃止区分
	private DeprecateClassification deprecate;

	// 出勤率の計算
	private CalculateMethod calculateMethod;

	/**	勤務種類設定 */
	private List<WorkTypeSet> workTypeSetList;

	/** 表示順 */
	private Integer dispOrder;

	@Override
	public void validate() {
		super.validate();
		if ("000".equals(this.workTypeCode.v())) {
			throw new BusinessException("Msg_385");
		}

		WorkTypeClassification moringType = this.dailyWork.getMorning();
		WorkTypeClassification afternoonType = this.dailyWork.getAfternoon();
		if (this.dailyWork.getWorkTypeUnit() == WorkTypeUnit.MonringAndAfternoon) {
			if ((moringType == afternoonType && moringType != WorkTypeClassification.Absence && moringType != WorkTypeClassification.SpecialHoliday)
					|| (moringType == WorkTypeClassification.Pause && afternoonType == WorkTypeClassification.Shooting)
					|| (moringType == WorkTypeClassification.Shooting && afternoonType == WorkTypeClassification.Pause)) {
				throw new BusinessException("Msg_395");
			}
		}
	}
	
	/**
	 * 出勤系かどうか判断
	 * @return true:出勤系,false:出勤系でない
	 */
	public boolean isWorkingDay() {
		if(dailyWork == null) { return false; }
		if (dailyWork.getWorkTypeUnit() == WorkTypeUnit.OneDay) {
			return isWorkingType(dailyWork.getOneDay());
		}
		return isWorkingType(dailyWork.getMorning()) || isWorkingType(dailyWork.getAfternoon());
	}

	/**
	 * 出勤系分類かどうか判断
	 * @param wt 勤務種類の分類
	 * @return true:出勤系分類,false:出勤系分類でない
	 */
	private boolean isWorkingType(WorkTypeClassification wt) {
		return wt == WorkTypeClassification.Attendance || wt == WorkTypeClassification.Shooting 
				|| wt == WorkTypeClassification.HolidayWork;
	}
	
	/** [6] 計算時に就業時間帯が不要かどうか判断する */
	public boolean isNoneWorkTimeType(){
		if (dailyWork != null && dailyWork.getWorkTypeUnit() == WorkTypeUnit.OneDay) {
			return isNoneWorkTimeType(dailyWork.getOneDay());
		}
		return false;
	}
	
	private boolean isNoneWorkTimeType(WorkTypeClassification wt) {
		return wt == WorkTypeClassification.Holiday || wt == WorkTypeClassification.Pause
				|| wt == WorkTypeClassification.LeaveOfAbsence || wt == WorkTypeClassification.Closure
				|| wt == WorkTypeClassification.ContinuousWork;
	}

	/**
	 * 
	 * @param companyId
	 * @param workTypeCode
	 * @param sortOrder
	 * @param symbolicName
	 * @param name
	 * @param abbreviationName
	 * @param memo
	 * @param displayAtr
	 * @param dailyWork
	 * @param deprecate
	 * @param calculateMethod
	 */
	public WorkType(String companyId, WorkTypeCode workTypeCode, WorkTypeSymbolicName symbolicName, WorkTypeName name,
			WorkTypeAbbreviationName abbreviationName, WorkTypeMemo memo, DailyWork dailyWork,
			DeprecateClassification deprecate, CalculateMethod calculateMethod) {
		super();
		this.companyId = companyId;
		this.workTypeCode = workTypeCode;
		this.symbolicName = symbolicName;
		this.name = name;
		this.abbreviationName = abbreviationName;
		this.memo = memo;
		this.dailyWork = dailyWork;
		this.deprecate = deprecate;
		this.calculateMethod = calculateMethod;
		this.workTypeSetList = new ArrayList<>();
	}
	
	public WorkType(WorkTypeCode workTypeCode, WorkTypeSymbolicName symbolicName, WorkTypeName name,
			WorkTypeAbbreviationName abbreviationName, WorkTypeMemo memo, DailyWork dailyWork) {
		super();
		this.workTypeCode = workTypeCode;
		this.symbolicName = symbolicName;
		this.name = name;
		this.abbreviationName = abbreviationName;
		this.memo = memo;
		this.dailyWork = dailyWork;
		this.workTypeSetList = new ArrayList<>();
	}

	/**
	 * 
	 * @param companyId
	 * @param workTypeCode
	 * @param name
	 * @param abbreviationName
	 */
	public WorkType(String companyId, WorkTypeCode workTypeCode, WorkTypeName name,
			WorkTypeAbbreviationName abbreviationName) {
		super();
		this.companyId = companyId;
		this.workTypeCode = workTypeCode;
		this.name = name;
		this.abbreviationName = abbreviationName;
	}

	/**
	 * Creates the simple from java type.
	 *
	 * @param companyId
	 *            the company id
	 * @param workTypeCode
	 *            the work type code
	 * @param sortOrder
	 *            the sort order
	 * @param symbolicName
	 *            the symbolic name
	 * @param name
	 *            the name
	 * @param abbreviationName
	 *            the abbreviation name
	 * @param memo
	 *            the memo
	 * @param displayAtr
	 *            the display atr
	 * @return the work type
	 */
	public static WorkType createSimpleFromJavaType(String companyId, String workTypeCode, String symbolicName,
			String name, String abbreviationName, String memo, int workTypeUnit, int oneDay, int morning, int afternoon,
			int deprecate, int calculateMethod) {
		DailyWork dailyWork = new DailyWork();
		dailyWork.setWorkTypeUnit(EnumAdaptor.valueOf(workTypeUnit, WorkTypeUnit.class));
		dailyWork.setOneDay(EnumAdaptor.valueOf(oneDay, WorkTypeClassification.class));
		dailyWork.setAfternoon(EnumAdaptor.valueOf(afternoon, WorkTypeClassification.class));
		dailyWork.setMorning(EnumAdaptor.valueOf(morning, WorkTypeClassification.class));
		return new WorkType(companyId, new WorkTypeCode(workTypeCode), new WorkTypeSymbolicName(symbolicName),
				new WorkTypeName(name), new WorkTypeAbbreviationName(abbreviationName), new WorkTypeMemo(memo),
				dailyWork, EnumAdaptor.valueOf(deprecate, DeprecateClassification.class),
				EnumAdaptor.valueOf(calculateMethod, CalculateMethod.class));
	}
	
	/**
	 * 
	 * @param workTypeCode
	 * @param name
	 * @param abbreviationName
	 * @param symbolicName
	 * @param memo
	 * @param workTypeUnit
	 * @param oneDay
	 * @param morning
	 * @param afternoon
	 * @return
	 */
	public static WorkType createSimpleFromJavaType(String workTypeCode, String name, String abbreviationName,
			String symbolicName, String memo, int workTypeUnit, int oneDay, int morning, int afternoon) {
		DailyWork dailyWork = new DailyWork();
		dailyWork.setWorkTypeUnit(EnumAdaptor.valueOf(workTypeUnit, WorkTypeUnit.class));
		dailyWork.setOneDay(EnumAdaptor.valueOf(oneDay, WorkTypeClassification.class));
		dailyWork.setMorning(EnumAdaptor.valueOf(morning, WorkTypeClassification.class));
		dailyWork.setAfternoon(EnumAdaptor.valueOf(afternoon, WorkTypeClassification.class));
		return new WorkType(new WorkTypeCode(workTypeCode), new WorkTypeSymbolicName(symbolicName),
				new WorkTypeName(name), new WorkTypeAbbreviationName(abbreviationName), new WorkTypeMemo(memo),
				dailyWork);
	}

	/**
	 * Set work type set
	 * 
	 * @param workTypeList
	 */
	public void setWorkTypeSet(List<WorkTypeSet> workTypeList) {
		this.workTypeSetList = workTypeList;
	}
	
	
	public AttendanceHolidayAttr getAttendanceHolidayAttr() {
//		return this.dailyWork.getAttendanceHolidayAttr();
		return this.dailyWork.decisionNeedPredTime();
	}
	
	/**
	 * 出勤系か判定する
	 * @return　出勤系である
	 */
	public boolean isWeekDayAttendance() {
		return this.dailyWork.isWeekDayAttendance();
	}

	

	/**
	 * Set display order
	 * 
	 * @param order
	 */
	public void setDisplayOrder(int dispOrder) {
		this.dispOrder = dispOrder;
	}

	/**
	 * Check OneDay Morning Afternoon
	 * 
	 * @return
	 */
	public boolean isOneDay() {
		return this.dailyWork.getWorkTypeUnit() == WorkTypeUnit.OneDay;
	}

	/**
	 * Checks if is attendance or shooting.
	 *
	 * @param worktypeCls the worktype cls
	 * @return true, if is attendance or shooting
	 */
	public boolean isAttendanceOrShooting(WorkTypeClassification worktypeCls) {
		return worktypeCls == WorkTypeClassification.Attendance || worktypeCls == WorkTypeClassification.Shooting;
	}

	/**
	 * Checks if is attendance or shooting or holiday work.
	 *
	 * @param worktypeCls the worktype cls
	 * @return true, if is attendance or shooting or holiday work
	 */
	public boolean isAttendanceOrShootingOrHolidayWork(WorkTypeClassification worktypeCls) {
		return worktypeCls == WorkTypeClassification.Attendance || worktypeCls == WorkTypeClassification.Shooting
				|| worktypeCls == WorkTypeClassification.HolidayWork;
	}

	/**
	 * Gets the work type set by atr.
	 *
	 * @param atr the atr
	 * @return the work type set by atr
	 */
	public Optional<WorkTypeSet> getWorkTypeSetByAtr(WorkAtr atr) {
		return this.getWorkTypeSetList().stream().filter(item -> item.getWorkAtr() == atr).findFirst();
	}
	
	public WorkTypeSet getWorkTypeSetAvailable() {
		return this.getWorkTypeSetList().stream().filter(item -> item.getWorkAtr() == WorkAtr.OneDay 
				|| item.getWorkAtr() == WorkAtr.Afternoon  
				|| item.getWorkAtr() == WorkAtr.Monring).findFirst().get();
	}

	/**
	 * Gets the work type set.
	 *
	 * @return the work type set
	 */
	//勤務種類設定を取得する
	public WorkTypeSet getWorkTypeSet() {
		// 1日
		if (this.isOneDay()) {
			if (this.isAttendanceOrShootingOrHolidayWork(this.dailyWork.getOneDay())) {
				return this.getWorkTypeSetByAtr(WorkAtr.OneDay).orElse(null);
			} else {
				return null;
			}
		}
		// 午前と午後
		else {
			if (this.isAttendanceOrShooting(this.dailyWork.getMorning())) {
				WorkTypeSet am = this.getWorkTypeSetByAtr(WorkAtr.Monring).orElse(null);
				if (this.isAttendanceOrShooting(this.dailyWork.getAfternoon())) {
					WorkTypeSet pm = this.getWorkTypeSetByAtr(WorkAtr.Afternoon).orElse(null);
					return new WorkTypeSet(pm.getTimeLeaveWork(), am.getAttendanceTime());
				}
				return am;
			} else {
				if (this.isAttendanceOrShooting(this.dailyWork.getAfternoon())) {
					return this.getWorkTypeSetByAtr(WorkAtr.Afternoon).orElse(null);
				} else {
					return null;
				}
			}
		}
	}
	
	/**
	 * 廃止区分.廃止する
	 * @return
	 */
	public boolean isDeprecated() {
		return DeprecateClassification.Deprecated == this.deprecate;
	}
	
	/**
	 * 勤務種類設定の編集(一時的なSetter)
	 * @param workTypeSet
	 */
	public void addWorkTypeSet(WorkTypeSet workTypeSet) {
		if(this.workTypeSetList == null || this.workTypeSetList.isEmpty()) {
			List<WorkTypeSet> addItems = new ArrayList<>();
			addItems.add(workTypeSet);
			this.workTypeSetList = addItems;
		}
		else {
			this.workTypeSetList.add(workTypeSet);
		}
		
							
	}
	

	public boolean getDecisionAttendanceHolidayAttr() {
		return this.dailyWork.decisionNeedPredTime().isHoliday();
	}
	
	public Optional<HolidayAtr> getHolidayAtr() {
		if(this.getDailyWork().getWorkTypeUnit().isOneDay()) {
			return this.getWorkTypeSetList().stream().filter(tc -> tc.getWorkAtr().isOneDay()).map(ts ->ts.getHolidayAtr()).findFirst();
		}
		else {
			return this.getWorkTypeSetList().stream().filter(tc -> tc.getWorkAtr().isAfterNoon()).map(ts ->ts.getHolidayAtr()).findFirst();
		}
	}
	
	
	public HolidayAtr beforeDay(){
		switch(this.getDailyWork().getWorkTypeUnit()) {
		case OneDay:
			return this.getWorkTypeSetList().stream().filter(tc -> tc.getWorkAtr().isOneDay()).map(ts -> ts.getHolidayAtr()).findFirst().get();
		case MonringAndAfternoon:
			return this.getWorkTypeSetList().stream().filter(tc -> tc.getWorkAtr().isMorning()).map(ts -> ts.getHolidayAtr()).findFirst().get();
		default:
			throw new RuntimeException("uknown WorkTypeUnit:"+this.getDailyWork().getWorkTypeUnit());
		}
	}
	
	public HolidayAtr afterDay() {
		switch(this.getDailyWork().getWorkTypeUnit()) {
		case OneDay:
			return this.getWorkTypeSetList().stream().filter(tc -> tc.getWorkAtr().isOneDay()).map(ts -> ts.getHolidayAtr()).findFirst().get();
		case MonringAndAfternoon:
			return this.getWorkTypeSetList().stream().filter(tc -> tc.getWorkAtr().isAfterNoon()).map(ts -> ts.getHolidayAtr()).findFirst().get();
		default:
			throw new RuntimeException("uknown WorkTypeUnit:"+this.getDailyWork().getWorkTypeUnit());
		}	
	}
	
	/**
	 * 振替を行うかチェックする
	 * @return　振替をする
	 */
	public boolean  isGenSubHolidayForHolidayWork() {
		List<WorkTypeSet> workTypeSetList = getGenSubHolidayForHolidayWork();
		if(workTypeSetList.size()>1 || workTypeSetList.size() == 0) return false;
		return workTypeSetList.stream().findFirst().get().getGenSubHodiday().isCheck();
	}
	
	private List<WorkTypeSet> getGenSubHolidayForHolidayWork() {
		switch(this.dailyWork.getWorkTypeUnit()) {
		case OneDay:
			return this.workTypeSetList.stream().filter(ts -> ts.getWorkAtr().equals(WorkAtr.OneDay)).collect(Collectors.toList());
		case MonringAndAfternoon:
			return this.workTypeSetList.stream().filter(ts -> !ts.getWorkAtr().equals(WorkAtr.OneDay)).collect(Collectors.toList());
		default:
			return new ArrayList<>();
		}
	}
	
	/**
	 * create this Instance
	 * @return new Instance
	 */
	public WorkType clone() {
		WorkType cloned = new WorkType();
		try {
			cloned.companyId = this.companyId;
			cloned.workTypeCode = new WorkTypeCode(this.workTypeCode.v());
			cloned.symbolicName = new WorkTypeSymbolicName(this.symbolicName.v());
			cloned.name = new WorkTypeName(this.name.v());
			cloned.abbreviationName = new WorkTypeAbbreviationName(this.abbreviationName.v());
			cloned.memo = new WorkTypeMemo(this.memo.v());
			cloned.dailyWork = this.dailyWork.clone();
			cloned.deprecate = DeprecateClassification.valueOf(this.deprecate.value);
			cloned.calculateMethod = CalculateMethod.valueOf(this.calculateMethod.value);
			cloned.workTypeSetList = this.workTypeSetList.stream().map(c -> c.clone()).collect(Collectors.toList());
			if(this.dispOrder != null)
				cloned.dispOrder = new Integer(this.dispOrder);
		}
		catch (Exception e){
			throw new RuntimeException("WorkType clone error.");
		}
		return cloned;
	}
	
	/**
	 * Web終業時刻計算対象の判断
	 * @return true:対象,false:対象外
	 */
	public boolean isCalcTargetForEndClock() {
		if (this.workTypeCode.v().compareTo("002") == 0) return false;	// 半出＋プレミアム
		if (this.workTypeCode.v().compareTo("105") == 0) return false;	// プレミアムデー
		return true;
	}
	
	/**
	 * 1日半日出勤・1日休日系の判定（休出判定あり）
	 * @return 出勤日区分
	 */
	public AttendanceDayAttr chechAttendanceDay() {
		return this.dailyWork.chechAttendanceDay();
	}
	
	/**
	 * 1日半日出勤・1日休日系の判定
	 * @param workTypeCode
	 * @return
	 */
	public WorkStyle checkWorkDay() {
		// All day
		if (this.isOneDay()) {
			if (this.dailyWork.IsLeaveForADay()) {
				return WorkStyle.ONE_DAY_REST;
			}

			return WorkStyle.ONE_DAY_WORK;
		}

		// Half day
		if (this.dailyWork.IsLeaveForMorning()) {
			if (dailyWork.IsLeaveForAfternoon()) {
				return WorkStyle.ONE_DAY_REST;
			}

			return WorkStyle.AFTERNOON_WORK;
		}

		if (this.dailyWork.IsLeaveForAfternoon()) {
			return WorkStyle.MORNING_WORK;
		}

		return WorkStyle.ONE_DAY_WORK;
	}

	public WorkType(String companyId, WorkTypeCode workTypeCode, List<WorkTypeSet> workTypeSetList) {
		super();
		this.companyId = companyId;
		this.workTypeCode = workTypeCode;
		this.workTypeSetList = workTypeSetList;
	}

	public void setDeprecate(DeprecateClassification deprecate) {
		this.deprecate = deprecate;
	}
	
	/** 時間消化休暇日数を取得する */
	public double calcTimeConsumpVacationDays () {
		
		if (dailyWork.getWorkTypeUnit() == WorkTypeUnit.OneDay) {
			if (dailyWork.getOneDay() == WorkTypeClassification.TimeDigestVacation) {
				return 1;
			}
		} else {
			if (dailyWork.getMorning() == WorkTypeClassification.TimeDigestVacation) {
				return 0.5;
			}
		
			if (dailyWork.getAfternoon() == WorkTypeClassification.TimeDigestVacation) {
				return 0.5;
			}
		}
		
		return 0;
	}

	public void setWorkTypeCode(WorkTypeCode workTypeCode) {
		this.workTypeCode = workTypeCode;
	}

	public void setDailyWork(DailyWork dailyWork) {
		this.dailyWork = dailyWork;
	}
	
	/**
	 * 休日出勤かどうか判断する
	 * @return true=休出,false=休出ではない
	 */
	public boolean isHolidayWork(){
		if (this.isOneDay()){
			if (this.dailyWork.getOneDay() == WorkTypeClassification.HolidayWork) return true;
		}
		return false;
	}
	public boolean isHoliday(){
		if (this.isOneDay()){
			if (this.dailyWork.getOneDay() == WorkTypeClassification.Holiday) return true;
		}
		return false;
	}
	/** 連続勤務か */
	public boolean isContinuousWork() {
		if (this.isOneDay() && this.dailyWork.getOneDay() == WorkTypeClassification.ContinuousWork) 
			return true;
			
		return false;
	}
	
	/** 特別休暇の1日午前午後区分を取得 */
	public Optional<WorkAtr> getWorkAtrForSpecialHoliday(int spcNo) {
		
		/** if @1日の勤務.勤務区分 = １日 and @1日の勤務.1日 = 特別休暇	*/
		if (this.dailyWork.isOneDay() && this.dailyWork.getOneDay() == WorkTypeClassification.SpecialHoliday) {
			
			/** $1日 = @勤務種類設定 filter ($.1日午前午後区分 = 1日 and $.特別休暇枠 = 特別休暇枠NO) */		
			val oneDay = this.workTypeSetList.stream().filter(c -> c.getWorkAtr().isOneDay() 
					&& c.getSumSpHodidayNo() == spcNo).findFirst();
			
			/** return $1日.isPresent() ? 1日午前午後区分.１日 : Optional.empty */
			return oneDay.isPresent() ? Optional.of(WorkAtr.OneDay) : Optional.empty();
		}
		
		/** if @1日の勤務.午前 = 特別休暇 */				
		if (this.dailyWork.getMorning() == WorkTypeClassification.SpecialHoliday) {
			/** $午前 = @勤務種類設定 filter ($.1日午前午後区分 = 午前 and $.特別休暇枠 = 特別休暇枠NO) */	
			val morning = this.workTypeSetList.stream().filter(c -> c.getWorkAtr().isMorning() 
					&& c.getSumSpHodidayNo() == spcNo).findFirst();
			
			/** return $午前.isPresent() ? 1日午前午後区分.午前 : Optional.empty */
			return morning.isPresent() ? Optional.of(WorkAtr.Monring) : Optional.empty();
		}
		
		/** if @1日の勤務.午後 = 特別休暇 */
		if (this.dailyWork.getAfternoon() == WorkTypeClassification.SpecialHoliday) {
			/** $午後 = @勤務種類設定 filter ($.1日午前午後区分 = 午後 and $.特別休暇枠 = 特別休暇枠NO) */
			val afternoon = this.workTypeSetList.stream().filter(c -> c.getWorkAtr().isAfterNoon() 
					&& c.getSumSpHodidayNo() == spcNo).findFirst();
			
			/** return $午後.isPresent() ? 1日午前午後区分.午後 : Optional.empty */
			return afternoon.isPresent() ? Optional.of(WorkAtr.Afternoon) : Optional.empty();
		}
		return Optional.empty();
	}
	
	/** 欠勤の1日午前午後区分を取得 */
	public Optional<WorkAtr> getWorkAtrForAbsenceDay() {
		
		/** if @1日の勤務.勤務区分 = １日 and @1日の勤務.1日 = 欠勤	*/
		if (this.dailyWork.isOneDay() && this.dailyWork.getOneDay() == WorkTypeClassification.Absence) {
			
			/** return 1日午前午後区分.1日 */
			return Optional.of(WorkAtr.OneDay);
		}
		
		/** if @1日の勤務.勤務区分 = 午前と午後 and @1日の勤務.午前 = 欠勤 */				
		if (this.dailyWork.getMorning() == WorkTypeClassification.Absence) {

			/** return 1日午前午後区分.午前 */
			return Optional.of(WorkAtr.Monring);
		}
		
		/** if @1日の勤務.勤務区分 = 午前と午後 and @1日の勤務.午後 = 欠勤 */
		if (this.dailyWork.getAfternoon() == WorkTypeClassification.Absence) {
			
			/** 1日午前午後区分.午後 */
			return Optional.of(WorkAtr.Afternoon);
		}
		
		return Optional.empty();
	}
	
	/** [5] 指定の分類の1日午前午後区分を取得 */
	public Optional<WorkAtr> getWorkAtrForWorkTypeClassification(WorkTypeClassification clas) {
		
		/** if @1日の勤務.勤務区分 = １日 and @1日の勤務.1日 = 勤務種類の分類 */
		if (isOneDay() && dailyWork.getOneDay() == clas)
			return Optional.of(WorkAtr.OneDay);
		
		/** if @1日の勤務.勤務区分 = 午前と午後 */
		if (!isOneDay()) {
			
			/** if @1日の勤務.午前 = 勤務種類の分類 and @1日の勤務.午後 = 勤務種類の分類 */
			if (dailyWork.getAfternoon() == clas && dailyWork.getMorning() == clas)
				return Optional.of(WorkAtr.OneDay);
				
			/** if @1日の勤務.午前 = 勤務種類の分類 */
			if (dailyWork.getMorning() == clas)
				return Optional.of(WorkAtr.Monring);
			
			/** if @1日の勤務.午後 = 勤務種類の分類 */
			if (dailyWork.getAfternoon() == clas)
				return Optional.of(WorkAtr.Afternoon);
		}
		
		return Optional.empty();
	}
	
	//[10] 代休が発生する勤務種類かどうか判断する
	public boolean isSubstituteHolidayOccurs() {
		WorkStyle style = this.checkWorkDay();
		if(style.equals(WorkStyle.ONE_DAY_REST))
			return false;
		
		if(this.isHolidayWork())
			return this.getWorkTypeSet().getGenSubHodiday().isCheck();
		
		return true;
	}

	/**
	 * 出勤時刻自動セットであるか
	 * @return
	 */
	public boolean isAttendanceTimeAutoSet() {
		return this.workTypeSetList.stream().anyMatch( 
				workTimeSetting -> workTimeSetting.getAttendanceTime() == WorkTypeSetCheck.CHECK);
	}
	
	/**
	 * 退勤時刻自動セットであるか
	 * @return
	 */
	public boolean isLeaveTimeAutoSet() {
		return this.workTypeSetList.stream().anyMatch(
				workTimeSetting -> workTimeSetting.getTimeLeaveWork() == WorkTypeSetCheck.CHECK);
	}
	
}
