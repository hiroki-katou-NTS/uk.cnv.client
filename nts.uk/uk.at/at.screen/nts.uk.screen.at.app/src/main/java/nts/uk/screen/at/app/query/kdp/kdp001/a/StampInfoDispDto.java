package nts.uk.screen.at.app.query.kdp.kdp001.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampInfoDisp;

@Getter
@AllArgsConstructor
public class StampInfoDispDto {

	/**
	 * 打刻カード番号
	 */
	@Getter
	private final String stampNumber;

	/**
	 * 打刻日時
	 */
	@Getter
	private final GeneralDateTime stampDatetime;

	@Getter
	private final String stampStringDatetime;

	/**
	 * 打刻区分
	 */
	@Getter
	private final String stampAtr;

	/**
	 * 打刻
	 */
	@Getter
	private final StampInfoDto stamp;

	public static StampInfoDispDto fromDomain(StampInfoDisp domain) {

		return new StampInfoDispDto(domain.getStampNumber() != null ? domain.getStampNumber().v() : null,
				domain.getStampDatetime(), domain.getStampDatetime().toString("yyyy/MM/dd HH:mm:ss"),
				domain.getStampAtr(),
				domain.getStamp().isPresent() ? StampInfoDto.fromDomain(domain.getStamp().get()) : null);
	}
}
