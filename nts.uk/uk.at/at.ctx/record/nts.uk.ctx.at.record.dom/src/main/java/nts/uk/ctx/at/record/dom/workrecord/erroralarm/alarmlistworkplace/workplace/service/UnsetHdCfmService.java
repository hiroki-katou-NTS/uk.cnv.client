package nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.workplace.service;

import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.extractresult.ExtractResultDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.workplace.WorkplaceCheckName;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.DisplayMessage;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueDate;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.MessageDisplay;
import nts.uk.shr.com.i18n.TextResource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト（職場）.マスタチェック（職場）.アルゴリズム.公休日数の未設定を確認する
 *
 * @author Le Huu Dat
 */
@Stateless
public class UnsetHdCfmService {

    @Inject
    private WorkplaceMonthDaySettingRepository workplaceMonthDaySettingRepo;

    /**
     * 公休日数の未設定を確認する
     *
     * @param cid                会社ID
     * @param workplaceCheckName アラーム項目名
     * @param displayMessage     表示するメッセージ
     * @param ymPeriod           期間
     * @param workplaceIds       List＜職場ID＞
     * @return List＜抽出結果＞
     */
    public List<ExtractResultDto> confirm(String cid,
                                          WorkplaceCheckName workplaceCheckName,
                                          DisplayMessage displayMessage,
                                          YearMonthPeriod ymPeriod,
                                          List<String> workplaceIds) {
        // 空欄のリスト「抽出結果」を作成する
        List<ExtractResultDto> results = new ArrayList<>();

        // ドメインモデル「職場月間日数設定」を取得する。
        List<WorkplaceMonthDaySetting> wpMonthDaySetAll = workplaceMonthDaySettingRepo.findByWorkplaceIds(new CompanyId(cid), workplaceIds);

        // Input．List＜職場情報＞をループする
        for (String workplaceId : workplaceIds) {
            // 「Input．期間．開始日．年」から「Input．期間．終了日．年」までループする
            int startYear = ymPeriod.start().year();
            int endYear = ymPeriod.end().year();
            while (startYear <= endYear) {

                // ドメインモデル「職場月間日数設定」を取得する。
                int year = startYear;
                Optional<WorkplaceMonthDaySetting> wpMonthDaySetOpt = wpMonthDaySetAll.stream().filter(x -> x.getWorkplaceId().equals(workplaceId) && x.getManagementYear().v() == year).findFirst();

                if (!wpMonthDaySetOpt.isPresent()) {
                    // 「アラーム値メッセージ」を作成します。
                    String message = TextResource.localize("KAL020_206");

                    // ドメインオブジェクト「抽出結果」を作成してリスト「抽出結果」に追加
                    ExtractResultDto result = new ExtractResultDto(new AlarmValueMessage(message),
                            new AlarmValueDate(String.valueOf(startYear), Optional.empty()),
                            workplaceCheckName.v(),
                            Optional.of(message),
                            Optional.of(new MessageDisplay(displayMessage.v())),
                            workplaceId
                    );
                    results.add(result);
                }

                startYear++;
            }
        }

        // リスト「抽出結果」を返す。
        return results;
    }
}
