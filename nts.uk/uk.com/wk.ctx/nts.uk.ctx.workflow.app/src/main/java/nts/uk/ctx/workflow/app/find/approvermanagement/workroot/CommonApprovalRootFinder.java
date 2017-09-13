package nts.uk.ctx.workflow.app.find.approvermanagement.workroot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhase;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhaseRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApproverRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.CompanyApprovalRootRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRootRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRootRepository;
import nts.uk.shr.com.company.CompanyAdapter;
import nts.uk.shr.com.company.CompanyInfor;
import nts.uk.shr.com.context.AppContexts;
/**
 * 
 * @author hoatt
 *
 */
@Stateless
public class CommonApprovalRootFinder {
	@Inject
	private CompanyAdapter comAdapter;
	@Inject
	private PersonApprovalRootRepository repo;
	@Inject
	private CompanyApprovalRootRepository repoCom;
	@Inject
	private WorkplaceApprovalRootRepository repoWorkplace;
	@Inject
	private ApprovalPhaseRepository repoAppPhase;
	@Inject
	private ApproverRepository repoApprover;
	
	public DataFullDto getAllCommonApprovalRoot(ParamDto param){
		CommonApprovalRootDto a = getAllDataApprovalRoot(param);
		//TH: company - domain 会社別就業承認ルート
		if(param.getRootType() == 0){
			List<CompanyAppRootDto> lstCompanyRoot = a.getLstCompanyRoot();
			ObjGrouping result = this.groupingMapping(a.getLstCompanyRoot());
			List<ObjectDate> b = result.getLstkk();
			List<ObjDate> lstTrung = result.getLstApprovalId();
			List<DataDisplayComDto> kq = new ArrayList<>();
			int index = 0;
			for (ObjectDate objDate : b) {
				List<CompanyAppRootDto> lstItem = new ArrayList<>();
				ObjDate it = new ObjDate(objDate.getStartDate(), objDate.getEndDate());
				if(lstTrung.contains(it)){// duoc gop
					for (CompanyAppRootDto com : lstCompanyRoot) {
						if(com.getCompany().getStartDate().compareTo(objDate.getStartDate())==0 && com.getCompany().getEndDate().compareTo(objDate.getEndDate())==0){
							lstItem.add(com);
						}
					}
				}else{//khong duoc gop
					for (CompanyAppRootDto com : lstCompanyRoot) {
						if(com.getCompany().getApprovalId().compareTo(objDate.getAprovalId())==0){
							lstItem.add(com);
						}
					}
				}
				kq.add(new DataDisplayComDto(index,a.getCompanyName(), lstItem));
				index++;
			}
			return new DataFullDto(kq, null, null);
		}
		//TH: workplace - domain 職場別就業承認ルート
		if(param.getRootType() == 1){
			return new DataFullDto(null, null, null);
		}
		//TH: person - domain 個人別就業承認ルート
		else{
			return new DataFullDto(null, null, null);
		}
		

	}
	public CommonApprovalRootDto getPrivate(ParamDto param){
		return getAllDataApprovalRoot(param);
	}
	private CommonApprovalRootDto getAllDataApprovalRoot(ParamDto param){
		//user contexts
		String companyId = AppContexts.user().companyId();
		//get name company
//		Optional<CompanyInfor> companyCurrent = comAdapter.getCurrentCompany();
//		String companyName = companyCurrent == null ? "" : companyCurrent.get().getCompanyName();
		String companyName = "KAkashi";
		//TH: company - domain 会社別就業承認ルート
		if(param.getRootType() == 0){
			List<CompanyAppRootDto> lstComRoot = new ArrayList<>();
			//get all data from ComApprovalRoot (会社別就業承認ルート)
			List<ComApprovalRootDto> lstCom = this.repoCom.getAllComApprovalRoot(companyId)
								.stream()
								.map(c->ComApprovalRootDto.fromDomain(c))
								.collect(Collectors.toList());
			for (ComApprovalRootDto companyApprovalRoot : lstCom) {
				List<ApprovalPhaseDto> lstApprovalPhase = new ArrayList<>();
				List<ApproverDto> lstApprover = new ArrayList<ApproverDto>();
				//get All Approval Phase by BranchId
				List<ApprovalPhase> lstAppPhase = this.repoAppPhase.getAllApprovalPhasebyCode(companyId, companyApprovalRoot.getBranchId());
				for (ApprovalPhase approvalPhase : lstAppPhase) {
					//get All Approver By ApprovalPhaseId
					lstApprover = this.repoApprover.getAllApproverByCode(companyId, approvalPhase.getApprovalPhaseId())
								.stream()
								.map(c->ApproverDto.fromDomain(c))
								.collect(Collectors.toList());
					//lst (ApprovalPhase + lst Approver)
					lstApprovalPhase.add(new ApprovalPhaseDto(lstApprover, approvalPhase.getBranchId(),approvalPhase.getApprovalPhaseId(),
							approvalPhase.getApprovalForm().value, approvalPhase.getBrowsingPhase(), approvalPhase.getOrderNumber()));
				}
				//add in lstAppRoot
				lstComRoot.add(new CompanyAppRootDto(companyApprovalRoot,lstApprovalPhase));
			}
			return new CommonApprovalRootDto(companyName,lstComRoot, null, null);
		}
		//TH: workplace - domain 職場別就業承認ルート
		if(param.getRootType() == 1){
			List<WorkPlaceAppRootDto> lstWpRoot = new ArrayList<>();
			//get all data from WorkplaceApprovalRoot (職場別就業承認ルート)
			List<WpApprovalRootDto> lstWp = this.repoWorkplace.getAllWpApprovalRoot(companyId, param.getWorkplaceId())
					.stream()
					.map(c->WpApprovalRootDto.fromDomain(c))
					.collect(Collectors.toList());
			for (WpApprovalRootDto workplaceApprovalRoot : lstWp) {
				List<ApprovalPhaseDto> lstApprovalPhase = new ArrayList<>();
				List<ApproverDto> lstApprover = new ArrayList<>();
				//get All Approval Phase by BranchId
				List<ApprovalPhase> lstAppPhase = this.repoAppPhase.getAllApprovalPhasebyCode(companyId, workplaceApprovalRoot.getBranchId());
				for (ApprovalPhase approvalPhase : lstAppPhase) {
					//get All Approver By ApprovalPhaseId
					lstApprover = this.repoApprover.getAllApproverByCode(companyId, approvalPhase.getApprovalPhaseId())
							.stream()
							.map(c->ApproverDto.fromDomain(c))
							.collect(Collectors.toList());
					//lst (ApprovalPhase + lst Approver)
					lstApprovalPhase.add(new ApprovalPhaseDto(lstApprover, approvalPhase.getBranchId(),approvalPhase.getApprovalPhaseId(),
							approvalPhase.getApprovalForm().value, approvalPhase.getBrowsingPhase(), approvalPhase.getOrderNumber()));
				}
				//add in lstAppRoot
				lstWpRoot.add(new WorkPlaceAppRootDto(workplaceApprovalRoot,lstApprovalPhase));
			}
			return new CommonApprovalRootDto(companyName, null, lstWpRoot, null);
		}
		//TH: person - domain 個人別就業承認ルート
		else{
			List<PersonAppRootDto> lstPsRoot = new ArrayList<>();
			//get all data from PersonApprovalRoot (個人別就業承認ルート)
			List<PsApprovalRootDto> lstPs = this.repo.getAllPsApprovalRoot(companyId, param.getEmployeeId())
					.stream()
					.map(c->PsApprovalRootDto.fromDomain(c))
					.collect(Collectors.toList());
			for (PsApprovalRootDto personApprovalRoot : lstPs) {
				List<ApprovalPhaseDto> lstApprovalPhase = new ArrayList<>();
				List<ApproverDto> lstApprover = new ArrayList<>();
				//get All Approval Phase by BranchId
				List<ApprovalPhase> lstAppPhase = this.repoAppPhase.getAllApprovalPhasebyCode(companyId, personApprovalRoot.getBranchId());
				for (ApprovalPhase approvalPhase : lstAppPhase) {
					//get All Approver By ApprovalPhaseId
					lstApprover = this.repoApprover.getAllApproverByCode(companyId, approvalPhase.getApprovalPhaseId())
							.stream()
							.map(c->ApproverDto.fromDomain(c))
							.collect(Collectors.toList());
					//lst (ApprovalPhase + lst Approver)
					lstApprovalPhase.add(new ApprovalPhaseDto(lstApprover, approvalPhase.getBranchId(),approvalPhase.getApprovalPhaseId(),
							approvalPhase.getApprovalForm().value, approvalPhase.getBrowsingPhase(), approvalPhase.getOrderNumber()));
				}
				//add in lstAppRoot
				lstPsRoot.add(new PersonAppRootDto(personApprovalRoot,lstApprovalPhase));
			}
			return new CommonApprovalRootDto(companyName, null, null, lstPsRoot);
		}
	}
	/**
	 * grouping history
	 * @param lstRoot
	 * @return
	 */
	private ObjGrouping groupingMapping(List<CompanyAppRootDto> lstRoot){
		List<ComApprovalRootDto> origin = new ArrayList<ComApprovalRootDto>();
		List<ObjDate> result = new ArrayList<ObjDate>();
		List<ObjectDate> kkk = new ArrayList<>();
		List<ObjDate> check = new ArrayList<>();
		lstRoot.forEach(item ->{
			origin.add(item.getCompany());
		});
		boolean aaa = true;
		for (ComApprovalRootDto date1 : origin) {
			for (ComApprovalRootDto date2 : origin) {
				if (date1.getApprovalId() != date2.getApprovalId() && isOverlap(date1,date2)){//chong cheo
					aaa = false;
					break;
				}
				aaa = true;
			}
			if(aaa){//khong bi chong cheo voi ai ca.
				ObjDate xx = new ObjDate(date1.getStartDate(), date1.getEndDate());
				if(!result.contains(xx)){//co roi
					result.add(new ObjDate(date1.getStartDate(), date1.getEndDate()));
					kkk.add(new ObjectDate(date1.getApprovalId(),date1.getStartDate(), date1.getEndDate()));
				}else{
					if(!check.contains(xx)){
						check.add(new ObjDate(date1.getStartDate(), date1.getEndDate()));
					}
					
				}
			}else{//co bi chong cheo
				result.add(new ObjDate(date1.getStartDate(), date1.getEndDate()));
				kkk.add(new ObjectDate(date1.getApprovalId(),date1.getStartDate(), date1.getEndDate()));
			}

		}
		return new ObjGrouping(check, kkk);
		
	}
	
