package nts.uk.ctx.office.app.command.equipment.information;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;

/**
 * UKDesign.UniversalK.オフィス.OEM_設備マスタ.OEM002_設備の登録.A：設備の登録.メニュー別OCD.設備情報を更新する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EquipmentInformationUpdateCommandHandler extends CommandHandler<EquipmentInformationCommand> {
	
	@Inject
	private EquipmentInformationRepository repository;

	@Override
	protected void handle(CommandHandlerContext<EquipmentInformationCommand> context) {
		// 新規追加(契約コード、設備コード、設備名称、有効期間、設備分類コード、備考)
		EquipmentInformation domain = EquipmentInformation.createFromMemento(context.getCommand());
		// persist
		this.repository.update(domain);
	}
}
