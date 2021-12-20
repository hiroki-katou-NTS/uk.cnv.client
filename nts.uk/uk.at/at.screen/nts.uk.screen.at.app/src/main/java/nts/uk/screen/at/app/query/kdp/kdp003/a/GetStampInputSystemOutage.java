package nts.uk.screen.at.app.query.kdp.kdp003.a;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.gateway.dom.outage.CheckSystemAvailability;
import nts.uk.ctx.sys.gateway.dom.outage.PlannedOutage;
import nts.uk.ctx.sys.gateway.dom.outage.company.PlannedOutageByCompany;
import nts.uk.ctx.sys.gateway.dom.outage.company.PlannedOutageByCompanyRepository;
import nts.uk.ctx.sys.gateway.dom.outage.tenant.PlannedOutageByTenant;
import nts.uk.ctx.sys.gateway.dom.outage.tenant.PlannedOutageByTenantRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.loginuser.role.LoginUserRoles;
import nts.uk.shr.com.operation.SystemOperationSetting;
import nts.uk.shr.com.operation.SystemOperationSettingAdapter;

/**
 * 打刻入力(共有)システム停止について取得する
 * UKDesign.UniversalK.就業.KDP_打刻.KDP003_打刻入力(氏名選択).A:打刻入力(氏名選択).メニュー別OCD.打刻入力(共有)システム停止について取得する
 * @author chungnt
 *
 */

@Stateless
public class GetStampInputSystemOutage {

	@Inject
	private PlannedOutageByTenantRepository plannedOutageByTenantRepop;
	
	@Inject
	private PlannedOutageByCompanyRepository plannedOutageByCompanyRepo;
	
	@Inject
	private SystemOperationSettingAdapter systemOperationSettingAdapter;
	
	public GetStampInputSystemOutageDto getStampInputSystemOutage() {
		GetStampInputSystemOutageDto result = new GetStampInputSystemOutageDto();
		
		CheckSystemAvailabilityImp require = new CheckSystemAvailabilityImp();
		String companyId = AppContexts.user().companyId();
		String userId = AppContexts.user().userId();
		String tenantCode = AppContexts.user().contractCode();
		
		PlannedOutage.Status status = CheckSystemAvailability.isAvailable(require, tenantCode, companyId, userId);
		
		SystemOperationSetting systemOperationSetting = systemOperationSettingAdapter.getSetting();
		
		result.setStopMessage(status.getMessage().map(m -> m).orElse(""));
		result.setNotiMessage(systemOperationSetting.getMessage());
		
		if (result.getNotiMessage() != null && !result.getNotiMessage().equals("")) {
			result.setStopSystem(true);
		}
		
		return result;
	}
	
	class CheckSystemAvailabilityImp implements CheckSystemAvailability.Require {
		
		private LoginUserRoles loginUserRoles;

		@Override
		public Optional<PlannedOutageByTenant> getPlannedOutageByTenant(String tenantCode) {
			return plannedOutageByTenantRepop.find(tenantCode);
		}

		@Override
		public Optional<PlannedOutageByCompany> getPlannedOutageByCompany(String companyId) {
			return plannedOutageByCompanyRepo.find(companyId);
		}

		@Override
		public LoginUserRoles getLoginUserRoles(String userId) {
			return loginUserRoles;
		}	
	}
}
