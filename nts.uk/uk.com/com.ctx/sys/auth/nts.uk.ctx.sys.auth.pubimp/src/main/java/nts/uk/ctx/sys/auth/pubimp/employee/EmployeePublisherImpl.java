package nts.uk.ctx.sys.auth.pubimp.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;


import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.employee.JobTitleAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmpInfoImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmployeeInfoAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.person.EmployeeBasicInforAuthImport;
import nts.uk.ctx.sys.auth.dom.adapter.person.PersonAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.AffWorkplaceHistImport;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.AffiliationWorkplace;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.sys.auth.dom.algorithm.AcquireListWorkplaceByEmpIDService;
import nts.uk.ctx.sys.auth.dom.algorithm.AcquireUserIDFromEmpIDService;
import nts.uk.ctx.sys.auth.dom.algorithm.CanApprovalOnBaseDateService;
import nts.uk.ctx.sys.auth.dom.algorithm.DetermineEmpCanReferService;
import nts.uk.ctx.sys.auth.dom.algorithm.EmpReferenceRangeService;
import nts.uk.ctx.sys.auth.dom.employee.dto.EmployeeImport;
import nts.uk.ctx.sys.auth.dom.employee.dto.JobTitleValueImport;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;
import nts.uk.ctx.sys.auth.dom.grant.service.RoleIndividualService;
import nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.wkpmanager.EmpInfoAdapter;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.ctx.sys.auth.dom.wplmanagementauthority.WorkPlaceAuthority;
import nts.uk.ctx.sys.auth.dom.wplmanagementauthority.WorkPlaceAuthorityRepository;
import nts.uk.ctx.sys.auth.pub.employee.EmpWithRangeLogin;
import nts.uk.ctx.sys.auth.pub.employee.EmployeePublisher;
import nts.uk.ctx.sys.auth.pub.employee.NarrowEmpByReferenceRange;
import nts.uk.ctx.sys.auth.pub.employee.WorkPlaceAuthorityDto;
import nts.uk.ctx.sys.auth.pub.employee.WorkplaceManagerDto;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;
import nts.uk.ctx.sys.auth.pub.user.UserExport;
import nts.uk.ctx.sys.auth.pub.user.UserPublisher;
import nts.uk.ctx.sys.auth.pub.workplace.WorkplaceListPub;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class EmployeePublisherImpl implements EmployeePublisher {

	@Inject
	private PersonAdapter personAdapter;

	@Inject
	private UserPublisher userPublisher;

	@Inject
	private EmpReferenceRangeService empReferenceRangeService;

	@Inject
	private WorkplaceManagerRepository workplaceManagerRepository;

	@Inject
	private WorkplaceAdapter workplaceAdapter;

	@Inject
	private EmployeeInfoAdapter employeeInfoAdapter;

	@Inject
	private DetermineEmpCanReferService determineEmpCanRefer;

	@Inject
	private CanApprovalOnBaseDateService canApprovalOnBaseDateService;

	@Inject
	private AcquireListWorkplaceByEmpIDService acquireListWorkplace;

	@Inject
	private AcquireUserIDFromEmpIDService acquireUserIDFromEmpIDService;

	@Inject
	private RoleIndividualService roleIndividualService;

	@Inject
	private RoleExportRepo roleExportRepo;

	@Inject
	private WorkPlaceAuthorityRepository workPlaceAuthorityRepository;

	@Inject
	private RoleSetRepository roleSetRepository;

	@Inject
	private RoleSetGrantedJobTitleRepository roleSetGrantedJobTitleRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private RoleIndividualGrantRepository roleIndividualGrantRepository;

	@Inject
	private RoleSetGrantedPersonRepository roleSetGrantedPersonRepository;

	@Inject
	private JobTitleAdapter jobTitleAdapter;

	@Inject
	private EmpInfoAdapter empInfoAdapter;

	@Inject
	private RoleRepository roleRepo;

	@Inject
	private EmployeeAdapter employeeAdapter;
	
	@Inject
	private WorkplaceListPub workplaceListPub;
	
	@Inject 
	private GetEmfromWkpidAndBDate getEmfromWkpidAndBDate;
	
	@Inject
	private ObtainWkpListAndWkpManager obtainWkpListAndWkpManager;
	
	@Inject
	private WorkPlaceAuthorityRepository workPlaceAuthRepo;
	
	@Inject
	private WorkplaceManagerRepository workplaceManagerRepo;
	
	@Override
	public Optional<NarrowEmpByReferenceRange> findByEmpId(List<String> sID, int roleType, GeneralDate referenceDate) {
		// imported（権限管理）「社員」を取得する Request No1
		// employeeID = employeeID login
		String employeeIDLogin = AppContexts.user().employeeId();
		List<String> result = new ArrayList<>();
		Optional<EmployeeBasicInforAuthImport> employeeImport = personAdapter.getPersonInfor(employeeIDLogin);
		// List<String> listEmployeeID = listEmployeeImport.stream().map(c ->
		// c.getEmployeeId()).collect(Collectors.toList());
		if (!employeeImport.isPresent()) {
			return Optional.empty();
		} else {
			// アルゴリズム「紐付け先個人IDからユーザを取得する」を実行する
			// Execute algorithm "Acquire user from tied personal ID"
			Optional<UserExport> useExport = userPublisher.getUserByAssociateId(employeeImport.get().getPid());
			if (!useExport.isPresent()) {
				return Optional.empty();
			} else {
				Optional<Role> role = empReferenceRangeService.getByUserIDAndReferenceDate(useExport.get().getUserID(),
						roleType, referenceDate);
				if (!role.isPresent()) {
					if (sID.contains(employeeIDLogin)) {
						result.add(employeeIDLogin);
					}
					return Optional.of(new NarrowEmpByReferenceRange(result));
				}

				EmployeeReferenceRange referenceRange = role.get().getEmployeeReferenceRange();
				if (referenceRange == EmployeeReferenceRange.ALL_EMPLOYEE) {
					return Optional.of(new NarrowEmpByReferenceRange(sID));
				} else if (referenceRange == EmployeeReferenceRange.ONLY_MYSELF && roleType == RoleType.EMPLOYMENT.value) {
					
					//指定社員の職場管理者の職場リストを取得する（配下含む）
					//[RQ613]指定社員の職場管理者の職場リストを取得する（配下含む）
					List<String> subListWorkPlace = workplaceListPub.getWorkplaceId(GeneralDate.today(), employeeIDLogin);
					
					// 社員ID（List）と基準日から所属職場IDを取得 Lay request 227
					List<AffiliationWorkplace> lisAfiliationWorkplace = workplaceAdapter.findByListEmpIDAndDate(sID, GeneralDate.today());
					
					// 取得した所属職場履歴項目（List）を参照可能職場ID（List）で絞り込む
					result = lisAfiliationWorkplace.stream().filter(c -> {
						return subListWorkPlace.contains(c.getWorkplaceId()) || employeeIDLogin.equals(c.getEmployeeId());
					}).map(x -> x.getEmployeeId()).collect(Collectors.toList());
					
					return Optional.of(new NarrowEmpByReferenceRange(result));
				} else if (referenceRange == EmployeeReferenceRange.ONLY_MYSELF && roleType != RoleType.EMPLOYMENT.value) {
					if (sID.contains(employeeIDLogin)) {
						result.add(employeeIDLogin);
					}
					return Optional.of(new NarrowEmpByReferenceRange(result));
				}else {
					List<String> subListWorkPlace = new ArrayList<>();
					if (roleType == RoleType.EMPLOYMENT.value) {
						// 指定社員の職場管理者の職場リストを取得する（配下含む）
						// [RQ613]指定社員の職場管理者の職場リストを取得する（配下含む）
						subListWorkPlace.addAll(workplaceListPub.getWorkplaceId(GeneralDate.today(), employeeIDLogin));
					}
					// imported（権限管理）「所属職場履歴」を取得する
					// (Lấy imported（権限管理）「所属職場履歴」) Lay RequestList No.30
					Optional<AffWorkplaceHistImport> workPlace = workplaceAdapter
							.findWkpByBaseDateAndEmployeeId(referenceDate, employeeIDLogin);
					String workPlaceID1 = workPlace.get().getWorkplaceId();
					List<String> listWorkPlaceID3 = new ArrayList<>();
					if (referenceRange == EmployeeReferenceRange.DEPARTMENT_AND_CHILD) {
						// 配下の職場をすべて取得する
						// Lay RequestList No.154
						listWorkPlaceID3 = workplaceAdapter.findListWorkplaceIdByCidAndWkpIdAndBaseDate(
								AppContexts.user().companyId(), workPlaceID1, referenceDate);
					}
					// 社員ID（List）と基準日から所属職場IDを取得 Lay request 227
					List<AffiliationWorkplace> lisAfiliationWorkplace = workplaceAdapter.findByListEmpIDAndDate(sID,
							referenceDate);
					// 取得した所属職場履歴項目（List）を参照可能職場ID（List）で絞り込む
					List<String> listtWorkID = new ArrayList<>();
					listtWorkID.add(workPlaceID1);
					listtWorkID.addAll(subListWorkPlace);
					listtWorkID.addAll(listWorkPlaceID3);
					// 取得した所属職場履歴項目（List）を参照可能職場ID（List）で絞り込む
					result = lisAfiliationWorkplace.stream().filter(c -> listtWorkID.contains(c.getWorkplaceId()))
							.map(x -> x.getEmployeeId()).collect(Collectors.toList());

				}
			}

		}
		return Optional.of(new NarrowEmpByReferenceRange(result));
	}

	@Override
	public Optional<EmpWithRangeLogin> findByCompanyIDAndEmpCD(String companyID, String employeeCD) {
		// imported（権限管理）「社員」を取得する
		Optional<EmpInfoImport> empInfor = employeeInfoAdapter.getByComnyIDAndEmployeeCD(companyID, employeeCD);
		if (empInfor.isPresent()) {
			// 参照可能な社員かを判定する（職場）
			boolean result = determineEmpCanRefer.checkDetermineEmpCanRefer(GeneralDate.today(),
					empInfor.get().getEmployeeId(), RoleType.EMPLOYMENT.value);
			if (result == true) {
				return Optional.of((new EmpWithRangeLogin(empInfor.get().getPerName(), empInfor.get().getCompanyId(),
						empInfor.get().getPersonId(), empInfor.get().getEmployeeCode(),
						empInfor.get().getEmployeeId())));
			} else
				return Optional.empty();
		}
		return Optional.empty();
	}

	@Override
	public Optional<EmpWithRangeLogin> getByComIDAndEmpCD(String companyID, String employeeCD,
			GeneralDate referenceDate) {
		// imported（権限管理）「社員」を取得する Lấy request List No.18
		Optional<EmpInfoImport> empInfor = employeeInfoAdapter.getByComnyIDAndEmployeeCD(companyID, employeeCD);
		if (empInfor.isPresent()) {
			// 指定社員が基準日に承認権限を持っているかチェックする Lay request 305 tu domain service
			boolean result = canApprovalOnBaseDateService.canApprovalOnBaseDate(empInfor.get().getCompanyId(),
					empInfor.get().getEmployeeId(), referenceDate);
			if (result == true) {
				return Optional.of((new EmpWithRangeLogin(empInfor.get().getPerName(), empInfor.get().getCompanyId(),
						empInfor.get().getPersonId(), empInfor.get().getEmployeeCode(),
						empInfor.get().getEmployeeId())));
			} else
				return Optional.empty();
		}
		return Optional.empty();
	}

	@Override
	public List<String> getListWorkPlaceID(String employeeID, GeneralDate referenceDate) {
		// 社員IDからユーザIDを取得する
		// (Lấy userID từ employeeID)
		Optional<String> userID = acquireUserIDFromEmpIDService.getUserIDByEmpID(employeeID);
		if (!userID.isPresent()) {
			return new ArrayList<>();
		} else {
			// ユーザIDからロールを取得する
			// (lấy role từ userID)
			String roleID = roleIndividualService.getRoleFromUserId(userID.get(), RoleType.EMPLOYMENT.value,
					referenceDate);
			// 社員参照範囲を取得する
			// (Lấy phạm vi tham chiếu employee)
			OptionalInt optEmpRange = roleExportRepo.findEmpRangeByRoleID(roleID);
			// 指定社員が参照可能な職場リストを取得する
			// (Lấy list workplace của employee chỉ định)
			List<String> listWorkPlaceID = acquireListWorkplace.getListWorkPlaceID(employeeID, optEmpRange.getAsInt(),
					referenceDate);
			if (listWorkPlaceID.isEmpty()) {
				return new ArrayList<>();
			} else {
				return listWorkPlaceID;
			}
		}
	}

	@Override
	public List<String> getListEmployeeId(String wkpID, GeneralDate date) {
		String companyID = AppContexts.user().companyId();
		List<String> resultLst = new ArrayList<>();

		// ドメインモデル「所属職場権限」を取得する
		List<WorkPlaceAuthority> workPlaceAuthorityLst = workPlaceAuthorityRepository
				.getByFunctionAndAvailable(companyID, 3, true);

		List<String> roleIDLst = workPlaceAuthorityLst.stream().map(x -> x.getRoleId()).collect(Collectors.toList());

		// 職場、ロールIDから社員リストを取得する
		List<String> empLst2 = this.getEmpByWkpAndRole(wkpID, roleIDLst, date);

		// ドメインモデル「職場管理者」を取得する
		List<String> empLst3 = workplaceManagerRepository.findByWkpDateAndManager(wkpID, date, roleIDLst).stream()
				.map(x -> x.getEmployeeId()).collect(Collectors.toList());

		// (2)と(3)の社員リストを、重複を除いてマージする
		resultLst.addAll(empLst2);
		resultLst.addAll(empLst3);

		return resultLst.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 職場、ロールIDから社員リストを取得する
	 * 
	 * @param wkpID
	 * @param roleIDs
	 * @param date
	 * @return
	 */
	private List<String> getEmpByWkpAndRole(String wkpID, List<String> roleIDs, GeneralDate date) {
		String companyID = AppContexts.user().companyId();

		// output社員リストを初期化する
		List<String> result = new ArrayList<>();

		// ドメインモデル「ロールセット」を取得する
		List<RoleSet> roleSetLst = roleSetRepository.findByCIDAndEmpRoleLst(companyID, roleIDs);

		// ドメインモデル「ロールセット職位別付与」を取得する
		List<String> jobTitleLst = roleSetGrantedJobTitleRepository.findJobTitleByRoleCDLst(companyID,
				roleSetLst.stream().map(x -> x.getRoleSetCd().v()).collect(Collectors.toList()));

		// imported（権限管理）「所属職場履歴」を取得する (request list 120)
		List<String> employeeLst = workplaceAdapter.findListSIdByCidAndWkpIdAndPeriod(wkpID, date, date).stream()
				.map(x -> x.getEmployeeId()).collect(Collectors.toList());

		// 社員ID(List)から個人社員基本情報を取得 (request list 61)
		List<EmpInfoImport> empInforLst = empInfoAdapter.getEmpInfo(employeeLst);

		// 取得した社員リストでループする
		empInforLst.forEach(employee -> {
			String employeeID = employee.getEmployeeId();
			// ドメインモデル「ユーザ」を取得する
			Optional<User> opUser = userRepository.getByAssociatedPersonId(employee.getPersonId());
			if (opUser.isPresent()) {
				User user = opUser.get();
				// ドメインモデル「ロール個人別付与」を取得する
				Optional<RoleIndividualGrant> opRoleIndividualGrant = roleIndividualGrantRepository
						.findByDetail(user.getUserID(), companyID, RoleType.EMPLOYMENT.value, roleIDs, date);
				if (opRoleIndividualGrant.isPresent()) {
					result.add(employeeID);
					return;
				}
			}
			// roleSetLst
			// ドメインモデル「ロールセット個人別付与」を取得する
			List<String> listRoleCD = roleSetLst.stream().map(c -> c.getRoleSetCd().v()).collect(Collectors.toList());
				Optional<RoleSetGrantedPerson> opRoleSetGrantedPerson = roleSetGrantedPersonRepository.findByDetail(companyID, employeeID, listRoleCD, date);
						//.findByDetail(companyID, employeeID, roleCD, date);
				if (opRoleSetGrantedPerson.isPresent()) {
					result.add(employeeID);
					return;
				}
				// 社員の職位を取得する
				JobTitleValueImport jobTitleValueImport = jobTitleAdapter.findJobTitleBySid(employeeID, date);
				// 取得した職位をチェックする
				if ((jobTitleValueImport != null) && jobTitleLst.contains(jobTitleValueImport.getPositionId())) {
					result.add(employeeID);
					return;
				}
			
		});
		return result;
	}

	private List<String> getRoleIDByCID(String companyID) {
		List<Role> listRole = roleRepo.findByTypeAndRoleAtr(companyID, RoleType.EMPLOYMENT.value,
				RoleAtr.INCHARGE.value);
		List<String> listRoleID = listRole.stream().map(c -> c.getRoleId()).collect(Collectors.toList());
		if (listRoleID.isEmpty()) {
			return new ArrayList<>();
		}
		return listRoleID;
	}

	@Override
	public List<String> getListEmpID(String companyID, GeneralDate referenceDate) {
		// OUTPUT 社員ID（List）を初期化する
		List<String> listEmpID = new ArrayList<>();
		// 就業担当者ロールID(List)を取得する
		List<String> listRoleID = getRoleIDByCID(companyID);
		// ドメインモデル「ロール個人別付与」を取得する
		List<RoleIndividualGrant> listRoleIndi = roleIndividualGrantRepository.findRoleIndividual(companyID,
				RoleType.EMPLOYMENT.value, listRoleID, referenceDate);
		List<String> listUserID = listRoleIndi.stream().map(c -> c.getUserId()).collect(Collectors.toList());
		// ②ユーザID（List） をLoopする
		for (String userID : listUserID) {
			Optional<User> user = userRepository.getByUserID(userID);
			if(user.isPresent()) {
				if (user.get().getAssociatedPersonID().isPresent()) {
					String personalID = user.get().getAssociatedPersonID().get();
					// Lay thong tin Request 101
					Optional<EmployeeImport> empImport = employeeAdapter.getEmpInfo(companyID, personalID);
					if (empImport.isPresent()) {
						// OUTPUT 社員ID（List）に③社員.社員IDを追加する
						listEmpID.add(empImport.get().getEmployeeId());
					}
	
				}
			}
		}

		return listEmpID;
	}

	@Override
	public Map<String, List<String>> getListEmpInfo(String companyID, GeneralDate referenceDate, List<String> workplaceIds) {
		
		RequireRQ653Impl requireRQ653Impl = new RequireRQ653Impl(workPlaceAuthRepo, workplaceManagerRepo);
		
		// 職場リスト、基準日から就業確定できるロールを持っている社員を取得する
		Map<String, List<String>> mapWkpIdAndSid1 = getEmfromWkpidAndBDate.getData(requireRQ653Impl, companyID, referenceDate, workplaceIds);
		
		// 職場リスト、基準日から就業確定できる職場管理者を取得する
		Map<String, List<String>> mapWkpIdAndSid2 = obtainWkpListAndWkpManager.getData(requireRQ653Impl, companyID, referenceDate, workplaceIds);
		
		
		// 取得した2つの「Map<職場ID、社員ID>」から重複するものを排除する
		/*for (Map.Entry map2 : mapWkpIdAndSid2.entrySet()) {
			List<String> value = mapWkpIdAndSid1.get(map2.getKey());
			if(!value.isEmpty()){
				mapWkpIdAndSid1.put(map2.getKey().toString(), value);
				
			}
		}*/
		List<EmployeeMap> employeeMaps = new ArrayList<>();
		mapWkpIdAndSid1.forEach((k,v) ->{
			employeeMaps.addAll(v.stream().map(i -> new EmployeeMap(k,i)).collect(Collectors.toList()));
		});
		mapWkpIdAndSid2.forEach((k,v) ->{
			employeeMaps.addAll(v.stream().map(i -> new EmployeeMap(k,i)).collect(Collectors.toList()));
		});
		Map<String , List<String>> result = new HashMap<>();
		employeeMaps.stream().collect(Collectors.groupingBy(EmployeeMap::getWplID)).forEach((k,v) -> {
			result.put(k, v.stream().map(i->i.getEmpID()).distinct().collect(Collectors.toList()));
		});
		// 重複するものを排除した「Map<職場ID、社員ID>」を返す
		return result;
	}

	@AllArgsConstructor
	private static class RequireRQ653Impl implements EmployeePublisher.RequireRQ653 {
		
		private WorkPlaceAuthorityRepository workPlaceAuthRepo;
		
		private WorkplaceManagerRepository workplaceManagerRepo;
		
		@Override
		public List<WorkplaceManagerDto> getWorkplaceManager(List<String> workPlaceIds, GeneralDate baseDate) {
			List<WorkplaceManager> workplaceManagerLst = workplaceManagerRepo.findListWkpManagerByWkpIdsAndBaseDate(workPlaceIds, baseDate);
			if(workplaceManagerLst.isEmpty())
				return new ArrayList<>();
			List<WorkplaceManagerDto> rs = workplaceManagerLst.stream().map(m -> {
				return  new WorkplaceManagerDto(m.getWorkplaceManagerId(), m.getEmployeeId(), m.getWorkplaceId(), m.getHistoryPeriod());
			}).collect(Collectors.toList());
			return rs;
		}

		@Override
		public Optional<WorkPlaceAuthorityDto> getWorkAuthority(String companyId, String roleId, Integer functionNo) {
			Optional<WorkPlaceAuthority> result = workPlaceAuthRepo.getWorkPlaceAuthorityById(companyId, roleId, functionNo);
			if(result.isPresent()) {
				return Optional.of(new WorkPlaceAuthorityDto(result.get().getRoleId(), result.get().getCompanyId(), result.get().getFunctionNo().v(), result.get().isAvailability()));
			}
			return Optional.empty();
			
		}
		
		
	}

}
