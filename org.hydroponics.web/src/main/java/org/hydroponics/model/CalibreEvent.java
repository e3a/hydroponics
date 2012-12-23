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
package org.hydroponics.model;

/**
 * Calibre Event Entity
 */
public class CalibreEvent {

    private int temperature;
    private int humidity;
    private int current;
    private int moisture;

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getMoisture() {
        return moisture;
    }

    public void setMoisture(int moisture) {
        this.moisture = moisture;
    }

    @Override
    public String toString() {
        return "CalibreEvent{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", current=" + current +
                ", moisture=" + moisture +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalibreEvent that = (CalibreEvent) o;

        if (current != that.current) return false;
        if (humidity != that.humidity) return false;
        if (moisture != that.moisture) return false;
        if (temperature != that.temperature) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = temperature;
        result = 31 * result + humidity;
        result = 31 * result + current;
        result = 31 * result + moisture;
        return result;
    }
}
