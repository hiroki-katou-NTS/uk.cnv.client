package nts.uk.screen.at.app.kdw013.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ApprovalStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ConfirmStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ModeData;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.approval.ApprovalStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.ConfirmStatusActualDayChange;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLink;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLinkRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrRecordConvertResult;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrRecordTaskDetailToAttendanceItemService;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalConfirmCache;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemParent;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance.GetListTimeZoneDeletions;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.実績登録パラメータを作成する.実績登録パラメータを作成する
 */
@Stateless
public class CreateDpItemQuery {
	
	@Inject
	private ManHourRecordAndAttendanceItemLinkRepository  manHourRepo;
	
	@Inject
	private AttendanceItemConvertFactory  attendanceItemConvertFactory;
	
	@Inject
	private ApprovalStatusActualDayChange approvalStatusActualDayChange;
	
	@Inject
	private ConfirmStatusActualDayChange confirmStatusActualDayChange;
	
	@Inject
	private GetListTimeZoneDeletions getListTimeZoneDeletions;
	
	/**
	 * 実績登録パラメータを作成する
	 * 
	 * @param empTarget 対象社員 
	 * @param dateLst 対象日リスト 
	 * @param manHrlst 修正実績詳細 List<工数実績変換結果>
	 * @param integrationOfDailys 実績リスト List<日別実績(Work)>
	 */
	public DPItemParent CreateDpItem(String empTarget, List<GeneralDate> dateLst, List<ManHrRecordConvertResult> manHrlst,
			List<IntegrationOfDaily> integrationOfDailys) {
		
		String cId= AppContexts.user().companyId();
		
		String sId= AppContexts.user().employeeId();
		
		DatePeriod period  = createPeriod(dateLst);
		
		List<DPItemValue> dpitems = new ArrayList<>();
		
		//修正実績詳細をLoopする
		
		manHrlst.forEach(manhr -> {
			ManHrRecordTaskDetailToAttendanceItemServiceImpl require = new ManHrRecordTaskDetailToAttendanceItemServiceImpl(
					manHourRepo);

			// 1. 変換する(@Require, ItemValue, 工数実績作業詳細) 勤怠項目リスト
			List<ItemValue> taskItems = ManHrRecordTaskDetailToAttendanceItemService.convert(require,
					manhr.getManHrContents(), manhr.getTaskList());
			
			// 2 .休憩時間帯を勤怠項目に変換する
			List<ItemValue> itemVals = ConvertBreakTimeToAttendanceTtems(manhr, integrationOfDailys);
			
			// 3. call 値が変更している休憩項目のみ抽出する
			List<ItemValue> itemChangeds = extraItemsChangeds(manhr.getManHrContents(), itemVals);
			
			// 4. DPItemValueを作成する
			
			dpitems.addAll(createDPItemValue(empTarget, itemChangeds, taskItems, manhr));
		});
		
		// 5. [No.585]日の実績の承認状況を取得する（NEW）
		List<ApprovalStatusActualResult> lstApproval = approvalStatusActualDayChange.processApprovalStatus(cId, sId,
				Arrays.asList(empTarget), Optional.of(period), Optional.empty(), ModeData.NORMAL.value);
		// 6. [No.584]日の実績の確認状況を取得する（NEW）
		List<ConfirmStatusActualResult> lstConfirm = confirmStatusActualDayChange.processConfirmStatus(cId, sId,
				Arrays.asList(empTarget), Optional.of(period), Optional.empty());
		// 7. approvalConfirmCacheを作成する
		ApprovalConfirmCache approvalConfirmCache = new ApprovalConfirmCache(sId, Arrays.asList(empTarget), period, 0,
				lstConfirm, lstApproval);
		// 8. 「日別勤怠(Work)」を取得する
		
		// 9. 取得する(社員ID, 年月日)
		Map<GeneralDate, List<Integer>> mapTimeZones = this.getListTimeZoneDeletions.get(empTarget, dateLst);
		// 10. dailyEditsを作成する
		/**
		 * 取得した「Map<年月日,List<勤怠項目ID>>」から勤怠項目IDが含まれている「日別勤怠(Work)．編集状態．勤怠項目ID」を削除してdailyEdits作成する
		 * ※年月日と日別勤怠(Work)．年月日が同じものをチェックすること
		 * 
		 */
		
		List<DailyRecordDto> ids = integrationOfDailys.stream().map(x -> DailyRecordDto.from(x))
				.collect(Collectors.toList());
		
		mapTimeZones.entrySet().forEach(entry -> {

			Optional<IntegrationOfDaily> idOpt = integrationOfDailys.stream()
					.filter(id -> id.getYmd().equals(entry.getKey())).findFirst();

			idOpt.ifPresent(id -> {
				id.getEditState().removeIf(es -> entry.getValue().indexOf(es.getAttendanceItemId()) != -1);
			});

		});

		List<DailyRecordDto> dailyEdits = integrationOfDailys.stream().map(x -> DailyRecordDto.from(x))
				.collect(Collectors.toList());

		// 11.DPItemParentを作成する
		
		return createDPItemParent(empTarget, dpitems, approvalConfirmCache, ids, period, dailyEdits);
				
			
	}

