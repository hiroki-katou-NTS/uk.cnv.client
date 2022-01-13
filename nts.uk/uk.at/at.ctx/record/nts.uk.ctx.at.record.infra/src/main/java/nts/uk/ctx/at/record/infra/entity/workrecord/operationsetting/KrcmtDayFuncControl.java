package nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.Comment;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.YourselfConfirmError;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
* 日別実績の機能制限
*/
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_DAY_FUNC_CONTROL")
public class KrcmtDayFuncControl extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final JpaEntityMapper<KrcmtDayFuncControl> MAPPER = new JpaEntityMapper<>(KrcmtDayFuncControl.class);
	
	/**
	* ID
	*/
	@EmbeddedId
	public KrcmtDayFuncControlPk dayFuncControlPk;
    
	/**
	* コメント
	*/
	@Basic(optional = true)
	@Column(name = "COMMENT")
	public String comment;
     
    /**
    * 36協定情報を表示する
    */
    @Basic(optional = false)
    @Column(name = "DISP_36_ATR")
    public int disp36Atr;
     
    /**
    * フレックス勤務者のフレックス不足情報を表示する
    */
    @Basic(optional = false)
    @Column(name = "FLEX_DISP_ATR")
    public int flexDispAtr;
     
    /**
    * 抽出時にエラーがある場合はエラー参照ダイアログを表示する
    */
    @Basic(optional = false)
    @Column(name = "CHECK_ERR_REF_DISP")
    public int checkErrRefDisp;
    
    /**
    * 日の本人確認を利用する
    */
    @Basic(optional = false)
    @Column(name = "DAY_SELF_CHK")
    public int daySelfChk;
     
    /**
    * 月の本人確認を利用する
    */
    @Basic(optional = false)
    @Column(name = "MON_SELF_CHK")
    public int monSelfChk;
     
    /**
    * エラーがある場合の日の本人確認
    */
    @Basic(optional = true)
    @Column(name = "DAY_SELF_CHK_ERROR")
    public Integer daySelfChkError;
    
    /**
    * 日の承認者確認を利用する
    */
    @Basic(optional = false)
    @Column(name = "DAY_BOSS_CHK")
    public int dayBossChk;
     
    /**
    * 月の承認者確認を利用する
    */
    @Basic(optional = false)
    @Column(name = "MON_BOSS_CHK")
    public int monBossChk;
     
    /**
    * エラーがある場合の日の承認者確認
    */
    @Basic(optional = true)
    @Column(name = "DAY_BOSS_CHK_ERROR")
    public Integer dayBossChkError;

	@Override
	protected Object getKey() {
		return dayFuncControlPk;
	}
	
	public static KrcmtDayFuncControl toEntity(DaiPerformanceFun daiPerformanceFun,
			IdentityProcess identityProcess,
			ApprovalProcess approvalProcess) {
		
		return new KrcmtDayFuncControl(new KrcmtDayFuncControlPk(daiPerformanceFun.getCid()), 
						daiPerformanceFun.getComment().toString(),
						daiPerformanceFun.getDisp36Atr(), 
						daiPerformanceFun.getFlexDispAtr(), 
						daiPerformanceFun.getCheckErrRefDisp(),
						identityProcess.getUseDailySelfCk(),
						identityProcess.getUseMonthSelfCK(), 
						identityProcess.getYourselfConfirmError() == null ? null : identityProcess.getYourselfConfirmError().value,
						approvalProcess.getUseDailyBossChk(), 
						approvalProcess.getUseMonthBossChk(), 
						approvalProcess.getSupervisorConfirmError() == null ? null : approvalProcess.getSupervisorConfirmError().value);
	}
	
	public DaiPerformanceFun toDomainDaiPerformanceFun() {
		return new DaiPerformanceFun(this.dayFuncControlPk.cid, 
				new Comment(this.comment),
        		this.disp36Atr, 
        		this.flexDispAtr, 
        		this.checkErrRefDisp);
	}
	
	public IdentityProcess toDomainIdentityProcess() {
		return new IdentityProcess(this.dayFuncControlPk.cid, 
				this.daySelfChk, this.monSelfChk, 
        		this.daySelfChkError == null ? null : EnumAdaptor.valueOf(this.daySelfChkError, YourselfConfirmError.class));
	}
	
	public ApprovalProcess toDomainApprovalProcess() {
		return new ApprovalProcess(this.dayFuncControlPk.cid, 
        		this.dayBossChk, this.monBossChk, 
        		dayBossChkError == null ? null : EnumAdaptor.valueOf(this.dayBossChkError, YourselfConfirmError.class));
	}
}
