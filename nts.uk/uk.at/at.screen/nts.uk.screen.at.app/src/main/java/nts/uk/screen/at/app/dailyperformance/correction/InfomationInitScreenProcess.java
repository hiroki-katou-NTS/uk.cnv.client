package nts.uk.screen.at.app.dailyperformance.correction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmploymentRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.finddata.IFindDataDCRecord;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationMethod;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.operationsettings.TaskOperationSettingRepository;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalUseSettingDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.CorrectionOfDailyPerformance;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPControlDisplayItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPDataDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPErrorDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPParams;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceEmployeeDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DatePeriodInfo;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DisplayItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.IdentityProcessUseSetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ObjectShare;
import nts.uk.screen.at.app.dailyperformance.correction.dto.OperationOfDailyPerformanceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.DPCorrectionStateParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.companyhist.AffComHistItemAtScreen;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workplacehist.WorkPlaceHistTemp;
import nts.uk.screen.at.app.dailyperformance.correction.error.DCErrorInfomation;
import nts.uk.screen.at.app.dailyperformance.correction.finddata.IFindData;
import nts.uk.screen.at.app.dailyperformance.correction.month.asynctask.ParamCommonAsync;
import nts.uk.screen.at.app.dailyperformance.correction.searchemployee.FindAllEmployee;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.license.option.OptionLicense;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class InfomationInitScreenProcess {
	
	@Inject
	private DailyPerformanceScreenRepo repo;
	
	@Inject
	private IFindData findData;
	
	@Inject
	private FindAllEmployee findAllEmployee;

	@Resource
	private ManagedExecutorService executorService;

	@Inject
	private IFindDataDCRecord iFindDataDCRecord;
	
	@Inject
	private DailyPerformanceCorrectionProcessor processor;
	
	@Inject
	private RestrictConfirmEmploymentRepository restrictConfirmEmploymentRepository;
	
	@Inject
	private TaskOperationSettingRepository taskOperationSettingRepository;

	public Pair<DailyPerformanceCorrectionDto, ParamCommonAsync> initGetParam(DPParams param) {
		
		DateRange dateRange = param.dateRange;
		List<DailyPerformanceEmployeeDto> lstEmployee = param.lstEmployee;
		Integer initScreen = param.initScreen;
		Integer mode = param.mode;
		Integer displayFormat = param.displayFormat;
		CorrectionOfDailyPerformance correct = param.correctionOfDaily; 
		List<String> formatCodes = param.formatCodes;
//		Boolean showError = param.showError; 
//		Boolean showLock = param.showLock;
		ObjectShare objectShare = param.objectShare; 
		Integer closureId = param.closureId;
		Boolean initScreenOther = param.initFromScreenOther;
		//long timeStart = System.currentTimeMillis();
		String sId = AppContexts.user().employeeId();
		String companyId = AppContexts.user().companyId();
		Boolean needSortEmp = Boolean.FALSE;
		
		//起動に必要な情報の取得(Lấy các thông tin cần thiết cho khởi động)
		DailyPerformanceCorrectionDto screenDto = new DailyPerformanceCorrectionDto();
		findData.destroyFindData();
		iFindDataDCRecord.clearAllStateless();
		
		////起動に必要な情報の取得
		// identityProcessDto show button A2_6
		//アルゴリズム「本人確認処理の利用設定を取得する」を実行する
		Optional<IdentityProcessUseSetDto> identityProcessDtoOpt = repo.findIdentityProcessUseSet(companyId);
		screenDto.setIdentityProcessDtoOpt(identityProcessDtoOpt);
		screenDto.setIdentityProcessDto(identityProcessDtoOpt.isPresent() ? identityProcessDtoOpt.get()
				: new IdentityProcessUseSetDto(false, false, null));
		Optional<ApprovalUseSettingDto> approvalUseSettingDtoOpt = repo.findApprovalUseSettingDto(companyId);	
		screenDto.setApprovalUseSettingDtoOpt(approvalUseSettingDtoOpt);
		
		processor.setHideCheckbox(screenDto, identityProcessDtoOpt, approvalUseSettingDtoOpt, companyId, mode);
		
		//アルゴリズム「実績修正画面で利用するフォーマットを取得する」を実行する(thực hiện xử lý 「実績修正画面で利用するフォーマットを取得する」)
		OperationOfDailyPerformanceDto dailyPerformanceDto = repo.findOperationOfDailyPerformance();
		
		// 就業確定の機能制限を取得する
		Optional<RestrictConfirmEmployment> opRestrictConfirmEmployment = restrictConfirmEmploymentRepository.findByCompanyID(companyId);
		screenDto.setConfirmEmployment(opRestrictConfirmEmployment.map(x -> x.isConfirmEmployment()).orElse(null));
		
		// 作業運用設定を取得する
		Optional<TaskOperationSetting> opTaskOperationSetting = taskOperationSettingRepository.getTasksOperationSetting(companyId);
		
		screenDto.setShowWorkLoad(opTaskOperationSetting.map(x -> x.getTaskOperationMethod()==TaskOperationMethod.USED_IN_ACHIEVENTS).orElse(false)
				&& AppContexts.optionLicense().attendance().workload());
		
		// アルゴリズム「休暇の管理状況をチェックする」を実行する | Get holiday setting data --休暇の管理状況をチェックする
//		getHolidaySettingData(screenDto);
		
		/**
		 * システム日付を基準に1ヵ月前の期間を設定する | Set date range one month before system date
		 */
		/** 画面制御に関する情報を取得する | Acquire information on screen control */
		// アルゴリズム「社員の日別実績の権限をすべて取得する」を実行する | Execute "Acquire all permissions of
		// ログイン社員の日別実績の権限を取得する(Lấy tất cả quyền về 日別実績 của nhân viên)
		screenDto.setAuthorityDto(processor.getAuthority(screenDto));

		//保持パラメータを生成する(Tạo retentionParam-Param lưu giữ )
		//表示形式の特定 - xu ly tren UI la displayFormat
		
		// 社員に対応する処理締めを取得する
//		if (closureId == null) {
//			screenDto.setClosureId(processor.getClosureId(companyId, sId, GeneralDate.today()));
//		} else {
//			screenDto.setClosureId(closureId);
//		}
		//System.out.println("time get master" + (System.currentTimeMillis() - timeStart));
		Pair<Integer, DateRange> resultIndentityPeriod = processor.identificationPeriod(closureId, mode, dateRange);
		//System.out.println("time get period" + (System.currentTimeMillis() - timeStart));
		screenDto.setClosureId(resultIndentityPeriod.getLeft());
		DateRange rangeInit = resultIndentityPeriod.getRight();
		// 社員一覧を変更する -- Lấy nhân viên từ màn hinh khác hoặc lấy từ lần khởi động đầu
		// tiên
		// 対象社員の特定
		List<String> changeEmployeeIds = new ArrayList<>();
		if (lstEmployee.isEmpty()) {
			val employeeIds = objectShare == null
					? lstEmployee.stream().map(x -> x.getId()).collect(Collectors.toList())
		                    : CollectionUtil.isEmpty(objectShare.getLstExtractedEmployee()) ?  objectShare.getLstEmployeeShare() : objectShare.getLstExtractedEmployee();
			if (employeeIds.isEmpty())
				needSortEmp = true;
			changeEmployeeIds = processor.changeListEmployeeId(employeeIds, rangeInit, mode,
					objectShare != null, screenDto.getClosureId(), screenDto);
		} else {
			changeEmployeeIds = lstEmployee.stream().map(x -> x.getId()).collect(Collectors.toList());
		}

		List<String> employeeIdsOri = changeEmployeeIds;
		//System.out.println("time get employeeId" + (System.currentTimeMillis() - timeStart));	
		//<<Public>> パラメータに初期値を設定する
		///期間を変更する
		String empSelected = sId;
		if(displayFormat == 0){
			empSelected = objectShare == null ?  (lstEmployee.isEmpty() ? sId : lstEmployee.get(0).getId()) : objectShare. getIndividualTarget();
		}
		DatePeriodInfo resultPeriod = processor.changeDateRange(dateRange, rangeInit, objectShare, companyId, empSelected,
				screenDto, screenDto.getClosureId(), mode, displayFormat, param.changeFormat, initScreenOther, param.dpStateParam);
		//TODO: empty dateRange
		if(resultPeriod == null) {
			//throw new BusinessException(new RawErrorMessage("Error date range empty"));
			screenDto.setErrorInfomation(DCErrorInfomation.NOT_EMP_IN_HIST.value);
			//setStateParam(screenDto, resultPeriod, displayFormat, initScreenOther);
			return Pair.of(screenDto, null);
		}
		dateRange = resultPeriod.getTargetRange();
		screenDto.setPeriodInfo(resultPeriod);
		///表示形式を変更する -- get from Characteristic 
		DateRange datePeriodResult = dateRange;
		if(initScreen == 0 && objectShare != null && objectShare.getDisplayFormat() == 1){
			dateRange = new DateRange(objectShare.getDateTarget(), objectShare.getDateTarget());
			datePeriodResult = dateRange;
		}else if(displayFormat == 1 && !dateRange.getStartDate().equals(dateRange.getEndDate())){
			dateRange = new DateRange(dateRange.getStartDate(), dateRange.getStartDate());
		}
		screenDto.setDateRange(dateRange);
		screenDto.setDatePeriodResult(datePeriodResult);
		//System.out.println("time change period: " + (System.currentTimeMillis() - timeStart));
		//if(changeEmployeeIds.isEmpty()) return screenDto;
		// アルゴリズム「通常モードで起動する」を実行する
		/**
		 * アルゴリズム「表示形式に従って情報を取得する」を実行する | Execute "Get information according to
		 * display format"
		 */
		// アルゴリズム「対象者を抽出する」を実行する | Execute "Extract subject"
		//List<EmployeeInfoFunAdapterDto> employeeInfoAdapter = changeEmployeeIds.isEmpty() ? Collections.emptyList() :  employeeInfoFunAdapter.getListPersonInfor(changeEmployeeIds);
		//screenDto.setLstEmployee(converEmployeeList(employeeInfoAdapter));
		//get All Workplace employee
		//Map<String, String> wplNameMap = repo.getListWorkplaceAllEmp(changeEmployeeIds, screenDto.getDateRange().getEndDate());
		screenDto.setLstEmployee(findAllEmployee.findAllEmployee(changeEmployeeIds, dateRange.getEndDate()));
		// only get detail infomation employee when mode 2, 3 extract
		//System.out.println("time get data employee" + (System.currentTimeMillis() - timeStart));
		val timeStart1 = System.currentTimeMillis();
		Map<String, WorkPlaceHistTemp> WPHMap = repo.getWplByListSidAndPeriod(companyId, changeEmployeeIds, screenDto.getDateRange().getEndDate());
		//set name workplace
		screenDto.getLstEmployee().stream().map(x -> {
			val wph = WPHMap.get(x.getId());
			x.setWorkplaceName(wph == null ? "" : wph.getName());
			return x;
		}).collect(Collectors.toList());
		if(displayFormat == 0){
			String employeeSelect = objectShare == null ?  (lstEmployee.isEmpty() ? sId : lstEmployee.get(0).getId()) : objectShare. getIndividualTarget();
			changeEmployeeIds = changeEmployeeIds.stream().filter(x -> x.equals(employeeSelect)).collect(Collectors.toList());
		}
		//List<WorkPlaceHistImport> wPH = changeEmployeeIds.isEmpty() ? Collections.emptyList() : workplaceWorkRecordAdapter.getWplByListSidAndPeriod(changeEmployeeIds, new DatePeriod(GeneralDate.min(), GeneralDate.max()));
		System.out.println("time get data wplhis" + (System.currentTimeMillis() - timeStart1));//slow
		List<DailyPerformanceEmployeeDto> lstEmployeeData = processor.extractEmployeeData(initScreen, sId,
				screenDto.getLstEmployee(), objectShare);
		
		// 表示形式をチェックする | Check display format => UI
		// Create lstData: Get by listEmployee & listDate
		// 日付別の情報を取得する + 個人別の情報を取得する + エラーアラームの情報を取得する | Acquire information by
		// date + Acquire personalized information + Acquire error alarm
		// information
		screenDto.setLstData(processor.getListData(lstEmployeeData, dateRange, displayFormat));
		//get employee 
		//val timeStart2 = System.currentTimeMillis();
		//List<AffCompanyHistImport> affCompany = changeEmployeeIds.isEmpty() ? Collections.emptyList() : employeeHistWorkRecordAdapter.getWplByListSidAndPeriod(changeEmployeeIds, new DatePeriod(GeneralDate.min(), GeneralDate.max()));
		//社員ID（List）と指定期間から所属会社履歴項目を取得
		Map<String, List<AffComHistItemAtScreen>> affCompanyMap = repo.getAffCompanyHistoryOfEmployee(AppContexts.user().companyId(), changeEmployeeIds);
		
		//System.out.println("time map data wplhis, date:" + (System.currentTimeMillis() - timeStart)); //slow
		//val timeStart3 = System.currentTimeMillis();
		screenDto.setLstData(processor.setWorkPlace(WPHMap, affCompanyMap, screenDto.getLstData()));
		/// 対応する「日別実績」をすべて取得する | Acquire all corresponding "daily performance"
		List<String> listEmployeeId = screenDto.getLstData().stream().map(e -> e.getEmployeeId()).collect(Collectors.toSet()).stream().collect(Collectors.toList());
		if(listEmployeeId.isEmpty()) {
			//screenDto.setLstEmployee(Collections.emptyList());
			screenDto.setErrorInfomation(DCErrorInfomation.NOT_EMP_IN_HIST.value);
			setStateParam(screenDto, resultPeriod, displayFormat, initScreenOther);
			return Pair.of(screenDto, null);
		}
		//System.out.println("time map data wplhis, date:" + (System.currentTimeMillis() - timeStart));
		//パラメータ「表示形式」をチェックする - Đã thiết lập truyền từ UI nên không cần check lấy theo định dạng nào , nhân viên đã được truyền
		
		// Lấy thành tích nhân viên theo ngày 
		/// アルゴリズム「対象日に対応する社員の実績の編集状態を取得する」を実行する | Execute "Acquire edit status
		/// of employee's record corresponding to target date"| lay ve trang
		/// thai sua cua thanh tich nhan vien tuong ung
		//val timeStart4 = System.currentTimeMillis();
		/// アルゴリズム「実績エラーをすべて取得する」を実行する | Execute "Acquire all actual errors"
		List<DPErrorDto> lstError = processor.getErrorList(screenDto, listEmployeeId);
		screenDto.setDPErrorDto(lstError);
		//System.out.println("time get error:" + (System.currentTimeMillis() - timeStart));
//		Map<String, String> listEmployeeError = lstError.stream()
//				.collect(Collectors.toMap(e -> e.getEmployeeId(), e -> "", (x, y) -> x));
		if (displayFormat == 2) {
			// only filter data error
//			if(listEmployeeError.isEmpty()) throw new BusinessException("Msg_672");
//			listEmployeeId = listEmployeeId.stream().filter(x -> listEmployeeError.containsKey(x))
//					.collect(Collectors.toList());
//			screenDto.setLstData(screenDto.getLstData().stream()
//					.filter(x -> listEmployeeError.containsKey(x.getEmployeeId())).collect(Collectors.toList()));
		}
		
		List<DPDataDto> listData = new ArrayList<>();
		for (String employeeId : employeeIdsOri) {
			screenDto.getLstData().stream().forEach(item -> {
				if(item.getEmployeeId().equals(employeeId)){
					listData.add(item);
				}				
			});
			
		}
		screenDto.setLstData(needSortEmp ? listData.stream().sorted((x, y) ->x.getEmployeeCode().compareTo(y.getEmployeeCode())).collect(Collectors.toList()) : listData);
		// アルゴリズム「社員に対応する処理締めを取得する」を実行する | Execute "Acquire Process Tightening
		// Corresponding to Employees"--
		/// アルゴリズム「対象日に対応する承認者確認情報を取得する」を実行する | Execute "Acquire Approver
		/// Confirmation Information Corresponding to Target Date"
		// アルゴリズム「就業確定情報を取得する」を実行する
		/// アルゴリズム「日別実績のロックを取得する」を実行する (Tiến hành xử lý "Lấy về lock của thành
		// tích theo ngày")

		// check show column 本人
		// check show column 承認
		//DailyRecOpeFuncDto dailyRecOpeFun = findDailyRecOpeFun(screenDto, companyId, mode).orElse(null);
		//System.out.println("time before get item" + (System.currentTimeMillis() - timeStart4));
		boolean showButton = true;
		if (displayFormat == 0) {
			if (!listEmployeeId.isEmpty() && !sId.equals(listEmployeeId.get(0))) {
				showButton = false;
			}
		}
		
		//get itemId
		DisplayItem disItem = processor.getDisplayItems(correct, formatCodes, companyId, screenDto, listEmployeeId, showButton, dailyPerformanceDto);
		if(disItem == null || !disItem.getErrors().isEmpty()) {
			if(disItem != null) screenDto.setErrors(disItem.getErrors());
			setStateParam(screenDto, resultPeriod, displayFormat, initScreenOther);
			return Pair.of(screenDto,
					listEmployeeId.isEmpty() ? null
							: new ParamCommonAsync(listEmployeeId.get(0), dateRange, screenDto.getEmploymentCode(),
									screenDto.getAutBussCode(), displayFormat, screenDto.getIdentityProcessDto(),
									screenDto.getStateParam(), Optional.empty(), false));
		}
		screenDto.setAutBussCode(disItem.getAutBussCode());
		screenDto.setEmployeeIds(listEmployeeId);
		screenDto.setChangeEmployeeIds(changeEmployeeIds);
		//System.out.println("time get itemId:" + (System.currentTimeMillis() - timeStart));
		//get item Name
		DPControlDisplayItem dPControlDisplayItem = processor.getItemIdNames(disItem, showButton);
		screenDto.setLstControlDisplayItem(dPControlDisplayItem);
		screenDto.setDisItem(disItem);
		setStateParam(screenDto, resultPeriod, displayFormat, initScreenOther);
		//System.out.println("time init All" + (System.currentTimeMillis() - timeStart));
		return Pair.of(screenDto,
				listEmployeeId.isEmpty() ? null
						: new ParamCommonAsync(listEmployeeId.get(0), dateRange, screenDto.getEmploymentCode(),
								screenDto.getAutBussCode(), displayFormat, screenDto.getIdentityProcessDto(),
								screenDto.getStateParam(),  Optional.empty(), false));
	}
	
	private void setStateParam(DailyPerformanceCorrectionDto screenDto, DatePeriodInfo info, int displayFormat, Boolean transferDesScreen) {
		DPCorrectionStateParam cacheParam = new DPCorrectionStateParam(
				new DatePeriod(screenDto.getDateRange().getStartDate(), screenDto.getDateRange().getEndDate()),
				screenDto.getEmployeeIds(), displayFormat, screenDto.getEmployeeIds(), screenDto.getLstControlDisplayItem(), info, transferDesScreen);
		screenDto.setStateParam(cacheParam);

	}
}
