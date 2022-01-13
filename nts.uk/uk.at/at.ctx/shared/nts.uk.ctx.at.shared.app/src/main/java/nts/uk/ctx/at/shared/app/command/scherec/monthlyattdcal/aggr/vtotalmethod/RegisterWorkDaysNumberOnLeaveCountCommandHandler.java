package nts.uk.ctx.at.shared.app.command.scherec.monthlyattdcal.aggr.vtotalmethod;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCount;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCountRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KMF_休暇マスタ.KMF001_休暇の設定.アルゴリズム.休暇取得時の出勤日数カウントを登録する.休暇取得時の出勤日数カウントを登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterWorkDaysNumberOnLeaveCountCommandHandler
		extends CommandHandler<RegisterWorkDaysNumberOnLeaveCountCommand> {

	@Inject
	private WorkDaysNumberOnLeaveCountRepository repository;
	
	@Override
	protected void handle(CommandHandlerContext<RegisterWorkDaysNumberOnLeaveCountCommand> context) {
		String cid = AppContexts.user().companyId();
		RegisterWorkDaysNumberOnLeaveCountCommand command = context.getCommand();
		WorkDaysNumberOnLeaveCount leave = this.repository.findByCid(cid);
		List<Integer> leaveTypes = leave.getCountedLeaveList().stream()
				.map(data -> data.value).collect(Collectors.toList());
				
		// INPUT．出勤日数としてカウントするかをチェック
		if (command.isCounting()) {
			// INPUT.休暇の種類が登録されているかのチェック
			if (!leaveTypes.contains(command.getLeaveType())) {
				// カウントする休暇一覧にINPUT．休暇の種類を追加する（Insert）
				this.repository.insert(cid, command.getLeaveType());
			}
		} else {
			// INPUT.休暇の種類が登録されているかのチェック
			if (leaveTypes.contains(command.getLeaveType())) {
				// カウントする休暇一覧からINPUT．休暇の種類を削除する（Delete）
				this.repository.delete(cid, command.getLeaveType());
			}
		}
	}
}
