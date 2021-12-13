package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily;

import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;

/**
 * 固定のチェック条件
 *
 */
@RequiredArgsConstructor
public enum FixedCheckSDailyItems {

    /* スケジュール未作成 */
    SCHEDULE_CREATE_NOTCREATE(1, "スケジュール未作成"),

    /* 勤務種類未登録 */
    WORKTYPE_NOTREGISTED(2, "勤務種類未登録"),

    /* 就業時間帯未登録 */
    WORKTIME_NOTREGISTED(3, "就業時間帯未登録"),

    /* 就業時間帯未登録 */
    OVERLAP_TIMEZONE(4, "就業時間帯未登録"),

    /* 年複数回勤務 */
    WORK_MULTI_TIME(5, "複数回勤務"),

    /* 特定日出勤 */
    WORK_ON_SCHEDULEDAY(6, "特定日出勤"),

    /* 特定日出勤 */
    SCHEDULE_UNDECIDED(7, "特定日出勤");

    public final int value;
    public final String nameId;

    public static FixedCheckSDailyItems of(int value) {
        return EnumAdaptor.valueOf(value, FixedCheckSDailyItems.class);
    }
}
