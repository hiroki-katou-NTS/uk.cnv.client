package nts.uk.ctx.at.function.app.command.processexecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapterDto;
import nts.uk.ctx.at.function.dom.processexecution.TempAbsenceHistoryService;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionTask;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.DateHistoryItemImport;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.SharedTempAbsenceAdapter;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.TempAbsenceHisItemImport;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.TempAbsenceImport;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class TempAbsenceHistoryServiceImpl implements TempAbsenceHistoryService {

	@Inject
	private SharedTempAbsenceAdapter sharedTempAbsenceAdapter;

	@Inject
	private RecordWorkInfoFunAdapter recordWorkInfoFunAdapter;

	@Inject
	private WorkTypeRepository workTypeRepository;

	@Inject
	private WorkScheduleRepository workScheduleRepository;

	@Override
	public TempAbsenceImport getTempAbsence(String cid, DatePeriod period, List<String> employeeIds) {
		return this.sharedTempAbsenceAdapter.getTempAbsence(cid, period, employeeIds);
	}

	@Override
	public List<DatePeriod> findChangingLeaveHistoryPeriod(String sid, DatePeriod period, TempAbsenceImport tempAbsence,
			boolean isRecreateLeave, ProcessExecutionTask procExecTask) {
		List<DatePeriod> periodList = new ArrayList<>();
		CheckConditions checkConditions = new CheckConditions();
		String cid = AppContexts.user().companyId();
		// INPUT「休職・休業者再作成」をチェックする
		// INPUT「休職・休業者再作成」 = FALSE
		if (!isRecreateLeave) {
			// INPUT「処理期間」をOUTPUTとして返す
			return Arrays.asList(period);
		}

		// Filter history list
		List<DateHistoryItemImport> histories = tempAbsence.getHistories().stream()
				.filter(data -> data.getEmployeeId().equals(sid)).map(data -> data.getDateHistoryItems())
				.flatMap(Collection::stream).collect(Collectors.toList());

		DatePeriod newPeriod = null;
		Map<GeneralDate, String> workTypeCodeMap = this.getWorkTypeCodeMap(procExecTask, sid, period);
		// INPUT．「期間」をループする
		for (GeneralDate date : period.datesBetween()) {
			Optional<String> workTypeCode = workTypeCodeMap.entrySet().stream().filter(e -> e.getKey().equals(date))
					.map(Entry::getValue).findFirst();
			if (workTypeCode.isPresent()) {
				// ドメインモデル「勤務種類」を取得する
				Optional<WorkType> optWorkType = this.workTypeRepository.findByPK(cid, workTypeCode.get());
				if (optWorkType.isPresent()) {
					WorkType workType = optWorkType.get();
					// 取得したドメインモデル「勤務種類」とINPUT「休職休業履歴項目」を比較する
					checkConditions = this.compareLeaveHistory(sid, histories, tempAbsence.getHistoryItem(),
							checkConditions, workType, date);
					// 休職休業履歴差異 == TRUE AND 作成中期間 == FALSE AND 勤務種類差異 = TRUE
					if (checkConditions.canStartPeriod()) {
						checkConditions.setCreatingPeriod(true);
						// 「期間」を作成する
						newPeriod = new DatePeriod(date, null);
					}
					// （休職休業履歴差異 == FALSE AND 作成中期間 == TRUE）
					// OR（休職休業履歴差異 == TRUE AND 作成中期間 == TRUE AND 勤務種類差異 = FALSE）
					else if (checkConditions.canEndPeriod()) {
						checkConditions.setCreatingPeriod(false);
						// 「期間」の終了日を更新する
						newPeriod = new DatePeriod(newPeriod.start(), date.addDays(-1));
						// OUTPUT「期間（List）」に作成した「期間」を追加する
						periodList.add(newPeriod);
					}
				}
			}
		}
		// 処理の「期間」の終了日がNULLであるかチェック
		// NULL の場合
		if (newPeriod != null && newPeriod.end() == null) {
			// 「期間」の終了日を更新してOUTPUT「期間（List）」に追加する
			newPeriod = new DatePeriod(newPeriod.start(), period.end());
			periodList.add(newPeriod);
		}
		return periodList;
	}

	/**
	 * 取得したドメインモデル「勤務種類」とINPUT「休職休業履歴項目」を比較する
	 * 
	 * @return
	 */
	private CheckConditions compareLeaveHistory(String sid, List<DateHistoryItemImport> histItemList,
			List<TempAbsenceHisItemImport> historyItem, CheckConditions checkConditions, WorkType workType,
			GeneralDate date) {
		// 【比較条件】
		// 処理中の年月日が「休職休業履歴．期間」に含まれているかチェックする
		Optional<DateHistoryItemImport> optHist = histItemList.stream()
				.filter(data -> data.getStartDate().beforeOrEquals(date) && data.getEndDate().afterOrEquals(date))
				.findAny();
		if (!optHist.isPresent()) {
			// 含まれてない ⇒ 休職休業履歴差異 = False
			checkConditions.setLeaveHistoryDifferent(false);
			return checkConditions;
		}
		// 含まれている ⇒ 休職休業履歴差異 = True
		checkConditions.setLeaveHistoryDifferent(true);
		DateHistoryItemImport hist = optHist.get();
		// 休職休業履歴項目
		// ※履歴IDが「休職休業履歴」同じもの
		Optional<TempAbsenceHisItemImport> optHistItem = historyItem.stream()
				.filter(data -> data.getHistoryId().equals(hist.getHistoryId())).findAny();
		if (!optHist.isPresent()) {
			checkConditions.setLeaveHistoryDifferent(false);
			return checkConditions;
		}
		TempAbsenceHisItemImport histItem = optHistItem.get();
		// ◆それ以外は ⇒ 勤務種類差異 = True
		boolean isWorkTypeDifferent = true;
		if (workType.getDailyWork().isOneDay()) {
			// 「勤務種類．１日の勤務．勤務区分」 = 1日 AND 「勤務種類．１日の勤務．1日」= 休職 の場合
			if (workType.getDailyWork().getOneDay().equals(WorkTypeClassification.LeaveOfAbsence)) {
				// ・「休職休業履歴項目．NO」 == 1 ⇒ 勤務種類差異 = False
				// ・それ以外 ⇒ 勤務種類差異 = True
				isWorkTypeDifferent = histItem.getTempAbsenceFrNo() != 1;
			}
			// 「勤務種類．１日の勤務．勤務区分」 = 1日 AND 「勤務種類．１日の勤務．1日」= 休業 の場合
			if (workType.getDailyWork().getOneDay().equals(WorkTypeClassification.Closure)) {
				// ・「休職休業履歴項目．NO」 == 「勤務種類．勤務種類設定．休業区分」 ⇒ 勤務種類差異 = False
				// ・それ以外 ⇒ 勤務種類差異 = True
				isWorkTypeDifferent = workType.getWorkTypeSetList().stream()
						.anyMatch(data -> data.getCloseAtr().value + 2 != histItem.getTempAbsenceFrNo());
			}
		}
		checkConditions.setWorkTypeDifferent(isWorkTypeDifferent);
		return checkConditions;
	}

	private Map<GeneralDate, String> getWorkTypeCodeMap(ProcessExecutionTask processExecutionTask, String sid,
			DatePeriod period) {
		switch (processExecutionTask) {
		case SCH_CREATION:
			// 「勤務予定Repository．get*()」を実行する
			return this.workScheduleRepository.getListBySid(sid, period).stream()
					.sorted(Comparator.comparing(WorkSchedule::getYmd)).collect(Collectors.toMap(WorkSchedule::getYmd,
							data -> data.getWorkInfo().getRecordInfo().getWorkTypeCode().v()));
		case DAILY_CREATION:
		case DAILY_CALCULATION:
			// ドメインモデル「日別実績の勤務情報」を取得する
			return this.recordWorkInfoFunAdapter.findByEmpAndPeriod(sid, period).stream()
					.sorted(Comparator.comparing(RecordWorkInfoFunAdapterDto::getWorkingDate)).collect(Collectors.toMap(
							RecordWorkInfoFunAdapterDto::getWorkingDate, RecordWorkInfoFunAdapterDto::getWorkTypeCode));
		default:
			return Collections.emptyMap();
		}
	}

	@Setter
	@NoArgsConstructor
	private class CheckConditions {
		// 休職休業履歴差異
		private boolean isLeaveHistoryDifferent;

		// 作成中期間
		private boolean isCreatingPeriod;

		// 勤務種類差異
		private boolean isWorkTypeDifferent;

		public boolean canStartPeriod() {
			return this.isLeaveHistoryDifferent && !this.isCreatingPeriod && this.isWorkTypeDifferent;
		}

		public boolean canEndPeriod() {
			return (!this.isLeaveHistoryDifferent && this.isCreatingPeriod)
					|| (this.isLeaveHistoryDifferent && this.isCreatingPeriod && !this.isWorkTypeDifferent);
		}
	}
}
