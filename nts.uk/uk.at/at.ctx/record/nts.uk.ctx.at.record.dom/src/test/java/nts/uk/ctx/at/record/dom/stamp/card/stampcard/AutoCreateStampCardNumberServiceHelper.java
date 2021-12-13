package nts.uk.ctx.at.record.dom.stamp.card.stampcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDateTime;
//import nts.uk.ctx.at.record.dom.adapter.company.AddInforImport;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.AddInforImport;
//import nts.uk.ctx.at.record.dom.adapter.company.CompanyImport622;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyImport622;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeDataMngInfoImport;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardDigitNumber;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditMethod;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditing;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;

public class AutoCreateStampCardNumberServiceHelper {

	public static List<EmployeeDataMngInfoImport> getListEmployeeDataMngInfoImport() {

		List<EmployeeDataMngInfoImport> lst = new ArrayList<EmployeeDataMngInfoImport>();

		lst.add(new EmployeeDataMngInfoImport("DUMMY", "DUMMY", "DUMMY", "DUMMY", 1, GeneralDateTime.max(), "DUMMY",
				"DUMMY"));

		lst.add(new EmployeeDataMngInfoImport("DUMMY", "DUMMY", "DUMMY", "DUMMY", 2, GeneralDateTime.max(), "DUMMY",
				"DUMMY"));

		return lst;
	}

	public static Optional<CompanyImport622> getCompanyImport622() {
		String dum = "DUMMY";
		AddInforImport info = new AddInforImport(dum, dum, dum, dum, dum, dum, dum, dum);
		CompanyImport622 import622 = new CompanyImport622(dum, dum, dum, dum, dum, dum, dum, dum, dum, 1, info, 1);
				
		return Optional.of(import622);
	}

	public static Optional<StampCardEditing> getStampCardEditing() {
		StampCardEditing cardEditing = new StampCardEditing("DUMMY", new StampCardDigitNumber(10),
				StampCardEditMethod.PreviousSpace);

		return Optional.of(cardEditing);
	}

	public static Optional<StampCardEditing> getStampCardEditing_Null() {
		StampCardEditing cardEditing = new StampCardEditing("DUMMY", new StampCardDigitNumber(3),
				StampCardEditMethod.PreviousSpace);

		return Optional.of(cardEditing);
	}

	public static Optional<Stamp> getStamp() {
		
		Stamp stamp = new Stamp(new ContractCode("DUMMY"),
				new StampNumber("DUMMY"),
				GeneralDateTime.now(),
				new Relieve(AuthcMethod.ID_AUTHC, StampMeans.INDIVITION),
				null,
				null, 
				null);

		return Optional.of(stamp);
	}
	
	public static StampCard getStampCard() {
		StampCard stampCard = new StampCard("DUMMY", "DUMMY", "DUMMY");

		return stampCard;
	}

}
