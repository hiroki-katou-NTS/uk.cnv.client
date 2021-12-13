package nts.uk.ctx.sys.assist.infra.entity.deletedata;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.sys.assist.dom.deletedata.ResultDeletion;
import nts.uk.ctx.sys.assist.dom.storage.LoginInfo;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "SSPDT_DELETION_RESULT")
@NoArgsConstructor
@AllArgsConstructor
public class SspdtDeletionResult extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
    public SspdtResultDeletionPK sspdtResultDeletionPK;
	
	/** The company Id. */
	/** 会社ID */
	@Basic(optional = false)
	@Column(name = "CID")
	public String companyID;
	
	/** The deletion name. */
	/** 削除名称 */
	@Basic(optional = false)
	@Column(name = "DEL_NAME")
	public String delName;
	
	/** The deletion type. */
	/** 削除形態 */
	@Basic(optional = false)
	@Column(name = "DEL_TYPE")
	public int delType;
	
	/** The deleted file flag. */
	/** 削除済みファイル */
	@Basic(optional = false)
	@Column(name = "IS_DELETED_FILES_FLG")
	public boolean isDeletedFilesFlg;
	
	/** The deletion code. */
	/** 削除セットコード  */
	@Basic(optional = true)
	@Column(name = "DEL_CODE")
	public String delCode;
	
	/** The number employees. */
	/** 対象人数 */
	@Basic(optional = false)
	@Column(name = "NUMBER_EMPLOYEES")
	public int numberEmployees;
	
	/** The employee Id. */
	/** 実行者 */
	@Basic(optional = false)
	@Column(name = "SID")
	public String sId;
	
	/** The status. */
	/** 結果状態 */
	@Basic(optional = false)
	@Column(name = "STATUS")
	public int status;
	
	/** The start date time deletion. */
	/** 削除開始日時 */
	@Basic(optional = false)
	@Column(name = "START_DATE_TIME_DELETE")
	public GeneralDateTime startDateTimeDel;
	
	/** The end date time deletion. */
	/** 削除終了日時 */
	@Basic(optional = true)
	@Column(name = "END_DATE_TIME_DELETE")
	public GeneralDateTime endDateTimeDel;
	
	/** The file id. */
	/**  ファイルID */
	@Basic(optional = true)
	@Column(name = "FILE_ID")
	public String fileId;
	
	/** The file name. */
	/** 保存ファイル名 */
	@Basic(optional = true)
	@Column(name = "FILE_NAME")
	public String fileName;
	
	/** The file size. */
	/** ファイル容量 */
	@Basic(optional = true)
	@Column(name = "FILE_SIZE")
	public Integer fileSize;
	
	/** The password encrypt for compress file. */
	/** 手動保存の圧縮パスワード */
	@Basic(optional = true)
	@Column(name = "PASSWORD_FOR_COMPRESS_FILE")
	public String passwordCompressFileEncrypt;
	
	/**
	 * ログイン情報.IPアドレス
	 */
	@Basic(optional = false)
	@Column(name = "PC_IP")
	public String pcId;
	
	/**
	 * ログイン情報.PC名
	 */
	@Basic(optional = false)
	@Column(name = "PC_NAME")
	public String pcName;
	
	/**
	 * ログイン情報.アカウント
	 */
	@Basic(optional = false)
	@Column(name = "PC_ACOUNT")
	public String pcAccount;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "resultDeletion", orphanRemoval = true, fetch = FetchType.LAZY)
	private List<SspdtDeletionResultLog> listResultLogDeletions;
	
	@Override
	protected Object getKey() {
		return sspdtResultDeletionPK;
	}

	public ResultDeletion toDomain() {
		boolean isDeletedFilesFlg = this.isDeletedFilesFlg;
		return ResultDeletion.createFromJavatype
				(
				this.sspdtResultDeletionPK.delId, 
				this.companyID, 
				this.delName, 
				this.delType, 
				isDeletedFilesFlg,
				this.delCode, 
				this.numberEmployees,
				this.listResultLogDeletions.stream().map(item -> item.toDomain()).collect(Collectors.toList()),
				this.sId, 
				this.status,
				this.startDateTimeDel, 
				this.endDateTimeDel, 
				this.fileId, 
				this.fileName, 
				this.fileSize,
				this.passwordCompressFileEncrypt,
				new LoginInfo(pcId,pcName,pcAccount));
	}

	public static SspdtDeletionResult toEntity(ResultDeletion result) {
		return new SspdtDeletionResult
			(
			new SspdtResultDeletionPK(result.getDelId()),
			result.getCompanyId(), 
			result.getDelName().v(), 
			result.getDelType().value,
			result.isDeletedFilesFlg(),
			result.getDelCode().v(), 
			result.getNumberEmployees(),
			result.getSId(), 
			result.getStatus().value, 
			result.getStartDateTimeDel(), 
			result.getEndDateTimeDel().orElse(null), 
			result.getFileId(), 
			result.getFileName().v(), 
			result.getFileSize(), 
			result.getPasswordCompressFileEncrypt().isPresent() ? result.getPasswordCompressFileEncrypt().get().toString() : null,
			result.getLoginInfo().getIpAddress(),
			result.getLoginInfo().getPcName(),
			result.getLoginInfo().getAccount(),
			result.getListResultLogDeletions().stream().map(item -> SspdtDeletionResultLog.toEntity(item)).collect(Collectors.toList())
			);
	}
}
