package nts.uk.ctx.at.function.infra.entity.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmMailSendingRole;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.IndividualWkpClassification;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
import org.apache.commons.lang3.BooleanUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity: アラームメール送信ロール
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "KFNMT_ALARM_MAIL_SETROLE")
public class KfnmtAlstMailSetRole extends ContractUkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KfnmtAlstMailSetRolePK pk;

    /** ロール設定 */
    @Column(name = "ROLE_SET")
    public int roleSetting;

    /** マスタチェック結果を就業担当へ送信 */
    @Column(name = "SEND_MSTCHK_RESULT_CHARGE_EMP")
    public int sendResult;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    @OneToMany(mappedBy = "mailSetRole", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "KFNMT_ALST_SELECT_ROLE")
    public List<KfnmtAlstSelectRole> alarmSelectRoles;

    public void fromEntity(KfnmtAlstMailSetRole entity){
        this.roleSetting = entity.roleSetting;
        this.sendResult = entity.sendResult;
        this.alarmSelectRoles = entity.alarmSelectRoles;
    }

    public static KfnmtAlstMailSetRole of(AlarmMailSendingRole domain) {
        val cid = AppContexts.user().companyId();
        val selectRoles = domain.getRoleIds().stream().map(role -> new KfnmtAlstSelectRole(
                new KfnmtAlstSelectRolePK(
                        cid,
                        domain.getIndividualWkpClassify().value,
                        role)
        )).collect(Collectors.toList());
        return new KfnmtAlstMailSetRole(
                new KfnmtAlstMailSetRolePK(
                        cid,
                        domain.getIndividualWkpClassify().value),
                BooleanUtils.toInteger(domain.isRoleSetting()),
                BooleanUtils.toInteger(domain.isSendResult()),
                selectRoles
        );
    }

    public AlarmMailSendingRole toDomain() {
        return new AlarmMailSendingRole(
                IndividualWkpClassification.of(this.pk.personWkpAtr),
                BooleanUtils.toBoolean(this.roleSetting == 1),
                BooleanUtils.toBoolean(this.sendResult == 1),
                this.alarmSelectRoles.stream().map(r -> r.getPk().roleId).collect(Collectors.toList())
        );
    }
}
