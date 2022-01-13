package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

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
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @return List<社員ID>
	 */
	public static List<String> getAll(Require require, String employeeId, GeneralDate date, DatePeriod period) {

		// get
		List<String> employeeGetByWorkplaceGroup = getByWorkplaceGroup(require, employeeId, date, period, Optional.empty());
		List<String> employeeGetByWorkplace = getByWorkplace(require, employeeId, date, period, Optional.empty());

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
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @param targetOrg 対象組織
	 * @return List<社員ID>
	 */
	public static List<String> getByOrg(Require require, String employeeId, GeneralDate date, DatePeriod period, TargetOrgIdenInfor targetOrg) {

		// get
		List<String> employeeIdList;
		switch ( targetOrg.getUnit() ) {
			case WORKPLACE_GROUP:
				employeeIdList = getByWorkplaceGroup(require, employeeId, date, period, targetOrg.getWorkplaceGroupId());
				break;
			case WORKPLACE:
			default:
				employeeIdList = getByWorkplace(require, employeeId, date, period, targetOrg.getWorkplaceId());
				break;
		}

		// sort
		return sortEmployees(require, date, employeeIdList);

	}


	/**
	 * 職場グループ単位で取得する
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員ID>
	 */
	private static List<String> getByWorkplaceGroup (
			Require require, String employeeId, GeneralDate date, DatePeriod period, Optional<String> workplaceGroupId
	) {
		return workplaceGroupId.isPresent()
				? require.getEmpCanReferByWorkplaceGroup(employeeId, date, period, workplaceGroupId.get())
				: require.getAllEmpCanReferByWorkplaceGroup(employeeId, date, period);
	}

	/**
	 * 職場単位で取得する
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @param workplaceId 職場ID
	 * @return
	 */
	private static List<String> getByWorkplace (
			Require require, String employeeId, GeneralDate date, DatePeriod period, Optional<String> workplaceId
	) {

		// create search query
		RegulationInfoEmpQuery query = new RegulationInfoEmpQuery();
		{
			// システム区分: 就業
			query.setSystemType( CCG001SystemType.EMPLOYMENT );
			// 検索参照範囲: 参照可能範囲すべて
			query.setReferenceRange( SearchReferenceRange.ALL_REFERENCE_RANGE );

			// 基準日
			// TODO: 社員範囲検索Queryが修正されるまでは『期間』の終了日を基準日とする
			query.setBaseDate( period.end() );

			// 在職/休職/休業 判定期間
			query.setPeriodStart( period.start() );
			query.setPeriodEnd( period.end() );
		}

		if ( workplaceId.isPresent() ) {
			// 職場の指定あり⇒指定職場でフィルタする
			query.setFilterByWorkplace( true );
			query.setWorkplaceIds(Arrays.asList( workplaceId.get() ));
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
	private static List<String> sortEmployees(Require require, GeneralDate date, List<String> employeeIdList) {

		return require.sortEmployee(employeeIdList, EmployeeSearchCallSystemType.EMPLOYMENT, null, date, null);

	}



	public static interface Require {

		/**
		 * 職場グループで参照可能な所属社員を取得する
		 * @param empId 社員ID
		 * @param date 年月日
		 * @param period 期間
		 * @param workplaceGroupID 職場グループID
		 * @return List<社員ID>
		 */
		List<String> getEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period, String workplaceGroupID);

		/**
		 * 職場グループで参照可能な所属社員をすべて取得する
		 * @param empId 社員ID
		 * @param date 年月日
		 * @param period 期間
		 * @return List<社員ID>
		 */
		List<String> getAllEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period);

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
