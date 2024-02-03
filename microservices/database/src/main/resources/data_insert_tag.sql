INSERT INTO socialnet.tag (id, name, is_deleted) values
('1000', 'firstpost', FALSE),
('1001', 'helloeveryone', FALSE),
('1002', 'skillbox', FALSE),
('1003', 'programming', FALSE),
('1004', 'java', FALSE)

------------------------------------------

INSERT INTO socialnet.post2tag (id, post_id, tag_id) values
('1000', '1001', '1004'),
('1001', '1001', '1003'),
('1002', '1001', '1001'),
('1003', '1003', '1002'),
('1004', '1003', '1004'),
('1005', '1004', '1001'),
('1006', '1007', '1003'),
('1007', '1007', '1004'),
('1008', '1007', '1004'),
('1009', '1009', '1001')