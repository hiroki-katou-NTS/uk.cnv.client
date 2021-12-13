package nts.uk.ctx.sys.auth.ac.permission.roleId;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.permission.roleId.SpecificWorkPlaceAdaper;
import nts.uk.ctx.sys.auth.dom.wkpmanager.EmpInfoAdapter;

@Stateless
public class SpecificWorkPlace implements SpecificWorkPlaceAdaper {
	
	@Inject
	private EmpInfoAdapter empInfoAdapter;

	@Override
	public List<String> get(List<String> wkpIds, DatePeriod dateperiod) {
		return empInfoAdapter.getListEmployeeId(wkpIds, dateperiod);
	}
	
	
	
	
}
