package nts.uk.ctx.workflow.infra.repository.approvermanagement.workroot;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRootRepository;
import nts.uk.ctx.workflow.infra.entity.approvermanagement.workroot.WwfmtPsApprovalRoot;
import nts.uk.ctx.workflow.infra.entity.approvermanagement.workroot.WwfmtPsApprovalRootPK;
/**
 * 
 * @author hoatt
 *
 */
@Stateless
public class JpaPersonApprovalRootRepository extends JpaRepository implements PersonApprovalRootRepository{


	 private final String FIND_ALL = "SELECT c FROM WwfmtPsApprovalRoot c";
	 private final String FIN_BY_EMP = FIND_ALL
	   + " WHERE c.wwfmtPsApprovalRootPK.companyId = :companyId"
	   + " AND c.wwfmtPsApprovalRootPK.employeeId = :employeeId";
	 private final String FIND_BY_DATE = FIN_BY_EMP
	   + " AND c.endDate = :endDate";
	 private final String FIND_BY_BASEDATE = FIN_BY_EMP
			   + " AND c.stardDate <= :baseDate"
			   + " AND c.endDate => :baseDate"
			   + " AND c.employmentRootAtr IN (1,2,3)" 
			   + " AND c.applicationType = :appType";
	 private final String FIND_BY_BASEDATE_OF_COM = FIN_BY_EMP
			   + " AND c.stardDate <= :baseDate"
			   + " AND c.endDate => :baseDate"
			   + " AND c.employmentRootAtr = 0";
	/**
	 * get all Person Approval Root
	 * @param companyId
	 * @param employeeId
	 * @return
	 */
	@Override
	public List<PersonApprovalRoot> getAllPsApprovalRoot(String companyId, String employeeId) {
		return this.queryProxy().query(FIN_BY_EMP, WwfmtPsApprovalRoot.class)
				.setParameter("companyId", companyId)
				.setParameter("employeeId", employeeId)
				.getList(c->toDomainPsApR(c));
	}
	/**
	 * delete Person Approval Root
	 * @param companyId
	 * @param employeeId
	 * @param historyId
	 */
	@Override
	public void deletePsApprovalRoot(String companyId, String approvalId, String employeeId, String historyId) {
		WwfmtPsApprovalRootPK comPK = new WwfmtPsApprovalRootPK(companyId, approvalId, employeeId, historyId);
		this.commandProxy().remove(WwfmtPsApprovalRoot.class,comPK);
	}
	/**
	 * add Person Approval Root
	 * @param psAppRoot
	 */
	@Override
	public void addPsApprovalRoot(PersonApprovalRoot psAppRoot) {
		this.commandProxy().insert(toEntityPsApR(psAppRoot));
	}
	/**
	 * update Person Approval Root
	 * @param psAppRoot
	 */
	@Override
	public void updatePsApprovalRoot(PersonApprovalRoot psAppRoot) {
		WwfmtPsApprovalRoot a = toEntityPsApR(psAppRoot);
		WwfmtPsApprovalRoot x = this.queryProxy().find(a.wwfmtPsApprovalRootPK, WwfmtPsApprovalRoot.class).get();
		x.setStartDate(a.startDate);
		x.setEndDate(a.endDate);
		x.setApplicationType(a.applicationType);
		x.setBranchId(a.branchId);
		x.setAnyItemAppId(a.anyItemAppId);
		x.setConfirmationRootType(a.confirmationRootType);
		x.setEmploymentRootAtr(a.employmentRootAtr);
		this.commandProxy().update(x);
	}
	/**
	 * get Person Approval Root By End date
	 * @param companyId
	 * @param employeeId
	 * @param endDate
	 * @return
	 */
	@Override
	public List<PersonApprovalRoot> getPsApprovalRootByEdate(String companyId, String employeeId, GeneralDate endDate, Integer applicationType) {
		return this.queryProxy().query(FIND_BY_DATE, WwfmtPsApprovalRoot.class)
				.setParameter("companyId", companyId)
				.setParameter("employeeId", employeeId)
				.setParameter("endDate", endDate)
				.getList(c->toDomainPsApR(c));
	}
	/**
	 * get PsApprovalRoot
	 * @param companyId
	 * @param approvalId
	 * @param employeeId
	 * @param historyId
	 * @return
	 */
	@Override
	public Optional<PersonApprovalRoot> getPsApprovalRoot(String companyId, String approvalId, String employeeId, String historyId) {
		WwfmtPsApprovalRootPK pk = new WwfmtPsApprovalRootPK(companyId, approvalId, employeeId, historyId);
		return this.queryProxy().find(pk, WwfmtPsApprovalRoot.class).map(c->toDomainPsApR(c));
	}
	
