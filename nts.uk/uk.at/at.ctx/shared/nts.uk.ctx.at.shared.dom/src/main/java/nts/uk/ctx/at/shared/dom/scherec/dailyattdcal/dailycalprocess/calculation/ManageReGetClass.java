package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.DailyTime;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.AddSetting;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.DailyUnit;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.statutoryworktime.DailyCalculationPersonalInformation;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.*;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkCalcSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.CoreTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.OutingCalcWithinCoreTime;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 時間帯作成、時間計算で再取得が必要になっているクラスたちの管理クラス
 * @author keisuke_hoshina
 *
 */
@Getter
public class ManageReGetClass {
	
	/** 1日の範囲 */
	private CalculationRangeOfOneDay calculationRangeOfOneDay;
	
	/** 日別実績(WORK) */
	@Setter
	private IntegrationOfDaily integrationOfDaily;
	
	/** 勤務種類 */
	@Setter
	private Optional<WorkType> workType;
	
	/** 会社別設定管理 */
	private ManagePerCompanySet companyCommonSetting;
	
	/** 社員設定管理 */
	private ManagePerPersonDailySet personDailySetting;
	
	/** 統合就業時間帯 */
	private Optional<IntegrationOfWorkTime> integrationOfWorkTime;
	
	/**
	 * Constructor
	 * @param calculationRangeOfOneDay 1日の計算範囲
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param workType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 */
	public ManageReGetClass(
			CalculationRangeOfOneDay calculationRangeOfOneDay,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			Optional<WorkType> workType,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		super();
		this.calculationRangeOfOneDay = calculationRangeOfOneDay;
		this.companyCommonSetting = companyCommonSetting;
		this.personDailySetting = personDailySetting;
		this.workType = workType;
		this.integrationOfWorkTime = integrationOfWorkTime;
		this.integrationOfDaily = integrationOfDaily;
	}
	
	/**
	 * 就業時間帯の設定を取得する
	 * @return 就業時間帯の設定
	 */
	public Optional<WorkTimeSetting> getWorkTimeSetting() {
		if(!this.integrationOfWorkTime.isPresent())
			return Optional.empty();
		
		return Optional.of(this.integrationOfWorkTime.get().getWorkTimeSetting());
	}
	/**
	 * 就業時間帯別代休時間設定(List)を取得する
	 * @return 就業時間帯別代休時間設定(List)
	 */
	public List<WorkTimezoneOtherSubHolTimeSet> getSubHolTransferSetList() {
		if(!this.integrationOfWorkTime.isPresent())
			return Collections.emptyList();
		
		return this.integrationOfWorkTime.get().getCommonSetting().getSubHolTimeSet();
	}
	
	/**
	 * 日別計算用の個人情報を取得する
	 * @return 日別計算用の個人情報
	 */
	public DailyCalculationPersonalInformation getPersonalInfo() {
		return new DailyCalculationPersonalInformation(
				Optional.of(new DailyTime(this.personDailySetting.getPredetermineTimeSetByPersonWeekDay().getAdditionSet().getPredTime().getOneDay().valueAsMinutes())),
				new DailyTime(this.personDailySetting.getDailyUnit().getDailyTime().valueAsMinutes()),
				this.personDailySetting.getPersonInfo().getLaborSystem());
	}
	
	/**
	 * 法定労働時間を取得する
	 * @return 法定労働時間
	 */
	public DailyUnit getDailyUnit() {
		return this.personDailySetting.getDailyUnit();
	}
	
	/**
	 * 固定勤務の休憩時間帯を取得する
	 * @return 固定勤務の休憩時間帯
	 */
	public Optional<TimezoneOfFixedRestTimeSet> getFixRestTimeSetting() {
		if(!this.integrationOfWorkTime.isPresent() || !this.integrationOfWorkTime.get().getFixedWorkSetting().isPresent())
			return Optional.empty();
		
		if(this.integrationOfWorkTime.get().getFixedWorkSetting().get().getLstHalfDayWorkTimezone().isEmpty())
			return Optional.empty();
		
		return Optional.of(this.integrationOfWorkTime.get().getFixedWorkSetting().get().getLstHalfDayWorkTimezone().get(0).getRestTimezone());
	}
	
	/**
	 * 就業時間の時間帯設定(List)を取得する
	 * @return 就業時間の時間帯設定
	 */
	public List<EmTimeZoneSet> getFixWoSetting() {
		if(!this.workType.isPresent() || !this.integrationOfWorkTime.isPresent())
			return Collections.emptyList();
		
		return this.integrationOfWorkTime.get().getEmTimeZoneSetList(this.workType.get());
	}
	
	/**
	 * 固定勤務の計算設定を取得する
	 * @return 固定勤務の計算設定
	 */
	public Optional<FixedWorkCalcSetting> getOotsukaFixedWorkSet() {
		if(!this.integrationOfWorkTime.isPresent() || !this.integrationOfWorkTime.get().getFixedWorkSetting().isPresent())
			return Optional.empty();
			
		return this.integrationOfWorkTime.get().getFixedWorkSetting().get().getCalculationSetting();
	}
	
