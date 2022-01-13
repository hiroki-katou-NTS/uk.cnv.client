package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.List;
import java.util.Map;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.WorkDetailData;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;


public interface OuenWorkTimeSheetOfDailyRepo {

	public OuenWorkTimeSheetOfDaily find(String empId, GeneralDate ymd);
	
	public void update(List<OuenWorkTimeSheetOfDaily> domain);
	
	public void insert(List<OuenWorkTimeSheetOfDaily> domain);
	
	public void delete(List<OuenWorkTimeSheetOfDaily> domain);

	List<WorkDetailData> getWorkDetailData(List<String> empIdList, List<String> wkplIdList, DatePeriod period);

	public void remove(String sid, GeneralDate ymd);

	boolean findPK(String empId, GeneralDate ymd, int ouenNo);

	List<OuenWorkTimeSheetOfDaily> find(String sid, DatePeriod ymd);
	
	List<OuenWorkTimeSheetOfDaily> find(List<String> sid, DatePeriod ymd);
	
	List<OuenWorkTimeSheetOfDaily> find(Map<String, List<GeneralDate>> param);

	void removePK(String sid, GeneralDate ymd, int ouenNo);

	public void persist(List<OuenWorkTimeSheetOfDaily> domain);
	
	/**
	 * [2]  削除する
	 * 指定したキー条件に該当するレコードを削除する
	 * @param sId 社員ID
	 * @param ymd 年月日
	 * @param supportFrameNo 枠NO
	 */
	public void deleteBySupFrameNo(String sId, GeneralDate ymd, SupportFrameNo supportFrameNo);
	
	/**
	 * [9]  日別実績の応援作業別勤怠時間帯を取得する
	 * @param sId 社員ID
	 * @param dates 年月日リスト
	 * @return 応援作業時間帯
	 */
	public List<OuenWorkTimeSheetOfDaily> get(String sId, List<GeneralDate> dates);
}
