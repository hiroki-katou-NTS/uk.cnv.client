/**
 *
 */
package nts.uk.ctx.pereg.app.find.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.employeeinfo.TimeZoneScheduledMasterAtr;
import nts.uk.ctx.at.schedule.dom.shift.pattern.monthly.MonthlyPatternRepository;
import nts.uk.ctx.at.shared.app.find.workingcondition.WorkingConditionDto;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.repository.BusinessTypesRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.PaymentMethod;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.UpperLimitSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveAppSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPSettingRepository;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.HourlyPaymentAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.ManageAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.NotUseAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkScheduleBasicCreMethod;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkScheduleMasterReferenceAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.worktime.common.AbolishAtr;
import nts.uk.ctx.at.shared.dom.worktime.workplace.WorkTimeWorkplaceRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSet;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSetCheck;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.YearHolidayRepository;
import nts.uk.ctx.bs.employee.app.find.workplace.affiliate.AffWorlplaceHistItemDto;
import nts.uk.ctx.bs.employee.app.find.workplace.config.info.WorkplaceConfigInfoFinder;
import nts.uk.ctx.bs.employee.dom.classification.ClassificationRepository;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.ctx.bs.employee.dom.employment.history.SalarySegment;
import nts.uk.ctx.bs.employee.dom.jobtitle.info.JobTitleInfoRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.TempAbsenceRepositoryFrame;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.person.dom.person.info.BloodType;
import nts.uk.ctx.bs.person.dom.person.info.GenderPerson;
import nts.uk.ctx.pereg.app.find.person.info.item.CodeNameRefTypeDto;
import nts.uk.ctx.pereg.app.find.person.info.item.EnumRefConditionDto;
import nts.uk.ctx.pereg.app.find.person.info.item.MasterRefConditionDto;
import nts.uk.ctx.pereg.app.find.person.info.item.SelectionItemDto;
import nts.uk.ctx.pereg.app.find.person.setting.init.item.SelectionInitDto;
import nts.uk.ctx.pereg.app.find.person.setting.selectionitem.selection.SelectionFinder;
import nts.uk.ctx.pereg.app.find.processor.LayoutingProcessor;
import nts.uk.ctx.pereg.dom.person.info.category.PerInfoCategoryRepositoty;
import nts.uk.ctx.pereg.dom.person.info.category.PersonEmployeeType;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.ReferenceTypes;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsuranceOfficeRepository;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.empsocialinsgradehis.CalculationAtr;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.dto.PeregDto;

/**
 * @author danpv
 * @param <E>
 *
 */
@Stateless
public class ComboBoxRetrieveFactory {


	@Inject
	private SelectionFinder selectionFinder;

	@Inject
	private ClassificationRepository classificationRepo;

	@Inject
	private EmploymentRepository employmentRepo;

	@Inject
	private BusinessTypesRepository businessTypeRepo;

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private TempAbsenceRepositoryFrame tempAbsFrameRepo;

	@Inject
	private JobTitleInfoRepository jobTitleRepo;

	@Inject
	private LayoutingProcessor layoutingProcessor;

	@Inject
	private WorkTimeWorkplaceRepository workTimePlaceRepo;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepo;

	@Inject
	private WorkplaceConfigInfoFinder workPlaceFinder;

	@Inject
	private MonthlyPatternRepository monthlyPatternRepo;

	@Inject
	private BPSettingRepository bPSettingRepo;

	@Inject
	private PerInfoCategoryRepositoty categoryRepo;

	@Inject
	private YearHolidayRepository yearHolidayRepo;

	@Inject
	private GrantDateTblRepository gdTblRepository;

	@Inject
	private AffWorkplaceHistoryRepository affWorkplaceHistoryRepository;

	@Inject
	private SocialInsuranceOfficeRepository socialInsuranceOfficeRepository;

