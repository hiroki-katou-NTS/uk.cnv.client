package nts.uk.ctx.sys.auth.pub.employee;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;

public interface EmployeePublisher {
	/** RequestList338 */
	Optional<NarrowEmpByReferenceRange> findByEmpId(List<String> sID, int roleType, GeneralDate referenceDate);

	/** RequestList314 */
	Optional<EmpWithRangeLogin> findByCompanyIDAndEmpCD(String companyID, String employeeCD);
	
	/** RequestList315 */
	Optional<EmpWithRangeLogin> getByComIDAndEmpCD(String companyID , String employeeCD , GeneralDate referenceDate);
	
	/** RequestList334 [No.334]基準日、指定社員から参照可能な職場リストを取得する **/
	List<String> getListWorkPlaceID(String employeeID , GeneralDate referenceDate);
	
	/** RequestList218 [No.218]職場、基準日からアラーム通知先の社員を取得する  **/
	List<String> getListEmployeeId(String workplaceId, GeneralDate referenceDate);
	
	/** RequestList526 [No.526]就業担当者(社員IDList)を取得する **/
	List<String> getListEmpID(String companyID , GeneralDate referenceDate);
	
	/** [RQ.653]職場リスト、基準日から就業確定できる社員を取得する **/
	Map<String, List<String>> getListEmpInfo(String companyID , GeneralDate referenceDate, List<String> workplaceID);

	/**
	 * Request list 727 ([No.727])
	 * 職場IDから、アラームメールの受信を許可されている管理者を取得する
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.Export.[No.727]職場IDから、アラームメールの受信を許可されている管理者を取得する.[No.727]職場IDから、アラームメールの受信を許可されている管理者を取得する
	 */
	Map<String, List<String>> getManagersByWorkplaceIds(List<String> workplaceIds);
	
	
	public static interface RequireRQ653 {
		
		/**
		 * 職場管理者を取得(List<職場ID>、基準日)
		 * @param employeeID
		 * @param baseDate
		 * @return List<WorkplaceManagerDto>
		 */
		List<WorkplaceManagerDto> getWorkplaceManager(List<String> workPlaceIds, GeneralDate baseDate );

		/**
		 * 所属職場権限を取得(会社ID、ロールID、機能NO)
		 * @param roleId
		 * @param companyId
		 * @param functionNo
		 * @return WorkPlaceAuthority
		 */
		Optional<WorkPlaceAuthorityDto> getWorkAuthority(String companyId, String roleId,  Integer functionNo);
		
	}
	
}
