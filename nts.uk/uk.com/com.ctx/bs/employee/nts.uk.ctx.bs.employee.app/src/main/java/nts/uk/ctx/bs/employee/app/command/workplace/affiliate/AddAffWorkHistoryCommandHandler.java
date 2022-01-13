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
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.職場.所属職場履歴.アルゴリズム.Command.職場異動の登録をする.職場異動の登録
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AddAffWorkHistoryCommandHandler extends CommandHandlerWithResult<List<AddAffWorkplaceHistoryCommand>, List<MyCustomizeException>> {

	@Inject
	private PeregCommandHandlerCollector peregCommandHandlerCollector;
	
	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<AddAffWorkplaceHistoryCommand>> context) {
		List<AddAffWorkplaceHistoryCommand> listCommand = context.getCommand();
		List<MyCustomizeException> result = new ArrayList<>();
		
		val handler = this.peregCommandHandlerCollector
				.collectAddHandlers()
				.stream()
				.collect(Collectors.toMap(h -> h.targetCategoryCd(), h -> h))
				.get(new AddAffWorkplaceHistoryCommandHandler().targetCategoryCd());
		
		listCommand.stream().forEach(action -> {
			try {
				handler.handlePeregCommand(action);
			} catch (Throwable t) {
				if (t.getCause().getClass() == BusinessException.class) {
					BusinessException bex = (BusinessException)t.getCause();
					MyCustomizeException ex = new MyCustomizeException(bex.getMessageId(), Arrays.asList(action.getEmployeeId()));
					result.add(ex);
				}
			}
		});
		
		return result;
		
	}

	
}
