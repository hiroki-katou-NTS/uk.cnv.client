package nts.uk.ctx.at.request.dom.application.common.service.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.error.BusinessException;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.clock.ClockHourMinute;
import nts.gul.collection.CollectionUtil;
import nts.gul.mail.send.MailContents;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.RecordWorkInfoAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.RecordWorkInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.EnvAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.dto.MailDestinationImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.ApprovalRootStateAdapter;
import nts.uk.ctx.at.request.dom.application.common.service.application.IApplicationContentService;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.User;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AppCompltLeaveSyncOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.MailResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.PeriodCurrentMonth;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRec;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRecRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.SyncState;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.overtime.service.CheckWorkingInfoResult;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.BeforeAddCheckMethod;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.OTAppBeforeAccepRestric;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSet;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.emailset.Division;
import nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon.primitive.AppDisplayAtr;
import nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon.primitive.InitValueAtr;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.worktime.common.AbolishAtr;
import nts.uk.ctx.at.shared.dom.worktime.workplace.WorkTimeWorkplaceRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.service.WorkTypeIsClosedService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.mail.MailSender;
import nts.uk.shr.com.time.AttendanceClock;
import nts.uk.shr.com.url.RegisterEmbededURL;

@Stateless
public class OtherCommonAlgorithmImpl implements OtherCommonAlgorithm {

	@Inject
	private EmployeeRequestAdapter employeeAdaptor;

	@Inject
	private WorkTimeWorkplaceRepository workTimeWorkplaceRepo;

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;

	@Inject
	private AbsenceLeaveAppRepository absRepo;
	@Inject
	private AppHdsubRecRepository compLeaveRepo;

	@Inject
	private MailSender mailsender;

	@Inject
	private EnvAdapter envAdapter;

	@Inject
	private RegisterEmbededURL registerEmbededURL;

	@Inject
	private IApplicationContentService applicationContentService;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private RecordWorkInfoAdapter recordWorkInfoAdapter;

	@Inject
	private ScBasicScheduleAdapter scBasicScheduleAdapter;

	@Inject
	private WorkTypeRepository workTypeRepository;

	@Inject
	private WorkTimeSettingRepository workTimeRepository;

	@Inject
	private AppEmailSetRepository appEmailSetRepository;

	@Inject
	private CollectAchievement collectAchievement;

	@Inject
	private ApprovalRootStateAdapter approvalRootStateAdapter;

	@Inject
	private ApplicationRepository applicationRepository;

	public PeriodCurrentMonth employeePeriodCurrentMonthCalculate(String companyID, String employeeID, GeneralDate date){
		/*
		アルゴリズム「社員所属雇用履歴を取得」を実行する(thực hiện xử lý 「社員所属雇用履歴を取得」)
		*/
		SEmpHistImport empHistImport = employeeAdaptor.getEmpHist(companyID, employeeID, date);
		if(empHistImport==null || empHistImport.getEmploymentCode()==null){
			throw new BusinessException("Msg_426");
		}
		String employmentCD = empHistImport.getEmploymentCode();
		// ドメインモデル「雇用に紐づく就業締め」を取得する
		Optional<ClosureEmployment> closureEmployment = closureEmploymentRepository.findByEmploymentCD(companyID, employmentCD);
		if(!closureEmployment.isPresent()){
			throw new BusinessException("Msg_1134");
		}
		/*
		ドメインモデル「締め」を取得する(lấy thông tin domain「締め」)
		Object<String: tightenID, String: currentMonth> obj1 = Tighten.find(companyID, employeeCD); // obj1 <=> (締めID,当月)
		*/
		Optional<Closure> closure = closureRepository.findById(companyID, closureEmployment.get().getClosureId());
		if(!closure.isPresent()){
			throw new RuntimeException("khong co closure");
		}
		/*
		当月の期間を算出する(tính period của tháng hiện tại)
		Object<String: startDate, String: endDate> obj2 = Period.find(obj1.tightenID, obj1.currentMonth); // obj2 <=> 締め期間(開始年月日,終了年月日) 
		*/
		DatePeriod datePeriod = ClosureService.getClosurePeriod(closure.get().getClosureId().value,
				closure.get().getClosureMonth().getProcessingYm(), closure);
		return new PeriodCurrentMonth(closure.get().getClosureId(), datePeriod.start(), datePeriod.end());
	}
	/**
	 * 1.職場別就業時間帯を取得
	 */
	@Override
	public List<WorkTimeSetting> getWorkingHoursByWorkplace(String companyID, String employeeID, GeneralDate referenceDate) {
		// 申請本人の所属職場を含める上位職場を取得する
		List<String> wkpIDLst = employeeAdaptor.findWpkIdsBySid(companyID, employeeID, referenceDate);
		// 取得した所属職場ID＋その上位職場IDを先頭から最後までループする
		// Loop theo AffWorkplace ID + upperWorkplaceID  từ đầu đến cuối
		for(String wkpID : wkpIDLst) {
			// アルゴリズム「職場IDから職場別就業時間帯を取得」を実行する
			List<WorkTimeSetting> listWorkTime = workTimeWorkplaceRepo.getWorkTimeWorkplaceById(companyID, wkpID);
			if(listWorkTime.size()>0) {
				Collections.sort(listWorkTime, (x1, x2) -> x1.getWorktimeCode().compareTo(x2.getWorktimeCode()));
				return listWorkTime;
			}
		}
		// アルゴリズム「廃止区分によって就業時間帯を取得する」を実行する
		return workTimeSettingRepository.findByCompanyId(companyID).stream()
			.filter(x -> x.getAbolishAtr()==AbolishAtr.NOT_ABOLISH)
			.sorted(Comparator.comparing(x -> x.getWorktimeCode().v())).collect(Collectors.toList());
	}

