package nts.uk.screen.at.app.dailyperformance.correction.datadialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.dailyfix.IAppliCalDaiCorrecRepository;
import nts.uk.ctx.at.record.app.query.tasksupinforitemsetting.TaskSupInfoChoiceDetailsQuery;
import nts.uk.ctx.at.record.app.query.tasksupinforitemsetting.TaskSupInfoItemAndSelectInforQueryDto;
import nts.uk.ctx.at.record.dom.algorithm.masterinfo.CodeNameInfo;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHist;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHistRepo;
import nts.uk.ctx.at.request.app.find.application.applicationlist.AppWithDetailExportDto;
import nts.uk.ctx.at.request.app.find.application.applicationlist.ApplicationListForScreen;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.BusinessType;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.repository.BusinessTypesRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceCorrectionProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceScreenRepo;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AffEmploymentHistoryDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.type.TypeLink;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DataDialogWithTypeProcessor {

	@Inject
	private DailyPerformanceScreenRepo repo;
	
	@Inject
	private ApplicationListForScreen applicationListForScreen;
	
	@Inject
	private IAppliCalDaiCorrecRepository iAppliCalDaiCorrecRepository;
	
	@Inject
	private BusinessTypesRepository businessTypesRepository;
	
	@Inject
	private BPSettingRepository bpSettingRepository;
	
	@Inject
	private TaskingRepository taskingRepository;
	
	@Inject
	private TaskInitialSelHistRepo taskInitialSelHistRepo;
	
	@Inject
	private TaskSupInfoChoiceDetailsQuery taskSupInfoChoiceDetailsQuery;

	// 勤務種類
	public CodeNameType getDutyType(String companyId, String workTypeCode, String employmentCode) {
		List<WorkTypeChangedDto> dtos = repo.findWorkTypeChanged(employmentCode, workTypeCode, companyId);
		Set<String> workTypeCodes = dtos.stream().map(x -> x.getTypeCode()).collect(Collectors.toSet());
//		if (!workTypeCode.equals("") && workTypeCodes.isEmpty())
//			return CodeNameType.create(TypeLink.DUTY.value, new ArrayList<>())
//					.createError(true);
		List<CodeName> codeNames = repo.findWorkType(companyId, workTypeCodes);
		return CodeNameType.create(TypeLink.DUTY.value, codeNames).createError(!workTypeCodes.isEmpty());
	}

	// 勤務種類
	public CodeNameType getDutyTypeAll(String companyId) {
		List<CodeName> codeNames = repo.findWorkType(companyId, new HashSet<>());
		return CodeNameType.create(TypeLink.DUTY.value, codeNames);
	}

	// 就業時間帯
	public CodeNameType getWorkHours(String companyId, String workplaceId) {
		List<WorkTimeWorkplaceDto> workTimeDtos = repo.findWorkHours(companyId, workplaceId);
		List<String> shiftCodes = workTimeDtos.isEmpty() ? Collections.emptyList()
				: workTimeDtos.stream().map(x -> x.getWorkTimeID()).collect(Collectors.toList());
		List<CodeName> codeNames = repo.findWorkTimeZone(companyId, shiftCodes);
		return CodeNameType.create(TypeLink.WORK_HOURS.value, codeNames);
	}

	// 就業時間帯
	public CodeNameType getWorkHoursAll(String companyId) {
		List<CodeName> codeNames = repo.findWorkTimeZone(companyId, new ArrayList<>());
		return CodeNameType.create(TypeLink.WORK_HOURS.value, codeNames);
	}

	// 勤務場所
	public CodeNameType getServicePlace(String companyId) {
		List<CodeName> codeNames = repo.findWorkplaceLocation(companyId);
		return CodeNameType.create(TypeLink.SERVICE_PLACE.value, codeNames);
	}

	// 乖離理由
	public CodeNameType getReason(String companyId) {
		List<CodeName> codeNames = repo.findReason(companyId);
		return CodeNameType.create(TypeLink.REASON.value, codeNames);
	}

	// 職場--
	public CodeNameType getWorkPlace(String companyId, GeneralDate date) {
		List<CodeName> codeNames = repo.findWorkplace(companyId, date);
		return CodeNameType.create(TypeLink.WORKPLACE.value, codeNames);
	}

	// 分類
	public CodeNameType getClassification(String companyId) {
		List<CodeName> codeNames = repo.findClassification(companyId);
		return CodeNameType.create(TypeLink.CLASSIFICATION.value, codeNames);
	}

	// 職位--
	public CodeNameType getPossition(String companyId, GeneralDate date) {
		List<CodeName> codeNames = repo.findJobInfo(companyId, date);
		return CodeNameType.create(TypeLink.POSSITION.value, codeNames);
	}

	// 雇用区分
	public CodeNameType getEmployment(String companyId) {
		List<CodeName> codeNames = repo.findEmployment(companyId);
		return CodeNameType.create(TypeLink.EMPLOYMENT.value, codeNames);
	}

	//勤務種別
	public CodeNameType getBussinessType(String companyId) {
		List<BusinessType> lstBussinessType = businessTypesRepository.findAll(companyId);
		List<CodeName> codeNames = lstBussinessType.stream().map(x -> new CodeName(x.getBusinessTypeCode().v(), x.getBusinessTypeName().v(), "")).collect(Collectors.toList());
		return CodeNameType.create(TypeLink.BUSINESS_TYPE.value, codeNames);
	}
	
	// 加給
	public CodeNameType getAddition(String companyId) {
		List<BonusPaySetting> lstBussinessType = this.bpSettingRepository.getAllBonusPaySetting(companyId);
		List<CodeName> codeNames = lstBussinessType.stream()
				.map(x -> new CodeName(x.getCode().v(), x.getName().v(), ""))
				.collect(Collectors.toList());
		return CodeNameType.create(TypeLink.ADDITION.value, codeNames);
	}
	
	// 作業
	public CodeNameType getWorkWithFrameNo(String companyId, Integer frameNo) {
		List<Task> lstTask = taskingRepository.getListTask(companyId, new TaskFrameNo(frameNo));
		List<CodeName> codeNames = lstTask.stream()
				.map(x -> new CodeName(x.getCode().v(), x.getDisplayInfo().getTaskName().v(), String.valueOf(x.getTaskFrameNo().v())))
				.collect(Collectors.toList());
		return CodeNameType.create(TypeLink.WORK.value, codeNames);
	}
	
	// 作業
	public CodeNameType getWork(String companyId) {
		List<Task> lstTask = taskingRepository.getListTask(companyId);
		List<CodeName> codeNames = lstTask.stream()
				.map(x -> new CodeName(x.getCode().v(), x.getDisplayInfo().getTaskName().v(), String.valueOf(x.getTaskFrameNo().v())))
				.collect(Collectors.toList());
		return CodeNameType.create(TypeLink.WORK.value, codeNames);
	}
	
	// 作業補足選択肢
	public CodeNameType getWorkSupOption(GeneralDate ymd, int itemId) {
		List<TaskSupInfoItemAndSelectInforQueryDto> datas = taskSupInfoChoiceDetailsQuery.get(ymd, itemId);
		List<CodeName> codeNames = datas.stream()
				.map(x -> new CodeName(x.getCode(), x.getName(), x.getHistoryId()))
				.collect(Collectors.toList());
		return CodeNameType.create(TypeLink.WORK_SUP_OPTION.value, codeNames);
	}
	
	public CodeName getTypeDialog(int type, ParamDialog param) {
		String companyId = AppContexts.user().companyId();
		Optional<CodeName> codeName;
		switch (type) {
		case 1:
			// KDL002
			if (param.getItemId() != null && (param.getItemId() == 28 || param.getItemId() == 1)) {
				codeName = this.getDutyTypeAll(companyId).getCodeNames().stream()
						.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
				if(!codeName.isPresent()) return new CodeName(param.getWorkTypeCode(), TextResource.localize("KDW003_81"), "")
						.createError(ErrorTypeWorkType.NO_GROUP.code);
				if(param.getItemId() == 1) return codeName.get().createError(ErrorTypeWorkType.MASTER.code);
				
				
				AffEmploymentHistoryDto aff = repo.getAffEmploymentHistory(companyId, param.getEmployeeId(), param.getDate());
				CodeNameType codeNameType = this.getDutyType(companyId, param.getValueOld(),
						aff == null ? "" : aff.getEmploymentCode());
				Optional<CodeName> codeNameG = codeNameType.getCodeNames().stream().filter(x -> x.getCode().equals(param.getSelectCode()))
						.findFirst();
				CodeName codeNameTemp = codeNameG.isPresent() ? codeNameG.get().createError(ErrorTypeWorkType.MASTER.code)
						: codeName.get()
								.createError(codeNameType.getError() ? ErrorTypeWorkType.GROUP.code: ErrorTypeWorkType.MASTER.code);
				return codeNameTemp;
			} else {
				codeName = this.getDutyTypeAll(companyId).getCodeNames().stream()
						.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
				return codeName.isPresent() ? codeName.get() : null;
			}
		case 2:
			// KDL001
			codeName = this.getWorkHours(companyId, param.getWorkplaceId()).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 3:
			// KDL010
			codeName = this.getServicePlace(companyId).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 4:
			// KDL032
			codeName = this.getReason(companyId).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode()) && Integer.parseInt(x.getId()) == (DailyPerformanceCorrectionProcessor.DEVIATION_REASON_MAP.get(Integer.parseInt(param.getWorkTypeCode())))).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 5:
			// CDL008
			codeName = this.getWorkPlace(companyId, param.getDate()).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 6:
			// KCP002
			codeName = this.getClassification(companyId).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 7:
			// KCP003
			codeName = this.getPossition(companyId, param.getDate()).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 8:
			// KCP001
			codeName = this.getEmployment(companyId).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 14:
			// KCP001
			codeName = this.getBussinessType(companyId).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		
		case 15:
			// KDL012
			int workFrameNo = 0;
			for(Entry<Integer, List<Integer>> entry : DailyPerformanceCorrectionProcessor.WORK_FRAME_MAP.entrySet()) {
				if(entry.getValue().contains(param.getItemId())) {
					workFrameNo = entry.getKey();
					break;
				}
			}
			codeName = this.getWorkWithFrameNo(companyId, workFrameNo).getCodeNames().stream()
					.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
		case 19:
			// KDL013
			codeName = this.getWorkSupOption(param.getDate(), param.getItemId()).getCodeNames().stream()
				.filter(x -> x.getCode().equals(param.getSelectCode())).findFirst();
			return codeName.isPresent() ? codeName.get().createError(ErrorTypeWorkType.MASTER.code) :  new CodeName(param.getSelectCode(), TextResource.localize("KDW003_81"), "")
					.createError(ErrorTypeWorkType.NO_GROUP.code);
	
		default:
			return null;
		}
	}

	public List<CodeName> getAllTypeDialog(int type, ParamDialog param) {
		String companyId = AppContexts.user().companyId();
		switch (type) {
		case 1:
			// KDL002
			if (param.getItemId() != null && param.getItemId() == 28 && !param.getSelectCode().equals("")) {
				AffEmploymentHistoryDto aff = repo.getAffEmploymentHistory(companyId, param.getEmployeeId(), param.getDate());
				return this.getDutyType(companyId, param.getSelectCode(), aff == null ? "" : aff.getEmploymentCode())
						.getCodeNames();
			} else {
				return this.getDutyTypeAll(companyId).getCodeNames();
			}
		case 2:
			// KDL001
			return this.getWorkHours(companyId, param.getWorkplaceId()).getCodeNames();
		case 3:
			// KDL010
			return this.getServicePlace(companyId).getCodeNames();
		case 4:
			// KDL032
			return this.getReason(companyId).getCodeNames();
		case 5:
			// CDL008
			return this.getWorkPlace(companyId, param.getDate()).getCodeNames();
		case 6:
			// KCP002
			return this.getClassification(companyId).getCodeNames();
		case 7:
			// KCP003
			return this.getPossition(companyId, param.getDate()).getCodeNames();
		case 8:
			// KCP001
			return this.getEmployment(companyId).getCodeNames();
			
		case 14:
			// KCP001
			return this.getBussinessType(companyId).getCodeNames();
			
		case 15:
			// KDL012
			int workFrameNo = 0;
			for(Entry<Integer, List<Integer>> entry : DailyPerformanceCorrectionProcessor.WORK_FRAME_MAP.entrySet()) {
				if(entry.getValue().contains(param.getItemId())) {
					workFrameNo = entry.getKey();
					break;
				}
			}
			return this.getWorkWithFrameNo(companyId, workFrameNo).getCodeNames();
		case 19:
			// KDL013
			return this.getWorkSupOption(param.getDate(), param.getItemId()).getCodeNames();
		default:
			return null;
		}
	}

	public Map<Integer, Map<String, CodeName>> getAllCodeName(List<Integer> types, String companyId, GeneralDate date) {
		return types.stream().collect(Collectors.toMap(type -> type, type -> {
			switch (type) {
			case 1:
				// KDL002
				return toMap(this.getDutyTypeAll(companyId).getCodeNames());
			case 2:
				// KDL001
				return toMap(this.getWorkHoursAll(companyId).getCodeNames());
			case 3:
				// KDL010
				return toMap(this.getServicePlace(companyId).getCodeNames());
			case 4:
				// KDL032
				return new HashMap<>();
			case 5:
				// CDL008 WPL
				return toMapID(this.getWorkPlace(companyId, date).getCodeNames());
			case 6:
				// KCP002
				return toMap(this.getClassification(companyId).getCodeNames());
			case 7:
				// KCP003 POS
				return toMapID(this.getPossition(companyId, date).getCodeNames());
			case 8:
				// KCP001
				return toMap(this.getEmployment(companyId).getCodeNames());
			case 14:
				// CDL024
				return toMap(this.getBussinessType(companyId).getCodeNames());
			default:
				return new HashMap<>();
			}
		}));
	}
	
	private Map<String, CodeName> toMap(List<CodeName> codeNames) {
		return codeNames.stream().filter(distinctByKey(x -> x.getCode()))
				.collect(Collectors.toMap(x -> x.getCode(), x -> x));
	}

	private Map<String, CodeName> toMapID(List<CodeName> codeNames) {
		return codeNames.stream().filter(distinctByKey(x -> x.getId()))
				.collect(Collectors.toMap(x -> x.getId(), x -> x));
	}
	
	public Optional<CodeName> getCodeNameWithId(int type, GeneralDate date, String id) {
		String companyId = AppContexts.user().companyId();
		if (type == TypeLink.POSSITION.value) {
			return repo.findJobInfoId(companyId, date, id);
		} else if (type == TypeLink.WORKPLACE.value) {
			return repo.findWorkplaceId(companyId, date, id);
		}

		return null;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = new HashSet<>();
		return t -> seen.add(keyExtractor.apply(t));
	}
	
	// get application NO19
	public List<Integer> getNameAppliction(){
		String companyId = AppContexts.user().companyId();
		return iAppliCalDaiCorrecRepository.findByCom(companyId).stream().map(x -> x.getAppType().value).collect(Collectors.toList());
//		List<AppWithDetailExportDto> lstApp = applicationListForScreen.getAppWithOvertimeInfo(companyId).stream()
//				.map(x -> {
//					x.setAppType(convertTypeUi(x));
//					return x;
//				}).filter(x -> lstAppSlect.contains(x.getAppType())).collect(Collectors.toList());
//		List<EnumConstant> result =  lstApp.stream().map(x -> {
//			return new EnumConstant(x.getAppType(), x.getAppName(), x.getOvertimeAtr() == null ? "" : x.getOvertimeAtr().toString());
//		}).collect(Collectors.toList());
//		return result;
	}
	
	public int convertTypeUi(AppWithDetailExportDto dto) {
		switch (dto.getAppType()) {
		case 0:
			return dto.getOvertimeAtr() -1;
		case 7:
			return dto.getAppType() + dto.getStampAtr() + 1;
		case 6:
			return 7;
		case 1:
		case 2:
		case 4:
			return dto.getAppType() + 2;
		case 10:
			return 14;

		default:
			return dto.getAppType() + 999;
		}
	}
	
	public Map<Integer, Map<String, CodeNameInfo>> getAllDataMaster(String companyId, GeneralDate date,
			List<Integer> lstDivNO) {
		return DPText.ALL_ITEM_TYPE_MASTER.stream().collect(Collectors.toMap(type -> type, type -> {
			switch (type) {
			case 1:
				// KDL002
				return toMapMaster(this.getDutyTypeAll(companyId).getCodeNames());
			case 2:
				// KDL001
				return toMapMaster(this.getWorkHoursAll(companyId).getCodeNames());
			case 3:
				// KDL010
				return toMapMaster(this.getServicePlace(companyId).getCodeNames());
			case 4:
				// KDL032
				CodeNameType codeNameReason = this.getReason(companyId);
				List<CodeName> codeNames = codeNameReason.getCodeNames().stream()
						.filter(x -> lstDivNO.contains(Integer.parseInt(x.getId()))).collect(Collectors.toList());
				return toMapIDMaster(codeNames);
			case 5:
				// CDL008 WPL
				return toMapIDMaster(this.getWorkPlace(companyId, date).getCodeNames());
			case 6:
				// KCP002
				return toMapMaster(this.getClassification(companyId).getCodeNames());
			case 7:
				// KCP003 POS
				return toMapIDMaster(this.getPossition(companyId, date).getCodeNames());
			case 8:
				// KCP001
				return toMapMaster(this.getEmployment(companyId).getCodeNames());
			case 14:
				// CDL024
				return toMapMaster(this.getBussinessType(companyId).getCodeNames());
			default:
				return new HashMap<>();
			}
		}));
	}
	
	public Map<Integer, Map<String, CodeName>> getAllCodeNameWT(List<Integer> types, String companyId, String employeeId, String workTypeOld, GeneralDate date) {
		return types.stream().collect(Collectors.toMap(type -> type, type -> {
			switch (type) {
			case 1:
				// KDL002
				AffEmploymentHistoryDto aff = repo.getAffEmploymentHistory(companyId, employeeId, date);
				if(aff == null){
					return new HashMap<>();
				}
				List<WorkTypeChangedDto> dtos = repo.findWorkTypeChanged(aff.getEmploymentCode(), workTypeOld, companyId);
			    Set<String> workTypeCodes = dtos.stream().map(x -> x.getTypeCode()).collect(Collectors.toSet());
				if(workTypeCodes.isEmpty()){
					return toMap(repo.findWorkType(companyId, new HashSet<>()));
				}
				
		        List<CodeName> codeNameResults = repo.findWorkType(companyId, workTypeCodes);
				return toMap(codeNameResults);
			case 2:
				// KDL001
				return toMap(this.getWorkHoursAll(companyId).getCodeNames());
			case 3:
				// KDL010
				return toMap(this.getServicePlace(companyId).getCodeNames());
			case 4:
				// KDL032
				return new HashMap<>();
			case 5:
				// CDL008 WPL
				return toMapID(this.getWorkPlace(companyId, date).getCodeNames());
			case 6:
				// KCP002
				return toMap(this.getClassification(companyId).getCodeNames());
			case 7:
				// KCP003 POS
				return toMapID(this.getPossition(companyId, date).getCodeNames());
			case 8:
				// KCP001
				return toMap(this.getEmployment(companyId).getCodeNames());
			case 14:
				// CDL024
				return toMap(this.getBussinessType(companyId).getCodeNames());				
			default:
				return new HashMap<>();
			}
		}));
	}
	
	public Map<Integer, Map<String, CodeName>> getAllCodeNameByItemId(GeneralDate date, List<Integer> itemIds) {
		return itemIds.stream().collect(Collectors.toMap(itemId -> itemId, itemId -> {
				return toMap(this.getWorkSupOption(date, itemId).getCodeNames());
		}));
	}
	private Map<String, CodeNameInfo> toMapMaster(List<CodeName> codeNames) {
		return codeNames.stream().filter(distinctByKey(x -> x.getCode()))
				.collect(Collectors.toMap(x -> x.getCode(), x -> new CodeNameInfo(x.getCode(), x.getName(), x.getId())));
	}

	private Map<String, CodeNameInfo> toMapIDMaster(List<CodeName> codeNames) {
		return codeNames.stream().filter(distinctByKey(x -> x.getId()))
				.collect(Collectors.toMap(x -> x.getId(), x ->  new CodeNameInfo(x.getCode(), x.getName(), x.getId())));
	}
	
	public List<TaskInitialSelHist> getTaskInitialSel(String companyID) {
		return taskInitialSelHistRepo.getByCid(companyID);
	}
	
}
