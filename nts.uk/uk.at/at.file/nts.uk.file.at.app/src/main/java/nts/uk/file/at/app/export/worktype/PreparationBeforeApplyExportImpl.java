package nts.uk.file.at.app.export.worktype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.primitive.PrimitiveValue;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeSetRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExeConditionRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.UseDivision;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.AppDateContradictionAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.UnregisterableCheckAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.CancelAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.AppSetForProxyApp;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.BeforeAddCheckMethod;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReason;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReasonRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.RecordDate;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.appdeadlineset.AppDeadlineSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.appdeadlineset.DeadlineCriteria;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationrestrictionsetting.AppLimitSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.ReceptionRestrictionSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.FlexWorkAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeQuotaSetUse;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDispSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.CalcStampMiss;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.OverrideSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.ApplicationSettingItem;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.AtWorkAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.Time36AgreeCheckRegister;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.AppStampSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.ApplicationStampSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.SettingForEachType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.StampAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationTypeDisplayName;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.workchange.AppWorkChangeSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.workchange.InitDisplayWorktimeAtr;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandard;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandardRepository;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.ReasonTypeItem;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSet;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.emailset.Division;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSetRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.BreakOrRestTime;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.BusinessTripAppWorkType;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApplicationUseSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.requestbycompany.RequestByCompany;
import nts.uk.ctx.at.request.dom.setting.workplace.requestbycompany.RequestByCompanyRepository;
import nts.uk.ctx.at.request.dom.setting.workplace.requestbyworkplace.RequestByWorkplace;
import nts.uk.ctx.at.request.dom.setting.workplace.requestbyworkplace.RequestByWorkplaceRepository;
import nts.uk.ctx.at.shared.app.find.workrule.closure.ClosureHistoryFinder;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureHistoryFindDto;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrame;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrameRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.ApplicationStatus;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.lateearlycancellation.LateEarlyCancelReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.lateearlycancellation.LateEarlyCancelReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.OtHdWorkAppSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.hdworkapply.HdWorkAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.hdworkapply.HdWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.OtWorkAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.OtWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.ReflectWorkHourCondition;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.VacationApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.VacationApplicationReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubstituteLeaveAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeApp;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformation;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformationRepository;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameExport;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameQuery;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuPub;
import nts.uk.ctx.workflow.app.find.approvermanagement.setting.ApprovalSettingDto;
import nts.uk.ctx.workflow.app.find.approvermanagement.setting.ApprovalSettingFinder;
import nts.uk.screen.at.app.worktype.WorkTypeDto;
import nts.uk.screen.at.app.worktype.WorkTypeQueryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.data.SheetData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;

@Stateless
@DomainID(value = "PreparationBeforeApply")
public class PreparationBeforeApplyExportImpl implements MasterListData {

    private static final String COLUMN_NO_HEADER = "COLUMN_NO_HEADER_";
    private static final String CHECK = "○";
    private static final String NOT_CHECK = "-";
    private static final int ROW_SIZE_A4 = 4;
    private static final int ROW_SIZE_A5 = 3;
    private static final int ROW_SIZE_A9 = 4;
    private static final int MAIN_COL_SIZE = 6;
    private static final int REASON_COL_SIZE = 4;
    private static final int PRESENT_APP_COL_SIZE = 3;
    private static final int OVERTIME_COL_SIZE = 4;
    private static final int EMPLOYMENT_COL_SIZE = 7;
    private static final int WORKPLACE_COL_SIZE = 5;


    @Inject
    private ClosureHistoryFinder closureHistoryFinder;

    @Inject
    private AppWorkChangeSetRepository appWorkChangeSetRepo;

    @Inject
    private ApprovalSettingFinder approvalSettingFinder;

    @Inject
    private StandardMenuPub menuPub;

    @Inject
    private ApplicationSettingRepository appSettingRepo;

    @Inject
    private DisplayReasonRepository displayReasonRepo;

    @Inject
    private OvertimeAppSetRepository overtimeAppSetRepo;

    @Inject
    private AppReflectExeConditionRepository appReflectConditionRepo;

    @Inject
    private GoBackReflectRepository goBackReflectRepo;

    @Inject
    private LateEarlyCancelAppSetRepository lateEarlyCancelRepo;

    @Inject
    private ApplicationStampSettingRepository appStampSettingRepo;

    @Inject
    private ApprovalListDispSetRepository approvalListDispSetRepo;

    @Inject
    private AppTripRequestSetRepository appTripRequestSetRepo;

    @Inject
    private AppEmailSetRepository appEmailSetRepo;

    @Inject
    private HolidayApplicationSettingRepository holidayApplicationSettingRepo;

    @Inject
    private VacationApplicationReflectRepository holidayApplicationReflectRepo;

    @Inject
    private HolidayWorkAppSetRepository holidayWorkAppSetRepo;

    @Inject
    private TimeLeaveAppReflectRepository timeLeaveAppReflectRepo;

    @Inject
    private OptionalItemAppSetRepository optionalItemAppSetRepo;

    @Inject
    private SubstituteHdWorkAppSetRepository substituteHdWorkAppSetRepo;

    @Inject
    private OtHdWorkAppSettingRepository appSetRepo;

    @Inject
    private OtWorkAppReflectRepository otWorkReflectRepo;

    @Inject
    private HdWorkAppReflectRepository hdWorkReflectRepo;

    @Inject
    private SubLeaveAppReflectRepository substituteLeaveAppReflectRepo;

    @Inject
    private SubstituteWorkAppReflectRepository substituteWorkAppReflectRepo;

    @Inject
    private LateEarlyCancelReflectRepository lateEarlyCancelReflectRepo;

    @Inject
    private StampAppReflectRepository stampAppReflectRepo;

    @Inject
    private AppReasonStandardRepository appReasonStandardRepo;

    @Inject
    private OvertimeWorkFrameRepository otWorkFrameRepo;

    @Inject
    private OptionalItemRepository optionalItemRepo;

    @Inject
    private AppEmploymentSetRepository appEmploymentSetRepo;

    @Inject
    private EmploymentRepository empRepo;

    @Inject
    private WorkTypeQueryRepository workTypeQueryRepository;

    @Inject
    private RequestByCompanyRepository requestByCompanyRepo;

    @Inject
    private RequestByWorkplaceRepository requestByWorkplaceRepo;

    @Inject
    private WorkplaceInformationRepository wkpInforRepo;


