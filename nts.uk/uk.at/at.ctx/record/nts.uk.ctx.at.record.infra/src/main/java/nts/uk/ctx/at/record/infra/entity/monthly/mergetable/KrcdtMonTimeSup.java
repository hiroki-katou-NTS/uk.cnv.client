package nts.uk.ctx.at.record.infra.entity.monthly.mergetable;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.gul.reflection.FieldReflection;
import nts.gul.reflection.ReflectionUtil;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.OuenTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.OuenWorkAggregateDetail;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.OuenWorkAggregateFrameDetail;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 月別実績の応援時間
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_MON_TIME_SUP")
public class KrcdtMonTimeSup extends ContractUkJpaEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	public static final JpaEntityMapper<KrcdtMonTimeAtd> MAPPER = new JpaEntityMapper<>(KrcdtMonTimeAtd.class);

	@EmbeddedId
	public KrcdtMonMergePk id;

	/** 月別実績の応援時間　のカラム定義　*/
	@Column(name = "OTHER_TOTAL_TIME")
	public int ouenOtherTotalTime;
	@Column(name = "OTHER_TOTAL_AMOUNT")
	public long ouenOtherTotalAmount;
	@Column(name = "FRAME1_TOTAL_TIME")
	public int ouenFrame1TotalTime;
	@Column(name = "FRAME1_TOTAL_AMOUNT")
	public long ouenFrame1TotalAmount;
	@Column(name = "FRAME2_TOTAL_TIME")
	public int ouenFrame2TotalTime;
	@Column(name = "FRAME2_TOTAL_AMOUNT")
	public long ouenFrame2TotalAmount;
	@Column(name = "FRAME3_TOTAL_TIME")
	public int ouenFrame3TotalTime;
	@Column(name = "FRAME3_TOTAL_AMOUNT")
	public long ouenFrame3TotalAmount;
	@Column(name = "FRAME4_TOTAL_TIME")
	public int ouenFrame4TotalTime;
	@Column(name = "FRAME4_TOTAL_AMOUNT")
	public long ouenFrame4TotalAmount;
	@Column(name = "FRAME5_TOTAL_TIME")
	public int ouenFrame5TotalTime;
	@Column(name = "FRAME5_TOTAL_AMOUNT")
	public long ouenFrame5TotalAmount;
	@Column(name = "FRAME6_TOTAL_TIME")
	public int ouenFrame6TotalTime;
	@Column(name = "FRAME6_TOTAL_AMOUNT")
	public long ouenFrame6TotalAmount;
	@Column(name = "FRAME7_TOTAL_TIME")
	public int ouenFrame7TotalTime;
	@Column(name = "FRAME7_TOTAL_AMOUNT")
	public long ouenFrame7TotalAmount;
	@Column(name = "FRAME8_TOTAL_TIME")
	public int ouenFrame8TotalTime;
	@Column(name = "FRAME8_TOTAL_AMOUNT")
	public long ouenFrame8TotalAmount;
	@Column(name = "FRAME9_TOTAL_TIME")
	public int ouenFrame9TotalTime;
	@Column(name = "FRAME9_TOTAL_AMOUNT")
	public long ouenFrame9TotalAmount;
	@Column(name = "FRAME10_TOTAL_TIME")
	public int ouenFrame10TotalTime;
	@Column(name = "FRAME10_TOTAL_AMOUNT")
	public long ouenFrame10TotalAmount;
	@Column(name = "FRAME11_TOTAL_TIME")
	public int ouenFrame11TotalTime;
	@Column(name = "FRAME11_TOTAL_AMOUNT")
	public long ouenFrame11TotalAmount;
	@Column(name = "FRAME12_TOTAL_TIME")
	public int ouenFrame12TotalTime;
	@Column(name = "FRAME12_TOTAL_AMOUNT")
	public long ouenFrame12TotalAmount;
	@Column(name = "FRAME13_TOTAL_TIME")
	public int ouenFrame13TotalTime;
	@Column(name = "FRAME13_TOTAL_AMOUNT")
	public long ouenFrame13TotalAmount;
	@Column(name = "FRAME14_TOTAL_TIME")
	public int ouenFrame14TotalTime;
	@Column(name = "FRAME14_TOTAL_AMOUNT")
	public long ouenFrame14TotalAmount;
	@Column(name = "FRAME15_TOTAL_TIME")
	public int ouenFrame15TotalTime;
	@Column(name = "FRAME15_TOTAL_AMOUNT")
	public long ouenFrame15TotalAmount;
	@Column(name = "FRAME16_TOTAL_TIME")
	public int ouenFrame16TotalTime;
	@Column(name = "FRAME16_TOTAL_AMOUNT")
	public long ouenFrame16TotalAmount;
	@Column(name = "FRAME17_TOTAL_TIME")
	public int ouenFrame17TotalTime;
	@Column(name = "FRAME17_TOTAL_AMOUNT")
	public long ouenFrame17TotalAmount;
	@Column(name = "FRAME18_TOTAL_TIME")
	public int ouenFrame18TotalTime;
	@Column(name = "FRAME18_TOTAL_AMOUNT")
	public long ouenFrame18TotalAmount;
	@Column(name = "FRAME19_TOTAL_TIME")
	public int ouenFrame19TotalTime;
	@Column(name = "FRAME19_TOTAL_AMOUNT")
	public long ouenFrame19TotalAmount;
	@Column(name = "FRAME20_TOTAL_TIME")
	public int ouenFrame20TotalTime;
	@Column(name = "FRAME20_TOTAL_AMOUNT")
	public long ouenFrame20TotalAmount;
	@Column(name = "FRAME21_TOTAL_TIME")
	public int ouenFrame21TotalTime;
	@Column(name = "FRAME21_TOTAL_AMOUNT")
	public long ouenFrame21TotalAmount;
	@Column(name = "FRAME22_TOTAL_TIME")
	public int ouenFrame22TotalTime;
	@Column(name = "FRAME22_TOTAL_AMOUNT")
	public long ouenFrame22TotalAmount;
	@Column(name = "FRAME23_TOTAL_TIME")
	public int ouenFrame23TotalTime;
	@Column(name = "FRAME23_TOTAL_AMOUNT")
	public long ouenFrame23TotalAmount;
	@Column(name = "FRAME24_TOTAL_TIME")
	public int ouenFrame24TotalTime;
	@Column(name = "FRAME24_TOTAL_AMOUNT")
	public long ouenFrame24TotalAmount;
	@Column(name = "FRAME25_TOTAL_TIME")
	public int ouenFrame25TotalTime;
	@Column(name = "FRAME25_TOTAL_AMOUNT")
	public long ouenFrame25TotalAmount;
	@Column(name = "FRAME26_TOTAL_TIME")
	public int ouenFrame26TotalTime;
	@Column(name = "FRAME26_TOTAL_AMOUNT")
	public long ouenFrame26TotalAmount;
	@Column(name = "FRAME27_TOTAL_TIME")
	public int ouenFrame27TotalTime;
	@Column(name = "FRAME27_TOTAL_AMOUNT")
	public long ouenFrame27TotalAmount;
	@Column(name = "FRAME28_TOTAL_TIME")
	public int ouenFrame28TotalTime;
	@Column(name = "FRAME28_TOTAL_AMOUNT")
	public long ouenFrame28TotalAmount;
	@Column(name = "FRAME29_TOTAL_TIME")
	public int ouenFrame29TotalTime;
	@Column(name = "FRAME29_TOTAL_AMOUNT")
	public long ouenFrame29TotalAmount;
	@Column(name = "FRAME30_TOTAL_TIME")
	public int ouenFrame30TotalTime;
	@Column(name = "FRAME30_TOTAL_AMOUNT")
	public long ouenFrame30TotalAmount;
	@Column(name = "FRAME31_TOTAL_TIME")
	public int ouenFrame31TotalTime;
	@Column(name = "FRAME31_TOTAL_AMOUNT")
	public long ouenFrame31TotalAmount;
	@Column(name = "FRAME32_TOTAL_TIME")
	public int ouenFrame32TotalTime;
	@Column(name = "FRAME32_TOTAL_AMOUNT")
	public long ouenFrame32TotalAmount;
	@Column(name = "FRAME33_TOTAL_TIME")
	public int ouenFrame33TotalTime;
	@Column(name = "FRAME33_TOTAL_AMOUNT")
	public long ouenFrame33TotalAmount;
	@Column(name = "FRAME34_TOTAL_TIME")
	public int ouenFrame34TotalTime;
	@Column(name = "FRAME34_TOTAL_AMOUNT")
	public long ouenFrame34TotalAmount;
	@Column(name = "FRAME35_TOTAL_TIME")
	public int ouenFrame35TotalTime;
	@Column(name = "FRAME35_TOTAL_AMOUNT")
	public long ouenFrame35TotalAmount;
	@Column(name = "FRAME36_TOTAL_TIME")
	public int ouenFrame36TotalTime;
	@Column(name = "FRAME36_TOTAL_AMOUNT")
	public long ouenFrame36TotalAmount;
	@Column(name = "FRAME37_TOTAL_TIME")
	public int ouenFrame37TotalTime;
	@Column(name = "FRAME37_TOTAL_AMOUNT")
	public long ouenFrame37TotalAmount;
	@Column(name = "FRAME38_TOTAL_TIME")
	public int ouenFrame38TotalTime;
	@Column(name = "FRAME38_TOTAL_AMOUNT")
	public long ouenFrame38TotalAmount;
	@Column(name = "FRAME39_TOTAL_TIME")
	public int ouenFrame39TotalTime;
	@Column(name = "FRAME39_TOTAL_AMOUNT")
	public long ouenFrame39TotalAmount;
	@Column(name = "FRAME40_TOTAL_TIME")
	public int ouenFrame40TotalTime;
	@Column(name = "FRAME40_TOTAL_AMOUNT")
	public long ouenFrame40TotalAmount;
	@Column(name = "FRAME41_TOTAL_TIME")
	public int ouenFrame41TotalTime;
	@Column(name = "FRAME41_TOTAL_AMOUNT")
	public long ouenFrame41TotalAmount;
	@Column(name = "FRAME42_TOTAL_TIME")
	public int ouenFrame42TotalTime;
	@Column(name = "FRAME42_TOTAL_AMOUNT")
	public long ouenFrame42TotalAmount;
	@Column(name = "FRAME43_TOTAL_TIME")
	public int ouenFrame43TotalTime;
	@Column(name = "FRAME43_TOTAL_AMOUNT")
	public long ouenFrame43TotalAmount;
	@Column(name = "FRAME44_TOTAL_TIME")
	public int ouenFrame44TotalTime;
	@Column(name = "FRAME44_TOTAL_AMOUNT")
	public long ouenFrame44TotalAmount;
	@Column(name = "FRAME45_TOTAL_TIME")
	public int ouenFrame45TotalTime;
	@Column(name = "FRAME45_TOTAL_AMOUNT")
	public long ouenFrame45TotalAmount;
	@Column(name = "FRAME46_TOTAL_TIME")
	public int ouenFrame46TotalTime;
	@Column(name = "FRAME46_TOTAL_AMOUNT")
	public long ouenFrame46TotalAmount;
	@Column(name = "FRAME47_TOTAL_TIME")
	public int ouenFrame47TotalTime;
	@Column(name = "FRAME47_TOTAL_AMOUNT")
	public long ouenFrame47TotalAmount;
	@Column(name = "FRAME48_TOTAL_TIME")
	public int ouenFrame48TotalTime;
	@Column(name = "FRAME48_TOTAL_AMOUNT")
	public long ouenFrame48TotalAmount;
	@Column(name = "FRAME49_TOTAL_TIME")
	public int ouenFrame49TotalTime;
	@Column(name = "FRAME49_TOTAL_AMOUNT")
	public long ouenFrame49TotalAmount;
	@Column(name = "FRAME50_TOTAL_TIME")
	public int ouenFrame50TotalTime;
	@Column(name = "FRAME50_TOTAL_AMOUNT")
	public long ouenFrame50TotalAmount;
	@Column(name = "FRAME51_TOTAL_TIME")
	public int ouenFrame51TotalTime;
	@Column(name = "FRAME51_TOTAL_AMOUNT")
	public long ouenFrame51TotalAmount;
	@Column(name = "FRAME52_TOTAL_TIME")
	public int ouenFrame52TotalTime;
	@Column(name = "FRAME52_TOTAL_AMOUNT")
	public long ouenFrame52TotalAmount;
	@Column(name = "FRAME53_TOTAL_TIME")
	public int ouenFrame53TotalTime;
	@Column(name = "FRAME53_TOTAL_AMOUNT")
	public long ouenFrame53TotalAmount;
	@Column(name = "FRAME54_TOTAL_TIME")
	public int ouenFrame54TotalTime;
	@Column(name = "FRAME54_TOTAL_AMOUNT")
	public long ouenFrame54TotalAmount;
	@Column(name = "FRAME55_TOTAL_TIME")
	public int ouenFrame55TotalTime;
	@Column(name = "FRAME55_TOTAL_AMOUNT")
	public long ouenFrame55TotalAmount;
	@Column(name = "FRAME56_TOTAL_TIME")
	public int ouenFrame56TotalTime;
	@Column(name = "FRAME56_TOTAL_AMOUNT")
	public long ouenFrame56TotalAmount;
	@Column(name = "FRAME57_TOTAL_TIME")
	public int ouenFrame57TotalTime;
	@Column(name = "FRAME57_TOTAL_AMOUNT")
	public long ouenFrame57TotalAmount;
	@Column(name = "FRAME58_TOTAL_TIME")
	public int ouenFrame58TotalTime;
	@Column(name = "FRAME58_TOTAL_AMOUNT")
	public long ouenFrame58TotalAmount;
	@Column(name = "FRAME59_TOTAL_TIME")
	public int ouenFrame59TotalTime;
	@Column(name = "FRAME59_TOTAL_AMOUNT")
	public long ouenFrame59TotalAmount;
	@Column(name = "FRAME60_TOTAL_TIME")
	public int ouenFrame60TotalTime;
	@Column(name = "FRAME60_TOTAL_AMOUNT")
	public long ouenFrame60TotalAmount;
	@Column(name = "FRAME61_TOTAL_TIME")
	public int ouenFrame61TotalTime;
	@Column(name = "FRAME61_TOTAL_AMOUNT")
	public long ouenFrame61TotalAmount;
	@Column(name = "FRAME62_TOTAL_TIME")
	public int ouenFrame62TotalTime;
	@Column(name = "FRAME62_TOTAL_AMOUNT")
	public long ouenFrame62TotalAmount;
	@Column(name = "FRAME63_TOTAL_TIME")
	public int ouenFrame63TotalTime;
	@Column(name = "FRAME63_TOTAL_AMOUNT")
	public long ouenFrame63TotalAmount;
	@Column(name = "FRAME64_TOTAL_TIME")
	public int ouenFrame64TotalTime;
	@Column(name = "FRAME64_TOTAL_AMOUNT")
	public long ouenFrame64TotalAmount;
	@Column(name = "FRAME65_TOTAL_TIME")
	public int ouenFrame65TotalTime;
	@Column(name = "FRAME65_TOTAL_AMOUNT")
	public long ouenFrame65TotalAmount;
	@Column(name = "FRAME66_TOTAL_TIME")
	public int ouenFrame66TotalTime;
	@Column(name = "FRAME66_TOTAL_AMOUNT")
	public long ouenFrame66TotalAmount;
	@Column(name = "FRAME67_TOTAL_TIME")
	public int ouenFrame67TotalTime;
	@Column(name = "FRAME67_TOTAL_AMOUNT")
	public long ouenFrame67TotalAmount;
	@Column(name = "FRAME68_TOTAL_TIME")
	public int ouenFrame68TotalTime;
	@Column(name = "FRAME68_TOTAL_AMOUNT")
	public long ouenFrame68TotalAmount;
	@Column(name = "FRAME69_TOTAL_TIME")
	public int ouenFrame69TotalTime;
	@Column(name = "FRAME69_TOTAL_AMOUNT")
	public long ouenFrame69TotalAmount;
	@Column(name = "FRAME70_TOTAL_TIME")
	public int ouenFrame70TotalTime;
	@Column(name = "FRAME70_TOTAL_AMOUNT")
	public long ouenFrame70TotalAmount;
	@Column(name = "FRAME71_TOTAL_TIME")
	public int ouenFrame71TotalTime;
	@Column(name = "FRAME71_TOTAL_AMOUNT")
	public long ouenFrame71TotalAmount;
	@Column(name = "FRAME72_TOTAL_TIME")
	public int ouenFrame72TotalTime;
	@Column(name = "FRAME72_TOTAL_AMOUNT")
	public long ouenFrame72TotalAmount;
	@Column(name = "FRAME73_TOTAL_TIME")
	public int ouenFrame73TotalTime;
	@Column(name = "FRAME73_TOTAL_AMOUNT")
	public long ouenFrame73TotalAmount;
	@Column(name = "FRAME74_TOTAL_TIME")
	public int ouenFrame74TotalTime;
	@Column(name = "FRAME74_TOTAL_AMOUNT")
	public long ouenFrame74TotalAmount;
	@Column(name = "FRAME75_TOTAL_TIME")
	public int ouenFrame75TotalTime;
	@Column(name = "FRAME75_TOTAL_AMOUNT")
	public long ouenFrame75TotalAmount;
	@Column(name = "FRAME76_TOTAL_TIME")
	public int ouenFrame76TotalTime;
	@Column(name = "FRAME76_TOTAL_AMOUNT")
	public long ouenFrame76TotalAmount;
	@Column(name = "FRAME77_TOTAL_TIME")
	public int ouenFrame77TotalTime;
	@Column(name = "FRAME77_TOTAL_AMOUNT")
	public long ouenFrame77TotalAmount;
	@Column(name = "FRAME78_TOTAL_TIME")
	public int ouenFrame78TotalTime;
	@Column(name = "FRAME78_TOTAL_AMOUNT")
	public long ouenFrame78TotalAmount;
	@Column(name = "FRAME79_TOTAL_TIME")
	public int ouenFrame79TotalTime;
	@Column(name = "FRAME79_TOTAL_AMOUNT")
	public long ouenFrame79TotalAmount;
	@Column(name = "FRAME80_TOTAL_TIME")
	public int ouenFrame80TotalTime;
	@Column(name = "FRAME80_TOTAL_AMOUNT")
	public long ouenFrame80TotalAmount;
	@Column(name = "FRAME81_TOTAL_TIME")
	public int ouenFrame81TotalTime;
	@Column(name = "FRAME81_TOTAL_AMOUNT")
	public long ouenFrame81TotalAmount;
	@Column(name = "FRAME82_TOTAL_TIME")
	public int ouenFrame82TotalTime;
	@Column(name = "FRAME82_TOTAL_AMOUNT")
	public long ouenFrame82TotalAmount;
	@Column(name = "FRAME83_TOTAL_TIME")
	public int ouenFrame83TotalTime;
	@Column(name = "FRAME83_TOTAL_AMOUNT")
	public long ouenFrame83TotalAmount;
	@Column(name = "FRAME84_TOTAL_TIME")
	public int ouenFrame84TotalTime;
	@Column(name = "FRAME84_TOTAL_AMOUNT")
	public long ouenFrame84TotalAmount;
	@Column(name = "FRAME85_TOTAL_TIME")
	public int ouenFrame85TotalTime;
	@Column(name = "FRAME85_TOTAL_AMOUNT")
	public long ouenFrame85TotalAmount;
	@Column(name = "FRAME86_TOTAL_TIME")
	public int ouenFrame86TotalTime;
	@Column(name = "FRAME86_TOTAL_AMOUNT")
	public long ouenFrame86TotalAmount;
	@Column(name = "FRAME87_TOTAL_TIME")
	public int ouenFrame87TotalTime;
	@Column(name = "FRAME87_TOTAL_AMOUNT")
	public long ouenFrame87TotalAmount;
	@Column(name = "FRAME88_TOTAL_TIME")
	public int ouenFrame88TotalTime;
	@Column(name = "FRAME88_TOTAL_AMOUNT")
	public long ouenFrame88TotalAmount;
	@Column(name = "FRAME89_TOTAL_TIME")
	public int ouenFrame89TotalTime;
	@Column(name = "FRAME89_TOTAL_AMOUNT")
	public long ouenFrame89TotalAmount;
	@Column(name = "FRAME90_TOTAL_TIME")
	public int ouenFrame90TotalTime;
	@Column(name = "FRAME90_TOTAL_AMOUNT")
	public long ouenFrame90TotalAmount;
	@Column(name = "FRAME91_TOTAL_TIME")
	public int ouenFrame91TotalTime;
	@Column(name = "FRAME91_TOTAL_AMOUNT")
	public long ouenFrame91TotalAmount;
	@Column(name = "FRAME92_TOTAL_TIME")
	public int ouenFrame92TotalTime;
	@Column(name = "FRAME92_TOTAL_AMOUNT")
	public long ouenFrame92TotalAmount;
	@Column(name = "FRAME93_TOTAL_TIME")
	public int ouenFrame93TotalTime;
	@Column(name = "FRAME93_TOTAL_AMOUNT")
	public long ouenFrame93TotalAmount;
	@Column(name = "FRAME94_TOTAL_TIME")
	public int ouenFrame94TotalTime;
	@Column(name = "FRAME94_TOTAL_AMOUNT")
	public long ouenFrame94TotalAmount;
	@Column(name = "FRAME95_TOTAL_TIME")
	public int ouenFrame95TotalTime;
	@Column(name = "FRAME95_TOTAL_AMOUNT")
	public long ouenFrame95TotalAmount;
	@Column(name = "FRAME96_TOTAL_TIME")
	public int ouenFrame96TotalTime;
	@Column(name = "FRAME96_TOTAL_AMOUNT")
	public long ouenFrame96TotalAmount;
	@Column(name = "FRAME97_TOTAL_TIME")
	public int ouenFrame97TotalTime;
	@Column(name = "FRAME97_TOTAL_AMOUNT")
	public long ouenFrame97TotalAmount;
	@Column(name = "FRAME98_TOTAL_TIME")
	public int ouenFrame98TotalTime;
	@Column(name = "FRAME98_TOTAL_AMOUNT")
	public long ouenFrame98TotalAmount;
	@Column(name = "FRAME99_TOTAL_TIME")
	public int ouenFrame99TotalTime;
	@Column(name = "FRAME99_TOTAL_AMOUNT")
	public long ouenFrame99TotalAmount;
	
	public OuenTimeOfMonthly convertToOuen() {
		
		OuenTimeOfMonthly ouen = OuenTimeOfMonthly.create(
				OuenWorkAggregateDetail.create(
						new AttendanceTimeMonth(ouenOtherTotalTime), 
						new AttendanceAmountMonth(ouenOtherTotalAmount)));

		for (int i = 1; i <= 99; i++) {
			
			setOuenFrame(ouen, i);
		}
		
		return ouen;
	}
	
	public void reset() {
		ouenOtherTotalTime = 0;
		ouenOtherTotalAmount = 0L;
		for (int i = 1; i <= 99; i++) {
			
			setOuen(OuenWorkAggregateFrameDetail.create(i, OuenWorkAggregateDetail.empty()));
		}
	}
	
	private void setOuenFrame(OuenTimeOfMonthly ouen, int frame) {
		int time = ReflectionUtil.getFieldValue(
							FieldReflection.getField(getClass(), "ouenFrame" + frame + "TotalTime"), 
							this);

		long amount = ReflectionUtil.getFieldValue(
				FieldReflection.getField(getClass(), "ouenFrame" + frame + "TotalAmount"), 
				this);
		
		ouen.add(OuenWorkAggregateFrameDetail.create(
				frame, 
				OuenWorkAggregateDetail.create(
					new AttendanceTimeMonth(time), 
					new AttendanceAmountMonth(amount))));

	}
	
	public void setOuen(OuenTimeOfMonthly ouen) {
		
		ouenOtherTotalTime = ouen.getOther().getTotalTime().valueAsMinutes();
		ouenOtherTotalAmount = ouen.getOther().getTotalAmount().v();
		
		ouen.getFrames().stream().forEach(frame -> {
			
			setOuen(frame);
		});
	}
	
	private void setOuen(OuenWorkAggregateFrameDetail ouen) {
		ReflectionUtil.setFieldValue(
					FieldReflection.getField(getClass(), "ouenFrame" + ouen.getFrameNo() + "TotalTime"), 
					this,
					ouen.getDetail().getTotalTime().v());
		ReflectionUtil.setFieldValue(
					FieldReflection.getField(getClass(), "ouenFrame" + ouen.getFrameNo() + "TotalAmount"), 
					this,
					ouen.getDetail().getTotalAmount().v());
	}
	
	@Override
	protected Object getKey() {
		return id;
	}

}
