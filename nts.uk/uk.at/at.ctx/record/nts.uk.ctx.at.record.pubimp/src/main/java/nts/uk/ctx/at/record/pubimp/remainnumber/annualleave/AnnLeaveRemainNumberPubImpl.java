package nts.uk.ctx.at.record.pubimp.remainnumber.annualleave;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.ClosurePeriod;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.GetClosurePeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnLeaRemNumWithinPeriodProc;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggrResultOfAnnualLeave;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AnnualLeaveInfo;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.CheckShortageFlex;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AggrResultOfAnnualLeaveEachMonth;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AnnLeaveOfThisMonth;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AnnLeaveRemainNumberPub;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.ClosurePeriodEachYear;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.NextHolidayGrantDate;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.AnnualLeaveGrantExport;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.AnnualLeaveManageInforExport;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.AnnualLeaveRemainingNumberExport;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.ReNumAnnLeaReferenceDateExport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnLeaEmpBasicInfoDomService;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.CalcNextAnnualLeaveGrantDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.valueobject.AnnLeaRemNumValueObject;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TmpAnnualHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeave;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveMaxRemainingTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.HalfDayAnnLeaRemainingNum;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.HalfDayAnnualLeave;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.export.NextAnnualLeaveGrant;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AnnLeaveRemainNumberPubImpl implements AnnLeaveRemainNumberPub {

	@Inject
	private AnnLeaEmpBasicInfoDomService annLeaService;

	@Inject
	private CheckShortageFlex checkShortageFlex;

//	@Inject
//	private AnnLeaEmpBasicInfoRepository annLeaBasicInfoRepo;


	@Inject
	private TmpAnnualHolidayMngRepository tmpAnnualLeaveMngRepo;

	/** 社員 */
	@Inject
	private EmpEmployeeAdapter empEmployee;
	@Inject
	private RecordDomRequireService requireService;

//	@Inject
//	private AnnLeaGrantRemDataRepository repoAnnLeaGrantRe;

	@Override
	public AnnLeaveOfThisMonth getAnnLeaveOfThisMonth(String employeeId) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		AnnLeaveOfThisMonth result = new AnnLeaveOfThisMonth();
		try {

			String companyId = AppContexts.user().companyId();
			// 月初の年休残数を取得
			AnnLeaRemNumValueObject remainNumber = annLeaService.getAnnLeaveNumber(companyId, employeeId);
			result.setFirstMonthRemNumDays(remainNumber.getDays());
			result.setFirstMonthRemNumMinutes(remainNumber.getMinutes());
			// 計算した年休残数を出力用クラスにコピー
			Optional<GeneralDate> startDate = GetClosureStartForEmployee.algorithm(require, cacheCarrier, employeeId);
			if (!startDate.isPresent())
				return null;
			// 社員に対応する締め期間を取得する
			DatePeriod datePeriod = checkShortageFlex.findClosurePeriod(employeeId, startDate.get());

			// If closuedate is null
			if (datePeriod == null){
				return null;
			}

			// 期間中の年休残数を取得
			Optional<AggrResultOfAnnualLeave> aggrResult = GetAnnLeaRemNumWithinPeriodProc.algorithm(
					require, cacheCarrier, companyId, employeeId,
					datePeriod, InterimRemainMngMode.OTHER, datePeriod.end(), false, Optional.empty(),
					Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

			if (!aggrResult.isPresent())
				return null;
			result.setUsedDays(aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
					.getUsedNumberInfo().getUsedNumber().getUsedDays().orElse(new AnnualLeaveUsedDayNumber(0d)));

			result.setUsedMinutes(
					aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
							.getUsedNumber().getUsedTime().map(c -> c.valueAsMinutes()));

			result.setRemainDays(aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
					.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingDays());

			result.setRemainMinutes(
					aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingTime()
							.isPresent()
									? Optional.of(aggrResult.get().getAsOfPeriodEnd().getRemainingNumber()
											.getAnnualLeaveWithMinus().getRemainingNumberInfo().getRemainingNumber().getTotalRemainingTime()
											.get().v())
									: Optional.empty());

			// ドメインモデル「年休社員基本情報」を取得
//			Optional<AnnualLeaveEmpBasicInfo> basicInfo = annLeaBasicInfoRepo.get(employeeId);
			// 次回年休付与を計算
			List<NextAnnualLeaveGrant> annualLeaveGrant = CalcNextAnnualLeaveGrantDate.algorithm(
					require, cacheCarrier, companyId, employeeId, Optional.empty());

			if (annualLeaveGrant!= null && annualLeaveGrant.size() > 0){
				result.setGrantDate(annualLeaveGrant.get(0).getGrantDate());
				result.setGrantDays(annualLeaveGrant.get(0).getGrantDays().get().v());
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<AggrResultOfAnnualLeaveEachMonth> getAnnLeaveRemainAfterThisMonth(String employeeId,
			DatePeriod datePeriod) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		try {

			String companyId = AppContexts.user().companyId();
			GeneralDate baseDate = GeneralDate.today();
			// 社員に対応する処理締めを取得する
			Optional<Closure> closure = checkShortageFlex.findClosureByEmployee(employeeId, baseDate);
			if (!closure.isPresent())
				return null;
			// 指定した年月の期間をすべて取得する
			List<DatePeriod> periodByYearMonth = closure.get().getPeriodByYearMonth(datePeriod.end().yearMonth());
			if (periodByYearMonth == null || periodByYearMonth.size() == 0)
				return null;
			// 集計期間を計算する
			List<ClosurePeriod> listClosurePeriod = GetClosurePeriod.get(require, companyId, employeeId,
					periodByYearMonth.get(periodByYearMonth.size() - 1).end(), Optional.empty(), Optional.empty(),
					Optional.empty());
			// 締め処理期間のうち、同じ年月の期間をまとめる
			Map<YearMonth, List<ClosurePeriod>> listMap = listClosurePeriod.stream()
					.filter(item -> item.getYearMonth().compareTo(datePeriod.start().yearMonth()) >= 0
							&& item.getYearMonth().compareTo(datePeriod.end().yearMonth()) <= 0)
					.collect(Collectors.groupingBy(ClosurePeriod::getYearMonth));

			List<ClosurePeriodEachYear> listClosurePeriodEachYear = new ArrayList<ClosurePeriodEachYear>();

			for (Map.Entry<YearMonth, List<ClosurePeriod>> item : listMap.entrySet()) {
				GeneralDate start = null, end = null;
				for (ClosurePeriod closurePeriodItem : item.getValue()) {
					for (AggrPeriodEachActualClosure actualClosureItem : closurePeriodItem.getAggrPeriods()) {
						if (start == null || start.compareTo(actualClosureItem.getPeriod().start()) > 0) {
							start = actualClosureItem.getPeriod().start();
						}
						if (end == null || end.compareTo(actualClosureItem.getPeriod().end()) < 0) {
							end = actualClosureItem.getPeriod().end();
						}
					}
				}

				listClosurePeriodEachYear.add(new ClosurePeriodEachYear(item.getKey(), new DatePeriod(start, end)));
			}
            List<AggrResultOfAnnualLeaveEachMonth> result = new ArrayList<AggrResultOfAnnualLeaveEachMonth>();
            Optional<AggrResultOfAnnualLeave> aggrResultOfAnnualLeave = Optional.empty();
            for (ClosurePeriodEachYear item : listClosurePeriodEachYear) {
				// 期間中の年休残数を取得
                aggrResultOfAnnualLeave = GetAnnLeaRemNumWithinPeriodProc.algorithm(
                		require, cacheCarrier, companyId, employeeId,
						item.getDatePeriod(), InterimRemainMngMode.OTHER, item.getDatePeriod().end(), false, Optional.of(false),
                        Optional.empty(), aggrResultOfAnnualLeave, Optional.of(false),Optional.empty(), Optional.empty());
				// 結果をListに追加
                if (aggrResultOfAnnualLeave.isPresent()) {
                    result.add(new AggrResultOfAnnualLeaveEachMonth(item.getYearMonth(), aggrResultOfAnnualLeave.get()));
				}
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public NextHolidayGrantDate getNextHolidayGrantDate(String companyId, String employeeId) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		NextHolidayGrantDate result = new NextHolidayGrantDate();
		// ドメインモデル「年休社員基本情報」を取得
//		Optional<AnnualLeaveEmpBasicInfo> basicInfo = annLeaBasicInfoRepo.get(employeeId);
		// 次回年休付与を計算
		List<NextAnnualLeaveGrant> annualLeaveGrant = CalcNextAnnualLeaveGrantDate.algorithm(
				require, cacheCarrier, companyId, employeeId, Optional.empty());
		if (annualLeaveGrant.size() == 0)
			return null;
		result.setGrantDate(annualLeaveGrant.get(0).getGrantDate());

		return result;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ReNumAnnLeaReferenceDateExport getReferDateAnnualLeaveRemainNumber(String employeeID, GeneralDate date) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		ReNumAnnLeaReferenceDateExport result = new ReNumAnnLeaReferenceDateExport();
		List<AnnualLeaveGrantExport> annualLeaveGrantExports = new ArrayList<>();
		List<AnnualLeaveManageInforExport> annualLeaveManageInforExports = new ArrayList<>();

		String companyId = AppContexts.user().companyId();
		// 「社員」を取得する
		EmployeeImport employee = this.empEmployee.findByEmpId(employeeID);
		if (employee == null) return null;
		// 社員に対応する締め開始日を取得する
		Optional<GeneralDate> startDate = GetClosureStartForEmployee.algorithm(require, cacheCarrier, employeeID);
		if (!startDate.isPresent()) return null;
		// 「基準日」と「締め開始日」を比較　→　「補正後基準日」
		GeneralDate adjustDate = date;
		if (date.before(startDate.get())) adjustDate = startDate.get();
		// 集計終了日　←　「補正後基準日」+1年-1日
		GeneralDate aggrEnd = adjustDate.addYears(1).addDays(-1);
		// 「次回年休付与日を計算」を実行
		List<NextAnnualLeaveGrant> nextAnnualLeaveGrants = CalcNextAnnualLeaveGrantDate.algorithm(
				require, cacheCarrier,
				companyId, employeeID, Optional.of(new DatePeriod(adjustDate, aggrEnd)));
		if (nextAnnualLeaveGrants.size() > 0){
			// 次回付与日前日　←　先頭の「次回年休付与」．付与年月日-1日
			GeneralDate prevNextGrant = nextAnnualLeaveGrants.get(0).getGrantDate().addDays(-1);
			if (prevNextGrant.before(aggrEnd)){
				// 集計終了日　←　次回付与日前日
				aggrEnd = prevNextGrant;
			}
		}
		// 期間中の年休残数を取得
		DatePeriod datePeriod = new DatePeriod(startDate.get(), aggrEnd);
		Optional<AggrResultOfAnnualLeave> aggrResult = GetAnnLeaRemNumWithinPeriodProc.algorithm(require,cacheCarrier, companyId, employeeID,
				datePeriod, InterimRemainMngMode.OTHER, adjustDate, false, Optional.of(false), Optional.empty(),
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		if(aggrResult.isPresent()){
			AnnualLeaveInfo asOfPeriodEnd = aggrResult.get().getAsOfPeriodEnd();
			if(asOfPeriodEnd != null){
				// 年休（マイナスあり）
				AnnualLeave  realAnnualLeave = asOfPeriodEnd.getRemainingNumber().getAnnualLeaveWithMinus();
				// 半日年休（マイナスあり）
				Optional<HalfDayAnnualLeave> halfDayAnnualLeaveWithMinus = asOfPeriodEnd.getRemainingNumber().getHalfDayAnnualLeaveWithMinus();
				// 時間年休（マイナスあり）
				Optional<AnnualLeaveMaxRemainingTime> timeAnnualLeaveWithMinus = asOfPeriodEnd.getRemainingNumber().getTimeAnnualLeaveWithMinus();
				// set 半休残数（付与後）回数
				Integer numberOfRemainGrantPost = 0;
				// set 半休残数（付与前）回数
				Integer numberOfRemainGrantPre = 0;
				if(halfDayAnnualLeaveWithMinus.isPresent()){
					HalfDayAnnLeaRemainingNum remainingNum = halfDayAnnualLeaveWithMinus.get().getRemainingNum();
					if(remainingNum.getTimesAfterGrant().isPresent()){
						numberOfRemainGrantPost = remainingNum.getTimesAfterGrant().get().v();
					}
					numberOfRemainGrantPre = remainingNum.getTimesBeforeGrant().v();
				}
				// set 時間年休上限（付与後）
				Integer timeAnnualLeaveWithMinusGrantPost = 0;
				// set 時間年休上限（付与前）
				Integer timeAnnualLeaveWithMinusGrantPre = 0;
				if(timeAnnualLeaveWithMinus.isPresent()){
					Optional<LeaveRemainingTime> timeAfterGrant = timeAnnualLeaveWithMinus.get().getTimeAfterGrant();
					if(timeAfterGrant.isPresent()){
						timeAnnualLeaveWithMinusGrantPost = timeAfterGrant.get().v();
					}
					timeAnnualLeaveWithMinusGrantPre = timeAnnualLeaveWithMinus.get().getTimeBeforeGrant().v();
				}
				// set 年休残数（付与前）時間
				Integer annualLeaveGrantPreTime = 0;
				// set 年休残数（付与前）日数
				Double annualLeaveGrantPreDay = 0.00;
				// set 年休残数（付与後）時間
				Integer annualLeaveGrantPostTime = 0;
				// set 年休残数（付与後）日数
				Double annualLeaveGrantPostDay = 0.00;
				// set 年休残数時間
				Integer annualLeaveGrantTime = 0;
				// set 年休残数日数
				Double annualLeaveGrantDay = 0.00;
				if(realAnnualLeave.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().isPresent()){
					AnnualLeaveRemainingNumber remainingNumberAfterGrant = realAnnualLeave.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().get();

					if(remainingNumberAfterGrant.getTotalRemainingTime().isPresent()){
						annualLeaveGrantPostTime = remainingNumberAfterGrant.getTotalRemainingTime().get().v();
					}
					annualLeaveGrantPostDay = remainingNumberAfterGrant.getTotalRemainingDays().v();
				}
				if(realAnnualLeave.getRemainingNumberInfo().getRemainingNumberBeforeGrant().getTotalRemainingTime().isPresent()){
					annualLeaveGrantPreTime = realAnnualLeave.getRemainingNumberInfo().getRemainingNumberBeforeGrant().getTotalRemainingTime().get().v();
				}
				annualLeaveGrantPreDay = realAnnualLeave.getRemainingNumberInfo().getRemainingNumberBeforeGrant().getTotalRemainingDays().v();
				// 取得結果を出力用クラスに格納
				AnnualLeaveRemainingNumberExport annualLeaveRemainingNumberExport = new AnnualLeaveRemainingNumberExport(
						annualLeaveGrantPreDay,
						annualLeaveGrantPreTime,
						numberOfRemainGrantPre,
						timeAnnualLeaveWithMinusGrantPre,
						annualLeaveGrantPostDay,
						annualLeaveGrantPostTime,
						numberOfRemainGrantPost,
						timeAnnualLeaveWithMinusGrantPost,
						annualLeaveGrantDay,
						annualLeaveGrantTime,
						0.00,
						0.00);
				result.setAnnualLeaveRemainNumberExport(annualLeaveRemainingNumberExport);
				// add 年休付与情報(仮)
				if(!CollectionUtil.isEmpty(asOfPeriodEnd.getGrantRemainingDataList())){
					for(AnnualLeaveGrantRemainingData annualLeave : asOfPeriodEnd.getGrantRemainingDataList()){
						Double grantNumber = 0.00;
						Double daysUsedNo = 0.00;
						Integer usedMinutes = 0;
						Double remainDays = 0.00;
						Integer remainMinutes = 0;
						if(annualLeave.getDetails() != null){
							if (annualLeave.getDetails().getGrantNumber() != null) {
								grantNumber = annualLeave.getDetails().getGrantNumber().getDays() != null
										? annualLeave.getDetails().getGrantNumber().getDays().v() : 0.00;
							}
							if (annualLeave.getDetails().getUsedNumber() != null) {
								if (annualLeave.getDetails().getUsedNumber().getDays() != null) {
									daysUsedNo = annualLeave.getDetails().getUsedNumber().getDays().v();
								}
								if (annualLeave.getDetails().getUsedNumber().getMinutes().isPresent()) {
									usedMinutes = annualLeave.getDetails().getUsedNumber().getMinutes().get().v();
								}
							}
							if (annualLeave.getDetails().getRemainingNumber() != null) {
								if (annualLeave.getDetails().getRemainingNumber().getDays() != null) {
									remainDays = annualLeave.getDetails().getRemainingNumber().getDays().v();
								}
								if(annualLeave.getDetails().getRemainingNumber().getMinutes().isPresent()){
									remainMinutes = annualLeave.getDetails().getRemainingNumber().getMinutes().get().v();
								}
							}

						}
						AnnualLeaveGrantExport annualLeaveGrantExport = new AnnualLeaveGrantExport(annualLeave.getGrantDate(),
								grantNumber,
								daysUsedNo,
								usedMinutes,
								remainDays,
								remainMinutes,
								annualLeave.getDeadline());
						annualLeaveGrantExports.add(annualLeaveGrantExport);
					}
				}
			}
		}
		// 「暫定年休管理データ」を取得
//		val interimRemains = this.interimRemainRepo.getRemainBySidPriod(
//				employeeID, new DatePeriod(startDate.get(), employee.getRetiredDate()), RemainType.ANNUAL);
		val interimRemains = this.tmpAnnualLeaveMngRepo.getBySidPeriod(employeeID, new DatePeriod(startDate.get(), employee.getRetiredDate()));
		interimRemains.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));
		// add 年休管理情報(仮)
		for (val interimRemain : interimRemains){
//			val tmpAnnualLeaveMngOpt = this.tmpAnnualLeaveMngRepo.getById(interimRemain.getRemainManaID());
//			if (!tmpAnnualLeaveMngOpt.isPresent()) continue;
//			val tmpAnnualLeaveMng = tmpAnnualLeaveMngOpt.get();

			Double usedDays = interimRemain.getUsedNumber().getUsedDayNumberOrZero().v();
			// đối ứng bug #109638: thêm hiển thị workType cho KDL020
			String workTypeCD = interimRemain.getWorkTypeCode().v();
			Integer usedMinutes = 0;
			AnnualLeaveManageInforExport annualLeaveManageInforExport = new AnnualLeaveManageInforExport(
					interimRemain.getYmd(),
					usedDays,
					usedMinutes,
					interimRemain.getCreatorAtr().value,
					workTypeCD);
			annualLeaveManageInforExports.add(annualLeaveManageInforExport);
		}
		// 年休出勤率を計算する
		if (aggrResult.isPresent()){
			val asOfPeriodEnd = aggrResult.get().getAsOfPeriodEnd();
			if (asOfPeriodEnd.getGrantInfo().isPresent()){
				val grantInfo = asOfPeriodEnd.getGrantInfo().get();
				result.getAnnualLeaveRemainNumberExport().setAttendanceRate(
						grantInfo.getAttendanceRate().v().doubleValue());
				result.getAnnualLeaveRemainNumberExport().setWorkingDays(
						grantInfo.getGrantWorkingDays().v());
			}
		}
		// 基準日時点年休残数．年休残日数　←　0
		Double annualLeaveGrantDay = 0.00;
		for (AnnualLeaveGrantExport annualLeaveGrantExport : annualLeaveGrantExports){
			// 処理中の「年休付与残数データ．期限日」と「基準日」を比較
			if (annualLeaveGrantExport.getDeadline().afterOrEquals(date)){
				// 基準日時点年休残数‥年休残日数に加算
				annualLeaveGrantDay += annualLeaveGrantExport.getRemainDays();
			}
		}
		if (result.getAnnualLeaveRemainNumberExport() != null){
			result.getAnnualLeaveRemainNumberExport().setAnnualLeaveGrantDay(annualLeaveGrantDay);
		}

		result.setAnnualLeaveGrantExports(annualLeaveGrantExports);
		result.setAnnualLeaveManageInforExports(annualLeaveManageInforExports);
		return result;
	}

	/**
	 * @author hoatt
	 * KDR001
	 * RequestList #No.369 - ver2
	 * @param employeeId
	 * @return
	 */
	@Override
	public NextHolidayGrantDate getNextHdGrantDateVer2(String companyId, String employeeId, Optional<GeneralDate> closureDate) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		NextHolidayGrantDate result = new NextHolidayGrantDate();

		List<NextAnnualLeaveGrant> annLeaGrant = CalcNextAnnualLeaveGrantDate.calNextHdGrantV2(
				require, cacheCarrier, companyId, employeeId, Optional.empty(),
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), closureDate);
		if (annLeaGrant.size() == 0)
			return null;
		result.setGrantDate(annLeaGrant.get(0).getGrantDate());

		return result;
	}
	//RequestList #No.363 - ver2
	@Override
	public List<AggrResultOfAnnualLeaveEachMonth> getAnnLeaRemainAfThisMonVer2(String employeeId,
			DatePeriod datePeriod, MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		try {
			String companyId = AppContexts.user().companyId();
			// 社員に対応する処理締めを取得する
			Optional<Closure> closure = checkShortageFlex.findClosureByEmployee(employeeId, GeneralDate.today());
			if (!closure.isPresent())
				return new ArrayList<>();
			// 指定した年月の期間をすべて取得する
			List<DatePeriod> periodByYearMonth = closure.get().getPeriodByYearMonth(datePeriod.end().yearMonth());
			if (periodByYearMonth == null || periodByYearMonth.size() == 0)
				return new ArrayList<>();
			// 集計期間を計算する
			List<ClosurePeriod> listClosurePeriod = GetClosurePeriod.get(require, companyId, employeeId,
					periodByYearMonth.get(periodByYearMonth.size() - 1).end(), Optional.empty(), Optional.empty(),
					Optional.empty());
			// 締め処理期間のうち、同じ年月の期間をまとめる
			Map<YearMonth, List<ClosurePeriod>> listMap = listClosurePeriod.stream()
					.filter(item -> item.getYearMonth().compareTo(datePeriod.start().yearMonth()) >= 0
							&& item.getYearMonth().compareTo(datePeriod.end().yearMonth()) <= 0)
					.collect(Collectors.groupingBy(ClosurePeriod::getYearMonth));

			List<ClosurePeriodEachYear> listClosurePeriodEachYear = new ArrayList<ClosurePeriodEachYear>();

			for (Map.Entry<YearMonth, List<ClosurePeriod>> item : listMap.entrySet()) {
				GeneralDate start = null, end = null;
				for (ClosurePeriod closurePeriodItem : item.getValue()) {
					for (AggrPeriodEachActualClosure actualClosureItem : closurePeriodItem.getAggrPeriods()) {
						if (start == null || start.compareTo(actualClosureItem.getPeriod().start()) > 0) {
							start = actualClosureItem.getPeriod().start();
						}
						if (end == null || end.compareTo(actualClosureItem.getPeriod().end()) < 0) {
							end = actualClosureItem.getPeriod().end();
						}
					}
				}

				listClosurePeriodEachYear.add(new ClosurePeriodEachYear(item.getKey(), new DatePeriod(start, end)));
			}
            List<AggrResultOfAnnualLeaveEachMonth> result = new ArrayList<AggrResultOfAnnualLeaveEachMonth>();
            Optional<AggrResultOfAnnualLeave> aggrResultOfAnnualLeave = Optional.empty();
            for (ClosurePeriodEachYear item : listClosurePeriodEachYear) {
				// 期間中の年休残数を取得
                aggrResultOfAnnualLeave = GetAnnLeaRemNumWithinPeriodProc.algorithm(require, cacheCarrier, companyId, employeeId,
						item.getDatePeriod(), InterimRemainMngMode.OTHER, item.getDatePeriod().end(), false, Optional.of(false),
                        Optional.empty(), aggrResultOfAnnualLeave, Optional.of(false),Optional.empty(), Optional.empty());
				// 結果をListに追加
                if (aggrResultOfAnnualLeave.isPresent()) {
                    result.add(new AggrResultOfAnnualLeaveEachMonth(item.getYearMonth(), aggrResultOfAnnualLeave.get()));
				}
			}
			return result;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
	//RequestList #No.265 - ver2
	@Override
	public AnnLeaveOfThisMonth getAnnLeaOfThisMonVer2(String employeeId, MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		AnnLeaveOfThisMonth result = new AnnLeaveOfThisMonth();
		try {
			// 計算した年休残数を出力用クラスにコピー
			Optional<GeneralDate> startDate = GetClosureStartForEmployee.algorithm(require, cacheCarrier, employeeId);
			if (!startDate.isPresent()) {
				return null;
			}
			// 社員に対応する締め期間を取得する
			DatePeriod datePeriod = checkShortageFlex.findClosurePeriod(employeeId, startDate.get());
			// If closuedate is null
			if (datePeriod == null){
				return null;
			}

			String companyId = AppContexts.user().companyId();
			// 月初の年休残数を取得
			AnnLeaRemNumValueObject remainNumber = annLeaService.getAnnLeaveNumber(companyId, employeeId);
			result.setFirstMonthRemNumDays(remainNumber.getDays());
			result.setFirstMonthRemNumMinutes(remainNumber.getMinutes());

			// 期間中の年休残数を取得
//			Optional<AggrResultOfAnnualLeave> aggrResult = GetAnnLeaRemNumWithinPeriodProc.algorithm(
//					require, cacheCarrier, companyId, employeeId,
//					datePeriod, InterimRemainMngMode.OTHER, datePeriod.end(),
//					false, false, Optional.empty(), Optional.empty(), Optional.empty(), false,
//					companySets == null ?  Optional.empty() : Optional.of(companySets),
//					employeeSets == null ? Optional.empty() : Optional.of(employeeSets), Optional.empty());

			Optional<AggrResultOfAnnualLeave> aggrResult = GetAnnLeaRemNumWithinPeriodProc.algorithm(
					require, cacheCarrier, companyId, employeeId,
					datePeriod, InterimRemainMngMode.OTHER, datePeriod.end(), false, Optional.of(false), Optional.empty(),
					Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

			if (!aggrResult.isPresent()){
				return null;
			}
			result.setUsedDays(aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
					.getUsedNumberInfo().getUsedNumber().getUsedDays().orElse(new AnnualLeaveUsedDayNumber(0d)));

			result.setUsedMinutes(
					aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getUsedNumberInfo().getUsedNumber()
							.getUsedTime().map(c -> c.valueAsMinutes()));

			result.setRemainDays(aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
					.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingDays());

			result.setRemainMinutes(
					aggrResult.get().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingTime()
							.isPresent()
									? Optional.of(aggrResult.get().getAsOfPeriodEnd().getRemainingNumber()
											.getAnnualLeaveWithMinus().getRemainingNumberInfo().getRemainingNumber().getTotalRemainingTime()
											.get().v())
									: Optional.empty());

			// ドメインモデル「年休社員基本情報」を取得
//			Optional<AnnualLeaveEmpBasicInfo> basicInfo = annLeaBasicInfoRepo.get(employeeId);
			// 次回年休付与を計算
			List<NextAnnualLeaveGrant> annualLeaveGrant = CalcNextAnnualLeaveGrantDate.algorithm(require,
					cacheCarrier, companyId, employeeId, Optional.empty());
			if (annualLeaveGrant!= null && annualLeaveGrant.size() > 0){
				result.setGrantDate(annualLeaveGrant.get(0).getGrantDate());
				result.setGrantDays(annualLeaveGrant.get(0).getGrantDays().get().v());
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
