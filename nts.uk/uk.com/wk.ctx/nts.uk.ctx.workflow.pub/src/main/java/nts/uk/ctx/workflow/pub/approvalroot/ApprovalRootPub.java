package nts.uk.ctx.workflow.pub.approvalroot;

import java.util.List;
import java.util.Map;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.workflow.pub.approvalroot.export.ApprovalRootExport;
import nts.uk.ctx.workflow.pub.approvalroot.export.ApproverInfoExport;

public interface ApprovalRootPub {
	/**
	 * 1.社員の対象申請の承認ルートを取得する
	 * 
	 * @param cid
	 *            会社ID
	 * @param sid
	 *            社員ID（申請本人の社員ID）
	 * @param employmentRootAtr
	 *            就業ルート区分
	 * @param subjectRequest
	 *            対象申請
	 * @param standardDate
	 *            基準日
	 */
	List<ApprovalRootExport> getApprovalRootOfSubjectRequest(String cid, String sid, int employmentRootAtr, int appType,
			GeneralDate standardDate, int sysAtr);

	/**
	 * 3.職位から承認者へ変換する
	 * 
	 * @param cid
	 * @param sid
	 * @param baseDate
	 * @param jobTitleId
	 */
	List<ApproverInfoExport> convertToApprover(String cid, String sid, GeneralDate baseDate, String jobTitleId);
	
	Integer getCurrentApprovePhase(String rootStateID, Integer rootType);
	/**
	 * 就業の承認ルート未登録の社員を取得する
	 * @param cid
	 * @param period
	 * @param lstSid
	 * @return
	 */
	Map<String, List<String>> lstEmplUnregister(String cid, DatePeriod period, List<String> lstSid);
}
