package nts.uk.ctx.at.function.infra.repository.attendancerecord.export;

import java.math.BigDecimal;
import java.util.Optional;

import nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportSetMemento;
import nts.uk.ctx.at.function.dom.attendancerecord.export.ExportAtr;
import nts.uk.ctx.at.function.dom.attendancerecord.item.AttendanceRecordDisplay;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.KfnmtRptWkAtdOutframe;

/**
 * The Class JpaAttendanceRecordExportSetMemento.
 * @author NWS_QUANGNT
 */
public class JpaAttendanceRecordExportSetMemento implements AttendanceRecordExportSetMemento {

	/** The entity. */
	private KfnmtRptWkAtdOutframe entity;

	/**
	 * Instantiates a new jpa attendance record export set memento.
	 */
	public JpaAttendanceRecordExportSetMemento() {

	}

	/**
	 * Instantiates a new jpa attendance record export set memento.
	 *
	 * @param entity the entity
	 */
	public JpaAttendanceRecordExportSetMemento(KfnmtRptWkAtdOutframe entity) {
		this.entity = entity;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportSetMemento#setExportClassification(nts.uk.ctx.at.function.dom.attendancerecord.export.ExportAtr)
	 */
	@Override
	public void setExportClassification(ExportAtr exportAtr) {
		this.entity.setAttribute(new BigDecimal(exportAtr.value));

	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportSetMemento#setColumnIndex(int)
	 */
	@Override
	public void setColumnIndex(int index) {
		this.entity.getId().setColumnIndex(index);

	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportSetMemento#isUseAtr(java.lang.Boolean)
	 */
	@Override
	public void isUseAtr(Boolean isUseAtr) {

		this.entity.setUseAtr(isUseAtr);

	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportSetMemento#setUpperPosition(java.util.Optional)
	 */
	@Override
	public void setUpperPosition(Optional<AttendanceRecordDisplay> attendanceRecDisplay) {
		// No code

	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportSetMemento#setLowerPosition(java.util.Optional)
	 */
	@Override
	public void setLowerPosition(Optional<AttendanceRecordDisplay> attendanceRecDisplay) {
		// No code

	}

}
