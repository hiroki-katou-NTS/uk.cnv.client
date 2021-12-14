/**
 * 5:57:43 PM Aug 28, 2017
 */
package nts.uk.screen.at.ws.dailyperformance.correction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.web.session.HttpSubSession;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.DailyPerformanceAuthoritySetting;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.MonthlyPerfomanceAuthorityFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.bs.employee.dom.employee.service.SearchEmployeeService;
import nts.uk.ctx.bs.employee.dom.employee.service.dto.EmployeeSearchData;
import nts.uk.ctx.bs.employee.dom.employee.service.dto.EmployeeSearchDto;
import nts.uk.screen.at.app.dailymodify.command.DailyCalculationRCommandFacade;
import nts.uk.screen.at.app.dailymodify.command.DailyModifyRCommandFacade;
import nts.uk.screen.at.app.dailymodify.command.PersonalTightCommandFacade;
import nts.uk.screen.at.app.dailyperformance.correction.DPUpdateColWidthCommandHandler;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceCorrectionProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.DisplayRemainingHolidayNumber;
import nts.uk.screen.at.app.dailyperformance.correction.HolidayRemainParam;
import nts.uk.screen.at.app.dailyperformance.correction.InfomationInitScreenProcess;
import nts.uk.screen.at.app.dailyperformance.correction.UpdateColWidthCommand;
import nts.uk.screen.at.app.dailyperformance.correction.calctime.DailyCorrectCalcTimeService;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalConfirmCache;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPAttendanceItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPDataDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPHeaderDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemParent;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPParams;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCalculationDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DatePeriodInfo;
import nts.uk.screen.at.app.dailyperformance.correction.dto.EmpAndDate;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ErAlWorkRecordShortDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ErrorReferenceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.HolidayRemainNumberDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.DPCorrectionStateParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.calctime.DCCalcTime;
import nts.uk.screen.at.app.dailyperformance.correction.dto.calctime.DCCalcTimeParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.mobile.ErrorParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.mobile.MasterDialogParam;
import nts.uk.screen.at.app.dailyperformance.correction.flex.CalcFlexDto;
import nts.uk.screen.at.app.dailyperformance.correction.flex.CheckBeforeCalcFlex;
import nts.uk.screen.at.app.dailyperformance.correction.gendate.GenDateDto;
import nts.uk.screen.at.app.dailyperformance.correction.gendate.GenDateProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.kdw003b.DailyPerformErrorReferDto;
import nts.uk.screen.at.app.dailyperformance.correction.kdw003b.DailyPerformErrorReferExportDto;
import nts.uk.screen.at.app.dailyperformance.correction.kdw003b.DailyPerformErrorReferExportService;
import nts.uk.screen.at.app.dailyperformance.correction.kdw003b.DailyPerformErrorReferFinder;
import nts.uk.screen.at.app.dailyperformance.correction.loadupdate.DPLoadRowProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.loadupdate.DPPramLoadRow;
import nts.uk.screen.at.app.dailyperformance.correction.loadupdate.onlycheckbox.DPLoadVerProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.loadupdate.onlycheckbox.LoadVerData;
import nts.uk.screen.at.app.dailyperformance.correction.loadupdate.onlycheckbox.LoadVerDataResult;
import nts.uk.screen.at.app.dailyperformance.correction.lock.button.DPDisplayLockParam;
import nts.uk.screen.at.app.dailyperformance.correction.lock.button.DPDisplayLockProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.mobile.DPCorrectionProcessorMob;
import nts.uk.screen.at.app.dailyperformance.correction.month.asynctask.MonthParamInit;
import nts.uk.screen.at.app.dailyperformance.correction.month.asynctask.ParamCommonAsync;
import nts.uk.screen.at.app.dailyperformance.correction.month.asynctask.ProcessMonthScreen;
import nts.uk.screen.at.app.dailyperformance.correction.searchemployee.FindEmployeeBase;
import nts.uk.screen.at.app.dailyperformance.correction.selecterrorcode.DailyPerformanceErrorCodeProcessor;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author hungnm
 *
 */
@Path("screen/at/correctionofdailyperformance")
@Produces("application/json")
public class DailyPerformanceCorrectionWebService {

	@Inject
	private DailyPerformanceCorrectionProcessor processor;
	
	@Inject
	private DailyPerformanceErrorCodeProcessor errorProcessor;
	
