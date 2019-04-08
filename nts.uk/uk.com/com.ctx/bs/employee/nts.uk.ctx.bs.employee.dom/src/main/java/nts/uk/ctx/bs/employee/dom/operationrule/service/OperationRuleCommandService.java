package nts.uk.ctx.bs.employee.dom.operationrule.service;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.employee.dom.department.master.service.AddDepartmentConfigParam;
import nts.uk.ctx.bs.employee.dom.department.master.service.DepartmentCommandService;
import nts.uk.ctx.bs.employee.dom.department.master.service.UpdateDepartmentConfigParam;
import nts.uk.ctx.bs.employee.dom.operationrule.OperationRule;
import nts.uk.ctx.bs.employee.dom.operationrule.OperationRuleRepository;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.AddWorkplaceConfigParam;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.UpdateWorkplaceConfigParam;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceCommandService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class OperationRuleCommandService {

	private static final int WORKPLACE_MODE = 0;
	private static final int DEPARTMENT_MODE = 1;

	@Inject
	private OperationRuleRepository operationRepo;

	@Inject
	private WorkplaceCommandService wkpCommandService;

	@Inject
	private DepartmentCommandService depCommandService;

	/**
	 * 部門職場構成の追加
	 * 
	 * @param param
	 * @return historyId
	 */
	public String addWkpDepConfig(AddWkpDepConfigParam param) {
		String companyId = AppContexts.user().companyId();
		Optional<OperationRule> optOperationRule = operationRepo.getOperationRule(companyId);
		if (!optOperationRule.isPresent())
			throw new BusinessException("OperationRule not found!");
		OperationRule operationRule = optOperationRule.get();
		switch (operationRule.getDepWkpSynchAtr()) {
		case SYNCHRONIZED:
			String newHistoryId = param.getNewHistoryId();
			if (newHistoryId == null)
				newHistoryId = IdentifierUtil.randomUniqueId();

			AddWorkplaceConfigParam wkpParam = new AddWorkplaceConfigParam(companyId, newHistoryId,
					param.getPrevHistoryId(), param.getStartDate(), param.getEndDate(), param.isCopyPreviousConfig());
			wkpCommandService.addWorkplaceConfig(wkpParam);

			AddDepartmentConfigParam depParam = new AddDepartmentConfigParam(companyId, newHistoryId,
					param.getPrevHistoryId(), param.getStartDate(), param.getEndDate(), param.isCopyPreviousConfig());
			depCommandService.addDepartmentConfig(depParam);
			return newHistoryId;
		case NOT_SYNCHRONIZED:
			switch (param.getInitMode()) {
			case WORKPLACE_MODE:
				AddWorkplaceConfigParam wkpParam2 = new AddWorkplaceConfigParam(companyId, param.getNewHistoryId(),
						param.getPrevHistoryId(), param.getStartDate(), param.getEndDate(),
						param.isCopyPreviousConfig());
				return wkpCommandService.addWorkplaceConfig(wkpParam2);
			case DEPARTMENT_MODE:
				AddDepartmentConfigParam depParam2 = new AddDepartmentConfigParam(companyId, param.getNewHistoryId(),
						param.getPrevHistoryId(), param.getStartDate(), param.getEndDate(),
						param.isCopyPreviousConfig());
				return depCommandService.addDepartmentConfig(depParam2);
			default:
				return null;
			}
		default:
			return null;
		}
	}

	/**
	 * 部門職場構成の更新
	 * 
	 * @param param
	 */
	public void updateWkpDepConfig(UpdateWkpDepConfigParam param) {
		String companyId = AppContexts.user().companyId();
		Optional<OperationRule> optOperationRule = operationRepo.getOperationRule(companyId);
		if (!optOperationRule.isPresent())
			throw new BusinessException("OperationRule not found!");
		OperationRule operationRule = optOperationRule.get();
		switch (operationRule.getDepWkpSynchAtr()) {
		case SYNCHRONIZED:
			UpdateWorkplaceConfigParam wkpParam = new UpdateWorkplaceConfigParam(companyId, param.getHistoryId(),
					param.getStartDate(), param.getEndDate());
			wkpCommandService.updateWorkplaceConfig(wkpParam);

			UpdateDepartmentConfigParam depParam = new UpdateDepartmentConfigParam(companyId, param.getHistoryId(),
					param.getStartDate(), param.getEndDate());
			depCommandService.updateDepartmentConfig(depParam);
			break;
		case NOT_SYNCHRONIZED:
			switch (param.getInitMode()) {
			case WORKPLACE_MODE:
				UpdateWorkplaceConfigParam wkpParam2 = new UpdateWorkplaceConfigParam(companyId, param.getHistoryId(),
						param.getStartDate(), param.getEndDate());
				wkpCommandService.updateWorkplaceConfig(wkpParam2);
				break;
			case DEPARTMENT_MODE:
				UpdateDepartmentConfigParam depParam2 = new UpdateDepartmentConfigParam(companyId, param.getHistoryId(),
						param.getStartDate(), param.getEndDate());
				depCommandService.updateDepartmentConfig(depParam2);
				break;
			default:
				break;
			}
		default:
			break;
		}
	}
}
