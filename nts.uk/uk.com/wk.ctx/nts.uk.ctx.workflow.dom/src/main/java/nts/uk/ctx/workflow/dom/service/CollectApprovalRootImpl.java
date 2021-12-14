package nts.uk.ctx.workflow.dom.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.workflow.dom.adapter.bs.EmployeeAdapter;
import nts.uk.ctx.workflow.dom.adapter.bs.SyJobTitleAdapter;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.ConcurrentEmployeeImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.EmpInfoImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.JobTitleImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.SimpleJobTitleImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.StatusOfEmployment;
import nts.uk.ctx.workflow.dom.adapter.workplace.WorkplaceApproverAdapter;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.ApprovalSettingRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.ApproverRegisterSet;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.JobAssignSetting;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.JobAssignSettingRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.PrincipalApprovalFlg;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.UseClassification;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalForm;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhase;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhaseRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.Approver;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.CompanyApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.CompanyApprovalRootRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.EmploymentRootAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRootRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.SystemAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRootRepository;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalBehaviorAtr;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalComment;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalFrame;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalPhaseState;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalRootState;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApproverInfor;
import nts.uk.ctx.workflow.dom.approverstatemanagement.RootType;
import nts.uk.ctx.workflow.dom.service.output.ApprovalRepresenterOutput;
import nts.uk.ctx.workflow.dom.service.output.ApprovalRootContentOutput;
import nts.uk.ctx.workflow.dom.service.output.ErrorFlag;
import nts.uk.ctx.workflow.dom.service.output.LevelOutput;
import nts.uk.ctx.workflow.dom.service.output.LevelOutput.LevelInforOutput;
import nts.uk.ctx.workflow.dom.service.output.LevelOutput.LevelInforOutput.LevelApproverList;
import nts.uk.ctx.workflow.dom.service.output.LevelOutput.LevelInforOutput.LevelApproverList.LevelApproverInfo;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@RequestScoped
public class CollectApprovalRootImpl implements CollectApprovalRootService {
	
	@Inject
	private PersonApprovalRootRepository perApprovalRootRepository;
	
	@Inject
	private EmployeeAdapter employeeAdapter;
	
	@Inject
	private JobAssignSettingRepository jobAssignSetRepository;
	
	@Inject
	private WorkplaceApproverAdapter wkApproverAdapter;
	
	@Inject
	private SyJobTitleAdapter syJobTitleAdapter;
	
	@Inject
	private WorkplaceApprovalRootRepository wkpApprovalRootRepository;
	
	@Inject
	private CompanyApprovalRootRepository comApprovalRootRepository;
	
	@Inject
	private ApprovalPhaseRepository approvalPhaseRepository;
	
	@Inject
	private CollectApprovalAgentInforService collectApprovalAgentInforService;
	
	@Inject
	private ApprovalSettingRepository approvalSettingRepository;
	
	@Inject
	private ApprovalSettingService approvalSettingService;
	
