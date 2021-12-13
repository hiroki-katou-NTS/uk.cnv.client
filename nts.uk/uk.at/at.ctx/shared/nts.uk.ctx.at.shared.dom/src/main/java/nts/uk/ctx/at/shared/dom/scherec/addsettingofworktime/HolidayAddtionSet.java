/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/

package nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.serialize.binary.SerializableWithOptional;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeFrame;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;

/**
 * The Class HolidayAddtionSet.
 */
@Getter
@NoArgsConstructor
// 休暇加算時間設定
public class HolidayAddtionSet extends AggregateRoot implements SerializableWithOptional {

    /**
     * Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * The company id.
     */
    // 会社ID
    private String companyId;

    /**
     * The addition vacation set.
     */
    // 加算休暇設定
    private LeaveSetAdded additionVacationSet;

    // Refactor code Q&A 114177
    // 参照先
    private RefDesForAdditionalTakeLeave reference;

    /**
     * The time holiday addition.
     */
    /*時間休暇加算*/
    private List<TimeHolidayAdditionSet> timeHolidayAddition;

    private void writeObject(ObjectOutputStream stream) {
        writeObjectWithOptional(stream);
    }

    private void readObject(ObjectInputStream stream) {
        readObjectWithOptional(stream);
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.dom.DomainObject#validate()
     */
    @Override
    public void validate() {
        super.validate();
    }

    /**
     * Creates the from java type.
     *
     * @param companyId            the company id
     * @param reference            the reference
     * @param additionVacationSet  the addition vacation set
     * @param timeHolidayAddition  the time holiday addition
     * @return the holiday addtion set
     */
    public static HolidayAddtionSet createFromJavaType(String companyId, RefDesForAdditionalTakeLeave reference, LeaveSetAdded additionVacationSet,
                                                       List<TimeHolidayAdditionSet> timeHolidayAddition) {
        return new HolidayAddtionSet(companyId,
                reference,
                additionVacationSet,
                timeHolidayAddition);
    }

    /**
     * Instantiates a new holiday addtion set.
     *
     * @param companyId                              the company id
     * @param reference                              the reference
     * @param additionVacationSet                    the addition vacation set
     * @param timeHolidayAddition                    the time holiday addition
     */
    public HolidayAddtionSet(String companyId,
                             RefDesForAdditionalTakeLeave reference,
                             LeaveSetAdded additionVacationSet,
                             List<TimeHolidayAdditionSet> timeHolidayAddition) {
        super();
        this.companyId = companyId;
        this.additionVacationSet = additionVacationSet;
        this.timeHolidayAddition = timeHolidayAddition;
        this.reference = reference;
    }

	/**
	 * 時間休暇加算時間を計算する
	 * @param integrationOfDaily 日別勤怠(Work)
	 * @param deductionTimeSheet 控除時間帯
	 * @param calcMethodSet 休暇の計算方法の設定
	 * @param frames 就業時間内時間枠
	 * @param workTimeForm 就業時間帯の勤務形態
	 * @return 時間休暇加算時間
	 */
	public AttendanceTime calcTimeVacationAddTime(IntegrationOfDaily integrationOfDaily, DeductionTimeSheet deductionTimeSheet,
			HolidayCalcMethodSet calcMethodSet, List<WithinWorkTimeFrame> frames, WorkTimeForm workTimeForm) {
		if(!integrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			return AttendanceTime.ZERO;
		}
		// 遅刻の時間休暇加算時間を取得する
		List<LateTimeOfDaily> lateDailies = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getLateTimeOfDaily();
		AttendanceTime lateTime = new AttendanceTime(lateDailies.stream()
				.map(l -> l.calcVacationAddTime(calcMethodSet, this, frames, workTimeForm))
				.collect(Collectors.summingInt(a -> a.valueAsMinutes())));
		// 早退の時間休暇加算時間を取得する
		List<LeaveEarlyTimeOfDaily> leaveEarlyDailies = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getLeaveEarlyTimeOfDaily();
		AttendanceTime leaveEarlyTime = new AttendanceTime(leaveEarlyDailies.stream()
				.map(l -> l.calcVacationAddTime(calcMethodSet, this, frames, workTimeForm))
				.collect(Collectors.summingInt(a -> a.valueAsMinutes())));
		// 外出の時間休暇加算時間を取得する
		AttendanceTime outTime = new AttendanceTime(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getOutingTimeOfDaily().stream()
					.filter(o -> o.getReason().isPrivateOrUnion())
					.map(o -> o.calcVacationAddTime(calcMethodSet, this, deductionTimeSheet, workTimeForm))
					.collect(Collectors.summingInt(a -> a.valueAsMinutes())));
		
		AttendanceTime totalTime = AttendanceTime.ZERO;
		return totalTime
				.addMinutes(lateTime.valueAsMinutes())
				.addMinutes(leaveEarlyTime.valueAsMinutes())
				.addMinutes(outTime.valueAsMinutes());
	}

	/**
	 * 時間休暇加算時間を取得する
	 * @param useTime 時間休暇使用時間
	 * @param offsetTarget 相殺対象時間
	 * @param workTimeForm 就業時間帯の勤務形態
	 * @return 時間休暇加算時間
	 */
	public AttendanceTime getAddTime(
			TimevacationUseTimeOfDaily useTime,
			AttendanceTime offsetTarget,
			WorkTimeForm workTimeForm) {
		
		// 加算使用時間の計算
		int addUseMinutes = useTime.calcTotalVacationAddTime(Optional.of(this), AdditionAtr.WorkingHoursOnly);
		// 相殺時間を計算する
		int offsetMinutes = Math.min(addUseMinutes, offsetTarget.valueAsMinutes());
		// 時間休暇加算設定を取得する
		Optional<TimeHolidayAdditionSet> flowSet = this.getTimeHolidayAdditionSet(workTimeForm);
		if (!flowSet.isPresent()){
			// 固定勤務の時、相殺時間を返す
			if (workTimeForm.isFixed()) return new AttendanceTime(offsetMinutes);
			throw new RuntimeException("TimeHolidayAdditionSet not exist");
		}
		// 加算する時間を判断する
		return flowSet.get().getAddTime(new AttendanceTime(addUseMinutes), new AttendanceTime(offsetMinutes));
	}
	
	/**
	 * 時間休暇加算設定を取得する
	 * @param workTimeForm 就業時間帯の勤務形態
	 * @return 時間休暇加算設定
	 */
	public Optional<TimeHolidayAdditionSet> getTimeHolidayAdditionSet(WorkTimeForm workTimeForm) {
		if(workTimeForm.isFlex()) {
			return this.getTimeHolidayAddition().stream()
					.filter(t -> t.getWorkClass() == WorkClassOfTimeHolidaySet.WORK_FOR_FLEX)
					.findFirst();
		}
		if(workTimeForm.isFlow()) {
			return this.getTimeHolidayAddition().stream()
					.filter(t -> t.getWorkClass() == WorkClassOfTimeHolidaySet.WORK_FOR_FLOW)
					.findFirst();
		}
		return Optional.empty();
	}
}
