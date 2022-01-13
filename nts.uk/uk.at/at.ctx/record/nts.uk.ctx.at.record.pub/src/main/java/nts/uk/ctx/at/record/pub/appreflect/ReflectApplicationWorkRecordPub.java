package nts.uk.ctx.at.record.pub.appreflect;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReflectStatusResultExport;

public interface ReflectApplicationWorkRecordPub {

	public Pair<RCReflectStatusResultExport, Optional<AtomTask>> process(Object application, GeneralDate date,
			RCReflectStatusResultExport reflectStatus, GeneralDateTime reflectTime, String execId);
}
