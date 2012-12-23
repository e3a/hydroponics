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

public class ControllerEditBean {

    private String timeserver;
    private int timezone;

    public String getTimeserver() {
        return timeserver;
    }

    public void setTimeserver(String timeserver) {
        this.timeserver = timeserver;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "ControllerEditBean{" +
                ", timeserver='" + timeserver + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControllerEditBean that = (ControllerEditBean) o;

        if (timezone != that.timezone) return false;
        if (timeserver != null ? !timeserver.equals(that.timeserver) : that.timeserver != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = timeserver != null ? timeserver.hashCode() : 0;
        result = 31 * result + timezone;
        return result;
    }
}
