package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnualLeaveEmpBasicInfo;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.sha.ShaFlexMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.sha.ShaDeforLaborMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.sha.ShaRegulaMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.GetPeriodExcluseEntryRetireTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.WorkingTimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeSha;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 月別集計で必要な社員別設定
 * @author shuichi_ishida
 */
@Getter
@Setter
@NoArgsConstructor
public class MonAggrEmployeeSettings {

	/** 社員ID */
	private String employeeId;
	/** 社員 */
	private EmployeeImport employee;
	/** 所属雇用履歴 */
	private List<BsEmploymentHistoryImport> employments;
	/** 所属職場履歴 */
	private List<SharedAffWorkPlaceHisImport> workplaces;
	/** 上位職場履歴 */
	private Map<String, List<String>> workPlacesToRoot;
	/** 社員別通常勤務労働時間 */
	private Optional<WorkingTimeSetting> regLaborTime;
	/** 社員別変形労働労働時間 */
	private Optional<WorkingTimeSetting> irgLaborTime;
	/** 通常勤務社員別月別実績集計設定 */
	private Optional<ShaRegulaMonthActCalSet> shaRegSetOpt;
	/** 変形労働社員別月別実績集計設定 */
	private Optional<ShaDeforLaborMonthActCalSet> shaIrgSetOpt;
	/** フレックス社員別月別実績集計設定 */
	private Optional<ShaFlexMonthActCalSet> shaFlexSetOpt;
	/** 年休社員基本情報 */
	private Optional<AnnualLeaveEmpBasicInfo> annualLeaveEmpBasicInfoOpt;
	/** 締め状態管理 */
	private List<ClosureStatusManagement> closureStatusMngs;
	/** 労働条件項目 */
	private List<WorkingConditionItem> workingConditionItems;
	/** 労働条件: 履歴ID-期間 */
	private Map<String, DatePeriod> workingConditions;
	
	/** 集計開始日を締め開始日とする */
	@Setter
	private boolean noCheckStartDate;
	/** エラー情報 */
	private Map<String, ErrMessageContent> errorInfos;
	
	private MonAggrEmployeeSettings(String employeeId){
		this.employeeId = employeeId;
		this.employee = null;
		this.employments = new ArrayList<>() ;
		this.workplaces = new ArrayList<>();
		this.workPlacesToRoot = new HashMap<>();
		this.regLaborTime = Optional.empty();
		this.irgLaborTime = Optional.empty();
		this.shaRegSetOpt = Optional.empty();
		this.shaIrgSetOpt = Optional.empty();
		this.shaFlexSetOpt = Optional.empty();
		this.annualLeaveEmpBasicInfoOpt = Optional.empty();
		this.closureStatusMngs = new ArrayList<>();
		this.workingConditionItems = new ArrayList<>();
		this.workingConditions = new HashMap<>();
		
		this.noCheckStartDate = true;
		this.errorInfos = new HashMap<>();
	}
	
