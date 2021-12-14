package nts.uk.ctx.pereg.app.command.process.checkdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nts.arc.task.data.TaskDataSetter;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.GridLayoutPersonInfoClsDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoClsDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoValueDto;
import nts.uk.ctx.pereg.dom.person.info.category.CategoryType;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.item.PersonInfoItemDefinition;
import nts.uk.ctx.pereg.dom.person.setting.validatecheck.PerInfoValidChkCtgRepository;
import nts.uk.ctx.pereg.dom.person.setting.validatecheck.PerInfoValidateCheckCategory;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.find.PeregEmpInfoQuery;


@Stateless
public class CheckPersonInfoProcess {
	
	final String chekPersonInfoType = TextResource.localize("CPS013_5");
   
	final List<String> lisCategorythistory = Arrays.asList("CS00003", "CS00004", "CS00014",
			"CS00016", "CS00017", "CS00018", "CS00019", "CS00020", "CS00021", "CS00070");
	
	final List<String> listPersistentResidentHistoryCtg = Arrays.asList("CS00004","CS00014","CS00016","CS00017","CS00020","CS00070");
	
	final List<String> listPersistentResidentHisAndPersistentHisCtg = Arrays.asList("CS00004","CS00014","CS00016","CS00017","CS00020","CS00021","CS00070");
	
	final List<String> listItemToCheckSpace =  Arrays.asList("IS00003","IS00004","IS00015","IS00016");
	
	final List<String> listItem_Master_History = Arrays.asList("IS00084", "IS00085", "IS00079", "IS00073", "IS00131",
			"IS00140", "IS00158", "IS00167", "IS00176", "IS00149", "IS00194", "IS00203", "IS00212", "IS00221",
			"IS00230", "IS00239", "IS00185");
	
	private static final List<String> standardDateItemCodes = Arrays.asList("IS00020", "IS00077", "IS00082", "IS00119", "IS00781");
	
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;
	@Inject
	private BasicScheduleService basicService;
	@Inject
	private PerInfoValidChkCtgRepository perInfoCheckCtgRepo;
	
	private static final String JP_SPACE = "　";
	
	// 個人基本情報チェック (Check thông tin cá nhân cơ bản)
	public void checkPersonInfo(PeregEmpInfoQuery empCheck, Map<String, List<GridLayoutPersonInfoClsDto>> dataOfEmployee,
			CheckDataFromUI excuteCommand,Map<PersonInfoCategory, List<PersonInfoItemDefinition>> mapCategoryWithListItemDf, EmployeeDataMngInfo employee, String bussinessName, TaskDataSetter dataSetter, List<ErrorInfoCPS013> listError) {
		
		// アルゴリズム「履歴整合性チェック」を実行する (Thực thi thuật toán 「Check tính hợp lệ của lịch sử」)
		checkCategoryHistory(empCheck, dataOfEmployee, excuteCommand, mapCategoryWithListItemDf, dataSetter, employee, bussinessName, listError);
		
		// システム必須チェック (Kiểm tra required system)
		checkSystemRequired(empCheck, dataOfEmployee, excuteCommand, mapCategoryWithListItemDf, dataSetter, employee, bussinessName, listError);
		
		// 参照項目チェック処理 (Check item tham chiếu)
		checkReferenceItem(empCheck, dataOfEmployee, excuteCommand, mapCategoryWithListItemDf, dataSetter, employee, bussinessName, listError);
		
		//単一項目チェック(Check single item )
		checkSingleItem(empCheck, dataOfEmployee, excuteCommand, mapCategoryWithListItemDf, dataSetter, employee, bussinessName, listError);
		
	}

	private void checkSingleItem(PeregEmpInfoQuery empCheck,
			Map<String, List<GridLayoutPersonInfoClsDto>> dataOfEmployee, CheckDataFromUI excuteCommand,
			Map<PersonInfoCategory, List<PersonInfoItemDefinition>> mapCategoryWithListItemDf,
			TaskDataSetter dataSetter, EmployeeDataMngInfo employee, String bussinessName,
			List<ErrorInfoCPS013> listError) {
		
		List<PersonInfoCategory> listCategory = new ArrayList<>(mapCategoryWithListItemDf.keySet());
		
		Optional<PersonInfoCategory> isExitCS00020 = listCategory.stream().filter( ctg -> ctg.getCategoryCode().v().equals("CS00020")).findFirst();
		Optional<PersonInfoCategory> isExitCS00070 = listCategory.stream().filter( ctg -> ctg.getCategoryCode().v().equals("CS00070")).findFirst();
		List<LayoutPersonInfoValueDto> itemsOfWorkingCondition1 = new ArrayList<>();
		List<LayoutPersonInfoValueDto> itemsOfWorkingCondition2 = new ArrayList<>();
		List<LayoutPersonInfoValueDto> itemsIS00017 = new ArrayList<>();
		List<LayoutPersonInfoValueDto> itemsIS00020 = new ArrayList<>();
		mapCategoryWithListItemDf.entrySet().stream().forEach(c ->{
			PersonInfoCategory category =  c.getKey();
			List<String> itemCodeSingle = c.getValue().stream().filter(i -> i.isSingleItem())
					.map(i -> i.getItemCode().v()).collect(Collectors.toList());
			List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg = dataOfEmployee.get(category.getCategoryCode().v());
			listDataEmpOfCtg.forEach(data -> {
				List<LayoutPersonInfoValueDto> items = new ArrayList<>();
				itemsOfWorkingCondition1.removeAll(itemsOfWorkingCondition1);
				itemsOfWorkingCondition2.removeAll(itemsOfWorkingCondition2);
				List<LayoutPersonInfoClsDto> layoutDtos = data.getLayoutDtos();
				layoutDtos.forEach(item -> {
					items.addAll(item.getItems());
					if (category.getCategoryCode().v().equals("CS00020")){
						itemsOfWorkingCondition1.addAll(item.getItems());
					}
					if (category.getCategoryCode().v().equals("CS00070")) {
						itemsOfWorkingCondition2.addAll(item.getItems());
					}
					
					if (category.getCategoryCode().v().equals("CS00002")) {
						Optional<LayoutPersonInfoValueDto> itemIS00017Opt = item.getItems().stream()
								.filter(i -> i.getItemCode().equals("IS00017")).findFirst();
						if (itemIS00017Opt.isPresent()) {
							itemsIS00017.add(itemIS00017Opt.get());
						}
					}
					
					if (category.getCategoryCode().v().equals("CS00003")) {
						Optional<LayoutPersonInfoValueDto> IS00020Opt = item.getItems().stream()
								.filter(i -> i.getItemCode().equals("IS00020")).findFirst();
						if (IS00020Opt.isPresent()) {
								itemsIS00020.add(IS00020Opt.get());
						}
					}
					
				});
				
				if(category.getCategoryCode().v().equals("CS00020")) {
					// アルゴリズム「就業時間帯の必須チェック」を実行する (Thực hiện thuật toán [check required của worktime])
					checkRequiredWorktime1(itemsOfWorkingCondition1, employee, isExitCS00020.get(), bussinessName, dataSetter, listError);
				}
				
				if(category.getCategoryCode().v().equals("CS00070")) {
					checkRequiredWorktime2(itemsOfWorkingCondition2, employee, isExitCS00070.get(), bussinessName, dataSetter, listError);
				}
				
				items.forEach(item -> {
					// 必須項目のNULLチェック (Check Null cho các requiredItem)
					if(item.isRequired() && itemCodeSingle.contains(item.getItemCode())){
						checkNullOfRequiredItems(item, employee, category, bussinessName, dataSetter, listError);
					}
				});
				
				// 項目の特殊チェック (Check item đặc biệt)
				if (category.getCategoryCode().v().equals("CS00002")) {
					checkItemSpecial(items, employee, category, bussinessName, dataSetter, listError);
				}
				
				// 日付の逆転チェック (Check đảo ngược date )
				if (lisCategorythistory.contains(category.getCategoryCode().v())) {
					checkDateReversalOfHistories(items, employee, category, bussinessName, dataSetter, listError); 
				}
				
			});

		});
		
		if(!itemsIS00017.isEmpty() && !itemsIS00020.isEmpty()) {
			LayoutPersonInfoValueDto itemIS00020 = getMinValue(itemsIS00020);
			LayoutPersonInfoValueDto itemIS00017 = itemsIS00017.get(0);
			checkDateReversalOfSpecialItem(itemIS00017, itemIS00020, employee, bussinessName, dataSetter, listError);
		}
	}
	
	private LayoutPersonInfoValueDto getMinValue(List<LayoutPersonInfoValueDto> IS00020s) {
		LayoutPersonInfoValueDto itemMin = IS00020s.get(0);
		GeneralDate min = (GeneralDate) itemMin.getValue();
		for(int i = 0; i < IS00020s.size(); i++) {
			LayoutPersonInfoValueDto item = IS00020s.get(i);
			GeneralDate a = (GeneralDate) item.getValue();
			if(a.compareTo(min) < 0) {
				min = a;
				itemMin = IS00020s.get(i);
			}
		}
		return itemMin;
	}

