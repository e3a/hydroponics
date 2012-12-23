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
import org.hydroponics.org.hydroponics.udp.HydroponicsClientHandler;
import org.hydroponics.web.model.ControllerEditBean;
import org.hydroponics.web.validator.ControllerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.logging.Logger;

@Controller
@RequestMapping("/controller")
@SessionAttributes("controllerEditBean")
public class EditArduino {
    private static Logger logger = Logger.getLogger(EditArduino.class.getName());

    @Autowired
    private HydroponicsClientHandler hydroponicsClientHandler;
    @Autowired
    private ControllerValidator controllerValidator;

    @RequestMapping(method = RequestMethod.GET)
    public void setupNewForm(Model model) {
        ControllerEditBean controllerEditBean = hydroponicsClientHandler.getConfig();
        model.addAttribute(controllerEditBean);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute ControllerEditBean controllerEditBean, BindingResult result, SessionStatus status, String formAction) {
        logger.info(new StringBuffer("process submit:").append(controllerEditBean)
                .append("\n\tBindResult:").append(result)
                .append("\n\tSessionState:").append(status)
                .append("\n\tFormAction:").append(formAction).toString());

        if(formAction != null && formAction.equals(Constants.ACTION_SUBMIT)) {
            controllerValidator.validate(controllerEditBean, result);
            if (result.hasErrors()) {
                return "controller";
            } else {
                //TODO hydroponicsClientHandler.saveControllerConfig(controllerEditBean);
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
