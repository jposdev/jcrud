create table vets (
	vet_id                   identity primary key,
	name                     varchar(100) not null
);

create table pet_types (
	pet_type                 varchar(100) primary key,
	description              varchar not null
);

create table vet_specialties (
	vet_id                   bigint,
	pet_type                 varchar(100) references pet_types(pet_type),
	primary key (vet_id, pet_type),
	constraint vet_specialty__vet foreign key (vet_id) references vets(vet_id)
);

create table owners (
	owner_id                 identity primary key,
	name                     varchar(100) not null,
	address                  varchar not null
);

create table owner_phones (
	owner_phone_id           identity primary key,
	owner_id                 bigint not null,
	phone                    varchar(100) not null,
	description              varchar not null,
	constraint owner_phone__owner foreign key (owner_id) references owners(owner_id)
);

create table pets (
	pet_id                   identity primary key,
	name                     varchar(100) not null,
	birth_date               date not null,
	pet_type                 varchar(100) not null,
	owner_id                 bigint not null,
	constraint pet__pet_type foreign key (pet_type) references pet_types(pet_type),
	constraint pet__owner foreign key (owner_id) references owners(owner_id)
);

create table visits (
	visit_id                 identity primary key,
	pet_id                   bigint not null,
	vet_id                   bigint not null,
	visit_date               timestamp not null,
	constraint visit__pet foreign key (pet_id) references pets(pet_id),
	constraint visit__vet foreign key (vet_id) references vets(vet_id)
);
