package nts.uk.ctx.at.schedule.infra.repository.schedule.task.taskpalette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteDisplayInfo;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteName;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganization;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganizationRepository;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteRemark;
import nts.uk.ctx.at.schedule.infra.entity.schedule.task.taskpalette.KscmtTaskPalette;
import nts.uk.ctx.at.schedule.infra.entity.schedule.task.taskpalette.KscmtTaskPaletteDetail;
import nts.uk.ctx.at.schedule.infra.entity.schedule.task.taskpalette.KscmtTaskPaletteDetailPk;
import nts.uk.ctx.at.schedule.infra.entity.schedule.task.taskpalette.KscmtTaskPalettePk;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author quytb
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaTaskPaletteOrganizationRepository extends JpaRepository implements TaskPaletteOrganizationRepository {
	private static final String SELECT_TASK_PALETTE = "SELECT a FROM KscmtTaskPalette a WHERE a.kscmtTaskPalettePk.companyId = :companyId"
			+ " AND a.kscmtTaskPalettePk.targetUnit = :targetUnit AND a.kscmtTaskPalettePk.targetId = :targetId "
			+ " ORDER BY a.kscmtTaskPalettePk.page ASC";
	
	private static final String SELECT_TASK_PALETTE_DETAIL = "SELECT a FROM KscmtTaskPaletteDetail a WHERE a.kscmtTaskPaletteDetailPk.companyId = :companyId"
			+ " AND a.kscmtTaskPaletteDetailPk.targetUnit = :targetUnit AND a.kscmtTaskPaletteDetailPk.targetId = :targetId";

	private static final String SELECT_TASK_PALETTE_DETAIL_BY_PAGE = SELECT_TASK_PALETTE_DETAIL + " AND a.kscmtTaskPaletteDetailPk.page = :page";
	
	private static final String DELETE_TASK_PALETTE_DETAIL_BY_PAGE = "DELETE FROM KscmtTaskPaletteDetail a "
			+ "WHERE a.kscmtTaskPaletteDetailPk.companyId = :companyId"
			+ " AND a.kscmtTaskPaletteDetailPk.targetUnit = :targetUnit"
			+ " AND a.kscmtTaskPaletteDetailPk.targetId = :targetId"
			+ " AND a.kscmtTaskPaletteDetailPk.page = :page";
	
	@Override
	public void insert(String companyId, TaskPaletteOrganization domain) {
		this.commandProxy().insert(this.toKscmtTaskPaletteEntity(domain));
		this.commandProxy().insertAll(this.toKscmtTaskPaletteDetailEntity(domain));
	}

	@Override
	public void update(String companyId, TaskPaletteOrganization domain) {
		List<KscmtTaskPaletteDetail> kscmtTaskPaletteDetails = this.queryProxy().query(SELECT_TASK_PALETTE_DETAIL_BY_PAGE, KscmtTaskPaletteDetail.class)
					.setParameter("companyId", companyId)
					.setParameter("targetUnit", domain.getTargetOrg().getUnit().value)
					.setParameter("targetId", domain.getTargetOrg().getTargetId())
					.setParameter("page", domain.getPage())
					.getList();
		if (!kscmtTaskPaletteDetails.isEmpty()) {
			this.getEntityManager().createQuery(DELETE_TASK_PALETTE_DETAIL_BY_PAGE)
						.setParameter("companyId", companyId)
						.setParameter("targetUnit", domain.getTargetOrg().getUnit().value)
						.setParameter("targetId", domain.getTargetOrg().getTargetId())
						.setParameter("page", domain.getPage())
						.executeUpdate();
		}
		KscmtTaskPalettePk kscmtTaskPalettePk = new KscmtTaskPalettePk(companyId, domain.getTargetOrg().getUnit().value, domain.getTargetOrg().getTargetId(), domain.getPage());
		
		Optional<KscmtTaskPalette> optional = this.queryProxy().find(kscmtTaskPalettePk, KscmtTaskPalette.class);
		if(optional.isPresent()) {
			optional.get().pageName = domain.getDisplayInfo().getName().v();
			optional.get().remark = domain.getDisplayInfo().getRemark().get().v();
			this.commandProxy().update(optional.get());					
			this.commandProxy().insertAll(this.toKscmtTaskPaletteDetailEntity(domain));
		}		
	}

	@Override
	public void delete(TargetOrgIdenInfor targetOrg, int page) {
		String companyId = AppContexts.user().companyId();
		String targetId = null;
		if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE.value) {
			targetId = targetOrg.getWorkplaceId().isPresent() ? targetOrg.getWorkplaceId().get() : null;
		} else if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE_GROUP.value) {
			targetId = targetOrg.getWorkplaceGroupId().isPresent() ? targetOrg.getWorkplaceGroupId().get() : null;
		}
		
		List<KscmtTaskPaletteDetail> kscmtTaskPaletteDetails = this.queryProxy().query(SELECT_TASK_PALETTE_DETAIL_BY_PAGE, KscmtTaskPaletteDetail.class)
					.setParameter("companyId", companyId)
					.setParameter("targetUnit", targetOrg.getUnit().value)
					.setParameter("targetId", targetId)
					.setParameter("page", page)
					.getList();
		if(!kscmtTaskPaletteDetails.isEmpty()) {
			this.commandProxy().removeAll(kscmtTaskPaletteDetails);
		}
		this.commandProxy().remove(KscmtTaskPalette.class, new KscmtTaskPalettePk(companyId, targetOrg.getUnit().value, targetId, page));		

	}

	@Override
	public Optional<TaskPaletteOrganization> getByPage(TargetOrgIdenInfor targetOrg, int page) {
		String companyId = AppContexts.user().companyId();
		String targetId = null;
		if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE.value) {
			targetId = targetOrg.getWorkplaceId().isPresent() ? targetOrg.getWorkplaceId().get() : null;
		} else if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE_GROUP.value) {
			targetId = targetOrg.getWorkplaceGroupId().isPresent() ? targetOrg.getWorkplaceGroupId().get() : null;
		}
		
		KscmtTaskPalettePk kscmtTaskPalettePk = new KscmtTaskPalettePk(companyId, targetOrg.getUnit().value, targetId, page);
		
		Optional<KscmtTaskPalette> optional = this.queryProxy().find(kscmtTaskPalettePk, KscmtTaskPalette.class);
		if(optional.isPresent()) {
			List<KscmtTaskPaletteDetail> kscmtTaskPaletteDetails = this.queryProxy().query(SELECT_TASK_PALETTE_DETAIL_BY_PAGE, KscmtTaskPaletteDetail.class)
					.setParameter("companyId", companyId)
					.setParameter("targetUnit", targetOrg.getUnit().value)
					.setParameter("targetId", targetId)
					.setParameter("page", page)
					.getList();
			return Optional.ofNullable(this.toDomain(optional.get(), kscmtTaskPaletteDetails));
		}
		return Optional.empty();
	}

	@Override
	public List<TaskPaletteOrganization> getAll(TargetOrgIdenInfor targetOrg) {
		String companyId = AppContexts.user().companyId();
		String targetId = null;
		if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE.value) {
			targetId = targetOrg.getWorkplaceId().isPresent() ? targetOrg.getWorkplaceId().get() : null;
		} else if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE_GROUP.value) {
			targetId = targetOrg.getWorkplaceGroupId().isPresent() ? targetOrg.getWorkplaceGroupId().get() : null;
		}
		
		List<KscmtTaskPalette> kscmtTaskPalettes = this.queryProxy().query(SELECT_TASK_PALETTE, KscmtTaskPalette.class)
				.setParameter("companyId", companyId)
				.setParameter("targetUnit", targetOrg.getUnit().value)				
				.setParameter("targetId", targetId)
				.getList();
		
		List<KscmtTaskPaletteDetail> kscmtTaskPaletteDetails = this.queryProxy().query(SELECT_TASK_PALETTE_DETAIL, KscmtTaskPaletteDetail.class)
				.setParameter("companyId", companyId)
				.setParameter("targetUnit", targetOrg.getUnit().value)
				.setParameter("targetId", targetId)
				.getList();
		
		return this.toListDomain(kscmtTaskPalettes, kscmtTaskPaletteDetails);
	}

	private KscmtTaskPalette toKscmtTaskPaletteEntity(TaskPaletteOrganization domain) {		
		return new KscmtTaskPalette(
				new KscmtTaskPalettePk(AppContexts.user().companyId(), 
						domain.getTargetOrg().getUnit().value, 
						domain.getTargetOrg().getUnit().value == TargetOrganizationUnit.WORKPLACE.value ? domain.getTargetOrg().getWorkplaceId().get() : domain.getTargetOrg().getWorkplaceGroupId().get(), 
						domain.getPage()),				
				domain.getDisplayInfo().getName().v(), 				
				domain.getDisplayInfo().getRemark().isPresent() ? domain.getDisplayInfo().getRemark().get().v(): null);
	}

	private List<KscmtTaskPaletteDetail> toKscmtTaskPaletteDetailEntity(TaskPaletteOrganization domain) {
		List<KscmtTaskPaletteDetail> results = new ArrayList<KscmtTaskPaletteDetail>();
		domain.getTasks().forEach((position, taskCode) -> {	
			results.add(new KscmtTaskPaletteDetail(
					new KscmtTaskPaletteDetailPk(AppContexts.user().companyId(), 
							domain.getTargetOrg().getUnit().value, 
							domain.getTargetOrg().getUnit().value == TargetOrganizationUnit.WORKPLACE.value ? domain.getTargetOrg().getWorkplaceId().get() : domain.getTargetOrg().getWorkplaceGroupId().get(),
							domain.getPage(), position), 
							taskCode.v()));
		});		
		return results;
	}
	
	private TaskPaletteOrganization toDomain(KscmtTaskPalette kscmtTaskPalette, List<KscmtTaskPaletteDetail> kscmtTaskPaletteDetails) {
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		
		TaskPaletteDisplayInfo taskPaletteDisplayInfo = new TaskPaletteDisplayInfo(
				new TaskPaletteName(kscmtTaskPalette.pageName), 
				Optional.ofNullable(new TaskPaletteRemark(kscmtTaskPalette.remark)));
		
		Map<Integer, TaskCode> tasks = new HashMap<Integer, TaskCode>();	
		if(kscmtTaskPaletteDetails != null && !kscmtTaskPaletteDetails.isEmpty()) {
			kscmtTaskPaletteDetails.stream().forEach(taskPaletteDetail ->{
				tasks.put(taskPaletteDetail.kscmtTaskPaletteDetailPk.position, new TaskCode(taskPaletteDetail.taskCode));
			});
		}		
		
		if(kscmtTaskPalette.kscmtTaskPalettePk.targetUnit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(kscmtTaskPalette.kscmtTaskPalettePk.targetId);
		}
		
		if(kscmtTaskPalette.kscmtTaskPalettePk.targetUnit == TargetOrganizationUnit.WORKPLACE_GROUP.value) {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(kscmtTaskPalette.kscmtTaskPalettePk.targetId);
		}
		return TaskPaletteOrganization.create(targetOrgIdenInfor, kscmtTaskPalette.kscmtTaskPalettePk.page, taskPaletteDisplayInfo, tasks);
	}
	
	private List<TaskPaletteOrganization> toListDomain(List<KscmtTaskPalette> kscmtTaskPalettes, List<KscmtTaskPaletteDetail> kscmtTaskPaletteDetails){
		List<TaskPaletteOrganization> results = new ArrayList<TaskPaletteOrganization>();
		if (kscmtTaskPaletteDetails != null && !kscmtTaskPaletteDetails.isEmpty()) {
			Map<String, List<KscmtTaskPaletteDetail>> listKscmtTaskPaletteDetail = kscmtTaskPaletteDetails.stream()
					.collect(Collectors.groupingBy(KscmtTaskPaletteDetail::getTargetId));
			
			if (kscmtTaskPalettes != null && !kscmtTaskPalettes.isEmpty()) {
				kscmtTaskPalettes.stream().forEach(x -> {
					results.add(
							this.toDomain(x, listKscmtTaskPaletteDetail.get(x.kscmtTaskPalettePk.targetId)));
				});
			}
		}
		return results;
	}
}
