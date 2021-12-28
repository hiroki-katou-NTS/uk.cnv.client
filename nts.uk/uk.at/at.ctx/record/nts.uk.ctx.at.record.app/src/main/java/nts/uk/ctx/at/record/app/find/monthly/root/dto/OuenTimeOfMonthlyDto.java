package nts.uk.ctx.at.record.app.find.monthly.root.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate.PropType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.OuenTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.OuenWorkAggregateDetail;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.OuenWorkAggregateFrameDetail;

@Getter	
@Setter
@NoArgsConstructor
@AllArgsConstructor
/** 月別実績の応援時間 */
public class OuenTimeOfMonthlyDto implements ItemConst, AttendanceItemDataGate {

	/** 集計枠集計明細: 作業集計枠明細 */
	@AttendanceItemLayout(jpPropertyName = FRAME, layout = LAYOUT_A, listMaxLength = 99, indexField = DEFAULT_INDEX_FIELD_NAME)
	private List<OuenWorkAggregateFrameDetailDto> frames;

	/** その他集計明細: 応援作業集計明細  */
	@AttendanceItemLayout(jpPropertyName = OTHER, layout = LAYOUT_B)
	private OuenWorkAggregateDetailDto other;
	
	public static OuenTimeOfMonthlyDto from(OuenTimeOfMonthly domain) {
		
		return new OuenTimeOfMonthlyDto(
				ConvertHelper.mapTo(domain.getFrames(), f -> OuenWorkAggregateFrameDetailDto.from(f)),
				OuenWorkAggregateDetailDto.from(domain.getOther()));
	}
	
	public OuenTimeOfMonthly domain() {

		OuenTimeOfMonthly domain = OuenTimeOfMonthly.create(other == null 
					? OuenWorkAggregateDetail.empty() : other.domain()); 
		
		if (frames != null) {
			frames.stream().forEach(f -> {
				domain.add(f.domain());
			});
		}
		
		return domain;
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case FRAME:
			return PropType.IDX_LIST;
		default:
			return PropType.OBJECT;
		}
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case FRAME:
			return new OuenWorkAggregateFrameDetailDto();
		case OTHER:
			return new OuenWorkAggregateDetailDto();
		default:
			break;
		}
		return null;
	}
	
	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case OTHER:
			return Optional.ofNullable(this.other);
		default:
			return Optional.empty();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> List<T> gets(String path) {
		switch (path) {
		case FRAME:
			return (List<T>) this.frames;
		default:
			return new ArrayList<>();
		}
	}

	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case OTHER:
			this.other = (OuenWorkAggregateDetailDto) value;
			break;
		default:
			break;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> void set(String path, List<T> value) {
		switch (path) {
		case FRAME:
			this.frames = (List<OuenWorkAggregateFrameDetailDto>) value;
			break;
		default:
			break;
		}
	}
	
	@Override
	public int size(String path) {
		switch (path) {
		case FRAME:
			return 99;
		default:
			break;
		}
		return AttendanceItemDataGate.super.size(path);
	}
	

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	/** 応援作業集計明細 */
	public static class OuenWorkAggregateDetailDto implements AttendanceItemDataGate {
		
		/**　総集計時間: 勤怠月間時間　*/
		@AttendanceItemValue(type = ValueType.TIME)
		@AttendanceItemLayout(jpPropertyName = TIME, layout = LAYOUT_A)
		private int time;
		
		/**　合計金額: 勤怠月間金額　*/
		@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
		@AttendanceItemLayout(jpPropertyName = AMOUNT, layout = LAYOUT_B)
		private long amount;
		
		public static OuenWorkAggregateDetailDto from(OuenWorkAggregateDetail domain) {
			
			return new OuenWorkAggregateDetailDto(
					domain.getTotalTime().valueAsMinutes(), 
					domain.getTotalAmount().v());
		}
		
		public OuenWorkAggregateDetail domain() {
			
			return OuenWorkAggregateDetail.create(
					new AttendanceTimeMonth(time), 
					new AttendanceAmountMonth(amount));
		}
		
		@Override
		public Optional<ItemValue> valueOf(String path) {
			switch (path) {
			case TIME:
				return Optional.of(ItemValue.builder().value(time).valueType(ValueType.TIME));
			case AMOUNT:
				return Optional.of(ItemValue.builder().value(amount).valueType(ValueType.AMOUNT_NUM));
			default:
				return Optional.empty();
			}
		}

		@Override
		public void set(String path, ItemValue value) {
			switch (path) {
			case TIME:
				this.time = value.valueOrDefault(0);
				break;
			case AMOUNT:
				this.amount = value.valueOrDefault(0);
				break;
			default:
				break;
			}
		}
		
		@Override
		public PropType typeOf(String path) {
			switch (path) {
			case TIME:
			case AMOUNT:
				return PropType.VALUE;
			default:
				return PropType.OBJECT;
			}
		}
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	/** 作業集計枠明細 */
	public static class OuenWorkAggregateFrameDetailDto implements AttendanceItemDataGate {

		/**　集計枠No: int　*/
		private int no;

		/**　集計明細: 応援作業集計明細　*/
		@AttendanceItemLayout(jpPropertyName = DETAIL, layout = LAYOUT_A)
		private OuenWorkAggregateDetailDto detail;
		
		public static OuenWorkAggregateFrameDetailDto from(OuenWorkAggregateFrameDetail domain) {
			
			return new OuenWorkAggregateFrameDetailDto(domain.getFrameNo(), 
						OuenWorkAggregateDetailDto.from(domain.getDetail()));
		}
		
		public OuenWorkAggregateFrameDetail domain() {
			
			return OuenWorkAggregateFrameDetail.create(no, 
					detail == null ? OuenWorkAggregateDetail.empty() : detail.domain());
		}
		
		@Override
		public PropType typeOf(String path) {
			return PropType.OBJECT;
		}
		
		@Override
		public AttendanceItemDataGate newInstanceOf(String path) {
			switch (path) {
			case DETAIL:
				return new OuenWorkAggregateDetailDto();
			default:
				break;
			}
			return null;
		}
		
		@Override
		public Optional<AttendanceItemDataGate> get(String path) {
			switch (path) {
			case DETAIL:
				return Optional.ofNullable(this.detail);
			default:
				return Optional.empty();
			}
		}
		
		@Override
		public void set(String path, AttendanceItemDataGate value) {
			switch (path) {
			case DETAIL:
				this.detail = (OuenWorkAggregateDetailDto) value;
				break;
			default:
				break;
			}
		}
	}
	

}
