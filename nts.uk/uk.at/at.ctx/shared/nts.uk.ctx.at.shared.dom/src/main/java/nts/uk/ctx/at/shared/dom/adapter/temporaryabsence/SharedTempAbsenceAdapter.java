package nts.uk.ctx.at.shared.dom.adapter.temporaryabsence;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;

public interface SharedTempAbsenceAdapter {
    /**
     * 社員（List）と期間から休職休業を取得する
     *
     * @param cid         会社ID
     * @param period      期間
     * @param employeeIds List<社員ID>
     * @return List<休職休業履歴, 休職休業履歴項目>
     */
    TempAbsenceImport getTempAbsence(String cid, DatePeriod period, List<String> employeeIds);
    
    /**
     * 社員ID（List）と期間から期間中に１日でも休職・休業している社員を取得する
     * @param sids
     * @param period
     * @return
     */
    List<String> getAbsenceEmpsByPeriod(List<String> sids, DatePeriod period);
    
    /**
     * 枠番号から該当する休職休業枠を取得する
     * 
     * @param cid					会社ID
     * @param tempAbsenceFrameNos	枠NO（List）
     * @return
     */
    List<TempAbsenceFrameImport> getTempAbsenceFrameByListNo(String cid, List<Integer> tempAbsenceFrameNos);
}