	/**
	 * 設定読み込み
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @return 月別集計で必要な社員別設定
	 */
	public static MonAggrEmployeeSettings loadSettings(RequireM2 require, CacheCarrier cacheCarrier, 
			String companyId, String employeeId, DatePeriod period){
	
		MonAggrEmployeeSettings domain = new MonAggrEmployeeSettings(employeeId);
		List<String> employeeIds = new ArrayList<>(Arrays.asList(employeeId));
		
		// 社員
		domain.employee = require.employee(cacheCarrier, employeeId);
		if (domain.employee == null){
			domain.errorInfos.put("002", new ErrMessageContent(TextResource.localize("Msg_1156")));
			return domain;
		}
		
		// 取得用期間の確認　（過去1ヶ月、未来1ヶ月を配慮）　→　過去一ヶ月：前月の最終週、未来一ヶ月：フレックスの翌月繰越 
		GeneralDate findStart = GeneralDate.min();
		if (period.start().after(GeneralDate.min().addMonths(1))) findStart = period.start().addMonths(-1);
		GeneralDate findEnd = GeneralDate.max();
		if (period.end().before(GeneralDate.max().addMonths(-1))) findEnd = period.end().addMonths(1);
		DatePeriod findPeriod = GetPeriodExcluseEntryRetireTime.getPeriodExcluseEntryRetireTime(new GetPeriodExcluseEntryRetireTime.Require() {

			@Override
			public EmployeeImport employeeInfo(CacheCarrier cacheCarrier, String empId) {
				return domain.employee;
			}
		}, cacheCarrier, employeeId, new DatePeriod(findStart, findEnd)).orElse(null);
		
		if (findPeriod == null) {
			domain.errorInfos.put("002", new ErrMessageContent(TextResource.localize("Msg_1156")));
			return domain;
		}
		
		// 所属雇用履歴
		GeneralDate empCriteria = findPeriod.start();
		while (empCriteria.beforeOrEquals(findPeriod.end())){
			val employmentOpt = require.employmentHistory(cacheCarrier, companyId, employeeId, empCriteria);
			if (employmentOpt.isPresent()){
				domain.employments.add(employmentOpt.get());
				empCriteria = employmentOpt.get().getPeriod().end();
//				if (empCriteria.afterOrEquals(GeneralDate.max())) break;
			} else {

				domain.errorInfos.put("002", new ErrMessageContent(TextResource.localize("Msg_1156")));
				return domain;
			}
//			else break;
			empCriteria = empCriteria.addDays(1);
		}
		
		// 所属職場履歴
		GeneralDate wkpCriteria = findPeriod.start();
		while (wkpCriteria.beforeOrEquals(findPeriod.end())){
			val workplaceOpt = require.affWorkPlace(employeeId, wkpCriteria);
			if (workplaceOpt.isPresent()){
				domain.workplaces.add(workplaceOpt.get());
				wkpCriteria = workplaceOpt.get().getDateRange().end();
				
				// 終了日時点の上位職場履歴
				val workplacesToRoot = require.getCanUseWorkplaceForEmp(cacheCarrier, companyId, employeeId, wkpCriteria);
				domain.workPlacesToRoot.put(workplaceOpt.get().getWorkplaceId(), workplacesToRoot);
				
				// 次の履歴へ
//				if (wkpCriteria.afterOrEquals(GeneralDate.max())) break;
			} else {

				domain.errorInfos.put("002", new ErrMessageContent(TextResource.localize("Msg_1156")));
				return domain;
			}
//			else break;
			wkpCriteria = wkpCriteria.addDays(1);
		}
		
		// 社員別通常勤務労働時間
		val regWorkTime = require.regularLaborTimeByEmployee(companyId, employeeId);
		if (regWorkTime.isPresent()) domain.regLaborTime = Optional.of(regWorkTime.get());
		
		// 社員別変形労働労働時間
		val irgWorkTime = require.deforLaborTimeByEmployee(companyId, employeeId);
		if (irgWorkTime.isPresent()) domain.irgLaborTime = Optional.of(irgWorkTime.get());
		
		// 通常勤務社員別月別実績集計設定
		domain.shaRegSetOpt = require.monthRegulaCalcSetByEmployee(companyId, employeeId);
		
		// 変形労働社員別月別実績集計設定
		domain.shaIrgSetOpt = require.monthDeforLaborCalcSetByEmployee(companyId, employeeId);
		
		// フレックス社員別月別実績集計設定
		domain.shaFlexSetOpt = require.monthFlexCalcSetbyEmployee(companyId, employeeId);
		
		// 年休社員基本情報
		domain.annualLeaveEmpBasicInfoOpt = require.employeeAnnualLeaveBasicInfo(employeeId);
		
		// 締め処理状態
		domain.closureStatusMngs = require.employeeClosureStatusManagements(employeeIds, period);
		
		// 「労働条件項目」を取得
		empCriteria = findPeriod.start();
		List<WorkingConditionItem> workingConditionItems = new ArrayList<>();
		while (empCriteria.beforeOrEquals(findPeriod.end())){
			val workConditions = require.workingCondition(employeeId, new DatePeriod(empCriteria, empCriteria));
			if (!workConditions.isEmpty()){
				workingConditionItems.add(workConditions.get(0).getWorkingConditionItem());
				empCriteria = workConditions.get(0).getDatePeriod().end();
			} else {

				domain.errorInfos.put("002", new ErrMessageContent(TextResource.localize("Msg_1156")));
				return domain;
			}
			empCriteria = empCriteria.addDays(1);
		}
		// 同じ労働制の履歴を統合　と　「労働条件」の確認
		domain.integrateHistoryOfSameWorkSys(require, workingConditionItems);
		
		return domain;
	}
	
