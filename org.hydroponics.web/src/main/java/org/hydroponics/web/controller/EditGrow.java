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
import org.hydroponics.web.model.GrowEditBean;
import org.hydroponics.web.validator.GrowValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/edit")
@SessionAttributes("growEditBean")
public class EditGrow {
    private static Logger logger = Logger.getLogger(EditGrow.class.getName());

    @Autowired
    private HydroponicsDao hydroponicsDao;

    @Autowired
    private GrowValidator growValidator;

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        logger.info("WebDataBinder:");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public void setupNewForm(@RequestParam(value="growId", required=false) Integer growId, Model model) {
        logger.info(new StringBuffer("EditGrow: id:").append(growId).toString());

        GrowEditBean growEditBean = new GrowEditBean();

        if(growId != null) {
            Map<String, Object> grow = hydroponicsDao.getGrowById(growId);
            growEditBean.setId((Integer)grow.get(Constants.ID));
            growEditBean.setName((String)grow.get(Constants.NAME));
            growEditBean.setVegetation((Date)grow.get(Constants.VEGETATION));
            growEditBean.setFlower((Date)grow.get(Constants.FLOWER_DATE));
            growEditBean.setEnd((Date)grow.get(Constants.END_DATE));
            growEditBean.setResult((Integer)grow.get(Constants.RESULT));
            growEditBean.setPlants((Integer)grow.get(Constants.PLANTS));
        }
        model.addAttribute(growEditBean);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute GrowEditBean growEditBean, BindingResult result, SessionStatus status, String formAction) {
        logger.info(new StringBuffer("process submit:").append(growEditBean)
                .append("\n\tBindResult:").append(result)
                .append("\n\tSessionState:").append(status)
                .append("\n\tFormAction:").append(formAction).toString());

        if(formAction != null && formAction.equals(Constants.ACTION_SUBMIT)) {
            growValidator.validate(growEditBean, result);
            if (result.hasErrors()) {
                return Constants.PAGE_EDIT_GROW;
            } else {
                Map<String, Object> grow = new HashMap<String, Object>();
                grow.put(Constants.ID, growEditBean.getId());
                grow.put(Constants.NAME, growEditBean.getName());
                grow.put(Constants.VEGETATION, growEditBean.getVegetation());
                grow.put(Constants.FLOWER_DATE, growEditBean.getFlower());
                grow.put(Constants.END_DATE, growEditBean.getEnd());
                grow.put(Constants.PLANTS, growEditBean.getPlants());
                grow.put(Constants.RESULT, growEditBean.getResult());

                this.hydroponicsDao.saveGrow(grow);
                status.setComplete();
                return Constants.REDIRECT_MAIN;
            }
        } else if(formAction != null && formAction.equals(Constants.ACTION_DELETE)) {
            this.hydroponicsDao.deleteGrow(growEditBean.getId());
            status.setComplete();
            return Constants.REDIRECT_MAIN;
        } else if(formAction != null && formAction.equals(Constants.ACTION_CANCEL)) {
            status.setComplete();
            return Constants.REDIRECT_MAIN;
        } else {
            throw new RuntimeException("unknown form action:"+formAction);
        }
    }
}