package nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.basic.service.workplacecodecfm;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.EmployeeInfoImported;
import nts.uk.ctx.at.record.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.WorkplaceInformationImport;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.basic.BasicCheckName;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.extractresult.ExtractResultDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.DisplayMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueDate;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.MessageDisplay;
import nts.uk.shr.com.i18n.TextResource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト（職場）.マスタチェック(基本).アルゴリズム.マスタチェック(基本)の集計処理.職場コード確認
 *
 * @author Le Huu Dat
 */
@Stateless
public class WorkplaceCodeCfmService {

    @Inject
    private SyWorkplaceAdapter syWorkplaceAdapter;

    /**
     * 職場コード確認
     *
     * @param cid             会社ID
     * @param name            アラーム項目名
     * @param displayMessage  表示するメッセージ.
     * @param empInfosByWpMap Map＜職場ID、List＜社員情報＞＞
     * @param ymPeriod        期間
     * @return List＜抽出結果＞
     */
    public List<ExtractResultDto> confirm(String cid, BasicCheckName name, DisplayMessage displayMessage,
                                          Map<String, List<EmployeeInfoImported>> empInfosByWpMap, YearMonthPeriod ymPeriod) {
        DatePeriod period = new DatePeriod(GeneralDate.ymd(ymPeriod.start().year(), ymPeriod.start().month(), 1),
                GeneralDate.ymd(ymPeriod.end().year(), ymPeriod.end().month(), 1).addMonths(1).addDays(-1));
        // 空欄のリスト「抽出結果」を作成する。
        List<ExtractResultDto> results = new ArrayList<>();

        // 期間から職場情報を取得
        List<WorkplaceInformationImport> wpInfos = syWorkplaceAdapter.getByCidAndPeriod(cid, period);

        // Map＜職場ID、List＜社員情報＞をループする
        for (Map.Entry<String, List<EmployeeInfoImported>> empInfosByWp : empInfosByWpMap.entrySet()) {
            // ループ中の職場IDが取得したList＜職場情報＞の職場IDに存在するかチェック
            if (wpInfos.stream().anyMatch(x -> x.getWorkplaceId().contains(empInfosByWp.getKey()))) continue;

            // 「アラーム値メッセージ」を作成します。
            String message = TextResource.localize("KAL020_4");
            // ドメインオブジェクト「抽出結果」を作成します。
            ExtractResultDto result = new ExtractResultDto(new AlarmValueMessage(message),
                    new AlarmValueDate(ymPeriod.start().toString(), Optional.empty()),
                    name.v(),
                    Optional.ofNullable(TextResource.localize("KAL020_14", empInfosByWp.getKey())),
                    Optional.of(new MessageDisplay(displayMessage.v())),
                    empInfosByWp.getKey()
            );

            // リスト「抽出結果」に作成した「抽出結果」を追加する。
            results.add(result);
        }

        // リスト「抽出結果」を返す。
        return results;
    }


}
