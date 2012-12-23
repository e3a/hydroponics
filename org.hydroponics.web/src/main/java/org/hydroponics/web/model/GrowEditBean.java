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

import java.util.Date;

/**
 * Bean for the Grow Form.
 */
public class GrowEditBean {
    private int id;
    private String name;
    private Date vegetation;
    private Date flower;
    private Date end;
    private int plants;
    private int result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getVegetation() {
        return vegetation;
    }

    public void setVegetation(Date vegetation) {
        this.vegetation = vegetation;
    }

    public Date getFlower() {
        return flower;
    }

    public void setFlower(Date flower) {
        this.flower = flower;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getPlants() {
        return plants;
    }

    public void setPlants(int plants) {
        this.plants = plants;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GrowEditBean that = (GrowEditBean) o;

        if (id != that.id) return false;
        if (plants != that.plants) return false;
        if (result != that.result) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (flower != null ? !flower.equals(that.flower) : that.flower != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (vegetation != null ? !vegetation.equals(that.vegetation) : that.vegetation != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = id;
        result1 = 31 * result1 + (name != null ? name.hashCode() : 0);
        result1 = 31 * result1 + (vegetation != null ? vegetation.hashCode() : 0);
        result1 = 31 * result1 + (flower != null ? flower.hashCode() : 0);
        result1 = 31 * result1 + (end != null ? end.hashCode() : 0);
        result1 = 31 * result1 + plants;
        result1 = 31 * result1 + result;
        return result1;
    }

    @Override
    public String toString() {
        return "GrowEditBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vegetation=" + vegetation +
                ", flower=" + flower +
                ", end=" + end +
                ", plants=" + plants +
                ", result=" + result +
                '}';
    }
}