	@Inject
	private DPUpdateColWidthCommandHandler commandHandler;
	
	@Inject
	private DataDialogWithTypeProcessor dialogProcessor;
	
	@Inject
	private DataDialogWithTypeProcessor dataDialogWithTypeProcessor;
	
	@Inject
	private PersonalTightCommandFacade personalTightCommandFacade;
	
	@Inject
	private CheckBeforeCalcFlex checkBeforeCalcFlex;
	
	@Inject
	private DPLoadRowProcessor loadRowProcessor;
	
	@Inject
	private DailyPerformErrorReferExportService dailyPerformErrorExportService;

	@Inject
	private DailyPerformErrorReferFinder dailyPerforErrorReferFinder;
	
	@Inject
	private FindEmployeeBase findEmployeeBase;
	
	@Inject
	private DailyCorrectCalcTimeService dailyCorrectCalcTimeService;
	
	@Inject
	private DailyCalculationRCommandFacade dailyCalculationService;
	
	@Inject
	private DisplayRemainingHolidayNumber remainNumberService;

	@Inject
	private DPDisplayLockProcessor dpDisplayLockProcessor;
	
	@Inject
	private DailyPerformanceAuthoritySetting dailyPerformanceAuthoritySetting;
	
	@Inject
	private MonthlyPerfomanceAuthorityFinder monthlyPerfomanceAuthorityFinder;
	
	@Inject
	private HttpSubSession session;
	
	@Inject
	private DPLoadVerProcessor dPLoadVerProcessor;
	
	@Inject
	private InfomationInitScreenProcess infomationInit;
	
	@Inject
	private ProcessMonthScreen processMonthScreen;
	
	@Inject
	private GenDateProcessor genDateProcessor;
	
	@Inject
	private DailyModifyRCommandFacade dailyModifyRCommandFacade;
	
	@Inject
	private DPCorrectionProcessorMob dpCorrectionProcessorMob;
	
	@Inject
	private SearchEmployeeService searchEmployeeService;
	
	@POST
	@Path("startScreen")
	public DailyPerformanceCorrectionDto startScreen(DPParams params ) throws InterruptedException{
		Integer closureId = params.closureId;
		DailyPerformanceCorrectionDto screenDto = (DailyPerformanceCorrectionDto) session.getAttribute("resultReturn");
		DailyPerformanceCorrectionDto dtoResult = this.processor.generateData(screenDto, params.lstEmployee, params.initScreen, params.mode, params.displayFormat, params.correctionOfDaily, params.formatCodes, params.showError, params.showLock, params.objectShare, closureId);
		session.setAttribute("domainOlds", dtoResult.getDomainOld());
		session.setAttribute("domainOldForLog", cloneListDto(dtoResult.getDomainOld()));
		session.setAttribute("domainEdits", null);
		session.setAttribute("itemIdRCs", dtoResult.getLstControlDisplayItem() == null ? null : dtoResult.getLstControlDisplayItem().getMapDPAttendance());
		session.setAttribute("dataSource", dtoResult.getLstData());
		session.setAttribute("closureId", dtoResult.getClosureId());
		session.setAttribute("resultReturn", null);
		session.setAttribute("approvalConfirm", dtoResult.getApprovalConfirmCache());
		dtoResult.setApprovalConfirmCache(null);
		dtoResult.setLstCellState(dtoResult.getMapCellState().values().stream().collect(Collectors.toList()));
		dtoResult.setMapCellState(null);
		removeSession();
		dtoResult.setDomainOld(Collections.emptyList());
		return dtoResult;
	}
	
	@POST
	@Path("errorCode")
	public DailyPerformanceCorrectionDto condition(DPParams params ) throws InterruptedException{
		Integer closureId = (Integer) session.getAttribute("closureId");
		val results = this.errorProcessor.generateData(params.dateRange, params.lstEmployee, params.initScreen, params.mode, params.displayFormat, params.correctionOfDaily, params.errorCodes, params.formatCodes, params.showLock, closureId);
		session.setAttribute("domainOlds", results.getDomainOld());
		session.setAttribute("domainOldForLog", cloneListDto(results.getDomainOld()));
		session.setAttribute("domainEdits", null);
		session.setAttribute("itemIdRCs", results.getLstControlDisplayItem() == null ? null : results.getLstControlDisplayItem().getMapDPAttendance());
		session.setAttribute("dataSource", results.getLstData());
		session.setAttribute("closureId", results.getClosureId());
		session.setAttribute("resultReturn", null);
		session.setAttribute("approvalConfirm", results.getApprovalConfirmCache());
		results.setApprovalConfirmCache(null);
		results.setLstCellState(results.getMapCellState().values().stream().collect(Collectors.toList()));
		results.setMapCellState(null);
		removeSession();
		results.setDomainOld(Collections.emptyList());
		return results;
	}
	
