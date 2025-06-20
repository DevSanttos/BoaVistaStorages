package com.ibdev.boavistastorage.service;

import com.ibdev.boavistastorage.entity.Atendente;
import com.ibdev.boavistastorage.repository.AtendenteRepository;
import jakarta.persistence.NoResultException;

public class AtendenteService {
    private AtendenteRepository atendenteRepository;

    public AtendenteService(AtendenteRepository atendenteRepository) {
        this.atendenteRepository = atendenteRepository;
    }

    public Atendente findByLoginAndSenha(String login, String senha) {
        try {
            Atendente atendente = atendenteRepository.findByLogin(login);
            if (atendente != null && atendente.getSenha().equals(senha)) {
                return atendente;
            } else {
                throw new RuntimeException("Login ou senha inválidos");
            }
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar a consulta por login e senha: " + e.getMessage());
        }
    }
}