	@Override
	public ApprovalRootContentOutput getApprovalRootOfSubjectRequest(String companyID, String employeeID, EmploymentRootAtr rootAtr, 
			String targetType, GeneralDate standardDate, SystemAtr sysAtr, Optional<Boolean> lowerApprove) {
		// 承認単位の利用設定を取得する
		ApproverRegisterSet setting = approvalSettingService.getSettingUseUnit(companyID, sysAtr.value);
		if (setting.getEmployeeUnit() == UseClassification.DO_USE) {
			// ドメインモデル「個人別承認ルート」を取得する (Get domain "Individual approval route/Lộ trình phê duyệt cá nhân")
			Optional<PersonApprovalRoot> opPerAppRoot = perApprovalRootRepository.findByBaseDate(companyID, employeeID, standardDate, rootAtr, targetType, sysAtr.value);
			if(opPerAppRoot.isPresent()){
				List<ApprovalPhase> listApprovalPhaseBefore = approvalPhaseRepository.getAllIncludeApprovers(opPerAppRoot.get().getApprovalId());
				// 共通アルゴリズム「承認ルートを整理する」を実行する (Thực hiện thuật toán “điều chinhr/organize Approve route" )
				LevelOutput levelOutput = this.organizeApprovalRoute(companyID, employeeID, standardDate, listApprovalPhaseBefore, sysAtr, lowerApprove);
				// 共通アルゴリズム「承認ルートの異常チェック」を実行する (Thực hiện common algorithm "Check Approval Route bất thường"
				ErrorFlag errorFlag = this.checkApprovalRoot(levelOutput);
				levelOutput.setErrorFlag(errorFlag.value);
				ApprovalRootState approvalRootState = null;
				if(rootAtr==EmploymentRootAtr.CONFIRMATION) {
					approvalRootState = this.createFromApprovalPhaseListConfirm(companyID, standardDate, levelOutput);
					if(errorFlag == ErrorFlag.NO_ERROR){
						String appID = IdentifierUtil.randomUniqueId();
						approvalRootState = ApprovalRootState.createFromFirst(
								companyID,
								appID,  
								EnumAdaptor.valueOf(Integer.valueOf(targetType)+1, RootType.class),
								standardDate, 
								employeeID, 
								approvalRootState);
					}
				} else {
					approvalRootState = this.createFromApprovalPhaseList(companyID, standardDate, levelOutput);
					approvalRootState.setRootType(RootType.EMPLOYMENT_APPLICATION);
				}
				return new ApprovalRootContentOutput(approvalRootState, errorFlag);
			}
			// ドメインモデル「個人別承認ルート」を取得する(Get domain "Individual approval route/Lộ trình phê duyệt cá nhân")
			Optional<PersonApprovalRoot> opPerAppRootsOfCommon = perApprovalRootRepository.findByBaseDateOfCommon(companyID, employeeID, standardDate, sysAtr.value);
			if(opPerAppRootsOfCommon.isPresent()){
				List<ApprovalPhase> listApprovalPhaseBefore = approvalPhaseRepository.getAllIncludeApprovers(opPerAppRootsOfCommon.get().getApprovalId());
				// 共通アルゴリズム「承認ルートを整理する」を実行する (Thực hiện thuật toán “điều chinhr/organize Approve route" )
				LevelOutput levelOutput = this.organizeApprovalRoute(companyID, employeeID, standardDate, listApprovalPhaseBefore, sysAtr, lowerApprove);
				// 共通アルゴリズム「承認ルートの異常チェック」を実行する (Thực hiện common algorithm "Check Approval Route bất thường"
				ErrorFlag errorFlag = this.checkApprovalRoot(levelOutput);
				levelOutput.setErrorFlag(errorFlag.value);
				ApprovalRootState approvalRootState = null;
				if(rootAtr==EmploymentRootAtr.CONFIRMATION) {
					approvalRootState = this.createFromApprovalPhaseListConfirm(companyID, standardDate, levelOutput);
					if(errorFlag == ErrorFlag.NO_ERROR){
						String appID = IdentifierUtil.randomUniqueId();
						approvalRootState = ApprovalRootState.createFromFirst(
								companyID,
								appID,  
								EnumAdaptor.valueOf(Integer.valueOf(targetType)+1, RootType.class),
								standardDate, 
								employeeID, 
								approvalRootState);
					}
				} else {
					approvalRootState = this.createFromApprovalPhaseList(companyID, standardDate, levelOutput);
					approvalRootState.setRootType(RootType.EMPLOYMENT_APPLICATION);
				}
				return new ApprovalRootContentOutput(approvalRootState, errorFlag);
			}
			
		}
		if (setting.getWorkplaceUnit() == UseClassification.DO_USE) {
			// 対象者の所属職場・部門を含める上位職場・部門を取得する
			List<String> upperIDIncludeList = this.getUpperIDIncludeSelf(companyID, employeeID, standardDate, sysAtr);
			for (String paramID : upperIDIncludeList) {
				// ドメインモデル「職場別承認ルート」を取得する(lấy dữ liệu domain「職場別就業承認ルート」)
				Optional<WorkplaceApprovalRoot> opWkpAppRoot = wkpApprovalRootRepository.findByBaseDate(companyID, paramID, standardDate, rootAtr, targetType, sysAtr.value);
				if(opWkpAppRoot.isPresent()){
					List<ApprovalPhase> listApprovalPhaseBefore = approvalPhaseRepository.getAllIncludeApprovers(opWkpAppRoot.get().getApprovalId());
					// 共通アルゴリズム「承認ルートを整理する」を実行する (Thực hiện thuật toán “điều chinhr/organize Approve route" )
					LevelOutput levelOutput = this.organizeApprovalRoute(companyID, employeeID, standardDate, listApprovalPhaseBefore, sysAtr, lowerApprove);
					// 共通アルゴリズム「承認ルートの異常チェック」を実行する (Thực hiện common algorithm "Check Approval Route bất thường"
					ErrorFlag errorFlag = this.checkApprovalRoot(levelOutput);
					levelOutput.setErrorFlag(errorFlag.value);
					ApprovalRootState approvalRootState = null;
					if(rootAtr==EmploymentRootAtr.CONFIRMATION) {
						approvalRootState = this.createFromApprovalPhaseListConfirm(companyID, standardDate, levelOutput);
						if(errorFlag == ErrorFlag.NO_ERROR){
							String appID = IdentifierUtil.randomUniqueId();
							approvalRootState = ApprovalRootState.createFromFirst(
									companyID,
									appID,  
									EnumAdaptor.valueOf(Integer.valueOf(targetType)+1, RootType.class),
									standardDate, 
									employeeID, 
									approvalRootState);
						}
					} else {
						approvalRootState = this.createFromApprovalPhaseList(companyID, standardDate, levelOutput);
						approvalRootState.setRootType(RootType.EMPLOYMENT_APPLICATION);
					}
					return new ApprovalRootContentOutput(approvalRootState, errorFlag);
				}
				// ドメインモデル「職場別承認ルート」を取得する (Get domain "Approval Route workPlace ")
				Optional<WorkplaceApprovalRoot> opWkpAppRootsOfCom = wkpApprovalRootRepository.findByBaseDateOfCommon(companyID, paramID, standardDate, sysAtr.value);
				if(opWkpAppRootsOfCom.isPresent()){
					List<ApprovalPhase> listApprovalPhaseBefore = approvalPhaseRepository.getAllIncludeApprovers(opWkpAppRootsOfCom.get().getApprovalId());
					// 共通アルゴリズム「承認ルートを整理する」を実行する (Thực hiện thuật toán “điều chinhr/organize Approve route" )
					LevelOutput levelOutput = this.organizeApprovalRoute(companyID, employeeID, standardDate, listApprovalPhaseBefore, sysAtr, lowerApprove);
					// 共通アルゴリズム「承認ルートの異常チェック」を実行する (Thực hiện common algorithm "Check Approval Route bất thường"
					ErrorFlag errorFlag = this.checkApprovalRoot(levelOutput);
					levelOutput.setErrorFlag(errorFlag.value);
					ApprovalRootState approvalRootState = null;
					if(rootAtr==EmploymentRootAtr.CONFIRMATION) {
						approvalRootState = this.createFromApprovalPhaseListConfirm(companyID, standardDate, levelOutput);
						if(errorFlag == ErrorFlag.NO_ERROR){
							String appID = IdentifierUtil.randomUniqueId();
							approvalRootState = ApprovalRootState.createFromFirst(
									companyID,
									appID,  
									EnumAdaptor.valueOf(Integer.valueOf(targetType)+1, RootType.class),
									standardDate, 
									employeeID, 
									approvalRootState);
						}
					} else {
						approvalRootState = this.createFromApprovalPhaseList(companyID, standardDate, levelOutput);
						approvalRootState.setRootType(RootType.EMPLOYMENT_APPLICATION);
					}
					return new ApprovalRootContentOutput(approvalRootState, errorFlag);
				}
			}
			
		}
		if (setting.getCompanyUnit() == UseClassification.DO_USE) {
			// ドメインモデル「会社別承認ルート」を取得する(Get domain model "Approval Route by Company")
			Optional<CompanyApprovalRoot> opComAppRoot = comApprovalRootRepository.findByBaseDate(companyID, standardDate, rootAtr, targetType, sysAtr.value);
			if(opComAppRoot.isPresent()){
				List<ApprovalPhase> listApprovalPhaseBefore = approvalPhaseRepository.getAllIncludeApprovers(opComAppRoot.get().getApprovalId());
				// 共通アルゴリズム「承認ルートを整理する」を実行する (Thực hiện thuật toán “điều chinhr/organize Approve route" )
				LevelOutput levelOutput = this.organizeApprovalRoute(companyID, employeeID, standardDate, listApprovalPhaseBefore, sysAtr, lowerApprove);
				// 共通アルゴリズム「承認ルートの異常チェック」を実行する (Thực hiện common algorithm "Check Approval Route bất thường"
				ErrorFlag errorFlag = this.checkApprovalRoot(levelOutput);
				levelOutput.setErrorFlag(errorFlag.value);
				ApprovalRootState approvalRootState = null;
				if(rootAtr==EmploymentRootAtr.CONFIRMATION) {
					approvalRootState = this.createFromApprovalPhaseListConfirm(companyID, standardDate, levelOutput);
					if(errorFlag == ErrorFlag.NO_ERROR){
						String appID = IdentifierUtil.randomUniqueId();
						approvalRootState = ApprovalRootState.createFromFirst(
								companyID,
								appID,  
								EnumAdaptor.valueOf(Integer.valueOf(targetType)+1, RootType.class),
								standardDate, 
								employeeID, 
								approvalRootState);
					}
				} else {
					approvalRootState = this.createFromApprovalPhaseList(companyID, standardDate, levelOutput);
					approvalRootState.setRootType(RootType.EMPLOYMENT_APPLICATION);
				}
				return new ApprovalRootContentOutput(approvalRootState, errorFlag);
			}
			// ドメインモデル「会社別承認ルート」を取得する(Get domain model "Approval Route by Company")
			Optional<CompanyApprovalRoot> opCompanyAppRootsOfCom = comApprovalRootRepository.findByBaseDateOfCommon(companyID, standardDate, sysAtr.value);
			if(opCompanyAppRootsOfCom.isPresent()){
				List<ApprovalPhase> listApprovalPhaseBefore = approvalPhaseRepository.getAllIncludeApprovers(opCompanyAppRootsOfCom.get().getApprovalId());
				// 共通アルゴリズム「承認ルートを整理する」を実行する (Thực hiện thuật toán “điều chinhr/organize Approve route" )
				LevelOutput levelOutput = this.organizeApprovalRoute(companyID, employeeID, standardDate, listApprovalPhaseBefore, sysAtr, lowerApprove);
				// 共通アルゴリズム「承認ルートの異常チェック」を実行する (Thực hiện common algorithm "Check Approval Route bất thường"
				ErrorFlag errorFlag = this.checkApprovalRoot(levelOutput);
				levelOutput.setErrorFlag(errorFlag.value);
				ApprovalRootState approvalRootState = null;
				if(rootAtr==EmploymentRootAtr.CONFIRMATION) {
					approvalRootState = this.createFromApprovalPhaseListConfirm(companyID, standardDate, levelOutput);
					if(errorFlag == ErrorFlag.NO_ERROR){
						String appID = IdentifierUtil.randomUniqueId();
						approvalRootState = ApprovalRootState.createFromFirst(
								companyID,
								appID,  
								EnumAdaptor.valueOf(Integer.valueOf(targetType)+1, RootType.class),
								standardDate, 
								employeeID, 
								approvalRootState);
					}
				} else {
					approvalRootState = this.createFromApprovalPhaseList(companyID, standardDate, levelOutput);
					approvalRootState.setRootType(RootType.EMPLOYMENT_APPLICATION);
				}
				return new ApprovalRootContentOutput(approvalRootState, errorFlag);
			}
			
		}
		if(rootAtr==EmploymentRootAtr.CONFIRMATION) {
			return new ApprovalRootContentOutput(
					ApprovalRootState.builder().listApprovalPhaseState(Collections.emptyList())
					.rootType(EnumAdaptor.valueOf(Integer.valueOf(targetType)+1, RootType.class)).build(), 
					ErrorFlag.NO_APPROVER);
		}
		return new ApprovalRootContentOutput(
				ApprovalRootState.builder().listApprovalPhaseState(Collections.emptyList())
				.rootType(RootType.EMPLOYMENT_APPLICATION).build(), 
				ErrorFlag.NO_APPROVER);
	}
	
