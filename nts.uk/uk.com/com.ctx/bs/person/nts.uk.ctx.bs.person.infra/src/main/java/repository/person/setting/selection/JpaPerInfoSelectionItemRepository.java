package repository.person.setting.selection;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import entity.person.setting.selection.BpsmtSelectionItem;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.person.dom.person.setting.selection.IPerInfoSelectionItemRepository;
import nts.uk.ctx.bs.person.dom.person.setting.selection.PerInfoSelectionItem;

@Stateless
public class JpaPerInfoSelectionItemRepository extends JpaRepository implements IPerInfoSelectionItemRepository {

	private static final String SELECT_ALL = "SELECT si FROM BpsmtSelectionItem si";

	private static final String SELECT_ALL_PERSON_INFO_SELECTION_ITEMS_BY_CONTRACTCODE_QUERY = SELECT_ALL
			+ " WHERE si.contractCd = :contractCd";
	
	private static final String SELECT_PERSON_INFO_SELECTION_ITEM_BY_SELECTIONITEMID_QUERY = SELECT_ALL
			+ "WHERE si.selectionItemId = :selectionItemId";

	@Override
	public void add(PerInfoSelectionItem perInfoSelectionItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(PerInfoSelectionItem perInfoSelectionItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(PerInfoSelectionItem perInfoSelectionItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PerInfoSelectionItem> getAllPerInfoSelectionItem(String contractCd) {
		return this.queryProxy()
				.query(SELECT_ALL_PERSON_INFO_SELECTION_ITEMS_BY_CONTRACTCODE_QUERY, BpsmtSelectionItem.class)
				.setParameter("contractCd", contractCd).getList(c -> toDomain(c));
	}

	private PerInfoSelectionItem toDomain(BpsmtSelectionItem entity) {
		
		return PerInfoSelectionItem.createFromJavaType(entity.selectionItemPk.selectionItemId, entity.selectionItemName,
				entity.memo, entity.selectionItemClsAtr, entity.contractCd, entity.integrationCd, entity.selectionCd,
				entity.characterTypeAtr, entity.selectionName, entity.selectionExtCd);
		
	}

	@Override
	public Optional<PerInfoSelectionItem> getPerInfoSelectionItem(String selectionItemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkExist(String selectionItemId) {
		// TODO Auto-generated method stub
		return false;
	}

}
