package nts.uk.file.at.app.export.vacation.set;

import nts.uk.file.at.app.export.vacation.set.annualpaidleave.AcquisitionRuleExportRepository;
import nts.uk.file.at.app.export.vacation.set.annualpaidleave.AnnPaidLeaveRepository;
import nts.uk.file.at.app.export.vacation.set.compensatoryleave.TemHoliEmployeeRepository;
import nts.uk.file.at.app.export.vacation.set.compensatoryleave.TempHoliComImplRepository;
import nts.uk.file.at.app.export.vacation.set.nursingleave.NursingLeaveSetRepository;
import nts.uk.file.at.app.export.vacation.set.sixtyhours.Com60HourVacaRepository;
import nts.uk.file.at.app.export.vacation.set.subst.ComSubstVacatRepository;
import nts.uk.file.at.app.export.vacation.set.subst.EmpSubstVacaRepository;
import nts.uk.file.at.app.export.vacation.set.subst.EmplYearlyRetenSetRepository;
import nts.uk.file.at.app.export.vacation.set.subst.RetenYearlySetRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.*;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateless
@DomainID(value="EmployeeSystem")
public class EmployeeSystemImpl implements MasterListData {

    public static final String KMF001_166 = "項目";
    public static final String KMF001_B01 = "Level_1";
    public static final String KMF001_B02 = "Level_2";
    public static final String KMF001_167 = "値";
    /*----*/
    public static final String KMF001_2 = "Level_2";
    public static final String KMF001_3 = "Level_3";
    public static final String KMF001_4 = "Level_4";
    /*-----*/
    public static final String KMF001_204 = "雇用コード";
    public static final String KMF001_205 = "雇用名";
    public static final String KMF001_223 = "代休設定";
    public static final String KMF001_207 = "代休使用期限";
    public static final String KMF001_208 = "代休の先取り";
    public static final String KMF001_210 = "時間代休の管理";
    public static final String KMF001_211 = "消化単位";
    /*------*/
    public static final String KMF001_206 = "代休の管理";
    public static final String KMF001_209 = "代休期限チェック月数";
    public static final String KMF001_212 = "代休の発生に必要な休日出勤時間";
    public static final String KMF001_213 = "代休出勤時間の条件";
    public static final String KMF001_214 = "指定した時間を代休とする１日";
    public static final String KMF001_215 = "指定した時間を代休とする半日";
    public static final String KMF001_216 = "一定時間を超えたら代休とする";
    public static final String KMF001_217 = "代休の発生に必要な残業時間";
    public static final String KMF001_218 = "代休残業時間の条件";
    public static final String KMF001_219 = "指定した時間を代休とする１日_01";
    public static final String KMF001_220 = "指定した時間を代休とする半日_02";
    public static final String KMF001_221 = "一定時間を超えたら代休とする_03";
    /*---------*/
    public static final String KMF001_170 = "Level_3";
    /*60H*/
    /*---------------*/
    public static final String KMF001_224 = "振休の管理";
    public static final String KMF001_225 = "使用期限";
    public static final String KMF001_226 = "振休の先取り";
    /*----*/
    public static final String KMF001_200 = "利用設定";
    public static final String KMF001_201 = "保持できる年数";
    public static final String KMF001_202 = "上限日数";
    /*-----*/
    public static final String KMF001_203 = "出勤日数として加算";


    public static final String IS_MANAGE = "管理する";
    public static final String IS_MANAGE_OF_HOLIDAYS = "管理する";



    @Inject
    private AcquisitionRuleExportRepository mAcquisitionRuleExportRepository;
    @Inject
    private AnnPaidLeaveRepository mAnnPaidLeaveRepository;
    @Inject
    private RetenYearlySetRepository mRetenYearlySetRepository;
    @Inject
    private TempHoliComImplRepository mTempHoliComImplRepository;
    @Inject
    private TemHoliEmployeeRepository mTemHoliEmployeeRepository;
    @Inject
    private ComSubstVacatRepository mComSubstVacatRepository;
    @Inject
    private NursingLeaveSetRepository mNursingLeaveSetRepository;
    @Inject
    private Com60HourVacaRepository mCom60HourVacaRepository;
    @Inject
    private EmplYearlyRetenSetRepository mEmplYearlyRetenSetRepository;
    @Inject
    private EmpSubstVacaRepository mEmpSubstVacaRepository;

    @Override
    public String mainSheetName() {
        return TextResource.localize("KMF001_156");
    }

