package nts.uk.screen.com.app.find.cmm018;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.algorithm.AcquireUserIDFromEmpIDService;
import nts.uk.ctx.sys.auth.dom.permission.roleId.SpecificWorkPlaceAdaper;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService;
import nts.uk.ctx.sys.auth.dom.wplmanagementauthority.domservice.AprrovalWorkPlaceDomService;
import nts.uk.shr.com.context.AppContexts;

/**
 * 承認権限を持っている社員一覧を取得
 * @author hoangnd
 *
 */
@Stateless
public class ScreenQueryApprovalAuthorityEmp {
	
	@Inject
	private SpecificWorkPlaceAdaper specificWorkPlaceAdaper;
	
	@Inject
	private RoleRepository roleRepository;
	
	@Inject
	private AcquireUserIDFromEmpIDService acquireUserIDFromEmpIDService;
	
	@Inject
	private RoleSetService roleSetService;
	
	/**
	 * 
	 * @param require
	 * @param workPlaceId
	 * @param date
	 * @return
	 */
	public List<String> get(
			List<String> workPlaceIds,
			GeneralDate date,
			int sysAtr
			) { 
		
		if (sysAtr == 1) return Collections.emptyList();
		
		Require require = new Require(
				specificWorkPlaceAdaper,
				roleRepository,
				acquireUserIDFromEmpIDService,
				roleSetService);
		List<String> sids = AprrovalWorkPlaceDomService.get(
				require,
				workPlaceIds,
				date,
				AppContexts.user().companyId());
		
		return sids;
	}
	
	@AllArgsConstructor
	public class Require implements AprrovalWorkPlaceDomService.Require{

		private SpecificWorkPlaceAdaper specificWorkPlaceAdaper;
		
		private RoleRepository roleRepository;
		
		private AcquireUserIDFromEmpIDService acquireUserIDFromEmpIDService;
		
		private RoleSetService roleSetService;
		
		@Override
		public List<String> getSids(List<String> wplIds, DatePeriod date) {
			return specificWorkPlaceAdaper.get(wplIds, date);
		}

		@Override
		public List<Role> getRoles(String cid) {
			return roleRepository.obtainRoleWorks(cid);
		}

		@Override
		public Optional<String> getUserIDByEmpID(String employeeID) {
			return acquireUserIDFromEmpIDService.getUserIDByEmpID(employeeID);
		}

		@Override
		public Optional<RoleSet> getRoleSetFromUserId(String userId, GeneralDate baseDate) {
			return roleSetService.getRoleSetFromUserId(userId, baseDate);
		}
		
	}
}
