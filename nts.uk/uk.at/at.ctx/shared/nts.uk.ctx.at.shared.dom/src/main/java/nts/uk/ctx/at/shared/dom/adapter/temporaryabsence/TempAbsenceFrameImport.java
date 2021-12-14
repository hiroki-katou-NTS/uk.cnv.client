package nts.uk.ctx.at.shared.dom.adapter.temporaryabsence;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TempAbsenceFrameImport {

	/** The company id. */
	// 会社ID
	private String companyId;
	
	/** The temp absence fr no. */
	//休職休業枠NO (min = "1", max = "10")
	private int tempAbsenceFrNo;
	
	/** The use classification. */
	//使用区分 (USE(1) - NOT_USE(0))
	private int useClassification;
	
	/** The temp absence fr name. */
	//休職休業枠名称
	private String tempAbsenceFrName;
}
