package nts.uk.ctx.at.record.app.find.dailyperform.workrecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.OuenWorkTimeOfDailyDto;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.shared.app.util.attendanceitem.FinderFacade;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class OuenWorkTimeFinder extends FinderFacade {

	@Inject
	private OuenWorkTimeOfDailyRepo repo;
	
	@SuppressWarnings("unchecked")
	@Override
	public OuenWorkTimeOfDailyDto find(String employeeId, GeneralDate baseDate) {
		Optional<OuenWorkTimeOfDaily> domain = this.repo.find(employeeId, baseDate);
		
		if(!domain.isPresent()) {
			return new OuenWorkTimeOfDailyDto();
		}
		return OuenWorkTimeOfDailyDto.from(employeeId, baseDate, domain.get().getOuenTimes());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ConvertibleAttendanceItem> List<T> find(List<String> employeeId, DatePeriod datePeriod) {
		return (List<T>)this.repo.find(employeeId, datePeriod).stream()
				.map(d -> OuenWorkTimeOfDailyDto.from(d.getEmpId(), d.getYmd(), d.getOuenTimes()))
				.collect(Collectors.toList());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConvertibleAttendanceItem> List<T> find(Map<String, List<GeneralDate>> param) {
		return (List<T>) param.keySet().stream()
						.map(emp -> param.get(emp).stream().map(date -> find(emp, date))
													.filter(dto -> dto.isHaveData())
													.collect(Collectors.toList()))
						.flatMap(List::stream)
						.collect(Collectors.toList());
	}
	
	@Override
	public Object getDomain(String employeeId, GeneralDate baseDate) {
		return this.repo.find(employeeId, baseDate);
	}
}
