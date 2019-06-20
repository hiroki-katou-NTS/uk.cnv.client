package nts.uk.ctx.at.shared.app.command.workingcondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregUpdateListCommandHandler;
@Stateless
public class UpdateWorkingCondition2ListCommandHandler extends CommandHandlerWithResult<List<UpdateWorkingCondition2Command>, List<MyCustomizeException>>
implements PeregUpdateListCommandHandler<UpdateWorkingCondition2Command>{
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;

	@Inject
	private WorkingConditionRepository workingConditionRepository;

	@Inject
	private UpdateWorkingConditionCommandAssembler updateWorkingConditionCommandAssembler;
	@Override
	public String targetCategoryCd() {
		return "CS00070";
	}

	@Override
	public Class<?> commandClass() {
		return UpdateWorkingCondition2Command.class;
	}

	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<UpdateWorkingCondition2Command>> context) {
		List<UpdateWorkingCondition2Command> cmd = context.getCommand();
		String cid = AppContexts.user().companyId();
		
		UpdateWorkingCondition2Command updateFirst = cmd.get(0);
		// sidsPidsMap
		List<String> sids = cmd.stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		List<String> errorLst = new ArrayList<>();
		List<WorkingCondition> listHistBySids = new ArrayList<>();
		List<WorkingCondition> workingCondInserts = new ArrayList<>();
		List<WorkingConditionItem> workingCondItems = new ArrayList<>();
		List<MyCustomizeException> errorExceptionLst = new ArrayList<>();
		if (updateFirst.getStartDate() != null){
			List<WorkingCondition> listHistBySid =  workingConditionRepository.getBySidsAndCid(cid, sids);
			listHistBySids.addAll(listHistBySid);
		}
		cmd.stream().forEach(c ->{
			if(c.getStartDate() != null) {
				Optional<WorkingCondition> workingCondOpt =  listHistBySids.stream().filter(item -> item.getEmployeeId().equals(c.getEmployeeId())).findFirst();
				if(!workingCondOpt.isPresent()) {
					errorLst.add(c.getEmployeeId());
					return;
				}
				WorkingCondition workingCond = workingCondOpt.get();
				Optional<DateHistoryItem> itemToBeUpdated = workingCond.getDateHistoryItem().stream().filter(hist->hist.identifier().equals(c.getHistId())).findFirst();
				if (!itemToBeUpdated.isPresent()){
					errorLst.add(c.getEmployeeId());
					return;
				}
				GeneralDate endDate = c.getEndDate() !=null? c.getEndDate() : GeneralDate.max();
				workingCond.changeSpan(itemToBeUpdated.get(), new DatePeriod(c.getStartDate(), endDate));
				workingCondInserts.add(workingCond);
			}
			WorkingConditionItem  workingCondItem = updateWorkingConditionCommandAssembler.fromDTO2(c);
			workingCondItems.add(workingCondItem);
		});
		
		if(!workingCondInserts.isEmpty()) {
			workingConditionRepository.saveAll(workingCondInserts);
		}
		
		if(!workingCondItems.isEmpty()) {
			workingConditionItemRepository.updateAllWorkCond2(workingCondItems);
		}
		
		if(!errorLst.isEmpty()) {
			errorExceptionLst.add(new MyCustomizeException("Invalid item to be updated", errorLst));
		}
		return errorExceptionLst;
		
	}

}
