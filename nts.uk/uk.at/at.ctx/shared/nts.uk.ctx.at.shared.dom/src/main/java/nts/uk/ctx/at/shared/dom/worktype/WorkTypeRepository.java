/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktype;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Interface WorkTypeRepository.
 */
public interface WorkTypeRepository {
	
	Map<String , String> getCodeNameWorkType(String companyId, List<String> listWorktypeCode);

	/**
	 * Gets the possible work type.
	 *
	 * @param companyId the company id
	 * @param lstPossible the lst possible
	 * @return the possible work type
	 */
	List<WorkType> getPossibleWorkType(String companyId, List<String> lstPossible);
	
	/**
	 * Gets the possible work type and order.
	 *
	 * @param companyId the company id
	 * @param lstPossible the lst possible
	 * @return the possible work type and order
	 */
	List<WorkTypeInfor> getPossibleWorkTypeAndOrder(String companyId, List<String> lstPossible);
	
	/**
	 * Gets the possible work type and order.
	 *
	 * @param companyId the company id
	 * @param lstPossible the lst possible
	 * @return the possible work type and order
	 */
	List<WorkTypeInfor> getPossibleWorkTypeWithNoMasterAndOrder(String companyId, List<String> lstPossible);
	
	/**
	 * Find all by order.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<WorkTypeInfor> findAllByOrder(String companyId);

	/**
	 * Gets the possible work type with join.
	 *
	 * @param companyId the company id
	 * @param lstPossible the lst possible
	 * @return the possible work type
	 */
	List<WorkType> getPossibleWorkTypeV2(String companyId, List<String> lstPossible);
	
	/**
	 * Find by company id.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<WorkType> findByCompanyId(String companyId);
	
	/**
	 * Find by company id and leave absence.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<WorkType> findByCompanyIdAndLeaveAbsence(String companyId);
	
	
	/**
	 * Find not deprecate by company id.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	
	List<WorkType> findNotDeprecateByCompanyId(String companyId);
	
	/**
	 * Find code and name of work type by company id.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	
	List<List<String>> findCodeAndNameOfWorkTypeByCompanyId(String companyId);

	/**
	 * Find not deprecated.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<WorkType> findNotDeprecated(String companyId);

	/**
	 * Find not deprecated by list code.
	 *
	 * @param companyId the company id
	 * @param codes the codes
	 * @return the list
	 */
	List<WorkType> findNotDeprecatedByListCode(String companyId, List<String> codes);
	
	/**
	 * Find all by list code.
	 *
	 * @param companyId the company id
	 * @param codes the codes
	 * @return the list
	 */
	List<WorkType> findAllByListCode(String companyId, List<String> codes);
	
	/**
	 * Find work type.
	 *
	 * @param companyId the company id
	 * @param abolishAtr the abolish atr
	 * @param allDayAtrs the all day atrs
	 * @param halfAtrs the half atrs
	 * @return the list
	 */
	List<WorkType> findWorkType(String companyId, int abolishAtr, List<Integer> allDayAtrs, List<Integer> halfAtrs);
	
	/**
	 * Find by PK.
	 *
	 * @param companyId the company id
	 * @param workTypeCd the work type cd
	 * @return the optional
	 */
	Optional<WorkType> findByPK(String companyId, String workTypeCd);
	
	/**
	 * Find NoAbolish by PK.
	 *
	 * @param companyId the company id
	 * @param workTypeCd the work type cd
	 * @return the optional
	 */
	Optional<WorkType> findNoAbolishByPK(String companyId, String workTypeCd);
	
	/**
	 * Find work type set.
	 *
	 * @param companyId the company id
	 * @param workTypeCode the work type code
	 * @return the list
	 */
	List<WorkTypeSet> findWorkTypeSet(String companyId, String workTypeCode);
	
	/**
	 * Find work type set close atr and deprecateAtr.
	 *
	 * @param companyId the company id
	 * @param closeAtr the close atr
	 * @return the list
	 */
	List<WorkTypeSet> findWorkTypeSetCloseAtrDeprecateAtr(String companyId, int closeAtr, int deprecateAtr);
	
	/**
	 * 
	 * @param companyId
	 * @param closeAtr
	 * @param deprecateAtr
	 * @return
	 */
	List<WorkTypeSet> findWorkTypeByClosure(String companyId, int closeAtr, int deprecateAtr);
	
	/**
	 * Adds the.
	 *
	 * @param workType the work type
	 */
	void add(WorkType workType);
	
	/**
	 * Update.
	 *
	 * @param workType the work type
	 */
	void update(WorkType workType);
	
	/**
	 * Adds the work type set.
	 *
	 * @param workTypeSet the work type set
	 */
	void addWorkTypeSet(WorkTypeSet workTypeSet);
	
	/**
	 * Removes the work type set.
	 *
	 * @param companyId the company id
	 * @param workTypeCd the work type cd
	 */
	void removeWorkTypeSet(String companyId, String workTypeCd);
	
	/**
	 * Removes the.
	 *
	 * @param companyId the company id
	 * @param workTypeCd the work type cd
	 */
	void remove(String companyId, String workTypeCd);
	