	/**
	 * ktra date2 co nam trong date1 k? 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public boolean isOverlap(ComApprovalRootDto date1, ComApprovalRootDto date2){
		/**
		 * date 1.........|..............]..........
		 * date 2............|......................
		 * sDate1<sDate2
		 */
		if (date2.getStartDate().compareTo(date1.getStartDate()) > 0
				&& date2.getStartDate().compareTo(date1.getEndDate()) < 0) {
			return true;
		}
		/**
		 * date 1.........|..............]..........
		 * date 2.....|........]....................
		 * sDate2<sDate1 va eDate2>sDate1
		 */
		if (date2.getStartDate().compareTo(date1.getStartDate()) < 0
				&& date2.getEndDate().compareTo(date1.getStartDate()) > 0) {
			return true;
		}
		/**
		 * date 1.........|..............]..........
		 * date 2.............]....................
		 * sDate1<eDate2<eDate1
		 */
		if (date1.getStartDate().compareTo(date2.getEndDate()) < 0
				&& date2.getEndDate().compareTo(date1.getEndDate()) < 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is same date.
	 *
	 * @param date1 the date 1
	 * @param date2 the date 2
	 * @return true, if is same date
	 */
	public boolean isSameDate(ComApprovalRootDto date1, ComApprovalRootDto date2){
		if (date2.getStartDate().compareTo(date1.getStartDate()) == 0
				&& date2.getEndDate().compareTo(date1.getEndDate()) == 0) {
			return true;
		}
		return false;
	}
}
