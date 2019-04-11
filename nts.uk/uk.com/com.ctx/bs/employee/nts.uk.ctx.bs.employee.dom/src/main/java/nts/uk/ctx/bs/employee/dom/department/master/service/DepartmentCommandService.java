package nts.uk.ctx.bs.employee.dom.department.master.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.employee.dom.department.master.DepartmentConfiguration;
import nts.uk.ctx.bs.employee.dom.department.master.DepartmentConfigurationRepository;
import nts.uk.ctx.bs.employee.dom.department.master.DepartmentInformation;
import nts.uk.ctx.bs.employee.dom.department.master.DepartmentInformationRepository;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

@Stateless
public class DepartmentCommandService {

	@Inject
	private DepartmentConfigurationRepository depConfigRepo;

	@Inject
	private DepartmentInformationRepository depInforRepo;

	/**
	 * 部門構成を追加する
	 * 
	 * @param param
	 * @return historyId
	 */
	public String addDepartmentConfig(AddDepartmentConfigParam param) {
		if (param.getNewHistoryId() == null)
			param.setNewHistoryId(IdentifierUtil.randomUniqueId());
		Optional<DepartmentConfiguration> optDepConfig = depConfigRepo.getDepConfig(param.getCompanyId());
		if (optDepConfig.isPresent()) {
			DepartmentConfiguration depConfig = optDepConfig.get();
			depConfig.add(new DateHistoryItem(param.getNewHistoryId(),
					new DatePeriod(param.getStartDate(), param.getEndDate())));
			depConfigRepo.updateDepartmentConfig(depConfig);
		} else {
			DepartmentConfiguration depConfig = new DepartmentConfiguration(param.getCompanyId(),
					Arrays.asList(new DateHistoryItem(param.getNewHistoryId(),
							new DatePeriod(param.getStartDate(), param.getEndDate()))));
			depConfigRepo.addDepartmentConfig(depConfig);
		}

		if (param.isCopyPreviousConfig()) {
			List<DepartmentInformation> listDepartmentPrevHist = depInforRepo
					.getAllActiveDepartmentByCompany(param.getCompanyId(), param.getPrevHistoryId());
			List<DepartmentInformation> listDepartmentNewHist = listDepartmentPrevHist.stream()
					.map(d -> new DepartmentInformation(d.getCompanyId(), d.isDeleteFlag(), param.getNewHistoryId(),
							d.getDepartmentId(), d.getDepartmentCode(), d.getDepartmentName(), d.getDepartmentGeneric(),
							d.getDepartmentDisplayName(), d.getHierarchyCode(), d.getDepartmentExternalCode()))
					.collect(Collectors.toList());
			depInforRepo.addDepartments(listDepartmentNewHist);
		}
		return param.getNewHistoryId();
	}

	/**
	 * 部門構成を更新する
	 * 
	 * @param param
	 */
	public void updateDepartmentConfig(UpdateDepartmentConfigParam param) {
		Optional<DepartmentConfiguration> optDepConfig = depConfigRepo.getDepConfig(param.getCompanyId());
		if (optDepConfig.isPresent()) {
			DepartmentConfiguration depConfig = optDepConfig.get();
			depConfig.items().stream().filter(i -> i.identifier().equals(param.getHistoryId())).findFirst()
					.ifPresent(itemToBeChanged -> {
						depConfig.changeSpan(itemToBeChanged, new DatePeriod(param.getStartDate(), param.getEndDate()));
						depConfigRepo.updateDepartmentConfig(depConfig);
					});
		}
	}

	/**
	 * 部門構成を削除する
	 * 
	 * @param companyId
	 * @param historyId
	 */
	public void deleteDepartmentConfig(String companyId, String historyId) {
		Optional<DepartmentConfiguration> optDepConfig = depConfigRepo.getDepConfig(companyId);
		if (optDepConfig.isPresent()) {
			DepartmentConfiguration depConfig = optDepConfig.get();
			if (depConfig.items().size() == 1) {
				throw new BusinessException("Msg_57");
			}
			depConfig.items().stream().filter(i -> i.identifier().equals(historyId)).findFirst()
					.ifPresent(itemToBeRemoved -> {
						if (!itemToBeRemoved.contains(GeneralDate.ymd(9999, 12, 31))) {
							throw new BusinessException("Msg_55");
						}
						depConfig.remove(itemToBeRemoved);
						depConfig.items().get(0).changeSpan(
								new DatePeriod(depConfig.items().get(0).start(), GeneralDate.ymd(9999, 12, 31)));
						depConfigRepo.deleteDepartmentConfig(companyId, historyId);
						depConfigRepo.updateDepartmentConfig(depConfig);
					});
		}
		depInforRepo.deleteDepartmentInforOfHistory(companyId, historyId);
	}

}
