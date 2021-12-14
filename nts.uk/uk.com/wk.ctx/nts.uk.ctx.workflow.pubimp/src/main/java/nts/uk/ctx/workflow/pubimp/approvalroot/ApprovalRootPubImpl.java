package nts.uk.ctx.workflow.pubimp.approvalroot;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.service.unregisterapproval.EmployeeUnregisterApprovalRoot;
import nts.uk.ctx.workflow.pub.approvalroot.ApprovalRootPub;

@Stateless
public class ApprovalRootPubImpl implements ApprovalRootPub {
	
	@Inject
	private EmployeeUnregisterApprovalRoot emplUnregisterApprova;

	@Override
	public Map<String, List<String>> lstEmplUnregister(String cid, DatePeriod period, List<String> lstSid) {
		
		return emplUnregisterApprova.lstEmplUnregister(cid, period, lstSid);
	}

}
