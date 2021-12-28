package nts.uk.screen.at.app.find.ktg.ktg005.a;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.common.service.smartphone.output.DeadlineLimitCurrentMonth;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service.AppDeadlineSettingGet;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService.RequireM3;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.EmployeeIdClosureIdDto;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureIdReferEmployeeIds;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.ApplicationStatusWidgetItem;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.ApproveWidgetRepository;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.StandardWidget;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.StandardWidgetType;
import nts.uk.ctx.sys.portal.dom.toppagepart.standardwidget.TopPageDisplayYearMonthEnum;
import nts.uk.screen.at.app.ktgwidget.ktg004.CurrentClosingPeriod;
import nts.uk.screen.at.app.ktgwidget.ktg004.KTG004Finder;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 
 * @author sonnlb UKDesign.UniversalK.就業.KTG_ウィジェット.KTG005_申請件数.ユースケース.起動する.起動する
 */
@Stateless
public class StartScreenA {

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private AppDeadlineSettingGet appDeadlineSettingGet;

	@Inject
	private ApplicationRepository appRepo;

	@Inject
	private RoleExportRepo roleExportRepo;

	@Inject
	private ApproveWidgetRepository approveWidgetRepo;
	
	@Inject
	private GetClosureIdReferEmployeeIds getClosureIdReferEmployeeIds;
	
	@Inject
	private KTG004Finder KTG004Finder;
	
	@Inject 
	private ShareEmploymentAdapter shareEmploymentAdapter;

	/**
	 * @name 申請件数起動
	 * @param companyId  会社ID
	 * @param employeeId 社員ID
	 * @return
	 */
	public ExecutionResultNumberOfApplicationDto startScreenA(String cid, String employeeId) {
		//＜CR＞表示年月が存在する
		//存在しない場合
		List<EmployeeIdClosureIdDto> employeeIdClosureId = new ArrayList<EmployeeIdClosureIdDto>();
		//アルゴリズム「社員(list)に対応する処理締めを取得する」を実行する
		employeeIdClosureId = getClosureIdReferEmployeeIds.get( Arrays.asList(employeeId));
		
		//アルゴリズム「指定した年月の期間を算出する」を実行する
		//DatePeriod datePeriod = ClosureService.getClosurePeriod(ClosureService.createRequireM1(closureRepository, closureEmploymentRepo), topPageDisplayDate.isPresent()?topPageDisplayDate.get().getClosureId():employeeIdClosureId.get(0).getClosureId(), GeneralDate.today().yearMonth());
		
		//Get the processing deadline for employees - 社員に対応する処理締めを取得する
		RequireM3 require = ClosureService.createRequireM3(closureRepository, closureEmploymentRepo, shareEmploymentAdapter);
		Closure closure = ClosureService.getClosureDataByEmployee(require, new CacheCarrier(), employeeId, GeneralDate.today());
		
		//Calculate the period of the specified year and month - 指定した年月の期間を算出する
		DatePeriod datePeriod = ClosureService.getClosurePeriod(closure, closure.getClosureMonth().getProcessingYm());
		
		// 指定するウィジェットの設定を取得する
		Optional<StandardWidget> standardWidgetOpt = approveWidgetRepo.findByWidgetTypeAndCompanyId(StandardWidgetType.APPLICATION_STATUS, cid);
		
		ExecutionResultNumberOfApplicationDto result = new ExecutionResultNumberOfApplicationDto();
		//設定が取得できた場合
		if(standardWidgetOpt.isPresent()) {
			//名称、申請状況の詳細設定
			result.setTopPagePartName(standardWidgetOpt.get().getName().v());
			result.setAppSettings(standardWidgetOpt.get().getAppStatusDetailedSettingList().stream()
					.map(x -> new ApplicationStatusDetailedSettingDto(x.getDisplayType().value, x.getItem().value))
					.collect(Collectors.toList()));
		} else /*設定が取得できなかった場合*/	{
			/*
			 * 2021/02/18 EA3960
			 *  設定がない場合、初期値を登録する 
			 *  $＝申請件数の実行結果 
			 *  $.名称 ← #KTG005_15(あなたの申請)
			 *  $.申請状況の詳細設定 
			 *  ※.項目＝Enum「申請状況ウィジェットの項目」 
			 *  .項目、.表示区分 ← すべての項目、表示する
			 */
			result.setTopPagePartName(TextResource.localize("KTG005_15"));
			List<ApplicationStatusDetailedSettingDto> applicationStatusDetailedSettings = new ArrayList<>();
			for (ApplicationStatusWidgetItem value : ApplicationStatusWidgetItem.values()) {
				applicationStatusDetailedSettings.add(new ApplicationStatusDetailedSettingDto(NotUseAtr.USE.value, value.value));
			}
			result.setAppSettings(applicationStatusDetailedSettings);
		}
		
		// 取得した「申請状況の詳細設定」．表示区分をチェックする	
		Optional<ApplicationStatusDetailedSettingDto> itemOpt = result.getAppSettings().stream().filter(item -> item.getItem() == ApplicationStatusWidgetItem.MONTH_APP_DEADLINE.value && item.getDisplayType() == NotUseAtr.USE.value).findFirst();

		//表示する
		if (itemOpt.isPresent()) {
			// 申請締切設定を取得する
			DeadlineLimitCurrentMonth deadLine = appDeadlineSettingGet.getApplicationDeadline(cid, employeeId,
				employeeIdClosureId.get(0).getClosureId());
			result.setDueDate(deadLine.getOpAppDeadline().map(x -> x).orElse(GeneralDate.today()));
			result.setDeadlineSetting(deadLine.isUseAtr());
		}
		
		//取得した「申請状況の詳細設定」．表示区分をチェックする
		List<ApplicationStatusDetailedSettingDto> usedList = result.getAppSettings().stream()
			.filter(x -> x.getItem() != ApplicationStatusWidgetItem.MONTH_APP_DEADLINE.value && x.getDisplayType() == NotUseAtr.USE.value)
			.collect(Collectors.toList());
		//アルゴリズム「トップページの対象期間を取得する」を実行する
		CurrentClosingPeriod currentClosingPeriod = KTG004Finder.getTargetPeriodOfTopPage(
			employeeIdClosureId.get(0).getClosureId(),
			new CurrentClosingPeriod(datePeriod.end().yearMonth().v(), datePeriod.start(), datePeriod.end().addYears(1)), 
			Optional.empty(), 
			TopPageDisplayYearMonthEnum.THIS_MONTH_DISPLAY);
					
		//いずれが表示する
		if(!usedList.isEmpty()) {
			// アルゴリズム「申請件数取得」を実行する
			result.setNumberOfApp(GetNumberOfApps.getNumberOfApps(GetNumberOfApps.createRequire(appRepo), cid, new DatePeriod(currentClosingPeriod.getStartDate(), currentClosingPeriod.getEndDate()), employeeId));
		}
		// ログイン者が担当者か判断する
		boolean isEmployeeCharge = roleExportRepo.getWhetherLoginerCharge(AppContexts.user().roles()).isEmployeeCharge();
		//セット項目：
		//申請件数の実行結果．勤怠担当者である＝取得した就業担当者か
		result.setEmployeeCharge(isEmployeeCharge);
		
		result.setPeriod(DatePeriodDto.builder()
				.startDate(datePeriod.start().toString())
				.endDate(datePeriod.end().toString())
				.build());
		
		return result;
	}
}
