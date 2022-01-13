package nts.uk.file.at.infra.export.attendanceitemprepare;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.file.at.app.export.attendanceitemprepare.WorkTypeDtoExcel;
import nts.uk.file.at.app.export.attendanceitemprepare.WorkTypeGroupExcel;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaWorkTypeGroupExcelRepo extends JpaRepository implements WorkTypeGroupExcel{

	private static final String SELECT_ALL_WORKTYPE_EXCEL = "SELECT a.EMP_CD, b.NAME, a.WORKTYPE_GROUP_NO, d.WORKTYPE_GROUP_NAME, c.NAME, a.WORKTYPE_CD "
			+ "from KRCMT_CHANGEABLE_WKTP_GRP d "
			+ "join KRCMT_CHANGEABLE_WKTP_GRP_DETAIL a on d.CID = a.CID and d.EMP_CD = a.EMP_CD and d.WORKTYPE_GROUP_NO = a.WORKTYPE_GROUP_NO "
			+ "left join BSYMT_EMPLOYMENT b on b.CODE = a.EMP_CD and a.CID=b.CID "
			+ "left join KSHMT_WKTP c on a.WORKTYPE_CD = c.CD and b.CID=c.CID "
			+ "where a.CID=?companyId ";
	private static final String SELECT_ALL_DEFAULT_WORKTPYE = "select a.WORKTYPE_GROUP_NO, d.WORKTYPE_GROUP_NAME, b.NAME,b.CD "
			+ "from KRCMT_CHANGEABLE_WKTP_GRP d "
			+ "join KRCMT_CHANGEABLE_WKTP_GRP_DETAIL a on d.CID = a.CID and d.EMP_CD = a.EMP_CD and d.WORKTYPE_GROUP_NO = a.WORKTYPE_GROUP_NO "
			+ "left join KSHMT_WKTP b on a.WORKTYPE_CD = b.CD and b.CID=?companyId "
			+ "where a.CID='000000000000-0000' and a.EMP_CD='0'  ";
	
	@Override
	public Map<String, Map<Integer, List<WorkTypeDtoExcel>>> getAllWorkType() {
		//get data default 
		List<?> dataDefault = this.getEntityManager().createNativeQuery(SELECT_ALL_DEFAULT_WORKTPYE)
				.setParameter("companyId", AppContexts.user().companyId()).getResultList();
		List<WorkTypeDtoExcel> resultDefault = new ArrayList<>();
		dataDefault.stream().forEach(x -> {
			putRowToResultDefault(resultDefault, (Object[])x);
		});
		//get all data
		List<?> data = this.getEntityManager().createNativeQuery(SELECT_ALL_WORKTYPE_EXCEL)
				.setParameter("companyId", AppContexts.user().companyId()).getResultList();
		List<WorkTypeDtoExcel> result = new ArrayList<>();
		data.stream().forEach(x -> {
			putRowToResult(result, (Object[])x,resultDefault);
		});
		
		Map<String, Map<Integer, List<WorkTypeDtoExcel>>> map = result.stream()
			    .collect(Collectors.groupingBy(WorkTypeDtoExcel::getCodeEmp,
			        Collectors.groupingBy(WorkTypeDtoExcel::getGroupNo)));
		return map;
	}
	private void putRowToResultDefault(List<WorkTypeDtoExcel> resultDefault, Object[] x) {
		String codeEmp = null;
		String nameEmp = null;
		int groupNo = -1;
			if(x[0] !=null){
				groupNo = ((BigDecimal) x[0]).intValue();
			}
		String groupName = (String)x[1];
		String workTypeName = (String)x[2];
		String workTypeCode = (String)x[3];
		WorkTypeDtoExcel workTypeDtoExcel = new WorkTypeDtoExcel(codeEmp, nameEmp, groupNo, groupName, workTypeCode, workTypeName);
		resultDefault.add(workTypeDtoExcel);
		
	}
	private void putRowToResult(List<WorkTypeDtoExcel> result, Object[] x, List<WorkTypeDtoExcel> resultDefault) {
		String codeEmp = (String)x[0];
		String nameEmp = (String)x[1];
		int groupNo = -1;
			if(x[2] !=null){
				groupNo = ((BigDecimal) x[2]).intValue();
			}
		String groupName = (String)x[3];
		String workTypeName = (String)x[4];
		String workTypeCode = (String)x[5];
		if(x[2]==null && x[5]==null){
			for (WorkTypeDtoExcel workType : resultDefault) {
				WorkTypeDtoExcel a = new WorkTypeDtoExcel(codeEmp,nameEmp,workType.getGroupNo(),workType.getGroupName(),workType.getWorkTypeCode(),workType.getWorkTypeName());
				result.add(a);
			}
			
		}else {
			WorkTypeDtoExcel workTypeDtoExcel = new WorkTypeDtoExcel(codeEmp, nameEmp, groupNo, groupName,workTypeCode , workTypeName );
			result.add(workTypeDtoExcel);
		}
		
	}

}
