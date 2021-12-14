package nts.uk.ctx.at.request.dom.application.lateleaveearly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationApprovalService;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.init.DetailAppCommonSetService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.RegisterAtApproveReflectionInfoService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.after.NewAfterRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementEarly;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoNoDateOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.ArrivedLateLeaveEarlyInfoOutput;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.LateCancelation;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.LateEarlyDateChangeOutput;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.TimeReport;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author anhnm
 *
 */
@Stateless
public class LateLeaveEarlyServiceImp implements LateLeaveEarlyService {
	private final String DATE_FORMAT = "yyyy/MM/dd";

	@Inject
	private LateEarlyCancelAppSetRepository cancelAppSetRepository;

	@Inject
	private CommonAlgorithm common;

	@Inject
	private NewBeforeRegister newBeforeRegister;

	@Inject
	private RegisterAtApproveReflectionInfoService registerService;

	@Inject
	private ArrivedLateLeaveEarlyRepository lateEarlyRepository;

	@Inject
	private ApplicationApprovalService applicationService;

	@Inject
	private NewAfterRegister newAfterRegister;

	@Inject
	private DetailBeforeUpdate updateService;

	@Inject
	private DetailAfterUpdate afterUpdateService;

	@Inject
	private DetailAppCommonSetService detailAppCommonSetService;

	@Inject
	private ApplicationRepository applicationRepository;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.at.request.dom.application.lateleaveearly.LateLeaveEarlyService#
	 * getLateLeaveEarlyInfo(java.lang.String)
	 */
	@Override
	public ArrivedLateLeaveEarlyInfoOutput getLateLeaveEarlyInfo(int appId, List<String> appDates,
			AppDispInfoStartupOutput appDispInfoStartupOutput) {
		String companyId = AppContexts.user().companyId();


		List<String> sIds = new ArrayList<String>();
		sIds.add(AppContexts.user().employeeId());
		List<GeneralDate> appDatesLst = new ArrayList<GeneralDate>();

		if (!appDates.isEmpty()) {
			for (String appDate : appDates) {
				appDatesLst.add(GeneralDate.fromString(appDate, this.DATE_FORMAT));
			}
		}


		// 遅刻早退取消初期（新規）
		ArrivedLateLeaveEarlyInfoOutput displayInfo = this.initCancelLateEarlyApp(companyId, sIds, appDatesLst,
				appDispInfoStartupOutput);

		return displayInfo;
	}

	/**
	 * 遅刻早退取消初期（新規）
	 *
	 * @param companyId
	 * @param employmentLst
	 * @param appDates
	 * @param appDisplayInfo
	 * @return
	 */
	private ArrivedLateLeaveEarlyInfoOutput initCancelLateEarlyApp(String companyId, List<String> employmentLst,
			List<GeneralDate> appDates, AppDispInfoStartupOutput appDisplayInfo) {
		// 遅刻早退取消申請起動時の表示情報
		ArrivedLateLeaveEarlyInfoOutput displayInfo = new ArrivedLateLeaveEarlyInfoOutput();

		Optional<GeneralDate> appDate;
		Optional<AchievementEarly> achieveEarly = Optional.empty();

		// ドメインモデル「遅刻早退取消申請設定」を取得する
		LateEarlyCancelAppSet listAppSet = this.cancelAppSetRepository.getByCId(companyId);

		if (!appDisplayInfo.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().isPresent()) {
			achieveEarly = Optional.empty();
		} else {
			Optional<List<ActualContentDisplay>> actualContentDisplay = appDisplayInfo.getAppDispInfoWithDateOutput()
					.getOpActualContentDisplayLst();

			if (actualContentDisplay.isPresent() && !actualContentDisplay.get().isEmpty()
					&& !actualContentDisplay.get().isEmpty()) {

				Optional<AchievementDetail> achieve = actualContentDisplay.get().get(0).getOpAchievementDetail();
				if (achieve.isPresent()) {
					achieveEarly = Optional
							.of(actualContentDisplay.get().get(0).getOpAchievementDetail().get().getAchievementEarly());
				}
			}
		}

		if (appDates.isEmpty()) {
			appDate = Optional.empty();
		} else {
			appDate = Optional.of(appDates.get(0));
		}

		// 遅刻早退実績のチェック処理
		Optional<String> info = this.checkLateEarlyResult(achieveEarly, appDate);

		displayInfo.setAppDispInfoStartupOutput(appDisplayInfo);
		displayInfo.setLateEarlyCancelAppSet(listAppSet);
		displayInfo.setInfo(info);

		return displayInfo;
	}

