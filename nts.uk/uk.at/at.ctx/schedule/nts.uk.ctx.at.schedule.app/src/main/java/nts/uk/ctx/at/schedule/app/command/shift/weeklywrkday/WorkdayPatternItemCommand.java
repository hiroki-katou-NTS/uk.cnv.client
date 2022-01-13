package nts.uk.ctx.at.schedule.app.command.shift.weeklywrkday;

import lombok.Value;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.WorkdayDivision;
import nts.uk.ctx.at.schedule.dom.shift.weeklywrkday.DayOfWeek;
import nts.uk.ctx.at.schedule.dom.shift.weeklywrkday.WorkdayPatternItem;

@Value
public class WorkdayPatternItemCommand {

	// 曜日
	private int dayOfWeek;

	// 稼働日区分
	private int workdayDivision;

	public WorkdayPatternItem toDomain() {
    	return new WorkdayPatternItem(DayOfWeek.valueOf(this.dayOfWeek), WorkdayDivision.valuesOf(this.workdayDivision));
    }
}
