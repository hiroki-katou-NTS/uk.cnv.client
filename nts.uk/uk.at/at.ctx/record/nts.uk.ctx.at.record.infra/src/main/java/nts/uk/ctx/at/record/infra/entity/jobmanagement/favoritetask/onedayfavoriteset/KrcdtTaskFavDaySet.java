package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteSet;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * 1日お気に入りセット OneDayFavoriteSet
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TASK_FAV_DAY_SET")
public class KrcdtTaskFavDaySet extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "FAV_ID")
	public String favId;

	@Column(name = "FAV_NAME")
	public String favName;

	@Column(name = "SID")
	public String sId;
	
	@OneToMany(targetEntity = KrcdtTaskFavDaySetTs.class, mappedBy = "krcdtTaskFavDaySet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KRCDT_TASK_FAV_DAY_SET_TS")
	public List<KrcdtTaskFavDaySetTs> krcdttaskFavDaySetTsList;

	@Override
	protected Object getKey() {
		return this.favId;
	}

	public KrcdtTaskFavDaySet(OneDayFavoriteSet domain) {
		this.favId = domain.getFavId();
		this.favName = domain.getTaskName().v();
		this.sId = domain.getSId();
	}

}