    @Override
    public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery arg0) {
        return getCommonHeaderColumns(MAIN_COL_SIZE);
    }

    private List<MasterHeaderColumn> getCommonHeaderColumns(int colSize) {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        for (int i = 0; i < colSize; i++) {
            String columnText;
            if (i == 0) columnText = TextResource.localize("KAF022_454");
            else if (i == colSize - 1) columnText = TextResource.localize("KAF022_455");
            else columnText = "";
            columns.add(
                    new MasterHeaderColumn(
                            COLUMN_NO_HEADER + i,
                            columnText,
                            ColumnTextAlign.LEFT,
                            "",
                            true
                    )
            );
        }
        return columns;
    }

    private List<MasterData> getDataA4(List<AppDeadlineSetting> appDeadlineSetLst) {
        List<MasterData> data = new ArrayList<>();
        List<ClosureHistoryFindDto> listClosures = closureHistoryFinder.findAll();
        for (int t = 0; t < listClosures.size(); t++) {
            ClosureHistoryFindDto closureDto = listClosures.get(t);
            Optional<AppDeadlineSetting> appDeadlineSetting = appDeadlineSetLst.stream().filter(i -> i.getClosureId() == closureDto.getId()).findFirst();
            for (int row = 0; row < ROW_SIZE_A4; row++) {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < MAIN_COL_SIZE; col++) {
                    String value;
                    if (col == 0 && row == 0 && t == 0) value = TextResource.localize("KAF022_456");
                    else if (col == 1 && row == 0) value = closureDto.getId().toString();
                    else if (col == 2) {
                        if (row == 0) value = TextResource.localize("KAF022_457");
                        else if (row == 1) value = TextResource.localize("KAF022_458");
                        else if (row == 2) value = TextResource.localize("KAF022_459");
                        else value = "";
                    } else if (col == 5) {
                        switch (row) {
                            case 0:
                                value = closureDto.getName();
                                break;
                            case 1:
                                value = appDeadlineSetting.isPresent()
                                        ? (appDeadlineSetting.get().getUseAtr() == UseDivision.TO_USE ? CHECK : NOT_CHECK)
                                        : "";
                                break;
                            case 2:
                                if (appDeadlineSetting.isPresent() && appDeadlineSetting.get().getUseAtr() == UseDivision.TO_USE)
                                    value = appDeadlineSetting.get().getDeadlineCriteria() == DeadlineCriteria.CALENDAR_DAY
                                        ? TextResource.localize("KAF022_321") + TextResource.localize("KAF022_508")
                                        : TextResource.localize("KAF022_322") + TextResource.localize("KAF022_508");
                                else value = "";
                                break;
                            case 3:
                                if (appDeadlineSetting.isPresent() && appDeadlineSetting.get().getUseAtr() == UseDivision.TO_USE) {
                                    int resourceId = 323 + appDeadlineSetting.get().getDeadline().v();
                                    String resource = "KAF022_" + resourceId;
                                    value = TextResource.localize(resource) + TextResource.localize("KAF022_509");
                                } else value = "";
                                break;
                            default:
                                value = "";
                        }
                    } else value = "";

                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(value)
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }

                data.add(MasterData.builder().rowData(rowData).build());
            }
        }
        return data;
    }

    private List<MasterData> getDataA7(List<ReceptionRestrictionSetting> receptionRestrictionSettings) {
        receptionRestrictionSettings.sort(Comparator.comparing(ReceptionRestrictionSetting::getAppType));
        List<MasterData> data = new ArrayList<>();
        for (int t = 0; t < receptionRestrictionSettings.size(); t++) {
            ReceptionRestrictionSetting setting = receptionRestrictionSettings.get(t);
            int maxRow = setting.getAppType() == ApplicationType.OVER_TIME_APPLICATION ? 7 : 3;
            for (int row = 0; row < maxRow; row++) {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < MAIN_COL_SIZE; col++) {
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(getValueA7(col, row, t, maxRow - 1, setting))
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            }
        }
        return data;
    }

    private String getValueA7(int col, int row, int block, int lastRow, ReceptionRestrictionSetting setting) {
        if (col == 0 && row == 0 && block == 0) return TextResource.localize("KAF022_466");

        if (col == 1 && row == 0) return setting.getAppType().name;

        if (col == 2 && row == 0) return TextResource.localize("KAF022_467");
        if (col == 2 && row == lastRow) return TextResource.localize("KAF022_476");

        if (col == 3 && row == 0) return TextResource.localize("KAF022_468");
        if (lastRow > 2) {
            if (col == 3 && row == 1) return TextResource.localize("KAF022_469");
            if (col == 3 && row == 2) return TextResource.localize("KAF022_470");
            if (col == 3 && row == 3) return TextResource.localize("KAF022_472");
            if (col == 3 && row == lastRow) return TextResource.localize("KAF022_477");

            if (col == 4 && row == 2) return TextResource.localize("KAF022_471");
            if (col == 4 && row == 3) return TextResource.localize("KAF022_473");
            if (col == 4 && row == 4) return TextResource.localize("KAF022_474");
            if (col == 4 && row == 5) return TextResource.localize("KAF022_475");

            if (col == MAIN_COL_SIZE - 1) {
                if (row == 0) return setting.getOtAppBeforeAccepRestric().get().isToUse() ? CHECK : NOT_CHECK;
                if (row == 1)
                    return setting.getOtAppBeforeAccepRestric().get().isToUse()
                            ? setting.getOtAppBeforeAccepRestric().get().getMethodCheck() == BeforeAddCheckMethod.CHECK_IN_DAY ? TextResource.localize("KAF022_63") : TextResource.localize("KAF022_66")
                            : "";
                if (row == 2)
                    return setting.getOtAppBeforeAccepRestric().get().isToUse() && setting.getOtAppBeforeAccepRestric().get().getMethodCheck() == BeforeAddCheckMethod.CHECK_IN_DAY
                            ? setting.getOtAppBeforeAccepRestric().get().getDateBeforehandRestrictions().name + TextResource.localize("KAF022_510")
                            : "";
                if (row == 3)
                    return setting.getOtAppBeforeAccepRestric().get().isToUse() && setting.getOtAppBeforeAccepRestric().get().getMethodCheck() == BeforeAddCheckMethod.CHECK_IN_TIME
                            ? setting.getOtAppBeforeAccepRestric().get().getOpEarlyOvertime().map(val -> convertToTime(val.v()) + TextResource.localize("KAF022_510")).orElse("")
                            : "";
                if (row == 4)
                    return setting.getOtAppBeforeAccepRestric().get().isToUse() && setting.getOtAppBeforeAccepRestric().get().getMethodCheck() == BeforeAddCheckMethod.CHECK_IN_TIME
                            ? setting.getOtAppBeforeAccepRestric().get().getOpNormalOvertime().map(val -> convertToTime(val.v()) + TextResource.localize("KAF022_510")).orElse("")
                            : "";
                if (row == 5)
                    return setting.getOtAppBeforeAccepRestric().get().isToUse() && setting.getOtAppBeforeAccepRestric().get().getMethodCheck() == BeforeAddCheckMethod.CHECK_IN_TIME
                            ? setting.getOtAppBeforeAccepRestric().get().getOpEarlyNormalOvertime().map(val -> convertToTime(val.v()) + TextResource.localize("KAF022_510")).orElse("")
                            : "";
                return setting.getAfterhandRestriction().isAllowFutureDay() ? CHECK : NOT_CHECK;
            }
        } else {
            if (col == 3 && row == 2) return TextResource.localize("KAF022_477");

            if (col == 4 && row == 1) return TextResource.localize("KAF022_470");

            if (col == MAIN_COL_SIZE - 1) {
                if (row == 0) return setting.getBeforehandRestriction().get().isToUse() ? CHECK : NOT_CHECK;
                else if (row == 1)
                    return setting.getBeforehandRestriction().get().isToUse()
                            ? setting.getBeforehandRestriction().get().getDateBeforehandRestrictions().name + TextResource.localize("KAF022_510")
                            : "";
                else return setting.getAfterhandRestriction().isAllowFutureDay() ? CHECK : NOT_CHECK;
            }
        }

        return "";
    }

    private List<MasterData> getDataA11(AppLimitSetting appLimitSetting) {
        List<MasterData> data = new ArrayList<>();
        for (int row = 0; row < ROW_SIZE_A9; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < MAIN_COL_SIZE; col++) {
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(getValueA11(col, row, appLimitSetting))
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build()
                );
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private String getValueA11(int col, int row, AppLimitSetting setting) {
        if (col == 0 && row == 0) return TextResource.localize("KAF022_485");
        if (col == 1) {
            if (row == 0) return TextResource.localize("KAF022_488");
            if (row == 1) return TextResource.localize("KAF022_489");
            if (row == 2) return TextResource.localize("KAF022_490");
            else return TextResource.localize("KAF022_491");
        }
        if (col == MAIN_COL_SIZE - 1) {
            if (row == 0)
                return setting.isCanAppAchievementConfirm() ? TextResource.localize("KAF022_437") : TextResource.localize("KAF022_438");
            if (row == 1)
                return setting.isCanAppAchievementMonthConfirm() ? TextResource.localize("KAF022_437") : TextResource.localize("KAF022_438");
            if (row == 2)
                return setting.isCanAppFinishWork() ? TextResource.localize("KAF022_437") : TextResource.localize("KAF022_438");
            else
                return setting.isCanAppAchievementLock() ? TextResource.localize("KAF022_437") : TextResource.localize("KAF022_438");
        }
        return "";
    }

    private List<MasterData> getDataA17(RecordDate recordDateAtr, int approvalByPersonAtr) {
        List<MasterData> data = new ArrayList<>();
        for (int row = 0; row < ROW_SIZE_A5; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < MAIN_COL_SIZE; col++) {
                boolean value;
                if (row == 0) value = recordDateAtr == RecordDate.APP_DATE;
                else value = approvalByPersonAtr == 1;
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(getValueA17(col, row, value))
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private String getValueA17(int col, int row, boolean value) {
        if (col == 0 && row == 0) return TextResource.localize("KAF022_460");
        if (col == 1 && row == 0) return TextResource.localize("KAF022_461");
        if (col == 1 && row == 1) return TextResource.localize("KAF022_462");
        if (col == 1 && row == 2) return TextResource.localize("KAF022_498");
        if (col == MAIN_COL_SIZE - 1) {
            if (row == 0) return value ? TextResource.localize("KAF022_404") : TextResource.localize("KAF022_403");
            if (row == 1) return value ? TextResource.localize("KAF022_272") : TextResource.localize("KAF022_273");
            else return value ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
        }
        return "";
    }

    private List<MasterData> getDataA19(NotUseAtr nightOvertimeReflectAtr) {
        List<MasterData> data = new ArrayList<>();
        Map<String, MasterCellData> rowData = new HashMap<>();
        for (int col = 0; col < MAIN_COL_SIZE; col++) {
            String value;
            if (col == 0) value = TextResource.localize("KAF022_740");
            else if (col == 1) value = TextResource.localize("KAF022_487");
            else if (col == MAIN_COL_SIZE - 1)
                value = nightOvertimeReflectAtr == NotUseAtr.USE ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
            else value = "";
            rowData.put(
                    COLUMN_NO_HEADER + col,
                    MasterCellData.builder()
                            .columnId(COLUMN_NO_HEADER + col)
                            .value(value)
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
        }
        data.add(MasterData.builder().rowData(rowData).build());
        return data;
    }

    private List<MasterData> getDataA12(DisplayAtr prePostDisplayAtr) {
        List<MasterData> data = new ArrayList<>();
        Map<String, MasterCellData> rowData = new HashMap<>();
        for (int col = 0; col < MAIN_COL_SIZE; col++) {
            String value;
            if (col == 0) value = TextResource.localize("KAF022_741");
            else if (col == 1) value = TextResource.localize("KAF022_493");
            else if (col == MAIN_COL_SIZE - 1)
                value = prePostDisplayAtr == DisplayAtr.DISPLAY ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
            else value = "";
            rowData.put(
                    COLUMN_NO_HEADER + col,
                    MasterCellData.builder()
                            .columnId(COLUMN_NO_HEADER + col)
                            .value(value)
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
        }
        data.add(MasterData.builder().rowData(rowData).build());
        return data;
    }

    private List<MasterData> getDataA22(List<AppTypeSetting> appTypeSettings) {
        appTypeSettings.sort(Comparator.comparing(AppTypeSetting::getAppType));
        List<MasterData> data = new ArrayList<>();
        for (int block = 0; block < appTypeSettings.size(); block++) {
            AppTypeSetting setting = appTypeSettings.get(block);
            for (int row = 0; row < 2; row++) {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < MAIN_COL_SIZE; col++) {
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(getValueA22(block, row, col, setting))
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            }
        }
        return data;
    }

    private String getValueA22(int block, int row, int col, AppTypeSetting setting) {
        if (block == 0 && row == 0 && col == 0) return TextResource.localize("KAF022_464");
        if (row == 0 && col == 1) return setting.getAppType().name;
        if (col == 2 && row == 0) return TextResource.localize("KAF022_483");
        if (col == 2 && row == 1) return TextResource.localize("KAF022_484");
        if (col == 3 && row == 1) return TextResource.localize("KAF022_481");
        if (col == MAIN_COL_SIZE - 1) {
            if (row == 0) {
                if (setting.getDisplayInitialSegment().isPresent()) {
                    if (setting.getDisplayInitialSegment().get() == PrePostInitAtr.PREDICT)
                        return TextResource.localize("KAF022_745");
                    if (setting.getDisplayInitialSegment().get() == PrePostInitAtr.POSTERIOR)
                        return TextResource.localize("KAF022_746");
                    return TextResource.localize("KAF022_747");
                } else {
                    return "";
                }
            } else return setting.getCanClassificationChange().isPresent()
                    ? setting.getCanClassificationChange().get() ? CHECK : NOT_CHECK
                    : "";
        }
        return "";
    }

    private List<MasterData> getDataA20(AppLimitSetting setting) {
        List<MasterData> data = new ArrayList<>();
        for (int row = 0; row < 2; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < MAIN_COL_SIZE; col++) {
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(getValueA20(row, col, setting))
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private String getValueA20(int row, int col, AppLimitSetting setting) {
        if (col == 0 && row == 0) return TextResource.localize("KAF022_743");
        if (col == 1 && row == 0) return TextResource.localize("KAF022_744");
        if (col == 1 && row == 1) return TextResource.localize("KAF022_486");
        if (col == MAIN_COL_SIZE - 1) {
            if (row == 0)
                return setting.isStandardReasonRequired()
                        ? TextResource.localize("KAF022_75")
                        : TextResource.localize("KAF022_82");
            if (row == 1)
                return setting.isRequiredAppReason()
                        ? TextResource.localize("KAF022_75")
                        : TextResource.localize("KAF022_82");
        }
        return "";
    }

    private List<MasterData> getDataA8(List<DisplayReason> displayReasons) {
        displayReasons.sort(Comparator.comparing(DisplayReason::getAppType).thenComparing(DisplayReason::getHolidayAppType));
        List<MasterData> data = new ArrayList<>();
        for (int block = 0; block < displayReasons.size(); block++) {
            DisplayReason setting = displayReasons.get(block);
            for (int row = 0; row < 2; row++) {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < MAIN_COL_SIZE; col++) {
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(getValueA8(block, row, col, setting))
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            }
        }
        return data;
    }

    private String getValueA8(int block, int row, int col, DisplayReason setting) {
        if (block == 0 && row == 0 && col == 0) return TextResource.localize("KAF022_464");
        if (col == 1 && row == 0) {
            if (setting.getAppType() != ApplicationType.ABSENCE_APPLICATION)
                return setting.getAppType().name;
            else if (setting.getHolidayAppType() == 0)
                return setting.getAppType().name;
        }

        if (col == 2) {
            if (row == 0 && setting.getAppType() != ApplicationType.ABSENCE_APPLICATION)
                return TextResource.localize("KAF022_478");
            if (row == 0 && setting.getAppType() == ApplicationType.ABSENCE_APPLICATION)
                return "【" + setting.getOpHolidayAppType().get().name + "】";
            if (row == 1 && setting.getAppType() != ApplicationType.ABSENCE_APPLICATION)
                return TextResource.localize("KAF022_479");
        }

        if (col == 3) {
            if (setting.getAppType() != ApplicationType.ABSENCE_APPLICATION)
                return TextResource.localize("KAF022_36");
            else {
                if (row == 0) return TextResource.localize("KAF022_478");
                else return TextResource.localize("KAF022_479");
            }
        }

        if (col == 4 && setting.getAppType() == ApplicationType.ABSENCE_APPLICATION)
            return TextResource.localize("KAF022_36");

        if (col == MAIN_COL_SIZE - 1) {
            if (row == 0) return setting.getDisplayFixedReason() == DisplayAtr.DISPLAY ? CHECK : NOT_CHECK;
            else return setting.getDisplayAppReason() == DisplayAtr.DISPLAY ? CHECK : NOT_CHECK;
        }

        return "";
    }

    private List<MasterData> getDataA21(AppReflectExecutionCondition setting) {
        List<MasterData> data = new ArrayList<>();
        for (int row = 0; row < 2; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < MAIN_COL_SIZE; col++) {
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(getValueA21(row, col, setting))
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private String getValueA21(int row, int col, AppReflectExecutionCondition setting) {
        if (row == 0 && col == 0) return TextResource.localize("KAF022_77");
        if (col == 1) {
            if (row == 0) return TextResource.localize("KAF022_33");
            else return TextResource.localize("KAF022_34");
        }
        if (col == MAIN_COL_SIZE - 1) {
            if (row == 0) return setting.getEvenIfScheduleConfirmed() == NotUseAtr.USE
                    ? TextResource.localize("KAF022_44")
                    : TextResource.localize("KAF022_396");
            else return setting.getEvenIfWorkRecordConfirmed() == NotUseAtr.USE
                    ? TextResource.localize("KAF022_44")
                    : TextResource.localize("KAF022_396");
        }
        return "";
    }

    private String convertToTime(int param) {
        int m = param / 60;
        int s = param % 60;
        return m > 9 ? String.format("%02d:%02d", m, s) : String.format("%2d:%02d", m, s);
    }

    @Override
    public List<MasterData> getMasterDatas(MasterListExportQuery query) {
        String companyId = AppContexts.user().companyId();
        List<MasterData> data = new ArrayList<>();

        Optional<ApplicationSetting> applicationSetting = appSettingRepo.findByCompanyId(companyId);
        ApprovalSettingDto approvalSetting = approvalSettingFinder.findApproSet();
        List<DisplayReason> displayReasons = displayReasonRepo.findByCompanyId(companyId);
        Optional<AppReflectExecutionCondition> appReflectExecutionCondition = appReflectConditionRepo.findByCompanyId(companyId);
        if (!applicationSetting.isPresent()
                || !appReflectExecutionCondition.isPresent())
            return data;

        data.addAll(getDataA4(applicationSetting.get().getAppDeadlineSetLst()));
        data.addAll(getDataA7(applicationSetting.get().getReceptionRestrictionSettings()));
        data.addAll(getDataA11(applicationSetting.get().getAppLimitSetting()));
        data.addAll(getDataA17(applicationSetting.get().getRecordDate(), approvalSetting.getPrinFlg()));

        Integer nightOTReflectAtr = appSetRepo.getNightOvertimeReflectAtr(companyId);
        if (nightOTReflectAtr != null) {
            data.addAll(getDataA19(EnumAdaptor.valueOf(nightOTReflectAtr, NotUseAtr.class)));
        }

        data.addAll(getDataA12(applicationSetting.get().getAppDisplaySetting().getPrePostDisplayAtr()));
        data.addAll(getDataA22(applicationSetting.get().getAppTypeSettings()));
        data.addAll(getDataA20(applicationSetting.get().getAppLimitSetting()));
        data.addAll(getDataA8(displayReasons));
        data.addAll(getDataA21(appReflectExecutionCondition.get()));
        return data;
    }

    @Override
    public List<SheetData> extraSheets(MasterListExportQuery query) {
        List<SheetData> sheetData = new ArrayList<>();
        sheetData.add(SheetData.builder()
                .mainData(this.getReasonMasterData())
                .mainDataColumns(this.getReasonHeaderColumns())
                .sheetName(TextResource.localize("KAF022_511"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getRepresentAppMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(PRESENT_APP_COL_SIZE))
                .sheetName(TextResource.localize("KAF022_750"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getOvertimeMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(OVERTIME_COL_SIZE))
                .sheetName(TextResource.localize("KAF022_519"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getOtQuotaMasterData())
                .mainDataColumns(this.getOtQuotaHeaderColumns())
                .sheetName(TextResource.localize("KAF022_758"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getVacationMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(3))
                .sheetName(TextResource.localize("KAF022_537"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getWorkChangeMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(4))
                .sheetName(TextResource.localize("KAF022_564"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getBusinessTripMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(4))
                .sheetName(TextResource.localize("KAF022_751"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getDirectGoBackMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(3))
                .sheetName(TextResource.localize("KAF022_571"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getHolidayWorkMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(4))
                .sheetName(TextResource.localize("KAF022_575"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getTimeLeaveMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(3))
                .sheetName(TextResource.localize("KAF022_757"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getLateEarlyMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(3))
                .sheetName(TextResource.localize("KAF022_756"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getStampMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(4))
                .sheetName(TextResource.localize("KAF022_760"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getSubstituteMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(4))
                .sheetName(TextResource.localize("KAF022_581"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getOptionalItemMasterData())
                .mainDataColumns(this.getOptionalItemHeaderColumns())
                .sheetName(TextResource.localize("KAF022_759"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getApprovalDispMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(3))
                .sheetName(TextResource.localize("KAF022_586"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getMailMasterData())
                .mainDataColumns(this.getCommonHeaderColumns(5))
                .sheetName(TextResource.localize("KAF022_761"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getEmploymentMasterData())
                .mainDataColumns(this.getEmploymentHeaderColumns())
                .sheetName(TextResource.localize("KAF022_627"))
                .mode(this.mainSheetMode())
                .build());
        sheetData.add(SheetData.builder()
                .mainData(this.getWorkplaceMasterData())
                .mainDataColumns(this.getWorkplaceHeaderColumns())
                .sheetName(TextResource.localize("KAF022_634"))
                .mode(this.mainSheetMode())
                .build());
        return sheetData;
    }

    @Override
    public String mainSheetName() {
        return TextResource.localize("KAF022_453");
    }

    private List<MasterHeaderColumn> getReasonHeaderColumns() {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        for (int i = 0; i < REASON_COL_SIZE; i++) {
            String columnText;
            if (i == 0) columnText = TextResource.localize("KAF022_512");
            else if (i == 1) columnText = TextResource.localize("KAF022_513");
            else if (i == 2) columnText = TextResource.localize("KAF022_694");
            else columnText = TextResource.localize("KAF022_514");
            columns.add(
                    new MasterHeaderColumn(
                            COLUMN_NO_HEADER + i,
                            columnText,
                            ColumnTextAlign.LEFT,
                            "",
                            true
                    )
            );
        }
        return columns;
    }

    private  List<MasterData> getReasonMasterData() {
        String companyId = AppContexts.user().companyId();
        List<MasterData> data = new ArrayList<>();
        List<AppReasonStandard> reasons = appReasonStandardRepo.findByCompanyId(companyId);

        EnumSet.allOf(ApplicationType.class).forEach(appType -> {
            if (appType != ApplicationType.ABSENCE_APPLICATION) {
                AppReasonStandard reason = reasons.stream().filter(r -> r.getApplicationType() == appType).findFirst().orElse(null);
                if (reason != null && !reason.getReasonTypeItemLst().isEmpty()) {
                    reason.getReasonTypeItemLst().sort(Comparator.comparing(ReasonTypeItem::getDisplayOrder));
                    for (int row = 0; row < reason.getReasonTypeItemLst().size(); row++) {
                        ReasonTypeItem item = reason.getReasonTypeItemLst().get(row);
                        Map<String, MasterCellData> rowData = new HashMap<>();
                        for (int col = 0; col < REASON_COL_SIZE; col++) {
                            String value;
                            if (row == 0 && col == 0) value = reason.getApplicationType().name;
                            else if (col == 1) value = item.isDefaultValue() ? CHECK : NOT_CHECK;
                            else if (col == 2) value = item.getAppStandardReasonCD().v().toString();
                            else if (col == 3) value = item.getReasonForFixedForm().v();
                            else value = "";
                            rowData.put(
                                    COLUMN_NO_HEADER + col,
                                    MasterCellData.builder()
                                            .columnId(COLUMN_NO_HEADER + col)
                                            .value(value)
                                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                            .build());
                        }
                        data.add(MasterData.builder().rowData(rowData).build());
                    }
                }
//                else {
//                    Map<String, MasterCellData> rowData = new HashMap<>();
//                    for (int col = 0; col < REASON_COL_SIZE; col++) {
//                        String value;
//                        if (col == 0) value = appType.name;
//                        else value = "";
//                        rowData.put(
//                                COLUMN_NO_HEADER + col,
//                                MasterCellData.builder()
//                                        .columnId(COLUMN_NO_HEADER + col)
//                                        .value(value)
//                                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
//                                        .build());
//                    }
//                    data.add(MasterData.builder().rowData(rowData).build());
//                }
            } else {
                List<AppReasonStandard> hdReasons = reasons.stream().filter(r -> r.getApplicationType() == appType).collect(Collectors.toList());
                EnumSet.allOf(HolidayAppType.class).forEach(hdAppType -> {
                    AppReasonStandard hdReason = hdReasons.stream().filter(hdr -> hdr.getOpHolidayAppType().get() == hdAppType).findFirst().orElse(null);
                    if (hdReason != null && !hdReason.getReasonTypeItemLst().isEmpty()) {
                        hdReason.getReasonTypeItemLst().sort(Comparator.comparing(ReasonTypeItem::getDisplayOrder));
                        for (int row = 0; row < hdReason.getReasonTypeItemLst().size(); row++) {
                            ReasonTypeItem item = hdReason.getReasonTypeItemLst().get(row);
                            Map<String, MasterCellData> rowData = new HashMap<>();
                            for (int col = 0; col < REASON_COL_SIZE; col++) {
                                String value;
                                if (row == 0 && col == 0) value = hdReason.getApplicationType().name + " - 【" + hdReason.getOpHolidayAppType().get().name + "】";
                                else if (col == 1) value = item.isDefaultValue() ? CHECK : NOT_CHECK;
                                else if (col == 2) value = item.getAppStandardReasonCD().v().toString();
                                else if (col == 3) value = item.getReasonForFixedForm().v();
                                else value = "";
                                rowData.put(
                                        COLUMN_NO_HEADER + col,
                                        MasterCellData.builder()
                                                .columnId(COLUMN_NO_HEADER + col)
                                                .value(value)
                                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                                .build());
                            }
                            data.add(MasterData.builder().rowData(rowData).build());
                        }
                    }
//                    else {
//                        Map<String, MasterCellData> rowData = new HashMap<>();
//                        for (int col = 0; col < REASON_COL_SIZE; col++) {
//                            String value;
//                            if (col == 0) value = appType.name + " - 【" + hdAppType.name + "】";
//                            else value = "";
//                            rowData.put(
//                                    COLUMN_NO_HEADER + col,
//                                    MasterCellData.builder()
//                                            .columnId(COLUMN_NO_HEADER + col)
//                                            .value(value)
//                                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
//                                            .build());
//                        }
//                        data.add(MasterData.builder().rowData(rowData).build());
//                    }
                });
            }
        });
        return data;
    }

    private  List<MasterData> getRepresentAppMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();

        Optional<ApplicationSetting> applicationSetting = appSettingRepo.findByCompanyId(companyId);
        if (!applicationSetting.isPresent()) return data;

        List<StandardMenuNameQuery> queries = new ArrayList<>();
        queries.add(new StandardMenuNameQuery("KAF005", "A", Optional.of("overworkatr=0")));
        queries.add(new StandardMenuNameQuery("KAF005", "A", Optional.of("overworkatr=1")));
        queries.add(new StandardMenuNameQuery("KAF005", "A", Optional.of("overworkatr=2")));
        queries.add(new StandardMenuNameQuery("KAF006", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF007", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF008", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF009", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF010", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF012", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF004", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF002", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF002", "B", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF011", "A", Optional.empty()));
        queries.add(new StandardMenuNameQuery("KAF020", "A", Optional.empty()));
        List<StandardMenuNameExport> menuList = menuPub.getMenuDisplayName(companyId, queries);


        List<AppSetForProxyApp> appSetForProxyApps = applicationSetting.get().getAppSetForProxyApps();

        for (int row = 0; row < menuList.size(); row++) {
            StandardMenuNameExport menu = menuList.get(row);
            AppSetForProxyApp setting = appSetForProxyApps.stream().filter(o -> {
                switch (menu.getProgramId()) {
                    case "KAF005":
                        return o.getAppType() == ApplicationType.OVER_TIME_APPLICATION
                                && ((menu.getQueryString().equals("overworkatr=0") && o.getOpOvertimeAppAtr().get() == OvertimeAppAtr.EARLY_OVERTIME)
                                || (menu.getQueryString().equals("overworkatr=1") && o.getOpOvertimeAppAtr().get() == OvertimeAppAtr.NORMAL_OVERTIME)
                                || (menu.getQueryString().equals("overworkatr=2") && o.getOpOvertimeAppAtr().get() == OvertimeAppAtr.EARLY_NORMAL_OVERTIME));
                    case "KAF006":
                        return o.getAppType() == ApplicationType.ABSENCE_APPLICATION;
                    case "KAF007":
                        return o.getAppType() == ApplicationType.WORK_CHANGE_APPLICATION;
                    case "KAF008":
                        return o.getAppType() == ApplicationType.BUSINESS_TRIP_APPLICATION;
                    case "KAF009":
                        return o.getAppType() == ApplicationType.GO_RETURN_DIRECTLY_APPLICATION;
                    case "KAF010":
                        return o.getAppType() == ApplicationType.HOLIDAY_WORK_APPLICATION;
                    case "KAF012":
                        return o.getAppType() == ApplicationType.ANNUAL_HOLIDAY_APPLICATION;
                    case "KAF004":
                        return o.getAppType() == ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION;
                    case "KAF002":
                        return o.getAppType() == ApplicationType.STAMP_APPLICATION
                                && ((o.getOpStampRequestMode().get() == StampRequestMode.STAMP_ADDITIONAL && menu.getScreenId().equals("A"))
                                || (o.getOpStampRequestMode().get() == StampRequestMode.STAMP_ONLINE_RECORD && menu.getScreenId().equals("C")));
                    case "KAF011":
                        return o.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION;
                    case "KAF020":
                        return o.getAppType() == ApplicationType.OPTIONAL_ITEM_APPLICATION;
                    default:
                        return false;
                }
            }).findFirst().orElse(null);
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < PRESENT_APP_COL_SIZE; col++) {
                String value;
                if (row == 0 && col == 0) value = TextResource.localize("KAF022_668");
                else if (col == 1) value = menu.getDisplayName();
                else if (col == 2) value = setting == null ? NOT_CHECK : CHECK;
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private  List<MasterData> getOvertimeMasterData() {
        String companyId = AppContexts.user().companyId();
        List<MasterData> data = new ArrayList<>();
        Optional<OvertimeAppSet> applySetting = overtimeAppSetRepo.findSettingByCompanyId(companyId);
        Optional<OtWorkAppReflect> reflectSetting = otWorkReflectRepo.findReflectByCompanyId(companyId);
        if (!applySetting.isPresent() || !reflectSetting.isPresent())
            return data;

        OtWorkAppReflect overtimeWorkAppReflect = reflectSetting.get();
        for (int row = 0; row < 21; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < OVERTIME_COL_SIZE; col++) {
                String value;
                if (col == 0) {
                    if (row == 0) value = TextResource.localize("KAF022_520");
                    else if (row == 1) value = TextResource.localize("KAF022_526");
                    else if (row == 2) value = TextResource.localize("KAF022_531");
                    else if (row == 8) value = TextResource.localize("KAF022_284");
                    else if (row == 14) value = TextResource.localize("KAF022_410");
                    else value = "";
                } else if (col == 1) {
                    if (row == 0) value = TextResource.localize("KAF022_521");
                    else if (row == 1) value = TextResource.localize("KAF022_530");
                    else if (row == 2) value = TextResource.localize("KAF022_532");
                    else if (row == 3) value = TextResource.localize("KAF022_533");
                    else if (row == 4) value = TextResource.localize("KAF022_534");
                    else if (row == 5) value = TextResource.localize("KAF022_535");
                    else if (row == 6) value = TextResource.localize("KAF022_711");
                    else if (row == 7) value = TextResource.localize("KAF022_713");
                    else if (row == 8) value = TextResource.localize("KAF022_641");
                    else if (row == 9) value = TextResource.localize("KAF022_714");
                    else if (row == 10) value = TextResource.localize("KAF022_715");
                    else if (row == 11) value = TextResource.localize("KAF022_305");
                    else if (row == 12) value = TextResource.localize("KAF022_288");
                    else if (row == 13) value = TextResource.localize("KAF022_716");
                    else if (row == 14) value = TextResource.localize("KAF022_622");
                    else if (row == 17) value = TextResource.localize("KAF022_611");
                    else value = "";
                } else if (col == 2) {
                    if (row == 14) value = TextResource.localize("KAF022_717");
                    else if (row == 15) value = TextResource.localize("KAF022_718");
                    else if (row == 16) value = TextResource.localize("KAF022_719");
                    else if (row == 17) value = TextResource.localize("KAF022_720");
                    else if (row == 18) value = TextResource.localize("KAF022_719");
                    else if (row == 19) value = TextResource.localize("KAF022_721");
                    else if (row == 20) value = TextResource.localize("KAF022_722");
//                    else if (row == 21) value = TextResource.localize("KAF022_723");
                    else value = "";
                } else {
                    if (row == 0) value = overtimeWorkAppReflect.getReflectActualWorkAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_420") : TextResource.localize("KAF022_421");
                    else if (row == 1) value = applySetting.get().getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                    else if (row == 2) value = applySetting.get().getOvertimeLeaveAppCommonSet().getPreExcessDisplaySetting() == NotUseAtr.NOT_USE
                            ? TextResource.localize("KAF022_173") : TextResource.localize("KAF022_174");
                    else if (row == 3) {
                        if (applySetting.get().getOvertimeLeaveAppCommonSet().getPerformanceExcessAtr() == AppDateContradictionAtr.NOTCHECK)
                            value =  TextResource.localize("KAF022_173");
                        else if (applySetting.get().getOvertimeLeaveAppCommonSet().getPerformanceExcessAtr() == AppDateContradictionAtr.CHECKREGISTER)
                            value =  TextResource.localize("KAF022_174");
                        else value =  TextResource.localize("KAF022_175");
                    } else if (row == 4) value = applySetting.get().getOvertimeLeaveAppCommonSet().getOverrideSet() == OverrideSet.SYSTEM_TIME_PRIORITY
                            ? TextResource.localize("KAF022_709") : TextResource.localize("KAF022_710");
                    else if (row == 5) {
                        if (applySetting.get().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr() == Time36AgreeCheckRegister.NOT_CHECK)
                            value =  TextResource.localize("KAF022_173");
                        else if (applySetting.get().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr() == Time36AgreeCheckRegister.CHECK)
                            value =  TextResource.localize("KAF022_175");
                        else value =  TextResource.localize("KAF022_651");
                    } else if (row == 6) value = applySetting.get().getOvertimeLeaveAppCommonSet().getCheckOvertimeInstructionRegister() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_175") : TextResource.localize("KAF022_173");
                    else if (row == 7) value = applySetting.get().getOvertimeLeaveAppCommonSet().getCheckDeviationRegister() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_175") : TextResource.localize("KAF022_173");
                    else if (row == 8) value = applySetting.get().getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else if (row == 9) {
                        if (applySetting.get().getApplicationDetailSetting().getAtworkTimeBeginDisp() == AtWorkAtr.NOTDISPLAY) value = TextResource.localize("KAF022_37");
                        else if (applySetting.get().getApplicationDetailSetting().getAtworkTimeBeginDisp() == AtWorkAtr.DISPLAY) value = TextResource.localize("KAF022_301");
                        else if (applySetting.get().getApplicationDetailSetting().getAtworkTimeBeginDisp() == AtWorkAtr.AT_START_WORK_OFF_PERFORMANCE) value = TextResource.localize("KAF022_302");
                        else value = TextResource.localize("KAF022_303");
                    } else if (row == 10) value = applySetting.get().getApplicationDetailSetting().isDispSystemTimeWhenNoWorkTime()
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 11) value = applySetting.get().getApplicationDetailSetting().getTimeInputUse() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 12) value = applySetting.get().getApplicationDetailSetting().getRequiredInstruction()
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 13) value = applySetting.get().getApplicationDetailSetting().getPreRequireSet() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 14) value = overtimeWorkAppReflect.getBefore().getReflectWorkInfoAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                    else if (row == 15) value = overtimeWorkAppReflect.getBefore().getReflectActualOvertimeHourAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                    else if (row == 16) value = overtimeWorkAppReflect.getBefore().getBreakLeaveApplication().getBreakReflectAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                    else if (row == 17) value = overtimeWorkAppReflect.getAfter().getWorkReflect() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                    else if (row == 18) value = overtimeWorkAppReflect.getAfter().getBreakLeaveApplication().getBreakReflectAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                    else if (row == 19) value = overtimeWorkAppReflect.getAfter().getOthersReflect().getReflectDivergentReasonAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                    else value = overtimeWorkAppReflect.getAfter().getOthersReflect().getReflectPaytimeAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
//                    else value = applySetting.get().getApplicationDetailSetting().isDispSystemTimeWhenNoWorkTime()
//                            ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                }
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterHeaderColumn> getOtQuotaHeaderColumns() {
        return Arrays.asList(
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 0,
                        TextResource.localize("KAF022_689"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                ),
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 1,
                        TextResource.localize("KAF022_691"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                ),
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 2,
                        TextResource.localize("KAF022_690"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                )
        );
    }

    private List<MasterData> getOtQuotaMasterData() {
        String companyId = AppContexts.user().companyId();
        List<MasterData> data = new ArrayList<>();
        List<OvertimeQuotaSetUse> settings = overtimeAppSetRepo.getOvertimeQuotaSetting(companyId);
        List<OvertimeWorkFrame> frames = otWorkFrameRepo.getAllOvertimeWorkFrame(companyId);
        EnumSet.allOf(FlexWorkAtr.class).forEach(flex -> {
            int count = 0;
            for (int index = 0; index < 3; index++) {
                OvertimeAppAtr ot = EnumAdaptor.valueOf(index, OvertimeAppAtr.class);
                OvertimeQuotaSetUse setting = settings.stream().filter(i -> i.getFlexWorkAtr() == flex && i.getOvertimeAppAtr() == ot).findFirst().orElse(null);
                if (setting != null && !setting.getTargetOvertimeLimit().isEmpty()) {
                    count++;
                    setting.getTargetOvertimeLimit().sort(Comparator.comparing(OverTimeFrameNo::v));
                    for (int row = 0; row < setting.getTargetOvertimeLimit().size(); row++) {
                        OverTimeFrameNo target = setting.getTargetOvertimeLimit().get(row);
                        OvertimeWorkFrame frame = frames.stream().filter(f -> f.getOvertimeWorkFrNo().v().intValue() == target.v()).findFirst().orElse(null);
                        Map<String, MasterCellData> rowData = new HashMap<>();
                        for (int col = 0; col < 3; col++) {
                            String value;
                            if (col == 0 && row == 0 && count == 1) value = flex == FlexWorkAtr.FLEX_TIME ? "フレックス勤務者" : "フレックス勤務者以外";
                            else if (col == 1 && row == 0) value = TextResource.localize(ot.name);
                            else if (col == 2) value = frame != null ? frame.getOvertimeWorkFrName().v() : target.toString();
                            else value = "";
                            rowData.put(
                                    COLUMN_NO_HEADER + col,
                                    MasterCellData.builder()
                                            .columnId(COLUMN_NO_HEADER + col)
                                            .value(value)
                                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                            .build());
                        }
                        data.add(MasterData.builder().rowData(rowData).build());
                    }
                }
            }
        });
        return data;
    }

    private List<MasterData> getVacationMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<HolidayApplicationSetting> applySetting = holidayApplicationSettingRepo.findSettingByCompanyId(companyId);
        Optional<VacationApplicationReflect> reflectSetting = holidayApplicationReflectRepo.findReflectByCompanyId(companyId);
        if (!applySetting.isPresent() || !reflectSetting.isPresent())
            return data;
        applySetting.get().getHolidayApplicationTypeDisplayName().sort(Comparator.comparing(HolidayApplicationTypeDisplayName::getHolidayApplicationType));

        for (int row = 0; row < 17; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 3; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_148");
                else if (col == 0 && row == 8) value = TextResource.localize("KAF022_143");
                else if (col == 0 && row == 9) value = TextResource.localize("KAF022_144");
                else if (col == 0 && row == 16) value = TextResource.localize("KAF022_693");
                else if (col == 1) {
                    if (row == 0) value = TextResource.localize("KAF022_149");
                    else if (row == 1) value = TextResource.localize("KAF022_168");
                    else if (row == 2) value = TextResource.localize("KAF022_158");
                    else if (row == 3) value = TextResource.localize("KAF022_159");
                    else if (row == 4) value = TextResource.localize("KAF022_160");
                    else if (row == 5) value = TextResource.localize("KAF022_726");
                    else if (row == 6) value = TextResource.localize("KAF022_727");
                    else if (row == 7) value = TextResource.localize("KAF022_676");
                    else if (row == 8) value = TextResource.localize("KAF022_154");
                    else if (row == 9) value = TextResource.localize("KAF022_161");
                    else if (row == 10) value = TextResource.localize("KAF022_162");
                    else if (row == 11) value = TextResource.localize("KAF022_163");
                    else if (row == 12) value = TextResource.localize("KAF022_164");
                    else if (row == 13) value = TextResource.localize("KAF022_165");
                    else if (row == 14) value = TextResource.localize("KAF022_166");
                    else if (row == 15) value = TextResource.localize("KAF022_167");
                    else value = TextResource.localize("KAF022_728");
                } else if (col == 2) {
                    if (row == 0) value = reflectSetting.get().getWorkAttendanceReflect().getReflectWorkHour() == ReflectWorkHourCondition.NOT_REFLECT
                            ? TextResource.localize("KAF022_101")
                            : (reflectSetting.get().getWorkAttendanceReflect().getReflectWorkHour() == ReflectWorkHourCondition.REFLECT
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_171"));
                    else if (row == 1) value = reflectSetting.get().getWorkAttendanceReflect().getReflectAttendance() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_420") : TextResource.localize("KAF022_421");
                    else if (row == 2) value = reflectSetting.get().getTimeLeaveReflect().getAnnualVacationTime() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                    else if (row == 3) value = reflectSetting.get().getTimeLeaveReflect().getSuperHoliday60H() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                    else if (row == 4) value = reflectSetting.get().getTimeLeaveReflect().getSubstituteLeaveTime() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                    else if (row == 5) value = reflectSetting.get().getTimeLeaveReflect().getNursing() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                    else if (row == 6) value = reflectSetting.get().getTimeLeaveReflect().getChildNursing() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                    else if (row == 7) value = reflectSetting.get().getTimeLeaveReflect().getSpecialVacationTime() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                    else if (row == 8) value = applySetting.get().getHalfDayAnnualLeaveUsageLimitCheck() == UnregisterableCheckAtr.CHECKED
                            ? TextResource.localize("KAF022_175") : TextResource.localize("KAF022_173");
                    else if (row == 9) value = applySetting.get().getHolidayApplicationTypeDisplayName().get(0).getDisplayName().v();
                    else if (row == 10) value = applySetting.get().getHolidayApplicationTypeDisplayName().get(1).getDisplayName().v();
                    else if (row == 11) value = applySetting.get().getHolidayApplicationTypeDisplayName().get(2).getDisplayName().v();
                    else if (row == 12) value = applySetting.get().getHolidayApplicationTypeDisplayName().get(3).getDisplayName().v();
                    else if (row == 13) value = applySetting.get().getHolidayApplicationTypeDisplayName().get(4).getDisplayName().v();
                    else if (row == 14) value = applySetting.get().getHolidayApplicationTypeDisplayName().get(5).getDisplayName().v();
                    else if (row == 15) value = applySetting.get().getHolidayApplicationTypeDisplayName().get(6).getDisplayName().v();
                    else value = reflectSetting.get().getWorkAttendanceReflect().getOneDayLeaveDeleteAttendance() == NotUseAtr.USE
                                ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                }
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterData> getWorkChangeMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<AppWorkChangeSet> applySetting = appWorkChangeSetRepo.findByCompanyId(companyId);
        Optional<ReflectWorkChangeApp> reflectSetting = appWorkChangeSetRepo.findByCompanyIdReflect(companyId);
        if (!applySetting.isPresent() || !reflectSetting.isPresent())
            return data;

        for (int row = 0; row < 8; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 4; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_520");
                else if (col == 1 && row == 0) value = TextResource.localize("KAF022_565");
                else if (col == 1 && row == 1) value = TextResource.localize("KAF022_540");
                else if (col == 1 && row == 2) value = TextResource.localize("KAF022_567");
                else if (col == 1 && row == 5) value = TextResource.localize("KAF022_570");
                else if (col == 2 && row == 3) value = TextResource.localize("KAF022_568");
                else if (col == 2 && row == 4) value = TextResource.localize("KAF022_569");
                else if (col == 2 && row == 6) value = TextResource.localize("KAF022_568");
                else if (col == 2 && row == 7) value = TextResource.localize("KAF022_569");
                else if (col == 3) {
                    if (row == 0) value = applySetting.get().getInitDisplayWorktimeAtr() == InitDisplayWorktimeAtr.FIXEDTIME
                            ? TextResource.localize("KAF022_391") : TextResource.localize("KAF022_392");
                    else if (row == 1) value = reflectSetting.get().getWhetherReflectAttendance() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_389") : TextResource.localize("KAF022_390");
                    else if (row == 2) value = applySetting.get().getComment1().getComment().v();
                    else if (row == 3) value = applySetting.get().getComment1().getColorCode().v();
                    else if (row == 4) value = applySetting.get().getComment1().isBold() ? CHECK : NOT_CHECK;
                    else if (row == 5) value = applySetting.get().getComment2().getComment().v();
                    else if (row == 6) value = applySetting.get().getComment2().getColorCode().v();
                    else value = applySetting.get().getComment2().isBold() ? CHECK : NOT_CHECK;
                }
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build()
                                        .horizontalAlign(ColumnTextAlign.LEFT)
                                        .backgroundColor(col == 3 && (row == 3 || row == 6) ? value : "#ffffff"))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterData> getBusinessTripMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<AppTripRequestSet> setting = appTripRequestSetRepo.findById(companyId);
        if (!setting.isPresent())
            return data;

        for (int row = 0; row < 3; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 4; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_189");
                else if (col == 1 && row == 0) value = TextResource.localize("KAF022_567");
                else if (col == 2 && row == 1) value = TextResource.localize("KAF022_568");
                else if (col == 2 && row == 2) value = TextResource.localize("KAF022_569");
                else if (col == 3) {
                    if (row == 0) value = setting.get().getComment().getComment().v();
                    else if (row == 1) value = setting.get().getComment().getColorCode().v();
                    else value = setting.get().getComment().isBold() ? CHECK : NOT_CHECK;
                }
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build()
                                        .horizontalAlign(ColumnTextAlign.LEFT)
                                        .backgroundColor(col == 3 && row == 1 ? value : "#ffffff"))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterData> getDirectGoBackMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<GoBackReflect> setting = goBackReflectRepo.findByCompany(companyId);
        if (!setting.isPresent())
            return data;

        Map<String, MasterCellData> rowData = new HashMap<>();
        for (int col = 0; col < 3; col++) {
            String value;
            if (col == 0) value = TextResource.localize("KAF022_520");
            else if (col == 1) value = TextResource.localize("KAF022_573");
            else {
                if (setting.get().getReflectApplication() == ApplicationStatus.DO_REFLECT_1)
                    value = TextResource.localize("KAF022_198");
                else if (setting.get().getReflectApplication() == ApplicationStatus.DO_NOT_REFLECT_1)
                    value = TextResource.localize("KAF022_199");
                else if (setting.get().getReflectApplication() == ApplicationStatus.DO_REFLECT)
                    value = TextResource.localize("KAF022_200");
                else value = TextResource.localize("KAF022_201");
            };
            rowData.put(
                    COLUMN_NO_HEADER + col,
                    MasterCellData.builder()
                            .columnId(COLUMN_NO_HEADER + col)
                            .value(value)
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
        }
        data.add(MasterData.builder().rowData(rowData).build());
        return data;
    }

    private List<MasterData> getHolidayWorkMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<HolidayWorkAppSet> applySetting = holidayWorkAppSetRepo.findSettingByCompany(companyId);
        Optional<HdWorkAppReflect> optionalReflectSetting = hdWorkReflectRepo.findReflectByCompany(companyId);
        if (!applySetting.isPresent() || !optionalReflectSetting.isPresent())
            return data;
        HdWorkAppReflect reflectSetting = optionalReflectSetting.get();

        for (int row = 0; row < 20; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 4; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_520");
                else if (col == 0 && row == 1) value = TextResource.localize("KAF022_526");
                else if (col == 0 && row == 2) value = TextResource.localize("KAF022_531");
                else if (col == 0 && row == 9) value = TextResource.localize("KAF022_284");
                else if (col == 0 && row == 15) value = TextResource.localize("KAF022_417");
                else if (col == 1 && row == 0) value = TextResource.localize("KAF022_724");
                else if (col == 1 && row == 1) value = TextResource.localize("KAF022_530");
                else if (col == 1 && row == 2) value = TextResource.localize("KAF022_580");
                else if (col == 1 && row == 3) value = TextResource.localize("KAF022_532");
                else if (col == 1 && row == 4) value = TextResource.localize("KAF022_533");
                else if (col == 1 && row == 5) value = TextResource.localize("KAF022_534");
                else if (col == 1 && row == 6) value = TextResource.localize("KAF022_535");
                else if (col == 1 && row == 7) value = TextResource.localize("KAF022_711");
                else if (col == 1 && row == 8) value = TextResource.localize("KAF022_713");
                else if (col == 1 && row == 9) value = TextResource.localize("KAF022_641");
                else if (col == 1 && row == 10) value = TextResource.localize("KAF022_714");
                else if (col == 1 && row == 11) value = TextResource.localize("KAF022_715");
                else if (col == 1 && row == 12) value = TextResource.localize("KAF022_308");
                else if (col == 1 && row == 13) value = TextResource.localize("KAF022_290");
                else if (col == 1 && row == 14) value = TextResource.localize("KAF022_716");
                else if (col == 1 && row == 15) value = TextResource.localize("KAF022_622");
                else if (col == 1 && row == 16) value = TextResource.localize("KAF022_611");
                else if (col == 2 && row == 15) value = TextResource.localize("KAF022_725");
                else if (col == 2 && row == 16) value = TextResource.localize("KAF022_720");
                else if (col == 2 && row == 17) value = TextResource.localize("KAF022_719");
                else if (col == 2 && row == 18) value = TextResource.localize("KAF022_721");
                else if (col == 2 && row == 19) value = TextResource.localize("KAF022_773");
//                else if (col == 2 && row == 20) value = TextResource.localize("KAF022_723");
                else if (col == 3 && row == 0) value = applySetting.get().isUseDirectBounceFunction()
                        ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                else if (col == 3 && row == 1) value = applySetting.get().getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                else if (col == 3 && row == 2) value = applySetting.get().getCalcStampMiss() == CalcStampMiss.CAN_NOT_REGIS
                        ? TextResource.localize("KAF022_173") : TextResource.localize("KAF022_175");
                else if (col == 3 && row == 3) value = applySetting.get().getOvertimeLeaveAppCommonSet().getPreExcessDisplaySetting() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_174") : TextResource.localize("KAF022_173");
                else if (col == 3 && row == 4) value = applySetting.get().getOvertimeLeaveAppCommonSet().getPerformanceExcessAtr() == AppDateContradictionAtr.NOTCHECK
                        ? TextResource.localize("KAF022_173")
                        : applySetting.get().getOvertimeLeaveAppCommonSet().getPerformanceExcessAtr() == AppDateContradictionAtr.CHECKREGISTER
                                ? TextResource.localize("KAF022_174") : TextResource.localize("KAF022_175");
                else if (col == 3 && row == 5) value = applySetting.get().getOvertimeLeaveAppCommonSet().getOverrideSet() == OverrideSet.SYSTEM_TIME_PRIORITY
                        ? TextResource.localize("KAF022_709") : TextResource.localize("KAF022_710");
                else if (col == 3 && row == 6) value = applySetting.get().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr() == Time36AgreeCheckRegister.NOT_CHECK
                        ? TextResource.localize("KAF022_173")
                        : applySetting.get().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr() == Time36AgreeCheckRegister.CHECK
                                ? TextResource.localize("KAF022_175") : TextResource.localize("KAF022_651");
                else if (col == 3 && row == 7) value = applySetting.get().getOvertimeLeaveAppCommonSet().getCheckOvertimeInstructionRegister() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_175") : TextResource.localize("KAF022_173");
                else if (col == 3 && row == 8) value = applySetting.get().getOvertimeLeaveAppCommonSet().getCheckDeviationRegister() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_175") : TextResource.localize("KAF022_173");
                else if (col == 3 && row == 9) value = applySetting.get().getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                else if (col == 3 && row == 10) {
                    if (applySetting.get().getApplicationDetailSetting().getAtworkTimeBeginDisp() == AtWorkAtr.NOTDISPLAY)
                        value = TextResource.localize("KAF022_37");
                    else if (applySetting.get().getApplicationDetailSetting().getAtworkTimeBeginDisp() == AtWorkAtr.DISPLAY)
                        value = TextResource.localize("KAF022_301");
                    else if (applySetting.get().getApplicationDetailSetting().getAtworkTimeBeginDisp() == AtWorkAtr.AT_START_WORK_OFF_PERFORMANCE)
                        value = TextResource.localize("KAF022_302");
                    else value = TextResource.localize("KAF022_303");
                }
                else if (col == 3 && row == 11) value = applySetting.get().getApplicationDetailSetting().isDispSystemTimeWhenNoWorkTime()
                        ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                else if (col == 3 && row == 12) value = applySetting.get().getApplicationDetailSetting().getTimeInputUse() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                else if (col == 3 && row == 13) value = applySetting.get().getApplicationDetailSetting().getRequiredInstruction()
                        ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                else if (col == 3 && row == 14) value = applySetting.get().getApplicationDetailSetting().getPreRequireSet() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                else if (col == 3 && row == 15) value = reflectSetting.getBefore().getReflectActualHolidayWorkAtr() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                else if (col == 3 && row == 16) value = reflectSetting.getAfter().getWorkReflect() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                else if (col == 3 && row == 17) value = reflectSetting.getAfter().getBreakLeaveApplication().getBreakReflectAtr() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                else if (col == 3 && row == 18) value = reflectSetting.getAfter().getOthersReflect().getReflectDivergentReasonAtr() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                else if (col == 3 && row == 19) value = reflectSetting.getAfter().getOthersReflect().getReflectPaytimeAtr() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
//                else if (col == 3 && row == 20) value = TextResource.localize("KAF022_723");
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterData> getTimeLeaveMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<TimeLeaveApplicationReflect> setting = timeLeaveAppReflectRepo.findByCompany(companyId);
        if (!setting.isPresent())
            return data;

        for (int row = 0; row < 13; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 3; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_228");
                else if (col == 0 && row == 6) value = TextResource.localize("KAF022_753");
                else if (col == 0 && row == 12) value = TextResource.localize("KAF022_766");
                else if (col == 1) {
                    if (row == 0) value = TextResource.localize("KAF022_232");
                    else if (row == 1) value = TextResource.localize("KAF022_233");
                    else if (row == 2) value = TextResource.localize("KAF022_231");
                    else if (row == 3) value = TextResource.localize("KAF022_674");
                    else if (row == 4) value = TextResource.localize("KAF022_675");
                    else if (row == 5) value = TextResource.localize("KAF022_676");
                    else if (row == 6) value = TextResource.localize("KAF022_234");
                    else if (row == 7) value = TextResource.localize("KAF022_235");
                    else if (row == 8) value = TextResource.localize("KAF022_236");
                    else if (row == 9) value = TextResource.localize("KAF022_237");
                    else if (row == 10) value = TextResource.localize("KAF022_677");
                    else if (row == 11) value = TextResource.localize("KAF022_678");
                    else value = TextResource.localize("KAF022_767");
                } else if (col == 2) {
                    if (row == 0) value = setting.get().getCondition().getSuperHoliday60H() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 1) value = setting.get().getCondition().getSubstituteLeaveTime() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 2) value = setting.get().getCondition().getAnnualVacationTime() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 3) value = setting.get().getCondition().getChildNursing() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 4) value = setting.get().getCondition().getNursing() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 5) value = setting.get().getCondition().getSpecialVacationTime() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (row == 6) value = setting.get().getDestination().getFirstBeforeWork() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else if (row == 7) value = setting.get().getDestination().getFirstAfterWork() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else if (row == 8) value = setting.get().getDestination().getSecondBeforeWork() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else if (row == 9) value = setting.get().getDestination().getSecondAfterWork() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else if (row == 10) value = setting.get().getDestination().getPrivateGoingOut() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else if (row == 11) value = setting.get().getDestination().getUnionGoingOut() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else value = setting.get().getReflectActualTimeZone() == NotUseAtr.USE
                                ? TextResource.localize("KAF022_44") : TextResource.localize("KAF022_396");
                }
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterData> getLateEarlyMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        LateEarlyCancelAppSet applySetting = lateEarlyCancelRepo.getByCId(companyId);
        LateEarlyCancelReflect reflectSetting = lateEarlyCancelReflectRepo.getByCompanyId(companyId);
        if (applySetting == null || reflectSetting == null)
            return data;

        for (int row = 0; row < 2; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 3; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_520");
                else if (col == 1 && row == 0) value = TextResource.localize("KAF022_669");
                else if (col == 1) value = TextResource.localize("KAF022_670");
                else if (col == 2 && row == 0) value = reflectSetting.isClearLateReportWarning()
                        ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                else if (col == 2 && applySetting.getCancelAtr() == CancelAtr.NOT_USE) value =  TextResource.localize("KAF022_671");
                else if (col == 2 && applySetting.getCancelAtr() == CancelAtr.USE_NOT_CHECK) value = TextResource.localize("KAF022_672");
                else if (col == 2) value = TextResource.localize("KAF022_673");
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterData> getStampMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<AppStampSetting> applySetting = appStampSettingRepo.findSettingByCompanyId(companyId);
        Optional<StampAppReflect> reflectSetting = stampAppReflectRepo.findReflectByCompanyId(companyId);
        if (!applySetting.isPresent() || !reflectSetting.isPresent())
            return data;
        String[] typeNames = {
                TextResource.localize("KAF022_246"),
                TextResource.localize("KAF022_247"),
                TextResource.localize("KAF022_698"),
                TextResource.localize("KAF022_700"),
                TextResource.localize("KAF022_699"),
                TextResource.localize("KAF022_697"),
                TextResource.localize("KAF022_701")
        };

        Map<String, MasterCellData> rowData1 = new HashMap<>();
        for (int col = 0; col < 4; col++) {
            String value;
            if (col == 0) value = TextResource.localize("KAF022_242");
            else if (col == 1) value = TextResource.localize("KAF022_695");
            else if (col == 3) value = applySetting.get().getUseCancelFunction() == UseDivision.TO_USE
                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
            else value = "";
            rowData1.put(
                    COLUMN_NO_HEADER + col,
                    MasterCellData.builder()
                            .columnId(COLUMN_NO_HEADER + col)
                            .value(value)
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
        }
        data.add(MasterData.builder().rowData(rowData1).build());
        EnumSet.allOf(StampAtr.class).forEach(stamp -> {
            SettingForEachType settingForEachType = applySetting.get().getSettingForEachTypeLst().stream().filter(i -> i.getStampAtr() == stamp).findFirst().orElse(null);
            int rowSize;
            if (stamp == StampAtr.ATTENDANCE_RETIREMENT || stamp == StampAtr.SUPPORT_IN_SUPPORT_OUT) rowSize = 8;
            else if (stamp == StampAtr.GOING_OUT_RETURNING) rowSize = 11;
            else if (stamp == StampAtr.RECORDER_IMAGE) rowSize = 6;
            else rowSize = 7;
            for (int row = 0; row < rowSize; row++) {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < 4; col++) {
                    String value;
                    if (col == 0 && row == 0) value = typeNames[stamp.value];
                    else if (col == 1 && row == rowSize - 6) value = TextResource.localize("KAF022_256");
                    else if (col == 1 && row == rowSize - 3) value = TextResource.localize("KAF022_257");
                    else if (col == 1) {
                        if (stamp == StampAtr.ATTENDANCE_RETIREMENT) {
                            if (row == 0) value = TextResource.localize("KAF022_703");
                            else if (row == 1) value = TextResource.localize("KAF022_702");
                            else value = "";
                        } else if (stamp == StampAtr.GOING_OUT_RETURNING) {
                            if (row == 0) value = TextResource.localize("KAF022_696");
                            else if (row == 1) value = TextResource.localize("KAF022_251");
                            else value = "";
                        } else if (stamp == StampAtr.SUPPORT_IN_SUPPORT_OUT) {
                            if (row == 0) value = TextResource.localize("KAF022_696");
                            else if (row == 1) value = TextResource.localize("KAF022_243");
                            else value = "";
                        } else if (row == 0) value = TextResource.localize("KAF022_696");
                        else value = "";
                    }

                    else if (col == 2 && row == rowSize - 5) value = TextResource.localize("KAF022_568");
                    else if (col == 2 && row == rowSize - 4) value = TextResource.localize("KAF022_569");
                    else if (col == 2 && row == rowSize - 2) value = TextResource.localize("KAF022_568");
                    else if (col == 2 && row == rowSize - 1) value = TextResource.localize("KAF022_569");
                    else if (col == 2 && stamp == StampAtr.GOING_OUT_RETURNING) {
                        if (row == 1) value = TextResource.localize("KAF022_252");
                        else if (row == 2) value = TextResource.localize("KAF022_253");
                        else if (row == 3) value = TextResource.localize("KAF022_254");
                        else if (row == 4) value = TextResource.localize("KAF022_255");
                        else value = "";
                    }

                    else if (col == 3 && row == rowSize - 6) value = settingForEachType.getTopComment().getComment().v();
                    else if (col == 3 && row == rowSize - 5) value = settingForEachType.getTopComment().getColorCode().v();
                    else if (col == 3 && row == rowSize - 4) value = settingForEachType.getTopComment().isBold() ? CHECK : NOT_CHECK;
                    else if (col == 3 && row == rowSize - 3) value = settingForEachType.getBottomComment().getComment().v();
                    else if (col == 3 && row == rowSize - 2) value = settingForEachType.getBottomComment().getColorCode().v();
                    else if (col == 3 && row == rowSize - 1) value = settingForEachType.getBottomComment().isBold() ? CHECK : NOT_CHECK;
                    else if (col == 3) {
                        if (stamp == StampAtr.ATTENDANCE_RETIREMENT) {
                            if (row == 0) value = reflectSetting.get().getWorkReflectAtr() == NotUseAtr.USE
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else if (row == 1) value = reflectSetting.get().getExtraWorkReflectAtr() == NotUseAtr.USE
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else value = "";
                        } else if (stamp == StampAtr.GOING_OUT_RETURNING) {
                            if (row == 0) value = reflectSetting.get().getGoOutReflectAtr() == NotUseAtr.USE
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else if (row == 1) value = applySetting.get().getGoOutTypeDispControl().get(0).getDisplay() == DisplayAtr.DISPLAY
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else if (row == 2) value = applySetting.get().getGoOutTypeDispControl().get(1).getDisplay() == DisplayAtr.DISPLAY
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else if (row == 3) value = applySetting.get().getGoOutTypeDispControl().get(2).getDisplay() == DisplayAtr.DISPLAY
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else if (row == 4) value = applySetting.get().getGoOutTypeDispControl().get(3).getDisplay() == DisplayAtr.DISPLAY
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else value = "";
                        } else if (stamp == StampAtr.CHILDCARE_OUT_RETURN) {
                            if (row == 0) value = reflectSetting.get().getChildCareReflectAtr() == NotUseAtr.USE
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else value = "";
                        } else if (stamp == StampAtr.SUPPORT_IN_SUPPORT_OUT) {
                            if (row == 0) value = reflectSetting.get().getSupportReflectAtr() == NotUseAtr.USE
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else if (row == 1) value = applySetting.get().getSupportFrameDispNO().v() + " " + TextResource.localize("KAF022_755");
                            else value = "";
                        } else if (stamp == StampAtr.OUT_OF_CARE_RETURN_OF_CARE) {
                            if (row == 0) value = reflectSetting.get().getCareReflectAtr() == NotUseAtr.USE
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else value = "";
                        } else if (stamp == StampAtr.BREAK) {
                            if (row == 0) value = reflectSetting.get().getBreakReflectAtr() == NotUseAtr.USE
                                    ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                            else value = "";
                        } else value = "";
                    }
                    else value = "";
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(value)
                                    .style(MasterCellStyle.build()
                                            .horizontalAlign(ColumnTextAlign.LEFT)
                                            .backgroundColor(col == 3 && (row == (rowSize - 5) || (row == rowSize - 2)) ? value : "#ffffff")
                                    )
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            }
        });
        return data;
    }

    private List<MasterData> getSubstituteMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<SubstituteHdWorkAppSet> applySetting = substituteHdWorkAppSetRepo.findSettingByCompany(companyId);
        Optional<SubstituteLeaveAppReflect> subLeaveReflectSetting = substituteLeaveAppReflectRepo.findSubLeaveAppReflectByCompany(companyId);
        Optional<SubstituteWorkAppReflect> subWorkReflectSetting = substituteWorkAppReflectRepo.findSubWorkAppReflectByCompany(companyId);
        if (!applySetting.isPresent() || !subLeaveReflectSetting.isPresent() || !subWorkReflectSetting.isPresent())
            return data;

        for (int row = 0; row < 11; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 4; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_752");
                else if (col == 0 && row == 4) value = TextResource.localize("KAF022_692");
                else if (col == 0 && row == 9) value = TextResource.localize("KAF022_531");
                else if (col == 0 && row == 10) value = TextResource.localize("KAF022_693");
                else if (col == 1 && row == 0) value = TextResource.localize("KAF022_540");
                else if (col == 1 && row == 1) value = TextResource.localize("KAF022_583");
                else if (col == 1 && row == 4) value = TextResource.localize("KAF022_538");
                else if (col == 1 && row == 5) value = TextResource.localize("KAF022_540");
                else if (col == 1 && row == 6) value = TextResource.localize("KAF022_582");
                else if (col == 1 && row == 9) value = TextResource.localize("KAF022_584");
//                else if (col == 1 && row == 10) value = TextResource.localize("KAF022_585");
                else if (col == 1 && row == 10) value = TextResource.localize("KAF022_728");
                else if (col == 2 && row == 2) value = TextResource.localize("KAF022_568");
                else if (col == 2 && row == 3) value = TextResource.localize("KAF022_569");
                else if (col == 2 && row == 7) value = TextResource.localize("KAF022_568");
                else if (col == 2 && row == 8) value = TextResource.localize("KAF022_569");
                else if (col == 3 && row == 0) value = subWorkReflectSetting.get().getReflectAttendanceAtr() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_389") : TextResource.localize("KAF022_390");
                else if (col == 3 && row == 1) value = applySetting.get().getSubstituteWorkSetting().getComment().getComment().v();
                else if (col == 3 && row == 2) value = applySetting.get().getSubstituteWorkSetting().getComment().getColorCode().v();
                else if (col == 3 && row == 3) value = applySetting.get().getSubstituteWorkSetting().getComment().isBold() ? CHECK : NOT_CHECK;
                else if (col == 3 && row == 4) value = subLeaveReflectSetting.get().getWorkInfoAttendanceReflect().getReflectWorkHour() == ReflectWorkHourCondition.NOT_REFLECT
                        ? TextResource.localize("KAF022_101")
                        : subLeaveReflectSetting.get().getWorkInfoAttendanceReflect().getReflectWorkHour() == ReflectWorkHourCondition.REFLECT
                                ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_171");
                else if (col == 3 && row == 5) value = subLeaveReflectSetting.get().getWorkInfoAttendanceReflect().getReflectAttendance() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_420") : TextResource.localize("KAF022_421");
                else if (col == 3 && row == 6) value = applySetting.get().getSubstituteHolidaySetting().getComment().getComment().v();
                else if (col == 3 && row == 7) value = applySetting.get().getSubstituteHolidaySetting().getComment().getColorCode().v();
                else if (col == 3 && row == 8) value = applySetting.get().getSubstituteHolidaySetting().getComment().isBold() ? CHECK : NOT_CHECK;
                else if (col == 3 && row == 9) value = applySetting.get().getSimultaneousSetting().isSimultaneousApplyRequired()
                        ? TextResource.localize("KAF022_292") : TextResource.localize("KAF022_291");
                else if (col == 3 && row == 10) value = subLeaveReflectSetting.get().getWorkInfoAttendanceReflect().getOneDayLeaveDeleteAttendance() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_389") : TextResource.localize("KAF022_390");
//                else if (col == 3 && row == 11) value = TextResource.localize("KAF022_569");
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT).backgroundColor(col == 3 && (row == 2 || row == 7) ? value : "#ffffff"))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterHeaderColumn> getOptionalItemHeaderColumns() {
        return Arrays.asList(
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 0,
                        TextResource.localize("KAF022_681"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                ),
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 1,
                        TextResource.localize("KAF022_629"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                ),
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 2,
                        TextResource.localize("KAF022_99"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                ),
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 3,
                        TextResource.localize("KAF022_685"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                ),
                new MasterHeaderColumn(
                        COLUMN_NO_HEADER + 4,
                        TextResource.localize("KAF022_754"),
                        ColumnTextAlign.LEFT,
                        "",
                        true
                )
        );
    }

    private List<MasterData> getOptionalItemMasterData() {
        String companyId = AppContexts.user().companyId();
        List<OptionalItem> optionalItems = optionalItemRepo.findAll(companyId);
        List<OptionalItemApplicationSetting> settings = optionalItemAppSetRepo.findByCompany(companyId);
        settings.sort(Comparator.comparing(OptionalItemApplicationSetting::getCode));
        List<MasterData> data = new ArrayList<>();
        settings.forEach(setting -> {
            for (int row = 0; row < setting.getSettingItems().size(); row++) {
                ApplicationSettingItem item = setting.getSettingItems().get(row);
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < 5; col++) {
                    String value;
                    if (col == 0 && row == 0) value = setting.getCode().v();
                    else if (col == 1 && row == 0) value = setting.getName().v();
                    else if (col == 2 && row == 0) value = setting.isUseAtr() ? CHECK : NOT_CHECK;
                    else if (col == 3 && row == 0) value = setting.getDescription().map(PrimitiveValue::v).orElse("");
                    else if (col == 4) value = item.getOptionalItemNo().v().toString()
                            + "  "
                            + optionalItems.stream().filter(i -> i.getOptionalItemNo().v() == item.getOptionalItemNo().v()).findFirst().map(i -> i.getOptionalItemName().v()).orElse("");
                    else value = "";
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(value)
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            }
        });
        return data;
    }

    private List<MasterData> getApprovalDispMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<ApprovalListDisplaySetting> setting = approvalListDispSetRepo.findByCID(companyId);
        if (!setting.isPresent()) return data;

        for (int row = 0; row < 5; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 3; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_587");
                else if (col == 0 && row == 2) value = TextResource.localize("KAF022_589");
                else if (col == 1 && row == 0) value = TextResource.localize("KAF022_588");
                else if (col == 1 && row == 1) value = TextResource.localize("KAF022_590");
                else if (col == 1 && row == 2) value = TextResource.localize("KAF022_591");
                else if (col == 1 && row == 3) value = TextResource.localize("KAF022_595");
                else if (col == 1 && row == 4) value = TextResource.localize("KAF022_599");
                else if (col == 2 && row == 0) value = setting.get().getDisplayWorkPlaceName() == NotUseAtr.USE
                        ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                else if (col == 2 && row == 1) value = setting.get().getAppReasonDisAtr() == DisplayAtr.DISPLAY
                        ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                else if (col == 2 && row == 2) value = setting.get().getWarningDateDisAtr().toString() + TextResource.localize("KAF022_600");
                else if (col == 2 && row == 3) value = setting.get().getAdvanceExcessMessDisAtr() == DisplayAtr.DISPLAY
                        ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                else if (col == 2 && row == 4) value = setting.get().getActualExcessMessDisAtr() == DisplayAtr.DISPLAY
                        ? TextResource.localize("KAF022_36") : TextResource.localize("KAF022_37");
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterData> getMailMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        Optional<ApplicationSetting> applicationSetting = appSettingRepo.findByCompanyId(companyId);
        AppEmailSet setting = appEmailSetRepo.findByCID(companyId);
        if (setting == null || !applicationSetting.isPresent())
            return data;
        List<AppTypeSetting> appTypeSettings = applicationSetting.get().getAppTypeSettings();
        appTypeSettings.sort(Comparator.comparing(AppTypeSetting::getAppType));

        for (int i = 0; i < appTypeSettings.size(); i++) {
            for (int row = 0; row < 2; row++) {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < 5; col++) {
                    String value;
                    if (col == 0 && row == 0 && i == 0) value = TextResource.localize("KAF022_464");

                    else if (col == 1 && row == 0) value = appTypeSettings.get(i).getAppType().name;

                    else if (col == 2 && row == 0) value = TextResource.localize("KAF022_71");
                    else if (col == 2) value = TextResource.localize("KAF022_72");

                    else if (col == 3) value = TextResource.localize("KAF022_481");

                    else if (col == 4 && row == 0) value = appTypeSettings.get(i).isSendMailWhenRegister()
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");
                    else if (col == 4) value = appTypeSettings.get(i).isSendMailWhenApproval()
                            ? TextResource.localize("KAF022_75") : TextResource.localize("KAF022_82");

                    else value = "";
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(value)
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            }
        }

        for (int row = 0; row < 9; row++) {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < 5; col++) {
                String value;
                if (col == 0 && row == 0) value = TextResource.localize("KAF022_110");

                else if (col == 1) {
                    if (row == 0) value = TextResource.localize("KAF022_494");
                    else if (row == 1) value = TextResource.localize("KAF022_370");
                    else if (row == 2) value = TextResource.localize("KAF022_111");
                    else if (row == 3) value = TextResource.localize("KAF022_112");
                    else if (row == 4) value = TextResource.localize("KAF022_113");
                    else if (row == 5) value = TextResource.localize("KAF022_114");
                    else if (row == 6) value = TextResource.localize("KAF022_116");
                    else if (row == 7) value = TextResource.localize("KAF022_368");
                    else value = TextResource.localize("KAF022_369");
                }

                else if (col == 4) {
                    if (row == 0) value = applicationSetting.get().getAppDisplaySetting().getManualSendMailAtr() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_75") : TextResource.localize("82");
                    else if (row == 1) value = setting.getUrlReason() == NotUseAtr.USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("101");
                    else if (row == 2) value = setting.getEmailContentLst().stream().filter(i -> i.getDivision() == Division.HOLIDAY_WORK_INSTRUCTION).findFirst()
                            .map(i -> i.getOpEmailSubject().map(PrimitiveValueBase::v).orElse("")).orElse("");
                    else if (row == 3) value = setting.getEmailContentLst().stream().filter(i -> i.getDivision() == Division.HOLIDAY_WORK_INSTRUCTION).findFirst()
                            .map(i -> i.getOpEmailText().map(PrimitiveValueBase::v).orElse("")).orElse("");
                    else if (row == 4) value = setting.getEmailContentLst().stream().filter(i -> i.getDivision() == Division.OVERTIME_INSTRUCTION).findFirst()
                            .map(i -> i.getOpEmailSubject().map(PrimitiveValueBase::v).orElse("")).orElse("");
                    else if (row == 5) value = setting.getEmailContentLst().stream().filter(i -> i.getDivision() == Division.OVERTIME_INSTRUCTION).findFirst()
                            .map(i -> i.getOpEmailText().map(PrimitiveValueBase::v).orElse("")).orElse("");
                    else if (row == 6) value = setting.getEmailContentLst().stream().filter(i -> i.getDivision() == Division.APPLICATION_APPROVAL).findFirst()
                            .map(i -> i.getOpEmailText().map(PrimitiveValueBase::v).orElse("")).orElse("");
                    else if (row == 7) value = setting.getEmailContentLst().stream().filter(i -> i.getDivision() == Division.REMAND).findFirst()
                            .map(i -> i.getOpEmailSubject().map(PrimitiveValueBase::v).orElse("")).orElse("");
                    else value = setting.getEmailContentLst().stream().filter(i -> i.getDivision() == Division.REMAND).findFirst()
                                .map(i -> i.getOpEmailText().map(PrimitiveValueBase::v).orElse("")).orElse("");
                }
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        }
        return data;
    }

    private List<MasterHeaderColumn> getEmploymentHeaderColumns() {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        for (int i = 0; i < EMPLOYMENT_COL_SIZE; i++) {
            String columnText;
            if (i == 0) columnText = TextResource.localize("KAF022_628");
            else if (i == 1) columnText = TextResource.localize("KAF022_629");
            else if (i == 2) columnText = TextResource.localize("KAF022_454");
            else if (i == 6) columnText = TextResource.localize("KAF022_455");
            else columnText = "";
            columns.add(new MasterHeaderColumn(
                    COLUMN_NO_HEADER + i,
                    columnText,
                    ColumnTextAlign.LEFT,
                    "",
                    true
            ));
        }
        return columns;
    }

    private List<MasterData> getEmploymentMasterData() {
        String companyId = AppContexts.user().companyId();
        List<Employment> employments = empRepo.findAll(companyId);
        List<WorkTypeDto> workTypes = workTypeQueryRepository.findAllWorkType(companyId);
        List<AppEmploymentSet> settings = appEmploymentSetRepo.findByCompanyID(companyId);
        settings.sort(Comparator.comparing(AppEmploymentSet::getEmploymentCD));
        List<MasterData> data = new ArrayList<>();
        settings.forEach(setting -> {
            EnumSet.allOf(HolidayAppType.class).forEach(hdAppType -> {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < EMPLOYMENT_COL_SIZE; col++) {
                    String value;
                    if (col == 0 && hdAppType == HolidayAppType.ANNUAL_PAID_LEAVE) value = setting.getEmploymentCD();
                    else if (col == 1 && hdAppType == HolidayAppType.ANNUAL_PAID_LEAVE)
                        value = employments.stream()
                                .filter(i -> i.getEmploymentCode().v().equals(setting.getEmploymentCD()))
                                .findFirst()
                                .map(i -> i.getEmploymentName().v()).orElse("");
                    else if (col == 2 && hdAppType == HolidayAppType.ANNUAL_PAID_LEAVE) value = TextResource.localize("KAF022_729");
                    else if (col == 3) value = "【" + hdAppType.name + "】";
                    else if (col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == hdAppType).findFirst()
                            .map(i -> i.getOpHolidayTypeUse().get() ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101"))
                            .orElse("");
                    else value = "";
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(value)
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            });
            for (int row = 0; row < 30; row++) {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < EMPLOYMENT_COL_SIZE; col++) {
                    String value;
                    if (row == 0 && col == 2) value = TextResource.localize("KAF022_630");

                    else if (row == 0 && col == 3) value = ApplicationType.OVER_TIME_APPLICATION.name;
                    else if (row == 0 && col == 4) value = TextResource.localize("KAF022_468");
                    else if (row == 0 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.OVER_TIME_APPLICATION).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 1 && col == 4) value = TextResource.localize("KAF022_739");
                    else if (row == 1 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                                .filter(i -> i.getAppType() == ApplicationType.OVER_TIME_APPLICATION).findFirst()
                                .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                    WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                    return j + (wt != null ? wt.getName() : "");
                                }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 2 && col == 3) value = ApplicationType.ABSENCE_APPLICATION.name;
                    else if (row == 2 && col == 4) value = "【" + HolidayAppType.ANNUAL_PAID_LEAVE.name + "】";
                    else if (row == 2 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 2 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.ANNUAL_PAID_LEAVE)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 3 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 3 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.ANNUAL_PAID_LEAVE)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 4 && col == 4) value = "【" + HolidayAppType.SUBSTITUTE_HOLIDAY.name + "】";
                    else if (row == 4 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 4 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.SUBSTITUTE_HOLIDAY)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 5 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 5 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.SUBSTITUTE_HOLIDAY)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 6 && col == 4) value = "【" + HolidayAppType.ABSENCE.name + "】";
                    else if (row == 6 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 6 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.ABSENCE)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 7 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 7 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.ABSENCE)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 8 && col == 4) value = "【" + HolidayAppType.SPECIAL_HOLIDAY.name + "】";
                    else if (row == 8 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 8 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.SPECIAL_HOLIDAY)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 9 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 9 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.SPECIAL_HOLIDAY)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 10 && col == 4) value = "【" + HolidayAppType.YEARLY_RESERVE.name + "】";
                    else if (row == 10 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 10 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.YEARLY_RESERVE)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 11 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 11 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.YEARLY_RESERVE)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 12 && col == 4) value = "【" + HolidayAppType.HOLIDAY.name + "】";
                    else if (row == 12 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 12 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.HOLIDAY)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 13 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 13 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.HOLIDAY)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 14 && col == 4) value = "【" + HolidayAppType.DIGESTION_TIME.name + "】";
                    else if (row == 14 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 14 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.DIGESTION_TIME)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 15 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 15 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.ABSENCE_APPLICATION && i.getOpHolidayAppType().isPresent() && i.getOpHolidayAppType().get() == HolidayAppType.DIGESTION_TIME)
                            .findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 16 && col == 3) value = ApplicationType.WORK_CHANGE_APPLICATION.name;
                    else if (row == 16 && col == 4) value = TextResource.localize("KAF022_468");
                    else if (row == 16 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.WORK_CHANGE_APPLICATION).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 17 && col == 4) value = TextResource.localize("KAF022_739");
                    else if (row == 17 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.WORK_CHANGE_APPLICATION).findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 18 && col == 3) value = ApplicationType.BUSINESS_TRIP_APPLICATION.name;
                    else if (row == 18 && col == 4) value = TextResource.localize("KAF022_737");
                    else if (row == 18 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 18 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.BUSINESS_TRIP_APPLICATION && i.getOpBusinessTripAppWorkType().isPresent() && i.getOpBusinessTripAppWorkType().get() == BusinessTripAppWorkType.WORK_DAY).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 19 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 19 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.BUSINESS_TRIP_APPLICATION && i.getOpBusinessTripAppWorkType().isPresent() && i.getOpBusinessTripAppWorkType().get() == BusinessTripAppWorkType.WORK_DAY).findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 20 && col == 4) value = TextResource.localize("KAF022_738");
                    else if (row == 20 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 20 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.BUSINESS_TRIP_APPLICATION && i.getOpBusinessTripAppWorkType().isPresent() && i.getOpBusinessTripAppWorkType().get() == BusinessTripAppWorkType.HOLIDAY).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 21 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 21 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.BUSINESS_TRIP_APPLICATION && i.getOpBusinessTripAppWorkType().isPresent() && i.getOpBusinessTripAppWorkType().get() == BusinessTripAppWorkType.HOLIDAY).findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 22 && col == 3) value = ApplicationType.GO_RETURN_DIRECTLY_APPLICATION.name;
                    else if (row == 22 && col == 4) value = TextResource.localize("KAF022_468");
                    else if (row == 22 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.GO_RETURN_DIRECTLY_APPLICATION).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 23 && col == 4) value = TextResource.localize("KAF022_739");
                    else if (row == 23 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.GO_RETURN_DIRECTLY_APPLICATION).findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 24 && col == 3) value = ApplicationType.HOLIDAY_WORK_APPLICATION.name;
                    else if (row == 24 && col == 4) value = TextResource.localize("KAF022_468");
                    else if (row == 24 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.HOLIDAY_WORK_APPLICATION).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 25 && col == 4) value = TextResource.localize("KAF022_739");
                    else if (row == 25 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.HOLIDAY_WORK_APPLICATION).findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 26 && col == 3) value = ApplicationType.COMPLEMENT_LEAVE_APPLICATION.name;
                    else if (row == 26 && col == 4) value = TextResource.localize("KAF022_279");
                    else if (row == 26 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 26 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION && i.getOpBreakOrRestTime().isPresent() && i.getOpBreakOrRestTime().get() == BreakOrRestTime.BREAKTIME).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 27 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 27 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION && i.getOpBreakOrRestTime().isPresent() && i.getOpBreakOrRestTime().get() == BreakOrRestTime.BREAKTIME).findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else if (row == 28 && col == 4) value = TextResource.localize("KAF022_553");
                    else if (row == 28 && col == 5) value = TextResource.localize("KAF022_468");
                    else if (row == 28 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION && i.getOpBreakOrRestTime().isPresent() && i.getOpBreakOrRestTime().get() == BreakOrRestTime.RESTTIME).findFirst()
                            .map(i -> i.isDisplayWorkType() ? CHECK : NOT_CHECK).orElse("");
                    else if (row == 29 && col == 5) value = TextResource.localize("KAF022_739");
                    else if (row == 29 && col == 6) value = setting.getTargetWorkTypeByAppLst().stream()
                            .filter(i -> i.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION && i.getOpBreakOrRestTime().isPresent() && i.getOpBreakOrRestTime().get() == BreakOrRestTime.RESTTIME).findFirst()
                            .map(i -> i.isDisplayWorkType() ? String.join(",", i.getWorkTypeLst().stream().map(j -> {
                                WorkTypeDto wt = workTypes.stream().filter(k -> k.getWorkTypeCode().equals(j)).findFirst().orElse(null);
                                return j + (wt != null ? wt.getName() : "");
                            }).collect(Collectors.toList())) : "").orElse("");

                    else value = "";
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(value)
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            }
        });
        return data;
    }

    private List<MasterHeaderColumn> getWorkplaceHeaderColumns() {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        for (int i = 0; i < WORKPLACE_COL_SIZE; i++) {
            String columnText;
            if (i == 0) columnText = TextResource.localize("KAF022_635");
            else if (i == 1) columnText = TextResource.localize("KAF022_636");
            else if (i == 2) columnText = TextResource.localize("KAF022_637");
            else if (i == 3) columnText = TextResource.localize("KAF022_638");
            else columnText = TextResource.localize("KAF022_649");
            columns.add(new MasterHeaderColumn(
                    COLUMN_NO_HEADER + i,
                    columnText,
                    ColumnTextAlign.LEFT,
                    "",
                    true
            ));
        }
        return columns;
    }

    private List<MasterData> getWorkplaceMasterData() {
        List<MasterData> data = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        List<WorkplaceInformation> workplaces = wkpInforRepo.findByCompany(companyId);
        Optional<RequestByCompany> companySetting = requestByCompanyRepo.findByCompanyId(companyId);
        List<RequestByWorkplace> workplaceSettings = requestByWorkplaceRepo.findByCompany(companyId);
        if (!companySetting.isPresent()) return data;

        companySetting.get().getApprovalFunctionSet().getAppUseSetLst().sort(Comparator.comparing(ApplicationUseSetting::getAppType));
        companySetting.get().getApprovalFunctionSet().getAppUseSetLst().forEach(setting -> {
            Map<String, MasterCellData> rowData = new HashMap<>();
            for (int col = 0; col < WORKPLACE_COL_SIZE; col++) {
                String value;
                if (col == 1 && setting.getAppType() == ApplicationType.OVER_TIME_APPLICATION) value = TextResource.localize("Com_Company");
                else if (col == 2) value = setting.getAppType().name;
                else if (col == 3) value = setting.getUseDivision() == UseDivision.TO_USE
                        ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                else if (col == 4) value = setting.getMemo().v();
                else value = "";
                rowData.put(
                        COLUMN_NO_HEADER + col,
                        MasterCellData.builder()
                                .columnId(COLUMN_NO_HEADER + col)
                                .value(value)
                                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                .build());
            }
            data.add(MasterData.builder().rowData(rowData).build());
        });
        workplaceSettings.forEach(wkSetting -> {
            wkSetting.getApprovalFunctionSet().getAppUseSetLst().sort(Comparator.comparing(ApplicationUseSetting::getAppType));
            wkSetting.getApprovalFunctionSet().getAppUseSetLst().forEach(setting -> {
                Map<String, MasterCellData> rowData = new HashMap<>();
                for (int col = 0; col < WORKPLACE_COL_SIZE; col++) {
                    String value;
                    if (col == 0 && setting.getAppType() == ApplicationType.OVER_TIME_APPLICATION)
                        value = workplaces.stream().filter(i -> i.getWorkplaceId().equals(wkSetting.getWorkplaceID())).findFirst()
                                .map(i -> i.getWorkplaceCode().v()).orElse("");
                    else if (col == 1 && setting.getAppType() == ApplicationType.OVER_TIME_APPLICATION)
                        value = workplaces.stream().filter(i -> i.getWorkplaceId().equals(wkSetting.getWorkplaceID())).findFirst()
                                .map(i -> i.getWorkplaceName().v()).orElse("");
                    else if (col == 2) value = setting.getAppType().name;
                    else if (col == 3) value = setting.getUseDivision() == UseDivision.TO_USE
                            ? TextResource.localize("KAF022_100") : TextResource.localize("KAF022_101");
                    else if (col == 4) value = setting.getMemo().v();
                    else value = "";
                    rowData.put(
                            COLUMN_NO_HEADER + col,
                            MasterCellData.builder()
                                    .columnId(COLUMN_NO_HEADER + col)
                                    .value(value)
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                                    .build());
                }
                data.add(MasterData.builder().rowData(rowData).build());
            });
        });
        return data;
    }

}
