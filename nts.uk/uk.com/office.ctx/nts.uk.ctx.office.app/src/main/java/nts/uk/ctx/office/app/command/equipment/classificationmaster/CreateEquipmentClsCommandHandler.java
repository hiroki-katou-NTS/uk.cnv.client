package nts.uk.ctx.office.app.command.equipment.classificationmaster;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationName;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備分類マスタ.APP.設備分類を新規登録する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CreateEquipmentClsCommandHandler extends CommandHandler<EquipmentClsCommand> {

	@Inject
	private EquipmentClassificationRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<EquipmentClsCommand> context) {
		EquipmentClsCommand command = context.getCommand();
		String contractCd = AppContexts.user().contractCode();
		// Step1: get(設備分類コード): optional<設備分類>
		Optional<EquipmentClassification> optEqCls = this.repo.getByClassificationCode(contractCd, command.getCode());
		
		// Step2: not 設備分類.empty
		if (optEqCls.isPresent()) {
			throw new BusinessException("Msg_3");
		}
		
		// Step3: create(設備分類コード,設備分類名称)
		EquipmentClassification domain = new EquipmentClassification(
				new EquipmentClassificationCode(command.getCode()),
				new EquipmentClassificationName(command.getName()));
		
		// Step4: persist
		this.repo.insert(domain);
	}
}
