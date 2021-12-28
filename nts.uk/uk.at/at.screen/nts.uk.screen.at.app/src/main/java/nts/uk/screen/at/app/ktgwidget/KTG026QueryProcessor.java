package nts.uk.screen.at.app.ktgwidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import nts.arc.time.calendar.Year;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.adapter.monthly.agreement.GetExcessTimesYearAdapter;
import nts.uk.ctx.at.function.dom.adapter.standardtime.GetAgreementPeriodFromYearAdapter;
import nts.uk.ctx.at.record.app.find.monthly.agreement.export.AgreementExcessInfoDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AgreementTimeOfManagePeriodDto;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetAgreementTime;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetAgreementTimeOfMngPeriod;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementOperationSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ScheRecAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.setting.AgreementOperationSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.sys.auth.dom.adapter.person.EmployeeBasicInforAuthImport;
import nts.uk.ctx.sys.auth.dom.wkpmanager.EmpInfoAdapter;
import nts.uk.ctx.sys.auth.dom.wkpmanager.dom.EmpBasicInfoImport;
import nts.uk.screen.at.app.ktgwidget.find.dto.EmployeesOvertimeDisplayDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.PresentClosingPeriodDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.YearAndEmpOtHoursDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.YearMonthOvertime;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class KTG026QueryProcessor {

	// The record dom require service
	@Inject
	private RecordDomRequireService requireService;

	// The agreement operation setting repository
	@Inject
	private AgreementOperationSettingRepository agreementOperationSettingRepository;

	// The employee info adapter
	@Inject
	private EmpInfoAdapter empInfoAdapter;

	@Inject
	private GetAgreementPeriodFromYearAdapter aggremnentPeriodFromYear;

	@Inject
	private GetExcessTimesYearAdapter getExcessTimesYearAdapter;

	/**
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG026_時間外労働時間の表示(従業員用).アルゴリズム.起動する.起動する
	 * @param employeeId
	 * @param targetDate
	 * @param targetYear
	 * @param currentOrNextMonth
	 * @return
	 */
	public EmployeesOvertimeDisplayDto startScreenKtg026(int closureID, String employeeId, Integer targetDate, Integer targetYear,
			int currentOrNextMonth, int processingYm) {
		
		PresentClosingPeriodDto closingPeriod =  PresentClosingPeriodDto.builder()
					.processingYm(processingYm)
					.closureStartDate(null)
					.closureEndDate(null)
					.build();

		// アルゴリズム「年月を指定して、36協定期間の年度を取得する」を実行する
		Year year = this.getYearAgreementPeriod(YearMonth.of(processingYm));

		// 従業員用の時間外時間表示．当月含む年＝取得した年度
		int yearIncludeThisMonth = year.v();
		// 従業員用の時間外時間表示．表示する年＝取得した年度
		int displayYear = year.v();
		int yearIncludeNextMonth = 0;
		// INPUT．対象年をチェックする
		if (targetYear != null) { // INPUT．対象年 != NULL
			// 従業員用の時間外時間表示．表示する年＝INPUT．対象年
			displayYear = targetYear;

		} else if (targetDate != null) { // INPUT．対象年 = NULL && INPUT．対象年月 != NULL
			// アルゴリズム「年月を指定して、36協定期間の年度を取得する」を実行する
			// 従業員用の時間外時間表示．表示する年＝取得した年度
			displayYear = this.getYearAgreementPeriod(YearMonth.of(targetDate)).v();

		} else if (currentOrNextMonth == 2) { // INPUT．表示年月＝翌月表示
			yearIncludeNextMonth = this.getYearAgreementPeriod(YearMonth.of(closingPeriod.getProcessingYm()).addMonths(1)).v();
			displayYear = yearIncludeNextMonth;
		}

		// 社員ID(List)から個人社員基本情報を取得
		List<EmpBasicInfoImport> empInfoLst = this.empInfoAdapter.getListPersonInfo(Arrays.asList(employeeId));

		// 上長用の時間外時間表示．対象社員の個人情報＝取得した「個人社員基本情報」
		EmployeeBasicInforAuthImport empInfo = null;
		if (!empInfoLst.isEmpty()) {
			EmpBasicInfoImport empBasicInfo = empInfoLst.get(0);
			empInfo = new EmployeeBasicInforAuthImport(
					empBasicInfo.getPId(),
					empBasicInfo.getNamePerson(),
					empBasicInfo.getEntryDate(),
					empBasicInfo.getGender(),
					empBasicInfo.getBirthDay(),
					empBasicInfo.getEmployeeId(),
					empBasicInfo.getEmployeeCode(),
					empBasicInfo.getRetiredDate());
		}

		return EmployeesOvertimeDisplayDto.builder()
				.closureID(closureID)
				.empInfo(empInfo)
				.closingPeriod(closingPeriod)
				.yearIncludeThisMonth(yearIncludeThisMonth)
				.yearIncludeNextMonth(yearIncludeNextMonth)
				.displayYear(displayYear)
				.build();
	}

	/**
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG026_時間外労働時間の表示(従業員用).ユースケース.対象年の時間外労働時間を抽出する.対象年の時間外労働時間を抽出する
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG026_時間外労働時間の表示(従業員用).ユースケース.対象年を前年に切り替える.対象年を前年に切り替える
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG026_時間外労働時間の表示(従業員用).ユースケース.対象年を翌年に切り替える.対象年を翌年に切り替える
	 * 
	 * @param employeeId
	 * @param closingId
	 * @param targetYear
	 * @param processingDate
	 * @return 従業員用の時間外時間表示
	 */
	public EmployeesOvertimeDisplayDto extractOvertime(String employeeId, int closingId, Integer targetYear,
			int processingDate) {
		// 指定する年と指定する社員の時間外時間と超過回数の取得
		YearAndEmpOtHoursDto otHours = this.getYearAndEmployeeOTHours(employeeId, closingId, new Year(targetYear), YearMonth.of(processingDate));
		AgreementExcessInfoDto agreeInfo = AgreementExcessInfoDto.builder()
				.excessTimes(otHours.getAgreeInfo())
				.yearMonths(otHours.getOvertimeHours().stream()
								.map(x -> x.getYearMonth())
								.collect(Collectors.toList()))
				.build();
		// 「従業員用の時間外時間表示」を更新する
		return EmployeesOvertimeDisplayDto.builder()
					.ymOvertimes(otHours.getOvertimeHours())
					.agreeInfo(agreeInfo)
					.build();
	}

	/**
	 * 年月を指定して、36協定期間の年度を取得する
	 * 
	 * @param yearMonth 年月
	 * @return year 年度
	 */
	public Year getYearAgreementPeriod(YearMonth yearMonth) {
		// 会社ID
		String companyID = AppContexts.user().companyId();
		// 起算月を取得 Get the starting month
		AgreementOperationSetting agreeOpSet = agreementOperationSettingRepository.find(companyID).get();
		Year year = new Year(yearMonth.year());

		// パラメータ「年月」の月と起算月を比較 The month of the parameter "year-month" and the starting
		// month are compared
		if (yearMonth.month() < (agreeOpSet.getStartingMonth().value + 1)) { // パラメータ「年月」の月 ＜ 起算月 の場合
			// パラメータ「年月」の年を-1年する Make the year of the parameter "year-month" -1 year
			year = new Year(yearMonth.year() - 1);
		}

		return year;
	}

	/**
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG026_時間外労働時間の表示(従業員用).アルゴリズム.指定する年と指定する社員の時間外時間と超過回数の取得.指定する年と指定する社員の時間外時間と超過回数の取得
	 * @param employeeId 社員ID
	 * @param closingId 締めID
	 * @param targetYear 対象年
	 * @param processingDate 当月の年月
	 * @return year and employee overtime hours
	 */
	public YearAndEmpOtHoursDto getYearAndEmployeeOTHours(String employeeId, int closingId, Year targetYear, YearMonth processingDate) {
		// 年月ごとの時間外時間
		List<YearMonthOvertime> ymOvertimes = new ArrayList<YearMonthOvertime>();
		
		val require = requireService.createRequire();
		CacheCarrier cacheCarrier = new CacheCarrier();
		GeneralDate systemDate = GeneralDate.today();
		// [No.458]年間超過回数の取得
		// OUTPUT．「36協定超過情報」＝取得した「36協定超過情報」
		int agreeInfo = getExcessTimesYearAdapter.algorithm(employeeId, targetYear);
		
		// 年度から36集計期間を計算
		// ○社員に対応する処理締めを取得する (Lấy closure xử lý ứng với employee)
		Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, employeeId, systemDate);
		
		// ○年度から集計期間を取得 (Lấy thời gian tính toán từ niên độ )
		Optional<DatePeriod> datePeriod = aggremnentPeriodFromYear.algorithm(targetYear, closure);
		
		if (!datePeriod.isPresent()) {
			return YearAndEmpOtHoursDto.builder().build();
		}
		
		YearMonth loopYM = datePeriod.get().start().yearMonth();
		YearMonth nextYM = loopYM.addYears(1);
		// 取得した期間の年月をループする
		while (loopYM.lessThan(nextYM)) {
			int ym = loopYM.v();
			// ループする年月とINPUT．当月の年月の大小比較
			if (processingDate.addMonths(2).lessThanOrEqualTo(loopYM)) { // [INPUT．当月の年月．AddMonth(2)<=ループする年月]がtrue
				break;
				
			} else if (processingDate.lessThanOrEqualTo(loopYM)) { // [INPUT．当月の年月<=ループする年月]がtrue
				// 【NO.333】36協定時間の取得
				AgreementTimeOfManagePeriod agreementTimeDetail = GetAgreementTime.get(
						require
						, employeeId
						, loopYM
						, new ArrayList<>()
						, loopYM.lastGeneralDate()
						, ScheRecAtr.SCHEDULE);
				
				if (agreementTimeDetail == null) {
					agreementTimeDetail = new AgreementTimeOfManagePeriod(employeeId, loopYM);
				}
				
				ymOvertimes.add(YearMonthOvertime.builder()
						.yearMonth(ym)
						.agreeTime(AgreementTimeOfManagePeriodDto.from(agreementTimeDetail))
						.build());
				
			} else { // [INPUT．当月の年月<=ループする年月]がfalse
				// [NO.612]年月期間を指定して管理期間の36協定時間を取得する
				List<AgreementTimeOfManagePeriod> agreementTimeToppageLst = 
						GetAgreementTimeOfMngPeriod.get(require, Arrays.asList(employeeId), new YearMonthPeriod(loopYM, loopYM));
				
				if (!agreementTimeToppageLst.isEmpty()) {
					ymOvertimes.addAll(agreementTimeToppageLst.stream()
											.map(x -> YearMonthOvertime.builder()
													.yearMonth(ym)
													.agreeTime(AgreementTimeOfManagePeriodDto.from(x))
													.build())
											.collect(Collectors.toList()));
				} else {
					AgreementTimeOfManagePeriod agreementTimeDetail = new AgreementTimeOfManagePeriod(employeeId, loopYM);
					ymOvertimes.add(YearMonthOvertime.builder()
							.yearMonth(ym)
							.agreeTime(AgreementTimeOfManagePeriodDto.from(agreementTimeDetail))
							.build());
				}
			}
			
			loopYM = loopYM.nextMonth();
		}
		// OUTPUT．年月ごとの時間外時間をソートする
		ymOvertimes.stream().sorted((a, b) -> b.getYearMonth() - a.getYearMonth());
		
		return YearAndEmpOtHoursDto.builder()
					.agreeInfo(agreeInfo)
					.overtimeHours(ymOvertimes)
					.build();
		
	}
}
