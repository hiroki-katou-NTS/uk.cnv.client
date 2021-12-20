package nts.uk.ctx.at.record.ws.monthly.nursingleave;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.childnursingleave.ChildNursingLeave;
import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.childnursingleave.ChildNursingLeaveDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps.GetDeitalInfoNursingByEmp;
import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps.NursingAndChildNursingRemainDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.ChildCareNusingLeaveFinder;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.KDL051ProcessDto;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.shr.com.context.AppContexts;

@Path("at/record/monthly/nursingleave/")
@Produces("application/json")
public class KDL051ChangeId {
	/** The finder. */
	@Inject
	ChildCareNusingLeaveFinder childCareNusingLeaveFinder;

	@Inject
	private ChildNursingLeave childNursingLeave;

	@Inject
	private GetDeitalInfoNursingByEmp getDeitalInfoNursingByEmp;

	@Path("changeId/{employeeId}")
	@POST
	public KDL051ProcessDto getChildCareNusingLeave(@PathParam("employeeId") String employeeId) {
		return this.childCareNusingLeaveFinder.changeEmployee(employeeId);
	}

	@Path("changeIdKdl052/{employeeId}")
	@POST
	public KDL051ProcessDto getChildCareNusingLeaveKDL052(@PathParam("employeeId") String employeeId) {
		return this.childCareNusingLeaveFinder.changeEmployeeKDL052(employeeId);
	}

	@Path("getChildNursingLeave")
	@POST
	public ChildNursingLeaveDto getChildNursingLeave(List<String> listEmpId) {
		ChildNursingLeaveDto data = childNursingLeave.get(AppContexts.user().companyId(), listEmpId);
		return data;
	}

	@Path("getDeitalInfoNursingByEmp")
	@POST
	public NursingAndChildNursingRemainDto getDeitalInfoNursingByEmp(String employeeId) {
		NursingAndChildNursingRemainDto data = getDeitalInfoNursingByEmp.get(AppContexts.user().companyId(), employeeId,
				NursingCategory.ChildNursing);
		return data;
	}

	@Path("getDeitalInfoNursingByEmp/{employeeId}")
	@POST
	public NursingAndChildNursingRemainDto getDeitalInfoNursingByEmployee(@PathParam("employeeId") String employeeId) {
		NursingAndChildNursingRemainDto data = getDeitalInfoNursingByEmp.get(AppContexts.user().companyId(), employeeId,
				NursingCategory.Nursing);
		return data;
	}
}
