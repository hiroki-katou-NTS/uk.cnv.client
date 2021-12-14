package nts.uk.screen.at.app.ksu003.start.dto;

import lombok.Value;
/**
 * 
 * @author phongtq
 *
 */
@Value
public class GetInfoInitStartKsu003Dto {
	
	/** スケジュール修正日付別の表示設定 */
	private DisplaySettingByDateDto byDateDto;
	
	/** 組織の表示情報 */
	private DisplayInfoOrganizationDto displayInforOrganization;
	
	/** 複数回勤務管理 */
	private WorkManageMultiDto manageMultiDto;
	
	/** スケジュール修正の機能制御 */
	private ScheFunctionControlDto functionControlDto;

}
