package nts.uk.ctx.workflow.dom.approvermanagement.workroot;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface WorkplaceApprovalRootRepository {

	/**
	 * get All Workplace Approval Root
	 * 
	 * @param companyId
	 * @param workplaceId
	 * @return
	 */
	List<WorkplaceApprovalRoot> getWpRootStart(String companyId, String workplaceId, int sysAtr,
			List<Integer> lstAppType, List<Integer> lstNoticeID, List<String> lstEventID);

	/**
	 * get WpApprovalRoot
	 * 
	 * @param companyId
	 * @param approvalId
	 * @param workplaceId
	 * @param historyId
	 * @return 
	 */
	Optional<WorkplaceApprovalRoot> getWpApprovalRoot(String companyId, String approvalId, String workplaceId,
			String historyId);

	/**
	 * get Workplace Approval Root By End date
	 * 
	 * @param companyId
	 * @param workplaceId
	 * @param endDate
	 * @param applicationType
	 * @param employmentRootAtr
	 * @return
	 */
	List<WorkplaceApprovalRoot> getWpApprovalRootByEdate(String companyId, String workplaceId, GeneralDate endDate,
			Integer applicationType, int employmentRootAtr, String id, int sysAtr);

	/**
	 * 職場別承認ルート
	 * @param companyID 会社ID
	 * @param workplaceID 職場ID
	 * @param date 基準日
	 * @param rootAtr 就業ルート区分
	 * @param targetType 対象申請
	 * @param sysAtr システム区分
	 * @return
	 */
	Optional<WorkplaceApprovalRoot> findByBaseDate(String companyID, String workplaceID, GeneralDate date, EmploymentRootAtr rootAtr, 
			String targetType, int sysAtr);

	/**
	 * 職場別承認ルート common
	 * @param companyID 会社ID
	 * @param workplaceID 職場ID
	 * @param date 基準日
	 * @param sysAtr システム区分
	 * @return
	 */
	Optional<WorkplaceApprovalRoot> findByBaseDateOfCommon(String companyID, String workplaceID, GeneralDate date, int sysAtr);

	/**
	 * add Workplace Approval Root
	 * 
	 * @param wpAppRoot
	 */
	void addWpApprovalRoot(WorkplaceApprovalRoot wpAppRoot);
	/**
	 * add All Workplace Approval Root
	 * 
	 * @param wpAppRoot
	 */
	void addAllWpApprovalRoot(List<WorkplaceApprovalRoot> wpAppRoot);

	/**
	 * update Workplace Approval Root
	 * 
	 * @param wpAppRoot
	 */
	void updateWpApprovalRoot(WorkplaceApprovalRoot wpAppRoot);
	/**
	 * update All Workplace Approval Root
	 * 
	 * @param wpAppRoot
	 */
	void updateAllWpApprovalRoot(List<WorkplaceApprovalRoot> wpAppRoot);

	/**
	 * delete Person Approval Root
	 * 
	 * @param companyId
	 * @param workplaceId
	 * @param historyId
	 */
	void deleteWpApprovalRoot(String companyId, String approvalId, String workplaceId, String historyId);
	/**
	 * ドメインモデル「職場別就業承認ルート」を取得する(lấy dữ liệu domain 「職場別就業承認ルート」)
	 * @param companyID
	 * @param baseDate・期間．開始日 <= 基準日  ・期間．終了日 >= 基準日
	 * @return
	 */
	List<WorkplaceApprovalRoot> findAllByBaseDate(String companyID, GeneralDate baseDate, int sysAtr);
	/**
	 * get Work place Approval Root By type
	 * @param companyId
	 * @param workplaceId
	 * @param applicationType
	 * @param employmentRootAtr
	 * @return
	 */
	List<WorkplaceApprovalRoot> getWpApprovalRootByType(String companyId, String workplaceId, Integer applicationType,
			int employmentRootAtr, String id, int sysAtr);
	/**
	 * getWpAppRootLast
	 * @param companyId
	 * @param workplaceId
	 * @param endDate
	 * @return
	 */
	List<WorkplaceApprovalRoot> getWpAppRootLast(String companyId, String workplaceId, GeneralDate endDate, int sysAtr);
	
	List<WorkplaceApprovalRoot> getWpAppRoot(String companyID, GeneralDate date, 
			Integer employmentRootAtr, Integer confirmRootAtr);
	
	List<WorkplaceApprovalRoot> findByBaseDateJinji(String companyId, GeneralDate baseDate,
			List<Integer> lstNoticeID, List<String> lstEventID);
	/**
	 * 期間、システム、承認ルート区分から職場別承認ルートを取得
	 * @param cid
	 * @param period
	 * @param sysAtr
	 * @param lstRootAtr
	 * @return
	 */
	List<WorkplaceApprovalRoot> getAppRootByDatePeriod(String cid, DatePeriod period, SystemAtr sysAtr, List<Integer> lstRootAtr);
	
	/**
	 * [1] 承認者として登録されている職場リストを取得する
	 * 指定社員が基準日時点で承認者として登録されている職場リストを取得する
	 * @param sid 社員ID
	 * @param referDate 基準日
	 * @return 職場リスト
	 */
	List<String> getWkpsAsApprovers(String sid, GeneralDate referDate);
}
