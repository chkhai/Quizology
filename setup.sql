-- Use or create database
CREATE DATABASE IF NOT EXISTS quizapp_db;
USE quizapp_db;

-- Drop tables in reverse FK dependency order
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS achievements;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS quiz_results;
DROP TABLE IF EXISTS user_answers;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS users;

-- Users table (with bio and profile picture)
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       hashed_password VARCHAR(255) NOT NULL,
                       is_admin BOOLEAN DEFAULT FALSE,
                       bio TEXT,
                       profile_picture_url VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quizzes table
CREATE TABLE quizzes (
                         quiz_id INT AUTO_INCREMENT PRIMARY KEY,
                         user_id INT NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Questions table
CREATE TABLE questions (
                           question_id INT AUTO_INCREMENT PRIMARY KEY,
                           quiz_id INT NOT NULL,
                           text TEXT NOT NULL,
                           type ENUM('question_response', 'fill_in_blank', 'multiple_choice', 'picture_response') NOT NULL,
                           image_url VARCHAR(255),
                           FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE
);

-- Answers table
CREATE TABLE answers (
                         answer_id INT AUTO_INCREMENT PRIMARY KEY,
                         question_id INT NOT NULL,
                         answer_text TEXT NOT NULL,
                         is_correct BOOLEAN NOT NULL,
                         FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

-- User Answers table
CREATE TABLE user_answers (
                              user_answer_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              question_id INT NOT NULL,
                              given_answer TEXT NOT NULL,
                              is_correct BOOLEAN NOT NULL,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                              FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

-- Quiz Results table
CREATE TABLE quiz_results (
                              quiz_result_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              quiz_id INT NOT NULL,
                              total_score INT NOT NULL,
                              total_questions INT NOT NULL,
                              time_taken INT, -- in seconds
                              is_practice BOOLEAN DEFAULT FALSE,
                              completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                              FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE
);

-- Friend Requests table
CREATE TABLE friend_requests (
                                 friend_request_id INT AUTO_INCREMENT PRIMARY KEY,
                                 from_user INT NOT NULL,
                                 to_user INT NOT NULL,
                                 status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending',
                                 sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (from_user) REFERENCES users(user_id) ON DELETE CASCADE,
                                 FOREIGN KEY (to_user) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Friends table
CREATE TABLE friends (
                         friendship_id INT AUTO_INCREMENT PRIMARY KEY,
                         friend1_user_id INT NOT NULL,
                         friend2_user_id INT NOT NULL,
                         UNIQUE KEY unique_friendship (friend1_user_id, friend2_user_id),
                         FOREIGN KEY (friend1_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                         FOREIGN KEY (friend2_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Messages table
CREATE TABLE messages (
                          message_id INT AUTO_INCREMENT PRIMARY KEY,
                          from_user_id INT NOT NULL,
                          to_user_id INT NOT NULL,
                          type ENUM('challenge', 'friend_request', 'text') DEFAULT 'text',
                          text TEXT NOT NULL,
                          timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Achievements table
CREATE TABLE achievements (
                              achievement_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              achievement_name ENUM(
                                  'Amateur_Author',
                                  'Prolific_Author',
                                  'Prodigious_Author',
                                  'Quiz_Machine',
                                  'I_am_the_Greatest',
                                  'Practice_Makes_Perfect'
                                  ) NOT NULL,
                              quiz_id INT,
                              achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                              FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE
);

-- Announcements table (NEW)
CREATE TABLE announcements (
                               announcement_id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               title VARCHAR(255),
                               announcement_text TEXT NOT NULL,
                               url VARCHAR(255), -- optional image/quiz link
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert 5 admin users
INSERT INTO users (username, hashed_password, is_admin)
VALUES
    ('lkhiz23', 'a85cce133b87c29967f0c4cce6eaf76bf5d3f68b', TRUE),
    ('lchkh23', 'f87c1ea92d312bb8be0a16dfafd375f813f8255e', TRUE),
    ('sansi23', 'a27d4f58662f66473fe3e5f50bd70c44c1513f0f', TRUE),
    ('akave23', 'ccc28cccf8128a3f57f62b46407e4aa24f57a2b7', TRUE),
    ('lbegi23', 'dccb1290851d4887f849da9f1370629056592f36', TRUE);


-- Insert the Football Quiz
INSERT INTO quizzes (user_id, title, description)
VALUES
    (3, 'World Football Trivia', 'Test your football quiz knowledge with the questions of mixed difficulty.');

-- Insert questions for the quiz
INSERT INTO questions (quiz_id, text, type)
VALUES
    (1, 'Who won the FIFA World Cup in 2018?', 'multiple_choice'),
    (1, 'Who is the player known as “The Hand of God”?', 'multiple_choice'),
    (1, 'Name the football club where Khvicha Kvaratskhelia started his professional career.', 'multiple_choice'),
    (1, 'Which country has won the most FIFA World Cups?', 'multiple_choice'),
    (1, 'Which of the following two clubs share the same stadium?', 'multiple_choice');

-- Insert answers for first question (multiple_choice)
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (1, 'Brazil', FALSE),
    (1, 'Germany', FALSE),
    (1, 'France', TRUE),
    (1, 'Argentina', FALSE),

    (2, 'Maradona', TRUE),
    (2, 'Barcola', FALSE),
    (2, 'Di maria', FALSE),
    (2, 'Messi', FALSE),

    (3, 'FC Rustavi', FALSE),
    (3, 'Lokomotiv Moskov', FALSE),
    (3, 'Dinamo Tbilisi', TRUE),
    (3, 'PSG', FALSE),

    (4, 'Germany', FALSE),
    (4, 'Italy', FALSE),
    (4, 'Argentina', FALSE),
    (4, 'Brazil', TRUE),

    (5, 'AC Milan & FC Internazionale Milano', TRUE),
    (5, 'Bayern Munich & TSV 1860 Munich', FALSE),
    (5, 'Atletico Madrid & Rayo Vallecano', FALSE),
    (5, 'Dinamo Tbilisi & FC Iberia 1999', FALSE);


-- Insert NBA Basketball Quiz
INSERT INTO quizzes (user_id, title, description)
VALUES
    (3, 'NBA Legends & History', 'Test your knowledge of NBA basketball history, legends, and current stars.');

-- Insert questions for the NBA quiz
INSERT INTO questions (quiz_id, text, type)
VALUES
    (2, 'Who holds the record for most NBA championships won as a player?', 'multiple_choice'),
    (2, 'Which team has won the most NBA championships?', 'multiple_choice'),
    (2, 'Who is known as "His Airness"?', 'multiple_choice'),
    (2, 'What is the maximum number of players on an NBA roster during the regular season?', 'multiple_choice'),
    (2, 'Which player scored 100 points in a single NBA game?', 'multiple_choice');

-- Insert answers for NBA quiz questions
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (6, 'Michael Jordan', FALSE),
    (6, 'Bill Russell', TRUE),
    (6, 'Kareem Abdul-Jabbar', FALSE),
    (6, 'Magic Johnson', FALSE),

    (7, 'Los Angeles Lakers', FALSE),
    (7, 'Boston Celtics', TRUE),
    (7, 'Chicago Bulls', FALSE),
    (7, 'San Antonio Spurs', FALSE),

    (8, 'LeBron(Our Glorious King) James', FALSE),
    (8, 'Kobe Bryant', FALSE),
    (8, 'Michael Jordan', TRUE),
    (8, 'Magic Johnson', FALSE),

    (9, '12', FALSE),
    (9, '13', FALSE),
    (9, '15', TRUE),
    (9, '17', FALSE),

    (10, 'Kobe Bryant', FALSE),
    (10, 'Michael Jordan', FALSE),
    (10, 'Wilt Chamberlain', TRUE),
    (10, 'Stephen Curry', FALSE);



-- Insert Literature Question Response Quizzes
INSERT INTO quizzes (user_id, title, description)
VALUES
    (3, 'Classic Authors Quiz', 'Test your knowledge of famous authors and their works.'),
    (3, 'Literary Characters Quiz', 'Identify famous characters from literature.');

-- Insert questions for quizzes
INSERT INTO questions (quiz_id, text, type)
VALUES
    (3, 'Who wrote "Romeo and Juliet"?', 'question_response'),
    (3, 'Who is the author of "Pride and Prejudice"?', 'question_response'),
    (3, 'Which author created the character Sherlock Holmes?', 'question_response'),
    (3, 'Who wrote "To Kill a Mockingbird"?', 'question_response'),
    (3, 'Which Russian author wrote "Crime and Punishment"?', 'question_response'),

    (4, 'What is the name of Harry Potter\'s owl?', 'question_response'),
    (4, 'Who is the protagonist in "The Great Gatsby"?', 'question_response'),
    (4, 'What is the name of Atticus Finch\'s daughter in "To Kill a Mockingbird"?', 'question_response'),
    (4, 'Who is the captain in "Moby Dick"?', 'question_response'),
    (4, 'What is the name of the monster in Mary Shelley\'s "Frankenstein"?', 'question_response');

-- Insert answers for Classic Authors Quiz (text-based)
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (11, 'William Shakespeare', TRUE),
    (11, 'Shakespeare', TRUE),

    (12, 'Jane Austen', TRUE),
    (12, 'Austen', TRUE),

    (13, 'Arthur Conan Doyle', TRUE),
    (13, 'Conan Doyle', TRUE),
    (13, 'Doyle', TRUE),

    (14, 'Harper Lee', TRUE),
    (14, 'Lee', TRUE),

    (15, 'Fyodor Dostoevsky', TRUE),
    (15, 'Dostoevsky', TRUE),

-- Insert answers for Literary Characters Quiz (text-based)
    (16, 'Hedwig', TRUE),

    (17, 'Jay Gatsby', TRUE),
    (17, 'Gatsby', TRUE),

    (18, 'Scout Finch', TRUE),
    (18, 'Scout', TRUE),
    (18, 'Jean Louise Finch', TRUE),

    (19, 'Captain Ahab', TRUE),
    (19, 'Ahab', TRUE),

    (20, 'Frankenstein\'s monster', TRUE),
    (20, 'The monster', TRUE),
    (20, 'The creature', TRUE);



-- Insert GEOGRAPHY quiz
INSERT INTO quizzes (user_id, title, description)
VALUES
    (4, 'Geography quiz', 'Easy geography quiz to challenge yourself!');

INSERT INTO questions (quiz_id, text, type)
VALUES
    (5, 'Which is the longest river in the world?', 'multiple_choice'),
    (5, 'Which country has the most time zones?', 'multiple_choice'),
    (5, 'With how many countries does Georgia share a border?','multiple_choice'),
    (5, 'What is the capital of Canada?', 'multiple_choice'),
    (5, 'Which desert is the largest in the world by area?', 'multiple_choice'),
    (5, 'Which continent has the most countries?', 'multiple_choice'),
    (5, 'Which country has the flag with an unusual form?', 'multiple_choice'),
    (5, 'Which is one of the georgian regions occupied by russia?', 'multiple_choice');

-- Inserts answers for the fifth quiz
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (21, 'Nile', TRUE),
    (21, 'Amazon', FALSE),
    (21, 'Mississippi', FALSE),
    (21, 'Congo', FALSE),

    (22, 'Russia', FALSE),
    (22, 'United States', FALSE),
    (22, 'China', FALSE),
    (22, 'France', TRUE),

    (23, '3', FALSE),
    (23, '4', TRUE),
    (23, '5', FALSE),
    (23, '6', FALSE),

    (24, 'Toronto', FALSE),
    (24, 'Ottawa', TRUE),
    (24, 'Vancouver', FALSE),
    (24, 'Montreal', FALSE),

    (25, 'Sahara', FALSE),
    (25, 'Arabian', FALSE),
    (25, 'Gobi', FALSE),
    (25, 'Antarctic', TRUE),

    (26, 'Asia', FALSE),
    (26, 'Africa', TRUE),
    (26, 'Europe', FALSE),
    (26, 'North America', FALSE),

    (27, 'Bangladesh', FALSE),
    (27, 'Sri-Lanka', FALSE),
    (27, 'Nepal', TRUE),
    (27, 'Georgia', FALSE),

    (28, 'Abkhazia', TRUE),
    (28, 'Adjara', FALSE),
    (28, 'Guria', FALSE),
    (28, 'Svaneti', FALSE);


INSERT INTO quizzes (user_id, title, description)
VALUES
    (4, 'History quiz', 'Do you want to travel through time? - Take this quiz!');

INSERT INTO questions (quiz_id, text, type)
VALUES
    (6, 'The Great Wall of ________ was built to protect against invasions.', 'fill_in_blank'),
    (6, 'Queen ________ is one of the most famous rulers in Georgian history and reigned during the country\'s cultural peak.', 'fill_in_blank'),
    (6, 'The pyramids of ________ are one of the Seven Wonders of the Ancient World.','fill_in_blank'),
    (6, 'Nelson Mandela was the first Black president of ________.', 'fill_in_blank'),
    (6, 'World War II ended in the year ________.', 'fill_in_blank');

-- Inserts answers for the sixth quiz
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (29, 'China', TRUE),

    (30, 'Tamar', TRUE),

    (31, 'Egypt', TRUE),

    (32, 'South Africa', TRUE),

    (33, '1945', TRUE);



INSERT INTO quizzes (user_id, title, description)
VALUES
    (4, 'Movies', 'Test your reel knowledge!');

INSERT INTO questions (quiz_id, text, type)
VALUES
    (7, 'The movie Interstellar was directed by ________.', 'fill_in_blank'),
    (7, 'The character Jack Sparrow appears in the Pirates of the ________ series.', 'fill_in_blank'),
    (7, 'In The Matrix, the main character Neo is played by ________.','fill_in_blank'),
    (7, 'The wizarding school in Harry Potter is called ________ .', 'fill_in_blank'),
    (7, 'In Breakfast at Tiffany’s, the lead character Holly Golightly is played by ________ Hepburn.', 'fill_in_blank');

-- Insert answers for the 7th quiz
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (34, 'Christopher Nolan', TRUE),

    (35, 'Caribbean', TRUE),

    (36, 'Keanu Reeves', TRUE),

    (37, 'Hogwarts', TRUE),

    (38, 'Audrey', TRUE);

-- Insert EURO 2024 Quiz
INSERT INTO quizzes (user_id, title, description)
VALUES
    (2, 'EURO 2024 Quiz', '
Think you know everything about Euro 2024? From unforgettable goals to shocking upsets and star players, this quiz puts your football knowledge to the ultimate test!
Challenge yourself and see how well you remember the biggest moments, stats, and surprises of this year’s tournament.');

-- Insert answers for the 8th quiz
INSERT INTO questions (quiz_id, text, type)
VALUES
    (8, 'Who became the youngest player in EURO history when he
featured on the second day of the tournament?', 'multiple_choice'),
    (8, 'What was significant about Italy''s 2-1 win against Albania
in Group B?', 'multiple_choice'),
    (8, 'Georgia appeared at a major international tournament
for the very first time. Who did their one and only
victory at EURO 2024 come against?', 'multiple_choice'),
    (8, 'Which goalkeeper saved all three penalties in a
shoot-out during his team''s round of 16 victory?', 'multiple_choice'),
    (8, 'Who became the oldest-ever scorer at a EURO after
netting on Match day 3?', 'multiple_choice'),
    (8, 'Spain beat England 2-1 in the final in Berlin. Who scored the winning goal?','multiple_choice'),
    (8, 'A total of how many goals were scored at the tournament?', 'multiple_choice'),
    (8,'Spain''s 15 goals at EURO 2024 is a new tournament record.
Which team did they surpass?', 'multiple_choice'),
    (8, 'Whose winner after 89 minutes 59 seconds was the latest ever scored
in the semi-finals of a EURO or World Cup?', 'multiple_choice'),
    (8, 'Who made their 18th EURO appearance to beat the previous
 record for a goalkeeper set by Italy''s Gianluigi Buffon?', 'multiple_choice');

-- Insert answers for the 8th quiz
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (39, 'Zaire-Emery (France)', FALSE),
    (39, 'Leo Sauer (Slovakia)', FALSE),
    (39, 'Lamine Yamal (Spain)', TRUE),
    (39, 'Kobbie Mainoo(England)', FALSE),

    (40, 'Albania scored the fastest-ever EURO goal', TRUE),
    (40, 'It was Italy''s 50 victory in a EURO Finals', FALSE),
    (40, 'There were more shots than any other game in EURO history', FALSE),
    (40, 'Italy scored the latest-ever EURO GOAL', FALSE),

    (41, 'Czechia', FALSE),
    (41, 'Turkey', FALSE),
    (41, 'Spain', FALSE),
    (41, 'Portugal', TRUE),

    (42, 'Mike Maignan', FALSE),
    (42, 'Diogo Costa', TRUE),
    (42, 'Unai Simon', FALSE),
    (42, 'Jordan Pickford', FALSE),

    (43, 'Cristiano Ronaldo', FALSE),
    (43, 'Luka Modric', TRUE),
    (43, 'Olivier Giroud', FALSE),
    (43, 'Jesus Navas', FALSE),

    (44, 'Mikel Oyarzabal', TRUE),
    (44, 'Dani Olmo', FALSE),
    (44, 'Lamine Yamal', FALSE),
    (44, 'Mikel Merino', FALSE),

    (45, '95', FALSE),
    (45, '133', FALSE),
    (45, '117', TRUE),
    (45, '128', FALSE),

    (46, 'Italy', FALSE),
    (46, 'England', FALSE),
    (46, 'Germany', FALSE),
    (46, 'France', TRUE),

    (47, 'Dani Olmo (Spain)', FALSE),
    (47, 'Ollie Watkins (England)', TRUE),
    (47, 'Lamine Yamal (Spain)', FALSE),
    (47, 'Harry Kane (England)', FALSE),

    (48, 'Manuel Neuer', TRUE),
    (48, 'Gainluigi Donnaruma', FALSE),
    (48, 'Jan Oblak', FALSE),
    (48, 'Yann Sommer', FALSE);



-- Insert Math Quiz
INSERT INTO quizzes (user_id, title, description)
VALUES
    (2, 'Math Quiz', '
Think you''ve got what it takes to conquer numbers, logic, and problem-solving? This math quiz challenges your brain with
a variety of questions – from simple arithmetic to tricky puzzles.');


-- Insert questions for the 9th quiz
INSERT INTO questions (quiz_id, text, type)
VALUES
    (9, 'What is the sum of a triangle''s interior angles?', 'multiple_choice'),
    (9, 'What is the next number in the Fibonacci Sequences 0,1,1,2,3,5,8,13,21,34?', 'multiple_choice'),
    (9,'52 Divided By 4 Equals','multiple_choice'),
    (9,'What is the least common multiple of 6, 8, and 12?','multiple_choice'),
    (9,'When do we celebrate Pi day?','multiple_choice'),
    (9,'What is an eight-sided polygon?','multiple_choice'),
    (9,'Which of the following is a prime number?','multiple_choice'),
    (9,'Solve for x: 2x + 3 = 11','multiple_choice'),
    (9,'A rectangle has a length of 10 and a width of 4. What is its perimeter?','multiple_choice'),
    (9,'A bag contains 3 red, 5 blue, and 2 green balls. What is the probability of randomly picking a blue ball?','multiple_choice');

-- Insert answers for the 9th quiz
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (49, '360', FALSE),
    (49, '270', FALSE),
    (49, '180', TRUE),
    (49, '540', FALSE),

    (50, '54', FALSE),
    (50, '55', TRUE),
    (50, '68', FALSE),
    (50, '67', FALSE),

    (51, '15', FALSE),
    (51, '12', FALSE),
    (51, '14', FALSE),
    (51, '13', TRUE),

    (52, '32', FALSE),
    (52, '16', FALSE),
    (52, '24', TRUE),
    (52, '64', FALSE),

    (53, '14th March', TRUE),
    (53, '3rd December', FALSE),
    (53, '7th July', FALSE),
    (53, '6th June', FALSE),

    (54, 'Heptagon', FALSE),
    (54, 'Hexagon', FALSE),
    (54, 'Octagon', TRUE),
    (54, 'Decagon', FALSE),

    (55, '67', TRUE),
    (55, '91', FALSE),
    (55, '63', FALSE),
    (55, '57', FALSE),

    (56, '2', FALSE),
    (56, '3', FALSE),
    (56, '5', FALSE),
    (56, '4', TRUE),

    (57, '28', TRUE),
    (57, '14', FALSE),
    (57, '40', FALSE),
    (57, '20', FALSE),

    (58, '20%', FALSE),
    (58, '50%', TRUE),
    (58, '30%', FALSE),
    (58, '40%', FALSE);


-- Insert Cartoon Quiz
INSERT INTO quizzes (user_id, title, description)
VALUES (2, 'Cartoon Quiz','From SpongeBob to Scooby-Doo, this quiz is packed with your favorite cartoon characters! 🧽🐶
Test your memory on classic shows and fun facts from the world of animation. Great for all ages!');

-- Insert questions for the 10th quiz
INSERT INTO questions (quiz_id, text, type)
VALUES
    (10, 'What kind of animal is Scooby-Doo?', 'multiple_choice'),
    (10, 'Which cartoon features a character who lives in a pineapple under the sea?', 'multiple_choice'),
    (10, 'What are the names of the chipmunk brothers in “Alvin and the Chipmunks”??', 'multiple_choice'),
    (10, 'Which cartoon character says "What''s up, Doc?"', 'multiple_choice'),
    (10, 'Which cartoon features four turtles trained in ninjitsu?"', 'multiple_choice'),
    (10, 'In “Tom and Jerry”, who is the cat?', 'multiple_choice'),
    (10, 'Who is the red car in the movie/cartoon "Cars"?', 'multiple_choice'),
    (10, 'Which character is known for turning everything he touches into chaos and loves lasagna?', 'multiple_choice');

-- Insert answers for the 10th quiz
INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (59, 'Rabbit', FALSE),
    (59, 'Cat', FALSE),
    (59, 'Dog', TRUE),
    (59, 'Bear', FALSE),

    (60, 'Aqua Man', FALSE),
    (60, 'SpongeBob SquarePants', TRUE),
    (60, 'The Little Mermaid', FALSE),
    (60, 'Finding Nemo', FALSE),

    (61, 'Leo, Donnie, Raph', FALSE),
    (61, 'Tim, Tom, Jerry', FALSE),
    (61, ' Max, Mike, Moe', FALSE),
    (61, 'Alvin, Simon, Theodore', TRUE),

    (62, 'Mickey Mouse', FALSE),
    (62, 'Tom Cat', FALSE),
    (62, 'Bugs Bunny', TRUE),
    (62, 'Daffy Duck', FALSE),

    (63, 'Power Rangers', FALSE),
    (63, 'Pokémon', FALSE),
    (63, 'Teenage Mutant Ninja Turtles', TRUE),
    (63, 'Kung Fu Panda', FALSE),

    (64, 'Tom', TRUE),
    (64, 'Jerry', FALSE),
    (64, 'Felix', FALSE),
    (64, 'Spike', FALSE),

    (65, 'Lightning McQueen', TRUE),
    (65, 'Doc Hudson', FALSE),
    (65, 'Mater', FALSE),
    (65, 'Cruz Ramirez', FALSE),

    (66, 'Odie', FALSE),
    (66, 'Snoopy', FALSE),
    (66, 'Felix the Cat', FALSE),
    (66, 'Garfield', TRUE);


-- Insert LOL Esports Quiz
INSERT INTO quizzes (user_id, title, description)
VALUES
    (1, 'LOL Esports Quiz', 'Test your League Of Legends Esports Knowledge!');

INSERT INTO questions (quiz_id, text, type, image_url)
VALUES
    (11, 'Which professional League of Legends team won the World Championship and got the IG Kai’Sa skin as a reward?', 'multiple_choice', 'images/kaisa.jpg'),
    (11, 'In the 2022 Worlds Finals, which champion did DRX''s deft lock in during the iconic game 5 that won him the title?', 'multiple_choice', 'images/deft.jpg'),
    (11, 'During EU LCS Summer Semifinals, which unexpected champion did Caps play midlane that shocked the entire world?', 'multiple_choice', 'images/caps.jpg'),
    (11, 'At which event did G2 Esports beat SKT T1 in a 5 game series to reach a final?', 'multiple_choice', 'images/g2.jpg');

INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (67, 'Infinity Gaming', FALSE),
    (67, 'Incredible Geniuses', FALSE),
    (67, 'Immortal Guardians', FALSE),
    (67, 'Invictus Gaming', TRUE),
    (68, 'Lucian', FALSE),
    (68, 'Varus', FALSE),
    (68, 'Caitlyn', TRUE),
    (68, 'Ezreal', FALSE),
    (69, 'Vayne', TRUE),
    (69, 'Lucian', FALSE),
    (69, 'Karma', FALSE),
    (69, 'Lulu', FALSE),
    (70, 'MSI 2022', FALSE),
    (70, 'Worlds 2021', FALSE),
    (70, 'Worlds 2019', FALSE),
    (70, 'MSI 2019', TRUE);


-- Insert Bayern Quiz
INSERT INTO quizzes (user_id, title, description)
VALUES
    (1, 'Bayern Munich Quiz', 'Test your knowledge of FC Bayern Munich\'s glorious football history!');

INSERT INTO questions (quiz_id, text, type, image_url)
VALUES
    (12, 'In which year did Bayern Munich win their first UEFA Champions League title?', 'multiple_choice', 'images/ucl74.jpg'),
    (12, 'Which legendary Bayern striker was known as "Der Bomber"?', 'multiple_choice', 'images/derbomber.jpg'),
    (12, 'Who scored the winning goal in the 2013 UEFA Champions League Final for Bayern?', 'multiple_choice', 'images/ucl13.jpg'),
    (12, 'Which club did Bayern defeat 8-2 in the 2020 UCL quarter-finals?', 'multiple_choice', 'images/bayern82barca.jpg'),
    (12, 'How many UCL titles does Bayern Munich have?', 'multiple_choice', 'images/ucl.jpg');

INSERT INTO answers (question_id, answer_text, is_correct)
VALUES
    (71, '1973', FALSE),
    (71, '1974', TRUE),
    (71, '1975', FALSE),
    (71, '1976', FALSE),

    (72, 'Karl-Heinz Rummenigge', FALSE),
    (72, 'Gerd Muller', TRUE),
    (72, 'Lothar Matthäus', FALSE),
    (72, 'Uli Hoeneß', FALSE),

    (73, 'Franck Ribéry', FALSE),
    (73, 'Thomas Müller', FALSE),
    (73, 'Arjen Robben', TRUE),
    (73, 'Mario Götze', FALSE),

    (74, 'Real Madrid', FALSE),
    (74, 'Manchester City', FALSE),
    (74, 'Barcelona', TRUE),
    (74, 'PSG', FALSE),

    (75, '4', FALSE),
    (75, '5', FALSE),
    (75, '6', TRUE),
    (75, '7', FALSE);

