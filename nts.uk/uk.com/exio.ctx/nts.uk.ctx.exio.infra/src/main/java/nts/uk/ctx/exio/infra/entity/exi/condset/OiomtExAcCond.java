package nts.uk.ctx.exio.infra.entity.exi.condset;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 受入条件設定（定型）
 */
@Data
@Entity
@Table(name = "OIOMT_EX_AC_COND")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OiomtExAcCond extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	private OiomtStdAcceptCondSetPk stdAcceptCondSetPk;
	/**	契約コード */
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	public String contractCd;
	/**
	 * システム種類
	 */
	@Basic(optional = true)
	@Column(name = "SYSTEM_TYPE")
	public Integer systemType;
	/**
	 * 外部受入カテゴリID
	 */
	@Basic(optional = true)
	@Column(name = "CATEGORY_ID")
	private Integer categoryId;

	/**
	 * CSVデータの項目名行
	 */
	@Basic(optional = true)
	@Column(name = "CSV_DATA_LINE_NUMBER")
	private Integer csvDataLineNumber;

	/**
	 * 既存データの削除
	 */
	@Basic(optional = false)
	@Column(name = "DELETE_EXIST_DATA")
	private int deleteExistData;

	/**
	 * CSVデータの取込開始行
	 */
	@Basic(optional = true)
	@Column(name = "CSV_DATA_START_LINE")
	private Integer csvDataStartLine;

	/**
	 * 文字コード
	 */
	@Basic(optional = true)
	@Column(name = "CHARACTER_CODE")
	private Integer characterCode;

	/**
	 * 受入モード
	 */
	@Basic(optional = true)
	@Column(name = "ACCEPT_MODE")
	private Integer acceptMode;

	/**
	 * 外部受入条件名称
	 */
	@Basic(optional = false)
	@Column(name = "CONDITION_SET_NAME")
	private String conditionSetName;

	/**
	 * チェック完了
	 */
	@Basic(optional = true)
	@Column(name = "CHECK_COMPLETED")
	private int checkCompleted;

	/**
	 * 既存データの削除方法
	 */
	@Basic(optional = true)
	@Column(name = "DELETE_EXT_DATA_METHOD")
	private Integer deleteExtDataMethod;

	/**
	 * Gets primary key of entity.
	 *
	 * @return the primary key
	 */
	@Override
	protected Object getKey() {
		return this.stdAcceptCondSetPk;
	}

}
