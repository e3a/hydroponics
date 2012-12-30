package org.hydroponics.web.model;

/**
 * Bean for CalibreEvent.
 */
public class CalibreBean {

    private int id;
    private double temperature;
    private double humidity;
    private int electricity;
    private int result;

    public CalibreBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public int getElectricity() {
        return electricity;
    }

    public void setElectricity(int electricity) {
        this.electricity = electricity;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "CalibreBean{" +
                "id=" + id +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", electricity=" + electricity +
                ", result=" + result +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalibreBean that = (CalibreBean) o;

        if (electricity != that.electricity) return false;
        if (Double.compare(that.humidity, humidity) != 0) return false;
        if (id != that.id) return false;
        if (result != that.result) return false;
        if (Double.compare(that.temperature, temperature) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1;
        long temp;
        result1 = id;
        temp = temperature != +0.0d ? Double.doubleToLongBits(temperature) : 0L;
        result1 = 31 * result1 + (int) (temp ^ (temp >>> 32));
        temp = humidity != +0.0d ? Double.doubleToLongBits(humidity) : 0L;
        result1 = 31 * result1 + (int) (temp ^ (temp >>> 32));
        result1 = 31 * result1 + electricity;
        result1 = 31 * result1 + result;
        return result1;
    }
}
