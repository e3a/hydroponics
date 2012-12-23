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

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.hydroponics.web.model.ControllerEditBean;
import org.hydroponics.web.model.SchedulesEditBean;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hydroponics controller client handler.
 */
public class HydroponicsClientHandler {
    private static Logger logger = Logger.getLogger(HydroponicsClientHandler.class.getName());

    private BroadcastProtocolHandler broadcastProtocolHandler;
    public void setBroadcastProtocolHandler(BroadcastProtocolHandler broadcastProtocolHandler) {
        this.broadcastProtocolHandler = broadcastProtocolHandler;
    }

    private IoHandler handler;
    public void setHandler(IoHandler handler) {
        this.handler = handler;
    }
    private IoFilter codecFilter;
    public void setCodecFilter(IoFilter codecFilter) {
        this.codecFilter = codecFilter;
    }
    private IoFilter loggingFilter;
    public void setLoggingFilter(IoFilter loggingFilter) {
        this.codecFilter = codecFilter;
    }

    public ControllerEditBean getConfig() {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            byte[] b = request(new byte[]{'?', 'C'}, connector, new InetSocketAddress(broadcastProtocolHandler.getRemoteAddress(), 9997));
            ControllerEditBean res = new ControllerEditBean();
            connector.dispose(true);

            res.setTimezone(new Integer(b[0]));
            res.setTimeserver((b[1] & 0xFF) + "." + (b[2] & 0xFF) + "." + (b[3] & 0xFF) + "." + (b[4] & 0xFF));

            return res;
        } catch(Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            throw new RuntimeException(ex.toString());
        }
    }

    public List<Map<String, Object>> getStatus() {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            byte[] b = request(new byte[]{'?', 'S'}, connector, new InetSocketAddress(broadcastProtocolHandler.getRemoteAddress(), 9997));
            List<Map<String, Object>> res = new ArrayList<Map<String, java.lang.Object>>();
            for(int i=0; i<b.length; i=i+3) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("number", new Integer(b[i]));
                map.put("mode", new Integer(b[i+1]));
                map.put("status", new Integer(b[i+2]));
                res.add(map);
            }
            connector.dispose(true);
            return res;
        } catch(Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            throw new RuntimeException(ex.toString());
        }
    }
    public SchedulesEditBean getSwitch(int number) {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            byte[] b = request(new byte[]{'?', (byte)number}, connector, new InetSocketAddress(broadcastProtocolHandler.getRemoteAddress(), 9997));

            SchedulesEditBean bean = new SchedulesEditBean();
            bean.setNumber(number);
            bean.setMode(new Integer(b[0]));
            bean.setStatus(new Integer(b[1]));

            Collection<Integer> schedules = new ArrayList<Integer>();
            for(int i=0; i<(int)b[2]; i++) {
                schedules.add((int)b[3+(i*6)+0]);
                schedules.add((int)b[3+(i*6)+1]);
                schedules.add((int)b[3+(i*6)+2]);
                schedules.add((int)b[3+(i*6)+3]);
                schedules.add((int)b[3+(i*6)+4]);
                schedules.add((int)b[3+(i*6)+5]);
            }
            bean.setSchedules(schedules);
            connector.dispose(true);

            return bean;
        } catch(Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            throw new RuntimeException(ex.toString());
        }
    }

    public void saveSwitch(SchedulesEditBean scheduleEditBean) {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            byte[] b = new byte[3 + scheduleEditBean.getSchedules().size()];
            b[0] = (byte)scheduleEditBean.getNumber();
            b[1] = (byte)scheduleEditBean.getMode();
            b[2] = (byte)scheduleEditBean.getSchedules().size();
            int counter = 0;
            for(Integer i : scheduleEditBean.getSchedules()) {
                b[counter+3] = i.byteValue();
                counter += 1;
            }
            for(int i=0; i<b.length; i++) {
                System.out.print(b[i]);
                System.out.print(" ");
            }
            System.out.println();
            save(b, connector, new InetSocketAddress(broadcastProtocolHandler.getRemoteAddress(), 9997));
            connector.dispose(true);

        } catch(Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            throw new RuntimeException(ex.toString());
        }
    }

    private byte[] request(byte[] command, NioSocketConnector connector, SocketAddress address) {

        try {
            logger.info("connect to:" + address);

            connector.getFilterChain().addLast("codec", codecFilter);
            if(loggingFilter != null) {
                connector.getFilterChain().addLast("logger", loggingFilter);
            }
            connector.setHandler(handler);
            ConnectFuture future1 = connector.connect(address);
            future1.awaitUninterruptibly();
            if (!future1.isConnected()) {
                logger.log(Level.WARNING, "not connected to controller.");
                return null;
            }

            // do request
            IoSession session = future1.getSession();
            session.getConfig().setUseReadOperation(true);
            session.write(command).awaitUninterruptibly();

            final ReadFuture readFuture = session.read();
            readFuture.awaitUninterruptibly();

            if(readFuture.getException() != null) {
                logger.warning("response ex:"+readFuture.getException());
            }

            byte[] res = (byte[])readFuture.getMessage();

            if(logger.isLoggable(Level.INFO)) {
                StringBuffer buffer = new StringBuffer("Response:");
                for(int i=0; i<res.length; i++) {
                    buffer.append(res[i]);
                    buffer.append(" ");
                }
                logger.log(Level.INFO, buffer.toString());
            }

            session.close(true).awaitUninterruptibly();
            return res;

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
            return null;
        }
    }
    private void save(byte[] command, NioSocketConnector connector, SocketAddress address) {

        try {
            logger.info("connect to:" + address);

            connector.getFilterChain().addLast("codec", codecFilter);
            if(loggingFilter != null) {
                connector.getFilterChain().addLast("logger", loggingFilter);
            }
            connector.setHandler(handler);
            ConnectFuture future1 = connector.connect(address);
            future1.awaitUninterruptibly();
            if (!future1.isConnected()) {
                logger.log(Level.WARNING, "not connected to controller.");
                return;
            }

            // do request
            IoSession session = future1.getSession();
            session.getConfig().setUseReadOperation(true);
            session.write(command).awaitUninterruptibly();

            session.close(true).awaitUninterruptibly();

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }
}
