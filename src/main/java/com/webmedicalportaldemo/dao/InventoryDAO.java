package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.PharmacyItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InventoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public InventoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PharmacyItem> rowMapper = new RowMapper<PharmacyItem>() {
        @Override
        public PharmacyItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            PharmacyItem item = new PharmacyItem();
            item.setItemId(rs.getInt("item_id"));
            item.setDrugName(rs.getString("drug_name"));
            item.setUnitPrice(rs.getDouble("unit_price"));
            item.setStockQuantity(rs.getInt("stock_quantity"));
            return item;
        }
    };

    public List<PharmacyItem> getAllItems() {
        String sql = "SELECT * FROM pharmacy_inventory ORDER BY drug_name ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public PharmacyItem getItemById(int itemId) {
        String sql = "SELECT * FROM pharmacy_inventory WHERE item_id = ?";
        List<PharmacyItem> items = jdbcTemplate.query(sql, rowMapper, itemId);
        return items.isEmpty() ? null : items.get(0);
    }

    public boolean insertItem(PharmacyItem item) {
        String sql = "INSERT INTO pharmacy_inventory (drug_name, unit_price, stock_quantity) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, item.getDrugName(), item.getUnitPrice(), item.getStockQuantity()) > 0;
    }

    public boolean updateItem(PharmacyItem item) {
        String sql = "UPDATE pharmacy_inventory SET drug_name = ?, unit_price = ?, stock_quantity = ? WHERE item_id = ?";
        return jdbcTemplate.update(sql, item.getDrugName(), item.getUnitPrice(), item.getStockQuantity(), item.getItemId()) > 0;
    }

    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM pharmacy_inventory WHERE item_id = ?";
        return jdbcTemplate.update(sql, itemId) > 0;
    }
}