	@POST
	@Path("initParam")
	public DailyPerformanceCorrectionDto initScreen(DPParams params) throws InterruptedException{
		params.dpStateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		Pair<DailyPerformanceCorrectionDto, ParamCommonAsync> dtoResult = this.infomationInit.initGetParam(params);
		session.setAttribute("dpStateParam", dtoResult.getLeft().getStateParam());
		session.setAttribute("resultReturn", dtoResult.getLeft());
		session.setAttribute("resultMonthReturn", dtoResult.getRight());
		return dtoResult.getLeft();
	}
	
	@POST
	@Path("loadMonth")
	@SuppressWarnings("unchecked")
	public DailyPerformanceCorrectionDto loadMonth(MonthParamInit monthInit) throws InterruptedException{
		ParamCommonAsync paramCommonAsync = (ParamCommonAsync) session.getAttribute("resultMonthReturn");
		DPCorrectionStateParam stateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		Object objectCacheMonth = session.getAttribute("domainMonths");
		Optional<MonthlyRecordWorkDto> domainMonthOpt = objectCacheMonth == null ? Optional.empty()
				: (Optional<MonthlyRecordWorkDto>) objectCacheMonth;
		paramCommonAsync.setStateParam(stateParam);
		paramCommonAsync.setDomainMonthOpt(domainMonthOpt);
		paramCommonAsync.setLoadAfterCalc(monthInit.isLoadAfterCalc());
		DailyPerformanceCorrectionDto result = this.processMonthScreen.processMonth(paramCommonAsync);
		session.setAttribute("domainMonths", result.getMonthResult() == null ? null : result.getMonthResult().getDomainMonthOpt());
		//session.setAttribute("resultMonthReturn", null);
		return result;
	}
	
	@POST
	@Path("getErrors")
	public List<ErrorReferenceDto> getError(DPParams params ) {
		Integer closureId = (Integer) session.getAttribute("closureId");
		return this.processor.getListErrorRefer(params.dateRange, params.lstEmployee, closureId);
	}
	
	@POST
	@Path("updatecolumnwidth")
	public void getError(UpdateColWidthCommand command){
		this.commandHandler.handle(command);
	}
	
	@POST
	@Path("findCodeName")
	public CodeName findCodeName(DPParamDialog param){
		return this.dialogProcessor.getTypeDialog(param.getTypeDialog(), param.getParam());
	}
	
	@POST
	@Path("findAllCodeName")
	public List<CodeName> findAllCodeName(DPParamDialog param){
		return this.dialogProcessor.getAllTypeDialog(param.getTypeDialog(), param.getParam());
	}
	
	@POST
	@Path("addAndUpdate")
	@SuppressWarnings("unchecked")
	public DataResultAfterIU addAndUpdate(DPItemParent dataParent) {
		val domain  = session.getAttribute("domainEdits");
		List<DailyRecordDto> dailyEdits = new ArrayList<>();
		if(domain == null){
			dailyEdits = cloneListDto((List<DailyRecordDto>) session.getAttribute("domainOlds"));
		}else{
			dailyEdits = (List<DailyRecordDto>) domain;
		}
		dataParent.setDailyEdits(dailyEdits);
		dataParent.setDailyOlds((List<DailyRecordDto>) session.getAttribute("domainOlds"));
		dataParent.setDailyOldForLog((List<DailyRecordDto>) session.getAttribute("domainOldForLog"));
		dataParent.setLstAttendanceItem((Map<Integer, DPAttendanceItem>) session.getAttribute("itemIdRCs"));
		dataParent.setErrorAllSidDate((Boolean)session.getAttribute("errorAllCalc"));
		dataParent.setLstSidDateDomainError((List<Pair<String, GeneralDate>>)session.getAttribute("lstSidDateErrorCalc"));
		dataParent.setApprovalConfirmCache((ApprovalConfirmCache)session.getAttribute("approvalConfirm"));
		
		Object objectCacheMonth = session.getAttribute("domainMonths");
		Optional<MonthlyRecordWorkDto> domainMonthOpt = objectCacheMonth == null ? Optional.empty()
				: (Optional<MonthlyRecordWorkDto>) objectCacheMonth;
		dataParent.setDomainMonthOpt(domainMonthOpt);
		DataResultAfterIU dataResultAfterIU =  dailyModifyRCommandFacade.insertItemDomain(dataParent);
		//TODO: set cache month
		if(dataResultAfterIU.getDomainMonthOpt().isPresent()) {
			session.setAttribute("domainMonths", dataResultAfterIU.getDomainMonthOpt());
		}

		session.setAttribute("lstSidDateErrorCalc", dataResultAfterIU.getLstSidDateDomainError());
		session.setAttribute("errorAllCalc", dataResultAfterIU.isErrorAllSidDate());
		return dataResultAfterIU;
	}
	