	@Override
	public List<String> getPersonByWorkplacePosition(String cid, String wkpId, GeneralDate baseDate, String jobTitleId, SystemAtr systemAtr) {
		// アルゴリズム「指定職位の本務兼務社員を取得」を実行する
		List<ConcurrentEmployeeImport> employeeList = employeeAdapter.getConcurrentEmployee(cid, jobTitleId, baseDate);
		if(CollectionUtil.isEmpty(employeeList)) {
			return Collections.emptyList();
		}
		// ドメインモデル「職位指定の設定」を取得する
		Optional<JobAssignSetting> assignSet = jobAssignSetRepository.findById();
		if (assignSet.get().getIsConcurrently()) {
			// 取得した職位対象者から兼務役職者を除く
			employeeList.removeIf(x -> x.isConcurrent());
		}
		
		List<String> approvers = new ArrayList<>();
		for (ConcurrentEmployeeImport emp : employeeList) {
			if(systemAtr==SystemAtr.WORK) {
				// 所属職場を取得する
				String paramID = wkApproverAdapter.getWorkplaceIDByEmpDate(emp.getEmployeeId(), baseDate);
				if(paramID.equals(wkpId)) {
					// 所属職場は職場ID（申請本人の所属職場）と一致するかチェックする(check workplaceID lay duoc co trung voi nguoi viet don hay khong)
					approvers.add(emp.getEmployeeId());
				}
			} else {
				// 社員と基準日から所属職場履歴項目を取得する
				String paramID = wkApproverAdapter.getDepartmentIDByEmpDate(emp.getEmployeeId(), baseDate);
				if(paramID.equals(wkpId)) {
					// 取得した承認者の部門をチェック
					approvers.add(emp.getEmployeeId());
				}
			}
		}
		return approvers;
	}

