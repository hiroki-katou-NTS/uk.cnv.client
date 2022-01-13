package nts.uk.ctx.at.shared.infra.repository.scherec.dailyattendanceitem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.ControlOfAttendanceItems;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.primitivevalue.HeaderBackgroundColor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.ControlOfAttendanceItemsRepository;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KshmtDayAtdCtr;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KshmtDayAtdCtrPK;

@Stateless
public class JpaControlOfAttendanceItemsRepository extends JpaRepository implements ControlOfAttendanceItemsRepository {

	private static final String GET_BY_CODE = "SELECT c FROM KshmtDayAtdCtr c "
			+ " WHERE c.kshmtDayAtdCtrPK.companyID = :companyID "
			+ " AND c.kshmtDayAtdCtrPK.itemDailyID = :itemDailyID ";


	@Override
	public Optional<ControlOfAttendanceItems> getControlOfAttendanceItem(String companyID, int itemDailyID) {
		Optional<ControlOfAttendanceItems> data = this.queryProxy().query(GET_BY_CODE,KshmtDayAtdCtr.class)
				.setParameter("companyID", companyID)
				.setParameter("itemDailyID", itemDailyID)
				.getSingle(c->c.toDomain());
		return data;
	}

	@Override
	public void updateControlOfAttendanceItem(ControlOfAttendanceItems controlOfAttendanceItems) {
		this.queryProxy().find(
				new KshmtDayAtdCtrPK(controlOfAttendanceItems.getCompanyID(), controlOfAttendanceItems.getItemDailyID()),
				KshmtDayAtdCtr.class
		).ifPresent(updateEntity -> {
			updateEntity.headerBgColorOfDailyPer = controlOfAttendanceItems.getHeaderBgColorOfDailyPer().map(h -> h.v()).orElse(null);
			this.commandProxy().update(updateEntity);
		});
	}

	@Override
	public void insertControlOfAttendanceItem(ControlOfAttendanceItems controlOfAttendanceItems) {
		KshmtDayAtdCtr newEntity =KshmtDayAtdCtr.toEntity(controlOfAttendanceItems);
		this.commandProxy().insert(newEntity);
	}


    @Override
    @SneakyThrows
    public List<ControlOfAttendanceItems> getByItemDailyList(String companyID, List<Integer> itemDailyIDList) {
        // Check empty
        if (CollectionUtil.isEmpty(itemDailyIDList)) {
            return Collections.emptyList();
        }
		List<ControlOfAttendanceItems> data = this.queryProxy().query("SELECT c FROM KshmtDayAtdCtr c "
				+ " WHERE c.kshmtDayAtdCtrPK.companyID = :companyID "
				+ " AND c.kshmtDayAtdCtrPK.itemDailyID IN :itemDailyID", KshmtDayAtdCtr.class)
				.setParameter("companyID", companyID)
				.setParameter("itemDailyID", itemDailyIDList)
				.getList(KshmtDayAtdCtr::toDomain);
		return data;
    }

}