	private void checkRequiredWorktime2(List<LayoutPersonInfoValueDto> itemsOfWorkingCondition2,
			EmployeeDataMngInfo employee, PersonInfoCategory category, String bussinessName,
			TaskDataSetter dataSetter, List<ErrorInfoCPS013> listError) {
		
		Optional<LayoutPersonInfoValueDto> IS00184Opt = itemsOfWorkingCondition2.stream().filter(item -> item.getItemCode().equals("IS00184")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00193Opt = itemsOfWorkingCondition2.stream().filter(item -> item.getItemCode().equals("IS00193")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00202Opt = itemsOfWorkingCondition2.stream().filter(item -> item.getItemCode().equals("IS00202")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00211Opt = itemsOfWorkingCondition2.stream().filter(item -> item.getItemCode().equals("IS00211")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00220Opt = itemsOfWorkingCondition2.stream().filter(item -> item.getItemCode().equals("IS00220")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00229Opt = itemsOfWorkingCondition2.stream().filter(item -> item.getItemCode().equals("IS00229")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00238Opt = itemsOfWorkingCondition2.stream().filter(item -> item.getItemCode().equals("IS00238")).findFirst();
		
		
		if (IS00184Opt.isPresent()) {
			checkWorkTime(IS00184Opt.get(),"IS00185", "IS00187", "IS00188" ,itemsOfWorkingCondition2,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00193Opt.isPresent()) {
			checkWorkTime(IS00193Opt.get(),"IS00194", "IS00196", "IS00197" ,itemsOfWorkingCondition2,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00202Opt.isPresent()) {
			checkWorkTime(IS00202Opt.get(),"IS00203", "IS00205", "IS00206" ,itemsOfWorkingCondition2,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00211Opt.isPresent()) {
			checkWorkTime(IS00211Opt.get(),"IS00212", "IS00214", "IS00215" ,itemsOfWorkingCondition2,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00220Opt.isPresent()) {
			checkWorkTime(IS00220Opt.get(),"IS00221", "IS00223", "IS00224" ,itemsOfWorkingCondition2,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00229Opt.isPresent()) {
			checkWorkTime(IS00229Opt.get(),"IS00230", "IS00232", "IS00233" ,itemsOfWorkingCondition2,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00238Opt.isPresent()) {
			checkWorkTime(IS00238Opt.get(),"IS00239", "IS00241", "IS00242" ,itemsOfWorkingCondition2,  employee, category, bussinessName, dataSetter, listError);
		}
	}

	private void checkRequiredWorktime1(List<LayoutPersonInfoValueDto> itemsOfWorkingCondition1,
			EmployeeDataMngInfo employee, PersonInfoCategory category, String bussinessName, TaskDataSetter dataSetter,
			List<ErrorInfoCPS013> listError) {
		
		Optional<LayoutPersonInfoValueDto> IS00148Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals("IS00148")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00157Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals("IS00157")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00166Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals("IS00166")).findFirst();
		Optional<LayoutPersonInfoValueDto> IS00175Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals("IS00175")).findFirst();
		
		if (IS00148Opt.isPresent()) {
			checkWorkTime(IS00148Opt.get(),"IS00149", "IS00151", "IS00152" ,itemsOfWorkingCondition1,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00157Opt.isPresent()) {
			checkWorkTime(IS00157Opt.get(),"IS00158", "IS00160", "IS00161" ,itemsOfWorkingCondition1,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00166Opt.isPresent()) {
			checkWorkTime(IS00166Opt.get(),"IS00167", "IS00169", "IS00170" ,itemsOfWorkingCondition1,  employee, category, bussinessName, dataSetter, listError);
		}
		
		if (IS00175Opt.isPresent()) {
			checkWorkTime(IS00175Opt.get(),"IS00176", "IS00178", "IS00179" ,itemsOfWorkingCondition1,  employee, category, bussinessName, dataSetter, listError);
		}
	}

	
	
	private void checkWorkTime(LayoutPersonInfoValueDto itemWorkType, String itemCdWorkTime, String itemCdStartDate1,
			String itemCdEndDate1, List<LayoutPersonInfoValueDto> itemsOfWorkingCondition1,
			EmployeeDataMngInfo employee, PersonInfoCategory category, String bussinessName, TaskDataSetter dataSetter,
			List<ErrorInfoCPS013> listError) {
		String workTypeCD = (String) itemWorkType.getValue();
		//就業時間帯の必須チェック
		SetupType workTimeSetting = basicService.checkNeededOfWorkTimeSetting(workTypeCD);
		if (workTimeSetting == SetupType.REQUIRED) {
			Optional<LayoutPersonInfoValueDto> itemWorkTimeOpt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals(itemCdWorkTime)).findFirst();
			if (itemWorkTimeOpt.isPresent()) {
				Object itemWorkTime = itemWorkTimeOpt.get().getValue();
				if (itemWorkTime == null) {
					ErrorInfoCPS013 error_workTime = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
							chekPersonInfoType, category.getCategoryName().v(),
							TextResource.localize("Msg_956", itemWorkType.getItemName(), (String)itemWorkType.getValue(), itemWorkTimeOpt.get().getItemName() ));
					listError.add(error_workTime);
					setErrorDataGetter(error_workTime, dataSetter);
				}else {
					// check startTime 1
					Optional<LayoutPersonInfoValueDto> IS00151_StartTime1_Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals(itemCdStartDate1)).findFirst();
					if (IS00151_StartTime1_Opt.isPresent()) {
						Object IS00151_StartTime1 = IS00151_StartTime1_Opt.get().getValue();
						if (IS00151_StartTime1 == null ) {
							ErrorInfoCPS013 error_StartTime1 = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
									chekPersonInfoType, category.getCategoryName().v(),
									TextResource.localize("Msg_956", itemWorkType.getItemName(), (String)itemWorkType.getValue(), IS00151_StartTime1_Opt.get().getItemName() ));
							listError.add(error_StartTime1);
							setErrorDataGetter(error_StartTime1, dataSetter);
						}
					}
					
					// check endTime 1
					Optional<LayoutPersonInfoValueDto> IS00152_EndTime1_Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals(itemCdEndDate1)).findFirst();
					if (IS00152_EndTime1_Opt.isPresent()) {
						Object IS00152_EndTime1 = IS00152_EndTime1_Opt.get().getValue();
						if (IS00152_EndTime1 == null ) {
							ErrorInfoCPS013 error_EndTime1 = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
									chekPersonInfoType, category.getCategoryName().v(),
									TextResource.localize("Msg_956", itemWorkType.getItemName(), (String)itemWorkType.getValue(), IS00152_EndTime1_Opt.get().getItemName() ));
							listError.add(error_EndTime1);
							setErrorDataGetter(error_EndTime1, dataSetter);
						}
					}
				}
			}
		} else if(workTimeSetting == SetupType.NOT_REQUIRED){

			Optional<LayoutPersonInfoValueDto> itemWorkTimeOpt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals(itemCdWorkTime)).findFirst();
			if (itemWorkTimeOpt.isPresent()) {
				// check workTime
				Object itemWorkTime = itemWorkTimeOpt.get().getValue();
				if (itemWorkTime != null) {
					ErrorInfoCPS013 error_workTime = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
							chekPersonInfoType, category.getCategoryName().v(),
							TextResource.localize("Msg_956", itemWorkType.getItemName(), (String)itemWorkType.getValue(), itemWorkTimeOpt.get().getItemName() ));
					listError.add(error_workTime);
					setErrorDataGetter(error_workTime, dataSetter);
				}else {
					// check startTime 1
					Optional<LayoutPersonInfoValueDto> IS00151_StartTime1_Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals(itemCdStartDate1)).findFirst();
					if (IS00151_StartTime1_Opt.isPresent()) {
						Object IS00151_StartTime1 = IS00151_StartTime1_Opt.get().getValue();
						if (IS00151_StartTime1 != null ) {
							ErrorInfoCPS013 error_StartTime1 = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
									chekPersonInfoType, category.getCategoryName().v(),
									TextResource.localize("Msg_956", itemWorkType.getItemName(), (String)itemWorkType.getValue(), IS00151_StartTime1_Opt.get().getItemName() ));
							listError.add(error_StartTime1);
							setErrorDataGetter(error_StartTime1, dataSetter);
						}
					}
					
					// check endTime 1
					Optional<LayoutPersonInfoValueDto> IS00152_EndTime1_Opt = itemsOfWorkingCondition1.stream().filter(item -> item.getItemCode().equals(itemCdEndDate1)).findFirst();
					if (IS00152_EndTime1_Opt.isPresent()) {
						Object IS00152_EndTime1 = IS00152_EndTime1_Opt.get().getValue();
						if (IS00152_EndTime1 != null ) {
							ErrorInfoCPS013 error_EndTime1 = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
									chekPersonInfoType, category.getCategoryName().v(),
									TextResource.localize("Msg_956", itemWorkType.getItemName(), (String)itemWorkType.getValue(), IS00152_EndTime1_Opt.get().getItemName() ));
							listError.add(error_EndTime1);
							setErrorDataGetter(error_EndTime1, dataSetter);
						}
					}
				}
			}
		}
	}

	private void checkDateReversalOfHistories(List<LayoutPersonInfoValueDto> items, EmployeeDataMngInfo employee,
			PersonInfoCategory category, String bussinessName, TaskDataSetter dataSetter, List<ErrorInfoCPS013> listError) {
		switch (category.getCategoryCode().v()) {
		case "CS00003":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00020" , "IS00021", listError);
			break;
		case "CS00004":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00026" , "IS00027", listError);
			break;
		case "CS00014":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00066" , "IS00067", listError);
			break;
		case "CS00015":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00071" , "IS00072", listError);
			break;
		case "CS00016":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00077" , "IS00078", listError);
			break;
		case "CS00017":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00082" , "IS00083", listError);
			break;
		case "CS00018":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00087" , "IS00088", listError);
			break;
		case "CS00019":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00102" , "IS00103", listError);
			break;
		case "CS00020":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00119" , "IS00120", listError);
			break;
		case "CS00021":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00255" , "IS00256", listError);
			break;
		case "CS00070":
			checkItemReversal(items,employee, category, bussinessName, dataSetter , "IS00781" , "IS00782", listError);
			break;
		}
	}

	private void checkDateReversalOfSpecialItem(LayoutPersonInfoValueDto IS00017, LayoutPersonInfoValueDto IS00020,
			EmployeeDataMngInfo employee, String bussinessName, TaskDataSetter dataSetter,
			List<ErrorInfoCPS013> listError) {
			GeneralDate startDate = GeneralDate.fromString(IS00017.getValue().toString(), "yyyy/MM/dd");
			GeneralDate endDate = GeneralDate.fromString(IS00020.getValue().toString(), "yyyy/MM/dd");
			if (startDate.after(endDate)) {
				ErrorInfoCPS013 error_itemReversal = new ErrorInfoCPS013(employee.getEmployeeId(),
						IS00017.getCategoryId(), employee.getEmployeeCode().v(), bussinessName,
						chekPersonInfoType, IS00017.getCategoryName(),
						TextResource.localize("Msg_953", IS00017.getCategoryName(),
								IS00017.getItemName(), startDate.toString(), IS00020.getCategoryName(),
								IS00020.getItemName(), endDate.toString()));
				listError.add(error_itemReversal);
				setErrorDataGetter(error_itemReversal, dataSetter);
			}
	}

	private void checkItemReversal(List<LayoutPersonInfoValueDto> items, EmployeeDataMngInfo employee,
			PersonInfoCategory category, String bussinessName, TaskDataSetter dataSetter, String itemCodeStartDate,
			String itemCodeEndDate, List<ErrorInfoCPS013> listError) {
		Optional<LayoutPersonInfoValueDto> startDateOpt = items.stream()
				.filter(itCode -> itCode.getItemCode().equals(itemCodeStartDate)).findFirst();
		Optional<LayoutPersonInfoValueDto> endDateOpt = items.stream()
				.filter(itCode -> itCode.getItemCode().equals(itemCodeEndDate)).findFirst();
		if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
			GeneralDate startDate = GeneralDate.fromString(startDateOpt.get().getValue().toString(), "yyyy/MM/dd");
			GeneralDate endDate = GeneralDate.fromString(endDateOpt.get().getValue().toString(), "yyyy/MM/dd");
			if (startDate.after(endDate)) {
				ErrorInfoCPS013 error_itemReversal = new ErrorInfoCPS013(employee.getEmployeeId(),
						category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
						chekPersonInfoType, category.getCategoryName().v(),
						TextResource.localize("Msg_953", category.getCategoryName().v(),
								startDateOpt.get().getItemName(), startDate.toString(), category.getCategoryName().v(),
								endDateOpt.get().getItemName(), endDate.toString()));
				listError.add(error_itemReversal);
				setErrorDataGetter(error_itemReversal, dataSetter);
			}
		}
	}

