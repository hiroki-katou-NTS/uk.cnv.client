package nts.uk.ctx.pereg.app.find.licence;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.EmployeeLicense;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.EmployeeLicenseRepository;
import nts.uk.ctx.bs.employee.pub.employment.IEmployeeDataMngInfoPub;
import nts.uk.ctx.sys.auth.app.find.person.role.GetWhetherLoginerCharge;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class LicenseCheckFinder {

	@Inject
	private GetWhetherLoginerCharge getWhether;

	@Inject
	private EmployeeLicenseRepository employeeLicenseRepo;

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private IEmployeeDataMngInfoPub iEmployeeDataMngInfoPub;

	public LicensenCheckDto checkLicense() {
		//ライセンス上限をチェックする	
		String contractCD = AppContexts.user().contractCode();
		int registered = 0;
		int canBeRegistered = 0;
		String licenseKey = "";
		Optional<EmployeeLicense> employeeLicense = employeeLicenseRepo.findByKey(contractCD);

		if (!employeeLicense.isPresent()) {
			throw new RuntimeException("Can't find Employee License with contracCD" + contractCD);
		}
		EmployeeLicense license = employeeLicense.get();

		boolean display = checkDislay();
		
		LicenseUpperLimit licenseUpperLimit =  checkLicenseUpverLimit(GeneralDate.today());
		int status = licenseUpperLimit.getStatus().value;
		//終了状態＝ライセンス超過、ライセンス到達の場合
		registered = licenseUpperLimit.getRegistered();
		canBeRegistered = licenseUpperLimit.getCanBeRegistered();
		licenseKey = license.getLicenseKey().v();
		if(status == EndStatusLicenseCheck.OVER.value||status == EndStatusLicenseCheck.REACHED.value){
			String message = "Msg_1370";
			return new LicensenCheckDto(display, registered, canBeRegistered, licenseUpperLimit.getMaxRegistered(), message, licenseKey);
		}
		else if(status == EndStatusLicenseCheck.WARNING.value ){
			String message = "Msg_1371";
			return new LicensenCheckDto(display, registered, canBeRegistered, licenseUpperLimit.getMaxRegistered(), message, licenseKey);
		}
		else{
			return new LicensenCheckDto(display, registered, canBeRegistered, licenseUpperLimit.getMaxRegistered(), "", licenseKey);
		}
		
	}

	/**
	 * CPS001_ThanhPV add function check License when Start screen.
	 * 
	 */
	public LicensenCheckDto checkLicenseStartCPS001() {
		boolean dipslay = this.checkDislay();
		LicenseUpperLimit licenseUpperLimit = this.checkLicenseUpverLimit(GeneralDate.today());
		String msg = "";
		if(licenseUpperLimit.getStatus()==EndStatusLicenseCheck.OVER) {
			msg = "Msg_1372";
		}else if(licenseUpperLimit.getStatus()==EndStatusLicenseCheck.REACHED) {
			msg = "Msg_1373";
		}else if(licenseUpperLimit.getStatus()==EndStatusLicenseCheck.WARNING) {
			msg = "Msg_1371";
		}
		return new LicensenCheckDto(dipslay, licenseUpperLimit.getRegistered(), licenseUpperLimit.getCanBeRegistered(), licenseUpperLimit.getMaxRegistered(), msg, "");
	}
    //ライセンス表示するかどうかチェックする 
	private boolean checkDislay() {
		String sysAdminID = AppContexts.user().roles().forSystemAdmin();
		String groupAdminID = AppContexts.user().roles().forGroupCompaniesAdmin();
		String companyAdminID = AppContexts.user().roles().forCompanyAdmin();
		// システム管理者、グループ会社管理者、会社管理者のいずれかに該当する（ロールIDが設定されている）場合
		// (trong t/h thỏa mãn 1 trong: người quản lý system, người quản lý
		// company group, người quản lý company)
		
		//ライセンス仕様がちゃんと決まらないまま2021年の12月納品を迎えようとしているため
		//ライセンスを強制的に表示させないようにする。
		//仕様が固まり次第、99-104行目までのコードを復活させてください。
		return false;
//		if (!StringUtil.isNullOrEmpty(sysAdminID, true) || !StringUtil.isNullOrEmpty(groupAdminID, true) || !StringUtil.isNullOrEmpty(companyAdminID, true)) {
//			return true;
//		} else {
//			RoleWhetherLoginDto checkLogin = getWhether.getWhetherLoginerCharge();
//			return checkLogin.checkRole();
//		}
	}
	
	// ライセンス上限をチェックする - thuật toán: check license upper limit
	public LicenseUpperLimit checkLicenseUpverLimit(GeneralDate systemDate) {
		String contractCD = AppContexts.user().contractCode();
		Optional<EmployeeLicense> employeeLicense = employeeLicenseRepo.findByKey(contractCD);
		if (!employeeLicense.isPresent()) {
			throw new RuntimeException("Can't find EmployeeLicense>" + contractCD);
		}

		// RequestList503
		// アルゴリズム「廃止を除いて同一契約の会社をすべて取得する」(thuật toan)
		
		List<String> lstCompID = companyRepository.getAllCompanyByContractCdAndAboAtr(contractCD, 0);
	
		int numberPeopleEnrolled = iEmployeeDataMngInfoPub.countEmployeeByBaseDate(lstCompID, systemDate);

		// 在籍人数が、社員ライセンスの上限人数を超えているかチェックする(check số người thực đăng ký có vượt
		// quá số người giới hạn trên của employee license không)
		// 社員ライセンス．上限人数 ＜ 在籍人数
		
		int maxRegistered = employeeLicense.get().getMaxNumberLicenses().v();
		if (maxRegistered < numberPeopleEnrolled) {
			return new LicenseUpperLimit(EndStatusLicenseCheck.OVER, numberPeopleEnrolled, 0, maxRegistered);
		} else if (maxRegistered == numberPeopleEnrolled) {
			return new LicenseUpperLimit(EndStatusLicenseCheck.REACHED, numberPeopleEnrolled, 0, maxRegistered);
		} else {
			// 在籍人数 ＜ 社員ライセンス．上限人数
			int canBeRegistered = employeeLicense.get().getMaxNumberLicenses().v() - numberPeopleEnrolled;
			// 登録可能な残り人数 ＜＝ 社員ライセンス．警告人数
			if (canBeRegistered <= employeeLicense.get().getWarningNumberLicenses().v())
				return new LicenseUpperLimit(EndStatusLicenseCheck.WARNING, numberPeopleEnrolled, canBeRegistered, maxRegistered);
			else {
				return new LicenseUpperLimit(EndStatusLicenseCheck.NORMAL, numberPeopleEnrolled, canBeRegistered, maxRegistered);
			}
		}

	}

}
