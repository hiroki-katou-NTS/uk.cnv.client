package nts.uk.ctx.at.function.app.find.holidaysremaining;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.role.RoleExportRpAdapter;
import nts.uk.ctx.at.function.dom.holidaysremaining.HolidaysRemainingManagement;
import nts.uk.ctx.at.function.dom.holidaysremaining.PermissionOfEmploymentForm;
import nts.uk.ctx.at.function.dom.holidaysremaining.VariousVacationControlService;
import nts.uk.ctx.at.function.dom.holidaysremaining.repository.HolidaysRemainingManagementRepository;
import nts.uk.ctx.at.function.dom.holidaysremaining.repository.PermissionOfEmploymentFormRepository;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.ItemSelectionEnum;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 休暇残数管理表の出力項目設定
 */

@Stateless
public class HdRemainManageFinder {

    @Inject
    private HolidaysRemainingManagementRepository hdRemainingManagementRepo;
    @Inject
    private ClosureEmploymentRepository closureEmploymentRepository;
    @Inject
    private ClosureRepository closureRepository;
    @Inject
    private ShareEmploymentAdapter shrEmpAdapter;
    @Inject
    private PermissionOfEmploymentFormRepository permissionOfEmploymentFormRepository;
    @Inject
    private VariousVacationControlService variousVacationControlService;
    @Inject
    private RoleExportRpAdapter roleExportRepoAdapter;

    public List<HdRemainManageDto> findAll() {
        return this.hdRemainingManagementRepo.getHolidayManagerLogByCompanyId(AppContexts.user().companyId()).stream()
                .map(HdRemainManageDto::fromDomain).collect(Collectors.toList());
    }

    public List<HdRemainManageDto> findFreeSetting() {
        val sid = AppContexts.user().employeeId();
        val data = this.hdRemainingManagementRepo.getHolidayManagerLogByCompanyId(AppContexts.user().companyId()).stream()
                .map(HdRemainManageDto::fromDomain).collect(Collectors.toList());
        return data.stream()
                .filter(s -> s.getItemSelType() == ItemSelectionEnum.FREE_SETTING.value && s.getSid().equals(sid))
                .collect(Collectors.toList());
    }

    public List<HdRemainManageDto> findFreeStandard() {
        return this.hdRemainingManagementRepo.getHolidayManagerLogByCompanyId(AppContexts.user().companyId()).stream()
                .filter(s -> s.getItemSelectionCategory().value.equals(ItemSelectionEnum.STANDARD_SELECTION.value))
                .map(HdRemainManageDto::fromDomain).collect(Collectors.toList());

    }

    public List<HdRemainManageDto> finBySetting(Integer setting) {
        List<HdRemainManageDto> rs = new ArrayList<>();
        val cid = AppContexts.user().companyId();
        val sid = AppContexts.user().employeeId();
        val data = this.hdRemainingManagementRepo.getHolidayManagerLogByCompanyId(cid).stream()
                .map(HdRemainManageDto::fromDomain).collect(Collectors.toList());
        if (setting.equals(ItemSelectionEnum.FREE_SETTING.value)) {
            rs = data.stream()
                    .filter(s -> s.getItemSelType() == ItemSelectionEnum.FREE_SETTING.value && s.getSid().equals(sid))
                    .collect(Collectors.toList());
        } else if (setting.equals(ItemSelectionEnum.STANDARD_SELECTION.value)) {
            rs = data.stream()
                    .filter(s -> s.getItemSelType() == ItemSelectionEnum.STANDARD_SELECTION.value)
                    .collect(Collectors.toList());
        }
        return rs;

    }

