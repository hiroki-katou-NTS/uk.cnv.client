package nts.uk.ctx.at.shared.dom.remainingnumber.paymana;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.UnbalanceCompensation;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.CompensatoryDayoffDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.HolidayAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataDaysAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.AccuVacationBuilder;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.NumberConsecuVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;

/**
 * 振出管理データ
 * 
 * @author HopNT
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PayoutManagementData extends AggregateRoot {

	// 振出データID
	private String payoutId;

	private String cID;

	// 社員ID
	private String sID;

	// 振出日
	private CompensatoryDayoffDate payoutDate;

	// 使用期限日
	private GeneralDate expiredDate;

	// 法定内外区分
	private HolidayAtr lawAtr;

	// 発生日数
	private ManagementDataDaysAtr occurredDays;

	// 未使用日数
	private ManagementDataRemainUnit unUsedDays;

	// 振休消化区分
	private DigestionAtr stateAtr;

	// 消滅日
	public Optional<GeneralDate> disapearDate = Optional.empty(); // SONNLB sửa đoạn này là lỗi của 3SI, vì làm
																	// exception chết màn KAF011 ngày 19/04

	public PayoutManagementData(String payoutId, String cid, String sid, boolean unknowDate, GeneralDate dayoffDate,
			GeneralDate expiredDate, int lawId, Double occurredDays, Double unUsedDays, int stateAtr) {
		this.payoutId = payoutId;
		this.cID = cid;
		this.sID = sid;
		this.payoutDate = new CompensatoryDayoffDate(unknowDate, Optional.ofNullable(dayoffDate));
		this.expiredDate = expiredDate;
		this.lawAtr = EnumAdaptor.valueOf(lawId, HolidayAtr.class);
		this.occurredDays = new ManagementDataDaysAtr(occurredDays);
		this.unUsedDays = new ManagementDataRemainUnit(unUsedDays);
		this.stateAtr = EnumAdaptor.valueOf(stateAtr, DigestionAtr.class);
	}

	public PayoutManagementData(String payoutId, String cid, String sid, boolean unknowDate, GeneralDate dayoffDate,
			GeneralDate expiredDate, int lawId, Double occurredDays, Double unUsedDays, int stateAtr,
			GeneralDate disapearDate) {
		this.payoutId = payoutId;
		this.cID = cid;
		this.sID = sid;
		this.payoutDate = new CompensatoryDayoffDate(unknowDate, Optional.ofNullable(dayoffDate));
		this.expiredDate = expiredDate;
		this.lawAtr = EnumAdaptor.valueOf(lawId, HolidayAtr.class);
		this.occurredDays = new ManagementDataDaysAtr(occurredDays);
		this.unUsedDays = new ManagementDataRemainUnit(unUsedDays);
		this.stateAtr = EnumAdaptor.valueOf(stateAtr, DigestionAtr.class);
		this.disapearDate = Optional.ofNullable(disapearDate);
	}

	public void setRemainNumber(Double remain) {
		this.unUsedDays = new ManagementDataRemainUnit(remain);
	}

	public void setRemainNumberToFree(Double remain) {
		this.unUsedDays = new ManagementDataRemainUnit(this.unUsedDays.v() + remain);
	}

	public void setStateAtr(int stateAtr) {
		this.stateAtr = EnumAdaptor.valueOf(stateAtr, DigestionAtr.class);
	}

	public void update(UnbalanceCompensation data) {
		this.expiredDate = data.getDeadline();
		this.lawAtr = data.getLegalInExClassi();
		this.occurredDays = new ManagementDataDaysAtr(data.getNumberOccurren().getDay().v());
		this.unUsedDays = new ManagementDataRemainUnit(data.getUnbalanceNumber().getDay().v());
		this.stateAtr = data.getDigestionCate();
		this.disapearDate = data.getExtinctionDate();
	}

	public static PayoutManagementData create(String cid, UnbalanceCompensation payout) {

		PayoutManagementData domain = new PayoutManagementData();
		domain.cID = cid;
		domain.sID = payout.getEmployeeId();
		domain.payoutId = payout.getManageId();
		domain.payoutDate = new CompensatoryDayoffDate(payout.getDateOccur().isUnknownDate(),
				payout.getDateOccur().getDayoffDate());
		domain.update(payout);

		return domain;
	}

	// [1] 振出の未相殺に変換する
	public AccumulationAbsenceDetail convertSeqVacationState() {
		val common = new AccuVacationBuilder(this.sID, this.payoutDate, OccurrenceDigClass.OCCURRENCE,
				CreateAtr.FLEXCOMPEN.convertToMngData(true), this.payoutId)
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(this.occurredDays.v()),
								Optional.empty()))
						.unbalanceNumber(new NumberConsecuVacation(this.unUsedDays, Optional.empty())).build();
		return new UnbalanceCompensation(common, this.getExpiredDate(), this.getStateAtr(), this.getDisapearDate(),
				EnumAdaptor.valueOf(this.getLawAtr().value, HolidayAtr.class));
	}

	// 2] 残数が残っている
	public boolean isRemaing() {
		return this.stateAtr == DigestionAtr.UNUSED;
	}
}
