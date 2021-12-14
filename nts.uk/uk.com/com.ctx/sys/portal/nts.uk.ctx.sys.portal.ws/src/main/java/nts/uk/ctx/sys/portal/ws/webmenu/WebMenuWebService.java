package nts.uk.ctx.sys.portal.ws.webmenu;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.portal.app.command.webmenu.AddPersonalTyingCommandHandler;
import nts.uk.ctx.sys.portal.app.command.webmenu.AddWebMenuCommandHandler;
import nts.uk.ctx.sys.portal.app.command.webmenu.ChangeCompanyCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.ChangeCompanyCommandHandler;
import nts.uk.ctx.sys.portal.app.command.webmenu.ChangeCompanyResult;
import nts.uk.ctx.sys.portal.app.command.webmenu.CopyWebMenuCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.CopyWebMenuCommandHandler;
import nts.uk.ctx.sys.portal.app.command.webmenu.PersonTypingCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.RemoveWebMenuCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.RemoveWebMenuCommandHander;
import nts.uk.ctx.sys.portal.app.command.webmenu.UpdateWebMenuCommandHander;
import nts.uk.ctx.sys.portal.app.command.webmenu.WebMenuCommandBase;
import nts.uk.ctx.sys.portal.app.find.company.CompanyFinder;
import nts.uk.ctx.sys.portal.app.find.company.ShortCompanyDto;
import nts.uk.ctx.sys.portal.app.find.user.UserPortalFinder;
import nts.uk.ctx.sys.portal.app.find.webmenu.EditMenuBarDto;
import nts.uk.ctx.sys.portal.app.find.webmenu.MobileMenuDto;
import nts.uk.ctx.sys.portal.app.find.webmenu.PersonTypeDto;
import nts.uk.ctx.sys.portal.app.find.webmenu.ProgramNameDto;
import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuDto;
import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuFinder;
import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuSimpleDto;
import nts.uk.ctx.sys.portal.app.find.webmenu.detail.WebMenuDetailDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.loginuser.SessionLowLayer;

@Path("sys/portal/webmenu")
@Produces("application/json")
@Slf4j
public class WebMenuWebService extends WebService {

	@Inject
	private WebMenuFinder webMenuFinder;
	
	@Inject
	private CompanyFinder companyFinder;
	
	@Inject
	private UserPortalFinder userFinder;

	@Inject
	private AddWebMenuCommandHandler addWebMenuCommandHandler;

	@Inject
	private UpdateWebMenuCommandHander updateWebMenuCommandHander;

	@Inject
	private RemoveWebMenuCommandHander removeWebMenuCommandHander;
	
	@Inject
	private CopyWebMenuCommandHandler copyWebMenuCommandHander;
	
	@Inject
	private AddPersonalTyingCommandHandler addPersonTypeCommandHandler;
	
	@Inject
	private ChangeCompanyCommandHandler changeCompanyCommandHandler;
	
	@Inject
	private SessionLowLayer sessionLowLayer;
	@Context
	private HttpServletRequest request;

	@POST
	@Path("add")
	public void add(WebMenuCommandBase command) {
		this.addWebMenuCommandHandler.handle(command);
	}

	@POST
	@Path("update")
	public void update(WebMenuCommandBase command) {
		this.updateWebMenuCommandHander.handle(command);
	}

	@POST
	@Path("remove")
	public void remove(RemoveWebMenuCommand command) {
		this.removeWebMenuCommandHander.handle(command);
	}

	@POST
	@Path("find")
	public List<WebMenuDto> findAll() {
		return this.webMenuFinder.findAll();
	}

	@POST
	@Path("findallwithnomenubar")
	public List<WebMenuSimpleDto> findAllWithNoMenuBar() {
    	long startTime = System.currentTimeMillis();
		val test = this.webMenuFinder.findAllWithNoMenuBar();
        long endTime = System.currentTimeMillis();
        log.info("findAllWithNoMenuBar: " + (endTime - startTime) + " milliseconds");
        return test;
	}

	@POST
	@Path("finddefault")
	public WebMenuDetailDto findDefault() {
		return this.webMenuFinder.findDefault();
	}
	
	@POST
	@Path("program")
	public List<ProgramNameDto> getProgramName() {
		return this.webMenuFinder.getProgram();
	}
	
	@POST
	@Path("finddetails")
	public List<WebMenuDetailDto> findAllDetails() {
		return this.webMenuFinder.findAllDetails();
	}
	
	@POST
	@Path("find/{webMenuCode}")
	public WebMenuDto find(@PathParam("webMenuCode") String webMenuCode) {
		return this.webMenuFinder.find(webMenuCode);
	}
	
	@POST
	@Path("edit")
	public EditMenuBarDto editMenuBar() {
		return this.webMenuFinder.getEditMenuBarDto();
	}
	
	@POST
	@Path("copy")
	public void copyWebMenu(CopyWebMenuCommand command) {
		this.copyWebMenuCommandHander.handle(command);
	}
	
	@POST
	@Path("addPerson")
	public void addPerson(PersonTypingCommand command) {
 		this.addPersonTypeCommandHandler.handle(command);
	}
	
	@POST
	@Path("findPerson/{employeeId}")
	public List<PersonTypeDto> findAllPerson(@PathParam("employeeId") String employeeId) {
		return this.webMenuFinder.findAllPerson(employeeId);
	}
	
	@POST
	@Path("currentCompany")
	public JavaTypeResult<String> companyId() {
		return new JavaTypeResult<String>(AppContexts.user().companyId());
	}
	
	@POST
	@Path("companies")
	public List<ShortCompanyDto> companies() {
		return companyFinder.findAll();
	}
	
	@POST
	@Path("changeCompany")
	public ChangeCompanyResult changeCompany(String companyId) {
		String path = request.getHeader("PG-Path");
		String[] arr = path.substring(7).split("/");
		ChangeCompanyCommand command = new ChangeCompanyCommand(companyId);
		command.setScreenID(arr[5].toUpperCase());
		String msgResult = changeCompanyCommandHandler.handle(command);
		return new ChangeCompanyResult(command.getPersonName(), msgResult);
	}
	
	@POST
	@Path("username")
	public JavaTypeResult<String> userName() {
		return new JavaTypeResult<String>(userFinder.userName());
	}
	
	@POST
	@Path("showmanual")
	public boolean showManual() {
		return userFinder.showManual();
	}
	
	@POST
	@Path("logout")
	public void logout() {
		sessionLowLayer.loggedOut();
	}
	
	@POST
	@Path("mobile/findByEmp")
	public List<MobileMenuDto> mobileFindByEmp() {
		return this.webMenuFinder.getMenuToDisplay();
	}
	
}
