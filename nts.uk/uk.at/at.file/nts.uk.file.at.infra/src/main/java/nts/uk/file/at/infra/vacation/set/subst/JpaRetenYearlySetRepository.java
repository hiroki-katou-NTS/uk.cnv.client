package nts.uk.file.at.infra.vacation.set.subst;

import nts.arc.i18n.I18NText;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.uk.file.at.app.export.vacation.set.EmployeeSystemImpl;
import nts.uk.file.at.app.export.vacation.set.subst.RetenYearlySetRepository;
import nts.uk.file.at.infra.vacation.set.CommonTempHolidays;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

import javax.ejb.Stateless;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class JpaRetenYearlySetRepository extends JpaRepository implements RetenYearlySetRepository {

    private static final String GET_RENTEN_YEARLY_SETTING =
            "SELECT PL.MANAGE_ATR," +
                    "RY.MANAGEMENT_YEARLY_ATR," +
                    "RY.NUMBER_OF_YEAR," +
                    "RY.MAX_NUMBER_OF_DAYS," +
                    "RY.LEAVE_AS_WORK_DAYS "
                    + "FROM KMFMT_RETENTION_YEARLY RY,KALMT_ANNUAL_PAID_LEAVE PL "
                    + "WHERE RY.CID = ? AND PL.CID = RY.CID";
    private static final String NOT_MANAGER ="0";



    @Override
    public List<MasterData> getAllRetenYearlySet(String cid) {
        List<MasterData> datas = new ArrayList<>();
        String sql = String.format(GET_RENTEN_YEARLY_SETTING);
        try(PreparedStatement stmt = this.connection().prepareStatement(sql)) {
            stmt.setString(
                    1, cid);
                datas = new NtsResultSet(stmt.executeQuery())
                        .getList(x -> {
                            if (x.getString("MANAGE_ATR").equals(NOT_MANAGER)) {
                                return buildARow();
                            }
                            return buildARow(
                                    CommonTempHolidays.getTextEnumManageDistinct(Integer.valueOf(x.getString("MANAGEMENT_YEARLY_ATR")))
                                    , x.getString("NUMBER_OF_YEAR") + I18NText.getText("KMF001_198")
                                    , x.getString("MAX_NUMBER_OF_DAYS") + I18NText.getText("KMF001_197")
                                    , CommonTempHolidays.getTextEnumManageDistinct(Integer.valueOf(x.getString("LEAVE_AS_WORK_DAYS")))
                            );
                        });

        }catch(Exception e) {
            throw new RuntimeException(e);
        }
        return datas;
    }

    private MasterData buildARow(String value1, String value2, String value3, String value4) {
        Map<String, MasterCellData> data = new HashMap<>();

        data.put(EmployeeSystemImpl.KMF001_200, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_200)
                .value(value1)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        data.put(EmployeeSystemImpl.KMF001_201, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_201)
                .value(value2)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
        data.put(EmployeeSystemImpl.KMF001_202, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_202)
                .value(value3)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
        data.put(EmployeeSystemImpl.KMF001_203, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_203)
                .value(value4)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());

        return MasterData.builder().rowData(data).build();
    }

    private MasterData buildARow() {
        Map<String, MasterCellData> data = new HashMap<>();

        data.put(EmployeeSystemImpl.KMF001_200, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_200)
                .value(null)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        data.put(EmployeeSystemImpl.KMF001_201, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_201)
                .value(null)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
        data.put(EmployeeSystemImpl.KMF001_202, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_202)
                .value(null)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
        data.put(EmployeeSystemImpl.KMF001_203, MasterCellData.builder()
                .columnId(EmployeeSystemImpl.KMF001_203)
                .value(null)
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());

        return MasterData.builder().rowData(data).build();
    }
}