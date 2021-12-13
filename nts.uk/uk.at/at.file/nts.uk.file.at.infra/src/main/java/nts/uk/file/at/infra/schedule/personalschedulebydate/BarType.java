package nts.uk.file.at.infra.schedule.personalschedulebydate;

public enum BarType {
    /** 固定勤務時間横棒- C3_2_1 */
    FIXED_WORKING_HOURS(1),

    /** 休憩時間横棒 - C3_2_2*/
    BREAK_TIME(2),

    /** 時間外労働時間横棒 - C3_2_3*/
    OVERTIME_HOURS(3),

    /** 流動勤務時間横棒 - C3_2_4 */
    FLOWING_WORKING_HOURS(4),

    /** フレックス勤務時間横棒 - C3_2_5 */
    FLEX_WORKING_HOURS(5),

    /** コアタイム時間横棒 - C3_2_6 */
    CORE_TIME(6),

    /** 時間休暇横棒 - C3_2_7 */
    TIME_VACATION(7),

    /** 育児介護短時間横棒 - C3_2_8 */
    CHILDCARE_SHORT_TIME(8),

    /** 勤務実績横棒 - C3_2_14 */
    WORKING_HOURS_ACTUAL(9),

    /** 勤務実績横棒 - C3_2_15 */
    BREAK_TIME_ACTUAL(10),

    /** 時間外実績横棒 - C3_2_16 */
    OVERTIME_HOURS_ACTUAL(11);

    public final int value;
    BarType(int value) {
        this.value = value;
    }
}
