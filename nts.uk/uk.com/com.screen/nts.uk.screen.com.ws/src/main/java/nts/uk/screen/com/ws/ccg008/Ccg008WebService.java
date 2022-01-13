package nts.uk.screen.com.ws.ccg008;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.auth.app.find.employmentrole.InitDisplayPeriodSwitchSetFinder;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.InitDisplayPeriodSwitchSetDto;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.sys.portal.app.command.toppagesetting.AddTopPageReloadSettingCommandHandler;
import nts.uk.ctx.sys.portal.app.command.toppagesetting.ToppageReloadSettingCommand;
import nts.uk.ctx.sys.portal.app.find.toppagesetting.TopPageSettingFinder;
import nts.uk.ctx.sys.portal.dom.adapter.toppagesetting.LoginRoleSetCodeAdapter;
import nts.uk.ctx.sys.portal.dom.toppage.TopPageReloadSetting;
import nts.uk.ctx.sys.portal.dom.toppage.TopPageReloadSettingRepository;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPagePersonSettingRepository;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageRoleSettingRepository;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageSettings;
import nts.uk.ctx.sys.portal.dom.toppagesetting.service.TopPageSettingService;
import nts.uk.ctx.sys.portal.dom.toppagesetting.service.TopPageSettingService.Require;
import nts.uk.ctx.sys.shared.dom.user.builtin.BuiltInUser;
import nts.uk.shr.com.context.AppContexts;


@Path("screen/com/ccg008")
@Produces("application/json")
public class Ccg008WebService {
	@Inject
	private ClosureRepository closureRepo;
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;
	
	@Inject
	private InitDisplayPeriodSwitchSetFinder displayPeriodfinder;
	
	
	@Inject
	private TopPageReloadSettingRepository reloadRepo; 
	
	@Inject
	private TopPageSettingService settingService;
	
	@Inject 
	private AddTopPageReloadSettingCommandHandler addToppage;
	
	@Inject
	private TopPagePersonSettingRepository topPagePersonSettingRepo;
	
	@Inject
	private TopPageRoleSettingRepository topPageRoleSettingRepo;
	
	@Inject
	private LoginRoleSetCodeAdapter adapter;
	
	@Inject
	private RecordDomRequireService requireService;
	

	@POST
	@Path("get-cache")
	public Ccg008Dto cache() {
		
		if (BuiltInUser.USER_ID.equals(AppContexts.user().userId())) {
			return Ccg008Dto.forBuiltInUser();
		}
		
		String employeeID = AppContexts.user().employeeId();
		GeneralDate systemDate = GeneralDate.today();
		
		Closure closure = ClosureService.getClosureDataByEmployee(
				ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter),
				new CacheCarrier(), employeeID, systemDate);
		if(closure == null) {
			return null;
		}
		InitDisplayPeriodSwitchSetDto rq609 = displayPeriodfinder.targetDateFromLogin();
		
		DatePeriod  datePeriod = rq609.getListDateProcessed().get(0).getDatePeriod();
		return new Ccg008Dto(closure.getClosureId().value,
				rq609.getCurrentOrNextMonth(),
				datePeriod.start().toString(),
				datePeriod.end().toString(),
				rq609.getListDateProcessed().get(0).getTargetDate().toString());
		 
	}
	
	
	@POST
	@Path("get-closure")
	public Ccg008Dto closure(ClosureParams params) {
		if (BuiltInUser.USER_ID.equals(AppContexts.user().userId())) {
			return new Ccg008Dto(1, 0, null, null, null);
		}
		DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(this.requireService.createRequire(), params.getClosureId(), YearMonth.of(params.getProcessDate()));
		return new Ccg008Dto(params.getClosureId(), 0 , datePeriodClosure.start().toString(), datePeriodClosure.end().toString(), params.getProcessDate().toString());
				
	}
	
	@POST
	@Path("get-setting")
	public ToppageSettingDto getSetting() {
		String cId = AppContexts.user().companyId();
		String eId = AppContexts.user().employeeId();
		
		if (BuiltInUser.EMPLOYEE_ID.equals(eId)) {
			return ToppageSettingDto.forBuiltInUser();
		}
		
		Optional<TopPageReloadSetting> reloadSetting = reloadRepo.getByCompanyId(cId);
		Require require = new TopPageSettingFinder.TopPageSettingRequireImpl(topPagePersonSettingRepo, topPageRoleSettingRepo, adapter);
		Optional<TopPageSettings> topPageSetting = settingService.getTopPageSettings(require, cId, eId);
		ToppageSettingDto result = ToppageSettingDto.builder().build();
		if(reloadSetting.isPresent()) {
			result.setCid(reloadSetting.get().getCid());
			result.setReloadInterval(reloadSetting.get().getReloadInterval().value);
		}
		if(topPageSetting.isPresent()) {
			result.setTopMenuCode(topPageSetting.get().getTopMenuCode().v());
			result.setSystem(topPageSetting.get().getMenuLogin().getSystem().value);
			result.setMenuClassification(topPageSetting.get().getMenuLogin().getMenuClassification().value);
			result.setLoginMenuCode(topPageSetting.get().getMenuLogin().getLoginMenuCode().v());
		}
		return result;
	}
	
	@POST
	@Path("get-user")
	public boolean getUserContext() {
		return AppContexts.user().roles().isInChargeAttendance();
	}
	
	@POST
	@Path("/save")
	public void saveSelfSetting(ToppageReloadSettingCommand comamnd) {
		this.addToppage.handle(comamnd);
	}
}