	@Override
	public PrePostAtr preliminaryJudgmentProcessing(ApplicationType appType, GeneralDate appDate, OvertimeAppAtr overtimeAppAtr,
			OTAppBeforeAccepRestric otAppBeforeAccepRestric) {
		// 申請対象日とシステム日付を比較する
		GeneralDate systemDate = GeneralDate.today();
		if(appDate.after(systemDate)) {
			// 事前事後区分=事前
			return PrePostAtr.PREDICT;
		}
		if(appDate.before(systemDate)) {
			// 事前事後区分=事後 
			return PrePostAtr.POSTERIOR;
		}
		// INPUT．申請種類をチェックする
		if(appType != ApplicationType.OVER_TIME_APPLICATION) {
			// 事前事後区分=事後
			return PrePostAtr.POSTERIOR;
		}
		// INPUT.残業申請事前の受付制限．チェック方法をチェックする
		if(otAppBeforeAccepRestric.getMethodCheck() == BeforeAddCheckMethod.CHECK_IN_DAY) {
			// 事前事後区分=事後 
			return PrePostAtr.POSTERIOR;
		}
		AttendanceClock compareTime = null;
		switch (overtimeAppAtr) {
			case EARLY_OVERTIME:
				// 受付制限日時＝　INPUT.「残業申請事前の受付制限」．時刻（早出残業）
				compareTime = otAppBeforeAccepRestric.getOpEarlyOvertime().get();
				break;
			case NORMAL_OVERTIME:
				// 受付制限日時＝　INPUT.「残業申請事前の受付制限」．時刻（通常残業）
				compareTime = otAppBeforeAccepRestric.getOpNormalOvertime().get();
				break;
			case EARLY_NORMAL_OVERTIME:
				// 受付制限日時＝　INPUT.「残業申請事前の受付制限」．時刻（早出残業・通常残業）
				compareTime = otAppBeforeAccepRestric.getOpEarlyNormalOvertime().get();
				break;
			case MULTIPLE_OVERTIME:
				// 受付制限日時＝　INPUT.「残業申請事前の受付制限」．時刻（複数回残業）
				compareTime = otAppBeforeAccepRestric.getOpMultipleOvertime().get();
				break;
			default:
				break;
		}
		// システム日時と受付制限日時と比較する
		ClockHourMinute systemTime = ClockHourMinute.now();
		if(systemTime.v() > compareTime.v()) {
			// 事前事後区分=事後 
			return PrePostAtr.POSTERIOR;
		}
		// 事前事後区分=事前 
		return PrePostAtr.PREDICT;
	}
	/**
	 * 5.事前事後区分の判断
	 */
	@Override
	public InitValueAtr judgmentPrePostAtr(ApplicationType appType, GeneralDate appDate,boolean checkCaller) {
		InitValueAtr outputInitValueAtr = null;
//		String companyID = AppContexts.user().companyId();
//		Optional<AppTypeDiscreteSetting> appTypeDisc = appTypeDiscreteSettingRepo.getAppTypeDiscreteSettingByAppType(companyID, appType.value);
//		Optional<ApplicationSetting> appSetting = appSettingRepo.getApplicationSettingByComID(companyID);
//		if(appSetting.get().getDisplayPrePostFlg() == AppDisplayAtr.DISPLAY) { // AppDisplayAtr displayPrePostFlg
//			//メニューから起動(Boot from menu) : checkCaller == true
//			if(checkCaller) {
//				outputInitValueAtr = appTypeDisc.get().getPrePostInitFlg();
//			}else {// その他のPG（日別修正、トップページアラーム、残業指示）から起動(Start from other PG (daily correction, top page alarm, overtime work instruction)): checkCaller == false
//				outputInitValueAtr = InitValueAtr.POST;
//			}
//		}else { //if not display
//			outputInitValueAtr = InitValueAtr.NOCHOOSE;
//		}
		return outputInitValueAtr;
	}
	/**
	 * 9.同時申請された振休振出申請を取得する
	 * @author hoatt
	 * @param companyId
	 * @param appId
	 * @return
	 */
	@Override
	public AppCompltLeaveSyncOutput getAppComplementLeaveSync(String companyId, String appId) {

		Optional<AbsenceLeaveApp> abs = absRepo.findByAppId(appId);
		Optional<AppHdsubRec> sync = null;
		String absId = "";
		String recId = "";
		boolean synced = false;
		int type = 0;
		if(abs.isPresent()){//don xin nghi
			absId = appId;
			//tim lien ket theo abs
			sync = compLeaveRepo.findByAbsID(appId);
			if(sync.isPresent() && sync.get().getSyncing().equals(SyncState.SYNCHRONIZING)){
				recId = sync.get().getRecAppID();
				synced = true;
			}
		}else{//don lam bu
			type = 1;
			recId = appId;
			sync = compLeaveRepo.findByRecID(appId);
			if(sync.isPresent() && sync.get().getSyncing().equals(SyncState.SYNCHRONIZING)){
				absId = sync.get().getAbsenceLeaveAppID();
				synced = true;
			}
		}
		return new AppCompltLeaveSyncOutput(absId, recId, synced, type);
	}

