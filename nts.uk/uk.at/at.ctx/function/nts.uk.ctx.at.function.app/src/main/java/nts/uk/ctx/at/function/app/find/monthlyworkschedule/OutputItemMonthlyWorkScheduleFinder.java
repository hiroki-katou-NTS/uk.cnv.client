/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.function.app.find.monthlyworkschedule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.app.command.monthlyworkschedule.OutputItemMonthlyWorkScheduleCopyCommand;
import nts.uk.ctx.at.function.app.find.annualworkschedule.PeriodDto;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.enums.TypeOfItem;
import nts.uk.ctx.at.function.dom.attendanceitemname.service.AttendanceItemNameService;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.FormCanUsedForTime;
import nts.uk.ctx.at.function.dom.holidaysremaining.PermissionOfEmploymentForm;
import nts.uk.ctx.at.function.dom.holidaysremaining.repository.PermissionOfEmploymentFormRepository;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.DisplayTimeItem;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.MonPfmCorrectionFormat;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.MonPfmCorrectionFormatRepository;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkType;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeRepository;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.SheetCorrectedMonthly;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.ItemSelectionEnum;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.MonthlyAttendanceItemsDisplay;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.MonthlyFormatPerformanceAdapter;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.MonthlyFormatPerformanceImport;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.OutputItemMonthlyWorkSchedule;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.OutputItemMonthlyWorkScheduleRepository;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.TextSizeCommonEnum;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformAuthorRepo;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformanceFunctionNo;
import nts.uk.ctx.at.shared.app.service.workrule.closure.ClosureEmploymentService;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.BusinessType;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.repository.BusinessTypesRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.service.CompanyMonthlyItemService;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class OutputItemMonthlyWorkScheduleFinder.
 */
@Stateless
public class OutputItemMonthlyWorkScheduleFinder {

	/** The output item monthly work schedule repository. */
	@Inject
	private OutputItemMonthlyWorkScheduleRepository outputItemMonthlyWorkScheduleRepository;

	/** The format performance adapter. */
	@Inject
	private MonthlyFormatPerformanceAdapter monthlyFormatPerformanceAdapter;

	/** The mon pfm correction format repository. */
	@Inject
	private MonPfmCorrectionFormatRepository monPfmCorrectionFormatRepository;

	/** The business types repository. */
	@Inject
	private BusinessTypesRepository businessTypesRepository;

	/** The permission of employment form repository. */
	@Inject
	private PermissionOfEmploymentFormRepository permissionOfEmploymentFormRepository;

	/** The monthly record work type repository. */
	@Inject
	private MonthlyRecordWorkTypeRepository monthlyRecordWorkTypeRepository;

	@Inject
	private CompanyMonthlyItemService companyMonthlyItemService;

	@Inject
	private ClosureEmploymentService closureEmploymentService;

	/** The attendance item name service */
	@Inject
	private AttendanceItemNameService attendanceItemNameService;
	
	@Inject
	private DailyPerformAuthorRepo dailyPerformAuthorRepo;
	
	/** The Constant AUTHORITY. */
	// SettingUnitType.AUTHORITY.value
	private static final int AUTHORITY = 0;

	/** The Constant BUSINESS_TYPE. */
	// SettingUnitType.BUSINESS_TYPE.value
	private static final int BUSINESS_TYPE = 1;

	/** The Constant FUNCTION_NO. */
	private static final int FUNCTION_NO = 6;

	/** The Constant LIMIT_DISPLAY_ITEMS_BIG_SIZE. */
	private static final int LIMIT_DISPLAY_ITEMS_BIG_SIZE = 48;

	/** The Constant LIMIT_DISPLAY_ITEMS_SMALL_SIZE. */
	private static final int LIMIT_DISPLAY_ITEMS_SMALL_SIZE = 60;
	
	/** The Constant */
	
