package nts.uk.screen.com.app.command.cmm029;

import java.util.Arrays;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A : 機能の選択.メニュー別OCD.設定機能を登録する.設定機能を登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterFunctionSettingCommandHandler extends CommandHandler<RegisterFunctionSettingCommand> {

	@Inject
	private RegisterWorkSettingCommandHandler registerWorkSettingCommandHandler;
	
	@Inject
	private RegisterOtherSettingCommandHandler registerOtherSettingCommandHandler;

	@Override
	protected void handle(CommandHandlerContext<RegisterFunctionSettingCommand> context) {
		RegisterFunctionSettingCommand command = context.getCommand();
		// 1. 勤務の設定機能を登録する
		AtomTask registerWork = AtomTask.of(() -> this.registerWorkSettingCommandHandler.handle(command));
		// 2. その他の設定機能を登録する
		AtomTask registerOther = AtomTask.of(() -> this.registerOtherSettingCommandHandler.handle(command));
		// 1.1. 勤務の設定の処理
		// 2.1. その他の設定機能の処理
		transaction.allInOneTransaction(Arrays.asList(registerWork, registerOther));
	}
}
