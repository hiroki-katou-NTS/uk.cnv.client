package nts.uk.ctx.exio.app.input.execute;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.AsyncCommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.TransactionService;
import nts.uk.ctx.exio.dom.input.ExecuteImporting;
import nts.uk.ctx.exio.dom.input.manage.ExternalImportStateException;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExternalImportExecuteCommandHandler extends AsyncCommandHandler<ExternalImportExecuteCommand>{

	@Inject
	private ExternalImportExecuteRequire require;
	
	@Inject
	private TransactionService transaction;
	
	@Override
	protected void handle(CommandHandlerContext<ExternalImportExecuteCommand> context) {
		
		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		
		val require = this.require.create(companyId);
		val setting = require.getExternalImportSetting(companyId, command.getExternalImportCode());
		val currentState = require.getExternalImportCurrentState(companyId);
		
		val taskData = context.asAsync().getDataSetter();
		try {
			
			currentState.execute(require, setting, () -> {
				val atomTasks = ExecuteImporting.execute(require, setting);
				transaction.separateForEachTask(atomTasks);
			});
			
			taskData.setData("process", "done");
			
		} catch (ExternalImportStateException ex) {
			
			taskData.setData("process", "failed");
			taskData.setData("message", ex.getMessage());
		}
	}

}
