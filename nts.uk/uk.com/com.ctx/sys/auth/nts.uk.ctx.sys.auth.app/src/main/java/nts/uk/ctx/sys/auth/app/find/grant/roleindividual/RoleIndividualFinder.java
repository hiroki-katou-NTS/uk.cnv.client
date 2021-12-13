package nts.uk.ctx.sys.auth.app.find.grant.roleindividual;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.pub.employee.ResultRequest600Export;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoDto;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoDtoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoPub;
import nts.uk.ctx.bs.employee.pub.jobtitle.AffJobTitleBasicExport;
import nts.uk.ctx.bs.employee.pub.jobtitle.EmployeeJobHistExport;
import nts.uk.ctx.bs.employee.pub.jobtitle.SyJobTitlePub;
import nts.uk.ctx.bs.employee.pub.person.IPersonInfoPub;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.ctx.sys.auth.app.find.company.CompanyDto;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.*;
import nts.uk.ctx.sys.auth.app.query.GetEmployeeIDFromUserIDQuery;
import nts.uk.ctx.sys.auth.dom.GetPersonalEmployeeInfoByUserIdService;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeInfoImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.PersonalEmployeeInfoImport;
import nts.uk.ctx.sys.auth.dom.adapter.person.EmployeeBasicInforAuthImport;
import nts.uk.ctx.sys.auth.dom.adapter.person.PersonAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.person.PersonImport;
import nts.uk.ctx.sys.auth.dom.adapter.role.employment.*;
import nts.uk.ctx.sys.auth.dom.algorithm.AcquireUserIDFromEmpIDService;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserName;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

@Stateless
public class RoleIndividualFinder {

    @Inject
    private RoleIndividualGrantRepository roleIndividualGrantRepo;

    @Inject
    private UserRepository userRepo;

    @Inject
    private PersonAdapter personAdapter;

    @Inject
    private CompanyAdapter companyAdapter;

    @Inject
    private RoleAdapter roleAdapter;

    @Inject
    private WorkplacePub workplacePub;

    @Inject
    private SyJobTitlePub syJobTitlePub;

    @Inject
    private EmployeeInfoPub employeeInfoPub;

    @Inject
    private AcquireUserIDFromEmpIDService userIDFromEmpIDService;

    @Inject
    private SyEmployeePub syEmployeePub;

    @Inject
    private GetPersonalEmployeeInfoByUserIdService infoByUserIdService;

    @Inject
    private GetEmployeeIDFromUserIDQuery userIDQuery;



    private static final String COMPANY_ID_SYSADMIN = "000000000000-0000";

