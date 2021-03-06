package nts.uk.ctx.at.schedule.dom.shift.weeklywrkday;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.WorkdayDivision;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class WeeklyWorkDay.
 */
// 週間勤務設定
@Getter
@AllArgsConstructor
public class WeeklyWorkDayPattern extends AggregateRoot {

    /** The company id. */
    // 会社ID
    private final CompanyId companyId;

    /** The list Workday */
    //曜日勤務設定リスト
    private List<WorkdayPatternItem> listWorkdayPatternItem;

    /**
     * [C-1] 作る
     * @param companyId
     * @param listWorkdayPatternItem
     */
    public static WeeklyWorkDayPattern weeklyWorkDayPattern(CompanyId companyId, List<WorkdayPatternItem> listWorkdayPatternItem) {
        // inv-1 曜日勤務設定リスト.size == 7
        if (listWorkdayPatternItem.size() != 7) {
            throw new RuntimeException("System Error");
        }
        // 	inv-2	曜日勤務設定の曜日は重複していけない
        boolean hasOverlap = false;
        for (WorkdayPatternItem item : listWorkdayPatternItem) {
            if (listWorkdayPatternItem.stream().filter(i -> i.getDayOfWeek() == item.getDayOfWeek()).collect(Collectors.toList()).size() > 1) {
                hasOverlap = true;
            }
        }
        if (hasOverlap) {
            throw new RuntimeException("System Error");
        }
        return new WeeklyWorkDayPattern(companyId,listWorkdayPatternItem);
    }

    /**
     * 	[1] 対象日の稼働日区分を取得する
     * @param targetDate
     * @return ($曜日勤務設定リスト: find $.曜日 == $対象日の曜日).稼働日区分
     */
    public WorkdayDivision getWorkingDayCtgOfTagertDay(GeneralDate targetDate) {
        int targetDayOfWeek = targetDate.dayOfWeek();
        val result = this.getListWorkdayPatternItem().stream().filter(i -> i.getDayOfWeek().value == targetDayOfWeek).findFirst();
        if (result.isPresent()) {
            return result.get().getWorkdayDivision();
        }
        return null;
    }
}
