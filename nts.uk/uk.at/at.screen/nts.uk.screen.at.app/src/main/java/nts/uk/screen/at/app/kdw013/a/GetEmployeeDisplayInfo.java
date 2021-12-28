package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetAllEmployeeWithWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetWorkplaceOfEmployeeAdapter;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.DailyLock;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSettingRepository;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.CheckShortageFlex;
import nts.uk.screen.at.app.kdw013.query.GetEstimatedTimeZones;
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
	private CheckShortageFlex checkShortageFlex;

	@Inject
	private GetDailyPerformanceData getDailyPerformanceData;

	@Inject
	private GetWorkplaceOfEmployeeAdapter getWorkplaceOfEmployeeAdapter;

	@Inject
	private GetAllEmployeeWithWorkplaceAdapter getAllEmployeeWithWorkplaceAdapter;

	@Inject
	private GetEstimatedTimeZones getEstimatedTimeZones;

	@Inject
	private GetLockStatusOfDay getLockStatusOfDay;

	/**
	 * 
	 * @param sid 社員ID
	 * @param refDate 基準日
	 * @param period 表示期間
	 * @param itemIds 対象項目リスト： List<工数実績項目ID>
	 */
	public EmployeeDisplayInfo getInfo(String sid, GeneralDate refDate, DatePeriod period, List<String> itemIds) {

		EmployeeDisplayInfo result = new EmployeeDisplayInfo();

		String companyId = AppContexts.user().companyId();

		// 1. get(ログイン会社ID) 工数実績参照設定

		this.workChangeablePeriodSettingRepo.get(companyId).ifPresent(ms -> {
			// 2. 作業修正可能開始日付を取得する(@Require, 社員ID) 作業変更可能期間設定.isPresent
			RequireImpl1 require = new RequireImpl1(workChangeablePeriodSettingRepo, checkShortageFlex,
					getWorkplaceOfEmployeeAdapter, getAllEmployeeWithWorkplaceAdapter);
			GeneralDate workStartDate = ms.getWorkCorrectionStartDate(require, sid);

			result.setWorkStartDate(workStartDate);
		});

		// 3. call 社員ID,基準日 List＜確認者>

		List<ConfirmerDto> confirms = this.getWorkConfirmationStatus.get(sid, refDate);

		result.setLstComfirmerDto(confirms);

		// 4: <call>(社員ID,表示期間,対象項目リスト)
		GetDailyPerformanceDataResult dailyPerformanceData = getDailyPerformanceData.get(sid, period,
				itemIds.stream().filter(x -> x != null).map(x -> Integer.valueOf(x)).collect(Collectors.toList()));

		result.setDailyPerformanceData(dailyPerformanceData);

		// 5. 工数入力目安時間帯を取得する

		dailyPerformanceData.getLstIntegrationOfDaily().forEach(id -> {
			result.getEstimateZones().add(EstimatedTimeZoneDto.fromDomain(this.getEstimatedTimeZones.get(id)));
		});

		// 6. 日の実績のロック状況を取得する

		List<DailyLock> lockInfos = this.getLockStatusOfDay.get(sid, period);

		result.setLockInfos(lockInfos.stream().map(li -> DailyLockDto.fromDomain(li)).collect(Collectors.toList()));

		return result;
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
