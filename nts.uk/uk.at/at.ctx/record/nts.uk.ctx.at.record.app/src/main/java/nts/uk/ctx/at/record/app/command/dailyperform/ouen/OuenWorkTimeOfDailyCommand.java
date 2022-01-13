package nts.uk.ctx.at.record.app.command.dailyperform.ouen;

import java.util.Optional;

import lombok.Getter;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.OuenWorkTimeOfDailyDto;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.shared.app.util.attendanceitem.DailyWorkCommonCommand;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;

public class OuenWorkTimeOfDailyCommand extends DailyWorkCommonCommand {

	@Getter
	private Optional<OuenWorkTimeOfDaily> data;
	
	@Override
	public void setRecords(ConvertibleAttendanceItem item) {
		this.data = item == null || !item.isHaveData()
				? Optional.empty() 
				: Optional.ofNullable(((OuenWorkTimeOfDailyDto) item).toDomain(getEmployeeId(), getWorkDate()));
	}
	
	@Override
	public void updateData(Object data) {
		if(data != null){
			this.data = Optional.of((OuenWorkTimeOfDaily) data);
		}
	}
	
	@Override
	public Optional<OuenWorkTimeOfDaily> toDomain() {
		return this.data;
	}
	
	@Override
	public Optional<OuenWorkTimeOfDailyDto> toDto() {
		return getData().map(d -> OuenWorkTimeOfDailyDto.from(getEmployeeId(), getWorkDate(), d.getOuenTimes()));
	}
}
