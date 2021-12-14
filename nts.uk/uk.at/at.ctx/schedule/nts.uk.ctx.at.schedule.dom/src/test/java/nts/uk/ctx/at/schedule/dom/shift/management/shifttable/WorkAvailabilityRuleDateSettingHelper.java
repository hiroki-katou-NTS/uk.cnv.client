package nts.uk.ctx.at.schedule.dom.shift.management.shifttable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import mockit.Injectable;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.OneMonth;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.AssignmentMethod;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailability;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityByShiftMaster;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityMemo;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.WorkAvailabilityOfOneDay;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ColorCodeChar6;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.Remarks;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterDisInfor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterName;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class WorkAvailabilityRuleDateSettingHelper {

	public static WorkAvailabilityRuleDateSetting defaultCreate() {
		return new WorkAvailabilityRuleDateSetting(
				new OneMonth(DateInMonth.lastDay()),
				DateInMonth.of(20),
				new HolidayAvailabilityMaxdays(6));
	}

	/**
	 *
	 * @param closureDate 締め日
	 * @param deadline  勤務希望の締切日
	 * @param maxHolidayDays 希望休日の上限
	 * @return
	 */
	public static WorkAvailabilityRuleDateSetting createWithParam(int closureDate, int deadline, int maxHolidayDays) {
		return new WorkAvailabilityRuleDateSetting(
				new OneMonth(DateInMonth.of(closureDate)),
				DateInMonth.of(deadline),
				new HolidayAvailabilityMaxdays(maxHolidayDays));
	}

	/**
	 *
	 * @param require
	 * @param expectingDate 希望日
	 * @param assignmentMethod 勤務希望の指定方法
	 * @return
	 */
	public static WorkAvailabilityOfOneDay createExpectation(
			@Injectable WorkAvailability.Require require,
			GeneralDate expectingDate, AssignmentMethod assignmentMethod) {

		List<ShiftMasterCode> shiftMasterCodeList = assignmentMethod == AssignmentMethod.SHIFT ?
				Arrays.asList(new ShiftMasterCode("S01")) : Collections.emptyList();

		List<TimeSpanForCalc> timeZoneList = assignmentMethod == AssignmentMethod.TIME_ZONE ?
				Arrays.asList(new TimeSpanForCalc(new TimeWithDayAttr(100), new TimeWithDayAttr(200))) : Collections.emptyList();

		return WorkAvailabilityOfOneDay.create(
				require,
				"emp-id",
				expectingDate,
				new WorkAvailabilityMemo("memo"),
				assignmentMethod,
				shiftMasterCodeList,
				timeZoneList);
	}

	/**
	 * createExpectationByShiftMaster
	 * @param expectingDate 希望日
	 * @param shiftMaster シフトの勤務希望
	 * @return
	 */
	public static WorkAvailabilityOfOneDay createExpectationByShiftMaster(GeneralDate expectingDate, WorkAvailabilityByShiftMaster shiftMaster) {
		return new WorkAvailabilityOfOneDay("emp-id", expectingDate, new WorkAvailabilityMemo("memo"), shiftMaster);
	}

	/**
	 * createShiftMasterWithCodeName
	 * @param shiftMasterCode シフトマスタコード
	 * @param shiftMasterName シフトマスタ名称
	 * @return
	 */
	public static ShiftMaster createShiftMasterWithCodeName(String shiftMasterCode, String shiftMasterName) {
		return new ShiftMaster(
				shiftMasterCode + "-sid",
    			new ShiftMasterCode(shiftMasterCode),
    			new ShiftMasterDisInfor(
    					new ShiftMasterName(shiftMasterName),
    					new ColorCodeChar6("000000"),
    					new ColorCodeChar6("000000"),
    					Optional.of(new Remarks(shiftMasterCode + "-r"))),
    			"001",
    			"001",
    			Optional.empty()
    			);

	}
}