	/**
	 * Find work one day.
	 *
	 * @param companyId the company id
	 * @param abolishAtr the abolish atr
	 * @param worktypeAtr the worktype atr
	 * @return the list
	 */
	List<WorkType> findWorkOneDay(String companyId, int abolishAtr, int worktypeAtr);
	
	/**
	 * Find work one day.
	 *
	 * @param companyId the company id
	 * @param abolishAtr the abolish atr
	 * @param worktypeAtr the worktype atr
	 * @param oneDay the one day
	 * @return the list
	 */
	List<WorkType> findWorkOneDay(String companyId, int abolishAtr, int worktypeAtr, int oneDay);
	
	List<String> findWorkTypeCodeOneDay(String companyId, int abolishAtr, int worktypeAtr, int oneDay);
	

	List<WorkType> findHolidayWorkType(String companyId, int abolishAtr, int worktypeAtr, int workTypeClf, int holiday);
	
	List<WorkType> findHolidayWorkTypeClo(String companyId, int abolishAtr, int worktypeAtr, int workTypeClf, int closeAtr);

	List<WorkType> findWorkByDeprecate(String companyId, int deprecateAtr);
	
	/**
	 * Gets the acquired attendance work types.
	 *
	 * @param companyId the company id
	 * @return the acquired attendance work types
	 */
	List<WorkType> getAcquiredAttendanceWorkTypes(String companyId);
	
	/**
	 * Gets the acquired holiday work types.
	 *
	 * @param companyId the company id
	 * @return the acquired holiday work types
	 */
	List<WorkType> getAcquiredHolidayWorkTypes(String companyId);
	
	/**
	 * Gets the acquired leave system work types.
	 *
	 * @param companyId the company id
	 * @return the acquired leave system work types
	 */
	List<WorkType> getAcquiredLeaveSystemWorkTypes(String companyId);
	
	/**
	 * Find by deprecated.
	 * <<Public>> 指定した勤務種類をすべて取得する
	 * @param companyId the company id
	 * @param workTypeCd the work type cd
	 * @return the optional
	 */
	Optional<WorkType> findByDeprecated(String companyId, String workTypeCd);

	/**
	 * Find work type for shorting.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<WorkType> findWorkTypeForShorting(String companyId);

	/**
	 * Find work type for pause.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<WorkType> findWorkTypeForPause(String companyId);
	
	/**
	 * Find work type for app holiday app type.
	 *
	 * @param companyId the company id
	 * @param allDayAtrs the all day atrs
	 * @param mornings the mornings
	 * @param afternoons the afternoons
	 * @param morning the morning
	 * @param afternoon the afternoon
	 * @return the list
	 */
	List<WorkType> findWorkTypeForAppHolidayAppType(String companyId,List<Integer> allDayAtrs, List<Integer> mornings,List<Integer> afternoons,Integer morning,Integer afternoon);
	
	/**
	 * Find work type for half day.
	 *
	 * @param companyId the company id
	 * @param halfDay the half day
	 * @param workTypeCodes the work type codes
	 * @return the list
	 */
	List<WorkType> findWorkTypeForHalfDay(String companyId,List<Integer> halfDay,List<String> workTypeCodes);
	
	/**
	 * Find work type for all day and half day.
	 *
	 * @param companyId the company id
	 * @param halfDay the half day
	 * @param workTypeCodes the work type codes
	 * @param oneDays the one days
	 * @return the list
	 */
	List<WorkType> findWorkTypeForAllDayAndHalfDay(String companyId,List<Integer> halfDay,List<String> workTypeCodes,List<Integer> oneDays);
	
	/**
	 * Find work type by codes.
	 *
	 * @param companyId the company id
	 * @param workTypeCodes the work type codes
	 * @param abolishAtr the abolish atr
	 * @param worktypeAtr the worktype atr
	 * @return the list
	 */
	List<WorkType> findWorkTypeByCodes(String companyId,List<String> workTypeCodes,int abolishAtr, int worktypeAtr);
	
	/**
	 * Find work type by condition.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<WorkType> findWorkTypeByCondition(String companyId);
	/**
	 * @author hoatt
	 * get worktype 休暇申請の種類
	 * @param companyId 会社ID
	 * @param lstWorkTypeCD 勤務種類リスト
	 * @param deprecateAtr 廃止区分
	 * @param hdType 休暇申請の種類
	 * @return
	 */
	List<WorkType> findForAppHdKAF006(String companyId, List<String> lstWorkTypeCD, int deprecateAtr, List<Integer> hdType);

	boolean findWorkTypeRecord(String companyId, String workTypeCode);

	List<WorkTypeInfor> getNotRemoveWorkType(String companyId, List<String> lstPossible);
	
	List<WorkType> findListByCid(String companyId);
	
	List<WorkType> findByCidAndWorkTypeCodes(String companyId, List<String> workTypeCodes);

	List<WorkType> findForAppKAF008(String companyId, int deprecateAtr, int worktypeAtr, List<Integer> hdType);

	List<WorkType> findByDepreacateAtrAndWorkTypeAtr(String companyId, int deprecateAtr, int worktypeAtr);
	
	List<WorkType> getAllWorkTypeNotAbolished(String companyId);
	
}
