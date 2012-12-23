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
import org.hydroponics.Constants;
import org.hydroponics.model.CalibreEvent;
import org.hydroponics.model.HydroponicsDao;
import org.hydroponics.model.SwitchEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handler for the broadcast message.
 */
public class BroadcastProtocolHandler extends IoHandlerAdapter {

    private static Logger logger = Logger.getLogger(BroadcastProtocolHandler.class.getName());

    private List<PropertyChangeListener> propertyChangeListener;
    public void setPropertyChangeListener(List<PropertyChangeListener> propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
    }

    private HydroponicsDao hydroponicsDao;
    public void setHydroponicsDao(HydroponicsDao hydroponicsDao) {
        this.hydroponicsDao = hydroponicsDao;
    }

    private InetAddress remoteAddress;
    protected InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception {
        logger.log(Level.WARNING, new StringBuffer(BroadcastProtocolHandler.class.getSimpleName())
                .append(".exception[").append(cause.toString()).append("]").toString(), cause);
        session.close(true);
    }
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if(message != null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info(new StringBuffer(BroadcastProtocolHandler.class.getSimpleName())
                        .append(".message IP:").append(session.getRemoteAddress())
                        .append(" - ").append(message).toString());
            }
            if(message instanceof Date) {
                InetSocketAddress socketAddress = (InetSocketAddress)session.getRemoteAddress();
                remoteAddress = socketAddress.getAddress();
                logger.info(message.toString());

            } else if(message instanceof CalibreEvent) {
                hydroponicsDao.saveCalibre((CalibreEvent)message);
                if(propertyChangeListener != null) {
                    for(PropertyChangeListener listener : propertyChangeListener) {
                        listener.propertyChange(new PropertyChangeEvent(this, Constants.CALIBRE, null, message));
                    }
                }
            } else if(message instanceof SwitchEvent) {
                if(propertyChangeListener != null) {
                    for(PropertyChangeListener listener : propertyChangeListener) {
                        listener.propertyChange(new PropertyChangeEvent(this, Constants.SWITCHES, null, message));
                    }
                }
            }
        }
    }
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(new StringBuffer(BroadcastProtocolHandler.class.getSimpleName())
                    .append(".IDLE[").append(session.getIdleCount(status)).append("]").toString());
        }
    }
}
