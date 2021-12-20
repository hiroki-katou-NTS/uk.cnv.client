package nts.uk.ctx.at.function.infra.entity.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmListExecutionMailSetting;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.IndividualWkpClassification;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailAddress;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailAddressSet;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.NormalAutoClassification;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.PersonalManagerClassification;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Entity: アラームリスト実行メール設定
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "KFNMT_ALST_EXE_MAILSET")
public class KfnmtAlstExeMailSetting extends ContractUkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KfnmtAlstExeMailSettingPK pk;

    /**
     * 件名
     */
    @Column(name = "SUBJECT")
    public String subject;

    /**
     * 本文
     */
    @Column(name = "TEXT")
    public String text;

    /**
     * BBCメールアドレス
     */
    @Column(name = "BCC")
    public String bcc;

    /**
     * CCメールアドレス
     */
    @Column(name = "CC")
    public String cc;

    /**
     * 返信用メールアドレス
     */
    @Column(name = "MAIL_REPLY")
    public String mailReply;

    /**
     * 送信元アドレス
     */
    @Column(name = "SENDER_ADDRESS")
    public String senderAddress;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    public static KfnmtAlstExeMailSetting of(AlarmListExecutionMailSetting domain, String bccId, String ccId) {
        Optional<MailSettings> content = domain.getContentMailSettings();

        return new KfnmtAlstExeMailSetting(
                new KfnmtAlstExeMailSettingPK(
                        AppContexts.user().companyId(),
                        domain.getIndividualWkpClassify().value,
                        domain.getNormalAutoClassify().value,
                        domain.getPersonalManagerClassify().value),
                content.isPresent() && content.get().getSubject().isPresent() ? content.get().getSubject().get().v() : null,
                content.isPresent() && content.get().getText().isPresent() ? content.get().getText().get().v() : null,
                bccId,
                ccId,
                content.isPresent() && content.get().getMailRely().isPresent() ? content.get().getMailRely().get().v() : null,
                domain.getSenderAddress().isPresent() ? domain.getSenderAddress().get().v() : null
        );
    }

    public void fromEntity(KfnmtAlstExeMailSetting entity) {
        this.subject = entity.subject;
        this.text = entity.text;
        this.bcc = entity.bcc;
        this.cc = entity.cc;
        this.mailReply = entity.mailReply;
        this.senderAddress = entity.senderAddress;
    }

    public AlarmListExecutionMailSetting toDomain(List<MailAddressSet> mailSettingListBCC, List<MailAddressSet> mailSettingListCC) {
        val mailSettingBcc = mailSettingListBCC.stream().filter(x -> x.getId().equals(this.bcc))
                .map(MailAddressSet::getMailAddress).collect(Collectors.toList());
        val mailSettingCc = mailSettingListCC.stream().filter(x -> x.getId().equals(this.cc))
                .map(MailAddressSet::getMailAddress).collect(Collectors.toList());
        return new AlarmListExecutionMailSetting(
                IndividualWkpClassification.of(this.pk.personWkpAtr),
                NormalAutoClassification.of(this.pk.normalAutoAtr),
                PersonalManagerClassification.of(this.pk.personalManagerAtr),
                Optional.ofNullable((StringUtils.isNotEmpty(this.subject) || StringUtils.isNotEmpty(this.text) || !CollectionUtil.isEmpty(mailSettingBcc) || !CollectionUtil.isEmpty(mailSettingCc) || StringUtils.isNotEmpty(this.mailReply))
                        ? new MailSettings(
                        subject,
                        text,
                        mailSettingCc,
                        mailSettingBcc,
                        mailReply)
                        : null),
                Optional.ofNullable(StringUtils.isNotEmpty(this.senderAddress) ? new MailAddress(senderAddress) : null),
                false
        );
    }
}