	/**
	 * Find employment authority.
	 *
	 * @return the boolean
	 */
	public Boolean findEmploymentAuthority() {
		String companyId = AppContexts.user().companyId();
		String roleId = AppContexts.user().roles().forAttendance();
		Optional<PermissionOfEmploymentForm> optPermissionOfEmploymentForm = this.permissionOfEmploymentFormRepository
				.find(companyId, roleId, FUNCTION_NO);
		if (optPermissionOfEmploymentForm.isPresent()) {
			return optPermissionOfEmploymentForm.get().isAvailable();
		}
		return false;
	}
	
	public Boolean checkAuthority() {
		String roleId = AppContexts.user().roles().forAttendance();
		// ログイン社員の就業帳票の権限を取得する
		// ・ロールID：ログイン社員の就業ロールID
		// ・機能NO：51(自由設定区分)
		// ・利用できる：TRUE
		return this.dailyPerformAuthorRepo.getAuthorityOfEmployee(roleId,
				new DailyPerformanceFunctionNo(BigDecimal.valueOf(51l)), true);
	}

	/**
	 * UKDesign.UniversalK.就業.KWR_帳表.KWR006_月別勤務表 (monthly work schedule).
	 * A：月別勤務表 (Monthly work schedule).アルゴリズム (Thuat toan).起動処理 (Xu ly khoi dong).起動処理 (Xu ly khoi dong)
	 */
	public List<OutputItemMonthlyWorkScheduleDto> findAll(int itemType) {
		String employeeId = AppContexts.user().employeeId();
		List<OutputItemMonthlyWorkSchedule> result = this.outputItemMonthlyWorkScheduleRepository
				.findBySelectionAndCidAndSid(ItemSelectionEnum.valueOf(itemType)
											, AppContexts.user().companyId()
											, employeeId);
		return result.isEmpty() ? null : result.stream()
				.map(item -> {
					OutputItemMonthlyWorkScheduleDto dto = new OutputItemMonthlyWorkScheduleDto();
					dto.setItemCode(item.getItemCode().v());
					dto.setItemName(item.getItemName().v());
					dto.setLayoutID(item.getLayoutID());
					return dto;
				}).collect(Collectors.toList());
		}

