package nts.uk.ctx.sys.auth.app.find.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmpInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoPub;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmployeeInfoAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmployeeInfoImport;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.shared.dom.user.DisabledSegment;
import nts.uk.ctx.sys.shared.dom.user.SearchUser;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class UserFinder {

	@Inject
	private UserRepository userRepo;

	@Inject
	private EmployeeInfoAdapter employeeInfoAdapter;
	
	/** The company adapter. */
	@Inject
	private CompanyAdapter companyAdapter;
	
	@Inject
	private RoleIndividualGrantRepository roleIndividualGrantRepo;
	
	@Inject
	private EmployeeInfoPub employeeInfoPub;

	public List<UserAuthDto> searchUser(String userNameID) {
		GeneralDate date = GeneralDate.today();
		if (userNameID == null) {
			throw new BusinessException("Msg_438");
		}
		
		List<UserAuthDto> listUserDto = new ArrayList<UserAuthDto>();
		List<SearchUser> listSearchUser = userRepo.searchUser(userNameID, date).stream().sorted(Comparator.comparing(SearchUser::getLoginID))
										.collect(Collectors.toList());
		listSearchUser.forEach(item -> {
			
			
			if (item != null) {				
				// アルゴリズム「指定の個人IDから在籍社員を取得」を実行する
				List<EmpInfoExport> empInfoExportLst = employeeInfoPub.getEmpInfoByPid(item.getPersonId().trim());
				if (!CollectionUtil.isEmpty(empInfoExportLst)) {
					empInfoExportLst.forEach(empInfor -> {
						UserAuthDto userDto = new UserAuthDto();
						userDto.setUserID(item.getUserID());
						userDto.setLoginID(item.getLoginID());
						userDto.setUserName(item.getUserName());
						userDto.setEmpCD(empInfor.getEmployeeCode());
						userDto.setCompanyCD(companyAdapter.findCompanyByCid(empInfor.getCompanyId()).getCompanyCode());
						listUserDto.add(userDto);
					});		
				}				
			}			
		});
		
		return listUserDto;
	}

	public List<UserDto> getAllUser() {
		return userRepo.getAllUser().stream().map(c -> UserDto.fromDomain(c)).collect(Collectors.toList());
	}

	public List<UserDto> findByKey(UserKeyDto userKeyDto) {
		String companyId = AppContexts.user().companyId();
		if (companyId == null)
			return null;

		List<UserDto> result = new ArrayList<UserDto>();

		List<String> userIds = roleIndividualGrantRepo.findByCompanyIdAndRoleType(companyId, userKeyDto.getRoleType())
				.stream().map(c -> c.getUserId()).collect(Collectors.toList());

		DisabledSegment specialUser = EnumAdaptor.valueOf(userKeyDto.isSpecial() ? 1 : 0, DisabledSegment.class);
		DisabledSegment multiCompanyConcurrent = EnumAdaptor.valueOf(userKeyDto.isMulti() ? 1 : 0, DisabledSegment.class);
		
		List<User> listUser = new ArrayList<>();
		if (!userKeyDto.isMulti() && !userKeyDto.isSpecial()) {
			//get request list 60
			List<EmployeeInfoImport> listEmployeeInfo = employeeInfoAdapter.getEmployeesAtWorkByBaseDate(companyId,
					GeneralDate.today());
			if(listEmployeeInfo.isEmpty()) {
				return new ArrayList<>();
			}
			List<String> listEmployeePersonId = listEmployeeInfo.stream().map(c -> c.getPersonId()).collect(Collectors.toList());
			//Employee. Employee name like '% screen. Search string input%'
			List<String> listEmployeePersonIdFindName = listEmployeeInfo.stream().filter(c -> c.getEmployeeName().toLowerCase().contains(userKeyDto.getKey().toLowerCase())).map(c -> c.getPersonId()).collect(Collectors.toList()); 
			//query user multi condition
			listUser = userRepo.searchUserMultiCondition(GeneralDate.today(), multiCompanyConcurrent.value, specialUser.value, userKeyDto.getKey(), listEmployeePersonIdFindName, listEmployeePersonId);
		}else {
			listUser = userRepo.searchByKey(GeneralDate.today(), specialUser.value, multiCompanyConcurrent.value, userKeyDto.getKey());
		}
		
		result = listUser.stream().map(c -> UserDto.fromDomain(c)).collect(Collectors.toList());
		for (String id : userIds) {
			result.removeIf(c -> c.getUserID().equals(id));
		}
		return result;
	}

}
