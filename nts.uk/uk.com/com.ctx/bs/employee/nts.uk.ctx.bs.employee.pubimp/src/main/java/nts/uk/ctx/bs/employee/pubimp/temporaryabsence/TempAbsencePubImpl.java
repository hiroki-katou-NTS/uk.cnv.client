package nts.uk.ctx.bs.employee.pubimp.temporaryabsence;


import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsHistRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsItemRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHisItem;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHistory;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.TempAbsenceFrame;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.TempAbsenceFrameNo;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.TempAbsenceRepositoryFrame;
import nts.uk.ctx.bs.employee.pub.employee.TempAbsenceFrameExport;
import nts.uk.ctx.bs.employee.pub.temporaryabsence.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class TempAbsencePubImpl implements TempAbsencePub {

    @Inject
    private TempAbsHistRepository tempAbsHistRepo;
    @Inject
    private TempAbsItemRepository tempAbsItemRepo;
    @Inject
    private TempAbsenceRepositoryFrame tempAbsenceRepositoryFrame;

    @Override
    public TempAbsenceExport getTempAbsence(String cid, DatePeriod period, List<String> employeeIds) {

        Map<TempAbsenceFrameNo, String> tempAbsenceFrames = tempAbsenceRepositoryFrame.findByCid(cid)
                .stream().collect(Collectors.toMap(TempAbsenceFrame::getTempAbsenceFrNo, x -> x.getTempAbsenceFrName().v()));
        List<TempAbsenceHistory> tempAbsMap = tempAbsHistRepo.getByListSid(employeeIds, period);

        List<String> historyIds = new ArrayList<>();
        tempAbsMap.forEach(x -> x.getDateHistoryItems().forEach(c -> historyIds.add(c.identifier())));

        List<TempAbsenceHisItem> mapTempAbsItemMap = tempAbsItemRepo.getItemByHitoryIdList(historyIds);
        List<TempAbsenceHisItemExport> leaveHistItems = mapTempAbsItemMap.stream().map(x ->
                new TempAbsenceHisItemExport(
                        x.getTempAbsenceFrNo().v().intValue(),
                        tempAbsenceFrames.getOrDefault(x.getTempAbsenceFrNo(), ""),
                        x.getHistoryId(),
                        x.getEmployeeId(),
                        x.getRemarks().v(),
                        x.getSoInsPayCategory(),
                        x.getFamilyMemberId()
                )).collect(Collectors.toList());

        List<TempAbsenceHistoryExport> leaveHists = tempAbsMap.stream().map(x ->
                new TempAbsenceHistoryExport(
                        x.getCompanyId(),
                        x.getEmployeeId(),
                        x.getDateHistoryItems().stream().map(i ->
                                new DateHistoryItemExport(
                                        i.identifier(),
                                        i.start(),
                                        i.end()))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        return new TempAbsenceExport(leaveHists, leaveHistItems);
    }

	@Override
	public List<String> getAbsenceEmpsByPeriod(List<String> sids, DatePeriod period) {
		// ドメインモデル「休職休業履歴」を取得する
		List<TempAbsenceHistory> tempAbsenceHistories = this.tempAbsHistRepo.getByListSid(sids, period);
		// 「休職休業履歴」が取得できた社員ID一覧を返す
		return tempAbsenceHistories.stream().map(TempAbsenceHistory::getEmployeeId).collect(Collectors.toList());
	}

	@Override
	public List<TempAbsenceFrameExport> getTempAbsenceFrameByListNo(String cid, List<Integer> tempAbsenceFrameNos) {
		return this.tempAbsenceRepositoryFrame.findByCidAndFrameNos(cid, tempAbsenceFrameNos).stream()
				.map(data -> new TempAbsenceFrameExport(cid, data.getTempAbsenceFrNo().v().intValue(),
						data.getUseClassification().value, data.getTempAbsenceFrName().v()))
				.collect(Collectors.toList());
	}
}
