package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;

public interface StampRecordRepository {

	/**
	 * [1] insert(打刻記録)
	 * @param stampRecord
	 */
	public void insert(StampRecord stampRecord);

	/**
	 * [2] delete(打刻記録)
	 * @param contractCd
	 * @param stampNumber
	 * @param stampDateTime
	 */
	public void delete(String contractCd, String stampNumber, GeneralDateTime stampDateTime);

	/**
	 * [3] update(打刻記録)
	 * @param stampRecord
	 */
	public void update(StampRecord stampRecord);

	/**
	 * [4] 取得する
	 * @param stampNumbers
	 * @param stampDateTime
	 * @return 
	 */
	public List<StampRecord> get(String contractCode, List<StampNumber> stampNumbers, GeneralDate stampDate);

	/**
	 *  [5] 打刻カード未登録の打刻記録データを取得する
	 * @param period
	 * @return List<打刻記録>	
	 */
	public List<StampRecord> getStempRcNotResgistNumber(String contractCode, DatePeriod period);
	
	/**
	 * @param stampNumber
	 * @param stampDateTime
	 * @return
	 */
	public Optional<StampRecord> findByKey(StampNumber stampNumber, GeneralDateTime stampDateTime);

	/*** [6] 取得する
	 * 
	 * @param contractCd
	 *            契約コード
	 * @param stampNumber
	 *            打刻カード番号
	 * @param stampDateTime
	 *            打刻日時
	 * @return Optional<打刻記録>
	 */
	public Optional<StampRecord> get(String contractCd, String stampNumber, GeneralDateTime stampDateTime);

}
