package nts.uk.ctx.workflow.dom.service;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhase;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.EmploymentRootAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.SystemAtr;
import nts.uk.ctx.workflow.dom.service.output.ApprovalRootContentOutput;
import nts.uk.ctx.workflow.dom.service.output.ApproverFromGroupOuput;
import nts.uk.ctx.workflow.dom.service.output.ErrorFlag;
import nts.uk.ctx.workflow.dom.service.output.LevelOutput;
import nts.uk.ctx.workflow.dom.service.output.LevelOutput.LevelInforOutput.LevelApproverList.LevelApproverInfo;
/**
 * 承認ルートを取得する
 * @author Doan Duy Hung
 *
 */
public interface CollectApprovalRootService {
	
	/**
	 * 1.社員の対象申請の承認ルートを取得する
	 * Refactor5 UKDesign.ドメインモデル.NittsuSystem.UniversalK.ワークフロー.Export.[No.309]承認ルートを取得する.1.社員の対象申請の承認ルートを取得する(getApprovalRootOfSubjectRequest)
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param rootAtr 承認ルート区分
	 * @param targetType 対象申請
	 * @param standardDate 基準日
	 * @param sysAtr システム区分
	 * @param lowerApprove Optional<下位序列承認無＞
	 * @return
	 */
	public ApprovalRootContentOutput getApprovalRootOfSubjectRequest(
			String companyID, 
			String employeeID, 
			EmploymentRootAtr rootAtr, 
			String targetType, 
			GeneralDate standardDate,
			SystemAtr sysAtr,
			Optional<Boolean> lowerApprove);
	
	/**
	 * 6.職場に指定する職位の対象者を取得する
	 * @param cid 会社ID
	 * @param wkpId 職場ID（申請者の所属職場）
	 * @param baseDate 基準日
	 * @param jobTitleId 職位ID（承認者）
	 * @return
	 */
	public List<String> getPersonByWorkplacePosition(String cid, String wkpId, GeneralDate baseDate, String jobTitleId, SystemAtr systemAtr);
	
	/**
	 * 2.承認ルートを整理する（二次開発）
	 * @param companyID
	 * @param employeeID
	 * @param baseDate
	 * @param listApprovalPhase
	 * @param systemAtr
	 * @param lowerApprove
	 * @return
	 */
	public LevelOutput organizeApprovalRoute(String companyID, String employeeID, GeneralDate baseDate, List<ApprovalPhase> listApprovalPhase,
			SystemAtr systemAtr, Optional<Boolean> lowerApprove);
	
	/**
	 * 職位IDから序列の並び順を取得
	 * @param jobID
	 * @return
	 */
	public Optional<Integer> getDisOrderFromJobID(String jobID, String companyID, GeneralDate baseDate);
	
	/**
	 * 承認者グループから承認者を取得
	 * @param companyID
	 * @param approverGroupCD
	 * @param specWkpId
	 * @param paramID
	 * @param opDispOrder
	 * @param employeeID
	 * @param baseDate
	 * @param systemType
	 * @param lowerApprove
	 * @return
	 */
	public ApproverFromGroupOuput getApproverFromGroup(String companyID, String approverGroupCD, String specWkpId, String paramID, 
			Optional<Integer> opDispOrder, String employeeID, GeneralDate baseDate, SystemAtr systemAtr, Optional<Boolean> lowerApprove);
	
	/**
	 * 申請者より、下の職位の承認者とチェック
	 * @param systemType
	 * @param jobID
	 * @param opDispOrder
	 * @param lowerApprove
	 * @return
	 */
	public boolean checkApproverApplicantOrder(SystemAtr systemAtr, String jobID, Optional<Integer> opDispOrder, 
			Optional<Boolean> lowerApprove, String companyID, GeneralDate baseDate);
	
	/**
	 * 承認者を整理
	 * @param approverInfoLst
	 * @param baseDate
	 * @return
	 */
	public List<LevelApproverInfo> adjustApprover(List<LevelApproverInfo> approverInfoLst, GeneralDate baseDate, String companyID, String employeeID);
	
	/**
	 * 指定社員が基準日に承認権限を持っているかチェック
	 * @param approverInfoLst
	 * @param baseDate
	 * @return
	 */
	public List<LevelApproverInfo> checkApproverAuthor(List<LevelApproverInfo> approverInfoLst, GeneralDate baseDate, String companyID);
	
	/**
	 * 上位職場の承認者を探す
	 * @param companyID
	 * @param approverGroupCDLst
	 * @param opDispOrder
	 * @param employeeID
	 * @param baseDate
	 * @param systemAtr
	 * @param lowerApprove
	 * @param approvalAtr
	 * @return
	 */
	public List<LevelApproverInfo> getUpperApproval(String companyID, List<String> approverGroupCDLst, Optional<Integer> opDispOrder, 
			String employeeID, GeneralDate baseDate, SystemAtr systemAtr, Optional<Boolean> lowerApprove, ApprovalAtr approvalAtr);
	
	/**
	 * 7.承認ルートの異常チェック
	 * @param levelOutput
	 * @return
	 */
	public ErrorFlag checkApprovalRoot(LevelOutput levelOutput);
	
	/**
	 * 上位職場・部門を取得
	 * @param companyID
	 * @param employeeID
	 * @param date
	 * @param systemAtr
	 * @return
	 */
	public List<String> getUpperID(String companyID, String employeeID, GeneralDate date, SystemAtr systemAtr);
	
	/**
	 * 対象者の職場ID又は部門IDを取得
	 * @param systemAtr
	 * @param employeeID
	 * @param baseDate
	 * @return
	 */
	public String getIDBySystemType(SystemAtr systemAtr, String employeeID, GeneralDate baseDate);
	
	/**
	 * 対象者の所属職場・部門を含める上位職場・部門を取得する
	 * @param companyID
	 * @param employeeID
	 * @param date
	 * @param systemAtr
	 * @return
	 */
	public List<String> getUpperIDIncludeSelf(String companyID, String employeeID, GeneralDate date, SystemAtr systemAtr);
	
	/**
	 * 承認者の在職状態と承認権限をチェック
	 * @param approverInfoLst
	 * @param baseDate
	 * @return
	 */
	public List<LevelApproverInfo> checkApproverStatusAndAuthor(List<LevelApproverInfo> approverInfoLst, GeneralDate baseDate, String companyID);
}
