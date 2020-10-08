package nts.uk.ctx.at.function.infra.entity.outputitemsofannualworkledger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class DisplayContentsOfAnnualWorkLedgerPk implements Serializable {
    public static final long serialVersionUID = 1L;

    // 	設定ID
    @Column(name = "ID")
    public int iD;

    // 	出力順位-> 出力項目.順位
    @Column(name = "ITEM_POS")
    public int itemPos;

    // 	勤怠項目ID->出力項目詳細の選択勤怠項目.勤怠項目ID
    @Column(name = "ATTENDANCE_ITEM_ID")
    public int attendanceId;
}
