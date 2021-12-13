package nts.uk.ctx.at.function.app.find.alarmworkplace.alarmlist;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.adapter.alarm.AffAtWorkplaceExport;
import nts.uk.ctx.at.function.dom.adapter.alarm.EmployeeWorkplaceAdapter;
import nts.uk.ctx.at.function.dom.alarm.AlarmPatternCode;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.PreviousClassification;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.Days;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.EndSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.Month;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.StartSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.MonthNo;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.singlemonth.SingleMonth;
import nts.uk.ctx.at.function.dom.alarmworkplace.*;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.WorkplaceCategory;
import nts.uk.ctx.at.function.dom.alarmworkplace.export.AlarmListExtractResultWorkplaceData;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractprocessstatus.AlarmListExtractProcessStatusWorkplace;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractprocessstatus.AlarmListExtractProcessStatusWorkplaceRepository;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractprocessstatus.ExtractState;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractresult.AlarmListExtractInfoWorkplaceRepository;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.organization.adapter.EmploymentAdapter;
import nts.uk.ctx.at.shared.app.service.workrule.closure.ClosureEmploymentService;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.CurrentMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UKDesign.UniversalK.就業.KAL_アラームリスト.KAL011_アラームリスト(職場別)
 *
 * @author Le Huu Dat
 */
@Stateless
public class ExtractAlarmListWorkPlaceFinder {

    @Inject
    private AlarmPatternSettingWorkPlaceRepository alarmPatternSettingWorkPlaceRepo;
    @Inject
    private EmploymentAdapter employmentAdapter;
    @Inject
    private AlarmListExtractInfoWorkplaceRepository alarmListExtractInfoWorkplaceRepo;
    @Inject
    private AlarmListExtractProcessStatusWorkplaceRepository alarmListExtractProcessStatusWorkplaceRepo;
    @Inject
    private ClosureEmploymentService closureEmploymentService;
    @Inject
    private EmployeeWorkplaceAdapter empWkpAdapter;

    /**
     * アラームリストの初期起動
     */
    public InitActiveAlarmListDto initActiveAlarmList() {
        String cid = AppContexts.user().companyId();
        String sid = AppContexts.user().employeeId();
        GeneralDate baseDate = GeneralDate.today();

        List<AffAtWorkplaceExport> empWkpData = empWkpAdapter.getWorkplaceId(Arrays.asList(sid), baseDate);
        String workplaceId = empWkpData.isEmpty() ? null : empWkpData.get(0).getWorkplaceId();

        // ドメインモデル「アラームリストパターン設定(職場別)」を取得する。
        List<AlarmPatternSettingWorkPlace> alarmPatterns = alarmPatternSettingWorkPlaceRepo.findByCompanyId(cid);
        alarmPatterns = alarmPatterns.stream().sorted(Comparator.comparing(AlarmPatternSettingWorkPlace::getAlarmPatternCD))
                .collect(Collectors.toList());

        // アルゴリズム「社員IDと基準日から社員の雇用コードを取得」を実行する。
        Optional<EmploymentHistoryImported> empHistory = this.employmentAdapter.getEmpHistBySid(cid, sid, baseDate);
        if (!empHistory.isPresent()) {
            return new InitActiveAlarmListDto(null, alarmPatterns, null, null, null);
        }
        String employmentCode = empHistory.get().getEmploymentCode();
        Integer closureId = null;
        // 社員に対応する処理締めを取得する
        Optional<Closure> closureOpt = closureEmploymentService.findClosureByEmployee(AppContexts.user().employeeId(),
                GeneralDate.today());
        // 当月の年月を取得する。
        Integer processingYm = null;
        DatePeriod datePeriodClosure = null;
        if (closureOpt.isPresent()) {
            CurrentMonth closureMonth = closureOpt.get().getClosureMonth();
            if (closureMonth != null) {
                processingYm = closureMonth.getProcessingYm().v();

                // 当月の期間を取得する
                datePeriodClosure = ClosureService.getClosurePeriod(
                        closureOpt.get().getClosureId().value,
                        closureOpt.get().getClosureMonth().getProcessingYm(),
                        closureOpt);
            }
        }
        return new InitActiveAlarmListDto(employmentCode, alarmPatterns, processingYm, datePeriodClosure, workplaceId);
    }

