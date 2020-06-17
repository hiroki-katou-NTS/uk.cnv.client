package nts.uk.ctx.at.record.app.find.kdp.kdp001.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SettingPotalStampInputDto {

	/**
	 * ・ポータル打刻の打刻設定
	 */
	private PortalStampSettingsDto portalStampSettings;

	private List<EmpInfoPotalStampDto> empInfos;
}