	/**
	 * clones from 設定読み込み
	 */
	public static List<MonAggrEmployeeSettings> loadSettingsClones(RequireM2 require, CacheCarrier cacheCarrier, 
			String companyId, List<String> employeeIdd, DatePeriod period){
	
		List<MonAggrEmployeeSettings> domains = employeeIdd.stream().map(c-> new MonAggrEmployeeSettings(c)).collect(Collectors.toList());
		
		List<MonAggrEmployeeSettings> domainsError = new ArrayList<MonAggrEmployeeSettings>();
		List<String> employeeIdErr = new ArrayList<String>();
		
		List<EmployeeImport> employeeImport = require.employee(employeeIdd);
		
		for (MonAggrEmployeeSettings domain : domains) {
			domain.employee = employeeImport.stream().filter(c->c.getEmployeeId().equals(domain.employeeId)).findFirst().orElse(null);
			if (domain.employee == null){
				// 社員
				domain.errorInfos.put("002", new ErrMessageContent(TextResource.localize("Msg_1156")));
				domainsError.add(domain);
				employeeIdErr.add(domain.employeeId);
			}
		}
		domains = domains.stream().filter(c-> !employeeIdErr.contains(c.employeeId)).collect(Collectors.toList());
		List<String> employeeIds = domains.stream().map(c->c.employeeId).collect(Collectors.toList());
		
		// 取得用期間の確認　（過去1ヶ月、未来1ヶ月を配慮）
		GeneralDate findStart = GeneralDate.min();
		if (period.start().after(GeneralDate.min().addMonths(1))) findStart = period.start().addMonths(-1);
		GeneralDate findEnd = GeneralDate.max();
		if (period.end().before(GeneralDate.max().addMonths(-1))) findEnd = period.end().addMonths(1);
		DatePeriod findPeriod = new DatePeriod(findStart, findEnd);
		
		for (MonAggrEmployeeSettings domain : domains) {
			// 所属雇用履歴
			GeneralDate empCriteria = findPeriod.start();
			while (empCriteria.beforeOrEquals(findPeriod.end())){
				val employmentOpt = require.employmentHistory(cacheCarrier, companyId, domain.employeeId, empCriteria);
				if (employmentOpt.isPresent()){
					domain.employments.add(employmentOpt.get());
					empCriteria = employmentOpt.get().getPeriod().end();
					if (empCriteria.afterOrEquals(GeneralDate.max())) break;
					empCriteria = empCriteria.addDays(1);
				}
				else break;
			}
		}
		val workplacesToRootMap = require.getCanUseWorkplaceForEmp(companyId, domains.stream().map(c->c.employeeId).collect(Collectors.toList()), findPeriod);
		val workplaceOptMap = require.affWorkPlace(companyId, domains.stream().map(c->c.employeeId).collect(Collectors.toList()), findPeriod);
		for (MonAggrEmployeeSettings domain : domains) {	
			// 所属職場履歴
			GeneralDate wkpCriteria = findPeriod.start();
			while (wkpCriteria.beforeOrEquals(findPeriod.end())){
				if(workplaceOptMap.containsKey(wkpCriteria) && workplaceOptMap.get(wkpCriteria).containsKey(domain.employeeId)) {
					val workplaceOpt = workplaceOptMap.get(wkpCriteria).get(domain.employeeId);
					if (workplaceOpt.isPresent()){
						domain.workplaces.add(workplaceOpt.get());
						wkpCriteria = workplaceOpt.get().getDateRange().end();
						
						// 終了日時点の上位職場履歴
						if(workplacesToRootMap.containsKey(wkpCriteria) && workplacesToRootMap.get(wkpCriteria).containsKey(domain.employeeId)) {
							val workplacesToRoot = workplacesToRootMap.get(wkpCriteria).get(domain.employeeId);
							domain.workPlacesToRoot.put(workplaceOpt.get().getWorkplaceId(), workplacesToRoot);
						}
						// 次の履歴へ
						if (wkpCriteria.afterOrEquals(GeneralDate.max())) break;
						wkpCriteria = wkpCriteria.addDays(1);
					}
				}
			}
		}
		// 社員別通常勤務労働時間
		val regWorkTime = require.regularLaborTimeByEmployee(companyId, employeeIds);
		// 社員別変形労働労働時間
		val irgWorkTime = require.deforLaborTimeByEmployee(companyId, employeeIds);
		// 通常勤務社員別月別実績集計設定
		List<ShaRegulaMonthActCalSet> shaRegulaMonthActCalSet = require.monthRegulaCalcSetByEmployee(companyId, employeeIds);
		// 変形労働社員別月別実績集計設定
		List<ShaDeforLaborMonthActCalSet> monthRegulaCalcSetByEmployee = require.monthDeforLaborCalcSetByEmployee(companyId, employeeIds);
		// フレックス社員別月別実績集計設定
		List<ShaFlexMonthActCalSet> monthFlexCalcSetbyEmployee = require.monthFlexCalcSetbyEmployee(companyId, employeeIds);
		// 年休社員基本情報
		List<AnnualLeaveEmpBasicInfo> employeeAnnualLeaveBasicInfo = require.employeeAnnualLeaveBasicInfo(companyId, employeeIds);
		// 締め処理状態
		List<ClosureStatusManagement> employeeClosureStatusManagements = require.employeeClosureStatusManagements(employeeIds, period);
		// 「労働条件項目」を取得
		List<WorkingConditionItem> workingConditionItems = require.workingConditionItem(employeeIds, period);
		
		for (MonAggrEmployeeSettings domain : domains) {
			regWorkTime.stream().filter(c-> c.getEmpId().equals(domain.employeeId)).findAny().ifPresent( d-> domain.regLaborTime = Optional.of(d));
			irgWorkTime.stream().filter(c-> c.getEmpId().equals(domain.employeeId)).findAny().ifPresent( d-> domain.irgLaborTime = Optional.of(d));
			domain.shaRegSetOpt = shaRegulaMonthActCalSet.stream().filter(c-> c.getEmployeeId().equals(domain.employeeId)).findAny();
			domain.shaIrgSetOpt = monthRegulaCalcSetByEmployee.stream().filter(c-> c.getEmployeeId().equals(domain.employeeId)).findAny();
			domain.shaFlexSetOpt = monthFlexCalcSetbyEmployee.stream().filter(c-> c.getEmpId().equals(domain.employeeId)).findAny();
			domain.annualLeaveEmpBasicInfoOpt = employeeAnnualLeaveBasicInfo.stream().filter(c-> c.getEmployeeId().equals(domain.employeeId)).findAny();
			domain.closureStatusMngs = employeeClosureStatusManagements.stream().filter(c-> c.getEmployeeId().equals(domain.employeeId)).collect(Collectors.toList());
			List<WorkingConditionItem> workingConditionItem = workingConditionItems.stream().filter(c->c.getEmployeeId().equals(domain.employeeId)).collect(Collectors.toList());
			if (!workingConditionItems.isEmpty()){
				// 同じ労働制の履歴を統合　と　「労働条件」の確認
				domain.integrateHistoryOfSameWorkSys(require, workingConditionItem);
			}
		}
		domains.addAll(domainsError);
		
		return domains;
	}
	
