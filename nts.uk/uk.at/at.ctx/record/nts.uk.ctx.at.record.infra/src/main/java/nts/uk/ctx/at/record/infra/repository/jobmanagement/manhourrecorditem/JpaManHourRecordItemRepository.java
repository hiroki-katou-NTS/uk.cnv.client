package nts.uk.ctx.at.record.infra.repository.jobmanagement.manhourrecorditem;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItem;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItemRepository;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.manhourrecorditem.KrcmtManHrItem;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaManHourRecordItemRepository extends JpaRepository implements ManHourRecordItemRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT i FROM KrcmtManHrItem i";
	private static final String SELECT_BY_CID = SELECT_ALL_QUERY_STRING + " WHERE i.pk.cId = :cId";
	private static final String SELECT_BY_CID_AND_ITEM = SELECT_BY_CID + " AND i.pk.manHrItemId IN :items";
	
	@Override
	public void insert(ManHourRecordItem item) {
		this.commandProxy().insert(new KrcmtManHrItem(item));		
	}

	@Override
	public void update(ManHourRecordItem item) {
		this.commandProxy().update(new KrcmtManHrItem(item));	
		
	}

	@Override
	public List<ManHourRecordItem> get(String cId) {
		return this.queryProxy().query(SELECT_BY_CID, KrcmtManHrItem.class)
				.setParameter("cId", cId)
				.getList(item -> item.toDomain());
	}

	@Override
	public List<ManHourRecordItem> get(String cId, List<Integer> items) {
		if(items.isEmpty()) {
			return new ArrayList<ManHourRecordItem>();
		}
		return this.queryProxy().query(SELECT_BY_CID_AND_ITEM, KrcmtManHrItem.class)
				.setParameter("cId", cId)
				.setParameter("items", items)
				.getList(item -> item.toDomain());
		
	}

}
