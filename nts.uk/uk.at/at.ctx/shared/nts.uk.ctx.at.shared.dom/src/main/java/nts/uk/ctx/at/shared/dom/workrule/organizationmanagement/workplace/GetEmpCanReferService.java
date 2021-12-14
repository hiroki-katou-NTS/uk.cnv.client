package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;

/**
 * 参照可能な社員を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.職場.参照可能な社員を取得する
 * @author Hieult
 * 
 * OldName: GetEmpCanReferBySpecOrganizationService
 */
public class GetEmpCanReferService {
	
	/**
	 * すべて取得する
	 * @param require
	 * @param date 年月日
	 * @param employeeId 社員ID
	 * @return List<社員ID>
	 */
	public static List<String> getAll(Require require, GeneralDate date, String employeeId) {
		
		// get
		List<String> employeeGetByWorkplaceGroup = getByWorkplaceGroup(require, date, employeeId, Optional.empty());
		List<String> employeeGetByWorkplace = getByWorkplace(require, date, employeeId, Optional.empty());
		
		// merge -> distinct
		List<String> employeeIdList = Arrays.asList(employeeGetByWorkplaceGroup, employeeGetByWorkplace).stream()
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
		
		// sort
		return sortEmployees(require, date, employeeIdList);
	}
		
	/**
	 * 組織を指定して取得する		
	 * @param require
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @param targetOrg 対象組織
	 * @return List<社員ID>
	 */
	public static List<String> getByOrg(Require require, GeneralDate date, String employeeId, TargetOrgIdenInfor targetOrg){
		
		// get
		List<String> employeeIdList;
		switch ( targetOrg.getUnit() ) {
			case WORKPLACE_GROUP:
				employeeIdList = getByWorkplaceGroup(require, date, employeeId, targetOrg.getWorkplaceGroupId() );
				break;
			case WORKPLACE:
			default:
				employeeIdList = getByWorkplace(require, date, employeeId, targetOrg.getWorkplaceId());
				break;
		}
		
		// sort
		return sortEmployees(require, date, employeeIdList);
	}
	
	/**
	 * 職場グループ単位で取得する	
	 * @param require
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員ID>
	 */
	private static List<String> getByWorkplaceGroup (
			Require require,
			GeneralDate date,
			String employeeId,
			Optional<String> workplaceGroupId ){
		
		List<String> employeeIdList = workplaceGroupId.isPresent() ? 
				require.getEmpCanReferByWorkplaceGroup(date, employeeId, workplaceGroupId.get()) :
				require.getAllEmpCanReferByWorkplaceGroup(date, employeeId);
				
		return employeeIdList;
	}
	
	/**
	 * 職場単位で取得する	
	 * @param require
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @param workplaceId 職場ID
	 * @return
	 */
	private static List<String> getByWorkplace (
			Require require,
			GeneralDate date,
			String employeeId,
			Optional<String> workplaceId){
		
		// create search query
		RegulationInfoEmpQuery query = new RegulationInfoEmpQuery();
		query.setBaseDate(date);
		query.setSystemType(CCG001SystemType.EMPLOYMENT); // 就業
		query.setReferenceRange(SearchReferenceRange.ALL_REFERENCE_RANGE); // 参照可能範囲すべて

		if ( workplaceId.isPresent() ) {
			query.setFilterByWorkplace(true);
			query.setWorkplaceIds( Arrays.asList(workplaceId.get()) );
		}
		
		// search
		return require.searchEmployee(query, require.getRoleID());
	}
	
	/**
	 * 社員を並び替える
	 * @param require
	 * @param date 基準日
	 * @param employeeIdList 社員IDリスト
	 * @return List<社員ID>
	 */
	private static List<String> sortEmployees (
			Require require,
			GeneralDate date,
			List<String> employeeIdList){
		
		return require.sortEmployee(employeeIdList, EmployeeSearchCallSystemType.EMPLOYMENT, null, date, null);
	}
	
	public static interface Require {
		
		/**
		 * 職場グループで参照可能な所属社員を取得する
		 * @param date 年月日
		 * @param empId 社員ID
		 * @param workplaceGroupID 職場グループID
		 * @return List<社員ID>
		 */
		List<String> getEmpCanReferByWorkplaceGroup(GeneralDate date, String empId, String workplaceGroupID);
		
		/**
		 * 職場グループで参照可能な所属社員をすべて取得する
		 * @param date 年月日
		 * @param empId 社員ID
		 * @return List<社員ID>
		 */
		List<String> getAllEmpCanReferByWorkplaceGroup(GeneralDate date, String empId);
		
		/**
		 * 社員を並び替える
		 * @param employeeIdList 社員IDリスト
		 * @param systemType システム区分
		 * @param sortOrderNo 並び順NO
		 * @param date 年月日
		 * @param nameType 氏名の種類
		 * @return List<社員ID>
		 */
		List<String> sortEmployee(List<String> employeeIdList, EmployeeSearchCallSystemType systemType, Integer sortOrderNo, GeneralDate date, Integer nameType);
		
		/**
		 * ロールIDを取得する
		 * @return ロールID
		 */
		String getRoleID();
		
		/**
		 * 社員を検索する
		 * @param regulationInfoEmpQuery 社員検索の規定条件
		 * @param roleId ロールID
		 * @return List<社員ID>
		 */
		List<String> searchEmployee(RegulationInfoEmpQuery regulationInfoEmpQuery, String roleId);
	}
	
	
}
