package nts.uk.file.at.infra.export.attendanceitemprepare;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.file.at.app.export.attendanceitemprepare.ControlOfAttItemsRepoExcel;
import nts.uk.file.at.app.export.attendanceitemprepare.ControlOfAttendanceItemsDtoExcel;

@Stateless
public class JpaControlOfAttItemsRepo extends JpaRepository implements ControlOfAttItemsRepoExcel {

	private static final String GET_ALL = "select a.ITEM_DAILY_ID, a.TIME_INPUT_UNIT, a.HEADER_BACKGROUND_COLOR "
			+ "from KSHST_ATD_ITEM_CONTROL a where a.CID=?companyId";

	@Override
	public Map<Integer,ControlOfAttendanceItemsDtoExcel> getAllByCompanyId(String companyId) {
			List<?> data = this.getEntityManager().createNativeQuery(GET_ALL)
					.setParameter("companyId", companyId).getResultList();
			Map<Integer, ControlOfAttendanceItemsDtoExcel> listControl = new HashMap<>();
			data.stream().forEach(x -> {
				putRowToResult(listControl, (Object[])x);
			});
			
			return listControl;
			
	}

	private void putRowToResult(Map<Integer, ControlOfAttendanceItemsDtoExcel> listControl, Object[] x) {
		Integer id = 0;
		Integer unitItem = 0;
		String headerBackground = (String) x[2];
		if(x[0] !=null){
			id = ((BigDecimal) x[0]).intValue();
		}
		if(x[1] !=null){
			unitItem =((BigDecimal) x[1]).intValue();
		}
		ControlOfAttendanceItemsDtoExcel control = 
				new ControlOfAttendanceItemsDtoExcel(id, headerBackground,unitItem );
		listControl.put(control.getItemDailyID(), control);
	}

}