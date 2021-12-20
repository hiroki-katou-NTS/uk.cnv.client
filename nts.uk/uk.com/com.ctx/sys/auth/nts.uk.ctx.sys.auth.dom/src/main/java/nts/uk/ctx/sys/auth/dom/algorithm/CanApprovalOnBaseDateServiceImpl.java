package nts.uk.ctx.sys.auth.dom.algorithm;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.codec.binary.StringUtils;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.employee.dto.EmJobTitleHisImport;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitle;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.ApprovalAuthority;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.wkpmanager.EmpInfoAdapter;

@Stateless
public class CanApprovalOnBaseDateServiceImpl implements CanApprovalOnBaseDateService {

	@Inject
	private RoleSetGrantedPersonRepository roleSetPersonRepo;

//	@Inject
//	private JobTitleAdapter jobTitleAdapter;
	
	@Inject 
	private EmpInfoAdapter empInfoAdapter;

	@Inject
	private RoleSetGrantedJobTitleRepository roleSetGrantedJobTitleRepo;

	@Inject
	private RoleSetRepository roleSetRepo;
	
	@Inject
	private RoleRepository roleRepository;

	@Override
	public boolean canApprovalOnBaseDate(String companyId, String employeeID, GeneralDate date) {
		// Acquire domain model "Role set Individual Grant"
		String roleSetCode = "";
		Optional<RoleSetGrantedPerson> roleSetGrand = roleSetPersonRepo.findByIDAndDate(companyId, employeeID, date);
		if (!roleSetGrand.isPresent()) {
			//lấy thông tin request 33
	//		JobTitleValueImport jobTitle = jobTitleAdapter.findJobTitleBySid(employeeID, date);
			Optional<EmJobTitleHisImport> jobTitle = empInfoAdapter.getTitleHist(employeeID, date);
			if (!jobTitle.isPresent()){
				//Muto san
				//RQL305の「指定社員が基準日に承認権限を持っているかチェックする」
				//この処理は、outputでbooleanを返しますが、
				//基準日で職位が取得できない場合、falseを返したいです
				return false;
			}
			Optional<RoleSetGrantedJobTitle> roleJobTitle = roleSetGrantedJobTitleRepo.getByJobTitleId(companyId, jobTitle.get().getJobTitleID());
			if (roleJobTitle.isPresent()) {
				roleSetCode = roleJobTitle.get().getRoleSetCd().v();
			}
		} else {
			roleSetCode = roleSetGrand.get().getRoleSetCd().toString();
		}
		// Acquire domain model "Roll set"
		Optional<RoleSet> roleSet = roleSetRepo.findByRoleSetCdAndCompanyId(roleSetCode.toString(), companyId);
		if (roleSet.isPresent()) {
			RoleSet rs = roleSet.get();
			
			if (!rs.getEmploymentRoleId().isPresent()) {
				return false;
			} else {
				// 就業ロールIDから承認権限がある就業ロールを取得する
				Optional<Role> role = roleRepository.getRoleWorks(companyId, rs.getEmploymentRoleId().get());
				
				
				return role.flatMap(x -> x.getApprovalAuthority()).orElse(false);
			}
			
		} else {
			return false;
		}
	}

}
