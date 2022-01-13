package nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.daily.dailyaggregationprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.function.dom.adapter.DailyAttendanceItemAdapter;
import nts.uk.ctx.at.function.dom.adapter.DailyAttendanceItemAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.ErAlApplicationAdapter;
import nts.uk.ctx.at.function.dom.adapter.ErAlApplicationAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.ErrorAlarmWorkRecordAdapter;
import nts.uk.ctx.at.function.dom.adapter.ErrorAlarmWorkRecordAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.WorkRecordExtraConAdapter;
import nts.uk.ctx.at.function.dom.adapter.application.ApplicationAdapter;
import nts.uk.ctx.at.function.dom.adapter.application.ApplicationImport;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceItemValueImport;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceResultImport;
import nts.uk.ctx.at.function.dom.adapter.eralworkrecorddto.MessageWRExtraConAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.EmployeeDailyPerErrorAdapter;
import nts.uk.ctx.at.function.dom.adapter.workrecord.erroralarm.EmployeeDailyPerErrorImport;
import nts.uk.ctx.at.function.dom.alarm.alarmdata.ValueExtractAlarm;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.EmployeeSearchDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.PeriodByAlarmCategory;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.ErAlConstant;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.aggregationprocess.daily.dailyaggregationprocess.DailyAggregationProcessService.DataHolder;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.AttendanceItemLinking;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.repository.AttendanceItemLinkingRepository;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.repository.DailyAttendanceItemNameDomainService;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class DailyPerformanceService {

	// 社員の日別実績エラー一覧
	@Inject
	private EmployeeDailyPerErrorAdapter employeeDailyAdapter;

	// 勤務実績のエラーアラーム
	@Inject
	private ErrorAlarmWorkRecordAdapter errorAlarmWorkRecordAdapter;

	// 勤務実績のエラーアラームチェック
	@Inject
	private WorkRecordExtraConAdapter workRecordExtraConAdapter;

	// Get attendance item name 勤怠項目名
	@Inject
	private DailyAttendanceItemNameDomainService dailyAttendanceItemNameService;

	// エラー発生時に呼び出す申請一覧
	@Inject
	private ErAlApplicationAdapter erAlApplicationAdapter;

	// 書いた申請
	@Inject
	private ApplicationAdapter applicationAdapter;
	
	//Get attendance  value
	@Inject
	private DailyAttendanceItemAdapter attendanceItemAdapter; 
	
	// Get work type code
	@Inject
	private RecordWorkInfoFunAdapter recordWorkInfoFunAdapter;
	
	// Get work type name
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private DailyAttendanceItemAdapter dailyAttendanceItemAdapter;
	
	@Inject
	private AttendanceItemLinkingRepository attendanceItemLinkingRepository;
	
	

	public List<ValueExtractAlarm> aggregationProcess(DailyAlarmCondition dailyAlarmCondition, PeriodByAlarmCategory period, EmployeeSearchDto employee, String companyID) {
		List<ValueExtractAlarm> valueExtractAlarmList = new ArrayList<>();
		String KAL010_1 = TextResource.localize("KAL010_1");
		List<String> listErrorAlarmCode = dailyAlarmCondition.getErrorAlarmCode();
		if (listErrorAlarmCode == null || listErrorAlarmCode.isEmpty())
			return new ArrayList<>();

		// 勤務実績のエラーアラーム
		List<ErrorAlarmWorkRecordAdapterDto> errorAlarmWorkRecord = errorAlarmWorkRecordAdapter.getListErAlByListCode(companyID, listErrorAlarmCode);
		Map<String, ErrorAlarmWorkRecordAdapterDto> errorAlarmMap = errorAlarmWorkRecord.stream().collect(Collectors.toMap(ErrorAlarmWorkRecordAdapterDto::getCode, x -> x));

		// 社員の日別実績エラー一覧
		List<EmployeeDailyPerErrorImport> employeeDailyList = employeeDailyAdapter.getByErrorAlarm(employee.getId(), new DatePeriod(period.getStartDate(), period.getEndDate()), listErrorAlarmCode);

		if (dailyAlarmCondition.isAddApplication()) {
			for (EmployeeDailyPerErrorImport eDaily : employeeDailyList) {
				ErrorAlarmWorkRecordAdapterDto errorAlarm = errorAlarmMap.get(eDaily.getErrorAlarmWorkRecordCode());
				if (errorAlarm == null) {
					employeeDailyList.removeIf(e -> e.getErrorAlarmWorkRecordCode().equals(eDaily.getErrorAlarmWorkRecordCode()));
					listErrorAlarmCode.removeIf(x -> x.equals(eDaily.getErrorAlarmWorkRecordCode()));
					continue;
				}
				if (errorAlarm.getUseAtr() > 0) {
					Optional<ErAlApplicationAdapterDto> erAlApplicationOpt = erAlApplicationAdapter.getAllErAlAppByEralCode(companyID, eDaily.getErrorAlarmWorkRecordCode());
					if (!erAlApplicationOpt.isPresent())
						continue;

					List<Integer> listAppType = erAlApplicationOpt.get().getAppType();
					List<Integer> listAppTypeWrited = applicationAdapter.getApplicationBySID(Arrays.asList(employee.getId()), eDaily.getDate(), eDaily.getDate()).stream().map(x -> x.getAppType()).collect(Collectors.toList());
					//取得したエラーに対して申請が出されている場合は、アラームリストではエラー扱いにしない
					if (intersectTwoListAppType(listAppType, listAppTypeWrited)) {
						employeeDailyList.remove(eDaily);
					}
				}

			}

		}

		// Remove error code from errorAlarmWorkRecord if error code not in
		// employeeDailyList
		List<String> errorCodeList = employeeDailyList.stream().map(x -> x.getErrorAlarmWorkRecordCode()).collect(Collectors.toList());
		errorAlarmWorkRecord.removeIf(x -> !errorCodeList.contains(x.getCode()));

		List<String> errorAlarmCheckIDs = errorAlarmWorkRecord.stream().map(x -> x.getErrorAlarmCheckID()).collect(Collectors.toList());
		//勤務実績のエラーアラームチェック		
		List<MessageWRExtraConAdapterDto> messageList = workRecordExtraConAdapter.getMessageWRExtraConByListID(errorAlarmCheckIDs);

		Map<String, MessageWRExtraConAdapterDto> errAlarmCheckIDToMessage = messageList.stream()
				.collect(Collectors.toMap(MessageWRExtraConAdapterDto::getErrorAlarmCheckID, x -> x));

		//test reponse
		Set<String> emps = new HashSet<String>(); 
		Set<Integer> listItemIDs = new HashSet<Integer>();
		
		for(EmployeeDailyPerErrorImport employeeDailyPerErrorImport : employeeDailyList) {
			listItemIDs.addAll(employeeDailyPerErrorImport.getAttendanceItemList());
			emps.add(employeeDailyPerErrorImport.getEmployeeID());
		}
		DatePeriod periodNew =  new DatePeriod(period.getStartDate(), period.getEndDate());
		List<AttendanceResultImport> attdResoult = attendanceItemAdapter.getValueOf( new ArrayList<>(emps), periodNew, new ArrayList<>(listItemIDs) );
		
		//get list dailyAttendanceItems
		String companyId = AppContexts.user().companyId();
		List<DailyAttendanceItemAdapterDto> dailyAttendanceItems = this.dailyAttendanceItemAdapter
				.getDailyAttendanceItem(companyId, new ArrayList<>(listItemIDs)).stream().map(item -> {
					String name = item.getAttendanceName();
					if (name.indexOf("{#") >= 0) {
						int startLocation = name.indexOf("{");
						int endLocation = name.indexOf("}");
						name = name.replace(name.substring(startLocation, endLocation + 1),
								TextResource.localize(name.substring(startLocation + 2, endLocation)));
					}
					return new DailyAttendanceItemAdapterDto(item.getCompanyId(), item.getAttendanceItemId(), name,
							item.getDisplayNumber(), item.getUserCanUpdateAtr(), item.getDailyAttendanceAtr(),
							item.getNameLineFeedPosition(), item.getDisplayName());
				}).collect(Collectors.toList());
		List<AttendanceItemLinking> attendanceItemAndFrameNos = this.attendanceItemLinkingRepository
						.getFullDataByListAttdaId(new ArrayList<>(listItemIDs));
		
		for (EmployeeDailyPerErrorImport eDaily : employeeDailyList) {
			
			AttendanceResultImport attdResult = new AttendanceResultImport();
			for(AttendanceResultImport attendanceResultImport :attdResoult) {
				if(eDaily.getEmployeeID().equals(attendanceResultImport.getEmployeeId())) {
					attdResult = attendanceResultImport;
				}
			}
			
			AlarmContentMessage alarmContentMessage = this.calculateAlarmContentMessage(eDaily, companyID, errorAlarmMap,
					attdResult,
					dailyAttendanceItems,attendanceItemAndFrameNos);
						
			ValueExtractAlarm data = new ValueExtractAlarm(employee.getWorkplaceId(), employee.getId(), eDaily.getDate().toString(), KAL010_1,
					alarmContentMessage.getAlarmItem(), alarmContentMessage.getAlarmContent(),
					errAlarmCheckIDToMessage.get(errorAlarmMap.get(eDaily.getErrorAlarmWorkRecordCode()).getErrorAlarmCheckID()).getDisplayMessage(),null);

			valueExtractAlarmList.add(data);
		}

		return valueExtractAlarmList;

	}
	/**
	 * 日別実績のエラーアラームのアラーム値を生成する
	 * @param period 日次のカテゴリ期間
	 * @param employeeIds　List＜社員ID＞
	 * @param employee　List＜社員情報＞
	 * @param holder　日次の勤怠項目、勤務実績のエラーアラーム設定
	 * @param apps　申請情報
	 * @return
	 */
	public List<ValueExtractAlarm> aggregationProcess(DatePeriod period, List<String> employeeIds, List<EmployeeSearchDto> employee, DataHolder holder, List<ApplicationImport> apps) {
		
		List<ValueExtractAlarm> valueExtractAlarmList = new ArrayList<>();
		String KAL010_1 = TextResource.localize("KAL010_1");
		Map<String, ErrorAlarmWorkRecordAdapterDto> errorAlarmMap = holder.errorAlarmWorkRecord.stream().collect(Collectors.toMap(ErrorAlarmWorkRecordAdapterDto::getCode, x -> x));
		
		// ドメインモデル「勤務実績のエラーアラーム」を取得する
		List<EmployeeDailyPerErrorImport> employeeDailyList = employeeDailyAdapter.getByErrorAlarm(employeeIds, period, holder.eralCode);
		//日次のアラームチェック条件
		holder.dailyAlarmCondition.stream().forEach(dailyAlCon -> {
			List<EmployeeDailyPerErrorImport> empDailyError = employeeDailyList.stream().filter(error -> {
				//
				if(!dailyAlCon.getErrorAlarmCode().contains(error.getErrorAlarmWorkRecordCode())){
					return false;
				}
				//取得した「社員の日別実績エラー一覧」．勤務実績のエラーアラームコードと設定した「日別実績のエラーアラーム」．コードは合わなければ取得した「社員の日別実績エラー一覧」を除く
				if(!dailyAlCon.isAddApplication()){
					return true;
				}
				ErrorAlarmWorkRecordAdapterDto errorAlarm = errorAlarmMap.get(error.getErrorAlarmWorkRecordCode());
				//
				if(errorAlarm.getUseAtr() > 0){
					Optional<ErAlApplicationAdapterDto> erAlApplicationOpt = holder.eralApps.stream().filter(ea -> ea.getErrorAlarmCode().equals(error.getErrorAlarmWorkRecordCode())).findFirst();
					//
					if(erAlApplicationOpt.isPresent()){
						List<Integer> listAppTypeWrited = apps.stream().filter(app -> app.getAppDate().equals(error.getDate()) && app.getEmployeeID().equals(error.getEmployeeID())).map(app -> app.getAppType())
																		.collect(Collectors.toList());
						////取得したエラーに対して申請が出されている場合は、アラームリストではエラー扱いにしない
						if (intersectTwoListAppType(erAlApplicationOpt.get().getAppType(), listAppTypeWrited)) {
							return false;
						}
					}
				}
				return true;
			}).collect(Collectors.toList());
			
			// Remove error code from errorAlarmWorkRecord if error code not in
			// employeeDailyList
			List<String> errorCodeList = empDailyError.stream().map(x -> x.getErrorAlarmWorkRecordCode()).distinct().collect(Collectors.toList());

			// 勤務実績のエラーアラームチェック
			List<String> errorAlarmCheckIDs = holder.errorAlarmWorkRecord.stream()
					.filter(c -> errorCodeList.contains(c.getCode()))
					.map(x -> x.getErrorAlarmCheckID()).collect(Collectors.toList());
			//取得したエラー対応するアラーム値メッセージを生成する
			Map<String, MessageWRExtraConAdapterDto> errAlarmCheckIDToMessage = holder.messageList.stream()
					.filter(mes -> errorAlarmCheckIDs.contains(mes.getErrorAlarmCheckID()))
					.collect(Collectors.toMap(MessageWRExtraConAdapterDto::getErrorAlarmCheckID, x -> x));
			 
			Set<Integer> listItemIDs = empDailyError.stream()
					.map(error -> error.getAttendanceItemList())
					.flatMap(List::stream)
					.collect(Collectors.toSet());
			List<String> empIds = empDailyError.stream().map(error -> error.getEmployeeID()).collect(Collectors.toList());
			
			List<AttendanceItemLinking> currentNos = holder.attendanceItemAndFrameNos.stream()
					.filter(ai -> listItemIDs.contains(ai.getAttendanceItemId()))
					.collect(Collectors.toList());
			List<DailyAttendanceItemAdapterDto> currentAttendanceItems = holder.dailyAttendanceItems.stream()
					.filter(ai -> listItemIDs.contains(ai.getAttendanceItemId())).collect(Collectors.toList());
			//日別実績
			List<AttendanceResultImport> currentattdResoult = attendanceItemAdapter.getValueOf(empIds , period, new ArrayList<>(listItemIDs));
			
			empDailyError.stream().forEach(error -> {
				currentattdResoult.stream().filter(attendanceResult -> error.getEmployeeID().equals(attendanceResult.getEmployeeId())
																			&& error.getDate().equals(attendanceResult.getWorkingDate()))
											.findFirst().ifPresent(attendanceResult -> {
												AlarmContentMessage alarmContentMessage = this.calculateAlarmContentMessage(error, holder.comId, errorAlarmMap,attendanceResult,currentAttendanceItems,currentNos);
												EmployeeSearchDto em = employee.stream().filter(e -> e.getId().equals(error.getEmployeeID())).findAny().get();
												String checkedValue = null;
												ValueExtractAlarm data = new ValueExtractAlarm(em.getWorkplaceId(), em.getId(), TextResource.localize("KAL010_907",error.getDate().toString(ErAlConstant.DATE_FORMAT)), 
														KAL010_1, alarmContentMessage.getAlarmItemNew(), alarmContentMessage.getAlarmItem(),
														errAlarmCheckIDToMessage.get(errorAlarmMap.get(error.getErrorAlarmWorkRecordCode()).getErrorAlarmCheckID()).getDisplayMessage(),alarmContentMessage.getAlarmContent());

												valueExtractAlarmList.add(data);
				});
			});
		});

		return valueExtractAlarmList;

	}
	
	public List<ValueExtractAlarm> aggregationProcess(DailyAlarmCondition dailyAlarmCondition, DatePeriod period, List<String> employeeIds, 
			List<EmployeeSearchDto> employee, String companyID) {
		List<ValueExtractAlarm> valueExtractAlarmList = new ArrayList<>();
		String KAL010_1 = TextResource.localize("KAL010_1");
		List<String> listErrorAlarmCode = dailyAlarmCondition.getErrorAlarmCode();
		if (listErrorAlarmCode == null || listErrorAlarmCode.isEmpty())
			return new ArrayList<>();

		// 勤務実績のエラーアラーム
		List<ErrorAlarmWorkRecordAdapterDto> errorAlarmWorkRecord = errorAlarmWorkRecordAdapter.getListErAlByListCode(companyID, listErrorAlarmCode);
		Map<String, ErrorAlarmWorkRecordAdapterDto> errorAlarmMap = errorAlarmWorkRecord.stream().collect(Collectors.toMap(ErrorAlarmWorkRecordAdapterDto::getCode, x -> x));

		// 社員の日別実績エラー一覧
		List<EmployeeDailyPerErrorImport> employeeDailyList = employeeDailyAdapter.getByErrorAlarm(employeeIds, period, listErrorAlarmCode);

		if (dailyAlarmCondition.isAddApplication()) {
			for (EmployeeDailyPerErrorImport eDaily : employeeDailyList) {
				ErrorAlarmWorkRecordAdapterDto errorAlarm = errorAlarmMap.get(eDaily.getErrorAlarmWorkRecordCode());
				if (errorAlarm == null) {
					employeeDailyList.removeIf(e -> e.getErrorAlarmWorkRecordCode().equals(eDaily.getErrorAlarmWorkRecordCode()));
					listErrorAlarmCode.removeIf(x -> x.equals(eDaily.getErrorAlarmWorkRecordCode()));
					continue;
				}
				if (errorAlarm.getUseAtr() > 0) {
					Optional<ErAlApplicationAdapterDto> erAlApplicationOpt = erAlApplicationAdapter.getAllErAlAppByEralCode(companyID, eDaily.getErrorAlarmWorkRecordCode());
					if (!erAlApplicationOpt.isPresent())
						continue;

					List<Integer> listAppType = erAlApplicationOpt.get().getAppType();
					List<Integer> listAppTypeWrited = applicationAdapter.getApplicationBySID(employeeIds, eDaily.getDate(), eDaily.getDate()).stream().map(x -> x.getAppType()).collect(Collectors.toList());

					if (intersectTwoListAppType(listAppType, listAppTypeWrited)) {
						employeeDailyList.remove(eDaily);
					}
				}

			}

		}

		// Remove error code from errorAlarmWorkRecord if error code not in
		// employeeDailyList
		List<String> errorCodeList = employeeDailyList.stream().map(x -> x.getErrorAlarmWorkRecordCode()).collect(Collectors.toList());
		errorAlarmWorkRecord.removeIf(x -> !errorCodeList.contains(x.getCode()));

		// 勤務実績のエラーアラームチェック
		List<String> errorAlarmCheckIDs = errorAlarmWorkRecord.stream().map(x -> x.getErrorAlarmCheckID()).collect(Collectors.toList());
		List<MessageWRExtraConAdapterDto> messageList = workRecordExtraConAdapter.getMessageWRExtraConByListID(errorAlarmCheckIDs);

		Map<String, MessageWRExtraConAdapterDto> errAlarmCheckIDToMessage = messageList.stream().collect(Collectors.toMap(MessageWRExtraConAdapterDto::getErrorAlarmCheckID, x -> x));
		
		//TEst reponse
		Set<String> emps = new HashSet<String>(); 
		employeeDailyList.stream().map(c->c.getEmployeeID()).collect(Collectors.toList());
		Set<Integer> listItemIDs = new HashSet<Integer>();

		
		for(EmployeeDailyPerErrorImport employeeDailyPerErrorImport : employeeDailyList) {
			listItemIDs.addAll(employeeDailyPerErrorImport.getAttendanceItemList());
			emps.add(employeeDailyPerErrorImport.getEmployeeID());
		}
		
		List<AttendanceResultImport> attdResoult = attendanceItemAdapter.getValueOf(new ArrayList<>(emps) , period, new ArrayList<>(listItemIDs));
		
		//get list dailyAttendanceItems
		String companyId = AppContexts.user().companyId();
		List<DailyAttendanceItemAdapterDto> dailyAttendanceItems = this.dailyAttendanceItemAdapter
				.getDailyAttendanceItem(companyId, new ArrayList<>(listItemIDs)).stream().map(item -> {
					String name = item.getAttendanceName();
					if (name.indexOf("{#") >= 0) {
						int startLocation = name.indexOf("{");
						int endLocation = name.indexOf("}");
						name = name.replace(name.substring(startLocation, endLocation + 1),
								TextResource.localize(name.substring(startLocation + 2, endLocation)));
					}
					return new DailyAttendanceItemAdapterDto(item.getCompanyId(), item.getAttendanceItemId(), name,
							item.getDisplayNumber(), item.getUserCanUpdateAtr(), item.getDailyAttendanceAtr(),
							item.getNameLineFeedPosition(), item.getDisplayName());
				}).collect(Collectors.toList());
		List<AttendanceItemLinking> attendanceItemAndFrameNos = this.attendanceItemLinkingRepository
				.getFullDataByListAttdaId(new ArrayList<>(listItemIDs));
		
		for (EmployeeDailyPerErrorImport eDaily : employeeDailyList) {
			AttendanceResultImport attdResult = new AttendanceResultImport();
			for(AttendanceResultImport attendanceResultImport :attdResoult) {
				if(eDaily.getEmployeeID().equals(attendanceResultImport.getEmployeeId()) && eDaily.getDate().equals(attendanceResultImport.getWorkingDate())) {
					attdResult = attendanceResultImport;
					break;
				}
			}
			AlarmContentMessage alarmContentMessage = this.calculateAlarmContentMessage(eDaily, companyID, errorAlarmMap,attdResult,dailyAttendanceItems,attendanceItemAndFrameNos);
			EmployeeSearchDto em = employee.stream().filter(e -> e.getId().equals(eDaily.getEmployeeID())).findAny().get();
			ValueExtractAlarm data = new ValueExtractAlarm(em.getWorkplaceId(), em.getId(), eDaily.getDate().toString(), KAL010_1,
					alarmContentMessage.getAlarmItem(), alarmContentMessage.getAlarmContent(),
					errAlarmCheckIDToMessage.get(errorAlarmMap.get(eDaily.getErrorAlarmWorkRecordCode()).getErrorAlarmCheckID()).getDisplayMessage(),null);

			valueExtractAlarmList.add(data);
		}	

		return valueExtractAlarmList;

	}

	private boolean intersectTwoListAppType(List<Integer> listAppType1, List<Integer> listAppType2) {
		return listAppType1.stream().anyMatch(listAppType2::contains);
	}
	
	
	
	private AlarmContentMessage calculateAlarmContentMessage(EmployeeDailyPerErrorImport eDaily, String companyID, Map<String,
			ErrorAlarmWorkRecordAdapterDto> errorAlarmMap,
			AttendanceResultImport attendanceResult,
			List<DailyAttendanceItemAdapterDto> dailyAttendanceItems,
			List<AttendanceItemLinking> attendanceItemAndFrameNos) {
		
		// Attendance name
		Map<Integer, DailyAttendanceItem> attendanceNameMap = dailyAttendanceItemNameService.getNameOfDailyAttendanceItemNew(dailyAttendanceItems,attendanceItemAndFrameNos).stream()
				.collect(Collectors.toMap(DailyAttendanceItem::getAttendanceItemId, x -> x));
		// Attendance value
		//AttendanceResultImport attendanceResult = attendanceItemAdapter.getValueOf(eDaily.getEmployeeID(), eDaily.getDate(), eDaily.getAttendanceItemList());
		List<AttendanceItemValueImport> attendanceValue = attendanceResult.getAttendanceItems() == null ? new ArrayList<AttendanceItemValueImport>() : attendanceResult.getAttendanceItems();
		Map<Integer, AttendanceItemValueImport> mapAttendance = attendanceValue.stream().collect(Collectors.toMap(AttendanceItemValueImport::getItemId, x -> x));

		// アラーム値メッセージ
		String alarmItem = "";
		String alarmContent = "";

		if (eDaily.getErrorAlarmWorkRecordCode().equals("S001")) {

			alarmItem = TextResource.localize("KAL010_2");

			eDaily.getAttendanceItemList().sort((a, b) -> a.compareTo(b));
			for (Integer id : eDaily.getAttendanceItemList()) {
				alarmContent += "/" + attendanceNameMap.get(id).getAttendanceItemName();
			}
			if (alarmContent.length() > 0)
				alarmContent = alarmContent.substring(1, alarmContent.length());

		} else if (eDaily.getErrorAlarmWorkRecordCode().equals("S004")) {

			alarmItem = TextResource.localize("KAL010_3");
			eDaily.getAttendanceItemList().sort((a, b) -> a.compareTo(b));
			int i = 0;
			for (Integer id : eDaily.getAttendanceItemList()) {
				i += 1;
				if (i % 2 != 0) {
					alarmContent += "/" + attendanceNameMap.get(id).getAttendanceItemName() + ": " + formatValueAttendance(mapAttendance.get(id).getValue());
				} else {
					alarmContent += "、　" + attendanceNameMap.get(id).getAttendanceItemName() + ": " + formatValueAttendance(mapAttendance.get(id).getValue());
				}
			}
			if (alarmContent.length() > 0)
				alarmContent = alarmContent.substring(1, alarmContent.length());

		} else if (eDaily.getErrorAlarmWorkRecordCode().equals("S005")) {

			alarmItem = TextResource.localize("KAL010_4");

			Optional<String> optWorkTypeCode = recordWorkInfoFunAdapter.getWorkTypeCode(eDaily.getEmployeeID(), eDaily.getDate());
			if (!optWorkTypeCode.isPresent())
				throw new RuntimeException("Not found work type code!");
			Optional<WorkType> optWorkType = workTypeRepository.findByPK(companyID, optWorkTypeCode.get());
			if (!optWorkType.isPresent())
				throw new RuntimeException(" WorkType domain not found!");

			alarmContent = TextResource.localize("KAL010_5", optWorkType.get().getName().v(), formatValueAttendance(mapAttendance.get(16).getValue()),
					formatValueAttendance(mapAttendance.get(19).getValue()));

		} else if (eDaily.getErrorAlarmWorkRecordCode().equals("S003")) {

			alarmItem = TextResource.localize("KAL010_12");
			eDaily.getAttendanceItemList().sort((a, b) -> a.compareTo(b));

			int i = 0;
			for (Integer id : eDaily.getAttendanceItemList()) {
				i += 1;
				if (i % 2 != 0) {
					alarmContent += "/" + attendanceNameMap.get(id).getAttendanceItemName();
				} else {
					alarmContent += "、　" + attendanceNameMap.get(id).getAttendanceItemName();
				}
			}
			if (alarmContent.length() > 0)
				alarmContent = TextResource.localize("KAL010_2") + " " + alarmContent.substring(1, alarmContent.length());

		} else if (eDaily.getErrorAlarmWorkRecordCode().equals("S007")) {
			alarmItem = TextResource.localize("KAL010_68");
			Integer id = eDaily.getAttendanceItemList().get(0);
			alarmItem = TextResource.localize("KAL010_69", attendanceNameMap.get(id).getAttendanceItemName(), formatValueAttendance(mapAttendance.get(id).getValue()));

		} else if (eDaily.getErrorAlarmWorkRecordCode().equals("S008")) {
			alarmItem = TextResource.localize("KAL010_70");
			Integer id = eDaily.getAttendanceItemList().get(0);
			alarmItem = TextResource.localize("KAL010_71", attendanceNameMap.get(id).getAttendanceItemName(), formatValueAttendance(mapAttendance.get(id).getValue()));

		} else if (eDaily.getErrorAlarmWorkRecordCode().equals("S006")) {
			alarmItem = TextResource.localize("KAL010_17");
			alarmContent = TextResource.localize("KAL010_18");
		} else {
			alarmItem = errorAlarmMap.get(eDaily.getErrorAlarmWorkRecordCode()).getName();
		}

		return new AlarmContentMessage(alarmItem, alarmContent,errorAlarmMap.get(eDaily.getErrorAlarmWorkRecordCode()).getName());
	}
	
	private String formatValueAttendance(String attendanceValue) {
		int att = Integer.parseInt(attendanceValue);
		Integer hours = att / 60, minutes = att % 60;

		return String.join("", hours < 10 ? "0" : "", hours.toString(), ":", minutes < 10 ? "0" : "", minutes.toString());
	}

}
