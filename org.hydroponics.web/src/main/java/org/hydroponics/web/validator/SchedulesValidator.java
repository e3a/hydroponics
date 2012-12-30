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

import org.hydroponics.web.model.SchedulesEditBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class SchedulesValidator {
    public void validate(SchedulesEditBean schedulesEditBean, Errors errors) {
        String name = schedulesEditBean.getName();
        if (name == null) {
            errors.rejectValue("name", "required", "required");
        } else if (name.length() > 40) {
            errors.rejectValue("name", "Long", "switch.name.toLong");
        }
    }
}
