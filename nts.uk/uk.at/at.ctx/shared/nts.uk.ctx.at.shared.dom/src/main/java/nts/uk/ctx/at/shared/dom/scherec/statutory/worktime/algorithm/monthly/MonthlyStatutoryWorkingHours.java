package nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly;

import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.common.MonthlyEstimateTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.DailyStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet.LaborWorkTypeAttr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;

public class MonthlyStatutoryWorkingHours {
	
	public static Optional<MonAndWeekStatutoryTime> monAndWeekStatutoryTime(RequireM4 require, CacheCarrier cacheCarrier,
			  String cid, String employmentCd, String employeeId,  GeneralDate baseDate, YearMonth ym, WorkingSystem workingSystem) {
		//enum<労働制> = フレックス勤務、計算対象外　の場合
		if (workingSystem.isFlexTimeWork() || workingSystem.isExcludedWorkingCalculate()) {
			return Optional.empty();
		}
		//週の時間を取得
		MonthlyEstimateTime weeklyTime = getWeeklylyEstimateTime(require, cacheCarrier, cid, 
																employmentCd, employeeId, baseDate, workingSystem);
		//月の時間を取得
		MonthlyEstimateTime monthlyTime = getMonthlyEstimateTime(require, cacheCarrier, cid,
																employmentCd, employeeId, baseDate, ym, workingSystem);
		
		return Optional.of(new MonAndWeekStatutoryTime(weeklyTime,monthlyTime));
	}
	
	public static MonthlyFlexStatutoryLaborTime flexMonAndWeekStatutoryTime(RequireM1 require, CacheCarrier cacheCarrier, 
			String cid, String employmentCd, String employeeId, GeneralDate baseDate, YearMonth ym) {
		
		//取得処理
		return flexMonthlyUnit(require, cacheCarrier, cid, employmentCd, employeeId, baseDate, ym)
				.map(flexMonthUnit -> new MonthlyFlexStatutoryLaborTime(flexMonthUnit.getLaborTime().getLegalLaborTime(), 
																		flexMonthUnit.getLaborTime().getWithinLaborTime()
																			.orElseGet(() -> new MonthlyEstimateTime(0)), 
																		flexMonthUnit.getLaborTime().getWeekAvgTime()
																			.orElseGet(() -> new MonthlyEstimateTime(0))))
				.orElseGet(() -> MonthlyFlexStatutoryLaborTime.zeroMonthlyFlexStatutoryLaborTime());
	}
	
	/**
	 * 週の時間を取得
	 * @return
	 */
	private static MonthlyEstimateTime getWeeklylyEstimateTime(RequireM3 require, CacheCarrier cacheCarrier, String cid, 
			String employmentCd, String employeeId, GeneralDate baseDate, WorkingSystem workingSystem) {
		return DailyStatutoryLaborTime.getWorkingTimeSetting(require, cacheCarrier, 
				cid, employmentCd, employeeId, baseDate, workingSystem)
								.map(weeklyTime -> new MonthlyEstimateTime(weeklyTime.getWeeklyTime().getTime().valueAsMinutes()))
								.orElseGet(() -> new MonthlyEstimateTime(0));
	}
	
	
	/**
	 * 月の時間を取得
	 * @return
	 */
	private static MonthlyEstimateTime getMonthlyEstimateTime(RequireM2 require, CacheCarrier cacheCarrier, String cid, 
			String employmentCd, String employeeId, GeneralDate baseDate, YearMonth ym, WorkingSystem workingSystem) {
		
		return getMonthlyUnit(require, cacheCarrier, cid, employmentCd,  employeeId, baseDate, ym, workingSystem)
								.map(monthUnit -> monthUnit.getLaborTime().getLegalLaborTime())
								   .orElseGet(() -> new MonthlyEstimateTime(0));
	}
	
