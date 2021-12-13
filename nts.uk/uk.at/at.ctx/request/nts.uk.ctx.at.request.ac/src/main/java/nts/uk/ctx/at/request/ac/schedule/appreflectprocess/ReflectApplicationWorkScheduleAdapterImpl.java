package nts.uk.ctx.at.request.ac.schedule.appreflectprocess;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflectDaily;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.reflect.ReflectApplicationWorkScheduleAdapter;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.ReflectApplicationWorkSchedulePub;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReasonNotReflectDailyExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReasonNotReflectExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReflectStatusResultExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReflectedStateExport;

@Stateless
public class ReflectApplicationWorkScheduleAdapterImpl implements ReflectApplicationWorkScheduleAdapter {

	@Inject
	private ReflectApplicationWorkSchedulePub reflectApplicationWorkSchedulePub;

	@Override
	public Pair<ReflectStatusResult, AtomTask> process(Object application, GeneralDate date,
			ReflectStatusResult reflectStatus, int preAppWorkScheReflectAttr, String execId) {
		val result = reflectApplicationWorkSchedulePub.process(application, date, convertTo(reflectStatus),
				preAppWorkScheReflectAttr, execId);
		return Pair.of(convertToDom(result.getLeft()), result.getRight());
	}

	private SCReflectStatusResultExport convertTo(ReflectStatusResult dom) {
		return new SCReflectStatusResultExport(
				EnumAdaptor.valueOf(dom.getReflectStatus().value, SCReflectedStateExport.class),
				dom.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkRecord().value,
								SCReasonNotReflectDailyExport.class),
				dom.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkSchedule().value,
								SCReasonNotReflectExport.class));
	}

	private ReflectStatusResult convertToDom(SCReflectStatusResultExport export) {
		return new ReflectStatusResult(EnumAdaptor.valueOf(export.getReflectStatus().value, ReflectedState.class),
				export.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkRecord().value,
								ReasonNotReflectDaily.class),
				export.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkSchedule().value, ReasonNotReflect.class));
	}
}
