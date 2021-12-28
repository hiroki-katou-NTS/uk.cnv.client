package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroupRepository;

/**
 * 
 * @author sonnlb
 *
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.日別実績の作業時間帯グループ.App.日別実績の作業時間帯グループを取得する.日別実績の作業時間帯グループを取得する
 */
@Stateless
public class GetTaskTimeGroup {

	@Inject
	private TaskTimeGroupRepository repo;

	/**
	 * 取得する
	 * 
	 * @param sId 社員ID
	 * @param period 期間
	 * @return List<日別実績の作業時間帯グループDto>
	 */
	public List<TaskTimeGroupDto> get(String sId, DatePeriod period) {
		// 1. get(社員ID,期間)
		return this.repo.get(sId, period).stream().map(x -> TaskTimeGroupDto.fromDomain(x))
				.collect(Collectors.toList());

	}

}