	/**
	 * 同じ労働制の履歴を統合
	 * @param target 労働条件項目リスト　（統合前）
	 * @param repositories 月別集計が必要とするリポジトリ
	 */
	private void integrateHistoryOfSameWorkSys(RequireM1 require, List<WorkingConditionItem> target){

		this.workingConditionItems = new ArrayList<>();
		this.workingConditions = new HashMap<>();
		
		val itrTarget = target.listIterator();
		while (itrTarget.hasNext()){
			
			// 要素[n]を取得
			WorkingConditionItem startItem = itrTarget.next();
			val startHistoryId = startItem.getHistoryId();
			val startConditionOpt = require.workingCondition(startHistoryId);
			if (!startConditionOpt.isPresent()) continue;
			val startCondition = startConditionOpt.get();
			if (startCondition.getDateHistoryItem().isEmpty()) continue;
			DatePeriod startPeriod = startCondition.getDateHistoryItem().get(0).span();
			
			// 要素[n]と要素[n+1]以降を順次比較
			WorkingConditionItem endItem = null;
			while (itrTarget.hasNext()){
				WorkingConditionItem nextItem = target.get(itrTarget.nextIndex());
				if (startItem.getLaborSystem() != nextItem.getLaborSystem() ||
					startItem.getHourlyPaymentAtr() != nextItem.getHourlyPaymentAtr()){
					
					// 労働制または時給者区分が異なる履歴が見つかった時点で、労働条件の統合をやめる
					break;
				}
			
				// 労働制と時給者区分が同じ履歴の要素を順次取得
				endItem = itrTarget.next();
			}
			
			// 次の要素がなくなった、または、異なる履歴が見つかれば、集計要素を確定する
			if (endItem == null){
				this.workingConditionItems.add(startItem);
				this.workingConditions.putIfAbsent(startHistoryId, startPeriod);
				continue;
			}
			val endHistoryId = endItem.getHistoryId();
			val endConditionOpt = require.workingCondition(endHistoryId);
			if (!endConditionOpt.isPresent()) continue;;
			val endCondition = endConditionOpt.get();
			if (endCondition.getDateHistoryItem().isEmpty()) continue;
			this.workingConditionItems.add(endItem);
			this.workingConditions.putIfAbsent(endHistoryId,
					new DatePeriod(startPeriod.start(), endCondition.getDateHistoryItem().get(0).end()));
		}
	}
	