    public RoleIndividualDto findByCompanyAndRoleType(String companyID, int roleType) {
        String userName = "";
        // Get list RoleIndividualGrant
        if (roleType != RoleType.COMPANY_MANAGER.value)
            companyID = COMPANY_ID_SYSADMIN;

        List<RoleIndividualGrant> listRoleIndividualGrant = roleIndividualGrantRepo.findByCompanyIdAndRoleType(companyID, roleType);
        if (listRoleIndividualGrant.isEmpty()) {
            return new RoleIndividualDto(COMPANY_ID_SYSADMIN, new ArrayList<RoleIndividualGrantDto>());
        }

        // Get list User information
        List<String> listUserID = listRoleIndividualGrant.stream().map(c -> c.getUserId()).distinct().collect(Collectors.toList());

        List<User> listUser = userRepo.getByListUser(listUserID);

        List<String> listAssPersonID = listUser.stream().map(c -> c.getAssociatedPersonID().isPresent() ? c.getAssociatedPersonID().get() : "").distinct().collect(Collectors.toList());
        List<PersonImport> listPerson = personAdapter.findByPersonIds(listAssPersonID);

        // Build RoleIndividualGrantDto
        List<RoleIndividualGrantDto> listRoleIndividualGrantDto = new ArrayList<>();
        for (RoleIndividualGrant roleIndividualGrant : listRoleIndividualGrant) {
            // Filter get Users
            Optional<User> user = listUser.stream().filter(c -> c.getUserID().equals(roleIndividualGrant.getUserId())).findFirst();

            if (user.isPresent()) {
                if (user.get().getUserName().isPresent())
                    userName = user.get().getUserName().get().v();

                String loginID = user.get().getLoginID().v();
                // Filter get Person
                if (user.get().getAssociatedPersonID().isPresent()) {
                    Optional<PersonImport> optPerson = listPerson.stream().filter(c -> c.getPersonId().equals(user.get().getAssociatedPersonID().get())).findFirst();
                    if (optPerson.isPresent())
                        userName = optPerson.get().getPersonName();
                    else {
                        userName = "";
                    }
                }
                // Add to list
                RoleIndividualGrantDto dto = new RoleIndividualGrantDto(
                        roleIndividualGrant.getCompanyId(),
                        roleIndividualGrant.getRoleId(),
                        roleIndividualGrant.getRoleType().value,
                        loginID,
                        roleIndividualGrant.getUserId(),
                        userName,
                        roleIndividualGrant.getValidPeriod().start(),
                        roleIndividualGrant.getValidPeriod().end(),"","","");
                listRoleIndividualGrantDto.add(dto);
            }
        }
        listRoleIndividualGrantDto.sort((obj1, obj2) -> {
            return obj1.getLoginID().compareTo(obj2.getLoginID());
        });
        return new RoleIndividualDto(COMPANY_ID_SYSADMIN, listRoleIndividualGrantDto);

    }

    public RoleIndividualGrantMetaDto getCAS012Metadata() {
        LoginUserContext user = AppContexts.user();
        if (!user.roles().have().systemAdmin())
            return null;

        // Get List Enum RoleType
        // #117468 - no 6 remove 「グループ会社管理者」
        List<EnumConstant> enumRoleType = EnumAdaptor.convertToValueNameList(RoleType.class, RoleType.SYSTEM_MANAGER, RoleType.COMPANY_MANAGER);

        // Get list Company Information
        List<CompanyImport> listCompanyImport = companyAdapter.findAllCompany();

        return new RoleIndividualGrantMetaDto(enumRoleType, listCompanyImport);
    }

    public List<RoleTypeDto> getCAS013Metadata() {
        val user = AppContexts.user();
        if (!user.roles().have().companyAdmin()){
            throw  new BusinessException("Msg_1103");
        }
        // Get List Enum RoleType
        List<EnumConstant> enumRoleType = EnumAdaptor.convertToValueNameList(RoleType.class,
                RoleType.EMPLOYMENT, RoleType.SALARY, RoleType.HUMAN_RESOURCE,
                RoleType.OFFICE_HELPER, RoleType.MY_NUMBER, RoleType.PERSONAL_INFO);

        List<RoleTypeDto> roleTypeDtos = new ArrayList<>();
        for (EnumConstant r : enumRoleType) {
            roleTypeDtos.add(new RoleTypeDto(r.getValue(), r.getFieldName(), r.getLocalizedName()));
        }
        return roleTypeDtos;
    }

