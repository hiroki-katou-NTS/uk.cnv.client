package nts.uk.ctx.at.function.pubimp.dailyattendanceitemname;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.function.dom.dailyattendanceitem.repository.DailyAttendanceItemNameDomainService;
import nts.uk.ctx.at.function.pub.dailyattendanceitemname.DailyAttendanceItemPub;
import nts.uk.ctx.at.function.pub.dailyattendanceitemname.DailyAttendanceItemPubDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DailyAttendanceItemPubImpl implements DailyAttendanceItemPub{
	
	@Inject
	private DailyAttendanceItemNameDomainService dailyAttendanceItemNameDomainService;

	@Override
	public List<DailyAttendanceItemPubDto> getDailyAttendanceItemName(List<Integer> dailyAttendanceItemIds) {
		return this.dailyAttendanceItemNameDomainService.getNameOfDailyAttendanceItem(dailyAttendanceItemIds).stream().map(f -> {
			return new DailyAttendanceItemPubDto(
					f.getAttendanceItemId(), 
					f.getAttendanceItemName(), 
					f.getAttendanceItemDisplayNumber(), 
					f.getTypeOfAttendanceItem(), 
					f.getFrameCategory(),
					f.getDisplayName());
		}).collect(Collectors.toList());
	}

}
