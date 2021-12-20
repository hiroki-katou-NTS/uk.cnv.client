package nts.uk.file.at.infra.vacation.set.annualpaidleave;

import nts.arc.i18n.I18NText;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCount;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCountRepository;
import nts.uk.file.at.app.export.vacation.set.EmployeeSystemImpl;
import nts.uk.file.at.app.export.vacation.set.annualpaidleave.AnnPaidLeaveRepository;
import nts.uk.file.at.infra.vacation.set.CommonTempHolidays;
import nts.uk.file.at.infra.vacation.set.DataEachBox;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.sql.PreparedStatement;
import java.util.*;

@Stateless
public class JpaAnnPaidLeaveRepository extends JpaRepository implements AnnPaidLeaveRepository {

	private static final int LEAVE_TYPE = 1;
	private static final String GET_PAID_LEA_EXPORT = "SELECT  pl.MANAGE_ATR, " +
			" mas.RETENTION_YEAR, " +
			" mas.SCHEDULD_WORKING_DAYS, " +
			" mas.HALF_MANAGE_ATR, " +
			" mas.HALF_MAX_REFERENCE, " +
			" mas.HALF_MAX_UNIFORM_COMP, " +
			" mas.HALF_ROUND_PROC, " +
			" pl.PRIORITY_TYPE, " +
			" tas.TIME_MANAGE_ATR, " +
			" tas.TIME_UNIT, " +
			" tas.TIME_MAX_DAY_MANAGE_ATR, " +
			" tas.TIME_MAX_DAY_REFERENCE, " +
			" tas.TIME_MAX_DAY_UNIF_COMP, " +
			" tas.ROUND_PROC_CLA AS ROUND_PROC_CLA_TAS, " +
			" tas.TIME_OF_DAY_REFERENCE, " +
			" tas.UNIFORM_TIME, " +
			" tas.CONTRACT_TIME_ROUND " +
			"FROM (SELECT MANAGE_ATR,PRIORITY_TYPE,CID FROM KSHMT_HDPAID_SET WHERE KSHMT_HDPAID_SET.CID = ? )  as pl " +
			"INNER JOIN KSHMT_HDPAID_SET_MNG mas on pl.CID = mas.CID " +
			"INNER JOIN KSHMT_HDPAID_TIME_SET tas on mas.CID = tas.CID ";
	private static final int NOT_MANAGER = 0;
	
	@Inject
	private WorkDaysNumberOnLeaveCountRepository workDaysNumberOnLeaveCountRepository;

