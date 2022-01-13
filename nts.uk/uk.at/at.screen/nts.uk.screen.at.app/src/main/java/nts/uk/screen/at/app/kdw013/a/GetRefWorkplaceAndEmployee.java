package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetAllEmployeeWithWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetWorkplaceOfEmployeeAdapter;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSettingRepository;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.CheckShortageFlex;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.WorkplaceExportServiceAdapter;
import nts.uk.ctx.bs.employee.dom.workplace.info.OutsideWorkplaceCode;
import nts.uk.ctx.bs.employee.dom.workplace.info.WkpCode;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceDisplayName;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceGenericName;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceInfo;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceName;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.参照可能職場・社員を取得する
 * 
 * @author tutt <<ScreenQuery>>
 */
@Stateless
public class GetRefWorkplaceAndEmployee {

	@Inject
	private ManHourRecordReferenceSettingRepository workChangeablePeriodSettingRepo;

	@Inject
	private WorkplaceExportServiceAdapter serviceAdapter;

	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;
	
	@Inject
	private ManHourRecordReferenceSettingRepository manRepo;
	
	@Inject
	private GetWorkplaceOfEmployeeAdapter getWorkplaceOfEmployeeAdapter;

	@Inject
	private GetAllEmployeeWithWorkplaceAdapter getAllEmployeeWithWorkplaceAdapter;
	
	@Inject
	private CheckShortageFlex checkShortageFlex;
	
	public GetRefWorkplaceAndEmployeeDto get(GeneralDate refDate) {
		String companyId = AppContexts.user().companyId();
		GetRefWorkplaceAndEmployeeDto result = new GetRefWorkplaceAndEmployeeDto();

		// 1: get(ログイン会社ID)
		Optional<ManHourRecordReferenceSetting> optWorkChangeablePeriodSetting = workChangeablePeriodSettingRepo
				.get(companyId);

		// 2: 参照可能範囲を取得する(ログイン会社ID, ログインユーザID, ログイン社員ID, 基準日): Map<社員ID,職場ID>
		RequireImpl require = new RequireImpl(manRepo, getWorkplaceOfEmployeeAdapter,getAllEmployeeWithWorkplaceAdapter, checkShortageFlex);
		List<String> lstEmpIds = new ArrayList<>();
		List<String> listWorkplaceId = new ArrayList<>();
		Map<String, String> employeeInfos = new HashMap<>();
		
		if (optWorkChangeablePeriodSetting.isPresent()) {
			employeeInfos = optWorkChangeablePeriodSetting.get()
					.getWorkCorrectionStartDate(require, companyId, AppContexts.user().userId(), AppContexts.user().employeeId(), refDate);
			
			lstEmpIds = new ArrayList<String>(employeeInfos.keySet());
			listWorkplaceId = employeeInfos.values().stream().distinct().collect(Collectors.toList());
		}

		// 3: <call>()
		// [No.600]社員ID（List）から社員コードと表示名を取得（削除社員考慮）
		DatePeriod datePeriod = new DatePeriod(refDate, refDate);
		List<EmployeeBasicInfoDto> lstEmployeeInfo = empEmployeeAdapter.getEmpInfoLstBySids(lstEmpIds, datePeriod, true, true).stream().map(info-> {
					return new EmployeeBasicInfoDto(info.getSid(), info.getEmployeeCode(), info.getEmployeeName());
		}).collect(Collectors.toList());

		// 4: <call>()
		// [No.560]職場IDから職場の情報をすべて取得する
		// ※Map<社員ID,職場ID>からList＜社員ID（List）から社員コードと表示名を取得＞にない「社員ID」を除いてから「対象職場」を作る

		List<WorkplaceInfo> workplaceInfos = serviceAdapter
				.getWorkplaceInforByWkpIds(companyId, listWorkplaceId, refDate).stream()
				.map(mapper -> new WorkplaceInfo(companyId, 
						null,
						mapper.getWorkplaceId(),
						new WkpCode(mapper.getWorkplaceCode()), 
						new WorkplaceName(mapper.getWorkplaceName()),
						new WorkplaceGenericName(mapper.getWorkplaceGenericName()),
						new WorkplaceDisplayName(mapper.getWorkplaceDisplayName()),
						new OutsideWorkplaceCode(mapper.getWorkplaceExternalCode())))
				.collect(Collectors.toList());

		result.setWorkplaceInfos(workplaceInfos);
		result.setLstEmployeeInfo(lstEmployeeInfo);
		result.setEmployeeInfos(employeeInfos);

		return result;
	}
	
	@AllArgsConstructor
	public class RequireImpl implements ManHourRecordReferenceSetting.Require {
		
		private ManHourRecordReferenceSettingRepository manRepo;
		
		private GetWorkplaceOfEmployeeAdapter getWorkplaceOfEmployeeAdapter;
		
		private GetAllEmployeeWithWorkplaceAdapter getAllEmployeeWithWorkplaceAdapter;
		
		private CheckShortageFlex checkShortageFlex;
		
		@Override
		public ManHourRecordReferenceSetting get() {
			return manRepo.get(AppContexts.user().companyId()).get();
		}

		@Override
		public DatePeriod getPeriod(String employeeId, GeneralDate date) {
			return checkShortageFlex.findClosurePeriod(employeeId, date);
		}

		@Override
		public Map<String, String> getWorkPlace(String userID, String employeeID, GeneralDate date) {
			return getWorkplaceOfEmployeeAdapter.get(userID, employeeID, date);
		}

		@Override
		public Map<String, String> getByCID(String companyId, GeneralDate baseDate) {
			return getAllEmployeeWithWorkplaceAdapter.get(companyId, baseDate);
		}
		
	}

}
