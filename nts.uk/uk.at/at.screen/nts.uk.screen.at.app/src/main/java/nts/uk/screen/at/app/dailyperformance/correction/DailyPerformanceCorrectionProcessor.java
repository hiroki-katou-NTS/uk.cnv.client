/**
 * 1:57:38 PM Aug 21, 2017
 */
package nts.uk.screen.at.app.dailyperformance.correction;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.adapter.person.EmployeeInfoFunAdapterDto;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.dom.adapter.employment.EmploymentHisOfEmployeeImport;
import nts.uk.ctx.at.record.dom.adapter.initswitchsetting.DateProcessedRecord;
import nts.uk.ctx.at.record.dom.adapter.initswitchsetting.InitSwitchSetAdapter;
import nts.uk.ctx.at.record.dom.adapter.initswitchsetting.InitSwitchSetDto;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.ApprovalStatusAdapter;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.dtos.ApprovalRootOfEmployeeImport;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.dtos.ApproveRootStatusForEmpImport;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.enums.ApprovalActionByEmpl;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.enums.ApprovalStatusForEmployee;
import nts.uk.ctx.at.record.dom.daily.dailyperformance.classification.EnumCodeName;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ApprovalStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ConfirmStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ReleasedAtr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.approval.ApprovalStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.ConfirmStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.finddata.IFindDataDCRecord;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.GetClosurePeriod;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.SettingUnitType;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.YourselfConfirmError;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;
import nts.uk.ctx.at.request.app.find.application.applicationlist.AppGroupExportDto;
import nts.uk.ctx.at.request.app.find.application.applicationlist.ApplicationExportDto;
import nts.uk.ctx.at.request.app.find.application.applicationlist.ApplicationListForScreen;
import nts.uk.ctx.at.request.dom.application.ReflectedState_New;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHolidayRepository;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeUseSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.DailyAttendanceItemNameAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.DailyAttendanceItemNameAdapterDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.pub.workrule.closure.PresentClosingPeriodExport;
import nts.uk.ctx.at.shared.pub.workrule.closure.ShClosurePub;
import nts.uk.ctx.bs.employee.pub.workplace.ResultRequest597Export;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.screen.at.app.dailymodify.command.common.ProcessCommonCalc;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyQueryProcessor;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.ValidatorDataDailyRes;
import nts.uk.screen.at.app.dailyperformance.correction.closure.FindClosureDateService;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeNameType;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ActualLockDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AffEmploymentHistoryDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalConfirmCache;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalUseSettingDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AuthorityFomatDailyDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AuthorityFormatInitialDisplayDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AuthorityFormatSheetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ChangeSPR;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ClosureDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ColumnSetting;
import nts.uk.screen.at.app.dailyperformance.correction.dto.CorrectionOfDailyPerformance;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DCMessageError;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPAttendanceItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPAttendanceItemControl;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPBusinessTypeControl;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPCellDataDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPControlDisplayItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPDataDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPErrorDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPErrorSettingDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPHeaderDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPHideControlCell;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPSheetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceEmployeeDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyRecEditSetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DatePeriodInfo;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRangeClosureId;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DisplayFormat;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DisplayItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DivergenceTimeDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ErrorReferenceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.FormatDPCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.IdentityProcessUseSetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ObjectShare;
import nts.uk.screen.at.app.dailyperformance.correction.dto.OperationOfDailyPerformanceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.OptionalItemDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.SPRCheck;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ScreenMode;
import nts.uk.screen.at.app.dailyperformance.correction.dto.WorkFixedDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.WorkInfoOfDailyPerformanceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.AggrPeriodClosure;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.DPCorrectionStateParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.checkapproval.ApproveRootStatusForEmpDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.checkshowbutton.DailyPerformanceAuthorityDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.companyhist.AffComHistItemAtScreen;
import nts.uk.screen.at.app.dailyperformance.correction.dto.type.TypeLink;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workplacehist.WorkPlaceHistTemp;
import nts.uk.screen.at.app.dailyperformance.correction.error.DCErrorInfomation;
import nts.uk.screen.at.app.dailyperformance.correction.error.ShowDialogError;
import nts.uk.screen.at.app.dailyperformance.correction.lock.CheckLockDataDaily;
import nts.uk.screen.at.app.dailyperformance.correction.lock.ClosureSidDto;
import nts.uk.screen.at.app.dailyperformance.correction.lock.ConfirmationMonthDto;
import nts.uk.screen.at.app.dailyperformance.correction.lock.DPLock;
import nts.uk.screen.at.app.dailyperformance.correction.lock.DPLockDto;
import nts.uk.screen.at.app.dailyperformance.correction.month.ErrorMonthProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * @author hungnm
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DailyPerformanceCorrectionProcessor {

	@Inject
	private DailyPerformanceScreenRepo repo;

	@Inject
	private DailyModifyQueryProcessor dailyModifyQueryProcessor;

	@Inject
	private DailyAttendanceItemNameAdapter dailyAttendanceItemNameAdapter;

	@Inject
	private DataDialogWithTypeProcessor dataDialogWithTypeProcessor;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;

	@Inject
	private ShClosurePub shClosurePub;

	@Inject
	private ApplicationListForScreen applicationListFinder;
	
	@Inject
	private ApprovalStatusAdapter approvalStatusAdapter;
	
	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeavingOfDailyPerformanceRepository;

	@Inject
	private DPLock findLock;
		
	@Inject
	private ShowDialogError showDialogError;
	
	@Resource
	private ManagedExecutorService executorService;

	@Inject
	private ErrorMonthProcessor errorMonthProcessor;
	
	@Inject
	private PublicHolidayRepository publicHolidayRepository;
	
//	@Inject
//	private CheckClosingEmployee checkClosingEmployee;
	
	@Inject
	private RoleRepository roleRepository;
	
//	@Inject
//	private SyWorkplacePub syWorkplacePub;
	
	@Inject
	private WorkplacePub workplacePub;
	
	@Inject
	private CheckLockDataDaily checkLockDataDaily;
	
	@Inject
	private InitSwitchSetAdapter initSwitchSetAdapter;
	
	@Inject
	private ApprovalStatusActualDayChange approvalStatusActualDayChange;
	
	@Inject
	private ConfirmStatusActualDayChange confirmStatusActualDayChange;

	@Inject
	private FindClosureDateService findClosureService;
	
	@Inject
	private IFindDataDCRecord iFindDataDCRecord;
	
	@Inject 
	private RecordDomRequireService requireService;
	
	@Inject
	private ClosureRepository closureRepository;
	
    static final Integer[] DEVIATION_REASON  = {436, 438, 439, 441, 443, 444, 446, 448, 449, 451, 453, 454, 456, 458, 459, 799, 801, 802, 804, 806, 807, 809, 811, 812, 814, 816, 817, 819, 821, 822};
	public static final Map<Integer, Integer> DEVIATION_REASON_MAP = IntStream.range(0, DEVIATION_REASON.length-1).boxed().collect(Collectors.toMap(x -> DEVIATION_REASON[x], x -> x/3 +1));
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DPText.DATE_FORMAT);

	/**
	 * Get List Data include:<br/>
	 * Employee and Date
	 **/
	public List<DPDataDto> getListData(List<DailyPerformanceEmployeeDto> listEmployee, DateRange dateRange,
			Integer displayFormat) {
		List<DPDataDto> result = new ArrayList<>();
		if (listEmployee.size() > 0) {
			List<GeneralDate> lstDate = dateRange.toListDate();
			int dataId = 0;
			for (int j = 0; j < listEmployee.size(); j++) {
				DailyPerformanceEmployeeDto employee = listEmployee.get(j);
				for (int i = 0; i < lstDate.size(); i++) {
					result.add(new DPDataDto(
							displayFormat + "_" + convertFormatString(employee.getId()) + "_" + convertFormatString(converDateToString(lstDate.get(i))) + "_"
									+ convertFormatString(converDateToString(lstDate.get(lstDate.size() - 1))) + "_" + dataId,
							"", "", lstDate.get(i), false, false, employee.getId(), employee.getCode(),
							employee.getBusinessName(), employee.getWorkplaceId(), "", ""));
					dataId++;
				}
			}
		}
		return result;
	}
    
	public String convertFormatString(String data){
		return data.replace("-", "_");
	}
	public String converDateToString(GeneralDate genDate) {
		return DATE_FORMATTER.format(genDate.toLocalDate());
	}

	public DailyPerformanceCorrectionDto generateData(DailyPerformanceCorrectionDto screenDto,
			List<DailyPerformanceEmployeeDto> lstEmployee, Integer initScreen, Integer mode, Integer displayFormat,
			CorrectionOfDailyPerformance correct, List<String> formatCodes, Boolean showError, Boolean showLock, ObjectShare objectShare, Integer closureId) {
		//System.out.println("start daily");
		iFindDataDCRecord.clearAllStateless();
		//long startTime = System.currentTimeMillis();
		String NAME_EMPTY = TextResource.localize("KDW003_82");
		String NAME_NOT_FOUND = TextResource.localize("KDW003_81");
		List<String> listEmployeeId = screenDto.getEmployeeIds();
		//List<String> changeEmployeeIds = screenDto.getEmployeeIds();
		String companyId = AppContexts.user().companyId();
		String sId =  AppContexts.user().employeeId();
		DateRange dateRange = screenDto.getDateRange();
//		CountDownLatch countDownLatch = new CountDownLatch(1);
		val dateRangeTemp = dateRange;
		
		Optional<IdentityProcessUseSetDto> identityProcessDtoOpt = screenDto.getIdentityProcessDtoOpt();
		Optional<ApprovalUseSettingDto> approvalUseSettingDtoOpt = screenDto.getApprovalUseSettingDtoOpt();
		List<DPErrorDto> lstError = screenDto.getDPErrorDto();
		DisplayItem disItem = screenDto.getDisItem();
		
		Map<Integer, DPAttendanceItem> mapDP = screenDto.getLstControlDisplayItem().getMapDPAttendance();
						
		// disable cell
		screenDto.markLoginUser(sId);
		//long start1 = System.currentTimeMillis();
		screenDto.createAccessModifierCellState(mapDP);
		//System.out.println("time disable : " + (System.currentTimeMillis() - startTime));
		
		// get data from DB
		List<DailyModifyResult> results = new ArrayList<>();
		Set<Pair<String, GeneralDate>> setErrorEmpDate = new HashSet<>();
		if(displayFormat == 2) {
			setErrorEmpDate = lstError.stream().map(x -> Pair.of(x.getEmployeeId(), x.getProcessingDate())).collect(Collectors.toSet());
		}
		Pair<List<DailyModifyResult>, List<DailyRecordDto>> resultPair = new GetDataDaily(listEmployeeId, dateRange, disItem.getLstAtdItemUnique(), setErrorEmpDate, dailyModifyQueryProcessor).getAllData();
		results = resultPair.getLeft();
		screenDto.setDomainOld(resultPair.getRight());
		screenDto.getItemValues().addAll(results.isEmpty() ? new ArrayList<>() : results.get(0).getItems());
//		List<ItemValue> dataValue = screenDto.getItemValues().stream().sorted((x, y) -> x.getItemId() - y.getItemId()).collect(Collectors.toList());
		Map<String, DailyModifyResult> resultDailyMap = results.stream().collect(Collectors
				.toMap(x -> mergeString(x.getEmployeeId(), "|", x.getDate().toString()), Function.identity(), (x, y) -> x));
		//System.out.println("time get data : " + (System.currentTimeMillis() - startTime));
		
		//// 11. Excel: 未計算のアラームがある場合は日付又は名前に表示する
		// Map<Integer, Integer> typeControl =
		//// lstAttendanceItem.stream().collect(Collectors.toMap(DPAttendanceItem::
		//// getId, DPAttendanceItem::getAttendanceAtr));
		List<WorkInfoOfDailyPerformanceDto> workInfoOfDaily = repo.getListWorkInfoOfDailyPerformance(listEmployeeId,
				dateRange);
		//System.out.println("time get wokInfo : " + (System.currentTimeMillis() - startTime));
		// set error, alarm
		if (screenDto.getLstEmployee().size() > 0) {
			if (lstError.size() > 0) {
				// Get list error setting
				//long timeT = System.currentTimeMillis();
				List<DPErrorSettingDto> lstErrorSetting = this.repo
						.getErrorSetting(companyId, lstError.stream().map(e -> e.getErrorCode()).collect(Collectors.toList()), true, true, false);
				// Seperate Error and Alarm
				if(lstErrorSetting.isEmpty()) {
					lstError = new ArrayList<>();
				}
				//System.out.println("khoang time load Error: "+ (System.currentTimeMillis() - timeT));
				screenDto.addErrorToResponseData(lstError, lstErrorSetting, mapDP, false);
			}
		}

		// No 20 get submitted application
		// disable check box sign
		Map<String, Boolean> disableSignMap = new HashMap<>();
		Map<String, String> appMapDateSid = getApplication(listEmployeeId, dateRange, disableSignMap);
		
		screenDto.getLstFixedHeader().forEach(column -> {
			screenDto.getLstControlDisplayItem().getColumnSettings().add(new ColumnSetting(column.getKey(), false));
		});
		
		DPLockDto dpLockDto = findLock.checkLockAll(companyId, listEmployeeId, dateRangeTemp, sId, mode, identityProcessDtoOpt, approvalUseSettingDtoOpt);
		//System.out.println("time get lock : " + (System.currentTimeMillis() - startTime));
//		String keyFind = IdentifierUtil.randomUniqueId();
		//long startTime1 = System.currentTimeMillis();
		List<ConfirmStatusActualResult> confirmResults = new ArrayList<>();
		List<ApprovalStatusActualResult> approvalResults = new ArrayList<>();
		
//		if (displayFormat != 1) {
//			confirmResults = confirmApprovalStatusActualDay.processConfirmStatus(companyId, listEmployeeId,
//					new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), screenDto.getClosureId(),
//					Optional.of(keyFind));
			confirmResults = confirmStatusActualDayChange.processConfirmStatus(companyId, sId, listEmployeeId, Optional.of(new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate())), Optional.empty(), false);
			//System.out.println("khoang thoi gian load checkbox 1:" + (System.currentTimeMillis() - startTime1));

			approvalResults = approvalStatusActualDayChange.processApprovalStatus(companyId, sId, listEmployeeId, Optional.of(new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate())), Optional.empty(), mode, false);
			iFindDataDCRecord.clearAllStateless();
