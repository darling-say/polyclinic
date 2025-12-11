package com.polyclinic.registry;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private DatabaseManager dbManager;

    public AppointmentDAO() {
        this.dbManager = new DatabaseManager();
    }

    // Создать новую запись на приём
    public boolean createAppointment(Appointment appointment) throws PolyclinicException {
        AppLogger.debug("Создание записи на приём: врач ID=" + appointment.getDoctorId() +
                ", пациент полис=" + appointment.getPatientPolicy() +
                ", время=" + appointment.getAppointmentDateTime());

        // Проверка 1: Нельзя создавать записи на прошедшую дату
        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            AppLogger.warning("Попытка создания записи на прошедшую дату: " + appointment.getAppointmentDateTime());
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.VALIDATION_ERROR,
                    "Нельзя создавать записи на прошедшую дату"
            );
        }

        // Проверка 2: У пациента не должно быть другой записи на это время
        System.out.println("Проверка занятости пациента...");
        if (isPatientBusy(appointment.getPatientPolicy(), appointment.getAppointmentDateTime())) {
            System.out.println("Пациент уже занят в это время!");
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.VALIDATION_ERROR,
                    "У пациента уже есть другая запись на это время"
            );
        }

        // Проверка 3: У пациента не должно быть записей в пределах 30-минутного интервала
        System.out.println("Проверка временного слота пациента...");
        if (hasPatientTimeSlotConflict(appointment.getPatientPolicy(), appointment.getAppointmentDateTime())) {
            System.out.println("Пациент уже занят в соседнее время!");
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.VALIDATION_ERROR,
                    "У пациента уже есть запись на это время или в соседние 30 минут (прием длится 30 минут)"
            );
        }

        try {
            // Проверяем, не занято ли это время у врача
            if (!isTimeSlotAvailable(appointment.getDoctorId(), appointment.getAppointmentDateTime())) {
                AppLogger.warning("Время уже занято у врача ID=" + appointment.getDoctorId() +
                        " на " + appointment.getAppointmentDateTime());
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.APPOINTMENT_CONFLICT,
                        "Время " + appointment.getAppointmentDateTime() + " уже занято у врача ID " +
                                appointment.getDoctorId()
                );
            }

            Connection connection = dbManager.getConnection();
            String sql = "INSERT INTO appointments (doctor_id, patient_policy, appointment_datetime, notes) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, appointment.getDoctorId());
            statement.setInt(2, appointment.getPatientPolicy());
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            statement.setString(4, appointment.getNotes());

            System.out.println("Выполняем SQL: INSERT INTO appointments...");
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                AppLogger.info("Запись успешно создана в БД, ID=" + appointment.getId());
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    appointment.setId(generatedKeys.getInt(1));
                    System.out.println("Запись создана успешно! ID: " + appointment.getId());
                }
                statement.close();
                connection.close();
                return true;
            }

            statement.close();
            connection.close();
            return false;

        } catch (SQLException e) {
            AppLogger.error("SQL ошибка при создании записи на приём", e);
            // Проверяем, не дублируется ли запись
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("unique constraint")) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.APPOINTMENT_CONFLICT,
                        "У пациента уже есть запись на это время",
                        e
                );
            }

            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при создании записи на приём: " + e.getMessage(),
                    e
            );
        }
    }

    // Проверяет, есть ли у пациента уже запись на это время
    public boolean isPatientBusy(int patientPolicy, LocalDateTime appointmentDateTime) throws PolyclinicException {
        System.out.println("=== Проверка isPatientBusy ===");
        System.out.println("Пациент полис: " + patientPolicy);
        System.out.println("Время проверки: " + appointmentDateTime);

        String sql = "SELECT COUNT(*) as count FROM appointments WHERE patient_policy = ? AND appointment_datetime = ?";

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientPolicy);
            stmt.setTimestamp(2, Timestamp.valueOf(appointmentDateTime));

            System.out.println("SQL: " + sql);
            System.out.println("Параметры: " + patientPolicy + ", " + appointmentDateTime);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Найдено записей: " + count);
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке занятости пациента: " + e.getMessage());
            e.printStackTrace();
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке занятости пациента: " + e.getMessage());
        }

        System.out.println("Записей не найдено");
        return false;
    }

    // Более строгая проверка - учитывает, что прием длится 30 минут
    public boolean hasPatientTimeSlotConflict(int patientPolicy, LocalDateTime appointmentDateTime)
            throws PolyclinicException {

        System.out.println("=== Проверка hasPatientTimeSlotConflict ===");
        System.out.println("Пациент полис: " + patientPolicy);
        System.out.println("Время проверки: " + appointmentDateTime);

        // Предполагаем, что прием длится 30 минут
        LocalDateTime startTime = appointmentDateTime.minusMinutes(29); // за 29 минут до
        LocalDateTime endTime = appointmentDateTime.plusMinutes(30);    // через 30 минут после

        System.out.println("Проверяемый интервал: " + startTime + " - " + endTime);

        String sql = """
        SELECT COUNT(*) as count FROM appointments 
        WHERE patient_policy = ? 
        AND appointment_datetime BETWEEN ? AND ?
        """;

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientPolicy);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));

            System.out.println("SQL: " + sql);
            System.out.println("Параметры: " + patientPolicy + ", " + startTime + ", " + endTime);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Найдено записей в интервале: " + count);
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке временных слотов пациента: " + e.getMessage());
            e.printStackTrace();
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке временных слотов пациента: " + e.getMessage());
        }

        System.out.println("Конфликтов не найдено");
        return false;
    }

    // Получить все записи врача на определенную дату
    public List<Appointment> getAppointmentsByDoctorAndDate(int doctorId, LocalDate date) throws PolyclinicException {
        List<Appointment> appointments = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM appointments WHERE doctor_id = ? " +
                    "AND DATE(appointment_datetime) = ? " +
                    "ORDER BY appointment_datetime";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);
            statement.setDate(2, Date.valueOf(date));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(resultSet.getInt("id"));
                appointment.setDoctorId(resultSet.getInt("doctor_id"));
                appointment.setPatientPolicy(resultSet.getInt("patient_policy"));
                appointment.setAppointmentDateTime(
                        resultSet.getTimestamp("appointment_datetime").toLocalDateTime()
                );
                appointment.setNotes(resultSet.getString("notes"));

                // Проверяем, есть ли колонка completed в таблице
                try {
                    appointment.setCompleted(resultSet.getBoolean("completed"));
                    appointment.setStatus(resultSet.getBoolean("completed") ? "выполнено" : "запланировано");
                } catch (SQLException e) {
                    // Колонки может не быть - игнорируем
                }

                appointments.add(appointment);
            }

            statement.close();
            connection.close();

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при загрузке записей врача " + doctorId + " на дату " + date + ": " + e.getMessage(),
                    e
            );
        }

        return appointments;
    }

    // Проверить доступность времени
    public boolean isTimeSlotAvailable(int doctorId, LocalDateTime dateTime) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? " +
                    "AND appointment_datetime = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);
            statement.setTimestamp(2, Timestamp.valueOf(dateTime));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println("Время у врача " + doctorId + " на " + dateTime + " занято " + count + " раз");
                return count == 0;
            }

            statement.close();
            connection.close();
            return true;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке доступности времени: " + e.getMessage(),
                    e
            );
        }
    }

    // Получить свободные временные слоты врача на дату
    public List<LocalTime> getAvailableTimeSlots(int doctorId, LocalDate date) throws PolyclinicException {
        List<LocalTime> availableSlots = new ArrayList<>();

        try {
            // 1. Получить расписание врача на этот день недели
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            List<Schedule> schedules = scheduleDAO.getScheduleByDoctor(doctorId);

            int dayOfWeek = date.getDayOfWeek().getValue();
            Schedule daySchedule = schedules.stream()
                    .filter(s -> s.getDayOfWeek().getValue() == dayOfWeek)
                    .findFirst()
                    .orElse(null);

            if (daySchedule == null) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.SCHEDULE_CONFLICT,
                        "Врач ID " + doctorId + " не работает в " + getDayName(dayOfWeek)
                );
            }

            // 2. Получить занятые слоты
            List<Appointment> existingAppointments = getAppointmentsByDoctorAndDate(doctorId, date);

            // 3. Сгенерировать все возможные слоты (по 30 минут)
            LocalTime startTime = daySchedule.getStartTime();
            LocalTime endTime = daySchedule.getEndTime();
            LocalTime currentTime = startTime;

            while (currentTime.isBefore(endTime)) {
                LocalTime slotTime = currentTime;

                // Проверяем, занят ли этот слот
                boolean isOccupied = existingAppointments.stream()
                        .anyMatch(a -> a.getAppointmentDateTime().toLocalTime().equals(slotTime));

                if (!isOccupied) {
                    availableSlots.add(slotTime);
                }

                currentTime = currentTime.plusMinutes(30); // Слоты по 30 минут
            }

            if (availableSlots.isEmpty()) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.APPOINTMENT_CONFLICT,
                        "Нет свободных слотов у врача ID " + doctorId + " на " + date
                );
            }

        } catch (PolyclinicException e) {
            // Пробрасываем PolyclinicException дальше
            throw e;
        } catch (Exception e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при получении свободных слотов: " + e.getMessage(),
                    e
            );
        }

        return availableSlots;
    }

    // Получить все записи пациента
    public List<Appointment> getAppointmentsByPatient(int patientPolicy) throws PolyclinicException {
        List<Appointment> appointments = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT a.*, d.name as doctor_name, d.surname as doctor_surname " +
                    "FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.patient_policy = ? " +
                    "ORDER BY a.appointment_datetime DESC";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, patientPolicy);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(resultSet.getInt("id"));
                appointment.setDoctorId(resultSet.getInt("doctor_id"));
                appointment.setPatientPolicy(resultSet.getInt("patient_policy"));
                appointment.setAppointmentDateTime(
                        resultSet.getTimestamp("appointment_datetime").toLocalDateTime()
                );
                appointment.setNotes(resultSet.getString("notes"));

                try {
                    appointment.setCompleted(resultSet.getBoolean("completed"));
                } catch (SQLException e) {
                    // Колонки может не быть
                }

                appointments.add(appointment);
            }

            statement.close();
            connection.close();

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при загрузке записей пациента с полисом " + patientPolicy + ": " + e.getMessage(),
                    e
            );
        }

        return appointments;
    }

    // Получить все записи
    public List<Appointment> getAllAppointments() throws PolyclinicException {
        List<Appointment> appointments = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM appointments ORDER BY appointment_datetime DESC";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(resultSet.getInt("id"));
                appointment.setDoctorId(resultSet.getInt("doctor_id"));
                appointment.setPatientPolicy(resultSet.getInt("patient_policy"));
                appointment.setAppointmentDateTime(
                        resultSet.getTimestamp("appointment_datetime").toLocalDateTime()
                );
                appointment.setNotes(resultSet.getString("notes"));

                appointments.add(appointment);
            }

            statement.close();
            connection.close();

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при получении всех записей: " + e.getMessage(),
                    e
            );
        }

        return appointments;
    }

    // Удалить запись
    public boolean deleteAppointment(int appointmentId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "DELETE FROM appointments WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, appointmentId);

            int rowsDeleted = statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowsDeleted == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Запись с ID " + appointmentId + " не найдена"
                );
            }

            return true;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при удалении записи: " + e.getMessage(),
                    e
            );
        }
    }

    // Получить запись по ID
    public Appointment getAppointmentById(int appointmentId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM appointments WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, appointmentId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(resultSet.getInt("id"));
                appointment.setDoctorId(resultSet.getInt("doctor_id"));
                appointment.setPatientPolicy(resultSet.getInt("patient_policy"));
                appointment.setAppointmentDateTime(
                        resultSet.getTimestamp("appointment_datetime").toLocalDateTime()
                );
                appointment.setNotes(resultSet.getString("notes"));

                try {
                    appointment.setCompleted(resultSet.getBoolean("completed"));
                } catch (SQLException e) {
                    // Колонки может не быть
                }

                statement.close();
                connection.close();
                return appointment;
            } else {
                statement.close();
                connection.close();
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Запись с ID " + appointmentId + " не найдена"
                );
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при получении записи по ID: " + e.getMessage(),
                    e
            );
        }
    }

    // Обновить запись
    public boolean updateAppointment(Appointment appointment) throws PolyclinicException {
        try {
            // Проверка занятости пациента для обновляемой записи (исключая саму запись)
            if (hasPatientTimeSlotConflictForUpdate(appointment.getPatientPolicy(),
                    appointment.getAppointmentDateTime(), appointment.getId())) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.VALIDATION_ERROR,
                        "У пациента уже есть другая запись на это время"
                );
            }

            Connection connection = dbManager.getConnection();
            String sql = "UPDATE appointments SET doctor_id = ?, patient_policy = ?, " +
                    "appointment_datetime = ?, notes = ?, completed = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, appointment.getDoctorId());
            statement.setInt(2, appointment.getPatientPolicy());
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            statement.setString(4, appointment.getNotes());
            statement.setBoolean(5, appointment.isCompleted());
            statement.setInt(6, appointment.getId());

            int rowsUpdated = statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowsUpdated == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Запись с ID " + appointment.getId() + " не найдена для обновления"
                );
            }

            return true;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при обновлении записи: " + e.getMessage(),
                    e
            );
        }
    }

    // Проверка конфликта для обновления (исключая текущую запись)
    private boolean hasPatientTimeSlotConflictForUpdate(int patientPolicy, LocalDateTime appointmentDateTime,
                                                        int excludeAppointmentId) throws PolyclinicException {

        LocalDateTime startTime = appointmentDateTime.minusMinutes(29);
        LocalDateTime endTime = appointmentDateTime.plusMinutes(30);

        String sql = """
        SELECT COUNT(*) as count FROM appointments 
        WHERE patient_policy = ? 
        AND appointment_datetime BETWEEN ? AND ?
        AND id != ?
        """;

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientPolicy);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));
            stmt.setInt(4, excludeAppointmentId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке временных слотов пациента для обновления: " + e.getMessage());
        }

        return false;
    }

    // Вспомогательный метод для получения названия дня недели
    private String getDayName(int dayNumber) {
        switch(dayNumber) {
            case 1: return "Понедельник";
            case 2: return "Вторник";
            case 3: return "Среда";
            case 4: return "Четверг";
            case 5: return "Пятница";
            case 6: return "Суббота";
            case 7: return "Воскресенье";
            default: return "Неизвестный день";
        }
    }
}