	private ApprovalRootState createFromApprovalPhaseList(String companyID, GeneralDate date,
			LevelOutput levelOutput) {
		List<ApprovalPhaseState> listApprovalPhaseState = levelOutput.getLevelInforLst().stream().map(levelInforLst -> {
			List<ApprovalFrame> resultApprovalFrame = levelInforLst.getApproverLst().stream().map(levelApproverList -> {
				List<ApproverInfor> lstApproverInfo = levelApproverList.getApproverInfoLst()
					.stream().map(approverInfo -> new ApproverInfor(
							approverInfo.getApproverID(), 
							ApprovalBehaviorAtr.UNAPPROVED, 
							"", 
							null, 
							new ApprovalComment(""), 
							approverInfo.getApproverInListOrder())).collect(Collectors.toList());
				return ApprovalFrame.convert(
						levelApproverList.getOrder(), 
						levelApproverList.isComfirmAtr() ? 1 : 0, 
						date, 
						lstApproverInfo);
			}).collect(Collectors.toList());
			return ApprovalPhaseState.createFormTypeJava(
					levelInforLst.getLevelNo(), 
					ApprovalBehaviorAtr.UNAPPROVED.value, 
					levelInforLst.getApprovalForm(), 
					resultApprovalFrame);
		}).collect(Collectors.toList());	
		return ApprovalRootState.builder()
				.listApprovalPhaseState(listApprovalPhaseState)
				.build();
	}
	
	private ApprovalRootState createFromApprovalPhaseListConfirm(String companyID, GeneralDate date,
			LevelOutput levelOutput) {
		List<ApprovalPhaseState> listApprovalPhaseState = levelOutput.getLevelInforLst().stream().map(levelInforOutput -> {
			List<ApprovalFrame> resultApprovalFrame = new ArrayList<>();
			List<LevelApproverList> filterLevelApproverList = levelInforOutput.getApproverLst()
					.stream().filter(x -> x.getApproverInfoLst().size() > 0).collect(Collectors.toList());
			if(CollectionUtil.isEmpty(filterLevelApproverList)) {
				return ApprovalPhaseState.createFormTypeJava(
						6 - levelInforOutput.getLevelNo(), 
						ApprovalBehaviorAtr.UNAPPROVED.value, 
						levelInforOutput.getApprovalForm(), 
						resultApprovalFrame);
			}
			if(filterLevelApproverList.size() > 1) {
				filterLevelApproverList.forEach(levelApproverList -> {
					List<ApprovalFrame> approvalFrameLst = levelApproverList.getApproverInfoLst()
						.stream().map(approverInfo -> {
							ApproverInfor approverInfor = new ApproverInfor(
								approverInfo.getApproverID(), 
								ApprovalBehaviorAtr.UNAPPROVED, 
								"", 
								null, 
								new ApprovalComment(""),
								1);
							return ApprovalFrame.convert(
									levelApproverList.getOrder(), 
									levelApproverList.isComfirmAtr() ? 1 : 0, 
									date, 
									Arrays.asList(approverInfor));
						}).collect(Collectors.toList());
					resultApprovalFrame.addAll(approvalFrameLst);
				});
			} else {
				List<LevelApproverInfo> levelApproverInfoLst = filterLevelApproverList.get(0).getApproverInfoLst();
				for(int i = 0; i < levelApproverInfoLst.size(); i++) {
					ApproverInfor approverInfor = new ApproverInfor(
							levelApproverInfoLst.get(i).getApproverID(), 
							ApprovalBehaviorAtr.UNAPPROVED, 
							"", 
							null, 
							new ApprovalComment(""),
							1);
					resultApprovalFrame.add(ApprovalFrame.convert(
								i + 1, 
								filterLevelApproverList.get(0).isComfirmAtr() ? 1 : 0, 
								date, 
								Arrays.asList(approverInfor)));
				}
			}
			return ApprovalPhaseState.createFormTypeJava(
					6 - levelInforOutput.getLevelNo(), 
					ApprovalBehaviorAtr.UNAPPROVED.value, 
					levelInforOutput.getApprovalForm(), 
					resultApprovalFrame);
		}).collect(Collectors.toList());	
		return ApprovalRootState.builder()
				.listApprovalPhaseState(listApprovalPhaseState)
				.build();
	}
	
