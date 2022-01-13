package nts.uk.ctx.at.shared.infra.repository.scherec.monthlyattendanceitem;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.TimeInputUnit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.primitivevalue.HeaderBackgroundColor;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.ControlOfMonthlyItems;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.ControlOfMonthlyItemsRepository;
import nts.uk.ctx.at.shared.infra.entity.scherec.monthlyattendanceitem.KshmtMonItemControl;

@Stateless
public class JpaControlOfMonthlyItemsRepository extends JpaRepository implements ControlOfMonthlyItemsRepository {

	private static final String GET_BY_CODE = "SELECT c FROM KshmtMonItemControl c "
			+ " WHERE c.krcmtControlOfMonthlyItemsPK.companyID = :companyID "
			+ " AND c.krcmtControlOfMonthlyItemsPK.itemMonthlyID = :itemMonthlyID ";

	@Override
	public Optional<ControlOfMonthlyItems> getControlOfMonthlyItem(String companyID, int itemMonthlyID) {
		Optional<ControlOfMonthlyItems> data = this.queryProxy().query(GET_BY_CODE, KshmtMonItemControl.class)
				.setParameter("companyID", companyID).setParameter("itemMonthlyID", itemMonthlyID)
				.getSingle(KshmtMonItemControl::toDomain);
		return data;
	}

	@Override
	public void updateControlOfMonthlyItem(ControlOfMonthlyItems controlOfMonthlyItems) {
		KshmtMonItemControl newEntity = KshmtMonItemControl.toEntity(controlOfMonthlyItems);
		this.queryProxy().find(newEntity.getKrcmtControlOfMonthlyItemsPK(), KshmtMonItemControl.class).ifPresent(updateEntity -> {
			updateEntity.headerBgColorOfMonthlyPer = newEntity.headerBgColorOfMonthlyPer;
			this.commandProxy().update(updateEntity);
		});
	}

	@Override
	public void addControlOfMonthlyItem(ControlOfMonthlyItems controlOfMonthlyItems) {
		KshmtMonItemControl newEntity = KshmtMonItemControl.toEntity(controlOfMonthlyItems);
		this.commandProxy().insert(newEntity);
	}

	@Override
	public List<ControlOfMonthlyItems> getListControlOfMonthlyItem(String companyID, List<Integer> itemMonthlyIDs) {
		List<ControlOfMonthlyItems> data = new ArrayList<>();
		CollectionUtil.split(itemMonthlyIDs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			try (PreparedStatement statement = this.connection().prepareStatement(
						"SELECT * from KSHMT_MON_ITEM_CONTROL h"
						+ " WHERE h.CID = ? AND h.ITEM_MONTHLY_ID IN (" + subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {
				statement.setString(1, companyID);
				for (int i = 0; i < subList.size(); i++) {
					statement.setInt(i + 2, subList.get(i));
				}
				data.addAll(new NtsResultSet(statement.executeQuery()).getList(rec -> new ControlOfMonthlyItems(
						companyID,
						rec.getInt("ITEM_MONTHLY_ID"),
						rec.getString("HEADER_BACKGROUND_COLOR") != null ? new HeaderBackgroundColor(rec.getString("HEADER_BACKGROUND_COLOR")) : null
				)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return data;
	}

}
