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
package org.hydroponics.web.controller;

import org.hydroponics.Constants;
import org.hydroponics.model.CalibreEvent;
import org.hydroponics.model.SwitchEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Long Polling controller for the Calibre
 */
@Controller(value="CalibreAsyncController")
public class CalibreAsyncController implements PropertyChangeListener {

    private static Logger logger = Logger.getLogger(CalibreAsyncController.class.getName());

    private final Collection<DeferredResult<Map<String, Object>>> calibreRequests = new ArrayList<DeferredResult<Map<String, Object>>>();

    @RequestMapping("/update")
    @ResponseBody
    public DeferredResult<Map<String, Object>> update() {

        final DeferredResult<Map<String, Object>> result =
                new DeferredResult<Map<String, Object>>();
        this.calibreRequests.add(result);

        logger.fine("add deferred result");

        result.onCompletion(new Runnable() {
            public void run() {
                logger.fine("remove deferred result:" + calibreRequests.size());
                calibreRequests.remove(result);
            }
        });
        return result;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getNewValue() instanceof CalibreEvent) {
            logger.fine("updateResults:" + this.calibreRequests.size());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(Constants.EVENT, Constants.CALIBRE);
            map.put(Constants.TEMPERATURE, ((CalibreEvent) propertyChangeEvent.getNewValue()).getTemperature());
            map.put(Constants.HUMIDITY, ((CalibreEvent) propertyChangeEvent.getNewValue()).getHumidity());
            map.put(Constants.CURRENT, ((CalibreEvent) propertyChangeEvent.getNewValue()).getCurrent());
            map.put(Constants.MOISTURE, ((CalibreEvent) propertyChangeEvent.getNewValue()).getMoisture());
            for(DeferredResult<Map<String, Object>> calibreRequest : calibreRequests) {
                calibreRequest.setResult(map);
            }
        } else if(propertyChangeEvent.getNewValue() instanceof SwitchEvent) {
            logger.fine("updateResults:" + this.calibreRequests.size());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(Constants.EVENT, Constants.SWITCHES);
            map.put(Constants.NUMBER, ((SwitchEvent) propertyChangeEvent.getNewValue()).getNumber());
            map.put(Constants.STATUS, ((SwitchEvent) propertyChangeEvent.getNewValue()).getStatus());
            map.put(Constants.MODE, ((SwitchEvent) propertyChangeEvent.getNewValue()).getMode());
            for(DeferredResult<Map<String, Object>> calibreRequest : calibreRequests) {
                calibreRequest.setResult(map);
            }
        } else {
            logger.info("unknown event: "+propertyChangeEvent.getNewValue());
        }
    }
}
