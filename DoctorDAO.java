package com.polyclinic.registry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    private DatabaseManager dbManager;

    public DoctorDAO() {
        this.dbManager = new DatabaseManager();
    }

    // Получить всех врачей
    public List<Doctor> getAllDoctors() throws PolyclinicException {
        List<Doctor> doctors = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM doctors ORDER BY surname, name");

            while (resultSet.next()) {
                Doctor doctor = new Doctor(
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("second_name"),
                        resultSet.getInt("id"),
                        resultSet.getString("specialty")
                );
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при загрузке списка врачей: " + e.getMessage(),
                    e
            );
        }

        return doctors;
    }

    // Добавить нового врача
    public boolean addDoctor(Doctor doctor) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "INSERT INTO doctors (name, surname, second_name, specialty) VALUES (?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, doctor.getName());
            statement.setString(2, doctor.getSurname());
            statement.setString(3, doctor.getSecondName());
            statement.setString(4, doctor.getSpecialty());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // Получаем сгенерированный ID и устанавливаем его врачу
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    doctor.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при добавлении врача: " + e.getMessage(),
                    e
            );
        }
    }

    // Удалить врача по ID
    public boolean deleteDoctor(int id) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();

            // Сначала проверяем, нет ли записей у этого врача
            String checkSql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, id);
            ResultSet rs = checkStatement.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Нельзя удалить врача, у которого есть записи на приём. ID врача: " + id
                );
            }

            // Проверяем, есть ли расписание у врача
            String checkScheduleSql = "SELECT COUNT(*) FROM schedules WHERE doctor_id = ?";
            PreparedStatement checkScheduleStmt = connection.prepareStatement(checkScheduleSql);
            checkScheduleStmt.setInt(1, id);
            ResultSet rsSchedule = checkScheduleStmt.executeQuery();

            if (rsSchedule.next() && rsSchedule.getInt(1) > 0) {
                // Удаляем расписание врача
                String deleteScheduleSql = "DELETE FROM schedules WHERE doctor_id = ?";
                PreparedStatement deleteScheduleStmt = connection.prepareStatement(deleteScheduleSql);
                deleteScheduleStmt.setInt(1, id);
                deleteScheduleStmt.executeUpdate();
            }

            // Удаляем врача
            String sql = "DELETE FROM doctors WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.DOCTOR_NOT_FOUND,
                        "Врач с ID " + id + " не найден"
                );
            }

            return true;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при удалении врача: " + e.getMessage(),
                    e
            );
        }
    }

    // Обновить данные врача
    public boolean updateDoctor(Doctor doctor) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "UPDATE doctors SET name = ?, surname = ?, second_name = ?, specialty = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, doctor.getName());
            statement.setString(2, doctor.getSurname());
            statement.setString(3, doctor.getSecondName());
            statement.setString(4, doctor.getSpecialty());
            statement.setInt(5, doctor.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.DOCTOR_NOT_FOUND,
                        "Врач с ID " + doctor.getId() + " не найден для обновления"
                );
            }

            return rowsUpdated > 0;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при обновлении врача: " + e.getMessage(),
                    e
            );
        }
    }

    // Найти врача по ID
    public Doctor getDoctorById(int id) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM doctors WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Doctor(
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("second_name"),
                        resultSet.getInt("id"),
                        resultSet.getString("specialty")
                );
            } else {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.DOCTOR_NOT_FOUND,
                        "Врач с ID " + id + " не найден"
                );
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при поиске врача: " + e.getMessage(),
                    e
            );
        }
    }

    // Получить врачей для ComboBox
    public List<Doctor> getDoctorsForComboBox() throws PolyclinicException {
        List<Doctor> doctors = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT id, surname, name, second_name, specialty FROM doctors ORDER BY surname, name"
            );

            while (resultSet.next()) {
                Doctor doctor = new Doctor(
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("second_name"),
                        resultSet.getInt("id"),
                        resultSet.getString("specialty")
                );
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при загрузке врачей для выпадающего списка: " + e.getMessage(),
                    e
            );
        }

        return doctors;
    }

    // Поиск врачей по специализации
    public List<Doctor> getDoctorsBySpecialty(String specialty) throws PolyclinicException {
        List<Doctor> doctors = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM doctors WHERE specialty = ? ORDER BY surname, name";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, specialty);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Doctor doctor = new Doctor(
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("second_name"),
                        resultSet.getInt("id"),
                        resultSet.getString("specialty")
                );
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при поиске врачей по специализации: " + e.getMessage(),
                    e
            );
        }

        return doctors;
    }

    // Проверить, существует ли врач с таким ID
    public boolean doctorExists(int id) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT COUNT(*) FROM doctors WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке существования врача: " + e.getMessage(),
                    e
            );
        }
    }
}