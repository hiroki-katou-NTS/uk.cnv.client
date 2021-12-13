package nts.uk.ctx.exio.infra.entity.exi.execlog;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDateTime;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 外部受入実行結果ログ
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OIODT_EX_AC_EXEC_LOG")
public class OiodtExAcExecLog extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	public OiomtExacExeResultLogPk pk;
	/**	契約コード */
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	public String contractCd;
	/**
	 * 実行者ID
	 */
	@Basic(optional = false)
	@Column(name = "EXECUTOR_ID")
	public String executorId;

	/**
	 * ユーザID
	 */
	@Basic(optional = false)
	@Column(name = "USER_ID")
	public String userId;

	/**
	 * 処理開始日時
	 */
	@Basic(optional = false)
	@Column(name = "PROCESS_START_DATETIME")
	public GeneralDateTime processStartDatetime;

	/**
	 * 定型区分
	 */
	@Basic(optional = false)
	@Column(name = "STANDARD_ATR")
	public int standardAtr;

	/**
	 * 実行形態
	 */
	@Basic(optional = false)
	@Column(name = "EXECUTE_FORM")
	public boolean executeForm;

	/**
	 * 対象件数
	 */
	@Basic(optional = false)
	@Column(name = "TARGET_COUNT")
	public int targetCount;

	/**
	 * エラー件数
	 */
	@Basic(optional = false)
	@Column(name = "ERROR_COUNT")
	public int errorCount;

	/**
	 * ファイル名
	 */
	@Basic(optional = false)
	@Column(name = "FILE_NAME")
	public String fileName;

	/**
	 * システム種類
	 */
	@Basic(optional = false)
	@Column(name = "SYSTEM_TYPE")
	public int systemType;

	/**
	 * 結果状態
	 */
	@Basic(optional = false)
	@Column(name = "RESULT_STATUS")
	public Integer resultStatus;

	/**
	 * 処理終了日時
	 */
	@Basic(optional = false)
	@Column(name = "PROCESS_END_DATETIME")
	public GeneralDateTime processEndDatetime;

	/**
	 * 処理区分
	 */
	@Basic(optional = false)
	@Column(name = "PROCESS_ATR")
	public int processAtr;

	@Override
	protected Object getKey() {
		return pk;
	}
}
