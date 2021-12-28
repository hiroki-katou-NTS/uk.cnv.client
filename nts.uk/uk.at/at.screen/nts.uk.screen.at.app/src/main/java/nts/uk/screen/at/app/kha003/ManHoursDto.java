package nts.uk.screen.at.app.kha003;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManHoursDto {
    List<ManHourInfoDto> manHoursSummaryTables;
    List<TaskFrameSettingDto> taskFrameSettings;
}