	/**
	 * 遅刻早退実績のチェック処理
	 *
	 * @param lateEarlyActualResults
	 *            Optional
	 * @param appDate
	 *            Optional
	 * @return info Optional<String>
	 */
	private Optional<String> checkLateEarlyResult(Optional<AchievementEarly> lateEarlyActualResults,
			Optional<GeneralDate> appDate) {
		Optional<String> errorMsg = Optional.empty();

		if (appDate.isPresent()) {
			if (!lateEarlyActualResults.isPresent()) {
				errorMsg = Optional.of("Msg_1707");
			} else {
				if (lateEarlyActualResults.get().getScheAttendanceTime1() == null
						&& !lateEarlyActualResults.get().getScheAttendanceTime2().isPresent()
						&& lateEarlyActualResults.get().getScheDepartureTime1() == null
						&& !lateEarlyActualResults.get().getScheDepartureTime2().isPresent()) {
					errorMsg = Optional.of("Msg_1707");
				}
			}
		}

		return errorMsg;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.at.request.dom.application.lateleaveearly.LateLeaveEarlyService#
	 * getChangeAppDate(int, java.util.List, java.lang.String)
	 */
	@Override
	public LateEarlyDateChangeOutput getChangeAppDate(int appType, List<String> appDates, String baseDate,
			AppDispInfoNoDateOutput appDispNoDate, AppDispInfoWithDateOutput appDispWithDate,
			LateEarlyCancelAppSet listAppSet) {
		String companyId = AppContexts.user().companyId();
		List<GeneralDate> dateLst = new ArrayList<>();

		for (String date : appDates) {
			dateLst.add(GeneralDate.fromString(date, this.DATE_FORMAT));
		}
		ApplicationType applicationType = EnumAdaptor.valueOf(appType, ApplicationType.class);
		Optional<AchievementEarly> lateEarlyActualResults = Optional.empty();

		// 申請日を変更する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = this.common.changeAppDateProcess(companyId, dateLst,
				applicationType, appDispNoDate, appDispWithDate, Optional.empty());

		if (appDispInfoWithDateOutput.getOpActualContentDisplayLst().isPresent()
				&& appDispInfoWithDateOutput.getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail()
						.isPresent()
				&& appDispInfoWithDateOutput.getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail().get()
						.getAchievementEarly() != null) {
			lateEarlyActualResults = Optional.of(appDispInfoWithDateOutput.getOpActualContentDisplayLst().get().get(0)
					.getOpAchievementDetail().get().getAchievementEarly());
		}

		Optional<GeneralDate> appDate = Optional.empty();
		if (!appDates.isEmpty()) {
			appDate = Optional.of(GeneralDate.fromString(appDates.get(0), DATE_FORMAT));
		}

		// 遅刻早退実績のチェック処理
		Optional<String> errorInfo = this.checkLateEarlyResult(lateEarlyActualResults, appDate);

		LateEarlyDateChangeOutput output = new LateEarlyDateChangeOutput(appDispInfoWithDateOutput);
		output.setErrorInfo(errorInfo);

		return output;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.at.request.dom.application.lateleaveearly.LateLeaveEarlyService#
	 * getMessageList(int, java.util.List, java.lang.String,
	 * nts.uk.ctx.at.request.dom.application.common.service.setting.output.
	 * AppDispInfoNoDateOutput,
	 * nts.uk.ctx.at.request.dom.application.common.service.setting.output.
	 * AppDispInfoWithDateOutput)
	 */
	@Override
	public List<String> getMessageList(int appType, boolean agentAtr, boolean isNew,
			ArrivedLateLeaveEarlyInfoOutput infoOutput, Application application) {
		String companyID = AppContexts.user().companyId();

		List<ConfirmMsgOutput> listMsg = this.checkError(companyID, appType, agentAtr, isNew, infoOutput, application);

		return listMsg.stream().map(item -> item.getMsgID()).collect(Collectors.toList());
	}

	/**
	 * 共通登録前のエラーチェック処理
	 *
	 * @param appType
	 *            int
	 * @param agentAtr
	 *            boolean
	 * @param isNew
	 *            boolean
	 * @param infoOutput
	 *            ArrivedLateLeaveEarlyInfoOutput
	 * @param application
	 *            Application
	 * @return listError List<String>
	 */
	private List<ConfirmMsgOutput> checkError(String companyID, int appType, boolean agentAtr, boolean isNew,
			ArrivedLateLeaveEarlyInfoOutput infoOutput, Application application) {
		List<ConfirmMsgOutput> listMsg = new ArrayList<>();
		List<TimeReport> timeReportsTemp;
		List<LateCancelation> cancelTemp;
		Integer attendTime = null;
		Integer leaveTime = null;
		Integer attendTime2 = null;
		Integer leaveTime2 = null;
		LateCancelation cancelAttend = null;
		LateCancelation cancelLeave = null;
		LateCancelation cancelAttend2 = null;
		LateCancelation cancelLeave2 = null;



			// Get attend time by workno = 1 && classification = 0
			timeReportsTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateOrLeaveEarlies().stream().map(x -> {
				if (x.getWorkNo() == 1 && x.getLateOrEarlyClassification().value == 0) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

		attendTime = timeReportsTemp.isEmpty() ? null : timeReportsTemp.get(0).getTimeWithDayAttr().v();

			// Get leave time by workno = 1 && classification = 1
			timeReportsTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateOrLeaveEarlies().stream().map(x -> {
				if (x.getWorkNo() == 1 && x.getLateOrEarlyClassification().value == 1) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

		leaveTime = timeReportsTemp.isEmpty() ? null : timeReportsTemp.get(0).getTimeWithDayAttr().v();

			// Get attend time 2 by workno = 2 && classification = 0
			timeReportsTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateOrLeaveEarlies().stream().map(x -> {
				if (x.getWorkNo() == 2 && x.getLateOrEarlyClassification().value == 0) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

		attendTime2 = timeReportsTemp.isEmpty() ? null : timeReportsTemp.get(0).getTimeWithDayAttr().v();

			// Get attend time 2 by workno = 2 && classification = 1
			timeReportsTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateOrLeaveEarlies().stream().map(x -> {
			if (x.getWorkNo() == 2 && x.getLateOrEarlyClassification().value == 1) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

		leaveTime2 = timeReportsTemp.isEmpty() ? null : timeReportsTemp.get(0).getTimeWithDayAttr().v();

			// Get attend time by workno = 1 && classification = 0
			cancelTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateCancelation().stream().map(x -> {
				if (x.getWorkNo() == 1 && x.getLateOrEarlyClassification().value == 0) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

			cancelAttend = cancelTemp.isEmpty() ? null : cancelTemp.get(0);

			// Get attend time by workno = 1 && classification = 1
			cancelTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateCancelation().stream().map(x -> {
				if (x.getWorkNo() == 1 && x.getLateOrEarlyClassification().value == 1) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

			cancelLeave = cancelTemp.isEmpty() ? null : cancelTemp.get(0);

			// Get attend time by workno = 2 && classification = 0
			cancelTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateCancelation().stream().map(x -> {
				if (x.getWorkNo() == 2 && x.getLateOrEarlyClassification().value == 0) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

			cancelAttend2 = cancelTemp.isEmpty() ? null : cancelTemp.get(0);

			// Get attend time by workno = 2 && classification = 1
			cancelTemp = infoOutput.getArrivedLateLeaveEarly().get().getLateCancelation().stream().map(x -> {
				if (x.getWorkNo() == 2 && x.getLateOrEarlyClassification().value == 1) {
					return x;
				}
				return null;
			}).filter(m -> (m != null)).collect(Collectors.toList());

			cancelLeave = cancelTemp.isEmpty() ? null : cancelTemp.get(0);

		if (attendTime != null && leaveTime != null && attendTime > leaveTime) {
			throw new BusinessException("Msg_1677");
		}

		if (leaveTime != null && attendTime2 != null && leaveTime > attendTime2) {
			throw new BusinessException("Msg_1677");
		}

		if (attendTime2 != null && leaveTime2 != null && attendTime2 > leaveTime2) {
				throw new BusinessException("Msg_1677");
			}

		if (attendTime != null && leaveTime2 != null && attendTime > leaveTime2) {
			throw new BusinessException("Msg_1677");
		}

		if (leaveTime != null && leaveTime2 != null && leaveTime > leaveTime2) {
			throw new BusinessException("Msg_1677");
		}

		if (attendTime != null && attendTime2 != null && attendTime > attendTime2) {
			throw new BusinessException("Msg_1677");
		}

		if (application.getPrePostAtr().value == 0) {
			if (attendTime == null && attendTime2 == null && leaveTime == null && leaveTime2 == null) {
				throw new BusinessException("Msg_1681");
			}
		} else {
			if (attendTime == null && attendTime2 == null && leaveTime == null && leaveTime2 == null
					&& cancelAttend == null
					&& cancelAttend2 == null && cancelLeave == null && cancelLeave2 == null) {
				throw new BusinessException("Msg_1681");
			}
			}


		if (isNew) {
			// 2-1.新規画面登録前の処理
			listMsg = this.newBeforeRegister.processBeforeRegister_New(
			        companyID, 
			        EmploymentRootAtr.APPLICATION,
					agentAtr, 
					application, 
					null,
					infoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
					null,
					infoOutput.getAppDispInfoStartupOutput(), 
					new ArrayList<String>(), 
					Optional.empty(),
					Optional.empty(), 
					false);
		} else {
			// 4-1.詳細画面登録前の処理
			this.updateService.processBeforeDetailScreenRegistration(companyID, application.getEmployeeID(),
					application.getAppDate().getApplicationDate(), EmploymentRootAtr.APPLICATION.value,
					application.getAppID(), application.getPrePostAtr(), application.getVersion(), null, null,
					infoOutput.getAppDispInfoStartupOutput(), new ArrayList<String>(), Optional.empty(), false, 
					Optional.empty(), Optional.empty());
		}

		return listMsg;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.at.request.dom.application.lateleaveearly.LateLeaveEarlyRepository
	 * #register(int, boolean, boolean,
	 * nts.uk.ctx.at.request.dom.application.lateorleaveearly.
	 * ArrivedLateLeaveEarlyInfoOutput,
	 * nts.uk.ctx.at.request.dom.application.Application)
	 */
	@Override
	public ProcessResult register(int appType, ArrivedLateLeaveEarlyInfoOutput infoOutput, Application application) {
		String employeeId = AppContexts.user().employeeId();
		ProcessResult processResult = new ProcessResult();
		processResult.setProcessDone(true);
		processResult.setAppIDLst(Arrays.asList(application.getAppID()));

		// ドメインモデル「遅刻早退取消申請」の新規登録する (đăng ký mới domain 「遅刻早退取消申請」)
		this.registerDomain(application, infoOutput);


		// 2-2.新規画面登録時承認反映情報の整理
		String reflectAppId = this.registerService.newScreenRegisterAtApproveInfoReflect(employeeId, application);

		// 2-3.新規画面登録後の処理
		AppTypeSetting appTypeSetting = infoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getApplicationSetting()
				.getAppTypeSettings().stream().filter(x -> x.getAppType()==application.getAppType()).findAny().orElse(null);
		processResult = this.newAfterRegister.processAfterRegister(Arrays.asList(application.getAppID()),
				appTypeSetting,
				infoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().isMailServerSet(),
				false);
		if(Strings.isNotBlank(reflectAppId)) {
			processResult.setReflectAppIdLst(Arrays.asList(reflectAppId));
		}
		return processResult;
	}

	/**
	 * ドメインモデル「遅刻早退取消申請」の新規登録する (đăng ký mới domain 「遅刻早退取消申請」)
	 *
	 * @param application
	 * @param infoOutput
	 */
	public void registerDomain(Application application, ArrivedLateLeaveEarlyInfoOutput infoOutput) {
		String cID = AppContexts.user().companyId();
		lateEarlyRepository.registerLateLeaveEarly(cID, application, infoOutput);
		this.registerApplication(application, infoOutput);
	}

	/**
	 * @param cID
	 * @param application
	 * @param infoOutput
	 */
	public void registerApplication(Application application, ArrivedLateLeaveEarlyInfoOutput infoOutput) {
		Optional<List<ApprovalPhaseStateImport_New>> opListApproval = infoOutput.getAppDispInfoStartupOutput()
				.getAppDispInfoWithDateOutput().getOpListApprovalPhaseState();

		this.applicationService.insertApp(application, opListApproval.get());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.at.request.dom.application.lateleaveearly.LateLeaveEarlyService#
	 * getInitB(java.lang.String)
	 */
	@Override
	public ArrivedLateLeaveEarlyInfoOutput getInitB(String appId, AppDispInfoStartupOutput infoStartupOutput) {

		String companyId = AppContexts.user().companyId();

		// 14-1.詳細画面起動前申請共通設定を取得する
		AppDispInfoStartupOutput appDispInfoStartupOutput = this.detailAppCommonSetService
				.getCommonSetBeforeDetail(companyId, appId);

		// 遅刻早退取消初期（詳細）
		ArrivedLateLeaveEarlyInfoOutput info = this.initLateEarlyDetail(companyId, appId, appDispInfoStartupOutput);

		return info;
	}

	/**
	 * 遅刻早退取消初期（詳細）
	 *
	 * @param companyId
	 * @param appid
	 * @param infoStartupOutput
	 * @return
	 */
	private ArrivedLateLeaveEarlyInfoOutput initLateEarlyDetail(String companyId, String appid,
			AppDispInfoStartupOutput infoStartupOutput) {
		ArrivedLateLeaveEarlyInfoOutput output = new ArrivedLateLeaveEarlyInfoOutput();
		Application application = infoStartupOutput.getAppDetailScreenInfo().get().getApplication();

		// ドメインモデル「遅刻早退取消申請」を取得する
		// (Lấy domain 「遅刻早退取消申請」)
		ArrivedLateLeaveEarly arrivedLateLeaveEarly = this.lateEarlyRepository.getLateEarlyApp(companyId, appid,
				application);

		// ドメインモデル「遅刻早退取消申請設定」を取得する
		// (Lấy domain 「遅刻早退取消申請設定」)
		LateEarlyCancelAppSet lateEarlyCancelAppSet = this.cancelAppSetRepository.getByCId(companyId);

		output.setArrivedLateLeaveEarly(Optional.of(arrivedLateLeaveEarly));
		output.setLateEarlyCancelAppSet(lateEarlyCancelAppSet);
		output.setAppDispInfoStartupOutput(infoStartupOutput);

		return output;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.at.request.dom.application.lateleaveearly.LateLeaveEarlyService#
	 * update(java.lang.String, nts.uk.ctx.at.request.dom.application.Application,
	 * nts.uk.ctx.at.request.dom.application.lateorleaveearly.ArrivedLateLeaveEarly)
	 */
	@Override
	public ProcessResult update(String companyId, Application application,
			ArrivedLateLeaveEarly arrivedLateLeaveEarly, AppDispInfoStartupOutput infoStartupOutput) {
		this.updateDomain(application, arrivedLateLeaveEarly);

		// 4-2.詳細画面登録後の処理
		ProcessResult result = this.afterUpdateService.processAfterDetailScreenRegistration(companyId,
				application.getAppID(), infoStartupOutput);
		return result;
	}

	/**
	 * ドメインモデル「遅刻早退取消申請」の更新する (Update domain 「遅刻早退取消申請」)
	 *
	 * @param application
	 * @param arrivedLateLeaveEarly
	 */
	private void updateDomain(Application application, ArrivedLateLeaveEarly arrivedLateLeaveEarly) {
		String companyId = AppContexts.user().companyId();

		this.lateEarlyRepository.updateLateLeaveEarly(companyId, application, arrivedLateLeaveEarly);

		this.applicationRepository.update(application);
	}
}
