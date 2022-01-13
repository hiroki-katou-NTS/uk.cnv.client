package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;

/**
 * 打刻Repository
 */
public interface StampDakokuRepository {

	/**
	 * [1] insert(打刻)
	 * @param stamp
	 */
	public void insert(Stamp stamp);

	/**
	 * [2] delete(打刻)
	 * @param stampNumber
	 * @param stampDateTime
	 */
	public void delete(String contractCode, String stampNumber, GeneralDateTime stampDateTime, int changeClockArt);

	/**
	 * [3] update(打刻)
	 * @param stamp
	 */
	public void update(Stamp stamp);

	/**
	 * [4] 取得する
	 * 
	 * @param 契約コード
	 *            contractCode
	 * @param stampNumbers
	 * @param stampDateTime
	 * @return
	 */
	public List<Stamp> get(String contractCode, List<StampNumber> stampNumbers, GeneralDate stampDate);

	/**
	 * [5] 打刻カード未登録の打刻データを取得する
	 * 
	 * @param 契約コード
	 *            contractCode
	 * @param period
	 * @return
	 */
	public List<Stamp> getStempRcNotResgistNumberStamp(String contractCode, DatePeriod period);
	
	public List<Stamp> getByListCard(List<String> stampNumbers);
	
	public List<Stamp> getByDateperiod(String companyId,DatePeriod period);
	
	public List<Stamp> getByCardAndPeriod(String companyId,List<String> listCard,DatePeriod period);

	public List<Stamp> getByDateTimeperiod(List<String> listCard, GeneralDateTime startDate, GeneralDateTime endDate);

	public Optional<Stamp> get(String contractCode, StampNumber stampNumber);
	
	//[6] exists(契約コード,打刻カード番号, 打刻日時,時刻変更区分)
	public boolean existsStamp(ContractCode contractCode, StampNumber stampNumber, GeneralDateTime dateTime,
			ChangeClockAtr changeClockArt) ;

}
