package nts.uk.screen.at.app.kha003.b;

import nts.uk.ctx.at.record.app.find.workrecord.workmanagement.manhoursummarytable.ManHourSummaryDataFinder;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryData;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * ＜＜ScreenQuery＞＞ 工数集計データを作成する
 * UKDesign.UniversalK.就業.KHA_応援管理.KHA003_工数集計表.Ｂ：集計期間の設定.メニュー別OCD.工数集計データを作成する.工数集計データを作成する
 */
@Stateless
public class CreateManHourSummaryData {
    @Inject
    private ManHourSummaryDataFinder finder;

    public ManHourSummaryData get(ManHourPeriod param) {
        return finder.getManHourSummary(param.getDatePeriod(), param.getYearMonthPeriod());
    }
}
