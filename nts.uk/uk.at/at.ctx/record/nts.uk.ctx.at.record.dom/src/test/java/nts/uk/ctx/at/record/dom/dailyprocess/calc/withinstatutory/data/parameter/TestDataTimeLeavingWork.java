package nts.uk.ctx.at.record.dom.dailyprocess.calc.withinstatutory.data.parameter;

import java.util.Map;
import java.util.function.Function;

import lombok.val;
import nts.uk.ctx.at.record.dom.worklocation.WorkLocationCD;
import nts.uk.ctx.at.record.dom.worktime.TimeActualStamp;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingWork;
import nts.uk.ctx.at.record.dom.worktime.WorkStamp;
import nts.uk.ctx.at.record.dom.worktime.enums.StampSourceInfo;
import nts.uk.ctx.at.shared.dom.common.time.CsvParameter;
import nts.uk.ctx.at.shared.dom.common.time.TestDataCsvRecord;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * TimeLeavingWork(出退勤)のテストデータ
 */
public class TestDataTimeLeavingWork {

	public static Map<String, TimeLeavingWork> build() {
		return CsvParameter.load(
				"/testdata/WithinWorkTimeSheetTest/parameter/TimeLeavingWork.csv",
				buildTimeLeavingWork,
				TimeLeavingWork.class);
		
	}

	/**
	 * WorkStamp
	 */
	static Function<TestDataCsvRecord, WorkStamp> buildWorkStamp = record -> {
		return new WorkStamp(
				record.asInt("AfterRoundingTime", v -> new TimeWithDayAttr(v)),
				record.asInt("timeWithDay", v -> new TimeWithDayAttr(v)),
				record.asStrOpt("locationCode").map(v -> new WorkLocationCD(v)).orElse(null),
				record.asEnum("stampSourceInfo", StampSourceInfo.class));
	};
	
	/**
	 * TimeActualStamp
	 */
	static Function<TestDataCsvRecord, TimeActualStamp> buildTimeActualStamp = record -> {
		val actualStamp = record.child("actualStamp", buildWorkStamp).get();
		val stamp = record.child("stamp", buildWorkStamp).get();
		return new TimeActualStamp(actualStamp, stamp, record.asInt("numberOfReflectionStamp"));
	};
	
	/**
	 * TimeLeavingWork
	 */
	static Function<TestDataCsvRecord, TimeLeavingWork> buildTimeLeavingWork = record -> {
		val workNo = new WorkNo(record.asInt("workNo"));
		val attendanceStamp = record.child("attendanceStamp", buildTimeActualStamp);
		val leaveStamp = record.child("leaveStamp", buildTimeActualStamp);
		return new TimeLeavingWork(workNo, attendanceStamp.get(), leaveStamp.get());
	};
}
