package nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.basic.service.esttimeamountcfm;

import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.adapter.shift.estimate.company.CompanyEstablishmentAdaptor;
import nts.uk.ctx.at.record.dom.adapter.shift.estimate.company.CompanyEstablishmentImport;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.basic.BasicCheckName;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.extractresult.ExtractResultDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.DisplayMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueDate;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.MessageDisplay;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト（職場）.マスタチェック(基本).アルゴリズム.マスタチェック(基本)の集計処理.目安時間・金額確認
 *
 * @author Le Huu Dat
 */
@Stateless
public class EstTimeAmountCfmService {

    @Inject
    private CompanyEstablishmentAdaptor companyEstablishmentAdaptor;

    /**
     * 目安時間・金額確認
     *
     * @param cid            会社ID
     * @param name           アラーム項目名
     * @param displayMessage 表示するメッセージ.
     * @param ymPeriod       期間
     * @return List＜抽出結果＞
     */
    public List<ExtractResultDto> confirm(String cid, BasicCheckName name, DisplayMessage displayMessage, YearMonthPeriod ymPeriod) {
        // 空欄のリスト「抽出結果」を作成する
        List<ExtractResultDto> results = new ArrayList<>();

        // 「Input．期間．開始日．年」から「Input．期間．終了日．年」までループする。
        int startYear = ymPeriod.start().year();
        int endYear = ymPeriod.end().year();
        while (startYear <= endYear) {
            // ドメインモデル「全社目安設定」を取得する。
            Optional<CompanyEstablishmentImport> comEstOpt = companyEstablishmentAdaptor.findById2(cid, startYear);
            if (!comEstOpt.isPresent()) {
                // 「アラーム値メッセージ」を作成します。
                String message = TextResource.localize("KAL020_8", String.valueOf(startYear), AppContexts.user().companyCode());

                // ドメインオブジェクト「抽出結果」を作成してリスト「抽出結果」に追加
                ExtractResultDto result = new ExtractResultDto(new AlarmValueMessage(message),
                        new AlarmValueDate(String.valueOf(startYear), Optional.empty()),
                        name.v(),
                        Optional.ofNullable(TextResource.localize("KAL020_20")),
                        Optional.of(new MessageDisplay(displayMessage.v())),
                        null
                );
                results.add(result);
            }

            startYear++;
        }

        // リスト「抽出結果」を返す。
        return results;
    }
}
