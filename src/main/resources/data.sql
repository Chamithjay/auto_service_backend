-- src/main/resources/data.sql

-- 1. INSERT USERS (ADMINS, EMPLOYEES, CUSTOMERS)
-- We need these IDs first. Passwords are hashed with "password"
INSERT INTO users (user_type, username, email, password, role)
VALUES
    ('ADMIN', 'admin', 'admin@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'ADMIN'),
    ('EMPLOYEE', 'emp_john', 'john@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE'),
    ('CUSTOMER', 'cust_alice', 'alice@gmail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER');

-- Update Employee/Admin specific fields
UPDATE users SET employee_id = 'E1001', position = 'Senior Mechanic' WHERE username = 'emp_john';
UPDATE users SET access_level = 1 WHERE username = 'admin';


-- 2. INSERT SERVICES
INSERT INTO services_modifications (service_item_name, vehicle_type, required_employee_count, service_item_cost, service_item_type, estimated_duration_minutes)
VALUES
    ('Premium Oil Change', 'CAR', 1, 5000.00, 'SERVICE', 45),
    ('Brake Pad Replacement', 'CAR', 1, 12000.00, 'SERVICE', 90),
    ('Spoiler Installation', 'CAR', 2, 25000.00, 'MODIFICATION', 120);


-- 3. INSERT VEHICLES (Must link to a customer ID)
-- (Assuming 'cust_alice' has ID 3)
INSERT INTO vehicles (vehicle_name, registration_no, vehicle_type, model, customer_id)
VALUES
    ('My Toyota', 'CAR-1234', 'CAR', 'Corolla', 3),
    ('My Honda', 'CAR-5678', 'CAR', 'Civic', 3);


-- 4. INSERT APPOINTMENTS (Must link to customer and vehicle)
-- (Assuming vehicle 'CAR-1234' has ID 1 and 'cust_alice' has ID 3)
INSERT INTO appointments (appointment_date, appointment_start_time, appointment_end_time, status, total_cost, vehicle_id, customer_id)
VALUES
    ('2025-10-28', '10:00:00', '11:30:00', 'COMPLETED', 17000.00, 1, 3),
    ('2025-10-29', '14:00:00', '16:00:00', 'COMPLETED', 25000.00, 1, 3);


-- 5. INSERT APPOINTMENT_JOBS (Links appointment to a service)
-- (Assuming appointment 1 links to services 1 and 2)
-- (Assuming appointment 2 links to service 3)
INSERT INTO appointment_jobs (appointment_id, service_item_id, job_status)
VALUES
    (1, 1, 'COMPLETED'), -- Appt 1 gets Oil Change
    (1, 2, 'COMPLETED'), -- Appt 1 gets Brake Replacement
    (2, 3, 'COMPLETED'); -- Appt 2 gets Spoiler


-- 6. INSERT JOB_ASSIGNMENTS (Links a job to an employee)
-- (Assuming 'emp_john' has ID 2 and the jobs above have IDs 1, 2, 3)
INSERT INTO job_assignments (appointment_job_id, employee_id, start_time, end_time)
VALUES
    (1, 2, '10:00:00', '10:45:00'), -- Job 1 done by John
    (2, 2, '10:45:00', '11:30:00'), -- Job 2 done by John
    (3, 2, '14:00:00', '16:00:00'); -- Job 3 done by John

-- =========================
-- Additional seed data
-- A larger, consistent dataset for more realistic testing
-- Password hash reused for all new users (same as existing)
-- =========================

-- 7. ADD MORE USERS (employees + customers)
INSERT INTO users (user_type, username, email, password, role) VALUES
  ('EMPLOYEE', 'emp_jane', 'jane@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE'),
  ('EMPLOYEE', 'emp_mike', 'mike@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE'),
  ('EMPLOYEE', 'emp_sara', 'sara@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE'),
  ('EMPLOYEE', 'emp_lee', 'lee@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE'),
  ('EMPLOYEE', 'emp_tom', 'tom@autoservice.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'EMPLOYEE'),

  ('CUSTOMER', 'cust_bob', 'bob@gmail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_carol', 'carol@yahoo.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_dave', 'dave@hotmail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_erin', 'erin@outlook.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_fred', 'fred@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_gina', 'gina@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_hank', 'hank@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_ivy', 'ivy@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_jack', 'jack@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_kelly', 'kelly@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_liam', 'liam@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_maya', 'maya@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_noah', 'noah@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER'),
  ('CUSTOMER', 'cust_olivia', 'olivia@mail.com', '$2a$12$4o.gA9jDacpYTRIl/C8LDeu8lqIR.I/oKOu6kpfk.yNlOMf.LGEVq', 'CUSTOMER');

-- Set employee-specific fields for new employees
UPDATE users SET employee_id = 'E1002', position = 'Technician' WHERE username = 'emp_jane';
UPDATE users SET employee_id = 'E1003', position = 'Technician' WHERE username = 'emp_mike';
UPDATE users SET employee_id = 'E1004', position = 'Diagnostics Specialist' WHERE username = 'emp_sara';
UPDATE users SET employee_id = 'E1005', position = 'Electrical Specialist' WHERE username = 'emp_lee';
UPDATE users SET employee_id = 'E1006', position = 'Shop Assistant' WHERE username = 'emp_tom';

-- 8. ADD MANY SERVICES / MODIFICATIONS
INSERT INTO services_modifications (service_item_name, vehicle_type, required_employee_count, service_item_cost, service_item_type, estimated_duration_minutes) VALUES
  ('Full Inspection', 'CAR', 1, 8000.00, 'SERVICE', 60),
  ('Tire Rotation', 'CAR', 1, 3000.00, 'SERVICE', 30),
  ('Wheel Alignment', 'CAR', 1, 10000.00, 'SERVICE', 75),
  ('Battery Replacement', 'CAR', 1, 15000.00, 'SERVICE', 40),
  ('AC Recharge', 'CAR', 1, 7000.00, 'SERVICE', 50),
  ('Performance Chip Tuning', 'CAR', 1, 40000.00, 'MODIFICATION', 180),
  ('Exhaust Upgrade', 'CAR', 2, 22000.00, 'MODIFICATION', 150),
  ('Window Tinting', 'CAR', 1, 6000.00, 'MODIFICATION', 90),
  ('Soft Top Replacement', 'MOTORCYCLE', 1, 9000.00, 'MODIFICATION', 120),
  ('Oil Change (Motorcycle)', 'MOTORCYCLE', 1, 2500.00, 'SERVICE', 30),
  ('Transmission Service', 'CAR', 2, 35000.00, 'SERVICE', 240),
  ('Brake Fluid Flush', 'CAR', 1, 4500.00, 'SERVICE', 45),
  ('Coolant Flush', 'CAR', 1, 4000.00, 'SERVICE', 45),
  ('Headlight Replacement', 'CAR', 1, 2500.00, 'SERVICE', 25),
  ('Custom Paint Touchup', 'CAR', 2, 18000.00, 'MODIFICATION', 300);

-- 9. ADD VEHICLES FOR NEW CUSTOMERS (link via subquery username)
INSERT INTO vehicles (vehicle_name, registration_no, vehicle_type, model, customer_id) VALUES
  ('Bob\'s Ford', 'CAR-2345', 'CAR', 'Focus', (SELECT id FROM users WHERE username='cust_bob')),
  ('Carol\'s BMW', 'CAR-3456', 'CAR', '320i', (SELECT id FROM users WHERE username='cust_carol')),
  ('Dave\'s Truck', 'TRK-9988', 'TRUCK', 'F150', (SELECT id FROM users WHERE username='cust_dave')),
  ('Erin\'s Honda', 'CAR-8765', 'CAR', 'Accord', (SELECT id FROM users WHERE username='cust_erin')),
  ('Fred\'s Civic', 'CAR-4321', 'CAR', 'Civic', (SELECT id FROM users WHERE username='cust_fred')),
  ('Gina\'s Scooter', 'MOTO-1001', 'MOTORCYCLE', 'Vespa', (SELECT id FROM users WHERE username='cust_gina')),
  ('Hank\'s Ram', 'TRK-4455', 'TRUCK', 'Ram 1500', (SELECT id FROM users WHERE username='cust_hank')),
  ('Ivy\'s Mini', 'CAR-7788', 'CAR', 'Cooper', (SELECT id FROM users WHERE username='cust_ivy')),
  ('Jack\'s Tesla', 'CAR-8899', 'CAR', 'Model 3', (SELECT id FROM users WHERE username='cust_jack')),
  ('Kelly\'s Subaru', 'CAR-9900', 'CAR', 'Outback', (SELECT id FROM users WHERE username='cust_kelly')),
  ('Liam\'s Audi', 'CAR-1112', 'CAR', 'A4', (SELECT id FROM users WHERE username='cust_liam')),
  ('Maya\'s Jeep', 'TRK-1314', 'TRUCK', 'Wrangler', (SELECT id FROM users WHERE username='cust_maya')),
  ('Noah\'s Accord', 'CAR-1516', 'CAR', 'Accord', (SELECT id FROM users WHERE username='cust_noah')),
  ('Olivia\'s Corolla', 'CAR-1718', 'CAR', 'Corolla', (SELECT id FROM users WHERE username='cust_olivia'));

-- 10. ADD MANY APPOINTMENTS (linked by vehicle reg and customer username)
-- Dates spread across Nov/Dec 2025 to provide varied dataset
INSERT INTO appointments (appointment_date, appointment_start_time, appointment_end_time, status, total_cost, vehicle_id, customer_id) VALUES
  ('2025-11-01', '09:00:00', '10:30:00', 'COMPLETED', 11000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-2345'), (SELECT id FROM users WHERE username='cust_bob')),
  ('2025-11-02', '11:00:00', '12:00:00', 'COMPLETED', 3000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-3456'), (SELECT id FROM users WHERE username='cust_carol')),
  ('2025-11-03', '13:00:00', '15:30:00', 'COMPLETED', 42000.00, (SELECT id FROM vehicles WHERE registration_no='TRK-9988'), (SELECT id FROM users WHERE username='cust_dave')),
  ('2025-11-04', '08:30:00', '09:30:00', 'COMPLETED', 7000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-8765'), (SELECT id FROM users WHERE username='cust_erin')),
  ('2025-11-05', '10:00:00', '11:00:00', 'COMPLETED', 3000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-4321'), (SELECT id FROM users WHERE username='cust_fred')),
  ('2025-11-06', '14:00:00', '15:00:00', 'COMPLETED', 2500.00, (SELECT id FROM vehicles WHERE registration_no='MOTO-1001'), (SELECT id FROM users WHERE username='cust_gina')),
  ('2025-11-07', '09:00:00', '12:00:00', 'COMPLETED', 46000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-1112'), (SELECT id FROM users WHERE username='cust_liam')),
  ('2025-11-08', '13:00:00', '14:00:00', 'COMPLETED', 4500.00, (SELECT id FROM vehicles WHERE registration_no='CAR-7788'), (SELECT id FROM users WHERE username='cust_ivy')),
  ('2025-11-09', '10:00:00', '11:30:00', 'COMPLETED', 15000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-8899'), (SELECT id FROM users WHERE username='cust_jack')),
  ('2025-11-10', '15:00:00', '17:00:00', 'COMPLETED', 18000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-9900'), (SELECT id FROM users WHERE username='cust_kelly')),
  ('2025-11-11', '09:30:00', '10:15:00', 'COMPLETED', 2500.00, (SELECT id FROM vehicles WHERE registration_no='CAR-1516'), (SELECT id FROM users WHERE username='cust_noah')),
  ('2025-11-12', '11:00:00', '12:00:00', 'COMPLETED', 4000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-1718'), (SELECT id FROM users WHERE username='cust_olivia')),
  ('2025-12-01', '09:00:00', '10:30:00', 'SCHEDULED', 8000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-1234'), (SELECT id FROM users WHERE username='cust_alice')),
  ('2025-12-02', '10:00:00', '11:00:00', 'SCHEDULED', 6000.00, (SELECT id FROM vehicles WHERE registration_no='CAR-5678'), (SELECT id FROM users WHERE username='cust_alice')),
  ('2025-12-03', '14:00:00', '16:30:00', 'SCHEDULED', 22000.00, (SELECT id FROM vehicles WHERE registration_no='TRK-1314'), (SELECT id FROM users WHERE username='cust_maya'));

-- 11. ADD appointment_jobs linking appointments to services (use subqueries to find ids)
-- Example: Bob's appointment: Full Inspection + Wheel Alignment
INSERT INTO appointment_jobs (appointment_id, service_item_id, job_status) VALUES
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-01' AND customer_id=(SELECT id FROM users WHERE username='cust_bob')),
    (SELECT id FROM services_modifications WHERE service_item_name='Full Inspection'),
    'COMPLETED'
  ),
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-01' AND customer_id=(SELECT id FROM users WHERE username='cust_bob')),
    (SELECT id FROM services_modifications WHERE service_item_name='Wheel Alignment'),
    'COMPLETED'
  ),

  -- Carol: Tire Rotation
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-02' AND customer_id=(SELECT id FROM users WHERE username='cust_carol')),
    (SELECT id FROM services_modifications WHERE service_item_name='Tire Rotation'),
    'COMPLETED'
  ),

  -- Dave: Performance Chip Tuning + Exhaust Upgrade
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-03' AND customer_id=(SELECT id FROM users WHERE username='cust_dave')),
    (SELECT id FROM services_modifications WHERE service_item_name='Performance Chip Tuning'),
    'COMPLETED'
  ),
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-03' AND customer_id=(SELECT id FROM users WHERE username='cust_dave')),
    (SELECT id FROM services_modifications WHERE service_item_name='Exhaust Upgrade'),
    'COMPLETED'
  ),

  -- Erin: AC Recharge
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-04' AND customer_id=(SELECT id FROM users WHERE username='cust_erin')),
    (SELECT id FROM services_modifications WHERE service_item_name='AC Recharge'),
    'COMPLETED'
  ),

  -- Fred: Tire Rotation
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-05' AND customer_id=(SELECT id FROM users WHERE username='cust_fred')),
    (SELECT id FROM services_modifications WHERE service_item_name='Tire Rotation'),
    'COMPLETED'
  ),

  -- Gina: Oil Change (Motorcycle)
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-06' AND customer_id=(SELECT id FROM users WHERE username='cust_gina')),
    (SELECT id FROM services_modifications WHERE service_item_name='Oil Change (Motorcycle)'),
    'COMPLETED'
  ),

  -- Liam: Battery Replacement
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-07' AND customer_id=(SELECT id FROM users WHERE username='cust_liam')),
    (SELECT id FROM services_modifications WHERE service_item_name='Battery Replacement'),
    'COMPLETED'
  ),

  -- Ivy: Brake Fluid Flush
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-08' AND customer_id=(SELECT id FROM users WHERE username='cust_ivy')),
    (SELECT id FROM services_modifications WHERE service_item_name='Brake Fluid Flush'),
    'COMPLETED'
  ),

  -- Jack: Window Tinting
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-09' AND customer_id=(SELECT id FROM users WHERE username='cust_jack')),
    (SELECT id FROM services_modifications WHERE service_item_name='Window Tinting'),
    'COMPLETED'
  ),

  -- Kelly: Custom Paint Touchup
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-10' AND customer_id=(SELECT id FROM users WHERE username='cust_kelly')),
    (SELECT id FROM services_modifications WHERE service_item_name='Custom Paint Touchup'),
    'COMPLETED'
  ),

  -- Noah: Headlight Replacement
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-11' AND customer_id=(SELECT id FROM users WHERE username='cust_noah')),
    (SELECT id FROM services_modifications WHERE service_item_name='Headlight Replacement'),
    'COMPLETED'
  ),

  -- Olivia: Coolant Flush
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-11-12' AND customer_id=(SELECT id FROM users WHERE username='cust_olivia')),
    (SELECT id FROM services_modifications WHERE service_item_name='Coolant Flush'),
    'COMPLETED'
  ),

  -- Alice upcoming: Full Inspection (scheduled)
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-12-01' AND customer_id=(SELECT id FROM users WHERE username='cust_alice')),
    (SELECT id FROM services_modifications WHERE service_item_name='Full Inspection'),
    'SCHEDULED'
  ),

  -- Alice upcoming: Tire Rotation (scheduled)
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-12-02' AND customer_id=(SELECT id FROM users WHERE username='cust_alice')),
    (SELECT id FROM services_modifications WHERE service_item_name='Tire Rotation'),
    'SCHEDULED'
  ),

  -- Maya: Exhaust Upgrade (scheduled)
  (
    (SELECT id FROM appointments WHERE appointment_date='2025-12-03' AND customer_id=(SELECT id FROM users WHERE username='cust_maya')),
    (SELECT id FROM services_modifications WHERE service_item_name='Exhaust Upgrade'),
    'SCHEDULED'
  );

