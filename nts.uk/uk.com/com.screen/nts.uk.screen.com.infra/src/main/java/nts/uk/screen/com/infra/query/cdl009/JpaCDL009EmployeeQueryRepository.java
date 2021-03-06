/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.screen.com.infra.query.cdl009;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.screen.com.app.find.cdl009.CDL009EmployeeQueryRepository;
import nts.uk.screen.com.app.find.cdl009.EmployeeSearchOutput;
import nts.uk.screen.com.app.find.cdl009.SearchEmpInput;

/**
 * The Class JpaCDL009EmployeeQueryRepository.
 */
@Stateless
public class JpaCDL009EmployeeQueryRepository extends JpaRepository implements CDL009EmployeeQueryRepository{
	
	/** The Constant LEAVE_ABSENCE_QUOTA_NO. */
	public static final int LEAVE_ABSENCE_QUOTA_NO = 1;

	/** The Constant SEARCH_BY_WORKPLACE. */
	private static final String SEARCH_BY_WORKPLACE = "SELECT e.bsymtEmployeeDataMngInfoPk.sId, e.employeeCode, p.businessName, wi.workplaceName, "
			+ "ach.startDate, ach.endDate, absHis.startDate, absHis.endDate, absHisItem.tempAbsFrameNo "
			+ "FROM BsymtAffiWorkplaceHist awh "
			+ "LEFT JOIN BsymtAffCompanyHist ach ON ach.bsymtAffCompanyHistPk.sId = awh.sid "
			+ "LEFT JOIN BsymtTempAbsHist absHis ON e.bsymtEmployeeDataMngInfoPk.sId = absHis.sid "
			+ "LEFT JOIN BsymtTempAbsHistItem absHisItem ON absHis.histId = absHisItem.histId "
			+ "LEFT JOIN BsymtAffiWorkplaceHistItem awhi ON awh.hisId = awhi.hisId "
			+ "LEFT JOIN BsymtWorkplaceInfor wi ON wi.pk.workplaceId = awhi.workPlaceId "
			+ "LEFT JOIN BsymtEmployeeDataMngInfo e ON awh.sid = e.bsymtEmployeeDataMngInfoPk.sId "
			+ "LEFT JOIN BpsmtPerson p ON e.bsymtEmployeeDataMngInfoPk.pId = p.bpsmtPersonPk.pId "
			+ "WHERE awhi.workPlaceId IN :wplIds "
			+ "AND awh.strDate <= :refDate "
			+ "AND awh.endDate >= :refDate "
			+ "AND e.delStatus = 0";
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.screen.com.app.find.cdl009.CDL009EmployeeQueryRepository#
	 * searchEmpByWorkplaceList(nts.uk.screen.com.app.find.cdl009.
	 * SearchEmpInput)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeeSearchOutput> searchEmpByWorkplaceList(SearchEmpInput input) {

		List<EmployeeSearchOutput> result = new ArrayList<>();

		if (CollectionUtil.isEmpty(input.getEmpStatus()) || CollectionUtil.isEmpty(input.getWorkplaceIdList())) {
			return Collections.emptyList();
		}

		List<Object[]> employees = new ArrayList<>();
		CollectionUtil.split(input.getWorkplaceIdList(), DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			employees.addAll(this.getEntityManager().createQuery(SEARCH_BY_WORKPLACE).setParameter("wplIds", subList)
					.setParameter("refDate", input.getReferenceDate()).getResultList());
		});

		result = employees.parallelStream().filter(employee -> input.getEmpStatus().stream().anyMatch(status -> {
			GeneralDate refDate = input.getReferenceDate();
			GeneralDate comStart = (GeneralDate) employee[4];
			GeneralDate comEnd = (GeneralDate) employee[5];
			GeneralDate absStart = (GeneralDate) employee[6];
			GeneralDate absEnd = (GeneralDate) employee[7];
			Integer absNo = (Integer) employee[8];

			Boolean isWorking = (comStart.beforeOrEquals(refDate) && comEnd.afterOrEquals(refDate))
					&& (absStart == null || (absStart.afterOrEquals(refDate) || absEnd.beforeOrEquals(refDate)));
			switch (StatusOfEmployment.valueOf(status)) {
			case INCUMBENT:
				return isWorking;
			case HOLIDAY:
				return !isWorking && comEnd.afterOrEquals(refDate) && absNo != LEAVE_ABSENCE_QUOTA_NO;
			case LEAVE_OF_ABSENCE:
				return !isWorking && comEnd.afterOrEquals(refDate) && absNo == LEAVE_ABSENCE_QUOTA_NO;
			case RETIREMENT:
				return comEnd.before(refDate);
			default:
				return false;
			}
		})).map(filteredItem -> {
			return EmployeeSearchOutput.builder().employeeId((String) filteredItem[0])
					.employeeCode((String) filteredItem[1]).employeeName((String) filteredItem[2])
					.workplaceName((String) filteredItem[3]).build();
		}).distinct().collect(Collectors.toList());
		
		Set<EmployeeSearchOutput> employeeSearch = result.stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmployeeSearchOutput::getEmployeeId))));
		
		result = employeeSearch.stream().collect(Collectors.toList());
		
		return result;

	}
}