	@Override
	public LevelOutput organizeApprovalRoute(String companyID, String employeeID, GeneralDate baseDate,
			List<ApprovalPhase> listApprovalPhase, SystemAtr systemAtr, Optional<Boolean> lowerApprove) {
		// 申請者の対象申請の承認者一覧を作成(Tạp ApproverList của TargerApplication của Applicant)
		LevelOutput result = new LevelOutput(0, new ArrayList<>());
		// ドメインモデル「承認フェーズ」．順序１～５ループする(loop tu Approval phase1denApproval phase5)
		listApprovalPhase.sort((a,b) -> a.getPhaseOrder() - b.getPhaseOrder());
		for(ApprovalPhase approvalPhase : listApprovalPhase) {
			// ループ中の承認フェーズに承認者を設定したかチェックする(check xem Approval phase dang xu ly co duoc cai dat nguoi xac nhan hay khong)
			if(CollectionUtil.isEmpty(approvalPhase.getApprovers())) {
				continue;
			}
			// レベル情報を作成(tạo thông tin level)
			LevelInforOutput levelInforOutput = new LevelInforOutput(
					approvalPhase.getPhaseOrder(), 
					approvalPhase.getApprovalForm().value, 
					approvalPhase.getApprovalAtr().value, 
					new ArrayList<>());
			Optional<Integer> opDispOrder = Optional.empty();
			// ドメインモデル「承認フェーズ」．承認者１～５ループする(loop tu Approval phase1denApproval phase5)
			approvalPhase.getApprovers().sort((a,b) -> a.getApproverOrder() - b.getApproverOrder());
			for(Approver approver : approvalPhase.getApprovers()) {
				// 承認者一覧を作成(Tạo ApproverList)
				LevelApproverList levelApproverList = new LevelApproverList(
						approver.getApproverOrder(), 
						"", 
						approver.getConfirmPerson().value!=0?true:false, 
						new ArrayList<>());
				List<LevelApproverInfo> approverInfoLst = new ArrayList<>();
				// ループ中の承認フェーズ．承認者指定区分をチェック(Check ApprovalPhase.ApproverSettingAtr đang loop)
				if(approvalPhase.getApprovalAtr()==ApprovalAtr.PERSON) {
					/*approverInfoLst.add(new ApproverInfo(
							"", 
							approver.getEmployeeId(), 
							approver.getApproverOrder(), 
							approver.getConfirmPerson().value!=0?true:false, 
							""));*/
					approverInfoLst.add(new LevelApproverInfo(
							approver.getEmployeeId(), 
							"", 1));
				} else {
					// 社員IDと基準日から職位情報を取得(Lấy thông tin position từ EmployeeID và BaseDate)
					JobTitleImport jobOfEmp = syJobTitleAdapter.findJobTitleBySid(employeeID, baseDate);
					// 職位IDから序列の並び順を取得(Lấy thứ tự sắp xếp theo cấp bậc từ postionID)
					opDispOrder = this.getDisOrderFromJobID(jobOfEmp.getPositionId(), companyID, baseDate);
					String paramID = this.getIDBySystemType(systemAtr, employeeID, baseDate);
					// 承認者グループから承認者を取得(Lấy approver từ ApproverGroup)
					approverInfoLst = getApproverFromGroup(
							companyID, 
							approver.getJobGCD(), 
							approver.getSpecWkpId(),
							paramID,
							opDispOrder, 
							employeeID, 
							baseDate, 
							systemAtr, 
							lowerApprove);
				}
				// 承認者を整理(Điều chỉnh Approver)
				levelApproverList.setApproverInfoLst(this.adjustApprover(approverInfoLst, baseDate, companyID, employeeID));
				if(!CollectionUtil.isEmpty(levelApproverList.getApproverInfoLst())) {
					levelInforOutput.getApproverLst().add(levelApproverList);
				}
				if(approvalPhase.getApprovalAtr()!=ApprovalAtr.PERSON && !CollectionUtil.isEmpty(levelApproverList.getApproverInfoLst())) {
					break;
				}
			}
			Integer approverCount = levelInforOutput.getApproverLst().stream()
					.collect(Collectors.summingInt(x -> x.getApproverInfoLst().size()));
			if(approverCount <= 0) {
				// 上位職場・部門の承認者を探す(Tìm kiếm Approver của UpperWorkplace)
				List<LevelApproverInfo> upperLevelApproverInfo = this.getUpperApproval(
						companyID, 
						approvalPhase.getApprovers().stream().map(x -> x.getJobGCD()).collect(Collectors.toList()), 
						opDispOrder, 
						employeeID, 
						baseDate, 
						systemAtr, 
						lowerApprove, 
						approvalPhase.getApprovalAtr());
				levelInforOutput.setApproverLst(Arrays.asList(new LevelApproverList(1, "", false, upperLevelApproverInfo)));
			}
			result.getLevelInforLst().add(levelInforOutput);
		}
		return result;
	}
	
