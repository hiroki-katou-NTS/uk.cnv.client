package nts.uk.ctx.at.record.ac.workplace;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetWorkplaceOfEmployeeAdapter;
import nts.uk.ctx.sys.auth.pub.workplace.WorkplaceListPub;

/**
 * 
 * @author sonnlb
 *
 */
@Stateless
public class GetWorkplaceOfEmployeeAdapterImpl implements GetWorkplaceOfEmployeeAdapter {
	
	@Inject
	private WorkplaceListPub workplaceListPub;

	@Override
	public Map<String, String> get(String userID, String employeeID, GeneralDate date) {
		return this.workplaceListPub.getWorkPlace(userID, employeeID, date);
	}

}
