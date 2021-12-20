package nts.uk.ctx.at.request.ws.dialog.employmentsystem;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.Data;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.EmploymentSystemFinder;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.HolidaySubstituteDto;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.HolidaySubstituteFinder;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.RemainNumberConfirmDto;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.startholidayconfirmation.GetDetailInfoEmpRemainHoliday;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.startholidayconfirmation.StartHolidayConfDto;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.startholidayconfirmation.StartHolidayConfirmation;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.shr.com.context.AppContexts;

@Path("at/request/employment")
@Produces("application/json")
public class HolidaySubstituteServices {
	
	@Inject
	private HolidaySubstituteFinder finder;
	
	@Inject
	private EmployeeRequestAdapter employeeRequestAdapter;
	
	@Inject
	private StartHolidayConfirmation startHolidayConfirmation;
	
	@Inject
	private GetDetailInfoEmpRemainHoliday getDetailInfoEmpRemainHoliday;
	
	@POST
	@Path("getHolidaySub")
	public HolidaySubstituteDto getHolidaySubstitute(ParamKdl005 param) {		
		HolidaySubstituteDto result = finder.findHolidaySubstitute(param.getEmployeeIds());
		return result;
	}
	
	@POST
	@Path("getStartHolidayConf")
	public StartHolidayConfDto getStartHolidayConf(List<String> listEmployeeId) {		
		StartHolidayConfDto result = startHolidayConfirmation.get(listEmployeeId);
		return result;
	}
	
	@POST
	@Path("getHolidayConfByEmp")
	public RemainNumberConfirmDto getHolidayConfByEmp(String employeeId) {		
		RemainNumberConfirmDto result = getDetailInfoEmpRemainHoliday.getByEmpId(employeeId);
		return result;
	}
	
	@POST
	@Path("getSid")
	public List<String> getEmployee() {		
		List<String> result = employeeRequestAdapter.getAllSidByCid(AppContexts.user().companyId());
		return result;
	}
	
}

@Data
class ParamKdl005{
	 List<String> employeeIds;
	 String baseDate;
}
