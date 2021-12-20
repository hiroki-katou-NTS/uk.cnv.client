package nts.uk.ctx.at.function.infra.repository.alarm.mailsettings;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmMailSendingRole;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmMailSendingRoleRepository;
import nts.uk.ctx.at.function.infra.entity.alarm.mailsettings.KfnmtAlstMailSetRole;

import javax.ejb.Stateless;
import java.util.Optional;

@Stateless
public class JpaAlarmMailSendingRoleRepository extends JpaRepository implements AlarmMailSendingRoleRepository {
    @Override
    public Optional<AlarmMailSendingRole> find(String cid, int individualWkpClassify) {
        String query = "SELECT a FROM KfnmtAlstMailSetRole a WHERE a.pk.companyID = :cid AND a.pk.personWkpAtr = :individualWkpClassify ";
        return this.queryProxy().query(query, KfnmtAlstMailSetRole.class)
                .setParameter("cid", cid)
                .setParameter("individualWkpClassify", individualWkpClassify)
                .getSingle(KfnmtAlstMailSetRole::toDomain);
    }

    @Override
    public void insert(AlarmMailSendingRole alarmMailSendingRole) {
        if (alarmMailSendingRole == null) return;
        this.commandProxy().insert(KfnmtAlstMailSetRole.of(alarmMailSendingRole));
    }

    @Override
    public void update(AlarmMailSendingRole alarmMailSendingRole) {
        if (alarmMailSendingRole == null) return;
        val newEntity = KfnmtAlstMailSetRole.of(alarmMailSendingRole);
        val updateEntity = this.queryProxy().find(newEntity.pk, KfnmtAlstMailSetRole.class).orElse(null);
        if (updateEntity != null) {
            updateEntity.fromEntity(newEntity);
            this.commandProxy().update(updateEntity);
        }
    }
}
