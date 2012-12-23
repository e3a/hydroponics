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
import org.hydroponics.model.HydroponicsDao;
import org.hydroponics.org.hydroponics.udp.HydroponicsClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for Hydroponics web application.<p/>
 */
@Controller
public class HydroponicsController {

    private static Logger logger = Logger.getLogger(HydroponicsController.class.getName());

    @Autowired
    private HydroponicsDao hydroponicsDao;
    @Autowired
    private HydroponicsClientHandler hydroponicsClientHandler;

    @RequestMapping(value="/main.htm")
    public ModelAndView overviewHandler() {

        logger.info("/main.htm");

        ModelAndView mav = new ModelAndView();
        Map<String, Object> grow = hydroponicsDao.getCurrentGrow();
        mav.addObject(Constants.GROW, grow);
        mav.addObject(Constants.CALIBRE, hydroponicsDao.getCalibre());

        List<Map<String, Object>> switches = hydroponicsClientHandler.getStatus();
        if(switches != null) {
            for(Map<String, Object> s : switches) {
                Integer id = (Integer)s.get(Constants.NUMBER);
                String name = hydroponicsDao.getSwitchName(id);
                s.put(Constants.NAME, (name==null||name.isEmpty()?"Switch "+id:name));
            }
        } else {
            switches = new ArrayList<Map<String, Object>>();
        }
        logger.log(Level.INFO, new StringBuffer("switches:").append(switches).toString());

        mav.addObject(Constants.SWITCHES, switches);
        if(grow != null && grow.containsKey(Constants.ID)) {
            mav.addObject(Constants.IMAGES, hydroponicsDao.getImages((Integer)grow.get(Constants.ID)));
        }
        return mav;
    }
    @RequestMapping(value="/list.htm")
    public ModelAndView listHandler() {

        logger.info("/list.htm");
        ModelAndView mav = new ModelAndView();
        Collection<Map<String, Object>> list = hydroponicsDao.getGrowList();
        logger.info(new StringBuffer("GrowList: Size:").append(list == null ? "null" : list.size()).toString());
        mav.addObject(Constants.GROW_LIST, list);
        return mav;
    }

    @RequestMapping(value="/grow.htm")
    public ModelAndView growHandler(@RequestParam("id") int growId) {
       logger.info("/grow.htm");
        ModelAndView mav = new ModelAndView();
        mav.addObject(Constants.GROW, hydroponicsDao.getGrowById(growId));
        return mav;
    }

    @RequestMapping(value="/calibre.csv")
    public ModelAndView calibreHandler(@RequestParam("growId") int growId) {

        logger.info("/calibre.csv");

        ModelAndView mav = new ModelAndView();
        mav.addObject(Constants.ROW_SET, hydroponicsDao.getCalibreList(growId));
        return mav;
    }

    @RequestMapping(value="/current.csv")
    public ModelAndView currentHandler(@RequestParam("growId") int growId) {

        logger.info("/current.csv");

        ModelAndView mav = new ModelAndView();
        mav.addObject(Constants.ROW_SET, hydroponicsDao.getCurrentList(growId));
        return mav;
    }

    @RequestMapping(value="/moisture.csv")
    public ModelAndView moistureHandler(@RequestParam("growId") int growId) {

        logger.info("/moisture.csv");

        ModelAndView mav = new ModelAndView();
        mav.addObject(Constants.ROW_SET, hydroponicsDao.getMoistureList(growId));
        return mav;
    }

    @RequestMapping(value="/fertilizer.csv")
    public ModelAndView fertilizerHandler(@RequestParam("growId") int growId) {

        logger.info("/fertilizer.csv");

        ModelAndView mav = new ModelAndView();
        mav.addObject(Constants.ROW_SET, hydroponicsDao.getFertilizerList(growId));
        return mav;
    }
}