    public HDDto findAllNew() {
        val sid = AppContexts.user().employeeId();
        val data = this.hdRemainingManagementRepo.getHolidayManagerLogByCompanyId(AppContexts.user().companyId()).stream()
                .map(HdRemainManageDto::fromDomain).collect(Collectors.toList());

        List<HdRemainManageDto> listFreeSetting = data.stream()
                .filter(s -> s.getItemSelType() == ItemSelectionEnum.FREE_SETTING.value
                        && s.getSid().equals(sid))
                .sorted(Comparator.comparing(HdRemainManageDto::getCd))
                .collect(Collectors.toList());
        List<HdRemainManageDto> listStandard = data.stream()
                .filter(s -> s.getItemSelType() == ItemSelectionEnum.STANDARD_SELECTION.value)
                .sorted(Comparator.comparing(HdRemainManageDto::getCd))
                .collect(Collectors.toList());
        return new HDDto(listFreeSetting, listStandard);

    }

    public Optional<HolidaysRemainingManagement> findByCode(String code) {

        return this.hdRemainingManagementRepo.getHolidayManagerByCidAndExecCd(AppContexts.user().companyId(), code);
    }
    public Optional<HolidaysRemainingManagement> findByLayOutId(String layOutId) {

        return this.hdRemainingManagementRepo.getHolidayManagerByLayOutId(layOutId);
    }

    public HdRemainManageDto findDtoByCode(String code) {
        Optional<HolidaysRemainingManagement> hdManagement = this.hdRemainingManagementRepo
                .getHolidayManagerByCidAndExecCd(AppContexts.user().companyId(), code);
        return hdManagement.map(HdRemainManageDto::fromDomain).orElse(null);
    }

    public HdRemainManageDto findDtoByLayOutId(String layOutId) {
        Optional<HolidaysRemainingManagement> hdManagement = this.hdRemainingManagementRepo
                .getHolidayManagerByLayOutId(layOutId);
        return hdManagement.map(HdRemainManageDto::fromDomain).orElse(null);
    }

    // 当月を取得
    public Optional<YearMonth> getCurrentMonth(String companyId, String employeeId, GeneralDate systemDate) {
        // ドメインモデル「所属雇用履歴」を取得する
        Optional<BsEmploymentHistoryImport> bsEmploymentHistOpt = shrEmpAdapter
                .findEmploymentHistory(companyId, employeeId, systemDate);
        if (!bsEmploymentHistOpt.isPresent()) {
            return Optional.empty();
        }

        String employmentCode = bsEmploymentHistOpt.get().getEmploymentCode();
        // ドメインモデル「雇用に紐づく就業締め」を取得する
        Optional<ClosureEmployment> closureEmploymentOpt = closureEmploymentRepository.findByEmploymentCD(companyId,
                employmentCode);

        // 雇用に紐づく締めを取得する
        Integer closureId = 1;
        if (closureEmploymentOpt.isPresent()) {
            closureId = closureEmploymentOpt.get().getClosureId();
        }

        // 当月の年月を取得する
        Optional<Closure> closureOpt = closureRepository.findById(companyId, closureId);
        if (closureOpt.isPresent()) {
            return Optional.of(closureOpt.get().getClosureMonth().getProcessingYm());
        }
        return Optional.empty();
    }

    public DateHolidayRemainingDto getDate() {
        String companyId = AppContexts.user().companyId();
        String employeeId = AppContexts.user().employeeId();
        GeneralDate baseDate = GeneralDate.today(); //

        Optional<YearMonth> currentMonthOpt = this.getCurrentMonth(companyId, employeeId, baseDate);

        if (!currentMonthOpt.isPresent()) {
            return null;
        }

        // fix hộ bug team G #102630
        GeneralDate endDate = GeneralDate.ymd(currentMonthOpt.get().year(), currentMonthOpt.get().month(), 1).addMonths(1);
        GeneralDate startDate = endDate.addYears(-1);

        return new DateHolidayRemainingDto(startDate.toString(), endDate.toString());
    }

