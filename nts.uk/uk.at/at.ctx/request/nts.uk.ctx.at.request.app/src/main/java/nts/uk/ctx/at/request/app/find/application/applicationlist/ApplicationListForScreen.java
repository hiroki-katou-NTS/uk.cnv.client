package nts.uk.ctx.at.request.app.find.application.applicationlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleImport;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.algorithm.SpecHdFrameForWkTypeSetService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ApplicationListForScreen {
	@Inject
	private ScBasicScheduleAdapter scBasicScheduleAdapter;
	
	@Inject
	private ApplicationRepository applicationRepository_New;
	
	@Inject
	private SpecHdFrameForWkTypeSetService specHdFrameForWkTypeSetService;
	
	@Inject
	public WorkTypeRepository workTypeRepo;
	/**
	 * 社員、期間に一致する申請を取得する
	 * requestList #26
	 * getApplicationBySID 
	 * @param employeeID
	 * @param startDate
	 * @param endDate
	 * @return list<ApplicationExport>
	 */
	public List<ApplicationExportDto> getApplicationBySID(List<String> employeeID, GeneralDate startDate, GeneralDate endDate){
		
		List<ApplicationExportDto> applicationExports = new ArrayList<>();
		// ドメインモデル「申請」を取得する
		List<Application> applications = applicationRepository_New.getApplicationBySIDs(employeeID, startDate, endDate);
		
		if (CollectionUtil.isEmpty(applications)) { // 終了状態：申請取得失敗
			
			return Collections.emptyList();
		}
		
		
		for (Application application : applications) { // 申請の件数分ループ
			// 対象日リストを取得する
			List<GeneralDate> targetList = this.getDateTargets(application);
			
			for (GeneralDate date : targetList) { // 対象日リストのループ
				ApplicationExportDto applicationExport = ApplicationExportDto.builder()
						.appDate(date)
						.employeeID(application.getEmployeeID())
						.appType(application.getAppType().value)
						.appTypeName(application.getAppType().name)
						.reflectState(application.getReflectionStatus().getListReflectionStatusOfDay().get(0).getActualReflectStatus().value)
						.prePostAtr(application.getPrePostAtr().value)
						.build();
				
				applicationExports.add(applicationExport);
			}
			
		}		
		return applicationExports;
	}
	
	
	
	/**
	 * 社員、期間に一致する申請をグループ化して取得する
	 * RequestList #542
	 * @param employeeID
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<AppGroupExportDto> getApplicationGroupBySID(List<String> employeeID, GeneralDate startDate,
			GeneralDate endDate) {
		List<ApplicationExportDto> appExportLst = this.getApplicationBySID(employeeID, startDate, endDate);
		List<AppGroupExportDto> result = new ArrayList<>();
		
		Map<Object, List<AppGroupExportDto>> mapDate =  appExportLst.stream()
				.map(x -> new AppGroupExportDto(x.getAppDate(),x.getAppType(),x.getEmployeeID(),x.getAppTypeName(), x.getPrePostAtr()))
				.collect(Collectors.groupingBy(x -> Pair.of(x.getAppDate(), x.getEmployeeID())));
		mapDate.entrySet().stream().forEach(x -> {
			Map<Object, List<AppGroupExportDto>> mapDateType = x.getValue().stream().collect(Collectors.groupingBy(y -> y.getAppType()));
			mapDateType.entrySet().stream().forEach(y -> {
				if(Integer.valueOf(y.getKey().toString())==ApplicationType.ABSENCE_APPLICATION.value){
					Map<Object, List<AppGroupExportDto>> mapDateTypeAbsence = y.getValue().stream().collect(Collectors.groupingBy(z -> z.getAppTypeName()));
					mapDateTypeAbsence.entrySet().stream().forEach(z -> {
						Map<Object, List<AppGroupExportDto>> mapDateTypeAbsencPrePost = z.getValue().stream().collect(Collectors.groupingBy(t -> t.getPrePostAtr()));
						mapDateTypeAbsencPrePost.entrySet().stream().forEach(t -> {
							result.add(t.getValue().get(0));
						});
						
					});
				} else {
					Map<Object, List<AppGroupExportDto>> mapDateTypePrePost = y.getValue().stream().collect(Collectors.groupingBy(t -> t.getPrePostAtr()));
					mapDateTypePrePost.entrySet().stream().forEach(z -> {
						result.add(z.getValue().get(0));
					});
				}
			});
		});
		return result;
	}
	
	/**
	 * [No.556]遷移先申請画面一覧を取得する
	 * RequestList #556
	 * @param companyID
	 * @return
	 */
	public List<AppWithDetailExportDto> getAppWithOvertimeInfo(String companyID){
		List<AppWithDetailExportDto> result = new ArrayList<>();
		// ドメインモデル「申請表示名」を取得する
//		List<AppDispName> appDispNameLst = appDispNameRepository.getAll();
//		for(AppDispName appDispName : appDispNameLst){
//			if(appDispName.getDispName() == null){
//				continue;
//			}
//			if(appDispName.getAppType()==ApplicationType.OVER_TIME_APPLICATION){
//				// outputパラメータに残業申請のモード別の値をセットする
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.OVER_TIME_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + OverTimeAtr.PREOVERTIME.name + ")",
//						OverTimeAtr.PREOVERTIME.value + 1,
//						null));
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.OVER_TIME_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + OverTimeAtr.REGULAROVERTIME.name + ")",
//						OverTimeAtr.REGULAROVERTIME.value + 1,
//						null));
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.OVER_TIME_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + OverTimeAtr.ALL.name + ")",
//						OverTimeAtr.ALL.value + 1,
//						null));
//			} else if(appDispName.getAppType()==ApplicationType.STAMP_APPLICATION){
//				// outputパラメータに打刻申請のモード別の値をセットする
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.STAMP_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + StampRequestMode_Old.STAMP_GO_OUT_PERMIT.name + ")",
//						null,
//						StampRequestMode_Old.STAMP_GO_OUT_PERMIT.value));
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.STAMP_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + StampRequestMode_Old.STAMP_WORK.name + ")",
//						null,
//						StampRequestMode_Old.STAMP_WORK.value));
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.STAMP_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + StampRequestMode_Old.STAMP_CANCEL.name + ")",
//						null,
//						StampRequestMode_Old.STAMP_CANCEL.value));
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.STAMP_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + StampRequestMode_Old.STAMP_ONLINE_RECORD.name + ")",
//						null,
//						StampRequestMode_Old.STAMP_ONLINE_RECORD.value));
//				result.add(new AppWithDetailExportDto(
//						ApplicationType.STAMP_APPLICATION.value,
//						appDispName.getDispName().v() + "申請" + " (" + StampRequestMode_Old.OTHER.name + ")",
//						null,
//						StampRequestMode_Old.OTHER.value));
//			} else {
//				// outputパラメータに値をセットする
//				result.add(new AppWithDetailExportDto(appDispName.getAppType().value, appDispName.getDispName().v() + "申請", null, null));
//			}
//		}
		return result;
	}
	
	/** No.2846
	 * 対象日リストを取得する
	 * @param application
	 * @return
	 */
	public List<GeneralDate> getDateTargets(Application application) {
		List<GeneralDate> output = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		
		// 申請開始日、申請終了日をチェックする
		if (application.getOpAppStartDate().isPresent() && application.getOpAppEndDate().isPresent()) {
			
			GeneralDate startDate = application.getOpAppStartDate().get().getApplicationDate();
			GeneralDate endDate = application.getOpAppEndDate().get().getApplicationDate();
			
			
			if (startDate.equals(endDate)) { // 申請開始日＝申請終了日の場合
				output.add(application.getAppDate().getApplicationDate());
				
				return output;
			} else { // 申請開始日＜＞申請終了日の場合
				Boolean condition = application.getAppType() != ApplicationType.WORK_CHANGE_APPLICATION;
				
				if (condition) { // (申請種類≠勤務変更申請) OR (申請種類＝勤務変更申請　＆　休日を除外する)の場合
					// 申請開始日から申請終了日までのリストをoutputに追加する
					for(GeneralDate loopDate = startDate;
							loopDate.beforeOrEquals(endDate);
							loopDate = loopDate.addDays(1)) {
						// Imported「勤務予定基本情報」を取得する
						ScBasicScheduleImport scBasicScheduleImport = scBasicScheduleAdapter.findByIDRefactor(application.getEmployeeID(), loopDate);
						// 1日休日の判定
						Boolean isJudgmentHolidayDay = specHdFrameForWkTypeSetService.jubgeHdOneDay(companyId, scBasicScheduleImport.getWorkTypeCode());
						if (!isJudgmentHolidayDay) { // それ以外
							output.add(loopDate);
						}
					}
					
				} else { // 申請種類＝勤務変更申請　＆休日を除外しない
					for(GeneralDate loopDate = startDate;
							loopDate.beforeOrEquals(endDate);
							loopDate = loopDate.addDays(1)) {
						output.add(loopDate);
					}
				}
				
			}
			
						
		}
			
		return output;
		
	}
}
