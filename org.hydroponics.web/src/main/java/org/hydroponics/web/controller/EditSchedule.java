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
import org.hydroponics.web.model.SchedulesEditBean;
import org.hydroponics.web.validator.SchedulesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/schedules")
@SessionAttributes("schedulesEditBean")
public class EditSchedule {
    private static Logger logger = Logger.getLogger(EditSchedule.class.getName());

    @Autowired
    private HydroponicsDao hydroponicsDao;
    @Autowired
    private SchedulesValidator schedulesValidator;
    @Autowired
    private HydroponicsClientHandler hydroponicsClientHandler;


    @InitBinder()
    public void initBinder(WebDataBinder binder) throws Exception {
        binder.registerCustomEditor(ArrayList.class, Constants.PAGE_EDIT_SCHEDULES, new CustomCollectionEditor(ArrayList.class) {
            protected Object convertElement(Object element) {
                return super.convertElement(element);
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public void setupNewForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        logger.info(new StringBuffer("EditSwitch: id:").append(id).toString());

        SchedulesEditBean bean = hydroponicsClientHandler.getSwitch(id);
        bean.setName(hydroponicsDao.getSwitchName(id));

        logger.info(new StringBuffer("EditSchedule").append(bean).toString());
        model.addAttribute(bean);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute SchedulesEditBean schedulesEditBean, BindingResult result, SessionStatus status, String formAction) {
        logger.info(new StringBuffer("process submit:").append(schedulesEditBean)
                .append("\n\tBindResult:").append(result)
                .append("\n\tSessionState:").append(status)
                .append("\n\tFormAction:").append(formAction).toString());

        if (formAction != null && formAction.equals(Constants.ACTION_SUBMIT)) {
            schedulesValidator.validate(schedulesEditBean, result);
            if (result.hasErrors()) {
                return Constants.PAGE_EDIT_SCHEDULES;
            } else {
                logger.info("SAVE:" + schedulesEditBean);
                if (schedulesEditBean.getName() != null) {
                    hydroponicsDao.saveSwitchName(schedulesEditBean.getNumber(), schedulesEditBean.getName());
                }

                SchedulesEditBean oldSchedule = hydroponicsClientHandler.getSwitch(schedulesEditBean.getNumber());
                if (schedulesEditBean.getMode() != oldSchedule.getMode() ||
                        !schedulesEditBean.getSchedules().equals(oldSchedule.getSchedules())) {

                    logger.info("Schedules changed.");
                    hydroponicsClientHandler.saveSwitch(schedulesEditBean);

                } else if (logger.isLoggable(Level.INFO)) {
                    logger.info("Schedules unchanged.");
                }

                status.setComplete();
                return Constants.REDIRECT_MAIN;
            }
        } else if (formAction != null && formAction.equals(Constants.ACTION_CANCEL)) {
            return Constants.REDIRECT_MAIN;
        } else {
            throw new RuntimeException("unknown form action:" + formAction);
        }
    }
}
