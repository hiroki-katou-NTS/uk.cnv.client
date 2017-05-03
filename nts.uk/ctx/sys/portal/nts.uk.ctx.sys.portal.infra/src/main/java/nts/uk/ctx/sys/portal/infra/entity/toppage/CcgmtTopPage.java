package nts.uk.ctx.sys.portal.infra.entity.toppage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CCGMT_TOP_PAGE")
public class CcgmtTopPage extends UkJpaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public CcgmtTopPagePK ccgmtTopPagePK;

	@Column(name = "TOP_PAGE_NAME")
	public String topPageName;

	@Column(name = "LANG_NO")
	public int languageNumber;

	@Column(name = "LAYOUTID")
	public String layoutId;

	@Override
	protected Object getKey() {
		return ccgmtTopPagePK;
	}

}
