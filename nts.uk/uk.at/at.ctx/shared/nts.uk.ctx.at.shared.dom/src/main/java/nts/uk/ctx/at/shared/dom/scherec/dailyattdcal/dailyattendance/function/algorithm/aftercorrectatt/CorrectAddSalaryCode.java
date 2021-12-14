package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.aftercorrectatt;

import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BPUnitUseSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * @author thanh_nx
 *
 *         加給コードを補正する
 */
public class CorrectAddSalaryCode {

	public static void correct(Require require, String companyId, IntegrationOfDaily domainDaily) {
		// 加給利用単位を取得する
		Optional<BPUnitUseSetting> setting = require.getSetting(companyId);

		// 加給利用単位を確認する
		if (!setting.isPresent())
			return;
		// 加給設定を取得する
		Optional<BonusPaySetting> bonusPaySetting = setting.get().getBonusPaySetting(require,
				domainDaily.getEmployeeId(), domainDaily.getYmd(),
				domainDaily.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull(),
				new WorkplaceId(domainDaily.getAffiliationInfor().getWplID()));

		// 加給コードをセットする
		domainDaily.getAffiliationInfor().setBonusPaySettingCode(bonusPaySetting.map(x -> x.getCode()));
		return;
	}

	public static interface Require extends BPUnitUseSetting.Require {
		// BPUnitUseSettingRepository
		Optional<BPUnitUseSetting> getSetting(String companyId);
	}
}