	private DPItemParent createDPItemParent(String empTarget, List<DPItemValue> dpitems, ApprovalConfirmCache approvalConfirmCache, List<DailyRecordDto> ids, DatePeriod period , List<DailyRecordDto> dailyEdits) {
		
		/**
		 * ■DPItemParent
		 * mode = 0
		 * employeeId = INPUT「対象社員」
		 * itemValues = 作成したList<DPItemValue>
		 * dataCheckSign = List.Empty
		 * dataCheckApproval = List.Empty
		 * dateRange = 作成したList<DPItemValue>の「date」が一番小さい日～一番大きい日で期間を作成する
		 * spr = NULL
		 * monthValue =NULL
		 * dailyOlds = INPUT「日別実績(Work)」から「itemValues．date」と同じものをセットする
		 * dailyEdits = INPUT「日別実績(Work)」から「itemValues．date」と同じものをセットする
		 * dailyOldForLog = INPUT「日別実績(Work)」から「itemValues．date」と同じものをセットする
		 * flagCalculation = false
		 * cellEdits = List.Empty
		 * lstAttendanceItem = List.Empty
		 * lstData = List.Empty
		 * lstSidDateDomainError = List.Empty
		 * errorAllSidDate = false
		 * lstNotFoundWorkType = List.Empty
		 * showDialogError = false
		 * showFlex = false
		 * checkDailyChange = true
		 * approvalConfirmCache = 作成した「approvalConfirmCache」
		 * domainMonthOpt = Optional.empty
		 * paramCommonAsync = NULL
		 */
		return 	new DPItemParent(
    			0, 
    			empTarget,
    			dpitems, 
    			new ArrayList<>(), 
    			new ArrayList<>(), 
    			new DateRange(period.start(), period.end()) , 
    			null, 
    			null, 
    			ids,
    			dailyEdits, 
    			ids, 
    			false, 
    			new ArrayList<>(), 
    			new HashMap<>(), 
    			new ArrayList<>(), 
    			new ArrayList<>(), 
    			false, 
    			new ArrayList<>(), 
    			false, 
    			false, 
    			true, 
    			approvalConfirmCache, 
    			Optional.empty(), 
    			null,
    			null);
	}

	private DatePeriod createPeriod(List<GeneralDate> dateLst) {
		GeneralDate start = Collections.min(dateLst);
		GeneralDate end = Collections.max(dateLst);
		return new DatePeriod(start, end);
	}

