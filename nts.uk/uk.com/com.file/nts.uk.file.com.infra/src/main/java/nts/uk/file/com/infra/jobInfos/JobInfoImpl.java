package nts.uk.file.com.infra.jobInfos;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.uk.file.com.app.jobInfos.JobInfoColumn;
import nts.uk.file.com.app.jobInfos.JobInfoRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

@Stateless
public class JobInfoImpl extends JpaRepository implements JobInfoRepository {
	
	private static final String GET_EXPORT_EXCEL = 
			"SELECT"
			+	" CASE"
			+		" WHEN TBL.ROW_NUMBER = 1 THEN TBL.APPLY_CONCURRENT_PERSON"
			+		" ELSE NULL"
			+	" END APPLY_CONCURRENT_PERSON,"
			+	" TBL.JOB_CD_DIS, TBL.JOB_NAME, TBL.ROLESET_NAME"
			+	" FROM"
			+		" (SELECT job.APPLY_CONCURRENT_PERSON, "
			+ " IIF(his.END_DATE = CONVERT(DATETIME, '9999-12-31 00:00:00', 120), info.JOB_CD, '') AS JOB_CD_DIS, "
			+ " IIF(his.END_DATE = CONVERT(DATETIME, '9999-12-31 00:00:00', 120), info.JOB_NAME, 'マスタ未登録') AS JOB_NAME, "
			+ " d.ROLESET_CD +' '+ s.ROLE_SET_NAME AS ROLESET_NAME,"
			+			" ROW_NUMBER() OVER (ORDER BY CASE WHEN his.END_DATE <> CONVERT(DATETIME, '9999-12-31 00:00:00', 120) THEN 1 ELSE 0 END ASC, info.JOB_CD ) AS ROW_NUMBER"
			+			" FROM BSYMT_JOB_INFO info"
			+ " 		  INNER JOIN (SELECT *, ROW_NUMBER() OVER ( PARTITION BY CID, JOB_ID ORDER BY END_DATE DESC ) AS RN FROM BSYMT_JOB_HIST) his " 
			+ "				ON info.JOB_ID = his.JOB_ID AND info.HIST_ID = his.HIST_ID AND info.CID = his.CID AND his.RN = 1 "		
			+			" INNER JOIN SACMT_ROLESET s ON info.CID = s.CID "
			+			" INNER JOIN SACMT_ROLESET_JOB d ON s.ROLE_SET_CD = d.ROLESET_CD AND info.JOB_ID = d.JOB_ID AND s.CID = d.CID "
			+			" INNER JOIN SACMT_ROLESET_JOB job ON info.CID = job.CID"
			+	" WHERE info.CID = ? ) TBL ";
	
	
	private static final String GET_EXPORT_DATA = 
			"SELECT"
			+	" CASE"
			+		" WHEN TBL.ROW_NUMBER = 1 THEN TBL.ROLESET_CD"
			+		" ELSE NULL"
			+	" END ROLESET_CD,"
			+	" CASE"
			+		" WHEN TBL.ROW_NUMBER = 1 THEN TBL.ROLE_SET_NAME"
			+		" ELSE NULL"
			+	" END ROLE_SET_NAME,"
			+	" TBL.SCD, TBL.BUSINESS_NAME, TBL.START_DATE, TBL.END_DATE"
			+ " FROM"
			+ 		" (SELECT per.ROLESET_CD, rs.ROLE_SET_NAME, em.SCD, "
			+ 				" IIF(p.BUSINESS_NAME IS NOT NULL, p.BUSINESS_NAME, us.USER_NAME) AS BUSINESS_NAME, "
			+ 				" per.START_DATE, per.END_DATE, "
			+				" ROW_NUMBER() OVER (PARTITION BY per.ROLESET_CD "
			+				" ORDER BY  rs.ROLE_SET_CD ASC, em.SCD ASC) AS ROW_NUMBER "
			+			" FROM SACMT_ROLESET_PERSON per"
			+				" INNER JOIN SACMT_ROLESET rs ON rs.CID = per.CID AND rs.ROLE_SET_CD = per.ROLESET_CD" 
			+				" INNER JOIN BSYMT_SYAIN em ON per.SID = em.SID"
			+		 		" INNER JOIN BSYMT_AFF_COM_HIST aff ON per.SID = aff.SID" 
			+				" INNER JOIN BPSMT_PERSON p ON aff.PID = p.PID "
			+				" INNER JOIN SACMT_USER us ON p.PID = us.ASSO_PID "
			+	" WHERE per.CID = ?"
			+		 " AND per.START_DATE <= CONVERT(DATETIME, ?, 102) "
		    +		 " AND per.END_DATE >= CONVERT(DATETIME, ?, 102) ) TBL";
	
	@Override
	public List<MasterData> getDataRoleSetPosExport() {
		String cid = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		try (PreparedStatement stmt = this.connection().prepareStatement(GET_EXPORT_EXCEL.toString())){
			stmt.setString(1, cid);
			datas.addAll(new NtsResultSet(stmt.executeQuery()).getList(i->buildMasterListData(i)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return datas;
	}
	
	private MasterData buildMasterListData(NtsResultRecord r){
		Map<String,MasterCellData> data = new HashMap<>();
			data.put(JobInfoColumn.CAS014_41, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_41)
                .value(r.getInt("APPLY_CONCURRENT_PERSON") != null ? r.getInt("APPLY_CONCURRENT_PERSON") == 1 ? "○" : "-" : null)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
            data.put(JobInfoColumn.CAS014_42, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_42)
                .value(r.getString("JOB_CD_DIS"))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
            data.put(JobInfoColumn.CAS014_43, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_43)
                .value(r.getString("JOB_NAME"))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
            data.put(JobInfoColumn.CAS014_44, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_44)
                .value(r.getString("ROLESET_NAME"))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
		return MasterData.builder().rowData(data).build();
	}
	
	@Override
	public List<MasterData> getDataRoleSetEmpExport(String date){
		String cid = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		try (PreparedStatement stmt = this.connection().prepareStatement(GET_EXPORT_DATA.toString())){
			stmt.setString(1, cid);
			stmt.setString(2, date);
			stmt.setString(3, date);
			datas.addAll(new NtsResultSet(stmt.executeQuery()).getList(i->getDataRoleSetEmpExport(i)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return datas;
	}
	
	private MasterData getDataRoleSetEmpExport(NtsResultRecord r){
		Map<String,MasterCellData> data = new HashMap<>();
			data.put(JobInfoColumn.CAS014_45, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_45)
                .value(r.getString("ROLESET_CD"))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
            data.put(JobInfoColumn.CAS014_46, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_46)
                .value(r.getString("ROLE_SET_NAME"))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
            data.put(JobInfoColumn.CAS014_47, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_47)
                .value(r.getString("SCD"))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
            data.put(JobInfoColumn.CAS014_48, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_48)
                .value(r.getString("BUSINESS_NAME"))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
            data.put(JobInfoColumn.CAS014_49, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_49)
                .value(GeneralDate.localDate(((Date) (r.getDate("START_DATE"))).toLocalDate()))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
            data.put(JobInfoColumn.CAS014_50, MasterCellData.builder()
                .columnId(JobInfoColumn.CAS014_50)
                .value(GeneralDate.localDate(((Date) (r.getDate("END_DATE"))).toLocalDate()))
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
		return MasterData.builder().rowData(data).build();
	}
}
