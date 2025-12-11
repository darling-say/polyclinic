package com.polyclinic.registry;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDAO {
    private DatabaseManager dbManager;

    public PatientDAO() {
        this.dbManager = new DatabaseManager();
    }

    // Получить всех пациентов
    public List<Patient> getAllPatients() throws PolyclinicException {
        List<Patient> patients = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM patients");

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                String secondName = resultSet.getString("second_name");

                // Если в БД NULL или пустая строка - делаем пустую строку
                if (secondName == null) {
                    secondName = "";
                }

                int policy = resultSet.getInt("policy_number");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");

                // ДАТА МОЖЕТ БЫТЬ NULL
                LocalDate birthDate = null;
                java.sql.Date sqlDate = resultSet.getDate("date_of_birth");
                if (sqlDate != null) {
                    birthDate = sqlDate.toLocalDate();
                }

                Patient patient = new Patient(name, surname, secondName, policy, phone, address, birthDate);
                patients.add(patient);
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при загрузке списка пациентов: " + e.getMessage(),
                    e
            );
        }

        return patients;
    }

    // Добавить нового пациента
    public boolean addPatient(Patient patient) throws PolyclinicException {
        try {
            // Проверка на существующий номер полиса
            if (policyNumberExists(patient.getPolicy())) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "Номер полиса занят"
                );
            }

            // Проверка на существующий номер телефона
            if (phoneNumberExists(patient.getPhone())) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "Номер телефона уже зарегистрирован"
                );
            }

            String sql = "INSERT INTO patients (name, surname, second_name, policy_number, phone, address, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?)";

            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patient.getName());
            statement.setString(2, patient.getSurname());

            // ОТЧЕСТВО: сохраняем как пустую строку если null или пустое
            String secondName = patient.getSecondName();
            statement.setString(3, (secondName != null && !secondName.trim().isEmpty()) ? secondName : "");

            statement.setInt(4, patient.getPolicy());
            statement.setString(5, patient.getPhone());
            statement.setString(6, patient.getAddress());

            // ДАТА РОЖДЕНИЯ: может быть null
            LocalDate birthDate = patient.getDateOfBirth();
            if (birthDate != null) {
                statement.setDate(7, java.sql.Date.valueOf(birthDate));
            } else {
                statement.setNull(7, java.sql.Types.DATE);
            }

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Дополнительная проверка на случай, если проверки выше не сработали
            if (e.getMessage().toLowerCase().contains("policy_number")) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "Номер полиса занят"
                );
            } else if (e.getMessage().toLowerCase().contains("phone")) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "Номер телефона уже зарегистрирован"
                );
            } else {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.DATABASE_ERROR,
                        "Ошибка при добавлении пациента: " + e.getMessage(),
                        e
                );
            }
        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при добавлении пациента: " + e.getMessage(),
                    e
            );
        }
    }

    // Удалить пациента по номеру полиса
    public boolean deletePatient(int policyNumber) throws PolyclinicException {
        try {
            // Сначала проверяем, есть ли связанные записи на приём
            if (hasAppointments(policyNumber)) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Нельзя удалить пациента, у которого есть записи на приём. Полис: " + policyNumber
                );
            }

            String sql = "DELETE FROM patients WHERE policy_number = ?";

            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, policyNumber);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.PATIENT_NOT_FOUND,
                        "Пациент с полисом " + policyNumber + " не найден"
                );
            }

            return true;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при удалении пациента: " + e.getMessage(),
                    e
            );
        }
    }

    // Метод для проверки существования номера полиса
    public boolean policyNumberExists(int policyNumber) throws PolyclinicException {
        String sql = "SELECT COUNT(*) FROM patients WHERE policy_number = ?";

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, policyNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке номера полиса: " + e.getMessage());
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR, "Не удалось проверить номер полиса", e);
        }

        return false;
    }

    // Метод для проверки существования номера полиса при исключении текущего пациента
    public boolean policyNumberExists(int policyNumber, int excludePolicy) throws PolyclinicException {
        String sql = "SELECT COUNT(*) FROM patients WHERE policy_number = ? AND policy_number != ?";

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, policyNumber);
            pstmt.setInt(2, excludePolicy);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке номера полиса: " + e.getMessage());
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR, "Не удалось проверить номер полиса", e);
        }

        return false;
    }

    // Метод для проверки при добавлении (без исключений)
    public boolean phoneNumberExists(String phoneNumber) throws PolyclinicException {
        String sql = "SELECT COUNT(*) FROM patients WHERE phone = ?";

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phoneNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке номера телефона: " + e.getMessage());
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR, "Не удалось проверить номер телефона", e);
        }

        return false;
    }

    // Метод для проверки при редактировании (исключая текущего пациента)
    public boolean phoneNumberExists(String phoneNumber, int excludePolicy) throws PolyclinicException {
        System.out.println("DEBUG: Проверка телефона '" + phoneNumber + "' исключая пациента с полисом: " + excludePolicy);

        String sql = "SELECT COUNT(*) FROM patients WHERE phone = ? AND policy_number != ?";

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phoneNumber);
            pstmt.setInt(2, excludePolicy);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("DEBUG: Найдено записей: " + count);
                    return count > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке номера телефона: " + e.getMessage());
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR, "Не удалось проверить номер телефона", e);
        }

        return false;
    }

    // Проверить, есть ли у пациента записи на приём
    private boolean hasAppointments(int policyNumber) throws SQLException {
        Connection connection = dbManager.getConnection();
        String sql = "SELECT COUNT(*) FROM appointments WHERE patient_policy = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, policyNumber);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }

        return false;
    }

    // Получить пациента по номеру полиса
    public Patient getPatientByPolicy(int policyNumber) throws PolyclinicException {
        Patient patient = null;

        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM patients WHERE policy_number = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, policyNumber);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                patient = new Patient();
                patient.setName(resultSet.getString("name"));
                patient.setSurname(resultSet.getString("surname"));
                patient.setSecondName(resultSet.getString("second_name"));
                patient.setPolicy(resultSet.getInt("policy_number"));
                patient.setPhone(resultSet.getString("phone"));
                patient.setAddress(resultSet.getString("address"));

                // Обработка даты рождения (может быть null)
                java.sql.Date birthDateSql = resultSet.getDate("date_of_birth");
                if (birthDateSql != null) {
                    patient.setDateOfBirth(birthDateSql.toLocalDate());
                }
            } else {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.PATIENT_NOT_FOUND,
                        "Пациент с полисом " + policyNumber + " не найден"
                );
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при поиске пациента по полису: " + e.getMessage(),
                    e
            );
        }

        return patient;
    }

    // Обновить данные пациента
    public boolean updatePatient(Patient patient) throws PolyclinicException {
        try {
            // Проверка на существующий номер телефона (исключая текущего пациента)
            if (phoneNumberExists(patient.getPhone(), patient.getPolicy())) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "Номер телефона уже зарегистрирован у другого пациента"
                );
            }

            String sql = "UPDATE patients SET name = ?, surname = ?, second_name = ?, phone = ?, address = ?, date_of_birth = ? WHERE policy_number = ?";

            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patient.getName());
            statement.setString(2, patient.getSurname());
            statement.setString(3, patient.getSecondName());
            statement.setString(4, patient.getPhone());
            statement.setString(5, patient.getAddress());

            if (patient.getDateOfBirth() != null) {
                statement.setDate(6, java.sql.Date.valueOf(patient.getDateOfBirth()));
            } else {
                statement.setNull(6, java.sql.Types.DATE);
            }

            statement.setInt(7, patient.getPolicy());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.PATIENT_NOT_FOUND,
                        "Пациент с полисом " + patient.getPolicy() + " не найден для обновления"
                );
            }

            return rowsUpdated > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Обработка нарушения уникальности при обновлении
            if (e.getMessage().toLowerCase().contains("policy_number")) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "Номер полиса занят"
                );
            } else if (e.getMessage().toLowerCase().contains("phone")) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "Номер телефона уже зарегистрирован у другого пациента"
                );
            } else {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.DATABASE_ERROR,
                        "Ошибка при обновлении пациента: " + e.getMessage(),
                        e
                );
            }
        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при обновлении пациента: " + e.getMessage(),
                    e
            );
        }
    }

    // Поиск пациентов по фамилии
    public List<Patient> searchPatientsBySurname(String surname) throws PolyclinicException {
        List<Patient> patients = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM patients WHERE surname LIKE ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, surname + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Patient patient = new Patient();
                patient.setName(resultSet.getString("name"));
                patient.setSurname(resultSet.getString("surname"));
                patient.setSecondName(resultSet.getString("second_name"));
                patient.setPolicy(resultSet.getInt("policy_number"));
                patient.setPhone(resultSet.getString("phone"));
                patient.setAddress(resultSet.getString("address"));

                java.sql.Date birthDateSql = resultSet.getDate("date_of_birth");
                if (birthDateSql != null) {
                    patient.setDateOfBirth(birthDateSql.toLocalDate());
                }

                patients.add(patient);
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при поиске пациентов по фамилии: " + e.getMessage(),
                    e
            );
        }

        return patients;
    }
}