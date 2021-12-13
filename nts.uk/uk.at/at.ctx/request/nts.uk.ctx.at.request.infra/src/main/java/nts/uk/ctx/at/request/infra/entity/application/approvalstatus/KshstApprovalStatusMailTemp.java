package nts.uk.ctx.at.request.infra.entity.application.approvalstatus;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.dom.application.approvalstatus.ApprovalStatusMailTemp;
import nts.uk.ctx.at.request.dom.application.approvalstatus.ApprovalStatusMailType;
import nts.uk.ctx.at.request.dom.setting.company.mailsetting.mailholidayinstruction.Content;
import nts.uk.ctx.at.request.dom.setting.company.mailsetting.mailholidayinstruction.Subject;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 承認状況メールテンプレート
 * 
 * @author dat.lh
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRQMT_APPROVAL_MAIL_TEMP")
public class KshstApprovalStatusMailTemp extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	public KshstApprovalStatusMailTempPk approvalStatusMailTempPk;

	/**
	 * URL承認埋込
	 */
	@Basic(optional = true)
	@Column(name = "URL_APPROVAL_EMBED")
	public boolean urlApprovalEmbed;

	/**
	 * URL日別埋込
	 */
	@Basic(optional = true)
	@Column(name = "URL_DAY_EMBED")
	public boolean urlDayEmbed;

	/**
	 * URL月別埋込
	 */
	@Basic(optional = true)
	@Column(name = "URL_MONTH_EMBED")
	public boolean urlMonthEmbed;

	/**
	 * メール件名
	 */
	@Basic(optional = false)
	@Column(name = "SUBJECT")
	public String subject;

	/**
	 * メール本文
	 */
	@Basic(optional = false)
	@Column(name = "TEXT")
	public String text;

	@Override
	protected Object getKey() {
		return approvalStatusMailTempPk;
	}

	public ApprovalStatusMailTemp toDomain() {
		return new ApprovalStatusMailTemp(this.approvalStatusMailTempPk.cid,
				EnumAdaptor.valueOf(this.approvalStatusMailTempPk.type, ApprovalStatusMailType.class),
				Objects.isNull(this.urlApprovalEmbed) ? null
						: EnumAdaptor.valueOf(BooleanUtils.toInteger(this.urlApprovalEmbed), NotUseAtr.class),
				Objects.isNull(this.urlDayEmbed) ? null : EnumAdaptor.valueOf(BooleanUtils.toInteger(this.urlDayEmbed), NotUseAtr.class),
				Objects.isNull(this.urlMonthEmbed) ? null : EnumAdaptor.valueOf(BooleanUtils.toInteger(this.urlMonthEmbed), NotUseAtr.class),
				new Subject(this.subject), new Content(this.text));
	}

	public static KshstApprovalStatusMailTemp toEntity(ApprovalStatusMailTemp domain) {

		return new KshstApprovalStatusMailTemp(
				new KshstApprovalStatusMailTempPk(
						domain.getCid(), 
						domain.getMailType().value), 
				BooleanUtils.toBoolean(domain.getUrlApprovalEmbed().value),
				BooleanUtils.toBoolean(domain.getUrlDayEmbed().value), 
				BooleanUtils.toBoolean(domain.getUrlMonthEmbed().value), 
				domain.getMailSubject().v(), 
				domain.getMailContent().v());
	}

}
