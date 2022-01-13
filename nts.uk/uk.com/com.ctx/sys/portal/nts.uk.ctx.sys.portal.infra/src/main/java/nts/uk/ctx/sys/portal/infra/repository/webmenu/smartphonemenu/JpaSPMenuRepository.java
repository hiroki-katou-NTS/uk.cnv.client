package nts.uk.ctx.sys.portal.infra.repository.webmenu.smartphonemenu;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.portal.dom.standardmenu.MenuCode;
import nts.uk.ctx.sys.portal.dom.webmenu.smartphonemenu.SPMenuEmployment;
import nts.uk.ctx.sys.portal.dom.webmenu.smartphonemenu.SPMenuGroup;
import nts.uk.ctx.sys.portal.dom.webmenu.smartphonemenu.SPMenuOrder;
import nts.uk.ctx.sys.portal.dom.webmenu.smartphonemenu.SPMenuRepository;
import nts.uk.ctx.sys.portal.infra.entity.webmenu.smartphonemenu.SptmtSPMenuGroup;
import nts.uk.ctx.sys.portal.infra.entity.webmenu.smartphonemenu.SptmtSPMenuK;
import nts.uk.ctx.sys.portal.infra.entity.webmenu.smartphonemenu.SptmtSPMenuKPK;
import nts.uk.ctx.sys.portal.infra.entity.webmenu.smartphonemenu.SptmtSPMenuSort;

/**
 * 
 * @author sonnh1
 *
 */
@Stateless
public class JpaSPMenuRepository extends JpaRepository implements SPMenuRepository {

	private static final String SEL = "select s from SptmtSPMenuK s ";
	private static final String SEL_GROUP = "select s from SptmtSPMenuGroup s ";
	private static final String SEL_SORT = "select s from SptmtSPMenuSort s ";
	private static final String SEL_BY_CID = SEL + "where s.companyId = :companyId";
	private static final String SEL_MENU_EMP = SEL
			+ "where s.companyId = :companyId and s.pk.roleId = :roleId and s.pk.menuCode IN :lstMenuCd";
	private static final String SEL_GROUP_BY_CID_LSTCODE = SEL_GROUP
			+ "where s.pk.companyId = :companyId and s.pk.code IN :lstCode";
	private static final String SEL_SORT_BY_CID_LSTCODE = SEL_SORT
			+ "where s.pk.companyId = :companyId and s.pk.menuCode IN :lstCode order by s.sortOrder asc";
	
	private static final String SEL_SP_MN_EMP = "SELECT a FROM SptmtSPMenuK a "
			+ "WHERE a.companyId = :companyID AND a.pk.roleId = :roleID AND a.dispAtr = :dispAtr";
	
	private static final String SEL_GROUP_BY_CID_LSTSUBCODE = SEL_GROUP
			+ "where s.pk.companyId = :companyId and s.pk.subCode IN :lstCode";
	
	private static final String SEL_SORT_BY_CID = SEL_SORT
			+ "where s.pk.companyId = :companyId order by s.sortOrder asc";

	@Override
	public List<SPMenuEmployment> getDataEmp(String companyId) {
		return this.queryProxy().query(SEL_BY_CID, SptmtSPMenuK.class).setParameter("companyId", companyId)
				.getList(t -> toDomainEmp(t));
	}

	@Override
	public List<SPMenuEmployment> getDataEmp(String companyId, String empRoleId, List<String> lstMenuCd) {
		return this.queryProxy().query(SEL_MENU_EMP, SptmtSPMenuK.class).setParameter("companyId", companyId)
				.setParameter("roleId", empRoleId).setParameter("lstMenuCd", lstMenuCd).getList(t -> toDomainEmp(t));
	}

	@Override
	public List<SPMenuGroup> getDataGroup(String companyId, List<String> lstCode) {
		List<SptmtSPMenuGroup> lstSptmtSPMenuGroup = this.queryProxy()
				.query(SEL_GROUP_BY_CID_LSTCODE, SptmtSPMenuGroup.class).setParameter("companyId", companyId)
				.setParameter("lstCode", lstCode).getList();
		return this.toDomainGroup(lstSptmtSPMenuGroup);
	}

	@Override
	public List<SPMenuOrder> getDataOrder(String companyId, List<String> lstCode) {
		return this.queryProxy().query(SEL_SORT_BY_CID_LSTCODE, SptmtSPMenuSort.class)
				.setParameter("companyId", companyId).setParameter("lstCode", lstCode).getList(t -> toDomainSort(t));
	}

	private List<SPMenuGroup> toDomainGroup(List<SptmtSPMenuGroup> s) {
		List<SPMenuGroup> results = new ArrayList<>();
		Map<String, Map<String, List<String>>> map = s.stream().collect(Collectors.groupingBy(x -> x.pk.companyId,
				Collectors.groupingBy(x -> x.pk.code, Collectors.mapping(x -> x.pk.subCode, Collectors.toList()))));
		map.entrySet().forEach(entry -> {
			results.addAll(entry.getValue().entrySet().stream()
					.map(x -> SPMenuGroup.createFromJavaType(entry.getKey(), x.getKey(), x.getValue()))
					.collect(Collectors.toList()));
		});
		return results;
	}

	private SPMenuOrder toDomainSort(SptmtSPMenuSort s) {
		return SPMenuOrder.createFromJavaType(s.pk.companyId, s.pk.menuCode, s.sortOrder);
	}

	private SPMenuEmployment toDomainEmp(SptmtSPMenuK s) {
		return SPMenuEmployment.createFromJavaType(s.companyId, s.pk.roleId, s.pk.menuCode, BooleanUtils.toInteger(s.dispAtr));
	}

	private SptmtSPMenuK toEntityEmp(SPMenuEmployment s) {
		return new SptmtSPMenuK(new SptmtSPMenuKPK(s.getEmploymentRole(), s.getMenuCode().v()), s.getCompanyId(),
				BooleanUtils.toBoolean(s.getDisplayAtr().value));
	}

	@Override
	public void add(List<SPMenuEmployment> lstSPMenuEmployment) {
		this.commandProxy()
				.insertAll(lstSPMenuEmployment.stream().map(x -> toEntityEmp(x)).collect(Collectors.toList()));

	}

	@Override
	public List<SPMenuEmployment> findSPMenuEmploymentUse(String companyID, String roleID) {
		return this.queryProxy().query(SEL_SP_MN_EMP, SptmtSPMenuK.class).setParameter("companyID", companyID)
				.setParameter("roleID", roleID).setParameter("dispAtr", true).getList(t -> toDomainEmp(t));
	}

	@Override
	public List<SPMenuOrder> findSPMenuOrderASC(String companyID) {
		return this.queryProxy().query(SEL_SORT_BY_CID, SptmtSPMenuSort.class)
				.setParameter("companyId", companyID).getList(t -> toDomainSort(t));
	}

	@Override
	public List<SPMenuGroup> findSPMenuGroupSubList(String companyID, List<MenuCode> subLst) {
		List<SptmtSPMenuGroup> lstSptmtSPMenuGroup = this.queryProxy()
				.query(SEL_GROUP_BY_CID_LSTSUBCODE, SptmtSPMenuGroup.class).setParameter("companyId", companyID)
				.setParameter("lstCode", subLst).getList();
		return this.toDomainGroup(lstSptmtSPMenuGroup);
	}

	@Override
	public void update(List<SPMenuEmployment> lstSPMenuEmployment) {
		List<SptmtSPMenuK> entities = lstSPMenuEmployment.stream().map(x->toEntityEmp(x)).collect(Collectors.toList());
		this.commandProxy().updateAll(entities);
	}

}
