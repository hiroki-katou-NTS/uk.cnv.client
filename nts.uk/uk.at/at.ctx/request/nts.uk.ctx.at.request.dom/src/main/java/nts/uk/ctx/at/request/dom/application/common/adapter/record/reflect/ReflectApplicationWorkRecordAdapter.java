package nts.uk.ctx.at.request.dom.application.common.adapter.record.reflect;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;

public interface ReflectApplicationWorkRecordAdapter {
	public Pair<ReflectStatusResult, Optional<AtomTask>> process(Object application, GeneralDate date,
			ReflectStatusResult reflectStatus, GeneralDateTime reflectTime, String execId);
}
