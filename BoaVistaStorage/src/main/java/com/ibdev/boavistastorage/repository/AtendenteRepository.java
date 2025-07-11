package com.ibdev.boavistastorage.repository;

import com.ibdev.boavistastorage.entity.Atendente;
import com.ibdev.boavistastorage.entity.Atendente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;

public class AtendenteRepository {
    private EntityManager em;

    public AtendenteRepository(EntityManager em) {
        this.em = em;
    }

    public void create(Atendente atendente) {
        try {
            em.getTransaction().begin();
            em.persist(atendente);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Atendente já cadastrado com os mesmos dados.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar atendente: " + e.getMessage());
        }
    }

    public void readAll() {
        em.createQuery("select a from Atendente a", Atendente.class)
                .getResultList()
                .forEach(System.out::println);
    }

    public Atendente findById(Long id) {
        return em.find(Atendente.class, id);
    }

    public Atendente findByNome(String nome) {
        try {
            return em.createQuery("select a from Atendente a where a.nome = :nome", Atendente.class)
                    .setParameter("nome", nome)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Atendente findByTelefone(String telefone) {
        return em.createQuery("select a from Atendente a where a.telefone = :telefone", Atendente.class)
                .setParameter("telefone", telefone)
                .getSingleResult();
    }


    public Atendente findByCPF(String CPF) {
        return em.createQuery("select a from Atendente a where a.CPF = :cpf", Atendente.class)
                .setParameter("cpf", CPF)
                .getSingleResult();
    }

    public Atendente findByEndereco(String endereco) {
        return em.createQuery("select a from Atendente a where a.endereco = :endereco", Atendente.class)
                .setParameter("endereco", endereco)
                .getSingleResult();
    }

    public Atendente findByLogin(String login) {
        return em.createQuery("select a from Atendente a where a.login = :login", Atendente.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public Atendente findBySenha(String senha) {
        return em.createQuery("select a from Atendente a where a.senha = :senha", Atendente.class)
                .setParameter("senha", senha)
                .getSingleResult();
    }

    public Atendente findByLoginAndSenha(String login, String senha) {
        try {
            return em.createQuery("select a from Atendente a where a.login = :login and a.senha = :senha", Atendente.class)
                    .setParameter("login", login)
                    .setParameter("senha", senha)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar a consulta por login e senha: " + e.getMessage());
        }
    }

    public void update(Long idAtendente, Atendente atendente) {
        try {
            em.getTransaction().begin();

            Atendente atendenteDB = em.find(Atendente.class, idAtendente);

            if (atendenteDB != null) {
                atendenteDB.setNome(atendente.getNome());
                atendenteDB.setTelefone(atendente.getTelefone());
                atendenteDB.setCPF(atendente.getCPF());
                atendenteDB.setEndereco(atendente.getEndereco());
                atendenteDB.setLogin(atendente.getLogin());
                atendenteDB.setSenha(atendente.getSenha());
            } else {
                System.out.println("Atendente não encontrado!");
                throw new RuntimeException("Erro ao realizar a consulta por ID.");
            }
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao realizar a consulta por ID." + e.getMessage());
        }
    }

    public void delete(Long id) {
        try {
            em.getTransaction().begin();
            em.remove(em.find(Atendente.class, id));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao deletar o Atendente: " + e.getMessage());
        }
    }
}
