package nts.uk.ctx.at.request.dom.application.holidayworktime.service;

import java.util.List;
import java.util.Map;

import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.AppHdWorkDispInfoOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;

/**
 * Refactor5
 * @author huylq
 *
 */
public interface HolidayWorkRegisterService {

	/**
	 * UKDesign.UniversalK.就業.KAF_申請.KAF010_休日出勤時間申請.アルゴリズム.2.休出申請（新規）登録処理.2.休出申請（新規）登録処理
	 * @param companyId
	 * @param appHolidayWork
	 * @param appTypeSetting
	 * @param appHdWorkDispInfoOutput
	 * @return
	 */
	public ProcessResult register(
			String companyId,
			AppHolidayWork appHolidayWork,
			AppTypeSetting appTypeSetting, 
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput
			);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.KAF010_休日出勤時間申請.アルゴリズム.2.休出申請（新規）登録処理(複数人).2.休出申請（新規）登録処理(複数人)
	 * @param companyId
	 * @param empList
	 * @param appTypeSetting
	 * @param appHdWorkDispInfoOutput
	 * @param appHolidayWork
	 * @param approvalRootContentMap
	 * @param appOvertimeDetailMap
	 * @return
	 */
	public ProcessResult registerMulti(String companyId, List<String> empList, AppTypeSetting appTypeSetting, 
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput,
			AppHolidayWork appHolidayWork, Map<String, ApprovalRootContentImport_New> approvalRootContentMap);

	/**
	 * UKDesign.UniversalK.就業.KAF_申請.KAF010_休日出勤時間申請.アルゴリズム.8.休出申請（詳細）登録処理.8.休出申請（詳細）登録処理
	 * @param companyId
	 * @param appHolidayWork
	 * @param appDispInfoStartupOutput
	 * @return
	 */
	public ProcessResult update(String companyId, AppHolidayWork appHolidayWork, AppDispInfoStartupOutput appDispInfoStartupOutput);

	/**
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS10_休日出勤時間申請（スマホ）.A：休日出勤申請（新規・編集）.アルゴリズム.休日出勤申請の登録処理.休日出勤申請の登録処理
	 * @param mode
	 * @param companyId
	 * @param appHdWorkDispInfo
	 * @param appHolidayWork
	 * @param appTypeSetting
	 * @return
	 */
	public ProcessResult registerMobile(Boolean mode, String companyId, AppHdWorkDispInfoOutput appHdWorkDispInfo,
			AppHolidayWork appHolidayWork, AppTypeSetting appTypeSetting);
}
