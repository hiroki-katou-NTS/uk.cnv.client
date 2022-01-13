package nts.uk.ctx.cloud.operate.app.command;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.scoped.session.SessionContextProvider;
import nts.arc.task.tran.AtomTask;
import nts.arc.task.tran.TransactionService;
import nts.uk.ctx.bs.company.dom.company.Company;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.cloud.operate.dom.tenant.CreateDefaultCompany;
import nts.uk.ctx.cloud.operate.dom.service.CreateInitialOptionsService;
import nts.uk.ctx.cloud.operate.dom.tenant.CreateTenant;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyDataCommandHanlder;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthentication;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationRepository;
import nts.uk.shr.com.company.CompanyId;
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.com.context.loginuser.DefaultLoginUserContext;
import nts.uk.shr.com.context.loginuser.LoginUserContextManager;
import nts.uk.shr.com.context.loginuser.SessionLowLayer;
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

	@Inject
	LoginUserContextManager manager;

	@Override
	protected void handle(CommandHandlerContext<CreateTenantOnCloudCommand> context) {

		CreateTenantOnCloudCommand command = context.getCommand();

		//TODO
		RequireImpl require = new RequireImpl();
//		AtomTask at = CreateInitialOptionsService.create(require,
//				context.getCommand().getTenanteCode(),
//				context.getCommand().getOptionCode());

		// いろいろ作成する前に新規で作るテナントコードをログインセッションにセットしておく
		String companyCode = "0001";
		manager.loggedInAsUser(
				"",
				"",
				command.getTenanteCode(),
				CompanyId.create(command.getTenanteCode(), companyCode),
				companyCode
		);

		//02 TODO
		//人数ライセンス　皮だけ作るやつ
		//03 TODO
		//テナントを作る()
		AtomTask createTenant = CreateTenant.create(require,
										command.getTenanteCode(),
										command.getTenantStartDate(),
										command.getTenantPassword());
		//05 TODO
		//デフォルト会社を作る()
		AtomTask createCompany = CreateDefaultCompany.create(
				require,
				command.getTenanteCode(),
				command.getCompanyName());
		
//		List<AtomTask> tasks = Arrays.asList(at, createTenant, createCompany);
		List<AtomTask> tasks = Arrays.asList(createTenant, createCompany);
		transaction.allInOneTransaction(tasks);
	}

	@AllArgsConstructor
	private class RequireImpl implements
			CreateInitialOptionsService.Require,
			CreateDefaultCompany.Require,
			CreateTenant.Require{

		@Override
		public boolean existsTenant(String tenantCode) {
			return tenantAuthenticationRepository.find(tenantCode).isPresent();
		}

		public void save(TenantAuthentication tenantAuthentication) {
			tenantAuthenticationRepository.insert(tenantAuthentication);
		}

		@Override
		public void saveInitialOption(OptionLicensePurchaseState optionLicensePurchaseState) {
			// TODO Auto-generated method stub
		}

		@Override
		public void save(Company company) {
			companyRepository.insertCom(company);
		}
	}
}
