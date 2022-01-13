package nts.uk.file.at.infra.export.attendanceitemprepare;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.file.at.app.export.attendanceitemprepare.ControlOfAttMonthlyDtoExcel;
import nts.uk.file.at.app.export.attendanceitemprepare.ControlOfAttMonthlyRepoExcel;

@Stateless
public class JpaControlOfAttItemsMonthlyRepo extends JpaRepository implements ControlOfAttMonthlyRepoExcel {

	private static final String GET_ALL = "select a.ITEM_MONTHLY_ID, a.HEADER_BACKGROUND_COLOR "
			+ "from KSHMT_MON_ITEM_CONTROL a where a.CID=?companyId";

	@Override
	public Map<Integer,ControlOfAttMonthlyDtoExcel> getAllByCompanyId(String companyId) {
			List<?> data = this.getEntityManager().createNativeQuery(GET_ALL)
					.setParameter("companyId", companyId).getResultList();
			Map<Integer, ControlOfAttMonthlyDtoExcel> listControl = new HashMap<>();
			data.stream().forEach(x -> {
				putRowToResult(listControl, (Object[])x);
			});
			
			return listControl;
			
	}

	private void putRowToResult(Map<Integer, ControlOfAttMonthlyDtoExcel> listControl, Object[] x) {
		Integer id = 0;
//		Float unitItem = 0f;
		String headerBackground = (String) x[1];
		if(x[0] !=null){
			id = ((BigDecimal) x[0]).intValue();
		}
//		if(x[1] !=null){
//			unitItem =((BigDecimal) x[1]).floatValue();
//		}
		ControlOfAttMonthlyDtoExcel control = new ControlOfAttMonthlyDtoExcel(id, headerBackground);
		listControl.put(control.getItemMonthlyID(), control);
	}
//			
//	
//

	

}
