package com.ibdev.boavistastorage.service;

import com.ibdev.boavistastorage.entity.StatusEstoque;
import com.ibdev.boavistastorage.repository.InsumoRepository;
import com.ibdev.boavistastorage.entity.Insumo;

public class InsumoService {
    private InsumoRepository insumoRepository;

    public InsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    public boolean salvarInsumo(String nome, double precoCusto, String unidadeDeMedida, double quantidadeEstoque) {
        if(nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome do insumo não pode ser vazio.");
        }
        if(precoCusto < 0) {
            throw new IllegalArgumentException("Preço de custo não pode ser negativo.");
        }
        if(unidadeDeMedida == null || unidadeDeMedida.isEmpty()) {
            throw new IllegalArgumentException("Unidade de medida não pode ser vazia.");
        }
        if(quantidadeEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa.");
        }
        Insumo insumo = new Insumo(nome, precoCusto, unidadeDeMedida, quantidadeEstoque);
        return insumoRepository.create(insumo);
    }

    public Insumo buscarInsumoPorId(Long id) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("ID do insumo inválido.");
        }
        Insumo insumo = insumoRepository.findById(id);
        if(insumo == null) {
            throw new RuntimeException("Insumo não encontrado com o ID: " + id);
        }
        return insumo;
    }

    public Insumo buscarInsumoPorNome(String nome) {
        if(nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome do insumo não pode ser vazio.");
        }
        Insumo insumo = insumoRepository.findByName(nome);
        if(insumo == null) {
            throw new RuntimeException("Insumo não encontrado com o nome: " + nome);
        }
        return insumo;
    }
    public Insumo buscarInsumoPorPrecoCusto(double precoCusto) {
        if(precoCusto < 0) {
            throw new IllegalArgumentException("Preço de custo não pode ser negativo.");
        }
        Insumo insumo = insumoRepository.findByPrecoCusto(precoCusto);
        if(insumo == null) {
            throw new RuntimeException("Insumo não encontrado com o preço de custo: " + precoCusto);
        }
        return insumo;
    }

    public Insumo buscarInsumoPorQuantidadeEstoque(double quantidadeEstoque) {
        if(quantidadeEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa.");
        }
        Insumo insumo = insumoRepository.findByQuantidadeEstoque(quantidadeEstoque);
        if(insumo == null) {
            throw new RuntimeException("Insumo não encontrado com a quantidade em estoque: " + quantidadeEstoque);
        }
        return insumo;
    }

    public Insumo buscarInsumoPorStatusEstoque(StatusEstoque statusEstoque) {
        if(statusEstoque == null) {
            throw new IllegalArgumentException("Status de estoque não pode ser vazio.");
        }
        Insumo insumo = insumoRepository.findByStatusEstoque(statusEstoque);
        if(insumo == null) {
            throw new RuntimeException("Insumo não encontrado com o status de estoque: " + statusEstoque);
        }
        return insumo;
    }

    public boolean atualizarInsumo(Long id, Insumo insumoAtualizado) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("ID do insumo inválido.");
        }
        if(insumoAtualizado == null) {
            throw new IllegalArgumentException("Insumo atualizado não pode ser nulo.");
        }
        return insumoRepository.update(id, insumoAtualizado);
    }

    public boolean deletarInsumo(Long id) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("ID do insumo inválido.");
        }
        Insumo insumo = insumoRepository.findById(id);
        if(insumo == null) {
            throw new RuntimeException("Insumo não encontrado com o ID: " + id);
        }
        return insumoRepository.delete(id);
    }

    public void listarTodosInsumos() {
        insumoRepository.readAll();
    }

}
