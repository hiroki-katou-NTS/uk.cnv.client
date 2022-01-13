package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.DailyLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.IGetDailyLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.StatusActualDay;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.ScWorkplaceAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.AffWorkplaceHistoryItem;
import nts.uk.ctx.at.shared.app.service.workrule.closure.ClosureEmploymentService;

/**
 * 
 * @author sonnlb
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.日の実績のロック状況を取得する.日の実績のロック状況を取得する
 */
@Stateless
public class GetLockStatusOfDay {

	@Inject
	private ScWorkplaceAdapter scWorkplaceAdapter;

	@Inject
	private ClosureEmploymentService closureEmploymentService;

	@Inject
	private IGetDailyLock iGetDailyLock;

	/**
	 * 
	 * @param sId 社員ID
	 * @param period 対象期間
	 * @return List<日別実績のロック状態> List<日の実績のロック状態> 2 cái này là một, cơ mà có vẻ
	 * như thiết kế viết sai
	 */
	public List<DailyLock> get(String sId, DatePeriod period) {

		List<DailyLock> result = new ArrayList<>();
		// 対象期間をループする
		period.datesBetween().forEach(date -> {
			// 社員と基準日から所属職場履歴項目を取得する
			AffWorkplaceHistoryItem affItem = this.scWorkplaceAdapter.getAffWkpHistItemByEmpDate(sId, date);
			// 社員に対応する処理締めを取得する
			this.closureEmploymentService.findClosureByEmployee(sId, date).ifPresent(c -> {
				// 日の実績の状況を取得する
				DailyLock dailyLock = this.iGetDailyLock
						.getDailyLock(new StatusActualDay(sId, date, affItem.getWorkplaceId(), c.getClosureId().value));

				result.add(dailyLock);
			});

		});

		return result;
	}
}
