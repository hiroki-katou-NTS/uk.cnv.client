/**
 * 
 */
package nts.uk.ctx.at.record.app.find.dailyperform.supporttime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.OuenWorkTimeSheetOfDailyDto;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.shared.app.util.attendanceitem.FinderFacade;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;

/**
 * @author laitv
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SupportTimeFinder extends FinderFacade{
	
	@Inject
	private OuenWorkTimeSheetOfDailyRepo repo;
	
	@SuppressWarnings("unchecked")
	@Override
	public OuenWorkTimeSheetOfDailyDto find(String employeeId, GeneralDate baseDate) {
		
		OuenWorkTimeSheetOfDaily ouenWorkTimeSheetOfDaily = repo.find(employeeId, baseDate);
		if(ouenWorkTimeSheetOfDaily == null || ouenWorkTimeSheetOfDaily.getOuenTimeSheet().isEmpty())
			return null;
		OuenWorkTimeSheetOfDaily dailyDto = new OuenWorkTimeSheetOfDaily(employeeId, baseDate, ouenWorkTimeSheetOfDaily.getOuenTimeSheet());
		OuenWorkTimeSheetOfDailyDto rs = OuenWorkTimeSheetOfDailyDto.getDto(employeeId, baseDate, dailyDto);
		
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ConvertibleAttendanceItem> List<T> finds(String employeeId, GeneralDate baseDate) {
		
		OuenWorkTimeSheetOfDaily domainDaily = repo.find(employeeId, baseDate);
		if(domainDaily == null || domainDaily.getOuenTimeSheet().isEmpty())
			return null;
		
		OuenWorkTimeSheetOfDailyDto rs = OuenWorkTimeSheetOfDailyDto.getDto(domainDaily);
		return (List<T>) rs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ConvertibleAttendanceItem> List<T> find(List<String> employeeIds, DatePeriod period) {
		List<OuenWorkTimeSheetOfDaily> domainDailys = this.repo.find(employeeIds, period);
		
		List<OuenWorkTimeSheetOfDailyDto> dtos = group(domainDailys);
		return (List<T>) dtos;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConvertibleAttendanceItem> List<T> find(Map<String, List<GeneralDate>> param) {
		
		List<OuenWorkTimeSheetOfDaily> domains = this.repo.find(param);
		List<OuenWorkTimeSheetOfDailyDto> dtos = group(domains);
		return (List<T>) dtos;
	}
	
	private List<OuenWorkTimeSheetOfDailyDto> group(List<OuenWorkTimeSheetOfDaily> domains) {
		Map<String, Map<GeneralDate, List<OuenWorkTimeSheetOfDaily>>> supports = domains.stream()
				.collect(Collectors.groupingBy(OuenWorkTimeSheetOfDaily::getEmpId,
						Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
								.collect(Collectors.groupingBy(OuenWorkTimeSheetOfDaily::getYmd, Collectors.toList())))));

		List<OuenWorkTimeSheetOfDailyDto> dto = new ArrayList<>();

		supports.entrySet().forEach(es -> {
			es.getValue().entrySet().forEach(ves -> {
				ves.getValue().forEach( tves -> {
						dto.add(OuenWorkTimeSheetOfDailyDto.getDto(tves));
				});
			});
		});
		
		return dto;
	}

	@Override
	public Object getDomain(String employeeId, GeneralDate baseDate) {
		val domain = this.repo.find(employeeId, baseDate);
		return domain;
	}
}
