package nts.uk.ctx.at.record.app.command.dailyperformanceformat.businesstype;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmployee;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmployeeHistory;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmployeeHistoryInter;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.repository.BusinessTypeEmpOfHistoryRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.repository.BusinessTypeOfEmployeeRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.repository.BusinessTypeOfHistoryGeneralRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.PeregUpdateListCommandHandler;
@Stateless
public class UpdateBusinessWorkTypeOfHistoryListCommandHandler extends CommandHandler<List<UpdateBusinessWorkTypeOfHistoryCommand>>
implements PeregUpdateListCommandHandler<UpdateBusinessWorkTypeOfHistoryCommand>{
	@Inject
	private BusinessTypeOfEmployeeRepository typeOfEmployeeRepos;

	@Inject
	private BusinessTypeEmpOfHistoryRepository typeEmployeeOfHistoryRepos;

	@Inject
	private BusinessTypeOfHistoryGeneralRepository typeOfHistoryGeneralRepos;
	@Override
	public String targetCategoryCd() {
		return "CS00021";
	}

	@Override
	public Class<?> commandClass() {
		return UpdateBusinessWorkTypeOfHistoryCommand.class;
	}

	@Override
	protected void handle(CommandHandlerContext<List<UpdateBusinessWorkTypeOfHistoryCommand>> context) {
		List<UpdateBusinessWorkTypeOfHistoryCommand> cmd = context.getCommand();
		String cid = AppContexts.user().companyId();
		// Update history table
		// In case of date period are exist in the screen
		List<String> errorLst = new ArrayList<>();
		UpdateBusinessWorkTypeOfHistoryCommand updateFirst = cmd.get(0);
		List<BusinessTypeOfEmployeeHistoryInter> bTypeOfEmployeeHistoryInterLst = new ArrayList<>();
		List<BusinessTypeOfEmployee> histItems = new ArrayList<>();
		List<BusinessTypeOfEmployeeHistory> bTypeOfEmployeeHistory = new ArrayList<>();
		// sidsPidsMap
		List<String> sids = cmd.parallelStream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		List<String> histIds = cmd.parallelStream().map(c -> c.getHistoryId()).collect(Collectors.toList());
		List<BusinessTypeOfEmployee> bTypeOfEmployee = typeOfEmployeeRepos.findAllByHistIds(histIds);
		
		if(updateFirst.getStartDate()!= null) {
			List<BusinessTypeOfEmployeeHistory> bTypeEmpHistLst = typeEmployeeOfHistoryRepos.findByEmployeeDesc(cid,  sids);
			bTypeOfEmployeeHistory.addAll(bTypeEmpHistLst);
		}
		
		cmd.parallelStream().forEach(c ->{
			if(c.getStartDate()!= null) {
				Optional<BusinessTypeOfEmployeeHistory> optional = bTypeOfEmployeeHistory.parallelStream().filter(item -> item.getEmployeeId().equals(c.getEmployeeId())).findFirst();
				if (!optional.isPresent()) {
					errorLst.add(c.getEmployeeId());
					return;
				}
				BusinessTypeOfEmployeeHistory bEmployeeHistory = optional.get();
				Optional<DateHistoryItem> optionalHisItem = bEmployeeHistory.getHistory().stream()
						.filter(x -> x.identifier().equals(c.getHistoryId())).findFirst();
				if (!optionalHisItem.isPresent()) {
					errorLst.add(c.getEmployeeId());
					return;
				}
				bEmployeeHistory.changeSpan(optionalHisItem.get(), new DatePeriod(c.getStartDate(), c.getEndDate()));
				bTypeOfEmployeeHistoryInterLst.add(new BusinessTypeOfEmployeeHistoryInter(bEmployeeHistory, optionalHisItem.get()));
			}
			Optional<BusinessTypeOfEmployee> bTypeOfEmp = bTypeOfEmployee.parallelStream().filter(emp -> emp.getHistoryId().equals(c.getHistoryId())).findFirst();
			// update typeof employee
			if (!bTypeOfEmp.isPresent()) {
				errorLst.add(c.getEmployeeId());
				return;
			}
			BusinessTypeOfEmployee bEmployee = BusinessTypeOfEmployee.createFromJavaType(c.getBusinessTypeCode(), c.getHistoryId(),
					c.getEmployeeId());
			histItems.add(bEmployee);
		});
		
		if(!bTypeOfEmployeeHistoryInterLst.isEmpty()) {
			typeOfHistoryGeneralRepos.updateAll(bTypeOfEmployeeHistoryInterLst);
		}
		
		if(!histItems.isEmpty()) {
			typeOfEmployeeRepos.updateAll(histItems);
		}
		
	}

}
