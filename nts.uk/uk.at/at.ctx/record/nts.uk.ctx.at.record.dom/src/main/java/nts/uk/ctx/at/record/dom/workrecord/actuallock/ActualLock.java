/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.dom.workrecord.actuallock;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.AchievementAtr;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;

/**
 * The Class ActualLock.
 */
// 当月の実績ロック
@Getter
public class ActualLock extends AggregateRoot {

	/** The company id. */
	// 会社ID
	private String companyId;

	/** The closure id. */
	// 締めID
	private ClosureId closureId;

	/** The daily lock state. */
	// 日別のロック状態
	private LockStatus dailyLockState;

	/** The monthly lock state. */
	// 月別のロック状態
	private LockStatus monthlyLockState;

	/**
	 * Instantiates a new actual lock.
	 *
	 * @param memento the memento
	 */
	public ActualLock(ActualLockGetMemento memento) {
		this.companyId = memento.getCompanyId();
		this.closureId = memento.getClosureId();
		this.dailyLockState = memento.getDailyLockState();
		this.monthlyLockState = memento.getMonthyLockState();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(ActualLockSetMemento memento) {
		memento.setCompanyId(this.companyId);
		memento.setClosureId(this.closureId);
		memento.setDailyLockState(this.dailyLockState);
		memento.setMonthlyLockState(this.monthlyLockState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((closureId == null) ? 0 : closureId.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActualLock other = (ActualLock) obj;
		if (closureId != other.closureId)
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		return true;
	}

	public void lockDaily() {
		this.dailyLockState = LockStatus.LOCK;
	}

	public void unlockDaily() {
		this.dailyLockState = LockStatus.UNLOCK;
	}

	public void lockMonthly() {
		this.monthlyLockState = LockStatus.LOCK;
	}

	public void unlockMonthly() {
		this.monthlyLockState = LockStatus.UNLOCK;
	}

	/**
	 * [1] ロックされていない期間を求める
	 * 
	 * @param require
	 * @param period         期間
	 * @param achievementAtr ロック確認単位
	 * @return 	List<期間>
	 */
	public List<DatePeriod> askForUnlockedPeriod(Require require, DatePeriod period, AchievementAtr achievementAtr) {
		List<DatePeriod> listPeriod = new ArrayList<>();
		// if ロック確認単位 == 日別 && @日別のロック状態 == アンロック && @月別のロック状態 == アンロック
		// else if ロック確認単位 == 月別 && @月別のロック状態 == アンロック
		if ((achievementAtr == AchievementAtr.DAILY && this.dailyLockState == LockStatus.UNLOCK
				&& this.monthlyLockState == LockStatus.UNLOCK)
				|| (achievementAtr == AchievementAtr.MONTHLY && this.monthlyLockState == LockStatus.UNLOCK)) {
			listPeriod.add(period);
			return listPeriod;
		}
		//	val $締め = require.締めを取得する(@締めID);
		Closure closure = require.findClosureById(this.closureId.value);
		//	val $締め期間 = require.指定した年月の期間を算出する(@締めID、$締め。当月);
		DatePeriod periodClosure = require.getClosurePeriod(this.closureId.value, closure.getClosureMonth().getProcessingYm());
		GeneralDate startPeriodClosure = periodClosure.start().addDays(-1);
		GeneralDate endPeriodClosure = periodClosure.end().addDays(1);
		return period.subtract(new DatePeriod(startPeriodClosure, endPeriodClosure));
	}

	public static interface Require {
		/**
		 * アルゴリズム.指定した年月の期間を算出する(締めID、年月)	
		 * ClosureService.getClosurePeriod
		 * @param closureId
		 * @param processYm
		 * @return
		 */
		DatePeriod getClosurePeriod(int closureId, YearMonth processYm);
		
		Closure findClosureById(int closureId);
	}

	public ActualLock(String companyId, ClosureId closureId, LockStatus dailyLockState, LockStatus monthlyLockState) {
		super();
		this.companyId = companyId;
		this.closureId = closureId;
		this.dailyLockState = dailyLockState;
		this.monthlyLockState = monthlyLockState;
	}

	
}
