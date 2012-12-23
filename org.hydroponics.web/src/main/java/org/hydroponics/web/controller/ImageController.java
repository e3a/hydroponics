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

import org.hydroponics.model.HydroponicsDao;
import org.hydroponics.web.servlet.ImageServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the image static file.<p/>
 */
@Controller
@RequestMapping("/image")
public class ImageController {
    private static Logger logger = Logger.getLogger(ImageController.class.getName());

    @Autowired
    private HydroponicsDao hydroponicsDao;

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public String imageHandler(@PathVariable int id, @RequestParam(value="type", required=false) String type, Model modell) {
        if(logger.isLoggable(Level.INFO)) logger.info("Get Image: " + id);
        modell.addAttribute(ImageServlet.IMAGE, hydroponicsDao.getImageById(id, type));
        return "image";
    }
}
