package nts.uk.ctx.sys.portal.app.find.roleset;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.portal.dom.adapter.employee.EmployeeAdapter;
import nts.uk.ctx.sys.portal.dom.adapter.employee.EmployeeDto;
import nts.uk.ctx.sys.portal.dom.adapter.role.AffJobHistoryDto;
import nts.uk.ctx.sys.portal.dom.adapter.role.RoleGrantAdapter;
import nts.uk.ctx.sys.portal.dom.adapter.role.RoleSetGrantedJobTitleDetailDto;
import nts.uk.ctx.sys.portal.dom.adapter.role.RoleSetGrantedPersonDto;
import nts.uk.ctx.sys.portal.dom.adapter.user.UserAdapter;
import nts.uk.ctx.sys.portal.dom.adapter.user.UserDto;

@Stateless
public class RoleSetPortalFinder {

	@Inject
	private UserAdapter userAdapter;
	
	@Inject
	private RoleGrantAdapter roleAdapter;
	
	@Inject
	private EmployeeAdapter employeeAdapter;
	
	public Optional<String> getRoleSetCode(String companyId, String userId) {
		Optional<UserDto> userOpt = userAdapter.getUserInfo(userId);
		if (!userOpt.isPresent()) return Optional.empty(); 
		UserDto user = userOpt.get();
		if (user.getAssociatedPersonID() == null) return Optional.empty();
		Optional<EmployeeDto> employeeOpt = employeeAdapter.getEmployee(companyId, user.getAssociatedPersonID());
		if (!employeeOpt.isPresent()) return Optional.empty();
		EmployeeDto employee = employeeOpt.get();
		Optional<RoleSetGrantedPersonDto> roleSetGrantedPerson = roleAdapter.getRoleSetPersonGrant(employee.getEmployeeId());
		
		Optional<String> roleSetCd = Optional.empty();
		if (roleSetGrantedPerson.isPresent()) {
			roleSetCd = roleSetGrantedPerson.map(roleSetPerson -> roleSetPerson.getRoleSetCd());
		} else {
			Optional<AffJobHistoryDto> jobHistory = roleAdapter.getAffJobHist(employee.getEmployeeId(), GeneralDate.today());
			if (jobHistory.isPresent()) {
				Optional<RoleSetGrantedJobTitleDetailDto> roleSetGrantedJobTitle = roleAdapter.getRoleSetJobTitleGrant(companyId, jobHistory.get().getJobTitleId());
				if (roleSetGrantedJobTitle.isPresent()) {
					roleSetCd = roleSetGrantedJobTitle.map(roleSetJobTitle -> roleSetJobTitle.getRoleSetCd());
				} else {
					// TODO: Get 兼務職位履歴 and check if not present, then get default role set 
					// otherwise, get RoleSetGrantedJobTitleDetailDto with retrieved job title Id
					// and applyToConcurrentPerson = true
					roleSetCd = getDefaultRoleSet(companyId);
				}
			} else {
				roleSetCd = getDefaultRoleSet(companyId);
			}
		}
		return roleSetCd;
	}
	
	/**
	 * Get default role set.
	 * @param companyId company Id
	 * @return default role set
	 */
	private Optional<String> getDefaultRoleSet(String companyId) {
		// デフォルトロールセットがない可能性がある　仕様が変わった
		return roleAdapter.getDefaultRoleSet(companyId)
				.map(roleSet -> roleSet.getRoleSetCd());
	}
}
