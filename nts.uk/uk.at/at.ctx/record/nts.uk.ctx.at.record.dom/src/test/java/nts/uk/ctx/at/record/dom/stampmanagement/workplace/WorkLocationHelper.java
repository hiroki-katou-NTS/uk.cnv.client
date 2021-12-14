package nts.uk.ctx.at.record.dom.stampmanagement.workplace;

import java.util.ArrayList;
import java.util.Optional;

import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;

public class WorkLocationHelper {
	
	public static WorkLocation getDefault() {
		
		return new WorkLocation(new ContractCode("ContractCode"), new WorkLocationCD("WorkLocationCD"),
				new WorkLocationName("WorkLocationName"), new StampMobilePossibleRange(RadiusAtr.M_100, new GeoCoordinate(100, 200)),
				new ArrayList<>(), Optional.empty());
		
	}

}
