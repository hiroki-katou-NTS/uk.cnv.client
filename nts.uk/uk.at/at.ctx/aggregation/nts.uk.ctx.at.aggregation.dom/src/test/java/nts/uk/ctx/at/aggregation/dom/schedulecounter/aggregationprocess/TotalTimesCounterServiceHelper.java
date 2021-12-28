package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.val;
import mockit.Injectable;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.ConditionThresholdLimit;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.CountAtr;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.SummaryAtr;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.SummaryList;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalCondition;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimes;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimesABName;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimesName;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.UseAtr;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.memento.TotalConditionGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.memento.TotalTimesGetMemento;
public class TotalTimesCounterServiceHelper {
	@Injectable
	private static WorkInfoOfDailyAttendance workInformation;
	
	@AllArgsConstructor
	static class TotalTimesGetMementoImpl implements TotalTimesGetMemento{
		private Integer totalCountNo;
		private UseAtr useAtr;
		@Override
		public String getCompanyId() {
			return "cid";
		}
		@Override
		public Integer getTotalCountNo() {
			return this.totalCountNo;
		}
		@Override
		public CountAtr getCountAtr() {
			return CountAtr.ONEDAY;
		}
		@Override
		public UseAtr getUseAtr() {
			return this.useAtr;
		}
		@Override
		public TotalTimesName getTotalTimesName() {
			return new TotalTimesName("totalTimeName");
		}
		@Override
		public TotalTimesABName getTotalTimesABName() {
			return new TotalTimesABName("totalTimeAbName");
		}
		@Override
		public SummaryAtr getSummaryAtr() {
			return SummaryAtr.COMBINATION;
		}
		@Override
		public TotalCondition getTotalCondition() {
			return null;
		}
		@Override
		public SummaryList getSummaryList() {
			return createWorkInfo(Arrays.asList("W01"), Arrays.asList("T01", "T02"));
		}
	}
	
	@AllArgsConstructor
	static class TotalConditionImpl implements TotalConditionGetMemento{
		private UseAtr lowerLimitSettingAtr;
		private UseAtr upperLimitSettingAtr;
		private Optional<ConditionThresholdLimit> thresoldLowerLimit;
		private Optional<ConditionThresholdLimit> thresoldUpperLimit;
		private Optional<Integer> atdItemId;
		
		@Override
		public UseAtr getUpperLimitSettingAtr() {
			return this.lowerLimitSettingAtr;
		}
		@Override
		public UseAtr getLowerLimitSettingAtr() {
			return this.upperLimitSettingAtr;
		}
		@Override
		public Optional<ConditionThresholdLimit> getThresoldUpperLimit() {
			return this.thresoldUpperLimit;
		}
		@Override
		public Optional<ConditionThresholdLimit> getThresoldLowerLimit() {
			return this.thresoldLowerLimit;
		}
		@Override
		public Optional<Integer> getAttendanceItemId() {
			return this.atdItemId;
		}
	}
	
	/**
	 * 集計対象一覧を作る
	 * @param workTypes 勤務種類コード
	 * @param workTimes 就業時間帯コード
	 * @return
	 */
	public static SummaryList createWorkInfo(List<String> workTypeCodes, List<String> workTimeCodes) {
		val summaryList = new SummaryList();
		summaryList.setWorkTypeCodes(workTypeCodes);
		summaryList.setWorkTimeCodes(workTimeCodes);
		return summaryList;
	}
	
	/**
	 * 回数集計を作る
	 * @param totalCountNo 回数集計NO
	 * @param useAtr するしない区分
	 * @return
	 */
	public static TotalTimes createTotalTimes(Integer totalCountNo
			, UseAtr useAtr) {
		
		TotalTimesGetMementoImpl totalTimeMemento = new TotalTimesGetMementoImpl(
				totalCountNo, useAtr);
		
		return new TotalTimes(totalTimeMemento);
	}
	
	/**
	 * 日別実績(Work)を作る
	 * @param sid 社員ID
	 * @param ymd 年月日
	 * @return
	 */
	public static IntegrationOfDaily createDailyWorks(String sid, GeneralDate ymd ) {
		return new IntegrationOfDaily(
				  sid, ymd, workInformation
				, CalAttrOfDailyAttd.defaultData()
				, null
				, Optional.empty()
				, Collections.emptyList()
				, Optional.empty()
				, new BreakTimeOfDailyAttd(Collections.emptyList())
				, Optional.empty()
				, Optional.empty()
				, Optional.empty()
				, Optional.empty()
				, Optional.empty()
				, Optional.empty()
				, Collections.emptyList()
				, Optional.empty()
				, Collections.emptyList()
				, Collections.emptyList()
				, Collections.emptyList()
				, Optional.empty());
	}
}
