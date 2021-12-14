package nts.uk.ctx.at.record.infra.entity.reservation.bento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.reservation.bento.*;
import nts.uk.ctx.at.record.dom.reservation.bentomenu.closingtime.ReservationClosingTimeFrame;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KRCDT_RESERVATION")
@AllArgsConstructor
@NoArgsConstructor
public class KrcdtReservation extends ContractUkJpaEntity {
	
	@EmbeddedId
	public KrcdtReservationPK pk;

	@Column(name = "RESERVATION_YMD")
	public GeneralDate date;
	
	@Column(name = "RESERVATION_FRAME")
	public int frameAtr;
	
	@Column(name = "CARD_NO")
	public String cardNo;
	
	@Column(name = "ORDERED")
	public int ordered;

	@Column(name = "WORK_LOCATION_CD")
	public String workLocationCode;
	
	@OneToMany(targetEntity = KrcdtReservationDetail.class, mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "KRCDT_RESERVATION_DETAIL")
	public List<KrcdtReservationDetail> reservationDetails;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	public BentoReservation toDomain() {
		return new BentoReservation(
				new ReservationRegisterInfo(cardNo), 
				new ReservationDate(date, EnumAdaptor.valueOf(frameAtr, ReservationClosingTimeFrame.class)), 
				ordered == 0 ? false : true,
				Optional.ofNullable(workLocationCode == null? null: new WorkLocationCode(workLocationCode)),
				reservationDetails.stream().map(x -> x.toDomain()).collect(Collectors.toList()));
	}
	
	public static KrcdtReservation fromDomain(BentoReservation bentoReservation) {
		String reservationId = IdentifierUtil.randomUniqueId();
		return new KrcdtReservation(
				new KrcdtReservationPK(
						AppContexts.user().companyId(), reservationId
						), 
				bentoReservation.getReservationDate().getDate(), 
				bentoReservation.getReservationDate().getClosingTimeFrame().value, 
				bentoReservation.getRegisterInfor().getReservationCardNo(), 
				bentoReservation.isOrdered() ? 1 : 0 ,
				bentoReservation.getWorkLocationCode().isPresent()?
						bentoReservation.getWorkLocationCode().get().v(): null ,
				bentoReservation.getBentoReservationDetails().stream().map(x -> KrcdtReservationDetail.fromDomain(x, reservationId)).collect(Collectors.toList()));
	}

	public KrcdtReservation updateFromDomain(BentoReservation bentoReservation){
		this.date = bentoReservation.getReservationDate().getDate();
		this.frameAtr = bentoReservation.getReservationDate().getClosingTimeFrame().value;
		this.cardNo = bentoReservation.getRegisterInfor().getReservationCardNo();
		this.ordered = bentoReservation.isOrdered() ? 1 : 0;
		this.workLocationCode = bentoReservation.getWorkLocationCode().isPresent()?
				bentoReservation.getWorkLocationCode().get().v(): null;

		List<KrcdtReservationDetail> reservationDetailRemove = new ArrayList<>();
		for(KrcdtReservationDetail detail : this.reservationDetails){
			Optional<BentoReservationDetail> domainOpt = bentoReservation.getBentoReservationDetails()
					.stream().filter(x -> x.getFrameNo() == detail.pk.frameNo).findFirst();
			if (domainOpt.isPresent()){
				BentoReservationDetail domain = domainOpt.get();
				detail.quantity = domain.getBentoCount().v();
				detail.autoReservation = domain.isAutoReservation() ? 1 : 0;
			}else{
				reservationDetailRemove.add(detail);
			}
		}

		this.reservationDetails.removeAll(reservationDetailRemove);

		for (BentoReservationDetail domain : bentoReservation.getBentoReservationDetails()){
			Optional<KrcdtReservationDetail> entityOpt = this.reservationDetails.stream()
					.filter(x -> x.pk.frameNo == domain.getFrameNo()).findFirst();
			if (!entityOpt.isPresent()){
				this.reservationDetails.add(KrcdtReservationDetail.fromDomain(domain, this.pk.reservationID));
			}
		}

		return this;
	}
}
