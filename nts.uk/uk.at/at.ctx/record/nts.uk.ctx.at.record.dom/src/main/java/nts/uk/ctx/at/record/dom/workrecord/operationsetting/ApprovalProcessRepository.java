package nts.uk.ctx.at.record.dom.workrecord.operationsetting;

import java.util.Optional;
import java.util.List;

/**
* 承認処理の利用設定
*/
public interface ApprovalProcessRepository
{

    List<ApprovalProcess> getAllApprovalProcess();

    Optional<ApprovalProcess> getApprovalProcessById(String cid);

}
