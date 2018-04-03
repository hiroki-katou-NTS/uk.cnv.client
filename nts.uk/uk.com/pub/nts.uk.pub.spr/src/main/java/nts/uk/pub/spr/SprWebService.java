package nts.uk.pub.spr;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.val;
import nts.uk.pub.spr.SprStubHelper.RecordApplicationStatusResult;
import nts.uk.pub.spr.SprStubHelper.RequestApplicationStatusResult;
import nts.uk.pub.spr.approvalroot.SprApprovalRootService;
import nts.uk.pub.spr.appstatus.SprAppStatusService;
import nts.uk.pub.spr.dailystatus.SprDailyStatusService;
import nts.uk.pub.spr.login.SprLoginFormService;
import nts.uk.pub.spr.login.output.LoginUserContextSpr;

@Path("public/spr")
public class SprWebService {
	
	@Inject
	private SprLoginFormService sprLoginFormService;
	
	@Inject
	private SprAppStatusService sprAppStatusService;
	
	@Inject
	private SprDailyStatusService sprDailyStatusService;
	
	@Inject
	private SprApprovalRootService sprApprovalRootService;

	@POST
	@Path("01/loginfromspr")
	@Produces("text/html")
	public String loginFromSpr(
			@FormParam("menu") String menuCode,
			@FormParam("loginemployeeCode") String loginEmployeeCode,
			@FormParam("employeeCode") String targetEmployeeCode,
			@FormParam("starttime") String startTime,
			@FormParam("endtime") String endTime,
			@FormParam("date") String targetDate,
			@FormParam("selecttype") String selectType,
			@FormParam("applicationID") String applicationID,
			@FormParam("reason") String reason) {
		LoginUserContextSpr loginUserContextSpr = sprLoginFormService.loginFromSpr(
				menuCode, 
				loginEmployeeCode, 
				targetEmployeeCode, 
				startTime, 
				endTime, 
				targetDate, 
				selectType, 
				applicationID, 
				reason);
		val paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("menu", SprStubHelper.formatParam(menuCode));
		paramsMap.put("loginemployeeCode", SprStubHelper.formatParam(loginEmployeeCode));
		paramsMap.put("employeeCode", SprStubHelper.formatParam(targetEmployeeCode));
		paramsMap.put("starttime", SprStubHelper.formatParamTime(startTime));
		paramsMap.put("endtime", SprStubHelper.formatParamTime(endTime));
		paramsMap.put("date", SprStubHelper.formatParam(targetDate));
		paramsMap.put("selecttype", SprStubHelper.formatParam(selectType));
		paramsMap.put("applicationID", SprStubHelper.formatParam(applicationID));
		
		val html = new StringBuilder()
				.append("<!DOCTYPE html>")
				.append("<html><body>");
		paramsMap.forEach((name, value) -> {
			html.append(name + " : " + value + "<br/>");
		});
		html.append("<script>");
		html.append("window.location.href = \"http://localhost:8080/nts.uk.at.web/view/kdw/003/a/index.xhtml"
				+ "?contractCode=000000000000"
				+ "&password=123456"
				+ "&companyCode=0001"
				+ "&employeeCode=000001"
				+ "$password=0\";");
		html.append("</script>");
		html.append("</body></html>");
		
		
		return html.toString();
	}
	
	@POST
	@Path("02/getapplicationstatus")
	@Produces("application/json")
	public SprStubHelper.StatusContainer<SprStubHelper.RequestApplicationStatusResult> getRequestApplicationStatus(
			SprStubHelper.ApplicationStatusQuery query) {
		
		List<RequestApplicationStatusResult> requestAppStatusList = sprAppStatusService.getAppStatus(
				query.getLoginemployeeCode(), 
				query.getEmployeeCode(), 
				query.getStartdate(), 
				query.getEnddate())
				.stream()
				.map(x -> new SprStubHelper.RequestApplicationStatusResult(
						x.getDate().toString("yyyy/MM/dd"), 
						x.getStatus1(), 
						x.getStatus2(), 
						x.getApplicationID1().map(y -> y.toString()).orElse(null), 
						x.getApplicationID2().map(y -> y.toString()).orElse(null)))
				.collect(Collectors.toList());
		
		return new SprStubHelper.StatusContainer<>(requestAppStatusList);
	}
	
	@POST
	@Path("03/getstatusofdaily")
	@Produces("application/json")
	public SprStubHelper.StatusContainer<SprStubHelper.RecordApplicationStatusResult> getRecordApplicationStatus(
			SprStubHelper.ApplicationStatusQuery query) {

		List<RecordApplicationStatusResult> recordAppStatusList = sprDailyStatusService.getStatusOfDaily(
				query.getLoginemployeeCode(), 
				query.getEmployeeCode(), 
				query.getStartdate(), 
				query.getEnddate())
				.stream()
				.map(x -> new SprStubHelper.RecordApplicationStatusResult(
						x.getDate().toString("yyyy/MM/dd"), 
						x.getStatus1(), 
						x.getStatus2()))
				.collect(Collectors.toList());
		
		
		return new SprStubHelper.StatusContainer<>(recordAppStatusList);
	}
	
	@POST
	@Path("04/getapprovalroot")
	@Produces("application/json")
	public SprStubHelper.EmployeesContainer<SprStubHelper.ApplicationTargetResult> getApprovalRoot(
			SprStubHelper.ApplicationTargetQuery query) {
		
		sprApprovalRootService.getApprovalRoot(
				query.getLoginemployeeCode(), 
				query.getDate())
				.stream()
				.map(x -> new SprStubHelper.ApplicationTargetResult(
						x.getEmployeeCD(), 
						x.getStatus1(), 
						x.getStatus2()))
				.collect(Collectors.toList());
		
		return new SprStubHelper.EmployeesContainer<>(SprStubHelper.ApplicationTargetResult.create());
	}
}
