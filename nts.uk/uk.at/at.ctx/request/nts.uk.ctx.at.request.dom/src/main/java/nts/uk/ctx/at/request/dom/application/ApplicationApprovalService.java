package nts.uk.ctx.at.request.dom.application;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;

/**
 * 
 * @author Doan Duy Hung
 *
 */
public interface ApplicationApprovalService {
	
	/**
	 * refactor 4
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請.アルゴリズム.申請を削除する.申請を削除する
	 * @param appID
	 */
	public void delete(String appID, ApplicationType appType, Optional<StampRequestMode> stampRequestMode, Optional<HdsubRecLinkData> hdSubRecLink);
	
	/**
	 * refactor 4
	 */
	public void insertApp(Application application, List<ApprovalPhaseStateImport_New> listApprovalPhaseState);
	
}
