DROP TABLE IF EXISTS event CASCADE;

CREATE TABLE IF NOT EXISTS event (
event_id bigint auto_increment,
event_title varchar(255),
event_description varchar(255),
event_date timestamp,
event_place varchar(500),
event_latitude varchar(20),
event_longitude varchar(20),
primary key (event_id)
);

ALTER TABLE event ALTER COLUMN event_id RESTART WITH 5;

INSERT INTO event (
event_title,
event_description,
event_date,
event_place,
event_latitude,
event_longitude
)
VALUES (
 'jogging',
 'Jogging activity round the forest.',
 current_date,
 'Park river 222',
 '23.2365',
 '65.2354'
 ), (
 'mounting',
 'Mounting activity round the forest.',
 current_date,
 'Zoo 3/22',
 '33.2354',
 '35.6587'
);

