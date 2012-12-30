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
package org.hydroponics.web.validator;

import org.hydroponics.web.model.FertilizerEditBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FertilizerValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return FertilizerEditBean.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        FertilizerEditBean fertilizer = (FertilizerEditBean) object;
        int f = fertilizer.getFertilizer();
        if (f == 0) {
            errors.rejectValue("fertilizer", "required", "required");
        }
        int grow = fertilizer.getGrow();
        if (grow == 0) {
            errors.rejectValue("grow", "required", "required");
        }
    }
}