	@Override
	public Optional<Integer> getDisOrderFromJobID(String jobID, String companyID, GeneralDate baseDate) {
		Optional<Integer> opDispOrder = Optional.empty();
		List<SimpleJobTitleImport> requestInfoList = syJobTitleAdapter
				.findByIds(companyID, Arrays.asList(jobID), baseDate);
		if(!CollectionUtil.isEmpty(requestInfoList)){
			opDispOrder = Optional.ofNullable(requestInfoList.get(0).getDisporder());
		}
		return opDispOrder;
	}

	@Override
	public List<LevelApproverInfo> getApproverFromGroup(String companyID, String approverGroupCD, String specWkpId, String paramID,
			Optional<Integer> opDispOrder, String employeeID, GeneralDate baseDate, SystemAtr systemAtr, Optional<Boolean> lowerApprove) {
		List<LevelApproverInfo> result = new ArrayList<>();
		// 承認者Gコードから職位情報を取得(Lấy thông tin position từ ApproverGCode)
		List<String> jobIDLst = syJobTitleAdapter.getJobIDFromGroup(companyID, approverGroupCD);
		// 取得したList＜職位ID＞をループ(Loop List<PositionID> đã lấy)
		for(String jobID : jobIDLst) {
			// Input．特定職場IDをチェック(Check Input. SepecificWorkplaceID)
			if(Strings.isBlank(specWkpId)) {
				// 申請者より、下の職位の承認者とチェック(Check Approver có chức vụ thấp hơn người làm đơn)
				boolean getFlag = this.checkApproverApplicantOrder(systemAtr, jobID, opDispOrder, lowerApprove, companyID, baseDate);
				if(!getFlag) {
					continue;
				}
			}
			// 6.職場に指定する職位の対象者を取得する(getPersonByWorkplacePosition)
			List<String> approverInfoLoopLst = this.getPersonByWorkplacePosition(
					companyID, 
					Strings.isNotBlank(specWkpId) ? specWkpId : paramID, 
					baseDate, 
					jobID,
					systemAtr);
			// 社員ID(List)から個人社員基本情報を取得
			List<EmpInfoImport> empInfoImportLst = employeeAdapter.getEmpInfo(approverInfoLoopLst);
			empInfoImportLst.sort(Comparator.comparing(EmpInfoImport::getEmployeeCode));
			// 承認者リストに取得した承認者を追加(THêm Approver đã lấy vào ApproverList)
			for(int i = 0; i < empInfoImportLst.size(); i++) {
				result.add(new LevelApproverInfo(
						empInfoImportLst.get(i).getEmployeeId(), 
						"", 
						i+1));
			}
		}
		return result;
	}

	@Override
	public boolean checkApproverApplicantOrder(SystemAtr systemAtr, String jobID, Optional<Integer> opDispOrder, 
			Optional<Boolean> lowerApprove, String companyID, GeneralDate baseDate) {
		// 取得フラグ　＝　True(GetFlag = True)
		boolean result = true;
		// Optional<申請者の序列の並び順＞をチェック(Check Optional<thứ tự sắp xếp theo cấp bậc của người làm đơn＞)
		if(!opDispOrder.isPresent()) {
			return result;
		}
		// 職位IDから序列の並び順を取得(Lất thứ tự sắp xếp theo cấp bậc từ PositionID)
		List<SimpleJobTitleImport> simpleJobTitleImportList = syJobTitleAdapter.findByIds(companyID, Arrays.asList(jobID), baseDate);
		// 取得したOptional<並び順＞をチェック(Check Optional<order＞ đã lấy)
		if(CollectionUtil.isEmpty(simpleJobTitleImportList)){
			return result;
		}	
		// 序列の並び順を比較(So sánh thứ tự sắp xép của cấp bậc)
		Integer jobOrder = simpleJobTitleImportList.get(0).getDisporder();
		if(jobOrder==null) {
			result = true;
			return result;
		}
		if(
			(
				(
					systemAtr==SystemAtr.HUMAN_RESOURCES 
					&& 
					(!lowerApprove.isPresent() || !lowerApprove.get())
				) 					
				|| 	
				systemAtr==SystemAtr.WORK
			) 
			&& 
			jobOrder <= opDispOrder.get()
		) {
			return result;
		}
		// 取得フラグ　＝　False(GetFlag = False)
		result = false;
		return result;
	}

