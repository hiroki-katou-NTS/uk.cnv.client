package nts.uk.screen.at.ws.cmm040.WorkLocationWS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.app.command.worklocation.AddIPAddressCmd;
import nts.uk.ctx.at.record.app.command.worklocation.AddIPAddressCmdHandler;
import nts.uk.ctx.at.record.app.command.worklocation.DeleteIPAddressCmd;
import nts.uk.ctx.at.record.app.command.worklocation.DeleteIPAddressCmdHandler;
import nts.uk.ctx.at.record.app.command.worklocation.DeleteWorkLocationCmdHandler;
import nts.uk.ctx.at.record.app.command.worklocation.InsertUpdateWorkLocationCmd;
import nts.uk.ctx.at.record.app.command.worklocation.InsertWorkLocationCmdHandler;
import nts.uk.ctx.at.record.app.command.worklocation.UpdateWorkLocationCmdHandler;
import nts.uk.ctx.at.record.app.find.worklocation.Ipv4AddressDto;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.RadiusAtr;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.screen.at.app.cmm040.worklocation.CheckWorkplace;
import nts.uk.screen.at.app.cmm040.worklocation.CompanyAndWorkInfoOutput;
import nts.uk.screen.at.app.cmm040.worklocation.GetCompanyAndWorkInfo;
import nts.uk.screen.at.app.cmm040.worklocation.GetIPSettings;
import nts.uk.screen.at.app.cmm040.worklocation.GetPlaceOfWork;
import nts.uk.screen.at.app.cmm040.worklocation.GetPlaceOfWorkOutput;
import nts.uk.shr.com.context.AppContexts;

@Path("at/screen/cmm040")
@Produces("application/json")
public class WorkLocationWS extends WebService {

	@Inject
	private GetPlaceOfWork getPlaceOfWork;

	@Inject
	private InsertWorkLocationCmdHandler insertWorkLocationCmdHandler;

	@Inject
	private DeleteWorkLocationCmdHandler deleteWorkLocationCmdHandler;

	@Inject
	private UpdateWorkLocationCmdHandler updateWorkLocationCmdHandler;

	@Inject
	private AddIPAddressCmdHandler addIPAddressCmdHandler;

	@Inject
	private DeleteIPAddressCmdHandler deleteIPAddressCmdHandler;

	@Inject
	private WorkLocationRepository repo;

	@Inject
	private GetIPSettings getIPSettings;

	@Inject
	private GetCompanyAndWorkInfo getCompanyAndWorkInfo;
	
	@Inject
	private CheckWorkplace checkWorkplace;

	@POST
	@Path("start")
	public GetPlaceOfWorkOutput get() {
		return this.getPlaceOfWork.get();
	}

	@POST
	@Path("getWorkPlace")
	public CompanyAndWorkInfoOutput getWorkPlace(GetWorkPlaceParam param) {
		List<String> cidLogin = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		cidLogin.add(cid);
		String contractCd = AppContexts.user().contractCode();
		if(param.listWorkplace.isEmpty()){
		return this.getCompanyAndWorkInfo.get(contractCd, param.listWorkplace,
				cidLogin, param.workLocationCD);
		}
		else{
			return this.getCompanyAndWorkInfo.get(contractCd, param.listWorkplace,
					param.listWorkplace.stream().map(i -> i.getCompanyId()).distinct().collect(Collectors.toList()), param.workLocationCD);
			}
	}

	// List<Ipv4AddressDto>
	@POST
	@Path("startB")
	public List<Ipv4AddressDto> getIP4Address(String param) {

		return this.getIPSettings.getIP4Address(param);
	}

	@POST
	@Path("saveDataScreenB")
	public List<Ipv4AddressDto> saveDataScreenB(AddIPAddressCmd command) {
		return this.addIPAddressCmdHandler.handle(command);
	}

	@POST
	@Path("deleteScreenA")
	public void deleteScreenA(String command) {
		this.deleteWorkLocationCmdHandler.handle(command);
	}

	@POST
	@Path("deleteScreenB")
	public void deleteScreenB(DeleteIPAddressCmd command) {
		this.deleteIPAddressCmdHandler.handle(command);
	}

	@POST
	@Path("update")
	public void updateWorkLocation(InsertUpdateWorkLocationCmd command) {
			this.updateWorkLocationCmdHandler.handle(command);
	}
	@POST
	@Path("insert")
	public void insertWorkLocation(InsertUpdateWorkLocationCmd command) {
		this.insertWorkLocationCmdHandler.handle(command);

	}
	@POST
	@Path("enum")
	public List<EnumConstant> getVacationExpirationEnum() {
		return EnumAdaptor.convertToValueNameList(RadiusAtr.class);
	}
	
	@POST
	@Path("checkWorkplace")
	public void checkWorkplace(CheckWorkplaceParam param) {
		this.checkWorkplace.checkWorkPlace(param.workplaceID);
	}
	
	@POST
	@Path("checkDelete")
	public boolean checkDelete() {
		return AppContexts.user().roles().have().systemAdmin();
	}

}
