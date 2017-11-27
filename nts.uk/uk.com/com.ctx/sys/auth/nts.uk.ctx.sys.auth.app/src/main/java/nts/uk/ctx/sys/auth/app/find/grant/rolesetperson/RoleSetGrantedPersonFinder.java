package nts.uk.ctx.sys.auth.app.find.grant.rolesetperson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BusinessException;
import nts.uk.ctx.sys.auth.app.find.grant.rolesetjob.RoleSetDto;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeAdapter;
import nts.uk.ctx.sys.auth.dom.employee.dto.EmployeeImport;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
public class RoleSetGrantedPersonFinder {

	@Inject
	private RoleSetRepository roleSetRepo;

	@Inject
	private RoleSetGrantedPersonRepository roleSetPersonRepo;

	@Inject
	private EmployeeAdapter employeeAdapter;

	public List<RoleSetDto> getAllRoleSet() {
		String companyId = AppContexts.user().companyId();
		if (StringUtils.isNoneEmpty(companyId))
			return null;
		// get Role Set by companyId, sort ASC
		List<RoleSetDto> listRoleSet = roleSetRepo.findByCompanyId(companyId).stream()
				.map(item -> new RoleSetDto(item.getRoleSetCd().v(), item.getRoleSetName().v()))
				.collect(Collectors.toList());

		if (listRoleSet == null || listRoleSet.isEmpty()) {
			throw new BusinessException("Msg_713");
		}

		listRoleSet.sort((rs1, rs2) -> rs1.getCode().compareTo(rs2.getCode()));
		return listRoleSet;

	}

	public List<RoleSetGrantedPersonDto> getAllRoleSetGrantedPersonByRoleSetCd(String roleSetCd) {
		String companyId = AppContexts.user().companyId();
		if (StringUtils.isNoneEmpty(companyId))
			return null;
		List<RoleSetGrantedPerson> listRoleSetPerson = roleSetPersonRepo.getAll(roleSetCd, companyId);

		if (listRoleSetPerson != null && !listRoleSetPerson.isEmpty()) {
			List<RoleSetGrantedPersonDto> listRoleSetPersonDto = new ArrayList<>();
			for (RoleSetGrantedPerson rp : listRoleSetPerson) {
				EmployeeImport empInfo = employeeAdapter.findByEmpId(rp.getEmployeeID());
				RoleSetGrantedPersonDto dto = new RoleSetGrantedPersonDto(rp.getRoleSetCd().v(), rp.getEmployeeID(),
						empInfo.getEmployeeCode(), empInfo.getPersonalName(), rp.getValidPeriod().start(),
						rp.getValidPeriod().end());
				listRoleSetPersonDto.add(dto);
			}
			// sort by empCd (SCD) asc
			listRoleSetPersonDto.sort((rsp1, rsp2) -> rsp1.getEmployeeCd().compareTo(rsp2.getEmployeeCd()));
			return listRoleSetPersonDto;
		} else
			return null;
	}
	
	public EmployeeImport getEmployeeInfo(String employeeId){
		return employeeAdapter.findByEmpId(employeeId);
	}
}
