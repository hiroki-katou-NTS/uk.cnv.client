package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.closegetunlockedperiod.ClosingGetUnlockedPeriod;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLock;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;

/**
 * 実績処理できる期間を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.実績処理できる期間を取得する.実績処理できる期間を取得する
 * 
 * @author tutk
 *
 */
@Stateless
public class GetPeriodCanProcesse {

	/**
	 * 
	 * @param require
	 * @param employeeId           社員Id
	 * @param period               期間
	 * @param listEmploymentHis    所属雇用履歴一覧
	 * @param IgnoreFlagDuringLock ロック中の計算/集計できるか
	 * @param achievementAtr       実績区分（日別｜月別）
	 * @return 期間一覧
	 */
	public static List<DatePeriod> get(Require require, String employeeId, DatePeriod period,
			List<EmploymentHistoryImported> listEmploymentHis, IgnoreFlagDuringLock ignoreFlagDuringLock,
			AchievementAtr achievementAtr) {
		// 最新のドメインモデル「締め状態管理」を取得する
		List<ClosureStatusManagement> listClosureStatus = require.getAllByEmpId(employeeId);
		List<DatePeriod> listPeriod = new ArrayList<>();
		Optional<DatePeriod> optPeriod = Optional.empty();
		if (!listClosureStatus.isEmpty()) {
			listClosureStatus = listClosureStatus.stream()
					.sorted(Comparator.comparing(ClosureStatusManagement::getYearMonth)
							.thenComparing(Comparator.comparing(data -> data.getPeriod().end())).reversed())
					.collect(Collectors.toList());
			ClosureStatusManagement dataMax = listClosureStatus.get(0);
			// 締めされていない期間を求める
			optPeriod = dataMax.closureStateManagenent(period);
			if (!optPeriod.isPresent()) {
				return listPeriod;
			}
		}else {
			//⁂注意：締め状態管理取得できない場合、パラメータ。期間を使う
			optPeriod = Optional.of(period);
		}
		for (EmploymentHistoryImported employmentHis : listEmploymentHis) {
			//雇用履歴に含まれる期間を取得る
			Optional<DatePeriod> optP = Optional.empty();
			if (employmentHis.getPeriod().start().beforeOrEquals(optPeriod.get().start())) {
				optP = getGeneralPeriod(employmentHis.getPeriod(), optPeriod.get());
			} else {
				optP = getGeneralPeriod(optPeriod.get(), employmentHis.getPeriod());
			}
			if (optP.isPresent()) {
				// 実績締めロックされない期間を取得する
				List<DatePeriod> listP = ClosingGetUnlockedPeriod.get(require, optP.get(),
						employmentHis.getEmploymentCode(), ignoreFlagDuringLock, achievementAtr);
				if (!listP.isEmpty()) {
					// 取得できた期間をOutput期間一覧にいれる
					listPeriod.addAll(listP);
				}
			}
		}
		return listPeriod;
	}

	/**
	 * condition : period1.start() <= period2.start()
	 * 
	 * @param period1
	 * @param period2
	 * @return
	 */
	private static Optional<DatePeriod> getGeneralPeriod(DatePeriod period1, DatePeriod period2) {
		if (period1.contains(period2.start())) {
			GeneralDate endDate = period2.end().beforeOrEquals(period1.end()) ? period2.end() : period1.end();
			return Optional.of(new DatePeriod(period2.start(), endDate));
		}
		return Optional.empty();
	}

	public static interface Require extends ActualLock.Require {
		/**
		 * 最新のドメインモデル「締め状態管理」を取得する ClosureStatusManagementRepository
		 * 
		 * @param companyId
		 * @param employeeId
		 * @return
		 */
		List<ClosureStatusManagement> getAllByEmpId(String employeeId);

		/**
		 * Requireで締めIDを取得する ClosureEmploymentRepository
		 * 
		 * @param employmentCode
		 * @return
		 */
		Optional<ClosureEmployment> findByEmploymentCD(String employmentCode);

		/**
		 * Requireで「当月の実績ロック」を取得する ActualLockRepository
		 * 
		 * @param companyId
		 * @param closureId
		 * @return
		 */
		Optional<ActualLock> findById(int closureId);

	}
}
