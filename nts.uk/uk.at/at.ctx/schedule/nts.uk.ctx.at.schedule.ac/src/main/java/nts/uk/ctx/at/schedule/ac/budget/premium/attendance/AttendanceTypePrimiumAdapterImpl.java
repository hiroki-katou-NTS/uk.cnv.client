package nts.uk.ctx.at.schedule.ac.budget.premium.attendance;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.function.pub.attendancetype.AttendanceTypePub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceTypePriServiceDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceTypePrimiumAdapter;
@Stateless
public class AttendanceTypePrimiumAdapterImpl implements AttendanceTypePrimiumAdapter {
	@Inject
	private AttendanceTypePub atPub;
	@Override
	public List<AttendanceTypePriServiceDto> getItemByScreenUseAtr(String companyId, int screenUseAtr) {
		List<AttendanceTypePriServiceDto> data = atPub.getItemByScreenUseAtr(companyId, screenUseAtr).stream()
				.map(x -> new AttendanceTypePriServiceDto(x.getAttendanceItemId()))
				.collect(Collectors.toList());
		return data;
	}

}
