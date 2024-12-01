create sequence if not exists auth.user_id_seq
increment by 50;

create table if not exists auth.userdata (
	id serial primary key,
	login varchar(255) not null,
	password varchar(255) not null,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	registration_date timestamptz(6) not null
);

create table if not exists auth.user_role (
	id uuid primary key,
	role varchar(255) NOT NULL,
	user_id integer not null references auth.userdata(id)
);

create table if not exists auth.refresh_token (
	id uuid not null primary key,
	expires_at timestamptz(6) not null,
	user_id integer not null references auth.userdata(id)
);
