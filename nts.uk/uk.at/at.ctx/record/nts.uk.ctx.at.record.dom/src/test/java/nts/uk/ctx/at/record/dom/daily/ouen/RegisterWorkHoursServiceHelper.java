package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author chungnt
 *
 */

public class RegisterWorkHoursServiceHelper {

	public static List<WorkDetailsParam> get() {
		
		List<WorkDetailsParam> result = new ArrayList<>();
		
		WorkDetailsParam detailsParam = new WorkDetailsParam(new SupportFrameNo(1),
				new TimeZone(
						new WorkTimeInformation(
								new ReasonTimeChange(
										EnumAdaptor.valueOf(0, TimeChangeMeans.class), 
										Optional.empty()),
								new TimeWithDayAttr(100)),
						new WorkTimeInformation(
								new ReasonTimeChange(
										EnumAdaptor.valueOf(0, TimeChangeMeans.class), 
										Optional.empty()),
								new TimeWithDayAttr(1000)),
						Optional.empty()),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());
		
		result.add(detailsParam);
		result.add(detailsParam);
		result.add(detailsParam);
		result.add(detailsParam);
		
		return result;
	}
	
}
