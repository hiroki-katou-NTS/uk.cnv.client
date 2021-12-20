package nts.uk.ctx.at.record.app.find.monthly.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.app.find.monthly.root.AbsenceLeaveRemainDataDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AffiliationInfoOfMonthlyDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AgreementTimeOfManagePeriodDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AnnLeaRemNumEachMonthDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AnyItemOfMonthlyDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AttendanceTimeOfMonthlyDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyCareHdRemainDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyChildCareHdRemainDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyDayoffRemainDataDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyPublicHolidayRemainDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRemarksDto;
import nts.uk.ctx.at.record.app.find.monthly.root.RsvLeaRemNumEachMonthDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.MonthlyItemCommon;
import nts.uk.ctx.at.record.app.find.monthly.root.dto.SpecialHolidayRemainDataDtoWrap;
import nts.uk.ctx.at.record.dom.monthly.TimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.TimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.app.util.attendanceitem.MonthlyFinderFacade;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.MonthMergeKey;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class MonthlyRecordWorkFinder extends MonthlyFinderFacade {

//	@Inject
//	private AffiliationInfoOfMonthlyFinder affi;
//	
//	@Inject
//	private AttendanceTimeOfMonthlyFinder attendanceTime;
//	
//	@Inject
//	private AnnLeaRemNumEachMonthFinder annLeaFinder;
//	
//	@Inject
//	private RsvLeaRemNumEachMonthFinder rsvLeaFinder;
	
	@Inject
	private AnyItemOfMonthlyFinder anyItemFinder;

//	@Inject
//	private DayOffRemainMonthFinder dayOffFinder;
//	
//	@Inject
//	private SpecialHolidayRemainMonthFinder specialHolidayFinder;
//	
//	@Inject
//	private AbsenceLeaveRemainMonthFinder absenceLeaveFinder;
	
	@Inject
	private MonthlyRemarksFinder remarksFinder;
	
//	@Inject
//	private MonthlyCareRemainFinder careFinder;
//	
//	@Inject
//	private MonthlyChildCareRemainFinder childCareFinder;
	
	@Inject 
	private AgreementTimeOfManagePeriodFinder agreementFinder; 
	
	@Inject
	private TimeOfMonthlyRepository timeRepo;
	
	@Inject
	private RemainMergeRepository remainRepo;
	
	@Override
	@SuppressWarnings("unchecked")
	public MonthlyRecordWorkDto find(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		MonthlyRecordWorkDto dto = new MonthlyRecordWorkDto();
		dto.setClosureDate(ClosureDateDto.from(closureDate));
		dto.setClosureID(closureId.value);
		dto.setEmployeeId(employeeId);
		dto.setYearMonth(yearMonth);
		
		timeRepo.find(employeeId, yearMonth, closureId, closureDate).ifPresent(t -> {
			dto.setAffiliation(AffiliationInfoOfMonthlyDto.from(t.getAffiliation().orElse(null)));
			dto.setAttendanceTime(AttendanceTimeOfMonthlyDto.from(t.getAttendanceTime().orElse(null)));
			remainRepo.find(new MonthMergeKey(employeeId, yearMonth, closureId, closureDate)).ifPresent(r -> {
				dto.setAnnLeave(AnnLeaRemNumEachMonthDto.from(r.getAnnLeaRemNumEachMonth()));
				dto.setRsvLeave(RsvLeaRemNumEachMonthDto.from(r.getRsvLeaRemNumEachMonth()));
				dto.setDayOff(MonthlyDayoffRemainDataDto.from(r.getMonthlyDayoffRemainData()));
				dto.setAbsenceLeave(AbsenceLeaveRemainDataDto.from(r.getAbsenceLeaveRemainData()));
				dto.setSpecialHoliday(SpecialHolidayRemainDataDtoWrap.from(r.getSpecialHolidayRemainData()));
				dto.setCare(MonthlyCareHdRemainDto.from(r.getMonCareHdRemain()));
				dto.setChildCare(MonthlyChildCareHdRemainDto.from(r.getMonChildHdRemain()));
				dto.setPublicHoliday(MonthlyPublicHolidayRemainDto.from(r.getMonPublicHoliday()));
			});

			dto.setAgreementTime(agreementFinder.find(employeeId, yearMonth, closureId, closureDate));
			dto.setRemarks(remarksFinder.find(employeeId, yearMonth, closureId, closureDate));
			dto.setAnyItem(anyItemFinder.find(employeeId, yearMonth, closureId, closureDate));
			
			dto.exsistData();
		});
		
		return dto;
	}

	public <T extends ConvertibleAttendanceItem> List<T> find(Collection<String> employeeId, YearMonthPeriod range) {
		return find(employeeId, ConvertHelper.yearMonthsBetween(range));
	}

	@Override
	public <T extends ConvertibleAttendanceItem> List<T> find(Collection<String> employeeId, DatePeriod range) {
		return find(employeeId, ConvertHelper.yearMonthsBetween(range));
	}

	@Override
	public <T extends ConvertibleAttendanceItem> List<T> find(Collection<String> employeeId, YearMonth yearMonth) {
		return find(employeeId, Arrays.asList(yearMonth));
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConvertibleAttendanceItem> List<T> find(Collection<String> employeeId,
			Collection<YearMonth> yearMonth) {
		List<String> empIds = new ArrayList<>(employeeId);
		List<YearMonth> yearmonths = new ArrayList<>(yearMonth);
		List<TimeOfMonthly> time = timeRepo.findBySidsAndYearMonths(empIds, yearmonths);
		List<RemainMerge> remain = remainRepo.findBySidsAndYearMonths(empIds, yearmonths);
//		List<AffiliationInfoOfMonthlyDto> aff = affi.find(employeeId, yearMonth);
//		List<AttendanceTimeOfMonthlyDto> att = attendanceTime.find(employeeId, yearMonth);
		List<AnyItemOfMonthlyDto> any = anyItemFinder.find(employeeId, yearMonth);
//		List<AnnLeaRemNumEachMonthDto> ann = annLeaFinder.find(employeeId, yearMonth);
//		List<RsvLeaRemNumEachMonthDto> rsv = rsvLeaFinder.find(employeeId, yearMonth);
//		List<MonthlyDayoffRemainDataDto> dayOff = dayOffFinder.find(employeeId, yearMonth);
//		List<AbsenceLeaveRemainDataDto> absenceLeave = absenceLeaveFinder.find(employeeId, yearMonth);
//		List<SpecialHolidayRemainDataDto> specialHoliday = specialHolidayFinder.find(employeeId, yearMonth);
		List<MonthlyRemarksDto> remarks = remarksFinder.find(employeeId, yearMonth);
//		List<MonthlyCareHdRemainDto> care = careFinder.find(employeeId, yearMonth);
//		List<MonthlyChildCareHdRemainDto> childCare = childCareFinder.find(employeeId, yearMonth);
		List<AgreementTimeOfManagePeriodDto> agreement = agreementFinder.find(employeeId, yearMonth);
		return (List<T>) time.stream().map(a -> {
			AffiliationInfoOfMonthly aff = a.getAffiliation().get();
			MonthlyRecordWorkDto dto = new MonthlyRecordWorkDto();
			dto.setClosureDate(ClosureDateDto.from(aff.getClosureDate()));
			dto.setClosureID(aff.getClosureId().value);
			dto.setEmployeeId(aff.getEmployeeId());
			dto.setYearMonth(aff.getYearMonth());
			dto.setAffiliation(AffiliationInfoOfMonthlyDto.from(aff));
			dto.setAttendanceTime(AttendanceTimeOfMonthlyDto.from(a.getAttendanceTime().orElse(null)));
			filterRemain(remain, aff).ifPresent(r -> {
				dto.setAnnLeave(AnnLeaRemNumEachMonthDto.from(r.getAnnLeaRemNumEachMonth()));
				dto.setRsvLeave(RsvLeaRemNumEachMonthDto.from(r.getRsvLeaRemNumEachMonth()));
				dto.setDayOff(MonthlyDayoffRemainDataDto.from(r.getMonthlyDayoffRemainData()));
				dto.setAbsenceLeave(AbsenceLeaveRemainDataDto.from(r.getAbsenceLeaveRemainData()));
				dto.setSpecialHoliday(SpecialHolidayRemainDataDtoWrap.from(r.getSpecialHolidayRemainData()));
				dto.setCare(MonthlyCareHdRemainDto.from(r.getMonCareHdRemain()));
				dto.setChildCare(MonthlyChildCareHdRemainDto.from(r.getMonChildHdRemain()));
				dto.setPublicHoliday(MonthlyPublicHolidayRemainDto.from(r.getMonPublicHoliday()));
			});
			dto.setAnyItem(filterItem(any, aff));
			dto.setRemarks(filterItem(remarks, aff));
			dto.setAgreementTime(filterItemX(agreement, aff));
			dto.exsistData();
			return dto;
		}).collect(Collectors.toList());
	}
	
	private Optional<RemainMerge> filterRemain(List<RemainMerge> remains, AffiliationInfoOfMonthly aff) {
		return remains.stream().filter(at ->  at.getMonthMergeKey().getEmployeeId().equals(aff.getEmployeeId()) 
											&& at.getMonthMergeKey().getYearMonth().equals(aff.getYearMonth())
											&& at.getMonthMergeKey().getClosureDate().equals(aff.getClosureDate())
											&& at.getMonthMergeKey().getClosureDate().getClosureDay().equals(aff.getClosureDate().getClosureDay())
											&& at.getMonthMergeKey().getClosureDate().getLastDayOfMonth() == aff.getClosureDate().getLastDayOfMonth())
							.findFirst();
	}

	private <T extends MonthlyItemCommon> T filterItemX(List<T> att, AffiliationInfoOfMonthly a) {
		return att.stream().filter(at ->  at.employeeId().equals(a.getEmployeeId()) 
											&& at.yearMonth().equals(a.getYearMonth())).findFirst().orElse(null);
	}

	private <T extends MonthlyItemCommon> T filterItem(List<T> att, AffiliationInfoOfMonthly a) {
		return att.stream().filter(at -> 
				at.getClosureDate().getClosureDay() == a.getClosureDate().getClosureDay().v()
				&& at.getClosureDate().getLastDayOfMonth() == a.getClosureDate().getLastDayOfMonth()
				&& at.employeeId().equals(a.getEmployeeId()) 
				&& at.yearMonth().equals(a.getYearMonth()) 
				&& at.getClosureID() == a.getClosureId().value).findFirst().orElse(null);
	}
	
	private <T extends MonthlyItemCommon> List<T> filterItems(List<T> att, AffiliationInfoOfMonthly a) {
		return att.stream().filter(at -> 
				at.getClosureDate().getClosureDay() == a.getClosureDate().getClosureDay().v()
				&& at.getClosureDate().getLastDayOfMonth() == a.getClosureDate().getLastDayOfMonth()
				&& at.employeeId().equals(a.getEmployeeId()) 
				&& at.yearMonth().equals(a.getYearMonth()) 
				&& at.getClosureID() == a.getClosureId().value).collect(Collectors.toList());
	}
}
