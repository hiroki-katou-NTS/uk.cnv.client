package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.command.workrecord.workmanagement.DeleteWorkResultConfirmCommand;
import nts.uk.ctx.at.record.app.command.workrecord.workmanagement.DeleteWorkResultConfirmationCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.作業実績の確認を解除する
 * @author tutt
 *
 */
@Stateless
public class DeleteWorkRecordConfirmationCommandHandler {
	
	@Inject
	private DeleteWorkResultConfirmationCommandHandler deleteHandler;
	
	@Inject
	private GetWorkConfirmationStatus getStatus;
	
	public List<ConfirmerDto> delete(DeleteWorkResultConfirmCommand command) {
		//1: 解除する(対象者, 対象日, 確認者)
		//作業実績の確認を解除する
		deleteHandler.handle(command);
		
		//2: <call>() 作業確認状況を取得する
		return getStatus.get(command.getEmployeeId(), command.getDate());
	}

}
