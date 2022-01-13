package nts.uk.ctx.at.schedule.infra.entity.displaysetting.authcontrol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyFuncByWorkplace;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * KSCMT_FUNC_BYWKP : 	スケジュール修正職場別の機能
 *
 * @author viet.tx
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSCCT_FUNC_BYWKP")
public class KscmtFuncBywkp extends UkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final JpaEntityMapper<KscmtFuncBywkp> MAPPER = new JpaEntityMapper<>(KscmtFuncBywkp.class);

    /**
     * 利用可否権限の機能NO
     */
    @Id
    @Column(name = "FUNCTION_NO")
    public int functionNo;

    /**
     * 表示名
     */
    @Column(name = "NAME")
    public String name;

    /**
     * 説明文
     */
    @Column(name = "DESCRIPTION")
    public String description;

    /**
     * 表示順
     */
    @Column(name = "DISPLAY_ORDER")
    public int deisplayOrder;

    /**
     * 初期値
     */
    @Column(name = "INITIAL_VALUE")
    public int initialValue;

    @Override
    protected Object getKey() {
        return this.functionNo;
    }

    /**
     * Convert to domain
     *
     * @return
     */
    public ScheModifyFuncByWorkplace toDomain() {
        return new ScheModifyFuncByWorkplace(
                this.functionNo
                , this.name
                , this.description
                , this.deisplayOrder
                , this.initialValue == 1
        );
    }
}
