package nts.uk.ctx.at.record.dom.workrecord.operationsetting;

import java.util.Optional;
import java.util.List;

/**
* 本人確認処理の利用設定
*/
public interface IdentityProcessRepository
{

    List<IdentityProcess> getAllIdentityProcess();

    Optional<IdentityProcess> getIdentityProcessById(String cid);
}
