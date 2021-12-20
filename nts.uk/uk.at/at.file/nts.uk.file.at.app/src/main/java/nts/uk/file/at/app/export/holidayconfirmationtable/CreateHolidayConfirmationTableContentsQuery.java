package nts.uk.file.at.app.export.holidayconfirmationtable;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffAtWorkplaceImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.UnbalanceCompensation;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SeqVacationAssociationInfo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistItem;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 振休確認表の表示内容を作成する
 */
@Stateless
public class CreateHolidayConfirmationTableContentsQuery {
    @Inject
    private AffCompanyHistRepository affCompanyHistRepo;

    @Inject
    private ClosureRepository closureRepo;

    @Inject
    private ClosureEmploymentRepository closureEmploymentRepo;

    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;

    @Inject
    private RemainNumberTempRequireService requireService;

    /**
     *
     * @param referenceDate
     * @param employeeInfos
     * @param mngUnit 管理単位(1:日数管理/2:時間管理)
     * @param linkingMng 紐付管理(true:管理する/false:管理しない)
     * @param haveMoreHolidayThanDrawOut
     * @param haveMoreDrawOutThanHoliday
     * @param affWkps
     * @param workplaceInfos
     * @return
     */
    public List<HolidayConfirmationTableContent> create(GeneralDate referenceDate,
                                                        List<EmployeeBasicInfoImport> employeeInfos,
                                                        int mngUnit,
                                                        boolean linkingMng,
                                                        boolean haveMoreHolidayThanDrawOut,
                                                        boolean haveMoreDrawOutThanHoliday,
                                                        List<AffAtWorkplaceImport> affWkps,
                                                        List<WorkplaceInfor> workplaceInfos) {
        String companyId = AppContexts.user().companyId();
        List<HolidayConfirmationTableContent> contents = new ArrayList<>();

        List<AffCompanyHistByEmployee> affCompHists = affCompanyHistRepo.getAffEmployeeHistory(employeeInfos.stream().map(EmployeeBasicInfoImport::getSid).collect(Collectors.toList()));

        for (EmployeeBasicInfoImport employee : employeeInfos) {
            List<AffCompanyHistByEmployee> affCompHistsOfEmp = affCompHists.stream()
                    .filter(i -> i.getSId().equals(employee.getSid()))
                    .collect(Collectors.toList());
            if (affCompHistsOfEmp.isEmpty()) continue;

            AffAtWorkplaceImport affWkp = affWkps.stream().filter(i -> i.getEmployeeId().equals(employee.getSid())).findFirst().orElse(null);
            WorkplaceInfor workplace = affWkp == null ? null : workplaceInfos.stream().filter(i -> i.getWorkplaceId().equals(affWkp.getWorkplaceId())).findFirst().orElse(null);

            // 社員に対応する締め期間を取得する
            DatePeriod period = ClosureService.findClosurePeriod(
                    ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter),
                    new CacheCarrier(),
                    employee.getSid(),
                    referenceDate
            );

            // [No204]期間内の振出振休残数を取得する
            AbsRecMngInPeriodRefactParamInput periodRefactParamInput = new AbsRecMngInPeriodRefactParamInput(
                    companyId,
                    employee.getSid(),
                    period,
                    period == null ? GeneralDate.today() : period.end(),
                    false,
                    false,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    new FixedManagementDataMonth()
            );
            CompenLeaveAggrResult absRecRemain = NumberCompensatoryLeavePeriodQuery.process(
                    requireService.createRequire(),
                    periodRefactParamInput
            );

            if (mngUnit == 1) { // 管理単位(1:日数管理/2:時間管理)
                boolean output = false;
                if (haveMoreDrawOutThanHoliday && haveMoreHolidayThanDrawOut) {
                    if (absRecRemain.getCarryoverDay().v() + absRecRemain.getOccurrenceDay().v() != absRecRemain.getDayUse().v()) {
                        output = true;
                    }
                } else if (haveMoreHolidayThanDrawOut) {
                    if (absRecRemain.getCarryoverDay().v() + absRecRemain.getOccurrenceDay().v() < absRecRemain.getDayUse().v()) {
                        output = true;
                    }
                } else if (haveMoreDrawOutThanHoliday) {
                    if (absRecRemain.getCarryoverDay().v() + absRecRemain.getOccurrenceDay().v() > absRecRemain.getDayUse().v()) {
                        output = true;
                    }
                } else {
                    output = true;
                }

                if (output) {
                    HolidayAcquisitionInfo holidayAcquisitionInfo = new HolidayAcquisitionInfo(
                            absRecRemain.getCarryoverDay().v(),
                            absRecRemain.getOccurrenceDay().v(),
                            absRecRemain.getDayUse().v(),
                            absRecRemain.getRemainDay().v(),
                            absRecRemain.getUnusedDay().v(),
                            absRecRemain.getRemainDay().v() < 0,
                            new ArrayList<>(),
                            new ArrayList<>()
                    );
                    for (AffCompanyHistByEmployee hist : affCompHistsOfEmp) {
                        for (AffCompanyHistItem histItem : hist.items()) {
                            for (AccumulationAbsenceDetail detail : absRecRemain.getVacationDetails().getLstAcctAbsenDetail()) {
                                if (detail.getDateOccur().getDayoffDate().isPresent()
                                        && histItem.getDatePeriod().start().beforeOrEquals(detail.getDateOccur().getDayoffDate().get())
                                        && histItem.getDatePeriod().end().afterOrEquals(detail.getDateOccur().getDayoffDate().get())) {
                                    OccurrenceAcquisitionDetail acquisitionDetail = new OccurrenceAcquisitionDetail();
                                    acquisitionDetail.setOccurrenceDigCls(detail.getOccurrentClass());
                                    acquisitionDetail.setDate(detail.getDateOccur());
                                    acquisitionDetail.setOccurrencesUseNumber(detail.getNumberOccurren());
                                    acquisitionDetail.setStatus(detail.getDataAtr());
                                    if (acquisitionDetail.getOccurrenceDigCls() == OccurrenceDigClass.OCCURRENCE) {
                                        UnbalanceCompensation itemOccurrence = (UnbalanceCompensation) detail;
                                        acquisitionDetail.setDeadline(Optional.ofNullable(itemOccurrence.getDeadline()));
                                        acquisitionDetail.setExpiringThisMonth(acquisitionDetail.getDeadline().isPresent()
                                                        ? Optional.of(
                                                acquisitionDetail.getDeadline().get().afterOrEquals(period.start())
                                                        && acquisitionDetail.getDeadline().get().beforeOrEquals(period.end())
                                                ) : Optional.empty()
                                        );
                                    } else {
                                        acquisitionDetail.setDeadline(Optional.empty());
                                        acquisitionDetail.setExpiringThisMonth(Optional.empty());
                                    }
                                    holidayAcquisitionInfo.getOccurrenceAcquisitionDetails().add(acquisitionDetail);
                                }
                            }
                            if (linkingMng) {
                                for (SeqVacationAssociationInfo associationInfo : absRecRemain.getLstSeqVacation()) {
                                    if (histItem.getDatePeriod().start().beforeOrEquals(associationInfo.getOutbreakDay())
                                            && histItem.getDatePeriod().end().afterOrEquals(associationInfo.getOutbreakDay())
                                            && histItem.getDatePeriod().start().beforeOrEquals(associationInfo.getDateOfUse())
                                            && histItem.getDatePeriod().end().afterOrEquals(associationInfo.getDateOfUse())) {
                                        LinkingInfo linkingInfo = new LinkingInfo(
                                                associationInfo.getOutbreakDay(),
                                                associationInfo.getDateOfUse(),
                                                associationInfo.getDayNumberUsed().v()
                                        );
                                        holidayAcquisitionInfo.getLinkingInfos().add(linkingInfo);
                                    }
                                }
                            }
                        }
                    }
                    HolidayConfirmationTableContent content = new HolidayConfirmationTableContent(
                            employee.getEmployeeCode(),
                            employee.getEmployeeName(),
                            workplace == null ? "" : workplace.getWorkplaceCode(),
                            workplace == null ? "" : workplace.getWorkplaceName(),
                            workplace == null ? "" : workplace.getHierarchyCode(),
                            Optional.of(holidayAcquisitionInfo)
                    );
                    contents.add(content);
                }
            }
        }
        if (contents.isEmpty()) throw new BusinessException("Msg_1926");
        return contents;
    }
}