	@POST
	@Path("insertClosure")
	public void insertClosure(EmpAndDate empAndDate){
		ApprovalConfirmCache approvalConfirmCache = (ApprovalConfirmCache)session.getAttribute("approvalConfirm");
		DPCorrectionStateParam stateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		ApprovalConfirmCache result = personalTightCommandFacade.addRemovePersonalTight(empAndDate.getEmployeeId(), empAndDate.getDate(), approvalConfirmCache, stateParam, true);
		session.setAttribute("approvalConfirm", result);
	}
	
	@POST
	@Path("releaseClosure")
	public JavaTypeResult<String> releaseClosure(EmpAndDate empAndDate){
		ApprovalConfirmCache approvalConfirmCache = (ApprovalConfirmCache)session.getAttribute("approvalConfirm");
		DPCorrectionStateParam stateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		ApprovalConfirmCache result = personalTightCommandFacade.addRemovePersonalTight(empAndDate.getEmployeeId(), empAndDate.getDate(), approvalConfirmCache, stateParam, false);
		session.setAttribute("approvalConfirm", result);
		return new JavaTypeResult<String>("");
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    final Set<Object> seen = new HashSet<>();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
	@POST
	@Path("getApplication")
	public List<Integer> getApplicationName() {
		return dataDialogWithTypeProcessor.getNameAppliction();
	}
	
	@POST
	@Path("getFlexCheck")
	public JavaTypeResult<String>  getValueTimeFlex(CalcFlexDto calc) {
		return new JavaTypeResult<String>(checkBeforeCalcFlex.getConditionCalcFlex(calc));
	}
	
	@POST
	@Path("loadRow")
	@SuppressWarnings("unchecked")
	public DailyPerformanceCorrectionDto reloadRow(DPPramLoadRow param) {
		val domain  = session.getAttribute("domainEdits");
		List<DailyRecordDto> dailyEdits = new ArrayList<>();
		if(domain == null){
			dailyEdits = (List<DailyRecordDto>) session.getAttribute("domainOlds");
		}else{
			dailyEdits = (List<DailyRecordDto>) domain;
		}
		param.setDailys(dailyEdits);
		param.setLstSidDateDomainError((List<Pair<String, GeneralDate>>)session.getAttribute("lstSidDateErrorCalc"));
		param.setErrorAllSidDate((Boolean)session.getAttribute("errorAllCalc"));
		Integer closureId = (Integer) session.getAttribute("closureId");
		param.setClosureId(closureId);
		param.setApprovalConfirmCache((ApprovalConfirmCache)session.getAttribute("approvalConfirm"));
		DPCorrectionStateParam stateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		param.setStateParam(stateParam);
		
		Object objectCacheMonth = session.getAttribute("domainMonths");
		Optional<MonthlyRecordWorkDto> domainMonthOpt = objectCacheMonth == null ? Optional.empty()
				: (Optional<MonthlyRecordWorkDto>) objectCacheMonth;
		param.setDomainMonthOpt(domainMonthOpt);
		val result = loadRowProcessor.reloadGrid(param);
		if(!param.getOnlyLoadMonth()) {
			session.setAttribute("approvalConfirm", result.getApprovalConfirmCache());
			session.setAttribute("domainOlds", result.getDomainOld());
			session.setAttribute("domainOldForLog", result.getDomainOldForLog());
		}
		result.setApprovalConfirmCache(null);
		result.setDomainOld(Collections.emptyList());
		session.setAttribute("domainEdits", null);
		result.setLstCellState(result.getMapCellState().values().stream().collect(Collectors.toList()));
		result.setMapCellState(null);
		return result;
	}

	@POST
	@Path("loadVerData")
	@SuppressWarnings("unchecked")
	public LoadVerDataResult addAndUpdate(LoadVerData loadVerData) {
		val domain = session.getAttribute("domainEdits");
		List<DailyRecordDto> dailyEdits = new ArrayList<>();
		if (domain == null) {
			dailyEdits = (List<DailyRecordDto>) session.getAttribute("domainOlds");
		} else {
			dailyEdits = (List<DailyRecordDto>) domain;
		}
		loadVerData.setLstDomainOld(dailyEdits);
		loadVerData.setApprovalConfirmCache((ApprovalConfirmCache)session.getAttribute("approvalConfirm"));
		DPCorrectionStateParam stateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		loadVerData.setStateParam(stateParam);
		val result = dPLoadVerProcessor.loadVerAfterCheckbox(loadVerData);
		session.setAttribute("domainEdits", null);
		session.setAttribute("domainOlds", result.getLstDomainOld());
		result.setLstDomainOld(new ArrayList<>());
		session.setAttribute("approvalConfirm", result.getApprovalConfirmCache());
		result.setApprovalConfirmCache(null);
		return result;
	}
	
	@POST
	@Path("exportCsv")
	public ExportServiceResult exportCsvErrorInfor(List<DailyPerformErrorReferExportDto> command) {
		return this.dailyPerformErrorExportService.start(command);
	}

	@POST
	@Path("getErrAndAppTypeCd")
	public DailyPerformErrorReferDto findByCidAndListErrCd(List<String> listErrorCode) {
		return this.dailyPerforErrorReferFinder.findByCidAndListErrCd(listErrorCode);
	}
	
	/**
	 * typeOfAttendanceItem = 0 to case is monthly
	 * 
	 * @param dailyAttendanceItemIds
	 * @return
	 */
	@POST
	@Path("getNameMonthlyAttItem")
	public List<AttItemName> getNameOfMonthlyAttendanceItem(List<Integer> monthlyAttendanceItemIds) {
		return this.monthlyPerfomanceAuthorityFinder.getListAttendanceItemName(monthlyAttendanceItemIds);
	}
	
	@POST
	@Path("getnameattItembytype/{type}")
	public List<AttItemName> getNameOfAttendanceItemByType(@PathParam(value = "type") int type) {
		return this.monthlyPerfomanceAuthorityFinder.getListAttendanceItemNameByType(type);
	}
	
	@POST
	@Path("getNamedailyAttItem")
	public List<AttItemName> getNameOfDailyAttendanceItem(List<Integer> dailyAttendanceItemIds) {
		return this.dailyPerformanceAuthoritySetting.getListAttendanceItemName(dailyAttendanceItemIds);
	}
	

	@POST
	@Path("get-info/{employeeCode}")
	public EmployeeSearchData getInfo(@PathParam(value = "employeeCode") String employeeCode) {
		EmployeeSearchDto dto = new EmployeeSearchDto();
		dto.setBaseDate(GeneralDate.today());
		dto.setSystem("1");
		dto.setEmployeeCode(employeeCode);
		return this.searchEmployeeService.searchByCode(dto);
	}
	
	@POST
	@Path("calcTime")
	@SuppressWarnings("unchecked")
	public DCCalcTime calcTime(DCCalcTimeParam dcTimeParam) {
		val domain  = session.getAttribute("domainEdits");
		List<DailyRecordDto> dailyEdits = new ArrayList<>();
		if(domain == null){
			dailyEdits = cloneListDto((List<DailyRecordDto>) session.getAttribute("domainOlds"));
		}else{
			dailyEdits = (List<DailyRecordDto>) domain;
		}
		
		val result = dailyCorrectCalcTimeService.calcTime(dailyEdits, dcTimeParam.getItemEdits(), dcTimeParam.getChangeSpr31(), dcTimeParam.getChangeSpr34(), dcTimeParam.isNotChangeCell());
		session.setAttribute("domainEdits", result.getDailyEdits());
		result.setDailyEdits(Collections.emptyList());
		return result;
	}
	
	@POST
	@Path("calculation")
	@SuppressWarnings("unchecked")
	public DailyPerformanceCalculationDto calculation(DPItemParent dataParent) {
		val domain  = session.getAttribute("domainEdits");
		List<DailyRecordDto> dailyEdits = new ArrayList<>();
		if(domain == null){
			dailyEdits = cloneListDto((List<DailyRecordDto>) session.getAttribute("domainOlds"));
		}else{
			dailyEdits = (List<DailyRecordDto>) domain;
		}
		dataParent.setDailyEdits(dailyEdits);
		dataParent.setDailyOlds((List<DailyRecordDto>) session.getAttribute("domainOlds"));
		
		dataParent.setLstAttendanceItem((Map<Integer, DPAttendanceItem>) session.getAttribute("itemIdRCs"));
		dataParent.setLstData((List<DPDataDto>) session.getAttribute("dataSource"));
		Object objectCacheMonth = session.getAttribute("domainMonths");
		Optional<MonthlyRecordWorkDto> domainMonthOpt = objectCacheMonth == null ? Optional.empty()
				: (Optional<MonthlyRecordWorkDto>) objectCacheMonth;
		dataParent.setDomainMonthOpt(domainMonthOpt);

		ParamCommonAsync paramCommonAsync = (ParamCommonAsync) session.getAttribute("resultMonthReturn");
		dataParent.setParamCommonAsync(paramCommonAsync);
		val result = dailyCalculationService.calculateCorrectedResults(dataParent);

		session.setAttribute("domainEdits", result.getCalculatedRows());
		session.setAttribute("lstSidDateErrorCalc", result.getLstSidDateDomainError());
		session.setAttribute("errorAllCalc", result.isErrorAllSidDate());
		result.setCalculatedRows(Collections.emptyList());
		return result;
	}
	
	@POST
	@Path("getRemainNum")
	public HolidayRemainNumberDto getRemainNumb(HolidayRemainParam param) {
		return remainNumberService.getRemainingHolidayNumber(param.getEmployeeId(), param.getClosureDate());
	}

	@POST
	@Path("lock")
	public DailyPerformanceCorrectionDto processLock(DPDisplayLockParam param) {
		Integer closureId = (Integer) session.getAttribute("closureId");
		param.setClosureId(closureId);
		DPCorrectionStateParam stateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		param.setStateParam(stateParam);
		DailyPerformanceCorrectionDto dtoResult = dpDisplayLockProcessor.processDisplayLock(param);
		dtoResult.setLstCellState(dtoResult.getMapCellState().values().stream().collect(Collectors.toList()));
		dtoResult.setMapCellState(null);
		return dtoResult;
	}

	@POST
	@Path("gendate")
	public DatePeriodInfo genDateFromYM(GenDateDto param) {
		DPCorrectionStateParam stateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		DatePeriodInfo dateInfo = genDateProcessor.genDateFromYearMonth(param);
		if (stateParam != null && dateInfo != null) {
			stateParam.setDateInfo(dateInfo);
			session.setAttribute("dpStateParam", stateParam);
		}
		return dateInfo;
	}
	
	private List<DailyRecordDto> cloneListDto(List<DailyRecordDto> dtos){
		if(dtos == null) return new ArrayList<>();
		return dtos.stream().map(x -> x.clone()).collect(Collectors.toList());
	}
	
	private void removeSession() {
		session.setAttribute("lstSidDateErrorCalc", Collections.emptyList());
		session.setAttribute("errorAllCalc", false);
	}
	
	@POST
	@Path("getErrorMobile")
	public List<ErAlWorkRecordShortDto> getErrorMobile(ErrorParam errorParam) {
		return dpCorrectionProcessorMob.getErrorMobile(
				new DatePeriod(errorParam.getStartDate(), errorParam.getEndDate()), 
				errorParam.getEmployeeIDLst(), 
				errorParam.getAttendanceItemID());
	}
	
	@POST
	@Path("getMasterDialogMob")
	public Map<Integer, List<CodeName>> getMasterDialog(MasterDialogParam masterDialogParam) {
		String companyID = AppContexts.user().companyId();
		Map<Integer, Map<String, CodeName>> allMasterData = dialogProcessor.getAllCodeNameWT(
				masterDialogParam.getTypes(),
				companyID,
				masterDialogParam.getEmployeeID(),
				masterDialogParam.getWorkTypeCD(),
				masterDialogParam.getDate());
		return allMasterData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<CodeName>(entry.getValue().values())));
	}
	
	@POST
	@Path("getPrimitiveAll")
	public Map<Integer, String> getPrimitiveAll() {
		return DPHeaderDto.getPrimitiveAll();
	}
}
