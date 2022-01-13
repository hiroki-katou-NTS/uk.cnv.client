/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.pub.employee;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * The Interface EmployeePub.
 */
public interface SyEmployeePub {

	/**
	 * Find by wpk ids.
	 *
	 * @param companyId the company id
	 * @param workplaceIds the workplace ids
	 * @param baseDate the base date
	 * @return the list
	 */
	List<EmployeeExport> findByWpkIds(String companyId, List<String> workplaceIds, GeneralDate baseDate);

	/**
	 * Gets the concurrent employee.
	 *
	 * @param companyId the company id
	 * @param jobId the job id
	 * @param baseDate the base date
	 * @return the concurrent employee
	 */
	// RequestList77
	List<ConcurrentEmployeeExport> getConcurrentEmployee(String companyId, String jobId, GeneralDate baseDate);

	/**
	 * Find by emp id.
	 *
	 * @param sId the emp id
	 * @return the employee export
	 */
	// RequestList #1-2
	EmployeeBasicInfoExport findBySId(String sId);
	EmployeeBasicInfoExport findBySIdRequire(CacheCarrier cacheCarrier, String sId);

	/**
	 * Find by emp ids.
	 * 社員IDのListを基に社員情報のListを返してほしいです。
	 * @param sIds the s ids
	 * @return the list
	 */
	// RequestList126
	List<EmployeeBasicInfoExport> findBySIds(List<String> sIds);


	/** Get list employees in the workplace by baseDate and employeeID
	 * @param sid
	 * @param baseDate
	 * @return
	 */
	//  RequestList #243
	List<String> GetListSid(String sid , GeneralDate baseDate);


	/**
	 * Get List EmployeeCode
	 * @param sid
	 * @param basedate
	 * @return
	 */
	// RequestList #212
	List<String> getEmployeeCode(String sid , GeneralDate basedate);

	/**
	 * Gets the sdata mng info.
	 *
	 * @param sid the sid
	 * @return the sdata mng info
	 */
	// Redmine #87534
	Optional<EmployeeDataMngInfoExport> getSdataMngInfo(String sid);

	/**
	 *
	 * 削除されていない社員を取得
	 *
	 * @param companyId
	 * @param employeeCode
	 * @return
	 */
	Optional<EmployeeDataMngInfoExport> getSdataMngInfoByEmployeeCode(String companyId, String employeeCode);

	/**
	 * 社員コードから社員データ管理情報を取得する
	 * @param companyId 会社ID
	 * @param employeeCodes 社員コード(List)
	 * @return 社員データ管理情報(List)
	 */
	List<EmployeeDataMngInfoExport> findSdataMngInfoByEmployeeCodes(String companyId, List<String> employeeCodes);

	/**
	 * Find by emp ids.
	 * 社員ID（List）から社員コードと表示名を取得
	 * @param sIds the sids
	 * @return the list
	 */
	// RequestList228
	List<EmployeeInfoExport> getByListSid(List<String> sIds);


	/**
	 * Get List EmployeeId By WorkPlace and Employment and TemporaryLeaveAbsenceHistory
	 * @param sIds
	 * @return
	 */
	// RequestList335
	List<String> getListEmpByWkpAndEmpt(List<String> wkps , List<String> lstempts , DatePeriod dateperiod);

	/**
	 * Get List EmployeeId By CompanyId
	 * @param sIds
	 * @return
	 */
	// RequestList52
	List<EmpOfLoginCompanyExport> getListEmpOfLoginCompany(String cid);

	/**
	 * Find by emp id.
	 *
	 * @param sId the emp id
	 * @return the employee export
	 */
	// RequestList377
	EmployeeBasicExport getEmpBasicBySId(String sId);

	/**
	 * RequestList
	 * @param sid
	 * @return status of Employee
	 */
	StatusOfEmployeeExport getStatusOfEmployee(String sid);

	/**
	 * 社員が削除されたかを取得
	 * RequestList248
	 * @param sid
	 * @return
	 */
	boolean isEmployeeDelete(String sid);

