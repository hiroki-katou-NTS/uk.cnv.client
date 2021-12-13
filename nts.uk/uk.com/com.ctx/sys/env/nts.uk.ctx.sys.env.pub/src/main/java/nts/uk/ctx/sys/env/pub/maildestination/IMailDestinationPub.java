package nts.uk.ctx.sys.env.pub.maildestination;

import java.util.List;

/**
 * The Interface MailDestinationRepository.
 */
public interface IMailDestinationPub {

	/**
	 * RequestList 397
	 * @param cid			会社ID
	 * @param sids			社員ID(List) 
	 * @param functionId	機能ID
	 * @return メール送信先
	 */
	MailDestinationExport getEmployeeMails(String cid, List<String> sids, int functionId);
}
