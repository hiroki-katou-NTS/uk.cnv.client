package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

import java.util.Optional;

public interface MailDestinationFunctionManageRepository {

	// [1] Insert(メール送信先機能管理)																							
	void insert(MailDestinationFunctionManage domain);
	
	// [2] Update(メール送信先機能管理)																							
	void update(MailDestinationFunctionManage domain);
	
	// [3] Delete(会社ID、機能ID)																							
	void delete(String cid, int functionId);
	
	// [4] get																					
	Optional<MailDestinationFunctionManage> findByFunctionId(String cid, int functionId);
}
