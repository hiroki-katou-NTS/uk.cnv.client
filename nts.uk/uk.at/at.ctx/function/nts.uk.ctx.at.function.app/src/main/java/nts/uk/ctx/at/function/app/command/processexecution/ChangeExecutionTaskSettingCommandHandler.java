package nts.uk.ctx.at.function.app.command.processexecution;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.function.app.find.processexecution.dto.ExecutionTaskSettingDto;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.CurrentExecutionStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogManage;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogManageRepository;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.ExecutionTaskSetting;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KBT_更新処理自動実行.KBT002_更新処理自動実行.F:実行選択.アルゴリズム.実行タスクの有効設定を変更する.実行タスクの有効設定を変更する
 */
@Stateless
//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ChangeExecutionTaskSettingCommandHandler
		extends CommandHandlerWithResult<ChangeExecutionTaskSettingCommand, ExecutionTaskSettingDto> {

	@Inject
	private ProcessExecutionLogManageRepository processExecutionLogManageRepository;

	@Inject
	private SaveExecutionTaskSettingCommandHandler saveExecutionTaskSettingCommandHandler;

	@Override
	protected ExecutionTaskSettingDto handle(CommandHandlerContext<ChangeExecutionTaskSettingCommand> context) {
		ChangeExecutionTaskSettingCommand command = context.getCommand();
		SaveExecutionTaskSettingCommand saveCommand = command.convertToSaveCommand();
		// ドメインモデル「更新処理自動実行管理」を取得し、「現在の実行状態」を確認する
		Optional<ProcessExecutionLogManage> optLogManage = this.processExecutionLogManageRepository
				.getLogByCIdAndExecCd(AppContexts.user().companyId(), command.getExecItemCd());
		if (optLogManage.isPresent()) {
			ProcessExecutionLogManage logManage = optLogManage.get();
			if (logManage.getCurrentStatus().isPresent()) {
				CurrentExecutionStatus status = logManage.getCurrentStatus().get();
				// 「実行中」の場合
				if (status.equals(CurrentExecutionStatus.RUNNING)) {
					// エラーメッセージ「#Msg_1318」を表示する
					throw new BusinessException("Msg_1318");
				}
				// 「実行中」以外の場合
				ExecutionTaskSetting executionTaskSetting = command.toDomain();

				try {
					// 登録処理
					this.saveExecutionTaskSettingCommandHandler.handle(saveCommand);
				} catch (Exception e) {
					throw new BusinessException("Msg_1110");
				}
				// 更新後の「実行タスク設定」を返す
				return ExecutionTaskSettingDto.fromDomain(executionTaskSetting);
			}
		}
		return null;
	}
}
