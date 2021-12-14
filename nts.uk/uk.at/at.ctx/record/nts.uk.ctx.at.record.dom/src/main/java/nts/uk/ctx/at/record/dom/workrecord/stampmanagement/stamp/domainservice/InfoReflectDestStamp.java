package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

/**
 * @author thanh_nx
 *
 *         打刻の反映先情報
 */
@AllArgsConstructor
@Getter
public class InfoReflectDestStamp {
	
	//年月日
	private GeneralDate date;
	
	//社員ID
	private String sid;
	
	//会社ID
    private String cid;
}
