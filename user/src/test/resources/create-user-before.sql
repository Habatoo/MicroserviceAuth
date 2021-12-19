DROP TABLE IF EXISTS usr CASCADE;

CREATE TABLE IF NOT EXISTS usr (
user_id bigint auto_increment,
user_name varchar(255),
user_password varchar(255),
user_email varchar(255),
primary key (user_id)
);

ALTER TABLE usr ALTER COLUMN user_id RESTART WITH 5;

INSERT INTO usr (
user_name, user_password, user_email
)
VALUES (
 'administrator',
 '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS',
 'admin@a.com'
 ), (
'moderator',
 '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS',
'moderator@a.com'
), (
'user',
 '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS',
'user@a.com'
);

