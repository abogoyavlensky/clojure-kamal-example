-- FORWARD
INSERT INTO director (name) VALUES
('Christopher Nolan'),
('Steven Spielberg'),
('Martin Scorsese'),
('Quentin Tarantino'),
('Ridley Scott'),
('James Cameron'),
('David Fincher'),
('Alfred Hitchcock'),
('Stanley Kubrick'),
('Peter Jackson');

INSERT INTO movie (title, year, director_id) VALUES
('Inception', 2010, 1), -- Christopher Nolan
('Jurassic Park', 1993, 2), -- Steven Spielberg
('Goodfellas', 1990, 3), -- Martin Scorsese
('Pulp Fiction', 1994, 4), -- Quentin Tarantino
('Gladiator', 2000, 5), -- Ridley Scott
('Avatar', 2009, 6), -- James Cameron
('Fight Club', 1999, 7), -- David Fincher
('Psycho', 1960, 8), -- Alfred Hitchcock
('A Clockwork Orange', 1971, 9), -- Stanley Kubrick
('The Lord of the Rings: The Fellowship of the Ring', 2001, 10); -- Peter Jackson

-- BACKWARD
