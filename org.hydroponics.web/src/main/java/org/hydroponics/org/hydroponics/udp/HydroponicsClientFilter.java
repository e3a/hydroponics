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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encoder-/Decoder Filter for the client requests.
 */
public class HydroponicsClientFilter implements ProtocolCodecFactory {
    private static Logger logger = Logger.getLogger(HydroponicsClientFilter.class.getName());

    @Override
    public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
        return new ProtocolEncoder() {
            @Override
            public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info(new StringBuffer(HydroponicsClientFilter.class.getSimpleName())
                            .append(".encode[").append(message).append("]").toString());
                }
                out.write(IoBuffer.wrap((byte[]) message));
            }

            @Override
            public void dispose(IoSession session) throws Exception {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(new StringBuffer(HydroponicsClientFilter.class.getSimpleName())
                            .append(".dispose[").append(session).append("]").toString());
                }
            }
        };
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
        return new CumulativeProtocolDecoder() {
            @Override
            public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(new StringBuffer(HydroponicsClientFilter.class.getSimpleName())
                            .append(".decode[").append(session).append("]").toString());
                }
                final int messageLength = in.get();
                if (in.remaining() == messageLength) {
                    byte arr[] = new byte[messageLength];
                    in.get(arr);
                    out.write(arr);
                    return true;

                } else {
                    in.position(0);
                    return false;
                }
            }
        };
    }
}