    public PermissionOfEmploymentFormDto getPermissionOfEmploymentForm() {
        String companyId = AppContexts.user().companyId();
        String employeeRoleId = AppContexts.user().roles().forAttendance();

        Optional<PermissionOfEmploymentForm> permission = this.permissionOfEmploymentFormRepository.find(companyId,
                employeeRoleId, 1);
        return permission
                .map(permissionOfEmploymentForm -> new PermissionOfEmploymentFormDto(
                        permissionOfEmploymentForm.getCompanyId(), permissionOfEmploymentForm.getRoleId(),
                        permissionOfEmploymentForm.getFunctionNo(), permissionOfEmploymentForm.isAvailable()))
                .orElseGet(() -> new PermissionOfEmploymentFormDto(companyId, employeeRoleId, 1, false));

    }

    public RoleWhetherLoginDto getCurrentLoginerRole() {
        RoleWhetherLoginDto role = new RoleWhetherLoginDto();
        role.setEmployeeCharge(roleExportRepoAdapter.getCurrentLoginerRole().isEmployeeCharge());
        return role;
    }

    public VariousVacationControlDto getVariousVacationControl() {
        return VariousVacationControlDto.fromDomain(variousVacationControlService.getVariousVacationControl());
    }

    // 当月を取得 - ver2
    public Map<String, YearMonth> getCurrentMonthVer2(String companyId, List<String> lstSID, GeneralDate systemDate) {
        // ドメインモデル「所属雇用履歴」を取得する
        Map<String, BsEmploymentHistoryImport> bsEmpHistt = shrEmpAdapter.findEmpHistoryVer2(companyId, lstSID, systemDate);
        if (bsEmpHistt.isEmpty()) {
            return new HashMap<>();
        }

        List<String> lstEmpCode = bsEmpHistt.entrySet().stream().map(c -> c.getValue().getEmploymentCode()).collect(Collectors.toList());
        // ドメインモデル「雇用に紐づく就業締め」を取得する
        List<ClosureEmployment> lstClosureEmp = closureEmploymentRepository.findListEmployment(companyId, lstEmpCode);
        if (lstClosureEmp.isEmpty()) {
            return new HashMap<>();
        }
        // 雇用に紐づく締めを取得する
        Integer clsIdDefault = 1;
        List<Integer> lstClsId = lstClosureEmp.stream().map(c -> c.getClosureId()).collect(Collectors.toList());
        if (!lstClsId.contains(clsIdDefault)) {
            lstClsId.add(clsIdDefault);
        }
        // 当月の年月を取得する
        List<Closure> lstCls = closureRepository.findByListId(companyId, lstClsId);
        Map<String, YearMonth> mapResult = new HashMap<>();//key sid
        Map<String, YearMonth> mapTmp = new HashMap<>();//key empCD
        for (String sid : lstSID) {
            String empCD = bsEmpHistt.get(sid).getEmploymentCode();
            if (mapTmp.containsKey(sid)) {//co san trong mapTmp
                if (mapTmp.get(empCD) == null) {//Gtri null
                    continue;
                }
                mapResult.put(sid, mapTmp.get(empCD));
            } else {
                YearMonth curMon = this.findCurrMon(empCD, lstClosureEmp, lstCls);
                if (curMon != null) {//Gtri != null
                    mapResult.put(sid, curMon);
                }
                mapTmp.put(empCD, curMon);
            }
        }
        return mapResult;
    }

    private YearMonth findCurrMon(String empCD, List<ClosureEmployment> lstClosureEmp, List<Closure> lstCls) {
        for (ClosureEmployment closure : lstClosureEmp) {
            if (closure.getEmploymentCD().equals(empCD)) {
                return this.findYearMonth(lstCls, closure.getClosureId());
            }
        }
        return this.findYearMonth(lstCls, 1);
    }

    private YearMonth findYearMonth(List<Closure> lstCls, Integer clsID) {
        for (Closure closure : lstCls) {
            if (closure.getClosureId().value == clsID) {
                return closure.getClosureMonth().getProcessingYm();
            }
        }
        return null;
    }
}
