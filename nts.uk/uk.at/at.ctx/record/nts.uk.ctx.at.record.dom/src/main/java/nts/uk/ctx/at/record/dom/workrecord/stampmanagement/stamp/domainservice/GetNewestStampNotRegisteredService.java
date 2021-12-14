package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.shr.com.context.AppContexts;

/**
 * DS: 打刻カード未登録の最新打刻データを取得する
 * path: UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.打刻カード未登録の最新打刻データを取得する
 * 
 * @author chungnt
 *
 */

public class GetNewestStampNotRegisteredService {

	/**
	 * [1] 取得する
	 * 
	 * @param require
	 * @param period
	 * @return List<StampInfoDisp> List<表示する打刻情報>
	 */

	public static List<StampInfoDisp> get(Require require, DatePeriod period, String contractCode) {
				
		// $打刻情報リスト = 打刻カード未登録の打刻データを取得する#取得する(require, 期間)
		List<StampInfoDisp> list = RetrieveNoStampCardRegisteredService.get(require, period, contractCode);

		if (list.isEmpty()) {
			return new ArrayList<StampInfoDisp>();
		}

		// Map<打刻カード番号, List<表示する打刻情報>> 打刻データ = 打刻情報リスト：map groupingBy $.打刻カード番号
		Map<StampNumber, List<StampInfoDisp>> result = list.stream()
				
				.collect(Collectors.groupingBy(StampInfoDisp::getStampNumber, Collectors.toList()));

		return result.values().stream().map(m -> m.stream()
				
				.sorted((x, y) -> y.getStampDatetime().compareTo(x.getStampDatetime()))
				
				.findFirst().orElse(null))
				
				.filter(m -> m != null).collect(Collectors.toList());
	}

	public static interface Require extends RetrieveNoStampCardRegisteredService.Require {

	}
}