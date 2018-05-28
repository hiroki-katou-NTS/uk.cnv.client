package nts.uk.ctx.at.schedule.dom.schedule.commonalgorithm;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.EmployeeGeneralInfoImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.classification.ExClassificationHistItemImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.classification.ExClassificationHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.employment.ExEmploymentHistItemImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.employment.ExEmploymentHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.jobtitle.ExJobTitleHistItemImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.jobtitle.ExJobTitleHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.workplace.ExWorkPlaceHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.workplace.ExWorkplaceHistItemImported;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository;
import nts.uk.ctx.at.shared.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmp;
import nts.uk.ctx.at.shared.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmpAdaptor;
import nts.uk.ctx.at.shared.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmpHis;
import nts.uk.ctx.at.shared.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmpHisAdaptor;
import nts.uk.shr.infra.i18n.resource.I18NResourcesForUK;

/**
 * 勤務予定マスタ情報を取得する(lấy các thông tin master 勤務予定マスタ情報)
 * 
 * @author sonnh1
 *
 */
@Stateless
public class ScheduleMasterInformationServiceImpl implements ScheduleMasterInformationService {

	@Inject
	private ScheduleErrorLogRepository scheduleErrorLogRepository;

	@Inject
	private I18NResourcesForUK internationalization;

	@Inject
	private BusinessTypeOfEmpHisAdaptor businessTypeOfEmpHisAdaptor;

	@Inject
	private BusinessTypeOfEmpAdaptor businessTypeOfEmpAdaptor;

	@Override
	public Optional<ScheduleMasterInformationDto> getScheduleMasterInformationDto(String employeeId,
			GeneralDate baseDate, String exeId, EmployeeGeneralInfoImported empGeneralInfo) {
		
		Optional<ScheduleMasterInformationDto> result = Optional.of(new ScheduleMasterInformationDto());

		Map<String, List<ExEmploymentHistItemImported>> mapEmploymentHist = empGeneralInfo.getEmploymentDto().stream()
				.collect(Collectors.toMap(ExEmploymentHistoryImported::getEmployeeId,
						ExEmploymentHistoryImported::getEmploymentItems));

		Map<String, List<ExClassificationHistItemImported>> mapClassificationHist = empGeneralInfo
				.getClassificationDto().stream()
				.collect(Collectors.toMap(ExClassificationHistoryImported::getEmployeeId,
						ExClassificationHistoryImported::getClassificationItems));

		Map<String, List<ExJobTitleHistItemImported>> mapJobTitleHist = empGeneralInfo.getJobTitleDto().stream()
				.collect(Collectors.toMap(ExJobTitleHistoryImported::getEmployeeId,
						ExJobTitleHistoryImported::getJobTitleItems));

		Map<String, List<ExWorkplaceHistItemImported>> mapWorkplaceHist = empGeneralInfo.getWorkplaceDto().stream()
				.collect(Collectors.toMap(ExWorkPlaceHistoryImported::getEmployeeId,
						ExWorkPlaceHistoryImported::getWorkplaceItems));

		// 雇用コードを取得する
		boolean valueSetEmpCode = this.setEmployeeCode(exeId, employeeId, baseDate, result, mapEmploymentHist);
		if (!valueSetEmpCode) {
			return Optional.empty();
		}
		// 分類コードを取得する
		this.getClassificationCode(employeeId, baseDate, result, mapClassificationHist);

		// 職位IDを取得する
		boolean valueAcquireJobTitleId = this.acquireJobTitleId(exeId, employeeId, baseDate, result, mapJobTitleHist);
		if (!valueAcquireJobTitleId) {
			return Optional.empty();
		}

		// 職場IDを取得する
		boolean valueAcquireWorkplaceId = this.acquireWorkplaceId(exeId, employeeId, baseDate, result,
				mapWorkplaceHist);
		if (!valueAcquireWorkplaceId) {
			return Optional.empty();
		}

		// 勤務種別コードを取得する
		this.acquireWorkTypeCode(employeeId, baseDate, result);

		return result;
	}

