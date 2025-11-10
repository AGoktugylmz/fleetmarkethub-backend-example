create table roles(
    id         varchar(255) not null primary key,
    name       varchar(10)        constraint uk_roles_unique_name        unique,
    created_at timestamp    not null,
    updated_at timestamp
);

create table users(
    id                 varchar(255) not null        primary key,
    email              varchar(100) not null        constraint uk_users_unique_email        unique,
    password           varchar(255) not null,
    title              varchar(100),
    name               varchar(50)  not null,
    last_name          varchar(50)  not null,
    gender             varchar(6),
    birth_date         date,
    avatar             text,
    created_at         timestamp    not null,
    email_activated_at timestamp,
    blocked_at         timestamp,
    updated_at         timestamp
);

create table refresh_tokens(
    id              varchar(255) not null        primary key,
    user_id         varchar(255) not null        constraint fk_refresh_tokens_user_id        references users        on delete cascade,
    token           varchar(255) not null        constraint uk_refresh_tokens_token        unique,
    expiration_date timestamp    not null,
    created_at      timestamp    not null,
    updated_at      timestamp
);

create table user_roles(
    user_id varchar(255) not null        constraint fk_user_roles_user_id        references users        on delete cascade,
    role_id varchar(255) not null        constraint fk_user_roles_role_id        references roles,
    primary key (user_id, role_id)
);

INSERT INTO public.roles (id, name, created_at, updated_at) VALUES ('1', 'ADMIN', '2022-09-29 22:37:31.000000', null);
INSERT INTO public.roles (id, name, created_at, updated_at) VALUES ('2', 'USER', '2022-07-29 22:37:19.000000', null);
INSERT INTO public.roles (id, name, created_at, updated_at) VALUES ('3', 'CONSULTANT', '2022-07-29 22:37:19.000000', null);

INSERT INTO public.users (id, email, password, title, name, last_name, gender, birth_date, avatar, created_at, email_activated_at, blocked_at, updated_at) VALUES ('0bce7c25-d9dc-41bd-9ff4-250fedcd0d2f', 'user@example3.com', '$2a$10$3BTfmiPiVqaLpYlAXndy5.qTLxvXp65W5Dx0He/VskOqMpmEilEd2', null, 'User', 'Example', 'MALE', null, null, '2022-10-02 20:13:31.479515', null, null, '2022-10-02 20:13:31.479515');

INSERT INTO public.user_roles (user_id, role_id) VALUES ('0bce7c25-d9dc-41bd-9ff4-250fedcd0d2f', '2');
