/* This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.hydroponics.org.hydroponics.udp;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class ClientProtocolHandler extends IoHandlerAdapter {

    private static Logger logger = Logger.getLogger(ClientProtocolHandler.class.getName());

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.log(Level.WARNING, new StringBuffer(ClientProtocolHandler.class.getSimpleName())
                .append(".exception[").append(cause.toString()).append("]").toString(), cause);
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message != null) {
            byte[] ipBytes = (byte[]) message;
            if (logger.isLoggable(Level.INFO)) {
                logger.info(new StringBuffer(ClientProtocolHandler.class.getSimpleName())
                        .append(".message:").append(message).toString());
            }
            System.out.println("message received:" + message);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(new StringBuffer(ClientProtocolHandler.class.getSimpleName())
                    .append(".IDLE[").append(session.getIdleCount(status)).append("]").toString());
        }
    }
}