	private static final Map<String, Class<?>> enumMap;
	static {
		Map<String, Class<?>> aMap = new HashMap<>();
		// 性別
		aMap.put("E00001", GenderPerson.class);
		// 血液型
		aMap.put("E00002", BloodType.class);
		// 給与区分
		aMap.put("E00003", SalarySegment.class);
		// 育児介護区分
		aMap.put("E00004", ChildCareAtr.class);
		// 予定管理区分
		aMap.put("E00005", NotUseAtr.class);
		// 労働制
		aMap.put("E00006", WorkingSystem.class);
		// 勤務予定基本作成方法
		aMap.put("E00007", WorkScheduleBasicCreMethod.class);
		// 勤務予定作成マスタ参照区分
		aMap.put("E00008", WorkScheduleMasterReferenceAtr.class);
		// 勤務予定の時間帯マスタ参照区分
		aMap.put("E00009", TimeZoneScheduledMasterAtr.class);
		// 時給者区分
		aMap.put("E00010", HourlyPaymentAtr.class);
		// するしない区分
		aMap.put("E00011", ManageAtr.class);
		// 特別休暇適用設定
		aMap.put("E00012", SpecialLeaveAppSetting.class);
		// 60H超過時の精算方法
		aMap.put("E00013", PaymentMethod.class);
		// 子の看護・介護休暇上限設定
		aMap.put("E00014", UpperLimitSetting.class);
		// 休暇期限切れ状態
		aMap.put("E00015", LeaveExpirationStatus.class);
		// 算定区分
		aMap.put("E00042", CalculationAtr.class);

		enumMap = Collections.unmodifiableMap(aMap);
	}

	private static final String JP_SPACE = "　";

	public List<ComboBoxObject> getComboBox(SelectionItemDto selectionItemDto, String employeeId,
			GeneralDate standardDate, boolean isRequired, PersonEmployeeType perEmplType, boolean isDataType6, String categoryCode, String workplaceId, boolean isCps009) {

		if (standardDate == null) {
			standardDate = GeneralDate.today();
		}

		ReferenceTypes RefType = selectionItemDto.getReferenceType();
		String refCd = "";
		switch (RefType) {
		case ENUM:
			EnumRefConditionDto enumTypeDto = (EnumRefConditionDto) selectionItemDto;
			refCd = enumTypeDto.getEnumName();
			break;
		case CODE_NAME:
			CodeNameRefTypeDto codeNameTypeDto = (CodeNameRefTypeDto) selectionItemDto;
			refCd = codeNameTypeDto.getTypeCode();
			break;
		case DESIGNATED_MASTER:
			MasterRefConditionDto masterRefTypeDto = (MasterRefConditionDto) selectionItemDto;
			refCd = masterRefTypeDto.getMasterType();
			break;
		}
		return getComboBox(RefType, refCd, standardDate, employeeId, workplaceId, isRequired, perEmplType, isDataType6, categoryCode, null, isCps009);
	}

	/**
	 * @param comboBoxParam
	 * @return
	 * only run with CODE_NAME or DESIGNATED_MASTER case
	 * không dùng cho màn cps009 nên tham số isCps009 = false
	 */
	public List<ComboBoxObject> getFlexibleComboBox(ComboBoxParam comboBoxParam) {
		ReferenceTypes referenceType = comboBoxParam.getComboBoxType();
		PersonEmployeeType perEmplType = PersonEmployeeType.EMPLOYEE;
		String referenceCode = "";
		switch (referenceType) {
		case CODE_NAME:
			referenceCode = comboBoxParam.getTypeCode();
			// 2018/04/05 because can't get personEmployeeType from UI -> get from DB
			Optional<PersonInfoCategory> categoryOpt = categoryRepo.getPerInfoCategory(comboBoxParam.getCategoryId(),
					AppContexts.user().contractCode());
			perEmplType = categoryOpt.get().getPersonEmployeeType();
			break;
		case DESIGNATED_MASTER:
			referenceCode = comboBoxParam.getMasterType();
			break;
		default:
			break;
		}
		return getComboBox(referenceType, referenceCode, comboBoxParam.getStandardDate(), comboBoxParam.getEmployeeId(),
				comboBoxParam.getWorkplaceId(), comboBoxParam.isRequired(), perEmplType, true, null, comboBoxParam.getBaseDate(), false);

	}

