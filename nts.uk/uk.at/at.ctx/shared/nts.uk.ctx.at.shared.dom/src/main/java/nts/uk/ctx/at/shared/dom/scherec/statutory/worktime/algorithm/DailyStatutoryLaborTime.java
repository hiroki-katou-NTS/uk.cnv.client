package nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm;

import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.DailyUnit;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.WorkingTimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;

/**
 * 
 * @author ken_takasu
 *
 */
public class DailyStatutoryLaborTime {

	/**
	 * 日の法定労働時間を取得（通常、変形用）
	 * 
	 * @param workingSystem
	 * @param shainRegularLaborTime
	 * @param shainTransLaborTime
	 * @param wkpRegularLaborTime
	 * @param wkpTransLaborTime
	 * @param empRegularLaborTime
	 * @param empTransLaborTime
	 * @param comRegularLaborTime
	 * @param comTransLaborTime
	 * @return
	 */
	public static DailyUnit getDailyUnit(RequireM1 require, CacheCarrier cacheCarrier, 
			String companyId, String employmentCd, String employeeId, GeneralDate baseDate,
			WorkingSystem workingSystem) {

		if (workingSystem.isFlexTimeWork() || workingSystem.isExcludedWorkingCalculate()) {
			return DailyUnit.zero();
		}

		// 取得する単位を取得
		return getWorkingTimeSetting(require, cacheCarrier, companyId, employmentCd, employeeId, baseDate, workingSystem)
				.map(w -> w.getDailyTime()).orElse(DailyUnit.zero());
	}

	public static DailyUnit getDailyUnit(RequireM1 require, CacheCarrier cacheCarrier,
			String companyId, String employmentCd, String employeeId, GeneralDate baseDate,
			WorkingSystem workingSystem, Optional<UsageUnitSetting> usageSetting) {

		if (workingSystem.isFlexTimeWork() || workingSystem.isExcludedWorkingCalculate()) {
			return DailyUnit.zero();
		}

		// 取得する単位を取得
		return getWorkingTimeSetting(require, cacheCarrier, companyId, employmentCd, employeeId, baseDate, workingSystem)
				.map(w -> w.getDailyTime()).orElse(DailyUnit.zero());
	}

	public static Optional<WorkingTimeSetting> getWorkingTimeSetting(RequireM1 require, CacheCarrier cacheCarrier,
			String companyId, String employmentCd, String employeeId, GeneralDate baseDate,
			WorkingSystem workingSystem) {

		/* 労働時間と日数の設定の利用単位の設定 */
		return getWorkingTimeSetting(require, cacheCarrier, companyId, employmentCd, employeeId, baseDate,
				workingSystem, require.usageUnitSetting(companyId));
	}

	public static Optional<WorkingTimeSetting> getWorkingTimeSetting(RequireM6 require, CacheCarrier cacheCarrier,
			String companyId, String employmentCd, String employeeId, GeneralDate baseDate, WorkingSystem workingSystem,
			Optional<UsageUnitSetting> usageUnitSetting) {

		/* 労働時間と日数の設定の利用単位の設定 */
		if (!usageUnitSetting.isPresent()) {
			return Optional.empty();
		}

		if (usageUnitSetting.get().isEmployee()) {// 社員の労働時間を管理する場合
			val result = getShainWorkingTimeSettingRequire(require, companyId, employeeId,
					workingSystem);
			if (result.isPresent()) {
				return result;
			}
		}
		if (usageUnitSetting.get().isWorkPlace()) {// 職場の労働時間を管理する場合
			// 職場の設定を取得
			val result = getWkpWorkingTimeSettingRequire(require, cacheCarrier, companyId,
					employeeId, baseDate, workingSystem);
			if (result.isPresent()) {
				return result;
			}
		}
		if (usageUnitSetting.get().isEmployment()) {// 雇用の労働時間を管理する場合
			// 雇用別設定の取得
			val result = getEmpWorkingTimeSettingRequire(require, companyId, employmentCd,
					workingSystem);
			if (result.isPresent()) {
				return result;
			}
		}

		// 会社別設定の取得
		return getComWorkingTimeSettingRequire(require, companyId, workingSystem);
	}

