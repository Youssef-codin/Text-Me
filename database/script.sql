--DROP TABLE users;
--
--CREATE TABLE users(
--usr_name VARCHAR(40) PRIMARY KEY UNIQUE NOT NULL,
--usr_email VARCHAR(255) UNIQUE NOT NULL,
--usr_password VARCHAR(100) NOT NULL,
--usr_salt VARCHAR(40) NOT NULL,
--usr_key BLOB NOT NULL
--);

--
--CREATE TABLE msgs(
--msg_id INTEGER PRIMARY KEY,
--msg BLOB NOT NULL,
--msg_sender VARCHAR(40) NOT NULL,
--aes_sender BLOB NOT NULL,
--msg_receiver VARCHAR(40) NOT NULL,
--aes_receiver BLOB NOT NULL,
--time_stamp INTEGER NOT NULL,
--aes_iv BLOB NOT NULL,
--FOREIGN KEY (msg_sender) REFERENCES users(usr_name),
--FOREIGN KEY (msg_receiver) REFERENCES users(usr_name)
--);


--PRAGMA foreign_keys = on;

--DELETE FROM users;
--DELETE FROM msgs;
SELECT * FROM users;
SELECT * FROM msgs;