	@Override
	public MailResult sendMailApproverApprove(List<String> employeeIDList, Application application) {
		// ドメインモデル「申請メール設定」を取得する(get domain model 「」)
		AppEmailSet appEmailSet = appEmailSetRepository.findByDivision(Division.APPLICATION_APPROVAL);
		// アルゴリズム「承認者へ送る」を実行する(thực hiện thuật toán 「Gửi tới người phê duyệt」)
		MailResult mailResult = sendMailApprover(employeeIDList, application, appEmailSet.getEmailContentLst().get(0).getOpEmailText().map(x -> x.v()).orElse(""), "");
		return new MailResult(mailResult.getSuccessList(), mailResult.getFailList(), mailResult.getFailServerList());
	}
	@Override
	public MailResult sendMailApproverDelete(List<String> employeeIDList, Application application, String content) {
		String inputText = I18NText.getText("Msg_1262",Collections.emptyList());
		// アルゴリズム「承認者へ送る」を実行する (Thực hiện thuật toán "Gửi tới người phê duyệt")
		MailResult mailResult = sendMailApprover(employeeIDList, application, inputText, content);
		return new MailResult(mailResult.getSuccessList(), mailResult.getFailList(), mailResult.getFailServerList());
	}
	@Override
	public MailResult sendMailApplicantApprove(Application application) {
		String inputText = I18NText.getText("Msg_1263",Collections.emptyList());
		MailResult mailResult = sendMailApplicant(application, inputText);
		return new MailResult(mailResult.getSuccessList(), mailResult.getFailList(), mailResult.getFailServerList());
	}
	@Override
	public MailResult sendMailApplicantDeny(Application application) {
		String inputText = I18NText.getText("Msg_1264",Collections.emptyList());
		MailResult mailResult = sendMailApplicant(application, inputText);
		return new MailResult(mailResult.getSuccessList(), mailResult.getFailList(), mailResult.getFailServerList());
	}
	@Override
	public MailResult sendMailApprover(List<String> listDestination, Application application, String text, String content) {
		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();
		List<String> failServerList = new ArrayList<>();
		String sIDlogin = AppContexts.user().employeeId();
		String companyID = AppContexts.user().companyId();
		List<String> paramIDList = new ArrayList<>();
		paramIDList.addAll(listDestination);
		paramIDList.add(sIDlogin);
		// ログイン者のメールアドレスを取得する
		List<MailDestinationImport> mailResultList = envAdapter.getEmpEmailAddress(companyID, paramIDList, 6);
		String loginMail = mailResultList.stream().filter(x -> x.getEmployeeID().equals(sIDlogin)).findAny()
				.map(x -> {
					if(CollectionUtil.isEmpty(x.getOutGoingMails()) || x.getOutGoingMails().get(0)==null){
						return "";
					} else {
						return x.getOutGoingMails().get(0).getEmailAddress();
					}
				}).orElse("");
		// ログイン者の社員名を取得する
		String loginName = employeeAdaptor.getEmployeeName(sIDlogin);
		// 申請者の社員名を取得する
		String applicantName = employeeAdaptor.getEmployeeName(application.getEmployeeID());
		for(String employeeID : listDestination){
			String employeeName = employeeAdaptor.getEmployeeName(employeeID);
			// 対象者のメールアドレスを取得する
			String approverMail = mailResultList.stream().filter(x -> x.getEmployeeID().equals(employeeID)).findAny()
					.map(x -> {
						if(CollectionUtil.isEmpty(x.getOutGoingMails()) || x.getOutGoingMails().get(0)==null){
							return "";
						} else {
							return x.getOutGoingMails().get(0).getEmailAddress();
						}
					}).orElse("");
			if(Strings.isBlank(approverMail)){
				// エラーメッセージ Msg_768　対象者氏名　エラーリストにセットする
				failList.add(employeeName);
				continue;
			}
			String URL = "";
			// ドメインモデル「申請メール設定」を取得する
			AppEmailSet appEmailSet = appEmailSetRepository.findByCID(companyID);
			if(appEmailSet.getUrlReason() == NotUseAtr.USE){
				// 埋込URL情報登録申請(application đăng ký info URL nén)
				URL = registerEmbededURL.registerEmbeddedForApp(
						application.getAppID(),
						application.getAppType().value,
						application.getPrePostAtr().value,
						"",
						employeeID);
			};
			// メール送信時申請内容の作成
			String appContent = content; 
			if(Strings.isBlank(appContent)) {
				applicationContentService.getApplicationContent(application);
			} 
			// 申請を差し戻すメール本文の編集
			String newText = Strings.isNotBlank(URL) ? text + "\n" + URL : text;
			String appName = application.getAppType().name;
			String mailContentToSend = I18NText.getText("Msg_703",
					loginName,
					newText,
					application.getAppDate().getApplicationDate().toString(),
					appName,
					applicantName,
					application.getAppDate().getApplicationDate().toString(),
					appContent,
					loginName,
					loginMail);
			String mailTitle = application.getAppDate().getApplicationDate().toString()+"　"+appName;
			String mailBody = mailContentToSend;
			try {
				// メールを送信する(gửi mail)
				mailsender.sendFromAdmin(approverMail, new MailContents(mailTitle, mailBody));
				successList.add(employeeName);
			} catch (Exception e) {
				failServerList.add(employeeName);
			}
		}
		return new MailResult(successList, failList, failServerList);
	}
	@Override
	public MailResult sendMailApplicant(Application application, String text) {
		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();
		List<String> failServerList = new ArrayList<>();
		String sIDlogin = AppContexts.user().employeeId();
		String companyID = AppContexts.user().companyId();
		String employeeID = application.getEmployeeID();
		String employeeName = employeeAdaptor.getEmployeeName(employeeID);
		List<String> listDestination = new ArrayList<>(Arrays.asList(sIDlogin, employeeID));
		// ログイン者のメールアドレスを取得する
		List<MailDestinationImport> mailResultList = envAdapter.getEmpEmailAddress(companyID, listDestination, 6);
		String loginMail = mailResultList.stream().filter(x -> x.getEmployeeID().equals(sIDlogin)).findAny()
				.map(x -> {
					if(CollectionUtil.isEmpty(x.getOutGoingMails()) || x.getOutGoingMails().get(0)==null){
						return "";
					} else {
						return x.getOutGoingMails().get(0).getEmailAddress();
					}
				}).orElse("");
		// ログイン者の社員名を取得する
		String loginName = employeeAdaptor.getEmployeeName(sIDlogin);
		// 社員名を取得する
		String applicantName = employeeAdaptor.getEmployeeName(application.getEmployeeID());
		String applicantMail = mailResultList.stream().filter(x -> x.getEmployeeID().equals(employeeID)).findAny()
				.map(x -> {
					if(CollectionUtil.isEmpty(x.getOutGoingMails()) || x.getOutGoingMails().get(0)==null){
						return "";
					} else {
						return x.getOutGoingMails().get(0).getEmailAddress();
					}
				}).orElse("");
		if(Strings.isBlank(applicantMail)){
			// エラーメッセージ Msg_768　対象者氏名　エラーリストにセットする
			failList.add(employeeName);
			return new MailResult(successList, failList, failServerList);
		}
		String URL = "";
		// ドメインモデル「申請メール設定」を取得する
		AppEmailSet appEmailSet = appEmailSetRepository.findByCID(companyID);
		if(appEmailSet.getUrlReason() == NotUseAtr.USE){
			URL = registerEmbededURL.registerEmbeddedForApp(
					application.getAppID(),
					application.getAppType().value,
					application.getPrePostAtr().value,
					"",
					employeeID);
		};
		// メール送信時申請内容の作成
		String appContent = applicationContentService.getApplicationContent(application);
		// 申請を差し戻すメール本文の編集
		String newText = Strings.isNotBlank(URL) ? text + "\n" + URL : text;
		String appName = application.getAppType().name;
		String mailContentToSend = I18NText.getText("Msg_703",
				loginName,
				newText,
				application.getAppDate().getApplicationDate().toLocalDate().toString(),
				appName,
				applicantName,
				application.getAppDate().getApplicationDate().toLocalDate().toString(),
				appContent,
				loginName,
				loginMail);
		String mailTitle = application.getAppDate().getApplicationDate().toString()+"　"+appName;
		String mailBody = mailContentToSend;
		try {
			// メールを送信する
			mailsender.sendFromAdmin(applicantMail, new MailContents(mailTitle, mailBody));
			successList.add(employeeName);
		} catch (Exception e) {
			failServerList.add(employeeName);
		}
		return new MailResult(successList, failList, failServerList);
	}
	@Override
	public List<GeneralDate> lstDateIsHoliday(String sid, DatePeriod dates, List<ActualContentDisplay> actualContentDisplayLst) {
		// 日付一覧をクリアする（初期化）
		List<GeneralDate> lstOutput = new ArrayList<>();
		// INPUT．期間．開始日から期間．終了日までループする
		for(int i = 0; dates.start().daysTo(dates.end()) - i >= 0; i++){
			GeneralDate loopDate = dates.start().addDays(i);
			// INPUT．「表示する実績内容<List>」に処理中の年月日のデータがあるか確認する
			Optional<AchievementDetail> opAchievementDetail = Optional.empty();
			Optional<ActualContentDisplay> opActualContentDisplay = actualContentDisplayLst.stream().filter(x -> x.getDate().equals(loopDate)).findAny();
			if(opActualContentDisplay.isPresent()) {
				opAchievementDetail = opActualContentDisplay.get().getOpAchievementDetail();
			}
			if(!opAchievementDetail.isPresent()) {
				//実績の取得
				String companyID = AppContexts.user().companyId();
				ActualContentDisplay achInfor = collectAchievement.getAchievement(companyID, sid, loopDate);
				if(!achInfor.getOpAchievementDetail().isPresent()) {
					continue;
				}
				opAchievementDetail = achInfor.getOpAchievementDetail();
			}
			// 1日休日の判定
			if(opAchievementDetail.get().getWorkTypeCD() != null
					&& WorkTypeIsClosedService.checkHoliday(createM1(), opAchievementDetail.get().getWorkTypeCD()) //1日休日の判定
					) {
				// 日付一覧.Add(ループする日)
				lstOutput.add(loopDate);
			}
		}
		return lstOutput;
	}
	@Override
	public WorkType getWorkTypeScheduleSpec(String companyID, String employeeID, GeneralDate appDate) {
		// Imported(申請承認)「勤務実績」を取得する
		RecordWorkInfoImport recordWorkInfoImport = recordWorkInfoAdapter.getRecordWorkInfoRefactor(employeeID, appDate);
		if(recordWorkInfoImport.getWorkTypeCode() != null){
			String workTypeCd = recordWorkInfoImport.getWorkTimeCode().v();
			// ドメインモデル「勤務種類」を1件取得する
			Optional<WorkType> opWorkType = workTypeRepository.findByPK(companyID, workTypeCd);
			if(!opWorkType.isPresent()){
				return null;
			}
			WorkType workType = opWorkType.get();
			return workType;
		}
		// Imported(申請承認)「勤務予定」を取得する
		ScBasicScheduleImport opScBasicScheduleImport = scBasicScheduleAdapter.findByIDRefactor(employeeID, appDate);
		if(opScBasicScheduleImport != null){
			String workTypeCd = opScBasicScheduleImport.getWorkTypeCode();
			// ドメインモデル「勤務種類」を1件取得する
			Optional<WorkType> opWorkType = workTypeRepository.findByPK(companyID, workTypeCd);
			if(!opWorkType.isPresent()){
				return null;
			}
			WorkType workType = opWorkType.get();
			return workType;
		}
		return null;
	}




