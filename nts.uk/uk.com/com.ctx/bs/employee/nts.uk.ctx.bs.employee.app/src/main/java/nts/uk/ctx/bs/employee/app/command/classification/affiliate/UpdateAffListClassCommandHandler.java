package nts.uk.ctx.bs.employee.app.command.classification.affiliate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.AffClassHistItem;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.AffClassHistItemRepository;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.AffClassHistory;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.AffClassHistoryRepository;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.AffClassHistoryRepositoryService;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.MidAffClass;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregUpdateListCommandHandler;

@Stateless
public class UpdateAffListClassCommandHandler extends CommandHandlerWithResult<List<UpdateAffClassificationCommand>, List<MyCustomizeException>>
		implements PeregUpdateListCommandHandler<UpdateAffClassificationCommand> {
	@Inject
	private AffClassHistoryRepository affClassHistoryRepo;

	@Inject
	private AffClassHistItemRepository affClassHistItemRepo;

	@Inject
	private AffClassHistoryRepositoryService affClassHistoryRepositoryService;

	@Override
	public String targetCategoryCd() {
		return "CS00004";
	}

	@Override
	public Class<?> commandClass() {
		return UpdateAffClassificationCommand.class;
		
	}

	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<UpdateAffClassificationCommand>> context) {
		List<UpdateAffClassificationCommand> command = context.getCommand();
		String cid = AppContexts.user().companyId();
		List<MidAffClass> histories = new ArrayList<>();
		List<AffClassHistItem> items = new ArrayList<>();
		Map<String, List<AffClassHistory>> affClassHisMaps = new HashMap<>();
		List<String> errors = new ArrayList<>();
		List<MyCustomizeException> errorExceptionLst = new ArrayList<>();
		// sidsPidsMap
		List<String> sids = command.stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		// In case of date period are exist in the screen, do thi???t l???p ???n hi???n cho c??ng
		// m???t c??ng ty n??n t???t item c???a c??c nh??n vi??n ???????c hi???n th??? gi???ng nhau
		// v?? v???y m?? m??nh s??? ki???m tra startDate c???a nh??n vi??n ?????u ti??n trong list ?????
		// check xem c?? hi???n th??? tr??n m??n h??nh ko?
		UpdateAffClassificationCommand updateFirst = command.get(0);
		if (updateFirst.getStartDate() != null) {
			Map<String, List<AffClassHistory>> affClassHisMap = affClassHistoryRepo.getBySidsWithCid(cid, sids).stream()
					.collect(Collectors.groupingBy(c -> c.getEmployeeId()));
			affClassHisMaps.putAll(affClassHisMap);
		}
		
		command.stream().forEach(c -> {
			try {
				if (c.getStartDate() != null) {
					List<AffClassHistory> affClassHistLst = affClassHisMaps.get(c.getEmployeeId());
					if (affClassHistLst != null) {
						AffClassHistory historyOption = affClassHistLst.get(0);
						Optional<DateHistoryItem> itemToBeUpdateOpt = historyOption.getPeriods().stream()
								.filter(date -> date.identifier().equals(c.getHistoryId())).findFirst();
						if (itemToBeUpdateOpt.isPresent()) {
							historyOption.changeSpan(itemToBeUpdateOpt.get(), new DatePeriod(c.getStartDate(),
									c.getEndDate() != null ? c.getEndDate() : ConstantUtils.maxDate()));
							histories.add(new MidAffClass(historyOption, itemToBeUpdateOpt.get()));

						} else {
							errors.add(c.getEmployeeId());
							return;
						}

					} else {
						errors.add(c.getEmployeeId());
						return;
					}

				} else {
					errors.add(c.getEmployeeId());
					return;
				}

				// update history item
				AffClassHistItem historyItem = AffClassHistItem.createFromJavaType(c.getEmployeeId(), c.getHistoryId(),
						c.getClassificationCode());
				items.add(historyItem);

			} catch (BusinessException e) {
				MyCustomizeException ex = new MyCustomizeException(e.getMessageId(), Arrays.asList(c.getEmployeeId()), "??????");
				errorExceptionLst.add(ex);
			}
		});

			if(!histories.isEmpty()) {
				affClassHistoryRepositoryService.updateAll(histories);
			}
			
			if(!items.isEmpty()) {
				affClassHistItemRepo.updateAll(items);
			}
			
			if(!errors.isEmpty()) {
				errorExceptionLst.add(new MyCustomizeException("Invalid", errors));
			}
		return errorExceptionLst;
	}
}