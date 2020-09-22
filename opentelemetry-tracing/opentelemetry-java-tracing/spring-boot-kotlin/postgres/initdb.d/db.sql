drop role if exists test_user;
create role test_user with login password 'password';
grant all privileges on database test_db TO test_user;
