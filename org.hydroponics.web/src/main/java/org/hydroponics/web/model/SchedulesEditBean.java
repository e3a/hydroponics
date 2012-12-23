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
package org.hydroponics.web.model;

import java.util.Collection;

public class SchedulesEditBean {
    private int number;
    private String name;
    private int mode;
    private int status;

    private Collection<Integer> schedules;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Collection<Integer> getSchedules() {
        return schedules;
    }

    public void setSchedules(Collection<Integer> schedules) {
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        return "SchedulesEditBean{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", mode=" + mode +
                ", status=" + status +
                ", schedules=" + schedules +
                '}';
    }
}
