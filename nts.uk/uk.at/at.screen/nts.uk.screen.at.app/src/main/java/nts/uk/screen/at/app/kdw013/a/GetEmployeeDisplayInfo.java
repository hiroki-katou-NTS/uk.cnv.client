package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetAllEmployeeWithWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetWorkplaceOfEmployeeAdapter;
import nts.uk.ctx.at.record.dom.daily.ouen.GetTheWorkYouUseMostRecentlyService;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSettingRepository;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.CheckShortageFlex;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.対象社員の表示情報を取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetEmployeeDisplayInfo {

	@Inject
	private ManHourRecordReferenceSettingRepository workChangeablePeriodSettingRepo;

	@Inject
	private GetWorkConfirmationStatus getWorkConfirmationStatus;

	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenRepo;

	@Inject
	private TaskingRepository taskRepo;

	@Inject
	private TaskFrameUsageSettingRepository taskFrameUsageSettingRepo;

	@Inject
	private CheckShortageFlex checkShortageFlex;

	@Inject
	private GetDailyPerformanceData getDailyPerformanceData;
	
	@Inject
	private GetWorkplaceOfEmployeeAdapter getWorkplaceOfEmployeeAdapter;
	
	@Inject
	private GetAllEmployeeWithWorkplaceAdapter getAllEmployeeWithWorkplaceAdapter;

	/**
	 * 
	 * @param sid
	 * @param refDate
	 * @param period
	 * @return
	 */
	public EmployeeDisplayInfo getInfo(String sid, GeneralDate refDate, DatePeriod period) {
		EmployeeDisplayInfo employeeDisplayInfo = new EmployeeDisplayInfo();

		// 1: 取得する(@Require, 社員ID, 年月日):List<作業グループ>
		RequireImpl require = new RequireImpl(ouenRepo, taskRepo, taskFrameUsageSettingRepo);
		RequireImpl1 require1 = new RequireImpl1(workChangeablePeriodSettingRepo, checkShortageFlex, getWorkplaceOfEmployeeAdapter,getAllEmployeeWithWorkplaceAdapter);

		List<WorkGroup> workGroups = GetTheWorkYouUseMostRecentlyService.get(require, sid);
		employeeDisplayInfo.setWorkGroups(workGroups);

		// 2: get(ログイン会社ID)
		Optional<ManHourRecordReferenceSetting> OptWorkChangeablePeriodSetting = workChangeablePeriodSettingRepo
				.get(AppContexts.user().companyId());

		// 3: [作業変更可能期間設定.isPresent]:作業修正可能開始日付を取得する(@Require, 社員ID):年月日
		if (OptWorkChangeablePeriodSetting.isPresent()) {
			GeneralDate date = OptWorkChangeablePeriodSetting.get().getWorkCorrectionStartDate(require1, sid);
			employeeDisplayInfo.setDate(date);
		}

		// 4: <call>(社員ID,基準日)
		employeeDisplayInfo.setLstComfirmerDto(getWorkConfirmationStatus.get(sid, refDate));

		// 5: <call>(社員ID,表示期間)
		employeeDisplayInfo.setWorkRecordDetails(getDailyPerformanceData.get(sid, period));

		return employeeDisplayInfo;
	}

	@AllArgsConstructor
	public class RequireImpl implements GetTheWorkYouUseMostRecentlyService.Require {

		private OuenWorkTimeSheetOfDailyRepo ouenRepo;

		private TaskingRepository taskRepo;

		private TaskFrameUsageSettingRepository taskFrameUsageSettingRepo;

		@Override
		public Optional<Task> getTask(TaskFrameNo taskFrameNo, WorkCode code) {
			return taskRepo.getOptionalTask(AppContexts.user().companyId(), taskFrameNo, new TaskCode(code.v()));
		}

		@Override
		public TaskFrameUsageSetting getTaskFrameUsageSetting() {
			return taskFrameUsageSettingRepo.getWorkFrameUsageSetting(AppContexts.user().companyId());
		}

		@Override
		public List<OuenWorkTimeSheetOfDaily> findOuenWorkTimeSheetOfDaily(String empId, DatePeriod targetPeriod) {
			return ouenRepo.find(empId, targetPeriod);
		}
	}

	@AllArgsConstructor
	public class RequireImpl1 implements ManHourRecordReferenceSetting.Require {

		private ManHourRecordReferenceSettingRepository workChangeablePeriodSettingRepo;

		private CheckShortageFlex checkShortageFlex;
		
		private GetWorkplaceOfEmployeeAdapter getWorkplaceOfEmployeeAdapter;
		
		private GetAllEmployeeWithWorkplaceAdapter getAllEmployeeWithWorkplaceAdapter;

		@Override
		public ManHourRecordReferenceSetting get() {
			return workChangeablePeriodSettingRepo.get(AppContexts.user().companyId()).orElse(null);
		}

		@Override
		public DatePeriod getPeriod(String employeeId, GeneralDate date) {
			return checkShortageFlex.findClosurePeriod(employeeId, date);
		}

		@Override
		public Map<String, String> getWorkPlace(String userID, String employeeID, GeneralDate date) {
			return getWorkplaceOfEmployeeAdapter.get(userID, employeeID, date);
		}

		@Override
		public Map<String, String> getByCID(String companyId, GeneralDate baseDate) {
			return getAllEmployeeWithWorkplaceAdapter.get(companyId, baseDate);
		}

	}
}
