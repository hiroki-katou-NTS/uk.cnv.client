package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import lombok.val;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class AlarmListExecutionMailSettingTest {
    @Test
    public void getters() {
        AlarmListExecutionMailSetting executionMailSetting = new AlarmListExecutionMailSetting(
                IndividualWkpClassification.INDIVIDUAL,
                NormalAutoClassification.NORMAL,
                PersonalManagerClassification.EMAIL_SETTING_FOR_ADMIN,
                Optional.empty(),
                Optional.empty(),
                false);
        NtsAssert.invokeGetters(executionMailSetting);
    }

    @Test
    public void setters() {
        AlarmListExecutionMailSetting target = new AlarmListExecutionMailSetting(
                IndividualWkpClassification.of(0),
                NormalAutoClassification.of(1),
                PersonalManagerClassification.of(1),
                Optional.of(new MailSettings(
                        "subject@gmail.com",
                        "text",
                        Collections.singletonList(new MailAddress("cc1@gmail.com")),
                        Collections.singletonList(new MailAddress("bcc1@gmail.com")),
                        "admin@gmail.com"
                )),
                Optional.of(new MailAddress("sender@gmail.com")),
                false);

        // Assert
        assertThat(target.getIndividualWkpClassify().value).isEqualTo(0);
        assertThat(target.getNormalAutoClassify().value).isEqualTo(1);
        assertThat(target.getPersonalManagerClassify().value).isEqualTo(1);
        assertThat(target.getContentMailSettings().get().getSubject().get().v()).isEqualTo("subject@gmail.com");
        assertThat(target.getContentMailSettings().get().getText().get().v()).isEqualTo("text");
        assertThat(target.getContentMailSettings().get().getMailAddressBCC()).hasSize(1);
        assertThat(target.getContentMailSettings().get().getMailAddressBCC().get(0).v()).isEqualTo("bcc1@gmail.com");
        assertThat(target.getContentMailSettings().get().getMailAddressCC()).hasSize(1);
        assertThat(target.getContentMailSettings().get().getMailAddressCC().get(0).v()).isEqualTo("cc1@gmail.com");
        assertThat(target.getSenderAddress().get().v()).isEqualTo("sender@gmail.com");
    }

    @Test
    public void isAlreadyConfigured_false(){
        AlarmListExecutionMailSetting instance = new AlarmListExecutionMailSetting(
                IndividualWkpClassification.of(0),
                NormalAutoClassification.of(1),
                PersonalManagerClassification.of(1),
                Optional.empty(),
                Optional.empty(),
                false);

        // Execute
        val result = instance.isAlreadyConfigured();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void isAlreadyConfigured_true(){
        AlarmListExecutionMailSetting instance = new AlarmListExecutionMailSetting(
                IndividualWkpClassification.of(0),
                NormalAutoClassification.of(1),
                PersonalManagerClassification.of(1),
                Optional.of(new MailSettings(
                        "subject@gmail.com",
                        "text",
                        Collections.singletonList(new MailAddress("cc1@gmail.com")),
                        Collections.singletonList(new MailAddress("bcc1@gmail.com")),
                        "admin@gmail.com"
                )),
                Optional.of(new MailAddress("sender@gmail.com")),
                false);

        // Execute
        val result = instance.isAlreadyConfigured();
        // Assert
        assertThat(result).isTrue();
    }
}
