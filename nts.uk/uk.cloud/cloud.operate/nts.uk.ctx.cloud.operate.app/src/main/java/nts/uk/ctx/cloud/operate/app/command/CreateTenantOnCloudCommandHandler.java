package nts.uk.ctx.cloud.operate.app.command;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.task.tran.TransactionService;
import nts.uk.ctx.bs.company.dom.company.Company;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.cloud.operate.dom.service.CreateDefaultCompanyService;
import nts.uk.ctx.cloud.operate.dom.service.CreateInitialOptionsService;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthentication;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationRepository;
import nts.uk.ctx.sys.tenant.dom.service.CreateTenantService;
import nts.uk.shr.com.license.option.purchasestate.OptionLicensePurchaseState;

/**
 * クラウド環境にテナントを追加する
 * @author ai_muto
 *
 */
@Stateless
@Transactional
public class CreateTenantOnCloudCommandHandler extends CommandHandler<CreateTenantOnCloudCommand> {

//	@Inject
//    private CreateInitialOptionsService createInisialOptionService;

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private TenantAuthenticationRepository tenantAuthenticationRepository;
	
	@Inject
	private TransactionService transaction;
	
	@Override
	protected void handle(CommandHandlerContext<CreateTenantOnCloudCommand> context) {
		//TODO
		RequireImple require = new RequireImple();
//		AtomTask at = CreateInitialOptionsService.create(require,
//				context.getCommand().getTenanteCode(),
//				context.getCommand().getOptionCode());

		//02 TODO
		//人数ライセンス　皮だけ作るやつ
		//03 TODO
		//テナントを作る()
		AtomTask createTenant = CreateTenantService.create(require,
										context.getCommand().getTenanteCode(),
										context.getCommand().getTenantPassword());
		//05 TODO
		//デフォルト会社を作る()
		AtomTask createCompany = CreateDefaultCompanyService.create(require, 
																	context.getCommand().getTenanteCode(),
																	context.getCommand().getCompanyName());
		
//		List<AtomTask> tasks = Arrays.asList(at, createTenant, createCompany);
		List<AtomTask> tasks = Arrays.asList(createTenant, createCompany);
		transaction.allInOneTransaction(tasks);
	}

	@AllArgsConstructor
	private class RequireImple implements CreateInitialOptionsService.Require,
																	  CreateDefaultCompanyService.Require,
																	  CreateTenantService.Require{

		public void save(TenantAuthentication tenantAuthentication) {
			tenantAuthenticationRepository.insert(tenantAuthentication);
		}
		@Override
		public void saveDefaultCompany(Company company) {
			companyRepository.insertCom(company);
		}
		@Override
		public void saveInitialOption(OptionLicensePurchaseState optionLicensePurchaseState) {
			// TODO Auto-generated method stub
		}
	}
}