    /**
     * アラームリストパータン設定を選択する
     *
     * @param alarmPatternCode 職場のエラームチェックID
     * @param processingYm     当月の年月
     * @return
     */
    public List<CheckConditionDto> getCheckConditions(String alarmPatternCode, Integer processingYm,
                                                      GeneralDate closureStartDate, GeneralDate closureEndDate) {
        String cid = AppContexts.user().companyId();
        // ドメインモデル「チェック条件」を取得する。
        Optional<AlarmPatternSettingWorkPlace> patern = alarmPatternSettingWorkPlaceRepo.getBy(cid,
                new AlarmPatternCode(alarmPatternCode));
        List<CheckCondition> checkConList = patern.get().getCheckConList();

        // ドメインモデル「チェック条件」．抽出する範囲をもとに初期表示する期間を求める
        return calcPeriod(checkConList, YearMonth.of(processingYm), new DatePeriod(closureStartDate, closureEndDate));
    }
    /**
     * 期間を算出する
     *
     * @param checkConditions ドメイン「チェック条件」一覧
     * @param processingYm    当月
     * @param closurePeriod   締め期間
     */
    private List<CheckConditionDto> calcPeriod(List<CheckCondition> checkConditions, YearMonth processingYm, DatePeriod closurePeriod) {
        List<CheckConditionDto> periods = new ArrayList<>();
        for (CheckCondition checkCond : checkConditions) {
            WorkplaceCategory category = checkCond.getWorkplaceCategory();
            CheckConditionDto period = null;
            switch (category) {
                case MASTER_CHECK_BASIC:
                    // 基本の期間を表示に取得する
                    period = getBasicPeriod(checkCond.getRangeToExtract(), processingYm);
                    break;
                case MASTER_CHECK_WORKPLACE:
                    // 職場の期間を表示に取得する
                    period = getWorkplacePeriod(checkCond.getRangeToExtract(), processingYm);
                    break;
                case MONTHLY:
                    // 期間を作成する。
                    SingleMonth singleMonth = (SingleMonth) checkCond.getRangeToExtract();
                    // ・期間　＝　Input．「当月」」ードメイン「チェック条件」の「単月」．月数
                    period = new CheckConditionDto(WorkplaceCategory.MONTHLY, processingYm.addMonths(-singleMonth.getMonthNo()));
                    break;
                case SCHEDULE_DAILY:
                    // スケジュール／日次の期間を取得する。
                    period = getScheduleDailyPeriod(category, checkCond.getRangeToExtract(), processingYm, closurePeriod);
                    break;
                case MASTER_CHECK_DAILY:
                    // マスタチェック(日次)の期間を取得する。
                    period = getScheduleDailyPeriod(category, checkCond.getRangeToExtract(), processingYm, closurePeriod);
                    break;
                case APPLICATION_APPROVAL:
                    // 申請承認の期間を取得する。
                    period = getScheduleDailyPeriod(category, checkCond.getRangeToExtract(), processingYm, closurePeriod);
                    break;
            }
            if (period != null) {
                periods.add(period);
            }
        }

        return periods;
    }

    /**
     * 基本の期間を表示に取得する
     *
     * @param rangeToExtract ドメイン「抽出期間(月単位)」
     * @param processingYm   当月の年月
     * @return 期間
     */
    private CheckConditionDto getBasicPeriod(RangeToExtract rangeToExtract, YearMonth processingYm) {
        return getYmPeriod(WorkplaceCategory.MASTER_CHECK_BASIC, rangeToExtract, processingYm);
    }

    /**
     * 職場の期間を表示に取得する
     *
     * @param rangeToExtract ドメイン「抽出期間(月単位)」
     * @param processingYm   当月の年月
     * @return 期間
     */
    private CheckConditionDto getWorkplacePeriod(RangeToExtract rangeToExtract, YearMonth processingYm) {
        return getYmPeriod(WorkplaceCategory.MASTER_CHECK_WORKPLACE, rangeToExtract, processingYm);
    }

    private CheckConditionDto getYmPeriod(WorkplaceCategory category, RangeToExtract rangeToExtract, YearMonth processingYm) {
        ExtractionPeriodMonthly period = (ExtractionPeriodMonthly) rangeToExtract;
        // 「Input．当月の年月」-「Input．開始月．抽出期間(月単位)．月数」を開始月とする
        Optional<MonthNo> startMonthNo = period.getStartMonth().getStrMonthNo();
        YearMonth startYm = null;
        if (startMonthNo.isPresent()) {
            startYm = processingYm.addMonths(startMonthNo.get().getMonthNo());
        }
        // 「Input．当月の年月」-「Input．終了月．抽出期間(月単位)．月数」を終了月とする
        Optional<MonthNo> endMonthNo = period.getEndMonth().getEndMonthNo();
        YearMonth endYm = null;
        if (endMonthNo.isPresent()) {
            endYm = processingYm.addMonths(endMonthNo.get().getMonthNo());
        }
        return new CheckConditionDto(category, startYm, endYm);
    }

    /**
     * スケジュール／日次の期間を表示に取得する
     *
     * @param category       カテゴリ
     * @param rangeToExtract ドメイン「抽出期間(日単位)」
     * @param processingYm   当月
     * @return 期間
     */
    private CheckConditionDto getScheduleDailyPeriod(WorkplaceCategory category, RangeToExtract rangeToExtract,
                                                     YearMonth processingYm, DatePeriod closurePeriod) {
        return new CheckConditionDto(category,
                getStartDate(rangeToExtract, processingYm, closurePeriod),
                getEndDate(rangeToExtract, processingYm, closurePeriod));
    }

