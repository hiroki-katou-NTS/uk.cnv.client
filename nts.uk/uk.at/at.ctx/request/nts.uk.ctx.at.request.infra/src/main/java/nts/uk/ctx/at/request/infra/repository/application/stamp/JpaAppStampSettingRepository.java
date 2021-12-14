package nts.uk.ctx.at.request.infra.repository.application.stamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampSettingRepository;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.UseDivision;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.AppCommentSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.AppStampSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.Comment;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.GoOutType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.GoOutTypeDispControl;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.SettingForEachType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.StampAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.SupportFrameDispNO;
import nts.uk.shr.com.color.ColorCode;
@Stateless
public class JpaAppStampSettingRepository extends JpaRepository implements AppStampSettingRepository{

	public static final String FIND_BY_ID = "SELECT *\r\n" + 
			"  FROM KRQMT_APP_STAMP WHERE CID = @cid";
	@Override
	public Optional<AppStampSetting> findByAppID(String companyID) {
		
		return new NtsStatement(FIND_BY_ID,this.jdbcProxy())
					.paramString("cid", companyID)
					.getSingle(res -> toDomain(res));
				
	}

	@Override
	public void addStamp(AppStampSetting appStamp) {
		
	}

	@Override
	public void updateStamp(AppStampSetting appStamp) {
		
	}

	@Override
	public void delete(String companyID, String appID) {
		
	}
	