	private List<ComboBoxObject> getMasterComboBox(String masterType, String employeeId, GeneralDate standardDate,
			String workplaceId, String categoryCode, GeneralDate realBaseDate, boolean isCps009) {
		String companyId = AppContexts.user().companyId();
		switch (masterType) {

		case "M00001":
			// 部門マスタ
			break;
		case "M00002":
			// 職場マスタ
			return workPlaceFinder.findFlatList(standardDate).stream()
					.map(workPlace -> new ComboBoxObject(workPlace.getWorkplaceId(),
							workPlace.code + JP_SPACE + workPlace.name))
					.collect(Collectors.toList());

		case "M00003":
			return employmentRepo.findAll(companyId).stream()
					.map(employment -> new ComboBoxObject(employment.getEmploymentCode().v(),
							employment.getEmploymentCode().v() + JP_SPACE + employment.getEmploymentName().v()))
					.collect(Collectors.toList());

		case "M00004":
			// 分類マスタ１
			return classificationRepo.getAllManagementCategory(companyId).stream()
					.map(classification -> new ComboBoxObject(classification.getClassificationCode().v(),
							classification.getClassificationCode().v() + JP_SPACE
									+ classification.getClassificationName().v()))
					.collect(Collectors.toList());
		case "M00005":
			// 職位マスタ
			return jobTitleRepo.findAll(companyId, standardDate).stream()
					.map(jobTitle -> new ComboBoxObject(jobTitle.getJobTitleId(),
							jobTitle.getJobTitleCode() + JP_SPACE + jobTitle.getJobTitleName().v()))
					.collect(Collectors.toList());
		case "M00006":
			// 休職休業マスタ
			return tempAbsFrameRepo.findWithUseState(companyId, NotUseAtr.USE.value).stream().map(
					frame -> new ComboBoxObject(frame.getTempAbsenceFrNo().v() + "", frame.getTempAbsenceFrName().v()))
					.collect(Collectors.toList());
		case "M00007":
			// 勤務種別マスタ
			return businessTypeRepo.findAll(companyId).stream()
					.map(businessType -> new ComboBoxObject(businessType.getBusinessTypeCode().v(),
							businessType.getBusinessTypeCode().v() + JP_SPACE + businessType.getBusinessTypeName().v()))
					.collect(Collectors.toList());
		case "M00008":
			// 勤務種類マスタ
			List<List<String>> lstWTDomain = workTypeRepo.findCodeAndNameOfWorkTypeByCompanyId(companyId);
			List<ComboBoxObject> lstReturn = lstWTDomain.stream()
					.map(workType -> new ComboBoxObject(workType.get(0), workType.get(0) + JP_SPACE + workType.get(1)))
					.collect(Collectors.toList());

			return lstReturn;
		case "M00009":
			// 就業時間帯マスタ
			if(isCps009 == true) {
				return workTimeSettingRepo.findActiveItems(companyId).stream()
						.map(workTimeSetting -> new ComboBoxObject(workTimeSetting.getWorktimeCode().v(),
								workTimeSetting.getWorktimeCode() + JP_SPACE
										+ workTimeSetting.getWorkTimeDisplayName().getWorkTimeName().v()))
						.collect(Collectors.toList());

			}else {
				if (workplaceId == null ) {
					PeregDto resultDto = layoutingProcessor.findSingle(PeregQuery.createQueryLayout("CS00017", employeeId, "", standardDate));
					if (resultDto != null) {
						AffWorlplaceHistItemDto workPlaceItem = (AffWorlplaceHistItemDto) resultDto.getDomainDto();
						workplaceId = workPlaceItem.getWorkplaceCode();
					} else {
						// this case shouldn't happen
					}
				} else if (employeeId != null) {
					if (standardDate == null && realBaseDate != null) {
						PeregDto resultDto = layoutingProcessor.findSingle(PeregQuery.createQueryLayout("CS00020", employeeId, "", realBaseDate));
						if (resultDto != null) {
							WorkingConditionDto wrkCond = (WorkingConditionDto) resultDto.getDomainDto();
							standardDate = wrkCond.getStartDate();
						}
					}
					Optional<AffWorkplaceHistory> affWorkplaceHist = affWorkplaceHistoryRepository.getByEmpIdAndStandDate(employeeId, standardDate);
					if (!affWorkplaceHist.isPresent()){
						workplaceId = null;
					}
				}
				
				// Get all workTime installed at Kmk017
				List<String> workTimeCodeList = workTimePlaceRepo.getWorkTimeWorkplaceById(companyId, workplaceId)
						.stream().map(x -> x.getWorktimeCode().v()).collect(Collectors.toList());

				// Lấy tên tương ứng của WorkTime được cài đặt ở màn Kmk017
				// tương ứng với list workTimeCodeList được lấy từ step trước
				// đó.
				return workTimeSettingRepo.getListWorkTimeSetByListCode(companyId, workTimeCodeList).stream()
						.map(workTimeSetting -> new ComboBoxObject(workTimeSetting.getWorktimeCode().v(),
								workTimeSetting.getWorktimeCode() + JP_SPACE
										+ workTimeSetting.getWorkTimeDisplayName().getWorkTimeName().v()))
						.collect(Collectors.toList());
					
			}

		case "M00010":
			// 出勤系の勤務種類を取得する
			return workTypeRepo.getAcquiredAttendanceWorkTypes(companyId).stream()
					.map(attWkType -> new ComboBoxObject(attWkType.getWorkTypeCode().v(),
							attWkType.getWorkTypeCode().v() + JP_SPACE + attWkType.getName().v()))
					.collect(Collectors.toList());
		case "M00011":
			// 休日系の勤務種類を取得する
			return workTypeRepo.getAcquiredHolidayWorkTypes(companyId).stream()
					.map(attWkType -> new ComboBoxObject(attWkType.getWorkTypeCode().v(),
							attWkType.getWorkTypeCode().v() + JP_SPACE + attWkType.getName().v()))
					.collect(Collectors.toList());

		case "M00012":
			// 休出系の勤務種類を取得する
			return workTypeRepo.getAcquiredLeaveSystemWorkTypes(companyId).stream()
					.map(attWkType -> new ComboBoxObject(attWkType.getWorkTypeCode().v(),
							attWkType.getWorkTypeCode().v() + JP_SPACE + attWkType.getName().v()))
					.collect(Collectors.toList());

		case "M00013":
			// 休日系の勤務種類を取得する
			List<WorkType> workType = workTypeRepo.getAcquiredHolidayWorkTypes(companyId);

			List<WorkType> workTypeNew = new ArrayList<WorkType>();

			workType.forEach((item -> {
				WorkTypeSet workTypeSet = item.getWorkTypeSetList().get(0);
				if (workTypeSet.getDigestPublicHd() == WorkTypeSetCheck.CHECK) {
					workTypeNew.add(item);
				}
			}));

			return workType.stream()
					.map(attWkType -> new ComboBoxObject(attWkType.getWorkTypeCode().v(),
							attWkType.getWorkTypeCode().v() + JP_SPACE + attWkType.getName().v()))
					.collect(Collectors.toList());

		case "M00014":
			// 月間パターンマスタ
			return monthlyPatternRepo.findAll(companyId).stream()
					.map(x -> new ComboBoxObject(x.getMonthlyPatternCode().v(),
							x.getMonthlyPatternCode().v() + JP_SPACE + x.getMonthlyPatternName().v()))
					.collect(Collectors.toList());

		case "M00015":
			// 加給時間帯マスタ
			return bPSettingRepo.getAllBonusPaySetting(companyId).stream()
					.map(x -> new ComboBoxObject(x.getCode().v(), x.getCode().v() + JP_SPACE + x.getName().v()))
					.collect(Collectors.toList());
		case "M00016":
			return yearHolidayRepo.findAll(companyId).stream()
					.map(grantTable -> new ComboBoxObject(grantTable.getYearHolidayCode().v(),
							grantTable.getYearHolidayName().v()))
					.collect(Collectors.toList());
		case "M00017":
			Integer specialHolidayCode = convertFromCategoryCode(categoryCode);
			if (specialHolidayCode == null ) return new ArrayList<>();
			return gdTblRepository.findBySphdCd(companyId, specialHolidayCode).stream()
					.map(yearServicePer -> new ComboBoxObject(yearServicePer.getGrantDateCode().v(),
							yearServicePer.getGrantDateName().v()))
					.collect(Collectors.toList());
		case "M00019":
		    //社会保険事業所
			return socialInsuranceOfficeRepository.findByCid(companyId)
					.stream()
					.map(x -> new ComboBoxObject(x.getCode().v(), x.getCode().v() + JP_SPACE + x.getName().v())).collect(Collectors.toList());
		default:
			break;
		}
		return new ArrayList<>();
	}

