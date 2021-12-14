package nts.uk.screen.com.app.find.equipment.achievement;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormSetting;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormSettingRepository;
import nts.uk.screen.com.app.find.equipment.achievement.ac.EquipmentUsageSettingsAdapter;
import nts.uk.screen.com.app.find.equipment.achievement.ac.EquipmentUsageSettingsImport;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.オフィス.OEM_設備マスタ.OEM003_設備利用実績の設定.A：設備利用実績の設定.メニュー別OCD.設備利用実績設定の登録内容を取得する.設備利用実績設定の登録内容を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EquipmentUsageSettingsScreenQuery {

	@Inject
	private EquipmentFormSettingRepository equipmentFormSettingRepository;

	@Inject
	private EquipmentUsageSettingsAdapter equipmentUsageSettingsAdapter;

	public EquipmentUsageSettingsDto findSettings() {
		String cid = AppContexts.user().companyId();
		// 1. get(ログイン会社ID)
		EquipmentUsageSettingsImport settings = this.equipmentUsageSettingsAdapter.findSettings();
		// 2. get(ログイン会社ID)
		Optional<EquipmentFormSetting> optFormSetting = this.equipmentFormSettingRepository.findByCid(cid);
		return new EquipmentUsageSettingsDto(settings.getItemSettings(), settings.getFormatSetting(),
				optFormSetting.map(EquipmentFormSettingDto::fromDomain).orElse(null));
	}
}
