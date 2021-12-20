package nts.uk.ctx.at.request.ws.dialog.employmentsystem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import lombok.Data;
import lombok.Value;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.AcquisitionNumberRestDayDto;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.EmployeeBasicInfoDto;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.EmploymentSystemFinder;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.shr.com.context.AppContexts;

@Path("at/request/dialog/employmentsystem")
@Produces("application/json")
public class EmploymentSystemService extends WebService {
	@Inject
	EmploymentSystemFinder employeeFinder;
	
	@Inject
	private EmployeeRequestAdapter employeeRequestAdapter;
	
	// Code for KDL005
	
	@POST
	@Path("getEmployeeList")
	public DataParam getEmployeeList(EmployeeParam param)
	{		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate localDate = LocalDate.now();
		String systemDate = dtf.format(localDate);
		String baseDate = param.getBaseDate().isEmpty() ? systemDate : param.getBaseDate();
		
		// アルゴリズム「代休確認ダイア起動」を実行する
		List<EmployeeBasicInfoDto> employeeBasicInfo = employeeFinder.getEmployeeData(param.getEmployeeIds(), baseDate);
				
		DataParam result = new DataParam(employeeBasicInfo, baseDate);
		 
		return result;
	}
	
	@POST
	@Path("getDetailsConfirm/{employeeId}/{baseDate}")
	public AcquisitionNumberRestDayDto getDetailsConfirm(@PathParam("employeeId") String employeeId, @PathParam("baseDate") String baseDate)
	{		
		// アルゴリズム「代休確認ダイア詳細取得」を実行する
		return employeeFinder.getDetailsConfirm(employeeId, baseDate);
	}
	
	// Code for KDL009
	
	@POST
	@Path("getEmployee")
	public DataParam getEmployee(EmployeeParam param)
	{		
		// アルゴリズム「振休確認ダイアログ開始」を実行する
		List<EmployeeBasicInfoDto> employeeBasicInfo = employeeFinder.getEmployee(param.getEmployeeIds(), param.getBaseDate());
		
		DataParam result = new DataParam(employeeBasicInfo, param.getBaseDate());
		
		return result;
	}
	
	/**
	 * アルゴリズム「振休残数情報の取得」を実行する
	 * 
	 * @param employeeId
	 * @param baseDate
	 * @return
	 */
	@POST
	@Path("getAcquisitionNumberRestDays/{employeeId}/{baseDate}")
	public AcquisitionNumberRestDayDto getAcquisitionNumberRestDays(@PathParam("employeeId") String employeeId, @PathParam("baseDate") String baseDate) {		
		// 振休残数情報の取得
		return employeeFinder.getAcquisitionNumberRestDays(employeeId, baseDate);
	}
	
	@POST
	@Path("getSid")
	public List<String> getEmployee() {		
		List<String> result = employeeRequestAdapter.getAllSidByCid(AppContexts.user().companyId());
		return result;
	}
	
}

@Value
class DataParam{
	List<EmployeeBasicInfoDto> employeeBasicInfo;
	String baseDate;
}

@Data
class EmployeeParam{
	 List<String> employeeIds;
	 String baseDate;
}