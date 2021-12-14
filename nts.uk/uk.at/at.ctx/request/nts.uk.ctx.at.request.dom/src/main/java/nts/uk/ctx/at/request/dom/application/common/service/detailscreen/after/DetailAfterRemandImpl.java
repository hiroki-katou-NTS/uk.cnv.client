package nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after;

import java.util.ArrayList;
import java.util.Arrays;
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
import nts.gul.mail.send.MailContents;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonForReversion;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.ReflectionStatusOfDay;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.EnvAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.dto.MailDestinationImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.dto.OutGoingMailImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.ApprovalRootStateAdapter;
import nts.uk.ctx.at.request.dom.application.common.service.application.IApplicationContentService;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.MailSenderResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.MailResult;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSet;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.emailset.Division;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.mail.MailSender;
import nts.uk.shr.com.url.RegisterEmbededURL;

/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class DetailAfterRemandImpl implements DetailAfterRemand {

	@Inject
	private ApplicationRepository applicationRepository;

	@Inject
	private ApprovalRootStateAdapter approvalRootStateAdapter;

	@Inject
	private EmployeeRequestAdapter employeeAdapter;

	@Inject
	private MailSender mailsender;

	@Inject
	private RegisterEmbededURL registerEmbededURL;
	
	@Inject
	private EnvAdapter envAdapter;
	
	@Inject
	private ApplicationSettingRepository applicationSettingRepository;
	
	@Inject
	private AppEmailSetRepository appEmailSetRepository;
	
	@Inject
	private IApplicationContentService applicationContentService;
	
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	/**
	 * 11-2.詳細画面差し戻し後の処理
	 */
	@Override
	public MailResult doRemand(String companyID, RemandCommand remandCm) {
		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();
		List<String> failServerList = new ArrayList<>();
		boolean isSendMail = true;
		for (String appID : remandCm.getAppID()) {
			Application application = applicationRepository.findByID(companyID, appID).get();
			//ドメインモデル「申請」の差し戻し理由を画面のコメントで更新する-(update lý do trả về của domain 「申請」 bằng comment trên màn hình)
			//Bug#101502
			//差し戻しの日付(Short_YMD) + 全角スペース +差し戻しを行った承認者の名前 + "⇒" + 差し戻し先 + "：" + 改行 + 差し戻しコメント(C1-5)
			Integer order = remandCm.getOrder();
			String destination = order != null && order > 0 ? "フェーズ" + order + "の承認者" : remandCm.getApplicaintName();
			//差し戻しを行った承認者の名前
			String reSname = employeeAdapter.getEmployeeName(AppContexts.user().employeeId());
			String remandReason = GeneralDate.today().toString() + "　" + reSname + "⇒" + destination + "：" + "\n" + remandCm.getRemandReason();
			application.setOpReversionReason(Optional.of(new ReasonForReversion(remandReason)));
			//UPDATE ドメインモデル「申請」
			applicationRepository.update(application);
			Optional<AppTypeSetting> opAppTypeSetting = Optional.empty();
			Optional<ApplicationSetting> opApplicationSetting = applicationSettingRepository.findByCompanyId(companyID);
			if(opApplicationSetting.isPresent()) {
				opAppTypeSetting = opApplicationSetting.get().getAppTypeSettings().stream()
						.filter(x -> x.getAppType()==application.getAppType()).findAny();
			}
			MailResult mailResult = new MailResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
			if (order != null) {// 差し戻し先が承認者の場合
				//Imported（承認申請）「差し戻しする（承認者まで）」-(Imported（approvalApplication）「trả về（đến  approver）」)
				//RequestList482
				List<String> employeeList = approvalRootStateAdapter.doRemandForApprover(companyID, appID, order);
				//承認処理時に自動でメールを送信するが　trueの場合(check sendMailWhenApprove trong domain Applicationsetting)
				if(opAppTypeSetting.map(x -> x.isSendMailWhenApproval()).orElse(false)) {
					//「申請種類別設定」．新規登録時に自動でメールを送信するがtrue
					mailResult = this.getMailSenderResult(application, employeeList, remandReason, isSendMail);
				}
			} else {// 差し戻し先が申請本人の場合
				//Imported（承認申請）「差し戻しする（本人まで）」-(Trả về bản thân người làm đơn)
				//RequestList No.480
				approvalRootStateAdapter.doRemandForApplicant(companyID, appID);
				//Imported（承認申請）「差し戻し反映情報更新」-(Update thông tin phản ánh trả về) - RequestList No.481
				//「反映情報」．実績反映状態を「差し戻し」にする
				for(ReflectionStatusOfDay reflectionStatusOfDay : application.getReflectionStatus().getListReflectionStatusOfDay()) {
					reflectionStatusOfDay.setActualReflectStatus(ReflectedState.REMAND);
				}
				//「反映情報」．予定反映状態を「差し戻し」にする
				for(ReflectionStatusOfDay reflectionStatusOfDay : application.getReflectionStatus().getListReflectionStatusOfDay()) {
					reflectionStatusOfDay.setScheReflectStatus(ReflectedState.REMAND);
				}
				//UPDATE ドメインモデル「申請」
				applicationRepository.update(application);
				// 暫定データの登録
				List<GeneralDate> dateLst = new ArrayList<>();
				GeneralDate startDate = application.getOpAppStartDate().map(x -> x.getApplicationDate()).orElse(application.getAppDate().getApplicationDate());
				GeneralDate endDate = application.getOpAppEndDate().map(x -> x.getApplicationDate()).orElse(application.getAppDate().getApplicationDate());
				dateLst = new DatePeriod(startDate, endDate).datesBetween();
				interimRemainDataMngRegisterDateChange.registerDateChange(companyID, application.getEmployeeID(), dateLst);
				//ドメインモデル「申請種類別設定」．承認処理時に自動でメールを送信するをチェックする-(Check 「申請種類別設定」．Tự động gửi mail khi approve)
				if(opAppTypeSetting.map(x -> x.isSendMailWhenApproval()).orElse(false)) {
					//申請者本人にメール送信する-(Send mail đến bản thân người làm đơn)
					mailResult = this.getMailSenderResult(application, Arrays.asList(application.getEmployeeID()), remandReason, isSendMail);
				}
			}
			successList.addAll(mailResult.getSuccessList().stream().distinct().collect(Collectors.toList()));
			failList.addAll(mailResult.getFailList().stream().distinct().collect(Collectors.toList()));
			failServerList.addAll(mailResult.getFailServerList().stream().distinct().collect(Collectors.toList()));
			isSendMail = false;
		}
		return new MailResult(successList, failList, failServerList);
	}

	@Override
	public MailResult getMailSenderResult(Application application, List<String> employeeList, String returnReason, boolean isSendMail) {
		//doi ung kaf011 - tranh spam mail
		if(!isSendMail){
			return new MailResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}
		String applicantID = application.getEmployeeID();
		String mailTitle = "";
		String mailBody = "";
		String cid = AppContexts.user().companyId();
		String sidLogin = AppContexts.user().employeeId();
		//アルゴリズム「申請理由出力_共通」を実行する -> xu ly trong ham get content
		String appContent = applicationContentService.getApplicationContent(application);
		// String appContent = appContentService.getApplicationContent(application);
		AppEmailSet appEmailSet = appEmailSetRepository.findByDivision(Division.REMAND);
		mailTitle = appEmailSet.getEmailContentLst().stream().findFirst().map(x -> x.getOpEmailSubject().map(y -> y.v()).orElse("")).orElse("");
		mailBody = appEmailSet.getEmailContentLst().stream().findFirst().map(x -> x.getOpEmailText().map(y -> y.v()).orElse("")).orElse("");
//		Optional<UrlEmbedded> urlEmbedded = urlEmbeddedRepo.getUrlEmbeddedById(AppContexts.user().companyId());
		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();
		List<String> failServerList = new ArrayList<>();
		// Using RQL 419 instead (1 not have mail)
		//get list mail by list sID
		List<MailDestinationImport> lstMail = envAdapter.getEmpEmailAddress(cid, employeeList, 6);
//		Optional<AppDispName> appDispName = repoAppDispName.getDisplay(application.getAppType().value);
		String appName = application.getAppType().name;
//		if(appDispName.isPresent()){
//			appName = appDispName.get().getDispName().v();
//		}
		//get mail login
		List<MailDestinationImport> lstMailLogin = envAdapter.getEmpEmailAddress(cid, Arrays.asList(sidLogin), 6);
		List<OutGoingMailImport> mailLogin = lstMailLogin.get(0).getOutGoingMails();
		String loginMail = mailLogin.isEmpty() ||  mailLogin.get(0) == null || mailLogin.get(0).getEmailAddress() == null ? "" :
					mailLogin.get(0).getEmailAddress();
		//get name login
		String nameLogin = employeeAdapter.getEmployeeName(sidLogin);
		for (String employee : employeeList) {
			String employeeName = employeeAdapter.getEmployeeName(employee);
			OutGoingMailImport mail = envAdapter.findMailBySid(lstMail, employee);
			String employeeMail = mail == null || mail.getEmailAddress() == null ? "" : mail.getEmailAddress();
			// アルゴリズム「差し戻しメール埋込URL取得」を実行する
			String urlFull = this.getRemandEmailEmbeddedURL(
					application.getAppID(), 
					application.getAppType(), 
					application.getPrePostAtr(), 
					employee, 
					appEmailSet.getUrlReason()==NotUseAtr.USE);
			String mailContentToSend = I18NText.getText("Msg_1060",
					//｛0｝氏名 - ログイン者
					nameLogin,
					//｛1｝メール本文 - 差し戻しメールテンプレート
					mailBody,
					//｛2｝システム日付
					GeneralDate.today().toString(),
					//｛3｝申請種類（名称） - 申請
					appName,
					//｛4｝申請者の氏名 - 申請
					employeeAdapter.getEmployeeName(applicantID),
					//｛5｝申請日付 - 申請
					application.getAppDate().getApplicationDate().toString(),
					//｛6｝申請内容 - 申請
					appContent + urlFull,
					//｛7｝氏名 - ログイン者
					nameLogin,
					//｛8｝メールアドレス - ログイン者
					loginMail,
					//{9}差し戻し理由 //ver2
					returnReason);
			if (Strings.isBlank(employeeMail)) {
				failList.add(employeeName);
				continue;
			} else {
				try {
					mailsender.sendFromAdmin(employeeMail, new MailContents(mailTitle, mailContentToSend));
					successList.add(employeeName);
				} catch (Exception ex) {
					failServerList.add(employeeName);
				}
			}
		}
		return new MailResult(successList, failList, failServerList);
	}
	
	private String getRemandEmailEmbeddedURL(String appID, ApplicationType appType, PrePostAtr prePostAtr,
			String employeeID, boolean urlInclude) {
		// ドメインモデル「申請メール設定」を取得する
		if(!urlInclude) {
			return "";
		}
		// アルゴリズム「埋込URL情報登録申請」を実行する
		String urlInfo = registerEmbededURL.registerEmbeddedForApp(appID, appType.value, prePostAtr.value, "", employeeID);
		// 埋込用URLが作成された場合
		if(Strings.isBlank(urlInfo)) {
			return "";
		}
		// 本文追加用URLを作成する
		return "\n" + I18NText.getText("KDL034_17") + "\n" + urlInfo;
	}
}
