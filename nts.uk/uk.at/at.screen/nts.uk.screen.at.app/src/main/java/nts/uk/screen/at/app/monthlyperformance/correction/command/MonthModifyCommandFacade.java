package nts.uk.screen.at.app.monthlyperformance.correction.command;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.command.monthly.MonthlyRecordWorkCommand;
import nts.uk.ctx.at.record.app.command.monthly.MonthlyRecordWorkCommandHandler;
import nts.uk.ctx.at.record.app.find.monthly.finder.MonthlyRecordWorkFinder;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyQuery;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;


@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MonthModifyCommandFacade {

	@Inject
	private MonthlyRecordWorkCommandHandler commandHandler;
	
	@Inject
	private MonthlyRecordWorkFinder finder;
	
	@Inject
	private OptionalItemRepository optionalItemRepo;
	
	public void handleUpdate(MonthlyModifyQuery query) {
		MonthlyRecordWorkDto dto = toDto(query);
		MonthlyRecordWorkCommand comand = createCommand(dto, query);
		this.commandHandler.handleUpdate(comand);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void handleUpdate(List<MonthlyRecordWorkCommand> commands) {
		this.commandHandler.handleUpdate(commands);
	}

	public MonthlyRecordWorkDto toDto(MonthlyModifyQuery query) {
		MonthlyRecordWorkDto oldValues = finder.find(query.getEmployeeId(), new YearMonth(query.getYearMonth()),
				ClosureId.valueOf(query.getClosureId()),
				new ClosureDate(query.getClosureDate().getClosureDay(), query.getClosureDate().getLastDayOfMonth()));
		oldValues = AttendanceItemUtil.fromItemValues(oldValues, query.getItems(), AttendanceItemType.MONTHLY_ITEM);
		
		if(oldValues.getAffiliation() != null){
			oldValues.getAffiliation().setVersion(query.getVersion());
		}
		if(oldValues.getAttendanceTime() != null){
			oldValues.getAttendanceTime().setVersion(query.getVersion());
		}
		
		
		return oldValues;
	}
	
	public List<MonthlyRecordWorkCommand> createMultiCommand(List<MonthlyModifyQuery> query,List<MonthlyRecordWorkDto> values) {
		Set<String> emps = new HashSet<>();
		Set<YearMonth> yearmonth = new HashSet<>();
		query.stream().forEach(q -> {
			emps.add(q.getEmployeeId());
			yearmonth.add(new YearMonth(q.getYearMonth()));
		});
		//List<MonthlyRecordWorkDto> oldValues = finder.find(emps, yearmonth);
		return values.stream().map(v -> {
			MonthlyModifyQuery q = query.stream().filter(qr -> {
				return qr.getClosureId() == v.getClosureID() && qr.getEmployeeId().equals(v.getEmployeeId())
						&& v.yearMonth().compareTo(qr.getYearMonth()) == 0 && v.getClosureDate().equals(qr.getClosureDate());
			}).findFirst().orElse(null);
			if(q == null){
				return null;
			}
			//fix bug 106911	
//			IntegrationOfMonthly domain = v.toDomain(v.employeeId(), v.yearMonth(), v.getClosureID(), v.getClosureDate());
//			MonthlyRecordWorkDto dtoNew = MonthlyRecordWorkDto.fromOnlyAttTime(domain);
			MonthlyRecordWorkDto dto = AttendanceItemUtil.fromItemValues(v, q.getItems(), AttendanceItemType.MONTHLY_ITEM);
			Map<Integer, OptionalItem> master = optionalItemRepo.findAll(AppContexts.user().companyId())
																.stream().collect(Collectors.toMap(c -> c.getOptionalItemNo().v(), c -> c));
			dto.getAnyItem().correctItems(master);

			if(dto.getAffiliation() != null){
				dto.getAffiliation().setVersion(q.getVersion());
			}
			if(dto.getAttendanceTime() != null){
				dto.getAttendanceTime().setVersion(q.getVersion());
			}
			return createCommand(dto, q);
		}).filter(v -> v != null).collect(Collectors.toList());
	}

	private MonthlyRecordWorkCommand createCommand(MonthlyRecordWorkDto dto, MonthlyModifyQuery query) {
		MonthlyRecordWorkCommand command = new MonthlyRecordWorkCommand();
		command.withData(dto).fromItems(query.getItems());
		command.yearMonth(new YearMonth(query.getYearMonth()));
		command.closureDate(query.getClosureDate());
		command.closureId(query.getClosureId());
		command.forEmployee(query.getEmployeeId());
		return command;
	}
}
