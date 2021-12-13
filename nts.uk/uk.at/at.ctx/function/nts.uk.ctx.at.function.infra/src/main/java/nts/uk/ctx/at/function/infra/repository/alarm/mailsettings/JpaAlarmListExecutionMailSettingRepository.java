package nts.uk.ctx.at.function.infra.repository.alarm.mailsettings;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.primitive.PrimitiveValueBase;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.*;
import nts.uk.ctx.at.function.infra.entity.alarm.mailsettings.KfnmtAlstExeMailSetting;
import nts.uk.ctx.at.function.infra.entity.alarm.mailsettings.KfnmtMailSettingList;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class JpaAlarmListExecutionMailSettingRepository extends JpaRepository implements AlarmListExecutionMailSettingRepository {
    private static final String SELECT;
    private static final String SELECT_ALL_BY_CID_PM;
    private static final String SELECT_ALL_BY_CID_PM_IM;
    private static final String SELECT_ALL_BY_CID_IW_NA;

    static {
        StringBuilder builderString = new StringBuilder();
        builderString.append("SELECT a FROM KfnmtAlstExeMailSetting a ");
        SELECT = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(SELECT);
        builderString.append("WHERE a.pk.companyID = :cid AND a.pk.personWkpAtr = :individualWkpClassify");
        SELECT_ALL_BY_CID_PM = builderString.toString();
        SELECT_ALL_BY_CID_IW_NA = SELECT_ALL_BY_CID_PM + " AND a.pk.normalAutoAtr = :normalAutoClassify";
        SELECT_ALL_BY_CID_PM_IM = SELECT_ALL_BY_CID_PM + " AND a.pk.personalManagerAtr = :personalManagerClassify";
    }

    private static final String FIND_MAIL_LIST = "SELECT a FROM KfnmtMailSettingList a WHERE a.listMailId IN :listMailIds";
    private static final String DELETE_MAIL_LIST = "DELETE FROM KfnmtMailSettingList m "
            + "WHERE m.listMailId = :listBccId "
            + "OR m.listMailId = :listCcId ";

    @Override
    public List<AlarmListExecutionMailSetting> getByCId(String cid, int individualWkpClassify) {
        val mailSet = this.queryProxy().query(SELECT_ALL_BY_CID_PM, KfnmtAlstExeMailSetting.class)
                .setParameter("cid", cid)
                .setParameter("individualWkpClassify", individualWkpClassify)
                .getList();
        if (mailSet.isEmpty()) return Collections.emptyList();

        val bccIds = mailSet.stream().map(x -> x.bcc).collect(Collectors.toList());
        val mailSettingListBCC = this.queryProxy().query(FIND_MAIL_LIST, KfnmtMailSettingList.class)
                .setParameter("listMailIds", bccIds).getList(this::toAddress);
        val ccIds = mailSet.stream().map(x -> x.cc).collect(Collectors.toList());
        val mailSettingListCC = this.queryProxy().query(FIND_MAIL_LIST, KfnmtMailSettingList.class)
                .setParameter("listMailIds", ccIds).getList(this::toAddress);

        return mailSet.stream().map(x -> x.toDomain(mailSettingListBCC, mailSettingListCC)).collect(Collectors.toList());
    }

    private MailAddressSet toAddress(KfnmtMailSettingList entity) {
        return new MailAddressSet(entity.listMailId, new MailAddress(entity.mailAddress));
    }

    @Override
    public void insertAll(List<AlarmListExecutionMailSetting> domainList) {
        if (domainList.isEmpty()) return;
        domainList.forEach(domain -> {
            String bccId = IdentifierUtil.randomUniqueId();
            String ccId = IdentifierUtil.randomUniqueId();

            // Insert entity KfnmtMailSettingList (BCC & CC)
            insertMailList(bccId, ccId, domain);

            // Insert entity KfnmtAlstExeMailSetting
            this.commandProxy().insert(KfnmtAlstExeMailSetting.of(domain, bccId, ccId));
        });
    }

    @Override
    public void updateAll(List<AlarmListExecutionMailSetting> domainList) {
        if (domainList.isEmpty()) return;

        domainList.forEach(domain -> {
            String bccId = IdentifierUtil.randomUniqueId();
            String ccId = IdentifierUtil.randomUniqueId();
            // Convert domain to entity
            val newEntity = KfnmtAlstExeMailSetting.of(domain, bccId, ccId);
            // Find exist
            val updateEntity = this.queryProxy().find(newEntity.pk, KfnmtAlstExeMailSetting.class).orElse(null);
            if (updateEntity != null) {
                // Delete entity KfnmtMailSettingList (BCC & CC)
                this.getEntityManager().createQuery(DELETE_MAIL_LIST)
                        .setParameter("listBccId", updateEntity.bcc)
                        .setParameter("listCcId", updateEntity.cc)
                        .executeUpdate();
                // Insert entity KfnmtMailSettingList (BCC & CC)
                insertMailList(bccId, ccId, domain);

                // Update entity KfnmtAlstExeMailSetting
                updateEntity.fromEntity(newEntity);
                this.commandProxy().update(updateEntity);
            } else {
                this.commandProxy().insert(newEntity);
            }
        });
    }

    @Override
    public List<AlarmListExecutionMailSetting> getByCompanyId(String cid, int personalManagerClassify, int individualWRClassification) {
        val mailSet = this.queryProxy().query(SELECT_ALL_BY_CID_PM_IM, KfnmtAlstExeMailSetting.class)
                .setParameter("cid", cid)
                .setParameter("individualWkpClassify", individualWRClassification)
                .setParameter("personalManagerClassify", personalManagerClassify)
                .getList();
        if (mailSet.isEmpty()) return Collections.emptyList();

        val bccIds = mailSet.stream().map(x -> x.bcc).collect(Collectors.toList());
        val mailSettingListBCC = this.queryProxy().query(FIND_MAIL_LIST, KfnmtMailSettingList.class)
                .setParameter("listMailIds", bccIds).getList(this::toAddress);
        val ccIds = mailSet.stream().map(x -> x.cc).collect(Collectors.toList());
        val mailSettingListCC = this.queryProxy().query(FIND_MAIL_LIST, KfnmtMailSettingList.class)
                .setParameter("listMailIds", ccIds).getList(this::toAddress);

        return mailSet.stream().map(x -> x.toDomain(mailSettingListBCC, mailSettingListCC)).collect(Collectors.toList());
    }

    @Override
    public List<AlarmListExecutionMailSetting> findBy(String cid, int individualWkpClassify, int normalAutoClassify) {
        val mailSet = this.queryProxy().query(SELECT_ALL_BY_CID_IW_NA, KfnmtAlstExeMailSetting.class)
                .setParameter("cid", cid)
                .setParameter("individualWkpClassify", individualWkpClassify)
                .setParameter("normalAutoClassify", normalAutoClassify)
                .getList();
        if (mailSet.isEmpty()) return Collections.emptyList();

        val bccIds = mailSet.stream().map(x -> x.bcc).collect(Collectors.toList());
        val mailSettingListBCC = this.queryProxy().query(FIND_MAIL_LIST, KfnmtMailSettingList.class)
                .setParameter("listMailIds", bccIds).getList(this::toAddress);
        val ccIds = mailSet.stream().map(x -> x.cc).collect(Collectors.toList());
        val mailSettingListCC = this.queryProxy().query(FIND_MAIL_LIST, KfnmtMailSettingList.class)
                .setParameter("listMailIds", ccIds).getList(this::toAddress);

        return mailSet.stream().map(x -> x.toDomain(mailSettingListBCC, mailSettingListCC)).collect(Collectors.toList());
    }

    private void insertMailList(String bccId, String ccId, AlarmListExecutionMailSetting domain) {
        Optional<MailSettings> content = domain.getContentMailSettings();
        List<String> bccList = !content.isPresent() ? Collections.emptyList() : content.get().getMailAddressBCC().stream().map(PrimitiveValueBase::v).collect(Collectors.toList());
        List<KfnmtMailSettingList> mailSettingListBCC = bccList.stream().map(mailAddress -> new KfnmtMailSettingList(bccId, mailAddress)).collect(Collectors.toList());
        this.commandProxy().insertAll(mailSettingListBCC);

        List<String> ccList = !content.isPresent() ? Collections.emptyList() : content.get().getMailAddressCC().stream().map(PrimitiveValueBase::v).collect(Collectors.toList());
        List<KfnmtMailSettingList> mailSettingListCC = ccList.stream().map(mailAddress -> new KfnmtMailSettingList(ccId, mailAddress)).collect(Collectors.toList());
        this.commandProxy().insertAll(mailSettingListCC);
    }
}