	private void checkItemSpecial(List<LayoutPersonInfoValueDto> items, EmployeeDataMngInfo employee,
			PersonInfoCategory category, String bussinessName, TaskDataSetter dataSetter,
			List<ErrorInfoCPS013> listError) {
		
		// 半角または全角スペースあるか(có hay không half space or full space?)
		items.forEach(item ->{
			if (listItemToCheckSpace.contains(item.getItemCode())) {
				String value = (String) item.getValue();
				if (value.startsWith(JP_SPACE) || value.endsWith(JP_SPACE) || !value.contains(JP_SPACE)) {
					ErrorInfoCPS013 error_itemReq = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
							chekPersonInfoType, category.getCategoryName().v(),TextResource.localize("Msg_952", item.getItemName()));
					listError.add(error_itemReq);
					setErrorDataGetter(error_itemReq, dataSetter);
				}
			}
		});
	}

	private void checkNullOfRequiredItems(LayoutPersonInfoValueDto item, EmployeeDataMngInfo employee,
			PersonInfoCategory category, String bussinessName, TaskDataSetter dataSetter,
			List<ErrorInfoCPS013> listError) {
		if (item.getValue() == null || item.getValue() == "") {
			ErrorInfoCPS013 error_itemReq = new ErrorInfoCPS013(employee.getEmployeeId(),category.getPersonInfoCategoryId(), employee.getEmployeeCode().v(), bussinessName,
					chekPersonInfoType, category.getCategoryName().v(),TextResource.localize("Msg_955", item.getItemName()));
			listError.add(error_itemReq);
			setErrorDataGetter(error_itemReq, dataSetter);
		}
	}

	/**
	 * 参照項目チェック処理 (Check item tham chiếu)
	 * @param empCheck
	 * @param dataOfEmployee mapping categoryCode - List<GridLayoutPersonInfoClsDto>
	 * @param excuteCommand
	 * @param mapCategoryWithListItemDf
	 * @param result
	 * @param employee
	 * @param bussinessName
	 */
	private void checkReferenceItem(PeregEmpInfoQuery empCheck,
			Map<String, List<GridLayoutPersonInfoClsDto>> dataOfEmployee, CheckDataFromUI excuteCommand,
			Map<PersonInfoCategory, List<PersonInfoItemDefinition>> mapCategoryWithListItemDf,
			TaskDataSetter dataSetter, EmployeeDataMngInfo employee, String bussinessName,
			List<ErrorInfoCPS013> listError) {
		
		Map<PersonInfoCategory, List<PersonInfoItemDefinition>> itemCodesByCtg = new HashMap<>();
		
		Map<String, String> itemStartCode = new HashMap<>();
		
		mapCategoryWithListItemDf.entrySet().forEach(c -> {
			
			List<PersonInfoItemDefinition> lstItemDefIsSelection = c.getValue().stream()
					.filter(i -> i.isSelection())
					.collect(Collectors.toList());
			
			Optional<String> itemDefinedStartCode = c.getValue().stream()
					.filter(i -> standardDateItemCodes.contains(i.getItemCode().v())).map(i -> i.getItemCode().v())
					.findFirst();
			
			if (!CollectionUtil.isEmpty(lstItemDefIsSelection)) {
				itemCodesByCtg.put(c.getKey(), lstItemDefIsSelection);
			}

			if (itemDefinedStartCode.isPresent()) {
				itemStartCode.put(c.getKey().getCategoryCode().v(), itemDefinedStartCode.get());
			}
		});

		if (itemCodesByCtg.isEmpty())
			return;
		
		Map<String, String>  masterName = getNameMasterError();
		
		itemCodesByCtg.entrySet().forEach(c -> {
			
			List<GridLayoutPersonInfoClsDto> empData = dataOfEmployee.get(c.getKey().getCategoryCode().toString());
			
			List<PersonInfoItemDefinition> lstItemDefineFull = mapCategoryWithListItemDf.get(c.getKey());
			
			if (CollectionUtil.isEmpty(empData))
				return;
			
			empData.stream().forEach(emp -> {
				
				List<LayoutPersonInfoClsDto> listClsDto = emp.getLayoutDtos();
				
				
				List<LayoutPersonInfoValueDto> listAllItemValue = new ArrayList<>();
				listClsDto.forEach(cls -> {
					List<LayoutPersonInfoValueDto> itemsValue = cls.getItems();
					listAllItemValue.addAll(itemsValue);
				});
				
				listClsDto.stream().forEach(cls -> {
					
					List<LayoutPersonInfoValueDto> itemsValue = cls.getItems();
					List<PersonInfoItemDefinition> listItemDefinedSelection = c.getValue();  // list nay chỉ chua danh sach item selection
					
					listItemDefinedSelection.forEach(itemDf -> {
						
						Optional<LayoutPersonInfoValueDto> itemValueOpt = itemsValue.stream()
								.filter(i -> i.getItemCode().equals(itemDf.getItemCode().toString())).findFirst();
						
						if (itemValueOpt.isPresent()) {
							checkError(itemValueOpt.get(), c.getKey().getCategoryCode().toString(), itemStartCode, itemsValue, listAllItemValue, lstItemDefineFull, dataSetter, employee, bussinessName, itemDf, masterName, listError);
						}
					});
				});
			});
		});
	}
	
	/**
	 * 
	 * @param itemValue : item selection ddang check
	 * @param categoryCode
	 * @param itemStartCode
	 * @param itemsValue : list Item nawmf torng cungf 1 claasfication
	 * @param lstItemDefineFull
	 * @param dataSetter
	 * @param employee
	 * @param bussinessName
	 * @param itemDf
	 * @param masterName
	 * @param listError
	 */
	public void checkError(LayoutPersonInfoValueDto itemValue, String categoryCode, Map<String, String> itemStartCode,
			List<LayoutPersonInfoValueDto> itemsValue, List<LayoutPersonInfoValueDto> listAlltemsValue, List<PersonInfoItemDefinition> lstItemDefineFull,  TaskDataSetter dataSetter, EmployeeDataMngInfo employee,
			String bussinessName, PersonInfoItemDefinition itemDf, Map<String, String> masterName,
			List<ErrorInfoCPS013> listError) {
		
		if (itemDf.isEnum()) {
			checkErrorEnum(dataSetter, employee, bussinessName, itemValue, listError);
		}

		if (itemDf.isCodeName()) {
			checkCodeName(dataSetter, employee, bussinessName, itemValue, listError);
		}

		if (itemDf.isDesignateMaster()) {
			
			if (itemValue.getItemCode().toString().equals("IS00073")) {
				// CS00015	部門本務 itemStartDate IS00071
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00071 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00071")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00073 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00073")).findFirst();
				if (itemStartDateIS00071.isPresent() && itemMasterIS00073.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00071.get().getValue(),itemMasterIS00073.get().getValue(), masterName, listError);
				}

			} else if (itemValue.getItemCode().toString().equals("IS00079")) {
				// CS00016	職位 itemStartDate IS00077
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00077 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00077")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00079 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00079")).findFirst();
				if (itemStartDateIS00077.isPresent() && itemMasterIS00079.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00077.get().getValue(),itemMasterIS00079.get().getValue(), masterName, listError);
				}

			} else if (itemValue.getItemCode().toString().equals("IS00084")) {
				// CS00017 職場 itemStartDate IS00082
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00082 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00082")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00084 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00084")).findFirst();
				if (itemStartDateIS00082.isPresent() && itemMasterIS00084.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00082.get().getValue(),itemMasterIS00084.get().getValue(), masterName, listError);
				}
				
			} else if (itemValue.getItemCode().toString().equals("IS00085")) {
				// CS00017 職場 itemStartDate IS00082
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00082 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00082")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00085 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00085")).findFirst();
				if (itemStartDateIS00082.isPresent() && itemMasterIS00085.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00082.get().getValue(),itemMasterIS00085.get().getValue(), masterName, listError);
				}
				
			} else if (itemValue.getItemCode().toString().equals("IS00131")) {
				// CS00020
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00119 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00119")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00131 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00131")).findFirst();
				if (itemStartDateIS00119.isPresent() && itemMasterIS00131.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00119.get().getValue(),itemMasterIS00131.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00140")) {
				// CS00020
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00119 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00119")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00140 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00140")).findFirst();
				if (itemStartDateIS00119.isPresent() && itemMasterIS00140.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00119.get().getValue(),itemMasterIS00140.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00158")) {
				// CS00020
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00119 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00119")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00158 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00158")).findFirst();
				if (itemStartDateIS00119.isPresent() && itemMasterIS00158.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00119.get().getValue(),itemMasterIS00158.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00167")) {
				// CS00020
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00119 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00119")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00167 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00167")).findFirst();
				if (itemStartDateIS00119.isPresent() && itemMasterIS00167.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00119.get().getValue(),itemMasterIS00167.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00176")) {
				// CS00020
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00119 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00119")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00176 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00176")).findFirst();
				if (itemStartDateIS00119.isPresent() && itemMasterIS00176.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00119.get().getValue(),itemMasterIS00176.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00149")) {
				// CS00020
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00119 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00119")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00149 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00149")).findFirst();
				if (itemStartDateIS00119.isPresent() && itemMasterIS00149.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00119.get().getValue(),itemMasterIS00149.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00194")) {
				// CS00070
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00781 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00781")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00194 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00194")).findFirst();
				if (itemStartDateIS00781.isPresent() && itemMasterIS00194.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00781.get().getValue(),itemMasterIS00194.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00203")) {
				// CS00070
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00781 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00781")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00203 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00203")).findFirst();
				if (itemStartDateIS00781.isPresent() && itemMasterIS00203.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00781.get().getValue(),itemMasterIS00203.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00212")) {
				// CS00070
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00781 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00781")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00212 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00212")).findFirst();
				if (itemStartDateIS00781.isPresent() && itemMasterIS00212.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00781.get().getValue(),itemMasterIS00212.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00221")) {
				// CS00070
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00781 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00781")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00221 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00221")).findFirst();
				if (itemStartDateIS00781.isPresent() && itemMasterIS00221.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00781.get().getValue(),itemMasterIS00221.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00230")) {
				// CS00070
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00781 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00781")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00230 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00230")).findFirst();
				if (itemStartDateIS00781.isPresent() && itemMasterIS00230.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00781.get().getValue(),itemMasterIS00230.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00239")) {
				// CS00070
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00781 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00781")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00239 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00239")).findFirst();
				if (itemStartDateIS00781.isPresent() && itemMasterIS00239.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00781.get().getValue(),itemMasterIS00239.get().getValue(), masterName, listError);
				}
			} else if (itemValue.getItemCode().toString().equals("IS00185")) {
				// CS00070
				Optional<LayoutPersonInfoValueDto> itemStartDateIS00781 = listAlltemsValue.stream().filter(i -> i.getItemCode().equals("IS00781")).findFirst();
				Optional<LayoutPersonInfoValueDto> itemMasterIS00185 = itemsValue.stream().filter(i -> i.getItemCode().equals("IS00185")).findFirst();
				if (itemStartDateIS00781.isPresent() && itemMasterIS00185.isPresent()) {
					checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, itemStartDateIS00781.get().getValue(),itemMasterIS00185.get().getValue(), masterName, listError);
				}
			} else{
				checkDesignateMaster(dataSetter, employee, bussinessName, itemValue, null, null, masterName, listError);
			}
		}
	}
	
	/** "IS00020", "IS00077", "IS00082", "IS00119", "IS00781"
	 * Enum参照する場合 ( TH tham chiếu Enum)
	 * @param result
	 * @param employee
	 * @param bussinessName
	 * @param item
	 */
	public void checkErrorEnum(TaskDataSetter dataSetter, EmployeeDataMngInfo employee, String bussinessName,
			LayoutPersonInfoValueDto item, List<ErrorInfoCPS013> listError) {
		 String value = (String) item.getValue();
		 
		if (Objects.isNull(value)) {
			return;
		}

		if (value.trim().equals("")) {
			return;
		}
			
		List<String> comboxValue = item.getLstComboBoxValue().stream().map(c -> c.getOptionValue()).sorted().collect(Collectors.toList());
		if (comboxValue.size() >0 && !comboxValue.contains(value)) {
			//Enum値の最小値
			String min = " ";
			if(item.isRequired()){
				 min = comboxValue.size() == 0? " " : comboxValue.get(0);
			}else{
				 min = comboxValue.size() >  1? comboxValue.get(1) : " ";
			}
			String max = comboxValue.size() == 0? " ": comboxValue.get(comboxValue.size() - 1);
			//{0} : 項目名, {1} : データの値, {2} : Enum値の最小値 (giá trị max của list combox)
			ErrorInfoCPS013 error = new ErrorInfoCPS013(employee.getEmployeeId(), item.getCategoryId(),
					employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, item.getCategoryName(),
					TextResource.localize("Msg_949", item.getItemName(), value == null? "": value, min, max));
			listError.add(error);
			setErrorDataGetter(error, dataSetter);
		}
	}
	
	/**
	 * 個人情報選択項目マスタ参照する場合 ( TH tham chiếu Personal Information Selection Item Master)
	 * @param result
	 * @param employee
	 * @param bussinessName
	 * @param item
	 */
	public void checkCodeName(TaskDataSetter dataSetter, EmployeeDataMngInfo employee, String bussinessName,
			LayoutPersonInfoValueDto item, List<ErrorInfoCPS013> listError) {
		String value = (String) item.getValue();
		if (Objects.isNull(value)) {
			return;
		}

		if (value.trim().equals("")) {
			return;
		}
		List<String> comboxValue = item.getLstComboBoxValue().stream().map(c -> c.getOptionValue()).sorted().collect(Collectors.toList());
		if (comboxValue.size() >0 && !comboxValue.contains(value)) {
			//{0} : 項目名, {1} : データの値
			ErrorInfoCPS013 error = new ErrorInfoCPS013(employee.getEmployeeId(), item.getCategoryId(),
					employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, item.getCategoryName(),
					TextResource.localize("Msg_950", item.getItemName(), value == null? "": value));
			listError.add(error);
			setErrorDataGetter(error, dataSetter);
		}
	}
	
	/**
	 * マスタ未登録チェックする (check master item)
	 * @param result
	 * @param employee
	 * @param bussinessName
	 * @param item
	 * @param startValue
	 */
	public void checkDesignateMaster(TaskDataSetter dataSetter, EmployeeDataMngInfo employee, String bussinessName,
			LayoutPersonInfoValueDto item, Object startValue,Object comboboxValue, Map<String, String> masterNames,
			List<ErrorInfoCPS013> listError) {
		 String value = (String) item.getValue();
		 List<String> comboxValue = item.getLstComboBoxValue().stream().map(c -> c.getOptionValue()).sorted().collect(Collectors.toList());
		 String masterName  = masterNames.get(item.getItemCode());
		 
		if (Objects.isNull(value)) {
			return;
		}

		if (value.trim().equals("")) {
			return;
		}
		
		if (comboxValue.size() > 0 && !comboxValue.contains(value)) {
			// 履歴ありマスタの場合 (TH master có history)
			if (listItem_Master_History.contains(item.getItemCode())) {
				ErrorInfoCPS013 error = new ErrorInfoCPS013(employee.getEmployeeId(), item.getCategoryId(),
						employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, item.getCategoryName(),
						TextResource.localize("Msg_936", item.getItemName(), value == null ? "null" : value, masterName, startValue.toString()));
				listError.add(error);
				setErrorDataGetter(error, dataSetter);
			} else {
				// 履歴なしマスタの場合 ( TH master không có history)
				ErrorInfoCPS013 error = new ErrorInfoCPS013(employee.getEmployeeId(), item.getCategoryId(),
						employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, item.getCategoryName(),
						TextResource.localize("Msg_937", item.getItemName(), value == null ? "null" : value, masterName));
				listError.add(error);
				setErrorDataGetter(error, dataSetter);
			}
		}
	}


	/**
	 * システム必須チェック (Kiểm tra required system)
	 * @param empCheck
	 * @param dataOfEmployee
	 * @param excuteCommand
	 * @param mapCategoryWithListItemDf
	 * @param result
	 * @param employee
	 * @param bussinessName
	 */
	private void checkSystemRequired(PeregEmpInfoQuery empCheck,
			Map<String, List<GridLayoutPersonInfoClsDto>> dataOfEmployee, CheckDataFromUI excuteCommand,
			Map<PersonInfoCategory, List<PersonInfoItemDefinition>> mapCategoryWithListItemDf,
			TaskDataSetter dataSetter, EmployeeDataMngInfo employee, String bussinessName, 
			List<ErrorInfoCPS013> listError) {
		
		// baseDate from UI
		GeneralDate baseDate = GeneralDate.fromString(excuteCommand.getDateTime() , "yyyy/MM/dd");
		
		/**
		 * 各システム必須となったカテゴリだけチェック対象とする ＜下記の条件の該当するカテゴリが対象となる、それ以外は除外する＞
		 * [個人情報整合性チェックカテゴリ]の [就業システム必須]:True または [人事システム必須]:True または
		 * [給与システム必須]:True の場合 ----------------
		 * Chi co categories system Required la doi tuong check < categorie tuong duong voi dieu kien
		 * duoi day la doi tuong, cai khac thi loai bo>. 
		 * [Personal Information Integrity Check Category]. 
		 * [Employment system required]: True or
		 * [Human Resources System Required]: True or 
		 * [Salary System Required]: True
		 */

		List<PersonInfoCategory> listCategory = new ArrayList<>(mapCategoryWithListItemDf.keySet());
		
		List<String> listCategoryCode = listCategory.stream().map(m -> m.getCategoryCode().v()).collect(Collectors.toList());
		
		List<PerInfoValidateCheckCategory> lstCtgSetting = this.perInfoCheckCtgRepo.getListPerInfoValidByListCtgId(listCategoryCode, AppContexts.user().contractCode());
		
		List<PerInfoValidateCheckCategory> listCategoryFilter = lstCtgSetting.stream()
				.filter(ctg -> ((ctg.getHumanSysReq().value == NotUseAtr.USE.value)
						|| (ctg.getPaySysReq().value == NotUseAtr.USE.value)
						|| (ctg.getJobSysReq().value == NotUseAtr.USE.value)))
				.collect(Collectors.toList());
		
		List<String> listCategoryCodeFilter = listCategoryFilter.stream().map(m -> m.getCategoryCd().v()).collect(Collectors.toList());
		
		for (int i = 0; i < listCategory.size(); i++) {
				PersonInfoCategory category = listCategory.get(i);
				
				if (!listCategoryCodeFilter.contains(category.getCategoryCode().v())) {
					continue;
				}
				
				List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg = dataOfEmployee.get(category.getCategoryCode().v());
				
				if(category.isHistoryCategory()){
					List<DatePeriod> lstDatePeriod =  getListDataHistory(category, listDataEmpOfCtg);
					
					if (CollectionUtil.isEmpty(lstDatePeriod)) {
						continue;
					}
					boolean checkExitHisDataOfBaseDate = false;
					for (int j = 0; j < lstDatePeriod.size(); j++) {
						if (baseDate.afterOrEquals(lstDatePeriod.get(j).start()) && baseDate.beforeOrEquals(lstDatePeriod.get(j).end())) {
							checkExitHisDataOfBaseDate = true;
						}
					}
					
					if (!checkExitHisDataOfBaseDate) {
						//TODO
						/**
						データ取得条件：基準日＝パラメータ.基準日
						注意：
						１：期間が「年期間」の場合は、パラメータ.基準日の年を利用
						　例：基準日:2018/04/01   ==>2018
						            カテゴリ[CS00079 社員住民税納付方法] チェックする際には[2018]を基準年として利用する
						２：期間が「年月期間」の場合は、パラメータ.基準日の[年月]を利用
						　例：基準日:2018/04/01   ==>201804
						          カテゴリ[CS00077 社員所得税情報]チェックする際には[201804]を基準年月として利用
						*/
						ErrorInfoCPS013 error_6 = new ErrorInfoCPS013(employee.getEmployeeId(), category.getPersonInfoCategoryId(),
								employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, category.getCategoryName().v(),
								TextResource.localize("Msg_951", baseDate.toString()));
						listError.add(error_6);
						setErrorDataGetter(error_6, dataSetter);
					}
					
				}
				
				if(category.isMultiCategory() || category.isSingleCategory()){
					if (listDataEmpOfCtg.isEmpty()) {
						ErrorInfoCPS013 error_7 = new ErrorInfoCPS013(employee.getEmployeeId(), category.getPersonInfoCategoryId(),
								employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, category.getCategoryName().v(),
								TextResource.localize("Msg_1482"));
						listError.add(error_7);
						setErrorDataGetter(error_7, dataSetter);
					}
				}
		}
	}

	/**
	 * アルゴリズム「履歴整合性チェック」を実行する (Thực thi thuật toán 「Check tính hợp lệ của lịch sử」)
	 * @param empCheck
	 * @param dataOfEmployee
	 * @param excuteCommand
	 * @param mapCategoryWithListItemDf
	 * @param result
	 * @param employee
	 * @param bussinessName
	 */
	public void checkCategoryHistory(PeregEmpInfoQuery empCheck, Map<String, List<GridLayoutPersonInfoClsDto>> dataOfEmployee, CheckDataFromUI excuteCommand,
			Map<PersonInfoCategory, List<PersonInfoItemDefinition>> mapCategoryWithListItemDf, TaskDataSetter dataSetter, EmployeeDataMngInfo employee, String bussinessName, List<ErrorInfoCPS013> listError) {
		
		List<PersonInfoCategory> listCategory = new ArrayList<>(mapCategoryWithListItemDf.keySet());
		
		List<PersonInfoCategory> filterCtg = listCategory.stream().filter(ctg -> ctg.isHistoryCategoryCps013())
				.collect(Collectors.toList());
		if (CollectionUtil.isEmpty(filterCtg)) {
			return;
		}
		
		for (int i = 0; i < filterCtg.size(); i++) {
			PersonInfoCategory ctg = filterCtg.get(i);
			List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg = dataOfEmployee.get(ctg.getCategoryCode().v());
			
			if (CollectionUtil.isEmpty(listDataEmpOfCtg)) {
				continue;
			}
			
			List<DatePeriod> listDataPeriodOfCtgHis = getListDataHistory(ctg, listDataEmpOfCtg);

			// 「制約：連続かつ永続かつ１件以上存在」該当するカテゴリのデータ１件もない場合
			// (TH không có dữ liệu của category tương ứng「Constraint：tồn tại
			// hơn 1 cái liên tục hoặc lâu dài」)
			if (checkPersistentResidentHistory(ctg, listDataEmpOfCtg)) {
				ErrorInfoCPS013 error_1 = new ErrorInfoCPS013(employee.getEmployeeId(), ctg.getPersonInfoCategoryId(),
						employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, ctg.getCategoryName().v(),
						TextResource.localize("Msg_935"));
				listError.add(error_1);
				setErrorDataGetter(error_1, dataSetter);
				
				
				// 履歴データ特殊チェック (check historyData đặc biệt)
				checkHistoricalDataSpecial(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis, employee, bussinessName, dataSetter, listError);
				
				continue;
			}

			// 期間重複チェック (kiểm tra trùng lặp thời gian)
			DatePeriod error = checkOverlapPeriod(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis);
			if (error != null) {
				ErrorInfoCPS013 error_2 = new ErrorInfoCPS013(employee.getEmployeeId(), ctg.getPersonInfoCategoryId(),
						employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, ctg.getCategoryName().v(),
						TextResource.localize("Msg_933", error.start().toString(), error.end().toString()));
				listError.add(error_2);
				setErrorDataGetter(error_2, dataSetter);
				
				// 履歴データ特殊チェック (check historyData đặc biệt)
				checkHistoricalDataSpecial(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis, employee, bussinessName, dataSetter, listError);
				
				continue;
			}

			// 空白期間チェック (kiem tra thoi gian trong)
			List<DatePeriod> listDateBlank = checkBlankOfPeriod(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis);
			if (!listDateBlank.isEmpty()) {
				
				listDateBlank.forEach(date -> {
					ErrorInfoCPS013 error_3 = new ErrorInfoCPS013(employee.getEmployeeId(), ctg.getPersonInfoCategoryId(),
							employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, ctg.getCategoryName().v(),
							TextResource.localize("Msg_931",date.start().toString() , date.end().toString() ));
					listError.add(error_3);
					setErrorDataGetter(error_3, dataSetter);
				}); 
				
				// 履歴データ特殊チェック (check historyData đặc biệt)
				checkHistoricalDataSpecial(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis, employee, bussinessName, dataSetter, listError);
				
				continue;
			}

			// 永続チェック (kiểm tra ràng buộc lâu dài -永続)
			if (!checkPermanent(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis)) {
				DatePeriod endList = listDataPeriodOfCtgHis.get(listDataPeriodOfCtgHis.size() - 1);
				ErrorInfoCPS013 error_4 = new ErrorInfoCPS013(employee.getEmployeeId(), ctg.getPersonInfoCategoryId(),
						employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, ctg.getCategoryName().v(),
						TextResource.localize("Msg_932", endList.start().toString(), endList.end().toString()));
				listError.add(error_4);
				setErrorDataGetter(error_4, dataSetter);
				//TODO
				// 履歴データ特殊チェック (check historyData đặc biệt)
				checkHistoricalDataSpecial(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis, employee, bussinessName, dataSetter, listError);
				
				continue;
			}
			
			// 履歴データ特殊チェック (check historyData đặc biệt)
			checkHistoricalDataSpecial(ctg, listDataEmpOfCtg, listDataPeriodOfCtgHis, employee, bussinessName, dataSetter, listError);
			
		}
	}
	
	private void setErrorDataGetter(ErrorInfoCPS013 error, TaskDataSetter dataSetter) {
		String ramdom = IdentifierUtil.randomUniqueId();
		ObjectMapper mapper = new ObjectMapper();
		try {
			dataSetter.setData("employeeId" + ramdom, mapper.writeValueAsString(error));
		} catch (JsonProcessingException e) {
			System.out.println("cps013 mapper object to json fail");
			e.printStackTrace();
			
		}
	}

	/**
	 * 履歴データ特殊チェック (Check historyData đặc biệt)
	 * @param ctg
	 * @param listDataEmpOfCtg
	 * @param listDataPeriodOfCtgHis
	 * @param employee
	 * @param bussinessName
	 * @param result
	 */
	private void checkHistoricalDataSpecial(PersonInfoCategory ctg, List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg,
			List<DatePeriod> listDataPeriodOfCtgHis, EmployeeDataMngInfo employee, String bussinessName, TaskDataSetter dataSetter, List<ErrorInfoCPS013> listError) {
		
		if (AppContexts.user().roles().forAttendance() != null  && ctg.getCategoryCode().toString().equals("CS00014")) {
			listDataEmpOfCtg.forEach(data -> {
				List<LayoutPersonInfoValueDto> items = new ArrayList<>();
				List<LayoutPersonInfoClsDto> layoutDtos = data.getLayoutDtos();
				layoutDtos.forEach(item -> {
					items.addAll(item.getItems());
				});
				
				String employmentCode = (String) items.stream().filter(i -> i.getItemCode().toString().equals("IS00068")).findFirst().get().getValue();
				
				Optional<ClosureEmployment> closureEmp = closureEmploymentRepository.findByEmploymentCD(AppContexts.user().companyId(), employmentCode);
				if(!closureEmp.isPresent()){
					ErrorInfoCPS013 error_5 = new ErrorInfoCPS013(employee.getEmployeeId(), ctg.getPersonInfoCategoryId(),
							employee.getEmployeeCode().v(), bussinessName, chekPersonInfoType, ctg.getCategoryName().v(),
							TextResource.localize("Msg_934", employmentCode ));
					listError.add(error_5);
					setErrorDataGetter(error_5, dataSetter);
				}
			});
		}
	}

	private boolean checkPermanent(PersonInfoCategory ctg, List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg,
			List<DatePeriod> listDataPeriodOfCtgHis) {
		if (ctg.getCategoryType() ==  CategoryType.CONTINUOUSHISTORY) {
			if (listPersistentResidentHisAndPersistentHisCtg.contains(ctg.getCategoryCode().v()) && !listDataPeriodOfCtgHis.isEmpty()) {
				GeneralDate endDate =  listDataPeriodOfCtgHis.get(listDataPeriodOfCtgHis.size() - 1).end();
				if (endDate.equals(GeneralDate.max())) {
					return true;
				}else{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 空白期間チェック (kiem tra thoi gian trong)
	 * @param ctg
	 * @param listDataEmpOfCtg
	 * @param listDataPeriodOfCtgHis
	 * @return
	 */
	private List<DatePeriod> checkBlankOfPeriod(PersonInfoCategory ctg, List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg, List<DatePeriod> listDataPeriodOfCtgHis) {
		
		if(ctg.getCategoryCode().v().equals("CS00003")) return new ArrayList<>();
		
		if (listDataPeriodOfCtgHis.isEmpty() || listDataPeriodOfCtgHis.size() <= 1) {
			return new ArrayList<>();
		}
		if (ctg.getCategoryType() ==  CategoryType.CONTINUOUSHISTORY) {
			if (listPersistentResidentHisAndPersistentHisCtg.contains(ctg.getCategoryCode().v()) && !listDataPeriodOfCtgHis.isEmpty() && listDataPeriodOfCtgHis.size() > 1) {
				List<DatePeriod> result = new ArrayList<>();
				for (int i = 0; i < listDataPeriodOfCtgHis.size() - 1; i++) {
					DatePeriod datePeriod_i = listDataPeriodOfCtgHis.get(i);
					DatePeriod datePeriod_i1 = listDataPeriodOfCtgHis.get(i + 1);
					if(!isHistoryContinue(datePeriod_i, datePeriod_i1)){
						if(result.isEmpty()){
							result.add(new DatePeriod(datePeriod_i.end().addDays(1), datePeriod_i1.start().addDays(-1)));
						}
					}
				}
				return result;
			}
		}
		
		return new ArrayList<>();
	}

	private boolean isHistoryContinue(DatePeriod datePeriod1, DatePeriod datePeriod2) {
		
		if (datePeriod1.end().addDays(1).equals(datePeriod2.start())) {
			return true;
		}
		return false;
	}

	// 期間重複チェック (kiểm tra trùng lặp thời gian)
	private DatePeriod checkOverlapPeriod(PersonInfoCategory ctg, List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg, List<DatePeriod> listDataPeriod ) {
		
		if (listDataPeriod.isEmpty() || listDataPeriod.size() <= 1) {
			return null;
		}
		DatePeriod dateOverLap = isOverlap(listDataPeriod);
		
		if (dateOverLap == null) return null;
		return dateOverLap;
	}

	/**
	 * CS00003  - UnduplicatableHistory
	 * CS00004  - PersistentResidentHistory
	 * CS00014  - PersistentResidentHistory
	 * CS00015  - Department
	 * CS00016  - PersistentResidentHistory
	 * CS00017  - PersistentResidentHistory
	 * CS00018  - UnduplicatableHistory 
	 * CS00019  - UnduplicatableHistory
	 * CS00020  - PersistentResidentHistory
	 * CS00021  - PersistentHistory
	 * CS00070  - PersistentResidentHistory
	 * @param ctg
	 * @param listDataEmpOfCtg
	 */
	private List<DatePeriod> getListDataHistory(PersonInfoCategory ctg, List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg ) {
		
		List<DatePeriod> historys = new ArrayList<>();
		switch (ctg.getCategoryCode().v()) {
		case "CS00003":
			historys = getListDatePeriod("IS00020","IS00021",listDataEmpOfCtg);
			break;
		case "CS00004":
			historys = getListDatePeriod("IS00026","IS00027",listDataEmpOfCtg);
			break;
		case "CS00014":
			historys = getListDatePeriod("IS00066","IS00067",listDataEmpOfCtg);
			break;
		case "CS00015":
			// chua doi ung
			//historys = getListDatePeriod("IS00071","IS00072",listDataEmpOfCtg);
			break;
		case "CS00016":
			historys = getListDatePeriod("IS00077","IS00078",listDataEmpOfCtg);
			break;
		case "CS00017":
			historys = getListDatePeriod("IS00082","IS00083",listDataEmpOfCtg);
			break;
		case "CS00018":
			historys = getListDatePeriod("IS00087","IS00088",listDataEmpOfCtg);
			break;
		case "CS00019":
			historys = getListDatePeriod("IS00102","IS00103",listDataEmpOfCtg);
			break;
		case "CS00020":
			historys = getListDatePeriod("IS00119","IS00120",listDataEmpOfCtg);
			break;
		case "CS00021":
			historys = getListDatePeriod("IS00255","IS00256",listDataEmpOfCtg);
			break;
		case "CS00070":
			historys = getListDatePeriod("IS00781","IS00782",listDataEmpOfCtg);
			break;
		}
		return historys.stream().sorted((a, b) -> a.start().compareTo(b.start())).collect(Collectors.toList());
	}

	private List<DatePeriod> getListDatePeriod(String itemCodeStartDate, String itemCodeEndDate, List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg) {
		List<DatePeriod> result = new ArrayList<>();
		if (CollectionUtil.isEmpty(listDataEmpOfCtg)) {
			return new ArrayList<>();
		}
		
		listDataEmpOfCtg.forEach(data ->{
			List<LayoutPersonInfoValueDto> items = new ArrayList<>(); 
			List<LayoutPersonInfoClsDto> layoutDtos = data.getLayoutDtos();
			layoutDtos.forEach(item -> {
				items.addAll(item.getItems());
			});
			
			Optional<LayoutPersonInfoValueDto> startDateOpt = items.stream().filter(i -> i.getItemCode().equals(itemCodeStartDate)).findFirst();
			Optional<LayoutPersonInfoValueDto> endDateOpt = items.stream().filter(i -> i.getItemCode().equals(itemCodeEndDate)).findFirst();
			if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
				GeneralDate startDate = GeneralDate.fromString(startDateOpt.get().getValue().toString() , "yyyy/MM/dd");
				GeneralDate endtDate = null;
				if (endDateOpt.get().getValue() != null) {
					endtDate  = GeneralDate.fromString(endDateOpt.get().getValue().toString() , "yyyy/MM/dd");
				}
				result.add(new DatePeriod(startDate, endtDate));
			}
		});
		return result;
	}

	private boolean checkPersistentResidentHistory(PersonInfoCategory ctg, List<GridLayoutPersonInfoClsDto> listDataEmpOfCtg) {
		if (ctg.getCategoryType() ==  CategoryType.CONTINUOUSHISTORY) {
			if (listPersistentResidentHistoryCtg.contains(ctg.getCategoryCode().v())) {
				if(CollectionUtil.isEmpty(listDataEmpOfCtg)) {
					return true;
				};
			}
		}
		return false;
	}
	
	/**
	 * 履歴重複の場合 ( TH trùng lặp lịch sử)
	 * @param historys
	 * @return
	 */
	private DatePeriod isOverlap(List<DatePeriod> historys) {
		List<DatePeriod> sortedList = historys.stream()
				.sorted(Comparator.comparing(DatePeriod::start))
				.collect(Collectors.toList());
		
		List<DatePeriod> result = new ArrayList<>();
		sortedList.forEach(his1 -> {
			sortedList.forEach(hisOther -> {
				if (his1 != hisOther) {
					if ((his1.start().afterOrEquals(hisOther.start()) && his1.start().beforeOrEquals(hisOther.end())) || 
							(his1.start().beforeOrEquals(hisOther.start()) && his1.start().afterOrEquals(hisOther.end()))) {
						result.add(new DatePeriod(his1.start(), hisOther.end()));
					}

					if ((his1.end().afterOrEquals(hisOther.start()) && his1.end().beforeOrEquals(hisOther.end())) || 
							(his1.end().beforeOrEquals(hisOther.start()) && his1.end().afterOrEquals(hisOther.end()))) {
						result.add(new DatePeriod(hisOther.start(), his1.end()));
					}
				}
			});
		});
		
		return result.isEmpty() ? null : result.stream()
				.sorted(Comparator.comparing(DatePeriod::start))
				.collect(Collectors.toList()).get(0);
	}
	
	private Map<String, String> getNameMasterError(){
		Map<String, String> masterName = new HashMap<>();
		masterName.put("IS00001" , "社員CD");		
		masterName.put("IS00002" , "外部CD");		
		masterName.put("IS00003" , "個人名");		
		masterName.put("IS00004" , "個人名カナ");		
		masterName.put("IS00005" , "個人名ローマ字");		
		masterName.put("IS00006" , "個人名ローマ字カナ");		
		masterName.put("IS00007" , "個人名他言語");		
		masterName.put("IS00008" , "個人名他言語カナ");		
		masterName.put("IS00009" , "表示氏名(ビジネスネーム)");		
		masterName.put("IS00010" , "表示氏名(ビジネスネーム)カナ");		
		masterName.put("IS00011" , "表示氏名(ビジネスネーム)英語");		
		masterName.put("IS00012" , "表示氏名(ビジネスネーム)その他");		
		masterName.put("IS00013" , "個人旧姓");		
		masterName.put("IS00014" , "個人旧姓カナ");		
		masterName.put("IS00015" , "個人届出名");		
		masterName.put("IS00016" , "個人届出名カナ");		
		masterName.put("IS00017" , "生年月日");		
		masterName.put("IS00783" , "年齢");		
		masterName.put("IS00018" , "性別");		
		masterName.put("IS00019" , "血液型");		
		masterName.put("IS00020" , "入社年月日");		
		masterName.put("IS00021" , "退職年月日");		
		masterName.put("IS00784" , "勤続年数");		
		masterName.put("IS00022" , "本採用年月日");		
		masterName.put("IS00023" , "採用区分");		
		masterName.put("IS00024" , "退職金計算開始日");		
		masterName.put("IS00025" , "期間");		
		masterName.put("IS00026" , "開始日");		
		masterName.put("IS00027" , "終了日");		
		masterName.put("IS00028" , "分類");		
		masterName.put("IS00029" , "期間");		
		masterName.put("IS00030" , "開始日");		
		masterName.put("IS00031" , "終了日");		
		masterName.put("IS00032" , "分類CD");		
		masterName.put("IS00033" , "期間");		
		masterName.put("IS00034" , "開始日");		
		masterName.put("IS00035" , "終了日");		
		masterName.put("IS00036" , "分類CD");		
		masterName.put("IS00037" , "期間");		
		masterName.put("IS00038" , "開始日");		
		masterName.put("IS00039" , "終了日");		
		masterName.put("IS00040" , "分類CD");		
		masterName.put("IS00041" , "期間");		
		masterName.put("IS00042" , "開始日");		
		masterName.put("IS00043" , "終了日");		
		masterName.put("IS00044" , "分類CD");		
		masterName.put("IS00045" , "期間");		
		masterName.put("IS00046" , "開始日");		
		masterName.put("IS00047" , "終了日");		
		masterName.put("IS00048" , "分類CD");		
		masterName.put("IS00049" , "期間");		
		masterName.put("IS00050" , "開始日");		
		masterName.put("IS00051" , "終了日");		
		masterName.put("IS00052" , "分類CD");		
		masterName.put("IS00053" , "期間");		
		masterName.put("IS00054" , "開始日");		
		masterName.put("IS00055" , "終了日");		
		masterName.put("IS00056" , "分類CD");		
		masterName.put("IS00057" , "期間");		
		masterName.put("IS00058" , "開始日");		
		masterName.put("IS00059" , "終了日");		
		masterName.put("IS00060" , "分類CD");		
		masterName.put("IS00061" , "期間");		
		masterName.put("IS00062" , "開始日");		
		masterName.put("IS00063" , "終了日");		
		masterName.put("IS00064" , "分類CD");		
		masterName.put("IS00065" , "期間");		
		masterName.put("IS00066" , "開始日");		
		masterName.put("IS00067" , "終了日");		
		masterName.put("IS00068" , "雇用");		
		masterName.put("IS00069" , "給与区分");		
		masterName.put("IS00070" , "期間");		
		masterName.put("IS00071" , "発令日");		
		masterName.put("IS00072" , "終了日");		
		masterName.put("IS00073" , "部門");		
		masterName.put("IS00074" , "所属履歴異動種類");		
		masterName.put("IS00075" , "分配率");		
		masterName.put("IS00076" , "期間");		
		masterName.put("IS00077" , "開始日");		
		masterName.put("IS00078" , "終了日");		
		masterName.put("IS00079" , "職位");		
		masterName.put("IS00080" , "備考");		
		masterName.put("IS00081" , "期間");		
		masterName.put("IS00082" , "開始日");		
		masterName.put("IS00083" , "終了日");		
		masterName.put("IS00084" , "職場");		
		masterName.put("IS00085" , "職場");		
		masterName.put("IS00086" , "休職休業期間");		
		masterName.put("IS00087" , "休職休業開始日");		
		masterName.put("IS00088" , "休職休業終了日");		
		masterName.put("IS00089" , "休職休業");		
		masterName.put("IS00090" , "家族の同一の要介護状態について介護休業したことがあるか（介護休業の場合）");	
		masterName.put("IS00091" , "対象の家族についてのこれまでの介護休業および介護短時間勤務の日数（介護休業の場合）");	
		masterName.put("IS00092" , "出産種別（産前休業の場合）");		
		masterName.put("IS00093" , "子の区分（育児休業の場合）");		
		masterName.put("IS00094" , "縁組成立の年月日（育児休業で子が養子の場合）");		
		masterName.put("IS00095" , "同じ子について育児休業をしたことがあるか（育児休業の場合）");	
		masterName.put("IS00096" , "１歳を超えての休業の申出の場合で申出者が育児休業中でない場合、配偶者が休業しているか（育児休業の場合）");		
		masterName.put("IS00097" , "社会保険支給対象区分");		
		masterName.put("IS00098" , "備考");		
		masterName.put("IS00099" , "休業対象家族氏名");		
		masterName.put("IS00100" , "休業対象家族続柄");		
		masterName.put("IS00101" , "短時間勤務期間");		
		masterName.put("IS00102" , "短時間開始日");		
		masterName.put("IS00103" , "短時間終了日");		
		masterName.put("IS00104" , "短時間勤務区分");		
		masterName.put("IS00105" , "育児時間1");		
		masterName.put("IS00106" , "育児開始1");		
		masterName.put("IS00107" , "育児終了1");		
		masterName.put("IS00108" , "育児時間2");		
		masterName.put("IS00109" , "育児開始2");		
		masterName.put("IS00110" , "育児終了2");		
		masterName.put("IS00111" , "備考");		
		masterName.put("IS00112" , "短時間対象家族氏名");		
		masterName.put("IS00113" , "短時間対象家族続柄");		
		masterName.put("IS00114" , "家族の同一の要介護状態について介護短時間勤務をしたことがあるか（介護短時間の場合）");		
		masterName.put("IS00115" , "対象の家族についてのこれまでの介護休業および介護短時間勤務の日数（介護短時間の場合）");	
		masterName.put("IS00116" , "子の区分（育児短時間の場合）");		
		masterName.put("IS00117" , "縁組成立の年月日（育児短時間で子が養子の場合）");		
		masterName.put("IS00118" , "期間");		
		masterName.put("IS00119" , "開始日");		
		masterName.put("IS00120" , "終了日");		
		masterName.put("IS00252" , "就業区分");		
		masterName.put("IS00253" , "契約時間");		
		masterName.put("IS00260" , "休暇時の就業時間加算設定");		
		masterName.put("IS00248" , "休暇時の加算時間");		
		masterName.put("IS00249" , "加算時間１日");		
		masterName.put("IS00250" , "加算時間午前");		
		masterName.put("IS00251" , "加算時間午後");		
		masterName.put("IS00247" , "休暇時の就業時間帯の自動設定");		
		masterName.put("IS00258" , "勤務時間の自動設定");		
		masterName.put("IS00246" , "加給時間帯");		
		masterName.put("IS00259" , "時給者区分");		
		masterName.put("IS00122" , "スケジュール管理設定");		
		masterName.put("IS00121" , "スケジュール管理");		
		masterName.put("IS00123" , "スケジュール作成方法");		
		masterName.put("IS00124" , "カレンダーの参照先");		
		masterName.put("IS00125" , "基本勤務の参照先");		
		masterName.put("IS00127" , "月間パターン");		
		masterName.put("IS00126" , "就業時間帯の参照先");					
		masterName.put("IS00129" , "平日時勤務設定");		
		masterName.put("IS00130" , "平日の勤務種類");		
		masterName.put("IS00131" , "就業時間帯");		
		masterName.put("IS00132" , "平日の勤務時間1");		
		masterName.put("IS00133" , "平日の開始時刻1");		
		masterName.put("IS00134" , "平日の終了時刻1");		
		masterName.put("IS00135" , "平日の勤務時間2");		
		masterName.put("IS00136" , "平日の開始時刻2");		
		masterName.put("IS00137" , "平日の終了時刻2");		
		masterName.put("IS00261" , "休日時勤務設定");		
		masterName.put("IS00128" , "勤務種類");		
		masterName.put("IS00138" , "休出時勤務設定");		
		masterName.put("IS00139" , "勤務種類");		
		masterName.put("IS00140" , "就業時間帯");		
		masterName.put("IS00141" , "休出の勤務時間1");		
		masterName.put("IS00142" , "休出の開始時刻1");		
		masterName.put("IS00143" , "休出の終了時刻1");		
		masterName.put("IS00144" , "休出の勤務時間2");		
		masterName.put("IS00145" , "休出の開始時刻2");		
		masterName.put("IS00146" , "休出の終了時刻2");		
		masterName.put("IS00156" , "法定休出時勤務設定");		
		masterName.put("IS00157" , "勤務種類");		
		masterName.put("IS00158" , "就業時間帯");		
		masterName.put("IS00159" , "法定休出の勤務時間1");		
		masterName.put("IS00160" , "法定休出の開始時刻1");		
		masterName.put("IS00161" , "法定休出の終了時刻1");		
		masterName.put("IS00162" , "法定休出の勤務時間2");		
		masterName.put("IS00163" , "法定休出の開始時刻2");		
		masterName.put("IS00164" , "法定休出の終了時刻2");		
		masterName.put("IS00165" , "法定外休出時勤務設定");		
		masterName.put("IS00166" , "勤務種類");		
		masterName.put("IS00167" , "就業時間帯");		
		masterName.put("IS00168" , "法定外休出の勤務時間1");		
		masterName.put("IS00169" , "法定外休出の開始時刻1");		
		masterName.put("IS00170" , "法定外休出の終了時刻1");		
		masterName.put("IS00171" , "法定外休出の勤務時間2");		
		masterName.put("IS00172" , "法定外休出の開始時刻2");		
		masterName.put("IS00173" , "法定外休出の終了時刻2");		
		masterName.put("IS00174" , "祝日時勤務設定");		
		masterName.put("IS00175" , "勤務種類");		
		masterName.put("IS00176" , "就業時間帯");		
		masterName.put("IS00177" , "法定外祝日の勤務時間1");		
		masterName.put("IS00178" , "法定外祝日の開始時刻1");		
		masterName.put("IS00179" , "法定外祝日の終了時刻1");		
		masterName.put("IS00180" , "法定外祝日の勤務時間2");		
		masterName.put("IS00181" , "法定外祝日の開始時刻2");		
		masterName.put("IS00182" , "法定外祝日の終了時刻2");		
		masterName.put("IS00147" , "公休出勤時勤務設定");		
		masterName.put("IS00148" , "勤務種類");		
		masterName.put("IS00149" , "就業時間帯");		
		masterName.put("IS00150" , "公休休出の勤務時間1");		
		masterName.put("IS00151" , "公休休出の開始時刻1");		
		masterName.put("IS00152" , "公休休出の終了時刻1");		
		masterName.put("IS00153" , "公休休出の勤務時間2");		
		masterName.put("IS00154" , "公休休出の開始時刻2");		
		masterName.put("IS00155" , "公休休出の終了時刻2");		
		masterName.put("IS00780" , "期間");		
		masterName.put("IS00781" , "開始日");		
		masterName.put("IS00782" , "終了日");		
		masterName.put("IS00192" , "月曜勤務設定");		
		masterName.put("IS00193" , "勤務種類");		
		masterName.put("IS00194" , "就業時間帯");		
		masterName.put("IS00195" , "月曜の勤務時間1");		
		masterName.put("IS00196" , "月曜の開始時刻1");		
		masterName.put("IS00197" , "月曜の終了時刻1");		
		masterName.put("IS00198" , "月曜の勤務時間2");		
		masterName.put("IS00199" , "月曜の開始時刻2");		
		masterName.put("IS00200" , "月曜の終了時刻2");		
		masterName.put("IS00201" , "火曜勤務設定");		
		masterName.put("IS00202" , "勤務種類");		
		masterName.put("IS00203" , "就業時間帯");		
		masterName.put("IS00204" , "火曜の勤務時間1");		
		masterName.put("IS00205" , "火曜の開始時刻1");		
		masterName.put("IS00206" , "火曜の終了時刻1");		
		masterName.put("IS00207" , "火曜の勤務時間2");		
		masterName.put("IS00208" , "火曜の開始時刻2");		
		masterName.put("IS00209" , "火曜の終了時刻2");		
		masterName.put("IS00210" , "水曜勤務設定");		
		masterName.put("IS00211" , "勤務種類");		
		masterName.put("IS00212" , "就業時間帯");		
		masterName.put("IS00213" , "水曜の勤務時間1");		
		masterName.put("IS00214" , "水曜の開始時刻1");		
		masterName.put("IS00215" , "水曜の終了時刻1");		
		masterName.put("IS00216" , "水曜の勤務時間2");		
		masterName.put("IS00217" , "水曜の開始時刻2");		
		masterName.put("IS00218" , "水曜の終了時刻2");		
		masterName.put("IS00219" , "木曜勤務設定");		
		masterName.put("IS00220" , "勤務種類");		
		masterName.put("IS00221" , "就業時間帯");		
		masterName.put("IS00222" , "木曜の勤務時間1");		
		masterName.put("IS00223" , "木曜の開始時刻1");		
		masterName.put("IS00224" , "木曜の終了時刻1");		
		masterName.put("IS00225" , "木曜の勤務時間2");		
		masterName.put("IS00226" , "木曜の開始時刻2");		
		masterName.put("IS00227" , "木曜の終了時刻2");		
		masterName.put("IS00228" , "金曜勤務設定");		
		masterName.put("IS00229" , "勤務種類");		
		masterName.put("IS00230" , "就業時間帯");		
		masterName.put("IS00231" , "金曜の勤務時間1");		
		masterName.put("IS00232" , "金曜の開始時刻1");		
		masterName.put("IS00233" , "金曜の終了時刻1");		
		masterName.put("IS00234" , "金曜の勤務時間2");		
		masterName.put("IS00235" , "金曜の開始時刻2");		
		masterName.put("IS00236" , "金曜の終了時刻2");		
		masterName.put("IS00237" , "土曜勤務設定");		
		masterName.put("IS00238" , "勤務種類");		
		masterName.put("IS00239" , "就業時間帯");		
		masterName.put("IS00240" , "土曜の勤務時間1");		
		masterName.put("IS00241" , "土曜の開始時刻1");		
		masterName.put("IS00242" , "土曜の終了時刻1");		
		masterName.put("IS00243" , "土曜の勤務時間2");		
		masterName.put("IS00244" , "土曜の開始時刻2");		
		masterName.put("IS00245" , "土曜の終了時刻2");		
		masterName.put("IS00183" , "日曜勤務設定");		
		masterName.put("IS00184" , "勤務種類");		
		masterName.put("IS00185" , "就業時間帯");		
		masterName.put("IS00186" , "日曜の勤務時間1");		
		masterName.put("IS00187" , "日曜の開始時刻1");		
		masterName.put("IS00188" , "日曜の終了時刻1");		
		masterName.put("IS00189" , "日曜の勤務時間2");		
		masterName.put("IS00190" , "日曜の開始時刻2");		
		masterName.put("IS00191" , "日曜の終了時刻2");		
		masterName.put("IS00254" , "期間");		
		masterName.put("IS00255" , "開始日");		
		masterName.put("IS00256" , "終了日");		
		masterName.put("IS00257" , "勤務種別");		
		masterName.put("IS00262" , "個人携帯電話番号");		
		masterName.put("IS00263" , "個人メールアドレス");		
		masterName.put("IS00264" , "個人携帯メールアドレス");		
		masterName.put("IS00265" , "緊急連絡先名1");		
		masterName.put("IS00266" , "緊急連絡先電話番号1");		
		masterName.put("IS00267" , "緊急連絡先メモ1");		
		masterName.put("IS00268" , "緊急連絡先名2");		
		masterName.put("IS00269" , "緊急連絡先電話番号2");		
		masterName.put("IS00270" , "緊急連絡先メモ2");		
		masterName.put("IS00271" , "会社携帯電話番号");		
		masterName.put("IS00272" , "会社メールアドレス");		
		masterName.put("IS00273" , "会社携帯メールアドレス");		
		masterName.put("IS00274" , "座席電話番号ダイヤルイン");		
		masterName.put("IS00275" , "座席電話番号内線");		
		masterName.put("IS00276" , "年休残数");		
		masterName.put("IS00277" , "前回年休付与日");		
		masterName.put("IS00278" , "年休付与");		
		masterName.put("IS00279" , "年休付与基準日");		
		masterName.put("IS00280" , "年休付与テーブル");		
		masterName.put("IS00281" , "次回年休付与日");		
		masterName.put("IS00282" , "次回年休付与日数");		
		masterName.put("IS00283" , "次回時間年休付与上限");		
		masterName.put("IS00284" , "年間所定労働日数");		
		masterName.put("IS00285" , "導入前労働日数");		
		masterName.put("IS00286" , "時間年休上限");		
		masterName.put("IS00287" , "時間年休上限時間");		
		masterName.put("IS00288" , "時間年休使用時間");		
		masterName.put("IS00289" , "時間年休残時間");		
		masterName.put("IS00290" , "半休上限");		
		masterName.put("IS00291" , "半休上限回数");		
		masterName.put("IS00292" , "半休使用回数");		
		masterName.put("IS00293" , "半休残回数");		
		masterName.put("IS00294" , "積立年休残数");		
		masterName.put("IS00295" , "特別休暇付与基準日");		
		masterName.put("IS00296" , "特別休暇管理");		
		masterName.put("IS00297" , "付与設定");		
		masterName.put("IS00298" , "付与日数");		
		masterName.put("IS00299" , "勤続年数テーブル");		
		masterName.put("IS00300" , "次回付与日");		
		masterName.put("IS00301" , "特別休暇残数");		
		masterName.put("IS00302" , "特別休暇付与基準日");		
		masterName.put("IS00303" , "特別休暇管理");		
		masterName.put("IS00304" , "付与設定");		
		masterName.put("IS00305" , "付与日数");		
		masterName.put("IS00306" , "勤続年数テーブル");		
		masterName.put("IS00307" , "次回付与日");		
		masterName.put("IS00308" , "特別休暇残数");		
		masterName.put("IS00309" , "特別休暇付与基準日");		
		masterName.put("IS00310" , "特別休暇管理");		
		masterName.put("IS00311" , "付与設定");		
		masterName.put("IS00312" , "付与日数");		
		masterName.put("IS00313" , "勤続年数テーブル");		
		masterName.put("IS00314" , "次回付与日");		
		masterName.put("IS00315" , "特別休暇残数");		
		masterName.put("IS00316" , "特別休暇付与基準日");		
		masterName.put("IS00317" , "特別休暇管理");		
		masterName.put("IS00318" , "付与設定");		
		masterName.put("IS00319" , "付与日数");		
		masterName.put("IS00320" , "勤続年数テーブル");		
		masterName.put("IS00321" , "次回付与日");		
		masterName.put("IS00322" , "特別休暇残数");		
		masterName.put("IS00323" , "特別休暇付与基準日");		
		masterName.put("IS00324" , "特別休暇管理");		
		masterName.put("IS00325" , "付与設定");		
		masterName.put("IS00326" , "付与日数");		
		masterName.put("IS00327" , "勤続年数テーブル");		
		masterName.put("IS00328" , "次回付与日");		
		masterName.put("IS00329" , "特別休暇残数");		
		masterName.put("IS00330" , "特別休暇付与基準日");		
		masterName.put("IS00331" , "特別休暇管理");		
		masterName.put("IS00332" , "付与設定");		
		masterName.put("IS00333" , "付与日数");		
		masterName.put("IS00334" , "勤続年数テーブル");		
		masterName.put("IS00335" , "次回付与日");		
		masterName.put("IS00336" , "特別休暇残数");		
		masterName.put("IS00337" , "特別休暇付与基準日");		
		masterName.put("IS00338" , "特別休暇管理");		
		masterName.put("IS00339" , "付与設定");		
		masterName.put("IS00340" , "付与日数");		
		masterName.put("IS00341" , "勤続年数テーブル");		
		masterName.put("IS00342" , "次回付与日");		
		masterName.put("IS00343" , "特別休暇残数");		
		masterName.put("IS00344" , "特別休暇付与基準日");		
		masterName.put("IS00345" , "特別休暇管理");		
		masterName.put("IS00346" , "付与設定");		
		masterName.put("IS00347" , "付与日数");		
		masterName.put("IS00348" , "勤続年数テーブル");		
		masterName.put("IS00349" , "次回付与日");		
		masterName.put("IS00350" , "特別休暇残数");		
		masterName.put("IS00351" , "特別休暇付与基準日");		
		masterName.put("IS00352" , "特別休暇管理");		
		masterName.put("IS00353" , "付与設定");		
		masterName.put("IS00354" , "付与日数");		
		masterName.put("IS00355" , "勤続年数テーブル");		
		masterName.put("IS00356" , "次回付与日");		
		masterName.put("IS00357" , "特別休暇残数");		
		masterName.put("IS00358" , "特別休暇付与基準日");		
		masterName.put("IS00359" , "特別休暇管理");		
		masterName.put("IS00360" , "付与設定");		
		masterName.put("IS00361" , "付与日数");		
		masterName.put("IS00362" , "勤続年数テーブル");		
		masterName.put("IS00363" , "次回付与日");		
		masterName.put("IS00364" , "特別休暇残数");		
		masterName.put("IS00366" , "代休残数");		
		masterName.put("IS00368" , "振休残数");		
		masterName.put("IS00369" , "公休残数");		
		masterName.put("IS00370" , "60H超休管理");		
		masterName.put("IS00371" , "発生単位");		
		masterName.put("IS00372" , "精算方法");		
		masterName.put("IS00374" , "60H超休残数");		
		masterName.put("IS00375" , "子の看護休暇管理");		
		masterName.put("IS00376" , "子の看護上限設定");		
		masterName.put("IS00377" , "本年度の子の看護上限日数");		
		masterName.put("IS00378" , "次年度の子の看護上限日数");		
		masterName.put("IS00379" , "子の看護使用日数");
		masterName.put("IS01101" , "子の看護使用時間");
		masterName.put("IS00380" , "介護休暇管理");		
		masterName.put("IS00381" , "介護上限設定");		
		masterName.put("IS00382" , "本年度の介護上限日数");		
		masterName.put("IS00383" , "次年度の介護上限日数");		
		masterName.put("IS00384" , "介護使用日数");
		masterName.put("IS01102" , "介護使用時間");
		masterName.put("IS00385" , "年休付与日");		
		masterName.put("IS00386" , "年休期限日");		
		masterName.put("IS00387" , "年休期限切れ状態");		
		masterName.put("IS00388" , "年休使用状況");		
		masterName.put("IS00389" , "付与数");		
		masterName.put("IS00390" , "付与日数");		
		masterName.put("IS00391" , "付与時間");		
		masterName.put("IS00392" , "使用数");		
		masterName.put("IS00393" , "使用日数");		
		masterName.put("IS00394" , "使用時間");		
		masterName.put("IS00395" , "残数");		
		masterName.put("IS00396" , "残日数");		
		masterName.put("IS00397" , "残時間");		
		masterName.put("IS00398" , "積立年休付与日");		
		masterName.put("IS00399" , "積立年休期限日");		
		masterName.put("IS00400" , "積立年休期限切れ状態");		
		masterName.put("IS00401" , "積立年休使用状況");		
		masterName.put("IS00403" , "付与数");		
		masterName.put("IS00404" , "使用数");		
		masterName.put("IS00405" , "使用日数");		
		masterName.put("IS00406" , "上限超過消滅日数");		
		masterName.put("IS00408" , "残数");		
		masterName.put("IS00409" , "付与日");		
		masterName.put("IS00410" , "期限日");		
		masterName.put("IS00411" , "期限切れ状態");		
		masterName.put("IS00412" , "使用状況");		
		masterName.put("IS00413" , "付与数");		
		masterName.put("IS00414" , "付与日数");		
		masterName.put("IS00415" , "付与時間");		
		masterName.put("IS00416" , "使用数");		
		masterName.put("IS00417" , "使用日数");		
		masterName.put("IS00418" , "使用時間");		
		masterName.put("IS00419" , "上限超過消滅日数");		
		masterName.put("IS00420" , "上限超過消滅時間");		
		masterName.put("IS00421" , "残数");		
		masterName.put("IS00422" , "残日数");		
		masterName.put("IS00423" , "残時間");		
		masterName.put("IS00424" , "付与日");		
		masterName.put("IS00425" , "期限日");		
		masterName.put("IS00426" , "期限切れ状態");		
		masterName.put("IS00427" , "使用状況");		
		masterName.put("IS00428" , "付与数");		
		masterName.put("IS00429" , "付与日数");		
		masterName.put("IS00430" , "付与時間");		
		masterName.put("IS00431" , "使用数");		
		masterName.put("IS00432" , "使用日数");		
		masterName.put("IS00433" , "使用時間");		
		masterName.put("IS00434" , "上限超過消滅日数");		
		masterName.put("IS00435" , "上限超過消滅時間");		
		masterName.put("IS00436" , "残数");		
		masterName.put("IS00437" , "残日数");		
		masterName.put("IS00438" , "残時間");		
		masterName.put("IS00439" , "付与日");		
		masterName.put("IS00440" , "期限日");		
		masterName.put("IS00441" , "期限切れ状態");		
		masterName.put("IS00442" , "使用状況");		
		masterName.put("IS00443" , "付与数");		
		masterName.put("IS00444" , "付与日数");		
		masterName.put("IS00445" , "付与時間");		
		masterName.put("IS00446" , "使用数");		
		masterName.put("IS00447" , "使用日数");		
		masterName.put("IS00448" , "使用時間");		
		masterName.put("IS00449" , "上限超過消滅日数");		
		masterName.put("IS00450" , "上限超過消滅時間");		
		masterName.put("IS00451" , "残数");		
		masterName.put("IS00452" , "残日数");		
		masterName.put("IS00453" , "残時間");		
		masterName.put("IS00454" , "付与日");		
		masterName.put("IS00455" , "期限日");		
		masterName.put("IS00456" , "期限切れ状態");		
		masterName.put("IS00457" , "使用状況");		
		masterName.put("IS00458" , "付与数");		
		masterName.put("IS00459" , "付与日数");		
		masterName.put("IS00460" , "付与時間");		
		masterName.put("IS00461" , "使用数");		
		masterName.put("IS00462" , "使用日数");		
		masterName.put("IS00463" , "使用時間");		
		masterName.put("IS00464" , "上限超過消滅日数");		
		masterName.put("IS00465" , "上限超過消滅時間");		
		masterName.put("IS00466" , "残数");		
		masterName.put("IS00467" , "残日数");		
		masterName.put("IS00468" , "残時間");		
		masterName.put("IS00469" , "付与日");		
		masterName.put("IS00470" , "期限日");		
		masterName.put("IS00471" , "期限切れ状態");		
		masterName.put("IS00472" , "使用状況");		
		masterName.put("IS00473" , "付与数");		
		masterName.put("IS00474" , "付与日数");		
		masterName.put("IS00475" , "付与時間");		
		masterName.put("IS00476" , "使用数");		
		masterName.put("IS00477" , "使用日数");		
		masterName.put("IS00478" , "使用時間");		
		masterName.put("IS00479" , "上限超過消滅日数");		
		masterName.put("IS00480" , "上限超過消滅時間");		
		masterName.put("IS00481" , "残数");		
		masterName.put("IS00482" , "残日数");		
		masterName.put("IS00483" , "残時間");		
		masterName.put("IS00484" , "付与日");		
		masterName.put("IS00485" , "期限日");		
		masterName.put("IS00486" , "期限切れ状態");		
		masterName.put("IS00487" , "使用状況");		
		masterName.put("IS00488" , "付与数");		
		masterName.put("IS00489" , "付与日数");		
		masterName.put("IS00490" , "付与時間");		
		masterName.put("IS00491" , "使用数");		
		masterName.put("IS00492" , "使用日数");		
		masterName.put("IS00493" , "使用時間");		
		masterName.put("IS00494" , "上限超過消滅日数");		
		masterName.put("IS00495" , "上限超過消滅時間");		
		masterName.put("IS00496" , "残数");		
		masterName.put("IS00497" , "残日数");		
		masterName.put("IS00498" , "残時間");		
		masterName.put("IS00499" , "付与日");		
		masterName.put("IS00500" , "期限日");		
		masterName.put("IS00501" , "期限切れ状態");		
		masterName.put("IS00502" , "使用状況");		
		masterName.put("IS00503" , "付与数");		
		masterName.put("IS00504" , "付与日数");		
		masterName.put("IS00505" , "付与時間");		
		masterName.put("IS00506" , "使用数");		
		masterName.put("IS00507" , "使用日数");		
		masterName.put("IS00508" , "使用時間");		
		masterName.put("IS00509" , "上限超過消滅日数");		
		masterName.put("IS00510" , "上限超過消滅時間");		
		masterName.put("IS00511" , "残数");		
		masterName.put("IS00512" , "残日数");		
		masterName.put("IS00513" , "残時間");		
		masterName.put("IS00514" , "付与日");		
		masterName.put("IS00515" , "期限日");		
		masterName.put("IS00516" , "期限切れ状態");		
		masterName.put("IS00517" , "使用状況");		
		masterName.put("IS00518" , "付与数");		
		masterName.put("IS00519" , "付与日数");		
		masterName.put("IS00520" , "付与時間");		
		masterName.put("IS00521" , "使用数");		
		masterName.put("IS00522" , "使用日数");		
		masterName.put("IS00523" , "使用時間");		
		masterName.put("IS00524" , "上限超過消滅日数");		
		masterName.put("IS00525" , "上限超過消滅時間");		
		masterName.put("IS00526" , "残数");		
		masterName.put("IS00527" , "残日数");		
		masterName.put("IS00528" , "残時間");		
		masterName.put("IS00529" , "付与日");		
		masterName.put("IS00530" , "期限日");		
		masterName.put("IS00531" , "期限切れ状態");		
		masterName.put("IS00532" , "使用状況");		
		masterName.put("IS00533" , "付与数");		
		masterName.put("IS00534" , "付与日数");		
		masterName.put("IS00535" , "付与時間");		
		masterName.put("IS00536" , "使用数");		
		masterName.put("IS00537" , "使用日数");		
		masterName.put("IS00538" , "使用時間");		
		masterName.put("IS00539" , "上限超過消滅日数");		
		masterName.put("IS00540" , "上限超過消滅時間");		
		masterName.put("IS00541" , "残数");		
		masterName.put("IS00542" , "残日数");		
		masterName.put("IS00543" , "残時間");		
		masterName.put("IS00544" , "付与日");		
		masterName.put("IS00545" , "期限日");		
		masterName.put("IS00546" , "期限切れ状態");		
		masterName.put("IS00547" , "使用状況");		
		masterName.put("IS00548" , "付与数");		
		masterName.put("IS00549" , "付与日数");		
		masterName.put("IS00550" , "付与時間");		
		masterName.put("IS00551" , "使用数");		
		masterName.put("IS00552" , "使用日数");		
		masterName.put("IS00553" , "使用時間");		
		masterName.put("IS00554" , "上限超過消滅日数");		
		masterName.put("IS00555" , "上限超過消滅時間");		
		masterName.put("IS00556" , "残数");		
		masterName.put("IS00557" , "残日数");		
		masterName.put("IS00558" , "残時間");		
		masterName.put("IS00559" , "特別休暇付与基準日");		
		masterName.put("IS00560" , "特別休暇管理");		
		masterName.put("IS00561" , "付与設定");		
		masterName.put("IS00562" , "付与日数");		
		masterName.put("IS00563" , "勤続年数テーブル");		
		masterName.put("IS00564" , "次回付与日");		
		masterName.put("IS00565" , "特別休暇残数");		
		masterName.put("IS00566" , "特別休暇付与基準日");		
		masterName.put("IS00567" , "特別休暇管理");		
		masterName.put("IS00568" , "付与設定");		
		masterName.put("IS00569" , "付与日数");		
		masterName.put("IS00570" , "勤続年数テーブル");		
		masterName.put("IS00571" , "次回付与日");		
		masterName.put("IS00572" , "特別休暇残数");		
		masterName.put("IS00573" , "特別休暇付与基準日");		
		masterName.put("IS00574" , "特別休暇管理");		
		masterName.put("IS00575" , "付与設定");		
		masterName.put("IS00576" , "付与日数");		
		masterName.put("IS00577" , "勤続年数テーブル");		
		masterName.put("IS00578" , "次回付与日");		
		masterName.put("IS00579" , "特別休暇残数");		
		masterName.put("IS00580" , "特別休暇付与基準日");		
		masterName.put("IS00581" , "特別休暇管理");		
		masterName.put("IS00582" , "付与設定");		
		masterName.put("IS00583" , "付与日数");		
		masterName.put("IS00584" , "勤続年数テーブル");		
		masterName.put("IS00585" , "次回付与日");		
		masterName.put("IS00586" , "特別休暇残数");		
		masterName.put("IS00587" , "特別休暇付与基準日");		
		masterName.put("IS00588" , "特別休暇管理");		
		masterName.put("IS00589" , "付与設定");		
		masterName.put("IS00590" , "付与日数");		
		masterName.put("IS00591" , "勤続年数テーブル");		
		masterName.put("IS00592" , "次回付与日");		
		masterName.put("IS00593" , "特別休暇残数");		
		masterName.put("IS00594" , "特別休暇付与基準日");		
		masterName.put("IS00595" , "特別休暇管理");		
		masterName.put("IS00596" , "付与設定");		
		masterName.put("IS00597" , "付与日数");		
		masterName.put("IS00598" , "勤続年数テーブル");		
		masterName.put("IS00599" , "次回付与日");		
		masterName.put("IS00600" , "特別休暇残数");		
		masterName.put("IS00601" , "特別休暇付与基準日");		
		masterName.put("IS00602" , "特別休暇管理");		
		masterName.put("IS00603" , "付与設定");		
		masterName.put("IS00604" , "付与日数");		
		masterName.put("IS00605" , "勤続年数テーブル");		
		masterName.put("IS00606" , "次回付与日");		
		masterName.put("IS00607" , "特別休暇残数");		
		masterName.put("IS00608" , "特別休暇付与基準日");		
		masterName.put("IS00609" , "特別休暇管理");		
		masterName.put("IS00610" , "付与設定");		
		masterName.put("IS00611" , "付与日数");		
		masterName.put("IS00612" , "勤続年数テーブル");		
		masterName.put("IS00613" , "次回付与日");		
		masterName.put("IS00614" , "特別休暇残数");		
		masterName.put("IS00615" , "特別休暇付与基準日");		
		masterName.put("IS00616" , "特別休暇管理");		
		masterName.put("IS00617" , "付与設定");		
		masterName.put("IS00618" , "付与日数");		
		masterName.put("IS00619" , "勤続年数テーブル");		
		masterName.put("IS00620" , "次回付与日");		
		masterName.put("IS00621" , "特別休暇残数");		
		masterName.put("IS00622" , "特別休暇付与基準日");		
		masterName.put("IS00623" , "特別休暇管理");		
		masterName.put("IS00624" , "付与設定");		
		masterName.put("IS00625" , "付与日数");		
		masterName.put("IS00626" , "勤続年数テーブル");		
		masterName.put("IS00627" , "次回付与日");		
		masterName.put("IS00628" , "特別休暇残数");		
		masterName.put("IS00629" , "付与日");		
		masterName.put("IS00630" , "期限日");		
		masterName.put("IS00631" , "期限切れ状態");		
		masterName.put("IS00632" , "使用状況");		
		masterName.put("IS00633" , "付与数");		
		masterName.put("IS00634" , "付与日数");		
		masterName.put("IS00635" , "付与時間");		
		masterName.put("IS00636" , "使用数");		
		masterName.put("IS00637" , "使用日数");		
		masterName.put("IS00638" , "使用時間");		
		masterName.put("IS00639" , "上限超過消滅日数");		
		masterName.put("IS00640" , "上限超過消滅時間");		
		masterName.put("IS00641" , "残数");		
		masterName.put("IS00642" , "残日数");		
		masterName.put("IS00643" , "残時間");		
		masterName.put("IS00644" , "付与日");		
		masterName.put("IS00645" , "期限日");		
		masterName.put("IS00646" , "期限切れ状態");		
		masterName.put("IS00647" , "使用状況");		
		masterName.put("IS00648" , "付与数");		
		masterName.put("IS00649" , "付与日数");		
		masterName.put("IS00650" , "付与時間");		
		masterName.put("IS00651" , "使用数");		
		masterName.put("IS00652" , "使用日数");		
		masterName.put("IS00653" , "使用時間");		
		masterName.put("IS00654" , "上限超過消滅日数");		
		masterName.put("IS00655" , "上限超過消滅時間");		
		masterName.put("IS00656" , "残数");		
		masterName.put("IS00657" , "残日数");		
		masterName.put("IS00658" , "残時間");		
		masterName.put("IS00659" , "付与日");		
		masterName.put("IS00660" , "期限日");		
		masterName.put("IS00661" , "期限切れ状態");		
		masterName.put("IS00662" , "使用状況");		
		masterName.put("IS00663" , "付与数");		
		masterName.put("IS00664" , "付与日数");		
		masterName.put("IS00665" , "付与時間");		
		masterName.put("IS00666" , "使用数");		
		masterName.put("IS00667" , "使用日数");		
		masterName.put("IS00668" , "使用時間");		
		masterName.put("IS00669" , "上限超過消滅日数");		
		masterName.put("IS00670" , "上限超過消滅時間");		
		masterName.put("IS00671" , "残数");		
		masterName.put("IS00672" , "残日数");		
		masterName.put("IS00673" , "残時間");		
		masterName.put("IS00674" , "付与日");		
		masterName.put("IS00675" , "期限日");		
		masterName.put("IS00676" , "期限切れ状態");		
		masterName.put("IS00677" , "使用状況");		
		masterName.put("IS00678" , "付与数");		
		masterName.put("IS00679" , "付与日数");		
		masterName.put("IS00680" , "付与時間");		
		masterName.put("IS00681" , "使用数");		
		masterName.put("IS00682" , "使用日数");		
		masterName.put("IS00683" , "使用時間");		
		masterName.put("IS00684" , "上限超過消滅日数");		
		masterName.put("IS00685" , "上限超過消滅時間");		
		masterName.put("IS00686" , "残数");		
		masterName.put("IS00687" , "残日数");		
		masterName.put("IS00688" , "残時間");		
		masterName.put("IS00689" , "付与日");		
		masterName.put("IS00690" , "期限日");		
		masterName.put("IS00691" , "期限切れ状態");		
		masterName.put("IS00692" , "使用状況");		
		masterName.put("IS00693" , "付与数");		
		masterName.put("IS00694" , "付与日数");		
		masterName.put("IS00695" , "付与時間");		
		masterName.put("IS00696" , "使用数");		
		masterName.put("IS00697" , "使用日数");		
		masterName.put("IS00698" , "使用時間");		
		masterName.put("IS00699" , "上限超過消滅日数");		
		masterName.put("IS00700" , "上限超過消滅時間");		
		masterName.put("IS00701" , "残数");		
		masterName.put("IS00702" , "残日数");		
		masterName.put("IS00703" , "残時間");		
		masterName.put("IS00704" , "付与日");		
		masterName.put("IS00705" , "期限日");		
		masterName.put("IS00706" , "期限切れ状態");		
		masterName.put("IS00707" , "使用状況");		
		masterName.put("IS00708" , "付与数");		
		masterName.put("IS00709" , "付与日数");		
		masterName.put("IS00710" , "付与時間");		
		masterName.put("IS00711" , "使用数");		
		masterName.put("IS00712" , "使用日数");		
		masterName.put("IS00713" , "使用時間");		
		masterName.put("IS00714" , "上限超過消滅日数");		
		masterName.put("IS00715" , "上限超過消滅時間");		
		masterName.put("IS00716" , "残数");		
		masterName.put("IS00717" , "残日数");		
		masterName.put("IS00718" , "残時間");		
		masterName.put("IS00719" , "付与日");		
		masterName.put("IS00720" , "期限日");		
		masterName.put("IS00721" , "期限切れ状態");		
		masterName.put("IS00722" , "使用状況");		
		masterName.put("IS00723" , "付与数");		
		masterName.put("IS00724" , "付与日数");		
		masterName.put("IS00725" , "付与時間");		
		masterName.put("IS00726" , "使用数");		
		masterName.put("IS00727" , "使用日数");		
		masterName.put("IS00728" , "使用時間");		
		masterName.put("IS00729" , "上限超過消滅日数");		
		masterName.put("IS00730" , "上限超過消滅時間");		
		masterName.put("IS00731" , "残数");		
		masterName.put("IS00732" , "残日数");		
		masterName.put("IS00733" , "残時間");		
		masterName.put("IS00734" , "付与日");		
		masterName.put("IS00735" , "期限日");		
		masterName.put("IS00736" , "期限切れ状態");		
		masterName.put("IS00737" , "使用状況");		
		masterName.put("IS00738" , "付与数");		
		masterName.put("IS00739" , "付与日数");		
		masterName.put("IS00740" , "付与時間");		
		masterName.put("IS00741" , "使用数");		
		masterName.put("IS00742" , "使用日数");		
		masterName.put("IS00743" , "使用時間");		
		masterName.put("IS00744" , "上限超過消滅日数");		
		masterName.put("IS00745" , "上限超過消滅時間");		
		masterName.put("IS00746" , "残数");		
		masterName.put("IS00747" , "残日数");		
		masterName.put("IS00748" , "残時間");		
		masterName.put("IS00749" , "付与日");		
		masterName.put("IS00750" , "期限日");		
		masterName.put("IS00751" , "期限切れ状態");		
		masterName.put("IS00752" , "使用状況");		
		masterName.put("IS00753" , "付与数");		
		masterName.put("IS00754" , "付与日数");		
		masterName.put("IS00755" , "付与時間");		
		masterName.put("IS00756" , "使用数");		
		masterName.put("IS00757" , "使用日数");		
		masterName.put("IS00758" , "使用時間");		
		masterName.put("IS00759" , "上限超過消滅日数");		
		masterName.put("IS00760" , "上限超過消滅時間");		
		masterName.put("IS00761" , "残数");		
		masterName.put("IS00762" , "残日数");		
		masterName.put("IS00763" , "残時間");		
		masterName.put("IS00764" , "付与日");		
		masterName.put("IS00765" , "期限日");		
		masterName.put("IS00766" , "期限切れ状態");		
		masterName.put("IS00767" , "使用状況");		
		masterName.put("IS00768" , "付与数");		
		masterName.put("IS00769" , "付与日数");		
		masterName.put("IS00770" , "付与時間");		
		masterName.put("IS00771" , "使用数");		
		masterName.put("IS00772" , "使用日数");		
		masterName.put("IS00773" , "使用時間");		
		masterName.put("IS00774" , "上限超過消滅日数");		
		masterName.put("IS00775" , "上限超過消滅時間");		
		masterName.put("IS00776" , "残数");		
		masterName.put("IS00777" , "残日数");		
		masterName.put("IS00778" , "残時間");		
		masterName.put("IS00779" , "カード番号");		

		return masterName;
			
	
	}
}
