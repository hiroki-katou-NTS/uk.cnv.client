package nts.uk.file.at.app.export.schedule.personalschedulebydate;

import lombok.val;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.GeneralDate;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * 個人スケジュール表(日付別)を出力する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).D：出力の設定.メニュー別OCD.個人スケジュール表(日付別)印刷処理
 */
@Stateless
public class PersonalScheduleByDateExportService extends ExportService<PersonalScheduleByDateQuery> {

    @Inject
    private CreatePersonalScheduleByDateFileQuery exportQuery;

    @Inject
    private PersonalScheduleByDateExportGenerator exportGenerator;

    @Override
    protected void handle(ExportServiceContext<PersonalScheduleByDateQuery> exportServiceContext) {
        val query = exportServiceContext.getQuery();

        // 1.1. 取得する(Input.対象組織,Input. 並び順社員リスト, Input.年月日, Input.実績も取得するか)
        PersonalScheduleByDateDataSource dataSource = this.exportQuery.get(
                query.getOrgUnit(),
                query.getOrgId(),
                GeneralDate.fromString(query.getBaseDate(), "yyyy/MM/dd"),
                query.getSortedEmployeeIds(),
                query.isDisplayActual(),
                query.isGraphVacationDisplay(),
                query.isDoubleWorkDisplay()
        );
        dataSource.setQuery(query);

        // 1.2. create report
        this.exportGenerator.generate(exportServiceContext.getGeneratorContext(), dataSource);
    }
}
