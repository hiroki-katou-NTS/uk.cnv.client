package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeCode;
import nts.uk.ctx.bs.employee.dom.workplace.EmployeeAffiliation;

/**
 * 職場グループに所属する社員をすべて取得する
 * @author HieuLt
 *
 */
public class GetAllEmpWhoBelongWorkplaceGroupService {
	
	/**
	 * [1] 取得する	
	 * @param require
	 * @param baseDate
	 * @param workplaceGroupId
	 * @return
	 */
	public static List<EmployeeAffiliation> getAllEmp(Require require ,GeneralDate baseDate , String workplaceGroupId ){
		//$対象期間 = 期間( 基準日, 基準日 ) 
		DatePeriod targetDatePeriod = new DatePeriod(baseDate, baseDate);
		//$職場IDリスト = require.職場グループに所属する職場を取得する( 職場グループID )
		List<String> lstWorkplaceGroupId = require.getWorkplaceBelongsWorkplaceGroup(workplaceGroupId);
		List<EmployeeAffiliation> result = new ArrayList<>();
		 lstWorkplaceGroupId.stream().map(c -> result.addAll(createListEmpOrganizations(require, targetDatePeriod ,workplaceGroupId,c)))
		.collect(Collectors.toList());
		return result;
	}
	/**
	 * [prv-1] 社員の所属組織リストを作成する
	 * @param require
	 * @param datePeriod
	 * @param workplaceGroupId
	 * @param workplaceId
	 * @return
	 */
	private static List<EmployeeAffiliation> createListEmpOrganizations(Require require, DatePeriod datePeriod ,String workplaceGroupId , String workplaceId){
		//	$社員情報リスト = require.職場の所属社員を取得する( 職場ID, 対象期間 )		
		List<EmployeeInfoData> data = require.getEmployeesWhoBelongWorkplace(workplaceId, datePeriod);
		List<EmployeeAffiliation> result = data.stream().map(x -> new EmployeeAffiliation(
				x.getEmpId(),
				Optional.ofNullable(new EmployeeCode(x.getEmpCd())),
				Optional.ofNullable(x.getEmpName()),
				workplaceId,
				Optional.ofNullable(workplaceGroupId))).collect(Collectors.toList());
		return result;
	}
	
	public static interface Require {
		/**
		 * [R-1] 職場グループに所属する職場を取得する
		 * 職場グループ所属情報Repository.職場グループに所属する職場を取得する( 会社ID, 職場グループID )		
		 * @param workplaceGroupId
		 * @return
		 */
		List<String> getWorkplaceBelongsWorkplaceGroup(String workplaceGroupId);
		
		/**
		 * [R-2] 職場の所属社員を取得する	
		 * アルゴリズム.職場の所属社員を取得する( 職場ID, 期間 )		
		 * @param workplaceId
		 * @param datePeriod
		 * @return
		 */
		List<EmployeeInfoData> getEmployeesWhoBelongWorkplace(String workplaceId, DatePeriod datePeriod);
	}

}
