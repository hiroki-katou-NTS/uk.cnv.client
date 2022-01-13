package nts.uk.ctx.at.function.ac.alarm;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.function.dom.adapter.alarm.IMailDestinationAdapter;
import nts.uk.ctx.at.function.dom.adapter.alarm.MailDestinationAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.alarm.OutGoingMailAlarm;
import nts.uk.ctx.sys.env.pub.maildestination.IMailDestinationPub;

@Stateless
public class IMailDestinationAdapterImpl implements IMailDestinationAdapter {
	@Inject
	private IMailDestinationPub iMailDestinationPub;
	
	@Override
	public MailDestinationAlarmImport getEmpEmailAddress(String cID, String sID, Integer functionID) {
		List<String> sIDs = Arrays.asList(sID);
		List<MailDestinationAlarmImport> listEmpMail = iMailDestinationPub.getEmployeeMails(cID, sIDs, functionID)
				.getSentMailLists().stream()
				.map(x -> new MailDestinationAlarmImport(x.getSid(), mapGoingMail(x.getMailAddresses())))
				.collect(Collectors.toList());
		return listEmpMail.get(0);
	}
	
	private List<OutGoingMailAlarm> mapGoingMail(List<String> outGoingMails) {
		return outGoingMails.stream().map(x -> new OutGoingMailAlarm(x))
				.collect(Collectors.toList());
	}

}
