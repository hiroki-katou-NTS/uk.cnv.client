package nts.uk.ctx.office.app.command.equipment.data;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BundledBusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataRepository;
import nts.uk.ctx.office.dom.equipment.data.ItemData;
import nts.uk.ctx.office.dom.equipment.data.RegisterResult;
import nts.uk.ctx.office.dom.equipment.data.domainservice.UpdateUsageRecordDomainService;
import nts.uk.ctx.office.dom.equipment.data.domainservice.UpdateUsageRecordDomainService.Require;
import nts.uk.ctx.office.dom.equipment.data.require.UpdateUsageRecordDomainServiceRequireImpl;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.APP.利用実績の更新をする.利用実績の更新をする
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UpdateEquipmentDataCommandHandler extends CommandHandler<EquipmentDataCommand> {

	@Inject
	private EquipmentDataRepository equipmentDataRepository;

	@Inject
	private EquipmentRecordItemSettingRepository equipmentRecordItemSettingRepository;

	@Override
	protected void handle(CommandHandlerContext<EquipmentDataCommand> context) {
		String cid = AppContexts.user().companyId();

		Require require = new UpdateUsageRecordDomainServiceRequireImpl(equipmentRecordItemSettingRepository,
				equipmentDataRepository);
		EquipmentDataCommand command = context.getCommand();
		EquipmentClassificationCode equipmentClsCode = new EquipmentClassificationCode(
				command.getEquipmentClassificationCode());
		EquipmentCode equipmentCode = new EquipmentCode(command.getEquipmentCode());
		List<ItemData> itemDatas = command.getItemDatas().stream()
				.map(data -> new ItemData(new EquipmentItemNo(data.getItemNo()),
						new ActualItemUsageValue(data.getActualValue())))
				.collect(Collectors.toList());

		// 1. 変更する(require, 会社ID, 設備コード, 入力日, 設備分類コード, 利用者ID, 利用日, Map<項目NO、入力値>)
		RegisterResult registerResult = UpdateUsageRecordDomainService.update(require, cid, equipmentCode,
				command.getInputDate(), equipmentClsCode, command.getSid(), command.getUseDate(), itemDatas);
		// 2. [エラーがあるか＝true]
		if (registerResult != null && registerResult.isHasError()) {
			BundledBusinessException ex = BundledBusinessException.newInstance();
			registerResult.getErrorItems().forEach(err -> ex.addMessage(err.getErrorMessage()));
			throw ex;
		}
		// 3. [エラーがあるか＝false]
		registerResult.getPersistTask().ifPresent(transaction::execute);
	}

}
