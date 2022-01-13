package nts.uk.ctx.sys.auth.pubimp.grant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.pub.department.master.DepartmentPub;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.sys.auth.dom.algorithm.CanApprovalOnBaseDateService;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;
import nts.uk.ctx.sys.auth.dom.roleset.ApprovalAuthority;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.wkpmanager.EmpInfoAdapter;
import nts.uk.ctx.sys.auth.pub.grant.RoleSetGrantedEmployeePub;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class RoleSetGrantedEmployeePubImpl implements RoleSetGrantedEmployeePub {

	@Inject
	private RoleSetRepository roleSetRepo;

	@Inject
	private RoleSetGrantedPersonRepository roleSetPersonRepo;

	@Inject
	private WorkplaceAdapter wkpAdapter;
	
	@Inject
	private CanApprovalOnBaseDateService canApprovalOnBaseDateService;
	@Inject
	private EmpInfoAdapter empInfoAdapter;
	@Inject
	private RoleSetGrantedJobTitleRepository repoRoleJob;
	@Inject
	private DepartmentPub departmentPub;
	
	@Override
	public List<String> findEmpGrantedInWorkplace(String workplaceId, DatePeriod period) {
		String companyId = AppContexts.user().companyId();

		// Execute the algorithm "Acquire Employees from the Workplace"
		List<String> empIds = wkpAdapter.findListSIdByCidAndWkpIdAndPeriod(workplaceId, period.start(), period.end())
				.stream().map(item -> item.getEmployeeId()).collect(Collectors.toList());

		// TODO 2021.09.28 delete field approvalAuthority in domain RoleSet
		//Acquire the domain model "Role set"
		//roleSetRepo.findByCompanyId(companyId).stream().filter(item -> item.getApprovalAuthority() == ApprovalAuthority.HasRight).collect(Collectors.toList());
		List<RoleSet> roleSets = new ArrayList<>();

		// Acquire domain model "Role set Granted person"
		List<RoleSetGrantedPerson> roleSetPersons = new ArrayList<>();
		for (RoleSet roleSet : roleSets) {
			List<RoleSetGrantedPerson> tmp = roleSetPersonRepo.getAll(roleSet.getRoleSetCd().v(), companyId);
			if (tmp != null && !tmp.isEmpty()) {
				tmp.stream().filter(item -> item.getValidPeriod().contains(period)).collect(Collectors.toList());
				roleSetPersons.addAll(tmp);
			}
		}
		List<String> empIds2 = roleSetPersons.stream().filter(item -> empIds.contains(item.getEmployeeID())).map(item -> item.getEmployeeID()).collect(Collectors.toList());

		return empIds2;
	}

	@Override
	public boolean canApprovalOnBaseDate(String companyId, String employeeID, GeneralDate date) {
		return canApprovalOnBaseDateService.canApprovalOnBaseDate(companyId, employeeID, date);
	}

	@Override
	public List<String> findEmpGrantedInWkpVer2(List<String> lstWkpDepId, GeneralDate baseDate, int sysAtr) {
		String companyId = AppContexts.user().companyId();
		List<String> lst0 = new ArrayList<>();
		if(sysAtr == 0) {//就業の場合
			//アルゴリズム「職場から社員を取得する」を実行する-(Lấy employee từ Workplace") no.466
			lst0 = empInfoAdapter.getListEmployeeId(lstWkpDepId, new DatePeriod(baseDate,baseDate));
		}else {
			//期間内に特定の部門（List）に所属している社員一覧を取得
			lst0 = departmentPub.getlstSidByDepAndDate(lstWkpDepId, new DatePeriod(baseDate, baseDate));
		}
		List<String> lst1 = lst0;		
		//ドメインモデル「ロールセット」を取得する-(Lấy domain [RoleSet])
		//条件： 会社ID←ログイン会社ID; 承認権限＝true
		// TODO 2021.09.28 delete field approvalAuthority in domain RoleSet
		List<String> roleSetCDLst = new ArrayList<>();
		/**roleSetRepo.findByCompanyId(companyId).stream()
				.filter(item -> item.getApprovalAuthority() == ApprovalAuthority.HasRight)
				.map(c -> c.getRoleSetCd().v())
				.collect(Collectors.toList()); */
		//ドメインモデル「ロールセット個人別付与」を取得する-(Lấy domain [RoleSetGrantedPerson])
//		条件：
//		会社ID←ログイン会社ID (Company ID ← Login company ID)
//		社員ID←取得した社員ID（list）（１）(Employee ID ← Employee ID acquired (list))
//		ロールセットコード←取得したロールセット．コード(list) (Roll set code ← The acquired roll set code)
//		期間From <= 基準日 <= 期間To (Period From <= baseDate <= Period To) 
		List<String> lst2 = roleSetPersonRepo.getSidByRoleSidDate(companyId, lst1, roleSetCDLst, baseDate);
		//ドメインモデル「ロールセット職位別付与」を取得する-(Lấy domain [RoleSetGrantedJobTitle])
		//条件： 会社ID←ログイン会社ID; ロールセットコード←取得したロールセットコード（list）
		List<String> jobTitleIds = repoRoleJob.findJobTitleByRoleCDLst(companyId, roleSetCDLst);
		//imported（権限管理）「職位リストと基準日から社員を取得する」-Lấy employee từ list chức vụ và baseDate
		//RequestList No.515
		//条件： 会社ID←ログイン会社ID;基準日←システム日付;職位ID（List）←取得したロールセット職位別付与．職位ID（list）
		List<String> lst3 = new ArrayList<>();
		if(!jobTitleIds.isEmpty()){
			//hoatt 2018.12.17
			//EA修正履歴 No.3022
			//基準日←パラメータ．基準日
			lst3 = empInfoAdapter.getListEmployee(jobTitleIds, baseDate);
		}
		//（１）の社員リストと（３）の社員リスト両方に存在する社員IDを抽出する-(Xuất ra employeeID có trong cả 2 list: empoyeeList（１） và （３）)
		List<String> lst4 = lst3.stream().filter(c -> lst1.contains(c)).collect(Collectors.toList());
		//取得した社員IDのリストを、重複を除いてマージする-(Loại trừ các employee trùng lặp trong list employeeID đã lấy, và kết hợp lại)
		List<String> result = lst2;
		for (String c : lst4) {
			if(!result.contains(c)){
				result.add(c);
			}
		}
		return result;
	}

}