	/**
	 * UKDesign.UniversalK.就業.KWR_帳表.KWR006_月別勤務表 (monthly work schedule).C：出力項目設定 (Setting hạng mục output).アルゴリズム(Thuật toán).
     * 初期データ取得処理 (Xử lý lấy data ban đầu).初期データ取得処理
	 * Find by SelectionType, Cid and Sid.
	 * @return the map
	 */
	public Map<String, Object> findBySelectionAndCidAndSid(int itemType) {
		String companyID = AppContexts.user().companyId();
		Map<String, Object> mapDtoReturn = new HashMap<>();
		// ドメインモデル「画面で使用可能な月次勤怠項目」を取得する
		List<Integer> attdIds = this.attendanceItemNameService.getMonthlyAttendanceItemsAvaiable(companyID
																	 , FormCanUsedForTime.MONTHLY_WORK_SCHEDULE
																	 , TypeOfItem.Monthly);

		//アルゴリズム「会社の月次を取得する」を実行する (Execute the algorithm "Get company's monthly")
		List<MonthlyAttendanceItemDto> lstDailyAtdItemDto = this.companyMonthlyItemService
				.getMonthlyItems(companyID, Optional.empty(), attdIds, new ArrayList<>()).stream().map(dto -> {
					MonthlyAttendanceItemDto dtoClientReturn = new MonthlyAttendanceItemDto();
					dtoClientReturn.setCode(dto.getAttendanceItemDisplayNumber());
					dtoClientReturn.setId(dto.getAttendanceItemId());
					dtoClientReturn.setName(dto.getAttendanceItemName());
					dtoClientReturn.setAttendanceItemAtt(dto.getTypeOfAttendanceItem());
					return dtoClientReturn;
				}).collect(Collectors.toList());

		// ドメインモデル「月次の勤怠項目」をすべて取得する(Acquire all domain model "monthly attendance
		// items")
		if (!attdIds.isEmpty()) {
			mapDtoReturn.put("monthlyAttendanceItem", lstDailyAtdItemDto);
		} else {
			mapDtoReturn.put("monthlyAttendanceItem", Collections.emptyList());
		}

		@SuppressWarnings("unchecked")
		Map<Integer, String> mapCodeNameAttendance = convertListToMapAttendanceItem(
				(List<MonthlyAttendanceItemDto>) mapDtoReturn.get("monthlyAttendanceItem"));

		// ドメインモデル「月別勤務表の出力項目」をすべて取得する
		// get all domain OutputItemMonthlyWorkSchedule
		List<OutputItemMonthlyWorkSchedule> lstOutputItemMonthlyWorkSchedule = this.outputItemMonthlyWorkScheduleRepository
				.findBySelectionAndCidAndSid(ItemSelectionEnum.valueOf(itemType),
														AppContexts.user().companyId(), 
														AppContexts.user().employeeId());

		if (!lstOutputItemMonthlyWorkSchedule.isEmpty()) {
			mapDtoReturn.put("outputItemMonthlyWorkSchedule", lstOutputItemMonthlyWorkSchedule.stream().map(domain -> {
				OutputItemMonthlyWorkScheduleDto dto = new OutputItemMonthlyWorkScheduleDto();
				dto.setItemCode(domain.getItemCode().v());
				dto.setItemName(domain.getItemName().v());
				dto.setLstDisplayedAttendance(
						toDtoTimeItemTobeDisplay(domain.getLstDisplayedAttendance(), mapCodeNameAttendance));
				dto.setRemarkInputContent(domain.getRemarkInputNo().value);
				dto.setLayoutID(domain.getLayoutID());
				dto.setTextSize(domain.getTextSize().value);
				dto.setRemarkPrinted(domain.isRemarkPrinted());
				return dto;
			}).sorted(Comparator.comparing(OutputItemMonthlyWorkScheduleDto::getItemCode))
					.collect(Collectors.toList()));
		}

		// find nothing
		return mapDtoReturn;
	}

	// algorithm for screen D: start screen
	public MonthlyPerformanceDataReturnDto getFormatMonthlyPerformance() {
		String companyId = AppContexts.user().companyId();
		// Get domain from request list 402 ドメインモデル「実績修正画面で利用するフォーマット」を取得する
		MonthlyPerformanceDataReturnDto dto = new MonthlyPerformanceDataReturnDto();

		Optional<MonthlyFormatPerformanceImport> optFormatPerformanceImport = monthlyFormatPerformanceAdapter
				.getFormatPerformance(companyId);

		if (!optFormatPerformanceImport.isPresent()) {
			dto.setSettingUnitType("Unknown");
			dto.setListItems(new ArrayList<>());
			return dto;
		}

		switch (optFormatPerformanceImport.get().getSettingUnitType()) {
		case AUTHORITY: // In case of authority
			dto.setSettingUnitType("権限");
			// Get domain ドメインモデル「会社の月別実績の修正のフォーマット」を取得する
			List<MonPfmCorrectionFormat> lstMonPfmCorrectionFormat = monPfmCorrectionFormatRepository
					.getAllMonPfm(companyId);
			dto.setListItems(lstMonPfmCorrectionFormat.stream().map(obj -> {
				return new MonthlyDataInforReturnDto(obj.getMonthlyPfmFormatCode().v(),
						obj.getMonPfmCorrectionFormatName().v());
			}).collect(Collectors.toList()));
			break;
		case BUSINESS_TYPE: // In case of work type
			dto.setSettingUnitType("勤務種別");
			// Get domain ドメインモデル「勤務種別の月別実績の修正のフォーマット」を取得する
			List<MonthlyRecordWorkType> lstMonthlyRecordWorkType = monthlyRecordWorkTypeRepository
					.getAllMonthlyRecordWorkType(companyId);
			Set<String> setBusinessTypeFormatMonthlyCode = lstMonthlyRecordWorkType.stream()
					.map(domain -> domain.getBusinessTypeCode().v()).collect(Collectors.toSet());

			// Get domain businessTypeCode ドメインモデル「勤務種別」を取得する
			List<BusinessType> lstBusinessType = businessTypesRepository.findAll(companyId);

			dto.setListItems(lstBusinessType.stream()
					.filter(domain -> setBusinessTypeFormatMonthlyCode.contains(domain.getBusinessTypeCode().v()))
					.map(domain -> new MonthlyDataInforReturnDto(domain.getBusinessTypeCode().v(),
							domain.getBusinessTypeName().v()))
					.collect(Collectors.toList()));
			break;
		default:
			dto.setSettingUnitType("Unknown");
			dto.setListItems(new ArrayList<>());
		}
		return dto;
	}