	public AppStampSetting toDomain(NtsResultRecord res ) {
		AppStampSetting appStampSetting = new AppStampSetting();
		
		appStampSetting.setCompanyID(res.getString("CID"));
		
		appStampSetting.setSupportFrameDispNO(new SupportFrameDispNO(res.getInt("SUP_DISP_CNT")));
		
		appStampSetting.setUseCancelFunction(EnumAdaptor.valueOf(res.getInt("CANCEL_DISP_ATR"), UseDivision.class));
		
		List<SettingForEachType> settingForEachTypeLst = new ArrayList<SettingForEachType>();
		
//		出勤/退勤
		SettingForEachType attendenceRetirement = new SettingForEachType();
		attendenceRetirement.setStampAtr(StampAtr.ATTENDANCE_RETIREMENT);
		
		AppCommentSet appCommentSetAttendenceRetirement1 = new AppCommentSet(); 
		appCommentSetAttendenceRetirement1.setColorCode(new ColorCode(res.getString("WK_CMT_FONT_COLOR1")));
		appCommentSetAttendenceRetirement1.setComment(new Comment(res.getString("WK_CMT_CONTENT1")));
		appCommentSetAttendenceRetirement1.setBold(BooleanUtils.toBoolean(res.getInt("WK_CMT_FONT_WEIGHT1")));
		attendenceRetirement.setTopComment(appCommentSetAttendenceRetirement1);
		
		AppCommentSet appCommentSetAttendenceRetirement2 = new AppCommentSet(); 
		appCommentSetAttendenceRetirement2.setColorCode(new ColorCode(res.getString("WK_CMT_FONT_COLOR2")));
		appCommentSetAttendenceRetirement2.setComment(new Comment(res.getString("WK_CMT_CONTENT2")));
		appCommentSetAttendenceRetirement2.setBold(BooleanUtils.toBoolean(res.getInt("WK_CMT_FONT_WEIGHT2")));
		attendenceRetirement.setBottomComment(appCommentSetAttendenceRetirement2);
		
		settingForEachTypeLst.add(attendenceRetirement);
		
		
		
//		外出/戻り
		SettingForEachType goOut = new SettingForEachType();
		goOut.setStampAtr(StampAtr.GOING_OUT_RETURNING);
		
		AppCommentSet appCommentSetGoOut1 = new AppCommentSet(); 
		appCommentSetGoOut1.setColorCode(new ColorCode(res.getString("GOOUT_CMT_FONT_COLOR1")));
		appCommentSetGoOut1.setComment(new Comment(res.getString("GOOUT_CMT_CONTENT1")));
		appCommentSetGoOut1.setBold(BooleanUtils.toBoolean(res.getInt("GOOUT_CMT_FONT_WEIGHT1")));
		goOut.setTopComment(appCommentSetGoOut1);
		
		AppCommentSet appCommentSetGoOut2 = new AppCommentSet(); 
		appCommentSetGoOut2.setColorCode(new ColorCode(res.getString("GOOUT_CMT_FONT_COLOR2")));
		appCommentSetGoOut2.setComment(new Comment(res.getString("GOOUT_CMT_CONTENT2")));
		appCommentSetGoOut2.setBold(BooleanUtils.toBoolean(res.getInt("GOOUT_CMT_FONT_WEIGHT2")));
		goOut.setBottomComment(appCommentSetGoOut2);
		
		settingForEachTypeLst.add(goOut);
		
		
//		育児外出/育児戻り
		SettingForEachType childCare = new SettingForEachType();
		childCare.setStampAtr(StampAtr.CHILDCARE_OUT_RETURN);
		
		AppCommentSet appCommentSetchildCare1 = new AppCommentSet(); 
		appCommentSetchildCare1.setColorCode(new ColorCode(res.getString("CHILD_CARE_CMT_FONT_COLOR1")));
		appCommentSetchildCare1.setComment(new Comment(res.getString("CHILD_CARE_CMT_CONTENT1")));
		appCommentSetchildCare1.setBold(BooleanUtils.toBoolean(res.getInt("CHILD_CARE_CMT_FONT_WEIGHT1")));
		childCare.setTopComment(appCommentSetchildCare1);
		
		AppCommentSet appCommentSetchildCare2 = new AppCommentSet(); 
		appCommentSetchildCare2.setColorCode(new ColorCode(res.getString("CHILD_CARE_CMT_FONT_COLOR2")));
		appCommentSetchildCare2.setComment(new Comment(res.getString("CHILD_CARE_CMT_CONTENT2")));
		appCommentSetchildCare2.setBold(BooleanUtils.toBoolean(res.getInt("CHILD_CARE_CMT_FONT_WEIGHT2")));
		childCare.setBottomComment(appCommentSetchildCare2);
		
		settingForEachTypeLst.add(childCare);
		
		
//		応援入/応援出
		SettingForEachType support = new SettingForEachType();
		support.setStampAtr(StampAtr.SUPPORT_IN_SUPPORT_OUT);
		
		AppCommentSet appCommentSetSupport1 = new AppCommentSet(); 
		appCommentSetSupport1.setColorCode(new ColorCode(res.getString("SUP_CMT_FONT_COLOR1")));
		appCommentSetSupport1.setComment(new Comment(res.getString("SUP_CMT_CONTENT1")));
		appCommentSetSupport1.setBold(BooleanUtils.toBoolean(res.getInt("SUP_CMT_FONT_WEIGHT1")));
		support.setTopComment(appCommentSetSupport1);
		
		AppCommentSet appCommentSetSupport2 = new AppCommentSet(); 
		appCommentSetSupport2.setColorCode(new ColorCode(res.getString("SUP_CMT_FONT_COLOR2")));
		appCommentSetSupport2.setComment(new Comment(res.getString("SUP_CMT_CONTENT2")));
		appCommentSetSupport2.setBold(BooleanUtils.toBoolean(res.getInt("SUP_CMT_FONT_WEIGHT2")));
		support.setBottomComment(appCommentSetSupport2);
		
		settingForEachTypeLst.add(support);
		
		
//		介護外出/介護戻り
		
		SettingForEachType outCare = new SettingForEachType();
		outCare.setStampAtr(StampAtr.OUT_OF_CARE_RETURN_OF_CARE);
		
		AppCommentSet appCommentSetOutCare1 = new AppCommentSet(); 
		appCommentSetOutCare1.setColorCode(new ColorCode(res.getString("CARE_CMT_FONT_COLOR1")));
		appCommentSetOutCare1.setComment(new Comment(res.getString("CARE_CMT_CONTENT1")));
		appCommentSetOutCare1.setBold(BooleanUtils.toBoolean(res.getInt("CARE_CMT_FONT_WEIGHT1")));
		outCare.setTopComment(appCommentSetOutCare1);
		
		AppCommentSet appCommentSetOutCare2 = new AppCommentSet(); 
		appCommentSetOutCare2.setColorCode(new ColorCode(res.getString("CARE_CMT_FONT_COLOR2")));
		appCommentSetOutCare2.setComment(new Comment(res.getString("CARE_CMT_CONTENT2")));
		appCommentSetOutCare2.setBold(BooleanUtils.toBoolean(res.getInt("CARE_CMT_FONT_WEIGHT2")));
		outCare.setBottomComment(appCommentSetOutCare2);
		
		settingForEachTypeLst.add(outCare);
		
		
//		休憩	
		SettingForEachType breaks = new SettingForEachType();
		breaks.setStampAtr(StampAtr.BREAK);
		
		AppCommentSet appCommentSetBreak1 = new AppCommentSet(); 
		appCommentSetBreak1.setColorCode(new ColorCode(res.getString("BREAK_CMT_FONT_COLOR1")));
		appCommentSetBreak1.setComment(new Comment(res.getString("BREAK_CMT_CONTENT1")));
		appCommentSetBreak1.setBold(BooleanUtils.toBoolean(res.getInt("BREAK_CMT_FONT_WEIGHT1")));
		breaks.setTopComment(appCommentSetBreak1);
		
		AppCommentSet appCommentSetBreak2 = new AppCommentSet(); 
		appCommentSetBreak2.setColorCode(new ColorCode(res.getString("BREAK_CMT_FONT_COLOR2")));
		appCommentSetBreak2.setComment(new Comment(res.getString("BREAK_CMT_CONTENT2")));
		appCommentSetBreak2.setBold(BooleanUtils.toBoolean(res.getInt("BREAK_CMT_FONT_WEIGHT2")));
		breaks.setBottomComment(appCommentSetBreak2);
		
		settingForEachTypeLst.add(breaks);
		
		
//		レコーダーイメージ
		
		SettingForEachType recoder = new SettingForEachType();
		recoder.setStampAtr(StampAtr.RECORDER_IMAGE);
		
		AppCommentSet appCommentSetRecoder1 = new AppCommentSet(); 
		appCommentSetRecoder1.setColorCode(new ColorCode(res.getString("NR_CMT_FONT_COLOR1")));
		appCommentSetRecoder1.setComment(new Comment(res.getString("NR_CMT_CONTENT1")));
		appCommentSetRecoder1.setBold(BooleanUtils.toBoolean(res.getInt("NR_CMT_FONT_WEIGHT1")));
		recoder.setTopComment(appCommentSetRecoder1);
		
		AppCommentSet appCommentSetRecoder2 = new AppCommentSet(); 
		appCommentSetRecoder2.setColorCode(new ColorCode(res.getString("NR_CMT_FONT_COLOR2")));
		appCommentSetRecoder2.setComment(new Comment(res.getString("NR_CMT_CONTENT2")));
		appCommentSetRecoder2.setBold(BooleanUtils.toBoolean(res.getInt("NR_CMT_FONT_WEIGHT2")));
		recoder.setBottomComment(appCommentSetRecoder2);
		
		settingForEachTypeLst.add(recoder);
		
		
		List<GoOutTypeDispControl> goOutTypeDispControl = new ArrayList<GoOutTypeDispControl>();
		
//		私用
		GoOutTypeDispControl type1 = new GoOutTypeDispControl();
		type1.setGoOutType(GoOutType.PRIVATE);
		type1.setDisplay(EnumAdaptor.valueOf(res.getInt("STAMP_OUT_PRI_DISP_ATR"), DisplayAtr.class));
		goOutTypeDispControl.add(type1);
		
//		公用
		GoOutTypeDispControl type2 = new GoOutTypeDispControl();
		type2.setGoOutType(GoOutType.OFFICE);
		type2.setDisplay(EnumAdaptor.valueOf(res.getInt("STAMP_OUT_PUB_DISP_ATR"), DisplayAtr.class));
		goOutTypeDispControl.add(type2);
		
//		有償
		GoOutTypeDispControl type3 = new GoOutTypeDispControl();
		type3.setGoOutType(GoOutType.COMPENSATION);
		type3.setDisplay(EnumAdaptor.valueOf(res.getInt("STAMP_OUT_COMP_DISP_ATR"), DisplayAtr.class));
		goOutTypeDispControl.add(type3);
		
//		組合
		
		GoOutTypeDispControl type4 = new GoOutTypeDispControl();
		type4.setGoOutType(GoOutType.UNION);
		type4.setDisplay(EnumAdaptor.valueOf(res.getInt("STAMP_OUT_UNION_DISP_ATR"), DisplayAtr.class));
		goOutTypeDispControl.add(type4);
		
		appStampSetting.setSettingForEachTypeLst(settingForEachTypeLst);
		appStampSetting.setGoOutTypeDispControl(goOutTypeDispControl);

		
		return appStampSetting;
	}
	

}