	@Override
	public List<LevelApproverInfo> adjustApprover(List<LevelApproverInfo> approverInfoLst, GeneralDate baseDate, String companyID, String employeeID) {
		List<LevelApproverInfo> result = new ArrayList<>();
		// 指定社員が基準日に承認権限を持っているかチェック
		List<LevelApproverInfo> approverInfoAfterLst = this.checkApproverStatusAndAuthor(approverInfoLst, baseDate, companyID);
		// 取得した承認者リストをチェック(Check ApproverList đã lấy)
		if(CollectionUtil.isEmpty(approverInfoAfterLst)) {
			return Collections.emptyList();
		}
		for(LevelApproverInfo approverInfo : approverInfoAfterLst) {
			// 承認代行情報の取得処理(xử lý lấy thông tin đại diện approve)
			ApprovalRepresenterOutput approvalRepresenterOutput = collectApprovalAgentInforService.getApprovalAgentInfor(
					companyID, 
					Arrays.asList(approverInfo.getApproverID()));
			if(!approvalRepresenterOutput.getListApprovalAgentInfor().get(0).isPass()) {
				String representerID = "";
				if(!CollectionUtil.isEmpty(approvalRepresenterOutput.getListAgent())) {
					representerID = approvalRepresenterOutput.getListAgent().get(0);
				}
				// Output．承認者一覧に取得した承認者の代行情報リストを追加(Them list thông tin đại diện của người approve đã lấy vào Output.ApproverList)
				result.add(new LevelApproverInfo(approverInfo.getApproverID(), representerID, approverInfo.getApproverInListOrder()));
			}
		}
		// ドメインモデル「承認設定」．本人による承認をチェックする(Check domain「承認設定」. 本人による承認 )
		PrincipalApprovalFlg principalApprovalFlg = approvalSettingRepository.getPrincipalByCompanyId(companyID).orElse(PrincipalApprovalFlg.NOT_PRINCIPAL);
		if(principalApprovalFlg.equals(PrincipalApprovalFlg.NOT_PRINCIPAL)){
			// 申請本人社員IDを承認者IDリストから消す(Xóa 申請本人社員ID từ ApproverIDList)
			List<LevelApproverInfo> listDeleteApprover = result.stream().filter(x -> x.getApproverID().equals(employeeID)).collect(Collectors.toList());
			result.removeAll(listDeleteApprover);
		}
		if(CollectionUtil.isEmpty(result)) {
			return Collections.emptyList();
		}
		List<LevelApproverInfo> result2 = new ArrayList<>();
		for(int i = 0; i < result.size(); i++) {
			LevelApproverInfo approverInfo = result.get(i);
			result2.add(new LevelApproverInfo(approverInfo.getApproverID(), approverInfo.getAgentID(), i + 1));
		}
		return result2;
	}

	@Override
	public List<LevelApproverInfo> checkApproverAuthor(List<LevelApproverInfo> approverInfoLst, GeneralDate baseDate, String companyID) {
		List<LevelApproverInfo> removeLst = new ArrayList<>();
		// 承認者リストをループ(Loop ApproverList)
		for(LevelApproverInfo approverInfo : approverInfoLst) {
			// 指定社員が基準日に承認権限を持っているかチェックする(Check xem employee chỉ định có quyền approve ở thời điểm baseDate hay ko)
			boolean canApproval = employeeAdapter.canApprovalOnBaseDate(companyID, approverInfo.getApproverID(), baseDate);
			// 取得した権限状態をチェック(Check trạng thái quyền hạn đã lấy)
			if(canApproval) {
				continue;
			}
			// 承認者リストにループ中の承認者を除く(Xóa approver đang loop trong ApproverList)
			removeLst.add(approverInfo);
		}
		approverInfoLst.removeAll(removeLst);
		return approverInfoLst;
	}

	@Override
	public List<LevelApproverInfo> getUpperApproval(String companyID, List<String> approverGroupCDLst, Optional<Integer> opDispOrder, 
			String employeeID, GeneralDate baseDate, SystemAtr systemAtr, Optional<Boolean> lowerApprove, ApprovalAtr approvalAtr) {
		// Input．承認者指定区分をチェック(Check Input . ApproverSettingAtr)
		if(approvalAtr!=ApprovalAtr.APPROVER_GROUP) {
			return Collections.emptyList();
		}
		// Input．承認者一覧をチェック(Check Input. ApproverList)
		if(CollectionUtil.isEmpty(approverGroupCDLst)) {
			return Collections.emptyList();
		}
		// 上位職場・部門を探す
		List<String> upperIDLst = this.getUpperID(companyID, employeeID, baseDate, systemAtr);
		List<LevelApproverInfo> approverInfoLst = new ArrayList<>();
		for(String loopID : upperIDLst) {
			for(String approverGroupCD : approverGroupCDLst) {
				// 承認者グループから承認者を取得(Lấy Approver từ ApproverGroup)
				approverInfoLst = this.getApproverFromGroup(companyID, approverGroupCD, "", loopID, opDispOrder, employeeID, baseDate, systemAtr, lowerApprove);
				// 取得した承認者リストをチェック(Check ApproverList đã  lấy)
				if(!CollectionUtil.isEmpty(approverInfoLst)) {
					break;
				}
			}
			if(!CollectionUtil.isEmpty(approverInfoLst)) {
				break;
			}
		}
		// 取得した承認者リストをチェック(Check ApproverList đã lấy)
		if(!CollectionUtil.isEmpty(approverInfoLst)) {
			// 承認者を整理(Điều chỉnh Approver)
			return this.adjustApprover(approverInfoLst, baseDate, companyID, employeeID);
		}
		return Collections.emptyList();
	}
	
