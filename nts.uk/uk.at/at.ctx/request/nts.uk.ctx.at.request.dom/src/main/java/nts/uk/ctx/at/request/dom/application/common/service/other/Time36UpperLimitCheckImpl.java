package nts.uk.ctx.at.request.dom.application.common.service.other;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.OvertimeHoursDetails;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeaveRepository;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreePeriodYMDExport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementPeriodByYMDAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeStatusAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.ExcessTimesYearAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.reflect.GetApplicationReflectionResultAdapter;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.Time36ErrorOutput;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarly;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarlyRepository;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplicationRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36Agree;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeAnnual;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeMonth;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeUpperLimit;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeUpperLimitAverage;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeUpperLimitMonth;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImage;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImageRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampRepository;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplication;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.obtainappreflect.ObtainAppReflectResultProcess;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.Time36AgreeCheckRegister;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeYear;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SysEmploymentHisAdapter;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreMaxAverageTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreMaxTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ScheRecAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36AgreementError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36AgreementErrorAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36ErrorInforList;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class Time36UpperLimitCheckImpl implements Time36UpperLimitCheck {

	@Inject
	SysEmploymentHisAdapter sysEmploymentHisAdapter;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;

	@Inject
	private AgreementTimeAdapter agreementTimeAdapter;

	@Inject
	private ExcessTimesYearAdapter excessTimesYearAdapter;

//	@Inject
//	private OutsideOTSettingService outsideOTSettingService;

	@Inject
	private AgreementTimeStatusAdapter agreementTimeStatusAdapter;
	
	@Inject
	private ClosureRepository closureRepository;
	
//	@Inject
//	private OvertimeRestAppCommonSetRepository overtimeRestAppCommonSetRepository;
	
	@Inject
	private AgreementPeriodByYMDAdapter agreementPeriodByYMDAdapter;
	
	@Inject
	private AppWorkChangeRepository appWorkChangeRepository;
	
	@Inject
	private GoBackDirectlyRepository goBackDirectlyRepository;
	
	@Inject
	private AppStampRepository appStampRepository;
	
	@Inject
	private ArrivedLateLeaveEarlyRepository arrivedLateLeaveEarlyRepository;
	
	@Inject
	private BusinessTripRepository businessTripRepository;
	
	@Inject
	private AppRecordImageRepository appRecordImageRepository;
	
	@Inject
	private TimeLeaveApplicationRepository timeLeaveApplicationRepository;
	
	@Inject
	private AppOverTimeRepository appOverTimeRepository;
	
	@Inject
	private ApplyForLeaveRepository applyForLeaveRepository;
	
	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;
	
	@Inject
	private AbsenceLeaveAppRepository absenceLeaveAppRepository;
	
	@Inject
	private RecruitmentAppRepository recruitmentAppRepository;
	
	@Inject
	private OptionalItemApplicationRepository optionalItemApplicationRepository;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject
	private GetApplicationReflectionResultAdapter getApplicationReflectionResultAdapter;

	
	// 36協定時間のエラーチェック処理
	private List<Time36ErrorOutput> agree36CheckError(Time36AgreeCheckRegister time36AgreeCheckRegister, Time36Agree time36Agree, YearMonth yearMonth){
		List<Time36ErrorOutput> time36ErrorLst = new ArrayList<>();
		// 登録不可３６協定チェック区分をチェック
		if(time36AgreeCheckRegister==Time36AgreeCheckRegister.CHECK_ONLY_UPPER_OVERTIME){
			return time36ErrorLst;
		}
		// 月間のチェック
		List<Time36ErrorOutput> monthlyError = this.monthlyCheck(time36Agree.getAgreeMonth(), time36Agree.getApplicationTime(), yearMonth);
		// 年間のチェック
		List<Time36ErrorOutput> annualError = this.annualCheck(time36Agree.getAgreeAnnual(), time36Agree.getApplicationTime());
		time36ErrorLst.addAll(monthlyError);
		time36ErrorLst.addAll(annualError);
		return time36ErrorLst;
	} 
	
	// 月間のチェック
	private List<Time36ErrorOutput> monthlyCheck(Time36AgreeMonth agreeMonth, AttendanceTimeMonth apptime, YearMonth yearMonth){
		List<Time36ErrorOutput> time36ErrorLst = new ArrayList<>();
		// 36協定時間の状態チェック
		/** TODO: 36協定時間対応により、コメントアウトされた */
//		AgreementTimeStatusOfMonthly checkAgreement = agreementTimeStatusAdapter.checkAgreementTimeStatus(
//				new AttendanceTimeMonth(apptime.v()+agreeMonth.getActualTime().v()),
//				agreeMonth.getLimitAlarmTime(), 
//				agreeMonth.getLimitErrorTime(),
//				agreeMonth.getExceptionLimitAlarmTime(), 
//				agreeMonth.getExceptionLimitErrorTime());
		
//		if (AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR.equals(checkAgreement)
//				|| AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR.equals(checkAgreement)) {
//			// エラー情報一覧に「月間エラー」を追加
//			String realTime = Integer.toString(agreeMonth.getActualTime().v() + apptime.v());
//			String limitTime = "";
//			if(agreeMonth.getExceptionLimitErrorTime().isPresent()){	
//				limitTime = agreeMonth.getExceptionLimitErrorTime().get().toString();
//			} else {
//				limitTime = agreeMonth.getLimitErrorTime().toString();
//			}
//			time36ErrorLst.add(new Time36ErrorOutput(Time36ErrorFlg.MONTH, realTime, limitTime, "", ""));
//			if(!(agreeMonth.getYear36OverMonth().stream().filter(x -> x.equals(yearMonth)).count() > 0)){
//				// 「時間外時間の詳細」．36年間超過回数 += 1、「時間外時間の詳細」．36年間超過月.Add(「時間外時間の詳細」．年月)
//				agreeMonth.setNumOfYear36Over(agreeMonth.getNumOfYear36Over().v()+1);
//				List<YearMonth> oldLst = agreeMonth.getYear36OverMonth();
//				oldLst.add(yearMonth);
//				agreeMonth.setYear36OverMonth(oldLst);
//			}
//		} else if(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM.equals(checkAgreement)
//				|| AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP.equals(checkAgreement)){
//			if(!(agreeMonth.getYear36OverMonth().stream().filter(x -> x.equals(yearMonth)).count() > 0)){
//				// 「時間外時間の詳細」．36年間超過回数 += 1、「時間外時間の詳細」．36年間超過月.Add(「時間外時間の詳細」．年月)
//				agreeMonth.setNumOfYear36Over(agreeMonth.getNumOfYear36Over().v()+1);
//				List<YearMonth> oldLst = agreeMonth.getYear36OverMonth();
//				oldLst.add(yearMonth);
//				agreeMonth.setYear36OverMonth(oldLst);
//			}
//		}
		return time36ErrorLst;
	}
	
	// 年間のチェック
	private List<Time36ErrorOutput> annualCheck(Time36AgreeAnnual agreeAnnual, AttendanceTimeMonth applicationTime){
		List<Time36ErrorOutput> time36ErrorLst = new ArrayList<>();
		/** TODO: 36協定時間対応により、コメントアウトされた */
//		AgreementTimeYear agreementTimeYear = AgreementTimeYear.of(agreeAnnual.getLimitTime(), agreeAnnual.getActualTime(), AgreTimeYearStatusOfMonthly.NORMAL);
		Optional<AttendanceTimeYear> requestTimeOpt = Optional.ofNullable(new AttendanceTimeYear(applicationTime.v()));
		// [NO.545]36協定年間時間の状態チェック
//		AgreTimeYearStatusOfMonthly yearStatus = agreementTimeAdapter.timeYear(agreementTimeYear, requestTimeOpt);
//		if(yearStatus==AgreTimeYearStatusOfMonthly.EXCESS_LIMIT){
//			String realTime = Integer.toString(agreeAnnual.getActualTime().v() + applicationTime.v());
//			String limitTime = agreeAnnual.getLimitTime().toString();
//			time36ErrorLst.add(new Time36ErrorOutput(Time36ErrorFlg.YEAR, realTime, limitTime, "", ""));
//		}
		return time36ErrorLst;
	}
	
	// 36協定上限時間のエラーチェック
	private List<Time36ErrorOutput> agree36UpperLimitCheckError(String companyID, String employeeID, GeneralDate appDate, Time36AgreeUpperLimit time36AgreeUpperLimit){
		List<Time36ErrorOutput> time36ErrorLst = new ArrayList<>();
		// 月間のチェック
		List<Time36ErrorOutput> monthlyUpperError = this.monthlyUpperLimitCheck(time36AgreeUpperLimit.getAgreeUpperLimitMonth(), time36AgreeUpperLimit.getApplicationTime());
		// 複数月平均のチェック
		List<Time36ErrorOutput> monthlyAverageError = this.monthlyAverageCheck(companyID, employeeID, appDate, 
				time36AgreeUpperLimit.getAgreeUpperLimitAverage(), time36AgreeUpperLimit.getApplicationTime());
		time36ErrorLst.addAll(monthlyUpperError);
		time36ErrorLst.addAll(monthlyAverageError);
		return time36ErrorLst;
	} 
	
	// 月間のチェック
	private List<Time36ErrorOutput> monthlyUpperLimitCheck(Time36AgreeUpperLimitMonth agreeUpperLimitMonth, AttendanceTimeMonth applicationTime){
		List<Time36ErrorOutput> time36ErrorLst = new ArrayList<>();
		// [NO.540]36協定上限時間の状態チェック
		/** TODO: 36協定時間対応により、コメントアウトされた */
//		AgreMaxTimeStatusOfMonthly maxTimeStatus = agreementTimeAdapter.maxTime(
//				new AttendanceTimeMonth(agreeUpperLimitMonth.getOverTime().v() + applicationTime.v()), 
//				agreeUpperLimitMonth.getUpperLimitTime(), 
//				Optional.empty());
//		if(maxTimeStatus==AgreMaxTimeStatusOfMonthly.ALARM_OVER){
//			// エラー情報一覧に「上限月間時間エラー」を追加
//			String realTime = Integer.toString(agreeUpperLimitMonth.getOverTime().v() + applicationTime.v());
//			String limitTime = agreeUpperLimitMonth.getUpperLimitTime().toString();
//			time36ErrorLst.add(new Time36ErrorOutput(Time36ErrorFlg.MAX_MONTH, realTime, limitTime, "", ""));
//		}
		return time36ErrorLst;
	}
	
	// 複数月平均のチェック
	private List<Time36ErrorOutput> monthlyAverageCheck(String companyID, String employeeID, GeneralDate appDate,
			Time36AgreeUpperLimitAverage agreeUpperLimitAverage, AttendanceTimeMonth applicationTime){
		List<Time36ErrorOutput> time36ErrorLst = new ArrayList<>();
		// 36協定上限複数月平均時間の状態チェック
		/** TODO: 36協定時間対応により、コメントアウトされた */
//		AgreMaxAverageTimeMulti agreMaxAverageTimeMulti = agreementTimeAdapter.maxAverageTimeMulti(
//				companyID, 
//				AgreMaxAverageTimeMulti.of(
//						agreeUpperLimitAverage.getUpperLimitTime(), 
//						agreeUpperLimitAverage.getAverageTimeLst()
//							.stream().map(x -> AgreMaxAverageTime.of(
//									x.getPeriod(), 
//									x.getTotalTime(), 
//									AgreMaxTimeStatusOfMonthly.NORMAL))
//							.collect(Collectors.toList())), 
//				Optional.ofNullable(new AttendanceTime(applicationTime.v())), 
//				Optional.ofNullable(appDate));
		/** TODO: 36協定時間対応により、コメントアウトされた */
//		for(AgreMaxAverageTime agreMaxAverageTime : agreMaxAverageTimeMulti.getAverageTimeList()){
//			if(agreMaxAverageTime.getStatus()==AgreMaxTimeStatusOfMonthly.ALARM_OVER){
//				// エラー情報一覧に「上限複数月平均時間エラー」を追加
//				String yearMonthStart = agreMaxAverageTime.getPeriod().start().toString();
//				String yearMonthEnd = agreMaxAverageTime.getPeriod().end().toString();
//				String realTime = agreMaxAverageTime.getAverageTime().toString();
//				String limitTime = agreMaxAverageTimeMulti.getMaxTime().toString();
//				time36ErrorLst.add(new Time36ErrorOutput(Time36ErrorFlg.AVERAGE_MONTH, realTime, limitTime, yearMonthStart, yearMonthEnd));
//			}
//		}
		return time36ErrorLst;
	}

	@Override
	public Time36ErrorInforList checkRegister(String companyID, String employeeID, String employmentCD,
			Application application, Optional<AppOverTime> opAppOverTime, Optional<AppHolidayWork> opAppHolidayWork,
			Time36AgreeCheckRegister extratimeExcessAtr, NotUseAtr extratimeDisplayAtr) {
		Time36ErrorInforList time36ErrorInforList = new Time36ErrorInforList(new ArrayList<>());
		// INPUT．時間外超過区分と時間外表示区分をチェックする
		if(extratimeExcessAtr==Time36AgreeCheckRegister.NOT_CHECK && extratimeDisplayAtr==NotUseAtr.NOT_USE) {
			return time36ErrorInforList;
		}
		// 時間外時間の詳細を作成
		OvertimeHoursDetails overtimeHoursDetails = this.createOvertimeHoursDetails(
				companyID, 
				employeeID, 
				employmentCD, 
				application, 
				opAppOverTime, 
				opAppHolidayWork);
		// 36協定時間のエラーチェック処理
		Time36ErrorInforList time36ErrorInforList1 = this.time36AgreementCheck(extratimeExcessAtr, overtimeHoursDetails);
		time36ErrorInforList.getTime36AgreementErrorLst().addAll(time36ErrorInforList1.getTime36AgreementErrorLst());
		// 36協定上限時間のエラーチェック
		Time36ErrorInforList time36ErrorInforList2 = this.time36AgreementMaxCheck(extratimeExcessAtr, overtimeHoursDetails);
		time36ErrorInforList.getTime36AgreementErrorLst().addAll(time36ErrorInforList2.getTime36AgreementErrorLst());
		return time36ErrorInforList;
	}
	
	/**
	 * refactor 5
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム."18.３６時間の上限チェック(新規登録)_NEW".時間外時間の詳細を作成.時間外時間の詳細を作成
	 * @param companyID
	 * @param employeeID
	 * @param employmentCD
	 * @param application
	 * @param opAppOverTime
	 * @param opAppHolidayWork
	 * @return
	 */
	private OvertimeHoursDetails createOvertimeHoursDetails(String companyID, String employeeID, String employmentCD,
			Application application, Optional<AppOverTime> opAppOverTime, Optional<AppHolidayWork> opAppHolidayWork){
		OvertimeHoursDetails overtimeHoursDetails = new OvertimeHoursDetails();
		// 雇用に紐づく締めを取得する
		Optional<ClosureEmployment> closureEmpOtp = closureEmploymentRepository.findByEmploymentCD(companyID,
				employmentCD);
		Optional<Closure> opClosure = closureRepository.findById(companyID, closureEmpOtp.get().getClosureId());
		if(!opClosure.isPresent()){
			throw new RuntimeException("khong co closure");
		}
		Closure closure = opClosure.get();
		// 指定した年月日時点の締め期間を取得する
		Optional<ClosurePeriod> closurePeriodOpt = closure.getClosurePeriodByYmd(application.getAppDate().getApplicationDate());
		// [NO.601]年月日を指定して、36協定期間を取得する
		AgreePeriodYMDExport agreePeriodYMDExport = agreementPeriodByYMDAdapter.getAgreementPeriod(
				companyID, application.getAppDate().getApplicationDate(), closurePeriodOpt.map(x -> x.getClosureId()).orElse(null));
		overtimeHoursDetails.setYearMonth(agreePeriodYMDExport.getDateTime());
		// 日別実績への申請反映結果を取得
		Require require = new Require(
				appWorkChangeRepository,
				goBackDirectlyRepository,
				appStampRepository,
				arrivedLateLeaveEarlyRepository,
				businessTripRepository,
				appRecordImageRepository,
				timeLeaveApplicationRepository,
				appOverTimeRepository,
				applyForLeaveRepository,
				appHolidayWorkRepository,
				absenceLeaveAppRepository,
				recruitmentAppRepository,
				optionalItemApplicationRepository,
				applicationRepository,
				getApplicationReflectionResultAdapter);
		if (opAppOverTime.isPresent()) {
			final val applicationTemp = application;
			application = (Application)opAppOverTime.map(x -> {
				x.setApplication(applicationTemp);
				return x;
			}).orElse(null);
		} else if (opAppHolidayWork.isPresent()) {
			final val applicationTemp = application;
			application = (Application)opAppHolidayWork.map(x -> {
				x.setApplication(applicationTemp);
				return x;
			}).orElse(null);
		}
		Optional<IntegrationOfDaily> dailyRecordOp = ObtainAppReflectResultProcess.process(
				require,
				companyID,
				employeeID,
				application.getAppDate().getApplicationDate(),
				Optional.ofNullable(application)
				);
		List<IntegrationOfDaily> dailyRecord = new ArrayList<>();
		if (dailyRecordOp.isPresent()) {
			dailyRecord.add(dailyRecordOp.get());
		}
		// 【NO.333】36協定時間の取得
		AgreementTimeOfManagePeriod agreementTimeOfManagePeriod = agreementTimeAdapter.getAgreementTimeOfManagePeriod(
				employeeID, 
				overtimeHoursDetails.getYearMonth(), 
				dailyRecord, 
				GeneralDate.today(), 
				ScheRecAtr.SCHEDULE);
		overtimeHoursDetails.setAgreementTime(agreementTimeOfManagePeriod.getAgreementTime());
		overtimeHoursDetails.setLegalMaxTime(agreementTimeOfManagePeriod.getLegalMaxTime());
		overtimeHoursDetails.setStatus(agreementTimeOfManagePeriod.getStatus());
		// [NO.599]36協定上限複数月平均時間と年間時間の取得(日指定)
		AgreementTimeImport agreementTimeImport = agreementTimeAdapter.getAverageAndYear(
				companyID, 
				employeeID, 
				overtimeHoursDetails.getYearMonth(), 
				application.getAppDate().getApplicationDate(), 
				ScheRecAtr.SCHEDULE);
		overtimeHoursDetails.setAgreementTimeYear(agreementTimeImport.getAgreementTimeYear());
		overtimeHoursDetails.setAgreMaxAverageTimeMulti(agreementTimeImport.getAgreMaxAverageTimeMulti());
		return overtimeHoursDetails;
	}
	
	/**
	 * refactor 5
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム."18.３６時間の上限チェック(新規登録)_NEW".時間外時間.アルゴリズム.36協定時間のエラーチェック処理.36協定時間のエラーチェック処理
	 * @param extratimeExcessAtr
	 * @param overtimeHoursDetails
	 */
	private Time36ErrorInforList time36AgreementCheck(Time36AgreeCheckRegister extratimeExcessAtr, OvertimeHoursDetails overtimeHoursDetails) {
		Time36ErrorInforList time36ErrorInforList = new Time36ErrorInforList(new ArrayList<>());
		// INPUT．時間外超過区分をチェックする
		if(extratimeExcessAtr==Time36AgreeCheckRegister.NOT_CHECK) {
			return time36ErrorInforList;
		}
		// INPUT．時間外時間の詳細．月別実績の36協定時間状態をチェックする
		if(overtimeHoursDetails.getStatus()==AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR ||
				overtimeHoursDetails.getStatus()==AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR) {
			// エラー情報一覧に項目を追加
			time36ErrorInforList.getTime36AgreementErrorLst().add(new Time36AgreementError(
					overtimeHoursDetails.getAgreementTime().getThreshold().getErAlTime().getError().v(), 
					overtimeHoursDetails.getAgreementTime().getAgreementTime().v(), 
					Time36AgreementErrorAtr.MONTH_ERROR, 
					Optional.empty()));
		}
		// INPUT．時間外時間の詳細．36協定年間時間．状態をチェックする
		if(overtimeHoursDetails.getAgreementTimeYear().getStatus()==AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR ||
				overtimeHoursDetails.getAgreementTimeYear().getStatus()==AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR) {
			// エラー情報一覧に項目を追加
			time36ErrorInforList.getTime36AgreementErrorLst().add(new Time36AgreementError(
					overtimeHoursDetails.getAgreementTimeYear().getRecordTime().getThreshold().getErAlTime().getError().v(), 
					overtimeHoursDetails.getAgreementTimeYear().getRecordTime().getTargetTime().v(), 
					Time36AgreementErrorAtr.YEAR_ERROR, 
					Optional.empty()));
		}
		return time36ErrorInforList;
	}
	
	/**
	 * refactor 5
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム."18.３６時間の上限チェック(新規登録)_NEW".時間外時間.アルゴリズム.36協定上限時間のエラーチェック.36協定上限時間のエラーチェック
	 * @param extratimeExcessAtr
	 * @param overtimeHoursDetails
	 * @return
	 */
	private Time36ErrorInforList time36AgreementMaxCheck(Time36AgreeCheckRegister extratimeExcessAtr, OvertimeHoursDetails overtimeHoursDetails) {
		Time36ErrorInforList time36ErrorInforList = new Time36ErrorInforList(new ArrayList<>());
		// INPUT．時間外超過区分をチェックする
		if(extratimeExcessAtr==Time36AgreeCheckRegister.NOT_CHECK) {
			return time36ErrorInforList;
		}
		// INPUT．時間外時間の詳細．月別実績の36協定時間状態をチェックする
		if(overtimeHoursDetails.getStatus()==AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP ||
				overtimeHoursDetails.getStatus()==AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY) {
			// エラー情報一覧に項目を追加
			time36ErrorInforList.getTime36AgreementErrorLst().add(new Time36AgreementError(
					overtimeHoursDetails.getLegalMaxTime().getThreshold().getErAlTime().getError().v(), 
					overtimeHoursDetails.getLegalMaxTime().getAgreementTime().v(), 
					Time36AgreementErrorAtr.MAX_MONTH_ERROR, 
					Optional.empty()));
		}
		// INPUT．時間外時間の詳細．36協定年間時間．状態をチェックする
		if(overtimeHoursDetails.getAgreementTimeYear().getStatus()==AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP ||
				overtimeHoursDetails.getAgreementTimeYear().getStatus()==AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY) {
			// エラー情報一覧に項目を追加
			time36ErrorInforList.getTime36AgreementErrorLst().add(new Time36AgreementError(
					overtimeHoursDetails.getAgreementTimeYear().getLimitTime().getThreshold().getErAlTime().getError().v(), 
					overtimeHoursDetails.getAgreementTimeYear().getLimitTime().getTargetTime().v(), 
					Time36AgreementErrorAtr.MAX_YEAR_ERROR, 
					Optional.empty()));
		}
		// INPUT．36協定上限複数月平均時間．平均時間をループする
		for(AgreMaxAverageTime agreMaxAverageTime : overtimeHoursDetails.getAgreMaxAverageTimeMulti().getAverageTimes()) {
			// ループする平均時間．状態をチェックする
			if(agreMaxAverageTime.getStatus()==AgreMaxTimeStatusOfMonthly.ERROR_OVER) {
				// エラー情報一覧に項目を追加
				time36ErrorInforList.getTime36AgreementErrorLst().add(new Time36AgreementError(
				overtimeHoursDetails.getAgreMaxAverageTimeMulti().getMaxTime().getError().v(), 
				agreMaxAverageTime.getAverageTime().v(), 
				Time36AgreementErrorAtr.MAX_MONTH_AVERAGE_ERROR, 
				Optional.of(agreMaxAverageTime.getPeriod())));
			}
		}
		return time36ErrorInforList;
	}
	
	@AllArgsConstructor
	public class Require implements ObtainAppReflectResultProcess.Require {
		
		private AppWorkChangeRepository appWorkChangeRepository;
		
		private GoBackDirectlyRepository goBackDirectlyRepository;
		
		private AppStampRepository appStampRepository;
		
		private ArrivedLateLeaveEarlyRepository arrivedLateLeaveEarlyRepository;
		
		private BusinessTripRepository businessTripRepository;
		
		private AppRecordImageRepository appRecordImageRepository;
		
		private TimeLeaveApplicationRepository timeLeaveApplicationRepository;
		
		private AppOverTimeRepository appOverTimeRepository;
		
		private ApplyForLeaveRepository applyForLeaveRepository;
		
		private AppHolidayWorkRepository appHolidayWorkRepository;
		
		private AbsenceLeaveAppRepository absenceLeaveAppRepository;
		
		private RecruitmentAppRepository recruitmentAppRepository;
		
		private OptionalItemApplicationRepository optionalItemApplicationRepository;
		
		private ApplicationRepository applicationRepository;
		
		private GetApplicationReflectionResultAdapter getApplicationReflectionResultAdapter;
		
		
		
		@Override
		public Optional<AppWorkChange> findAppWorkCg(String companyId, String appID, Application app) {
			
			return appWorkChangeRepository.findbyID(companyId, appID, app).flatMap(x -> {
				x.setApplication(app);
				return Optional.of(x);
			});
		}

		@Override
		public Optional<GoBackDirectly> findGoBack(String companyId, String appID, Application app) {
			
			return goBackDirectlyRepository.find(companyId, appID, app).flatMap(x -> {
				x.setApplication(app);
				return Optional.of(x);
			});
		}

		@Override
		public Optional<AppStamp> findAppStamp(String companyId, String appID, Application app) {
			
			return appStampRepository.findByAppID(companyId, appID, app).flatMap(x -> {
				x.setApplication(app);
				return Optional.of(x);
			});
		}

		@Override
		public Optional<ArrivedLateLeaveEarly> findArrivedLateLeaveEarly(String companyId, String appID,
				Application application) {

			return Optional.ofNullable(arrivedLateLeaveEarlyRepository.getLateEarlyApp(companyId, appID, application)).flatMap(x -> {
				x.setApplication(application);
				return Optional.of(x);
			});
			
		}

		@Override
		public Optional<BusinessTrip> findBusinessTripApp(String companyId, String appID, Application app) {
			
			return businessTripRepository.findByAppId(companyId, appID, app).flatMap(x -> {
				x.setApplication(app);
				return Optional.of(x);
			});
		}

		@Override
		public Optional<AppRecordImage> findAppRecordImage(String companyId, String appID, Application app) {
			
			return appRecordImageRepository.findByAppID(companyId, appID, app).flatMap(x -> {
				x.setApplication(app);
				return Optional.of(x);
			});
		}

		@Override
		public Optional<TimeLeaveApplication> findTimeLeavById(String companyId, String appId) {
			
			return timeLeaveApplicationRepository.findById(companyId, appId);
		}

		@Override
		public Optional<AppOverTime> findOvertime(String companyId, String appId) {
			
			return appOverTimeRepository.find(companyId, appId);
		}

		@Override
		public Optional<ApplyForLeave> findApplyForLeave(String CID, String appId) {
			
			return applyForLeaveRepository.findApplyForLeave(CID, appId);
		}

		@Override
		public Optional<AppHolidayWork> findAppHolidayWork(String companyId, String appId) {
			
			return appHolidayWorkRepository.find(companyId, appId);
		}

		@Override
		public Optional<AbsenceLeaveApp> findAbsenceByID(String applicationID) {
			
			return absenceLeaveAppRepository.findByAppId(applicationID);
		}

		@Override
		public Optional<RecruitmentApp> findRecruitmentByID(String applicationID) {
			
			return recruitmentAppRepository.findByID(applicationID);
		}

		@Override
		public Optional<OptionalItemApplication> getOptionalByAppId(String companyId, String appId) {
			
			return optionalItemApplicationRepository.getByAppId(companyId, appId);
		}

		@Override
		public List<Application> getAppForReflect(String sid, GeneralDate dateData, List<Integer> recordStatus,
				List<Integer> scheStatus, List<Integer> appType) {
			
			return applicationRepository.getAppForReflect(sid, new DatePeriod(dateData, dateData), recordStatus, scheStatus, appType);
		}

		@Override
		public Optional<IntegrationOfDaily> getAppReflectResult(String cid, ApplicationShare application,
				GeneralDate baseDate, Optional<IntegrationOfDaily> dailyData) {
			return getApplicationReflectionResultAdapter.getApp(cid, application, baseDate, dailyData);
		}
		
	}
}
