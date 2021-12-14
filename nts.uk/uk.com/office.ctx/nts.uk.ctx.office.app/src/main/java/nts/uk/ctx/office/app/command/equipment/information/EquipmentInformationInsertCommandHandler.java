package nts.uk.ctx.office.app.command.equipment.information;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.オフィス.OEM_設備マスタ.OEM002_設備の登録.A：設備の登録.メニュー別OCD.設備を新規登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EquipmentInformationInsertCommandHandler extends CommandHandler<EquipmentInformationCommand> {

	@Inject
	private EquipmentInformationRepository repository;
	
	@Override
	protected void handle(CommandHandlerContext<EquipmentInformationCommand> context) {
		// 新規追加(契約コード、設備コード、設備名称、有効期間、設備分類コード、備考)
		EquipmentInformation domain = EquipmentInformation.createFromMemento(context.getCommand());
		if (this.repository.findByPk(AppContexts.user().companyId(), domain.getEquipmentCode().v())
				.isPresent()) {
			throw new BusinessException("Msg_3");
		}
		// persist
		this.repository.insert(domain);
	}
	
}
