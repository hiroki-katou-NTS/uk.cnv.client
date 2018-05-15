package nts.uk.ctx.at.function.infra.repository.attendancerecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nts.uk.ctx.at.function.dom.attendancerecord.item.CalculateAttendanceRecordSetMemento;
import nts.uk.ctx.at.function.dom.attendancerecord.item.CalculateItemAttributes;
import nts.uk.ctx.at.function.dom.attendancerecord.item.ItemName;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.KfnstAttndRec;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.item.KfnstAttndRecItem;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.item.KfnstAttndRecItemPK;

public class JpaCalculateAttendanceRecordSetMemento implements CalculateAttendanceRecordSetMemento {
	/** The kfnst attnd rec. */
	private KfnstAttndRec kfnstAttndRec;

	/** The kfnst attnd rec item. */
	private List<KfnstAttndRecItem> kfnstAttndRecItems;
	
	

	/**
	 * Instantiates a new jpa calculate attendance record set memento.
	 *
	 * @param kfnstAttndRec the kfnst attnd rec
	 * @param kfnstAttndRecItems the kfnst attnd rec items
	 */
	public JpaCalculateAttendanceRecordSetMemento(KfnstAttndRec kfnstAttndRec,
			List<KfnstAttndRecItem> kfnstAttndRecItems) {
		super();
		this.kfnstAttndRec = kfnstAttndRec;
		this.kfnstAttndRecItems = kfnstAttndRecItems;
	}

	@Override
	public void setAttribute(CalculateItemAttributes attribute) {
		this.kfnstAttndRec.setAttribute(new BigDecimal(attribute.value));
	}

	@Override
	public void setName(ItemName name) {
		this.kfnstAttndRec.setItemName(name.v());
	}

	@Override
	public void setAddedItem(List<Integer> idList) {
		idList.forEach(e -> {
			KfnstAttndRecItemPK pk = new KfnstAttndRecItemPK(null, 0, 0, 0, 0, e);
			KfnstAttndRecItem entity = new KfnstAttndRecItem(pk, new BigDecimal(1));
			kfnstAttndRecItems.add(entity);
		});
	}

	@Override
	public void setSubtractedItem(List<Integer> idList) {
		// TODO Auto-generated method stub
		
	}

}
