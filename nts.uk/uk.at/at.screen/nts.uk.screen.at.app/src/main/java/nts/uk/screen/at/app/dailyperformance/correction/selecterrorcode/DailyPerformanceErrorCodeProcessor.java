package nts.uk.screen.at.app.dailyperformance.correction.selecterrorcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ApprovalStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ConfirmStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.approval.ApprovalStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.ConfirmStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.finddata.IFindDataDCRecord;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHist;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskItem;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHolidayRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyQueryProcessor;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceCorrectionProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceScreenRepo;
import nts.uk.screen.at.app.dailyperformance.correction.GetDataDaily;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeNameType;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AffEmploymentHistoryDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalConfirmCache;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalUseSettingDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ColumnSetting;
import nts.uk.screen.at.app.dailyperformance.correction.dto.CorrectionOfDailyPerformance;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPAttendanceItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPCellDataDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPControlDisplayItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPDataDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPErrorDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPErrorSettingDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPHideControlCell;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceEmployeeDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyRecEditSetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DisplayItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.IdentityProcessUseSetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.OperationOfDailyPerformanceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ScreenMode;
import nts.uk.screen.at.app.dailyperformance.correction.dto.WorkInfoOfDailyPerformanceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.checkapproval.ApproveRootStatusForEmpDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.checkshowbutton.DailyPerformanceAuthorityDto;
import nts.uk.screen.at.app.dailyperformance.correction.lock.DPLock;
import nts.uk.screen.at.app.dailyperformance.correction.lock.DPLockDto;
import nts.uk.screen.at.app.dailyperformance.correction.process.CheckClosingEmployee;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class DailyPerformanceErrorCodeProcessor {

	@Inject
	private DailyPerformanceScreenRepo repo;

	@Inject
	private DailyModifyQueryProcessor dailyModifyQueryProcessor;

	@Inject
	private DataDialogWithTypeProcessor dataDialogWithTypeProcessor;

	@Inject
	private DailyPerformanceCorrectionProcessor dailyProcessor;
	
	@Inject
	private DPLock findLock;
	
	@Inject
	private PublicHolidayRepository publicHolidayRepository;
	
	@Inject
	private IFindDataDCRecord iFindDataDCRecord;
	
	@Inject
	private CheckClosingEmployee checkClosingEmployee;
	
	@Inject
	private ConfirmStatusActualDayChange confirmStatusActualDayChange;
	
	@Inject
	private ApprovalStatusActualDayChange approvalStatusActualDayChange;

	private static final String LOCK_APPLICATION = "Application";
	private static final String COLUMN_SUBMITTED = "Submitted";
	public static final int MINUTES_OF_DAY = 24 * 60;
	private static final String STATE_DISABLE = "mgrid-disable";
	private static final String LOCK_APPLICATION_LIST = "ApplicationList";

	public DailyPerformanceCorrectionDto generateData(DateRange dateRange,
			List<DailyPerformanceEmployeeDto> lstEmployee, Integer initScreen, Integer mode, Integer displayFormat,
			CorrectionOfDailyPerformance correct, List<String> errorCodes, List<String> formatCodes, Boolean showLock, Integer closureId) {
		String sId = AppContexts.user().employeeId();
		String companyId = AppContexts.user().companyId();
		DailyPerformanceCorrectionDto screenDto = new DailyPerformanceCorrectionDto();
		String NAME_EMPTY = TextResource.localize("KDW003_82");
		String NAME_NOT_FOUND = TextResource.localize("KDW003_81");
		iFindDataDCRecord.clearAllStateless();
		// identityProcessDto show button A2_6
		// アルゴリズム「本人確認処理の利用設定を取得する」を実行する
		Optional<IdentityProcessUseSetDto> identityProcessDtoOpt = repo.findIdentityProcessUseSet(companyId);
		screenDto.setIdentityProcessDto(identityProcessDtoOpt.isPresent() ? identityProcessDtoOpt.get()
				: new IdentityProcessUseSetDto(false, false, null));
		screenDto.setClosureId(closureId);
		Optional<ApprovalUseSettingDto> approvalUseSettingDtoOpt = repo.findApprovalUseSettingDto(companyId);
		dailyProcessor.setHideCheckbox(screenDto, identityProcessDtoOpt, approvalUseSettingDtoOpt, companyId, mode);

		/**
		 * システム日付を基準に1ヵ月前の期間を設定する | Set date range one month before system date
		 */
		screenDto.setDateRange(dateRange);

		/** 画面制御に関する情報を取得する | Acquire information on screen control */
		// アルゴリズム「社員の日別実績の権限をすべて取得する」を実行する | Execute "Acquire all permissions of
		// employee's daily performance"--
		String roleId = AppContexts.user().roles().forAttendance();
		List<DailyPerformanceAuthorityDto> dailyPerformans = new ArrayList<>();
		if (roleId != null) {
			dailyPerformans = repo.findDailyAuthority(roleId);
		}
		if (dailyPerformans.isEmpty()) {
			throw new BusinessException("Msg_671");
		} else {
			// NO.15
			screenDto.setAuthorityDto(dailyPerformans);
		}

		// get employmentCode
		AffEmploymentHistoryDto employment = repo.getAffEmploymentHistory(companyId, sId, dateRange.getEndDate());
		screenDto.setEmploymentCode(employment == null ? "" : employment.getEmploymentCode());

		List<String> listEmployeeId = lstEmployee.stream().map(e -> e.getId()).collect(Collectors.toList());
		// 社員の締めをチェックする
//		Map<String, List<EmploymentHisOfEmployeeImport>> mapClosingEmpResult = checkClosingEmployee
//				.checkClosingEmployee(companyId, listEmployeeId,
//						new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), screenDto.getClosureId());
		List<DPErrorDto> lstError = this.repo.getListDPError(screenDto.getDateRange(), listEmployeeId, errorCodes);
		Map<String, String> mapIdError = new HashMap<>();
		for (DPErrorDto dto : lstError) {
			mapIdError.put(dto.getEmployeeId(), "");
		}
		lstEmployee = lstEmployee.stream().filter(x -> mapIdError.get(x.getId()) != null).collect(Collectors.toList());
		listEmployeeId = listEmployeeId.stream().filter(x -> mapIdError.containsKey(x)).collect(Collectors.toList());
		if (lstEmployee.isEmpty()) {
			throw new BusinessException("Msg_672");
		}
		screenDto.setLstEmployee(lstEmployee);
		// 表示形式をチェックする | Check display format => UI
		// Create lstData: Get by listEmployee & listDate
		// 日付別の情報を取得する + 個人別の情報を取得する + エラーアラームの情報を取得する | Acquire information by
		// date + Acquire personalized information + Acquire error alarm
		// information
		screenDto.setLstData(dailyProcessor.getListData(screenDto.getLstEmployee(), dateRange, displayFormat));
		// Lấy thành tích nhân viên theo ngày
		/// アルゴリズム「対象日に対応する社員の実績の編集状態を取得する」を実行する | Execute "Acquire edit status
		/// of employee's record corresponding to target date"| lay ve trang
		/// thai sua cua thanh tich nhan vien tuong ung
		List<DailyRecEditSetDto> dailyRecEditSets = repo.getDailyRecEditSet(listEmployeeId, dateRange);
		Map<String, Integer> dailyRecEditSetsMap = dailyRecEditSets.stream()
				.collect(Collectors.toMap(x -> dailyProcessor.mergeString(String.valueOf(x.getAttendanceItemId()), "|",
						x.getEmployeeId(), "|", dailyProcessor.converDateToString(x.getProcessingYmd())),
						x -> x.getEditState()));
		// No 19, 20 show/hide button
		boolean showButton = true;
		if (displayFormat == 0) {
			if (!listEmployeeId.isEmpty() && !sId.equals(listEmployeeId.get(0))) {
				showButton = false;
			}
		}
		OperationOfDailyPerformanceDto dailyPerformanceDto = repo.findOperationOfDailyPerformance();
		DisplayItem disItem = dailyProcessor.getDisplayItems(correct, formatCodes, companyId, screenDto, listEmployeeId,
				showButton, dailyPerformanceDto);
		
		List<DailyModifyResult> results = new ArrayList<>();
		Set<Pair<String, GeneralDate>> setErrorEmpDate = new HashSet<>();
		if(displayFormat == 2) {
			setErrorEmpDate = lstError.stream().map(x -> Pair.of(x.getEmployeeId(), x.getProcessingDate())).collect(Collectors.toSet());
		}
		Pair<List<DailyModifyResult>, List<DailyRecordDto>> resultPair = new GetDataDaily(listEmployeeId, dateRange, disItem.getLstAtdItemUnique(), setErrorEmpDate, dailyModifyQueryProcessor).getAllData();
		results = resultPair.getLeft();
		screenDto.setDomainOld(resultPair.getRight());
		screenDto.getItemValues().addAll(results.isEmpty() ? new ArrayList<>() : results.get(0).getItems());
		screenDto.getItemValues().stream().sorted((x, y) -> x.getItemId() - y.getItemId());
		
		DPControlDisplayItem dPControlDisplayItem = dailyProcessor.getItemIdNames(disItem, showButton);
		/// アルゴリズム「対象日に対応する社員の実績の編集状態を取得する」を実行する | Execute "Acquire edit status
		/// of employee's record corresponding to target date"| lay ve trang
		/// アルゴリズム「実績エラーをすべて取得する」を実行する | Execute "Acquire all actual errors"
		Map<Integer, DPAttendanceItem> mapDP =  dPControlDisplayItem.getMapDPAttendance();
		if (screenDto.getLstEmployee().size() > 0) {
			/// ドメインモデル「社員の日別実績エラー一覧」をすべて取得する +
			/// 対応するドメインモデル「勤務実績のエラーアラーム」をすべて取得する
			/// Acquire all domain model "employee's daily performance error
			/// list" + "work error error alarm" | lay loi thanh tich trong
			/// khoang thoi gian
			if (lstError.size() > 0) {
				// Get list error setting
				List<DPErrorSettingDto> lstErrorSetting = this.repo
						.getErrorSetting(companyId, lstError.stream().map(e -> e.getErrorCode()).collect(Collectors.toList()), true, true, true);
				// Seperate Error and Alarm
				if(lstErrorSetting.isEmpty()) {
					lstError = new ArrayList<>();
				}
				screenDto.addErrorToResponseData(lstError, lstErrorSetting, mapDP, true);
			}
		}
		screenDto.setLstControlDisplayItem(dPControlDisplayItem);
		screenDto.getItemValues().addAll(results.isEmpty() ? new ArrayList<>() : results.get(0).getItems());
		Map<String, DailyModifyResult> resultDailyMap = results.stream()
				.collect(Collectors.toMap(
						x -> dailyProcessor.mergeString(x.getEmployeeId(), "|", x.getDate().toString()),
						Function.identity(), (x, y) -> x));
		//// 11. Excel: 未計算のアラームがある場合は日付又は名前に表示する
		List<WorkInfoOfDailyPerformanceDto> workInfoOfDaily = repo.getListWorkInfoOfDailyPerformance(listEmployeeId,
				dateRange);
		List<DPDataDto> lstData = new ArrayList<DPDataDto>();
		// set error, alarm
//		if (screenDto.getLstEmployee().size() > 0) {
//			if (lstError.size() > 0) {
//				// Get list error setting
//				List<DPErrorSettingDto> lstErrorSetting = this.repo
//						.getErrorSetting(companyId, lstError.stream().map(e -> e.getErrorCode()).collect(Collectors.toList()), true, true, false);
//				// Seperate Error and Alarm
//				screenDto.addErrorToResponseData(lstError, lstErrorSetting, mapDP);
//			}
//		}

		Set<Integer> types = dPControlDisplayItem.getLstAttendanceItem() == null ? new HashSet<>()
				: dPControlDisplayItem.getLstAttendanceItem().stream().map(x -> x.getTypeGroup()).filter(x -> x != null)
						.collect(Collectors.toSet());
		Map<Integer, Map<String, CodeName>> mapGetName = dataDialogWithTypeProcessor
				.getAllCodeName(new ArrayList<>(types), companyId, dateRange.getEndDate());
		CodeNameType codeNameReason = dataDialogWithTypeProcessor.getReason(companyId);
		Map<String, CodeName> codeNameReasonMap = codeNameReason != null
				? codeNameReason.getCodeNames().stream()
						.collect(Collectors.toMap(x -> dailyProcessor.mergeString(x.getCode(), "|", x.getId()), x -> x))
				: Collections.emptyMap();
		CodeNameType codeNameTask = dataDialogWithTypeProcessor.getWork(companyId);
		Map<String, CodeName> codeNameTaskMap = codeNameTask != null
				? codeNameTask.getCodeNames().stream()
						.collect(Collectors.toMap(x -> dailyProcessor.mergeString(x.getCode(), "|", x.getId()), x -> x))
				: Collections.emptyMap();
		List<TaskInitialSelHist> taskInitialSelHistLst = dataDialogWithTypeProcessor.getTaskInitialSel(companyId);
		screenDto.markLoginUser(sId);
		screenDto.createAccessModifierCellState(mapDP);
		screenDto.getLstFixedHeader().forEach(column -> {
			screenDto.getLstControlDisplayItem().getColumnSettings().add(new ColumnSetting(column.getKey(), false));
		});

		// No 20 get submitted application
		// disable check box sign
		Map<String, Boolean> disableSignMap = new HashMap<>();
		Map<String, String> appMapDateSid = dailyProcessor.getApplication(listEmployeeId, dateRange, disableSignMap);
		
		screenDto.getLstFixedHeader().forEach(column -> {
			screenDto.getLstControlDisplayItem().getColumnSettings().add(new ColumnSetting(column.getKey(), false));
		});
		
		DPLockDto dpLock = findLock.checkLockAll(companyId, listEmployeeId, dateRange, sId, mode, identityProcessDtoOpt, approvalUseSettingDtoOpt);
		List<GeneralDate> holidayDate = publicHolidayRepository
				.getpHolidayWhileDate(companyId, dateRange.getStartDate(), dateRange.getEndDate()).stream()
				.map(x -> x.getDate()).collect(Collectors.toList());
//		String keyFind = IdentifierUtil.randomUniqueId();
		
		List<ConfirmStatusActualResult> confirmResults = confirmStatusActualDayChange.processConfirmStatus(companyId, sId, listEmployeeId, Optional.of(new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate())), Optional.empty(), false);

		List<ApprovalStatusActualResult> approvalResults = approvalStatusActualDayChange.processApprovalStatus(companyId, sId, listEmployeeId, Optional.of(new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate())), Optional.empty(), mode, false);
		iFindDataDCRecord.clearAllStateless();
		Map<Pair<String, GeneralDate>, ConfirmStatusActualResult> mapConfirmResult = confirmResults.stream().collect(Collectors.toMap(x -> Pair.of(x.getEmployeeId(), x.getDate()), x -> x));
		Map<Pair<String, GeneralDate>, ApprovalStatusActualResult> mapApprovalResults = approvalResults.stream().collect(Collectors.toMap(x -> Pair.of(x.getEmployeeId(), x.getDate()), x -> x));
		
		//cell hide check box approval
		List<DPHideControlCell> lstCellHideControl = new ArrayList<>();
		Map<String, ItemValue> itemValueMap = new HashMap<>();
		for (DPDataDto data : screenDto.getLstData()) {
			//filter Date in Period
//			if(!dailyProcessor.checkDataInClosing(Pair.of(data.getEmployeeId(), data.getDate()), mapClosingEmpResult)) {
//				continue;
//			}
			
			data.setEmploymentCode(screenDto.getEmploymentCode());
			if (!sId.equals(data.getEmployeeId())) {
				screenDto.setCellSate(data.getId(), LOCK_APPLICATION, STATE_DISABLE);
			}
			// map name submitted into cell
			if (appMapDateSid.containsKey(data.getEmployeeId() + "|" + data.getDate())) {
				data.addCellData(new DPCellDataDto(COLUMN_SUBMITTED,
						appMapDateSid.get(data.getEmployeeId() + "|" + data.getDate()), "", ""));
			} else {
				data.addCellData(new DPCellDataDto(COLUMN_SUBMITTED, "", "", ""));
			}
			data.addCellData(new DPCellDataDto(COLUMN_SUBMITTED, "", "", ""));
			data.addCellData(new DPCellDataDto(LOCK_APPLICATION, "", "", ""));
			data.addCellData(new DPCellDataDto(LOCK_APPLICATION_LIST, "", "", ""));
			//set checkbox sign
			ConfirmStatusActualResult dataSign = mapConfirmResult.get(Pair.of(data.getEmployeeId(), data.getDate()));
			data.setSign(dataSign == null ? false : dataSign.isStatus());
			// state check box sign
			boolean disableSignApp = disableSignMap.containsKey(data.getEmployeeId() + "|" + data.getDate()) && disableSignMap.get(data.getEmployeeId() + "|" + data.getDate());
			
			ApprovalStatusActualResult dataApproval = mapApprovalResults.get(Pair.of(data.getEmployeeId(), data.getDate()));
			//set checkbox approval
			data.setApproval(dataApproval == null ? false : mode == ScreenMode.NORMAL.value ? dataApproval.isStatusNormal() : dataApproval.isStatus());
			ApproveRootStatusForEmpDto approvalCheckMonth = dpLock.getLockCheckMonth().get(data.getEmployeeId() + "|" + data.getDate());
			
			DailyModifyResult resultOfOneRow = dailyProcessor.getRow(resultDailyMap, data.getEmployeeId(), data.getDate());
			if (resultOfOneRow != null && data.isErrorOther()) {
				dailyProcessor.lockDataCheckbox(sId, screenDto, data, identityProcessDtoOpt, approvalUseSettingDtoOpt, mode, data.isApproval(), data.isSign());

//				boolean lockDaykWpl = dailyProcessor.checkLockAndSetState(dpLock.getLockDayAndWpl(), data);
//                boolean lockHist = dailyProcessor.lockHist(dpLock.getLockHist(), data);
//                boolean lockApprovalMonth = approvalCheckMonth == null ? false : approvalCheckMonth.isCheckApproval();
//                boolean lockConfirmMonth = dailyProcessor.checkLockConfirmMonth(dpLock.getLockConfirmMonth(), data);
//                
//                lockDaykWpl = dailyProcessor.lockAndDisable(screenDto, data, mode, lockDaykWpl, data.isApproval(), lockHist, data.isSign(), lockApprovalMonth, lockConfirmMonth);	
                
				boolean lockDaykWpl = false, lockDay = false, lockWpl = false, lockHist = false, lockApprovalMonth = false, lockConfirmMonth = false;
				if (showLock == null || showLock) {
					lockDay = dailyProcessor.checkLockDay(dpLock.getLockDayAndWpl(), data);
					lockWpl = dailyProcessor.checkLockWork(dpLock.getLockDayAndWpl(), data);
					lockHist = dailyProcessor.lockHist(dpLock.getLockHist(), data);
					lockApprovalMonth = approvalCheckMonth == null ? false : approvalCheckMonth.isCheckApproval();
					lockConfirmMonth = dailyProcessor.checkLockConfirmMonth(dpLock.getLockConfirmMonth(), data);
					lockDaykWpl = lockDay || lockWpl;
					lockDaykWpl = dailyProcessor.lockAndDisable(screenDto, data, mode, lockDaykWpl, dataApproval == null ? false : dataApproval.isStatusNormal(), lockHist,
							data.isSign(), lockApprovalMonth, lockConfirmMonth);
				} else {
					lockDaykWpl = dailyProcessor.lockAndDisable(screenDto, data, mode, lockDaykWpl, false, lockHist,
							false, lockApprovalMonth, lockConfirmMonth);
				}
				
				//set cell state day
				//if(!textColorSpr){
					dailyProcessor.setTextColorDay(screenDto, data.getDate(), "date", data.getId(), holidayDate);
				//}
				
				if (resultOfOneRow != null) {
					itemValueMap = resultOfOneRow.getItems().stream()
							.collect(Collectors.toMap(x -> dailyProcessor.mergeString(String.valueOf(x.getItemId()),
									"|", data.getEmployeeId(), "|", data.getDate().toString()), x -> x));
				}
				Optional<TaskItem> opTaskItem = dailyProcessor.getTaskItemByEmpDate(taskInitialSelHistLst, data.getEmployeeId(), data.getDate());
				Optional<DailyRecordDto> opDailyRecordDto = screenDto.getDomainOld().stream().filter(x -> x.getEmployeeId().equals(data.getEmployeeId()) && x.getDate().equals(data.getDate())).findAny();
				dailyProcessor.processCellData(NAME_EMPTY, NAME_NOT_FOUND, screenDto, dPControlDisplayItem,
						mapGetName, codeNameReasonMap, codeNameTaskMap, itemValueMap, data, lockDaykWpl, dailyRecEditSetsMap, null, opTaskItem, opDailyRecordDto);
				lstData.add(data);
				Optional<WorkInfoOfDailyPerformanceDto> optWorkInfoOfDailyPerformanceDto = workInfoOfDaily.stream()
						.filter(w -> w.getEmployeeId().equals(data.getEmployeeId())
								&& w.getYmd().equals(data.getDate()))
						.findFirst();
				if (optWorkInfoOfDailyPerformanceDto.isPresent()
						&& optWorkInfoOfDailyPerformanceDto.get().getState() == CalculationState.No_Calculated)
					screenDto.setAlarmCellForFixedColumn(data.getId(), displayFormat);
				
				if(lockDay || lockHist || dataSign == null || (!dataSign.isStatus() ? (!dataSign.notDisableForConfirm() ? true : disableSignApp) : !dataSign.notDisableForConfirm())){
					screenDto.setCellSate(data.getId(), DPText.LOCK_SIGN, DPText.STATE_DISABLE);
				}
				
				if(lockDay || lockHist || dataApproval == null || (mode == ScreenMode.NORMAL.value ? !dataApproval.notDisableNormal() : !dataApproval.notDisableApproval())) {
					screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_DISABLE);
				}
				
				if(dataApproval == null) {
					screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_ERROR);
					lstCellHideControl.add(new DPHideControlCell(data.getId(), DPText.LOCK_APPROVAL));
				}
			}
		}
		screenDto.setLstHideControl(lstCellHideControl);
		screenDto.setLstData(lstData);
		screenDto.setApprovalConfirmCache(new ApprovalConfirmCache(sId, listEmployeeId,
				new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), mode, confirmResults,
				approvalResults));
		return screenDto;
	}

}