	/**
	 * 所属雇用履歴の取得
	 * @param ymd 年月日
	 * @return 所属雇用履歴
	 */
	public Optional<BsEmploymentHistoryImport> getEmployment(GeneralDate ymd){
		return this.employments.stream().filter(c -> c.getPeriod().contains(ymd))
				.findFirst().map(c -> new BsEmploymentHistoryImport(getEmployeeId(), c.getEmploymentCode(), "", c.getPeriod()));
	}
	
	/**
	 * 所属職場履歴の取得
	 * @param ymd 年月日
	 * @return 所属職場履歴
	 */
	public Optional<SharedAffWorkPlaceHisImport> getWorkplace(GeneralDate ymd){
		return this.workplaces.stream().filter(c -> c.getDateRange().contains(ymd)).findFirst();
	}
	
	/**
	 * 所属職場履歴の取得
	 * @param ymd 年月日
	 * @return 所属職場履歴
	 */
	public List<String> getWorkplacesToRoot(GeneralDate ymd){
		val workplaceOpt = this.workplaces.stream().filter(c -> c.getDateRange().contains(ymd)).findFirst();
		if (!workplaceOpt.isPresent()) return new ArrayList<>();
		val workplaceId = workplaceOpt.get().getWorkplaceId();
		if (!this.workPlacesToRoot.containsKey(workplaceId)) return new ArrayList<>();
		return this.workPlacesToRoot.get(workplaceId);
	}
	
