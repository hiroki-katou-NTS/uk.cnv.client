package nts.uk.ctx.sys.env.dom.mailserver;

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class SmtpInfo.
 */
@Getter
@Setter
// SMTP情報
public class SmtpInfo extends DomainObject implements Comparable<SmtpInfo> {
	
	/** The server. */
	// サーバ
	private Server server;
	
	/** The port. */
	// ポート
	private Port port;
	
	/**
	 * Instantiates a new smtp info.
	 *
	 * @param server the server
	 * @param port the port
	 */
	public SmtpInfo(Server server, Port port){
		this.server = server;
		this.port = port;
	}

	@Override
	public int compareTo(SmtpInfo o) {
		return Comparator.comparing(SmtpInfo::getServer)
				.thenComparing(SmtpInfo::getPort)
				.compare(this, o);
	}
}
