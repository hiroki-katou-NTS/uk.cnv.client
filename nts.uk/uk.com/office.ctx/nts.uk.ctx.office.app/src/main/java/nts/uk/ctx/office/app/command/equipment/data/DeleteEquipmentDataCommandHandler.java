package nts.uk.ctx.office.app.command.equipment.data;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.APP.利用実績の削除をする.利用実績の削除をする
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DeleteEquipmentDataCommandHandler extends CommandHandler<DeleteEquipmentDataCommand> {

	@Inject
	private EquipmentDataRepository equipmentDataRepository;
	
	@Override
	protected void handle(CommandHandlerContext<DeleteEquipmentDataCommand> context) {
		DeleteEquipmentDataCommand command = context.getCommand();
		String cid = AppContexts.user().companyId();
		String sid = command.getSid();

		// 1.get(設備コード、利用者ID、利用日、入力日)
		Optional<EquipmentData> optEquipmentData = this.equipmentDataRepository.findByUsageInfo(cid,
				command.getEquipmentCode(), command.getUseDate(), sid, command.getInputDate());
		// 2.[設備利用実績データ　IS NULL]
		if (!optEquipmentData.isPresent()) {
			throw new BusinessException("Msg_2237");
		}
		// 3.delete(設備コード、利用者ID、利用日、入力日)
		this.equipmentDataRepository.delete(cid, sid, command.getInputDate());
	}

}
