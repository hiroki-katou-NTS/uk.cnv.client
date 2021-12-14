package nts.uk.ctx.at.schedule.pub.schedule.workschedule;

import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * @author thanh_nx 勤務予定を取得する
 */
public interface WorkScheduleToIntegrationOfDailyPub {

	public Optional<Object> getWorkSchedule(String sid, GeneralDate date);

}
