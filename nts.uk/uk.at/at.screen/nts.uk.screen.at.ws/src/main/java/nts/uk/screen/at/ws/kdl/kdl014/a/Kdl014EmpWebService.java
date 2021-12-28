package nts.uk.screen.at.ws.kdl.kdl014.a;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.query.pub.employee.EmployeeInformationExport;
import nts.uk.screen.at.app.query.kdl.kdl014.a.ReferToTheStampingResultsFinder;
import nts.uk.screen.at.app.query.kdl.kdl014.a.dto.Kdl014EmpParamDto;
import nts.uk.screen.at.app.query.kdl.kdl014.a.dto.ReferToTheStampingResultsDto;
import nts.uk.screen.at.app.query.kdl.kdl014.a.dto.StampMeansDto;

@Path("screen/at/kdl014")
@Produces("application/json")
public class Kdl014EmpWebService extends WebService {
	
	@Inject
	private ReferToTheStampingResultsFinder stampResultSQ;
	
	@POST
	@Path("get")
	public ReferToTheStampingResultsDto get(Kdl014EmpParamDto input) {
		return stampResultSQ.get(input);
	}
	
	@POST
	@Path("getEmployeeData")
	public List<EmployeeInformationExport> getEmployeeData(Kdl014EmpParamDto param) {
	    return stampResultSQ.getEmployeeData(param);
	}
	
	@POST
	@Path("find/stampMeans")
	public List<EnumConstant> findStampMeansList() {
		return EnumAdaptor.convertToValueNameList(StampMeans.class);
	}
	
	@POST
	@Path("find/stampMeans/smart-phone")
	public StampMeansDto findStampMeanSmartPhone() {
		return new StampMeansDto(StampMeans.SMART_PHONE);
	}
	
	@POST
	@Path("find/stampMeans/time-clock")
	public StampMeansDto findstampMeanTimeClock() {
		return new StampMeansDto(StampMeans.TIME_CLOCK);
	}
	
}
