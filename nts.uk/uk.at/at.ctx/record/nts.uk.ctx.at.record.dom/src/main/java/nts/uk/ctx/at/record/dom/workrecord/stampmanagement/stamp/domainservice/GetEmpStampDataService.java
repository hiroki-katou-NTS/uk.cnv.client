package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;

/**
 * DS : 社員の打刻データを取得する	
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.社員の打刻データを取得する
 * @author tutk
 *
 */
public class GetEmpStampDataService {
	
	/**
	 * 	[1] 取得する
	 * @param require
	 * @param employeeId
	 * @param date
	 * @return
	 */
	public static Optional<StampDataOfEmployees> get(Require require,String employeeId,GeneralDate date) {
		
		List<StampCard> listStampCard = require.getListStampCard(employeeId);
		if(listStampCard.isEmpty()) {
			return Optional.empty();
		}
		List<StampNumber> listCard = listStampCard.stream().map(StampCard::getStampNumber).collect(Collectors.toList());
		
		List<Stamp> listStamp = require.getStamp(listCard, date);
		return Optional.of(new StampDataOfEmployees(employeeId, date, listStamp));
		
	}
	
	public static interface Require {
		/**
		 * [R-1] 社員の打刻カード情報を取得する   StampCardRepository
		 * @param sid
		 * @return
		 */
		List<StampCard> getListStampCard(String sid);
		
		/**
		 * [R-3] 打刻を取得する   StampDakokuRepository
		 * @param stampNumbers
		 * @param stampDateTime
		 * @return
		 */
		List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate date);
		
	}

}
