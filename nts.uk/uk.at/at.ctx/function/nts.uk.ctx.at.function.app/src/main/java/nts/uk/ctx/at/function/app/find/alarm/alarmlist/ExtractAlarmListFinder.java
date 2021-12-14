package nts.uk.ctx.at.function.app.find.alarm.alarmlist;

import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.function.dom.adapter.person.EmployeeInfoFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.person.EmployeeInfoFunAdapterDto;
import nts.uk.ctx.at.function.dom.adapter.standardmenu.StandardMenuAdaptor;
import nts.uk.ctx.at.function.dom.adapter.standardmenu.StandardMenuNameImport;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkPlaceInforExport;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.ExtractAlarmListService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.ExtractedAlarmDto;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.webmenu.AlarmListWebMenu;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.webmenu.AlarmListWebMenuRepository;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.webmenu.WebMenuInfo;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmCheckConditionCode;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmEmployeeList;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.PersisAlarmListExtractResultRepository;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.PersistenceAlarmListExtractResult;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
public class ExtractAlarmListFinder {

	@Inject
	private ExtractAlarmListService extractAlarmListService;

	@Inject
	private PersisAlarmListExtractResultRepository persisAlarmListExtractResultRepo;

	@Inject
	private EmployeeInfoFunAdapter empInfoAdapter;

	@Inject
	private WorkplaceAdapter workplaceAdapter;

	@Inject
	private AlarmListWebMenuRepository alarmListWebMenuRepo;

	@Inject
	private StandardMenuAdaptor standardMenuAdaptor;

	public ExtractedAlarmDto extractAlarm(ExtractAlarmQuery query) {
		return this.extractAlarmListService.extractAlarm(query.getListEmployee(), query.getAlarmCode(), query.getListPeriodByCategory(), "");
	}

	/**
	 * アラームリストの起動
	 */
	public List<ValueExtractAlarmDto> getWebMenu(List<String> employeeIds) {
		List<ValueExtractAlarmDto> result = new ArrayList<>();
	    String companyId = AppContexts.user().companyId();

		// 社員IDリストからアラーム抽出結果を取得
	    List<PersistenceAlarmListExtractResult> listExtractResults = persisAlarmListExtractResultRepo.getAlarmExtractResult(companyId, employeeIds);
		List<AlarmEmployeeList> alarmListExtractResults = new ArrayList<>();
		for (PersistenceAlarmListExtractResult extractResult : listExtractResults) {
			alarmListExtractResults.addAll(extractResult.getAlarmListExtractResults());
		}

		// 集計結果を元に社員を取得する
//		Set<String> employeeIdSet = alarmListExtractResults.stream().map(AlarmEmployeeList::getEmployeeID).collect(Collectors.toSet());

		// 集計結果を元に職場を取得する
		Set<String> workplaceIdSet = new HashSet<>();
		alarmListExtractResults.forEach(i -> {
			i.getAlarmExtractInfoResults().forEach(j -> {
				j.getExtractionResultDetails().forEach(k -> {
					k.getWpID().ifPresent(wkpId -> {
						workplaceIdSet.add(wkpId);
					});
				});
			});
		});

		// 社員ID(List)から個人社員基本情報を取得
		Map<String, EmployeeInfoFunAdapterDto> employeeInfoMap = empInfoAdapter.getListPersonInfor(employeeIds)
				.stream().collect(Collectors.toMap(EmployeeInfoFunAdapterDto::getEmployeeId, Function.identity()));

		// [No.560]職場IDから職場の情報をすべて取得する
		Map<String, WorkPlaceInforExport> workplaceMap = workplaceAdapter.getWorkplaceInforByWkpIds(companyId, new ArrayList<>(workplaceIdSet), GeneralDate.today())
				.stream().collect(Collectors.toMap(WorkPlaceInforExport::getWorkplaceId, Function.identity()));

		// 取得したデータから「アラームリスト抽出従業員情報」を作成して返す
	    List<AlarmListWebMenu> listWebMenus = alarmListWebMenuRepo.getAll(companyId);
		List<StandardMenuNameImport> listStandardMenus = standardMenuAdaptor.getMenus(companyId, 4); // システム　＝　勤次郎

        alarmListExtractResults.sort((e1, e2) -> {
			EmployeeInfoFunAdapterDto emp1 = employeeInfoMap.get(e1.getEmployeeID());
			EmployeeInfoFunAdapterDto emp2 = employeeInfoMap.get(e2.getEmployeeID());
			if (emp1 != null && emp2 != null) return emp1.getEmployeeCode().compareTo(emp2.getEmployeeCode());
			return e1.getEmployeeID().compareTo(e2.getEmployeeID());
		});
		alarmListExtractResults.forEach(r2 -> {
			r2.getAlarmExtractInfoResults().forEach(r3 -> {
				// 取得したList＜アラームリストのWebメニュー＞を絞り込む
				List<AlarmListWebMenu> tmpWebMenus = listWebMenus.stream()
						.filter(w -> r3.getAlarmCategory() == w.getAlarmCategory() && r3.getAlarmListCheckType() == w.getCheckType())
						.collect(Collectors.toList());

				// アラームからWebメニュー情報を探す
				List<WebMenuInfo> webMenuInfos = getWebMenuInfo(tmpWebMenus, r3.getAlarmCheckConditionNo(), r3.getAlarmCheckConditionCode());

				List<StandardMenuNameImport> filteredStandardMenus = listStandardMenus.stream().filter(i -> {
					return webMenuInfos.stream()
							.anyMatch(j -> j.getSystem().value == i.getSystem()
									&& j.getMenuClassification().value == i.getMenuClassification()
									&& j.getWebMenuCode().v().equals(i.getMenuCode()));
				}).collect(Collectors.toList());
				
				List<WebMenuInfoDto> menuList = filteredStandardMenus.stream()
						.map(i -> new WebMenuInfoDto(i.getProgramId(), i.getDisplayName(), i.getUrl(), i.getQueryString()))
						.collect(Collectors.toList());

				r3.getExtractionResultDetails().forEach(r4 -> {
					ValueExtractAlarmDto dto = new ValueExtractAlarmDto(
							IdentifierUtil.randomUniqueId(),
							r4.getWpID().orElse(null),
							r4.getWpID().map(wkpid -> workplaceMap.get(wkpid) == null ? null : workplaceMap.get(wkpid).getWorkplaceCode()).orElse(null),
							r4.getWpID().map(wkpid -> workplaceMap.get(wkpid) == null ? null : workplaceMap.get(wkpid).getWorkplaceName()).orElse(null),
							r2.getEmployeeID(),
							employeeInfoMap.get(r2.getEmployeeID()) == null ? null : employeeInfoMap.get(r2.getEmployeeID()).getEmployeeCode(),
							employeeInfoMap.get(r2.getEmployeeID()) == null ? null : employeeInfoMap.get(r2.getEmployeeID()).getBusinessName(),
							getAlarmValueDate(r4.getPeriodDate().getStartDate(), r4.getPeriodDate().getEndDate(), r3.getAlarmCategory()),
							r3.getAlarmCategory().value,
							r3.getAlarmCategory().nameId,
							r4.getAlarmName(),
							r4.getAlarmContent(),
							r4.getMessage().orElse(null),
							r4.getCheckValue().orElse(null),
							menuList
					);
					result.add(dto);
				});
			});
		});

		return result;
	}

