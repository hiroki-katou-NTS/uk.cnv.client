package nts.uk.ctx.workflow.dom.resultrecord.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.EmploymentRootAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.SystemAtr;
import nts.uk.ctx.workflow.dom.resultrecord.AppApproveInsTmp;
import nts.uk.ctx.workflow.dom.resultrecord.AppFrameInsTmp;
import nts.uk.ctx.workflow.dom.resultrecord.AppFrameInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppPhaseInsTmp;
import nts.uk.ctx.workflow.dom.resultrecord.AppPhaseInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootInstanceRepository;
import nts.uk.ctx.workflow.dom.resultrecord.RecordRootType;
import nts.uk.ctx.workflow.dom.service.CollectApprovalRootService;
import nts.uk.ctx.workflow.dom.service.output.ApprovalRootContentOutput;
import nts.uk.ctx.workflow.dom.service.output.ErrorFlag;
import nts.uk.shr.com.context.AppContexts;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class CreateDailyApproverImpl implements CreateDailyApprover {
	
	@Inject
	private AppRootInstanceRepository appRootInstanceRepository;
	
	@Inject
	private CollectApprovalRootService collectApprovalRootService;

	@Override
	public AppRootInstanceContent createDailyApprover(String employeeID, RecordRootType rootType, GeneralDate recordDate, GeneralDate closureStartDate) {
		String companyID = AppContexts.user().companyId();
		// 承認ルートを取得する（確認）
		ApprovalRootContentOutput approvalRootContentOutput = collectApprovalRootService.getApprovalRootOfSubjectRequest(
																companyID, 
																employeeID, 
																EmploymentRootAtr.CONFIRMATION, 
																Integer.valueOf(rootType.value-1).toString(), 
																recordDate, 
																SystemAtr.WORK, 
																Optional.empty());
		AppRootInstance appRootInstance = new AppRootInstance(
				approvalRootContentOutput.getApprovalRootState().getRootStateID(), 
				companyID, 
				employeeID, 
				new DatePeriod(GeneralDate.fromString("1900/01/01", "yyyy/MM/dd"), GeneralDate.fromString("9999/12/31", "yyyy/MM/dd")), 
				rootType, 
				approvalRootContentOutput.getApprovalRootState().getListApprovalPhaseState().stream()
					.map(x -> new AppPhaseInstance(
							x.getPhaseOrder(), 
							x.getApprovalForm(), 
							x.getListApprovalFrame().stream()
								.map(y -> new AppFrameInstance(
										y.getFrameOrder(), 
										y.getConfirmAtr().value==1?true:false, 
										y.getLstApproverInfo().stream().map(z -> z.getApproverID())
								.collect(Collectors.toList())))
					.collect(Collectors.toList())))
				.collect(Collectors.toList()));
		ErrorFlag errorFlag = approvalRootContentOutput.getErrorFlag();
		String errorMsgID = "";
		switch (errorFlag) {
		case NO_APPROVER:
			errorMsgID = "Msg_324";
			break;
		case NO_CONFIRM_PERSON:
			errorMsgID = "Msg_326";
			break;
		case APPROVER_UP_10:
			errorMsgID = "Msg_325";
			break;
		case ABNORMAL_TERMINATION:
			errorMsgID = "Msg_1339";
			break;
		default:
			break;
		}
		if(errorFlag!=ErrorFlag.NO_ERROR){
			return new AppRootInstanceContent(appRootInstance, errorFlag, errorMsgID);
		}
		// ドメインモデル「承認ルート中間データ」を削除する
		List<String> insIDLst = appRootInstanceRepository.findRootByEmpFromDate(companyID, employeeID, recordDate, rootType);
		appRootInstanceRepository.deleteByIDLst(insIDLst);
		// ドメインモデル「承認ルート中間データ」を取得する
		Pair<String, DatePeriod> opAppRootInstanceConflict = appRootInstanceRepository.findIDByEmpDateNewestBelow(companyID, employeeID, recordDate, rootType);
		if(Strings.isNotBlank(opAppRootInstanceConflict.getLeft())){
			// 履歴期間．開始日が一番新しいドメインモデル「承認ルート中間データ」を取得する
			String appRootInstanceConflictID = opAppRootInstanceConflict.getLeft();
			// output．承認ルートの内容は取得したドメインモデル「承認ルート中間データ」を比較する
			boolean isSame = compareInsContent(appRootInstance, appRootInstanceConflictID);
			if(isSame){
				// 履歴期間．開始日が一番新しいドメインモデル「承認ルート中間データ」をUPDATEする
				DatePeriod oldPeriod = opAppRootInstanceConflict.getRight();
				appRootInstance.setDatePeriod(new DatePeriod(oldPeriod.start(), GeneralDate.fromString("9999/12/31", "yyyy/MM/dd")));
				appRootInstance.setRootID(opAppRootInstanceConflict.getLeft());
				appRootInstanceRepository.update(appRootInstance);
				return new AppRootInstanceContent(appRootInstance, errorFlag, errorMsgID);
			} else {
				// 履歴の開始日を取得する
				GeneralDate startHistDate = this.getHistoryStartDate(companyID, employeeID, rootType, recordDate, closureStartDate, opAppRootInstanceConflict);
				// ドメインモデル「承認ルート中間データ」を削除する
				List<String> insIDLstByStartDate = appRootInstanceRepository.findRootByEmpFromDate(companyID, employeeID, startHistDate, rootType);
				appRootInstanceRepository.deleteByIDLst(insIDLstByStartDate);
				appRootInstance.setDatePeriod(new DatePeriod(startHistDate, GeneralDate.fromString("9999/12/31", "yyyy/MM/dd")));
				// 履歴期間．開始日が一番新しいドメインモデル「承認ルート中間データ」を取得する
				Pair<String, DatePeriod> appRootInstNewest = appRootInstanceRepository.findIDByEmpDateNewestBelow(companyID, employeeID, startHistDate, rootType);
				// 履歴期間．開始日が一番新しいドメインモデル「承認ルート中間データ」をUPDATEする
				if(appRootInstNewest.getRight().end().afterOrEquals(startHistDate)){
					// 履歴期間．終了日＞＝取得した履歴開始日
					appRootInstanceRepository.updateEndByID(appRootInstNewest.getLeft(), startHistDate.addDays(-1));
				}
				//承認状態をクリアする
				// appRootConfirmRepository.clearStatusFromDate(companyID, employeeID, recordDate, rootType);
			}
		}
		// 取得した承認ルートをドメインモデル「承認ルート中間データ」にINSERTする
		appRootInstanceRepository.insert(appRootInstance);
		return new AppRootInstanceContent(appRootInstance, errorFlag, errorMsgID);
	}
	
	private boolean compareAppRootContent(AppRootInstance oldAppRoot, AppRootInstance newAppRoot){
		for(AppPhaseInstance oldAppPhase : oldAppRoot.getListAppPhase()){
			Optional<AppPhaseInstance> opNewAppPhaseLoop = 
					newAppRoot.getListAppPhase().stream().filter(x -> x.getPhaseOrder()==oldAppPhase.getPhaseOrder()).findAny();
			if(!opNewAppPhaseLoop.isPresent()){
				return false;
			}
			boolean isSame = compareAppPhaseContent(oldAppPhase, opNewAppPhaseLoop.get());
			if(!isSame){
				return false;
			}
		}
		return true;
	}
	
	private boolean compareAppPhaseContent(AppPhaseInstance oldAppPhase, AppPhaseInstance newAppPhase){
		for(AppFrameInstance oldAppFrame : oldAppPhase.getListAppFrame()){
			Optional<AppFrameInstance> opNewAppFrameLoop = 
					newAppPhase.getListAppFrame().stream().filter(x -> x.getFrameOrder()==oldAppFrame.getFrameOrder()).findAny();
			if(!opNewAppFrameLoop.isPresent()){
				return false;
			}
			boolean isSame = compareAppFrameContent(oldAppFrame, opNewAppFrameLoop.get());
			if(!isSame){
				return false;
			}
		}
		return true;
	}
	
	private boolean compareAppFrameContent(AppFrameInstance oldAppFrame, AppFrameInstance newAppFrame){
		List<String> oldList = oldAppFrame.getListApprover();
		List<String> newList = newAppFrame.getListApprover();
		return oldList.containsAll(newList) && newList.containsAll(oldList);
	}

	/**
	 * 履歴の開始日を取得する
	 * @param companyID
	 * @param employeeID
	 * @param rootType
	 * @param date
	 * @param closureStartDate
	 * @param appRootInstance
	 * @return
	 */
	private GeneralDate getHistoryStartDate(String companyID, String employeeID, RecordRootType rootType, GeneralDate date, 
			GeneralDate closureStartDate, Pair<String, DatePeriod> appRootInstance){
		if(rootType==RecordRootType.CONFIRM_WORK_BY_MONTH){
			return date;
		}
		// input．年月日－１日～締め開始日まで－１日ずつループする
		GeneralDate loopDate = date.addDays(-1);
		do {
			String compareAppInsID = appRootInstance.getLeft();
			// 承認ルートを取得する（確認）
			ApprovalRootContentOutput approvalRootContentOutput = collectApprovalRootService.getApprovalRootOfSubjectRequest(
					companyID, 
					employeeID, 
					EmploymentRootAtr.CONFIRMATION, 
					Integer.valueOf(rootType.value-1).toString(), 
					loopDate, 
					SystemAtr.WORK, 
					Optional.empty());
			AppRootInstance appRootInsRs = new AppRootInstance(
					approvalRootContentOutput.getApprovalRootState().getRootStateID(), 
					companyID, 
					employeeID, 
					new DatePeriod(GeneralDate.fromString("1900/01/01", "yyyy/MM/dd"), GeneralDate.fromString("9999/12/31", "yyyy/MM/dd")), 
					rootType, 
					approvalRootContentOutput.getApprovalRootState().getListApprovalPhaseState().stream()
						.map(x -> new AppPhaseInstance(
								x.getPhaseOrder(), 
								x.getApprovalForm(), 
								x.getListApprovalFrame().stream()
									.map(y -> new AppFrameInstance(
											y.getFrameOrder(), 
											y.getConfirmAtr().value==1?true:false, 
											y.getLstApproverInfo().stream().map(z -> z.getApproverID())
									.collect(Collectors.toList())))
						.collect(Collectors.toList())))
					.collect(Collectors.toList()));
			if(appRootInstance.getRight().start().after(loopDate)){
				// ドメインモデル「承認ルート中間データ」を取得する
				String opAppInsID = appRootInstanceRepository.findIDByContainDate(companyID, employeeID, loopDate, rootType);
				if(Strings.isNotBlank(opAppInsID)){
					compareAppInsID = opAppInsID;
				}
			}
			// output．承認ルートの内容は取得したドメインモデル「承認ルート中間データ」を比較する
			boolean isSame = compareInsContent(appRootInsRs, compareAppInsID);
			if(isSame){
				// 履歴開始日＝ループ中の年月日+1日
				return loopDate.addDays(1);
			}
			// ループ中の年月日 ← ループ中の年月日－1日
			loopDate = loopDate.addDays(-1);
		} while(loopDate.afterOrEquals(closureStartDate));
		// 履歴開始日＝締め開始日
		return closureStartDate;
	}
	
	private boolean compareInsContent(AppRootInstance appRootInstance, String rootID) {
		List<AppPhaseInsTmp> appPhaseInsTmpLst = new ArrayList<>();
		List<AppFrameInsTmp> appFrameInsTmpLst = new ArrayList<>();
		List<AppApproveInsTmp> appApproveInsTmpLst = new ArrayList<>();
		for(AppPhaseInstance appPhaseInstance : appRootInstance.getListAppPhase()) {
			appPhaseInsTmpLst.add(new AppPhaseInsTmp(
					appRootInstance.getRootID(), 
					appPhaseInstance.getPhaseOrder(), 
					appPhaseInstance.getApprovalForm().value));
			for(AppFrameInstance appFrameInstance : appPhaseInstance.getListAppFrame()) {
				appFrameInsTmpLst.add(new AppFrameInsTmp(
						appRootInstance.getRootID(), 
						appPhaseInstance.getPhaseOrder(), 
						appFrameInstance.getFrameOrder(), 
						appFrameInstance.isConfirmAtr()?1:0));
				for(String approver : appFrameInstance.getListApprover()) {
					appApproveInsTmpLst.add(new AppApproveInsTmp(
							appRootInstance.getRootID(), 
							appPhaseInstance.getPhaseOrder(), 
							appFrameInstance.getFrameOrder(), 
							approver));
				}
			}
		}
		
		List<AppApproveInsTmp> appApproveInsDBLst = appRootInstanceRepository.findApproverTmpByID(rootID);
		if(appApproveInsTmpLst.size()!=appApproveInsDBLst.size()) {
			return false;
		}
		for(AppApproveInsTmp appApproveInsTmp : appApproveInsTmpLst) {
			boolean contain = appApproveInsDBLst.stream().filter(x -> x.equalOther(appApproveInsTmp)).findAny().isPresent();
			if(!contain) {
				return false;
			}
		}
		
		List<AppFrameInsTmp> appFrameInsDBLst = appRootInstanceRepository.findFrameTmpByID(rootID);
		if(appFrameInsTmpLst.size()!=appFrameInsDBLst.size()) {
			return false;
		}
		for(AppFrameInsTmp appFrameInsTmp : appFrameInsTmpLst) {
			boolean contain = appFrameInsDBLst.stream().filter(x -> x.equalOther(appFrameInsTmp)).findAny().isPresent();
			if(!contain) {
				return false;
			}
		}
		
		List<AppPhaseInsTmp> appPhaseInsDBLst = appRootInstanceRepository.findPhaseTmpByID(rootID);
		if(appPhaseInsTmpLst.size()!=appPhaseInsDBLst.size()) {
			return false;
		}
		for(AppPhaseInsTmp appPhaseInsTmp : appPhaseInsTmpLst) {
			boolean contain = appPhaseInsDBLst.stream().filter(x -> x.equalOther(appPhaseInsTmp)).findAny().isPresent();
			if(!contain) {
				return false;
			}
		}
		
		return true;
	}
}
