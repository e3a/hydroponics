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
package org.hydroponics.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Map;

public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;

    public static final String IMAGE = "image";
    public static final String IMAGE_DATA = "imageData";
    public static final String IMAGE_EXPIRES = "imageExpires";
    public static final String IMAGE_MIME_TYPE = "imageMimeType";

    public static final String VIEW_IMAGE_EXPIRES = "Expires";
    public static final String VIEW_IMAGE_EXPIRES_DATE = "Sun, 17 Jan 2038 19:14:07 GMT";

    public void init() throws ServletException {
        super.init();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException {

        Map image = ((Map) req.getAttribute(IMAGE));
        byte[] bytes = (byte[]) image.get(IMAGE_DATA);
        OutputStream out = new BufferedOutputStream(resp.getOutputStream());

        //send image to the browser
        resp.addHeader(VIEW_IMAGE_EXPIRES,
                (image.get(IMAGE_EXPIRES) == null ? VIEW_IMAGE_EXPIRES_DATE : (String) image.get(IMAGE_EXPIRES)));
        resp.setContentType((String) image.get(IMAGE_MIME_TYPE));
        resp.setContentLength(bytes.length);
        out.write(bytes);
        out.flush();
    }
}