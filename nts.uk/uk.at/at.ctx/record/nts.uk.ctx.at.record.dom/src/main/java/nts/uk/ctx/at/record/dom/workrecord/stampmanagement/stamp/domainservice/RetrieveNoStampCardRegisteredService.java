package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;

/**
 * DS : 打刻カード未登録の打刻データを取得する
 * 
 * @author tutk
 *
 */ 
public class RetrieveNoStampCardRegisteredService {

	public static List<StampInfoDisp> get(Require require, DatePeriod period, String contractCode) {
		List<StampRecord> listStampRecord = require.getStempRcNotResgistNumber(period);
		List<Stamp> listStamp = require.getStempRcNotResgistNumberStamp(contractCode, period);
		return createStampInfoDisplay(listStampRecord, listStamp);
	}

	/**
	 * [prv-1] 表示する打刻情報を作成する
	 * 
	 * @param listStampRecord
	 * @param listStamp
	 * @return
	 */
	private static List<StampInfoDisp> createStampInfoDisplay(List<StampRecord> listStampRecord,
			List<Stamp> listStamp) {
		// $打刻記録 in 打刻記録リスト
		return listStampRecord.stream().map(rc -> {
			// $対象打刻 = $打刻 in 打刻リスト : find $打刻記録.打刻カード番号 = $打刻.打刻カード番号 AND
			// $打刻記録.打刻日時 = $打刻.打刻日時
			Optional<Stamp> stamp = listStamp.stream().filter(s -> rc.getStampDateTime().equals(s.getStampDateTime())
					&& rc.getStampNumber().equals(s.getCardNumber())).findFirst();
			// map 表示する打刻情報#打刻区分を作成する($打刻記録.打刻カード番号, $打刻記録.打刻日時, $打刻記録.表示する打刻区分,
			// $対象打刻)
			return new StampInfoDisp(rc.getStampNumber(), rc.getStampDateTime(), rc.getStampTypeDisplay().v(), stamp);
		}).collect(Collectors.toList());

	}

	public static interface Require {
		/**
		 * [R-1] 打刻記録を取得する StampRecordRepository
		 * 
		 * @param period
		 * @return
		 */
		public List<StampRecord> getStempRcNotResgistNumber(DatePeriod period);

		/**
		 * [R-2] 打刻を取得する StampDakokuRepository
		 * 
		 * @param period
		 * @return
		 */
		public List<Stamp> getStempRcNotResgistNumberStamp(String contractCode, DatePeriod period);

	}
}
