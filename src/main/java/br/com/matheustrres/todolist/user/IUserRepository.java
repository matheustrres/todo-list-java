package br.com.matheustrres.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    public UserModel findByUsername(String username);
}
