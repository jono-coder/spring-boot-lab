-- noinspection SqlResolveForFile

INSERT INTO Client_Status(description) VALUES('Pending');
INSERT INTO Client_Status(description) VALUES('Active');
INSERT INTO Client_Status(description) VALUES('Suspended');
INSERT INTO Client_Status(description) VALUES('Disconnected');

INSERT INTO Client (name, account_no, status_id) VALUES ('ABC Shoes', 'ABC001', 1);
INSERT INTO Client (name, account_no, status_id) VALUES ('Z1 Trading', 'Z1001', 2);
INSERT INTO Client (name, account_no, status_id) VALUES ('Z2 Trading', 'Z2001', 4);
