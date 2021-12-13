package nts.uk.ctx.bs.employee.app.command.jobtitle.affiliate;

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
 * 職位.職務職位.アルゴリズム.Command.職位異動の取消を実行する.職位異動の取消を実行する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CancelExecuteTransferAffJobTitleCommandHandler extends CommandHandlerWithResult<List<DeleteAffJobTitleMainCommand>, List<MyCustomizeException>> {

	@Inject
	private PeregCommandHandlerCollector peregCommandHandlerCollector;
	
	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<DeleteAffJobTitleMainCommand>> context) {
		List<DeleteAffJobTitleMainCommand> command = context.getCommand();
		List<MyCustomizeException> result = new ArrayList<MyCustomizeException>();
		
		val jthandler = this.peregCommandHandlerCollector
				.collectDeleteHandlers()
				.stream()
				.collect(Collectors.toMap(h -> h.targetCategoryCd(), h -> h))
				.get(new DeleteAffJobTitleMainCommandHandler().targetCategoryCd());
	
		command.forEach(jt -> {
			try {
				jthandler.handlePeregCommand(jt);
			} catch (Throwable t) {
				if (t.getCause().getClass() == BusinessException.class) {
					BusinessException bex = (BusinessException)t.getCause();
					MyCustomizeException ex = new MyCustomizeException(bex.getMessageId(), Arrays.asList(jt.getEmployeeId() + " " + jt.getHistId()));
					result.add(ex);
				}
				
				if (t.getCause().getClass() == RuntimeException.class) {
					RuntimeException rex = (RuntimeException)t.getCause();
					MyCustomizeException ex = new MyCustomizeException(rex.getMessage(), Arrays.asList(jt.getEmployeeId() + " " + jt.getHistId()));
					result.add(ex);
				}
			}
		});
		
		return result;
	}

}
