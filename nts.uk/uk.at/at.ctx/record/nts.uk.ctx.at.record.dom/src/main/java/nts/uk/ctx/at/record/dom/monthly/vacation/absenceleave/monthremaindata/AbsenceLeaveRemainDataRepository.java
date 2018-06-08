package nts.uk.ctx.at.record.dom.monthly.vacation.absenceleave.monthremaindata;

import java.util.List;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.monthly.vacation.ClosureStatus;

public interface AbsenceLeaveRemainDataRepository {
	/**
	 * 振休月別残数データ
	 * @param employeeId
	 * @param ym
	 * @param status
	 * @return
	 */
	List<AbsenceLeaveRemainData> getDataBySidYmClosureStatus(String employeeId, YearMonth ym, ClosureStatus status);
	/**
	 * 振休月別残数データ 　を追加
	 * @param domain
	 */
	void create(AbsenceLeaveRemainData domain);
	/**
	 * 振休月別残数データ 　を更新
	 * @param domain
	 */
	void update(AbsenceLeaveRemainData domain);
}