	/**
	 * 雇用コードを取得する
	 * 
	 * @param exeId
	 * @param employeeId
	 * @param baseDate
	 * @param result
	 * @param mapEmploymentHist
	 * @return
	 */
	private boolean setEmployeeCode(String exeId, String employeeId, GeneralDate baseDate,
			Optional<ScheduleMasterInformationDto> result,
			Map<String, List<ExEmploymentHistItemImported>> mapEmploymentHist) {

		List<ExEmploymentHistItemImported> listEmpHistItem = mapEmploymentHist.get(employeeId);
		if (listEmpHistItem != null) {
			Optional<ExEmploymentHistItemImported> optEmpHistItem = listEmpHistItem.stream()
					.filter(empHistItem -> empHistItem.getPeriod().contains(baseDate) == true).findFirst();
			if (optEmpHistItem.isPresent()) {
				result.get().setEmployeeCode(optEmpHistItem.get().getEmploymentCode());
				return true;
			}
		}

		// if (employmentHistoryImported.isPresent()) {
		// String employmentCode =
		// employmentHistoryImported.get().getEmploymentCode();
		// result.get().setEmployeeCode(employmentCode);
		// return true;
		// }
		ScheduleErrorLog scheduleErrorLog = new ScheduleErrorLog(this.getErrorContent("Msg_602", "#Com_Employment"),
				exeId, baseDate, employeeId);
		this.scheduleErrorLogRepository.add(scheduleErrorLog);
		return false;
	}

	/**
	 * 分類コードを取得する
	 * 
	 * @param employeeId
	 * @param baseDate
	 * @param result
	 * @param mapClassificationHist
	 */
	private void getClassificationCode(String employeeId, GeneralDate baseDate,
			Optional<ScheduleMasterInformationDto> result,
			Map<String, List<ExClassificationHistItemImported>> mapClassificationHist) {
		// Imported「所属分類履歴」から分類コードを取得する(lấy 分類コード từ Imported「所属分類履歴」)
		// Optional<SClsHistImported> hisExport =
		// this.syClassificationAdapter.findSClsHistBySid(companyId, employeeId,
		// baseDate);
		List<ExClassificationHistItemImported> listClassHistItem = mapClassificationHist.get(employeeId);
		if (listClassHistItem != null) {
			Optional<ExClassificationHistItemImported> optClassHistItem = listClassHistItem.stream()
					.filter(classHistItem -> classHistItem.getPeriod().contains(baseDate)).findFirst();
			if (optClassHistItem.isPresent()) {
				result.get().setClassificationCode(optClassHistItem.get().getClassificationCode());
			}
		}
		// if (hisExport.isPresent()) {
		// String classificationCode = hisExport.get().getClassificationCode();
		// result.get().setClassificationCode(classificationCode);
		// }
		else {
			result.get().setClassificationCode(null);
		}
	}

	/**
	 * 職位IDを取得する
	 * 
	 * @param exeId
	 * @param employeeId
	 * @param baseDate
	 * @param result
	 * @param mapJobTitleHist
	 * @return
	 */
	private boolean acquireJobTitleId(String exeId, String employeeId, GeneralDate baseDate,
			Optional<ScheduleMasterInformationDto> result,
			Map<String, List<ExJobTitleHistItemImported>> mapJobTitleHist) {
		// Imported「所属職位履歴」から職位IDを取得する(lấy 職位ID từ Imported「所属職位履歴」)
		// Optional<EmployeeJobHistImported> employeeJobHisOptional =
		// this.syJobTitleAdapter.findBySid(employeeId,
		// baseDate);
		// if (employeeJobHisOptional.isPresent()) {
		// String jobId = employeeJobHisOptional.get().getJobTitleID();
		// result.get().setJobId(jobId);
		// return true;
		// }
		// else {
		// ScheduleErrorLog scheduleErrorLog = new
		// ScheduleErrorLog(this.getErrorContent("Msg_602", "#Com_Jobtitle"),
		// exeId, baseDate, employeeId);
		// this.scheduleErrorLogRepository.add(scheduleErrorLog);
		// return false;
		// }
		List<ExJobTitleHistItemImported> listJobTitleHistItem = mapJobTitleHist.get(employeeId);
		if (listJobTitleHistItem != null) {
			Optional<ExJobTitleHistItemImported> optJobTitleHistItem = listJobTitleHistItem.stream()
					.filter(jobTitleHistItem -> jobTitleHistItem.getPeriod().contains(baseDate)).findFirst();
			if (optJobTitleHistItem.isPresent()) {
				result.get().setJobId(optJobTitleHistItem.get().getJobTitleId());
				return true;
			}
		}

		ScheduleErrorLog scheduleErrorLog = new ScheduleErrorLog(this.getErrorContent("Msg_602", "#Com_Jobtitle"),
				exeId, baseDate, employeeId);
		this.scheduleErrorLogRepository.add(scheduleErrorLog);
		return false;
	}

