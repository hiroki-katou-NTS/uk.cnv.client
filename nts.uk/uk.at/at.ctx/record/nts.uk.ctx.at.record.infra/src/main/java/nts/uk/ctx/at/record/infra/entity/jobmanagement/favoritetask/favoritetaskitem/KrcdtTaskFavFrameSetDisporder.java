package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.favoritetaskitem;

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
 * お気に入り作業の表示順 FavoriteTaskDisplayOrder
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TASK_FAV_FRAME_SET_DISPORDER")
public class KrcdtTaskFavFrameSetDisporder extends ContractCompanyUkJpaEntity implements Serializable {

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

	public KrcdtTaskFavFrameSetDisporder(String sId, FavoriteDisplayOrder order) {
		this.sId = sId;
		this.favId = order.getFavId();
		this.disporder = order.getOrder();
	}

}
