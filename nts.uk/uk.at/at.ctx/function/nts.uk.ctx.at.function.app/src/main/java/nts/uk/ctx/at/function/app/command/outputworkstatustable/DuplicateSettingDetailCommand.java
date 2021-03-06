package nts.uk.ctx.at.function.app.command.outputworkstatustable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.WorkStatusOutputSettings;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.enums.SettingClassificationCommon;

@AllArgsConstructor
@Setter
@Getter
public class DuplicateSettingDetailCommand {
   private int settingCategory;
   private String settingId;
   private String settingCode;
   private String settingName;
}
