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
package org.hydroponics.dao;

import org.hydroponics.Constants;
import org.hydroponics.model.CalibreEvent;
import org.hydroponics.model.HydroponicsDao;
import org.hydroponics.web.model.FertilizerEditBean;
import org.hydroponics.web.servlet.ImageServlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Hydroponics DAO.
 */
public class JDBCHydroponicsDaoImpl implements HydroponicsDao, InitializingBean {
    private static Logger logger = Logger.getLogger(JDBCHydroponicsDaoImpl.class.getName());

    /* start spring properties */
    private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /* end spring properties */

    @Override
    public void afterPropertiesSet() throws Exception {
        //executeScript(JDBCHydroponicsDaoImpl.class.getResourceAsStream("/org/hydroponics/dao/create-database.sql"));
    }
    private void executeScript(InputStream inputStream) {
        try {
            BufferedReader d =
                    new BufferedReader(new InputStreamReader(inputStream));

            String thisLine, sqlQuery = "";
            while ((thisLine = d.readLine()) != null) {
                //Skip comments and empty lines
                if(thisLine.length() > 0 && thisLine.charAt(0) == '-' || thisLine.length() == 0 )
                    continue;

                sqlQuery = sqlQuery + " " + thisLine;

                if(sqlQuery.charAt(sqlQuery.length() - 1) == ';') {
                    logger.log(Level.FINE, sqlQuery);
                    sqlQuery = sqlQuery.replace(';' , ' '); //Remove the ; since jdbc complains
                    jdbcTemplate.execute(sqlQuery);
                    sqlQuery = "";
                }
            }
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "Error Creating the SQL Database : " + ex.getMessage(), ex);
        }
    }

    @Override
    public SqlRowSet getCalibreList(int grow) {
        return this.jdbcTemplate.queryForRowSet(
                "select timestamp, temperature, humidity from CALIBRE where GROW_ID=? order by timestamp asc",
                new Object[]{grow});
    }
    @Override
    public SqlRowSet getCurrentList(int grow) {
        return this.jdbcTemplate.queryForRowSet(
                "select timestamp, current from CALIBRE where GROW_ID=? order by timestamp asc",
                new Object[]{grow});
    }
    @Override
    public SqlRowSet getMoistureList(int grow) {
        return this.jdbcTemplate.queryForRowSet(
                "select timestamp, moisture from CALIBRE where GROW_ID=? order by timestamp asc",
                new Object[]{grow});
    }
    @Override
    public SqlRowSet getFertilizerList(int grow) {
        return this.jdbcTemplate.queryForRowSet(
                "select timestamp, fertilizer from FERTILIZER where GROW_ID=? order by timestamp asc",
                new Object[]{grow});
    }

    @Override
    public Map<String, Object> getCalibre() {
        if(this.jdbcTemplate.queryForInt("select count(0) from CALIBRE") == 0) {
            return new HashMap<String, Object>();
        }
        return (Map<String, Object>) this.jdbcTemplate.queryForObject(
                "select CALIBRE_ID, timestamp, temperature, humidity, current, moisture from CALIBRE where timestamp = (SELECT MAX(timestamp) FROM CALIBRE)",
                new CalibreMapper());
    }

    @Override
    public Collection<Map<String, Object>> getGrowList() {
        return this.jdbcTemplate.query(
                "select GROW_ID, name, flower, end, plants, result, vegetation from GROW", new GrowMapper());
    }

    @Override
    public Map<String, Object> getCurrentGrow() {
        if(this.jdbcTemplate.queryForInt("select count(0) from GROW") == 0) {
            return new HashMap<String, Object>();
        }
        return (Map<String, Object>) this.jdbcTemplate.queryForObject(
            "select GROW_ID, name, flower, end, plants, result, vegetation from GROW where vegetation = (SELECT MAX(vegetation) FROM GROW)",
            new GrowMapper());
    }

    @Override
    public Map<String, Object> getGrowById(final int id) {
        return (Map<String, Object>) this.jdbcTemplate.queryForObject(
            "select GROW_ID, name, vegetation, flower, end, result, plants from GROW where GROW_ID = ?",
            new Object[]{new Long(id)}, new GrowMapper());
    }

    public void saveGrow(Map<String, Object> grow) {
        logger.info(new StringBuffer("save grow:").append(grow).toString());
        if(grow.get(Constants.ID) != null && ((Integer)grow.get(Constants.ID)) > 0) {
            this.jdbcTemplate.update("update GROW set name = ?,  vegetation = ?, flower = ?, end = ?, result = ?, plants = ? where GROW_ID = ?",
                    new Object[] {
                        grow.get(Constants.NAME),
                        (grow.get(Constants.VEGETATION) == null ? null :
                                new java.sql.Date(((Date)grow.get(Constants.VEGETATION)).getTime())),
                        (grow.get(Constants.FLOWER_DATE) == null ? null :
                                new java.sql.Date(((Date)grow.get(Constants.FLOWER_DATE)).getTime())),
                        (grow.get(Constants.END_DATE) == null ? null :
                                new java.sql.Date(((Date)grow.get(Constants.END_DATE)).getTime())),
                        grow.get(Constants.RESULT),
                        grow.get(Constants.PLANTS),
                        grow.get(Constants.ID)
                    });
        } else {
            this.jdbcTemplate.update("insert into GROW (name, vegetation, flower, end, result, plants) values (?, ?, ?, ?, ?, ?)",
                    new Object[] {
                            grow.get(Constants.NAME),
                            (grow.get(Constants.VEGETATION) == null ? null :
                                    new java.sql.Date(((Date)grow.get(Constants.VEGETATION)).getTime())),
                            (grow.get(Constants.FLOWER_DATE) == null ? null :
                                    new java.sql.Date(((Date)grow.get(Constants.FLOWER_DATE)).getTime())),
                            (grow.get(Constants.END_DATE) == null ? null :
                                    new java.sql.Date(((Date)grow.get(Constants.END_DATE)).getTime())),
                            grow.get(Constants.RESULT),
                            grow.get(Constants.PLANTS)
                    });
        }
    }

    @Override
    public void deleteGrow(int id) {
        //TODO delete images
        //TODO delete fertilizer
        this.jdbcTemplate.update("delete from GROW where GROW_ID=?", new Object[]{id});
    }

    @Override
    public void saveCalibre(CalibreEvent calibreEvent) {
        logger.info(new StringBuffer("save calibreEvent:").append(calibreEvent).toString());
        Map<String, Object> currentGrow = getCurrentGrow();
        if(currentGrow!=null) {
        this.jdbcTemplate.update("insert into CALIBRE (timestamp, temperature, humidity, current, moisture, GROW_ID) values (?, ?, ?, ?, ?, ?)",
                new Object[] {new Timestamp(System.currentTimeMillis()),
                              calibreEvent.getTemperature(), calibreEvent.getHumidity(),
                              calibreEvent.getCurrent(), calibreEvent.getMoisture(),
                              (Integer)currentGrow.get(Constants.ID)
            });
        } else {
            logger.info("no current grow, not saving data.");
        }
    }

    @Override
    public void saveFertilizer(FertilizerEditBean fertilizer) {
        logger.info(new StringBuffer("save fertillizer:").append(fertilizer).toString());
        this.jdbcTemplate.update("insert into FERTILIZER (timestamp, fertilizer, GROW_ID) values (?, ?, ?)",
                new Object[] {new Timestamp(fertilizer.getTimestamp().getTime()),
                        fertilizer.getFertilizer(), fertilizer.getGrow()
                });
    }

    @Override
    @SuppressWarnings("all")
    public Map<String, Object> getImageById(int id, String type) {
        if("thumb".equals(type)) {
            return (Map<String, Object>) this.jdbcTemplate.queryForObject(
                    "select IMAGE_ID, thumbnail, mimeType from IMAGE where IMAGE_ID = ?", new Object[]{id},
                    new ImageMapper());
        } else {
            return (Map<String, Object>) this.jdbcTemplate.queryForObject(
                    "select IMAGE_ID, image, mimeType from IMAGE where IMAGE_ID = ?", new Object[]{id},
                    new ImageMapper());
        }
    }

    @Override
    public Collection<Integer> getImages(int grow) {
        return (Collection<Integer>) this.jdbcTemplate.queryForList(
                "select IMAGE_ID from IMAGE where GROW_ID = ? order by timestamp asc", new Object[]{grow}, Integer.class);
    }

    @Override
    public void saveImage(int grow, MultipartFile image) {
        logger.info("save image");

            try {
            BufferedImage buffImage = ImageIO.read(image.getInputStream());
        this.jdbcTemplate.update("insert into IMAGE (GROW_ID, timestamp, height, width, mimeType, thumbnail, image) values (?, ?, ?, ?, ?, ?, ?)",
                new Object[] {grow, new Timestamp(System.currentTimeMillis()),
                        buffImage.getHeight(), buffImage.getWidth(),
                        "image/jpeg", getThumbnail(buffImage), image.getBytes()
                });
            } catch(IOException ex) {
                logger.severe(ex.toString());
            }
    }
    public byte[] getThumbnail(BufferedImage buffImage) throws IOException {
        BufferedImage pDestImage = new BufferedImage(Constants.THUMBNAIL_WIDTH, Constants.THUMBNAIL_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

        AffineTransform transform = new AffineTransform();
        transform.scale((float)Constants.THUMBNAIL_WIDTH/(float)buffImage.getWidth(),
                (float)Constants.THUMBNAIL_HEIGHT/(float)buffImage.getHeight());

        Graphics2D g = (Graphics2D)pDestImage.getGraphics();

        //set the rendering hints for a good thumbnail image
        Map m = g.getRenderingHints();
        m.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        m.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        m.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        m.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY );
        g.setRenderingHints( m );

        g.drawImage(buffImage, transform, null);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(pDestImage, "JPEG", out);
        return out.toByteArray();
    }

    @Override
    public String getSwitchName(int id) {
        List<String> names = this.jdbcTemplate.queryForList("select name from SWITCH where SWITCH_ID = ?",
                new Object[] { id }, String.class);
        if (names.isEmpty()) {
            return new String();
        } else {
            return names.get(0);
        }
    }
    @Override
    public void saveSwitchName(int id, String name) {
        logger.info(new StringBuffer("save switch name:").append(id).append(":").append(name).toString());
        if(this.jdbcTemplate.queryForInt("select count(0) from SWITCH where SWITCH_ID = ?", id) == 1) {
            this.jdbcTemplate.update("update SWITCH set SWITCH_ID = ?,  name = ? where SWITCH_ID = ?",
                    new Object[] { id, name, id });
        } else {
            this.jdbcTemplate.update("insert into SWITCH (SWITCH_ID, name) values (?, ?)",
                    new Object[] { id, name });
        }
    }

    private static final class GrowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, Object> grow = new HashMap<String, java.lang.Object>();
            grow.put(Constants.ID, rs.getInt("GROW_ID"));
            grow.put(Constants.NAME, rs.getString("name"));
            grow.put(Constants.FLOWER_DATE, rs.getDate("flower"));
            grow.put(Constants.END_DATE, rs.getDate("end"));
            grow.put(Constants.PLANTS, rs.getInt("plants"));
            grow.put(Constants.RESULT, rs.getInt("result"));
            grow.put(Constants.VEGETATION, rs.getDate("vegetation"));

            if(rs.getDate("flower") != null && rs.getDate("vegetation") != null) {
                grow.put("vegetationDays", daysBetween(rs.getDate("vegetation"), rs.getDate("flower")));
            } else if(rs.getDate("flower") == null && rs.getDate("vegetation") != null) {
                grow.put("vegetationDays", daysBetween(rs.getDate("vegetation"), new Date(System.currentTimeMillis())));
            }
            if(rs.getDate("end") != null && rs.getDate("flower") != null) {
                grow.put("flowerDays", daysBetween(rs.getDate("flower"), rs.getDate("end")));
            } else if(rs.getDate("end") == null && rs.getDate("flower") != null) {
                grow.put("flowerDays", daysBetween(rs.getDate("flower"), new Date(System.currentTimeMillis())));
            }

            return grow;
        }
    }
    private static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private static final class CalibreMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, Object> calibre = new HashMap<String, java.lang.Object>();
            calibre.put(Constants.ID, rs.getInt("CALIBRE_ID"));
            calibre.put(Constants.TIMESTAMP, rs.getDate("timestamp"));
            calibre.put(Constants.TEMPERATURE, rs.getInt("temperature"));
            calibre.put(Constants.HUMIDITY, rs.getInt("humidity"));
            calibre.put(Constants.CURRENT, rs.getInt("current"));
            calibre.put(Constants.MOISTURE, rs.getInt("moisture"));
            return calibre;
        }
    }

    private static final class ImageMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, Object> image = new HashMap<String, java.lang.Object>();
            image.put(Constants.ID, rs.getInt("IMAGE_ID"));
            image.put(ImageServlet.IMAGE_DATA, rs.getBytes(2));
            image.put(ImageServlet.IMAGE_MIME_TYPE, rs.getString(3));
            return image;
        }
    }
}
