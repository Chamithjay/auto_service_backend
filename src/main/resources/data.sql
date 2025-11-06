-- 1. INSERT USERS (ADMIN, EMPLOYEE, CUSTOMER)
INSERT INTO users (id, user_type, username, email, password, role, requires_password_change) VALUES
                                                                                                 (1, 'EMPLOYEE', 'ad', 'ad@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE', false),
                                                                                                 (2, 'EMPLOYEE', 'emp_john', 'john@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE', false),
                                                                                                 (3, 'EMPLOYEE', 'emp_jane', 'jane@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE', false),
                                                                                                 (4, 'CUSTOMER', 'cust_alice', 'alice@gmail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER', false);

-- Update employee-specific fields
UPDATE users SET employee_id = 'E1001', position = 'Senior Mechanic' WHERE username = 'emp_john';
UPDATE users SET employee_id = 'E1002', position = 'Mechanic' WHERE username = 'emp_jane';

-- 2. INSERT SERVICES AND MODIFICATIONS (Note the quotes for the table name)
INSERT INTO "services and modifications" (service_item_id, service_item_name, vehicle_type, required_employee_count, service_item_cost, service_item_type, estimated_duration_minutes) VALUES
                                                                                                                                                                                           (1, 'Premium Oil Change', 'CAR', 1, 80.00, 'SERVICE', 45),
                                                                                                                                                                                           (2, 'Brake Pad Replacement', 'CAR', 1, 250.00, 'SERVICE', 90),
                                                                                                                                                                                           (3, 'Tire Rotation', 'CAR', 1, 50.00, 'SERVICE', 30),
                                                                                                                                                                                           (4, 'Spoiler Installation', 'CAR', 2, 400.00, 'MODIFICATION', 120);

-- 3. INSERT VEHICLES (Must link to a customer ID)
INSERT INTO vehicles (vehicle_id, vehicle_name, registration_no, vehicle_type, model, customer_id, created_at, updated_at) VALUES
                                                                                                                               (1, 'Alices Toyota', 'ABC-123', 'CAR', 'Corolla', 4, NOW(), NOW()),
                                                                                                                               (2, 'Alices Honda', 'XYZ-789', 'CAR', 'Civic', 4, NOW(), NOW());

-- 4. INSERT APPOINTMENTS (Dates are in late October 2025)
-- *** THIS LINE IS NOW FIXED to use "vehicl_id" to match your model's typo ***
INSERT INTO appointments (appointment_id, appointment_date, appointment_start_time, appointment_end_time, status, total_cost, vehicl_id) VALUES
                                                                                                                                             (1, '2025-10-25', '10:00:00', '11:30:00', 'COMPLETED', 330.00, 1),
                                                                                                                                             (2, '2025-10-28', '14:00:00', '16:00:00', 'COMPLETED', 400.00, 1),
                                                                                                                                             (3, '2025-10-30', '09:00:00', '09:30:00', 'COMPLETED', 50.00, 2);

-- 5. INSERT APPOINTMENT JOBS (Links appointment to services)
INSERT INTO "appointment jobs" (appointment_job_id, appointment_id, service_item_id, job_status) VALUES
                                                                                                     (1, 1, 1, 'COMPLETED'), -- Appt 1 gets Oil Change
                                                                                                     (2, 1, 2, 'COMPLETED'), -- Appt 1 gets Brake Replacement
                                                                                                     (3, 2, 4, 'COMPLETED'), -- Appt 2 gets Spoiler
                                                                                                     (4, 3, 3, 'COMPLETED'); -- Appt 3 gets Tire Rotation

-- 6. INSERT JOB ASSIGNMENTS (Links jobs to employees)
INSERT INTO "job assignments" (job_assignment_id, appointment_job_id, employee_id, start_time, end_time) VALUES
                                                                                                             (1, 1, 2, '10:00:00', '10:45:00'), -- Job 1 (Oil Change) done by John (ID 2)
                                                                                                             (2, 2, 2, '10:45:00', '11:30:00'), -- Job 2 (Brakes) done by John (ID 2)
                                                                                                             (3, 3, 3, '14:00:00', '16:00:00'), -- Job 3 (Spoiler) done by Jane (ID 3)
                                                                                                             (4, 4, 3, '09:00:00', '09:30:00'); -- Job 4 (Tires) done by Jane (ID 3)

-- 7. INSERT LEAVES (Dates in late October 2025)
INSERT INTO leaves (leave_id, leave_type, leave_date, leave_status, employee_id) VALUES
                                                                                     (1, 'FULLDAY', '2025-10-20', 'APPROVED', 2),
                                                                                     (2, 'HALFDAY_MORNING', '2025-10-22', 'APPROVED', 3),
                                                                                     (3, 'FULLDAY', '2025-10-23', 'APPROVED', 3);

-- This is a standard command for PostgreSQL to update the auto-increment sequences
-- It ensures that new items you create in the app don't have ID conflicts
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE(MAX(id), 1), true) FROM users;
SELECT setval(pg_get_serial_sequence('"services and modifications"', 'service_item_id'), COALESCE(MAX(service_item_id), 1), true) FROM "services and modifications";
SELECT setval(pg_get_serial_sequence('vehicles', 'vehicle_id'), COALESCE(MAX(vehicle_id), 1), true) FROM vehicles;
SELECT setval(pg_get_serial_sequence('appointments', 'appointment_id'), COALESCE(MAX(appointment_id), 1), true) FROM appointments;
SELECT setval(pg_get_serial_sequence('"appointment jobs"', 'appointment_job_id'), COALESCE(MAX(appointment_job_id), 1), true) FROM "appointment jobs";
SELECT setval(pg_get_serial_sequence('"job assignments"', 'job_assignment_id'), COALESCE(MAX(job_assignment_id), 1), true) FROM "job assignments";
SELECT setval(pg_get_serial_sequence('leaves', 'leave_id'), COALESCE(MAX(leave_id), 1), true) FROM leaves;