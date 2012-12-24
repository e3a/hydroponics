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

import org.hydroponics.web.model.FertilizerEditBean;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * The persistence facade.
 */
public interface HydroponicsDao {

    public SqlRowSet getCalibreList(int grow);
    public SqlRowSet getCurrentList(int grow);
    public SqlRowSet getMoistureList(int grow);
    public SqlRowSet getFertilizerList(int grow);

    public Map<String, Object> getCalibre();

    public Collection<Map<String, Object>> getGrowList();
    public Map<String, Object> getCurrentGrow();
    
    public Map<String, Object> getGrowById(int id);

    public void saveGrow(Map<String, Object> grow);
    public void deleteGrow(int id);
    public void saveCalibre(CalibreEvent calibreEvent);
    public void saveFertilizer(FertilizerEditBean fertilizer);

    public Map<String, Object> getImageById(int id, String type);
    public Collection<Integer> getImages(int grow);
    public void saveImage(int grow, MultipartFile image);

    public String getSwitchName(int id);
    public void saveSwitchName(int id, String name);
}