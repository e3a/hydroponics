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
import org.hydroponics.web.model.FertilizerEditBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Controller
@RequestMapping("/addFertilizer")
@SessionAttributes("fertilizerEditBean")
public class AddFertilizer {
    private static Logger logger = Logger.getLogger(AddFertilizer.class.getName());

    @Autowired
    private HydroponicsDao hydroponicsDao;

    @Autowired
    private Validator fertilizerValidator;

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        logger.info("WebDataBinder:");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public void setupNewForm(Model model) {

        logger.info("AddFertilizer");

        FertilizerEditBean fertilizerEditBean = new FertilizerEditBean();
        fertilizerEditBean.setTimestamp(new Date(System.currentTimeMillis()));
        fertilizerEditBean.setGrow((Integer)hydroponicsDao.getCurrentGrow().get(Constants.ID));
        model.addAttribute(fertilizerEditBean);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute FertilizerEditBean fertilizerEditBean, BindingResult result, SessionStatus status, String formAction) {
        logger.info(new StringBuffer("process submit:").append(fertilizerEditBean)
                .append("\n\tBindResult:").append(result)
                .append("\n\tSessionState:").append(status)
                .append("\n\tFormAction:").append(formAction).toString());

        if(formAction != null && formAction.equals(Constants.ACTION_SUBMIT)) {
            fertilizerValidator.validate(fertilizerEditBean, result);
            if (result.hasErrors()) {
                return Constants.PAGE_ADD_FERTILIZER;
            } else {
               this.hydroponicsDao.saveFertilizer(fertilizerEditBean);
                status.setComplete();
                return Constants.REDIRECT_MAIN;
            }
        } else if(formAction != null && formAction.equals(Constants.ACTION_CANCEL)) {
            status.setComplete();
            return Constants.REDIRECT_MAIN;
        } else {
            throw new RuntimeException("unknown form action:"+formAction);
        }
    }
}
