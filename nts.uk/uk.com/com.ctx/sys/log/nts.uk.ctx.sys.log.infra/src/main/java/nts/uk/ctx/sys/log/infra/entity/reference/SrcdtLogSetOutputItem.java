package nts.uk.ctx.sys.log.infra.entity.reference;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.log.dom.reference.LogSetOutputItem;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/*
 * author: hiep.th
 */
@Entity
@Table(name = "SRCDT_LOG_SET_OUTPUT_ITEM")
@NoArgsConstructor
public class SrcdtLogSetOutputItem extends UkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public SrcdtLogSetOutputItemPK srcdtLogSetOutputItemPK;
	
	/** 表示順 */
	@Basic(optional=false)
	@Column(name = "DISPLAY_ORDER")
    public int displayOrder;
	
	/** 囲い文字  */
	@Basic(optional=false)
	@Column(name = "IS_USE_FLG")
    public int isUseFlg;

	@ManyToOne
	@JoinColumn(name="LOG_SET_ID", referencedColumnName="LOG_SET_ID", insertable = false, updatable = false)		
	public SrcdtLogDisplaySetting logDisplaySetCond;
	
	@OneToMany(mappedBy = "logSetOutputItemCond", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SrcdtLogSetItemDetail> listLogSetItemDetails;
	
	@Override
	protected Object getKey() {
		return srcdtLogSetOutputItemPK;
	}

	public LogSetOutputItem toDomain() {
		boolean isUseFlg = this.isUseFlg == 1;
		return LogSetOutputItem.createFromJavatype(this.srcdtLogSetOutputItemPK.logSetId, this.srcdtLogSetOutputItemPK.itemNo,
				this.displayOrder, isUseFlg, this.listLogSetItemDetails.stream().map(item -> item.toDomain()).collect(Collectors.toList()));
	}

	public static SrcdtLogSetOutputItem toEntity(LogSetOutputItem domain) {
		int isUseFlag = domain.isUseFlag() ? 1 : 0;
		return new SrcdtLogSetOutputItem(new SrcdtLogSetOutputItemPK(domain.getLogSetId(), domain.getItemNo()),
				 domain.getDisplayOrder(), isUseFlag, 
				 domain.getLogSetItemDetails().stream().map(item -> SrcdtLogSetItemDetail.toEntity(item)).collect(Collectors.toList()));
	}

	public SrcdtLogSetOutputItem(SrcdtLogSetOutputItemPK srcdtLogSetOutputItemPK, int displayOrder, 
			int isUseFlg, List<SrcdtLogSetItemDetail> listLogSetItemDetails) {
		super();
		this.srcdtLogSetOutputItemPK = srcdtLogSetOutputItemPK;
		this.displayOrder = displayOrder;
		this.isUseFlg = isUseFlg;
		this.listLogSetItemDetails = listLogSetItemDetails;
	}
}
