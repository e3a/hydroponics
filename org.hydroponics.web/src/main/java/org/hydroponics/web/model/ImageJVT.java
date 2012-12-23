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

import java.util.Arrays;
import java.util.Date;

public class ImageJVT {

    private int id;
    private Date timestamp;
    private int height;
    private int width;
    private String mimeType;
    private byte[] image;
    private byte[] thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageJVT imageJVT = (ImageJVT) o;

        if (height != imageJVT.height) return false;
        if (id != imageJVT.id) return false;
        if (width != imageJVT.width) return false;
        if (!Arrays.equals(image, imageJVT.image)) return false;
        if (mimeType != null ? !mimeType.equals(imageJVT.mimeType) : imageJVT.mimeType != null) return false;
        if (!Arrays.equals(thumbnail, imageJVT.thumbnail)) return false;
        if (timestamp != null ? !timestamp.equals(imageJVT.timestamp) : imageJVT.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + height;
        result = 31 * result + width;
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (image != null ? Arrays.hashCode(image) : 0);
        result = 31 * result + (thumbnail != null ? Arrays.hashCode(thumbnail) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImageJVT{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", height=" + height +
                ", width=" + width +
                ", mimeType='" + mimeType + '\'' +
                ", image=" + image +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
