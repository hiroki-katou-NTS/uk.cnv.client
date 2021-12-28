package nts.uk.ctx.at.schedule.ac.budget.premium.attendance;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.function.pub.dailyattendanceitemname.DailyAttendanceItemPub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceNamePriniumAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceNamePriniumDto;
@Stateless
public class AttendanceNamePrimiumAdapterImpl implements AttendanceNamePriniumAdapter {
	@Inject
	private DailyAttendanceItemPub itemPub;
	@Override
	public List<AttendanceNamePriniumDto> getDailyAttendanceItemName(List<Integer> dailyAttendanceItemIds) {
		List<AttendanceNamePriniumDto> data = itemPub.getDailyAttendanceItemName(dailyAttendanceItemIds)
				.stream()
				.map(x -> new AttendanceNamePriniumDto(x.getAttendanceItemId(), x.getAttendanceItemName(), x.getAttendanceItemDisplayNumber()))
				.collect(Collectors.toList());
		return data;
	}

}
