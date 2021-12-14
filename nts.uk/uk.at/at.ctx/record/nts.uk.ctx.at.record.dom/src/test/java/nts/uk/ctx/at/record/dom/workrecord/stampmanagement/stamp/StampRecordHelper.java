package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import java.util.ArrayList;
import java.util.List;

import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;
/**
 * 
 * @author tutk
 *
 */
public class StampRecordHelper {

	public static StampRecord getStampRecord() {
		return new StampRecord(
				new ContractCode("DUMMY"),
				new StampNumber("stampNumber"), 
				GeneralDateTime.now(), 
				new StampTypeDisplay(""));
	}
	
	public static List<StampRecord> getListStampRecord() {
		List<StampRecord> data = new ArrayList<>();
		data.add(getStampRecord());
		
		data.add(new StampRecord(
				new ContractCode("DUMMY"),
				new StampNumber("stampNumber"), 
				GeneralDateTime.now(), 
				new StampTypeDisplay("")));
		
		data.add(new StampRecord(
				new ContractCode("DUMMY"),
				new StampNumber("stampNumber"), 
				GeneralDateTime.now(),
				new StampTypeDisplay("")));
		
		return data;
	}
	public static StampRecord getStampSetStampArtAndRevervationAtr(boolean stampArt,ReservationArt revervationAtr) {
		return new StampRecord(
				new ContractCode("DUMMY"),
				new StampNumber("stampNumber"), 
				GeneralDateTime.now(),
				new StampTypeDisplay(""));
	}
}
