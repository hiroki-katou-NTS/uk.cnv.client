package nts.uk.screen.com.app.command.equipment.achievement;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.app.command.equipment.achievement.dto.EquipmentUsageRecordItemSettingDto;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentPerformInputFormatSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormSettingRepository;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormatSettingRepository;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.オフィス.OEM_設備マスタ.OEM003_設備利用実績の設定.A：設備利用実績の設定.メニュー別OCD.設備項目設定マスタを登録する.設備項目設定マスタを登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class InsertEquipmentUsageSettingsCommandHandler extends CommandHandler<EquipmentUsageSettingsCommand> {

	@Inject
	private EquipmentRecordItemSettingRepository equipmentRecordItemSettingRepository;
	
	@Inject
	private EquipmentFormatSettingRepository equipmentFormatSettingRepository;
	
	@Inject
	private EquipmentFormSettingRepository equipmentFormSettingRepository;

	@Override
	protected void handle(CommandHandlerContext<EquipmentUsageSettingsCommand> context) {
		// 1. 新規登録する(会社ID, 項目設定DTO)
		String cid = AppContexts.user().companyId();
		EquipmentUsageSettingsCommand command = context.getCommand();
		List<EquipmentUsageRecordItemSetting> itemSettings = command.getItemSettings().stream()
				.map(EquipmentUsageRecordItemSettingDto::toDomain).collect(Collectors.toList());
		EquipmentPerformInputFormatSetting formatSetting = command.getFormatSetting().toDomain();
		EquipmentFormSetting formSetting = command.getFormSetting().toDomain();
		// 2. 削除後Insertする(会社ID、List<設備利用実績の項目設定)
		this.equipmentRecordItemSettingRepository.insertAllAfterDeleteAll(cid, itemSettings);
		// 3. 削除後Insertする(設備の実績入力フォーマット設定)
		this.equipmentFormatSettingRepository.insertAfterDelete(formatSetting);
		// 4. 削除後Insertする(設備帳票設定)
		this.equipmentFormSettingRepository.insertAfterDelete(formSetting);
	}
}
