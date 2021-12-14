package nts.uk.ctx.sys.env.infra.entity.mailnoticeset;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionId;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionName;
import nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionGetMemento;
import nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionSetMemento;
import nts.uk.ctx.sys.env.dom.mailnoticeset.SortOrder;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "SEVMT_MAIL_FUNCTION")
@Getter
@Setter
public class SevmtMailFunction extends ContractUkJpaEntity implements Serializable, MailFunctionSetMemento, MailFunctionGetMemento {
    private static final long serialVersionUID = 1L;
   
    @Column(name = "EXCLUS_VER")
    private int exclusVer;
    
    @Id
    @Column(name = "FUNCTION_ID")
    private Integer functionId;

    @Column(name = "FUNCTION_NAME")
    private String functionName;
    
    @Column(name = "SEND_MAIL_SET_ATR")
    private int sendMailSetAtr;
    
    @Column(name = "SORT_ORDER")
    private int sortOrder;

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (functionId != null ? functionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SevmtMailFunction)) {
            return false;
        }
        SevmtMailFunction other = (SevmtMailFunction) object;
        if ((this.functionId == null && other.functionId != null) || (this.functionId != null && !this.functionId.equals(other.functionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication1.SevstMailFunction[ functionId=" + functionId + " ]";
    }

	@Override
	protected Object getKey() {
		return this.functionId;
	}

	public SevmtMailFunction() {
		super();
	}

	@Override
	public FunctionId getFunctionId() {
		return new FunctionId(this.functionId);
	}

	@Override
	public FunctionName getFunctionName() {
		return new FunctionName(this.functionName);
	}

	@Override
	public boolean isProprietySendMailSettingAtr() {
		return this.sendMailSetAtr == 1;
	}

	@Override
	public SortOrder getSortOrder() {
		return new SortOrder(this.sortOrder);
	}

	@Override
	public void setFunctionId(FunctionId functionId) {
		this.functionId = functionId.v();
	}

	@Override
	public void setFunctionName(FunctionName functionName) {
		this.functionName = functionName.v();
	}

	@Override
	public void setProprietySendMailSettingAtr(boolean proprietySendMailSettingAtr) {
		this.sendMailSetAtr = proprietySendMailSettingAtr ? 1 : 0;
	}

	@Override
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder.v();
	}
    
}
