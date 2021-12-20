package nts.uk.ctx.at.shared.ac.scherec.log.schedulework.schedule;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.adapter.log.schedulework.CorrectRecordDailyResultImport;
import nts.uk.ctx.at.shared.dom.scherec.adapter.log.schedulework.schedule.GetWorkScheduleLogAdapter;
import nts.uk.ctx.sys.log.pub.schedulework.schedule.GetWorkScheduleLogPub;

/**
 * @author thanh_nx
 *
 */
@Stateless
public class GetWorkScheduleLogAc implements GetWorkScheduleLogAdapter {

	@Inject
	private GetWorkScheduleLogPub pub;

	@Override
	public List<CorrectRecordDailyResultImport> getBySpecifyItemId(String sid, GeneralDate targetDate, Integer itemId) {
		return pub.getBySpecifyItemId(sid, targetDate, itemId).stream()
				.map(x -> new CorrectRecordDailyResultImport(x.getSid(), x.getTargetDate(), x.getItemId(),
						x.getCorrectTime()))
				.collect(Collectors.toList());
	}
}
