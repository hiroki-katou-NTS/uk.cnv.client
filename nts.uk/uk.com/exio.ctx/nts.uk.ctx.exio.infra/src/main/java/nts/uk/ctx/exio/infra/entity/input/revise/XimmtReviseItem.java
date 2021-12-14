package nts.uk.ctx.exio.infra.entity.input.revise;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.uk.ctx.exio.dom.input.importableitem.ItemType;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItem;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseValue;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.codeconvert.ExternalImportCodeConvert;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.date.DateRevise;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.date.ExternalImportDateFormat;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.integer.IntegerRevise;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.real.DecimalDigitNumber;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.real.RealRevise;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.string.Padding;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.string.PaddingLength;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.string.PaddingMethod;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.string.StringRevise;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time.HourlySegment;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time.TimeBase10Rounding;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time.TimeBase60Delimiter;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time.TimeBaseNumber;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time.TimeRevise;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *  項目の編集
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "XIMMT_REVISE_ITEM")
public class XimmtReviseItem extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private XimmtReviseItemPK pk;
	
	/**
	 * 項目型は本来ReviseItemには含まれていないが、ドメインへの復元時に型を区別するのに必要。
	 */
	@Column(name = "ITEM_TYPE")
	private int itemType;
	
	/*  */
	@Column(name = "IS_DECIMALIZATION")
	private Integer isDecimalization;
	
	/*  */
	@Column(name = "DECIMAL_DEGIT_NUMBER")
	private Integer decimalLength;
	
	/*  */
	@Column(name = "USE_PADDING")
	private Integer usePadding;
	
	/*  */
	@Column(name = "PADDING_LENGTH")
	private Integer paddingLength;
	
	/*  */
	@Column(name = "PADDING_METHOD")
	private Integer paddingMethod;
	
	/*  */
	@Column(name = "DATE_FORMAT")
	private Integer dateFormat;
	
	/*  */
	@Column(name = "HOURLY_SEGMENT")
	private Integer hourly;
	
	/*  */
	@Column(name = "TIME_BASE_NUMBER")
	private Integer baseNumber;
	
	/*  */
	@Column(name = "TIME_BASE60_DELIMITER")
	private Integer delimiter;
	
	/*  */
	@Column(name = "TIME_ROUNDING")
	private Integer rounding;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static final JpaEntityMapper<XimmtReviseItem> MAPPER = new JpaEntityMapper<>(XimmtReviseItem.class);
	
	public static XimmtReviseItem toEntity(ReviseItem domain) {
		
		val entity = new XimmtReviseItem();
		
		entity.pk = XimmtReviseItemPK.of(domain);
		toEntity(domain.getRevisingValue(), entity);
		
		return entity;
	}
	
	private static void toEntity(ReviseValue reviseValue, XimmtReviseItem entity) {

		if (reviseValue instanceof StringRevise) {
			val r = (StringRevise) reviseValue;
			
			entity.itemType = ItemType.STRING.value;
			entity.usePadding = r.isUsePadding() ? 1 : 0;
			r.getPadding().ifPresent(p -> {
				entity.paddingLength = p.getLength().v();
				entity.paddingMethod = p.getMethod().value;
			});
		}
		
		else if (reviseValue instanceof IntegerRevise) {
			entity.itemType = ItemType.INT.value;
		}
		
		else if (reviseValue instanceof RealRevise) {
			val r = (RealRevise) reviseValue;
			
			entity.itemType = ItemType.REAL.value;
			entity.isDecimalization = r.isDecimalization() ? 1 : 0;
			entity.decimalLength = r.getLength().map(l -> l.v()).orElse(null);
		}
		
		else if (reviseValue instanceof DateRevise) {
			val r = (DateRevise) reviseValue;
			
			entity.itemType = ItemType.DATE.value;
			entity.dateFormat = r.getDateFormat().value;
		}
		
		else if (reviseValue instanceof TimeRevise) {
			val r = (TimeRevise) reviseValue;
			
			// TIME_POINTでもDURATIONでもどちらでも良い。どちらにせよTimeReviseとしてtoDomainされるので問題ない。
			entity.itemType = ItemType.TIME_DURATION.value;
			
			entity.hourly = r.getHourly().value;
			entity.baseNumber = r.getBaseNumber().map(e -> e.value).orElse(null);
			entity.delimiter = r.getDelimiter().map(e -> e.value).orElse(null);
			entity.rounding = r.getRounding().map(e -> e.value).orElse(null);
		}
		
		else {
			throw new RuntimeException("unknown: " + reviseValue);
		}
	}
	
	public ReviseItem toDomain(Optional<ExternalImportCodeConvert> codeConvert) {
		return new ReviseItem(
				pk.getCompanyId(),
				new ExternalImportCode(pk.getSettingCode()),
				pk.getItemNo(),
				getReviseValue(codeConvert));
	}
	
	private ReviseValue getReviseValue(Optional<ExternalImportCodeConvert> codeConvert) {
		switch(EnumAdaptor.valueOf(itemType, ItemType.class)) {
			case STRING:
				return new StringRevise(
						usePadding == 1,
						Optional.ofNullable(usePadding == 1 ? new Padding(
								new PaddingLength(paddingLength),
								EnumAdaptor.valueOf(paddingMethod, PaddingMethod.class)) : null),
						codeConvert);
			case INT:
				return new IntegerRevise(
						codeConvert);
			case REAL:
				return new RealRevise(
						BooleanUtils.toBoolean(isDecimalization),
						Optional.ofNullable(isDecimalization == 1 ? new DecimalDigitNumber(decimalLength) : null));
			case DATE:
				return new DateRevise(
						EnumAdaptor.valueOf(dateFormat, ExternalImportDateFormat.class));
			case TIME_POINT:
			case TIME_DURATION:
				return new TimeRevise(
						EnumAdaptor.valueOf(hourly, HourlySegment.class),
						EnumAdaptor.optionalOf(baseNumber, TimeBaseNumber.class),
						EnumAdaptor.optionalOf(delimiter, TimeBase60Delimiter.class),
						EnumAdaptor.optionalOf(rounding, TimeBase10Rounding.class));
			default:
				throw new RuntimeException("項目型に対する実装が存在しません。:" + EnumAdaptor.valueOf(itemType, ItemType.class));
		}
	}
}
