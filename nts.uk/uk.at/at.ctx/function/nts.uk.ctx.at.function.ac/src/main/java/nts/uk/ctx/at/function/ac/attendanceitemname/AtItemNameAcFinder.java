package nts.uk.ctx.at.function.ac.attendanceitemname;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.enums.TypeOfItem;
import nts.uk.ctx.at.function.dom.attendanceitemname.AttendanceItemName;
import nts.uk.ctx.at.function.dom.attendanceitemname.service.AttendanceItemNameService;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItem;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AtItemNameAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.TypeOfItemImport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.service.CompanyMonthlyItemService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

@Stateless
public class AtItemNameAcFinder implements AtItemNameAdapter {

	@Inject
	private AttendanceItemNameService attendanceItemNameService;

	@Override
	public List<AttItemName> getNameOfAttendanceItem(List<Integer> attendanceItemIds, TypeOfItemImport type) {
		return attendanceItemNameService.getNameOfAttendanceItem(attendanceItemIds,
				EnumAdaptor.valueOf(type.value, TypeOfItem.class));
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	//	to ver7
	public List<AttItemName> getNameOfDailyAttendanceItem(List<DailyAttendanceItem> attendanceItems) {
		List<AttItemName> attItemName = attendanceItems.stream().map(x -> {
			AttItemName dto = new AttItemName();
			dto.setAttendanceItemId(x.getAttendanceItemId());
			// to ver7
			dto.setOldName(x.getAttendanceName().v());
			dto.setAttendanceItemName(x.getDisplayName().isPresent() && !x.getDisplayName().get().v().trim().equals("") ? x.getDisplayName().get().v() : x.getAttendanceName().v());
			dto.setAttendanceItemDisplayNumber(x.getDisplayNumber());
			dto.setNameLineFeedPosition(x.getNameLineFeedPosition());
			dto.setUserCanUpdateAtr(x.getUserCanUpdateAtr().value);
			dto.setTypeOfAttendanceItem(x.getDailyAttendanceAtr().value);
			// to ver7
			dto.setFrameCategory(null);
			dto.setAttendanceAtr(x.getDailyAttendanceAtr().value); //because dto.typeOfAttendanceItem is override in AttendanceItemNameService.getNameOfAttendanceItem()
			return dto;
		}).collect(Collectors.toList());
		// to ver7
		return attendanceItemNameService.getNameOfAttendanceItem(TypeOfItem.Daily, attItemName);
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	// to ver7
	public List<AttItemName> getNameOfMonthlyAttendanceItem(List<MonthlyAttendanceItem> attendanceItems) {
		List<AttItemName> attItemName = attendanceItems.stream().map(x -> {
			AttItemName dto = new AttItemName();
			dto.setAttendanceItemId(x.getAttendanceItemId());
			// to ver7
			dto.setOldName(x.getAttendanceName().v());
			dto.setAttendanceItemName(x.getDisplayName().isPresent() && !x.getDisplayName().get().v().trim().equals("") ? x.getDisplayName().get().v() : x.getAttendanceName().v());
			dto.setAttendanceItemDisplayNumber(x.getDisplayNumber());
			dto.setNameLineFeedPosition(x.getNameLineFeedPosition());
			dto.setUserCanUpdateAtr(x.getUserCanUpdateAtr().value);
			dto.setTypeOfAttendanceItem(x.getMonthlyAttendanceAtr().value);
			// to ver7
			dto.setFrameCategory(null);
			dto.setAttendanceAtr(x.getMonthlyAttendanceAtr().value); //because dto.typeOfAttendanceItem is override in AttendanceItemNameService.getNameOfAttendanceItem()
			return dto;
		}).collect(Collectors.toList());
		// to ver7
		return attendanceItemNameService.getNameOfAttendanceItem(TypeOfItem.Monthly, attItemName);
	}

	List<AttItemName> convertAttItem(List<AttendanceItemName> attItems) {
		return attItems.stream().map(x -> {
			AttItemName dto = new AttItemName();
			dto.setAttendanceItemId(x.getAttendanceItemId());
			dto.setAttendanceItemName(x.getAttendanceItemName());
			dto.setAttendanceItemDisplayNumber(x.getAttendanceItemDisplayNumber());
			dto.setTypeOfAttendanceItem(x.getTypeOfAttendanceItem());
			dto.setFrameCategory(x.getFrameCategory());
			dto.setNameLineFeedPosition(x.getNameLineFeedPosition());
			dto.setUserCanUpdateAtr(x.getUserCanUpdateAtr());
			return dto;
		}).collect(Collectors.toList());
	}
	
	@Inject
	private DailyAttendanceItemRepository dailyAttendanceItemRepo;

	@Inject
	private MonthlyAttendanceItemRepository monthlyAttendanceItemRepo;
	
	@Inject
	private CompanyMonthlyItemService companyMonthlyItemService;
	
	@Inject
	private CompanyDailyItemService companyDailyItemService;
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AttItemName> getNameOfAttdItemByType(TypeOfItemImport type) {
		String companyId = AppContexts.user().companyId();
		List<Integer> listAllId = new ArrayList<>(); 
		List<AttItemName> data = new ArrayList<>();
		if(type ==TypeOfItemImport.Daily) {
			listAllId = dailyAttendanceItemRepo.getList(companyId).stream().map(c->c.getAttendanceItemId()).collect(Collectors.toList());
			data = companyDailyItemService.getDailyItems(companyId, Optional.empty(), listAllId, null);
		}else if(type ==TypeOfItemImport.Monthly) {
			listAllId = monthlyAttendanceItemRepo.findAll(companyId).stream().map(c->c.getAttendanceItemId()).collect(Collectors.toList());
			data = companyMonthlyItemService.getMonthlyItems(companyId, Optional.empty(), listAllId, null);
		}
		
//		List<AttItemName> data = this.getNameOfAttendanceItem(listAllId, type);
		if(data.isEmpty())
			return Collections.emptyList();
		return data;
	}
}