	@Override
	public List<MasterData> getAnPaidLea(String cid) {

		List<MasterData> datas = new ArrayList<>();
		try (PreparedStatement stmt = this.connection().prepareStatement(GET_PAID_LEA_EXPORT)) {
			stmt.setString(1, cid);
			NtsResultSet result = new NtsResultSet(stmt.executeQuery());
			result.forEach(i -> {
				datas.addAll(buildMasterListData(i));
			});

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if(datas.isEmpty()){
			return buildMasterListData();
		}
		return datas;
	}

	private List<MasterData> buildMasterListData(NtsResultSet.NtsResultRecord rs) {
		List<MasterData> datas = new ArrayList<>();
		WorkDaysNumberOnLeaveCount leave = this.workDaysNumberOnLeaveCountRepository
				.findByCid(AppContexts.user().companyId());
		String isCounting = leave.getCountedLeaveList().stream()
				.map(data -> data.value).filter(data -> data == LEAVE_TYPE).findFirst().isPresent()
				? "管理する" : "管理しない";
		if (rs == null) {
			return buildMasterListData();
		}
		/*※2*/
		boolean isManager = rs.getString("MANAGE_ATR").equals("1");
		/*※3*/
		boolean isTimeManager = rs.getString("TIME_MANAGE_ATR").equals("1");
		/*※18*/
		boolean isHalfRefer = rs.getString("HALF_MAX_REFERENCE").equals("0");
		/*※19*/
		boolean isTimeMaxDay = rs.getString("TIME_MAX_DAY_REFERENCE").equals("0");
		/*※20*/
		boolean isReference = rs.getString("TIME_OF_DAY_REFERENCE").equals("0");
		if (isManager) {
			// Row 1
			datas.add(buildARow(new DataEachBox(I18NText.getText("KMF001_176"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(CommonTempHolidays.getTextEnumManageDistinct(Integer.valueOf(rs.getString("MANAGE_ATR"))), ColumnTextAlign.LEFT)));
			// Row 2
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_177"), ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_178"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(isCounting, ColumnTextAlign.LEFT)));
//			// Row 3
//			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
//					, new DataEachBox(null, ColumnTextAlign.LEFT)
//					, new DataEachBox(I18NText.getText("KMF001_179"), ColumnTextAlign.LEFT)
//					, new DataEachBox(I18NText.getText("KMF001_180"), ColumnTextAlign.LEFT)
//					, new DataEachBox(rs.getString("HALF_MAX_GRANT_DAY") + I18NText.getText("KMF001_197"), ColumnTextAlign.RIGHT)));
//			// Row 4
//			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
//					, new DataEachBox(null, ColumnTextAlign.LEFT)
//					, new DataEachBox(null, ColumnTextAlign.LEFT)
//					, new DataEachBox(I18NText.getText("KMF001_181"), ColumnTextAlign.LEFT)
//					, new DataEachBox(rs.getString("REMAINING_MAX_DAY") + I18NText.getText("KMF001_197"), ColumnTextAlign.RIGHT)));
			// Row 5 A9_5
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_182"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(rs.getString("RETENTION_YEAR")+ I18NText.getText("KMF001_198"), ColumnTextAlign.RIGHT)));
			// Row 6 A9_6
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_183"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(rs.getString("SCHEDULD_WORKING_DAYS")+ I18NText.getText("KMF001_197"), ColumnTextAlign.RIGHT)));
			// Row 7 A9_7
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_184"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(CommonTempHolidays.getTextEnumManageDistinct(Integer.valueOf(rs.getString("HALF_MANAGE_ATR"))), ColumnTextAlign.LEFT)));
			// Row 8 A9_8
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_185"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(CommonTempHolidays.getEnumMaxDayReference(Integer.valueOf(rs.getString("HALF_MAX_REFERENCE"))), ColumnTextAlign.LEFT)));
			// Row 9 A9_9
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_186"), ColumnTextAlign.LEFT)
					, new DataEachBox(isHalfRefer ? rs.getString("HALF_MAX_UNIFORM_COMP")  + I18NText.getText("KMF001_199") : null, ColumnTextAlign.RIGHT)));
			// A9_10
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_187"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(CommonTempHolidays.getEnumRoundProcessingClassification(Integer.valueOf(rs.getString("HALF_ROUND_PROC"))), ColumnTextAlign.LEFT)));
			// A9_11
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_188"), ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_189"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(CommonTempHolidays.getEnumAnnualPriority(Integer.valueOf(rs.getString("PRIORITY_TYPE"))), ColumnTextAlign.LEFT)));
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_190"), ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_191"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(CommonTempHolidays.getTextEnumManageDistinct(Integer.valueOf(rs.getString("TIME_MANAGE_ATR"))), ColumnTextAlign.LEFT)));
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_192"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager ? CommonTempHolidays.getTextEnumTimeDigestiveUnit(Integer.valueOf(rs.getString("TIME_UNIT"))) : null, ColumnTextAlign.RIGHT)));
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_193"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager ? CommonTempHolidays.getTextEnumManageDistinct(Integer.valueOf(rs.getString("TIME_MAX_DAY_MANAGE_ATR"))) : null, ColumnTextAlign.LEFT)));
			//ver2.2
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_338"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager ?CommonTempHolidays.getTextEnumDayTimeAnnualLeave(Integer.valueOf(rs.getString("TIME_OF_DAY_REFERENCE"))):null, ColumnTextAlign.LEFT)));
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_341"), ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager && isReference ? convertTotime(rs.getString("UNIFORM_TIME")):null, ColumnTextAlign.RIGHT)));
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_342"), ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager && !isReference ?CommonTempHolidays.getTextEnumContractTimeRound(Integer.valueOf(rs.getString("CONTRACT_TIME_ROUND"))):null, ColumnTextAlign.LEFT)));
			
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_194"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager ? CommonTempHolidays.getEnumMaxDayReference(Integer.valueOf(rs.getString("TIME_MAX_DAY_REFERENCE"))) : null, ColumnTextAlign.LEFT)));
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_195"), ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager && isTimeMaxDay ? rs.getString("TIME_MAX_DAY_UNIF_COMP")  + I18NText.getText("KMF001_197") : null, ColumnTextAlign.RIGHT)));
			datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(I18NText.getText("KMF001_196"), ColumnTextAlign.LEFT)
					, new DataEachBox(null, ColumnTextAlign.LEFT)
					, new DataEachBox(isTimeManager && isTimeMaxDay ? CommonTempHolidays.getEnumTimeAnnualRoundProcesCla(Integer.valueOf(rs.getString("ROUND_PROC_CLA_TAS"))) : null, ColumnTextAlign.LEFT)));
		} else {

			datas = buildMasterListData();
		}

		return datas;
	}

	private List<MasterData> buildMasterListData() {
		List<MasterData> datas = new ArrayList<>();

		// Row 1
		datas.add(buildARow(new DataEachBox(I18NText.getText("KMF001_176"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(CommonTempHolidays.getTextEnumManageDistinct(NOT_MANAGER), ColumnTextAlign.LEFT)));
		// Row 2
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_177"), ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_178"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		// Row 5
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_182"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.RIGHT)));
		// Row 6
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_183"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.RIGHT)));
		// Row 7
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_184"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.RIGHT)));
		// Row 8
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_185"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		// Row 9
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_186"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.RIGHT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_187"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_188"), ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_189"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_190"), ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_191"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_192"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.RIGHT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_193"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		//ver2.2
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_338"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_341"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.RIGHT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_342"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_194"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_195"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.RIGHT)));
		datas.add(buildARow(new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(I18NText.getText("KMF001_196"), ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)
				, new DataEachBox(null, ColumnTextAlign.LEFT)));


		return datas;
	}

	private MasterData buildARow(DataEachBox value1, DataEachBox value2, DataEachBox value3, DataEachBox value4, DataEachBox value5) {
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(EmployeeSystemImpl.KMF001_166, MasterCellData.builder()
				.columnId(EmployeeSystemImpl.KMF001_166)
				.value(value1.getValue())
				.style(MasterCellStyle.build().horizontalAlign(value1.getPositon()))
				.build());
		data.put(EmployeeSystemImpl.KMF001_2, MasterCellData.builder()
				.columnId(EmployeeSystemImpl.KMF001_2)
				.value(value2.getValue())
				.style(MasterCellStyle.build().horizontalAlign(value2.getPositon()))
				.build());
		data.put(EmployeeSystemImpl.KMF001_3, MasterCellData.builder()
				.columnId(EmployeeSystemImpl.KMF001_3)
				.value(value3.getValue())
				.style(MasterCellStyle.build().horizontalAlign(value3.getPositon()))
				.build());
		data.put(EmployeeSystemImpl.KMF001_4, MasterCellData.builder()
				.columnId(EmployeeSystemImpl.KMF001_4)
				.value(value4.getValue())
				.style(MasterCellStyle.build().horizontalAlign(value4.getPositon()))
				.build());
		data.put(EmployeeSystemImpl.KMF001_167, MasterCellData.builder()
				.columnId(EmployeeSystemImpl.KMF001_167)
				.value(value5.getValue())
				.style(MasterCellStyle.build().horizontalAlign(value5.getPositon()))
				.build());
		return MasterData.builder().rowData(data).build();
	}
	
	private String convertTotime(String value){
        return (Integer.parseInt(value)/60) +":"+(Integer.parseInt(value)%60 < 10? "0" + Integer.parseInt(value)%60 : Integer.parseInt(value)%60 );
    }


}
