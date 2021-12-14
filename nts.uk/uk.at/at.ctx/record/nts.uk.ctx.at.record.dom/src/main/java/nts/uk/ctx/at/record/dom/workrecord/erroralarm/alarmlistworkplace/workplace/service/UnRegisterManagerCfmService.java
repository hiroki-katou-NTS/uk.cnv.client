package nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.workplace.service;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.adapter.auth.wkpmanager.WorkplaceManagerAdapter;
import nts.uk.ctx.at.record.dom.adapter.auth.wkpmanager.WorkplaceManagerImport;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.extractresult.ExtractResultDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.workplace.WorkplaceCheckName;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.DisplayMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueDate;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.MessageDisplay;
import nts.uk.shr.com.i18n.TextResource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト（職場）.マスタチェック（職場）.アルゴリズム.管理者未登録を確認する
 *
 * @author Le Huu Dat
 */
@Stateless
public class UnRegisterManagerCfmService {

    @Inject
    private WorkplaceManagerAdapter workplaceManagerAdapter;

    /**
     * 管理者未登録を確認する
     *
     * @param workplaceCheckName アラーム項目名
     * @param displayMessage     表示するメッセージ
     * @param ymPeriod           期間
     * @param workplaceIds       List＜職場ID＞
     * @return List＜抽出結果＞
     */
    public List<ExtractResultDto> confirm(WorkplaceCheckName workplaceCheckName,
                                          DisplayMessage displayMessage,
                                          YearMonthPeriod ymPeriod,
                                          List<String> workplaceIds) {
        DatePeriod period = new DatePeriod(GeneralDate.ymd(ymPeriod.start().year(), ymPeriod.start().month(), 1),
                GeneralDate.ymd(ymPeriod.end().year(), ymPeriod.end().month(), 1).addMonths(1).addDays(-1));
        // 空欄のリスト「抽出結果」を作成する。
        List<ExtractResultDto> results = new ArrayList<>();

        // ドメインモデル「職場管理者」を取得する。
        List<WorkplaceManagerImport> wpMngs = workplaceManagerAdapter.findByPeriodAndWkpIds(workplaceIds, period);
        for (String workplaceId : workplaceIds) {
            // ループ中職場の「職場管理者」を絞り込み
            List<WorkplaceManagerImport> filteredMngs = wpMngs.stream().filter(x -> x.getWorkplaceId().equals(workplaceId)).collect(Collectors.toList());
            AlarmValueDate alarmValueDate = null;
            if (filteredMngs.isEmpty())
                alarmValueDate = new AlarmValueDate(ymPeriod.start().toString(), Optional.of(ymPeriod.end().toString()));
            else if (filteredMngs.stream().anyMatch(i -> i.getHistoryPeriod().start().after(period.start()))) {
                WorkplaceManagerImport errorMng = filteredMngs.stream().filter(i -> i.getHistoryPeriod().start().after(period.start())).findFirst().get();
                alarmValueDate = new AlarmValueDate(ymPeriod.start().toString(), Optional.of(errorMng.getHistoryPeriod().start().yearMonth().toString()));
            } else if (filteredMngs.stream().anyMatch(i -> i.getHistoryPeriod().end().before(period.start()))) {
                WorkplaceManagerImport errorMng = filteredMngs.stream().filter(i -> i.getHistoryPeriod().end().before(period.start())).findFirst().get();
                alarmValueDate = new AlarmValueDate(errorMng.getHistoryPeriod().end().yearMonth().toString(), Optional.of(ymPeriod.start().toString()));
            }
            if (alarmValueDate != null) {
                // 「アラーム値メッセージ」を作成します。
                String message = TextResource.localize("KAL020_207");

                // ドメインオブジェクト「抽出結果」を作成します。
                ExtractResultDto result = new ExtractResultDto(
                        new AlarmValueMessage(message),
                        alarmValueDate,
                        workplaceCheckName.v(),
                        Optional.ofNullable(message),
                        Optional.of(new MessageDisplay(displayMessage.v())),
                        workplaceId
                );

                // リスト「抽出結果」に作成した抽出結果を追加する。
                results.add(result);
            }
        }

        // リスト「抽出結果」を返す
        return results;
    }
}