	/**
	 * 社員ID(List)から個人社員基本情報を取得
	 * @param lstSid
	 * @return
	 */
	// RequestList61
	List<EmpInfoExport> getEmpInfo(List<String> lstSid);

	/**
	 * 期間内に特定の職場（List）に所属している社員一覧を取得
	 * RequestList462, RequestList466 trỏ đến RequestList462
	 * @param wkpIds
	 * @param dateperiod
	 * @return list sid đang làm việc
	 */
	List<String> getListEmployeeId(List<String> wkpIds, DatePeriod dateperiod);

	/**
	 * Find by S id and company id.
	 *
	 * @param sId the s id
	 * @param comanpyId the comanpy id
	 * @return the employee basic info export
	 */
	EmployeeBasicInfoExport findBySIdAndCompanyId(String sId, String comanpyId);

	/**
	 * 職位ID（List）と基準日から該当する社員一覧を取得する
	 * RequestList515, RequestList466 trỏ đến RequestList515
	 */
	List<String> getListEmployee(List<String> jobTitleIds, GeneralDate baseDate);

	/**
	 * RequestList101
	 * @param cid
	 * @param pid
	 */
	Optional<EmpInfoRegistered> getEmpInfo(String cid, String pid);

	/**
	 * Gets the sid cd pname by S ids.
	 *
	 * @param sIdsInput the s ids input
	 * @return the sid cd pname by S ids
	 */
	// RequestList126-2
	List<EmployeIdCdPnameExport> getSidCdPnameBySIds(List<String> sIdsInput);

	/**
	 * RequestList546
	 * 会社IDから使用する休業枠（休職を除く）を取得する
	 * @param cid
	 * @return
	 */
	List<TempAbsenceFrameExport> getTempAbsenceFrameByCid(String cid);

	/**
	 * [RQ614]社員CD、ビジネスネーム、カナから検索キーワードをもとに社員IDを取得する
	 */
	List<EmpInfo614> findEmpByKeyWordsListSid(EmpInfo614Param param);

	/**
	 * RequestList596 削除された社員を取り除く
	 * @param sids
	 * @return
	 */
	List<ResultRequest596Export> getEmpDeletedLstBySids(List<String> sids);

	/**
	 * RequestList600 社員ID（List）から社員コードと表示名を取得（削除社員考慮）
	 * @param sids - 社員一覧　：　List＜社員ID＞
	 * @param isDelete - 削除社員を取り除く：boolean
	 * @param period -  期間：期間
	 * @param isGetAffCompany - 会社に所属していない社員を取り除く：boolean
	 * @return
	 */
	List<ResultRequest600Export> getEmpInfoLstBySids(List<String> sids, DatePeriod period, boolean isDelete, boolean isGetAffCompany);

	/**
	 * RequestList493
	 * 社員（List）と期間から１日でも在職している社員を取得する
	 * @param sids
	 * @param period
	 * @return
	 */
	List<String> filterSidLstByDatePeriodAndSids(List<String> sids, DatePeriod period);

	/**
	 * 期間内に特定の会社に所属している社員一覧を取得する
	 * @param cid
	 * @param period
	 * @return
	 */
	List<String> filterSidByCidAndPeriod(String cid, DatePeriod period);

	Map<String, String> getAllSidAndScdBySids(List<String> sid);

	/**
	 *
	 * 社員IDから個人社員基本情報を取得
	 * @param sid
	 * @return
	 */
	PersonInfoJhn001Export getEmployeeInfo(String sid);

	/**
	 * request list 515
	 *
	 * @param sid
	 * @return
	 */
	List<EmployeeDataMngInfoExport> findBySidNotDel(List<String> sids);

	List<ResultRequest596Export> getEmpNotDeletedLstBySids(List<String> sids);

	Optional<EmployeeDataMngInfoExport> findByScdNotDel(String employeeCd, String companyId);
	
	/**
	 * 個人IDから個人社員情報を取得する
	 * @param personIds 個人IDリスト
	 * @return List<個人社員情報Exported>
	 */
	List<PersonalEmployeeInfoExport> getPersonEmployeeInfosByPersonId(List<String> personIds);
	
}
