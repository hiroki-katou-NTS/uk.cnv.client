/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.pubimp.employee.classification;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.ctx.bs.employee.dom.employment.affiliate.AffEmploymentHistory;
import nts.uk.ctx.bs.employee.dom.employment.affiliate.AffEmploymentHistoryRepository;
import nts.uk.ctx.bs.employee.pub.employee.employment.SyEmploymentPub;

/**
 * The Class SyEmploymentPubImp.
 */
@Stateless
public class SyEmploymentPubImp implements SyEmploymentPub {

	/** The employment adapter. */
	@Inject
	private EmploymentRepository employmentAdapter;

	/** The employment history repository. */
	@Inject
	private AffEmploymentHistoryRepository employmentHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.basic.pub.company.organization.employee.EmployeePub#
	 * getEmployeeCode(java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public String getEmploymentCode(String companyId, String employeeId, GeneralDate baseDate) {
		// Query
		List<AffEmploymentHistory> affEmploymentHistories = employmentHistoryRepository
				.searchEmploymentOfSids(Arrays.asList(employeeId), baseDate);

		List<String> employmentCodes = affEmploymentHistories.stream()
				.map(item -> item.getEmploymentCode().v()).collect(Collectors.toList());

		List<Employment> acEmploymentDtos = employmentAdapter.findByEmpCodes(employmentCodes);

		Map<String, String> comEmpMap = acEmploymentDtos.stream()
				.collect(Collectors.toMap((item) -> {
					return item.getCompanyId().v();
				}, (item) -> {
					return item.getEmploymentCode().v();
				}));

		// Return EmploymentCode
		return comEmpMap.get(companyId);
	}

}
