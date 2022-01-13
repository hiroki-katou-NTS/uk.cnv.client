package nts.uk.ctx.at.record.infra.repository.workrecord.workmanagement.manhoursummarytable;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormat;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormatRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.workmanagement.manhoursummarytable.KrcmtRptDaiTask;
import nts.uk.ctx.at.record.infra.entity.workrecord.workmanagement.manhoursummarytable.KrcmtRptDaiTaskPk;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Optional;

@Stateless
public class JpaManHourSummaryTableFormatRepository extends JpaRepository implements ManHourSummaryTableFormatRepository {
    private static final String SELECT;
    private static final String SELECT_ALL_BY_CID;
    private static final String SELECT_BY_CODE;

    static {
        StringBuilder builderString = new StringBuilder();
        builderString.append("SELECT a FROM KrcmtRptDaiTask a ");
        SELECT = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(SELECT);
        builderString.append("WHERE a.pk.cid = :cid ORDER BY a.pk.code ASC ");
        SELECT_ALL_BY_CID = builderString.toString();

        builderString = new StringBuilder();
        builderString.append(SELECT);
        builderString.append("WHERE a.pk.cid = :cid AND a.pk.code = :code ");
        SELECT_BY_CODE = builderString.toString();

    }

    @Override
    public void insert(ManHourSummaryTableFormat domain) {
        if (domain == null) return;
        KrcmtRptDaiTask entity = KrcmtRptDaiTask.toEntity(domain);
        this.commandProxy().insert(entity);
    }

    @Override
    public void update(ManHourSummaryTableFormat domain) {
        if (domain == null) return;
        KrcmtRptDaiTask newEntity = KrcmtRptDaiTask.toEntity(domain);
        KrcmtRptDaiTask updateEntity = this.queryProxy().find(newEntity.pk, KrcmtRptDaiTask.class).orElse(null);
        if (updateEntity != null) {
            updateEntity.fromEntity(newEntity);
            this.commandProxy().update(updateEntity);
        }
    }

    @Override
    public void delete(String companyId, String code) {
        KrcmtRptDaiTaskPk krcmtRptDaiTaskPk = new KrcmtRptDaiTaskPk(companyId, code);
        this.commandProxy().remove(KrcmtRptDaiTask.class, krcmtRptDaiTaskPk);
    }

    @Override
    public List<ManHourSummaryTableFormat> getAll(String cid) {
        return this.queryProxy().query(SELECT_ALL_BY_CID, KrcmtRptDaiTask.class)
                .setParameter("cid", cid).getList(KrcmtRptDaiTask::toDomain);
    }

    @Override
    public Optional<ManHourSummaryTableFormat> get(String cid, String code) {
        return this.queryProxy().query(SELECT_BY_CODE, KrcmtRptDaiTask.class)
                .setParameter("cid", cid)
                .setParameter("code", code)
                .getSingle(KrcmtRptDaiTask::toDomain);
    }
}
