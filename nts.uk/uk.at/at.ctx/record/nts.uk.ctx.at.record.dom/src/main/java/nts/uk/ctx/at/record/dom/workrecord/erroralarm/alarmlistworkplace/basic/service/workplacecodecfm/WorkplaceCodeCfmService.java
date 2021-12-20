package nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.basic.service.workplacecodecfm;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.gul.collection.CollectionUtil;
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
import java.util.*;
import java.util.stream.Collectors;

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
        DatePeriod period = new DatePeriod(ymPeriod.start().firstGeneralDate(), ymPeriod.end().lastGeneralDate());
        // 空欄のリスト「抽出結果」を作成する。
        List<ExtractResultDto> results = new ArrayList<>();

        // 期間から職場情報を取得
        List<WorkplaceInformationImport> wpInfos = syWorkplaceAdapter.getByCidAndPeriod(cid, period)
                .stream().filter(i -> !i.isDeleteFlag()).collect(Collectors.toList());

        // Map＜職場ID、List＜社員情報＞をループする
        for (Map.Entry<String, List<EmployeeInfoImported>> empInfosByWp : empInfosByWpMap.entrySet()) {
            AlarmValueDate alarmValueDate = null;
            // ループ中の職場IDが取得したList＜職場情報＞の職場IDに存在するかチェック
            List<WorkplaceInformationImport> filteredInfo = wpInfos.stream().filter(x -> x.getWorkplaceId().equals(empInfosByWp.getKey())).collect(Collectors.toList());

            if (CollectionUtil.isEmpty(filteredInfo)) {
                alarmValueDate = new AlarmValueDate(ymPeriod.start().toString(), Optional.of(ymPeriod.end().toString()));
            } else if (filteredInfo.stream().anyMatch(i -> i.getPeriod() != null && i.getPeriod().start().yearMonth().greaterThan(ymPeriod.start()))) {
                WorkplaceInformationImport errorWkp = filteredInfo.stream()
                        .filter(i -> i.getPeriod() != null && i.getPeriod().start().yearMonth().greaterThan(ymPeriod.start()))
                        .min(Comparator.comparing(i -> i.getPeriod().start().yearMonth()))
                        .get();
                alarmValueDate = new AlarmValueDate(ymPeriod.start().toString(), Optional.of(errorWkp.getPeriod().start().yearMonth().toString()));
            } else if (filteredInfo.stream().anyMatch(i -> i.getPeriod() != null && i.getPeriod().end().yearMonth().lessThan(ymPeriod.end()))) {
                WorkplaceInformationImport errorWkp = filteredInfo.stream()
                        .filter(i -> i.getPeriod() != null && i.getPeriod().end().yearMonth().lessThan(ymPeriod.end()))
                        .max(Comparator.comparing(i -> i.getPeriod().end().yearMonth()))
                        .get();
                alarmValueDate = new AlarmValueDate(errorWkp.getPeriod().end().yearMonth().toString(), Optional.of(ymPeriod.end().toString()));
            }

            if (alarmValueDate != null) {
                // 「アラーム値メッセージ」を作成します。
                String message = TextResource.localize("KAL020_4");
                // ドメインオブジェクト「抽出結果」を作成します。
                ExtractResultDto result = new ExtractResultDto(
                        new AlarmValueMessage(message),
                        alarmValueDate,
                        name.v(),
                        Optional.ofNullable(TextResource.localize("KAL020_14", empInfosByWp.getKey())),
                        Optional.of(new MessageDisplay(displayMessage.v())),
                        empInfosByWp.getKey()
                );

                // リスト「抽出結果」に作成した「抽出結果」を追加する。
                results.add(result);
            }
        }

        // リスト「抽出結果」を返す。
        return results;
    }


}
