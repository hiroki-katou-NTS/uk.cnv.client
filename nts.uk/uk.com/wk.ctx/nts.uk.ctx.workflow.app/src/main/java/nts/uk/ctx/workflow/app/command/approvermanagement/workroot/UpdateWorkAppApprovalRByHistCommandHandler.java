package nts.uk.ctx.workflow.app.command.approvermanagement.workroot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhase;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.CompanyApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkAppApprovalRootRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRoot;
import nts.uk.shr.com.context.AppContexts;
/**
 * 
 * @author hoatt
 *
 */
@Stateless
public class UpdateWorkAppApprovalRByHistCommandHandler extends CommandHandler<UpdateWorkAppApprovalRByHistCommand>{
	@Inject
	private WorkAppApprovalRootRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<UpdateWorkAppApprovalRByHistCommand> context) {
		String companyId = AppContexts.user().companyId();
		UpdateWorkAppApprovalRByHistCommand  objUpdateItem = context.getCommand();
		List<UpdateHistoryDto> lstHist = objUpdateItem.getLstUpdate();
		//history current
		String startDate = objUpdateItem.getStartDate();
		GeneralDate sDate = GeneralDate.localDate(LocalDate.parse(startDate));
		GeneralDate eDate = sDate.addDays(-1);
		String endDateUpdate = eDate.toString();//Edate: edit
		//history previous
		String startDatePrevious = objUpdateItem.getStartDatePrevious();
		GeneralDate sDatePrevious = GeneralDate.localDate(LocalDate.parse(startDatePrevious));
		GeneralDate eDatePrevious = sDatePrevious.addDays(-1);
		String endDatePrevious  = eDatePrevious.toString();//Edate to find history Previous 
		String endDateDelete = "9999/12/31";//Edate: delete
		for (UpdateHistoryDto updateItem : lstHist) {
			//TH: company - domain 会社別就業承認ルート
			if(objUpdateItem.getCheck()==1){
				Optional<CompanyApprovalRoot> comAppRootDb = repo.getComApprovalRoot(companyId, updateItem.getApprovalId(), updateItem.getHistoryId());
				if(!comAppRootDb.isPresent()){
					continue;
				}
				//item update
				CompanyApprovalRoot comAppRoot = CompanyApprovalRoot.updateSdateEdate(comAppRootDb.get(), objUpdateItem.getStartDate(), objUpdateItem.getEndDate());
				//find history previous
				List<CompanyApprovalRoot> lstOld= repo.getComApprovalRootByEdate(companyId, endDatePrevious);
				if(lstOld.isEmpty()){// history previous is not exist
					if(objUpdateItem.getEditOrDelete()==1){//TH: edit
						repo.updateComApprovalRoot(comAppRoot);
					}else{//TH: delete
						//get all  ApprovalPhase by BranchId
						List<ApprovalPhase> lstAPhase = repo.getAllApprovalPhasebyCode(companyId, comAppRoot.getBranchId());
						for (ApprovalPhase approvalPhase : lstAPhase) {
							//delete All Approver By Approval Phase Id
							repo.deleteAllApproverByAppPhId(companyId, approvalPhase.getApprovalPhaseId());
						}
						//delete All Approval Phase By Branch Id
						repo.deleteAllAppPhaseByBranchId(companyId, comAppRoot.getBranchId());
						//delete ComApprovalRoot
						repo.deleteComApprovalRoot(companyId, updateItem.getApprovalId(), updateItem.getHistoryId());
					}
				}else{// history previous is exist
					CompanyApprovalRoot com = lstOld.get(0);
					//check 編集後の履歴の開始年月日 > 取得した履歴の開始年月日 が falseの場合
					if(!checkStartDate(com.getPeriod().getStartDate().toString(),objUpdateItem.getStartDate())){
						throw new BusinessException("Msg_156");
					}
					if(objUpdateItem.getEditOrDelete()==1){//edit
						//history previous 
						CompanyApprovalRoot comAppRootUpdate= CompanyApprovalRoot.updateSdateEdate(com, com.getPeriod().getStartDate().toString(), endDateUpdate);
						//update history previous
						repo.updateComApprovalRoot(comAppRootUpdate);
						//update history current
						repo.updateComApprovalRoot(comAppRoot);
					}else{//delete 
						CompanyApprovalRoot comAppRootUpdate = CompanyApprovalRoot.createSimpleFromJavaType(companyId,
								com.getApprovalId(),
								com.getHistoryId(),
								com.getApplicationType().value.intValue(),
								com.getPeriod().getStartDate().toString(),
								endDateDelete,
								com.getBranchId(),
								com.getAnyItemApplicationId(),
								com.getConfirmationRootType().value.intValue(),
								com.getEmploymentRootAtr().value);
						//update history previous
						repo.updateComApprovalRoot(comAppRootUpdate);
						//get all  ApprovalPhase by BranchId
						List<ApprovalPhase> lstAPhase = repo.getAllApprovalPhasebyCode(companyId, comAppRoot.getBranchId());
						for (ApprovalPhase approvalPhase : lstAPhase) {
							//delete All Approver By Approval Phase Id
							repo.deleteAllApproverByAppPhId(companyId, approvalPhase.getApprovalPhaseId());
						}
						//delete All Approval Phase By Branch Id
						repo.deleteAllAppPhaseByBranchId(companyId, comAppRoot.getBranchId());
						//delete history current
						repo.deleteComApprovalRoot(companyId, updateItem.getApprovalId(), updateItem.getHistoryId());
					}
				}
			}
			//TH: workplace - domain 職場別就業承認ルート
			if(objUpdateItem.getCheck()==2){
				Optional<WorkplaceApprovalRoot> wpAppRootDb = repo.getWpApprovalRoot(companyId, updateItem.getApprovalId(), objUpdateItem.getWorkplaceId(), updateItem.getHistoryId());
				if(!wpAppRootDb.isPresent()){
					continue;
				}
				WorkplaceApprovalRoot wpAppRoot = WorkplaceApprovalRoot.updateSdateEdate(wpAppRootDb.get(), objUpdateItem.getStartDate(), objUpdateItem.getEndDate());
				//find history previous
				List<WorkplaceApprovalRoot> lstOld= repo.getWpApprovalRootByEdate(companyId, wpAppRoot.getWorkplaceId(), endDatePrevious);
				if(lstOld.isEmpty()){// history previous is not exist
					if(objUpdateItem.getEditOrDelete()==1){//TH: edit
						repo.updateWpApprovalRoot(wpAppRoot);
					}else{//TH: delete
						//get all  ApprovalPhase by BranchId
						List<ApprovalPhase> lstAPhase = repo.getAllApprovalPhasebyCode(companyId, wpAppRoot.getBranchId());
						for (ApprovalPhase approvalPhase : lstAPhase) {
							//delete All Approver By Approval Phase Id
							repo.deleteAllApproverByAppPhId(companyId, approvalPhase.getApprovalPhaseId());
						}
						//delete All Approval Phase By Branch Id
						repo.deleteAllAppPhaseByBranchId(companyId, wpAppRoot.getBranchId());
						//delete WpApprovalRoot
						repo.deleteWpApprovalRoot(companyId, updateItem.getApprovalId(), wpAppRoot.getWorkplaceId(), updateItem.getHistoryId());
					}
				}else{// history previous is exist
					WorkplaceApprovalRoot wp = lstOld.get(0);
					//check 編集後の履歴の開始年月日 > 取得した履歴の開始年月日 が falseの場合
					if(!checkStartDate(wp.getPeriod().getStartDate().toString(),objUpdateItem.getStartDate())){
						throw new BusinessException("Msg_156");
					}
					if(objUpdateItem.getEditOrDelete()==1){//edit
						//history previous 
						WorkplaceApprovalRoot wpAppRootUpdate = WorkplaceApprovalRoot.updateSdateEdate(wp, wp.getPeriod().getStartDate().toString(), endDateUpdate);
						//update history previous
						repo.updateWpApprovalRoot(wpAppRootUpdate);
						//update history current
						repo.updateWpApprovalRoot(wpAppRoot);
					}else{//delete 
						WorkplaceApprovalRoot wpAppRootUpdate = WorkplaceApprovalRoot.updateSdateEdate(wp, wp.getPeriod().getStartDate().toString(), endDateUpdate);
						//update history previous
						repo.updateWpApprovalRoot(wpAppRootUpdate);
						//get all  ApprovalPhase by BranchId
						List<ApprovalPhase> lstAPhase = repo.getAllApprovalPhasebyCode(companyId, wpAppRoot.getBranchId());
						for (ApprovalPhase approvalPhase : lstAPhase) {
							//delete All Approver By Approval Phase Id
							repo.deleteAllApproverByAppPhId(companyId, approvalPhase.getApprovalPhaseId());
						}
						//delete All Approval Phase By Branch Id
						repo.deleteAllAppPhaseByBranchId(companyId, wpAppRoot.getBranchId());
						//delete history current
						repo.deleteWpApprovalRoot(companyId, updateItem.getApprovalId(), wpAppRoot.getWorkplaceId(), updateItem.getHistoryId());
					}
				}
			}
			//TH: person - domain 個人別就業承認ルート
			if(objUpdateItem.getCheck()==3){
				Optional<PersonApprovalRoot> psAppRootDb = repo.getPsApprovalRoot(companyId, updateItem.getApprovalId(), objUpdateItem.getEmployeeId(), updateItem.getHistoryId());
				if(!psAppRootDb.isPresent()){
					continue;
				}
				PersonApprovalRoot psAppRoot = PersonApprovalRoot.updateSdateEdate(psAppRootDb.get(), objUpdateItem.getStartDate(), objUpdateItem.getEndDate());
				//find history previous
				List<PersonApprovalRoot> lstOld= repo.getPsApprovalRootByEdate(companyId, psAppRoot.getEmployeeId(),  endDatePrevious);
				if(lstOld.isEmpty()){// history previous is not exist
					if(objUpdateItem.getEditOrDelete()==1){//TH: edit
						repo.updatePsApprovalRoot(psAppRoot);
					}else{//TH: delete
						//get all  ApprovalPhase by BranchId
						List<ApprovalPhase> lstAPhase = repo.getAllApprovalPhasebyCode(companyId, psAppRoot.getBranchId());
						for (ApprovalPhase approvalPhase : lstAPhase) {
							//delete All Approver By Approval Phase Id
							repo.deleteAllApproverByAppPhId(companyId, approvalPhase.getApprovalPhaseId());
						}
						//delete All Approval Phase By Branch Id
						repo.deleteAllAppPhaseByBranchId(companyId, psAppRoot.getBranchId());
						//delete PsApprovalRoot
						repo.deletePsApprovalRoot(companyId, updateItem.getApprovalId(), psAppRoot.getEmployeeId(), updateItem.getHistoryId());
					}
				}else{// history previous is exist
					PersonApprovalRoot ps = lstOld.get(0);
					//check 編集後の履歴の開始年月日 > 取得した履歴の開始年月日 が falseの場合
					if(!checkStartDate(ps.getPeriod().getStartDate().toString(),objUpdateItem.getStartDate())){
						throw new BusinessException("Msg_156");
					}
					if(objUpdateItem.getEditOrDelete()==1){//edit
						//history previous 
						PersonApprovalRoot psAppRootUpdate= PersonApprovalRoot.updateSdateEdate(ps, ps.getPeriod().getStartDate().toString(), endDateUpdate);
						//update history previous
						repo.updatePsApprovalRoot(psAppRootUpdate);
						//update history current
						repo.updatePsApprovalRoot(psAppRoot);
					}else{//delete 
						PersonApprovalRoot psAppRootUpdate= PersonApprovalRoot.updateSdateEdate(ps, ps.getPeriod().getStartDate().toString(), endDateUpdate);
						//update history previous
						repo.updatePsApprovalRoot(psAppRootUpdate);
						//get all  ApprovalPhase by BranchId
						List<ApprovalPhase> lstAPhase = repo.getAllApprovalPhasebyCode(companyId, psAppRoot.getBranchId());
						for (ApprovalPhase approvalPhase : lstAPhase) {
							//delete All Approver By Approval Phase Id
							repo.deleteAllApproverByAppPhId(companyId, approvalPhase.getApprovalPhaseId());
						}
						//delete All Approval Phase By Branch Id
						repo.deleteAllAppPhaseByBranchId(companyId, psAppRoot.getBranchId());
						//delete history current
						repo.deletePsApprovalRoot(companyId, updateItem.getApprovalId(), psAppRoot.getEmployeeId(),  psAppRoot.getHistoryId());
					}
				}
			}
		}
	}
	/**
	 * check 編集後の履歴の開始年月日 > 取得した履歴の開始年月日 が falseの場合
	 * @param sDatePre
	 * @param sDateCur
	 * @return
	 */
	public boolean checkStartDate(String sDatePre, String sDateCur){
		if(sDateCur.compareTo(sDatePre)>0){
			return true;
		}
		return false;
	}
}
