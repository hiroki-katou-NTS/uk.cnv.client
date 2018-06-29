/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.app.command.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.security.hash.password.PasswordHash;
import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.gateway.app.command.login.dto.CheckChangePassDto;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoDtoImport;
import nts.uk.ctx.sys.gateway.dom.adapter.user.CheckBeforeChangePass;
import nts.uk.ctx.sys.gateway.dom.adapter.user.PassStatus;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserImportNew;
import nts.uk.ctx.sys.gateway.dom.login.Contract;
import nts.uk.ctx.sys.gateway.dom.login.ContractCode;
import nts.uk.ctx.sys.gateway.dom.login.ContractRepository;
import nts.uk.ctx.sys.gateway.dom.login.adapter.CompanyInformationAdapter;
import nts.uk.ctx.sys.gateway.dom.login.adapter.ListCompanyAdapter;
import nts.uk.ctx.sys.gateway.dom.login.adapter.RoleAdapter;
import nts.uk.ctx.sys.gateway.dom.login.adapter.RoleFromUserIdAdapter;
import nts.uk.ctx.sys.gateway.dom.login.adapter.RoleIndividualGrantAdapter;
import nts.uk.ctx.sys.gateway.dom.login.adapter.RoleType;
import nts.uk.ctx.sys.gateway.dom.login.adapter.SysEmployeeAdapter;
import nts.uk.ctx.sys.gateway.dom.login.dto.CompanyInforImport;
import nts.uk.ctx.sys.gateway.dom.login.dto.CompanyInformationImport;
import nts.uk.ctx.sys.gateway.dom.login.dto.EmployeeDataMngInfoImport;
import nts.uk.ctx.sys.gateway.dom.login.dto.EmployeeImport;
import nts.uk.ctx.sys.gateway.dom.login.dto.RoleImport;
import nts.uk.ctx.sys.gateway.dom.login.dto.RoleIndividualGrantImport;
import nts.uk.ctx.sys.gateway.dom.login.dto.SDelAtr;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.AccountLockPolicy;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.AccountLockPolicyRepository;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.LockInterval;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.LockOutMessage;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.PasswordPolicy;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.PasswordPolicyRepository;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.lockoutdata.LockOutData;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.lockoutdata.LockOutDataDto;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.lockoutdata.LockOutDataRepository;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.lockoutdata.LockType;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.loginlog.LoginLog;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.loginlog.LoginLogDto;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.loginlog.LoginLogRepository;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.loginlog.OperationSection;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.loginlog.SuccessFailureClassification;
import nts.uk.ctx.sys.gateway.dom.singlesignon.UseAtr;
import nts.uk.ctx.sys.gateway.dom.singlesignon.WindowsAccount;
import nts.uk.ctx.sys.gateway.dom.singlesignon.WindowsAccountInfo;
import nts.uk.ctx.sys.gateway.dom.singlesignon.WindowsAccountRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.loginuser.LoginUserContextManager;
import nts.uk.shr.com.enumcommon.Abolition;
import nts.uk.shr.com.system.config.InstallationType;

/**
 * The Class LoginBaseCommandHandler.
 *
 * @param <T>
 *            the generic type
 */
@Stateless
public abstract class LoginBaseCommandHandler<T> extends CommandHandlerWithResult<T, CheckChangePassDto> {

	/** The employee adapter. */
	@Inject
	private SysEmployeeAdapter employeeAdapter;

	/** The company information adapter. */
	@Inject
	private CompanyInformationAdapter companyInformationAdapter;

	/** The list company adapter. */
	@Inject
	private ListCompanyAdapter listCompanyAdapter;

	/** The manager. */
	@Inject
	private LoginUserContextManager manager;

	/** The contract repository. */
	@Inject
	private ContractRepository contractRepository;

	/** The role from user id adapter. */
	@Inject
	private RoleFromUserIdAdapter roleFromUserIdAdapter;

	/** The window account repository. */
	@Inject
	private WindowsAccountRepository windowAccountRepository;

	/** The user adapter. */
	@Inject
	private UserAdapter userAdapter;

	/** The account lock policy repository. */
	@Inject
	private AccountLockPolicyRepository accountLockPolicyRepository;