	/**
	 * 職場IDを取得する
	 * 
	 * @param exeId
	 * @param employeeId
	 * @param baseDate
	 * @param result
	 * @param mapWorkplaceHist
	 * @return
	 */
	private boolean acquireWorkplaceId(String exeId, String employeeId, GeneralDate baseDate,
			Optional<ScheduleMasterInformationDto> result,
			Map<String, List<ExWorkplaceHistItemImported>> mapWorkplaceHist) {
		// Imported「所属職場履歴」から職場IDを取得する(lấy職場ID từ Imported「所属職場履歴」)
		// Optional<SWkpHistImported> swkpHisOptional =
		// this.syWorkplaceAdapter.findBySid(employeeId, baseDate);
		// if (swkpHisOptional.isPresent()) {
		// String workPlaceId = swkpHisOptional.get().getWorkplaceId();
		// result.get().setWorkplaceId(workPlaceId);
		// return true;
		// } else {
		// ScheduleErrorLog scheduleErrorLog = new
		// ScheduleErrorLog(this.getErrorContent("Msg_602", "#Com_Workplace"),
		// exeId, baseDate, employeeId);
		// this.scheduleErrorLogRepository.add(scheduleErrorLog);
		// return false;
		// }

		List<ExWorkplaceHistItemImported> listWorkplaceHistItem = mapWorkplaceHist.get(employeeId);
		if (listWorkplaceHistItem != null) {
			Optional<ExWorkplaceHistItemImported> optWorkplaceHistItem = listWorkplaceHistItem.stream()
					.filter(workplaceHistItem -> workplaceHistItem.getPeriod().contains(baseDate) == true).findFirst();
			if (optWorkplaceHistItem.isPresent()) {
				result.get().setWorkplaceId(optWorkplaceHistItem.get().getWorkplaceId());
				return true;
			}
		}

		ScheduleErrorLog scheduleErrorLog = new ScheduleErrorLog(this.getErrorContent("Msg_602", "#Com_Workplace"),
				exeId, baseDate, employeeId);
		this.scheduleErrorLogRepository.add(scheduleErrorLog);
		return false;

	}

	/**
	 * 
	 * 勤務種別コードを取得する
	 * 
	 * @param employeeId
	 * @param baseDate
	 * @param result
	 */

	private void acquireWorkTypeCode(String employeeId, GeneralDate baseDate,
			Optional<ScheduleMasterInformationDto> result) {
		// ドメインモデル「社員の勤務種別の履歴」を取得する
		Optional<BusinessTypeOfEmpHis> businessTypeOfEmpHis = this.businessTypeOfEmpHisAdaptor
				.findByBaseDateAndSid(baseDate, employeeId);
		if (!businessTypeOfEmpHis.isPresent()) {
			result.get().setBusinessTypeCode(null);
			return;
		}
		// ドメインモデル「社員の勤務種別」を取得する
		Optional<BusinessTypeOfEmp> businessTypeOfEmp = this.businessTypeOfEmpAdaptor.getBySidAndHistId(employeeId,
				businessTypeOfEmpHis.get().getHistoryId());
		if (!businessTypeOfEmp.isPresent()) {
			result.get().setBusinessTypeCode(null);
			return;
		}

		result.get().setBusinessTypeCode(businessTypeOfEmp.get().getBusinessTypeCode());
	}

	/**
	 * 
	 * @param messageId
	 * @param paramMsg
	 * @return error content
	 */
	private String getErrorContent(String messageId, String paramMsg) {
		return internationalization.localize(messageId, paramMsg).get();
	}
}