-- 12. ASSIGN JOBS TO EMPLOYEES (link appointment_job id and employee id via subqueries)
-- Assignments for completed jobs
INSERT INTO job_assignments (appointment_job_id, employee_id, start_time, end_time) VALUES
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-01' AND customer_id=(SELECT id FROM users WHERE username='cust_bob')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Full Inspection') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_jane'),
    '09:00:00', '10:00:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-01' AND customer_id=(SELECT id FROM users WHERE username='cust_bob')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Wheel Alignment') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_mike'),
    '10:00:00', '10:30:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-02' AND customer_id=(SELECT id FROM users WHERE username='cust_carol')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Tire Rotation') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_sara'),
    '11:00:00', '11:30:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-03' AND customer_id=(SELECT id FROM users WHERE username='cust_dave')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Performance Chip Tuning') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_lee'),
    '13:00:00', '15:00:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-03' AND customer_id=(SELECT id FROM users WHERE username='cust_dave')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Exhaust Upgrade') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_mike'),
    '15:00:00', '15:30:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-04' AND customer_id=(SELECT id FROM users WHERE username='cust_erin')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='AC Recharge') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_jane'),
    '08:30:00', '09:30:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-05' AND customer_id=(SELECT id FROM users WHERE username='cust_fred')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Tire Rotation') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_sara'),
    '10:00:00', '10:30:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-06' AND customer_id=(SELECT id FROM users WHERE username='cust_gina')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Oil Change (Motorcycle)') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_jane'),
    '14:00:00', '14:30:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-07' AND customer_id=(SELECT id FROM users WHERE username='cust_liam')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Battery Replacement') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_mike'),
    '09:00:00', '09:40:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-08' AND customer_id=(SELECT id FROM users WHERE username='cust_ivy')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Brake Fluid Flush') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_tom'),
    '13:00:00', '13:45:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-09' AND customer_id=(SELECT id FROM users WHERE username='cust_jack')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Window Tinting') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_lee'),
    '10:00:00', '11:00:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-10' AND customer_id=(SELECT id FROM users WHERE username='cust_kelly')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Custom Paint Touchup') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_mike'),
    '15:00:00', '17:00:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-11' AND customer_id=(SELECT id FROM users WHERE username='cust_noah')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Headlight Replacement') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_tom'),
    '09:30:00', '10:00:00'
  ),
  (
    (SELECT aj.id FROM appointment_jobs aj WHERE aj.appointment_id=(SELECT id FROM appointments WHERE appointment_date='2025-11-12' AND customer_id=(SELECT id FROM users WHERE username='cust_olivia')) AND aj.service_item_id=(SELECT id FROM services_modifications WHERE service_item_name='Coolant Flush') LIMIT 1),
    (SELECT id FROM users WHERE username='emp_sara'),
    '11:00:00', '12:00:00'
  );

-- End of additional seed data
