package nts.uk.ctx.at.record.dom.stampmanagement.workplace;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.shr.com.net.Ipv4Address;

/**
 * AG: 勤務場所
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.打刻管理.勤務場所.勤務場所
 * @author hieult
 *
 */
@Getter
public class WorkLocation extends AggregateRoot {
	
	/** 契約コード */
	private final ContractCode contractCode;
	
	/** コード */
	private final WorkLocationCD workLocationCD;    

	/** 名称 */
	private WorkLocationName workLocationName; 
	
	/** 打刻範囲 */
	private StampMobilePossibleRange stampRange;
	
	/** IPアドレス一覧*/
	private List<Ipv4Address> listIPAddress;
	
	/** 職場*/
	@Setter
	private Optional<WorkplacePossible> workplace;
	
	/**
	 * 	[1] 携帯打刻で打刻してもいい位置か判断する（地理座標input）
	 * @param geoCoordinate
	 * @return
	 */
	public boolean canStamptedByMobile(GeoCoordinate geoCoordinate) {
		return this.stampRange.checkWithinStampRange(geoCoordinate);
	}

	public WorkLocation(ContractCode contractCode, WorkLocationCD workLocationCD, WorkLocationName workLocationName,
			StampMobilePossibleRange stampRange, List<Ipv4Address> listIPAddress, Optional<WorkplacePossible> workplace) {
		super();
		this.contractCode = contractCode;
		this.workLocationCD = workLocationCD;
		this.workLocationName = workLocationName;
		this.stampRange = stampRange;
		this.listIPAddress = listIPAddress;
		this.workplace = workplace;
	}
}
