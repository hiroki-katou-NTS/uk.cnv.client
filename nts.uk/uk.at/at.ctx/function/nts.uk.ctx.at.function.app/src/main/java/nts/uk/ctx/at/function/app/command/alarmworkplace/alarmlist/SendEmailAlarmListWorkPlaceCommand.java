package nts.uk.ctx.at.function.app.command.alarmworkplace.alarmlist;

import lombok.Data;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmDto;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmManualDto;

import java.util.List;

@Data
public class SendEmailAlarmListWorkPlaceCommand {

    private List<String> workplaceIds;
    List<ValueExtractAlarmManualDto> listValueExtractAlarmDto;
    private String currentAlarmCode;

}