	/**
	 * 個人別就業承認ルート」を取得する
	 * 就業ルート区分(申請か、確認か、任意項目か)
	 * @param cid
	 * @param sid
	 * @param baseDate
	 * @param appType
	 */
	@Override
	public List<PersonApprovalRoot> findByBaseDate(String cid, String sid, Date baseDate, String appType) {
		return this.queryProxy().query(FIND_BY_BASEDATE, WwfmtPsApprovalRoot.class)
				.setParameter("companyId", cid)
				.setParameter("employeeId", sid)
				.setParameter("baseDate", baseDate)
				.setParameter("appType", appType)
				.getList(c->toDomainPsApR(c));
	}
	
	/**
	 * 個人別就業承認ルート」を取得する
	 * 就業ルート区分(共通)
	 * @param cid
	 * @param sid
	 * @param baseDate
	 * @param appType
	 */
	@Override
	public List<PersonApprovalRoot> findByBaseDateOfCommon(String cid, String sid, Date baseDate) {
		return this.queryProxy().query(FIND_BY_BASEDATE_OF_COM, WwfmtPsApprovalRoot.class)
				.setParameter("companyId", cid)
				.setParameter("employeeId", sid)
				.setParameter("baseDate", baseDate)
				.getList(c->toDomainPsApR(c));
	}
	
	
	/**
	 * convert entity WwfmtPsApprovalRoot to domain PersonApprovalRoot
	 * @param entity
	 * @return
	 */
	private PersonApprovalRoot toDomainPsApR(WwfmtPsApprovalRoot entity){
		val domain = PersonApprovalRoot.createSimpleFromJavaType(entity.wwfmtPsApprovalRootPK.companyId,
				entity.wwfmtPsApprovalRootPK.approvalId,
				entity.wwfmtPsApprovalRootPK.employeeId,
				entity.wwfmtPsApprovalRootPK.historyId,
				entity.applicationType,
				entity.startDate.toString(),
				entity.endDate.toString(),
				entity.branchId,
				entity.anyItemAppId,
				entity.confirmationRootType,
				entity.employmentRootAtr);
		return domain;
	}
	/**
	 * convert domain PersonApprovalRoot to entity WwfmtPsApprovalRoot
	 * @param domain
	 * @return
	 */
	private WwfmtPsApprovalRoot toEntityPsApR(PersonApprovalRoot domain){
		val entity = new WwfmtPsApprovalRoot();
		entity.wwfmtPsApprovalRootPK = new WwfmtPsApprovalRootPK(domain.getCompanyId(), domain.getApprovalId(), domain.getEmployeeId(), domain.getHistoryId());
		entity.startDate = domain.getPeriod().getStartDate();
		entity.endDate = domain.getPeriod().getEndDate();
		entity.applicationType = domain.getApplicationType().value;
		entity.branchId = domain.getBranchId();
		entity.anyItemAppId = domain.getAnyItemApplicationId();
		entity.confirmationRootType = domain.getConfirmationRootType().value;
		entity.employmentRootAtr = domain.getEmploymentRootAtr().value;
		return entity;
	}
}
