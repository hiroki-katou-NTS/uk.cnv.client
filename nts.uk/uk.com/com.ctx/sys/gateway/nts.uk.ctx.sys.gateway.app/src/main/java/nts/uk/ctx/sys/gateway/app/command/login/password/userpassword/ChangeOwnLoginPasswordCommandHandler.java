package nts.uk.ctx.sys.gateway.app.command.login.password.userpassword;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.TransactionService;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.ChangeLoginPasswordOfUser;
import nts.uk.shr.com.context.AppContexts;

/**
 * 自分のログインパスワードを変更する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ChangeOwnLoginPasswordCommandHandler extends CommandHandler<ChangeOwnLoginPasswordCommand> {
	
	@Inject
	private ChangeLoginPasswordCommandRequire requireProvider;
	
	@Inject
	private TransactionService transaction;
	
	@Override
	protected void handle(CommandHandlerContext<ChangeOwnLoginPasswordCommand> context) {
		
		val require = requireProvider.create();
		val command = context.getCommand();
		
		val atomTask = ChangeLoginPasswordOfUser.change(require, AppContexts.user().userId(),
				command.getCurrentPassword(), command.getNewPassword(), command.getConfirmPassword());
		
		transaction.execute(atomTask);
	}
	
	public static interface Require extends ChangeLoginPasswordOfUser.Require {
		
	}
}
