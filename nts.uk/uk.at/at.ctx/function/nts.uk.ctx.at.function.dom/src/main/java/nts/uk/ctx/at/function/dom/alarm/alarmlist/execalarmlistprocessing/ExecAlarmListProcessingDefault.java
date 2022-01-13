package nts.uk.ctx.at.function.dom.alarm.alarmlist.execalarmlistprocessing;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.auth.dom.employmentrole.EmployeeReferenceRange;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapter;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapterImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;
import nts.uk.ctx.at.function.dom.adapter.dailymonthlyprocessing.DailyMonthlyprocessAdapterFn;
import nts.uk.ctx.at.function.dom.adapter.dailymonthlyprocessing.ExeStateOfCalAndSumImportFn;
import nts.uk.ctx.at.function.dom.adapter.employeebasic.SyEmployeeFnAdapter;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.EmployeeSearchDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.ExtractAlarmListService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.ExtractedAlarmDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.PeriodByAlarmCategory;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.persistenceextractresult.AlarmTopPageProcessingService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.sendautoexeemail.OutputSendAutoExe;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.sendautoexeemail.SendAutoExeEmailService;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.CheckConditionTimeDto;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.ExtractionRangeService;
import nts.uk.ctx.at.function.dom.alarm.extraprocessstatus.AlarmListExtraProcessStatus;
import nts.uk.ctx.at.function.dom.alarm.extraprocessstatus.AlarmListExtraProcessStatusRepository;
import nts.uk.ctx.at.function.dom.alarm.extraprocessstatus.ExtractionState;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.workrule.closure.*;
import nts.uk.shr.com.i18n.TextResource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class ExecAlarmListProcessingDefault implements ExecAlarmListProcessingService {

	@Inject
	private AlarmListExtraProcessStatusRepository alarmListExtraProcessStatusRepo;

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private ExtractionRangeService extractionRangeService;

	@Inject
	private ExtractAlarmListService extractAlarmListService;

	@Inject
	private SyEmployeeFnAdapter syEmployeeFnAdapter;

	@Inject
	private RegulationInfoEmployeeAdapter regulationInfoEmployeeAdapter;

	@Inject
	private EmployeeInformationAdapter employeeInformationAdapter;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;

	@Inject
	private SendAutoExeEmailService sendAutoExeEmailService;

	@Inject
	private DailyMonthlyprocessAdapterFn dailyMonthlyprocessAdapterFn;

	@Inject
	private AlarmTopPageProcessingService alarmTopPageProcessingService;

	@Override
	public OutputExecAlarmListPro execAlarmListProcessing(String extraProcessStatusID, String companyId,
	                                                      List<String> workplaceIdList, List<String> listPatternCode, GeneralDateTime dateTime,
	                                                      boolean sendMailPerson, boolean sendMailAdmin, String alarmCode, String empCalAndSumExecLogID,
														  String runCode, boolean isDisplayAdmin, boolean isDisplayPerson) {
		String errorMessage = "";
		// ドメインモデル「アラームリスト抽出処理状況」を取得する
		// TODO : ・状態＝処理中???
		Optional<AlarmListExtraProcessStatus> alarmListExtraProcessStatus = alarmListExtraProcessStatusRepo
				.getAlListExtaProcessByID(extraProcessStatusID);
		// 取得できなかった場合
		if (!alarmListExtraProcessStatus.isPresent()) {
			// ドメインモデル「アラームリスト抽出処理状況」を作成する
			int timenow = GeneralDateTime.now().hours() * 60 + GeneralDateTime.now().seconds();
			AlarmListExtraProcessStatus alarm = new AlarmListExtraProcessStatus(extraProcessStatusID, companyId,
					GeneralDate.today(), timenow, null, GeneralDate.today(), timenow, ExtractionState.ABNORMAL_TERMI);
			alarmListExtraProcessStatusRepo.addAlListExtaProcess(alarm);
			// エラー内容にエラーメッセージ(#Msg_1432)を設定する
			errorMessage = TextResource.localize("Msg_1432");
			return new OutputExecAlarmListPro(false, errorMessage, false);

		}

		// 期間内に特定の職場（List）に所属している社員一覧を取得
		List<String> listsyEmployeeFn = syEmployeeFnAdapter.getListEmployeeId(workplaceIdList,
				new DatePeriod(dateTime.toDate(), dateTime.toDate()));
		// if (listsyEmployeeFn.isEmpty())
		// return new OutputExecAlarmListPro(false,errorMessage);
		// クエリモデル「社員の情報」を取得する
		EmployeeInformationQueryDtoImport params = new EmployeeInformationQueryDtoImport(listsyEmployeeFn,
				dateTime.toDate(), true, false, false, true, false, false);
		List<EmployeeInformationImport> employeeInformation = employeeInformationAdapter.getEmployeeInfo(params);

		Set<String> listEmploymentCode = employeeInformation.stream().map(c -> c.getEmployment().getEmploymentCode())
				.collect(Collectors.toSet());
		// ドメインモデル「雇用に紐づく就業締め」を取得する
		List<ClosureEmployment> listClosureEmp = new ArrayList<>();
		for (String empCode : listEmploymentCode) {
			Optional<ClosureEmployment> closureEmployment = closureEmploymentRepository.findByEmploymentCD(companyId,
					empCode);
			if (closureEmployment.isPresent())
				listClosureEmp.add(closureEmployment.get());
		}
		// if (listClosureEmp.isEmpty())
		// return new OutputExecAlarmListPro(false,errorMessage);


		// 「締め」を取得する
		List<Closure> listClosure = closureRepository.findAllActive(companyId, UseClassification.UseClass_Use);

		// 取得した社員を雇用毎に分ける

		// 雇用毎に集計処理をする
		List<ExtractedAlarmDto> listExtractedAlarmDto = new ArrayList<>();
		for (ClosureEmployment closureEmployment : listClosureEmp) {
			List<String> listOneCode = new ArrayList<>();
			listOneCode.add(closureEmployment.getEmploymentCD());
			RegulationInfoEmployeeAdapterImport employeeInfo = this.createQueryEmployee(listOneCode, dateTime.toDate(),
					dateTime.toDate(), workplaceIdList);
			List<RegulationInfoEmployeeAdapterDto> lstRegulationInfoEmployee = this.regulationInfoEmployeeAdapter
					.find(employeeInfo);
			// if (lstRegulationInfoEmployee.isEmpty())
			// return new OutputExecAlarmListPro(false,errorMessage);
			List<EmployeeSearchDto> listEmployeeSearch = lstRegulationInfoEmployee.stream().map(c -> convertToImport(c))
					.collect(Collectors.toList());
			Integer processingYm = null;
			for (Closure closure : listClosure) {
				if (closureEmployment.getClosureId() == closure.getClosureId().value) {
					processingYm = closure.getClosureMonth().getProcessingYm().v();
					break;
				}
			}
			// 期間を算出する
			for (String patternCode : listPatternCode) {
				List<CheckConditionTimeDto> listCheckCondition = extractionRangeService.getPeriodByCategory(patternCode,
						companyId, closureEmployment.getClosureId(), processingYm);
				List<PeriodByAlarmCategory> listPeriodByCategory = new ArrayList<>();
				for (CheckConditionTimeDto checkConditionTime : listCheckCondition) {
					if (checkConditionTime.getCategory() == AlarmCategory.SCHEDULE_4WEEK.value
							|| checkConditionTime.getCategory() == AlarmCategory.DAILY.value
							|| checkConditionTime.getCategory() == AlarmCategory.SCHEDULE_DAILY.value
							|| checkConditionTime.getCategory() == AlarmCategory.WEEKLY.value
							|| checkConditionTime.getCategory() == AlarmCategory.APPLICATION_APPROVAL.value) {
						GeneralDate startDate = GeneralDate.fromString(checkConditionTime.getStartDate(), "yyyy/MM/dd");
						GeneralDate endDate = GeneralDate.fromString(checkConditionTime.getEndDate(), "yyyy/MM/dd");
						PeriodByAlarmCategory periodByAlarmCategory = new PeriodByAlarmCategory(
								checkConditionTime.getCategory(), checkConditionTime.getCategoryName(), startDate,
								endDate, checkConditionTime.getPeriod36Agreement());
						listPeriodByCategory.add(periodByAlarmCategory);
					} else if (checkConditionTime.getCategory() == AlarmCategory.MONTHLY.value
							|| checkConditionTime.getCategory() == AlarmCategory.MULTIPLE_MONTH.value
							|| checkConditionTime.getCategory() == AlarmCategory.SCHEDULE_MONTHLY.value
							|| checkConditionTime.getCategory() == AlarmCategory.SCHEDULE_YEAR.value) {
						GeneralDate startDate = GeneralDate.fromString(checkConditionTime.getStartMonth().substring(0, 4) + "/" + checkConditionTime.getStartMonth().substring(4, 6) + "/01",
								"yyyy/MM/dd");
						GeneralDate endDate = GeneralDate
								.fromString(checkConditionTime.getEndMonth().substring(0, 4) + "/" + checkConditionTime.getEndMonth().substring(4, 6) + "/01", "yyyy/MM/dd").addMonths(1)
								.addDays(-1);
						PeriodByAlarmCategory periodByAlarmCategory = new PeriodByAlarmCategory(
								checkConditionTime.getCategory(), checkConditionTime.getCategoryName(), startDate,
								endDate, checkConditionTime.getPeriod36Agreement());
						listPeriodByCategory.add(periodByAlarmCategory);

					} else if (checkConditionTime.getCategory() == AlarmCategory.MASTER_CHECK.value
							|| checkConditionTime.getCategory() == AlarmCategory.APPLICATION_APPROVAL.value){
						PeriodByAlarmCategory periodByAlarmCategory = new PeriodByAlarmCategory(
								checkConditionTime.getCategory(), checkConditionTime.getCategoryName(), null,
								null, checkConditionTime.getPeriod36Agreement());
						listPeriodByCategory.add(periodByAlarmCategory);
					} else if (checkConditionTime.getCategory() == AlarmCategory.AGREEMENT.value) {
						if (checkConditionTime.getCategoryName().equals("36協定　1・2・4週間")) {
							GeneralDate startDate = GeneralDate.fromString(checkConditionTime.getStartDate(),
									"yyyy/MM/dd");
							GeneralDate endDate = GeneralDate.fromString(checkConditionTime.getStartDate(),
									"yyyy/MM/dd");
							PeriodByAlarmCategory periodByAlarmCategory = new PeriodByAlarmCategory(
									checkConditionTime.getCategory(), checkConditionTime.getCategoryName(), startDate,
									endDate, checkConditionTime.getPeriod36Agreement());
							listPeriodByCategory.add(periodByAlarmCategory);
						} else if (checkConditionTime.getCategoryName().equals("36協定　年間")) {
							GeneralDate startDate = GeneralDate.fromString(checkConditionTime.getYear() + "/"
									+ checkConditionTime.getStartMonth().substring(4, 6) + "/01", "yyyy/MM/dd");
							GeneralDate endDate = GeneralDate
									.fromString(
											checkConditionTime.getYear() + "/"
													+ checkConditionTime.getEndMonth().substring(4, 6) + "/01",
											"yyyy/MM/dd")
									.addYears(1).addMonths(-1);
							PeriodByAlarmCategory periodByAlarmCategory = new PeriodByAlarmCategory(
									checkConditionTime.getCategory(), checkConditionTime.getCategoryName(), startDate,
									endDate, checkConditionTime.getPeriod36Agreement());
							listPeriodByCategory.add(periodByAlarmCategory);
						} else {
							GeneralDate startDate = GeneralDate.fromString(checkConditionTime.getStartMonth().substring(0, 4) + "/" + checkConditionTime.getStartMonth().substring(4, 6) + "/01",
									"yyyy/MM/dd");
							GeneralDate endDate = GeneralDate
									.fromString(checkConditionTime.getStartMonth().substring(0, 4) + "/" + checkConditionTime.getStartMonth().substring(4, 6) + "/01", "yyyy/MM/dd").addMonths(1)
									.addDays(-1);
							PeriodByAlarmCategory periodByAlarmCategory = new PeriodByAlarmCategory(
									checkConditionTime.getCategory(), checkConditionTime.getCategoryName(), startDate,
									endDate, checkConditionTime.getPeriod36Agreement());
							listPeriodByCategory.add(periodByAlarmCategory);
						}
					}

				}

				// 集計処理を実行する
				ExtractedAlarmDto extractedAlarmDto = extractAlarmListService.extractAlarm(listEmployeeSearch,
						patternCode, listPeriodByCategory, runCode);
				listExtractedAlarmDto.add(extractedAlarmDto);
				// アラーム（トップページ）永続化の処理
                List<String> lstSid = listEmployeeSearch.stream().map(EmployeeSearchDto::getId).collect(Collectors.toList());
				alarmTopPageProcessingService.persisTopPageProcessing(runCode, patternCode, lstSid, listPeriodByCategory, extractedAlarmDto.getPersisAlarmExtractResult(),
						extractedAlarmDto.getAlarmExtractConditions(), isDisplayAdmin, isDisplayPerson);

				//ドメインモデル「更新処理自動実行ログ」を取得しチェックする（中断されている場合は更新されているため、最新の情報を取得する）
				Optional<ExeStateOfCalAndSumImportFn> exeStateOfCalAndSumImportFn = dailyMonthlyprocessAdapterFn.executionStatus(empCalAndSumExecLogID);
				if (exeStateOfCalAndSumImportFn.isPresent())
					if (exeStateOfCalAndSumImportFn.get() == ExeStateOfCalAndSumImportFn.START_INTERRUPTION) {
						return new OutputExecAlarmListPro(true, errorMessage, true);
					}
			} // end for listpattencode

		} // end list employmentcode

		Optional<OutputSendAutoExe> outputSendAutoExe = sendAutoExeEmailService.sendAutoExeEmail(companyId, dateTime,
				listExtractedAlarmDto, sendMailPerson, sendMailAdmin, alarmCode);

		AlarmListExtraProcessStatus alarmListExtra = alarmListExtraProcessStatus.get();
		int endTime = GeneralDateTime.now().hours() * 60 + GeneralDateTime.now().minutes();
		alarmListExtra.setEndDateAndEndTime(GeneralDate.today(), endTime);
		if (outputSendAutoExe.isPresent()) {
			if (outputSendAutoExe.get().getExtractionState() == ExtractionState.ABNORMAL_TERMI) {
				alarmListExtra.setStatus(ExtractionState.ABNORMAL_TERMI);
				alarmListExtraProcessStatusRepo.updateAlListExtaProcess(alarmListExtra);
				errorMessage = TextResource.localize(outputSendAutoExe.get().getErrorMessage());
				return new OutputExecAlarmListPro(false, errorMessage, false);
			} else {
				alarmListExtra.setStatus(ExtractionState.SUCCESSFUL_COMPLE);
				alarmListExtraProcessStatusRepo.updateAlListExtaProcess(alarmListExtra);
				errorMessage = TextResource.localize(outputSendAutoExe.get().getErrorMessage());
				return new OutputExecAlarmListPro(true, errorMessage, false);
			}
		}
		alarmListExtra.setStatus(ExtractionState.SUCCESSFUL_COMPLE);
		alarmListExtraProcessStatusRepo.updateAlListExtaProcess(alarmListExtra);
		return new OutputExecAlarmListPro(true, errorMessage, false);
	}

	private EmployeeSearchDto convertToImport(RegulationInfoEmployeeAdapterDto employeeInfo) {
		return new EmployeeSearchDto(employeeInfo.getEmployeeId(), employeeInfo.getEmployeeCode(),
				employeeInfo.getEmployeeName(), employeeInfo.getWorkplaceId(), employeeInfo.getWorkplaceCode(),
				employeeInfo.getWorkplaceName());
	}

	private RegulationInfoEmployeeAdapterImport createQueryEmployee(List<String> employeeCodes, GeneralDate startDate,
	                                                                GeneralDate endDate, List<String> wordplaceIds) {
		RegulationInfoEmployeeAdapterImport query = new RegulationInfoEmployeeAdapterImport();
		query.setBaseDate(GeneralDateTime.now());
		query.setReferenceRange(EmployeeReferenceRange.ONLY_MYSELF.value);
		query.setFilterByEmployment(true);
		query.setEmploymentCodes(employeeCodes);
		// query.setFilterByDepartment(false);
		// query.setDepartmentCodes(Collections.emptyList());
		query.setFilterByWorkplace(true);
		query.setFilterByClassification(false);
		query.setClassificationCodes(Collections.emptyList());
		query.setFilterByJobTitle(false);
		query.setJobTitleCodes(Collections.emptyList());
		query.setFilterByWorktype(false);
		query.setWorktypeCodes(Collections.emptyList());
		query.setPeriodStart(startDate);
		query.setPeriodEnd(endDate);
		query.setIncludeIncumbents(true);
		query.setIncludeWorkersOnLeave(false);
		query.setIncludeOccupancy(false);
		// query.setIncludeAreOnLoan(true);
		// query.setIncludeGoingOnLoan(false);
		query.setIncludeRetirees(false);
		query.setRetireStart(GeneralDate.today());
		query.setRetireEnd(GeneralDate.today());
		query.setFilterByClosure(false);
		query.setSystemType(2);
		query.setWorkplaceCodes(wordplaceIds);
		query.setNameType("ビジネスネーム日本語");
		return query;
	}

}
