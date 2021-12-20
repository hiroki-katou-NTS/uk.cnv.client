/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.dom.employee.mgndata;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.employee.service.dto.EmployeeIdPersonalIdDto;

public interface EmployeeDataMngInfoRepository {
	void add(EmployeeDataMngInfo domain);

	void update(EmployeeDataMngInfo domain);

	void updateAfterRemove(EmployeeDataMngInfo domain);

	void remove(EmployeeDataMngInfo domain);

	void remove(String sid, String pId);

	EmployeeDataMngInfo findById(String sid, String pId);
	// Lanlt code start

	Optional<EmployeeInfo> findById(String sid);

	List<EmployeeSimpleInfo> findByIds(List<String> lstId);

	Optional<EmployeeInfo> getDepartment(String departmentId, GeneralDate date);

	// Lanlt code end

	List<EmployeeDataMngInfo> findByEmployeeId(String sid);

	List<EmployeeDataMngInfo> findByPersonId(String pid);

	List<EmployeeDataMngInfo> findByCompanyId(String cid);
	
	List<Object[]> findByCompanyIdAndBaseDate(String cid, GeneralDate baseDate);

	Optional<EmployeeDataMngInfo> findByEmpId(String sId);

	// sonnlb code start

	List<EmployeeDataMngInfo> getEmployeeNotDeleteInCompany(String cId, String sCd);

	// sonnlb code end

	void updateRemoveReason(EmployeeDataMngInfo domain);

	List<EmployeeDataMngInfo> getListEmpToDelete(String cid);

	/**
	 * 削除されていない社員を取得
	 * @param empcode
	 * @param cid
	 * @return
	 */
	Optional<EmployeeDataMngInfo> findByEmployeCD(String empcode, String cid);

	// add by duongtv
	/**
	 * Find by list employee id.
	 *
	 * @param companyId
	 *            the company id
	 * @param employeeIds
	 *            the employee ids
	 * @return the list
	 */
	List<EmployeeDataMngInfo> findByListEmployeeId(String companyId, List<String> employeeIds);

	/**
	 * Find by list employee code.
	 *
	 * @param companyId
	 *            the company id
	 * @param employeeCodes
	 *            the employee codes
	 * @return the list
	 */
	List<EmployeeDataMngInfo> findByListEmployeeCode(String companyId, List<String> employeeCodes);

	/**
	 * Get List EmployeeDataMngInfo By List Sid
	 * 
	 * @param listSid
	 * @return
	 */
	List<EmployeeDataMngInfo> findByListEmployeeId(List<String> listSid);

	/**
	 * Get EmployeeDataMngInfo
	 * 
	 * @param cid
	 * @param pid
	 * @return
	 */
	Optional<EmployeeDataMngInfo> findByCidPid(String cid, String pid);

	/**
	 * Req No.125
	 * 
	 * @param cId
	 * @param sCd
	 * @return
	 */
	Optional<EmployeeDataMngInfo> getEmployeeByCidScd(String cId, String sCd);
	
	/**
	 * Req No.18
	 * deletedStatus = 0
	 * @param cId
	 * @param sCd
	 * @return
	 */
	Optional<EmployeeDataMngInfo> getEmployeeNotDel(String cId, String sCd);

	/**
	 * @param companyId
	 * @param startLetters
	 * @return return Optional<Value> if can get data Optional<empty> if it doesn't
	 *         match startLetters
	 */
	Optional<String> findLastEml(String companyId, String startLetters, int length);
	
	/**
	 * Get all Employee By CompanyId, Order by Scd ASC
	 * @param cid
	 * @return
	 */
	List<EmployeeDataMngInfo> getAllByCid(String cid);
	
	int countEmplsByBaseDate(List<String> lstCompID, GeneralDate baseDate);

	/**
	 * Find by cid employee code and deleted status.
	 *
	 * @param cid the cid
	 * @param pid the pid
	 * @param deletedStatus the deleted status
	 * @return the optional
	 */
	Optional<EmployeeDataMngInfo> findByCidEmployeeCodeAndDeletedStatus(String cid, String pid, EmployeeDeletionAttr deletedStatus);
	// request list 515
	List<EmployeeDataMngInfo> findBySidNotDel(List<String> sid);
	/**
	 * @author lanlt
	 * getAllEmpNotDeleteByCid phục vụ một phần cho request 120-1
	 * @param companyId
	 * @param companyId
	 * @return
	 */
	List<EmployeeDataMngInfo> getAllEmpNotDeleteByCid(String companyId);
	
	/**
	 * 個人ID(List)から会社IDに一致する社員に絞り込む 
	 */
	List<EmployeeDataMngInfo> findEmployeesMatchingName(List<String> pid, String companyId);
	
	/**
	 * method Fix performance for 個人ID(List)から会社IDに一致する社員に絞り込む 
	 */
	List<EmployeeDataMngInfo> findEmployeesMatchingName(String keyWord, String companyId);
	
	/**
	 * 会社IDと社員コード(部分一致)から社員IDListを取得する
	 */
	List<EmployeeIdPersonalIdDto> findEmployeePartialMatchCode(String cId, String sCd);

	/**
	 * Get personIds by employeeIds
	 * @param sids
	 * @return List<PerEmpData>
	 * */
	List<PerEmpData> getEmploymentInfos(List<String> sids, GeneralDate baseDate);
	
	/**
	 * update all domain
	 * @param domains
	 * @author lanlt
	 */
	void updateAll(List<EmployeeDataMngInfo> domains);
	
	// request list 596
	List<EmployeeDataMngInfo> findBySidDel(List<String> sid);
	
	Map<String, String> getAllSidAndScdBySids(List<String> sids);

	Optional<EmployeeDataMngInfo> findByScdNotDel(String employeeCd, String companyId);

	// Pub get all Sid
	List<String> getAllSidByCid(String cid);
	
	/**
	 * 個人IDリストから取得する
	 * @param personIdList 個人IDリスト
	 * @return List<社員データ管理情報>
	 */
	List<EmployeeDataMngInfo> getByPersonIdList(List<String> personIdList);
	
}
