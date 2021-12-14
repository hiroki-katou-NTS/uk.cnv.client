package nts.uk.ctx.at.request.ac.record.dailyperform.appreflect;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.pub.appreflect.ReflectApplicationWorkRecordPub;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReasonNotReflectDailyExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReasonNotReflectExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReflectStatusResultExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReflectedStateExport;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflectDaily;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.reflect.ReflectApplicationWorkRecordAdapter;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;

@Stateless
public class ReflectAppWorkRecordAdapterImpl implements ReflectApplicationWorkRecordAdapter {

	@Inject
	private ReflectApplicationWorkRecordPub pub;

	@Override
	public Pair<ReflectStatusResult, Optional<AtomTask>> process(Object application, GeneralDate date,
			ReflectStatusResult reflectStatus, GeneralDateTime reflectTime) {
		val result =  pub.process(application, date, convertTo(reflectStatus), reflectTime);
		return Pair.of(convertToDom(result.getLeft()), result.getRight());
	}

	private RCReflectStatusResultExport convertTo(ReflectStatusResult dom) {
		return new RCReflectStatusResultExport(
				EnumAdaptor.valueOf(dom.getReflectStatus().value, RCReflectedStateExport.class),
				dom.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkRecord().value,
								RCReasonNotReflectDailyExport.class),
				dom.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(dom.getReasonNotReflectWorkSchedule().value,
								RCReasonNotReflectExport.class));
	}
	
	private ReflectStatusResult convertToDom(RCReflectStatusResultExport export) {
		return new ReflectStatusResult(EnumAdaptor.valueOf(export.getReflectStatus().value, ReflectedState.class),
				export.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkRecord().value,
								ReasonNotReflectDaily.class),
				export.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(export.getReasonNotReflectWorkSchedule().value, ReasonNotReflect.class));
	}
}
