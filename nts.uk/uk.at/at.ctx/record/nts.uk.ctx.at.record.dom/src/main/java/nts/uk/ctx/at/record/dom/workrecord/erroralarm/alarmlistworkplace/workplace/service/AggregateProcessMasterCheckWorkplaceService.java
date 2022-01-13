package nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.workplace.service;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.EmployeeInfoImported;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.extractresult.AlarmListExtractionInfoWorkplaceDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.extractresult.ExtractResultDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.service.EmployeeInfoByWorkplaceService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.workplace.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト（職場）.マスタチェック（職場）.アルゴリズム.マスタチェック(職場)の集計処理
 *
 * @author Le Huu Dat
 */
@Stateless
public class AggregateProcessMasterCheckWorkplaceService {

//    @Inject
//    private EmployeeInfoByWorkplaceService employeeInfoByWorkplaceService;
    @Inject
    private AlarmFixedExtractionConditionRepository alarmFixedExtractionConditionRepo;
    @Inject
    private AlarmFixedExtractionItemRepository alarmFixedExtractionWpItemRepo;
    @Inject
    private NoRefTimeCfmService noRefTimeCfmService;
    @Inject
    private TimeNotSetFor36EstCfmService timeNotSetFor36EstCfmService;
    @Inject
    private UnsetHdCfmService unsetHdCfmService;
    @Inject
    private UnRegisterManagerCfmService unRegisterManagerCfmService;

    /**
     * マスタチェック(職場)の集計処理
     *
     * @param cid             会社ID
     * @param ymPeriod        期間
     * @param alarmCheckWkpId List＜職場のエラームチェックID＞
     * @param workplaceIds    List＜職場ID＞
     * @return List＜アラームリスト抽出情報（職場）＞
     */
    public List<AlarmListExtractionInfoWorkplaceDto> process(String cid, YearMonthPeriod ymPeriod, List<String> alarmCheckWkpId,
                                                             List<String> workplaceIds) {
        // 空欄のリスト「アラーム抽出結果（職場別）」を作成する。
        List<AlarmListExtractionInfoWorkplaceDto> alarmListResults = new ArrayList<>();

        // ドメインオブジェクト「アラームリスト（職場）固定抽出条件」を取得する。
        List<AlarmFixedExtractionCondition> fixedExtractWpConds = alarmFixedExtractionConditionRepo.getBy(alarmCheckWkpId, true);

        List<AlarmFixedExtractionItem> wkpItems = alarmFixedExtractionWpItemRepo.getAll();

        for (AlarmFixedExtractionCondition wp : fixedExtractWpConds) {
            List<ExtractResultDto> extractResults = new ArrayList<>();

            // ドメインモデル「アラームリスト（職場）固定抽出項目」．職場チェック名称を取得する。
            wkpItems.stream().filter(i -> i.getNo() == wp.getNo()).findFirst().ifPresent(wkpItem -> {
                // ループ中項目の「アラームリスト（職場）固定抽出条件」．Noをチェックする。
                switch (wp.getNo()) {
                    case NO_REF_TIME:
                        // 基準時間の未設定を確認する。
                        extractResults.addAll(noRefTimeCfmService.confirm(cid, wkpItem.getWorkplaceCheckName(), wp.getDisplayMessage(), ymPeriod, workplaceIds));
                        break;
                    case TIME_NOT_SET_FOR_36_ESTIMATED:
                        // 36協定目安時間の未設定を確認する
                        extractResults.addAll(timeNotSetFor36EstCfmService.confirm(wkpItem.getWorkplaceCheckName(), wp.getDisplayMessage(), ymPeriod, workplaceIds));
                        break;
                    case UNSET_OF_HD:
                        // 公休日数の未設定を確認する。
                        extractResults.addAll(unsetHdCfmService.confirm(cid, wkpItem.getWorkplaceCheckName(), wp.getDisplayMessage(), ymPeriod, workplaceIds));
                        break;
                    case NOT_REGISTERED:
                        // 管理者を確認する。
                        extractResults.addAll(unRegisterManagerCfmService.confirm(wkpItem.getWorkplaceCheckName(), wp.getDisplayMessage(), ymPeriod, workplaceIds));
                        break;
                    default:
                        break;
                }
            });

            // アラームリスト抽出情報（職場）を作成してList＜アラームリスト抽出情報（職場）＞に追加
            List<AlarmListExtractionInfoWorkplaceDto> results = extractResults.stream().map(x ->
                    new AlarmListExtractionInfoWorkplaceDto(wp.getId(), 1, x))
                    .collect(Collectors.toList());
            alarmListResults.addAll(results);
        }

        // リスト「アラーム抽出結果（職場別）」を返す。
        return alarmListResults;
    }
}
