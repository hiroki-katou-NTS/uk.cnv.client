package nts.uk.ctx.sys.env.pub.maildestination;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.gul.collection.CollectionUtil;

/**
 * @author sonnlb Mail Destination メール送信先
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDestination {

	/* 社員ID */
	private String employeeID;

	/* 社員ID */
	private List<OutGoingMail> outGoingMails;

	public void addOutGoingMails(String mailAddress) {
		if (CollectionUtil.isEmpty(this.outGoingMails)) {
			List<OutGoingMail> newOutGoingMails = new ArrayList<OutGoingMail>();
			newOutGoingMails.add(new OutGoingMail(mailAddress));
			this.outGoingMails = newOutGoingMails;
		} else {
			this.outGoingMails.add(new OutGoingMail(mailAddress));
		}

	}
}
