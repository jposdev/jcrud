insert into vets(vet_id, name) values
	(-1, 'John Smith'),
	(-2, 'Jane Doe'),
;

insert into pet_types(pet_type, description) values
	('cat', 'Felis catus'),
	('dog', 'Canis lupus familiaris'),
;

insert into vet_specialties(vet_id, pet_type) values
	(-1, 'cat'),
	(-2, 'cat'),
	(-2, 'dog'),
;

insert into owners(owner_id, name, address) values
	(-1, 'Alex Jameson', 'Penny Lane 12'),
	(-2, 'Warren Stewart', 'Brick Lane 6'),
;

insert into owner_phones(owner_phone_id, owner_id, phone, description) values
	(-1, -1, '555-2345', 'Home phone'),
	(-2, -2, '555-1234', 'Home phone'),
	(-3, -2, '555-5678', 'Mobile phone'),
;

insert into pets(pet_id, name, birth_date, pet_type, owner_id) values
	(-1, 'Felix', '2012-12-03', 'cat', -1),
	(-2, 'Lucy', '2013-01-04', 'cat', -1),
	(-3, 'Warren', '2011-05-05', 'dog', -2),
;

insert into visits(visit_id, pet_id, vet_id, visit_date) values
	(-1, -1, -1, '2014-03-05 10:00:00'),
	(-2, -3, -2, '2014-04-06 16:00:00'),
;
