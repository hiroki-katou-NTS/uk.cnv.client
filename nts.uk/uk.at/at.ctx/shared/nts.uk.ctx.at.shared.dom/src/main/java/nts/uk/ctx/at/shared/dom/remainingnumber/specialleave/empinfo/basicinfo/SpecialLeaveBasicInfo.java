package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTbl;

/**
 * domain 特別休暇基本情報
 * 
 * @author Hop.NT
 *
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialLeaveBasicInfo extends AggregateRoot {

	private String cID;
	// 社員ID
	private String sID;

	// 特別休暇コード
	private SpecialHolidayCode specialLeaveCode;

	// 使用区分
	private UseAtr used;

	// 適用設定
	private SpecialLeaveAppSetting applicationSet;

	// 付与設定
	private SpecialLeaveGrantSetting grantSetting;

	public SpecialLeaveBasicInfo(String cid, String sid, int spLeaCD, BigDecimal used, BigDecimal appSet,
			GeneralDate grantDate, Integer grantDay, String grantTbl) {
		this.cID = cid;
		this.sID = sid;
		this.specialLeaveCode = new SpecialHolidayCode(spLeaCD);
		if (used == null) {
			this.used = UseAtr.NOT_USE;
		} else {
			this.used = EnumAdaptor.valueOf(used.intValue(), UseAtr.class);
		}
		if (appSet == null) {
			this.applicationSet = SpecialLeaveAppSetting.PRESCRIBED;
		} else {
			this.applicationSet = EnumAdaptor.valueOf(appSet.intValue(), SpecialLeaveAppSetting.class);
		}
		this.grantSetting = new SpecialLeaveGrantSetting(grantDate, grantDay, grantTbl);
	}
	
	public SpecialLeaveBasicInfo(String cid, String sid, int spLeaCD, Integer used, Integer appSet,
			GeneralDate grantDate, Integer grantDay, String grantTbl) {
		this.cID = cid;
		this.sID = sid;
		this.specialLeaveCode = new SpecialHolidayCode(spLeaCD);
		if (used == null) {
			this.used = UseAtr.NOT_USE;
		} else {
			this.used = EnumAdaptor.valueOf(used.intValue(), UseAtr.class);
		}
		if (appSet == null) {
			this.applicationSet = SpecialLeaveAppSetting.PRESCRIBED;
		} else {
			this.applicationSet = EnumAdaptor.valueOf(appSet.intValue(), SpecialLeaveAppSetting.class);
		}
		this.grantSetting = new SpecialLeaveGrantSetting(grantDate, grantDay, grantTbl);
	}

	
	/**
	 * 付与日数を取得する
	 * @param gtantDay
	 * @return
	 */
	public LeaveGrantDayNumber getGrantDay(LeaveGrantDayNumber gtantDay){
		// 「特別休暇基本情報．適用設定＝個人の条件を適用する」　
		if ( getApplicationSet().equals(SpecialLeaveAppSetting.PERSONAL) ){
			// 付与日数　←　特別休暇基本情報．付与設定．付与日数
			if(grantSetting.getGrantDays().isPresent()){
				return new LeaveGrantDayNumber((double)grantSetting.getGrantDays().get().v());
			}
		}
		
		return gtantDay;	
	}
	
	/**
	 * 付与テーブルを取得する
	 * @param cid
	 * @param require
	 * @return
	 */
	public Optional<GrantDateTbl> getGrantDateTbl(String cid, Require require){
	
		if(this.getApplicationSet().equals(SpecialLeaveAppSetting.PERSONAL)){
			if(this.getGrantSetting().getGrantTable().isPresent()){
				return require.grantDateTbl(cid, this.getSpecialLeaveCode().v(), this.getGrantSetting().getGrantTable().get().toString());
			}
		}
		
		List<GrantDateTbl> grantDateTblList = require.grantDateTbl(cid, this.getSpecialLeaveCode().v());

		return grantDateTblList.stream()
				.filter(c->c.isSpecified()) // 規定のテーブルとする
				.findFirst();
	}
	
	/**
	 * 使用するか判断する
	 * @return
	 */
	public boolean isUsed(){
		return this.getUsed().isUse();
	}
	
	public static interface Require{
		Optional<GrantDateTbl> grantDateTbl(String companyId, int specialHolidayCode, String grantDateCode);
		
		List<GrantDateTbl> grantDateTbl(String companyId, int specialHolidayCode);
	}
}