	// algorithm for screen D: copy
	public MonthlyReturnItemDto executeCopy(OutputItemMonthlyWorkScheduleCopyCommand copyCommand) {
		String companyId = AppContexts.user().companyId();
		MonthlyReturnItemDto returnDto = new MonthlyReturnItemDto();
		// Get employee by command
		String employeeId = AppContexts.user().employeeId();
		
		// get domain 月別勤務表の出力項目
		Optional<OutputItemMonthlyWorkSchedule> optOutputItemMonthlyWorkSchedule = outputItemMonthlyWorkScheduleRepository
				.findBySelectionAndCidAndSidAndCode(
						  ItemSelectionEnum.valueOf(copyCommand.getItemType())
						, companyId
						, copyCommand.getCodeCopy()
						, employeeId);

		if (optOutputItemMonthlyWorkSchedule.isPresent()) {
			throw new BusinessException("Msg_3");
		} else {
			List<DisplayTimeItemDto> dtos = getDomConvertMonthlyWork(companyId
					, copyCommand.getCodeSourceSerivce()
					, TextSizeCommonEnum.valueOf(copyCommand.getFontSize()));
			returnDto.setLstDisplayTimeItem(dtos);

			Map<String, Object> kwr006Lst = this.findBySelectionAndCidAndSid(copyCommand.getItemType());
			@SuppressWarnings("unchecked")
			Map<Integer, String> mapCodeNameAttendance = convertListToMapAttendanceItem(
					(List<MonthlyAttendanceItemDto>)kwr006Lst.get("monthlyAttendanceItem"));
			//Get size of list item of kdw008 in kwr006 
			List<DisplayTimeItemDto> newDtos = dtos.stream().filter(item -> mapCodeNameAttendance.containsKey(item.getItemDaily())).map(domain -> {
				return domain;
			}).collect(Collectors.toList());
			//compare if kdw008(right) and kwr006(left) doesn't equals
			if (newDtos.size() != dtos.size()) {
				List<String> lstMsgErr = new ArrayList<>();
				lstMsgErr.add("Msg_1476");
				returnDto.setErrorList(lstMsgErr);
			}
			return returnDto;
		}
	}

