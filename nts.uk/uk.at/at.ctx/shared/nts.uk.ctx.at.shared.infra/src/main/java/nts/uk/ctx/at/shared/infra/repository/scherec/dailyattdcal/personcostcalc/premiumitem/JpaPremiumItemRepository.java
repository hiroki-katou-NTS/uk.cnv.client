package nts.uk.ctx.at.shared.infra.repository.scherec.dailyattdcal.personcostcalc.premiumitem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.UseAttribute;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem.KmnmpPremiumItemPK;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem.KmnmtPremiumItem;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem.KscmtPremiumItem;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class JpaPremiumItemRepository extends JpaRepository implements PremiumItemRepository{

	private static final String FIND_ALL = "SELECT a FROM KscmtPremiumItem a WHERE a.kmnmpPremiumItemPK.companyID = :CID";
	
	private static final String FIND_BY_LIST_DISPLAY_NUMBER = FIND_ALL + " AND a.kmnmpPremiumItemPK.displayNumber IN :displayNumbers";
	
	private static final String FIND_BY_LIST_PREMIUM_NO_IS_USE = FIND_ALL +  " AND a.kmnmpPremiumItemPK.displayNumber NOT IN :displayNumbers AND a.useAtr = :useAtr";
	
//	private static final String FIND_ALL_IS_USE = FIND_ALL + " AND a.useAtr = :useAtr";
	
	@Override
	public void update(PremiumItem premiumItem) {
		KscmtPremiumItem item = this.queryProxy().find(new KmnmpPremiumItemPK(premiumItem.getCompanyID(),
				premiumItem.getDisplayNumber().value), KscmtPremiumItem.class).get();
		
		if(premiumItem.getUseAtr() == UseAttribute.Use){
			item.setUseAtr(premiumItem.getUseAtr().value);
			item.setName(premiumItem.getName().v());
		} else {
			item.setUseAtr(premiumItem.getUseAtr().value);
		}
		this.commandProxy().update(item);
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<PremiumItem> findByCompanyID(String companyID) {
		return this.queryProxy().query(FIND_ALL + " ORDER BY a.kmnmpPremiumItemPK.displayNumber", KscmtPremiumItem.class).setParameter("CID", companyID)
				.getList(x -> convertToDomain(x));
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<PremiumItem> findByCompanyIDAndDisplayNumber(String companyID, List<Integer> displayNumbers) {
		List<PremiumItem> resultList = new ArrayList<>();
		CollectionUtil.split(displayNumbers, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_LIST_DISPLAY_NUMBER, KscmtPremiumItem.class)
								  .setParameter("CID", companyID)
								  .setParameter("displayNumbers", subList)
								  .getList(x -> convertToDomain(x)));
		});
		return resultList;
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<PremiumItem> findByCompanyIDAndListPremiumNo (String companyID, List<Integer> premiumNo) {
		if (CollectionUtil.isEmpty(premiumNo)) {
			return this.findAllIsUse(companyID);
		}
		List<PremiumItem> resultList = new ArrayList<>();
		CollectionUtil.split(premiumNo, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_LIST_PREMIUM_NO_IS_USE, KscmtPremiumItem.class)
								  .setParameter("CID", companyID)
								  .setParameter("displayNumbers", subList)
								  .setParameter("useAtr", UseAttribute.Use.value)
								  .getList(x -> convertToDomain(x)));
		});
		return resultList;
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<PremiumItem> findAllIsUse (String companyID) {
//		return this.queryProxy().query(FIND_ALL_IS_USE, KscmtPremiumItem.class)
//				.setParameter("CID", companyID)
//				.setParameter("useAtr", UseAttribute.Use.value)
//				.getList(x -> convertToDomain(x));
		
		try (val statement = this.connection().prepareStatement("select * FROM KSRMT_PREMIUM_ITEM where CID = ? and USE_ATR = ?")) {
			statement.setString(1, companyID);
			statement.setInt(2, UseAttribute.Use.value);
			return new NtsResultSet(statement.executeQuery()).getList(rec -> {
				val entity = new KscmtPremiumItem();
				entity.kmnmpPremiumItemPK = new KmnmpPremiumItemPK();
				entity.kmnmpPremiumItemPK.companyID = companyID;
				entity.kmnmpPremiumItemPK.displayNumber = rec.getInt("PREMIUM_NO");
				entity.useAtr = UseAttribute.Use.value;
				entity.name = rec.getString("PREMIUM_NAME");
				return entity;
			}).stream().map(e -> convertToDomain(e)).collect(Collectors.toList());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * convert PremiumItem Entity Object to PremiumItem Domain Object
	 * @param kmnmtPremiumItem PremiumItem Entity Object
	 * @return PremiumItem Domain Object
	 */ 
	private PremiumItem convertToDomain(KscmtPremiumItem kmnmtPremiumItem){
		return new PremiumItem(
				kmnmtPremiumItem.kmnmpPremiumItemPK.companyID, 
				ExtraTimeItemNo.valueOf(kmnmtPremiumItem.kmnmpPremiumItemPK.displayNumber),
				new PremiumName(kmnmtPremiumItem.name), 
				EnumAdaptor.valueOf(kmnmtPremiumItem.useAtr, UseAttribute.class));
	}

	
}
