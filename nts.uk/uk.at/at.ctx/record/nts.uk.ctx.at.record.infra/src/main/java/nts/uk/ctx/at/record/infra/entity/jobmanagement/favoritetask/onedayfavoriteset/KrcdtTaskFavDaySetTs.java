package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * 
 * @author tutt
 *
 */

@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TASK_FAV_DAY_SET_TS")
public class KrcdtTaskFavDaySetTs extends ContractCompanyUkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KrcdtTaskFavDaySetTsPk pk;

	@Column(name = "END_CLOCK")
	public int endClock;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "FAV_ID", referencedColumnName = "FAV_ID")
	public KrcdtTaskFavDaySet krcdtTaskFavDaySet;
	
	@OneToMany(targetEntity = KrcdtTaskFavDaySetItem.class, mappedBy = "krcdtTaskFavDaySetTs", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KRCDT_TASK_FAV_DAY_SET_ITEM")
	public List<KrcdtTaskFavDaySetItem> krcdtTaskFavDaySetItemList;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcdtTaskFavDaySetTs(String favId, int startClock, int endClock) {
		super();
		this.pk = new KrcdtTaskFavDaySetTsPk(favId, startClock);
		this.endClock = endClock;
	}
}
