package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeCode;
import nts.uk.ctx.bs.employee.dom.workplace.EmployeeAffiliation;

/**
 * 職場グループに所属する社員をすべて取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.職場.職場グループ.職場グループに所属する社員をすべて取得する
 * @author HieuLt
 */
public class GetAllEmpWhoBelongWorkplaceGroupService {

	/**
	 * 取得する
	 * @param require Require
	 * @param period 期間
	 * @param workplaceGroupId 職場グループID
	 * @return
	 */
	public static List<EmployeeAffiliation> getAllEmp(Require require, DatePeriod period, String workplaceGroupId) {

		// 職場グループに所属する職場を取得
		val workplaceIds = require.getWorkplaceBelongsWorkplaceGroup(workplaceGroupId);
		// 職場に所属する社員を取得
		return workplaceIds.stream()
			.map( e -> GetAllEmpWhoBelongWorkplaceGroupService.createListEmpOrganizations(require, period, workplaceGroupId, e) )
			.flatMap( List::stream )
			.collect(Collectors.toList());

	}


	/**
	 * 社員の所属組織リストを作成する
	 * @param require Require
	 * @param period 対象期間
	 * @param workplaceGroupId 職場グループID
	 * @param workplaceId 職場ID
	 * @return 社員の所属組織リスト
	 */
	private static List<EmployeeAffiliation> createListEmpOrganizations(
			Require require, DatePeriod period, String workplaceGroupId, String workplaceId
	){

		// 対象期間中に指定職場に所属している社員を取得
		val empInfoDatas = require.getEmployeesWhoBelongWorkplace(workplaceId, period);
		// 『社員の所属組織』に変換する
		return empInfoDatas.stream()
				.map(x -> EmployeeAffiliation.create(
						x.getEmpId()
					,	new EmployeeCode(x.getEmpCd())
					,	x.getEmpName()
					,	workplaceId
					,	workplaceGroupId
				) ).collect(Collectors.toList());

	}



	public static interface Require {

		/**
		 * 職場グループに所属する職場を取得する
		 * @param workplaceGroupId 職場グループID
		 * @return 職場IDリスト
		 */
		List<String> getWorkplaceBelongsWorkplaceGroup(String workplaceGroupId);

		/**
		 * 職場の所属社員を取得する
		 * @param workplaceId 職場ID
		 * @param datePeriod 期間
		 * @return 社員情報リスト
		 */
		List<EmployeeInfoData> getEmployeesWhoBelongWorkplace(String workplaceId, DatePeriod datePeriod);

	}

}