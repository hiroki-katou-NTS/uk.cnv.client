package nts.uk.ctx.at.function.dom.dailyattendanceitem.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.function.dom.adapter.DailyAttendanceItemAdapterDto;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.AttendanceItemLinking;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.enums.TypeOfItem;
import nts.uk.ctx.at.function.dom.attendanceitemname.service.AttendanceItemNameService;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

/*
 * NamPT
 * Set name of dailyAttendanceItem
 * 勤怠項目に対応する名称を生成する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DailyAttendanceItemNameDomainServiceImpl implements DailyAttendanceItemNameDomainService {
	
	@Inject
	private AttendanceItemNameService attendanceItemNameService; 

	@Inject
	private CompanyDailyItemService companyDailyItemService;
	@Override
	public List<DailyAttendanceItem> getNameOfDailyAttendanceItem(List<Integer> dailyAttendanceItemIds) {
		//
		LoginUserContext login = AppContexts.user();
		String companyId = login.companyId();
		List<AttItemName> data = companyDailyItemService.getDailyItems(companyId, Optional.empty(), dailyAttendanceItemIds, null);
		return data.stream()
				.map(item -> {
					DailyAttendanceItem dto = new DailyAttendanceItem();
					dto.setAttendanceItemId(item.getAttendanceItemId());
					dto.setAttendanceItemName(item.getAttendanceItemName());
					dto.setAttendanceItemDisplayNumber(item.getAttendanceItemDisplayNumber());
					dto.setTypeOfAttendanceItem(item.getTypeOfAttendanceItem());
					dto.setFrameCategory(item.getFrameCategory());
					dto.setDisplayName(item.getOldName());
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public List<DailyAttendanceItem> getNameOfDailyAttendanceItemNew(
			List<DailyAttendanceItemAdapterDto> dailyAttendanceItems,
			List<AttendanceItemLinking> attendanceItemAndFrameNos) {
		List<AttItemName> attendanceItems = dailyAttendanceItems.stream().map(item -> {
			AttItemName dto = new AttItemName();
			dto.setAttendanceItemId(item.getAttendanceItemId());
			dto.setAttendanceItemName(item.getAttendanceName());
			dto.setAttendanceItemDisplayNumber(item.getDisplayNumber());
			dto.setTypeOfAttendanceItem(item.getDailyAttendanceAtr());
			return dto;
		}).collect(Collectors.toList());
		attendanceItemAndFrameNos = attendanceItemAndFrameNos.stream()
				.filter(item -> item.getTypeOfAttendanceItem().equals(TypeOfItem.Daily)).collect(Collectors.toList());
		return attendanceItemNameService.getNameOfAttendanceItem(attendanceItems, attendanceItemAndFrameNos).stream()
				.map(item -> {
					DailyAttendanceItem dto = new DailyAttendanceItem();
					dto.setAttendanceItemId(item.getAttendanceItemId());
					dto.setAttendanceItemName(item.getAttendanceItemName());
					dto.setAttendanceItemDisplayNumber(item.getAttendanceItemDisplayNumber());
					dto.setTypeOfAttendanceItem(item.getTypeOfAttendanceItem());
					dto.setFrameCategory(item.getFrameCategory());
					return dto;
				}).collect(Collectors.toList());
	}

}
