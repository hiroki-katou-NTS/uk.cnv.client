/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.scherec.totaltimes;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.export.attdstatus.AttendanceStatusList;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.memento.TotalTimesGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.memento.TotalTimesSetMemento;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceDayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * 回数集計
 */
@Getter
public class TotalTimes extends AggregateRoot {

	/** 会社ID */
	private String companyId;

	/** 回数集計NO */
	private Integer totalCountNo;

	/** するしない区分 */
	private UseAtr useAtr;

	/** 回数集計名称 */
	private TotalTimesName totalTimesName;

	/** 回数集計略名 */
	private TotalTimesABName totalTimesABName;

	/** 回数集計区分 */
	private SummaryAtr summaryAtr;

	/** 回数集計条件 */
	private TotalCondition totalCondition;

	/** 集計対象一覧 */
	private SummaryList summaryList;

	/** 半日勤務カウント区分 */
	private CountAtr countAtr;

	public TotalTimes(TotalTimesGetMemento memento) {
		super();
		this.companyId = memento.getCompanyId();
		this.totalCountNo = memento.getTotalCountNo();
		this.countAtr = memento.getCountAtr();
		this.useAtr = memento.getUseAtr();
		this.totalTimesName = memento.getTotalTimesName();
		this.totalTimesABName = memento.getTotalTimesABName();
		this.summaryAtr = memento.getSummaryAtr();
		this.totalCondition = memento.getTotalCondition();
		this.summaryList = memento.getSummaryList();

		// Validate.
		if (this.useAtr == UseAtr.NotUse) {
			return;
		}
		
		if ((this.summaryAtr.equals(SummaryAtr.DUTYTYPE)
				|| this.summaryAtr.equals(SummaryAtr.COMBINATION))
				&& CollectionUtil.isEmpty(this.summaryList.getWorkTypeCodes())) {
			throw new BusinessException("Msg_216", "KMK009_8");
		}

		if ((this.summaryAtr.equals(SummaryAtr.WORKINGTIME)
				|| this.summaryAtr.equals(SummaryAtr.COMBINATION))
				&& CollectionUtil.isEmpty(this.summaryList.getWorkTimeCodes())) {
			throw new BusinessException("Msg_216", "KMK009_9");
		}
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(TotalTimesSetMemento memento) {
		memento.setCompanyId(this.companyId);
		memento.setCountAtr(this.countAtr);
		memento.setSummaryAtr(this.summaryAtr);
		memento.setSummaryList(this.summaryList);
		memento.setTotalCondition(this.totalCondition);
		memento.setTotalCountNo(this.totalCountNo);
		memento.setTotalTimesABName(this.totalTimesABName);
		memento.setTotalTimesName(this.totalTimesName);
		memento.setUseAtr(this.useAtr);
	}

	/** 日別勤怠から時間・回数を集計する */
	public TotalCount aggregateTotalCount(RequireM1 require, List<IntegrationOfDaily> dailyWorks,
			AttendanceStatusList attendanceStates) {
		
		/** 空の回数集計結果情報を作成する */
		val result = new TotalCount(this.totalCountNo);
		val attendanceStateMap = attendanceStates.getMap();
		
		/** 回数集計。利用区分を確認する */
		if (this.useAtr == UseAtr.NotUse) {
			
			return result;
		}
		
		/** 日別勤怠（Work）一覧をループする */
		dailyWorks.stream().forEach(dw -> {
			
			/** 大塚カスタマイズの条件判断 */
			if (!result.checkOotsukaCustomize(this.totalCountNo, 
					dw.getWorkInformation().getRecordInfo().getWorkTypeCode().v())) {
				return;
			}
			
			/** 出勤状態を取得する */
			val attendanceState = attendanceStateMap.containsKey(dw.getYmd()) 
					? attendanceStateMap.get(dw.getYmd()).isExistAttendance() : true;
					
			/** 日の回数、時間を確認する */
			checkTimeAndCount(require, dw, attendanceState, result);
		});
		
		return result;
	}

	/** 日の回数、時間を確認する */
	public void checkTimeAndCount(RequireM1 require, IntegrationOfDaily dailyWork,
			boolean attendanceState, TotalCount totalCount) {
		
		/** ○勤務情報の判断 */
		if (!isTargetWork(dailyWork.getWorkInformation().getRecordInfo())) {
			return;
		}
		
		/** ○出勤状態の判断 */
		if (!checkAttendance(attendanceState)) {
			return;
		}
		
		/** ○勤務時間の判断 */
		if (!this.totalCondition.checkWorkTime(require, dailyWork)) {
			return;
		}
		
		/** ○時間・回数を取得 */
		addTotalCount(require, dailyWork, totalCount);
	}
	
	/** 出勤状態の判断 */
	private boolean checkAttendance(boolean attendanceState) {
		
		/** ○回数集計区分を判断 */
		if (summaryAtr == SummaryAtr.DUTYTYPE) { /** 勤務種類　の場合 */
			
			/** ○trueを返す */
			return true;
		}
		
		/** ○出勤状態を判断する */
		return attendanceState;
	}
	
	/** ○時間・回数を取得 */
	private void addTotalCount(RequireM2 require, IntegrationOfDaily dailyWork, TotalCount totalCount) {
		
		/** ○回数を取得 */
		val count = count(require, dailyWork.getWorkInformation().getRecordInfo());
		
		/** 時間を取得 */
		val time = time(count, dailyWork);
		
		/** ○回数集計結果情報を返す */
		totalCount.addCount(count);
		totalCount.addTime(time);
	}
	
	/** 時間を取得 */
	private int time(double count, IntegrationOfDaily dailyWork) {
		
		/** ○パラメータ「回数」を確認 */
		if (count > 0) {
			
			/** ○パラメータ「総労働時間」を返す */
			return dailyWork.getAttendanceTimeOfDailyPerformance().
					map(at -> at.getActualWorkingTimeOfDaily().getTotalWorkingTime().getTotalTime().v())
					.orElse(0);
		}
		
		/** ○0：00を返す */
		return 0;
	}

	/** ○回数を取得 */
	private double count(RequireM2 require, WorkInformation workInfo) {
		
		/** ○ドメインモデル「勤務種類」を取得 */
		val workType = require.workType(AppContexts.user().companyId(), workInfo.getWorkTypeCode().v())
							.orElse(null);
		
		if (workType == null) {
			/** ○0回を返す */
			return 0;
		}
		
		/** ○カウント区分を判断 */
		if (this.countAtr == CountAtr.ONEDAY) {
			/** ○1回を返す */
			return 1d;
		}
		
		/** ○1日半日出勤・1日休日系の判定 */
		if (workType.chechAttendanceDay() == AttendanceDayAttr.HALF_TIME_AM ||
				workType.chechAttendanceDay() == AttendanceDayAttr.HALF_TIME_PM) {
			/** ○0.5日を返す */
			return 0.5;
		}

		/** ○1回を返す */
		return 1d;
	}
	
	/** 勤務情報の判断 */
	public boolean isTargetWork(WorkInformation workInfo) {
		
		/** ○属性「集計区分」を取得 */
		switch (this.summaryAtr) {
		case DUTYTYPE:
			
			/** ○取得した勤務種類が「勤務種類一覧」に含まれているか確認する */
			return isTargetWorkType(workInfo);
		case WORKINGTIME:
			
			/** ○取得した就業時間帯が「就業時間帯一覧」に含まれているか確認する */
			return isTargetWorkTime(workInfo);
		case COMBINATION:
			
			/** ○取得した勤務種類が「勤務種類一覧」に含まれているか確認する */
			/** ○取得した就業時間帯が「就業時間帯一覧」に含まれているか確認する */
			return isTargetWorkType(workInfo) && isTargetWorkTime(workInfo);
		default:
			return false;
		}
	}

	private boolean isTargetWorkTime(WorkInformation workInfo) {
		return this.summaryList.getWorkTimeCodes().contains(
				workInfo.getWorkTimeCodeNotNull().map(c -> c.v()).orElse(""));
	}

	private boolean isTargetWorkType(WorkInformation workInfo) {
		return this.summaryList.getWorkTypeCodes().contains(workInfo.getWorkTypeCode().v());
	}
	
	public static interface RequireM2 {
		
		Optional<WorkType> workType(String companyId, String workTypeCd);
	}
	
	public static interface RequireM1 extends TotalCondition.RequireM1, RequireM2 {
		
	}
}
