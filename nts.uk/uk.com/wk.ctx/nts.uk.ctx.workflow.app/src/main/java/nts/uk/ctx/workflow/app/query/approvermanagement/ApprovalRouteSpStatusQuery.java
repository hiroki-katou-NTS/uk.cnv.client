package nts.uk.ctx.workflow.app.query.approvermanagement;

import java.util.List;

import lombok.Value;

/**
 * OUTPUT: 異動者の承認ルート指定状況を取得する
 * 承認ルート状況
 * @author NWS-DungDV
 *
 */
@Value
public class ApprovalRouteSpStatusQuery {

	// 指定者
	String sid;
	
	// 承認職場リスト
	List<String> workplaceList;
	
	// 承認対象社員リスト
	List<String> approveEmpList;
	
	// 指定者の承認者リスト
	List<String> approverList;
}
