package nts.uk.ctx.sys.env.dom.mailnoticeset.company;

import mockit.Mocked;
import nts.arc.testing.assertion.NtsAssert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UserInfoUseMethodTest {

    @Mocked
    ContactSetting.ContactSettingBuilder contactSettingBuilder;

    @Mocked
    OtherContact.OtherContactBuilder otherContactBuilder;

    @Mocked
    EmailDestinationFunction emailDestinationFunction;

    @Mocked
    SettingContactInformation settingContactInformation;

    SettingContactInformationDto settingContactInformationDto = null;

    List<OtherContactDto> otherContactDtos = null;

    UserInfoUseMethodDto domainDto1 = null;

    UserInfoUseMethodDto domainDto2 = null;

    UserInformationUseMethod domain1 = null;

    @Before
    public void initTest() {

        otherContactDtos = new ArrayList<>();
        otherContactDtos.add(OtherContactDto.builder()
                .no(1)
                .contactUsageSetting(2)
                .contactName("")
                .build());
        otherContactDtos.add(OtherContactDto.builder()
                .no(2)
                .contactUsageSetting(2)
                .contactName("")
                .build());
        otherContactDtos.add(OtherContactDto.builder()
                .no(3)
                .contactUsageSetting(2)
                .contactName("")
                .build());
        otherContactDtos.add(OtherContactDto.builder()
                .no(4)
                .contactUsageSetting(0)
                .contactName("")
                .build());
        otherContactDtos.add(OtherContactDto.builder()
                .no(5)
                .contactUsageSetting(1)
                .contactName("")
                .build());

        settingContactInformationDto = SettingContactInformationDto.builder()
                .dialInNumber(ContactSettingDto.builder()
                        .contactUsageSetting(2)
                        .updatable(1)
                        .build())
                .companyEmailAddress(ContactSettingDto.builder()
                        .contactUsageSetting(1)
                        .updatable(1)
                        .build())
                .companyMobileEmailAddress(ContactSettingDto.builder()
                        .contactUsageSetting(0)
                        .updatable(1)
                        .build())
                .personalEmailAddress(ContactSettingDto.builder()
                        .contactUsageSetting(2)
                        .updatable(1)
                        .build())
                .personalMobileEmailAddress(ContactSettingDto.builder()
                        .contactUsageSetting(2)
                        .updatable(1)
                        .build())
                .extensionNumber(ContactSettingDto.builder()
                        .contactUsageSetting(2)
                        .updatable(1)
                        .build())
                .companyMobilePhone(ContactSettingDto.builder()
                        .contactUsageSetting(1)
                        .updatable(1)
                        .build())
                .personalMobilePhone(ContactSettingDto.builder()
                        .contactUsageSetting(2)
                        .updatable(1)
                        .build())
                .emergencyNumber1(ContactSettingDto.builder()
                        .contactUsageSetting(1)
                        .updatable(1)
                        .build())
                .emergencyNumber2(ContactSettingDto.builder()
                        .contactUsageSetting(2)
                        .updatable(1)
                        .build())
                .otherContacts(otherContactDtos)
                .build();

        domainDto1 = UserInfoUseMethodDto.builder()
                .companyId("000000000000-0001")
                .useOfProfile(1)
                .useOfPassword(1)
                .useOfNotice(1)
                .useOfLanguage(1)
                .settingContactInformationDto(settingContactInformationDto)
                .build();

        domain1 = UserInformationUseMethod.createFromMemento(domainDto1);

        domainDto2 = UserInfoUseMethodDto.builder().build();
        domain1.setMemento(domainDto2);

        emailDestinationFunction = new EmailDestinationFunction(EmailClassification.valueOf(0), new ArrayList<>());
        settingContactInformation = SettingContactInformation.builder().build();
    }

    @Test
    public void getters() {
        NtsAssert.invokeGetters(domain1);
        NtsAssert.invokeGetters(domainDto2);
    }

    @Test
    public void testEmailClassificationNull() {
        assertNull(EmailClassification.valueOf(10));
    }

    @Test
    public void testContactUsageSettingNull() {
        assertNull(ContactUsageSetting.valueOf(10));
    }

    @Test
    public void testContactSettingDtoToString() {
    	String builder = ContactSetting.builder().toString();
        assertThat(builder).isNotBlank();
    }

    @Test
    public void testOtherContactDtoToString() {
    	String builder = OtherContact.builder().toString();
        assertThat(builder).isNotBlank();
    }

    @Test
    public void testEmailDestinationFunctionToString() {
    	String builder = EmailDestinationFunction.builder().toString();
        assertThat(builder).isNotBlank();
    }

    @Test
    public void testSettingContactInformationToString() {
        String builder = SettingContactInformation.builder().toString();
        assertThat(builder).isNotBlank();
    }
}