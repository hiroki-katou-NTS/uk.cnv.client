package nts.uk.ctx.at.request.dom.application.common.adapter.schedule.reflect;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;

public interface ReflectApplicationWorkScheduleAdapter {
	
	public Pair<ReflectStatusResult, AtomTask> process(Object application, GeneralDate date,
			ReflectStatusResult reflectStatus, int preAppWorkScheReflectAttr, String execId);

}