	/** The login log repository. */
	@Inject
	private LoginLogRepository loginLogRepository;

	/** The lock out data repository. */
	@Inject
	private LockOutDataRepository lockOutDataRepository;

	/** The Constant FIST_COMPANY. */
	private static final Integer FIST_COMPANY = 0;
	
	/** The role individual grant adapter. */
	@Inject
	private RoleIndividualGrantAdapter roleIndividualGrantAdapter;

	/** The Password policy repo. */
	@Inject
	private PasswordPolicyRepository PasswordPolicyRepo;
	
	/** The role adapter. */
	@Inject
	private RoleAdapter roleAdapter;
	
	/** The employee info adapter. */
	@Inject
	private EmployeeInfoAdapter employeeInfoAdapter;
	
	/** The company bs adapter. */
	@Inject
	private CompanyBsAdapter companyBsAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command
	 * .CommandHandlerContext)
	 */
	@Override
	@Transactional
	protected CheckChangePassDto handle(CommandHandlerContext<T> context) {
		return this.internalHanler(context);
	}

	/**
	 * Internal hanler.
	 *
	 * @param context the context
	 * @return the check change pass dto
	 */
	protected abstract CheckChangePassDto internalHanler(CommandHandlerContext<T> context);

	/**
	 * Re check contract.
	 *
	 * @param contractCode
	 *            the contract code
	 * @param contractPassword
	 *            the contract password
	 */
	protected void reCheckContract(String contractCode, String contractPassword) {
		InstallationType systemConfig = AppContexts.system().getInstallationType();
		// case Cloud
		if (systemConfig.value == InstallationType.CLOUD.value) {
			// reCheck contract
			// pre check contract
			this.checkContractInput(contractCode, contractPassword);
			// contract auth
			this.contractAccAuth(contractCode, contractPassword);
		}
	}

	/**
	 * Check contract input.
	 *
	 * @param contractCode
	 *            the contract code
	 * @param contractPassword
	 *            the contract password
	 */
	private void checkContractInput(String contractCode, String contractPassword) {
		if (StringUtil.isNullOrEmpty(contractCode, true)) {
			throw new RuntimeException();
		}
		if (StringUtil.isNullOrEmpty(contractPassword, true)) {
			throw new RuntimeException();
		}
	}

	/**
	 * Check employee del status.
	 *
	 * @param sid            the sid
	 * @return the check change pass dto
	 */
	protected CheckChangePassDto checkEmployeeDelStatus(String sid) {
		// get Employee status
		Optional<EmployeeDataMngInfoImport> optMngInfo = this.employeeAdapter.getSdataMngInfo(sid);

		if (!optMngInfo.isPresent() || !SDelAtr.NOTDELETED.equals(optMngInfo.get().getDeletedStatus())) {
			throw new BusinessException("Msg_301");
		}
		
		return new CheckChangePassDto(false, null);
	}

	/**
	 * Contract acc auth.
	 *
	 * @param contractCode
	 *            the contract code
	 * @param contractPassword
	 *            the contract password
	 */
	private void contractAccAuth(String contractCode, String contractPassword) {
		Optional<Contract> contract = contractRepository.getContract(contractCode);
		if (contract.isPresent()) {
			// check contract pass
			if (!PasswordHash.verifyThat(contractPassword, contract.get().getContractCode().v())
					.isEqualTo(contract.get().getPassword().v())) {
				throw new RuntimeException();
			}
			// check contract time
			if (contract.get().getContractPeriod().start().after(GeneralDate.today())
					|| contract.get().getContractPeriod().end().before(GeneralDate.today())) {
				throw new RuntimeException();
			}
		} else {
			throw new RuntimeException();
		}
	}

	/**
	 * Sets the logged info.
	 *
	 * @param user
	 *            the user
	 * @param em
	 *            the em
	 * @param companyCode
	 *            the company code
	 */
	public void setLoggedInfo(UserImportNew user, EmployeeImport em, String companyCode) {
		// set info to session
		manager.loggedInAsEmployee(user.getUserId(), em.getPersonalId(), user.getContractCode(), em.getCompanyId(),
				companyCode, em.getEmployeeId(), em.getEmployeeCode());
	}

	/**
	 * Inits the session.
	 *
	 * @param user            the user
	 * @return the check change pass dto
	 */
	// init session
	public CheckChangePassDto initSession(UserImportNew user) {
		List<String> lstCompanyId = listCompanyAdapter.getListCompanyId(user.getUserId(), user.getAssociatePersonId());
		if (lstCompanyId.isEmpty()) {
			manager.loggedInAsEmployee(user.getUserId(), user.getAssociatePersonId(), user.getContractCode(), null,
					null, null, null);
		} else {
			// get employee
			Optional<EmployeeImport> opEm = this.employeeAdapter.getByPid(lstCompanyId.get(FIST_COMPANY),
					user.getAssociatePersonId());

			if (opEm.isPresent()) {
				// Check employee deleted status.
				this.checkEmployeeDelStatus(opEm.get().getEmployeeId());
			}

			// save to session
			CompanyInformationImport companyInformation = this.companyInformationAdapter
					.findById(lstCompanyId.get(FIST_COMPANY));
			if (opEm.isPresent()) {
				// set info to session if em # null
				manager.loggedInAsEmployee(user.getUserId(), user.getAssociatePersonId(), user.getContractCode(),
						companyInformation.getCompanyId(), companyInformation.getCompanyCode(),
						opEm.get().getEmployeeId(), opEm.get().getEmployeeCode());
			} else {
				// set info to session
				manager.loggedInAsEmployee(user.getUserId(), user.getAssociatePersonId(), user.getContractCode(),
						companyInformation.getCompanyId(), companyInformation.getCompanyCode(), null, null);
			}
		}
		this.setRoleId(user.getUserId());
		
		return new CheckChangePassDto(false, null);
	}

	/**
	 * Check after login.
	 *
	 * @param user            the user
	 * @param oldPassword the old password
	 * @return true, if successful
	 */
	protected boolean checkAfterLogin(UserImportNew user, String oldPassword) {

		if (user.getPassStatus() == PassStatus.Official.value) {
			// Get PasswordPolicy
			Optional<PasswordPolicy> passwordPolicyOpt = this.PasswordPolicyRepo
					.getPasswordPolicy(new ContractCode(user.getContractCode()));

			if (passwordPolicyOpt.isPresent()) {
				// Event Check
				return this.checkEvent(passwordPolicyOpt.get(), user, oldPassword);
			}
			
			return true;
		}

		return true;

	}

	/**
	 * Check event.
	 *
	 * @param passwordPolicy            the password policy
	 * @param user            the user
	 * @param oldPassword the old password
	 * @return true, if successful
	 */
	protected boolean checkEvent(PasswordPolicy passwordPolicy, UserImportNew user, String oldPassword) {
		// Check passwordPolicy isUse
		if (passwordPolicy.isUse()) {
			// Check Change Password at first login
			if (passwordPolicy.isInitialPasswordChange()) {
				// Check state
				if (user.getPassStatus() != PassStatus.InitPassword.value) {
					// Math PassPolicy
					CheckBeforeChangePass mess = this.userAdapter.passwordPolicyCheckForSubmit(user.getUserId(),
							oldPassword, user.getContractCode());
					
					if (mess.isError()) return false;
					
					return true;
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the role id.
	 *
	 * @param userId            the new role id
	 * @return the check change pass dto
	 */
	// set roll id into login user context
	public CheckChangePassDto setRoleId(String userId) {
		String humanResourceRoleId = this.getRoleId(userId, RoleType.HUMAN_RESOURCE);
		String employmentRoleId = this.getRoleId(userId, RoleType.EMPLOYMENT);
		String salaryRoleId = this.getRoleId(userId, RoleType.SALARY);
		String officeHelperRoleId = this.getRoleId(userId, RoleType.OFFICE_HELPER);
		String companyManagerRoleId = this.getRoleId(userId, RoleType.COMPANY_MANAGER);
		String systemManagerRoleId = this.getRoleId(userId, RoleType.SYSTEM_MANAGER);
		String personalInfoRoleId = this.getRoleId(userId, RoleType.PERSONAL_INFO);
		String groupCompanyManagerRoleId = this.getRoleId(userId, RoleType.GROUP_COMAPNY_MANAGER);
		// 就業
		if (employmentRoleId != null) {
			manager.roleIdSetter().forAttendance(employmentRoleId);
		}
		// 給与
		if (salaryRoleId != null) {
			manager.roleIdSetter().forPayroll(salaryRoleId);
		}
		// 人事
		if (humanResourceRoleId != null) {
			manager.roleIdSetter().forPersonnel(humanResourceRoleId);
		}
		// オフィスヘルパー
		if (officeHelperRoleId != null) {
			manager.roleIdSetter().forOfficeHelper(officeHelperRoleId);
		}
		// 会計
		// マイナンバー
		// グループ会社管理
		if (groupCompanyManagerRoleId != null) {
			manager.roleIdSetter().forGroupCompaniesAdmin(groupCompanyManagerRoleId);
		}
		// 会社管理者
		if (companyManagerRoleId != null) {
			manager.roleIdSetter().forCompanyAdmin(companyManagerRoleId);
		}
		// システム管理者
		if (systemManagerRoleId != null) {
			manager.roleIdSetter().forSystemAdmin(systemManagerRoleId);
		}
		// 個人情報
		if (personalInfoRoleId != null) {
			manager.roleIdSetter().forPersonalInfo(personalInfoRoleId);
		}
		
		return new CheckChangePassDto(false, null);
	}

	/**
	 * Gets the role id.
	 *
	 * @param userId
	 *            the user id
	 * @param roleType
	 *            the role type
	 * @return the role id
	 */
	protected String getRoleId(String userId, RoleType roleType) {
		String roleId = roleFromUserIdAdapter.getRoleFromUser(userId, roleType.value, GeneralDate.today());
		if (roleId == null || roleId.isEmpty()) {
			return null;
		}
		return roleId;
	}

	/**
	 * Compare hash password.
	 *
	 * @param user            the user
	 * @param password            the password
	 * @return the string
	 */
	protected String compareHashPassword(UserImportNew user, String password) {
		if (!PasswordHash.verifyThat(password, user.getUserId()).isEqualTo(user.getPassword())) {
			// アルゴリズム「ロックアウト」を実行する ※２次対応
			this.lockOutExecuted(user);
			return "Msg_302";
		}
		return null;
	}

	/**
	 * Lock out executed.
	 *
	 * @param user
	 *            the user
	 */
	private void lockOutExecuted(UserImportNew user) {
		// ドメインモデル「アカウントロックポリシー」を取得する
		AccountLockPolicy accountLockPolicy = this.accountLockPolicyRepository
				.getAccountLockPolicy(new ContractCode(user.getContractCode())).get();
		if (accountLockPolicy.isUse()) {
			// ロックアウト条件に満たしているかをチェックする (Check whether the lockout condition is
			// satisfied)
			if (this.checkLoginLog(user.getUserId(), accountLockPolicy)) {
				// Add to domain model LockOutData
				LockOutDataDto dto = LockOutDataDto.builder().userId(user.getUserId())
						.contractCode(accountLockPolicy.getContractCode().v()).logoutDateTime(GeneralDateTime.now())
						.lockType(LockType.AUTO_LOCK.value).build();
				LockOutData lockOutData = new LockOutData(dto);
				this.lockOutDataRepository.add(lockOutData);
			}
		}
		// Add to the domain model LoginLog
		LoginLogDto dto = LoginLogDto.builder().userId(user.getUserId())
				.contractCode(accountLockPolicy.getContractCode().v()).processDateTime(GeneralDateTime.now())
				.successOrFail(SuccessFailureClassification.Failure.value).operation(OperationSection.Login.value)
				.programId(AppContexts.programId()).build();
		LoginLog loginLog = new LoginLog(dto);
		this.loginLogRepository.add(loginLog);
	}

	/**
	 * Check login log.
	 *
	 * @param userId
	 *            the user id
	 * @param accountLockPolicy
	 *            the account lock policy
	 * @return true, if successful
	 */
	private boolean checkLoginLog(String userId, AccountLockPolicy accountLockPolicy) {
		GeneralDateTime startTime = GeneralDateTime.now();
		// Check the domain model [Account lock policy. Error interval]
		if (accountLockPolicy.getLockInterval().lessThanOrEqualTo(new LockInterval(0))) {
			startTime = GeneralDateTime.fromString("1901/01/01 00:00:00", "yyyy/MM/dd HH:mm:ss");
		} else {
			startTime = startTime.addMinutes(-1 * accountLockPolicy.getLockInterval().minute());
		}
		// Search the domain model [LoginLog] and acquire [number of failed
		// logs] → [failed times]
		Integer countFailure = this.loginLogRepository.getLoginLogByConditions(userId, startTime);

		// Return LockOut
		if (countFailure < accountLockPolicy.getErrorCount().v().intValue()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Compare account.
	 *
	 * @param context the context
	 * @return the windows account
	 */
	// アルゴリズム「アカウント照合」を実行する
	protected WindowsAccount compareAccount(HttpServletRequest context) {
		// Windowsログイン時のアカウントを取得する
		// get UserName and HostName
		String username = AppContexts.windowsAccount().getUserName();
		String domain = AppContexts.windowsAccount().getDomain();
		
		//cut hostname
		String hostname = domain.substring(0, domain.lastIndexOf(";"));

		// ドメインモデル「Windowsアカウント情報」を取得する
		// ログイン時アカウントとドメインモデル「Windowsアカウント情報」を比較する - get 「Windowsアカウント情報」 from
		// 「Windowsアカウント」
		Optional<WindowsAccount> opWindowAccount = this.windowAccountRepository.findbyUserNameAndHostName(username,
				hostname);

		if (!opWindowAccount.isPresent()) {
			// エラーメッセージ（#Msg_876）を表示する。
			throw new BusinessException("Msg_876");
		}
		
		//set List WindowsAccountInfor
		List<WindowsAccountInfo> windows = opWindowAccount.get().getAccountInfos()
				.stream().filter(item -> item.getHostName().v().equals(hostname)
						&& item.getUserName().v().equals(username) && item.getUseAtr().equals(UseAtr.Use))
				.collect(Collectors.toList());
		
		if (windows.isEmpty()) {
			throw new BusinessException("Msg_876");
		}
		return opWindowAccount.get();
	}

	/**
	 * Gets the user and check limit time.
	 *
	 * @param windowAccount
	 *            the window account
	 * @return the user and check limit time
	 */
	public UserImportNew getUserAndCheckLimitTime(WindowsAccount windowAccount) {
		// get user
		Optional<UserImportNew> optUserImport = this.userAdapter.findByUserId(windowAccount.getUserId());

		// Validate limit time
		if (optUserImport.isPresent()) {
			if (optUserImport.get().getExpirationDate().before(GeneralDate.today())) {
				throw new BusinessException("Msg_316");
			}
		}
		return optUserImport.get();
	}
	
	/**
	 * Error check.
	 *
	 * @param userId the user id
	 * @param roleType the role type
	 * @param contractCode the contract code
	 */
	//アルゴリズム「エラーチェック（形式１）」を実行する 
	public void errorCheck(String userId, Integer roleType, String contractCode){
		
		GeneralDate date = GeneralDate.today();
		//切替可能な会社一覧を取得する Get list company
		List<String> companyIds = this.getListCompany(userId, date, roleType);
		
		if (companyIds.isEmpty()){
			throw new BusinessException("Msg_281"); 
		} else {
			if (!this.checkAccoutLock(contractCode, userId).v().isEmpty()) {
				//return messageError
				throw new BusinessException(this.checkAccoutLock(contractCode, userId).v());
			}
		}
	}
	
	/**
	 * Error check 2.
	 *
	 * @param companyId the company id
	 */
	//ルゴリズム「エラーチェック」を実行する (Execute algorithm "error check")
	public void errorCheck2(String companyId){
		
		//ドメインモデル「会社」の使用区分をチェックする (Check usage classification of domain model "company")
		CompanyInforImport company = this.companyInformationAdapter.findComById(companyId);
		
		if (company.getIsAbolition() == Abolition.ABOLISH.value){
			throw new BusinessException("Msg_281");
		}
	}
	
	/**
	 * Gets the list company.
	 *
	 * @param userId the user id
	 * @param date the date
	 * @param roleType the role type
	 * @return the list company
	 */
	//切替可能な会社一覧を取得する
	private List<String> getListCompany(String userId, GeneralDate date, Integer roleType){
		
		//ドメインモデル「ロール個人別付与」を取得する  (get List RoleIndividualGrant)
		List<RoleIndividualGrantImport> roles = this.roleIndividualGrantAdapter.getByUserIDDateRoleType(userId, date, roleType);
		
		List<RoleImport> roleImp = new ArrayList<>();
		
		if (!roles.isEmpty()){
			//ドメインモデル「ロール」を取得する (Acquire domain model "role"
			roles.stream().map(roleItem -> {
				return roleImp.addAll(this.roleAdapter.getAllById(roleItem.getRoleId()));
			}).collect(Collectors.toList());
		}
		
		GeneralDate systemDate = GeneralDate.today();
		
		//ドメインモデル「ユーザ」を取得する get domain "User"
		Optional<UserImportNew> user = this.userAdapter.getByUserIDandDate(userId, systemDate);
		
		List<EmployeeInfoDtoImport> employees = new ArrayList<>();
		
		if (!user.get().getAssociatePersonId().isEmpty()){
			employees.addAll(this.employeeInfoAdapter.getEmpInfoByPid(user.get().getAssociatePersonId()));
			
			employees.forEach(empItem -> {
				//アルゴリズム「社員が削除されたかを取得」を実行する (Execute the algorithm "社員が削除されたかを取得")
				if(this.employeeAdapter.getStatusOfEmployee(empItem.getEmployeeId()).isDeleted()){
					//社員（List）から当該社員を除く  (Remove the employee from the employee (List))
					employees.remove(empItem);
				}
			});
		}
		
		//imported（権限管理）「会社」を取得する  (imported (authority management) Acquire "company") Request No.51
		List<CompanyBsImport> companys = this.companyBsAdapter.getAllCompany();
		
		List<String> companyIdAll = companys.stream().map(item -> {
			return item.getCompanyId();
		}).collect(Collectors.toList());
		
		List<String> lstCompanyId = new ArrayList<>();
		
		// merge duplicate companyId from lstRole and lstEm
		if (!roleImp.isEmpty()){
			List<String> lstComp = new ArrayList<>();
			roleImp.forEach(role -> {
				if (role.getCompanyId() != null) {
					lstComp.add(role.getCompanyId());
				}
			});
			
			lstCompanyId.addAll(lstComp);
		}

		if (!employees.isEmpty()){
			List<String> lstComp = new ArrayList<>();
			employees.forEach(emp -> {
				if (emp.getCompanyId() != null) {
					lstComp.add(emp.getCompanyId());
				}
			});
			
			lstCompanyId.addAll(lstComp);
		}
		
		lstCompanyId = lstCompanyId.stream().distinct().collect(Collectors.toList());
		
		//取得した会社（List）から、会社IDのリストを抽出する (Extract the list of company IDs from the acquired company (List))
		List<String> lstCompanyFinal = lstCompanyId.stream().filter(com -> companyIdAll.contains(com)).collect(Collectors.toList());
		
		return lstCompanyFinal;
	}
	
	/**
	 * Check accout lock.
	 *
	 * @param contractCode the contract code
	 * @param userId the user id
	 * @return the lock out message
	 */
	private LockOutMessage checkAccoutLock(String contractCode, String userId){
		//ドメインモデル「アカウントロックポリシー」を取得する (Acquire the domain model "account lock policy")
		if (this.accountLockPolicyRepository.getAccountLockPolicy(new ContractCode(contractCode)).isPresent()){
			AccountLockPolicy accountLockPolicy = this.accountLockPolicyRepository
					.getAccountLockPolicy(new ContractCode(contractCode)).get();
			if (accountLockPolicy.isUse()) {
				//ドメインモデル「ロックアウトデータ」を取得する (Acquire domain model "lockout data")
				Optional<LockOutData> lockoutData = this.lockOutDataRepository.findByUserId(userId);
				
				if (lockoutData.isPresent()){
					//エラーメッセージ（ドメインモデル「アカウントロックポリシー.ロックアウトメッセージ」）を表示する 
					//(Display error message (domain model "Account lock policy. Lockout message"))
					return accountLockPolicy.getLockOutMessage();
				}
			}
		}
		return new LockOutMessage(null);
	}
}
