/**
 * 
 */
package nts.uk.ctx.at.record.infra.repository.workrecord.operationsetting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.ChangeableWorktypeGroup;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmpRepo;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmployment;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtChangeableWktpGrp;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtChangeableWktpGrpDetail;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtChangeableWktpGrpDetailPk;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtChangeableWktpGrpPk;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author danpv
 *
 */
@Stateless
public class JpaWorkingTypeChangedByEmploymentRepository extends JpaRepository implements WorkingTypeChangedByEmpRepo {

//	private static final String GET_ALL_OF_EMPLOYEE = "SELECT wtc FROM KrcmtWorktypeChangeable wtc"
//			+ " WHERE wtc.pk.cid = :companyId AND wtc.pk.empCode = :employeeCode";
//	
	private static final String GET_ALL_OF_EMPLOYEE_FOR_GRP = "SELECT wtc FROM KrcmtChangeableWktpGrp wtc"
			+ " WHERE wtc.pk.cid = :companyId AND wtc.pk.empCd = :employeeCode";
	
	private static final String GET_ALL_OF_EMPLOYEE_FOR_GRP_DETAIL = "SELECT wtc FROM KrcmtChangeableWktpGrpDetail wtc"
			+ " WHERE wtc.pk.cid = :companyId AND wtc.pk.empCd = :employeeCode";
	
	private static final String GET_EMPCODE_BY_COMPANYID = "SELECT DISTINCT wtc.pk.empCd FROM KrcmtChangeableWktpGrpDetail wtc"
			+ " WHERE wtc.pk.cid = :companyId";

	@Override
	public WorkingTypeChangedByEmployment get(CompanyId companyId, EmploymentCode empCode) {
		List<KrcmtChangeableWktpGrpDetail> entitesGrpDetail = this.queryProxy()
				.query(GET_ALL_OF_EMPLOYEE_FOR_GRP_DETAIL, KrcmtChangeableWktpGrpDetail.class).setParameter("companyId", companyId.v())
				.setParameter("employeeCode", empCode.v()).getList();
		List<KrcmtChangeableWktpGrp> entitesGrp = this.queryProxy()
				.query(GET_ALL_OF_EMPLOYEE_FOR_GRP, KrcmtChangeableWktpGrp.class).setParameter("companyId", companyId.v())
				.setParameter("employeeCode", empCode.v()).getList();
		
//		EA修正　NO.3995
//		if (entities.isEmpty()) {
//			// default company
//			entities = this.queryProxy().query(GET_ALL_OF_EMPLOYEE, KrcmtWorktypeChangeable.class)
//					.setParameter("companyId", "000000000000-0000").setParameter("employeeCode", "0").getList();
//		}
		Map<Integer, ChangeableWorktypeGroup> map = new HashMap<>();

		entitesGrp.forEach(entGrp -> {
			ChangeableWorktypeGroup group = null;
			if (!map.containsKey(entGrp.pk.workTypeGroupNo.intValue())) {
				group = new ChangeableWorktypeGroup(entGrp.pk.workTypeGroupNo.intValue(), entGrp.workTypeGroupName);
				map.put(entGrp.pk.workTypeGroupNo.intValue(), group);
				
			}
		});
		
		entitesGrpDetail.forEach(entGrpDetail -> {
			ChangeableWorktypeGroup group = null; 
			if (!map.containsKey(entGrpDetail.pk.workTypeGroupNo.intValue())) {
				group = new ChangeableWorktypeGroup(entGrpDetail.pk.workTypeGroupNo.intValue(), null);
			} else {
				group = map.get(entGrpDetail.pk.workTypeGroupNo.intValue());
			}
			group.getWorkTypeList().add(entGrpDetail.pk.workTypeCd);
			map.put(entGrpDetail.pk.workTypeGroupNo.intValue(), group);
		});

		List<ChangeableWorktypeGroup> groups = new ArrayList<>(map.values());
		groups.forEach(group -> {
			if (group.getWorkTypeList().size() == 1) {
				List<String> workTypes = new ArrayList<>(group.getWorkTypeList());
				if (workTypes.get(0) == "") {
					group.getWorkTypeList().clear();
				}
			}
		});

		return new WorkingTypeChangedByEmployment(companyId, empCode, new ArrayList<>(map.values()));
	}

