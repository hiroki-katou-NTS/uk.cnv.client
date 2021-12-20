package nts.uk.ctx.at.function.infra.repository.attendancerecord.export;

import java.math.BigDecimal;
import java.util.Optional;

import nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportGetMemento;
import nts.uk.ctx.at.function.dom.attendancerecord.export.ExportAtr;
import nts.uk.ctx.at.function.dom.attendancerecord.item.AttendanceRecordDisplay;
import nts.uk.ctx.at.function.dom.attendancerecord.item.CalculateAttendanceRecord;
import nts.uk.ctx.at.function.dom.attendancerecord.item.SingleAttendanceRecord;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.KfnmtRptWkAtdOutframe;
import nts.uk.ctx.at.function.infra.repository.attendancerecord.JpaCalculateAttendanceRecordGetMemento;
import nts.uk.ctx.at.function.infra.repository.attendancerecord.JpaSingleAttendanceRecordGetMemento;
/**
 * The Class JpaAttendanceRecordExportGetMemento.
 * 
 * @author NWS_QUANGNT
 */
public class JpaAttendanceRecordExportGetMemento implements AttendanceRecordExportGetMemento {
	
	
	/** The entity. */
	private KfnmtRptWkAtdOutframe upperEntity;

	private KfnmtRptWkAtdOutframe lowerEntity;

	/**
	 * Instantiates a new jpa attendance record export get memento.
	 *
	 * @param entity
	 *            the entity
	 */
	public JpaAttendanceRecordExportGetMemento(KfnmtRptWkAtdOutframe upperEntity, KfnmtRptWkAtdOutframe lowerEntity) {
		this.upperEntity = upperEntity;
		this.lowerEntity = lowerEntity;
	}

	/**
	 * Instantiates a new jpa attendance record export get memento.
	 */
	JpaAttendanceRecordExportGetMemento() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.
	 * AttendanceRecordExportGetMemento#getExportAtr()
	 */
	@Override
	public ExportAtr getExportAtr() {
		if(this.upperEntity!=null) {
			return ExportAtr.valueOf((int) this.upperEntity.getId().getOutputAtr());
		}else {
			return ExportAtr.valueOf((int) this.lowerEntity.getId().getOutputAtr());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.
	 * AttendanceRecordExportGetMemento#getColumnIndex()
	 */
	@Override
	public int getColumnIndex() {
		if(this.upperEntity!=null) {
			return (int) this.upperEntity.getId().getColumnIndex();
		}else{
			return (int) this.lowerEntity.getId().getColumnIndex();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.
	 * AttendanceRecordExportGetMemento#isUseAtr()
	 */
	@Override
	public Boolean isUseAtr() {
		if(this.upperEntity!=null) {
			// ZERO = false, ONE = true
			return this.upperEntity.isUseAtr();
		}else {
			return this.lowerEntity.isUseAtr();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.
	 * AttendanceRecordExportGetMemento#getUpperPosition()
	 */
	@Override
	public Optional<AttendanceRecordDisplay> getUpperPosition() {

		if (upperEntity != null) {

			// 1 <= Attribute <= 3
			if (this.upperEntity.getAttribute().compareTo(new BigDecimal(0)) == 1
					&& this.upperEntity.getAttribute().compareTo(new BigDecimal(4)) == -1)
				return Optional
						.of(new SingleAttendanceRecord(new JpaSingleAttendanceRecordGetMemento(upperEntity, null)));
			// 4 <= Attribute <= 7
			if (this.upperEntity.getAttribute().compareTo(new BigDecimal(3)) == 1
					&& this.upperEntity.getAttribute().compareTo(new BigDecimal(8)) == -1)
				return Optional.of(
						new CalculateAttendanceRecord(new JpaCalculateAttendanceRecordGetMemento(upperEntity, null)));
		}
		return Optional.empty();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.function.dom.attendancerecord.export.
	 * AttendanceRecordExportGetMemento#getLowerPosition()
	 */
	@Override
	public Optional<AttendanceRecordDisplay> getLowerPosition() {

		if (lowerEntity != null) {
			// 1 <= Attribute <= 3
			if (this.lowerEntity.getAttribute().compareTo(new BigDecimal(0)) == 1
					&& this.lowerEntity.getAttribute().compareTo(new BigDecimal(4)) == -1)
				return Optional
						.of(new SingleAttendanceRecord(new JpaSingleAttendanceRecordGetMemento(lowerEntity, null)));
			// 4 <= Attribute <= 7
			if (this.lowerEntity.getAttribute().compareTo(new BigDecimal(3)) == 1
					&& this.lowerEntity.getAttribute().compareTo(new BigDecimal(8)) == -1)
				return Optional.of(
						new CalculateAttendanceRecord(new JpaCalculateAttendanceRecordGetMemento(lowerEntity, null)));
		}
		return Optional.empty();
	}

}