    /**
     * 開始日を取得する
     */
    private GeneralDate getStartDate(RangeToExtract rangeToExtract, YearMonth processingYm, DatePeriod closurePeriod) {
        ExtractionPeriodDaily period = (ExtractionPeriodDaily) rangeToExtract;
        GeneralDate systemDate = GeneralDate.today();
        GeneralDate startDate = null;
        // 開始日を取得する方法をチェックする。
        if (StartSpecify.DAYS.equals(period.getStartDate().getStartSpecify())) {
            Optional<Days> strDays = period.getStartDate().getStrDays();
            if (strDays.isPresent()) {
                // 「前・後」をチェックする
                if (PreviousClassification.BEFORE.equals(strDays.get().getDayPrevious())) {
                    // 開始日を作成する。
                    // ・開始日　＝　システム日付ー「Input．抽出期間(日単位)．開始日．日数指定．日」
                    startDate = systemDate.addDays(-strDays.get().getDay().v());
                } else {
                    // ・開始日　＝　システム日付+「Input．抽出期間(日単位)．開始日．日数指定．日」
                    startDate = systemDate.addDays(strDays.get().getDay().v());
                }
            }
        } else if (StartSpecify.MONTH.equals(period.getStartDate().getStartSpecify())) {
            // 開始日を作成する。
            // 指定した年月の期間をすべて取得する
            Optional<Closure> closureOpt = closureEmploymentService.findClosureByEmployee(AppContexts.user().employeeId(),
                    GeneralDate.today());
            Optional<Month> strMonth = period.getStartDate().getStrMonth();
            if (strMonth.isPresent()) {
                // ・開始の年月　＝　「Input．当月の年月」+「Input．抽出期間(日単位)．開始日．締め日指定．月数」
                YearMonth ym = processingYm.addMonths(strMonth.get().getMonth());
                if (closureOpt.isPresent()) {
                    List<DatePeriod> periodList = closureOpt.get().getPeriodByYearMonth(ym);
                    if (!periodList.isEmpty()) {
                        startDate = periodList.get(0).start();
                    }
                }
            }
        }
        return startDate;
    }

    /**
     * 終了日を取得する
     */
    private GeneralDate getEndDate(RangeToExtract rangeToExtract, YearMonth processingYm, DatePeriod closurePeriod) {
        ExtractionPeriodDaily period = (ExtractionPeriodDaily) rangeToExtract;
        GeneralDate systemDate = GeneralDate.today();
        GeneralDate endDate = null;
        // 終了日を取得する方法をチェックする。
        if (EndSpecify.DAYS.equals(period.getEndDate().getEndSpecify())) {
            Optional<Days> endDays = period.getEndDate().getEndDays();
            if (endDays.isPresent()) {
                // 「前・後」をチェックする
                if (PreviousClassification.BEFORE.equals(endDays.get().getDayPrevious())) {
                    // 終了日を作成する。
                    // ・終了日　＝　システム日付ー「Input．抽出期間(日単位)．終了日．日数指定．日」
                    endDate = systemDate.addDays(-endDays.get().getDay().v());
                } else {
                    // ・終了日　＝　システム日付+「Input．抽出期間(日単位)．終了日．日数指定．日」
                    endDate = systemDate.addDays(endDays.get().getDay().v());
                }
            }
        } else if (EndSpecify.MONTH.equals(period.getEndDate().getEndSpecify())) {
            // 終了日を作成する。
            // 指定した年月の期間をすべて取得する
            Optional<Closure> closureOpt = closureEmploymentService.findClosureByEmployee(AppContexts.user().employeeId(),
                    GeneralDate.today());
            Optional<Month> startMonth = period.getEndDate().getEndMonth();
            if (startMonth.isPresent()) {
                // ・年月　＝　「Input．締め．当月．当月」+「Input．抽出期間(日単位)．開始日．締め日指定．月数」
                YearMonth ym = processingYm.addMonths(startMonth.get().getMonth());
                if (closureOpt.isPresent()) {
                    List<DatePeriod> periodList = closureOpt.get().getPeriodByYearMonth(ym);
                    if (!periodList.isEmpty()) {
                        endDate = periodList.get(0).end();
                    }
                }

            }

        }
        return endDate;
    }

    private GeneralDate getDate(YearMonth ym, int day) {
        GeneralDate lastDate = ym.lastGeneralDate();
        if (day > lastDate.day()) {
            return lastDate;
        } else {
            return GeneralDate.ymd(ym.year(), ym.month(), day);
        }
    }
    /**
     * 抽出状況を確認する
     */
    public void checkProcessStatus() {
        String cid = AppContexts.user().companyId();
        String sid = AppContexts.user().employeeId();
        // ドメインモデル「アラームリスト抽出処理状況(職場別)」をチェックする
        List<AlarmListExtractProcessStatusWorkplace> processes = alarmListExtractProcessStatusWorkplaceRepo.getBy(cid, sid, ExtractState.PROCESSING);
        if (!CollectionUtil.isEmpty(processes)) {
            throw new BusinessException("Msg_993");
        }
    }
    public List<AlarmListExtractResultWorkplaceData> getExtractResult(String processId) {
        // 取得した職場情報一覧から「アラーム抽出結果（職場別）」にデータをマッピングする
        return AlarmListExtractResultWorkplaceData.fromDomains(alarmListExtractInfoWorkplaceRepo.getById(processId));
    }
}