    public List<Cas013aDto> getRoleGrants(String roleId) {

        List<Cas013aDto> rGrants = new ArrayList<>();
        if (roleId == null)
            return rGrants;
        List<RoleIndividualGrant> listRoleGrants = this.roleIndividualGrantRepo.findByRoleId(roleId);
        if (listRoleGrants.size() == 0) {
            return rGrants;
        }
        for (val grant: listRoleGrants) {
            val uid = grant.getUserId();
            Optional<PersonalEmployeeInfoImport> optEmployeeInfoImport =
                    userIDQuery.getEmployeeIDFromUserID(uid);
            if(optEmployeeInfoImport.isPresent()){
                PersonalEmployeeInfoImport employeeInfoImport = optEmployeeInfoImport.get();
                List<EmployeeInfoImport> employeeInfos = employeeInfoImport.getEmployeeInfos();
                employeeInfos.forEach(e->{
                    CompanyImport companyInfo = companyAdapter.findCompanyByCid(e.getCompanyId());
                    rGrants.add( Cas013aDto.fromDomain(
                            grant,
                            e.getCompanyId(),
                            companyInfo.getCompanyCode(),
                            companyInfo.getCompanyName(),
                            e.getEmployeeId(),
                            e.getEmployeeCode(),
                            employeeInfoImport.getBussinessName()));
                });
            }
        }
        return rGrants.stream().sorted(Comparator.comparing(Cas013aDto::getEmployeeCode))
                .collect(Collectors.toList());
    }
    public RoleIndividualGrantDto getRoleGrant(String userId, String roleId,String companyId) {
        if (userId == null || roleId == null)
            return null;
        Optional<RoleIndividualGrant> rGrant = this.roleIndividualGrantRepo.findByKey(userId, companyId, roleId);
        if (!rGrant.isPresent()) {
            return null;
        }
        Optional<User> user = userRepo.getByUserID(rGrant.get().getUserId());
        val pid = user.get().getAssociatedPersonID();
        Optional<EmployeeInfoDto> employeeDataMngInfoOptional =  employeeInfoPub.getEmployeeInfoByCidPid(companyId, pid.get());

        List<RoleIndividualGrant> ListRoleGrants = new ArrayList<>();
        ListRoleGrants = this.roleIndividualGrantRepo.findByCompanyRole(companyId, roleId);

        List<String> uid = ListRoleGrants.stream().map(RoleIndividualGrant::getUserId).distinct().collect(Collectors.toList());
        List<RoleExport> roleExportList = roleAdapter.getListRole(uid);
        val listPersonOptional = roleExportList.stream().filter( c -> c.getPersonId().equals(pid.get())).findFirst();
        String userName = "";
        String employeeName = "";
        if (user.get().getUserName().isPresent())
            userName = user.get().getUserName().get().v();
        if(listPersonOptional.isPresent())
        employeeName = listPersonOptional.get().getPersonName();
        return RoleIndividualGrantDto.fromDomain(
                rGrant.get(),
                userName,
                user.get().getLoginID().v(),
                employeeDataMngInfoOptional.isPresent()? employeeDataMngInfoOptional.get().getEmployeeId() : null,
                employeeDataMngInfoOptional.isPresent()?employeeDataMngInfoOptional.get().getEmployeeCode() : null,
                employeeName);

    }
    public CompanyInfo getCompanyInfo(String cid) {
        CompanyImport companyInfo = companyAdapter.findCompanyByCid(cid);
        return new CompanyInfo(companyInfo.getCompanyCode(),
                companyInfo.getCompanyName(),
                companyInfo.getCompanyId(),
                companyInfo.getIsAbolition());
    }
    public WorkPlaceInfo GetWorkPlaceInfo(String employeeID,String cid) {

        GeneralDate baseDate = GeneralDate.today();
        Optional<SWkpHistExport> sWkpHistExport = workplacePub.findBySidNew(cid, employeeID, baseDate);
        if(sWkpHistExport.isPresent()){
            SWkpHistExport export = sWkpHistExport.get();
            WorkPlaceInfo workPlaceInfo = new WorkPlaceInfo(export.getWorkplaceCode(), export.getWorkplaceName());
            return workPlaceInfo;
        }
        return null;
    }

    public JobTitle GetJobTitle(String employeeID) {
        GeneralDate baseDate = GeneralDate.today();

        Optional<AffJobTitleBasicExport> affJobTitleBasicExport =  syJobTitlePub.getBySidAndBaseDate(employeeID, baseDate);
        if(affJobTitleBasicExport.isPresent()){
            AffJobTitleBasicExport affJobTitleBasicExport1 = affJobTitleBasicExport.get();
            JobTitle jobTitle = new JobTitle(affJobTitleBasicExport1.getJobTitleCode(), affJobTitleBasicExport1.getJobTitleName());
            return jobTitle;
        }
        return null;
    }

