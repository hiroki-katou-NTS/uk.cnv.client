package nts.uk.ctx.at.record.app.command.monthly.publicholiday;

import lombok.Getter;
import nts.uk.ctx.at.record.app.command.monthly.MonthlyWorkCommonCommand;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyPublicHolidayRemainDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;

public class MonthPublicHolidayRemainCommand extends MonthlyWorkCommonCommand {

	@Getter
	private MonthlyPublicHolidayRemainDto data;
	
	@Override
	public void setRecords(ConvertibleAttendanceItem item) {
		this.data = item == null || !item.isHaveData() ? null : (MonthlyPublicHolidayRemainDto) item;
		
	}

	@Override
	public void updateData(Object data) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
	public PublicHolidayRemNumEachMonth toDomain(){
		return data.toDomain(getEmployeeId(), getYearMonth(), getClosureId(), getClosureDate());
	}
	

}