//			approvalResults = approvalStatusActualDay.processApprovalStatus(companyId, listEmployeeId,
//					new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), screenDto.getClosureId(), mode,
//					Optional.of(keyFind));
			// approvalResults = new ArrayList<>();
//			startTime1 = System.currentTimeMillis();
			//System.out.println("khoang thoi gian load checkbox 2:" + (System.currentTimeMillis() - startTime1));
			//System.out.println("time load check box : " + (System.currentTimeMillis() - startTime));
//		} else {
//			confirmResults = confirmApprovalStatusActualDay.processConfirmStatus(companyId, listEmployeeId,
//					dateRange.getStartDate(), screenDto.getClosureId());
//			System.out.println("thoi gian load checkbox 1:" + (System.currentTimeMillis() - startTime1));
//
//			approvalResults = approvalStatusActualDay.processApprovalStatus(companyId, listEmployeeId,
//					dateRange.getStartDate(), screenDto.getClosureId(), mode);
//			// approvalResults = new ArrayList<>();
//			System.out.println("thoi gian load checkbox 2:" + (System.currentTimeMillis() - startTime1));
//		}
		
		// 社員の締めをチェックする
//		Map<String, List<EmploymentHisOfEmployeeImport>> mapClosingEmpResult = checkClosingEmployee
//				.checkClosingEmployee(companyId, changeEmployeeIds,
//						new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), screenDto.getClosureId());

		List<DailyRecEditSetDto> dailyRecEditSets = repo.getDailyRecEditSet(listEmployeeId, dateRange);
		Map<String, Integer> dailyRecEditSetsMap = dailyRecEditSets.stream()
				.collect(Collectors.toMap(x -> mergeString(String.valueOf(x.getAttendanceItemId()), "|",
						x.getEmployeeId(), "|", converDateToString(x.getProcessingYmd())), x -> x.getEditState()));
		
		mapDataIntoGrid(screenDto, sId, appMapDateSid, listEmployeeId, resultDailyMap, mode, displayFormat, showLock,
				identityProcessDtoOpt, approvalUseSettingDtoOpt, dateRange, objectShare,
				companyId, disItem, screenDto.getLstControlDisplayItem(), dailyRecEditSetsMap,
				workInfoOfDaily, disableSignMap, NAME_EMPTY, NAME_NOT_FOUND, dpLockDto, confirmResults, approvalResults);
		// set cell data
		//System.out.println("time get data into cell : " + (System.currentTimeMillis() - startTime));
		//bug 107966 disable edit flex in case lock
		if(displayFormat == 0 && screenDto.getMonthResult() != null && screenDto.getMonthResult().getFlexShortage() != null) {
			boolean disableFlex = checkLockDataDaily.checkLockInPeriod(screenDto.getLstData(),
					screenDto.getMonthResult().getFlexShortage().getPeriodCheckLock());
			screenDto.setLockDisableFlex(disableFlex);
			screenDto.setRangeLock(screenDto.getMonthResult().getFlexShortage().getRangeLock());
		}
		OperationOfDailyPerformanceDto dailyPerformanceDto = repo.findOperationOfDailyPerformance();
		screenDto.setShowErrorDialog(showDialogError.showDialogError(lstError, showError, dailyPerformanceDto));
		screenDto.setDateRange(screenDto.getDatePeriodResult());
		screenDto.resetDailyInit();
		//System.out.println("end daily"+ (System.currentTimeMillis() - startTime));
		screenDto.setApprovalConfirmCache(new ApprovalConfirmCache(sId, listEmployeeId,
				new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), mode, confirmResults,
				approvalResults));
		return screenDto;
	}

	public void mapDataIntoGrid(DailyPerformanceCorrectionDto screenDto, String sId, Map<String, String> appMapDateSid, List<String> listEmployeeId, 
			Map<String, DailyModifyResult> resultDailyMap, Integer mode, Integer displayFormat, Boolean showLock,
			Optional<IdentityProcessUseSetDto> identityProcessDtoOpt, Optional<ApprovalUseSettingDto> approvalUseSettingDtoOpt,
			DateRange dateRange, ObjectShare objectShare, String companyId,
			DisplayItem disItem, DPControlDisplayItem dPControlDisplayItem,
			Map<String, Integer> dailyRecEditSetsMap, List<WorkInfoOfDailyPerformanceDto> workInfoOfDaily, Map<String, Boolean> disableSignMap,
			String NAME_EMPTY, String NAME_NOT_FOUND, DPLockDto dpLock, List<ConfirmStatusActualResult> confirmResults, List<ApprovalStatusActualResult> approvalResults
//			Map<String, List<EmploymentHisOfEmployeeImport>> mapClosingEmpResult
			){
		Map<String, ItemValue> itemValueMap = new HashMap<>();
		List<DPDataDto> lstData = new ArrayList<DPDataDto>();
		Set<Integer> types = dPControlDisplayItem.getLstAttendanceItem() == null ? new HashSet<>()
				: dPControlDisplayItem.getLstAttendanceItem().stream().map(x -> x.getTypeGroup()).filter(x -> x != null)
						.collect(Collectors.toSet());
		Map<Integer, Map<String, CodeName>> mapGetName = dataDialogWithTypeProcessor
				.getAllCodeName(new ArrayList<>(types), companyId, dateRange.getEndDate());
		CodeNameType codeNameReason = dataDialogWithTypeProcessor.getReason(companyId);
		Map<String, CodeName> codeNameReasonMap = codeNameReason != null
				? codeNameReason.getCodeNames().stream()
						.collect(Collectors.toMap(x -> mergeString(x.getCode(), "|", x.getId()), x -> x))
				: Collections.emptyMap();
						//get status check box 
		List<GeneralDate> holidayDate = publicHolidayRepository
				.getpHolidayWhileDate(companyId, dateRange.getStartDate(), dateRange.getEndDate()).stream()
				.map(x -> x.getDate()).collect(Collectors.toList());
		Map<Pair<String, GeneralDate>, ConfirmStatusActualResult> mapConfirmResult = confirmResults.stream().collect(Collectors.toMap(x -> Pair.of(x.getEmployeeId(), x.getDate()), x -> x, (x, y) -> x));
		Map<Pair<String, GeneralDate>, ApprovalStatusActualResult> mapApprovalResults = approvalResults.stream().collect(Collectors.toMap(x -> Pair.of(x.getEmployeeId(), x.getDate()), x -> x, (x, y) -> x));

		//cell hide check box approval
		List<DPHideControlCell> lstCellHideControl = new ArrayList<>();
		for (DPDataDto data : screenDto.getLstData()) {
			//filter Date in Period
//			if(!checkDataInClosing(Pair.of(data.getEmployeeId(), data.getDate()), mapClosingEmpResult)) {
//				continue;
//			}
			boolean textColorSpr = false;
			data.setEmploymentCode(screenDto.getEmploymentCode());
			if (!sId.equals(data.getEmployeeId())) {
				screenDto.setCellSate(data.getId(), DPText.LOCK_APPLICATION, DPText.STATE_DISABLE);
			}
			// map name submitted into cell
			if (appMapDateSid.containsKey(data.getEmployeeId() + "|" + data.getDate())) {
				data.addCellData(new DPCellDataDto(DPText.COLUMN_SUBMITTED,
						appMapDateSid.get(data.getEmployeeId() + "|" + data.getDate()), "", ""));
			} else {
				data.addCellData(new DPCellDataDto(DPText.COLUMN_SUBMITTED, "", "", ""));
			}
			data.addCellData(new DPCellDataDto(DPText.COLUMN_SUBMITTED, "", "", ""));
			data.addCellData(new DPCellDataDto(DPText.LOCK_APPLICATION, "", "", ""));
			data.addCellData(new DPCellDataDto(DPText.LOCK_APPLICATION_LIST, "", "", ""));
			
			//set checkbox sign
			ConfirmStatusActualResult dataSign = mapConfirmResult.get(Pair.of(data.getEmployeeId(), data.getDate()));
			data.setSign(dataSign == null ? false : dataSign.isStatus());
			// state check box sign
			boolean disableSignApp = disableSignMap.containsKey(data.getEmployeeId() + "|" + data.getDate()) && disableSignMap.get(data.getEmployeeId() + "|" + data.getDate());
			
			ApprovalStatusActualResult dataApproval = mapApprovalResults.get(Pair.of(data.getEmployeeId(), data.getDate()));
			//set checkbox approval
			data.setApproval(dataApproval == null ? false : mode == ScreenMode.NORMAL.value ? dataApproval.isStatusNormal() : 
				dataApproval.isStatusNormal() && dataApproval.getPermissionCheck().value == 0 && dataApproval.getPermissionRelease().value == 0 ? true : dataApproval.isStatus());
				
			ApproveRootStatusForEmpDto approvalCheckMonth = dpLock.getLockCheckMonth().get(data.getEmployeeId() + "|" + data.getDate());
		//	}
			DailyModifyResult resultOfOneRow = getRow(resultDailyMap, data.getEmployeeId(), data.getDate());
			boolean lockDaykWpl = false, lockDay = false, lockWpl = false, lockHist = false, lockApprovalMonth = false, lockConfirmMonth = false;
			if (resultOfOneRow != null && (displayFormat == 2 ? !data.getError().equals("") : true)) {
				//set disable and lock
				lockDataCheckbox(sId, screenDto, data, identityProcessDtoOpt, approvalUseSettingDtoOpt, mode, data.isApproval(), data.isSign());
				if (showLock == null || showLock) {
					lockDay = checkLockDay(dpLock.getLockDayAndWpl(), data);
					lockWpl = checkLockWork(dpLock.getLockDayAndWpl(), data);
					lockHist = lockHist(dpLock.getLockHist(), data);
					lockApprovalMonth = approvalCheckMonth == null ? false : approvalCheckMonth.isCheckApproval();
					lockConfirmMonth = checkLockConfirmMonth(dpLock.getLockConfirmMonth(), data);
					lockDaykWpl = lockDay || lockWpl;
					lockDaykWpl = lockAndDisable(screenDto, data, mode, lockDaykWpl, dataApproval == null ? false : dataApproval.isStatusNormal(), lockHist,	
							data.isSign(), lockApprovalMonth, lockConfirmMonth);
				} else {
					lockDaykWpl = lockAndDisable(screenDto, data, mode, lockDaykWpl, false, lockHist,
							false, lockApprovalMonth, lockConfirmMonth);
				}
                
				if(displayFormat == 0 && objectShare != null && objectShare.getInitClock() != null && data.getDate().equals(objectShare.getEndDate())){
					// set question SPR 
					screenDto.setShowQuestionSPR(checkSPR(companyId, disItem.getLstAtdItemUnique(), data.getState(), approvalUseSettingDtoOpt.get(), identityProcessDtoOpt.get(), data.isApproval(), data.isSign()).value);
				    if(data.getDate().equals(objectShare.getEndDate())){
				    	//screenDto.set().add(new TextStyle("_"+data.getId(), "date", "italic-text"));
				    	screenDto.setCellSate(data.getId(), "date", DPText.ITALIC_TEXT);
				    	textColorSpr = true;
				    }
				}
				//set cell state day
				if(!textColorSpr){
					setTextColorDay(screenDto, data.getDate(), "date", data.getId(), holidayDate);
				}
				itemValueMap = resultOfOneRow.getItems().stream()
						.collect(Collectors.toMap(x -> mergeString(String.valueOf(x.getItemId()), "|",
								data.getEmployeeId(), "|", data.getDate().toString()), x -> x));
				processCellData(NAME_EMPTY, NAME_NOT_FOUND, screenDto, dPControlDisplayItem, mapGetName, codeNameReasonMap,
						itemValueMap,  data, lockDaykWpl, dailyRecEditSetsMap, objectShare);
				lstData.add(data);
				// DPCellDataDto bPCellDataDto = new DPCellDataDto(columnKey,
				// value,
				// dataType, type);
				Optional<WorkInfoOfDailyPerformanceDto> optWorkInfoOfDailyPerformanceDto = workInfoOfDaily.stream()
						.filter(w -> w.getEmployeeId().equals(data.getEmployeeId())
								&& w.getYmd().equals(data.getDate()))
						.findFirst();
				if (optWorkInfoOfDailyPerformanceDto.isPresent()
						&& optWorkInfoOfDailyPerformanceDto.get().getState() == CalculationState.No_Calculated)
					screenDto.setAlarmCellForFixedColumn(data.getId(), displayFormat);
			}
			
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
		screenDto.setLstHideControl(lstCellHideControl);
		// chech ca hai gia tri spr thay doi
		if (displayFormat == 0 && objectShare != null && objectShare.getInitClock() != null) {
			screenDto
					.setShowQuestionSPR((screenDto.getChangeSPR().isChange31() || screenDto.getChangeSPR().isChange34())
							&& screenDto.getShowQuestionSPR() != SPRCheck.INSERT.value ? SPRCheck.SHOW_CONFIRM.value
									: SPRCheck.INSERT.value);
		}
		screenDto.setLstData(lstData);
		screenDto.getChangeSPR().setPrincipal(screenDto.getShowPrincipal())
				.setSupervisor(screenDto.getShowSupervisor());
		
	}
	
	public void setStateLock(DPDataDto data, String lockMessage){
		if (data.getState().equals(""))
			data.setState("lock|" + lockMessage);
		else
			data.setState(data.getState() + "|" + lockMessage);
	}
	
	public boolean lockAndDisable(DailyPerformanceCorrectionDto screenDto, DPDataDto data, int mode, boolean lockDaykWpl,
			boolean lockApproval, boolean lockHist, boolean lockSign, boolean lockApprovalMonth, boolean lockConfirmMonth) {
		boolean lock = false;
		if (lockDaykWpl || lockApproval || lockHist || lockSign || lockApprovalMonth || lockConfirmMonth) {
			if (lockDaykWpl) {
				//lockCell(screenDto, data, true);
				 lock = true;
			}
			
			if (lockApprovalMonth) {
				setStateLock(data, DPText.LOCK_CHECK_MONTH);
				//lockCell(screenDto, data, true);
				lock = true;
			}
			
			if (lockConfirmMonth) {
				setStateLock(data, DPText.LOCK_CONFIRM_MONTH);
				if(mode != ScreenMode.APPROVAL.value) lock = true;
				
			}
			
			if (lockApproval) {
				setStateLock(data, DPText.LOCK_EDIT_APPROVAL);
				//lockCell(screenDto, data, false);
//				if(mode == ScreenMode.APPROVAL.value) {
//					lock = lock && false;
//				}else {
					lock = true;
//				}
			}

			if (lockSign) {
				setStateLock(data, DPText.LOCK_CHECK_SIGN);
				//lockCell(screenDto, data, false);
				if(mode != ScreenMode.APPROVAL.value)  lock = true;
			}
			
			if (lockHist) {
				setStateLock(data, DPText.LOCK_HIST);
				//lockCell(screenDto, data, true);
				lock = true;
			}
			
			if ((lockConfirmMonth || lockSign) && !(lockApprovalMonth || lockHist || lockDaykWpl || lockApproval))
				lockCell(screenDto, data, false);
			else
				lockCell(screenDto, data, false);
		}
		
		if (mode == ScreenMode.APPROVAL.value) screenDto.setCellSate(data.getId(), DPText.LOCK_SIGN, DPText.STATE_DISABLE);
		return lock;
	}
	
	public void processCellData(String NAME_EMPTY, String NAME_NOT_FOUND, DailyPerformanceCorrectionDto screenDto,
			DPControlDisplayItem dPControlDisplayItem,
			Map<Integer, Map<String, CodeName>> mapGetName,  Map<String, CodeName> mapReasonName, Map<String, ItemValue> itemValueMap, DPDataDto data,
			boolean lock, Map<String, Integer> dailyRecEditSetsMap, ObjectShare share) {
		Set<DPCellDataDto> cellDatas = data.getCellDatas();
		String typeGroup = "";
		Integer cellEdit;
		if (dPControlDisplayItem.getLstAttendanceItem() != null) {
			for (DPAttendanceItem item : dPControlDisplayItem.getLstAttendanceItem()) {
				//DPAttendanceItem dpAttenItem1 = mapDP.get(item.getId());
				String itemIdAsString = item.getId().toString();
				// int a = 1;
				int attendanceAtr = item.getAttendanceAtr();
				String attendanceAtrAsString = String.valueOf(item.getAttendanceAtr());
				Integer groupType = item.getTypeGroup();
				String key = mergeString(itemIdAsString, "|", data.getEmployeeId(), "|" + data.getDate().toString());
				// fix tạm cho Hoa test, sau đó Thanh sẽ điều tra (TQP)
				String value = itemValueMap.get(key) != null && itemValueMap.get(key == "" ? 0 : key).value() != null
						? itemValueMap.get(key).value().toString() : "";
				cellEdit = dailyRecEditSetsMap.get(mergeString(itemIdAsString, "|", data.getEmployeeId(), "|" + converDateToString(data.getDate())));
				
				if (attendanceAtr == DailyAttendanceAtr.Code.value
						|| attendanceAtr == DailyAttendanceAtr.Classification.value) {
					String nameColKey = mergeString(DPText.NAME, itemIdAsString);
					if (attendanceAtr == DailyAttendanceAtr.Code.value) {
						String codeColKey = mergeString(DPText.CODE, itemIdAsString);
						typeGroup = typeGroup
								+ mergeString(String.valueOf(item.getId()), ":", String.valueOf(groupType), "|");
						if (lock) {
							screenDto.setCellSate(data.getId(), codeColKey, DPText.STATE_DISABLE, true);
							screenDto.setCellSate(data.getId(), nameColKey, DPText.STATE_DISABLE, true);
						}
						if (value.isEmpty() || value.equals("null")) {
							cellDatas.add(new DPCellDataDto(mergeString(DPText.CODE, itemIdAsString), "",
									attendanceAtrAsString, DPText.TYPE_LABEL));
							value = NAME_EMPTY;
						} else {
							if (groupType != null) {
								if (groupType == TypeLink.WORKPLACE.value || groupType == TypeLink.POSSITION.value) {
//									Optional<CodeName> optCodeName = dataDialogWithTypeProcessor
//											.getCodeNameWithId(groupType, data.getDate(), value);
									val mapCodeNameAll = mapGetName.get(groupType).get(value);
									cellDatas.add(new DPCellDataDto(codeColKey,
											mapCodeNameAll != null ? mapCodeNameAll.getCode() : value,
											attendanceAtrAsString, DPText.TYPE_LABEL));
									value = mapCodeNameAll == null ? NAME_NOT_FOUND : mapCodeNameAll.getName();
								}else if(groupType == TypeLink.REASON.value){
									int group = DEVIATION_REASON_MAP.get(item.getId());
									cellDatas.add(new DPCellDataDto(codeColKey, value,attendanceAtrAsString, DPText.TYPE_LABEL));
									value = mapReasonName.containsKey(value+"|"+group) ? mapReasonName.get(value+"|"+group).getName() : NAME_NOT_FOUND;
								}
								else {
									cellDatas.add(
											new DPCellDataDto(codeColKey, value, attendanceAtrAsString, DPText.TYPE_LABEL));
									value = mapGetName.get(groupType).containsKey(value)
											? mapGetName.get(groupType).get(value).getName() : NAME_NOT_FOUND;
								}
							}
						}
						cellDatas.add(new DPCellDataDto(nameColKey, value, attendanceAtrAsString, DPText.TYPE_LINK));
						// set color edit
						cellEditColor(screenDto, data.getId(), nameColKey, cellEdit);
						cellEditColor(screenDto, data.getId(), codeColKey, cellEdit);
					} else {
						String noColKey = mergeString(DPText.NO, itemIdAsString);
						if (lock) {
							screenDto.setCellSate(data.getId(), noColKey, DPText.STATE_DISABLE, true);
							screenDto.setCellSate(data.getId(), nameColKey, DPText.STATE_DISABLE, true);
						}
						cellDatas.add(new DPCellDataDto(noColKey, value.isEmpty() ? 0 : Integer.parseInt(value), attendanceAtrAsString, DPText.TYPE_LABEL));
						cellDatas.add(new DPCellDataDto(nameColKey, value.isEmpty() ? 0 : Integer.parseInt(value), attendanceAtrAsString, DPText.TYPE_LINK));
						cellEditColor(screenDto, data.getId(), nameColKey, cellEdit);
						cellEditColor(screenDto, data.getId(), noColKey, cellEdit);
					}

				} else {
					String anyChar = mergeString(DPText.ADD_CHARACTER, itemIdAsString);
					// set color edit
					cellEditColor(screenDto, data.getId(), anyChar, cellEdit);
					if (lock) {
						screenDto.setCellSate(data.getId(), anyChar, DPText.STATE_DISABLE, true);
					}
					if (attendanceAtr == DailyAttendanceAtr.Time.value
							|| attendanceAtr == DailyAttendanceAtr.TimeOfDay.value) {
						//set SPR
						if(share != null && share.getInitClock() != null && share.getDisplayFormat() == 0 && data.getDate().equals(share.getEndDate())){
							boolean change31 = false;
							boolean change34 = false;
							if (!lock) {
								if (screenDto.getShowQuestionSPR() != SPRCheck.NOT_INSERT.value) {
									if (item.getId() == 31
											&& data.getEmployeeId().equals(share.getInitClock().getEmployeeId())
											&& data.getDate().equals(share.getInitClock().getDateSpr())) {
										// value = share.getInitClock().getGoOut() != null ?
										// share.getInitClock().getGoOut() : "";
										if (!share.getInitClock().getGoOut().equals("")) {
											// if (value.equals("") || (Integer.parseInt(value) !=
											// Integer.parseInt(share.getInitClock().getGoOut())))
											change31 = true;
										}
										ChangeSPR changeSPR31 = processSPR(data.getEmployeeId(), data.getDate(), share,
												change31, false);
										changeSPR31.setChange34(screenDto.getChangeSPR().isChange34());
										screenDto.setChangeSPR(changeSPR31.setRow31(data.getId()));
									} else if (item.getId() == 34
											&& data.getEmployeeId().equals(share.getInitClock().getEmployeeId())
											&& data.getDate().equals(share.getInitClock().getDateSpr())) {
										if (!share.getInitClock().getLiveTime().equals("")) {
											// if (value.equals("") || (Integer.parseInt(value) !=
											// Integer.parseInt(share.getInitClock().getLiveTime())))
											change34 = true;
										}
										ChangeSPR changeSPR34 = processSPR(data.getEmployeeId(), data.getDate(), share,
												false, change34);
										changeSPR34.setChange31(screenDto.getChangeSPR().isChange31());
										screenDto.setChangeSPR(changeSPR34.setRow34(data.getId()));
									}
									// insertStampSourceInfo(data.getEmployeeId(), data.getDate(), att, leav);
									screenDto.getChangeSPR().setShowSupervisor(data.isApproval());
								}
							} else {
								ChangeSPR changeSpr = new ChangeSPR(change31, change34);
								changeSpr.setMessageIdError("Msg_1531");
								screenDto.setChangeSPR(changeSpr);
							}
						}
						if (!value.isEmpty()) {
							// convert HH:mm
							int minute =0 ;
							if(Integer.parseInt(value.equals("0.0") ? "0" : value) >= 0){
								minute = Integer.parseInt(value.equals("0.0") ? "0" : value);
							}else{
								if (attendanceAtr == DailyAttendanceAtr.TimeOfDay.value) {
									minute = 0 - ((Integer.parseInt(value)+ (1 + -Integer.parseInt(value) / DPText.MINUTES_OF_DAY) * DPText.MINUTES_OF_DAY));
								} else {
									minute = Integer.parseInt(value);
								}
							}
							int hours = minute / 60;
							int minutes = Math.abs(minute) % 60;
							String valueConvert = (minute < 0 && hours == 0) ?  "-"+String.format(DPText.FORMAT_HH_MM, hours, minutes) : String.format(DPText.FORMAT_HH_MM, hours, minutes);
							cellDatas.add(new DPCellDataDto(anyChar, valueConvert,
									attendanceAtrAsString, DPText.TYPE_LABEL));
						} else {
							cellDatas.add(new DPCellDataDto(anyChar, value, attendanceAtrAsString, DPText.TYPE_LABEL));
						}
					} else if(attendanceAtr == DailyAttendanceAtr.AmountOfMoney.value){
						cellDatas.add(new DPCellDataDto(anyChar, value.equals("0.0") ? "0" : value, attendanceAtrAsString, DPText.TYPE_LABEL));
					} else {
						cellDatas.add(new DPCellDataDto(anyChar, value, attendanceAtrAsString, DPText.TYPE_LABEL));
					}
				}
			}
		}
		data.setTypeGroup(typeGroup);
		data.setCellDatas(cellDatas);
	}

	public DailyModifyResult getRow(Map<String, DailyModifyResult> resultDailyMap, String sId, GeneralDate date) {
		return resultDailyMap.isEmpty() ? null : resultDailyMap.get(mergeString(sId, "|", date.toString()));
	}

	public void lockCell(DailyPerformanceCorrectionDto screenDto, DPDataDto data, boolean lockSign) {
		//screenDto.setCellSate(data.getId(), LOCK_DATE, DPText.STATE_DISABLE);
		//screenDto.setCellSate(data.getId(), LOCK_EMP_CODE, DPText.STATE_DISABLE);
		//screenDto.setCellSate(data.getId(), LOCK_EMP_NAME, DPText.STATE_DISABLE);
		//screenDto.setCellSate(data.getId(), LOCK_ERROR, DPText.STATE_DISABLE);
		if(lockSign) screenDto.setCellSate(data.getId(), DPText.LOCK_SIGN, DPText.STATE_DISABLE);
		//screenDto.setCellSate(data.getId(), LOCK_PIC, DPText.STATE_DISABLE);
//		screenDto.setCellSate(data.getId(), DPText.LOCK_APPLICATION, DPText.STATE_DISABLE);
//		screenDto.setCellSate(data.getId(), DPText.COLUMN_SUBMITTED, DPText.STATE_DISABLE);
//		screenDto.setCellSate(data.getId(), DPText.LOCK_APPLICATION_LIST, DPText.STATE_DISABLE);
	}
    
	public void cellEditColor(DailyPerformanceCorrectionDto screenDto, String rowId, String columnKey, Integer cellEdit ){
		// set color edit
		if(cellEdit != null){
			if(cellEdit == 0){
				screenDto.setCellSate(rowId, columnKey, DPText.HAND_CORRECTION_MYSELF);
			}else if(cellEdit == 1){
				screenDto.setCellSate(rowId, columnKey, DPText.HAND_CORRECTION_OTHER);
			}else{
				screenDto.setCellSate(rowId, columnKey, DPText.REFLECT_APPLICATION);
			}
		}
	}
	
	public boolean checkLockDay(Map<String, DatePeriod> employeeAndDateRange, DPDataDto data) {
		boolean lock = false;
		if (!employeeAndDateRange.isEmpty()) {
			for (int i = 1; i <= 5; i++) {
				String idxAsString = String.valueOf(i);
				DatePeriod dateD = employeeAndDateRange
						.get(mergeString(data.getEmployeeId(), "|", idxAsString, "|", DPText.LOCK_EDIT_CELL_DAY));
				String lockD = "";
				if (dateD != null && inRange(data, dateD)) {
					lockD = mergeString("|", DPText.LOCK_EDIT_CELL_DAY);
				}
				
				if (!lockD.isEmpty()) {
					data.setState(mergeString("lock", lockD));
					lock = true;
				}
			}
		}
		return lock;
	}
	
	public boolean checkLockWork(Map<String, DatePeriod> employeeAndDateRange, DPDataDto data) {
		boolean lock = false;
		if (!employeeAndDateRange.isEmpty()) {
			for (int i = 1; i <= 5; i++) {
				String idxAsString = String.valueOf(i);
				DatePeriod dateC = employeeAndDateRange.get(mergeString(data.getEmployeeId(), "|", idxAsString, "|",
						data.getWorkplaceId(), "|", DPText.LOCK_EDIT_CELL_WORK));
				String lockC = "";
				if (dateC != null && inRange(data, dateC)) {
					lockC = mergeString("|", DPText.LOCK_EDIT_CELL_WORK);
				}
				if (!lockC.isEmpty()) {
					data.setState(mergeString("lock", lockC));
					lock = true;
				}
			}
		}
		return lock;
	}
	
	public boolean lockHist(Pair<List<ClosureDto>, Map<Integer, DatePeriod>> empHist, DPDataDto data) {
		Integer closureId = empHist.getLeft().stream()
				.filter(x -> x.getSid().equals(data.getEmployeeId()) && inRange(data, x.getDatePeriod()))
				.map(x -> x.getClosureId()).findFirst().orElse(null);
		   if(closureId == null) return false;
		   val datePeriod = empHist.getRight().get(closureId);
		   if(datePeriod != null && data.getDate().after(datePeriod.end())) return false;
           if(datePeriod != null && (data.getDate().afterOrEquals(datePeriod.start()) && data.getDate().beforeOrEquals(datePeriod.end()))) return false;
           return true;
	}
	 
	public boolean checkLockConfirmMonth(Pair<List<ClosureSidDto>, List<ConfirmationMonthDto>> pairClosureMonth, DPDataDto data){
		if (pairClosureMonth == null)
			return false;

		List<ClosureSidDto> lstClosure = pairClosureMonth.getLeft();

		Optional<ClosureSidDto> closureSidDtoOpt = lstClosure.stream()
				.filter(x -> x.getSid().equals(data.getEmployeeId())).findFirst();

		if (!closureSidDtoOpt.isPresent())
			return false;

		Optional<ClosurePeriod> cPeriod = closureSidDtoOpt.get().getClosure().getClosurePeriodByYmd(data.getDate());

		if (!cPeriod.isPresent())
			return false;

		Optional<ConfirmationMonthDto> monthOpt = pairClosureMonth.getRight().stream()
				.filter(x -> x.getEmployeeId().equals(data.getEmployeeId())
						  && x.getClosureId() == cPeriod.get().getClosureId().value
						  && x.getProcessYM() == cPeriod.get().getYearMonth().v().intValue()
						  && x.getClosureDay() == cPeriod.get().getPeriod().end().day()).findFirst();
		return monthOpt.isPresent();
	}
	
    public Map<String,  String> getApplication(List<String> listEmployeeId, DateRange dateRange, Map<String, Boolean> disableSignMap){
		// No 20 get submitted application
		Map<String, String> appMapDateSid = new HashMap<>();
		//requestlist26
		List<ApplicationExportDto> appplicationDisable = listEmployeeId.isEmpty() ? Collections.emptyList()
				: applicationListFinder.getApplicationBySID(listEmployeeId, dateRange.getStartDate(),
						dateRange.getEndDate());
		//requestlist542
		List<AppGroupExportDto> appplicationName = listEmployeeId.isEmpty() ? Collections.emptyList()
				: applicationListFinder.getApplicationGroupBySID(listEmployeeId, dateRange.getStartDate(),
						dateRange.getEndDate());
		appplicationName.forEach(x -> {
			String key = x.getEmployeeID() + "|" + x.getAppDate();
			if (appMapDateSid.containsKey(key)) {
				appMapDateSid.put(key, appMapDateSid.get(key) + "  " + x.getAppTypeName());
			} else {
				appMapDateSid.put(key, x.getAppTypeName());
			}
		});
		
		appplicationDisable.forEach(x -> {
			String key = x.getEmployeeID() + "|" + x.getAppDate();
			if (disableSignMap != null) {
				boolean disable = (x.getReflectState() == ReflectedState_New.NOTREFLECTED.value
						|| x.getReflectState() == ReflectedState_New.REMAND.value)
						&& x.getAppType() != nts.uk.ctx.at.request.dom.application.ApplicationType.OVER_TIME_APPLICATION.value;
				if (disableSignMap.containsKey(key)) {
					disableSignMap.put(key, disableSignMap.get(key) || disable);
				} else {
					disableSignMap.put(key, disable);
				}
			}
		});
		return appMapDateSid;
    }
    
	public boolean inRange(DPDataDto data, DatePeriod dateM) {
		return data.getDate().afterOrEquals(dateM.start()) && data.getDate().beforeOrEquals(dateM.end());
	}

	public void lockDataCheckbox(String sId, DailyPerformanceCorrectionDto screenDto, 
			DPDataDto data, Optional<IdentityProcessUseSetDto> identityProcessUseSetDto, Optional<ApprovalUseSettingDto> approvalUseSettingDto,
			int mode, boolean checkApproval, boolean sign) {
		// disable, enable check sign no 10
		if (!sId.equals(data.getEmployeeId())) {
			screenDto.setCellSate(data.getId(), DPText.LOCK_SIGN, DPText.STATE_DISABLE);
			// screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL,
			// DPText.STATE_DISABLE);
		} else {
			if (identityProcessUseSetDto.isPresent()) {
				int selfConfirmError = identityProcessUseSetDto.get().getYourSelfConfirmError();
				// lock sign
				if (selfConfirmError == YourselfConfirmError.CANNOT_CHECKED_WHEN_ERROR.value) {
					if (data.getError().contains("ER") && data.isSign()) {
						screenDto.setCellSate(data.getId(), DPText.LOCK_SIGN, DPText.STATE_ERROR);
					} else if (data.getError().contains("ER") && !data.isSign()) {
						screenDto.setCellSate(data.getId(), DPText.LOCK_SIGN, DPText.STATE_DISABLE);
					}
					// thieu check khi co data
				} else if (selfConfirmError == YourselfConfirmError.CANNOT_REGISTER_WHEN_ERROR.value) {
					// co the dang ky data nhưng ko đăng ký được check box
				}
			}
		}

		if (approvalUseSettingDto.isPresent()) {
			// lock approval
			if(mode == ScreenMode.NORMAL.value){
				screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_DISABLE);
				return;
			}
			int supervisorConfirmError = approvalUseSettingDto.get().getSupervisorConfirmErrorAtr();
			if (supervisorConfirmError == YourselfConfirmError.CANNOT_CHECKED_WHEN_ERROR.value) {
				if (data.getError().contains("ER") && data.isApproval()) {
					screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_ERROR);
				} else if (data.getError().contains("ER") && !data.isApproval()) {
					screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_DISABLE);
				}
				// thieu check khi co data
			} else if (supervisorConfirmError == YourselfConfirmError.CANNOT_REGISTER_WHEN_ERROR.value) {
				// co the dang ky data nhưng ko đăng ký được check box
			}

			// disable, enable checkbox with approveRootStatus
