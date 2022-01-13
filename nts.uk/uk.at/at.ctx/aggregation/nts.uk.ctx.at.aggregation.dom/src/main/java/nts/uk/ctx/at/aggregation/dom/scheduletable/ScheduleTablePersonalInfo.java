package nts.uk.ctx.at.aggregation.dom.scheduletable;

import java.util.HashMap;
import java.util.Map;

import lombok.Value;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.uk.ctx.at.aggregation.dom.adapter.rank.EmployeeRankInfoImported;
import nts.uk.ctx.at.aggregation.dom.adapter.team.EmployeeTeamInfoImported;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpLicenseClassification;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmployeeInfoImported;

/**
 * スケジュール表の個人情報
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.スケジュール表の個人情報
 * @author dan_pv
 */
@Value
public class ScheduleTablePersonalInfo {
	
	/**
	 * 社員ID
	 */
	private final String employeeId;
	
	/**
	 * 個人情報Map
	 */
	private final Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap;
	
	/**
	 * 作る
	 * @param employeeId 社員ID
	 * @param employeeInfo 社員情報
	 * @param employeeTeamInfo 社員所属チーム情報
	 * @param employeeRankInfo 社員ランク情報
	 * @param employeeLicenseClass 社員免許区分
	 * @return
	 */
	public static ScheduleTablePersonalInfo create(
			String employeeId,
			EmployeeInfoImported employeeInfo,
			EmployeeTeamInfoImported employeeTeamInfo,
			EmployeeRankInfoImported employeeRankInfo,
			EmpLicenseClassification employeeLicenseClass
			) {
		
		Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap = new HashMap<>();
		
		addEmployeeName(personalInfoMap, employeeInfo);
		addEmployment(personalInfoMap, employeeInfo);
		addJobTitle(personalInfoMap, employeeInfo);
		addClassification(personalInfoMap, employeeInfo);
		addTeam(personalInfoMap, employeeTeamInfo);
		addRank(personalInfoMap, employeeRankInfo);
		addNurseClassification(personalInfoMap, employeeLicenseClass);
		
		return new ScheduleTablePersonalInfo(employeeId, personalInfoMap);
	}
	
	/**
	 * 社員名を追加する
	 * @param personalInfoMap
	 * @param employeeInfo 社員情報
	 */
	private static void addEmployeeName(
			Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap,
			EmployeeInfoImported employeeInfo) {

		personalInfoMap.put(
				ScheduleTablePersonalInfoItem.EMPLOYEE_NAME,
				new ScheduleTablePersonalInfoItemData(
						employeeInfo.getEmployeeCode(),
						employeeInfo.getBusinessName()));
	}
	
	/**
	 * 雇用を追加する
	 * @param personalInfoMap
	 * @param employeeInfo 社員情報
	 */
	private static void addEmployment(
			Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap,
			EmployeeInfoImported employeeInfo) {
		
		if ( employeeInfo.getEmployment().isPresent() ) {
			
			personalInfoMap.put(
					ScheduleTablePersonalInfoItem.EMPLOYMENT, 
					new ScheduleTablePersonalInfoItemData(
							employeeInfo.getEmployment().get().getEmploymentCode(), 
							employeeInfo.getEmployment().get().getEmploymentName()));
		}
	}
	
	/**
	 * 職位を追加する
	 * @param personalInfoMap
	 * @param employeeInfo 社員情報
	 */
	private static void addJobTitle(
			Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap,
			EmployeeInfoImported employeeInfo) {
		
		if ( employeeInfo.getPosition().isPresent() ) {
			
			personalInfoMap.put(
					ScheduleTablePersonalInfoItem.JOBTITLE, 
					new ScheduleTablePersonalInfoItemData(
							employeeInfo.getPosition().get().getPositionCode(), 
							employeeInfo.getPosition().get().getPositionName()));
		}
	}
	
	/**
	 * 分類を追加する
	 * @param personalInfoMap
	 * @param employeeInfo 社員情報
	 */
	private static void addClassification(
			Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap,
			EmployeeInfoImported employeeInfo) {
		
		if ( employeeInfo.getClassification().isPresent() ) {
			
			personalInfoMap.put(
					ScheduleTablePersonalInfoItem.CLASSIFICATION, 
					new ScheduleTablePersonalInfoItemData(
							employeeInfo.getClassification().get().getClassificationCode(), 
							employeeInfo.getClassification().get().getClassificationName()));
		}
	}
	
	/**
	 * チームを追加する
	 * @param personalInfoMap
	 * @param employeeTeamInfo 社員所属チーム情報
	 */
	private static void addTeam(
			Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap,
			EmployeeTeamInfoImported employeeTeamInfo) {
		
		if ( employeeTeamInfo != null && employeeTeamInfo.getTeamCode().isPresent() && employeeTeamInfo.getTeamName().isPresent() ) {
			
			personalInfoMap.put(
					ScheduleTablePersonalInfoItem.TEAM,
					new ScheduleTablePersonalInfoItemData(
							employeeTeamInfo.getTeamCode().get(), 
							employeeTeamInfo.getTeamName().get()));
		}
	}
	
	/**
	 * ランクを追加する
	 * @param personalInfoMap
	 * @param employeeRankInfo 社員ランク情報
	 */
	private static void addRank(
			Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap,
			EmployeeRankInfoImported employeeRankInfo) {
		
		if ( employeeRankInfo != null && employeeRankInfo.getRankCode().isPresent() && employeeRankInfo.getRankSymbol().isPresent() ) {
			
			personalInfoMap.put(
					ScheduleTablePersonalInfoItem.RANK, 
					new ScheduleTablePersonalInfoItemData(
							employeeRankInfo.getRankCode().get(), 
							employeeRankInfo.getRankSymbol().get()));
		}
	}
	
	/**
	 * 看護区分を追加する
	 * @param personalInfoMap
	 * @param employeeLicenseClass 社員免許区分
	 */
	private static void addNurseClassification(
			Map<ScheduleTablePersonalInfoItem, ScheduleTablePersonalInfoItemData> personalInfoMap,
			EmpLicenseClassification employeeLicenseClass) {
		
		if ( employeeLicenseClass != null && employeeLicenseClass.getOptLicenseClassification().isPresent() ){
			
			EnumConstant constant = EnumAdaptor.convertToValueName(
					employeeLicenseClass.getOptLicenseClassification().get());
			
			personalInfoMap.put(
					ScheduleTablePersonalInfoItem.NURSE_CLASSIFICATION, 
					new ScheduleTablePersonalInfoItemData(
							String.valueOf(constant.getValue()), 
							constant.getLocalizedName()));
		}
	}
	
}
