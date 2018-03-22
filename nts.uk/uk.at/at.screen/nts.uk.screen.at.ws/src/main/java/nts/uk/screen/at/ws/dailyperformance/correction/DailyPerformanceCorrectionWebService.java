/**
 * 5:57:43 PM Aug 28, 2017
 */
package nts.uk.screen.at.ws.dailyperformance.correction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.enums.EnumConstant;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ValueType;
import nts.uk.screen.at.app.dailymodify.command.DailyModifyCommandFacade;
import nts.uk.screen.at.app.dailymodify.command.PersonalTightCommandFacade;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyQuery;
import nts.uk.screen.at.app.dailyperformance.correction.DPUpdateColWidthCommandHandler;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceCorrectionProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.UpdateColWidthCommand;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.ValidatorDataDaily;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.ParamDialog;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemParent;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.EmpAndDate;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ErrorReferenceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.type.TypeLink;
import nts.uk.screen.at.app.dailyperformance.correction.selecterrorcode.DailyPerformanceErrorCodeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.selectitem.DailyPerformanceSelectItemProcessor;

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
	private DailyPerformanceSelectItemProcessor selectProcessor;
	
	@Inject
	private DPUpdateColWidthCommandHandler commandHandler;
	
	@Inject
	private DataDialogWithTypeProcessor dialogProcessor;
	
	@Inject
	private DailyModifyCommandFacade dailyModifyCommandFacade;
	
	@Inject
	private DataDialogWithTypeProcessor dataDialogWithTypeProcessor;
	
	@Inject
	private ValidatorDataDaily validatorDataDaily;
	
	@Inject
	private PersonalTightCommandFacade personalTightCommandFacade;
	
	@POST
	@Path("startScreen")
	public DailyPerformanceCorrectionDto startScreen(DPParams params ) throws InterruptedException{
		return this.processor.generateData(params.dateRange, params.lstEmployee, params.initScreen, params.displayFormat, params.correctionOfDaily, params.formatCodes);
	}
	
	@POST
	@Path("errorCode")
	public DailyPerformanceCorrectionDto condition(DPParams params ) throws InterruptedException{
		return this.errorProcessor.generateData(params.dateRange, params.lstEmployee, params.initScreen, params.displayFormat, params.correctionOfDaily, params.errorCodes, params.formatCodes);
	}
	
	@POST
	@Path("selectCode")
	public DailyPerformanceCorrectionDto selectFormatCode(DPParams params ) throws InterruptedException{
		return this.selectProcessor.generateData(params.dateRange, params.lstEmployee, params.initScreen, params.displayFormat, params.correctionOfDaily, params.formatCodes);
	}
	
	@POST
	@Path("getErrors")
	public List<ErrorReferenceDto> getError(DPParams params ) {
		return this.processor.getListErrorRefer(params.dateRange, params.lstEmployee);
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
	public Map<Integer, List<DPItemValue>> addAndUpdate(DPItemParent dataParent) {
		Map<Integer, List<DPItemValue>> resultError = new HashMap<>();
		List<DPItemValue> itemValueChild= dataParent.getItemValues().stream().map(x -> {
			DPItemValue item = x;
			if (x.getTypeGroup() == TypeLink.POSSITION.value) {
				CodeName codeName = dataDialogWithTypeProcessor.getTypeDialog(x.getTypeGroup(),
						new ParamDialog(x.getDate(), x.getValue()));
				item.setValue(codeName == null ? null : codeName.getId());
				return item;
			} else if (x.getTypeGroup() == TypeLink.WORKPLACE.value) {
				CodeName codeName = dataDialogWithTypeProcessor.getTypeDialog(x.getTypeGroup(),
						new ParamDialog(x.getDate(), x.getValue()));
				item.setValue(codeName == null ? null : codeName.getId());
				return item;
			}
			return item;
		}).collect(Collectors.toList());
		Map<Pair<String, GeneralDate>, List<DPItemValue>> mapSidDate = itemValueChild.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.getEmployeeId(), x.getDate())));
		// check error care item
		List<DPItemValue> itemErrors = new ArrayList<>();
		List<DPItemValue> itemInputErors = new ArrayList<>();
		mapSidDate.entrySet().forEach(x -> {
			List<DPItemValue> itemCovert = x.getValue().stream().filter(y -> y.getValue() != null)
					.collect(Collectors.toList()).stream().filter(distinctByKey(p -> p.getItemId()))
					.collect(Collectors.toList());
			List<DPItemValue> items = validatorDataDaily.checkCareItemDuplicate(itemCovert);
			if (!items.isEmpty()){
				itemErrors.addAll(items);
			}else{
				//List<DPItemValue> itemInputs = validatorDataDaily.checkCareInputData(itemCovert);
				//itemInputErors.addAll(itemInputs);
			}
			
		});
		if (itemErrors.isEmpty()) {
			if (itemInputErors.isEmpty()) {
				mapSidDate.entrySet().forEach(x -> {
					List<ItemValue> itemCovert = x.getValue().stream()
							.map(y -> new ItemValue(y.getValue(), ValueType.valueOf(y.getValueType()),
									y.getLayoutCode(), y.getItemId()))
							.collect(Collectors.toList()).stream().filter(distinctByKey(p -> p.itemId()))
							.collect(Collectors.toList());
					if (!itemCovert.isEmpty())
						dailyModifyCommandFacade.handleUpdate(new DailyModifyQuery(x.getValue().get(0).getEmployeeId(),
								x.getValue().get(0).getDate(), itemCovert));
				});
				// insert cell edit
				dailyModifyCommandFacade.handleEditCell(itemValueChild);
			}else{
				//resultError.put(1, itemInputErors);
				//return resultError;
			}
		}else{
			resultError.put(0, itemErrors);
			//return resultError;
		}
		
		// insert sign
		dailyModifyCommandFacade.insertSign(dataParent.getDataCheckSign());
		
		//
		if(dataParent.getMode() == 0){
			val dataCheck = validatorDataDaily.checkContinuousHolidays(dataParent.getEmployeeId(),
					dataParent.getDateRange());
			if (!dataCheck.isEmpty()) {
				resultError.put(2, dataCheck);
			}
		}
		return resultError;
	}
	
	@POST
	@Path("insertClosure")
	public void insertClosure(EmpAndDate empAndDate){
		personalTightCommandFacade.insertPersonalTight(empAndDate.getEmployeeId(), empAndDate.getDate());
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    final Set<Object> seen = new HashSet<>();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
	@POST
	@Path("getApplication")
	public List<EnumConstant> getApplicationName(List<String> errorCodes) {
		return dataDialogWithTypeProcessor.getNameAppliction(errorCodes);
	}
}
