CREATE SCHEMA IF NOT EXISTS IAM_Philip;
SET SCHEMA IAM_Philip;

DROP TABLE Identities IF EXISTS;

CREATE TABLE Identities
(
   uid long primary key not null,
   name varchar(255) not null,
   email varchar(255) not null,
   normalizedEmail varchar(255) not null,
   passwordHash varchar(255) not null
);

CREATE UNIQUE INDEX UK_Identities_email
	on Identities (email);
