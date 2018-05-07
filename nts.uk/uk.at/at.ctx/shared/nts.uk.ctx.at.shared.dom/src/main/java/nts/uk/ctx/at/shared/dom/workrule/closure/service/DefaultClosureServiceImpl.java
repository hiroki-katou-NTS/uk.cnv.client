/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.workrule.closure.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureDay;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureInfo;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * The Class DefaultClosureServiceImpl.
 */
@Stateless
public class DefaultClosureServiceImpl implements ClosureService {

	/** The closure repository. */
	@Inject
	private ClosureRepository closureRepository;

	/** The Constant MONTH_OF_YEAR. */
	private final static int MONTH_OF_YEAR = 12;
	
	/** The Constant FIRST_DAY_OF_MONTH. */
	private final static int FIRST_DAY_OF_MONTH = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService#
	 * getClosurePeriod(int, nts.arc.time.YearMonth)
	 */
	// 当月の期間を算出する 2018.4.4 update shuichu_ishida
	@Override
	public DatePeriod getClosurePeriod(int closureId, YearMonth processYm) {

		// 【処理概要】
		// 渡されてきた年月に応じた期間を返す。
		// ただし、締め日変更がある年月の場合、
		// 締め．当月．処理当月 以外の年月の時は、締め日変更前期間を返す。
		// 締め．当月．処理当月 と同じ年月の時は、締め日変更区分の設定値に応じた期間を返す。

		// ログインしている会社IDを取得する
		LoginUserContext loginUserContext = AppContexts.user();
		String companyId = loginUserContext.companyId();

		// ドメインモデル「締め」を取得する
		val closureOpt = this.closureRepository.findById(companyId, closureId);
		if (!closureOpt.isPresent())
			return null;
		val closure = closureOpt.get();

		// 当月の期間をすべて取得する （→ 指定した年月の期間をすべて取得する）
		val currentPeriods = closure.getPeriodByYearMonth(processYm);
		if (currentPeriods.size() <= 0)
			return null;

		// 当月の期間が2つある時は、2つめを当月の期間とする （1つの時は、1つめを当月の期間とする）
		val currentPeriod = currentPeriods.get(currentPeriods.size() - 1);

		// 翌月の期間をすべて取得する （→ 指定した年月の期間をすべて取得する）
		val nextPeriods = closure.getPeriodByYearMonth(processYm.addMonths(1));

		// 締め変更により、変更前・後期間がある年月か確認する
		// ※ 翌月の期間が2つ返ってくれば、翌月の1つめの期間が、当月の後期間に当たる （月度解釈のズレがある）
		boolean isMultiPeriod = false;
		if (nextPeriods.size() > 1)
			isMultiPeriod = true;

		if (isMultiPeriod) {
			// 前・後期間のある月度の時

			val currentMonth = closure.getClosureMonth();
			if (processYm.equals(currentMonth.getProcessingYm())) {
				// 「締め．当月．処理当月」と同じ年月の時

				if (currentMonth.getClosureClassification().isPresent()) {
					if (currentMonth.getClosureClassification()
							.get() == ClosureClassification.ClassificationClosingBefore) {
						// 「締め．当月．締め日変更区分」＝「締め日変更前期間」の時 → 当月の期間を返す
						return currentPeriod;
					} else {
						// 「締め．当月．締め日変更区分」＝「締め日変更後期間」の時 → 翌月の1つめの期間を返す
						return nextPeriods.get(0);
					}
				} else {
					// 「締め．当月．締め日変更区分」がない時 → 当月の期間を返す
					return currentPeriod;
				}
			} else {
				// 「締め．当月．処理当月」と異なる年月の時
				return currentPeriod;
			}
		}
		// 前・後期間のない月度の時 → 当月の期間を返す
		return currentPeriod;
	}

