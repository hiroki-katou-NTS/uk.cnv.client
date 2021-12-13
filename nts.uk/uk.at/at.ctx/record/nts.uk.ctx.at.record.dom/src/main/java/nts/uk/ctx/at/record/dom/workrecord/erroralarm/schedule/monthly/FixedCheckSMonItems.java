package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly;

import lombok.AllArgsConstructor;

/**
 * 固定のチェック条件
 *
 */
@AllArgsConstructor
public enum FixedCheckSMonItems {

    /* 年休優先 */
    ANNUAL_LEAVE(1, "スケジュール未作成"),

    /* 積立年休優先 */
    PRIORITY_ANNUAL_LEAVE(2, "勤務種類未登録"),

    /* 振休優先 */
    HOLIDAY(3, "振休優先"),

    /* 代休優先 */
    SUB_HOLIDAY(4, "代休優先");

    public final int value;
    public final String nameId;
}
