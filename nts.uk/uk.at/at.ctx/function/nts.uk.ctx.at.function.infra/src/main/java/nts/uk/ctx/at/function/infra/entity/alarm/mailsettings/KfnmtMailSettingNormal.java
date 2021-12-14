package nts.uk.ctx.at.function.infra.entity.alarm.mailsettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingNormal;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@NoArgsConstructor
@Table(name = "KFNMT_ALST_MAILSET_MANUAL")
public class KfnmtMailSettingNormal extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected Object getKey() {
		return companyID;
	}

	@Id
	@Column(name = "CID")
	public String companyID;

	@Column(name = "CC", nullable = true)
	public String mailAddressCC;

	@OneToMany(mappedBy="mailNormalCC", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "KFNMT_ALST_MAILSET")
	public List<KfnmtMailSettingList> mailSettingListCC;
	
	@Column(name = "BCC", nullable = true)
	public String mailAddressBCC;
	
	@OneToMany(mappedBy="mailNormalBCC", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "KFNMT_ALST_MAILSET")
	public List<KfnmtMailSettingList> mailSettingListBCC;
	
	@Column(name = "SUBJECT", nullable = true)
	public String subject;

	@Column(name = "TEXT", nullable = true)
	public String text;

	@Column(name = "MAIL_REPLY", nullable = true)
	public String mailRely;

	@Column(name = "ADMIN_CC", nullable = true)
	public String adminMailAddressCC;

	@OneToMany(mappedBy="mailNormalAdminCC", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "KFNMT_ALST_MAILSET")
	public List<KfnmtMailSettingList> mailSettingListAdminCC;
	
	@Column(name = "ADMIN_BCC", nullable = true)
	public String adminMailAddressBCC;
	
	@OneToMany(mappedBy="mailNormalAdminBCC", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "KFNMT_ALST_MAILSET")
	public List<KfnmtMailSettingList> mailSettingListAdminBCC;

	@Column(name = "ADMIN_SUBJECT", nullable = true)
	public String adminSubject;

	@Column(name = "ADMIN_TEXT", nullable = true)
	public String adminText;

	@Column(name = "ADMIN_MAIL_REPLY", nullable = true)
	public String adminMailRely;

	public MailSettingNormal toDomain(List<String> mailSettingListCC, List<String> mailSettingListBCC, List<String> mailSettingListAdminCC, List<String> mailSettingListAdminBCC){
		
		MailSettings mailSet = new MailSettings(this.subject, this.text, mailSettingListCC, mailSettingListBCC, this.mailRely);
		MailSettings adminMailSet = new MailSettings(this.adminSubject, this.adminText, mailSettingListAdminCC, mailSettingListAdminBCC, this.adminMailRely);
		 
		return new MailSettingNormal(this.companyID, mailSet, adminMailSet);
	}
	
	public static KfnmtMailSettingNormal toEntity(String IdCC, String IdBCC , String IdAdCC, String IdAdBCC, MailSettingNormal domain) {
		
		List<String> CC = domain.getMailSettings().get().getMailAddressCC();
		List<KfnmtMailSettingList> mailSettingListCC = new ArrayList<>();
		for (String c : CC) {
			mailSettingListCC.add(new KfnmtMailSettingList(IdCC, c));
		}
		
		List<String> BCC = domain.getMailSettings().get().getMailAddressBCC();
		List<KfnmtMailSettingList> mailSettingListBCC = new ArrayList<>();
		for (String c : BCC) {
			mailSettingListBCC.add(new KfnmtMailSettingList(IdBCC, c));
		}
		
		List<String> AdCC = domain.getMailSettingAdmins().get().getMailAddressCC();
		List<KfnmtMailSettingList> mailSettingListAdCC = new ArrayList<>();
		for (String c : AdCC) {
			mailSettingListAdCC.add(new KfnmtMailSettingList(IdAdCC, c));
		}
		
		List<String> AdBCC = domain.getMailSettingAdmins().get().getMailAddressBCC();
		List<KfnmtMailSettingList> mailSettingListAdBCC = new ArrayList<>();
		for (String c : AdBCC) {
			mailSettingListAdBCC.add(new KfnmtMailSettingList(IdAdBCC, c));
		}
		
		return new KfnmtMailSettingNormal(domain.getCompanyID(), 
				IdCC, mailSettingListCC, IdBCC, mailSettingListBCC,
				domain.getMailSettings().get().getSubject().get().v(), domain.getMailSettings().get().getText().get().v(), domain.getMailSettings().get().getMailRely().get().v(),
				IdAdCC, mailSettingListAdCC, IdAdBCC, mailSettingListAdBCC, 
				domain.getMailSettingAdmins().get().getSubject().get().v(), domain.getMailSettingAdmins().get().getText().get().v(), domain.getMailSettingAdmins().get().getMailRely().get().v());
	}

	public KfnmtMailSettingNormal(String companyID, String mailAddressCC, List<KfnmtMailSettingList> mailSettingListCC,
			String mailAddressBCC, List<KfnmtMailSettingList> mailSettingListBCC, String subject, String text,
			String mailRely, String adminMailAddressCC, List<KfnmtMailSettingList> mailSettingListAdminCC,
			String adminMailAddressBCC, List<KfnmtMailSettingList> mailSettingListAdminBCC, String adminSubject,
			String adminText, String adminMailRely) {
		super();
		this.companyID = companyID;
		this.mailAddressCC = mailAddressCC;
		this.mailSettingListCC = mailSettingListCC;
		this.mailAddressBCC = mailAddressBCC;
		this.mailSettingListBCC = mailSettingListBCC;
		this.subject = subject;
		this.text = text;
		this.mailRely = mailRely;
		this.adminMailAddressCC = adminMailAddressCC;
		this.mailSettingListAdminCC = mailSettingListAdminCC;
		this.adminMailAddressBCC = adminMailAddressBCC;
		this.mailSettingListAdminBCC = mailSettingListAdminBCC;
		this.adminSubject = adminSubject;
		this.adminText = adminText;
		this.adminMailRely = adminMailRely;
	}
	
	public void updateEntity(KfnmtMailSettingNormal entity){
		this.mailAddressCC = entity.mailAddressCC;
		this.mailSettingListCC = entity.mailSettingListCC;
		this.mailAddressBCC = entity.mailAddressBCC;
		this.mailSettingListBCC = entity.mailSettingListBCC;
		this.subject = entity.subject;
		this.text = entity.text;
		this.mailRely = entity.mailRely;
		this.adminMailAddressCC = entity.adminMailAddressCC;
		this.mailSettingListAdminCC = entity.mailSettingListAdminCC;
		this.adminMailAddressBCC = entity.adminMailAddressBCC;
		this.mailSettingListAdminBCC = entity.mailSettingListAdminBCC;
		this.adminSubject = entity.adminSubject;
		this.adminText = entity.adminText;
		this.adminMailRely = entity.adminMailRely;
	}
}