	@Override
	public void save(WorkingTypeChangedByEmployment workingType) {
		String cid = workingType.getCompanyId().v();
		String empCode = workingType.getEmpCode().v();

		// delete all old results
		List<KrcmtChangeableWktpGrp> deleteEntitiesGrp = this.queryProxy()
				.query(GET_ALL_OF_EMPLOYEE_FOR_GRP, KrcmtChangeableWktpGrp.class).setParameter("companyId", cid)
				.setParameter("employeeCode", empCode).getList();
		this.commandProxy().removeAll(deleteEntitiesGrp);
		this.getEntityManager().flush();
		
		List<KrcmtChangeableWktpGrpDetail> deleteEntitiesGrpDetail = this.queryProxy()
				.query(GET_ALL_OF_EMPLOYEE_FOR_GRP_DETAIL, KrcmtChangeableWktpGrpDetail.class).setParameter("companyId", cid)
				.setParameter("employeeCode", empCode).getList();
		this.commandProxy().removeAll(deleteEntitiesGrpDetail);
		this.getEntityManager().flush();
		
		// create and insert new results
		workingType.getChangeableWorkTypeGroups().forEach(group -> {
			
			if (group.getNo() >= 5 && group.getNo() <= 10 && group.getWorkTypeList() != null && group.getWorkTypeList().size() > 0 ) {
				KrcmtChangeableWktpGrpPk grpPk = new KrcmtChangeableWktpGrpPk(cid, empCode, new BigDecimal(group.getNo()));
				KrcmtChangeableWktpGrp grpEntity = new KrcmtChangeableWktpGrp(grpPk, group.getName() == null ? null : group.getName().v());
				this.commandProxy().insert(grpEntity);
			}
			
			if(group.getWorkTypeList() != null && group.getWorkTypeList().size() > 0) {
				group.getWorkTypeList().forEach(workTypeCode -> {
					KrcmtChangeableWktpGrpDetailPk grpDetailPk = new KrcmtChangeableWktpGrpDetailPk(cid, empCode, new BigDecimal(group.getNo()), workTypeCode);
					KrcmtChangeableWktpGrpDetail grpDetailEntity = new KrcmtChangeableWktpGrpDetail(grpDetailPk);
					this.commandProxy().insert(grpDetailEntity);
				});
			}
		});

	}

	@Override
	public List<String> checkSetting(String companyId, List<String> empCode) {
		String query = "SELECT wtc FROM KrcmtChangeableWktpGrpDetail wtc"
				+ " WHERE wtc.pk.cid = :companyId AND wtc.pk.empCd IN :employeeCode AND wtc.pk.workTypeCd != '　'";
		List<KrcmtChangeableWktpGrpDetail> entities = new ArrayList<>();
		CollectionUtil.split(empCode, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			entities.addAll(this.queryProxy().query(query, KrcmtChangeableWktpGrpDetail.class)
								.setParameter("companyId", companyId)
								.setParameter("employeeCode", subList).getList());
		});
		
		if(entities.isEmpty()) {
			new ArrayList<>();
		}
		List<String> emps = entities.stream().map(e -> e.pk.empCd).collect(Collectors.toList());
		return empCode.stream().filter(e -> emps.contains(e)).collect(Collectors.toList());
	}

	@Override
	public void copyEmployment(String companyId, WorkingTypeChangedByEmployment sourceData,
			List<String> targetEmploymentCodes) {
		//INPUT．「複写先リスト」をループする
		for (String target : targetEmploymentCodes) {
			//複写先のドメインモデル「雇用別の変更可能な勤務種類」を削除する
			List<KrcmtChangeableWktpGrp> deleteEntitiesGrp = this.queryProxy()
					.query(GET_ALL_OF_EMPLOYEE_FOR_GRP, KrcmtChangeableWktpGrp.class).setParameter("companyId", companyId)
					.setParameter("employeeCode", target).getList();
			this.commandProxy().removeAll(deleteEntitiesGrp);
			this.getEntityManager().flush();
			
			List<KrcmtChangeableWktpGrpDetail> deleteEntitiesGrpDetail = this.queryProxy()
					.query(GET_ALL_OF_EMPLOYEE_FOR_GRP_DETAIL, KrcmtChangeableWktpGrpDetail.class).setParameter("companyId", companyId)
					.setParameter("employeeCode", target).getList();
			this.commandProxy().removeAll(deleteEntitiesGrpDetail);
			this.getEntityManager().flush();
			
			//ドメインモデル「雇用別の変更可能な勤務種類」を新規登録する
			addEmploymentSet(sourceData, target);
		}
		
	}
	
	/**
	 * 複写先の前準備設定を追加する (Add)
	 * @param sourceData: source data copy
	 * @param employmentCode: target employment code
	 */
	private void addEmploymentSet(WorkingTypeChangedByEmployment sourceData, String employmentCode){
		String companyId = AppContexts.user().companyId();
		WorkingTypeChangedByEmployment search = this.get(new CompanyId(companyId), new EmploymentCode(employmentCode));
		search.setChangeableWorkTypeGroups(sourceData.getChangeableWorkTypeGroups());
		this.save(search);
	}

	@Override
	public void deleteEmploymentSetting(String companyId, String empCode) {
		// Get list KrcmtChangeableWktpGrp by companyID && employmentCode => remove
		List<KrcmtChangeableWktpGrp> deleteEmpGrp = this.queryProxy()
														.query(GET_ALL_OF_EMPLOYEE_FOR_GRP, KrcmtChangeableWktpGrp.class)
														.setParameter("companyId", companyId)
														.setParameter("employeeCode", empCode)
														.getList();
		this.commandProxy().removeAll(deleteEmpGrp);
		
		// Get list KrcmtChangeableWktpGrpDetail by companyID && employmentCode => remove
		List<KrcmtChangeableWktpGrpDetail> deleteGrpDetail = this.queryProxy()
														.query(GET_ALL_OF_EMPLOYEE_FOR_GRP_DETAIL, KrcmtChangeableWktpGrpDetail.class)
														.setParameter("companyId", companyId)
														.setParameter("employeeCode", empCode)
														.getList();
		
		this.commandProxy().removeAll(deleteGrpDetail);
	}

	@Override
	public List<String> getDistinctEmpCodeByCompanyId(String companyId) {
		List<String> EmpCodes = this.queryProxy().query(GET_EMPCODE_BY_COMPANYID, String.class).setParameter("companyId", companyId).getList();
		return EmpCodes;
				
	}
	

}
