/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.query.infra.repository.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.Query;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.infra.entity.employee.mngdata.BsymtEmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.infra.entity.employee.order.BsymtSrchSyaSortCnd;
import nts.uk.ctx.bs.employee.infra.entity.employee.order.BsymtSrchSyaSort;
import nts.uk.ctx.bs.employee.infra.entity.employee.order.BsymtEmployeeOrderPK;
import nts.uk.ctx.bs.person.infra.entity.person.info.BpsmtPerson;
import nts.uk.query.model.employee.CCG001SystemType;
import nts.uk.query.model.employee.EmployeeSearchQuery;
import nts.uk.query.model.employee.RegularSortingType;
import nts.uk.query.model.employee.RegulationInfoEmployee;
import nts.uk.query.model.employee.RegulationInfoEmployeeRepository;
import nts.uk.query.model.employee.SortingConditionOrder;

/**
 * The Class JpaRegulationInfoEmployeeRepository.
 */
@Stateless
public class JpaRegulationInfoEmployeeRepository extends JpaRepository implements RegulationInfoEmployeeRepository {

	/** The Constant LEAVE_ABSENCE_QUOTA_NO. */
	public static final int LEAVE_ABSENCE_QUOTA_NO = 1;

	/** The Constant NOT_DELETED. */
	private static final int NOT_DELETED = 0;

	/** The Constant MAX_WHERE_IN. */
	private static final int MAX_WHERE_IN = 1000;
	
	/** The Constant MAX_PARAMETERS. */
	private static final int MAX_PARAMETERS = 800;
	
	private static String DATE_FORMAT = "yyyy-MM-dd";
	private static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	

	/** The Constant NAME_TYPE. */
	// 現在は、氏名の種類を選択する機能がないので、「ビジネスネーム日本語」固定で
	// => 「氏名カナ」 ＝ 「ビジネスネームカナ」
	private static final int NAME_TYPE = 1;

	private static final String FIND_EMPLOYEE = "SELECT e, p"
			+ " FROM BsymtEmployeeDataMngInfo e"
			+ " LEFT JOIN BpsmtPerson p ON p.bpsmtPersonPk.pId = e.bsymtEmployeeDataMngInfoPk.pId"
			+ " WHERE e.bsymtEmployeeDataMngInfoPk.sId IN :listSid";

	private static final String FIND_WORKPLACE = "SELECT awh.sid, wi.workplaceCode, wi.pk.workplaceId, wi.workplaceName"
			+ " FROM BsymtAffiWorkplaceHist awh"
			+ " LEFT JOIN BsymtAffiWorkplaceHistItem awhi ON awhi.hisId = awh.hisId"
			+ " LEFT JOIN BsymtWorkplaceInfor wi ON wi.pk.workplaceId = awhi.workPlaceId"
			+ " WHERE awh.sid IN :listSid"
			+ " AND awh.strDate <= :refDate"
			+ " AND awh.endDate >= :refDate";
	
	private static final String SELECT_EMPLOYEE =  "SELECT"
			+ " SID, SCD, CLASSIFICATION_CODE, EMP_CD, COM_STR_DATE, JOB_CD, "
			+ " BUSINESS_NAME, WKP_ID, WKP_HIERARCHY_CD, WKP_CD, WKP_NAME, "
            + " DEP_ID, DEP_HIERARCHY_CD, DEP_CD, DEP_NAME, DEP_DELETE_FLAG, "
            + " WKP_DELETE_FLAG "
			+ " FROM EMPLOYEE_DATA_VIEW ";
	
