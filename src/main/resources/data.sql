insert into student (id, index_number, first_name, last_name) values (1, 'ra1-2014', 'Marko', 'Marković');
insert into student (id, index_number, first_name, last_name) values (2, 'ra2-2014', 'Milan', 'Milanović');
insert into student (id, index_number, first_name, last_name) values (3, 'ra3-2014', 'Ivana', 'Ivanović');
insert into student (id, index_number, first_name, last_name) values (4, 'ra4-2014', 'Bojan', 'Bojanović');
insert into student (id, index_number, first_name, last_name) values (5, 'ra5-2014', 'Pera', 'Perić');
insert into student (id, index_number, first_name, last_name) values (6, 'ra6-2014', 'Zoran', 'Zoranović');
insert into student (id, index_number, first_name, last_name) values (7, 'ra7-2014', 'Bojana', 'Bojanović');
insert into student (id, index_number, first_name, last_name) values (8, 'ra8-2014', 'Milana', 'Milanović');
insert into student (id, index_number, first_name, last_name) values (9, 'ra9-2014', 'Jovana', 'Jovanić');

insert into course (id, name) values (1, 'Matematika');
insert into course (id, name) values (2, 'Osnove programiranja');
insert into course (id, name) values (3, 'Objektno programiranje');

insert into teacher (id, first_name, last_name) values (1, 'Strahinja', 'Simić');
insert into teacher (id, first_name, last_name) values (2, 'Marina', 'Antić');
insert into teacher (id, first_name, last_name) values (3, 'Siniša', 'Branković');

insert into teaching (course_id, teacher_id) values (1, 1);
insert into teaching (course_id, teacher_id) values (1, 2);
insert into teaching (course_id, teacher_id) values (2, 2);
insert into teaching (course_id, teacher_id) values (3, 3);

insert into exam (id, student_id, course_id, date, grade) values (1, 1, 1, '2016-02-01', 9);
insert into exam (id, student_id, course_id, date, grade) values (2, 1, 2, '2016-04-19', 8);
insert into exam (id, student_id, course_id, date, grade) values (3, 2, 1, '2016-02-01', 10);
insert into exam (id, student_id, course_id, date, grade) values (4, 2, 2, '2016-04-19', 10);