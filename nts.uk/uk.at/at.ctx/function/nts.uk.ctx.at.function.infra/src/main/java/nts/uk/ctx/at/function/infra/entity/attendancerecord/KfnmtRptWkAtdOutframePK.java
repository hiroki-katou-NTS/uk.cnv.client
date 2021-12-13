package nts.uk.ctx.at.function.infra.entity.attendancerecord;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * The primary key class for the KFNMT_RPT_WK_ATD_OUTFRAME database table.
 * 
 */

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KfnmtRptWkAtdOutframePK implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="LAYOUT_ID")
	private String layoutId;

	@Column(name = "COLUMN_INDEX")
	private long columnIndex;
	
	@Column(name = "OUTPUT_ATR")
	private long outputAtr;

	@Column(name = "POSITION")
	private long position;

	public long getColumnIndex() {
		return this.columnIndex;
	}

	public void setColumnIndex(long columnIndex) {
		this.columnIndex = columnIndex;
	}

	public long getOutputAtr() {
		return this.outputAtr;
	}

	public void setOutputAtr(long outputAtr) {
		this.outputAtr = outputAtr;
	}

	public long getPosition() {
		return this.position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof KfnmtRptWkAtdOutframePK)) {
			return false;
		}
		KfnmtRptWkAtdOutframePK castOther = (KfnmtRptWkAtdOutframePK) other;
		return this.layoutId.equals(castOther.layoutId) && (this.columnIndex == castOther.columnIndex) && (this.outputAtr == castOther.outputAtr)
				&& (this.position == castOther.position);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.layoutId.hashCode();
		hash = hash * prime + ((int) (this.columnIndex ^ (this.columnIndex >>> 32)));
		hash = hash * prime + ((int) (this.outputAtr ^ (this.outputAtr >>> 32)));
		hash = hash * prime + ((int) (this.position ^ (this.position >>> 32)));

		return hash;
	}

	public String getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(String layoutId) {
		this.layoutId = layoutId;
	}
}