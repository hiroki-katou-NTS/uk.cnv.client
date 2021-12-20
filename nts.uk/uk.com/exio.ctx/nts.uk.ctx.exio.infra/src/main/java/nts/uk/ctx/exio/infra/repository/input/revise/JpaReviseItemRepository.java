package nts.uk.ctx.exio.infra.repository.input.revise;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItem;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItemRepository;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseValue;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.codeconvert.CodeConvertDetail;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.codeconvert.ExternalImportCodeConvert;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.integer.IntegerRevise;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.string.StringRevise;
import nts.uk.ctx.exio.infra.entity.input.revise.XimmtReviseItem;
import nts.uk.ctx.exio.infra.entity.input.revise.XimmtReviseItemPK;
import nts.uk.ctx.exio.infra.entity.input.revise.type.codeconvert.XimmtCodeConvert;
import nts.uk.ctx.exio.infra.entity.input.revise.type.codeconvert.XimmtCodeConvertDetail;

@Stateless
public class JpaReviseItemRepository extends JpaRepository implements ReviseItemRepository {

	@Override
	public Optional<ReviseItem> get(String companyId, ExternalImportCode settingCode, ImportingDomainId domainId, int importItemNumber) {
		
		String sql 	= " select f "
					+ " from XimmtReviseItem f"
					+ " where f.pk.companyId =:companyID "
					+ " and f.pk.settingCode =:settingCD "
					+ " and f.pk.domainId =:domainId "
					+ " and f.pk.itemNo =:importItemNO ";
		
		val entitiesOpt = this.queryProxy().query(sql, XimmtReviseItem.class)
				.setParameter("companyID", companyId)
				.setParameter("settingCD", settingCode.toString())
				.setParameter("importItemNO", importItemNumber)
				.setParameter("domainId", domainId.value)
				.getSingle();
		
		if(!entitiesOpt.isPresent()) {
			return Optional.empty();
		}
		
		val codeConvert = getCodeConvert(companyId, settingCode, domainId, importItemNumber);
		
		return Optional.of(entitiesOpt.get().toDomain(codeConvert));
	}
	
	// コード変換のDomainId追加は要否を検討中
	private Optional<ExternalImportCodeConvert> getCodeConvert(String companyId, ExternalImportCode settingCode, ImportingDomainId domainId, int importItemNumber){
		
		String sql = " select f "
				+ " from XimmtCodeConvert f"
				+ " where f.pk.companyId =:companyId "
				+ " and f.pk.settingCode =:settingCode "
				+ " and f.pk.domainId =:domainId "
				+ " and f.pk.itemNo =:itemNo ";
		
		val entities = this.queryProxy().query(sql, XimmtCodeConvert.class)
				.setParameter("companyId", companyId)
				.setParameter("settingCode", settingCode)
				.setParameter("domainId", domainId.value)
				.setParameter("itemNo", importItemNumber)
				.getSingle();
		if(!entities.isPresent()) {
			return Optional.empty();
		}
		val details = getCodeConvertDetails(companyId, settingCode, domainId, importItemNumber);
		return Optional.of(entities.get().toDomain(details));
		
	}
	
	private List<CodeConvertDetail> getCodeConvertDetails(String companyId, ExternalImportCode settingCode, ImportingDomainId domainId, int importItemNumber){
		
		String sql = " select f "
				+ " from XimmtCodeConvertDetail f"
				+ " where f.pk.companyId =:companyId "
				+ " and f.pk.settingCode =:settingCode "
				+ " and f.pk.domainId =:domainId "
				+ " and f.pk.itemNo =:itemNo ";
		
		return this.queryProxy().query(sql, XimmtCodeConvertDetail.class)
				.setParameter("companyId", companyId)
				.setParameter("settingCode", settingCode)
				.setParameter("domainId", domainId.value)
				.setParameter("itemNo", importItemNumber)
				.getList(rec -> rec.toDomain());
	}

	@Override
	public void persist(ReviseItem reviseItem) {

		delete(XimmtReviseItemPK.of(reviseItem));

		val parent = XimmtReviseItem.toEntity(reviseItem);
		commandProxy().insert(parent);
		
		getCodeConvert(reviseItem).ifPresent(codeConvert -> {
			commandProxy().insert(XimmtCodeConvert.toEntity(parent.getPk(), codeConvert));
			
			codeConvert.getConvertDetails().forEach(detail -> {
				commandProxy().insert(XimmtCodeConvertDetail.toEntity(parent.getPk(), detail));
			});
		});
	}

	@Override
	public void delete(String companyId, ExternalImportCode settingCode, ImportingDomainId domainId, int importItemNumber) {
		delete(new XimmtReviseItemPK(companyId, settingCode.v(), domainId.value, importItemNumber));
	}

	@Override
	public void delete(String companyId, ExternalImportCode settingCode) {
		
		deleteEntity(XimmtCodeConvertDetail.class.getSimpleName(), companyId, settingCode);
		deleteEntity(XimmtCodeConvert.class.getSimpleName(), companyId, settingCode);
		deleteEntity(XimmtReviseItem.class.getSimpleName(), companyId, settingCode);
	}

	@Override
	public void delete(String companyId, ExternalImportCode settingCode, ImportingDomainId domainId, List<Integer> itemNos) {
		itemNos.stream()
				.map(itemNo -> new XimmtReviseItemPK(companyId, settingCode.v(), domainId.value,itemNo))
				.forEach(this::delete);
	}
	
	private void delete(XimmtReviseItemPK pk) {
		
		deleteEntity(XimmtCodeConvertDetail.class.getSimpleName(), pk);
		deleteEntity(XimmtCodeConvert.class.getSimpleName(), pk);
		deleteEntity(XimmtReviseItem.class.getSimpleName(), pk);
	}
	
	private void deleteEntity(String entityName, XimmtReviseItemPK reviseItem) {
		
		String jpql = " delete from " + entityName + " f"
				+ " where f.pk.companyId = :companyId "
				+ " and f.pk.settingCode = :settingCode "
				+ " and f.pk.domainId = :domainId "
				+ " and f.pk.itemNo = :itemNo ";
		
		this.getEntityManager().createQuery(jpql)
				.setParameter("companyId", reviseItem.getCompanyId())
				.setParameter("settingCode", reviseItem.getSettingCode())
				.setParameter("domainId", reviseItem.getDomainId())
				.setParameter("itemNo", reviseItem.getItemNo())
				.executeUpdate();
	}
	
	private void deleteEntity(String entityName, String companyId, ExternalImportCode settingCode) {
		
		String jpql = " delete from " + entityName + " f"
				+ " where f.pk.companyId = :companyId "
				+ " and f.pk.settingCode = :settingCode ";
		
		this.getEntityManager().createQuery(jpql)
				.setParameter("companyId", companyId)
				.setParameter("settingCode", settingCode.v())
				.executeUpdate();
	}
	
	private static Optional<ExternalImportCodeConvert> getCodeConvert(ReviseItem reviseItem) {
		
		ReviseValue r = reviseItem.getRevisingValue();
		
		if (r instanceof StringRevise) {
			return ((StringRevise) r).getCodeConvert();
		}
		
		if (r instanceof IntegerRevise) {
			return ((IntegerRevise) r).getCodeConvert();
		}
		
		return Optional.empty();
	}
}