    public CompanyImport searchCompanyInfo(String cid) {
        if(cid == null) {
            return null;
        }
        CompanyImport companyInfo = companyAdapter.findCompanyByCid(cid);
        CompanyImport companyInfoDto = new CompanyImport(companyInfo.getCompanyCode(), companyInfo.getCompanyName(), companyInfo.getCompanyId(),companyInfo.getIsAbolition());
        return companyInfoDto;
    }

    public List<CompanyDto> getCompanyList() {
        List<CompanyImport> listCompany = new ArrayList<>();
        List<CompanyDto> listCompanyDTO = new ArrayList<CompanyDto>();
        LoginUserContext user = AppContexts.user();
        if (user.roles().forSystemAdmin() != null) {
            listCompany.addAll(companyAdapter.findAllCompanyImport());
        } else {
            // get company by cid
            listCompany.add(companyAdapter.findCompanyByCid(AppContexts.user().companyId()));
        }
        listCompany.stream().map(c -> {
            return listCompanyDTO.add(new CompanyDto(c.getCompanyCode(), c.getCompanyName(), c.getCompanyId()));
        }).collect(Collectors.toList());

        return listCompanyDTO;
    }
    public List<EmployeeBasicInfoDto> getListEmployeeInfo(String cid) {
        val rs = new ArrayList<EmployeeBasicInfoDto>();
        val basDate = GeneralDate.today();
        val period = new DatePeriod(basDate,basDate);

        // 社員情報リストを取得する
        Map<String, EmployeeInfoDtoExport> employeeInfoDtoExportList = employeeInfoPub.getEmployeesAtWorkByBaseDate(cid, basDate)
                .stream().collect(Collectors.toMap(EmployeeInfoDtoExport::getEmployeeId, i -> i));

        val siDs = new ArrayList<>(employeeInfoDtoExportList.keySet());

        val mEmpInfo = syEmployeePub.getEmpInfoLstBySids(siDs,period,true,true).stream()
                .collect(Collectors.toMap(ResultRequest600Export::getSid, i->i));

        List<String> listSid = new ArrayList<>(mEmpInfo.keySet());
        val mapSid =  personAdapter.listPersonInfor(listSid)
                .stream().collect(Collectors.toMap(EmployeeBasicInforAuthImport::getEmployeeId, i -> i));
        List<String> listPid = mapSid.values().stream().map(EmployeeBasicInforAuthImport::getPid).collect(Collectors.toList());

        
        Map<String, EmployeeJobHistExport> jobHistExportMap = syJobTitlePub.findSJobHistByListSIdV2(listSid, basDate).stream()
               .collect(Collectors.toMap(EmployeeJobHistExport::getEmployeeId, i -> i));
        val mapUidPerId = userIDFromEmpIDService.getUserIDByEmpID(listPid);

        for (val sid : listSid) {
            val pid = mapSid.get(sid)!=null?mapSid.get(sid).getPid():null;
            val wpInf = workplacePub.findBySidNew(cid,sid, basDate);
            rs.add(new EmployeeBasicInfoDto(
                    pid,
                    mapUidPerId.get(pid),
                    sid,
                    mEmpInfo.get(sid).getEmployeeCode(),
                    mEmpInfo.get(sid).getEmployeeName(),
                    jobHistExportMap.get(sid) != null ? jobHistExportMap.get(sid).getJobTitleID() : null,
                    jobHistExportMap.get(sid) != null ? jobHistExportMap.get(sid).getJobTitleCode() : null,
                    jobHistExportMap.get(sid) != null ? jobHistExportMap.get(sid).getJobTitleName() : null,
                    wpInf.isPresent()? wpInf.get().getWorkplaceId() : null,
                    wpInf.isPresent()? wpInf.get().getWorkplaceCode() : null,
                    wpInf.isPresent()? wpInf.get().getWorkplaceName() : null,
                    wpInf.isPresent()? wpInf.get().getWkpDisplayName() : null
            ));
        }
        return rs.stream().sorted(Comparator.comparing(e->e.employeeCode)).collect(Collectors.toList());
    }
    private static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
