package nts.uk.ctx.at.request.app.command.application.kdl035;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.app.command.application.kdl036.DataType;
import nts.uk.ctx.at.request.app.find.application.common.service.other.output.ActualContentDisplayDto;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.shared.app.find.remainingnumber.paymana.PayoutSubofHDManagementDto;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.vacation.setting.ApplyPermission;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class HolidayWorkAssociationStart {
    @Inject
    private InterimRecAbasMngRepository interimRecAbasMngRepo;

    @Inject
    private PayoutManagementDataRepository payoutMngRepo;

    @Inject
    private OtherCommonAlgorithm otherCommonAlgorithm;

    @Inject
    private ComSubstVacationRepository comSubstVacationRepo;

    @Inject
    private ClosureRepository closureRepo;

    @Inject
    private ClosureEmploymentRepository closureEmploymentRepo;

    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;
    
    @Inject
    private PayoutSubofHDManaRepository payoutSubofHDManaRepository;

    /**
     * 振休振出関連付けダイアログ起動
     * @param inputData
     * @return
     */
    public Kdl035OutputData init(Kdl035InputData inputData) {
        // 社員に対応する締め期間を取得する
        DatePeriod closurePeriod = ClosureService.findClosurePeriod(
                ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter),
                new CacheCarrier(),
                inputData.getEmployeeId(),
                GeneralDate.today()
        );

        List<SubstituteWorkData> outputData = getSubsituteWorkData(
                inputData.getEmployeeId(),
                closurePeriod,
                inputData.getManagementData().stream().map(PayoutSubofHDManagementDto::toDomain).collect(Collectors.toList())
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

        return new Kdl035OutputData(
                inputData.getEmployeeId(),
                inputData.getTargetSelectionAtr(),
                period.datesBetween().stream().filter(d -> !dates.contains(d)).collect(Collectors.toList()),
                outputData,
                inputData.getDaysUnit()
        );
    }

    /**
     * 紐付可能な振出データを取得する
     */
    private List<SubstituteWorkData> getSubsituteWorkData(String employeeId, DatePeriod closurePeriod, List<PayoutSubofHDManagement> managementData) {
        List<SubstituteWorkData> result = new ArrayList<>();

        // 暫定振出データを取得する
        result.addAll(getProvisionalDrawingData(employeeId, closurePeriod, managementData));

        // 確定振出データを取得する
        List<SubstituteWorkData> lstDrawingData = getFixedDrawingData(employeeId, closurePeriod, managementData);
        
        // 確定振出データを整理する
        result.addAll(this.organizeFixedData(employeeId, lstDrawingData));

        // 紐付け中の振出データを取得する
        result.addAll(getDrawingDataDuringLinking(employeeId, closurePeriod, managementData));
        
        // ドメインモデル「暫定残数管理データ」を取得する
//        List<PayoutSubofHDManagement> payoutSubofHDManagements = payoutSubofHDManaRepository.getByListOccDate(employeeId, result.stream().map(x -> x.getSubstituteWorkDate()).collect(Collectors.toList()));
//        List<GeneralDate> payoutDates = payoutSubofHDManagements.stream().map(x -> x.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList());
//        
//        result = result.stream().filter(x -> !payoutDates.contains(x.getSubstituteWorkDate())).collect(Collectors.toList());
        

        result.sort(Comparator.comparing(SubstituteWorkData::getSubstituteWorkDate));
        return result;
    }

    /**
     * 暫定振出データを取得する
     */
    private List<SubstituteWorkData> getProvisionalDrawingData(String employeeId, DatePeriod closurePeriod, List<PayoutSubofHDManagement> managementData) {
        List<GeneralDate> outbreakDays = managementData.stream().map(i -> i.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList());

        // ドメインモデル「暫定振出管理データ」を取得する
        List<SubstituteWorkData> result = interimRecAbasMngRepo.getRecBySidDatePeriod(
                employeeId,
                new DatePeriod(
                        closurePeriod.start(),
                        closurePeriod.start().addYears(1)
                )).stream()
                .filter(i -> !outbreakDays.contains(i.getYmd()) && i.getUnUsedDays().v() > 0)
                .map(recMng -> new SubstituteWorkData(
                        recMng.getCreatorAtr() == CreateAtr.RECORD || recMng.getCreatorAtr() == CreateAtr.FLEXCOMPEN ? DataType.ACTUAL.value : DataType.APPLICATION_OR_SCHEDULE.value,
                        recMng.getExpirationDate(),
                        recMng.getExpirationDate().beforeOrEquals(closurePeriod.end()),
                        recMng.getYmd(),
                        recMng.getUnUsedDays().v()
                )).collect(Collectors.toList());

        return result;
    }

    /**
     * 確定振出データを取得する
     */
    private List<SubstituteWorkData> getFixedDrawingData(String employeeId, DatePeriod closurePeriod, List<PayoutSubofHDManagement> managementData) {
        List<GeneralDate> outBreakDays = managementData.stream().map(i -> i.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList());

        // ドメインモデル「振出管理データ」を取得する
        List<SubstituteWorkData> result = payoutMngRepo.getByStateAtr(employeeId, DigestionAtr.UNUSED)
                .stream()
                .filter(i -> !i.getPayoutDate().isUnknownDate()
                        && (!i.getPayoutDate().getDayoffDate().isPresent() || !outBreakDays.contains(i.getPayoutDate().getDayoffDate().get()))
                        && i.getUnUsedDays().v() > 0)
                .map(payout -> new SubstituteWorkData(
                        DataType.ACTUAL.value,
                        payout.getExpiredDate(),
                        payout.getExpiredDate().beforeOrEquals(closurePeriod.end()),
                        payout.getPayoutDate().getDayoffDate().orElse(null),
                        payout.getUnUsedDays().v()
                )).collect(Collectors.toList());

        return result;
    }

    /**
     * 紐付け中の振出データを取得する
     */
    private List<SubstituteWorkData> getDrawingDataDuringLinking(String employeeId, DatePeriod closurePeriod, List<PayoutSubofHDManagement> managementData) {
        List<SubstituteWorkData> result = new ArrayList<>();

        if (managementData.isEmpty()) return result;

        List<GeneralDate> outBreakDays = managementData.stream().map(i -> i.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList());

        // ドメインモデル「暫定残数管理データ」を取得する
        // ドメインモデル「暫定振出管理データ」を取得する
//        List<SubstituteWorkData> recData = interimRecAbasMngRepo.getRecBySidDatePeriod(
//                employeeId,
//                new DatePeriod(
//                        outBreakDays.stream().min(GeneralDate::compareTo).get(),
//                        outBreakDays.stream().max(GeneralDate::compareTo).get()
//                )).stream()
//                .filter(i -> outBreakDays.contains(i.getYmd()))
//                .map(recMng -> new SubstituteWorkData(
//                        recMng.getCreatorAtr() == CreateAtr.RECORD || recMng.getCreatorAtr() == CreateAtr.FLEXCOMPEN ? DataType.ACTUAL.value : DataType.APPLICATION_OR_SCHEDULE.value,
//                        recMng.getExpirationDate(),
//                        recMng.getExpirationDate().beforeOrEquals(closurePeriod.end()),
//                        recMng.getYmd(),
//                        recMng.getUnUsedDays().v()
//                )).collect(Collectors.toList());
        List<SubstituteWorkData> recData = interimRecAbasMngRepo.getRecBySidDatePeriod(
                employeeId,
                new DatePeriod(
                    outBreakDays.stream().min(GeneralDate::compareTo).get(),
                    outBreakDays.stream().max(GeneralDate::compareTo).get()
                )).stream()
                .filter(i -> outBreakDays.contains(i.getYmd()))
                    .map(recMng -> {
                                PayoutSubofHDManagement a = managementData.stream().filter(c -> c.getAssocialInfo().getOutbreakDay().equals(recMng.getYmd()))
                                    .collect(Collectors.toList()).get(0);
                                return new SubstituteWorkData(
                                    recMng.getCreatorAtr() == CreateAtr.RECORD || recMng.getCreatorAtr() == CreateAtr.FLEXCOMPEN ? DataType.ACTUAL.value : DataType.APPLICATION_OR_SCHEDULE.value,
                                    recMng.getExpirationDate(),
                                    recMng.getExpirationDate().beforeOrEquals(closurePeriod.end()),
                                    recMng.getYmd(),
                                    a.getAssocialInfo().getDayNumberUsed().v()
                                );}
                ).collect(Collectors.toList());
        result.addAll(recData);

        // ドメインモデル「振出管理データ」を取得する
        List<SubstituteWorkData> payoutData = payoutMngRepo.getByListPayoutDate(AppContexts.user().companyId(), employeeId, outBreakDays)
                .stream()
                .filter(i -> i.getStateAtr() != DigestionAtr.EXPIRED && !i.getPayoutDate().isUnknownDate())
                .map(payout -> new SubstituteWorkData(
                        DataType.ACTUAL.value,
                        payout.getExpiredDate(),
                        payout.getExpiredDate().beforeOrEquals(closurePeriod.end()),
                        payout.getPayoutDate().getDayoffDate().orElse(null),
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
    public List<PayoutSubofHDManagementDto> determineAssociationTarget(Kdl035OutputData inputData) {
        double required = inputData.getDaysUnit() * inputData.getSubstituteHolidayList().size();
        inputData.getSubstituteWorkInfoList().sort(Comparator.comparingDouble(SubstituteWorkData::getRemainingNumber).reversed());
        double total = 0;
        for (int i = 0; i < inputData.getSubstituteWorkInfoList().size(); i++) {
            if (total - required > 0.5) throw new BusinessException("Msg_1761");
            total += inputData.getSubstituteWorkInfoList().get(i).getRemainingNumber();
        }

        String companyId = AppContexts.user().companyId();

        // 会社別の振休管理設定を取得する
        Optional<ComSubstVacation> comSubstVacation = comSubstVacationRepo.findById(companyId);
        if (!comSubstVacation.isPresent())
            throw new BusinessException(new RawErrorMessage("振休管理設定 Not Found!"));

        // INPUT．「List<振出データ>」と「振休日リスト」を並び変える
        inputData.getSubstituteHolidayList().sort(Comparator.naturalOrder());
        inputData.getSubstituteWorkInfoList().sort(Comparator.comparing(SubstituteWorkData::getExpirationDate));

        List<PayoutSubofHDManagementDto> result = new ArrayList<>();
        for (GeneralDate holiday : inputData.getSubstituteHolidayList()) {
            double requiredNumber = inputData.getDaysUnit();
            while (requiredNumber > 0.0 && inputData.getSubstituteWorkInfoList().stream().anyMatch(i -> i.getRemainingNumber() > 0)) {
                // 同一日かチェックする
                if (inputData.getSubstituteWorkInfoList().stream()
                        .anyMatch(subWorkData -> holiday.compareTo(subWorkData.getSubstituteWorkDate()) == 0)) {
                    throw new BusinessException("Msg_1900");
                }

                // 先取り許可するかチェックする
                if (inputData.getSubstituteWorkInfoList().stream()
                        .anyMatch(subWorkData -> comSubstVacation.get().getSetting().getAllowPrepaidLeave() == ApplyPermission.NOT_ALLOW
                                && holiday.before(subWorkData.getSubstituteWorkDate()))) {
                    throw new BusinessException("Msg_1899");
                }

                for (SubstituteWorkData subWorkData : inputData.getSubstituteWorkInfoList()) {
                    if (subWorkData.getRemainingNumber() > 0) {
                        // ループ中の「振休日」と「振出データ」の期限を確認する
                        if (!holiday.beforeOrEquals(subWorkData.getExpirationDate()))
                            throw new BusinessException("Msg_839");

                        // 「振出振休紐付け管理」を作成する
                        PayoutSubofHDManagementDto substituteMng = new PayoutSubofHDManagementDto(
                                inputData.getEmployeeId(),
                                subWorkData.getSubstituteWorkDate(),
                                holiday,
                                inputData.getDaysUnit() >= subWorkData.getRemainingNumber() ? subWorkData.getRemainingNumber() : inputData.getDaysUnit(),
                                inputData.getTargetSelectionAtr()
                        );
                        result.add(substituteMng);

                        // INPUT．「List<休振出データ>」を更新する
                        subWorkData.setRemainingNumber(subWorkData.getRemainingNumber() - inputData.getDaysUnit());

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
     * 確定振出データを整理する
     */
    public List<SubstituteWorkData> organizeFixedData(String employeeId, List<SubstituteWorkData> lstDrawingData) {
        if (lstDrawingData.isEmpty()) {
            return lstDrawingData;
        }
        // ドメイン「振出振休紐付け管理」を取得する
        List<PayoutSubofHDManagement> listPayoutSubofHDManagements = payoutSubofHDManaRepository.getByListOccDate(
                employeeId, 
                lstDrawingData.stream().map(x -> x.getSubstituteWorkDate()).collect(Collectors.toList()));
        
        Map<GeneralDate, Double> remainingByDate = new HashMap<GeneralDate, Double>();
        if (listPayoutSubofHDManagements.isEmpty()) {
            return lstDrawingData;
        }
        
        // 休出代休紐付け管理ListをLoopする
        listPayoutSubofHDManagements.forEach(x -> {
            if (remainingByDate.containsKey(x.getAssocialInfo().getOutbreakDay())) {
                remainingByDate.put(x.getAssocialInfo().getOutbreakDay(), 
                        remainingByDate.get(x.getAssocialInfo().getOutbreakDay()) + x.getAssocialInfo().getDayNumberUsed().v());
            } else {
                remainingByDate.put(x.getAssocialInfo().getOutbreakDay(), x.getAssocialInfo().getDayNumberUsed().v());
            }
        });
        
        // 整理した「確定振出データ」を渡す
        return lstDrawingData.stream().filter(x -> {
            return !remainingByDate.containsKey(x.getSubstituteWorkDate()) || (remainingByDate.containsKey(x.getSubstituteWorkDate()) && remainingByDate.get(x.getSubstituteWorkDate()) != x.getRemainingNumber());
        }).map(x -> {
            if (!remainingByDate.containsKey(x.getSubstituteWorkDate())) {
                return x;
            } else {
                Double remainingNumber = x.getRemainingNumber() - remainingByDate.get(x.getSubstituteWorkDate());
                x.setRemainingNumber(remainingNumber);
                return x;
            }
        }).collect(Collectors.toList());
    }
}
