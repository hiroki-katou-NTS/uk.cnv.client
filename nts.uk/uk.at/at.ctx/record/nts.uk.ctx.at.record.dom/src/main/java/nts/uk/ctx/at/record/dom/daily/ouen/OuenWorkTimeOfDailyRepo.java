package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface OuenWorkTimeOfDailyRepo {

	public Optional<OuenWorkTimeOfDaily> find(String empId, GeneralDate ymd);
	
	public List<OuenWorkTimeOfDaily> find(List<String> employeeId, DatePeriod datePeriod);
	
	public void update(List<OuenWorkTimeOfDaily> domain);
	
	public void update(OuenWorkTimeOfDaily domain);
	
	public void insert(List<OuenWorkTimeOfDaily> domain);
	
	public void insert(OuenWorkTimeOfDaily domain);
	
	public void delete(List<OuenWorkTimeOfDaily> domain);
	
	public void remove(String sid, GeneralDate ymd);

	public void delete(OuenWorkTimeOfDaily domain);

	public void removePK(String employeeId, GeneralDate today, int i);
}
