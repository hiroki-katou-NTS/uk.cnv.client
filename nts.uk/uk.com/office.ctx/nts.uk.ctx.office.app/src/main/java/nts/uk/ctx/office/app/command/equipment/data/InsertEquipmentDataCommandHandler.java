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
import nts.uk.ctx.office.dom.equipment.data.domainservice.InsertUsageRecordDomainService;
import nts.uk.ctx.office.dom.equipment.data.domainservice.InsertUsageRecordDomainService.Require;
import nts.uk.ctx.office.dom.equipment.data.require.InsertUsageRecordDomainServiceRequireImpl;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.APP.利用実績の新規登録をする.利用実績の新規登録をする
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class InsertEquipmentDataCommandHandler extends CommandHandler<EquipmentDataCommand> {

	@Inject
	private EquipmentInformationRepository equipmentInformationRepository;

	@Inject
	private EquipmentDataRepository equipmentDataRepository;

	@Inject
	private EquipmentRecordItemSettingRepository equipmentRecordItemSettingRepository;

	@Override
	protected void handle(CommandHandlerContext<EquipmentDataCommand> context) {
		Require require = new InsertUsageRecordDomainServiceRequireImpl(equipmentInformationRepository,
				equipmentRecordItemSettingRepository, equipmentDataRepository);
		EquipmentDataCommand command = context.getCommand();
		EquipmentClassificationCode equipmentClsCode = new EquipmentClassificationCode(
				command.getEquipmentClassificationCode());
		EquipmentCode equipmentCode = new EquipmentCode(command.getEquipmentCode());
		List<ItemData> itemDatas = command.getItemDatas().stream()
				.map(data -> new ItemData(new EquipmentItemNo(data.getItemNo()),
						new ActualItemUsageValue(data.getActualValue())))
				.collect(Collectors.toList());

		// 1.新規登録する(require, 会社ID, 設備コード, 利用日, 利用者ID, 設備分類コード, 項目データ)
		RegisterResult registerResult = InsertUsageRecordDomainService.insert(require, AppContexts.user().companyId(),
				equipmentCode, command.getUseDate(), AppContexts.user().employeeId(), equipmentClsCode, itemDatas);
		// 2.[エラーがあるか＝false]
		if (!registerResult.isHasError()) {
			registerResult.getPersistTask().ifPresent(transaction::execute);
		} else {
			// 3.[エラーがあるか＝true]
			BundledBusinessException ex = BundledBusinessException.newInstance();
			registerResult.getErrorItems().forEach(err -> ex.addMessage(err.getErrorMessage()));
			throw ex;
		}
	}
}
