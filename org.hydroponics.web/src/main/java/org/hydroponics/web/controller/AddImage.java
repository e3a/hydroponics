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
import org.hydroponics.web.model.ImageEditBean;
import org.hydroponics.web.validator.ImageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/addImage")
@SessionAttributes("imageEditBean")
public class AddImage {

    private static Logger logger = Logger.getLogger(AddImage.class.getName());

    @Autowired
    private HydroponicsDao hydroponicsDao;

    @Autowired
    private ImageValidator imageValidator;

    @RequestMapping(method = RequestMethod.GET)
    public void setupNewForm(Model model) {

        logger.info("add Image");
        ImageEditBean imageEditBean = new ImageEditBean();
        model.addAttribute(imageEditBean);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute ImageEditBean imageEditBean, BindingResult result, SessionStatus status, String formAction) {
        logger.info(new StringBuffer("process submit:").append(imageEditBean)
                .append("\n\tBindResult:").append(result)
                .append("\n\tSessionState:").append(status)
                .append("\n\tFormAction:").append(formAction).toString());

        if (formAction != null && formAction.equals(Constants.ACTION_SUBMIT)) {
            imageValidator.validate(imageEditBean, result);
            if (result.hasErrors()) {
                return Constants.PAGE_ADD_IMAGE;
            } else {
                try {
                    int grow = (Integer) this.hydroponicsDao.getCurrentGrow().get(Constants.ID);
                    this.hydroponicsDao.saveImage(grow, imageEditBean.getFile());
                    status.setComplete();
                    return Constants.REDIRECT_MAIN;
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.toString(), ex);
                    throw new RuntimeException(ex);
                }
            }
        } else if (formAction != null && formAction.equals(Constants.ACTION_CANCEL)) {
            status.setComplete();
            return Constants.REDIRECT_MAIN;
        } else {
            throw new RuntimeException("unknown form action:" + formAction);
        }
    }
}
