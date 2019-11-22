package nts.uk.ctx.hr.shared.ws.personalinfo.medicalhistory;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.ctx.hr.shared.dom.personalinfo.humanresourceevaluation.HumanResourceEvaluation;
import nts.uk.ctx.hr.shared.dom.personalinfo.humanresourceevaluation.HumanResourceEvaluationService;
import nts.uk.ctx.hr.shared.dom.personalinfo.humanresourceevaluation.PersonnelAssessment;
import nts.uk.ctx.hr.shared.dom.personalinfo.stresscheck.StressCheck;
import nts.uk.ctx.hr.shared.dom.personalinfo.stresscheck.StressCheckManagement;
import nts.uk.ctx.hr.shared.dom.personalinfo.stresscheck.StressCheckService;

@Path("hrtest")
@Produces(MediaType.APPLICATION_JSON)
public class TestWs {

	@Inject
	private HumanResourceEvaluationService hrSv;

	@Inject
	private StressCheckService stressCheckSv;

	@POST
	@Path("/testhr/1") // test service
	public HumanResourceEvaluation testDomain1(TestDto dto) {
		HumanResourceEvaluation domain = new HumanResourceEvaluation();
		return hrSv.loadHRevaluation(dto.getListSid(), dto.getBaseDate().addMonths(-1), domain);
	}
	
	@POST
	@Path("/testhr/2") // test service
	public List<PersonnelAssessment> testDomain2(TestDto dto) {
		HumanResourceEvaluation domain = testDomain1(dto);
		return hrSv.getHRevaluationBySid(dto.getSid(), dto.getBaseDate().addMonths(-1), domain);
	}

	@POST
	@Path("/teststress/1") // test service
	public StressCheckManagement testStress1(TestDto dto) {
		StressCheckManagement domain = new StressCheckManagement();
		return stressCheckSv.loadStressCheck(dto.getListSid(), dto.getBaseDate().addMonths(-1), domain);
	}

	@POST
	@Path("/teststress/2") // test service
	public List<StressCheck> testStress2(TestDto dto) {
		StressCheckManagement domain = testStress1(dto);
		return stressCheckSv.getStressCheck(dto.getSid(), dto.getBaseDate().addMonths(-1), domain);
	}

}
