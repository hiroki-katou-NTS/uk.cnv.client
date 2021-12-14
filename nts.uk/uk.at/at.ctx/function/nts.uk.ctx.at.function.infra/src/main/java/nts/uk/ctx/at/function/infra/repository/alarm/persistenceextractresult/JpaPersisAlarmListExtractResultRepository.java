package nts.uk.ctx.at.function.infra.repository.alarm.persistenceextractresult;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.function.infra.entity.alarm.persistenceextractresult.KfndtAlarmExtracResult;
import nts.uk.ctx.at.function.infra.entity.alarm.persistenceextractresult.KfndtAlarmExtracResultPK;
import nts.uk.ctx.at.function.infra.entity.alarm.persistenceextractresult.KfndtPersisAlarmExt;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmPatternCode;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmPatternName;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaPersisAlarmListExtractResultRepository extends JpaRepository implements PersisAlarmListExtractResultRepository {
    private static final String REMOVE_EXTRACT_RESULT = "DELETE FROM KfndtAlarmExtracResult a WHERE a.pk.cid = :cid AND a.pk.sid = :sid "
            + " AND a.pk.category = :category AND a.pk.alarmCheckCode = :code AND a.pk.checkAtr = :checkType "
            + " AND a.pk.conditionNo = :no AND a.pk.startDate = :startDate ";

    @Override
    public Optional<PersistenceAlarmListExtractResult> getAlarmExtractResult(String runCode, String patternCode, List<String> empIds) {
        return this.queryProxy().query("select distinct a from KfndtPersisAlarmExt a join a.extractResults b " +
                "where a.autoRunCode = :runCode and a.patternCode = :patternCode and b.pk.sid in :empIds", KfndtPersisAlarmExt.class)
                .setParameter("runCode", runCode)
                .setParameter("patternCode", patternCode)
                .setParameter("empIds", empIds).getSingle(KfndtPersisAlarmExt::toDomain);
    }

    @Override
    public Optional<PersistenceAlarmListExtractResult> getAlarmExtractResult(String companyId, String patternCode, String runCode) {
        String sql = "select distinct a from KfndtPersisAlarmExt a join a.extractResults b where a.pk.cid = :companyId and a.patternCode = :patternCode and a.autoRunCode = :runCode";
        val data = this.queryProxy().query(sql, KfndtPersisAlarmExt.class)
                .setParameter("companyId", companyId)
                .setParameter("patternCode", patternCode)
                .setParameter("runCode", runCode)
                .getList(KfndtPersisAlarmExt::toDomain);
        if (data.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(data.get(0));
    }

    @Override
    public void insert(PersistenceAlarmListExtractResult domain) {
        if (domain == null || domain.getAlarmListExtractResults().isEmpty()) {
            return;
        }
        val data = this.queryProxy().query("select distinct a from KfndtPersisAlarmExt a join a.extractResults b " +
                "where a.autoRunCode = :runCode and a.patternCode = :patternCode ", KfndtPersisAlarmExt.class)
                .setParameter("runCode", domain.getAutoRunCode())
                .setParameter("patternCode", domain.getAlarmPatternCode().v())
                .getSingle(KfndtPersisAlarmExt::toDomain);

        if (!data.isPresent()) {
            String processId = IdentifierUtil.randomUniqueId();
            KfndtPersisAlarmExt entity = KfndtPersisAlarmExt.of(domain, processId);
            this.commandProxy().insert(entity);
        } else {
            Optional<String> processId;
            try (PreparedStatement sql = this.connection().prepareStatement("SELECT TOP 1 a.PROCESS_ID FROM KFNDT_PERSIS_ALARM_EXT a"
                    + " INNER JOIN KFNDT_ALARM_EXTRAC_RESULT b "
                    + " ON a.CID = b.CID AND a.PROCESS_ID = b.PROCESS_ID"
                    + " WHERE a.AUTORUN_CODE = ?"
                    + " AND a.PATTERN_CODE = ?")) {
                sql.setString(1, domain.getAutoRunCode());
                sql.setString(2, domain.getAlarmPatternCode().v());
                processId = new NtsResultSet(sql.executeQuery()).getSingle(rec -> rec.getString("PROCESS_ID"));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (processId.isPresent()) {
            	KfndtPersisAlarmExt oldEntity = KfndtPersisAlarmExt.of(data.get(), processId.get());
                KfndtPersisAlarmExt updateEntity = KfndtPersisAlarmExt.of(domain, processId.get());
                oldEntity.extractResults.addAll(updateEntity.extractResults);
                this.commandProxy().update(oldEntity);
            }
        }
    }

    @Override
    public void delete(PersistenceAlarmListExtractResult domain) {
        if (domain == null || domain.getAlarmListExtractResults().isEmpty()) {
            return;
        }
        List<KfndtAlarmExtracResultPK> lstDelete = new ArrayList<>();
        String cid = domain.getCompanyID();
        for (AlarmEmployeeList alarmEmployeeList : domain.getAlarmListExtractResults()) {
            for (AlarmExtractInfoResult y : alarmEmployeeList.getAlarmExtractInfoResults()) {
                for (ExtractResultDetail z : y.getExtractionResultDetails()) {
                    lstDelete.add(new KfndtAlarmExtracResultPK(
                            cid,
                            "",
                            alarmEmployeeList.getEmployeeID(),
                            y.getAlarmCategory().value,
                            y.getAlarmCheckConditionCode().v(),
                            y.getAlarmListCheckType().value,
                            y.getAlarmCheckConditionNo(),
                            z.getPeriodDate().getStartDate().isPresent() ? String.valueOf(z.getPeriodDate().getStartDate().get()) : String.valueOf(GeneralDate.min())));
                }
            }
        }

        String delete = "DELETE FROM KFNDT_ALARM_EXTRAC_RESULT WHERE CID = ? AND SID = ? "
                + " AND CATEGORY = ? AND ALARM_CHECK_CODE = ? AND CHECK_ATR = ? "
                + " AND CONDITION_NO = ? AND START_DATE = ? ";
        for (KfndtAlarmExtracResultPK x : lstDelete) {
            PreparedStatement ps1 = null;
            try {
                ps1 = this.connection().prepareStatement(delete);
                ps1.setString(1, cid);
                ps1.setString(2, x.sid);
                ps1.setInt(3, x.category);
                ps1.setString(4, x.alarmCheckCode);
                ps1.setInt(5, x.checkAtr);
                ps1.setString(6, x.conditionNo);
                ps1.setString(7, x.startDate);
                ps1.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Exception: "+ e.getMessage());
            }

//            this.getEntityManager().createQuery(REMOVE_EXTRACT_RESULT)
//                    .setParameter("cid", cid)
//                    .setParameter("sid", x.sid)
//                    .setParameter("category", x.category)
//                    .setParameter("code", x.alarmCheckCode)
//                    .setParameter("checkType", x.checkAtr)
//                    .setParameter("no", x.conditionNo)
//                    .setParameter("startDate", x.startDate)
//                    .executeUpdate();
//            this.getEntityManager().flush();
        }
    }

    @Override
    public List<PersistenceAlarmListExtractResult> getAlarmExtractResult(String companyId, List<String> employeeIds) {
        if (CollectionUtil.isEmpty(employeeIds)) return Collections.emptyList();

        List<PersistenceAlarmListExtractResult> results;
        List<KfndtAlarmExtracResult> extractResults = this.queryProxy()
                .query("select a from KfndtAlarmExtracResult a where a.pk.cid = :companyId and a.pk.sid in :employeeIds", KfndtAlarmExtracResult.class)
                .setParameter("companyId", companyId)
                .setParameter("employeeIds", employeeIds)
                .getList();

        List<String> processIds = extractResults.stream().map(i -> i.pk.processId).distinct().collect(Collectors.toList());
        try (PreparedStatement statement = this.connection()
                .prepareStatement("select * from KFNDT_PERSIS_ALARM_EXT a where a.CID = ? and a.PROCESS_ID in ("
                        + processIds.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {

            statement.setString(1, companyId);
            for (int i = 0; i < processIds.size(); i++) {
                statement.setString(i + 2, processIds.get(i));
            }

            results = new NtsResultSet(statement.executeQuery()).getList(rec -> {
                String processId = rec.getString("PROCESS_ID");
                List<AlarmEmployeeList> alarmListExtractResults = KfndtAlarmExtracResult.toDomain(extractResults.stream()
                        .filter(i -> i.pk.processId.equals(processId)).collect(Collectors.toList()));

                return new PersistenceAlarmListExtractResult(
                        new AlarmPatternCode(rec.getString("PATTERN_CODE")),
                        new AlarmPatternName(rec.getString("PATTERN_NAME")),
                        alarmListExtractResults,
                        rec.getString("CID"),
                        rec.getString("AUTORUN_CODE")
                );
            });

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return results;
    }
}
