package nts.uk.ctx.sys.env.pub.maildestination;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sonnlb 
 * Out Going Mail 
 * 送信メール一覧
 */
@Data
@AllArgsConstructor
public class OutGoingMail {

	/**
	 * メールアドレス
	 */
	private String emailAddress;
}