	/** 全締めの当月と期間を取得する */
	// 2018.4.4 add shuichi_ishida
	@Override
	public List<ClosureInfo> getAllClosureInfo() {

		List<ClosureInfo> allClosureInfo = new ArrayList<>();

		// ログインしている会社ID 取得
		LoginUserContext loginUserContext = AppContexts.user();
		String companyId = loginUserContext.companyId();

		// 「締め」をすべて取得する
		val closures = this.closureRepository.findAllUse(companyId);

		for (val closure : closures) {

			// 当月の期間を算出する
			YearMonth currentMonth = closure.getClosureMonth().getProcessingYm();
			val targetPeriod = this.getClosurePeriod(closure.getClosureId().value, currentMonth);
			if (targetPeriod == null)
				continue;

			// 締め日変更区分をチェックする （当月に該当する締め変更履歴を確認する）
			Optional<ClosureHistory> targetHistoryOpt = Optional.empty();
			val closureClassOpt = closure.getClosureMonth().getClosureClassification();
			if (!closureClassOpt.isPresent()) {
				targetHistoryOpt = closure.getHistoryByYearMonth(currentMonth);
			} else {
				val closureClass = closureClassOpt.get();
				if (closureClass == ClosureClassification.ClassificationClosingBefore) {
					targetHistoryOpt = closure.getHistoryByYearMonth(currentMonth);
				} else {
					targetHistoryOpt = closure.getHistoryByYearMonth(currentMonth.addMonths(1));
				}
			}
			if (!targetHistoryOpt.isPresent())
				continue;
			val targetHistory = targetHistoryOpt.get();

			// 取得した期間、締め日、当月をパラメータに格納する
			ClosureInfo closureInfo = ClosureInfo.of(closure.getClosureId(),
					targetHistory.getClosureDate(), targetHistory.getClosureName(), currentMonth,
					targetPeriod);

			allClosureInfo.add(closureInfo);
		}
		return allClosureInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService#
	 * getClosurePeriod(int, nts.arc.time.YearMonth)
	 */
	@Override
	// <<Public>> 当月の期間を算出する create by TrangTH
	public DatePeriod getClosurePeriodNws (int closureId, YearMonth processingYm) {
		// get login user
		LoginUserContext loginUserContext = AppContexts.user();

		// get company id
		String companyId = loginUserContext.companyId();

		//
		Optional<ClosureHistory> optClosureHistory = this.closureRepository.findBySelectedYearMonth(companyId,
				closureId, processingYm.v());

		// Check exist
		if (!optClosureHistory.isPresent()) {
			return null;
		}

		ClosureHistory closureHistory = optClosureHistory.get();

		ClosureDay closureDay = closureHistory.getClosureDate().getClosureDay();

		Boolean isLastDayOfMonth = closureHistory.getClosureDate().getLastDayOfMonth();

		GeneralDate startDate = this.getExpectionDate(isLastDayOfMonth, processingYm.year(),
				isLastDayOfMonth ? processingYm.month() : processingYm.month() - 1,
				isLastDayOfMonth ? FIRST_DAY_OF_MONTH : closureDay.v() + 1, true);

		GeneralDate endDate = this.getExpectionDate(isLastDayOfMonth, processingYm.year(), processingYm.month(),
				closureDay.v(), false);

		return new DatePeriod(startDate, endDate);
	}

	/**
	 * Gets the expection date.
	 *
	 * @param lastDayOfMonth the last day of month
	 * @param year the year
	 * @param month the month
	 * @param day the day
	 * @param isStartDate the is start date
	 * @return the expection date
	 */
	// 日付の存在チェック
	private GeneralDate getExpectionDate(Boolean lastDayOfMonth, int year, int month, int day, Boolean isStartDate) {

		if (month == 0) {
			month = MONTH_OF_YEAR;
			year = year - 1;
		}

		if (lastDayOfMonth && isStartDate) {
			return GeneralDate.ymd(year, month, day);
		}

		return (lastDayOfMonth || !this.isDateOfMonth(year, month, day)) ? this.getLastDateOfMonth(year, month)
				: GeneralDate.ymd(year, month, day);
	}

	/**
	 * Gets the last date of month.
	 *
	 * @param year the year
	 * @param month the month
	 * @return the last date of month
	 */
	private GeneralDate getLastDateOfMonth(int year, int month) {
		GeneralDate baseDate = GeneralDate.ymd(year, month, 1);
		Calendar c = Calendar.getInstance();
		c.setTime(baseDate.date());
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return GeneralDate.legacyDate(c.getTime());
	}

	/**
	 * Checks if is date of month.
	 *
	 * @param year the year
	 * @param month the month
	 * @param dayOfMonth the day of month
	 * @return true, if is date of month
	 */
	private boolean isDateOfMonth(int year, int month, int dayOfMonth) {
		GeneralDate baseDate = this.getLastDateOfMonth(year, month);
		return dayOfMonth <= baseDate.day();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService#getClosureInfo()
	 */
	// 全締めの当月と期間を取得する
	public List<ClosureInfor> getClosureInfo() {
		String companyId = AppContexts.user().companyId();
		
		List<ClosureInfor> closureInfor = new ArrayList<>();

		List<Closure> closureList = this.closureRepository.getClosureList(companyId);

		closureList.forEach(item -> {
			// <<Public>> 当月の期間を算出する
			DatePeriod period = this.getClosurePeriod(item.getClosureId().value,
					item.getClosureMonth().getProcessingYm());
			
			//Check ClosureClassification 
			ClosureClassification closureClassification = item.getClosureMonth().getClosureClassification().isPresent()
					? item.getClosureMonth().getClosureClassification().get()
					: ClosureClassification.ClassificationClosingBefore;
			switch (closureClassification) {
			case ClassificationClosingBefore:
				item.setClosureHistories(item.getClosureHistories().stream()
						.filter(i -> i.getStartYearMonth().lessThanOrEqualTo(item.getClosureMonth().getProcessingYm())
								&& i.getEndYearMonth().greaterThanOrEqualTo(item.getClosureMonth().getProcessingYm()))
						.collect(Collectors.toList()));
				break;
			case ClassificationClosingAfter:
				item.setClosureHistories(item.getClosureHistories().stream()
						.filter(i -> item.getClosureMonth().getProcessingYm().equals(i.getStartYearMonth().previousMonth()))
						.collect(Collectors.toList()));
				break;
			default:
				item.setClosureHistories(item.getClosureHistories().stream()
						.filter(i -> i.getStartYearMonth().lessThanOrEqualTo(item.getClosureMonth().getProcessingYm())
								&& i.getEndYearMonth().greaterThanOrEqualTo(item.getClosureMonth().getProcessingYm()))
						.collect(Collectors.toList()));
				break;
			}
			//insert for Param 
			item.getClosureHistories().forEach(closure -> {
				closureInfor.add(ClosureInfor.builder().closureId(closure.getClosureId()).closureName(closure.getClosureName())
						.closureDate(closure.getClosureDate()).closureMonth(item.getClosureMonth())
						.period(period).build());
			});
		});
		
		//return List ClosureList
		return closureInfor;
	}
}
