package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteDisplayOrder;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * 1日お気に入り作業の表示順 OneDayFavoriteTaskDisplayOrder
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TASK_FAV_DAY_DISPORDER")
public class KrcdtTaskFavDayDispOrder extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "FAV_ID")
	public String favId;

	@Column(name = "SID")
	public String sId;
	
	@Column(name = "DISPORDER")
	public int disporder;
	
	@Override
	protected Object getKey() {
		return this.favId;
	}
	
	public FavoriteDisplayOrder toDomain() {
		return new FavoriteDisplayOrder(this.favId, this.disporder);
	}
	
	public KrcdtTaskFavDayDispOrder(String sId, FavoriteDisplayOrder order) {
		this.sId = sId;
		this.favId = order.getFavId();
		this.disporder = order.getOrder();
	}
}
