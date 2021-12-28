package nts.uk.file.at.infra.export.attendanceitemprepare;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.Query;

import lombok.SneakyThrows;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.FormatPerformance;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFun;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDayFuncControl;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtFormatPerformance;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtMonPerformanceFun;
import nts.uk.file.at.app.export.attendanceitemprepare.ApplicationCallExport;
import nts.uk.file.at.app.export.attendanceitemprepare.ApplicationTypeExport;
import nts.uk.file.at.app.export.attendanceitemprepare.OperationExcelRepo;
import nts.uk.file.at.app.export.attendanceitemprepare.RoleExport;
/**
 * 
 * @author Hoidd 2
 *
 */
@Stateless
public class JpaOperationRepository extends JpaRepository implements OperationExcelRepo
{
	int test = 0 ;
	//DayFuncControl
	private static final String SELECT_ALL_QUERY_STRING_DAY_FUNC = "SELECT f FROM KrcmtDayFuncControl f";
    private static final String SELECT_BY_KEY_STRING_DAY_FUNC = SELECT_ALL_QUERY_STRING_DAY_FUNC + " WHERE  f.dayFuncControlPk.cid =:cid ";
	
//    private static final String SELECT_ALL_QUERY_STRING_DAILY = "SELECT f FROM KrcmtDaiPerformEdFun f";
//    private static final String SELECT_BY_KEY_STRING_DAILY = SELECT_ALL_QUERY_STRING_DAILY + " WHERE  f.daiPerformanceFunPk.cid =:cid ";

    //Monthly
    private static final String SELECT_ALL_QUERY_STRING_MONTHLY = "SELECT a FROM KrcmtMonPerformanceFun a";
    private static final String SELECT_BY_KEY_STRING_MONTHLY = SELECT_ALL_QUERY_STRING_MONTHLY + " WHERE  a.monPerformanceFunPk.cid =:cid ";
    //format
    private static final String SELECT_ALL_QUERY_STRING_FORMAT = "SELECT b FROM KrcmtFormatPerformance b";
    private static final String SELECT_BY_KEY_STRING_FORMAT = SELECT_ALL_QUERY_STRING_FORMAT + " WHERE  b.formatPerformancePk.cid =:cid ";
    //IDENTITY
//    private static final String SELECT_ALL_QUERY_STRING_IDENTITY= "SELECT c FROM KrcmtIdentityProcess c";
//    private static final String SELECT_BY_KEY_STRING_IDENTITY = SELECT_ALL_QUERY_STRING_IDENTITY + " WHERE  c.identityProcessPk.cid =:cid ";
    //APPLICATIONCALL
    private static final String SELECT_BY_COM_APPLICATIONCALL = "SELECT c.APPLICATION_TYPE FROM KFNMT_DAY_APP_CALL c where c.CID=?cid";
	//Rold
    private static final String SELECT_ALL_ROLD = "SELECT b.ROLE_CD as codeRole,b.ROLE_NAME as nameRole,c.DESCRIPTION_OF_FUNCTION "
    		+ "as description ,c.DISPLAY_NAME_OF_FUNCTION as displayName, a.AVAILABILITY "
    		+ ",a.ROLE_ID "
    		+ "as availability from KRCMT_ATTENDANCE_AUT a left join SACMT_ROLE  b "
    		+ "on a.ROLE_ID=b.ROLE_ID and b.CID = a.CID left join KRCCT_ATTENDANCE_FUN c "
    		+ "on a.FUNCTION_NO=c.FUNCTION_NO where a.CID=?cid  ORDER BY b.ROLE_CD ";
	
    @Override
    public Optional<FormatPerformance> getFormatPerformanceById(String companyId){
        return this.queryProxy().query(SELECT_BY_KEY_STRING_FORMAT, KrcmtFormatPerformance.class)
        .setParameter("cid", companyId)
        .getSingle(c->c.toDomain());
    }
  
    @Override
    public Optional<DaiPerformanceFun> getDaiPerformanceFunById(String companyId){
        return this.queryProxy().query(SELECT_BY_KEY_STRING_DAY_FUNC, KrcmtDayFuncControl.class)
        .setParameter("cid", companyId)
        .getSingle(c->c.toDomainDaiPerformanceFun());
    }


    @Override
    public Optional<MonPerformanceFun> getMonPerformanceFunById(String companyId){
        return this.queryProxy().query(SELECT_BY_KEY_STRING_MONTHLY, KrcmtMonPerformanceFun.class)
        .setParameter("cid", companyId)
        .getSingle(c->c.toDomain());
    }
    
    @Override
    @SneakyThrows
    public Optional<IdentityProcess> getIdentityProcessById(String companyId){
    	return this.queryProxy().query(SELECT_BY_KEY_STRING_DAY_FUNC, KrcmtDayFuncControl.class)
    	        .setParameter("cid", companyId)
    	        .getSingle(c->c.toDomainIdentityProcess());
    }
    @Override
    @SneakyThrows
    public Optional<ApprovalProcess> getApprovalProcessById(String companyId){
    	return this.queryProxy().query(SELECT_BY_KEY_STRING_DAY_FUNC, KrcmtDayFuncControl.class)
    	        .setParameter("cid", companyId)
    	        .getSingle(c->c.toDomainApprovalProcess());
    }
	
	@Override
	public List<ApplicationCallExport> findByCom(String companyId) {
		List<?> data = this.getEntityManager().createNativeQuery(SELECT_BY_COM_APPLICATIONCALL)
				.setParameter("cid", companyId).getResultList();
		List<ApplicationCallExport> result = new ArrayList<>();
		data.stream().forEach(x -> {
			if(x !=null){
				int	valueEnum = ((BigDecimal) x).intValue();
				result.add(new ApplicationCallExport(companyId,EnumAdaptor.valueOf(valueEnum, ApplicationTypeExport.class)));
			}
		});
		
		return result;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<RoleExport> findRole(String companyId) {
		List<Object[]> rawResultList;
		Query query = this.getEntityManager().createNativeQuery(SELECT_ALL_ROLD)
				.setParameter("cid", companyId);
		rawResultList = query.getResultList();
		List<RoleExport> result = new ArrayList<>();
		
		rawResultList.stream().forEach(x -> {
			convertToRoleExport(result, (Object[])x);
		});
		return result;
	}

	private void convertToRoleExport(List<RoleExport> result, Object[] x) {
		 String codeRole = (x[0]!=null)?(String) x[0]:null;
		 String nameRole = (x[1]!=null)?(String) x[1]:null;
		 String displayName = (x[2]!=null)?(String) x[2]:null;
		 String description = (x[3]!=null)?(String) x[3]:null;
		 String roldeId = (String) x[5];
		 int availability = (x[4]!=null)?((BigDecimal) x[4]).intValue():-1;
		RoleExport reExport = 
				new RoleExport(codeRole, nameRole, displayName, description, availability,roldeId);
		result.add(reExport);

	}

	@Override
	public Optional<RestrictConfirmEmployment> getRestrictConfirmEmploymentByCompanyId(String companyId) {
		String sql = "select * from KRCMT_WORK_FIXED_CTR where CID = @companyId";
		return new NtsStatement(sql, this.jdbcProxy())
				.paramString("companyId", companyId)
				.getSingle(rec -> {
					return new RestrictConfirmEmployment(
							rec.getString("CID"), 
							rec.getInt("USAGE_ATR") == 0 ? false : true);
		});
	}
}
