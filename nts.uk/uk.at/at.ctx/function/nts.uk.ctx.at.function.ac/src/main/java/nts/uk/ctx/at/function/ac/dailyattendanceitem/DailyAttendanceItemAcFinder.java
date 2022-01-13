package nts.uk.ctx.at.function.ac.dailyattendanceitem;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.DailyAttendanceItemAdapter;
import nts.uk.ctx.at.function.dom.adapter.DailyAttendanceItemAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceItemValueImport;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceResultImport;
import nts.uk.ctx.at.record.pub.dailyattendanceitem.AttendanceItemService;
import nts.uk.ctx.at.record.pub.dailyattendanceitem.AttendanceItemValue;
import nts.uk.ctx.at.record.pub.dailyattendanceitem.AttendanceResult;
import nts.uk.ctx.at.shared.pub.scherec.dailyattendanceitem.DailyAttendanceItemRecPub;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DailyAttendanceItemAcFinder implements DailyAttendanceItemAdapter {

	@Inject
	private DailyAttendanceItemRecPub dailyAttendanceItemPub;
	
	@Inject
	private AttendanceItemService attendanceItemService;

	@Override
	public List<DailyAttendanceItemAdapterDto> getDailyAttendanceItem(String companyId,
			List<Integer> dailyAttendanceItemIds) {
		return this.dailyAttendanceItemPub.getDailyAttendanceItem(companyId, dailyAttendanceItemIds).stream().map(f -> {
			return new DailyAttendanceItemAdapterDto(f.getCompanyId(), f.getAttendanceItemId(), f.getAttendanceName(),
					f.getDisplayNumber(), f.getUserCanUpdateAtr(), f.getDailyAttendanceAtr(),
					f.getNameLineFeedPosition(), f.getDisplayName());
		}).collect(Collectors.toList());
	}

	@Override
	public List<DailyAttendanceItemAdapterDto> getDailyAttendanceItemList(String companyId) {
		return this.dailyAttendanceItemPub.getDailyAttendanceItemList(companyId).stream().map(f -> {
			return new DailyAttendanceItemAdapterDto(f.getCompanyId(), f.getAttendanceItemId(), f.getAttendanceName(),
					f.getDisplayNumber(), f.getUserCanUpdateAtr(), f.getDailyAttendanceAtr(),
					f.getNameLineFeedPosition(), f.getDisplayName());
		}).collect(Collectors.toList());
	}

	@Override
	public AttendanceResultImport getValueOf(String employeeId, GeneralDate workingDate, List<Integer> itemIds) {
		AttendanceResult  attendanceResult= attendanceItemService.getValueOf(employeeId, workingDate, itemIds);
		return buildResultImport(attendanceResult);
	}

	@Override
	public List<AttendanceResultImport> getValueOf(List<String> employeeIds, DatePeriod workingDate, List<Integer> itemIds) {
		List<AttendanceResult> listAttendanceResult =  attendanceItemService.getValueOf(employeeIds, workingDate, itemIds);
		return listAttendanceResult.stream().map( e -> buildResultImport(e)).collect(Collectors.toList());
	}
	
	private AttendanceItemValueImport buildValueImport(AttendanceItemValue export) {
		return  new AttendanceItemValueImport(export.getValueType(), export.getItemId(), export.getValue());
	}
	
	private AttendanceResultImport buildResultImport(AttendanceResult export) {
		return AttendanceResultImport.builder().employeeId(export.getEmployeeId()).workingDate(export.getWorkingDate())
				.attendanceItems(export.getAttendanceItems().stream().map(value -> buildValueImport(value)).collect(Collectors.toList())).build();		
	}
}