	/**
	 * 取得する単位を取得
	 * @return
	 */
	private static Optional<MonthlyWorkTimeSet> getMonthlyUnit(RequireM2 require, CacheCarrier cacheCarrier, String cid, 
			String employmentCd, String empId, GeneralDate baseDate, YearMonth ym, WorkingSystem workingSystem){
		
		/*労働時間と日数の設定の利用単位の設定*/
		val usageUnitSetting = require.usageUnitSetting(cid);
		if (!usageUnitSetting.isPresent()) {
			return Optional.empty();
		}
	
		Optional<MonthlyWorkTimeSet> result;
		if(usageUnitSetting.get().isEmployee()) {//社員の労働時間を管理する場合
			result = LaborTimeSettingService.laborTimeSettingByEmployee(require, workingSystem, cid, ym, empId);
			if(result.isPresent()) {
				return result;
			}
		}
		if(usageUnitSetting.get().isWorkPlace()) {//職場の労働時間を管理する場合
			//職場の設定を取得
			result = LaborTimeSettingService.laborTimeSettingByWorkplace(require, cacheCarrier, cid, 
																			workingSystem, empId, baseDate, ym);
			if(result.isPresent()) {
				return result;
			}
		}
		if(usageUnitSetting.get().isEmployment()) {//雇用の労働時間を管理する場合
			//雇用別設定の取得
			result = LaborTimeSettingService.laborTimeSettingByEmployment(require, workingSystem, cid, ym, employmentCd);
			if(result.isPresent()) {
				return result;
			}
		}
		//会社別設定の取得
		return LaborTimeSettingService.laborTimeSettingByCompany(require, workingSystem, cid, ym);
	}
	
	/**
	 * 取得処理(フレックス)
	 * @return
	 */
	private static Optional<MonthlyWorkTimeSet> flexMonthlyUnit(RequireM1 require, 
			CacheCarrier cacheCarrier, String cid, String employmentCd, 
			String employeeId, GeneralDate baseDate, YearMonth ym) {
		
		return require.usageUnitSetting(cid).flatMap(usageUnitSetting -> {/*労働時間と日数の設定の利用単位の設定*/
			Optional<MonthlyWorkTimeSet> result;
			
			if(usageUnitSetting.isEmployee()) {//社員の労働時間を管理する場合
				result =  require.monthlyWorkTimeSetSha(cid, employeeId, LaborWorkTypeAttr.FLEX, ym).map(c -> c);
				if(result.isPresent()) {
					return result;
				}
			}
			if(usageUnitSetting.isWorkPlace()) {//職場の労働時間を管理する場合
				//所属職場を含む上位階層の職場IDを取得
				List<String> workPlaceIdList = require.getCanUseWorkplaceForEmp(cacheCarrier, cid, employeeId, baseDate);
				for(String workPlaceId:workPlaceIdList) {
					result = require.monthlyWorkTimeSetWkp(cid, workPlaceId, LaborWorkTypeAttr.FLEX, ym).map(c -> c);
					
					if(result.isPresent()) {
						return result;
					}
				}
			}
			if(usageUnitSetting.isEmployment()) {//雇用の労働時間を管理する場合
				//雇用別設定の取得
				result = require.monthlyWorkTimeSetEmp(cid, employmentCd, LaborWorkTypeAttr.FLEX, ym).map(c -> c);
				if(result.isPresent()) {
					return result;
				}
			}
			//会社別設定の取得
			return require.monthlyWorkTimeSetCom(cid, LaborWorkTypeAttr.FLEX, ym).map(c -> c);
		});
	}
	
	public static interface RequireM1 extends RequireM0 {
		
		Optional<MonthlyWorkTimeSetSha> monthlyWorkTimeSetSha(String cid, String sid, LaborWorkTypeAttr laborAttr, YearMonth ym);

		Optional<MonthlyWorkTimeSetEmp> monthlyWorkTimeSetEmp(String cid, String empCode, LaborWorkTypeAttr laborAttr, YearMonth ym);

		Optional<MonthlyWorkTimeSetCom> monthlyWorkTimeSetCom(String cid, LaborWorkTypeAttr laborAttr, YearMonth ym);

		Optional<MonthlyWorkTimeSetWkp> monthlyWorkTimeSetWkp(String cid, String workplaceId, LaborWorkTypeAttr laborAttr, YearMonth ym);
		
		List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId, String employeeId, GeneralDate baseDate);
	}
	
	public static interface RequireM2 extends LaborTimeSettingService.RequireM1,
												LaborTimeSettingService.RequireM2, 
												LaborTimeSettingService.RequireM3, 
												LaborTimeSettingService.RequireM4,
												RequireM0 {
	}
	
	public static interface RequireM0 {
		
		Optional<UsageUnitSetting> usageUnitSetting(String companyId);
	}
	
	public static interface RequireM3 extends DailyStatutoryLaborTime.RequireM1 {} 
	
	public static interface RequireM4 extends RequireM2, RequireM3 {} 
}
