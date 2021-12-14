package nts.uk.ctx.at.shared.ac.temporaryabsence;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.DateHistoryItemImport;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.SharedTempAbsenceAdapter;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.TempAbsenceFrameImport;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.TempAbsenceHisItemImport;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.TempAbsenceHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.TempAbsenceImport;
import nts.uk.ctx.bs.employee.pub.temporaryabsence.TempAbsenceExport;
import nts.uk.ctx.bs.employee.pub.temporaryabsence.TempAbsencePub;

@Stateless
public class SharedTempAbsenceAdapterImpl implements SharedTempAbsenceAdapter {

    @Inject
    private TempAbsencePub tempAbsencePub;

    @Override
    public TempAbsenceImport getTempAbsence(String cid, DatePeriod period, List<String> employeeIds) {
        TempAbsenceExport tempAbsence = tempAbsencePub.getTempAbsence(cid, period, employeeIds);
        return new TempAbsenceImport(
                tempAbsence.getHistories().stream().map(x ->
                        new TempAbsenceHistoryImport(
                                x.getCompanyId(),
                                x.getEmployeeId(),
                                x.getDateHistoryItems().stream().map(c ->
                                        new DateHistoryItemImport(
                                                c.getHistoryId(),
                                                c.getStartDate(),
                                                c.getEndDate()))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList()),
                tempAbsence.getHistoryItem().stream().map(x ->
                        new TempAbsenceHisItemImport(
                                x.getTempAbsenceFrNo(),
                                x.getTempAbsenceFrName(),
                                x.getHistoryId(),
                                x.getEmployeeId(),
                                x.getRemarks(),
                                x.getSoInsPayCategory(),
                                x.getFamilyMemberId()))
                        .collect(Collectors.toList()));
    }

	@Override
	public List<String> getAbsenceEmpsByPeriod(List<String> sids, DatePeriod period) {
		return this.tempAbsencePub.getAbsenceEmpsByPeriod(sids, period);
	}

	@Override
	public List<TempAbsenceFrameImport> getTempAbsenceFrameByListNo(String cid, List<Integer> tempAbsenceFrameNos) {
		return this.tempAbsencePub.getTempAbsenceFrameByListNo(cid, tempAbsenceFrameNos).stream()
				.map(data -> new TempAbsenceFrameImport(data.getCompanyId(), data.getTempAbsenceFrNo(),
						data.getUseClassification(), data.getTempAbsenceFrName()))
				.collect(Collectors.toList());
	}
}
