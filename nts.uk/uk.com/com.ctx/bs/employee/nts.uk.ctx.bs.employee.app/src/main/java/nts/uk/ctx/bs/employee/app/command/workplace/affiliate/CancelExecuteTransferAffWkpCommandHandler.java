package nts.uk.ctx.bs.employee.app.command.workplace.affiliate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregCommandHandlerCollector;

/**
 * 職場.所属職場履歴.アルゴリズム.Command.職場異動の取消を実行する.職場異動の取消を実行する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CancelExecuteTransferAffWkpCommandHandler extends CommandHandlerWithResult<List<DeleteAffWorkplaceHistoryCommand>, List<MyCustomizeException>> {

	@Inject
	private PeregCommandHandlerCollector peregCommandHandlerCollector;
	
	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<DeleteAffWorkplaceHistoryCommand>> context) {
		List<DeleteAffWorkplaceHistoryCommand> command = context.getCommand();
		List<MyCustomizeException> result = new ArrayList<MyCustomizeException>();
		val wkphandler = this.peregCommandHandlerCollector
				.collectDeleteHandlers()
				.stream()
				.collect(Collectors.toMap(h -> h.targetCategoryCd(), h -> h))
				.get(new DeleteAffWorkplaceHistoryCommandHandler().targetCategoryCd());
		
		command.forEach(wkp -> {
			try {
				wkphandler.handlePeregCommand(wkp);
			} catch (Throwable t) {
				if (t.getCause().getClass() == BusinessException.class) {
					BusinessException bex = (BusinessException)t.getCause();
					MyCustomizeException ex = new MyCustomizeException(bex.getMessageId(), Arrays.asList(wkp.getEmployeeId() + " " + wkp.getHistoryId()));
					result.add(ex);
				}
				
				if (t.getCause().getClass() == RuntimeException.class) {
					RuntimeException rex = (RuntimeException)t.getCause();
					MyCustomizeException ex = new MyCustomizeException(rex.getMessage(), Arrays.asList(wkp.getEmployeeId() + " " + wkp.getHistoryId()));
					result.add(ex);
				}
			}
		});
		
		return result;
	}

}
