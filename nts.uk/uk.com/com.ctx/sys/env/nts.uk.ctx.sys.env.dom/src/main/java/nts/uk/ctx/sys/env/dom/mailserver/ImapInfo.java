/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailserver;

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class ImapInfo.
 */
@Getter
@Setter
// IMAP情報
public class ImapInfo extends DomainObject implements Comparable<ImapInfo> {

	/** The server. */
	// サーバ
	private Server server;

	/** The use server. */
	// サーバ使用
	private UseServer useServer;

	/** The port. */
	// ポート
	private Port port;

	/**
	 * Instantiates a new imap info.
	 *
	 * @param server
	 *            the server
	 * @param useServer
	 *            the use server
	 * @param port
	 *            the port
	 */
	public ImapInfo(Server server, UseServer useServer, Port port) {
		this.server = server;
		this.useServer = useServer;
		this.port = port;
	}

	@Override
	public int compareTo(ImapInfo o) {
		return Comparator.comparing(ImapInfo::getServer)
				.thenComparing(ImapInfo::getUseServer)
				.thenComparing(ImapInfo::getPort)
				.compare(this, o);
	}
}
