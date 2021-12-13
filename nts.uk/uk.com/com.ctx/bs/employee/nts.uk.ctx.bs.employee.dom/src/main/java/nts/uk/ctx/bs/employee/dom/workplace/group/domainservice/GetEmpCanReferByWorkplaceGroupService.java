package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
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
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @return
	 */
	public static Map<String, List<String>> getAll(Require require, String employeeId, GeneralDate date, DatePeriod period) {

		return GetEmpCanReferByWorkplaceGroupService.get(require, employeeId, date, period, Collections.emptyList());

	}

	/**
	 * 職場グループを指定して取得する
	 * @param require Require
	 * @param employeeId 年月日
	 * @param date 基準日
	 * @param period 期間
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員ID>
	 */
	public static List<String> getByWorkplaceGroup(
			Require require, String employeeId, GeneralDate date, DatePeriod period, String workplaceGroupId
	) {

		val canReferEmployees = GetEmpCanReferByWorkplaceGroupService.get(require, employeeId, date, period, Arrays.asList(workplaceGroupId));
		return canReferEmployees.getOrDefault(workplaceGroupId, Collections.emptyList());

	}

	/**
	 * 取得する
	 * @param require
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @param workplaceGroupIdList 職場グループIDリスト
	 * @return 職場グループごとの社員IDリスト
	 */
	private static Map<String, List<String>> get(
			Require require, String employeeId, GeneralDate date, DatePeriod period, List<String> workplaceGroupIdList
	) {

		// 参照可能範囲を取得
		Map<String, ScopeReferWorkplaceGroup> canReferRangeMap =
				GetWorkplaceGroupsAndEmpService.getWorkplaceGroup(require, date, employeeId);

		// 取得対象のフィルタリング
		if ( !workplaceGroupIdList.isEmpty() ) {
			// 指定されていない職場グループを除外
			canReferRangeMap = canReferRangeMap.entrySet().stream()
					.filter(entry -> workplaceGroupIdList.contains(entry.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}

		// 参照可能範囲に対応した社員IDリストを取得
		return canReferRangeMap.entrySet().stream()
				.collect(Collectors.toMap(
					Map.Entry::getKey,
					entry -> getEmployeeIdListByReferRange(require, employeeId, period, entry.getKey(), entry.getValue())
				));
	}

	/**
	 * 参照可能範囲で社員リストを取得する
	 * @param require
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param workplaceGroupId 職場グループID
	 * @param referRange 社員参照範囲
	 * @return 社員IDリスト
	 */
	private static List<String> getEmployeeIdListByReferRange(
			Require require, String employeeId, DatePeriod period, String workplaceGroupId, ScopeReferWorkplaceGroup referRange
	) {

		switch (referRange) {
			case ALL_EMPLOYEE:
				return GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, workplaceGroupId).stream()
					.map( EmployeeAffiliation::getEmployeeID )
					.collect(Collectors.toList());

			case ONLY_ME:
			default:
				return Arrays.asList( employeeId );
		}
	}



	public static interface Require
			extends	GetWorkplaceGroupsAndEmpService.Require
				,	GetAllEmpWhoBelongWorkplaceGroupService.Require {
	}

}