    @Override
    public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery masterListExportQuery) {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn(KMF001_166, TextResource.localize("KMF001_166"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn(KMF001_B01, "",
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn(KMF001_B02, "",
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn(KMF001_167, TextResource.localize("KMF001_167"),
                ColumnTextAlign.LEFT, "", true));
        return columns;
    }

    public List<MasterHeaderColumn> getHeaderColumns(EmployeeSystem mEmployeeSystem) {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        switch (mEmployeeSystem) {
            case ANNUAL_HOLIDAYS:{

                columns.add(new MasterHeaderColumn(KMF001_166, TextResource.localize("KMF001_166"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_2, "",
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_3, "",
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_4, "",
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_167, TextResource.localize("KMF001_167"),
                        ColumnTextAlign.LEFT, "", true));
                break;
            }

            case CROWDED_COMPANY: {

                columns.add(new MasterHeaderColumn(KMF001_200, TextResource.localize("KMF001_200"),
                    ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_201, TextResource.localize("KMF001_201"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_202, TextResource.localize("KMF001_202"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_203, TextResource.localize("KMF001_203"),
                        ColumnTextAlign.LEFT, "", true));
                break;
            }
            case STEADY_EMPLOYMENT: {

                columns.add(new MasterHeaderColumn(KMF001_204, TextResource.localize("KMF001_204"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_205, TextResource.localize("KMF001_205"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_200, TextResource.localize("KMF001_200"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_201, TextResource.localize("KMF001_201"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_202, TextResource.localize("KMF001_202"),
                        ColumnTextAlign.LEFT, "", true));
                break;
            }
            case OFFTIME_COMPANY: {

                columns.add(new MasterHeaderColumn(KMF001_206, TextResource.localize("KMF001_206"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_207, TextResource.localize("KMF001_207"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_208, TextResource.localize("KMF001_208"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_209, TextResource.localize("KMF001_209"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_210, TextResource.localize("KMF001_210"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_211, TextResource.localize("KMF001_211"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_212, TextResource.localize("KMF001_212"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_213, TextResource.localize("KMF001_213"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_214, TextResource.localize("KMF001_214"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_215, TextResource.localize("KMF001_215"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_216, TextResource.localize("KMF001_216"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_217, TextResource.localize("KMF001_217"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_218, TextResource.localize("KMF001_218"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_219, TextResource.localize("KMF001_219"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_220, TextResource.localize("KMF001_220"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_221, TextResource.localize("KMF001_221"),
                        ColumnTextAlign.LEFT, "", true));
                break;
            }
            case SUBSTITUTE_EMPLOYMENT: {
                columns.add(new MasterHeaderColumn(KMF001_204, TextResource.localize("KMF001_204"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_205, TextResource.localize("KMF001_205"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_223, TextResource.localize("KMF001_223"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_207, TextResource.localize("KMF001_207"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_208, TextResource.localize("KMF001_208"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_210, TextResource.localize("KMF001_210"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_211, TextResource.localize("KMF001_211"),
                        ColumnTextAlign.LEFT, "", true));
                break;
            }
            case SHUTDOWM_COMPANY: {

                columns.add(new MasterHeaderColumn(KMF001_224, TextResource.localize("KMF001_224"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_225, TextResource.localize("KMF001_225"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_226, TextResource.localize("KMF001_226"),
                        ColumnTextAlign.LEFT, "", true));
                break;
            }
            case PAID_WORK: {

                columns.add(new MasterHeaderColumn(KMF001_204, TextResource.localize("KMF001_204"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_205, TextResource.localize("KMF001_205"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_224, TextResource.localize("KMF001_224"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_225, TextResource.localize("KMF001_225"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_226, TextResource.localize("KMF001_226"),
                        ColumnTextAlign.LEFT, "", true));
                break;
            }
            case SIXTY_HOURS: {

                columns.add(new MasterHeaderColumn(KMF001_166, TextResource.localize("KMF001_166"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_B01, TextResource.localize(""),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_170, TextResource.localize(""),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_167, TextResource.localize("KMF001_167"),
                        ColumnTextAlign.LEFT, "", true));

                break;
            }
            case NURSING_CARE: {

                columns.add(new MasterHeaderColumn(KMF001_166, TextResource.localize("KMF001_166"),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_2, TextResource.localize(""),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_3, TextResource.localize(""),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_4, TextResource.localize(""),
                        ColumnTextAlign.LEFT, "", true));
                columns.add(new MasterHeaderColumn(KMF001_167, TextResource.localize("KMF001_167"),
                        ColumnTextAlign.LEFT, "", true));

                break;
            }
        }
        return columns;
    }

    @Override
    public List<MasterData> getMasterDatas(MasterListExportQuery query) {
        String companyId = AppContexts.user().companyId();
        return mAcquisitionRuleExportRepository.getAllAcquisitionRule(companyId);
    }
    @Override
    public List<SheetData> extraSheets(MasterListExportQuery query){
        List<SheetData> sheetDatas = new ArrayList<>();
        String companyId = AppContexts.user().companyId();
        SheetData sheetData1 = SheetData.builder()
                .mainData(mAnnPaidLeaveRepository.getAnPaidLea(companyId))
                .mainDataColumns(getHeaderColumns(EmployeeSystem.ANNUAL_HOLIDAYS))
                .sheetName(getSheetName(EmployeeSystem.ANNUAL_HOLIDAYS))
                .build();
        sheetDatas.add(sheetData1);

        List<MasterData> listAllRetenYearlySet = mRetenYearlySetRepository.getAllRetenYearlySet(companyId);
        SheetData sheetData2 = SheetData.builder()
                .mainData(listAllRetenYearlySet)
                .mainDataColumns(getHeaderColumns(EmployeeSystem.CROWDED_COMPANY))
                .sheetName(getSheetName(EmployeeSystem.CROWDED_COMPANY))
                .build();
        sheetDatas.add(sheetData2);

        if (listAllRetenYearlySet.get(0).getRowData().get(EmployeeSystemImpl.KMF001_200).getValue() != null && listAllRetenYearlySet.get(0).getRowData().get(EmployeeSystemImpl.KMF001_200).getValue().equals(IS_MANAGE)) {
            SheetData sheetData3 = SheetData.builder()
                    .mainData(mEmplYearlyRetenSetRepository.getAllEmplYearlyRetenSet(companyId))
                    .mainDataColumns(getHeaderColumns(EmployeeSystem.STEADY_EMPLOYMENT))
                    .sheetName(getSheetName(EmployeeSystem.STEADY_EMPLOYMENT))
                    .build();
            sheetDatas.add(sheetData3);
        }

        List<MasterData> listAllTemHoliCompany = mTempHoliComImplRepository.getAllTemHoliCompany(companyId);
        SheetData sheetData4 = SheetData.builder()
                .mainData(listAllTemHoliCompany)
                .mainDataColumns(getHeaderColumns(EmployeeSystem.OFFTIME_COMPANY))
                .sheetName(getSheetName(EmployeeSystem.OFFTIME_COMPANY))
                .build();
        sheetDatas.add(sheetData4);

        if (listAllTemHoliCompany.get(0).getRowData().get(EmployeeSystemImpl.KMF001_206).getValue().equals(IS_MANAGE_OF_HOLIDAYS)) {
            SheetData sheetData5 = SheetData.builder()
                    .mainData(mTemHoliEmployeeRepository.getTemHoliEmployee(companyId))
                    .mainDataColumns(getHeaderColumns(EmployeeSystem.SUBSTITUTE_EMPLOYMENT))
                    .sheetName(getSheetName(EmployeeSystem.SUBSTITUTE_EMPLOYMENT))
                    .build();
            sheetDatas.add(sheetData5);
        }

        List<MasterData> listAllComSubstVacation = mComSubstVacatRepository.getAllComSubstVacation(companyId);
        SheetData sheetData6 = SheetData.builder()
                .mainData(listAllComSubstVacation)
                .mainDataColumns(getHeaderColumns(EmployeeSystem.SHUTDOWM_COMPANY))
                .sheetName(getSheetName(EmployeeSystem.SHUTDOWM_COMPANY))
                .build();
        sheetDatas.add(sheetData6);

        if(listAllComSubstVacation.get(0).getRowData().get(EmployeeSystemImpl.KMF001_224).getValue().equals(IS_MANAGE)){
            SheetData sheetData7 = SheetData.builder()
                    .mainData(mEmpSubstVacaRepository.getAllEmpSubstVacation(companyId))
                    .mainDataColumns(getHeaderColumns(EmployeeSystem.PAID_WORK))
                    .sheetName(getSheetName(EmployeeSystem.PAID_WORK))
                    .build();
            sheetDatas.add(sheetData7);
        }

        SheetData sheetData8 = SheetData.builder()
                .mainData(mCom60HourVacaRepository.getAllCom60HourVacation(companyId))
                .mainDataColumns(getHeaderColumns(EmployeeSystem.SIXTY_HOURS))
                .sheetName(getSheetName(EmployeeSystem.SIXTY_HOURS))
                .build();
        sheetDatas.add(sheetData8);

        SheetData sheetData9 = SheetData.builder()
                .mainData(mNursingLeaveSetRepository.getAllNursingLeaveSetting(companyId))
                .mainDataColumns(getHeaderColumns(EmployeeSystem.NURSING_CARE))
                .sheetName(getSheetName(EmployeeSystem.NURSING_CARE))
                .build();
        sheetDatas.add(sheetData9);

        return sheetDatas;
    }

    private String getSheetName(EmployeeSystem mEmployeeSystem){
        switch (mEmployeeSystem) {
            case ANNUAL_HOLIDAYS: {
                return  TextResource.localize("KMF001_157");
            }
            case CROWDED_COMPANY: {
                return TextResource.localize("KMF001_158");
            }
            case STEADY_EMPLOYMENT: {
                return TextResource.localize("KMF001_159");
            }
            case OFFTIME_COMPANY: {
                return TextResource.localize("KMF001_160");
            }
            case SUBSTITUTE_EMPLOYMENT: {
                return TextResource.localize("KMF001_161");
            }
            case SHUTDOWM_COMPANY:{
                return TextResource.localize("KMF001_162");
            }
            case PAID_WORK:{
                return TextResource.localize("KMF001_163");
            }
            case SIXTY_HOURS:{
                return TextResource.localize("KMF001_164");
            }
            case NURSING_CARE: {
                return TextResource.localize("KMF001_165");
            }
        }
        return "";
    }

}
