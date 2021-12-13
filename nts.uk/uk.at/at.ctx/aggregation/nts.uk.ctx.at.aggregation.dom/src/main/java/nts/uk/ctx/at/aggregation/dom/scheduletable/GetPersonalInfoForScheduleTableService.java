package nts.uk.ctx.at.aggregation.dom.scheduletable;

import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.aggregation.dom.adapter.rank.EmployeeRankInfoImported;
import nts.uk.ctx.at.aggregation.dom.adapter.team.EmployeeTeamInfoImported;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpLicenseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.GetEmpLicenseClassificationService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.EmployeeInfoWantToBeGet;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmployeeInfoImported;

/**
 * スケジュール表の個人情報を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.スケジュール表の個人情報を取得する
 * @author dan_pv
 */
public class GetPersonalInfoForScheduleTableService {
	
	/**
	 * 取得する	
	 * @param require
	 * @param employeeIdList 社員リスト
	 * @param baseDate 基準日
	 * @param personalItemList 個人情報項目リスト
	 * @return
	 */
	public static List<ScheduleTablePersonalInfo> get(
			Require require,
			List<String> employeeIdList,
			GeneralDate baseDate,
			List<ScheduleTablePersonalInfoItem> personalItemList
			) {
		
		val employeeInfoList = getEmployeeInfo(require, employeeIdList, baseDate, personalItemList);
		val employeeTeamInfoList = getEmployeeTeamInfo(require, employeeIdList, personalItemList);
		val employeeRankInfoList = getEmployeeRankInfo(require, employeeIdList, personalItemList);
		val employeeLicenseClassList = getEmployeeLicenseClass(require, employeeIdList, baseDate, personalItemList);
		
		return employeeIdList.stream().map( employeeId -> {
			// employee's information
			EmployeeInfoImported employeeInfo = employeeInfoList.stream()
					.filter( x -> x.getEmployeeId().equals(employeeId))
					.findFirst().orElse(null);
			// employee's team
			EmployeeTeamInfoImported employeeTeamInfo = employeeTeamInfoList.stream()
					.filter( x -> x.getEmployeeID().equals(employeeId))
					.findFirst().orElse(null);
			// employee's rank
			EmployeeRankInfoImported employeeRankInfo = employeeRankInfoList.stream()
					.filter( x -> x.getEmployeeID().equals(employeeId))
					.findFirst().orElse(null);
			// employee' license classification
			EmpLicenseClassification employeeLicenseClass = employeeLicenseClassList.stream()
					.filter( x -> x.getEmpID().equals(employeeId))
					.findFirst().orElse(null);
			
			return ScheduleTablePersonalInfo.create(employeeId, employeeInfo, employeeTeamInfo, employeeRankInfo, employeeLicenseClass);
			
		}).collect(Collectors.toList());
	}
	
	/**
	 * 社員情報を取得する
	 * @param require
	 * @param employeeIds 社員リスト
	 * @param baseDate 基準日
	 * @param personalItemList 個人情報項目リスト
	 * @return
	 */
	private static List<EmployeeInfoImported> getEmployeeInfo(
			Require require, 
			List<String> employeeIds, 
			GeneralDate baseDate,
			List<ScheduleTablePersonalInfoItem> personalItemList) {
		
		EmployeeInfoWantToBeGet param = new EmployeeInfoWantToBeGet(
				false, 
				false, 
				personalItemList.contains(ScheduleTablePersonalInfoItem.JOBTITLE),
				personalItemList.contains(ScheduleTablePersonalInfoItem.EMPLOYMENT),
				personalItemList.contains(ScheduleTablePersonalInfoItem.CLASSIFICATION));
		
		return require.getEmployeeInfo(employeeIds, baseDate, param);
	}
	
	/**
	 * 社員所属チーム情報を取得する
	 * @param require
	 * @param employeeIds 社員リスト
	 * @param personalItemList 個人情報項目リスト
	 * @return
	 */
	private static List<EmployeeTeamInfoImported> getEmployeeTeamInfo(
			Require require, 
			List<String> employeeIds, 
			List<ScheduleTablePersonalInfoItem> personalItemList) {
		
		if ( !personalItemList.contains(ScheduleTablePersonalInfoItem.TEAM)) {
			
			return employeeIds.stream()
					.map(id -> EmployeeTeamInfoImported.createWithEmpty(id))
					.collect(Collectors.toList());
		}
		
		return require.getEmployeeTeamInfo(employeeIds);
	}
	
	/**
	 * 社員ランク情報を取得する
	 * @param require
	 * @param employeeIds 社員リスト
	 * @param personalItemList 個人情報項目リスト
	 * @return
	 */
	private static List<EmployeeRankInfoImported> getEmployeeRankInfo (
			Require require, 
			List<String> employeeIds, 
			List<ScheduleTablePersonalInfoItem> personalItemList) {
		
		if ( !personalItemList.contains(ScheduleTablePersonalInfoItem.RANK) ) {
			
			return employeeIds.stream()
					.map(id -> EmployeeRankInfoImported.createWithEmpty(id))
					.collect(Collectors.toList());
		}
		
		return require.getEmployeeRankInfo(employeeIds);
		
		
	}
	
	/**
	 * 社員免許区分を取得する
	 * @param require
	 * @param employeeIds 社員リスト
	 * @param baseDate 基準日
	 * @param personalItemList 個人情報項目リスト
	 * @return
	 */
	private static List<EmpLicenseClassification> getEmployeeLicenseClass (
			Require require, 
			List<String> employeeIds, 
			GeneralDate baseDate,
			List<ScheduleTablePersonalInfoItem> personalItemList) {
		
		if ( !personalItemList.contains(ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION) ) {
			
			return employeeIds.stream()
					.map(id -> EmpLicenseClassification.empLicenseClassification(id))
					.collect(Collectors.toList());
		}
		
		return GetEmpLicenseClassificationService.get(require, baseDate, employeeIds);
	}
			
	
	public static interface Require extends GetEmpLicenseClassificationService.Require {
		
		/**
		 * 社員の情報を取得する
		 * @param employeeIds 社員リスト
		 * @param baseDate 基準日
		 * @param param 取得したい社員情報
		 * @return
		 */
		List<EmployeeInfoImported> getEmployeeInfo(List<String> employeeIds, GeneralDate baseDate, EmployeeInfoWantToBeGet param);
		
		/**
		 * 所属スケジュールチーム情報を取得する
		 * @param employeeIds 社員リスト
		 * @return
		 */
		List<EmployeeTeamInfoImported> getEmployeeTeamInfo(List<String> employeeIds);
		
		/**
		 * 社員ランク情報を取得する
		 * @param employeeIds 社員リスト
		 * @return
		 */
		List<EmployeeRankInfoImported> getEmployeeRankInfo(List<String> employeeIds);
		
	}

}