	private List<DPItemValue> createDPItemValue(String empTarget, List<ItemValue> itemChangeds, List<ItemValue> taskItems,
			ManHrRecordConvertResult manhr) {
		// $対象項目 = DS「工数実績作業詳細から勤怠項目に変換する」で取得した項目 + 「$変更休憩リスト」

		taskItems.addAll(itemChangeds);
		/**
		 * ■$対象項目から「DPItemValue」を作成する
		 * rowId = INPUT「対象社員」+ 処理中の「工数実績変換結果．年月日」
		 * columnKey = INPUT「対象社員」+処理中の「工数実績変換結果．年月日」+$対象項目.itemId
		 * itemId = $対象項目.itemId
		 * value = $対象項目.value
		 * valueType = $対象項目.valueType
		 * layoutCode = $対象項目.layoutCode
		 * employeeId = INPUT「対象社員」
		 * date = 処理中の「工数実績変換結果．年月日」
		 * typeGroup = NULL
		 * message = NULL
		 */

		return taskItems.stream().map(iv -> new DPItemValue(empTarget, manhr.getYmd(), iv))
				.collect(Collectors.toList());

	}

	private List<ItemValue> extraItemsChangeds(List<ItemValue> taskItems, List<ItemValue> itemVals) {
		
		// $休憩項目リスト =
		// {157,159,163,165,169,171,175,177,181,183,187,189,193,195,199,201,205,207,211,213}
		List<Integer> itemIds = Arrays.asList(157, 159, 163, 165, 169, 171, 175, 177, 181, 183, 187, 189, 193, 195, 199,
				201, 205, 207, 211, 213);

		// $既存リスト = INPUT「勤怠項目リスト」：filter $休憩項目値.含む（$.itemId）
		List<ItemValue> existingList =  new ArrayList<>();
		
		itemIds.stream().forEach(id -> {
			taskItems.stream().filter(ti -> ti.getItemId() == id).findFirst().ifPresent(ti -> {
				existingList.add(ti);
			});
		});
		
		// 「$既存リスト」と「$休憩項目値」を比較してvalueが異なっている項目を「$変更休憩リスト」に追加する

		List<ItemValue> changedList = new ArrayList<>();
		
		existingList.forEach(eItem -> {

			itemVals.stream().filter(iv -> iv.getItemId() == eItem.getItemId()).findFirst().ifPresent(iv -> {
				if (iv.getValue() != null && !iv.getValue().equals(eItem.getValue())) {
					changedList.add(iv);
				}
			});
		});
		
		//変更休憩リスト
		return changedList;

	}

	private List<ItemValue> ConvertBreakTimeToAttendanceTtems(
			ManHrRecordConvertResult manhr, List<IntegrationOfDaily> integrationOfDailys) {
		
		// $休憩項目リスト =
		// {157,159,163,165,169,171,175,177,181,183,187,189,193,195,199,201,205,207,211,213}
		List<Integer> itemIds = Arrays.asList(157, 159, 163, 165, 169, 171, 175, 177, 181, 183, 187, 189, 193, 195,
				199, 201, 205, 207, 211, 213);
		
		//$対象実績 = INPUT「実績リスト」：$.年月日 == 処理中の「工数実績変換結果．年月日」
		
		Optional<IntegrationOfDaily> targetAchievements = integrationOfDailys.stream()
				.filter(x -> x.getYmd().equals(manhr.getYmd())).findFirst();
				 
		//$ DailyRecordToAttendanceItemConverter = AttendanceItemConvertFactory.createDailyConverter()
		DailyRecordToAttendanceItemConverter converter = attendanceItemConvertFactory.createDailyConverter();
		
		//$DailyRecordToAttendanceItemConverter.setData(INPUT「日別実績（$対象実績 ）」)
		if (targetAchievements.isPresent())
			converter.setData(targetAchievements.get());
		
		//$休憩項目値 = $DailyRecordToAttendanceItemConverter.convert($休憩項目リスト)
		return converter.convert(itemIds);
	}

	@AllArgsConstructor
	private class ManHrRecordTaskDetailToAttendanceItemServiceImpl
			implements ManHrRecordTaskDetailToAttendanceItemService.Require {

		private ManHourRecordAndAttendanceItemLinkRepository manHourRepo;

		@Override
		public List<ManHourRecordAndAttendanceItemLink> get(List<Integer> items) {
			return this.manHourRepo.get(AppContexts.user().companyId(), items);
		}

	}
}
