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

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;
import org.hydroponics.model.CalibreEvent;
import org.hydroponics.model.SwitchEvent;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encoder-/Decoder Filter for the broadcast message.
 */
public class BroadcastProtocolCodecFilter implements ProtocolCodecFactory {
    private static Logger logger = Logger.getLogger(BroadcastProtocolCodecFilter.class.getName());

    @Override
    public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
        return new ProtocolEncoder() {
            @Override
            public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(new StringBuffer(BroadcastProtocolCodecFilter.class.getSimpleName())
                            .append(".encode[").append(message).append("]").toString());
                }
            }

            @Override
            public void dispose(IoSession session) throws Exception {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(new StringBuffer(BroadcastProtocolCodecFilter.class.getSimpleName())
                            .append(".dispose[").append(session).append("]").toString());
                }
            }
        };
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
        return new ProtocolDecoder() {
            @Override
            public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(new StringBuffer(BroadcastProtocolCodecFilter.class.getSimpleName())
                            .append(".decode[").append(session).append("]").toString());
                }
                byte command = in.get();
                if (command == 'C') {
                    long timestamp = in.getUnsignedInt();
                    out.write(new Date(timestamp * 1000));

                } else if (command == 'V') {
                    CalibreEvent calibreEvent = new CalibreEvent();
                    calibreEvent.setTemperature(in.get());
                    calibreEvent.setHumidity(in.get());
                    calibreEvent.setCurrent(in.getUnsignedShort());
                    calibreEvent.setMoisture(in.getUnsignedShort());
                    out.write(calibreEvent);

                } else if (command == 'S') {
                    SwitchEvent switchEvent = new SwitchEvent();
                    switchEvent.setNumber(in.get());
                    switchEvent.setMode(in.get());
                    switchEvent.setStatus(in.get());
                    out.write(switchEvent);

                } else if (command == 'L') {
                    int logCommand = in.get();
                    switch (logCommand) {
                        case 1:
                            logger.info("LOG: start ntp update.");
                            break;
                        case 2:
                            logger.info("LOG: end ntp update.");
                            break;
                        case 3:
                            logger.info("LOG: Client Connected .");
                            break;
                        case 4:
                            logger.info("LOG: Client Disonnected .");
                            break;
                        case 5:
                            logger.info("LOG: Before update values.");
                            break;
                        case 6:
                            logger.info("LOG: after update values.");
                            break;
                        default:
                            logger.info("unknown log command");
                    }

                } else {
                    logger.log(Level.SEVERE, "unknown command:" + command);
                }
            }

            @Override
            public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(new StringBuffer(BroadcastProtocolCodecFilter.class.getSimpleName())
                            .append(".finishDecode[").append(session).append("]").toString());
                }
            }

            @Override
            public void dispose(IoSession session) throws Exception {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(new StringBuffer(BroadcastProtocolCodecFilter.class.getSimpleName())
                            .append(".dispose[").append(session).append("]").toString());
                }
            }
        };
    }
}
