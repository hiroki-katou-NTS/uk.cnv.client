package nts.uk.ctx.bs.employee.app.command.employee;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.bs.employee.app.command.jobtitle.affiliate.CancelExecuteTransferAffJobTitleCommandHandler;
import nts.uk.ctx.bs.employee.app.command.workplace.affiliate.CancelExecuteTransferAffWkpCommandHandler;
import nts.uk.shr.pereg.app.command.MyCustomizeException;

/**
 * APP.Command.異動の取消を実行する.異動の取消を実行する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CancelExecuteTransferCommandHandler extends CommandHandlerWithResult<CancelExecuteTransferCommand, CancelExecuteTransferResult> {

	@Inject
	private CancelExecuteTransferAffJobTitleCommandHandler cancelExecuteJobTitle;
	
	@Inject
	private CancelExecuteTransferAffWkpCommandHandler cancelExecuteWkp;
	
	@Override
	protected CancelExecuteTransferResult handle(CommandHandlerContext<CancelExecuteTransferCommand> context) {
		CancelExecuteTransferCommand command = context.getCommand();
		List<MyCustomizeException> affWorkplaces = cancelExecuteWkp.handle(command.getAffWorkplaces());
		List<MyCustomizeException> affJobTitles = cancelExecuteJobTitle.handle(command.getAffJobTitles());
		
		return new CancelExecuteTransferResult(affWorkplaces, affJobTitles);
	}

}
