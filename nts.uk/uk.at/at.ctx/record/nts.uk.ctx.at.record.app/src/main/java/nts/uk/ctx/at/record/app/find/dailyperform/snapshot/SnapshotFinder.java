package nts.uk.ctx.at.record.app.find.dailyperform.snapshot;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workschedule.snapshot.DailySnapshotWorkAdapter;
import nts.uk.ctx.at.shared.app.util.attendanceitem.FinderFacade;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;

@Stateless
public class SnapshotFinder extends FinderFacade{
	@Inject
	private DailySnapshotWorkAdapter repo;
	
	@SuppressWarnings("unchecked")
	@Override
	public SnapshotDto find(String employeeId, GeneralDate baseDate) {
		val domain = this.repo.find(employeeId, baseDate).map(c -> c.getSnapshot().toDomain());
		
		return SnapshotDto.from(employeeId, baseDate, domain.orElse(null));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ConvertibleAttendanceItem> List<T> find(List<String> employeeId, DatePeriod baseDate) {
		
		return (List<T>) employeeId.stream().map(c -> this.repo.find(c, baseDate))
			.flatMap(List::stream)
			.map(c -> SnapshotDto.from(c.getSid(), c.getYmd(), c.getSnapshot().toDomain()))
			.filter(d -> d.isHaveData())
			.collect(Collectors.toList());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConvertibleAttendanceItem> List<T> find(Map<String, List<GeneralDate>> param) {
		return (List<T>) param.keySet().stream()
						.map(c -> param.get(c).stream().map(d -> find(c, d))
													.filter(d -> d.isHaveData())
													.collect(Collectors.toList()))
						.flatMap(List::stream)
						.collect(Collectors.toList());
	}

	@Override
	public Object getDomain(String employeeId, GeneralDate baseDate) {
		val domain = this.repo.find(employeeId, baseDate).map(c -> c.getSnapshot().toDomain());
		
		return domain;
	}

}
