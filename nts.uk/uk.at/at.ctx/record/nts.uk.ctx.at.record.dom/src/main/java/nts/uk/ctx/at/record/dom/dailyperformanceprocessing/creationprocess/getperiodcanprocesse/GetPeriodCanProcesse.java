package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsCondition;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.closegetunlockedperiod.ClosingGetUnlockedPeriod;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLock;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.GetPeriodExcluseEntryRetireTime;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 実績処理できる期間を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.実績処理できる期間を取得する.実績処理できる期間を取得する
 * 
 * @author tutk
 *
 */
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
	public static List<DatePeriod> get(Require require, String companyId, String employeeId, DatePeriod period,
			List<EmploymentHistoryImported> listEmploymentHis, IgnoreFlagDuringLock ignoreFlagDuringLock,
			AchievementAtr achievementAtr) {
		List<DatePeriod> listPeriod = new ArrayList<>();
		
		/** 日別実績の作成入社前、退職後を期間から除く */
		val excluseEntyRetirePeriod = GetPeriodExcluseEntryRetireTime.getPeriodExcluseEntryRetireTime(require, 
				new CacheCarrier(), employeeId, period).orElse(null);
		if (excluseEntyRetirePeriod == null) return listPeriod; 
		
		/** 日別実績を作成する条件に応じて実行期間を補正する */
		val correctedPeriod = correctWithCreatingDailyResultsCondition(require, companyId, excluseEntyRetirePeriod).orElse(null);
		
		if (correctedPeriod == null) return listPeriod; 
		
		// 最新のドメインモデル「締め状態管理」を取得する
		List<ClosureStatusManagement> listClosureStatus = require.getAllByEmpId(employeeId);
		Optional<DatePeriod> optPeriod = Optional.empty();
		if (!listClosureStatus.isEmpty()) {
			listClosureStatus = listClosureStatus.stream()
					.sorted(Comparator.comparing(ClosureStatusManagement::getYearMonth)
							.thenComparing(Comparator.comparing(data -> data.getPeriod().end())).reversed())
					.collect(Collectors.toList());
			ClosureStatusManagement dataMax = listClosureStatus.get(0);
			// 締めされていない期間を求める
			optPeriod = dataMax.closureStateManagenent(correctedPeriod);
			if (!optPeriod.isPresent()) {
				return listPeriod;
			}
		} else {
			//⁂注意：締め状態管理取得できない場合、パラメータ。期間を使う
			optPeriod = Optional.of(correctedPeriod);
		}
		
		/** パラメータ。雇用履歴一覧を確認する */
		if (listEmploymentHis.isEmpty())
			/** 雇用履歴を取得する */
			listEmploymentHis = require.getEmpHistBySid(companyId, employeeId);
		
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
	
	/** 日別実績を作成する条件に応じて実行期間を補正する */
	private static Optional<DatePeriod> correctWithCreatingDailyResultsCondition(RequireM1 require, String cid, DatePeriod period) {
		
		/** ドメインモデル「日別実績を作成する条件」を取得する */
		val createDailyCondition = require.creatingDailyResultsCondition(cid)
				.orElseGet(() -> new CreatingDailyResultsCondition(cid, NotUseAtr.NOT_USE));
		
		/** 日別実績を作成する期間を補正する */
		return createDailyCondition.correctDailyCreatePeriod(period);
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
	
	public static interface RequireM1 {
		
		Optional<CreatingDailyResultsCondition> creatingDailyResultsCondition(String cid);
	}

	public static interface Require extends ActualLock.Require, RequireM1, GetPeriodExcluseEntryRetireTime.Require {
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
		
		List<EmploymentHistoryImported> getEmpHistBySid(String companyId, String employeeId);
	}
}