	private List<ComboBoxObject> getCodeNameComboBox(String typeCode, GeneralDate standardDate,
			PersonEmployeeType perEmplType) {
		List<SelectionInitDto> selectionList = selectionFinder.getAllSelectionByCompanyId(typeCode, standardDate,
				perEmplType);
		List<ComboBoxObject> lstComboBoxValue = new ArrayList<>();
		for (SelectionInitDto selection : selectionList) {
			lstComboBoxValue.add(new ComboBoxObject(selection.getSelectionId(), String.join(JP_SPACE, selection.getSelectionCode(), selection.getSelectionName())));
		}
		return lstComboBoxValue;
	}

	@SuppressWarnings("unchecked")
	private <E extends Enum<?>> List<ComboBoxObject> getEnumComboBox(String enumName) {

		Class<?> enumClass = enumMap.get(enumName);
		if (enumClass == null) {
			return new ArrayList<>();
		}
		List<EnumConstant> enumConstants = EnumAdaptor.convertToValueNameList((Class<E>) enumClass);
		
		if (enumName.equals("E00007")) {
			return specialWithE00007();
		}

		if (enumName.equals("E00008")) {
			return specialWithE00008(enumConstants);
		}

		return enumConstants.stream()
				.map(enumElement -> new ComboBoxObject(enumElement.getValue() + "", enumElement.getLocalizedName()))
				.collect(Collectors.toList());
	}
	
