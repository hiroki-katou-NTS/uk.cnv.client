package nts.uk.ctx.at.record.dom.daily.timegroup;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * RP: 日別実績の作業時間帯グループRepository
 * 
 * @author tutt
 *
 */
public interface TaskTimeGroupRepository {

	/**
	 * [1] Insert(日別実績の作業時間帯グループ)
	 * 
	 * @param timeGroup 日別実績の作業時間帯グループ
	 */
	void insert(TaskTimeGroup timeGroup);

	/**
	 * [2] Delete(社員ID,年月日)
	 * 
	 * @param sId  社員ID
	 * @param date 年月日
	 */
	void delete(String sId, GeneralDate date);

	/**
	 * [3] Get 指定社員の1日の日別実績の作業時間帯グループを取得する
	 * 
	 * @param sId  社員ID
	 * @param date 年月日
	 * @return 日別実績の作業時間帯グループ
	 */
	Optional<TaskTimeGroup> get(String sId, GeneralDate date);

	/**
	 * [4] Get* 指定社員の期間分の日別実績の作業時間帯グループを取得する
	 * 
	 * @param sId  社員ID
	 * @param date 年月日
	 * @return 日別実績の作業時間帯グループ
	 */
	List<TaskTimeGroup> get(String sId, DatePeriod period);

}
