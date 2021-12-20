package nts.uk.ctx.at.record.dom.adapter.specificdatesetting;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.specificdatesetting.RecSpecificDateSettingImport;

public interface RecSpecificDateSettingAdapter {

	/**
	 * 
	 * @param companyID
	 * @param workPlaceID
	 * @param date
	 * @return
	 */
	public RecSpecificDateSettingImport specificDateSettingService(String companyID, String workPlaceID, GeneralDate date);
	
	/**
	 * reqList490
	 * @param companyID
	 * @param list workPlaceID
	 * @param date
	 * @return
	 */
	public RecSpecificDateSettingImport specificDateSettingServiceByListWpl(String companyID, List<String> workPlaceID, GeneralDate date);
	
	public List<RecSpecificDateSettingImport> getList(String companyID, List<String> workPlaceID, DatePeriod datePeriod);
	
	/**
	 * Get list 特定日項目 by 特定日項目No
	 * @param companyId
	 * @param specifiDateNos
	 * @return
	 */
	List<RecSpecificDateItemImport> getSpecifiDateItem(String companyId, List<Integer> specifiDateNos);
	
	/**
	 * Find 職場の特定日設定を取得する, not get work place children
	 * @param companyID company id
	 * @param workPlaceID work place id
	 * @param date date
	 * @return 職場の特定日設定を取得
	 */
	RecSpecificDateSettingImport findSpecDateSetByWkpLst(String companyID, List<String> workPlaceID, GeneralDate date);
}
