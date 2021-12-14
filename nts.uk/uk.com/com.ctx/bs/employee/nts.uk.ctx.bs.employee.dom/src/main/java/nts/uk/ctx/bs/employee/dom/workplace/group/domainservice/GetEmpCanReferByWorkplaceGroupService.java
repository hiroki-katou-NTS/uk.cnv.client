package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.EmployeeAffiliation;

/**
 * 職場グループ単位で参照可能な社員を取得する	
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".基幹.社員.職場.職場グループ.職場グループ単位で参照可能な社員を取得する
 * @author HieuLt
 * 
 * OldName: GetEmpCanReferBySpecifyWorkgroupService
 */
public class GetEmpCanReferByWorkplaceGroupService {
	
	/**
	 * すべて取得する
	 * @param require
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @return
	 */
	public static Map<String, List<String>> getAll(
			Require require,
			GeneralDate date,
			String employeeId) {
		
		return get(require, date, employeeId, Collections.emptyList());
	}
	
	/**
	 * 職場グループを指定して取得する
	 * @param require
	 * @param date 基準日
	 * @param employeeId 年月日
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員ID>
	 */
	public static List<String> getByWorkplaceGroup(
			Require require,
			GeneralDate date,
			String employeeId,
			String workplaceGroupId) {
		
		val canReferEmployees = get(require, date, employeeId, Arrays.asList(workplaceGroupId));
		return canReferEmployees.getOrDefault(workplaceGroupId, Collections.emptyList());

	}
	
	/**
	 * 取得する
	 * @param require
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @param workplaceGroupIdList 職場グループIDリスト
	 * @return
	 */
	private static Map<String, List<String>> get(
			Require require, 
			GeneralDate date,
			String employeeId,
			List<String> workplaceGroupIdList) {
		
		Map<String, ScopeReferWorkplaceGroup> canReferRangeMap = 
				GetWorkplaceGroupsAndEmpService.getWorkplaceGroup(require, date, employeeId);
		
		if ( !workplaceGroupIdList.isEmpty() ) {
			canReferRangeMap = canReferRangeMap.entrySet().stream()
					.filter(entry -> workplaceGroupIdList.contains(entry.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		
		return canReferRangeMap.entrySet().stream()
				.collect(Collectors.toMap(
					Map.Entry::getKey, 
					entry -> getEmployeeIdListByReferRange(require, date, employeeId, entry.getKey(), entry.getValue())));
	}
	
	/**
	 * 参照可能範囲で社員リストを取得する
	 * @param require
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @param workplaceGroupId 職場グループID
	 * @param referRange 社員参照範囲
	 * @return
	 */
	private static List<String> getEmployeeIdListByReferRange(
			Require require, 
			GeneralDate date,
			String employeeId,
			String workplaceGroupId,
			ScopeReferWorkplaceGroup referRange
			) {
		
		switch (referRange) {
			case ALL_EMPLOYEE:
				return GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, date, workplaceGroupId).stream()
					.map( EmployeeAffiliation::getEmployeeID)
					.collect(Collectors.toList());
			
			case ONLY_ME:
			default:
				return Arrays.asList(employeeId);
		}
	}
	
	
	public static interface Require
			extends GetWorkplaceGroupsAndEmpService.Require, GetAllEmpWhoBelongWorkplaceGroupService.Require {
	}
}
