INSERT INTO socialnet.comment  (id, time, time_changed, comment_type ,post_id,
                                     parent_id, author_id, comment_text, is_blocked, is_deleted, comments_count,
                                     like_amount) values
('1000', '2024-01-10 14:05', '2024-01-10 14:05', 'POST', '1009', null, '10001', 'Great!!!!', false, false, '2', '0'),
('1001', '2024-01-10 14:05', '2024-01-10 14:05', 'COMMENT', '1009', '1000', '10004', 'Cool!!!!', false, false, '0', '0'),
('1002', '2024-01-10 14:05', '2024-01-10 14:05', 'COMMENT', '1009', '1000', '10005', 'Super!!!!', false, false, '0', '0'),
('1003', '2024-01-10 14:05', '2024-01-10 14:05', 'POST', '1009', null, '10006', 'Good!!!!', false, false, '0', '0'),
('1004', '2024-01-10 14:05', '2024-01-10 14:05', 'POST', '1009', null, '10007', 'Fantastic!!!!', false, false, '0', '0'),
('1005', '2024-01-10 14:05', '2024-01-10 14:05', 'POST', '1009', null, '10008', 'Unbelievable!!!!', false, false, '0', '0')
