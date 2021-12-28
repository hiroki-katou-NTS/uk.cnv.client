package nts.uk.ctx.exio.infra.entity.exo.outputitem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.exio.dom.exo.outputitem.CategoryItem;
import nts.uk.ctx.exio.dom.exo.outputitem.StandardOutputItem;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 外部出力出力項目(定型)
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OIOMT_EX_OUT_ITEM")
public class OiomtStdOutItem extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	public OiomtStdOutItemPk stdOutItemPk;

	/**
	 * 出力項目名
	 */
	@Basic(optional = false)
	@Column(name = "OUT_ITEM_NAME")
	public String outItemName;

	/**
	 * 項目型
	 */
	@Basic(optional = false)
	@Column(name = "ITEM_TYPE")
	public int itemType;

	/**
	 * カテゴリ項目
	 */
	@OneToMany(targetEntity = OiomtCtgItem.class, cascade = CascadeType.ALL, mappedBy = "oiomtStdOutItem", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "OIOMT_EX_OUT_CTG_ITEM")
	public List<OiomtCtgItem> oiomtCtgItems;

	@Override
	protected Object getKey() {
		return stdOutItemPk;
	}

	public StandardOutputItem toDomain() {
		return new StandardOutputItem(this.stdOutItemPk.cid, this.stdOutItemPk.outItemCd, this.stdOutItemPk.condSetCd,
				this.outItemName, this.itemType, this.oiomtCtgItems.stream().map(x -> {
					return new CategoryItem(Integer.valueOf(x.getCtgItemPk().ctgItemNo), x.getCtgId(), x.getOperationSymbol(),
							x.getCtgItemPk().displayOrder);
				}).collect(Collectors.toList()));
	}

	public static OiomtStdOutItem toEntity(StandardOutputItem domain) {
		return new OiomtStdOutItem(
				new OiomtStdOutItemPk(domain.getCid(), domain.getOutputItemCode().v(),
						domain.getConditionSettingCode().v()),
				domain.getOutputItemName().v(), domain.getItemType().value,
				domain.getCategoryItems().stream().map(item -> {
					return new OiomtCtgItem(
							new OiomtCtgItemPk(item.getItemNo().v().toString(), domain.getCid(), domain.getOutputItemCode().v(),
									domain.getConditionSettingCode().v(), item.getDisplayOrder()),
							item.getCategoryId().v(),
							item.getOperationSymbol().isPresent() ? item.getOperationSymbol().get().value : null,
							 null);
				}).collect(Collectors.toList()));
	}

	public static List<OiomtStdOutItem> toEntity(List<StandardOutputItem> listDomain) {
		List<OiomtStdOutItem> listOiomtStdOutItem = new ArrayList<OiomtStdOutItem>();
		for (StandardOutputItem standardOutputItem : listDomain) {
			listOiomtStdOutItem.add(OiomtStdOutItem.toEntity(standardOutputItem));
		}
		return listOiomtStdOutItem;

	}

}
