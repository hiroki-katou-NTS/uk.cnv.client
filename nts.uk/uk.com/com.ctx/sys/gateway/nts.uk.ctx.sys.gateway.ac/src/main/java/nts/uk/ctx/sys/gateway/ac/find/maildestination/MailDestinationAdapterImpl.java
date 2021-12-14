/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.ac.find.maildestination;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.env.pub.maildestination.IMailDestinationPub;
import nts.uk.ctx.sys.env.pub.maildestination.MailDestination;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.MailDestinationAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailDestinationImport;

/**
 * The Class MailDestinationAdapterImpl.
 */
@Stateless
public class MailDestinationAdapterImpl implements MailDestinationAdapter {

	/** The i mail destination pub. */
	@Inject
	private IMailDestinationPub iMailDestinationPub;
	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.gateway.dom.login.adapter.MailDestinationAdapter#getMailofEmployee(java.lang.String, java.util.List, java.lang.Integer)
	 */
	@Override
	public MailDestinationImport getMailofEmployee(String cid, List<String> lstSid, Integer functionId) {
		MailDestinationImport mailDestinationImport = new MailDestinationImport();
		List<MailDestination> lstMail = iMailDestinationPub.getEmpEmailAddress(cid, lstSid, functionId);
		lstMail.stream().forEach(i -> {
			List<String> mailAdds = new ArrayList<>();
			i.getOutGoingMails().stream().forEach(e -> {
				if (e.getEmailAddress() != null) {
					mailAdds.add(e.getEmailAddress());
				}
			});
			mailDestinationImport.addMail(mailAdds);
		});
		List<String> distintMailLst = mailDestinationImport.getOutGoingMails().stream().distinct()
				.collect(Collectors.toList());
		mailDestinationImport.setOutGoingMails(distintMailLst);
		return mailDestinationImport;
	}

}
