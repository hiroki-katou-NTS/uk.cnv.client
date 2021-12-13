package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.command.workrecord.workmanagement.AddWorkRecodConfirmationCommand;
import nts.uk.ctx.at.record.app.command.workrecord.workmanagement.AddWorkRecodConfirmationCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.作業実績を確認する
 * 
 * @author tutt
 *
 */
@Stateless
public class AddWorkRecordConfirmationCommandHandler {
	
	@Inject
	private AddWorkRecodConfirmationCommandHandler confirmHandler;

	@Inject
	private GetWorkConfirmationStatus getStatus;

	public List<ConfirmerDto> add(AddWorkRecodConfirmationCommand command) {
		// 1: get(対象者, 対象日, 確認者一覧)
		confirmHandler.handle(command);
		
		// 2: <call>() 作業確認状況を取得する
		return getStatus.get(command.getEmployeeId(), command.getDate());
	}

}
