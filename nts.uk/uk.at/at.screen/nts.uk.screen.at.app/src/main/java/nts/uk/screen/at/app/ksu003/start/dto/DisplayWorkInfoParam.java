package nts.uk.screen.at.app.ksu003.start.dto;

import java.util.List;

import lombok.Value;

@Value
public class DisplayWorkInfoParam {
	/** 社員リスト：List<社員ID> */
	private List<String> lstEmpId;
	
	/** ・抽出日：年月日 */
	private String date;
	
	/** 表示形式 */
	private int selectedDisplayPeriod;
}