	@Override
	public ErrorFlag checkApprovalRoot(LevelOutput levelOutput) {
		List<LevelInforOutput> levelInforLst = levelOutput.getLevelInforLst();
		if(CollectionUtil.isEmpty(levelInforLst)){
			return ErrorFlag.NO_APPROVER;
		}
		for(LevelInforOutput levelInforOutput : levelInforLst) {
			// check số lượng người approve
			Integer approverCount = levelInforOutput.getApproverLst().stream()
					.collect(Collectors.summingInt(x -> x.getApproverInfoLst().size()));
			// trên 10 người
			if(approverCount > 10) {
				return ErrorFlag.APPROVER_UP_10;
			} 
			// dưới 1 người
			if(approverCount <= 0) {
				return ErrorFlag.NO_APPROVER;
			}
			if(levelInforOutput.getApprovalForm() == ApprovalForm.EVERYONE_APPROVED.value){
				continue;
			}
			// check frame confirm không có người
			for(LevelApproverList levelApproverList : levelInforOutput.getApproverLst()){
				if(!levelApproverList.isComfirmAtr()){
					continue;
				}
				if(CollectionUtil.isEmpty(levelApproverList.getApproverInfoLst())){
					return ErrorFlag.NO_CONFIRM_PERSON;
				}
			}
		}
		return ErrorFlag.NO_ERROR;
	}

	@Override
	public List<String> getUpperID(String companyID, String employeeID, GeneralDate date, SystemAtr systemAtr) {
		// Input．システム区分をチェック
		if(systemAtr==SystemAtr.WORK) {
			// 社員と基準日から所属職場履歴項目を取得する
			String workplaceID = wkApproverAdapter.getWorkplaceIDByEmpDate(employeeID, date);
			// [No.569]職場の上位職場を取得する
			return wkApproverAdapter.getUpperWorkplace(companyID, workplaceID, date);
		} else {
			// 社員と基準日から所属部門履歴項目を取得する
			String departmentID = wkApproverAdapter.getDepartmentIDByEmpDate(employeeID, date);
			// 部門の上位部門を取得する
			return wkApproverAdapter.getUpperDepartment(companyID, departmentID, date);
		} 
	}

	@Override
	public String getIDBySystemType(SystemAtr systemAtr, String employeeID, GeneralDate baseDate) {
		if(systemAtr==SystemAtr.WORK) {
			// 社員と基準日から所属部門履歴項目を取得する
			return wkApproverAdapter.getWorkplaceIDByEmpDate(employeeID, baseDate);
		} else {
			// 社員と基準日から所属職場履歴項目を取得する
			return wkApproverAdapter.getDepartmentIDByEmpDate(employeeID, baseDate);
		}
	}

	@Override
	public List<String> getUpperIDIncludeSelf(String companyID, String employeeID, GeneralDate date, SystemAtr systemAtr) {
		// Input．システム区分をチェック
		if(systemAtr==SystemAtr.WORK) {
			// 社員と基準日から所属職場履歴項目を取得する
			String workplaceID = wkApproverAdapter.getWorkplaceIDByEmpDate(employeeID, date);
			// [No.571]職場の上位職場を基準職場を含めて取得する
			return wkApproverAdapter.getWorkplaceIdAndUpper(companyID, workplaceID, date);
		} else {
			// 社員と基準日から所属部門履歴項目を取得する
			String departmentID = wkApproverAdapter.getDepartmentIDByEmpDate(employeeID, date);
			// 部門の上位部門を基準部門を含めて取得する
			return wkApproverAdapter.getDepartmentIDAndUpper(companyID, departmentID, date);
		} 
	}
	
	@Override
	public List<LevelApproverInfo> checkApproverStatusAndAuthor(List<LevelApproverInfo> approverInfoLst, GeneralDate baseDate, String companyID) {
		List<LevelApproverInfo> removeLst = new ArrayList<>();
		// 承認者リストをループ(Loop ApproverList)
		for(LevelApproverInfo approverInfo : approverInfoLst) {
			// 在職状態を取得(lấy trạng thái atwork)
			StatusOfEmployment statusOfEmployment = employeeAdapter.getStatusOfEmployment(approverInfo.getApproverID(), baseDate).getStatusOfEmployment();
			// 承認者の在職状態をチェック(Check AtWorkStatus của approver)
			if(statusOfEmployment==StatusOfEmployment.INCUMBENT){
				// 指定社員が基準日に承認権限を持っているかチェックする(Check xem employee chỉ định có quyền approve ở thời điểm baseDate hay ko)
				boolean canApproval = employeeAdapter.canApprovalOnBaseDate(companyID, approverInfo.getApproverID(), baseDate);
				// 取得した権限状態をチェック(Check trạng thái quyền hạn đã lấy)
				if(canApproval) {
					continue;
				}
			}
			// 承認者リストにループ中の承認者を除く(Xóa approver đang loop trong ApproverList)
			removeLst.add(approverInfo);
		}
		approverInfoLst.removeAll(removeLst);
		return approverInfoLst;
	}
}
