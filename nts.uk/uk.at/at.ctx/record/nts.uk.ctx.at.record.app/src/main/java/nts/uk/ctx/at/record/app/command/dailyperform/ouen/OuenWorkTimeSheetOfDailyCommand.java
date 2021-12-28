package nts.uk.ctx.at.record.app.command.dailyperform.ouen;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.OuenWorkTimeSheetOfDailyAttendanceDto;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.OuenWorkTimeSheetOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.WorkContentDto;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.shared.app.util.attendanceitem.DailyWorkCommonCommand;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;

@NoArgsConstructor
public class OuenWorkTimeSheetOfDailyCommand extends DailyWorkCommonCommand {
	
	@Setter
	@Getter
	private List<OuenWorkTimeSheetOfDailyAttendance> data = new ArrayList<>();

	@Override
	public void setRecords(ConvertibleAttendanceItem item) {
		if(item != null && item.isHaveData()){
			val value = ((OuenWorkTimeSheetOfDailyDto) item).toDomain(getEmployeeId(), getWorkDate());
			updateDatas(value);
		}
	}
	
	@Override
	public void updateData(Object data) {
		if(data != null){
			OuenWorkTimeSheetOfDailyAttendance d = (OuenWorkTimeSheetOfDailyAttendance) data;
			this.data.removeIf(es -> es.getWorkNo() == d.getWorkNo());
			this.data.add(d);
		}
	}
	
	@Override
	public List<OuenWorkTimeSheetOfDailyAttendance> toDomain() {
		return data;
	}
	
	@Override
	public OuenWorkTimeSheetOfDailyDto toDto() {
		OuenWorkTimeSheetOfDaily domainDaily = OuenWorkTimeSheetOfDaily.create(getEmployeeId(), getWorkDate(), this.data);
		return OuenWorkTimeSheetOfDailyDto.getDto(domainDaily);
	}
	
	public OuenWorkTimeSheetOfDailyCommand corectOuenCommand(OuenWorkTimeSheetOfDailyCommand command, String workplaceId) {
		
		List<OuenWorkTimeSheetOfDailyAttendance> data = command.getData().stream().map(mapper -> {
			if (mapper.getWorkContent().getWorkplace().getWorkplaceId() != null && mapper.getWorkContent().getWorkplace().getWorkplaceId().v().isEmpty())
				return OuenWorkTimeSheetOfDailyCommand.correctOuen(mapper, workplaceId);
			return mapper;
		}).collect(Collectors.toList());
		
		return new OuenWorkTimeSheetOfDailyCommand(data);
	}
	
	public static OuenWorkTimeSheetOfDailyAttendance correctOuen(OuenWorkTimeSheetOfDailyAttendance dto, String workplaceId) {
		
		if (dto.getWorkContent().getWorkplace().getWorkplaceId() != null && dto.getWorkContent().getWorkplace().getWorkplaceId().v().isEmpty()) {
			WorkplaceOfWorkEachOuen workplace = WorkplaceOfWorkEachOuen.create(new WorkplaceId(workplaceId), dto.getWorkContent().getWorkplace().getWorkLocationCD().map(x -> x).orElse(null));
			WorkContent workContent = WorkContent.create(workplace, dto.getWorkContent().getWork(), dto.getWorkContent().getWorkSuppInfo());
			return new OuenWorkTimeSheetOfDailyAttendance(dto.getWorkNo(), workContent, dto.getTimeSheet(), dto.getInputFlag());
		}
		return dto;
	}

	public OuenWorkTimeSheetOfDailyCommand(List<OuenWorkTimeSheetOfDailyAttendance> data) {
		super();
		this.data = data;
	}
}
