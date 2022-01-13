package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class AlarmMailSendingRoleTest {
    @Test
    public void getters() {
        AlarmMailSendingRole target = new AlarmMailSendingRole(
                IndividualWkpClassification.INDIVIDUAL,
                true,
                false,
                Arrays.asList("ROLE_ID"));
        NtsAssert.invokeGetters(target);
    }

    @Test
    public void setters() {
        AlarmMailSendingRole target = new AlarmMailSendingRole(
                IndividualWkpClassification.INDIVIDUAL,
                true,
                false,
                Arrays.asList("ROLE_ID"));
        assertThat(target.getIndividualWkpClassify().value).isEqualTo(0);
        assertThat(target.isRoleSetting()).isTrue();
        assertThat(target.isSendResult()).isFalse();
        assertThat(target.getRoleIds()).hasSize(1);
        assertThat(target.getRoleIds().get(0)).isEqualTo("ROLE_ID");
    }
}
