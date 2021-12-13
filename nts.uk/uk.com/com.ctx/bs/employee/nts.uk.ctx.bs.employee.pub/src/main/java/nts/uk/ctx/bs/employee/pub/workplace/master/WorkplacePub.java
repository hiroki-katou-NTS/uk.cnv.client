package nts.uk.ctx.bs.employee.pub.workplace.master;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.pub.workplace.*;

import org.apache.commons.lang3.tuple.Pair;

public interface WorkplacePub {

	/**
	 * [No.559]運用している職場の情報をすべて取得する
	 * 
	 * @param companyId
	 * @param baseDate
	 * @return
	 */
	public List<WorkplaceInforExport> getAllActiveWorkplaceInfor(String companyId, GeneralDate baseDate);

	/**
	 * [No.560]職場IDから職場の情報をすべて取得する
	 * 
	 * @param companyId
	 * @param listWorkplaceId
	 * @param baseDate
	 * @return
	 */
	public List<WorkplaceInforExport> getWorkplaceInforByWkpIds(String companyId, List<String> listWorkplaceId,
                                                                GeneralDate baseDate);

	/**
	 * [No.561]過去の職場の情報を取得する
	 * 
	 * @param companyId
	 * @param historyId
	 * @param listWorkplaceId
	 * @return
	 */
	public List<WorkplaceInforExport> getPastWorkplaceInfor(String companyId, String historyId,
                                                            List<String> listWorkplaceId);

	/**
	 * [No.567]職場の下位職場を取得する
	 *
	 * @param companyId
	 * @param baseDate
	 * @param parentWorkplaceId
	 * @return
	 */
	public List<String> getAllChildrenOfWorkplaceId(String companyId, GeneralDate baseDate, String parentWorkplaceId);

	/**
	 * [No.573]職場の下位職場を基準職場を含めて取得する
	 *
	 * @param companyId
	 * @param baseDate
	 * @param workplaceId
	 * @return
	 */
	public List<String> getWorkplaceIdAndChildren(String companyId, GeneralDate baseDate, String workplaceId);

	/**
	 * RequestList No.30
	 * @param employeeId
	 * @param baseDate
	 * @return
	 */
	Optional<SWkpHistExport> findBySid(String employeeId, GeneralDate baseDate);
	
	/**
	 * Clone from RequestList No.30
	 * @param employeeId
	 * @param baseDate
	 * @return
	 */
	Map<GeneralDate, Map<String, Optional<SWkpHistExport>>> findBySid(String companyID, List<String> employeeId, DatePeriod baseDate);
	
	/**
	 * [No.650]社員が所属している職場を取得する
	 * 社員と基準日から所属職場履歴項目を取得する
	 * @param employeeID
	 * @param date
	 * @return
	 */
	public AffWorkplaceHistoryItemExport getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date);
	
	/**
	 * [No.569]職場の上位職場を取得する
	 * @param companyID
	 * @param workplaceID
	 * @param date
	 * @return
	 */
	public List<String> getUpperWorkplace(String companyID, String workplaceID, GeneralDate date);
	/**
	 * [No.571]職場の上位職場を基準職場を含めて取得する
	 *
	 * @param companyId
	 * @param baseDate
	 * @param workplaceId
	 * @return
	 */
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId);
	
	public List<String> findWpkIdsBySid(String companyId, String employeeId, GeneralDate baseDate);
	
	public Map<GeneralDate, Map<String, List<String>>> findWpkIdsBySids(String companyId, List<String> employeeId, DatePeriod date);

	public List<AffAtWorkplaceExport> findBySIdAndBaseDateV2(List<String> sids, GeneralDate baseDate);

	public List<DatePeriod> getLstPeriod(String companyId, DatePeriod period);

	Map<String, Pair<String,String>> getWorkplaceMapCodeBaseDateName(String companyId,
			List<String> wpkIds, List<GeneralDate> baseDates);

	List<AffAtWorkplaceExport> findBySIdAndBaseDate(List<String> sids, GeneralDate baseDate);

	List<AffWorkplaceExport> getByLstWkpIdAndPeriod(List<String> lstWkpId, GeneralDate startDate, GeneralDate endDate);

	List<String> getLstWorkplaceIdBySidAndPeriod(String employeeId, DatePeriod period);

	List<ResultRequest597Export> getLstEmpByWorkplaceIdsAndPeriod(List<String> workplaceIds, DatePeriod period);

	List<AffWorkplaceExport> findListSIdByCidAndWkpIdAndPeriod(
			String workplaceId, GeneralDate startDate, GeneralDate endDate);

	List<String> getListWorkplaceIdByBaseDate(GeneralDate baseDate);

	List<WorkPlaceHistExport> getWplByListSidAndPeriod(List<String> employeeIds, DatePeriod datePeriod);

	Optional<SWkpHistExport> findBySidNew(String employeeId, GeneralDate baseDate);

	Optional<SWkpHistExport> findBySidNew(String companyId, String employeeId, GeneralDate baseDate);

	WkpByEmpExport getLstHistByEmpAndPeriod(String employeeID, GeneralDate startDate, GeneralDate endDate);

	List<SWkpHistExport> findBySId(List<String> sids, GeneralDate baseDate);

	Optional<SWkpHistExport> findByWkpIdNew(String companyId, String wkpId, GeneralDate baseDate);

	List<AffWorkplaceHistoryExport> getWorkplaceBySidsAndBaseDate(List<String> employeeIds, GeneralDate baseDate);

	List<WkpConfigAtTimeExport> findByWkpIdsAtTime(String companyId, GeneralDate baseDate, List<String> wkpIds);

	List<WorkplaceInforExport> findByWkpIds(List<String> wkpIds);

	Optional<WkpCdNameExport> findByWkpId(String wkpId);

	/**
	 * [No.575]職場コードから職場IDを取得する
	 * @param 会社ID companyId
	 * @param 職場コード wkpCd
	 * @param 基準日 baseDate
	 * @return
	 */
	public Optional<String> getWkpNewByCdDate(String companyId, String wkpCd, GeneralDate baseDate);

	List<SWkpHistExport> findBySId(List<String> sids);
	
	/**
	 * 職場と基準日から所属職場履歴項目を取得する
	 * @param workPlaceId  職場ID
	 * @param baseDate 基準日
	 */
	List<AffWorkplaceHistoryItemExport2> getWorkHisItemfromWkpIdAndBaseDate(String workPlaceId, GeneralDate baseDate);
	
	// 職場（List）と基準日から所属職場履歴項目を取得する
	List<AffWorkplaceHistoryItemExport3> getWorkHisItemfromWkpIdsAndBaseDate(List<String> workPlaceIds, GeneralDate baseDate);


	Optional<SWkpHistWrkLocationExport> findBySidWrkLocationCD(String employeeId, GeneralDate baseDate);

	/**
	 * 期間から職場情報を取得
	 * @param companyId
	 * @param datePeriod
	 * @return
	 */
	List<WorkplaceInformationExport> getByCidAndPeriod(String companyId, DatePeriod datePeriod);

}