	//combine E00007 and E00008
	private List<ComboBoxObject> specialWithE00007() {
		List<ComboBoxObject> comboBoxList = new ArrayList<>();
		
		comboBoxList.add(new ComboBoxObject(0 + "", "カレンダー(会社)"));
		comboBoxList.add(new ComboBoxObject(1 + "", "カレンダー(職場)"));
		comboBoxList.add(new ComboBoxObject(2 + "", "カレンダー(分類)"));
		comboBoxList.add(new ComboBoxObject(3 + "", "月間パターン"));
		comboBoxList.add(new ComboBoxObject(4 + "", "個人情報曜日別"));
		return comboBoxList;
	}

	private List<ComboBoxObject> specialWithE00008(List<EnumConstant> enumConstants) {

		List<ComboBoxObject> comboBoxList = new ArrayList<>();
		for (EnumConstant enumElement : enumConstants) {
			int value = enumElement.getValue();
			String customText = "";
			if (value == WorkScheduleMasterReferenceAtr.WORK_PLACE.value) {
				customText = TextResource.localize("Com_Workplace");
			} else if (value == WorkScheduleMasterReferenceAtr.CLASSIFICATION.value) {
				customText = TextResource.localize("Com_Class");
				;
			}
			comboBoxList.add(new ComboBoxObject(value + "", customText));
		}
		return comboBoxList;
	}

	private Integer convertFromCategoryCode(String categoryCode) {
		Map<String, Integer> map = new HashMap<>();
		map.put("CS00025", 1);
		map.put("CS00026", 2);
		map.put("CS00027", 3);
		map.put("CS00028", 4);
		map.put("CS00029", 5);
		map.put("CS00030", 6);
		map.put("CS00031", 7);
		map.put("CS00032", 8);
		map.put("CS00033", 9);
		map.put("CS00034", 10);
		map.put("CS00049", 11);
		map.put("CS00050", 12);
		map.put("CS00051", 13);
		map.put("CS00052", 14);
		map.put("CS00053", 15);
		map.put("CS00054", 16);
		map.put("CS00055", 17);
		map.put("CS00056", 18);
		map.put("CS00057", 19);
		map.put("CS00058", 20);
		return map.get(categoryCode);
	}


	public List<ComboBoxObject> getComboBox(ReferenceTypes referenceType, String referenceCode,
			GeneralDate standardDate, String employeeId, String workplaceId, boolean isRequired,
			PersonEmployeeType perEmplType, boolean isDataType6, String categoryCode, GeneralDate realBaseDate,  boolean isCps009) {

		List<ComboBoxObject> resultList = new ArrayList<ComboBoxObject>();
		List<ComboBoxObject> comboboxItems = new ArrayList<ComboBoxObject>();
		switch (referenceType) {
		case ENUM:
			resultList = getEnumComboBox(referenceCode);
			break;
		case CODE_NAME:
			resultList = getCodeNameComboBox(referenceCode, standardDate, perEmplType);
			break;
		case DESIGNATED_MASTER:
			resultList = getMasterComboBox(referenceCode, employeeId, standardDate, workplaceId, categoryCode, realBaseDate, isCps009);
			break;

		}
		if (!CollectionUtil.isEmpty(resultList)) {
			if (!isRequired && isDataType6) {

				comboboxItems = new ArrayList<ComboBoxObject>(Arrays.asList(new ComboBoxObject("", "")));
			}
			comboboxItems.addAll(resultList);
		}
		return comboboxItems;
	}

}
