package nts.uk.screen.at.app.kdw013.a;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroup;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroupRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ActualManHrTaskBlockCreationService;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.DailyActualManHrActualTask;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.DailyAttendenceWorkToManHrRecordItemConvertService;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLink;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLinkRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrRecordConvertResult;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.日別実績データを取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetDailyPerformanceData {

	@Inject
	private IntegrationOfDailyGetter getter;
	
	@Inject
	private ManHourRecordAndAttendanceItemLinkRepository manHourRecordAndAttendanceItemLinkRepository;
	
	@Inject
	private TaskTimeGroupRepository  taskTimeGroupRepository;
	
	@Inject
	private AttendanceItemConvertFactory attendanceItemConvertFactory;

	/**
	 * 
	 * @param sId 社員ID
	 * @param period 期間
	 * @param itemIds 対象項目リスト：List<工数実績項目ID>
	 * @return List<日別勤怠(Work)>
	 *  List<日別実績の実績内容>
	 *  List<日別実績の工数実績作業>
	 */
	public GetDailyPerformanceDataResult get(String sId, DatePeriod period, List<Integer> itemIds) {
		GetDailyPerformanceDataResult result = new GetDailyPerformanceDataResult();

		// 1: get(社員ID,期間)
		result.setLstIntegrationOfDaily(getter.getIntegrationOfDaily(sId, period));
		
		//$工数実績項目リスト = INPUT「対象項目リスト」に(1,2,3,4,5,6,7,8)のIDを追加するもの
		
		itemIds.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
		
		//取得した「日別勤怠(Work)」をループする
		for (IntegrationOfDaily interDaily : result.getLstIntegrationOfDaily()) {
			
			// 2 . 日別勤怠(Work)から工数実績項目に変換する 処理中の「日別勤怠(Work)」,$工数実績項目リス
			ManHrRecordConvertResult convertRes = DailyAttendenceWorkToManHrRecordItemConvertService
					.convert(new DailyAttendenceWorkToManHrRecordItemConvertServiceImpl(
							manHourRecordAndAttendanceItemLinkRepository), interDaily, itemIds);
			
			result.getConvertRes().add(convertRes);
			
			// 3. 工数実績作業ブロックを作成する 社員ID,工数実績変換結果.年月日,工数実績変換結果.作業リスト 
			DailyActualManHrActualTask dailyManHrTask =  ActualManHrTaskBlockCreationService.create(
					new ActualManHrTaskBlockCreationServiceImpl(taskTimeGroupRepository), sId, convertRes.getYmd(),
					convertRes.getTaskList());
			
			result.getDailyManHrTasks().add(dailyManHrTask);
		}

		return result;
	}
	
	@AllArgsConstructor
	private class DailyAttendenceWorkToManHrRecordItemConvertServiceImpl
			implements DailyAttendenceWorkToManHrRecordItemConvertService.Require {

		private ManHourRecordAndAttendanceItemLinkRepository manHourRecordAndAttendanceItemLinkRepository;

		@Override
		public List<ManHourRecordAndAttendanceItemLink> get(List<Integer> items) {

			return this.manHourRecordAndAttendanceItemLinkRepository.get(AppContexts.user().companyId(), items);
		}

		@Override
		public DailyRecordToAttendanceItemConverter createDailyConverter() {
			return attendanceItemConvertFactory.createDailyConverter();
		}

	}
	
	@AllArgsConstructor
	private class ActualManHrTaskBlockCreationServiceImpl implements ActualManHrTaskBlockCreationService.Require {
		
		private TaskTimeGroupRepository  taskTimeGroupRepository;
		
		@Override
		public Optional<TaskTimeGroup> get(String sID, GeneralDate date) {
			return this.taskTimeGroupRepository.get(sID, date);
		}

	}

}