	@Override
	public boolean displayAppReasonContentFlg(AppDisplayAtr displayReasonFlg) {
		// Input．申請理由の表示区分をチェック
		if (displayReasonFlg == AppDisplayAtr.DISPLAY) {
			return true;
		}
		return false;
	}


	/**
	 * 12.マスタ勤務種類、就業時間帯データをチェック
	 * @param companyID
	 * @param wkTypeCode
	 * @param wkTimeCode
	 * @return
	 */
	@Override
	public CheckWorkingInfoResult checkWorkingInfo(String companyID, String wkTypeCode, String wkTimeCode) {
		CheckWorkingInfoResult result = new CheckWorkingInfoResult();


		// 「勤務種類CD ＝＝ Null」 をチェック
		boolean isWkTypeCDNotEmpty = !StringUtil.isNullOrEmpty(wkTypeCode, true);
		if (isWkTypeCDNotEmpty) {
			String WkTypeName = null;
			Optional<WorkType> wkTypeOpt = this.workTypeRepository.findNoAbolishByPK(companyID, wkTypeCode);
			if (wkTypeOpt.isPresent()) {
				WkTypeName = wkTypeOpt.get().getName().v();
			}
			// 「勤務種類名称を取得する」 ＝＝NULL をチェック
			boolean isWkTypeNameEmpty = StringUtil.isNullOrEmpty(WkTypeName, true);
			if (isWkTypeNameEmpty ) {
				// 勤務種類エラーFlg ＝ True
				result.setWkTypeError(true);
			}
		}
		// 「就業時間帯CD ＝＝ NULL」をチェック
		boolean isWkTimeCDNotEmpty = !StringUtil.isNullOrEmpty(wkTimeCode, true);
		if (isWkTimeCDNotEmpty) {
			// 「就業時間帯名称を取得する」＝＝ NULL をチェック
			String WkTimeName = null;
			Optional<WorkTimeSetting> wwktimeOpt = this.workTimeRepository.findByCodeAndAbolishCondition(companyID, wkTimeCode, AbolishAtr.NOT_ABOLISH);
			if (wwktimeOpt.isPresent()) {
				WkTimeName = wwktimeOpt.get().getWorkTimeDisplayName().getWorkTimeName().v();
			}
			boolean isWkTimeNameEmpty = StringUtil.isNullOrEmpty(WkTimeName, true);
			if (isWkTimeNameEmpty) {
				// 就業時間帯エラーFlg ＝ True
				result.setWkTimeError(true);
			}
		}


		return result;
	}

