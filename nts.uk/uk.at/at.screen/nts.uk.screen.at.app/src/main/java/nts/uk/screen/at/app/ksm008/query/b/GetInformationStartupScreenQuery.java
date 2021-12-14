package nts.uk.screen.at.app.ksm008.query.b;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.diagnose.stopwatch.concurrent.ConcurrentStopwatches;
import nts.arc.diagnose.stopwatch.embed.EmbedStopwatch;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapter;
import nts.uk.ctx.at.schedulealarm.app.query.alarmcheck.AlarmCheckConditionsQuery;
import nts.uk.ctx.at.schedulealarm.app.query.alarmcheck.AlarmCheckConditionsQueryDto;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.EmployeeSearchCallSystemType;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetEmpCanReferService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.RegulationInfoEmpQuery;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.pub.workplace.export.EmpOrganizationPub;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.EmpOrganizationExport;
import nts.uk.ctx.bs.person.dom.person.info.Person;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.query.pub.employee.EmployeeSearchQueryDto;
import nts.uk.query.pub.employee.RegulationInfoEmployeeExport;
import nts.uk.query.pub.employee.RegulationInfoEmployeePub;
import nts.uk.screen.at.app.ksm008.query.b.dto.InitScreenDto;
import nts.uk.screen.at.app.ksm008.query.b.dto.ParamInitScreen;
import nts.uk.screen.at.app.ksm008.query.b.dto.PersonInfoDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KSM_スケジュールマスタ.KSM008 スケジュールのアラームチェック設定.B: 同時出勤指定.メニュー別OCD.初期起動の情報取得する
 *
 *  @author 3Si/hai.tt
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GetInformationStartupScreenQuery {

    @Inject
    private EmpOrganizationPub empOrganizationPub;

    @Inject
    private WorkplaceGroupAdapter workplaceGroupAdapter;
    @Inject
    private RegulationInfoEmployeeAdapter regulInfoEmployeeAdap;
    @Inject
    private RegulationInfoEmployeePub regulInfoEmpPub;

    @Inject
    private EmployeeDataMngInfoRepository empMngDataRepo;
    @Inject
    private PersonRepository personRepository;

    @Inject
    private AlarmCheckConditionsQuery alarmCheckConditionsQuery;

    final static String SPACE = " ";
    final static String ZEZO_TIME = "00:00";
    final static String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm";

    public InitScreenDto getInfoStartup(ParamInitScreen param) {

        String code = param.getCode();
        String sid = AppContexts.user().employeeId();
        GeneralDate baseDate = GeneralDate.today();

        GetTargetIdentifiInforService.Require require = EmbedStopwatch.embed(new RequireImpl(empOrganizationPub));

        // 1. 取得する(Require, 年月日, 社員ID)
        TargetOrgIdenInfor targeOrg = GetTargetIdentifiInforService.get(require, baseDate, sid);

        GetEmpCanReferService.Require requireGetEmpBySpecrOrg = EmbedStopwatch.embed(new RequireGetEmpBySpecrOrgImpl(
                workplaceGroupAdapter,
                regulInfoEmployeeAdap,
                regulInfoEmpPub));

        // 2. 取得する(Require, 年月日, 社員ID, 対象組織識別情報)
        List<String> sids = GetEmpCanReferService.getByOrg(requireGetEmpBySpecrOrg, baseDate, sid, targeOrg);

        ConcurrentStopwatches.printAll();

        // 3. 取得する(List<社員ID>)
        // ドメインモデル「社員データ管理情報」を全て取得する
        List<EmployeeDataMngInfo> empInfos = empMngDataRepo.findByListEmployeeId(sids);

        // ドメインモデル「個人基本情報」を全て取得する
        Map<String, Person> mapPerson = personRepository.getAllPersonByPids(
                empInfos.stream().map(EmployeeDataMngInfo::getPersonId).collect(Collectors.toList()
                )).stream().collect(Collectors.toMap(Person::getPersonId, i -> i));

        InitScreenDto result = new InitScreenDto();

        List<PersonInfoDto> personInfoDtos = empInfos.stream().map( i -> new PersonInfoDto(
                i.getEmployeeId(),
                i.getEmployeeCode().v(),
                mapPerson.containsKey(i.getPersonId()) ? mapPerson.get(i.getPersonId()).getPersonNameGroup().getBusinessName().v() : ""
        )).collect(Collectors.toList());

        AlarmCheckConditionsQueryDto checkCondition = alarmCheckConditionsQuery.getCodeNameDescription(code);
        result.setPersonInfos(personInfoDtos);
        //TODO wait refactor code
        result.setAlarmCheck(checkCondition);

        return result;
    }


    @AllArgsConstructor
    private class RequireImpl implements GetTargetIdentifiInforService.Require {

        @Inject
        private EmpOrganizationPub empOrganizationPub;

        @Override
        public List<EmpOrganizationImport> getEmpOrganization(GeneralDate referenceDate, List<String> listEmpId) {

            List<EmpOrganizationExport> exports = empOrganizationPub.getEmpOrganiztion(referenceDate, listEmpId);
            List<EmpOrganizationImport> data = exports.stream().map(i -> {
                return new EmpOrganizationImport(new EmployeeId(i.getEmpId()), i.getBusinessName(), i.getEmpCd(), i.getWorkplaceId(), i.getWorkplaceGroupId());
            }).collect(Collectors.toList());
            return data;
        }

    }

    @AllArgsConstructor
    private class RequireGetEmpBySpecrOrgImpl implements GetEmpCanReferService.Require {

        @Inject
        private WorkplaceGroupAdapter workplaceGroupAdapter;
        @Inject
        private RegulationInfoEmployeeAdapter regulInfoEmpAdap;
        @Inject
        private RegulationInfoEmployeePub regulInfoEmpPub;

        @Override
        public List<String> getEmpCanReferByWorkplaceGroup(GeneralDate date, String empId, String workplaceGroupID) {
            List<String> data = workplaceGroupAdapter.getReferableEmp( date, empId, workplaceGroupID);
            return data;
        }
        
        @Override
        public List<String> sortEmployee(List<String> lstmployeeId, EmployeeSearchCallSystemType sysAtr, Integer sortOrderNo,
                                         GeneralDate referenceDate, Integer nameType) {
        	
            List<String> data = regulInfoEmpAdap.sortEmployee(
            		AppContexts.user().companyId(), 
            		lstmployeeId, 
            		sysAtr.value, 
            		sortOrderNo, 
            		nameType,
                    GeneralDateTime.fromString(referenceDate.toString() + SPACE + ZEZO_TIME, DATE_TIME_FORMAT));
            return data;
        }

        @Override
        public String getRoleID() {
        	
            return AppContexts.user().roles().forAttendance();
        }

        @Override
        public List<String> searchEmployee(RegulationInfoEmpQuery q, String roleId) {
            EmployeeSearchQueryDto query = EmployeeSearchQueryDto.builder()
                    .baseDate(GeneralDateTime.fromString(q.getBaseDate().toString() + SPACE + ZEZO_TIME, DATE_TIME_FORMAT))
                    .referenceRange(q.getReferenceRange().value)
                    .systemType(q.getSystemType().value)
                    .filterByWorkplace(q.getFilterByWorkplace())
                    .workplaceCodes(q.getWorkplaceIds())
                    .filterByEmployment(false)
                    .employmentCodes(new ArrayList<String>())
                    .filterByDepartment(false)
                    .departmentCodes(new ArrayList<String>())
                    .filterByClassification(false)
                    .classificationCodes(new ArrayList<String>())
                    .filterByJobTitle(false)
                    .jobTitleCodes(new ArrayList<String>())
                    .filterByWorktype(false)
                    .worktypeCodes(new ArrayList<String>())
                    .filterByClosure(false)
                    .closureIds(new ArrayList<Integer>())
                    .periodStart(GeneralDateTime.now())
                    .periodEnd(GeneralDateTime.now())
                    .includeIncumbents(true)
                    .includeWorkersOnLeave(true)
                    .includeOccupancy(true)
                    .includeRetirees(false)
                    .includeAreOnLoan(false)
                    .includeGoingOnLoan(false)
                    .retireStart(GeneralDateTime.now())
                    .retireEnd(GeneralDateTime.now())
                    .sortOrderNo(null)
                    .nameType(null)

                    .build();
            List<RegulationInfoEmployeeExport> data = regulInfoEmpPub.find(query);
            List<String> resultList = data.stream().map(item -> item.getEmployeeId())
                    .collect(Collectors.toList());
            return resultList;
        }

        @Override
		public List<String> getAllEmpCanReferByWorkplaceGroup(GeneralDate date, String empId) {
			// don't have to implement it
			return null;
		}

    }

}
