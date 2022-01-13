package nts.uk.screen.com.app.command.equipment.classificationmaster;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.オフィスヘルパー.OEM_設備マスタ.OEM004_設備分類の登録.A：設備分類の登録.メニュー別OCD.設備分類を削除する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DeleteEquipmentClsCommandHandler extends CommandHandler<String> {

	@Inject
	private EquipmentInformationRepository equipmentInfoRepo;
	
	@Inject
	private EquipmentClassificationRepository equipmentClsRepo;
	
	@Override
	protected void handle(CommandHandlerContext<String> context) {
		String command = context.getCommand();
		// Step1: get(会社コード,設備分類コード): List<設備情報>
		List<EquipmentInformation> equipInfors = this.equipmentInfoRepo.findByCode(command);
		
		// Step2: not 設備情報.empty
		if (!equipInfors.isEmpty()) {
			throw new BusinessException("Msg_2227");
		}
		
		// Step3: delete(ログイン会社ID,設備分類コード)
		this.equipmentClsRepo.delete(AppContexts.user().contractCode(), command);
	}

}