	/**
	 * 休暇の計算方法の設定を取得する
	 * @return 休暇の計算方法の設定
	 */
	public HolidayCalcMethodSet getHolidayCalcMethodSet() {
		return this.personDailySetting.getAddSetting().getVacationCalcMethodSet();
	}
	
	/**
	 * 計算処理に入ることができるかフラグ
	 * (造語)
	 * 計算処理に入る場合にのみManageReGetClassを作成する為、常に計算するを返す
	 * @return true
	 */
	public boolean getCalculatable() {
		return true;
	}
	
	/**
	 * 休憩回数を取得する
	 * @return 休憩回数
	 */
	public int getBreakCount() {
		//常に実績から取得する
		
		return integrationOfDaily.getBreakTime().getBreakTimeSheets().stream()
					.filter(timeSheet -> (timeSheet.getStartTime() != null && timeSheet.getEndTime() != null 
							&& timeSheet.getEndTime().greaterThan(timeSheet.getStartTime())))
					.collect(Collectors.toList()).size();
//		Optional<BreakTimeOfDailyAttd> record = integrationOfDaily.getBreakTime().stream()
//				.filter(dailyPerformance -> dailyPerformance.getBreakType().equals(BreakType.REFER_WORK_TIME))
//				.findFirst();
//		if(!record.isPresent()) return 0;
//		
//		return record.get().getBreakTimeSheets().stream()
//				.filter(timeSheet -> (timeSheet.getStartTime() != null && timeSheet.getEndTime() != null && timeSheet.getEndTime().greaterThan(timeSheet.getStartTime())))
//				.collect(Collectors.toList()).size();
	}
	
	/**
	 * コアタイム時間帯設定を取得する
	 * (出勤系ではない場合は最低勤務時間を0：00にする)
	 * @return コアタイム時間帯設定
	 */
	public Optional<CoreTimeSetting> getCoreTimeSetting() {
		if (!this.integrationOfWorkTime.isPresent()) return Optional.empty();
		return this.integrationOfWorkTime.get().getCoreTimeSettingForCalc(this.workType);
	}
	
	/**
	 * 休暇加算時間設定を取得する
	 * @return 休暇加算時間設定
	 */
	public Optional<HolidayAddtionSet> getHolidayAddtionSet(){
		return this.companyCommonSetting.getHolidayAdditionPerCompany();
	}
	
	/**
	 * 就業時間帯の共通設定を取得する
	 * @return 就業時間帯の共通設定
	 */
	public Optional<WorkTimezoneCommonSet> getWorkTimezoneCommonSet() {
		if(!this.integrationOfWorkTime.isPresent())
			return Optional.empty();
		
		return Optional.of(this.integrationOfWorkTime.get().getCommonSetting());
	}
	
	/**
	 * 法定内残業枠No(List)を取得する
	 * @return 法定内残業枠No(List)
	 */
	public List<OverTimeFrameNo> getStatutoryFrameNoList() {
		if(!this.workType.isPresent() || !this.integrationOfWorkTime.isPresent())
			return Collections.emptyList();

		return this.integrationOfWorkTime.get().getLegalOverTimeFrameNoList(this.workType.get());
	}
	
	/**
	 * フレックス計算設定を取得する
	 * @return フレックス計算設定
	 */
	public Optional<OutingCalcWithinCoreTime> getGoOutCalc() {
		if(!this.integrationOfWorkTime.isPresent() || !this.integrationOfWorkTime.get().getFlexWorkSetting().isPresent())
			return Optional.empty();
		
		return Optional.of(this.integrationOfWorkTime.get().getFlexWorkSetting().get().getCoreTimeSetting().getGoOutCalc());
	}
	
	/**
	 * 残業時間の時間帯設定(List)を取得する
	 * @return 残業時間の時間帯設定(List)
	 */
	public List<OverTimeOfTimeZoneSet> getOverTimeSheetSetting() {
		if(!this.workType.isPresent() || !this.integrationOfWorkTime.isPresent())
			return Collections.emptyList();
		
		return this.integrationOfWorkTime.get().getOverTimeOfTimeZoneSetList(this.workType.get());
	}
	
	/**
	 * 計算用所定時間設定を取得する
	 * @return 計算用所定時間設定
	 */
	public Optional<PredetermineTimeSetForCalc> getPredSetForOOtsuka() {
		return Optional.of(this.calculationRangeOfOneDay.getPredetermineTimeSetForCalc());
	}

	/**
	 * 加算設定を取得する
	 * @return 自身の労働制の加算設定
	 */
	public AddSetting getAddSetting() {
		return this.personDailySetting.getAddSetting();
	}
	
	/**
	 * 重複する時間帯で作り直す
	 * @param timeSpan 時間帯
	 * @param commonSet 就業時間帯の共通設定
	 * @return ManageReGetClass
	 */
	public Optional<ManageReGetClass> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan) {
		Optional<CalculationRangeOfOneDay> duplicate = this.calculationRangeOfOneDay.recreateWithDuplicate(timeSpan, this.getWorkTimezoneCommonSet());
		if(!duplicate.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new ManageReGetClass(
				duplicate.get(),
				this.companyCommonSetting,
				this.personDailySetting,
				this.workType,
				this.integrationOfWorkTime,
				this.integrationOfDaily));
	}
}
