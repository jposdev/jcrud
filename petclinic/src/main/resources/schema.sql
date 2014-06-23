create table vets (
	vet_id                   identity primary key,
	name                     varchar(100) not null
);

create table pet_types (
	pet_type                 varchar(100) primary key,
	description              varchar not null
);

create table vet_specialties (
	vet_id                   bigint references vets(vet_id),
	pet_type                 varchar(100) references pet_types(pet_type),
	primary key (vet_id, pet_type)
);

create table owners (
	owner_id                 identity primary key,
	name                     varchar(100) not null,
	address                  varchar not null
);

create table owner_phones (
	owner_phone_id           identity primary key,
	owner_id                 bigint references owners(owner_id),
	phone                    varchar(100) not null,
	description              varchar not null
);

create table pets (
	pet_id                   identity primary key,
	name                     varchar(100) not null,
	birth_date               date not null,
	pet_type                 varchar(100) not null references pet_types(pet_type),
	owner_id                 bigint not null references owners(owner_id)
);

create table visits (
	visit_id                 identity primary key,
	pet_id                   bigint not null references pets(pet_id),
	vet_id                   bigint not null references vets(vet_id),
	visit_date               timestamp not null
);