	private WorkTypeIsClosedService.RequireM1 createM1() {
		return new WorkTypeIsClosedService.RequireM1() {

			@Override
			public Optional<WorkType> workType(String companyId, String workTypeCd) {
				return workTypeRepository.findByPK(companyId, workTypeCd);
			}
		};
	}
	@Override
	public User userJudgment(String companyID, String appID, String employeeID) {
		// ドメインモデル「申請」を取得する (Lấy domain「申請」 )
		Application application = applicationRepository.findByID(appID).get();
		// アルゴリズム「指定した社員が承認者であるかの判断」を実行する
		Boolean isApprover = approvalRootStateAdapter.judgmentTargetPersonIsApprover(companyID, application.getAppID(), employeeID);
		if(isApprover){
			// ログイン者が申請本人かチェックする(Check xem login có phải là applicant không)
			if(application.getEmployeeID().equals(employeeID) || application.getEnteredPersonID().equals(employeeID)){
				// 利用者 = 申請本人&承認者
				return User.APPLICANT_APPROVER;
			} else {
				// 利用者 = 承認者
				return User.APPROVER;
			}
		} else {
			// ログイン者が申請本人かチェックする(Check xem login có phải là applicant không)
			if(application.getEmployeeID().equals(employeeID) || application.getEnteredPersonID().equals(employeeID)){
				// 利用者 = 申請本人
				return User.APPLICANT;
			} else {
				// 利用者 = その他
				return User.OTHER;
			}
		}
	}
}