	// アルゴリズム「月別勤務表用フォーマットをコンバートする」を実行する(Execute algorithm "Convert monthly work
	// table format")
	private List<DisplayTimeItemDto> getDomConvertMonthlyWork(String companyId, String code, TextSizeCommonEnum fontSize) {

		// Get domain 実績修正画面で利用するフォーマット from request list 402
		Optional<MonthlyFormatPerformanceImport> optFormatPerformanceImport = monthlyFormatPerformanceAdapter
				.getFormatPerformance(companyId);
		List<DisplayTimeItemDto> lstDataReturn = new ArrayList<>();
		if (optFormatPerformanceImport.isPresent()) {
			switch (optFormatPerformanceImport.get().getSettingUnitType()) {
			case AUTHORITY: // In case of authority
				// ドメインモデル「会社の月別実績の修正のフォーマット (Acquire the domain model
				// "format of company's daily performance correction")
				// 「日別実績の修正の表示項目」から表示項目を取得する (Acquire display items from
				// "display items for correction of daily performance")
				MonPfmCorrectionFormat monPfmCorrectionFormat = monPfmCorrectionFormatRepository
						.getMonPfmCorrectionFormat(companyId, code).get();
				
				lstDataReturn = monPfmCorrectionFormat.getDisplayItem()
						.getListSheetCorrectedMonthly().stream()
						.sorted(Comparator.comparing(SheetCorrectedMonthly::getSheetNo))
						.flatMap(t -> t.getListDisplayTimeItem().stream())
						.sorted(Comparator.comparing(DisplayTimeItem::getDisplayOrder))
						.map(item -> new DisplayTimeItemDto(item.getItemDaily()
			    				  , null
			    				  , item.getColumnWidthTable()))
						.collect(Collectors.toList());
				break;
			case BUSINESS_TYPE:
				// ドメインモデル「勤務種別日別実績の修正のフォーマット」を取得する (Acquire the domain model
				// "Format of working type daily performance correction)
				// 「日別実績の修正の表示項目」から表示項目を取得する (Acquire display items from
				// "display items for correction of daily performance")
				MonthlyRecordWorkType monthlyRecordWorkType = this.monthlyRecordWorkTypeRepository
						.getMonthlyRecordWorkTypeByCode(companyId, code).get();

				lstDataReturn = monthlyRecordWorkType.getDisplayItem()
						.getListSheetCorrectedMonthly().stream()
						.sorted(Comparator.comparing(SheetCorrectedMonthly::getSheetNo))
						.flatMap(t -> t.getListDisplayTimeItem().stream())
						.sorted(Comparator.comparing(DisplayTimeItem::getDisplayOrder))
						.map(item -> new DisplayTimeItemDto(item.getItemDaily()
			    				  , null
			    				  , item.getColumnWidthTable()))
						.collect(Collectors.toList());
				break;
			default:
				break;
			}
		}

		// 文字の大きさにより最大表示件数を求める
		// 大の場合：48件までとする
		// 小の場合：60件までとする
		int numberDisplayItem = fontSize == TextSizeCommonEnum.SMALL
								? LIMIT_DISPLAY_ITEMS_SMALL_SIZE
								: LIMIT_DISPLAY_ITEMS_BIG_SIZE;

		return lstDataReturn.stream()
				.limit(numberDisplayItem)
				.collect(Collectors.toList());
	}

	private List<TimeItemTobeDisplayDto> toDtoTimeItemTobeDisplay(List<MonthlyAttendanceItemsDisplay> lstDomainObject,
			Map<Integer, String> mapCodeNameAttendance) {
		return lstDomainObject.stream().map(domain -> {
			TimeItemTobeDisplayDto dto = new TimeItemTobeDisplayDto();
			dto.setAttendanceDisplay(domain.getAttendanceDisplay());
			dto.setOrderNo(domain.getOrderNo());
			dto.setAttendanceName(mapCodeNameAttendance.get(domain.getAttendanceDisplay()));
			return dto;
		}).sorted(Comparator.comparing(TimeItemTobeDisplayDto::getOrderNo)).collect(Collectors.toList());
	}

	/**
	 * Convert list to map attendance item.
	 *
	 * @param lst
	 *            the lst
	 * @return the map
	 */
	private Map<Integer, String> convertListToMapAttendanceItem(List<MonthlyAttendanceItemDto> lst) {
		return lst.stream()
				.collect(Collectors.toMap(MonthlyAttendanceItemDto::getId, MonthlyAttendanceItemDto::getName));
	}

	public PeriodDto getPeriod() {
		// 社員に対応する処理締めを取得する
		Optional<Closure> closureOtp = closureEmploymentService.findClosureByEmployee(AppContexts.user().employeeId(),
				GeneralDate.today());
		if (!closureOtp.isPresent()) {
			throw new BusinessException("Msg_1134");
		} else {
			Closure closure = closureOtp.get();
			YearMonth date = closure.getClosureMonth().getProcessingYm();
			return new PeriodDto(date.toString(), date.toString());
		}
	}
}