	/**
	 * 処理する期間が締められているかチェックする
	 * @param criteria 基準日
	 * @return true：締められている、false：締められていない
	 */
	public boolean checkClosedMonth(GeneralDate criteria){
		for (val closureStatusMng : this.closureStatusMngs){
			if (!closureStatusMng.getPeriod().contains(criteria)) continue;
			return true;
		}
		return false;
	}
	
	/**
	 * 労働条件項目の取得
	 * @param ymd 年月日
	 * @return 労働条件項目
	 */
	public Optional<WorkingConditionItem> getWorkingConditionItem(GeneralDate ymd){
		for (val workingConditionItem : this.workingConditionItems) {
			val historyId = workingConditionItem.getHistoryId();
			if (!this.workingConditions.containsKey(historyId)) continue;
			if (this.workingConditions.get(historyId).contains(ymd)) return Optional.of(workingConditionItem);
		}
		return Optional.empty();
	}
	
	public static interface RequireM2 extends RequireM1 {
		
		List<ClosureStatusManagement> employeeClosureStatusManagements(List<String> employeeIds, DatePeriod span);
		
		Optional<AnnualLeaveEmpBasicInfo> employeeAnnualLeaveBasicInfo(String employeeId);
		
		Optional<ShaFlexMonthActCalSet> monthFlexCalcSetbyEmployee(String cid, String sId);
		
		Optional<ShaDeforLaborMonthActCalSet> monthDeforLaborCalcSetByEmployee(String cId, String sId);
		
		Optional<ShaRegulaMonthActCalSet> monthRegulaCalcSetByEmployee(String cid, String sId);
		
		Optional<DeforLaborTimeSha> deforLaborTimeByEmployee(String cid, String empId);
		
		Optional<RegularLaborTimeSha> regularLaborTimeByEmployee(String Cid, String EmpId);
		
		List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId,
				String employeeId, GeneralDate baseDate);
		
		Optional<SharedAffWorkPlaceHisImport> affWorkPlace(String employeeId, GeneralDate baseDate);
		
		Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId,
				String employeeId, GeneralDate baseDate);
		
		EmployeeImport employee(CacheCarrier cacheCarrier, String empId);
		
		List<WorkingConditionItemWithPeriod> workingCondition(String employeeId, DatePeriod datePeriod);
		
		//clones
		List<AnnualLeaveEmpBasicInfo> employeeAnnualLeaveBasicInfo(String cId, List<String> employeeId);
		
		List<ShaFlexMonthActCalSet> monthFlexCalcSetbyEmployee(String cid, List<String> sId);
		
		List<ShaDeforLaborMonthActCalSet> monthDeforLaborCalcSetByEmployee(String cId, List<String> sId);
		
		List<ShaRegulaMonthActCalSet> monthRegulaCalcSetByEmployee(String cid, List<String> sId);
		
		List<DeforLaborTimeSha> deforLaborTimeByEmployee(String cid, List<String> empId);
		
		List<RegularLaborTimeSha> regularLaborTimeByEmployee(String Cid, List<String> EmpId);
		
		List<EmployeeImport> employee(List<String> empId);
		
		List<WorkingConditionItem> workingConditionItem(List<String> sId, DatePeriod datePeriod);
		
		Map<GeneralDate, Map<String, List<String>>> getCanUseWorkplaceForEmp(String companyId, List<String> employeeId, DatePeriod baseDate);
		
		Map<GeneralDate, Map<String, Optional<SharedAffWorkPlaceHisImport>>> affWorkPlace(String companyId, List<String> employeeId, DatePeriod baseDate);

	}
	
	public static interface RequireM1 {
		
		Optional<WorkingCondition> workingCondition(String historyId);
	}
}
