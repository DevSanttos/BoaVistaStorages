package com.ibdev.boavistastorage.controller;

import com.ibdev.boavistastorage.entity.EstoqueItem;
import com.ibdev.boavistastorage.entity.Insumo;
import com.ibdev.boavistastorage.entity.Produto;
import com.ibdev.boavistastorage.entity.StatusEstoque;
import com.ibdev.boavistastorage.entity.Vendavel;
import com.ibdev.boavistastorage.main.SceneManager;
import com.ibdev.boavistastorage.repository.InsumoRepository;
import com.ibdev.boavistastorage.repository.VendavelRepository;
import com.ibdev.boavistastorage.service.InsumoService;
import com.ibdev.boavistastorage.service.VendavelService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TelaEstoqueGerente implements Initializable {

    @FXML
    private Label btnVendaveis;

    @FXML
    private Label btnInsumos;


    @FXML
    private Label btnMenuCardapio;

    @FXML
    private Button btnAdicionar;

    @FXML
    private Label btnSair;

    @FXML
    private Label btnMenuInicio;

    @FXML
    private TableView<EstoqueItem> tabelaEstoque;

    @FXML
    private TableColumn<EstoqueItem, Long> colCodigo;

    @FXML
    private TableColumn<EstoqueItem, String> colProduto;

    @FXML
    private TableColumn<EstoqueItem, String> colQtde;

    @FXML
    private TableColumn<EstoqueItem, Double> colPreco;

    @FXML
    private TableColumn<EstoqueItem, StatusEstoque> colStatus;

    @FXML
    private TableColumn<EstoqueItem, Void> colAcoes;

    private ObservableList<EstoqueItem> listaItensEstoque = FXCollections.observableArrayList();

    private EntityManager entityManager;
    private InsumoService insumoService;
    private VendavelService vendavelService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Inicializando TelaEstoqueGerente");
        configurarTabela();
        configurarEventos();
        tabelaEstoque.setItems(listaItensEstoque);
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.insumoService = new InsumoService(new InsumoRepository(entityManager));
        this.vendavelService = new VendavelService(new VendavelRepository(entityManager));

        System.out.println("EntityManager setado na TelaEstoqueGerente. Tentando carregar dados...");
        Platform.runLater(this::carregarDadosEstoque);
    }

    private void configurarTabela() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProduto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQtde.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("precoCusto"));

        colPreco.setCellFactory(tc -> new TableCell<EstoqueItem, Double>() {
            @Override
            protected void updateItem(Double preco, boolean empty) {
                super.updateItem(preco, empty);
                if (empty || preco == null) {
                    setText(null);
                } else {
                    setText(String.format("R$ %.2f", preco));
                }
            }
        });

        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusEstoque"));
        colStatus.setCellFactory(tc -> new TableCell<EstoqueItem, StatusEstoque>() {
            @Override
            protected void updateItem(StatusEstoque status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.name());
                    switch (status) {
                        case BOM:
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case RAZOAVEL:
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case ZERADO:
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        Callback<TableColumn<EstoqueItem, Void>, TableCell<EstoqueItem, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<EstoqueItem, Void> call(final TableColumn<EstoqueItem, Void> param) {
                final TableCell<EstoqueItem, Void> cell = new TableCell<>() {

                    private final Button btnUpdate = new Button("Editar");
                    private final Button btnDelete = new Button("Excluir");

                    {
                        btnUpdate.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5;");
                        btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");

                        btnUpdate.setOnAction(event -> {
                            EstoqueItem item = getTableView().getItems().get(getIndex());
                            handleUpdateItem(item.getProdutoOriginal());
                        });

                        btnDelete.setOnAction(event -> {
                            EstoqueItem item = getTableView().getItems().get(getIndex());
                            handleDeleteItem(item.getProdutoOriginal());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, btnUpdate, btnDelete);
                            hbox.setAlignment(Pos.CENTER);
                            setGraphic(hbox);
                        }
                    }
                };
                return cell;
            }
        };
        colAcoes.setCellFactory(cellFactory);
    }

    private void carregarDadosEstoque() {
        if (entityManager == null || insumoService == null || vendavelService == null) {
            showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Serviços de persistência não configurados. Não é possível carregar dados.");
            return;
        }

        listaItensEstoque.clear();

        try {
            List<Vendavel> vendaveis = vendavelService.buscarTodosVendaveis();
            for (Vendavel vendavel : vendaveis) {
                listaItensEstoque.add(new EstoqueItem(vendavel));
            }
            System.out.println("Vendáveis carregados: " + vendaveis.size());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Vendáveis", "Não foi possível carregar dados de vendáveis: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            List<Insumo> insumos = insumoService.buscarTodosInsumos();
            for (Insumo insumo : insumos) {
                listaItensEstoque.add(new EstoqueItem(insumo));
            }
            System.out.println("Insumos carregados: " + insumos.size());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Insumos", "Não foi possível carregar dados de insumos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarEventos() {

        btnMenuCardapio.setOnMouseClicked(event -> {
            SceneManager.mudarCenaMaximizada(
                    "/com/ibdev/view/tela-cardapio.fxml", "Cardápio - Boa Vista Storage", entityManager
            );
        });

        btnSair.setOnMouseClicked(event -> {
            SceneManager.mudarCenaMaximizada(
                    "/com/ibdev/view/tela-login.fxml", "Login - Boa Vista Storage", entityManager
            );
        });

        btnInsumos.setOnMouseClicked(event -> {
            SceneManager.mudarCenaMaximizada(
                    "/com/ibdev/view/tela-crud-insumos.fxml", "Insumos - Boa Vista Storage", entityManager
            );
        });


        btnVendaveis.setOnMouseClicked(event -> {
            SceneManager.mudarCenaMaximizada(
                    "/com/ibdev/view/tela-crud-vendaveis.fxml", "Vendaveis - Boa Vista Storage", entityManager
            );
        });
        btnMenuInicio.setOnMouseClicked(event -> {
            SceneManager.mudarCenaMaximizada(
                    "/com/ibdev/view/tela-principal-gerente.fxml", "Tela Principal Gerente", entityManager
            );
        });

        btnAdicionar.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ibdev/view/tela-estoque-gerente-adicionarProduto.fxml"));
                Parent root = loader.load();

                TelaAdicionarEstoque adicionarController = loader.getController();
                adicionarController.setEntityManager(entityManager);

                adicionarController.setOnProdutoActionCompleted(novoProduto -> {
                    Platform.runLater(this::carregarDadosEstoque);
                });

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Adicionar Item ao Estoque");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(btnAdicionar.getScene().getWindow());
                stage.setResizable(false);
                stage.showAndWait();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro ao Abrir Tela", "Não foi possível carregar a tela de adição de estoque: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao preparar a tela de adição: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleUpdateItem(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ibdev/view/tela-estoque-gerente-adicionarProduto.fxml"));
            Parent root = loader.load();

            TelaAdicionarEstoque adicionarController = loader.getController();
            adicionarController.setEntityManager(entityManager);
            adicionarController.setProdutoParaEdicao(produto);

            adicionarController.setOnProdutoActionCompleted(produtoAtualizado -> {

                Platform.runLater(this::carregarDadosEstoque);
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Item do Estoque");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnAdicionar.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Abrir Tela", "Não foi possível carregar a tela de edição de estoque: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao preparar a tela de edição: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteItem(Produto produto) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Tem certeza que deseja excluir este item?");
        confirmAlert.setContentText("Item: " + produto.getNome() + " (ID: " + produto.getId() + ")");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    if (produto instanceof Vendavel) {
                        vendavelService.deletarVendavel(produto.getId());
                    } else if (produto instanceof Insumo) {
                        insumoService.deletarInsumo(produto.getId());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erro", "Tipo de produto desconhecido para exclusão.");
                        return;
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Item excluído com sucesso!");
                    carregarDadosEstoque();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro ao Excluir", "Não foi possível excluir o item: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}