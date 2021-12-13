package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import java.util.List;
import java.util.Optional;

/**
 * アラームリスト実行メール設定Repository
 */
public interface AlarmListExecutionMailSettingRepository {
    /*
    * 会社IDによってアラームリスト実行メール設定を取得する(会社ID、個人職場区分)
     * */
    List<AlarmListExecutionMailSetting> getByCId(String cid, int individualWkpClassify);

    /*
     * insertAll(List<アラームリスト実行メール設定>)
     * */
    void insertAll(List<AlarmListExecutionMailSetting> alarmExecMailSettings);

    /*
     * updateAll(List<アラームリスト実行メール設定>)
     * */
    void updateAll(List<AlarmListExecutionMailSetting> alarmExecMailSettings);

    /*
     * 会社IDと本人管理区分によってアラームリスト実行メール設定を取得する(会社ID、個人職場区分、本人管理区分)
     * */
    List<AlarmListExecutionMailSetting> getByCompanyId(String cid, int personalManagerClassify, int individualWRClassification);

    List<AlarmListExecutionMailSetting> findBy(String cid, int individualWkpClassify, int normalAutoClassify);
}