	private static Optional<WorkingTimeSetting> getComWorkingTimeSettingRequire(RequireM2 require, String companyId,
			WorkingSystem workingSystem) {

		if (workingSystem.isRegularWork()) {
			// 通常勤務 の場合
			return require.regularLaborTimeByCompany(companyId).map(t -> t);

		} else if (workingSystem.isVariableWorkingTimeWork()) {
			// 変形労働勤務 の場合
			return require.deforLaborTimeByCompany(companyId).map(t -> t);
		}

		return Optional.empty();
	}

	private static Optional<WorkingTimeSetting> getWkpWorkingTimeSettingRequire(RequireM3 require, CacheCarrier cacheCarrier,
			String companyId, String employeeId, GeneralDate baseDate, WorkingSystem workingSystem) {
		// 所属職場を含む上位階層の職場IDを取得
		List<String> workPlaceIdList = require.getCanUseWorkplaceForEmp(cacheCarrier, companyId, employeeId, baseDate);

		// 通常勤務 の場合
		if (workingSystem.isRegularWork()) {
			for (String workPlaceId : workPlaceIdList) {
				Optional<WorkingTimeSetting> result = require.regularLaborTimeByWorkplace(companyId, workPlaceId).map(t -> t);
				if (result.isPresent()) {
					return result;
				}
			}
		}
		// 変形労働勤務 の場合
		else if (workingSystem.isVariableWorkingTimeWork()) {
			for (String workPlaceId : workPlaceIdList) {
				Optional<WorkingTimeSetting> result = require.deforLaborTimeByWorkplace(employeeId, workPlaceId).map(t -> t);
				if (result.isPresent()) {
					return result;
				}
			}
		}

		return Optional.empty();
	}

	private static Optional<WorkingTimeSetting> getEmpWorkingTimeSettingRequire(RequireM4 require, String companyId,
			String employmentCode, WorkingSystem workingSystem) {
		if (workingSystem.isRegularWork()) {
			// 通常勤務 の場合
			return require.regularLaborTimeByEmployment(companyId, employmentCode).map(r -> r);
		} else if (workingSystem.isVariableWorkingTimeWork()) {
			// 変形労働勤務 の場合
			return require.deforLaborTimeByEmployment(companyId, employmentCode).map(r -> r);
		}

		return Optional.empty();
	}

	private static Optional<WorkingTimeSetting> getShainWorkingTimeSettingRequire(RequireM5 require, String companyId, 
			String employeeId, WorkingSystem workingSystem) {
		
		if (workingSystem.isRegularWork()) {
			// 通常勤務 の場合
			return require.regularLaborTimeByEmployee(companyId, employeeId).map(t -> t);
		} else if (workingSystem.isVariableWorkingTimeWork()) {
			// 変形労働勤務 の場合
			return require.deforLaborTimeByEmployee(companyId, employeeId).map(t -> t);
		}
		return Optional.empty();
	}
	
	public static interface RequireM1 extends RequireM6 {
		
		Optional<UsageUnitSetting> usageUnitSetting(String companyId);
	}
	
	public static interface RequireM2 {

		Optional<RegularLaborTimeCom> regularLaborTimeByCompany(String companyId);

		Optional<DeforLaborTimeCom> deforLaborTimeByCompany(String companyId);
	}
	
	public static interface RequireM3 {

		Optional<RegularLaborTimeWkp> regularLaborTimeByWorkplace(String cid, String wkpId);

		Optional<DeforLaborTimeWkp> deforLaborTimeByWorkplace(String cid, String wkpId);
		
		List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId,
				String employeeId, GeneralDate baseDate);
	}
	
	public static interface RequireM4 {

		Optional<RegularLaborTimeEmp> regularLaborTimeByEmployment(String cid, String employmentCode);

		Optional<DeforLaborTimeEmp> deforLaborTimeByEmployment(String cid, String employmentCode);
	}
	
	public static interface RequireM5 {

		Optional<RegularLaborTimeSha> regularLaborTimeByEmployee(String Cid, String EmpId);

		Optional<DeforLaborTimeSha> deforLaborTimeByEmployee(String cid, String empId);
	}

	public static interface RequireM6 extends RequireM2, RequireM3, RequireM4, RequireM5{
		
	}
}