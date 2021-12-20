package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import java.util.Optional;

public interface AlarmMailSendingRoleRepository {
    Optional<AlarmMailSendingRole> find(String cid, int individualWkpClassify);

    void insert(AlarmMailSendingRole alarmMailSendingRole);

    void update(AlarmMailSendingRole alarmMailSendingRole);
}
