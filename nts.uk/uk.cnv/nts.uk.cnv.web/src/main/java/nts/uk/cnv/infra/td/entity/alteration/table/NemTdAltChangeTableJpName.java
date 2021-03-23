package nts.uk.cnv.infra.td.entity.alteration.table;

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
import nts.uk.cnv.dom.td.alteration.content.ChangeTableJpName;
import nts.uk.cnv.infra.td.entity.alteration.NemTdAltContentPk;
import nts.uk.cnv.infra.td.entity.alteration.NemTdAlteration;

@SuppressWarnings("serial")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NEM_TD_ALT_CHANGE_TABLE_JPNAME")
public class NemTdAltChangeTableJpName extends JpaEntity implements Serializable {

	@EmbeddedId
	public NemTdAltContentPk pk;

	@Column(name = "JPNAME")
	public String jpName;

	@ManyToOne
    @PrimaryKeyJoinColumns({
    	@PrimaryKeyJoinColumn(name = "ALTERATION_ID", referencedColumnName = "ALTERATION_ID")
    })
	public NemTdAlteration alteration;
	
	public static NemTdAltChangeTableJpName toEntity(NemTdAltContentPk pk, ChangeTableJpName d) {
		val e = new NemTdAltChangeTableJpName();
		e.pk = pk;
		e.jpName = d.getJpName();
		return e;
	}

	public ChangeTableJpName toDomain() {
		return new ChangeTableJpName(this.jpName);
	}

	@Override
	protected Object getKey() {
		return pk;
	}
}