	private static final String EMPTY_LIST = "EMPTY_LIST";
	private static final Integer ELEMENT_300 = 300;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.query.model.EmployeeQueryModelRepository#find(nts.uk.query.model.
	 * EmployeeSearchQuery)
	 */
	@Override
	public List<RegulationInfoEmployee> find(String comId, EmployeeSearchQuery paramQuery) {
		if(paramQuery.getFilterByDepartment() == null){
			paramQuery.setFilterByDepartment(false);
		}
		// max paramenter count = 800
		int countParameter = 0;
		
		// Return empty list if all status of employee = 対象外
		if (!paramQuery.getIncludeIncumbents() && !paramQuery.getIncludeOccupancy() && !paramQuery.getIncludeRetirees()
				&& !paramQuery.getIncludeWorkersOnLeave()) {
			return Collections.emptyList();
		}
		
		// Constructing condition.
		List<String> employmentCodes = new ArrayList<>(Optional.ofNullable(paramQuery.getEmploymentCodes()).orElse(Collections.emptyList()));
		List<String> departmentCodes = new ArrayList<>(Optional.ofNullable(paramQuery.getDepartmentCodes()).orElse(Collections.emptyList()));
		List<String> workplaceCodes = new ArrayList<>(Optional.ofNullable(paramQuery.getWorkplaceCodes()).orElse(Collections.emptyList()));
		List<String> classificationCodes = new ArrayList<>(Optional.ofNullable(paramQuery.getClassificationCodes()).orElse(Collections.emptyList()));
		List<String> jobTitleCodes = new ArrayList<>(Optional.ofNullable(paramQuery.getJobTitleCodes()).orElse(Collections.emptyList()));
		//List<String> worktypeCodes = new ArrayList<>(Optional.ofNullable(paramQuery.getWorktypeCodes()).orElse(Collections.emptyList()));
		List<Integer> closureIds = new ArrayList<>(Optional.ofNullable(paramQuery.getClosureIds()).orElse(Collections.emptyList()));
		GeneralDateTime baseDate = paramQuery.getBaseDate();
		
		StringBuilder selectBuilder = new StringBuilder();

		selectBuilder.append(SELECT_EMPLOYEE);
		
		StringBuilder whereBuilder = new StringBuilder()
											// Add company condition 
											.append(" WHERE ((CID = '" + comId + "')")
											// Add NOT_DELETED condition
											.append(" AND (DEL_STATUS_ATR = " + NOT_DELETED + "))");
		String and = " AND ";
		String not = " NOT ";
		String or = " OR ";
		// Add NOT_DELETED condition
		countParameter += 2;
		// employment condition
		String empCondition = "( EMPLOYMENT_STR_DATE <= rfDate ) AND ( EMPLOYMENT_END_DATE >= rfDate )";
		if (paramQuery.getFilterByEmployment()) {
			// return empty list if condition code list is empty
			if (employmentCodes.isEmpty()) {
				return Collections.emptyList();
			}

			// update query conditions
			whereBuilder.append(and + empCondition);
			countParameter += 2;				
		} else {
			whereBuilder.append(and + " ( EMPLOYMENT_STR_DATE IS NULL " + or + empCondition + ")");
			countParameter += 3;
		}
        // department condition
        String depCondition = "( (DEP_STR_DATE <= rfDate AND DEP_END_DATE >= rfDate)"
				+ and + "( (DEP_CONF_END_DATE <= rfDate AND DEP_CONF_STR_DATE >= rfDate) " 
        		+ or  + " DEP_CONF_STR_DATE IS NULL) )";
        if (paramQuery.getFilterByDepartment()) {
            // return empty list if condition code list is empty
            if (departmentCodes.isEmpty()) {
                return Collections.emptyList();
            }

            // update query conditions
            whereBuilder.append(and + depCondition);
            countParameter += 5;
        } else {
            // conditions.add(cb.or(cb.isNull(root.get(EmployeeDataView_.depStrDate)), depCondition));
            whereBuilder.append(and  + " ( DEP_STR_DATE IS NULL " + or + depCondition + ")");
            countParameter += 6;
        }

		// workplace condition
		String wplCondition = "( (WKP_STR_DATE <= rfDate AND WKP_END_DATE >= rfDate )"
							+ and + "( (WKP_CONF_END_DATE >= rfDate AND WKP_CONF_STR_DATE <= rfDate )"
							+ or + " WKP_CONF_END_DATE IS NULL) )";
		if (paramQuery.getFilterByWorkplace()) {
			// return empty list if condition code list is empty
			if (workplaceCodes.isEmpty()) {
				return Collections.emptyList();
			}

			// update query conditions
			whereBuilder.append(and + wplCondition);
			countParameter += 6;
		} else {
			// conditions.add(cb.or(cb.isNull(root.get(EmployeeDataView_.wplStrDate)), wplCondition));
			whereBuilder.append(and  + " ( WKP_STR_DATE IS NULL " + or + wplCondition + ")");
			countParameter += 7;
		}

		// classification condition
		String clsCondition = "(CLASS_STR_DATE <= rfDate AND CLASS_END_DATE >= rfDate)";
		if (paramQuery.getFilterByClassification()) {
			// return empty list if condition code list is empty
			if (classificationCodes.isEmpty()) {
				return Collections.emptyList();
			}

			// update query conditions
			whereBuilder.append(and + clsCondition);
			countParameter += 2;
		} else {
			whereBuilder.append(and + " (CLASS_STR_DATE IS NULL" + or + clsCondition + ")");
			countParameter += 3;
		}

		// jobtitle condition
		String jobCondition = "((JOB_STR_DATE <= rfDate AND JOB_END_DATE >= rfDate)"
				+ and + "(JOB_INFO_STR_DATE <= rfDate AND JOB_INFO_END_DATE >= rfDate))";
		if (paramQuery.getFilterByJobTitle()) {
			// return empty list if condition code list is empty
			if (jobTitleCodes.isEmpty()) {
				return Collections.emptyList();
			}

			// update query conditions
			whereBuilder.append(and + jobCondition);
			countParameter += 4;
		} else {
			whereBuilder.append(and + "( JOB_STR_DATE IS NULL " + or + jobCondition + ")");
			countParameter += 5;
		}
		if (paramQuery.getSystemType() == CCG001SystemType.EMPLOYMENT.value) {
//			if (paramQuery.getFilterByWorktype()) {
//				// return empty list if condition code list is empty
//				if (worktypeCodes.isEmpty()) {
//					return Collections.emptyList();
//				}
//				
//				// update query conditions
//				whereBuilder.append(" AND (WORK_TYPE_CD in ('" + String.join("','", worktypeCodes) + "')");
//				String workDate = GeneralDate.localDate(baseDate.toLocalDate()).toString(DATE_FORMAT) + " 00:00:00";
//				whereBuilder.append(and + "( WORK_TYPE_STR_DATE <= '" + workDate + "' AND WORK_TYPE_END_DATE >= '"+ workDate +"' ) )");
//
//				countParameter += 2 + worktypeCodes.size();
//			}
			if (paramQuery.getFilterByClosure()) {
				// return empty list if condition code list is empty
				if (closureIds.isEmpty()) {
					return Collections.emptyList();
				}

				// update query conditions
				String closureIdsStr = closureIds.toString(); 
				whereBuilder.append(" AND (CLOSURE_ID in (" + closureIdsStr.substring(1, closureIdsStr.length()-1) + "))");
				countParameter += closureIds.size();

				// check exist before add employment conditions
				if (!paramQuery.getFilterByEmployment()) {
					whereBuilder.append(and + " (EMPLOYMENT_STR_DATE <= rfDate AND EMPLOYMENT_END_DATE >= rfDate) ");
					countParameter += 2;
				}
			}
		}

		// Filter result list by status of employee
		GeneralDateTime retireStart = paramQuery.getRetireStart() == null ? paramQuery.getPeriodStart()
				: paramQuery.getRetireStart();
		GeneralDateTime retireEnd = paramQuery.getRetireEnd() == null ? paramQuery.getPeriodEnd()
				: paramQuery.getRetireEnd();
		GeneralDateTime start = paramQuery.getPeriodStart();
		GeneralDateTime end = paramQuery.getPeriodEnd();
				
		// isWorking
		String isWorking = "((TEMP_ABS_FRAME_NO is null and ABS_STR_DATE is null) or (ABS_STR_DATE > endDate) or (ABS_END_DATE < startDate) )";
		
		// isWorking2 - fix cho trường hợp có nhiều khoảng lịch sử nghỉ bug: 118466
		String isWorking2 = " ((TEMP_ABS_FRAME_NO is null and ABS_STR_DATE is null) OR (SID NOT IN (SELECT DISTINCT SID FROM EMPLOYEE_DATA_VIEW WHERE ABS_STR_DATE <= startDate AND ABS_END_DATE >= endDate AND CID = '" + comId + "' )) ) ";
		
		// is in company
		String isInCompany = " NOT ( COM_STR_DATE > endDate OR COM_END_DATE < startDate) ";

		String incumbentCondition = null;
		String workerOnLeaveCondition = null;
		String occupancyCondition = null;
		String retireCondition = null;
		// includeIncumbents
		if (paramQuery.getIncludeIncumbents()) {
			incumbentCondition = isWorking2 + and + isInCompany;
		}

		// workerOnLeave
		if (paramQuery.getIncludeWorkersOnLeave()) {
			workerOnLeaveCondition = isInCompany + and + not + isWorking + and + "(TEMP_ABS_FRAME_NO = " + LEAVE_ABSENCE_QUOTA_NO + ")";
		}

		// Occupancy
		if (paramQuery.getIncludeOccupancy()) {
			occupancyCondition = isInCompany + and + not + isWorking  + and + "(TEMP_ABS_FRAME_NO <> " + LEAVE_ABSENCE_QUOTA_NO + ")";
		}

		// retire
		if (paramQuery.getIncludeRetirees()) {
			retireCondition = "COM_STR_DATE BETWEEN retireStart AND retireEnd" + and + "COM_END_DATE <> '9999-12-31 00:00:00'";
		}
		if(incumbentCondition != null || workerOnLeaveCondition != null || occupancyCondition != null || retireCondition != null) {
			whereBuilder.append("AND ( 1<>1 ");
			if (incumbentCondition != null) {
				whereBuilder.append(" OR (" + incumbentCondition + ")");
			}
			if(workerOnLeaveCondition != null) {
				whereBuilder.append(" OR (" + workerOnLeaveCondition + ")");
			}
			if(occupancyCondition != null) {
				whereBuilder.append(" OR (" + occupancyCondition + ")");
			}
			if(retireCondition != null) {
				whereBuilder.append(" OR (" + retireCondition + ")");
			}
			whereBuilder.append(")");
		}
		countParameter += 10;

		// sort
		StringBuilder orderBy = new StringBuilder();
		if (paramQuery.getSystemType() != CCG001SystemType.ADMINISTRATOR.value) {
			List<String> orders = this.getOrders(paramQuery.getSystemType(), NAME_TYPE,
					this.getSortConditions(comId, paramQuery.getSystemType(), paramQuery.getSortOrderNo()));
			orderBy.append(" ORDER BY " + String.join(",", orders));
		}
		List<RegulationInfoEmployee> resultList = new ArrayList<>();
		// Fix bug #100057
		if (CollectionUtil.isEmpty(employmentCodes)) {
			employmentCodes.add(EMPTY_LIST);
		}
		if (CollectionUtil.isEmpty(jobTitleCodes)) {
			jobTitleCodes.add(EMPTY_LIST);
		}
		if (CollectionUtil.isEmpty(classificationCodes)) {
			classificationCodes.add(EMPTY_LIST);
		}
		if (CollectionUtil.isEmpty(departmentCodes)) {
			departmentCodes.add(EMPTY_LIST);
		}
		if (CollectionUtil.isEmpty(workplaceCodes)) {
			workplaceCodes.add(EMPTY_LIST);
		}
		// employment condition

		CollectionUtil.split(employmentCodes, ELEMENT_300, splitEmploymentCodes -> {
			// workplace condition
			CollectionUtil.split(jobTitleCodes, ELEMENT_300, splitJobTitleCodes -> {
				// classification condition
				CollectionUtil.split(classificationCodes, ELEMENT_300, splitClassificationCodes -> {
					// jobtitle condition
					CollectionUtil.split(workplaceCodes, ELEMENT_300, splitWorkplaceCodes -> {
                        // department condition
                        CollectionUtil.split(departmentCodes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT - (splitEmploymentCodes.size() + splitJobTitleCodes.size() + splitClassificationCodes.size() + splitWorkplaceCodes.size()), splitDepartmentCodes -> { 

						resultList.addAll(executeQuery(
								splitEmploymentCodes,
								splitDepartmentCodes,
								splitWorkplaceCodes, 
								splitClassificationCodes, 
								splitJobTitleCodes, selectBuilder, whereBuilder, orderBy, 
								baseDate, retireStart, retireEnd, start, end,
								comId, paramQuery));
						});
					});
				});
			});
		});

		// Distinct employee in result list.
		List<RegulationInfoEmployee> resultListDistinct = resultList.stream()
								.filter(this.distinctByKey(RegulationInfoEmployee::getEmployeeID))
								.sorted(Comparator.comparing(RegulationInfoEmployee::getEmployeeCode))
								.collect(Collectors.toList());

		return resultListDistinct;
	}
		
	/**
	 * Gets the sort conditions.
	 *
	 * @param comId the com id
	 * @param systemType the system type
	 * @param sortOrderNo the sort order no
	 * @return the sort conditions
	 */
	private List<SortingConditionOrder> getSortConditions(String comId, Integer systemType, Integer sortOrderNo) {
		if (sortOrderNo == null) {
			return Collections.emptyList();
		}
		BsymtEmployeeOrderPK pk = new BsymtEmployeeOrderPK(comId, sortOrderNo, systemType);
		Optional<BsymtSrchSyaSort> empOrder = this.queryProxy().find(pk, BsymtSrchSyaSort.class);
		List<BsymtSrchSyaSortCnd> conditions = empOrder.isPresent() ? empOrder.get().getLstBsymtEmpOrderCond()
				: Collections.emptyList();

		return conditions.stream().map(cond -> {
			SortingConditionOrder mapped = new SortingConditionOrder();
			mapped.setOrder(cond.getConditionOrder());
			mapped.setType(RegularSortingType.valueOf(cond.getId().getOrderType()));
			return mapped;
		}).sorted(((a, b) -> a.getOrder() - b.getOrder())).collect(Collectors.toList());
	}
	
	/**
	 * Distinct by key.
	 *
	 * @param <T> the generic type
	 * @param keyExtractor the key extractor
	 * @return the java.util.function. predicate
	 */
	private <T> java.util.function.Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}

	private List<String> getCriteriaQueryOfSortingEmployees(String comId, List<String> sIds,
			GeneralDateTime referenceDate, List<SortingConditionOrder> sortConditions) {
		List<String> results = new ArrayList<>();
		
		String sql = buildQuery(comId, sIds, referenceDate, sortConditions);
		
		if(sql.contains("rfDate")) {
			sql = sql.replaceAll("rfDate", "'" + referenceDate.toString(DATE_TIME_FORMAT) + "'");
		}
		Query query = this.getEntityManager()
				.createNativeQuery(sql);

		@SuppressWarnings("unchecked")
		List<String> queryRs = query.getResultList();
		
		for(String res : queryRs) {
			results.add(String.valueOf(res));
		}

		return results.stream().distinct().collect(Collectors.toList());
	}
	
	private String buildQuery(String comId, List<String> sIds,
			GeneralDateTime referenceDate, List<SortingConditionOrder> sortConditions) {
		
		List<RegularSortingType> allSorts = sortConditions.stream().map(s -> s.getType()).collect(Collectors.toList());
		boolean sortEmployment = allSorts.contains(RegularSortingType.EMPLOYMENT);
		boolean sortDepartment = allSorts.contains(RegularSortingType.DEPARTMENT);
		boolean sortWorkplace = allSorts.contains(RegularSortingType.WORKPLACE);
		boolean sortClassification = allSorts.contains(RegularSortingType.CLASSIFICATION);
		boolean sortPosition = allSorts.contains(RegularSortingType.POSITION);
		boolean sortHireDate = allSorts.contains(RegularSortingType.HIRE_DATE);
		boolean sortName = allSorts.contains(RegularSortingType.NAME);
		
		// , empHisItem.EMP_CD, wkpconfinf.HIERARCHY_CD, classHisItem.CLASSIFICATION_CODE, jobseq.DISPORDER, jobInfo.JOB_CD, comHis.START_DATE, person.BUSINESS_NAME_KANA, employee.SCD
		StringBuilder joinBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder()
											.append(" WHERE 1=1")
											// company id
											.append(" AND employee.CID = '" + comId + "'")
											// employee id
											.append(" AND employee.SID in ('" + String.join("','", sIds) + "')");
		List<String> orderBy = new ArrayList<>();
		// orderBy.add("employee.SID");
		// Employment info
		if (sortEmployment) {
			joinBuilder.append(" LEFT JOIN BSYMT_AFF_EMP_HIST empHis ON employee.SID = empHis.SID AND employee.CID = empHis.CID")
				   .append(" LEFT JOIN BSYMT_AFF_EMP_HIST_ITEM empHisItem ON empHisItem.HIST_ID = empHis.HIST_ID");
			whereBuilder.append(" AND rfDate BETWEEN empHis.START_DATE AND empHis.END_DATE");
			orderBy.add("empHisItem.EMP_CD");
		}
		// department info
		if (sortDepartment) {
			joinBuilder.append(" LEFT JOIN BSYMT_AFF_DEP_HIST depHis ON employee.SID = depHis.SID AND employee.CID = depHis.CID")
					.append(" LEFT JOIN BSYMT_AFF_DEP_HIST_ITEM depHisItem ON depHis.HIST_ID = depHisItem.HIST_ID")
					.append(" LEFT JOIN BSYMT_DEP_INFO depInfo ON depHisItem.DEP_ID = depInfo.DEP_ID")
					.append(" LEFT JOIN BSYMT_DEP_CONFIG depconf ON depconf.DEP_HIST_ID = depInfo.DEP_HIST_ID AND depInfo.CID = depconf.CID");
			orderBy.add("depInfo.HIERARCHY_CD");
		}
		// Workplace info
		if (sortWorkplace) {
			joinBuilder.append(" LEFT JOIN BSYMT_AFF_WKP_HIST wplHis ON employee.SID = wplHis.SID AND employee.CID = wplHis.CID")
				   .append(" LEFT JOIN BSYMT_AFF_WKP_HIST_ITEM wplHisItem ON wplHis.HIST_ID = wplHisItem.HIST_ID")
				   .append(" LEFT JOIN BSYMT_WKP_INFO wkpInfo ON wplHisItem.WORKPLACE_ID = wkpInfo.WKP_ID")
				   .append(" LEFT JOIN BSYMT_WKP_CONFIG_2 wkpconf ON wkpconf.WKP_HIST_ID = wkpInfo.WKP_HIST_ID AND wkpconf.CID = wkpInfo.CID");
			whereBuilder.append(" AND rfDate BETWEEN wplHis.START_DATE AND wplHis.END_DATE")
						.append(" AND rfDate BETWEEN wkpconf.START_DATE AND wkpconf.END_DATE");
			orderBy.add("wkpInfo.HIERARCHY_CD");
		}
		// Classification info
		if (sortClassification) {
			joinBuilder.append(" LEFT JOIN BSYMT_AFF_CLASS_HIST classHis ON employee.SID = classHis.SID AND employee.CID = classHis.CID")
				   .append(" LEFT JOIN BSYMT_AFF_CLASS_HIST_ITEM classHisItem ON classHis.HIST_ID = classHisItem.HIST_ID");
			whereBuilder.append(" AND rfDate BETWEEN classHis.START_DATE AND classHis.END_DATE");
			orderBy.add("classHisItem.CLASSIFICATION_CODE");
			
		}
		// JobTitle Info
		if(sortPosition) {
			joinBuilder.append(" LEFT JOIN BSYMT_AFF_JOB_HIST jobHis ON employee.SID = jobHis.SID AND employee.CID = jobHis.CID")
				   .append(" LEFT JOIN BSYMT_AFF_JOB_HIST_ITEM jobHisItem ON jobHis.HIST_ID = jobHisItem.HIST_ID")
				   .append(" LEFT JOIN BSYMT_JOB_INFO jobInfo ON jobHisItem.JOB_TITLE_ID = jobInfo.JOB_ID")
				   .append(" LEFT JOIN BSYMT_JOB_HIST jobInfoHis ON jobInfo.HIST_ID = jobInfoHis.HIST_ID AND jobInfo.CID = jobInfoHis.CID")
				   .append(" LEFT JOIN BSYMT_JOB_RANK jobseq ON jobseq.SEQ_CD = jobInfo.SEQUENCE_CD AND jobseq.CID = jobInfo.CID");
			whereBuilder.append(" AND rfDate BETWEEN jobHis.START_DATE AND jobHis.END_DATE")
						.append(" AND rfDate BETWEEN jobInfoHis.START_DATE AND jobInfoHis.END_DATE");
			orderBy.add("jobseq.DISPORDER");
			orderBy.add("jobInfo.JOB_CD");
		}
		// Com hist
		if(sortHireDate) {
			joinBuilder.append(" LEFT JOIN BSYMT_AFF_COM_HIST comHis ON employee.SID = comHis.SID AND comHis.CID = employee.CID");
			orderBy.add("comHis.START_DATE");
		}
		// name
		if(sortName) {
			orderBy.add("person.BUSINESS_NAME_KANA");
		}
		// default
		orderBy.add("employee.SCD");
		
		StringBuilder selectBuilder = new StringBuilder();
		selectBuilder.append("SELECT employee.SID ")
			   .append(" FROM BSYMT_SYAIN employee")
			   .append(" LEFT JOIN BPSMT_PERSON person ON employee.PID = person.PID");
		
		return selectBuilder.toString() + joinBuilder.toString() + whereBuilder.toString() + " ORDER BY " + String.join(",", orderBy);
		
	}
	
	@AllArgsConstructor
	@Data
	public class EmployeeDataSort {
		private String sid;
		private String employmentCd;
		private String wplHierarchyCd;
		private String classificationCd;
		private String jobSeqDisp;
		private String jobCd;
		// hire date
		private GeneralDateTime comStrDate;
		private String businessNameKana;
		private String scd;

	}

	private List<String> getOrders(Integer systemType, Integer nameType, List<SortingConditionOrder> sortConditions) {
		
		sortConditions.sort((a,b) -> a.getOrder() - b.getOrder());
		Iterator<SortingConditionOrder> iterator = sortConditions.iterator();

		List<String> orders = new ArrayList<>();

		while (iterator.hasNext()) {
			SortingConditionOrder cond = iterator.next();
			switch (cond.getType()) {
			case EMPLOYMENT: // EMPLOYMENT
				orders.add("EMP_CD ASC");
				break;
			case DEPARTMENT: // DEPARTMENT
				// TODO: not covered
				break;
			case WORKPLACE: // WORKPLACE
				orders.add("WKP_HIERARCHY_CD ASC");
				break;
			case CLASSIFICATION: // CLASSIFICATION
				orders.add("CLASSIFICATION_CODE ASC");
				break;
			case POSITION: // POSITION
				orders.add("JOB_SEQ_DISP ASC");
				orders.add("JOB_CD ASC");
				break;
			case HIRE_DATE: // HIRE_DATE
				orders.add("COM_STR_DATE ASC");
				break;
			case NAME: // NAME
				// 現在は、氏名の種類を選択する機能がないので、「ビジネスネーム日本語」固定で
				// => 「氏名カナ」 ＝ 「ビジネスネームカナ」
				orders.add("BUSINESS_NAME_KANA ASC");
				break;
			}
		}

		// sort by worktype code
		if (systemType == CCG001SystemType.EMPLOYMENT.value) {
			// Katou said: その資料の記載は現在未対応の「並び替え機能」で使用するものです。
			// 現状は全システム区分で「社員コード（ASC）」でソートしてもらえませんでしょうか？
			// => hien tai chua doi ung cai nay
			// TODO: not covered
			// orders.add(cb.asc(root.get(EmployeeDataView_.workTypeCd)));
		}

		// always sort by employee code
		orders.add("SCD ASC");
		return orders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.query.model.employee.RegulationInfoEmployeeRepository#
	 * findInfoBySIds(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RegulationInfoEmployee> findInfoBySIds(List<String> sIds, GeneralDate referenceDate) {
		List<Object[]> persons = new ArrayList<>();

		CollectionUtil.split(sIds, MAX_WHERE_IN, (subList) -> {
			persons.addAll(this.getEntityManager().createQuery(FIND_EMPLOYEE)
					.setParameter("listSid", subList)
					.getResultList());
		});

		// get list employee
		Map<String, RegulationInfoEmployee> employeeInfoList = persons.stream().map(item -> {
			BsymtEmployeeDataMngInfo e = (BsymtEmployeeDataMngInfo) item[0];
			BpsmtPerson p = (BpsmtPerson) item[1];

			return RegulationInfoEmployee.builder()
					.employeeID(e.bsymtEmployeeDataMngInfoPk.sId)
					.employeeCode(e.employeeCode)
					.name(Optional.ofNullable(p.businessName))
					.classificationCode(Optional.empty())
					.departmentCode(Optional.empty())
					.employmentCode(Optional.empty())
					.hireDate(Optional.empty())
					.jobTitleCode(Optional.empty())
					.workplaceHierarchyCode(Optional.empty())
					.workplaceCode(Optional.empty())
					.workplaceId(Optional.empty())
					.workplaceName(Optional.empty())
					.build();
		}).collect(Collectors.toMap(RegulationInfoEmployee::getEmployeeID, v -> v, (oldValue, newValue) -> newValue));

		// Get workplace
		List<Object[]> workplaces = new ArrayList<>();
		CollectionUtil.split(sIds, MAX_WHERE_IN, (subList) -> {
			workplaces.addAll(this.getEntityManager().createQuery(FIND_WORKPLACE).setParameter("listSid", subList)
					.setParameter("refDate", referenceDate).getResultList());
		});

		// set workplace
		employeeInfoList.keySet().forEach(empId -> {
			Optional<Object[]> workplace = workplaces.stream().filter(wpl -> {
				String id = (String) wpl[0];
				return id.equals(empId);
			}).findAny();

			if (workplace.isPresent()) {
				String workplaceCode = (String) workplace.get()[1];
				String workplaceId = (String) workplace.get()[2];
				String workplaceName = (String) workplace.get()[3];

				RegulationInfoEmployee employee = employeeInfoList.get(empId); 
				employee.setWorkplaceCode(Optional.ofNullable(workplaceCode));
				employee.setWorkplaceId(Optional.ofNullable(workplaceId));
				employee.setWorkplaceName(Optional.ofNullable(workplaceName));
			}
		});

		return employeeInfoList.values().stream().collect(Collectors.toList());
	}
	
	
	/* (non-Javadoc)
	 * @see nts.uk.query.model.employee.RegulationInfoEmployeeRepository
	 * #findBySid(java.lang.String, java.lang.String, nts.arc.time.GeneralDateTime)
	 */
	@Override
	public RegulationInfoEmployee findBySid(String comId, String sid, GeneralDate baseDate, int systemType) {
		
		StringBuilder selectBuilder = new StringBuilder();
		selectBuilder.append(SELECT_EMPLOYEE);
//		// Constructing condition.
//		// Add company condition
//		// Add NOT_DELETED condition
		StringBuilder whereBuilder = new StringBuilder()
				// Add company condition 
				.append(" WHERE ((CID = '" + comId + "')")
				// Add NOT_DELETED condition
				.append(" AND (DEL_STATUS_ATR = " + NOT_DELETED + "))");
		// Where SID.
		whereBuilder.append(" AND (SID = '" + sid + "')");
		
        if (systemType == CCG001SystemType.SALARY.value) {
            // Department.
        	 whereBuilder.append(" AND (DEP_STR_DATE <= baseDate AND DEP_END_DATE >= baseDate )");
        } else {
            // Workplace.
            whereBuilder.append(" AND (WKP_STR_DATE <= baseDate AND WKP_END_DATE >= baseDate )");
        }

		// Find fist.
		String sql = selectBuilder.toString() + whereBuilder.toString();
		
		if(sql.contains("baseDate")) {
			sql = sql.replaceAll("baseDate", "'" + baseDate.toString(DATE_FORMAT) + "'");
		}
		
		Query query = this.getEntityManager()
				.createNativeQuery(sql);
		
		@SuppressWarnings("unchecked")
		List<Object[]> queryRs = query.getResultList();

		if(CollectionUtil.isEmpty(queryRs)) {
			return null;
		}
		
		// Convert.
		return convertToEmployeeInfo(queryRs.get(0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.query.model.employee.RegulationInfoEmployeeRepository#
	 * sortEmployees(java.lang.String, java.util.List, java.lang.Integer,
	 * java.lang.Integer, java.lang.Integer, nts.arc.time.GeneralDateTime)
	 */
	@Override
	public List<String> sortEmployees(String comId, List<String> sIds, Integer systemType, Integer orderNo,
			Integer nameType, GeneralDateTime referenceDate) {
		if (sIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> resultList = new ArrayList<>();
		List<SortingConditionOrder> sortConditions = this.getSortConditions(comId, systemType, orderNo);
		CollectionUtil.split(sIds, MAX_PARAMETERS, splitData -> {
			resultList.addAll(this.getCriteriaQueryOfSortingEmployees(comId, splitData, referenceDate, sortConditions));
		});

		
		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.query.model.employee.RegulationInfoEmployeeRepository#
	 * sortEmployees(java.lang.String, java.util.List, java.util.List,
	 * nts.arc.time.GeneralDateTime)
	 */
	@Override
	public List<String> sortEmployees(String comId, List<String> sIds, List<SortingConditionOrder> orders,
			GeneralDateTime referenceDate) {
		if (sIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> resultList = new ArrayList<>();
		CollectionUtil.split(sIds, MAX_PARAMETERS, splitData -> {
			resultList.addAll(this.getCriteriaQueryOfSortingEmployees(comId, splitData, referenceDate, orders));
		});

		return resultList;
	}
	
	/**
	 * Execute query.
	 *
	 * @param getFilterByEmployment the get filter by employment
	 * @param splitEmploymentCodes the split employment codes
	 * @param getFilterByWorkplace the get filter by workplace
	 * @param splitWorkplaceCodes the split workplace codes
	 * @param getFilterByClassification the get filter by classification
	 * @param splitClassificationCodes the split classification codes
	 * @param getFilterByJobTitle the get filter by job title
	 * @param splitJobTitleCodes the split job title codes
	 * @param conditions the conditions
	 * @param cb the cb
	 * @param cq the cq
	 * @param comId the com id
	 * @param paramQuery the param query
	 * @param em the em
	 * @param root the root
	 * @return the list
	 */

	private List<RegulationInfoEmployee> executeQuery( List<String> splitEmploymentCodes, List<String> splitDepartmentCodes,
								List<String> splitWorkplaceCodes, 
								List<String> splitClassificationCodes, 
								List<String> splitJobTitleCodes,
								StringBuilder selectBuilder,
								StringBuilder whereBuilderP,
								StringBuilder orderBuilder,
								GeneralDateTime baseDate, GeneralDateTime retireStart, GeneralDateTime retireEnd, GeneralDateTime start, GeneralDateTime end, 
								String comId, EmployeeSearchQuery paramQuery) {
		// clear where builder
		StringBuilder whereBuilder = new StringBuilder(whereBuilderP.toString());
		// employment condition
		if (paramQuery.getFilterByEmployment()) {
			if (splitEmploymentCodes.size() == 1 && splitEmploymentCodes.get(0).compareTo(EMPTY_LIST) == 0) {
				splitEmploymentCodes.clear();
			}
			whereBuilder.append(" AND (EMP_CD IN ('" + String.join("','", splitEmploymentCodes) + "'))" );
		}

        // department condition
        if (paramQuery.getFilterByDepartment()) {
            if (splitDepartmentCodes.size() == 1 && splitDepartmentCodes.get(0).compareTo(EMPTY_LIST) == 0) {
                splitDepartmentCodes.clear();
            }
//            conditions.add(root.get(EmployeeDataView_.depId).in(splitDepartmentCodes));
            whereBuilder.append(" AND (DEP_ID IN ('" + String.join("','", splitDepartmentCodes) + "'))" );
        }

		
		// workplace condition
		if (paramQuery.getFilterByWorkplace()) {
			if (splitWorkplaceCodes.size() == 1 && splitWorkplaceCodes.get(0).compareTo(EMPTY_LIST) == 0) {
				splitWorkplaceCodes.clear();
			}
			whereBuilder.append(" AND (WKP_ID IN ('" + String.join("','", splitWorkplaceCodes) + "'))" );
		}
		
		// classification condition
		if (paramQuery.getFilterByClassification()) {
			if (splitClassificationCodes.size() == 1 && splitClassificationCodes.get(0).compareTo(EMPTY_LIST) == 0) {
				splitClassificationCodes.clear();
			}
			whereBuilder.append(" AND (CLASSIFICATION_CODE IN ('" + String.join("','", splitClassificationCodes) + "'))" );
		}
		
		// jobtitle condition
		if (paramQuery.getFilterByJobTitle()) { 
			if (splitJobTitleCodes.size() == 1 && splitJobTitleCodes.get(0).compareTo(EMPTY_LIST) == 0) {
				splitJobTitleCodes.clear();
			}
			whereBuilder.append(" AND (JOB_TITLE_ID IN ('" + String.join("','", splitJobTitleCodes) + "'))" );
		}
		
		List<RegulationInfoEmployee> resultListInFunc = new ArrayList<>();

		String sql = selectBuilder.toString() + whereBuilder.toString() + orderBuilder.toString();
		if(sql.contains("rfDate")) {
			sql = sql.replaceAll("rfDate", "'" + baseDate.toString(DATE_TIME_FORMAT) + "'");
		}
		if(sql.contains("startDate")) {
			sql = sql.replaceAll("startDate", "'" + start.toString(DATE_TIME_FORMAT) + "'");
		}
		if(sql.contains("endDate")) {
			sql = sql.replaceAll("endDate", "'" + end.toString(DATE_TIME_FORMAT) + "'");
		}
		if(sql.contains("retireStart")) {
			sql = sql.replaceAll("retireStart", "'" + retireStart.toString(DATE_TIME_FORMAT) + "'");
		}
		if(sql.contains("retireEnd")) {
			sql = sql.replaceAll("retireEnd", "'" + retireEnd.toString(DATE_TIME_FORMAT) + "'");
		}
		System.out.println(sql);
		Query query = this.getEntityManager()
				.createNativeQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> queryRs = query.getResultList();
		
		for(Object[] res : queryRs) {
			resultListInFunc.add(convertToEmployeeInfo(res));
		}
				
		return resultListInFunc;
	}
	
	private RegulationInfoEmployee convertToEmployeeInfo(Object[] res) {
		return RegulationInfoEmployee.builder()
				.employeeID(String.valueOf(res[0]))
				.employeeCode(String.valueOf(res[1]))
				.classificationCode(Optional.ofNullable(res[2] != null ? String.valueOf(res[2]) : null))
				.employmentCode(Optional.ofNullable(res[3] != null ? String.valueOf(res[3]) : null))
				.hireDate(Optional.ofNullable(res[4] != null ? GeneralDateTime.fromString(String.valueOf(res[4]), "yyyy-MM-dd HH:mm:ss.S")  : null))
				.jobTitleCode(Optional.ofNullable(res[5] != null ? String.valueOf(res[5]) : null))
				.name(Optional.ofNullable(res[6] != null ? String.valueOf(res[6]) : null))
				.workplaceId(Optional.ofNullable(res[7] != null ? String.valueOf(res[7]) : null))
				.workplaceHierarchyCode(Optional.ofNullable(res[8] != null ? String.valueOf(res[8]) : null))
				.workplaceCode(Optional.ofNullable(res[9] != null ? String.valueOf(res[9]) : null))
				.workplaceName(Optional.ofNullable(res[10] != null ? String.valueOf(res[10]) : null))
				.workplaceDeleteFlag(Optional.ofNullable(res[15] != null ? Boolean.valueOf(String.valueOf(res[14])) : null))
				.departmentId(Optional.ofNullable(res[11] != null ? String.valueOf(res[11]) : null))
				.departmentHierarchyCode(Optional.ofNullable(res[12] != null ? String.valueOf(res[12]) : null))
				.departmentCode(Optional.ofNullable(res[13] != null ? String.valueOf(res[13]) : null))
				.departmentName(Optional.ofNullable(res[14] != null ? String.valueOf(res[14]) : null))
				.departmentDeleteFlag(Optional.ofNullable(res[14] != null ? Boolean.valueOf(String.valueOf(res[14])) : null))
				.build();
	}
}
