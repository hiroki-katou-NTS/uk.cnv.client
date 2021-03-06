/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.app.find.securitypolicy.lockoutdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmpInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoPub;
import nts.uk.ctx.sys.gateway.app.find.securitypolicy.lockoutdata.dto.LockOutDataDto;
import nts.uk.ctx.sys.gateway.app.find.securitypolicy.lockoutdata.dto.LockOutDataUserDto;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserDto;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.acountlock.locked.LockOutDataRepository;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.acountlock.locked.LockoutData;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.acountlock.locked.SearchUser;
import nts.uk.ctx.sys.shared.dom.company.CompanyInformationAdapter;
import nts.uk.shr.com.context.AppContexts;
/**
 * The Class LockOutDataUserFinder.
 */
@Stateless
public class LockOutDataUserFinder {
	
	/** The lock out data repository. */
	@Inject
	private LockOutDataRepository lockOutDataRepository;
	
	/** The user adapter. */
	@Inject
	private UserAdapter userAdapter;
	
	/** The company adapter. */
	@Inject
	private CompanyInformationAdapter companyAdapter;
	
	
	
	@Inject
	private EmployeeInfoPub employeeInfoPub;
	
	
	/**
	 * Find all.
	 *
	 * @return the list
	 */
	public List<LockOutDataUserDto> findAll() {

		List<LockOutDataUserDto> lstLockOutDataUserDto = new ArrayList<LockOutDataUserDto>();
		String contractCd = AppContexts.user().contractCode();
		//get list LockOutData
		List<LockoutData> lstLockOutData = lockOutDataRepository.findByContractCode(contractCd);
		
		lstLockOutData.forEach(item -> {			
			if (item != null) {
				UserDto userDto = userAdapter.getUser(Arrays.asList(item.getUserId())).get(0);
				if (userDto != null) {					
					// ???????????????????????????????????????????????????????????????????????????
					List<EmpInfoExport> empInfoExportLst = employeeInfoPub
							.getEmpInfoByPid(userDto.getAssociatedPersonID().trim());
					if (!CollectionUtil.isEmpty(empInfoExportLst)) {
						
						empInfoExportLst.forEach(empInfor -> {
							LockOutDataUserDto lockOutDataUserDto = new LockOutDataUserDto();
							lockOutDataUserDto.setLockOutDateTime(item.getLockOutDateTime());
							lockOutDataUserDto.setLogType((item.getLogType().value));
							lockOutDataUserDto.setUserId(item.getUserId());
							lockOutDataUserDto.setLoginId(userDto.getLoginId().trim());
							lockOutDataUserDto.setUserName(userDto.getUserName());
							lockOutDataUserDto.setCompanyCode(companyAdapter.findById(empInfor.getCompanyId()).getCompanyCode());
							lockOutDataUserDto.setEmployeeCode(empInfor.getEmployeeCode());
							lstLockOutDataUserDto.add(lockOutDataUserDto);
						});		
					}
				}				
			}
			
		});
		return lstLockOutDataUserDto;
	}

	/**
	 * Find and return users by userId
	 * @author Nguyen Van Hanh
	 * @param userId
	 * @return SearchUser
	 */
	public SearchUser findByUserId(String userId) {
		UserDto userDto = userAdapter.getUser(Arrays.asList(userId)).get(0);
		if (userDto != null) {
			return new SearchUser(userDto.getUserId(), userDto.getLoginId(), userDto.getUserName());
		}
		return null;
	}
	
	/**
	 * Find lock out data by user id.
	 *
	 * @param UserId the user id
	 * @return the lock out data dto
	 */
	public LockOutDataUserDto findLockOutDataByUserId(String UserId) {
		Optional<LockoutData> optLockOutData = lockOutDataRepository.find(UserId);
		if (!optLockOutData.isPresent()) {
			return null;
		}
		
		LockOutDataUserDto lockOutDataUserDto = new LockOutDataUserDto();
		
		LockoutData lockOutData = optLockOutData.get();
		lockOutDataUserDto.setLockOutDateTime(lockOutData.getLockOutDateTime());
		lockOutDataUserDto.setLogType((lockOutData.getLogType().value));
		lockOutDataUserDto.setUserId(lockOutData.getUserId());
		
		UserDto userDto = userAdapter.getUser(Arrays.asList(UserId)).get(0);
		
		if (userDto != null) {			
			lockOutDataUserDto.setLoginId(userDto.getLoginId().trim());
			lockOutDataUserDto.setUserName(userDto.getUserName());
			
			// ???????????????????????????????????????????????????????????????????????????
			List<EmpInfoExport> empInfoExportLst = employeeInfoPub
					.getEmpInfoByPid(userDto.getAssociatedPersonID().trim());
			if (!CollectionUtil.isEmpty(empInfoExportLst)) {
				lockOutDataUserDto.setCompanyCode(
						companyAdapter.findById(empInfoExportLst.get(0).getCompanyId()).getCompanyCode());
				lockOutDataUserDto.setEmployeeCode(empInfoExportLst.get(0).getEmployeeCode());

			}
		}
		return lockOutDataUserDto;
	}

}
