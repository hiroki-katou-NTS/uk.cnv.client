package nts.uk.ctx.at.record.infra.entity.daily.ouen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

@Entity
@NoArgsConstructor
@Table(name = "KRCDT_DAY_TS_SUP")
public class KrcdtDayOuenTimeSheet extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 主キー */
	@EmbeddedId
	public KrcdtDayOuenTimePK pk;

	/** 職場ID */
	@Column(name = "WORKPLACE_ID")
	public String workplaceId;		
	
	/** 勤務場所コード */
	@Column(name = "WORK_LOCATION_CD")
	public String workLocationCode;

	/** 勤務枠No */
	@Column(name = "WORK_NO")
	public int workNo;

	/** 開始時刻 */
	@Column(name = "START_TIME")
	public Integer startTime;

	/** 開始の時刻変更手段 */
	@Column(name = "START_TIME_CHANGE_WAY")
	public Integer startTimeChangeWay;

	/** 開始の打刻方法 */
	@Column(name = "START_STAMP_METHOD")
	public Integer startStampMethod;

	/** 終了時刻 */
	@Column(name = "END_TIME")
	public Integer endTime;

	/** 終了の時刻変更手段 */
	@Column(name = "END_TIME_CHANGE_WAY")
	public Integer endTimeChangeWay;

	/** 終了の打刻方法 */
	@Column(name = "END_STAMP_METHOD")
	public Integer endStampMethod;

	/** 作業CD1 */
	@Column(name = "WORK_CD1")
	public String workCd1;

	/** 作業CD2 */
	@Column(name = "WORK_CD2")
	public String workCd2;

	/** 作業CD3 */
	@Column(name = "WORK_CD3")
	public String workCd3;

	/** 作業CD4 */
	@Column(name = "WORK_CD4")
	public String workCd4;

	/** 作業CD5 */
	@Column(name = "WORK_CD5")
	public String workCd5;
	
	/** 作業CD5 */
	@Column(name = "WORK_REMARKS")
	public String workRemarks;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static List<KrcdtDayOuenTimeSheet> convert(OuenWorkTimeSheetOfDaily domain) {
		
		List<KrcdtDayOuenTimeSheet> rs = new ArrayList<KrcdtDayOuenTimeSheet>();
		
		for (OuenWorkTimeSheetOfDailyAttendance oTimeSheetAtt : domain.getOuenTimeSheet()) {
			KrcdtDayOuenTimeSheet entity = new KrcdtDayOuenTimeSheet();
			
			entity.pk = new KrcdtDayOuenTimePK(domain.getEmpId(), 
					domain.getYmd(), oTimeSheetAtt.getWorkNo());
			
			entity.workplaceId = oTimeSheetAtt.getWorkContent().getWorkplace().getWorkplaceId() == null ? null : oTimeSheetAtt.getWorkContent().getWorkplace().getWorkplaceId().v();		
			entity.workLocationCode = !oTimeSheetAtt.getWorkContent().getWorkplace().getWorkLocationCD().isPresent() ? null 
					: oTimeSheetAtt.getWorkContent().getWorkplace().getWorkLocationCD().get().v();
			
			oTimeSheetAtt.getWorkContent().getWork().ifPresent(work -> {
				entity.workCd1 = work.getWorkCD1().v() == "" ? null : work.getWorkCD1().v();
				entity.workCd2 = work.getWorkCD2().map(w -> w.v()).orElse(null);
				entity.workCd3 = work.getWorkCD3().map(w -> w.v()).orElse(null); 
				entity.workCd4 = work.getWorkCD4().map(w -> w.v()).orElse(null);
				entity.workCd5 = work.getWorkCD5().map(w -> w.v()).orElse(null);
			});
			
			oTimeSheetAtt.getWorkContent().getWorkRemarks().ifPresent(remarks -> {
				entity.workRemarks = remarks.v();
			});
			
			entity.workNo = oTimeSheetAtt.getTimeSheet().getWorkNo().v();
			
			oTimeSheetAtt.getTimeSheet().getStart().ifPresent(start -> {
				entity.startTimeChangeWay = null;
				entity.startStampMethod = null;
				if(start.getReasonTimeChange() != null) {
					if(start.getReasonTimeChange().getTimeChangeMeans() != null) {
						entity.startTimeChangeWay = start.getReasonTimeChange().getTimeChangeMeans().value;
					}
					entity.startStampMethod = start.getReasonTimeChange().getEngravingMethod().map(c -> c.value).orElse(null);
				} 
				
				entity.startTime = start.getTimeWithDay().map(c -> c.v()).orElse(null); 
			});
			
			oTimeSheetAtt.getTimeSheet().getEnd().ifPresent(end -> {
				entity.endTimeChangeWay = null;
				entity.endStampMethod = null;
				if(end.getReasonTimeChange() != null) {
					if(end.getReasonTimeChange().getTimeChangeMeans() != null) {
						entity.endTimeChangeWay = end.getReasonTimeChange().getTimeChangeMeans().value;
					}
					entity.endStampMethod = end.getReasonTimeChange().getEngravingMethod().map(c -> c.value).orElse(null);
				}
				
				entity.endTime = end.getTimeWithDay().map(c -> c.v()).orElse(null); 
			});
			
			rs.add(entity);
		}
		
		return rs;
	}
}
