package nts.uk.ctx.sys.portal.infra.entity.smartphonetoppageset;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * スマホのトップページ設定
 * 
 * @author sonnh1
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SPTMT_SP_TOPPAGE")
public class SptmtSPTopPage extends ContractUkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public SptmtSPTopPagePK pk;

	@Column(name = "DISP_ATR")
	public int dispAtr;

	@Override
	protected Object getKey() {
		return this.pk;
	}
}