	private List<WebMenuInfo> getWebMenuInfo(List<AlarmListWebMenu> tmpWebMenus, String alarmCode, AlarmCheckConditionCode alarmCheckConditionCode) {
		List<AlarmListWebMenu> filteredWebMenus = tmpWebMenus.stream()
				.filter(w -> w.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode.v())
						&& w.getAlarmCode().equals(alarmCode))
				.collect(Collectors.toList());
		if (filteredWebMenus.isEmpty()) {
			filteredWebMenus = tmpWebMenus.stream()
					.filter(w -> ((w.getAlarmCheckConditionCode() == null || w.getAlarmCheckConditionCode().v() == null)
							&& w.getAlarmCode().equals(alarmCode))
							|| (w.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode.v())
							&& w.getAlarmCode() == null))
					.collect(Collectors.toList());
			if (filteredWebMenus.isEmpty()) {
				filteredWebMenus = tmpWebMenus.stream()
						.filter(w -> (w.getAlarmCheckConditionCode() == null || w.getAlarmCheckConditionCode().v() == null)
								&& w.getAlarmCode() == null)
						.collect(Collectors.toList());
			}
		}
		return filteredWebMenus.isEmpty()
				? new ArrayList<>()
				: filteredWebMenus.stream()
				.map(i -> i.getWebMenuInfoList())
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private String getAlarmValueDate(Optional<GeneralDate> start, Optional<GeneralDate> end, AlarmCategory alarmCategory) {
		if (start.isPresent()) {
				switch (alarmCategory) {
					case MONTHLY:
					case MULTIPLE_MONTH:
					case SCHEDULE_MONTHLY:
						return end.isPresent()
								? start.get().toString("yyyy/MM") + "～" + end.get().toString("yyyy/MM")
								: start.get().toString("yyyy/MM");
					case SCHEDULE_YEAR:
					case ATTENDANCE_RATE_FOR_HOLIDAY:
						return end.isPresent()
								? start.get().toString("yyyy") + "～" + end.get().toString("yyyy")
								: start.get().toString("yyyy");
					default:
						return end.isPresent()
								? start.get().toString() + "～" + end.get().toString()
								: start.get().toString();
				}
		}
        return null;
	}
}
