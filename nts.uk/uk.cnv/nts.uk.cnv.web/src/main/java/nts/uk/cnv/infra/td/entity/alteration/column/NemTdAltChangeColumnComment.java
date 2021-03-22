package nts.uk.cnv.infra.td.entity.alteration.column;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.layer.infra.data.entity.JpaEntity;
import nts.uk.cnv.dom.td.alteration.content.column.ChangeColumnComment;
import nts.uk.cnv.infra.td.entity.alteration.NemTdAltContentPk;
import nts.uk.cnv.infra.td.entity.alteration.NemTdAlteration;

@SuppressWarnings("serial")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NEM_TD_ALT_CHANGE_COLUMN_COMMENT")
public class NemTdAltChangeColumnComment extends JpaEntity implements Serializable {

	@EmbeddedId
	public NemTdAltContentPk pk;

	@Column(name = "COLUMN_ID")
	public String columnId;

	@Column(name = "COMMENT")
	public String comment;

	@ManyToOne
    @PrimaryKeyJoinColumns({
    	@PrimaryKeyJoinColumn(name = "ALTERATION_ID", referencedColumnName = "ALTERATION_ID")
    })
	public NemTdAlteration alteration;
	
	public static NemTdAltChangeColumnComment toEntity(NemTdAltContentPk pk, ChangeColumnComment d) {
		val e = new NemTdAltChangeColumnComment();
		e.pk = pk;
		e.columnId = d.getColumnId();
		e.comment = d.getComment();
		return e;
	}

	public ChangeColumnComment toDomain() {
		return new ChangeColumnComment(this.columnId, this.comment);
	}

	@Override
	protected Object getKey() {
		return pk;
	}
}