//			if (approveRootStatus == null)
//				return;
//			if (approveRootStatus.getApproverEmployeeState() != null
//					&& !checkApproval) {
//				if(approveRootStatus.getApproverEmployeeState() != ApproverEmployeeState.PHASE_DURING) {
//					screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_DISABLE);
//				}else if(identityProcessUseSetDto.isPresent() && identityProcessUseSetDto.get().isUseConfirmByYourself() && !sign) {
//					screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_DISABLE);
//				}
//			}else if(approveRootStatus.getApprovalStatus() != null
//					&& approveRootStatus.getApprovalStatus().value == ReleasedProprietyDivision.NOT_RELEASE.value && checkApproval){
//				screenDto.setCellSate(data.getId(), DPText.LOCK_APPROVAL, DPText.STATE_DISABLE);
//			}
		}
	}

	public DisplayItem getDisplayItems(CorrectionOfDailyPerformance correct, List<String> formatCodes,
			String companyId, DailyPerformanceCorrectionDto screenDto, List<String> listEmployeeId,
			boolean showButton, OperationOfDailyPerformanceDto dailyPerformanceDto) {
		screenDto.setComment(dailyPerformanceDto != null ? dailyPerformanceDto.getComment() : null);
		screenDto.setTypeBussiness(dailyPerformanceDto != null ? dailyPerformanceDto.getSettingUnit().value : 1);
		DisplayItem disItem = this.getItemIds(listEmployeeId, screenDto.getDateRange(), correct, formatCodes,
				dailyPerformanceDto, companyId, showButton, screenDto);
		return disItem;
	}

	public Map<String, DatePeriod> extractEmpAndRange(DateRange dateRange, String companyId,
			Map<String, String> employmentWithSidMap) {
		Map<String, DatePeriod> employeeAndDateRange = new HashMap<>();
		List<ClosureDto> closureDtos = repo.getClosureId(employmentWithSidMap, dateRange.getEndDate());
		if (!closureDtos.isEmpty()) {
			closureDtos.forEach(x -> {
				DatePeriod datePeriod = ClosureService.getClosurePeriod(
						requireService.createRequire(),
						x.getClosureId(),
						new YearMonth(x.getClosureMonth()));
				Optional<ActualLockDto> actualLockDto = repo.findAutualLockById(companyId, x.getClosureId());
				if (actualLockDto.isPresent()) {
					if (actualLockDto.get().getDailyLockState() == 1 || actualLockDto.get().getMonthlyLockState() == 1) {
						employeeAndDateRange.put(
								mergeString(x.getSid(), "|", x.getClosureId().toString(), "|", DPText.LOCK_EDIT_CELL_DAY),
								datePeriod);
					}

//					if (actualLockDto.get().getMonthlyLockState() == 1) {
//						employeeAndDateRange.put(
//								mergeString(x.getSid(), "|", x.getClosureId().toString(), "|", DPText.LOCK_EDIT_CELL_MONTH),
//								datePeriod);
//					}
				}
				// アルゴリズム「表示項目を制御する」を実行する | Execute "control display items"
				List<WorkFixedDto> workFixeds = repo.findWorkFixed(x.getClosureId(), x.getClosureMonth());
				for (WorkFixedDto workFixedOp : workFixeds) {
					employeeAndDateRange.put(mergeString(x.getSid(), "|", x.getClosureId().toString(),
							"|" + workFixedOp.getWkpId(), "|", DPText.LOCK_EDIT_CELL_WORK), datePeriod);
				}
			});
		}
		return employeeAndDateRange;
	}

	public String mergeString(String... x) {
		return StringUtils.join(x);
	}

	public void setHideCheckbox(DailyPerformanceCorrectionDto screenDto, Optional<IdentityProcessUseSetDto> indentity, Optional<ApprovalUseSettingDto> approval, String companyId, int mode) {
			screenDto.setShowPrincipal(indentity.isPresent() && indentity.get().isUseConfirmByYourself());
			screenDto.setShowSupervisor(approval.isPresent() && approval.get().getUseDayApproverConfirm());
	}

	public List<DPErrorDto> getErrorList(DailyPerformanceCorrectionDto screenDto, List<String> listEmployeeId) {
		List<DPErrorDto> lstError = new ArrayList<>();
		if (screenDto.getLstEmployee().size() > 0) {
			/// ドメインモデル「社員の日別実績エラー一覧」をすべて取得する +
			/// 対応するドメインモデル「勤務実績のエラーアラーム」をすべて取得する
			/// Acquire all domain model "employee's daily performance error
			/// list" + "work error error alarm" | lay loi thanh tich trong
			/// khoang thoi gian
			return listEmployeeId.isEmpty() ? new ArrayList<>()
					: repo.getListDPError(screenDto.getDateRange(), listEmployeeId).stream().distinct().collect(Collectors.toList());
		} else {
			return lstError;
		}
	}

	public List<DailyPerformanceEmployeeDto> extractEmployeeData(Integer initScreen, String sId,
			List<DailyPerformanceEmployeeDto> emps, ObjectShare share) {
		if (initScreen == 0 && share == null) {
			return emps.stream().filter(x -> x.getId().equals(sId)).collect(Collectors.toList());
		}else if (initScreen == 0 && share.getDisplayFormat() == 0){
			List<DailyPerformanceEmployeeDto> datas = new ArrayList<>();
			if(!emps.isEmpty()) return emps.stream().filter(x-> x.getId().equals(share.getIndividualTarget())).collect(Collectors.toList());
			return datas;
		}
		return emps;

	}

	public List<DailyPerformanceEmployeeDto> extractEmployeeList(List<DailyPerformanceEmployeeDto> lstEmployee,
			String sId, DateRange range) {
		if (!lstEmployee.isEmpty()) {
			return lstEmployee;
		} else {
			return getListEmployee(sId, range);
		}
	}
    
	public List<DailyPerformanceEmployeeDto> converEmployeeList(List<EmployeeInfoFunAdapterDto> employees) {
		if (employees.isEmpty()) {
			return Collections.emptyList();
		} else {
			return employees.stream().map(x -> new DailyPerformanceEmployeeDto(x.getEmployeeId(), x.getEmployeeCode(),
					x.getBusinessName(), "", "", "", false)).collect(Collectors.toList());
		}
	}
	
	public List<DPDataDto> setWorkPlace(Map<String, WorkPlaceHistTemp> wPHMap, Map<String, List<AffComHistItemAtScreen>> affCompanyMap, List<DPDataDto> employees){
		//Map<String, List<AffComHistItemImport>> affCompanyMap = affCompany.stream().collect(Collectors.toMap(x -> x.getEmployeeId(), x -> x.getLstAffComHistItem(), (x, y) -> x));
		return employees.stream().map(x -> {
			x.setWorkplaceId(wPHMap.containsKey(x.getEmployeeId()) ?  wPHMap.get(x.getEmployeeId()).getWorkplaceId(): "");
			//x.setDatePriod(affCompanyMap.containsKey(x.getId()) ?  getDateRetire(affCompanyMap.get(x.getId()), x.getDate()): new DatePeriod(GeneralDate.today(), GeneralDate.today()));
			return x;
		}).filter(x -> affCompanyMap.containsKey(x.getEmployeeId()) &&  getDateRetire(affCompanyMap.get(x.getEmployeeId()), x.getDate())).collect(Collectors.toList());
		//.filter(x -> affCompanyMap.containsKey(x.getId()) &&  getDateRetire(affCompanyMap.get(x.getId()), x.getDate()));
	}
	
	public boolean getDateRetire(List<AffComHistItemAtScreen> dateHist, GeneralDate date){
		List<AffComHistItemAtScreen> data = dateHist.stream().filter(x -> x.getDatePeriod().end().afterOrEquals(date) && x.getDatePeriod().start().beforeOrEquals(date)).collect(Collectors.toList());
		if(data.isEmpty()) return false;
		else return true;
	}
	
	public String getEmploymentCode(String companyId, GeneralDate date, String sId) {
		AffEmploymentHistoryDto employment = repo.getAffEmploymentHistory(companyId, sId, date);
		return employment == null ? "" : employment.getEmploymentCode();
	}

	public List<DailyPerformanceAuthorityDto> getAuthority(DailyPerformanceCorrectionDto screenDto) {
		String roleId = AppContexts.user().roles().forAttendance();
		if (roleId != null) {
			List<DailyPerformanceAuthorityDto> dailyPerformans = repo.findDailyAuthority(roleId);
			if (!dailyPerformans.isEmpty()) {
				return dailyPerformans;
			}
		}
		throw new BusinessException("Msg_671");
	}

	// アルゴリズム「乖離理由を取得する」
	private Map<Integer, Boolean> getReasonDiscrepancy(String companyId, List<Integer> lstAtdItemUnique) {
		List<Integer> possiton = Arrays.asList(DEVIATION_REASON);
		List<Integer> divergenceNo = lstAtdItemUnique.stream().filter(x -> DEVIATION_REASON_MAP.containsKey(x))
				.map(x -> DEVIATION_REASON_MAP.get(x)).collect(Collectors.toSet()).stream().collect(Collectors.toList());
		// 日次勤怠項目一覧の勤怠項目に対応する乖離理由を取得する
		Map<Integer, Boolean> shows = new HashMap<>();
		if(divergenceNo.isEmpty()) return Collections.emptyMap();; 
		List<DivergenceTimeDto> divergenceTimeDtos = repo.findDivergenceTime(companyId, divergenceNo);
		if (divergenceTimeDtos.isEmpty())
			return Collections.emptyMap();
		Map<Integer, List<Integer>> groupMap = new HashMap<>();
		for (int i = 0; i < DEVIATION_REASON.length; i++) {
			if (groupMap.containsKey(i/3 + 1)) {
				val group = groupMap.get(i/3 + 1);
				group.add(DEVIATION_REASON[i]);
				groupMap.put(i/3 + 1, group);
			} else {
				val group = new ArrayList<Integer>();
				group.add(DEVIATION_REASON[i]);
				groupMap.put(i/3 + 1, group);
			}
		}
		divergenceTimeDtos.forEach(x -> {
			List<Integer> groupItem = groupMap.get(x.getDivergenceTimeNo());
			if (x.getDivTimeUseSet() == DivergenceTimeUseSet.NOT_USE.value)
				groupItem.forEach(y ->{
					shows.put(y, false);
				});
			else {
				groupItem.forEach(y -> {
					shows.put(y, false);
					if (possiton.indexOf(y) % 3 == 0) {
						shows.put(y, true);
					}
					if (x.getDvgcReasonInputed() && possiton.indexOf(y) % 3 == 2) {
						shows.put(y, true);
					}
					if (x.getDvgcReasonSelected() && possiton.indexOf(y) % 3 == 1) {
						shows.put(y, true);
					}
				});
			}
		});
		return shows;
	}

	public List<ErrorReferenceDto> getListErrorRefer(DateRange dateRange,
			List<DailyPerformanceEmployeeDto> lstEmployee, Integer closureId) {
		List<ErrorReferenceDto> lstErrorRefer = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		List<DPErrorDto> lstError = this.repo.getListDPError(dateRange,
				lstEmployee.stream().map(e -> e.getId()).collect(Collectors.toList())).stream().distinct().collect(Collectors.toList());
		Map<String, String> appMapDateSid = getApplication(new ArrayList<>(lstEmployee.stream().map(x -> x.getId()).collect(Collectors.toSet())), dateRange, null);
		int rowId = 0;
		if (lstError.size() > 0) {
			// 対応するドメインモデル「勤務実績のエラーアラーム」をすべて取得する
			// Get list error setting
			List<DPErrorSettingDto> lstErrorSetting = this.repo
					.getErrorSetting(companyId, lstError.stream().map(e -> e.getErrorCode()).collect(Collectors.toList()), true, true, false);
			// convert to list error reference
			//IntStream.range(0, lstError.size()).forEach(id -> {
			for (int id = 0; id < lstError.size(); id++) {
				for (DPErrorSettingDto errorSetting : lstErrorSetting) {
					if (lstError.get(id).getErrorCode().equals(errorSetting.getErrorAlarmCode())) {
						DPErrorDto value = lstError.get(id);
						if (lstError.get(id).getAttendanceItemId().size() > 0) {
							for (int x = 0; x < lstError.get(id).getAttendanceItemId().size(); x++) {
								// lstError.get(id).getAttendanceItemId().forEach(x
								// -> {
								lstErrorRefer.add(new ErrorReferenceDto(String.valueOf(rowId), value.getEmployeeId(),
										"", "", value.getProcessingDate(), value.getErrorCode(),
										value.getErrorAlarmMessage() == null
												? (errorSetting.getMessageDisplay() == null ? ""
														: errorSetting.getMessageDisplay())
												: value.getErrorAlarmMessage(),
										lstError.get(id).getAttendanceItemId().get(x), "", errorSetting.isBoldAtr(),
										errorSetting.getMessageColor(),
										appMapDateSid
												.containsKey(value.getEmployeeId() + "|" + value.getProcessingDate())
														? appMapDateSid.get(
																value.getEmployeeId() + "|" + value.getProcessingDate())
														: "", false));
								rowId++;
							}
						} else {
							lstErrorRefer.add(new ErrorReferenceDto(String.valueOf(rowId), value.getEmployeeId(),
									value.getProcessingDate(), value.getErrorCode(),
									value.getErrorAlarmMessage() == null ? (errorSetting.getMessageDisplay() == null ? "" : errorSetting.getMessageDisplay()) : value.getErrorAlarmMessage(),
									errorSetting.isBoldAtr(),
									errorSetting.getMessageColor(),
									appMapDateSid
											.containsKey(value.getEmployeeId() + "|" + value.getProcessingDate())
													? appMapDateSid.get(
															value.getEmployeeId() + "|" + value.getProcessingDate())
													: ""));
							rowId++;
						}
					}
				}
			}
		}
			// get list item to add item name
			Set<Integer> itemIds = lstError.stream().flatMap(x -> x.getAttendanceItemId().stream()).collect(Collectors.toSet());
			
			Map<Integer, String> lstAttendanceItem = dailyAttendanceItemNameAdapter.getDailyAttendanceItemName(new ArrayList<>(itemIds))
					.stream().collect(Collectors.toMap(DailyAttendanceItemNameAdapterDto::getAttendanceItemId,
							x -> x.getAttendanceItemName())); // 9s
			
			List<DPItemValue> dpItems = errorMonthProcessor.getErrorMonth(lstEmployee.stream().map(x -> x.getId()).collect(Collectors.toSet()), dateRange);
			for(DPItemValue value : dpItems) {
				lstErrorRefer.add(new ErrorReferenceDto(String.valueOf(rowId), value.getEmployeeId(),
						value.getDate(), "", value.getMessage(), true));
				rowId++;
			}
			
			// add employee code & name
			for (ErrorReferenceDto errorRefer : lstErrorRefer) {
				String name = errorRefer == null ? null : lstAttendanceItem.get(errorRefer.getItemId());
				errorRefer.setItemName(name == null ? "" : name);
				for (DailyPerformanceEmployeeDto employee : lstEmployee) {
					if (errorRefer.getEmployeeId().equals(employee.getId())) {
						errorRefer.setEmployeeCode(employee.getCode());
						errorRefer.setEmployeeName(employee.getBusinessName());
					}
				}
			}
			
//	   Map<String, List<EmploymentHisOfEmployeeImport>> mapClosingEmpResult = checkClosingEmployee.checkClosingEmployee(companyId, lstEmployee.stream().map(x -> x.getId()).distinct().collect(Collectors.toList()), 
//					new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), closureId);
//			
//		lstErrorRefer = lstErrorRefer.stream().filter(x -> {
//				if(x.getItemMonth().booleanValue()) {
//					return mapClosingEmpResult.containsKey(x.getEmployeeId());
//				}else {
//					return checkDataInClosing(Pair.of(x.getEmployeeId(), x.getDate()), mapClosingEmpResult);
//				}
//				
//			}).collect(Collectors.toList());
			
			
		return lstErrorRefer;
	}

	/**
	 * アルゴリズム「表示項目を制御する」を実行する | Execute the algorithm "control display items"
	 */
	public DisplayItem getItemIds(List<String> lstEmployeeId, DateRange dateRange,
			CorrectionOfDailyPerformance correct, List<String> formatCodeSelects,
			OperationOfDailyPerformanceDto dailyPerformanceDto, String companyId, boolean showButton,  DailyPerformanceCorrectionDto screenDto) {
		DisplayItem result = new DisplayItem();
		if (lstEmployeeId.size() > 0) {
			// 取得したドメインモデル「日別実績の運用」をチェックする | Check the acquired domain model
			// "Operation of daily performance"
			List<FormatDPCorrectionDto> lstFormat = new ArrayList<FormatDPCorrectionDto>();
			List<DPSheetDto> lstSheet = new ArrayList<DPSheetDto>();
			List<Integer> lstAtdItem = new ArrayList<>();
			List<Integer> lstAtdItemUnique = new ArrayList<>();
			List<DPBusinessTypeControl> lstDPBusinessTypeControl = new ArrayList<>();
			if (dailyPerformanceDto != null && dailyPerformanceDto.getSettingUnit() == SettingUnitType.AUTHORITY) {
				// setting button A2_4
				result.setSettingUnit(true);
				List<AuthorityFomatDailyDto> authorityFomatDailys = new ArrayList<>();
				List<AuthorityFormatSheetDto> authorityFormatSheets = new ArrayList<>();
				// アルゴリズム「社員の権限に対応する表示項目を取得する」を実行する
				// kiem tra thong tin rieng biet user
				if (correct == null) {
					if (formatCodeSelects == null || formatCodeSelects.isEmpty()) {
						List<AuthorityFormatInitialDisplayDto> initialDisplayDtos = repo
								.findAuthorityFormatInitialDisplay(companyId);
						if (!initialDisplayDtos.isEmpty()) {
							List<String> formatCodes = initialDisplayDtos.stream()
									.map(x -> x.getDailyPerformanceFormatCode()).collect(Collectors.toList());
							result.setFormatCode(formatCodes.stream().collect(Collectors.toSet()));
							result.setAutBussCode(result.getFormatCode());
							// Lấy về domain model "会社の日別実績の修正のフォーマット" tương ứng
							authorityFomatDailys = repo.findAuthorityFomatDaily(companyId, formatCodes);
							if(authorityFomatDailys.isEmpty()) throw new BusinessException("Msg_1402");
							List<BigDecimal> sheetNos = authorityFomatDailys.stream().map(x -> x.getSheetNo())
									.collect(Collectors.toList());
							authorityFormatSheets = sheetNos.isEmpty() ? Collections.emptyList()
									: repo.findAuthorityFormatSheet(companyId, formatCodes, sheetNos);
						} else {
							// アルゴリズム「表示項目の選択を起動する」を実行する
							/// 画面「表示フォーマットの選択」をモーダルで起動する(Chạy màn hình "Select
							// display format" theo cách thức) -- chay man hinh
							// C
							throw new BusinessException(DPText.SCREEN_KDW003);
						}
					} else {
						
						authorityFomatDailys = repo.findAuthorityFomatDaily(companyId, formatCodeSelects);
						if(CollectionUtil.isEmpty(authorityFomatDailys)) {
							List<AuthorityFormatInitialDisplayDto> initialDisplayDtos = repo
									.findAuthorityFormatInitialDisplay(companyId);
							List<String> formatCodes = initialDisplayDtos.stream()
									.map(x -> x.getDailyPerformanceFormatCode()).collect(Collectors.toList());
							if (!CollectionUtil.isEmpty(formatCodes)) {
								authorityFomatDailys = repo.findAuthorityFomatDaily(companyId, formatCodes);
								result.setFormatCode(formatCodes.stream().collect(Collectors.toSet()));
								result.setAutBussCode(result.getFormatCode());
								formatCodeSelects = formatCodes;
							}
						}
						
						result.setFormatCode(formatCodeSelects.stream().collect(Collectors.toSet()));
						result.setAutBussCode(result.getFormatCode());
						List<BigDecimal> sheetNos = authorityFomatDailys.stream().map(x -> x.getSheetNo())
								.collect(Collectors.toList());
						authorityFormatSheets = sheetNos.isEmpty() ? Collections.emptyList()
								: repo.findAuthorityFormatSheet(companyId, formatCodeSelects, sheetNos);
					}
				} else {
					// Lấy về domain model "会社の日別実績の修正のフォーマット" tương ứng
					List<String> formatCodes = Arrays.asList(correct.getPreviousDisplayItem());
					result.setFormatCode(formatCodes.stream().collect(Collectors.toSet()));
					authorityFomatDailys = repo.findAuthorityFomatDaily(companyId, formatCodes);
					List<BigDecimal> sheetNos = authorityFomatDailys.stream().map(x -> x.getSheetNo())
							.collect(Collectors.toList());
					authorityFormatSheets = sheetNos.isEmpty() ? Collections.emptyList()
							: repo.findAuthorityFormatSheet(companyId, formatCodes, sheetNos);
				}
				if (!authorityFomatDailys.isEmpty()) {
					// set FormatCode for button A2_4
					lstSheet = authorityFormatSheets.stream()
							.map(x -> new DPSheetDto(x.getSheetNo().toString(), x.getSheetName().toString()))
							.collect(Collectors.toList());
					Set<String> lstSheetNo = lstSheet.stream().map(DPSheetDto::getName).collect(Collectors.toSet());
					if (lstSheetNo.size() != lstSheet.size()) {
						lstSheet = lstSheet.stream().map(x -> new DPSheetDto(x.getName(), x.getName()))
								.collect(Collectors.toList());
					}
					lstFormat = authorityFomatDailys.stream()
							.map(x -> new FormatDPCorrectionDto(companyId, x.getDailyPerformanceFormatCode(),
									x.getAttendanceItemId(), x.getSheetNo().toString(), x.getDisplayOrder(),
									x.getColumnWidth().intValue()))
							.collect(Collectors.toList());
					lstAtdItem = lstFormat.stream().map(f -> f.getAttendanceItemId()).collect(Collectors.toList());
					lstAtdItemUnique = new HashSet<Integer>(lstAtdItem).stream().collect(Collectors.toList());

				}
			} else {
				// setting button A2_4
				result.setSettingUnit(false);
				// アルゴリズム「社員の勤務種別に対応する表示項目を取得する」を実行する
				/// アルゴリズム「社員の勤務種別をすべて取得する」を実行する
				List<String> lstBusinessTypeCode = this.repo.getListBusinessType(lstEmployeeId, dateRange);
				if (lstBusinessTypeCode.isEmpty()) {
					List<DCMessageError> errors = new ArrayList<>();
					DCMessageError bundleExeption = new DCMessageError();
					bundleExeption.setMessageId("Msg_1403");
					screenDto.getLstData().stream()
							.filter(ProcessCommonCalc.distinctByKey(x -> x.getEmployeeId())).forEach(x -> {
								bundleExeption.setMessage(TextResource.localize("Msg_1403", x.getEmployeeCode() + " " + x.getEmployeeName()));
								errors.add(bundleExeption);
							});
					// throw bundleExeption;
					result.setErrors(errors);
					return result;
				}
				
				result.setAutBussCode(new HashSet<>(lstBusinessTypeCode));
				// Create header & sheet
				if (lstBusinessTypeCode.size() > 0) {
                    // List item hide 
					lstFormat = this.repo.getListFormatDPCorrection(lstBusinessTypeCode).stream().collect(Collectors.toList()); // 10s
					if(lstFormat.isEmpty()) throw new BusinessException("Msg_1402");
					lstAtdItem = lstFormat.stream().map(f -> f.getAttendanceItemId()).collect(Collectors.toList());
					lstAtdItemUnique = new HashSet<Integer>(lstAtdItem).stream().collect(Collectors.toList());//.filter(x -> !itemHide.containsKey(x))
					lstSheet = this.repo.getFormatSheets(lstBusinessTypeCode);
					Set<String> lstSheetNo = lstSheet.stream().map(DPSheetDto::getName).collect(Collectors.toSet());
					if (lstSheetNo.size() != lstSheet.size()) {
						lstSheet = lstSheet.stream().map(x -> new DPSheetDto(x.getName(), x.getName()))
								.collect(Collectors.toList());
					}
				}
			}
			/// 対応するドメインモデル「勤務種別日別実績の修正のフォーマット」を取得する
			String authorityDailyID =  AppContexts.user().roles().forAttendance(); 
			if (lstFormat.size() > 0) {
				lstDPBusinessTypeControl = this.repo.getListBusinessTypeControl(companyId, authorityDailyID,
						lstAtdItemUnique, true);
				if(lstDPBusinessTypeControl.isEmpty()) {
					screenDto.setErrorInfomation(DCErrorInfomation.ITEM_HIDE_ALL.value);
					return null;
				}
			}
			Map<Integer, String> itemHide = lstDPBusinessTypeControl.stream().filter(x -> x.isUseAtr()).collect(Collectors.toMap(DPBusinessTypeControl :: getAttendanceItemId, x -> "", (x, y) -> x));

			Map<Integer, Boolean> itemHideReason = getReasonDiscrepancy(companyId, lstAtdItemUnique);
			lstFormat = lstFormat.stream().filter(x -> itemHide.containsKey(x.getAttendanceItemId()) 
					                              && ((itemHideReason.containsKey(x.getAttendanceItemId()) 
					                              && itemHideReason.get(x.getAttendanceItemId())) 
					                              || !itemHideReason.containsKey(x.getAttendanceItemId()))
					                              )
					                      .collect(Collectors.toList()); 
			result.setLstBusinessTypeCode(lstDPBusinessTypeControl);
			result.setLstFormat(lstFormat);
			result.setLstSheet(lstSheet);
			result.setLstAtdItemUnique(lstAtdItemUnique);
			result.setBussiness(dailyPerformanceDto.getSettingUnit().value);
		}
		return result;
	}

	/**
	 * アルゴリズム「表示項目を制御する」を実行する | Execute the algorithm "control display items"
	 */
	public DPControlDisplayItem getItemIdNames(DisplayItem disItem, boolean showButton) {
		DPControlDisplayItem result = new DPControlDisplayItem();
		String companyId = AppContexts.user().companyId();
		result.setFormatCode(disItem.getFormatCode());
		result.setSettingUnit(disItem.isSettingUnit());
		List<DPAttendanceItem> lstAttendanceItem = new ArrayList<>();
		List<Integer> lstAtdItemUnique = disItem.getLstAtdItemUnique();
		List<FormatDPCorrectionDto> lstFormat = disItem.getLstFormat();
		if (!lstAtdItemUnique.isEmpty()) {
			Set<Integer> lstGroupInput = new HashSet<>();
			lstAtdItemUnique.stream().forEach(x -> {
				val item = ValidatorDataDailyRes.INPUT_CHECK_MAP.get(x.intValue());
				if(item != null) {
					lstGroupInput.add(item);
					lstGroupInput.add(x);
				}else{
					lstGroupInput.add(x);
				}
			});
			Map<Integer, String> itemName = dailyAttendanceItemNameAdapter.getDailyAttendanceItemName(new ArrayList<>(lstGroupInput))
					.stream().collect(Collectors.toMap(DailyAttendanceItemNameAdapterDto::getAttendanceItemId,
							x -> x.getAttendanceItemName())); // 9s
			lstAttendanceItem = lstAtdItemUnique.isEmpty() ? Collections.emptyList()
					: this.repo.getListAttendanceItem(lstAtdItemUnique).stream().map(x -> {
						x.setName(itemName.get(x.getId()));
						return x;
					}).collect(Collectors.toList());
			result.setItemInputName(itemName);
		}
		
		//set item atr from optional
		Map<Integer, Integer> optionalItemOpt = AttendanceItemIdContainer.optionalItemIdsToNos(lstAtdItemUnique, AttendanceItemType.DAILY_ITEM);
		Map<Integer, OptionalItemAtr> optionalItemAtrOpt= optionalItemOpt.isEmpty() ? Collections.emptyMap()
				: repo.findByListNos(companyId, new ArrayList<>(optionalItemOpt.values())).stream()
						.filter(x -> x.getItemNo() != null && x.getOptionalItemAtr() != null)
						.collect(Collectors.toMap(x -> x.getItemNo(), OptionalItemDto::getOptionalItemAtr));
		setOptionalItemAtr(lstAttendanceItem, optionalItemOpt, optionalItemAtrOpt);
		
		result.createSheets(disItem.getLstSheet());
		Map<Integer, DPAttendanceItem> mapDP = lstAttendanceItem.stream().filter(x -> x.getAttendanceAtr().intValue() != DailyAttendanceAtr.ReferToMaster.value).collect(Collectors.toMap(DPAttendanceItem::getId, x -> x));
		result.setMapDPAttendance(mapDP);
		result.setLstAttendanceItem(new ArrayList<>(mapDP.values()));
		lstFormat = lstFormat.stream().distinct().filter(x -> mapDP.containsKey(x.getAttendanceItemId())).collect(Collectors.toList());
		result.addColumnsToSheet(lstFormat, mapDP, showButton);
		List<DPHeaderDto> lstHeader = new ArrayList<>();
		Map<Integer, DPAttendanceItemControl> mapAttendanceItemControl = this.repo
				.getListAttendanceItemControl(companyId, lstAtdItemUnique).stream().collect(Collectors.toMap(x -> x.getAttendanceItemId(), x -> x));
		for (FormatDPCorrectionDto dto : lstFormat) {
			lstHeader.add(DPHeaderDto.createSimpleHeader(companyId,
					mergeString(DPText.ADD_CHARACTER, String.valueOf(dto.getAttendanceItemId())),
					(dto.getColumnWidth()== null || dto.getColumnWidth() == 0) ? "100px" : String.valueOf(dto.getColumnWidth()) + DPText.PX, mapDP, mapAttendanceItemControl));
		}
		
		lstHeader.add(DPHeaderDto.addHeaderSubmitted());
		if (showButton) {
			lstHeader.add(DPHeaderDto.addHeaderApplication());
		}
		lstHeader.add(DPHeaderDto.addHeaderApplicationList());
		result.setLstHeader(lstHeader);
		//if (!disItem.isSettingUnit()) {
			if (disItem.getLstBusinessTypeCode().size() > 0) {
				// set header access modifier
				// only user are login can edit or others can edit
				result.setColumnsAccessModifier(disItem.getLstBusinessTypeCode());
			}
	//	}
		for (DPHeaderDto key : result.getLstHeader()) {
			ColumnSetting columnSetting = new ColumnSetting(key.getKey(), false);
			if (!key.getKey().equals("Application") && !key.getKey().equals("Submitted") && !key.getKey().equals("ApplicationList")) {
				if (!key.getGroup().isEmpty()) {
					result.getColumnSettings().add(new ColumnSetting(key.getGroup().get(0).getKey(), false));
					result.getColumnSettings().add(new ColumnSetting(key.getGroup().get(1).getKey(), false));
				} else {
					/*
					 * 時間 - thoi gian hh:mm 5, 回数: so lan 2, 金額 : so tien 3, 日数:
					 * so ngay -
					 */
					DPAttendanceItem dPItem = mapDP
							.get(Integer.parseInt(key.getKey().substring(1, key.getKey().length()).trim()));
					columnSetting.setTypeFormat(dPItem.getAttendanceAtr());
				}
			}
			result.getColumnSettings().add(columnSetting);

		}
		if (!lstAttendanceItem.isEmpty()) {
			// set text to header
			result.setHeaderText(lstAttendanceItem);
			// set color to header
			result.setLstAttendanceItem(lstAttendanceItem);
			result.getLstAttendanceItem().stream().forEach(c -> c.setName(""));
			//result.setHeaderColor(lstAttendanceItemControl);
		} else {
			result.setLstAttendanceItem(lstAttendanceItem);
		}
		// set combo box
		result.setComboItemCalc(EnumCodeName.getCalcHours());
		result.setComboItemDoWork(EnumCodeName.getDowork());
		result.setComboItemReason(EnumCodeName.getReasonGoOut());
		result.setComboItemCalcCompact(EnumCodeName.getCalcCompact());
		result.setComboTimeLimit(EnumCodeName.getComboTimeLimit());
		result.setItemIds(lstAtdItemUnique);
		return result;
	}
	
	private void setOptionalItemAtr(List<DPAttendanceItem> lstAttendanceItem, Map<Integer, Integer> optionalItemOpt,
			Map<Integer, OptionalItemAtr> optionalItemAtr){
		lstAttendanceItem.forEach(item -> {
			Integer itemNo = optionalItemOpt.get(item.getId());
			if(itemNo != null){
				OptionalItemAtr atr = optionalItemAtr.get(itemNo);
				if(atr != null && atr.value == OptionalItemAtr.TIME.value){
					item.setAttendanceAtr(DailyAttendanceAtr.Time.value);
					//item.setPrimitive(PrimitiveValueDaily.AttendanceTimeOfExistMinus.value);
				}else if(atr != null && atr.value == OptionalItemAtr.NUMBER.value){
					item.setAttendanceAtr(DailyAttendanceAtr.NumberOfTime.value);
					//item.setPrimitive(PrimitiveValueDaily.BreakTimeGoOutTimes.value);
				}else if(atr != null && atr.value == OptionalItemAtr.AMOUNT.value){
					item.setAttendanceAtr(DailyAttendanceAtr.AmountOfMoney.value);
				}
			}
		});
	}

	/** アルゴリズム「対象者を抽出する」を実行する */
	public List<DailyPerformanceEmployeeDto> getListEmployee(String sId, DateRange dateRange) {
		// アルゴリズム「自職場を取得する」を実行する
		// List<String> lstJobTitle = this.repo.getListJobTitle(dateRange);
		// List<String> lstEmployment = this.repo.getListEmployment();
		/// 対応するドメインモデル「所属職場」を取得する + 対応するドメインモデル「職場」を取得する
		Map<String, String> lstWorkplace = this.repo.getListWorkplace(sId, dateRange);
		// List<String> lstClassification = this.repo.getListClassification();
		// 取得したドメインモデル「所属職場．社員ID」に対応するImported「（就業）社員」を取得する
		if (lstWorkplace.isEmpty()) {
			return new ArrayList<>();
		}
		return this.repo.getListEmployee(null, null, lstWorkplace, null);
	}

	/** アルゴリズム「休暇残数を表示する」を実行する */
	public void getHolidaySettingData(DailyPerformanceCorrectionDto dailyPerformanceCorrectionDto) {
//		String companyId = AppContexts.user().companyId();
//		String employeeId = "";
//		GeneralDate baseDate = GeneralDate.today();
//		// アルゴリズム「年休設定を取得する」を実行する
//		dailyPerformanceCorrectionDto.setYearHolidaySettingDto(remainHolidayService.getAnnualLeaveSetting(companyId, employeeId, baseDate));
//		// アルゴリズム「振休管理設定を取得する」を実行する
//		dailyPerformanceCorrectionDto.setSubstVacationDto(remainHolidayService.getSubsitutionVacationSetting(companyId, employeeId, baseDate));
//		// アルゴリズム「代休管理設定を取得する」を実行する
//		dailyPerformanceCorrectionDto.setCompensLeaveComDto(remainHolidayService.getCompensatoryLeaveSetting(companyId, employeeId, baseDate));
//		// アルゴリズム「60H超休管理設定を取得する」を実行する
////		dailyPerformanceCorrectionDto.setCom60HVacationDto(this.repo.getCom60HVacationDto());
	}
	
	public List<String> changeListEmployeeId(List<String> employeeIds, DateRange range, int mode, boolean isTranfer, Integer closureId, DailyPerformanceCorrectionDto screenDto) {
		//初期表示社員を取得する
		String companyId = AppContexts.user().companyId();
		String employeeIdLogin = AppContexts.user().employeeId();
		List<String> lstEmployeeId = new ArrayList<>();
		if (mode == ScreenMode.NORMAL.value) {
			
			if(!employeeIds.isEmpty()) return employeeIds;
			//社員参照範囲を取得する
			Optional<Role> role = roleRepository.findByRoleId(AppContexts.user().roles().forAttendance());
			if (!role.isPresent() || role.get().getEmployeeReferenceRange() == null || role.get()
					.getEmployeeReferenceRange() == nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange.ONLY_MYSELF) {
				return Arrays.asList(employeeIdLogin);
			}
			DatePeriod period = new DatePeriod(range.getStartDate(), range.getEndDate());
			List<String> lstWplId = workplacePub.getLstWorkplaceIdBySidAndPeriod(employeeIdLogin, period);
			List<ResultRequest597Export> lstInfoEmp =  workplacePub.getLstEmpByWorkplaceIdsAndPeriod(lstWplId, period);
			
			
//			List<RegulationInfoEmployeeQueryR> regulationRs = regulationInfoEmployeePub.search(
//					createQueryEmployee(new ArrayList<>(), range.getStartDate(), range.getEndDate()));
//			lstEmployeeId = regulationRs.stream().map(x -> x.getEmployeeId()).distinct().collect(Collectors.toList());
//			if (employeeIds.isEmpty()) {
//				// List<RegulationInfoEmployeeQueryR> regulationRs=
//				// regulationInfoEmployeePub.search(createQueryEmployee(new ArrayList<>(),
//				// range.getStartDate(), range.getEndDate()));
//				//社員と同じ職場の社員を取得する
//				List<String> listEmp = repo.getListEmpInDepartment(employeeIdLogin,
//						new DateRange(range.getStartDate(), range.getEndDate()));
//				//社員一覧を特定の会社に在籍している社員に絞り込む
//				listEmp = repo.getAffCompanyHistorySidDate(companyId, listEmp,
//						new DateRange(range.getStartDate(), range.getEndDate()));
//				
//				lstEmployeeId = narrowEmployeeAdapter.findByEmpId(listEmp, 3);
			if (lstInfoEmp.isEmpty())
				return Arrays.asList(employeeIdLogin);
			lstEmployeeId = lstInfoEmp.stream().map(x -> x.getSid()).distinct().collect(Collectors.toList());
			lstEmployeeId.add(employeeIdLogin);
			lstEmployeeId = lstEmployeeId.stream().distinct().collect(Collectors.toList());
//			if (closureId != null) {
//				Map<String, String> employmentWithSidMap = repo.getAllEmployment(companyId, lstEmployeeId,
//						new DateRange(range.getEndDate(), range.getEndDate()));
//				List<ClosureDto> closureDtos = repo.getClosureId(employmentWithSidMap, range.getEndDate());
//				lstEmployeeId = closureDtos.stream().filter(x -> x.getClosureId().intValue() == closureId.intValue())
//						.map(x -> x.getSid()).collect(Collectors.toSet()).stream().collect(Collectors.toList());
//			}
//			} else {
//				// No 338
//				// RoleType 3:就業 EMPLOYMENT
//				if(!isTranfer)lstEmployeeId = narrowEmployeeAdapter.findByEmpId(employeeIds, 3);
//				else lstEmployeeId =  employeeIds;
//			}
			if(lstEmployeeId.isEmpty()){
				//throw new BusinessException("Msg_1342");
			}
			return lstEmployeeId;
		} else if (mode == ScreenMode.APPROVAL.value) {
			if(employeeIds.isEmpty()){
				// catch exception in case has not lst emp
				ApprovalRootOfEmployeeImport approvalRoot = approvalStatusAdapter.getApprovalRootOfEmloyee(range.getStartDate(), range.getEndDate(), employeeIdLogin, companyId, 1);
				List<String> emloyeeIdApp = approvalRoot == null ? Collections.emptyList() : approvalRoot.getApprovalRootSituations().stream().map(x -> x.getTargetID()).collect(Collectors.toSet()).stream().collect(Collectors.toList());
				lstEmployeeId =  emloyeeIdApp;
			}else {
				lstEmployeeId = employeeIds;
			}
			if(lstEmployeeId.isEmpty()){
				//throw new BusinessException("Msg_916");
				screenDto.setErrorInfomation(DCErrorInfomation.APPROVAL_NOT_EMP.value);
			}
			return lstEmployeeId;
		}
		return Collections.emptyList();
	}
	
	public void insertStampSourceInfo(String employeeId, GeneralDate date, Boolean stampSourceAt,
			Boolean stampSourceLeav) {
		Optional<TimeLeavingOfDailyPerformance> timeLeavingOpt = timeLeavingOfDailyPerformanceRepository
				.findByKey(employeeId, date);
		if (timeLeavingOpt.isPresent()) {
			TimeLeavingOfDailyPerformance timeLeaving = timeLeavingOpt.get();
			if (!timeLeaving.getAttendance().getTimeLeavingWorks().isEmpty()) {
				timeLeaving.getAttendance().getTimeLeavingWorks().stream().filter(x -> x.getWorkNo() != null && x.getWorkNo().v() == 1).forEach(x -> {
					Optional<TimeActualStamp> attOpt = x.getAttendanceStamp();
					if (attOpt.isPresent()) {
						Optional<WorkStamp> workStampOpt = attOpt.get().getStamp();
						if (workStampOpt.isPresent() && stampSourceAt) {
									workStampOpt.get().setPropertyWorkStamp(
											workStampOpt.get().getTimeDay().getTimeWithDay().isPresent()
													? workStampOpt.get().getTimeDay().getTimeWithDay().get()
													: null,
											workStampOpt.get().getLocationCode().isPresent()
													? workStampOpt.get().getLocationCode().get()
													: null,
											TimeChangeMeans.SPR_COOPERATION);
								}
					}

					Optional<TimeActualStamp> leavOpt = x.getLeaveStamp();
					if (leavOpt.isPresent() && stampSourceLeav) {
						Optional<WorkStamp> workStampOpt = leavOpt.get().getStamp();
								workStampOpt.get().setPropertyWorkStamp(
										workStampOpt.get().getTimeDay().getTimeWithDay().isPresent()
												? workStampOpt.get().getTimeDay().getTimeWithDay().get()
												: null,
										workStampOpt.get().getLocationCode().isPresent()
												? workStampOpt.get().getLocationCode().get()
												: null,
										TimeChangeMeans.SPR_COOPERATION);
							}
					timeLeavingOfDailyPerformanceRepository.update(timeLeaving);
				});
			}
		}
	}
	
	public Map<String, ApproveRootStatusForEmpDto> getCheckApproval(ApprovalStatusAdapter approvalStatusAdapter, List<String> employeeIds, DateRange dateRange, String employeeIdApproval, int mode){
		// get check
		if (employeeIds.isEmpty())
			return Collections.emptyMap();

		// get disable
		if (mode == ScreenMode.APPROVAL.value) {
			//long startTime = System.currentTimeMillis();
			ApprovalRootOfEmployeeImport approvalRoot = approvalStatusAdapter.getApprovalRootOfEmloyee(
					dateRange.getStartDate(), dateRange.getEndDate(), employeeIdApproval,
					AppContexts.user().companyId(), 1);
			//System.out.println("thoi gian getApp: "+ (System.currentTimeMillis() - startTime));
			Map<String, ApproveRootStatusForEmpDto> approvalRootMap = approvalRoot == null ? Collections.emptyMap()
					: approvalRoot.getApprovalRootSituations().stream().collect(
							Collectors.toMap(x -> mergeString(x.getTargetID(), "|", x.getAppDate().toString()), x -> {
								ApproveRootStatusForEmpDto dto = new ApproveRootStatusForEmpDto();
								if (x.getApprovalStatus() == null
										|| x.getApprovalStatus().getApprovalActionByEmpl() == null) {
									dto.setCheckApproval(false);
								} else {
									if (x.getApprovalStatus()
											.getApprovalActionByEmpl() == ApprovalActionByEmpl.APPROVALED) {
										dto.setCheckApproval(true);
									} else {
										dto.setCheckApproval(false);
									}
								}
								dto.setApproverEmployeeState(x.getApprovalAtr());
								return dto;
							}, (x, y) -> x));
			return approvalRootMap;
		} else {
			//long startTime = System.currentTimeMillis();
			List<ApproveRootStatusForEmpImport> approvals = approvalStatusAdapter.getApprovalByListEmplAndListApprovalRecordDateNew(dateRange.toListDate(), employeeIds, 1);
			//System.out.println("thoi gian getApp: "+ (System.currentTimeMillis() - startTime));
			Map<String, ApproveRootStatusForEmpDto> approvalRootMap = approvals.stream().collect(Collectors.toMap(x -> mergeString(x.getEmployeeID(), "|", x.getAppDate().toString()), x -> {
				return new ApproveRootStatusForEmpDto(null, x.getApprovalStatus() != ApprovalStatusForEmployee.UNAPPROVED);
			}, (x,y) ->x));
			return approvalRootMap;
		}
	}
	
	//出退勤打刻の初期値を埋める
	public SPRCheck checkSPR(String companyId, List<Integer> itemIds, String lock, ApprovalUseSettingDto approval, IdentityProcessUseSetDto indentity, boolean checkApproval, boolean checkIndentity){
		if (lock.matches(".*[D].*"))
			return SPRCheck.NOT_INSERT;
		List<Integer> items = itemIds.stream().filter(x -> (x == 31 || x == 34)).collect(Collectors.toList());
		if (items.size() == 0)
			return SPRCheck.NOT_INSERT;
		//check 取得しているドメインモデル「本人確認処理の利用設定」、「承認処理の利用設定」をチェックする 
		//false
		if((approval == null || (approval != null && !approval.getUseDayApproverConfirm())
		&& (indentity == null || (indentity != null && !indentity.isUseConfirmByYourself()))
		|| (!checkApproval && !checkIndentity))){
			//TODO  xu ly insert SPR va load 
			return SPRCheck.INSERT;
		}
		//
		return SPRCheck.SHOW_CONFIRM;
		
	}
	
	//ドメインモデル「日別実績の出退勤」を取得する
	public ChangeSPR processSPR(String employeeId, GeneralDate date, ObjectShare shareSPR, boolean change31, boolean change34){
		return new ChangeSPR(change31, change34);
	}
	
	public DatePeriodInfo changeDateRange(DateRange dateRange, DateRange dateRangeInit, ObjectShare objectShare, String companyId, String sId,
			DailyPerformanceCorrectionDto screenDto, Integer closureId, Integer mode, Integer displayFormat, boolean changeFormat, Boolean initScreenOther, DPCorrectionStateParam dpStateParam) {
		
		if (dateRange != null && (initScreenOther == null || !initScreenOther) && !changeFormat){
			screenDto.setEmploymentCode(getEmploymentCode(companyId, dateRange.getEndDate(), sId));
			if(dpStateParam != null) {
			DatePeriodInfo dateInfo = dpStateParam.getDateInfo();
			dateInfo.setTargetRange(dateRange);
			dateInfo.setClosureId(ClosureId.valueOf(closureId));
			return dateInfo;
			}
		}
		
		if (dateRange != null && initScreenOther != null && initScreenOther) {
			return updatePeriod(
					(objectShare == null || objectShare.getYearMonth() == null) ? Optional.empty()
							: Optional.of(YearMonth.of(objectShare.getYearMonth())),
					Optional.empty(), objectShare == null ? Optional.empty() : Optional.of(objectShare.getDateTarget()),
					displayFormat, sId, new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()));
		}

		boolean isObjectShare = objectShare != null &&((objectShare.getStartDate() != null
				&& objectShare.getEndDate() != null ) || objectShare.getYearMonth() != null);

		if (isObjectShare && objectShare.getInitClock() == null) {
			// get employmentCode
			dateRange = new DateRange(objectShare.getStartDate(), objectShare.getEndDate());
			screenDto.setEmploymentCode(getEmploymentCode(companyId, dateRange.getEndDate(), sId));
			return updatePeriod(
					objectShare.getYearMonth() == null ? Optional.empty()
							: Optional.ofNullable(YearMonth.of(objectShare.getYearMonth())),
					Optional.empty(), Optional.ofNullable(objectShare.getEndDate()), displayFormat, sId,
					new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()));
		} else {
			GeneralDate dateRefer = GeneralDate.today();
			if (isObjectShare && objectShare.getInitClock() != null) {
				// <<Public>> SPR日報から起動する
				dateRefer = objectShare.getEndDate();
				screenDto.setEmploymentCode(getEmploymentCode(companyId, dateRefer, sId));
				Optional<ClosureEmployment> closureEmploymentOptional = this.closureEmploymentRepository
						.findByEmploymentCD(companyId, screenDto.getEmploymentCode());
				if (closureEmploymentOptional.isPresent()) {
					// screenDto.setClosureId(closureEmploymentOptional.get().getClosureId());
					Optional<PresentClosingPeriodExport> closingPeriod = (isObjectShare
							&& objectShare.getInitClock() != null)
									? shClosurePub.find(companyId, closureEmploymentOptional.get().getClosureId(),
											dateRefer)
									: shClosurePub.find(companyId, closureEmploymentOptional.get().getClosureId());
					dateRange = new DateRange(closingPeriod.get().getClosureStartDate(),
							closingPeriod.get().getClosureEndDate());
					return updatePeriod(Optional.empty(), Optional.empty(), Optional.of(objectShare.getInitClock().getDateSpr()), displayFormat, sId, new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()));
				}else {
					return updatePeriod(Optional.empty(), Optional.empty(), Optional.of(objectShare.getInitClock().getDateSpr()), displayFormat, sId, new DatePeriod(dateRefer, dateRefer));
				}
			} else {
				screenDto.setEmploymentCode(getEmploymentCode(companyId, dateRefer, sId));
//				InitSwitchSetDto initSwitch = initSwitchSetAdapter.targetDateFromLogin();
				if(dateRangeInit != null) {
//					Optional<DateProcessedRecord> dateRecordOpt = initSwitch.getListDateProcessed().stream().filter(x -> x.getClosureID() == screenDto.getClosureId()).findFirst();
//					if(dateRecordOpt.isPresent() && dateRecordOpt.get().getDatePeriod() != null) {
//						DateRange rangeTemp =  new DateRange(dateRecordOpt.get().getDatePeriod().start(), dateRecordOpt.get().getDatePeriod().end());
						return updatePeriod(Optional.empty(), Optional.empty(), Optional.empty(), displayFormat, sId, new DatePeriod(dateRangeInit.getStartDate(), dateRangeInit.getEndDate()));
//					}
				}
			}
            
			DateRange rangeTemp =  new DateRange(GeneralDate.legacyDate(new Date()).addMonths(-1).addDays(+1),
					GeneralDate.legacyDate(new Date()));
			return updatePeriod(Optional.empty(), Optional.empty(), Optional.empty(), displayFormat, sId,  new DatePeriod(rangeTemp.getStartDate(), rangeTemp.getEndDate()));
		}
	}
	
	public Integer getClosureId(String companyId, String employeeId, GeneralDate date) {
		String empCode = getEmploymentCode(companyId, date, employeeId);
		Optional<ClosureEmployment> closureEmploymentOptional = this.closureEmploymentRepository
				.findByEmploymentCD(companyId, empCode);
		if(closureEmploymentOptional.isPresent()) return closureEmploymentOptional.get().getClosureId();
		return null;
	}
	
	public void setTextColorDay(DailyPerformanceCorrectionDto screenDto, GeneralDate date, String columnKey, String rowId, List<GeneralDate> holidayDates){
		
		boolean isHoliday = holidayDates.contains(date);
		if (isHoliday || date.dayOfWeek() == 7) {
			// Sunday
			screenDto.setCellSate(rowId, columnKey, DPText.COLOR_SUN);
		} else if (date.dayOfWeek() == 6) {
			// Saturday
			screenDto.setCellSate(rowId, columnKey, DPText.COLOR_SAT);
		}
	}
	
	//対象期間の更新
	public DatePeriodInfo updatePeriod(Optional<YearMonth> yearMonthOpt, Optional<Integer> closureIdShare, Optional<GeneralDate> dateTransfer, int displayFormat, String empTarget, DatePeriod period) {
		GeneralDate today = GeneralDate.today();
		DateRange result = new DateRange(today, today);
		if(period != null) {
			result = new DateRange(period.start(), period.end());
		}
		ClosureId closureId = null;
		YearMonth yearMonth = null;
		List<nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.ClosurePeriod> lstClosurePeriod  = new ArrayList<>();
		List<DateRangeClosureId> lstPeriod = new ArrayList<>();
		List<AggrPeriodClosure> lstClosureCache = new ArrayList<>();
		String empLogin = AppContexts.user().employeeId();
		// 個人別
		if (displayFormat == DisplayFormat.Individual.value) {
			if (yearMonthOpt.isPresent()) {
				GeneralDate dateRefer = GeneralDate.ymd(yearMonthOpt.get().year(), yearMonthOpt.get().month(),
						yearMonthOpt.get().lastDateInMonth());
				yearMonth = yearMonthOpt.get();
				lstClosurePeriod.addAll(GetClosurePeriod
						.fromYearMonth(requireService.createRequire(), new CacheCarrier(),
								empTarget, dateRefer, yearMonthOpt.get()));
			} else {
				Optional<ClosurePeriod> closurePeriodOpt = findClosureService.getClosurePeriod(empTarget,
						period.start());
				YearMonth ym = period.end().yearMonth();
				if(closurePeriodOpt.isPresent()) {
					ym = closurePeriodOpt.get().getYearMonth();
				}
				GeneralDate dateRefer = GeneralDate.ymd(ym.year(),
						ym.month(),
						ym.lastDateInMonth());
				yearMonth = ym;
				lstClosurePeriod.addAll(GetClosurePeriod
						.fromYearMonth(requireService.createRequire(), new CacheCarrier(),
								empTarget, dateRefer, ym));
			}
//			if(lstClosurePeriod.isEmpty()) return null;
			if(lstClosurePeriod.isEmpty()) return new DatePeriodInfo(new ArrayList<>(), result, yearMonth == null ? 0 : yearMonth.v(), closureId, lstClosureCache, lstPeriod);
			
			List<AggrPeriodEachActualClosure> lstAggrPeriod = lstClosurePeriod.stream().flatMap(x -> x.getAggrPeriods().stream())
					    .sorted((x, y) -> x.getPeriod().start().compareTo(y.getPeriod().end()))
						.collect(Collectors.toList());
			 
			List<DateRangeClosureId> lstAgg = lstClosurePeriod.stream().flatMap(x -> x.getAggrPeriods().stream())
					.map(x -> new DateRangeClosureId(x.getPeriod().start(), x.getPeriod().end(), x.getClosureId().value)).sorted((x, y) -> x.getStartDate().compareTo(y.getStartDate()))
					.collect(Collectors.toList());
			lstPeriod.addAll(lstAgg);
			
			//Optional<DateRange> dateOpt = lstAgg.stream().filter(x -> x.inRange(today)).findFirst();
			Optional<AggrPeriodEachActualClosure> dateAggOpt = Optional.empty();
		
			if(!closureIdShare.isPresent()) {
				dateAggOpt = lstAggrPeriod.stream().filter(x -> DateRange.convertPeriod(x.getPeriod()).inRange(today)).findFirst();
			}else {
				dateAggOpt = lstAggrPeriod.stream().filter(x -> x.getClosureId().value == closureIdShare.get()).findFirst();
			}
			
			AggrPeriodEachActualClosure dateAgg = dateAggOpt.isPresent() ? dateAggOpt.get() : lstAggrPeriod.get(0);
			result = DateRange.convertPeriod(dateAgg.getPeriod());
			closureId = dateAgg.getClosureId();
			lstClosureCache.addAll(lstClosurePeriod.stream().flatMap(x -> x.getAggrPeriods().stream()).map(
					x -> new AggrPeriodClosure(x.getClosureId(), x.getClosureDate(), x.getYearMonth().v(), x.getPeriod()))
					.collect(Collectors.toList()));
		
		} else if (displayFormat == DisplayFormat.ByDate.value) {
			// 日付別(daily)
			if(dateTransfer.isPresent()) {
				result = new DateRange(dateTransfer.get(), dateTransfer.get());
			}
			else if(period.start().beforeOrEquals(today) && period.end().afterOrEquals(today)) {
				result = new DateRange(today, today);
			}else {
				result = new DateRange(period.start(), period.start());
			}
		} else {
			// エラーアラーム(error alarm)
			Optional<ClosurePeriod> closurePeriodOpt = findClosureService.getClosurePeriod(empLogin,
					period.start());
			if(!closurePeriodOpt.isPresent()) {
				result = DateRange.convertPeriod(period);
			}else {
				result = DateRange.convertPeriod(closurePeriodOpt.get().getPeriod());
			}
		}
				
		return new DatePeriodInfo(new ArrayList<>(), result, yearMonth == null ? 0 : yearMonth.v(), closureId, lstClosureCache, lstPeriod);
	}
	
	public void requestForFlush(){
		this.repo.requestForFlush();
	}
	
	
	public boolean checkDataInClosing( Pair<String, GeneralDate> pairEmpDate, Map<String, List<EmploymentHisOfEmployeeImport>> mapClosingEmpResult) {
		val empWithListDate = mapClosingEmpResult.get(pairEmpDate.getLeft());
		if (empWithListDate == null)
			return false;
		List<DatePeriod> lstDatePeriod = empWithListDate.stream()
				.map(x -> new DatePeriod(x.getStartDate(), x.getEndDate())).collect(Collectors.toList());

		val check = lstDatePeriod.stream()
				.filter(x -> x.start().beforeOrEquals(pairEmpDate.getRight()) && x.end().afterOrEquals(pairEmpDate.getRight()))
				.findFirst();
		return check.isPresent();
	}
	
	public Pair<Integer, DateRange> identificationPeriod(Integer closureId, int mode, DateRange dateRange) {
		String companyId = AppContexts.user().companyId();
		String employeeId = AppContexts.user().employeeId();
		DateRange rangeTemp = new DateRange(GeneralDate.today(), GeneralDate.today());
		Integer closureIdResult = closureId == null ? getClosureId(companyId, employeeId, GeneralDate.today())
				: closureId;
		if (dateRange == null) {
			InitSwitchSetDto initSwitch = initSwitchSetAdapter.targetDateFromLogin();
			if (initSwitch != null && !CollectionUtil.isEmpty(initSwitch.getListDateProcessed())) {
				Optional<DateProcessedRecord> dateRecordOpt = initSwitch.getListDateProcessed().stream()
						.filter(x -> x.getClosureID() == closureIdResult).findFirst();
				if (dateRecordOpt.isPresent() && dateRecordOpt.get().getDatePeriod() != null) {
					rangeTemp = new DateRange(dateRecordOpt.get().getDatePeriod().start(),
							dateRecordOpt.get().getDatePeriod().end());
				}
			}
		}else {
			rangeTemp = dateRange;
		}
		return Pair.of(closureIdResult, rangeTemp);
	}
}
 
