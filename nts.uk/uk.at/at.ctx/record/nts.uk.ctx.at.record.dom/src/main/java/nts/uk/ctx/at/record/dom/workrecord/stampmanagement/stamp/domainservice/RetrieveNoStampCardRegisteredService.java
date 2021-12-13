package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.List;
import java.util.stream.Collectors;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;

/**
 * DS : 打刻カード未登録の打刻データを取得する
 * 
 * @author tutk
 *
 */
public class RetrieveNoStampCardRegisteredService {
	// [1] 取得する
	public static List<StampInfoDisp> get(Require require, DatePeriod period) {
		
		//$打刻リスト = require.打刻を取得する(期間)	
		List<Stamp> listStamp = require.getStempRcNotResgistNumberStamp(period);
		// return $打刻リスト: 表示する打刻情報#新規作成($)
		return listStamp.stream().map(stamp -> new StampInfoDisp(stamp)).collect(Collectors.toList());
	}

	public static interface Require {

		/**
		 * [R-2] 打刻を取得する StampDakokuRepository
		 * 
		 * @param period
		 * @return
		 */
		public List<Stamp> getStempRcNotResgistNumberStamp(DatePeriod period);

	}
}
