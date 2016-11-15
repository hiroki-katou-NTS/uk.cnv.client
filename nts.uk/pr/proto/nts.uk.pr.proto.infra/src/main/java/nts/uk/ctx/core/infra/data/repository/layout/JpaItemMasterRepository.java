package nts.uk.ctx.core.infra.data.repository.layout;

import java.util.List;
import java.util.Optional;
import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.pr.proto.dom.itemmaster.ItemMaster;
import nts.uk.ctx.pr.proto.dom.itemmaster.ItemMasterRepository;
import nts.uk.ctx.pr.proto.infra.entity.paymentdata.QcamtItem;

public class JpaItemMasterRepository extends JpaRepository implements ItemMasterRepository {
	
	private final String SELECT_NO_WHERE = "SELECT c FROM QcamtItem c";
	private final String FIND_ALL_ITEMS = SELECT_NO_WHERE
			+ " WHERE c.qcamtItemPK.ccd = :companyCode"
			+ " AND c.qcamtItemPK.ctgAtr = :categoryType";
	private final String FIND_ITEM = FIND_ALL_ITEMS 
			+ " AND c.qcamtItemPK.itemCd = :itemCode";
	/**
	 * find all item master by company code, category type
	 */
	@Override
	public List<ItemMaster> findAll(String companyCode, int categoryType) {		
		return this.queryProxy().query(FIND_ALL_ITEMS, QcamtItem.class)
				.setParameter("companyCode", companyCode)
				.setParameter("categoryType", categoryType)
				.getList(c -> toDomain(c));
	}	

	private static ItemMaster toDomain(QcamtItem entity) {
		val domain = ItemMaster.createSimpleFromJavaType(
				entity.qcamtItemPK.ccd, 
				entity.qcamtItemPK.itemCd, 
				entity.qcamtItemPK.ctgAtr, 
				entity.itemName);
		entity.toDomain(domain);
		return domain;
	}

	@Override
	/**
	 * find item by company code, category type, item code
	 */
	public Optional<ItemMaster> find(String companyCode, int categoryType, String itemCode) {
		return this.queryProxy().query(FIND_ITEM, QcamtItem.class)
				.setParameter("companyCode", companyCode)
				.setParameter("categoryType", categoryType)
				.setParameter("itemCd", itemCode)
				.getSingle(c -> toDomain(c));
	}

}
