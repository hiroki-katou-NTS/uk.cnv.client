package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.val;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.function.dom.adapter.alarm.MailExportRolesDto;
import nts.uk.ctx.at.function.dom.adapter.role.AlarmMailSettingsAdapter;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.*;
import nts.uk.shr.com.context.AppContexts;
import org.apache.commons.lang3.BooleanUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class MailSettingFinder {

    @Inject
    private MailSettingAutomaticRepository mailAutomaticRepo;

    @Inject
    private MailSettingNormalRepository mailNormalRepo;

    @Inject
    private AlarmMailSendingRoleRepository mailSendingRoleRepository;

    @Inject
    private AlarmListExecutionMailSettingRepository mailSettingRepository;

    @Inject
    private AlarmMailSettingsAdapter mailSettingsAdapter;

    public MailAutoAndNormalDto findMailSet() {
        String companyId = AppContexts.user().companyId();
        if (StringUtil.isNullOrEmpty(companyId, true))
            return null;

        MailSettingAutomaticDto mailSettingAutomaticDto = new MailSettingAutomaticDto(mailAutomaticRepo.findByCompanyId(companyId));
        MailSettingNormalDto mailSettingNormalDto = new MailSettingNormalDto(mailNormalRepo.findByCompanyId(companyId));

        return new MailAutoAndNormalDto(mailSettingAutomaticDto, mailSettingNormalDto);
    }

    public AlarmMailSendingRoleDto getRole(int individualWkpClassify) {
        val role = mailSendingRoleRepository.find(AppContexts.user().companyId(), individualWkpClassify);
        if (!role.isPresent()) {
            return null;
        }
        return new AlarmMailSendingRoleDto(role.get().getIndividualWkpClassify().value,
                role.get().isRoleSetting(),
                role.get().isSendResult(),
                role.get().getRoleIds()
        );
    }

    public MailSettingDto getConfigured(int personalManagerClassify, int individualWRClassification) {
        val alarmListExecutionMailSettingList = mailSettingRepository.getByCompanyId(AppContexts.user().companyId(),
                personalManagerClassify,
                individualWRClassification
        );
        if (alarmListExecutionMailSettingList.isEmpty()) {
            return new MailSettingDto(false, Collections.emptyList());
        }
        boolean isConfigured = alarmListExecutionMailSettingList.stream().anyMatch(x -> x.isAlreadyConfigured());
        val mailSettins = alarmListExecutionMailSettingList.stream().map(x -> {
                    val mailContents = x.getContentMailSettings();
                    return new RegisterAlarmExecutionMailSettingsDTO(
                            x.getIndividualWkpClassify().value,
                            x.getNormalAutoClassify().value,
                            x.getPersonalManagerClassify().value,
                            mailContents.isPresent() ? new MailSettingsContentDto(
                                    mailContents.get().getSubject().isPresent() ? mailContents.get().getSubject().get().v() : "",
                                    mailContents.get().getText().isPresent() ? mailContents.get().getText().get().v() : "",
                                    mailContents.get().getMailAddressBCC().stream().map(bcc -> bcc.v()).collect(Collectors.toList()),
                                    mailContents.get().getMailAddressCC().stream().map(cc -> cc.v()).collect(Collectors.toList()),
                                    mailContents.get().getMailRely().isPresent() ? mailContents.get().getMailRely().get().v() : ""
                            ) : null,
                            x.getSenderAddress().isPresent() ? x.getSenderAddress().get().v() : null,
                            x.isSendResult()
                    );
                }
        ).collect(Collectors.toList());
        return new MailSettingDto(isConfigured, mailSettins);
    }

    public AlarmMailSettingDataDto getMailSettingInfo() {
        String companyId = AppContexts.user().companyId();
        if (StringUtil.isNullOrEmpty(companyId, true)) return null;

        // Get AlarmListExecutionMailSetting
        val mailSettings = mailSettingRepository.getByCId(companyId, IndividualWkpClassification.INDIVIDUAL.value);
        val mailSettingListDto = mailSettings.stream().map(item -> new AlarmExecutionMailSettingDto(
                new MailSettingInfo(
                        item.getIndividualWkpClassify().value,
                        item.getNormalAutoClassify().value,
                        item.getPersonalManagerClassify().value,
                        item.getContentMailSettings().isPresent() ? ContentMailSettingDto.create(item.getContentMailSettings().get())
                                : new ContentMailSettingDto("", "", Collections.emptyList(), Collections.emptyList(), ""),
                        item.getSenderAddress().isPresent() ? item.getSenderAddress().get().v() : "",
                        item.isSendResult()
                ),
                item.isAlreadyConfigured()
        )).collect(Collectors.toList());

        // Get AlarmMailSendingRole
        val mailSendingRole = mailSendingRoleRepository.find(companyId, IndividualWkpClassification.INDIVIDUAL.value);
        val mailSendingRoleDto = mailSendingRole.map(x -> new AlarmMailSendingRoleDto(
                x.getIndividualWkpClassify().value,
                x.isRoleSetting(),
                x.isSendResult(),
                x.getRoleIds()
        )).orElse(null);

        // Get Role name
        List<MailExportRolesDto> roleNameList = mailSendingRoleDto != null ? mailSettingsAdapter.getRoleNameList(mailSendingRoleDto.getRoleIds()) : Collections.emptyList();

        return new AlarmMailSettingDataDto(mailSettingListDto, mailSendingRoleDto, roleNameList);
    }
}
