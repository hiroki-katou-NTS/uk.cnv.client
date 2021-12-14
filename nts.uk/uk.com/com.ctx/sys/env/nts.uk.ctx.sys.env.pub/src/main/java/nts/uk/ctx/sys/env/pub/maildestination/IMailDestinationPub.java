package nts.uk.ctx.sys.env.pub.maildestination;

import java.util.List;

/**
 * The Interface MailDestinationRepository.
 */
public interface IMailDestinationPub {

	/**
	 * @author sonnlb
	 * @param cID
	 * @param sID
	 * @param functionID
	 * @return List<MailDestination> : List＜メール送信先＞
	 * 社員のメールアドレスを取得する
	 * Request List 419
	 */
	List<MailDestination> getEmpEmailAddress(String cID, List<String> sIDs, Integer functionID);

}
