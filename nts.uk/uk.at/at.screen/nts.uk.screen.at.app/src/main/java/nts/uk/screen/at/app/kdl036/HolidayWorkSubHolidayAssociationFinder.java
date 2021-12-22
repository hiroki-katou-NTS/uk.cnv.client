package nts.uk.screen.at.app.kdl036;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.app.find.application.common.service.other.output.ActualContentDisplayDto;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.shared.app.find.remainingnumber.subhdmana.dto.LeaveComDayOffManaDto;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ApplyPermission;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.screen.at.app.dailyperformance.correction.closure.FindClosureDateService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class HolidayWorkSubHolidayAssociationFinder {
    @Inject
    private FindClosureDateService closureService;

    @Inject
    private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;

    @Inject
    private LeaveManaDataRepository holidayWorkMngRepo;

    @Inject
    private OtherCommonAlgorithm otherCommonAlgorithm;

    @Inject
    private CompensLeaveComSetRepository comSubstVacationRepo;

    @Inject
    private ClosureRepository closureRepo;

    @Inject
    private ClosureEmploymentRepository closureEmploymentRepo;

    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;
    
    @Inject
    private LeaveComDayOffManaRepository leaveComDayOffManaRepo;

    /**
     * 休出代休関連付けダイアログ起動
     * @param inputData
     * @return
     */
    public Kdl036OutputData init(Kdl036InputData inputData) {
        // 社員に対応する締め期間を取得する
        DatePeriod closurePeriod = ClosureService.findClosurePeriod(
                ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter),
                new CacheCarrier(),
                inputData.getEmployeeId(),
                GeneralDate.today()
        );

        List<HolidayWorkData> outputData = getSubsituteWorkData(
                inputData.getEmployeeId(),
                closurePeriod,
                inputData.getManagementData().stream().map(LeaveComDayOffManaDto::toDomain).collect(Collectors.toList())
        );

        DatePeriod period = inputData.getEndDate() != null ? new DatePeriod(inputData.getStartDate(), inputData.getEndDate()) : DatePeriod.oneDay(inputData.getStartDate());
        List<GeneralDate> dates = new ArrayList<>();
        if (period.start().compareTo(period.end()) != 0) {
            // 申請期間から休日の申請日を取得する
            dates.addAll(otherCommonAlgorithm.lstDateIsHoliday(
                    inputData.getEmployeeId(),
                    period,
                    inputData.getActualContentDisplayList().stream().map(ActualContentDisplayDto::toDomain).collect(Collectors.toList())
            ));
        }

        return new Kdl036OutputData(
                inputData.getEmployeeId(),
                inputData.getTargetSelectionAtr(),
                period.datesBetween().stream().filter(d -> !dates.contains(d)).collect(Collectors.toList()),
                outputData,
                inputData.getDaysUnit()
        );
    }

    /**
     * 紐付可能な休日出勤データを取得する
     */
    private List<HolidayWorkData> getSubsituteWorkData(String employeeId, DatePeriod closurePeriod, List<LeaveComDayOffManagement> managementData) {
        List<HolidayWorkData> result = new ArrayList<>();

        // 暫定休出データを取得する
        result.addAll(getProvisionalDrawingData(employeeId, closurePeriod, managementData));

        // 確定休出データを取得する
        List<HolidayWorkData> lstDrawingData = getFixedDrawingData(employeeId, closurePeriod, managementData);
        
        // 確定休出データを整理する
        result.addAll(this.organizeLeaveData(employeeId, lstDrawingData));

        // 紐付け中の休出データを取得する
        result.addAll(getDrawingDataDuringLinking(employeeId, closurePeriod, managementData));

        result.sort(Comparator.comparing(HolidayWorkData::getHolidayWorkDate));
        return result;
    }

    /**
     * 暫定休出データを取得する
     */
    private List<HolidayWorkData> getProvisionalDrawingData(String employeeId, DatePeriod closurePeriod, List<LeaveComDayOffManagement> managementData) {
        List<GeneralDate> outBreakDays = managementData.stream().map(i -> i.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList());

        // ドメインモデル「暫定休出管理データ」を取得する
        List<HolidayWorkData> result = interimBreakDayOffMngRepo.getBySidPeriod(
                employeeId,
                new DatePeriod(
                        closurePeriod.start(),
                        closurePeriod.start().addYears(1)
                )).stream()
                .filter(i -> !outBreakDays.contains(i.getYmd()) && i.getUnUsedDays().v() > 0 && i.getUnUsedTimes().v() <= 0)
                .map(recMng -> new HolidayWorkData(
                        recMng.getCreatorAtr() == CreateAtr.RECORD || recMng.getCreatorAtr() == CreateAtr.FLEXCOMPEN ? DataType.ACTUAL.value : DataType.APPLICATION_OR_SCHEDULE.value,
                        recMng.getExpirationDate(),
                        recMng.getExpirationDate().beforeOrEquals(closurePeriod.end()),
                        recMng.getYmd(),
                        recMng.getUnUsedDays().v()
                )).collect(Collectors.toList());

        return result;
    }

    /**
     * 確定休出データを取得する
     */
    private List<HolidayWorkData> getFixedDrawingData(String employeeId, DatePeriod closurePeriod, List<LeaveComDayOffManagement> managementData) {
        List<GeneralDate> outBreakDays = managementData.stream().map(i -> i.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList());

        // ドメインモデル「休出管理データ」を取得する
        List<HolidayWorkData> result = holidayWorkMngRepo.getBySidAndStateAtr(AppContexts.user().companyId(), employeeId, DigestionAtr.UNUSED)
                .stream()
                .filter(i -> !i.getComDayOffDate().isUnknownDate()
                        && (!i.getComDayOffDate().getDayoffDate().isPresent() || !outBreakDays.contains(i.getComDayOffDate().getDayoffDate().get()))
                        && i.getUnUsedDays().v() > 0 && i.getUnUsedTimes().v() <= 0)
                .map(payout -> new HolidayWorkData(
                        DataType.ACTUAL.value,
                        payout.getExpiredDate(),
                        payout.getExpiredDate().beforeOrEquals(closurePeriod.end()),
                        payout.getComDayOffDate().getDayoffDate().orElse(null),
                        payout.getUnUsedDays().v()
                )).collect(Collectors.toList());

        return result;
    }

    /**
     * 紐付け中の休出データを取得する
     */
    private List<HolidayWorkData> getDrawingDataDuringLinking(String employeeId, DatePeriod closurePeriod, List<LeaveComDayOffManagement> managementData) {
        List<HolidayWorkData> result = new ArrayList<>();

        if (managementData.isEmpty()) return result;

        List<GeneralDate> outBreakDays = managementData.stream().map(i -> i.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList());

        List<HolidayWorkData> recData = interimBreakDayOffMngRepo.getBySidPeriod(
                employeeId,
                new DatePeriod(
                        outBreakDays.stream().min(GeneralDate::compareTo).get(),
                        outBreakDays.stream().max(GeneralDate::compareTo).get()
                )).stream()
                .filter(i -> outBreakDays.contains(i.getYmd()))
                .map(recMng -> new HolidayWorkData(
                        recMng.getCreatorAtr() == CreateAtr.RECORD || recMng.getCreatorAtr() == CreateAtr.FLEXCOMPEN ? DataType.ACTUAL.value : DataType.APPLICATION_OR_SCHEDULE.value,
                        recMng.getExpirationDate(),
                        recMng.getExpirationDate().beforeOrEquals(closurePeriod.end()),
                        recMng.getYmd(),
                        recMng.getUnUsedDays().v()
                )).collect(Collectors.toList());
        result.addAll(recData);

        // ドメインモデル「休出管理データ」を取得する
        List<HolidayWorkData> payoutData = holidayWorkMngRepo.getBySidAndDatOff(employeeId, outBreakDays)
                .stream()
                .filter(i -> i.getSubHDAtr() != DigestionAtr.EXPIRED && !i.getComDayOffDate().isUnknownDate() && i.getUnUsedTimes().v() <= 0)
                .map(payout -> new HolidayWorkData(
                        DataType.ACTUAL.value,
                        payout.getExpiredDate(),
                        payout.getExpiredDate().beforeOrEquals(closurePeriod.end()),
                        payout.getComDayOffDate().getDayoffDate().orElse(null),
                        payout.getUnUsedDays().v()
                )).collect(Collectors.toList());
        result.addAll(payoutData);

        return result;
    }

    /**
     * 関連付け対象の決定
     * @param inputData
     * @return
     */
    public List<LeaveComDayOffManaDto> determineAssociationTarget(Kdl036OutputData inputData) {
        double required = inputData.getDaysUnit() * inputData.getSubstituteHolidayList().size();
        inputData.getHolidayWorkInfoList().sort(Comparator.comparingDouble(HolidayWorkData::getRemainingNumber).reversed());
        double total = 0;
        for (int i = 0; i < inputData.getHolidayWorkInfoList().size(); i++) {
            if (total - required > 0.5) throw new BusinessException("Msg_1758");
            total += inputData.getHolidayWorkInfoList().get(i).getRemainingNumber();
        }

        String companyId = AppContexts.user().companyId();

        // 会社別の代休管理設定を取得する
        CompensatoryLeaveComSetting comSubstVacation = comSubstVacationRepo.find(companyId);
        if (comSubstVacation == null)
            throw new BusinessException(new RawErrorMessage("代休管理設定 Not Found!"));

        // INPUT．「List<休日出勤データ>」と「代休日リスト」を並び変える
        inputData.getSubstituteHolidayList().sort(Comparator.naturalOrder());
        inputData.getHolidayWorkInfoList().sort(Comparator.comparing(HolidayWorkData::getExpirationDate));

        List<LeaveComDayOffManaDto> result = new ArrayList<>();
        for (GeneralDate holiday : inputData.getSubstituteHolidayList()) {
            double requiredNumber = inputData.getDaysUnit();
            while (requiredNumber > 0.0 && inputData.getHolidayWorkInfoList().stream().anyMatch(i -> i.getRemainingNumber() > 0)) {
                // 同一日かチェックする
                if (inputData.getHolidayWorkInfoList().stream()
                        .anyMatch(holidayWorkData -> holiday.compareTo(holidayWorkData.getHolidayWorkDate()) == 0)) {
                    throw new BusinessException("Msg_1901");
                }

                // 先取り許可するかチェックする
                if (inputData.getHolidayWorkInfoList().stream()
                        .anyMatch(holidayWorkData -> comSubstVacation.getCompensatoryAcquisitionUse().getPreemptionPermit() == ApplyPermission.NOT_ALLOW
                                && holiday.before(holidayWorkData.getHolidayWorkDate()))) {
                    throw new BusinessException("Msg_1902");
                }

                for (HolidayWorkData holidayWorkData : inputData.getHolidayWorkInfoList()) {
                    if (holidayWorkData.getRemainingNumber() > 0) {
                        // ループ中の「代休日」と「休日出勤データ」の期限を確認する
                        if (!holiday.beforeOrEquals(holidayWorkData.getExpirationDate()))
                            throw new BusinessException("Msg_970");

                        // 「休出代休紐付け管理」を作成する
                        LeaveComDayOffManaDto substituteMng = new LeaveComDayOffManaDto(
                                inputData.getEmployeeId(),
                                holidayWorkData.getHolidayWorkDate(),
                                holiday,
                                inputData.getDaysUnit() >= holidayWorkData.getRemainingNumber() ? holidayWorkData.getRemainingNumber() : inputData.getDaysUnit(),
                                inputData.getTargetSelectionAtr()
                        );
                        result.add(substituteMng);

                        // INPUT．「List<休日出勤データ>」を更新する
                        holidayWorkData.setRemainingNumber(holidayWorkData.getRemainingNumber() - inputData.getDaysUnit());

                        // 「必要日数」を更新する
                        requiredNumber = requiredNumber - substituteMng.getDayNumberUsed();
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * 確定休出データを整理する
     */
    public List<HolidayWorkData> organizeLeaveData(String employeeId, List<HolidayWorkData> lstDrawingData) {
        if (lstDrawingData.isEmpty()) {
            return lstDrawingData;
        }
        // ドメイン「休出代休紐付け管理」を取得する 
        List<LeaveComDayOffManagement> listLeaveComDayOffManagements = 
                leaveComDayOffManaRepo.getLeavByListOccDate(
                        employeeId, 
                        lstDrawingData.stream().map(x -> x.getHolidayWorkDate()).collect(Collectors.toList()));
        
        Map<GeneralDate, Double> remainingByDate = new HashMap<GeneralDate, Double>();
        if (listLeaveComDayOffManagements.isEmpty()) {
            return lstDrawingData;
        }
        
        // 休出代休紐付け管理の発生日ListをLoopする
        listLeaveComDayOffManagements.forEach(x -> {
            if (remainingByDate.containsKey(x.getAssocialInfo().getOutbreakDay())) {
                remainingByDate.put(x.getAssocialInfo().getOutbreakDay(), 
                        remainingByDate.get(x.getAssocialInfo().getOutbreakDay()) + x.getAssocialInfo().getDayNumberUsed().v());
            } else {
                remainingByDate.put(x.getAssocialInfo().getOutbreakDay(), x.getAssocialInfo().getDayNumberUsed().v());
            }
        });
        
        // 紐付けした確定休出を整理する
        return lstDrawingData.stream().filter(x -> {
            return !remainingByDate.containsKey(x.getHolidayWorkDate()) || (remainingByDate.containsKey(x.getHolidayWorkDate()) && remainingByDate.get(x.getHolidayWorkDate()) != x.getRemainingNumber());
        }).map(x -> {
            if (!remainingByDate.containsKey(x.getHolidayWorkDate())) {
                return x;
            } else {
                Double remainingNumber = x.getRemainingNumber() - remainingByDate.get(x.getHolidayWorkDate());
                x.setRemainingNumber(remainingNumber);
                return x;
            }
        }).collect(Collectors.toList());
    }
}
