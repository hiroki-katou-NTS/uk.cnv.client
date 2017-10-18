package repository.person.setting.selectionitem.selection;

import java.util.List;

import javax.ejb.Stateless;

import entity.person.setting.selectionitem.selection.PpemtSelItemOrder;
import entity.person.setting.selectionitem.selection.PpemtSelItemOrderPK;
import entity.person.setting.selectionitem.selection.PpemtSelection;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.person.dom.person.setting.selectionitem.selection.SelectionItemOrder;
import nts.uk.ctx.bs.person.dom.person.setting.selectionitem.selection.SelectionItemOrderRepository;

/**
 * 
 * @author tuannv
 *
 */

@Stateless
public class JpaSelectionItemOrderRepository extends JpaRepository implements SelectionItemOrderRepository {

	private static final String SELECT_ALL = "SELECT si FROM PpemtSelItemOrder si";
	private static final String SELECT_ALL_SELECTION_ID = SELECT_ALL + " WHERE si.selectionIdPK.selectionIdPK = :selectedId";

	@Override
	public void add(SelectionItemOrder selectionItemOrder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(SelectionItemOrder selectionItemOrder) {
		// TODO Auto-generated method stub

	}

	// Domain:
	private SelectionItemOrder toDomain(PpemtSelItemOrder entity) {
		return SelectionItemOrder.selectionItemOrder(entity.selectionIdPK.selectionIdPK, entity.histidPK,
				entity.disPorder, entity.initSelection);

	}

	// to Entity:
	private static PpemtSelItemOrder toEntity(SelectionItemOrder domain) {
		PpemtSelItemOrderPK key = new PpemtSelItemOrderPK(domain.getSelectionID());
		return new PpemtSelItemOrder(key, domain.getHistId(), domain.getDisporder().v(),
				domain.getInitSelection().value);
	}

	@Override
	public List<SelectionItemOrder> getAllOrderItemSelection(String selectedId) {
		return this.queryProxy().query(SELECT_ALL_SELECTION_ID, PpemtSelItemOrder.class)
				.setParameter("selectedId", selectedId).getList(c -> toDomain(c));

	}

}
