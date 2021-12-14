/**
 * 9:59:33 AM Jun 6, 2017
 */
package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.WorkingTimesheetCode;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * @author hungnm 加給利用単位
 *
 */
@Getter
public class BPUnitUseSetting extends AggregateRoot {

	private String companyId;

	private UseAtr workplaceUseAtr;

	private UseAtr personalUseAtr;

	private UseAtr workingTimesheetUseAtr;

	public BPUnitUseSetting(String companyId, UseAtr workplaceUseAtr, UseAtr personalUseAtr,
			UseAtr workingTimesheetUseAtr) {
		super();
		this.companyId = companyId;
		this.workplaceUseAtr = workplaceUseAtr;
		this.personalUseAtr = personalUseAtr;
		this.workingTimesheetUseAtr = workingTimesheetUseAtr;
	}

	public static BPUnitUseSetting createFromJavaType(String companyId, int workplaceUseAtr, int personalUseAtr,
			int workingTimesheetUseAtr) {
		return new BPUnitUseSetting(companyId, EnumAdaptor.valueOf(workplaceUseAtr, UseAtr.class),
				EnumAdaptor.valueOf(personalUseAtr, UseAtr.class),
				EnumAdaptor.valueOf(workingTimesheetUseAtr, UseAtr.class));
	}

	// [1] 加給設定を取得する
	public Optional<BonusPaySetting> getBonusPaySetting(Require require, String employeeId, GeneralDate baseDate,
			Optional<WorkTimeCode> workTimeCode, WorkplaceId workplaceId) {
		Optional<BonusPaySetting> setting = getSettingFromWorkTimeCode(require, workTimeCode);
		if (setting.isPresent())
			return setting;
		
		setting = getSettingFromEmployee(require, employeeId, baseDate);
		if (setting.isPresent())
			return setting;
		
		setting = getSettingFromWpl(require, workplaceId, baseDate);
		if (setting.isPresent())
			return setting;
		
		return getSettingFromCompany(require);
	}

	// [prv-1] 加給設定を取得する
	private Optional<BonusPaySetting> getBonusPaySettingCommon(Require require, BonusPaySettingCode code) {
		return require.getBonusPaySetting(companyId, code);
	}

	// [prv-2] 就業時間帯から加給設定を取得する
	private Optional<BonusPaySetting> getSettingFromWorkTimeCode(Require require, Optional<WorkTimeCode> workTimeCode) {
		if (workingTimesheetUseAtr == UseAtr.NOT_USE || !workTimeCode.isPresent())
			return Optional.empty();
		val workTimeSheetPaySet = require.getWTBPSetting(companyId, new WorkingTimesheetCode(workTimeCode.get().v()));
		if (!workTimeSheetPaySet.isPresent()) {
			return Optional.empty();
		}
		// ドメインモデル「加給設定」を取得する
		return getBonusPaySettingCommon(require, workTimeSheetPaySet.get().getBonusPaySettingCode());
	}

	//[prv-3] 社員から加給設定を取得する
	private Optional<BonusPaySetting> getSettingFromEmployee(Require require, String employeeId, GeneralDate baseDate) {
		if (personalUseAtr == UseAtr.NOT_USE)
			return Optional.empty();
		// 社員の労働条件を取得する
		Optional<WorkingConditionItem> workingConditionItem = WorkingConditionService
				.findWorkConditionByEmployee(require, employeeId, baseDate);

		if (workingConditionItem.isPresent() && workingConditionItem.get().getTimeApply().isPresent()) {
			// ドメインモデル「加給設定」を取得する
			return getBonusPaySettingCommon(require,
					new BonusPaySettingCode(workingConditionItem.get().getTimeApply().get().v()));
		}
		return Optional.empty();
	}

	//[prv-4] 職場から加給設定を取得する
	private Optional<BonusPaySetting> getSettingFromWpl(Require require, WorkplaceId workplaceId, GeneralDate baseDate) {
		if (workplaceUseAtr == UseAtr.NOT_USE )
			return Optional.empty();
		
		//$職場ID一覧
		List<String> wplIds = require.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId.v());
		
		for (String wpl : wplIds) {
			Optional<WorkplaceBonusPaySetting> wPBPSetting = require.getWPBPSetting(companyId, new WorkplaceId(wpl));
			if (wPBPSetting.isPresent()) {
				return getBonusPaySettingCommon(require, wPBPSetting.get().getBonusPaySettingCode());
			}
		}

	   return Optional.empty();
		
	}
	
	// [prv-5] 会社から加給設定を取得する
	private Optional<BonusPaySetting> getSettingFromCompany(Require require) {
		Optional<CompanyBonusPaySetting> settingCom = require.getSettingCom(companyId);
		if (!settingCom.isPresent())
			return Optional.empty();

		// ドメインモデル「加給設定」を取得する
		return getBonusPaySettingCommon(require, settingCom.get().getBonusPaySettingCode());
	}
	
	public static interface Require extends WorkingConditionService.RequireM1 {
		// BPSettingRepository
		Optional<BonusPaySetting> getBonusPaySetting(String companyId, BonusPaySettingCode bonusPaySettingCode);
		
		// WTBonusPaySettingRepository
		Optional<WorkingTimesheetBonusPaySetting> getWTBPSetting(String companyId,
				WorkingTimesheetCode workingTimesheetCode);
		
		// SharedAffWorkplaceHistoryItemAdapter
		List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId);
		
		// WPBonusPaySettingRepository
		Optional<WorkplaceBonusPaySetting> getWPBPSetting(String companyId, WorkplaceId wpl);

		// CPBonusPaySettingRepository
		Optional<CompanyBonusPaySetting> getSettingCom(String companyId);
	}
}
