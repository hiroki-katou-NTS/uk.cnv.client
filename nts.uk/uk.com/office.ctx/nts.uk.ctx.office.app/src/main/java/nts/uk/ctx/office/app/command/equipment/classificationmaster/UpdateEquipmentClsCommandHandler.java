package nts.uk.ctx.office.app.command.equipment.classificationmaster;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationName;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備分類マスタ.APP.設備分類を更新登録する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UpdateEquipmentClsCommandHandler extends CommandHandler<EquipmentClsCommand> {
	
	@Inject
	private EquipmentClassificationRepository repo;

	@Override
	protected void handle(CommandHandlerContext<EquipmentClsCommand> context) {
		EquipmentClsCommand command = context.getCommand();
		EquipmentClassification domain = new EquipmentClassification(
				new EquipmentClassificationCode(command.getCode()),
				new EquipmentClassificationName(command.getName()));

		// persist
		this.repo.update(domain);
	}
}
