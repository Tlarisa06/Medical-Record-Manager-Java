package medicalrecord;

import medicalrecord.domain.Pacient;
import medicalrecord.domain.Programare;
import medicalrecord.exceptions.ServiceException;
import medicalrecord.service.CabinetService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class HelloController {

    private CabinetService service;
    private final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    // Tab Pacienti
    @FXML private TableView<Pacient> pacientiTable;
    @FXML private TableColumn<Pacient, Integer> colIdPacient;
    @FXML private TableColumn<Pacient, String> colNumePacient;
    @FXML private TableColumn<Pacient, String> colPrenumePacient;
    @FXML private TableColumn<Pacient, Integer> colVarstaPacient;

    @FXML private TextField idPacientField;
    @FXML private TextField numePacientField;
    @FXML private TextField prenumePacientField;
    @FXML private TextField varstaPacientField;

    // Tab Programari
    @FXML private TableView<Programare> programariTable;
    @FXML private TableColumn<Programare, Integer> colIdProgramare;
    @FXML private TableColumn<Programare, Integer> colIdPacientProgramare;
    @FXML private TableColumn<Programare, String> colNumePacientProgramare;
    @FXML private TableColumn<Programare, String> colDataProgramare;
    @FXML private TableColumn<Programare, String> colScopProgramare;

    @FXML private TextField idProgramareField;
    @FXML private TextField idPacientProgramareField;
    @FXML private TextField dataProgramareField;
    @FXML private TextField scopProgramareField;

    // Tab Rapoarte
    @FXML private ListView<String> rapoarteListView;

    public void setService(CabinetService service) {
        this.service = service;
        refreshPacientiTable();
        refreshProgramariTable();
    }

    @FXML
    public void initialize() {
        //pacient
        colIdPacient.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        colNumePacient.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNume()));

        colPrenumePacient.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrenume()));

        colVarstaPacient.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getVarsta()).asObject());

        // programare

        colIdProgramare.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        colIdPacientProgramare.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPacient().getId()).asObject());

        colNumePacientProgramare.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getPacient().getNume() + " " + cellData.getValue().getPacient().getPrenume()));

        colDataProgramare.setCellValueFactory(cellData ->
                new SimpleStringProperty(SDF.format(cellData.getValue().getData())));

        colScopProgramare.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getScop()));


        // listeners
        pacientiTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populatePacientFields(newSelection);
                    } else {
                        clearPacientFields();
                    }
                });

        programariTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateProgramareFields(newSelection);
                    } else {
                        clearProgramareFields();
                    }
                });
    }

    @FXML
    private void handleAdaugaPacient() {
        try {
            String nume = numePacientField.getText();
            String prenume = prenumePacientField.getText();
            int varsta = Integer.parseInt(varstaPacientField.getText());

            service.adaugaPacient(nume, prenume, varsta);
            clearPacientFields();
            refreshPacientiTable();
        } catch (NumberFormatException e) {
            showAlert("Eroare de Intrare", "Vârsta trebuie să fie un număr întreg valid.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdatePacient() {
        try {
            int id = Integer.parseInt(idPacientField.getText());
            String nume = numePacientField.getText();
            String prenume = prenumePacientField.getText();
            int varsta = Integer.parseInt(varstaPacientField.getText());

            service.updatePacient(id, nume, prenume, varsta);
            clearPacientFields();
            refreshPacientiTable();
            refreshProgramariTable();
            showAlert("Succes", "Pacientul a fost actualizat.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Eroare de Intrare", "ID-ul și Vârsta trebuie să fie numere întregi valide.", Alert.AlertType.ERROR);
        } catch (ServiceException e) {
            showAlert("Eroare Serviciu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeletePacient() {
        try {
            int id = Integer.parseInt(idPacientField.getText());
            service.deletePacient(id);
            clearPacientFields();
            refreshPacientiTable();
            refreshProgramariTable();
            showAlert("Succes", "Pacientul a fost șters.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Eroare de Intrare", "ID-ul Pacientului trebuie să fie un număr întreg valid.", Alert.AlertType.ERROR);
        } catch (ServiceException e) {
            showAlert("Eroare Serviciu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshPacientiTable() {
        if (service != null) {
            ObservableList<Pacient> pacienti = FXCollections.observableList(service.getPacienti());
            pacientiTable.setItems(pacienti);
        }
    }

    private void clearPacientFields() {
        idPacientField.clear();
        numePacientField.clear();
        prenumePacientField.clear();
        varstaPacientField.clear();
    }

    private void populatePacientFields(Pacient p) {
        idPacientField.setText(String.valueOf(p.getId()));
        numePacientField.setText(p.getNume());
        prenumePacientField.setText(p.getPrenume());
        varstaPacientField.setText(String.valueOf(p.getVarsta()));
    }

    @FXML
    private void handleAdaugaProgramare() {
        try {
            int idPacient = Integer.parseInt(idPacientProgramareField.getText());
            String dataInput = dataProgramareField.getText();
            String scop = scopProgramareField.getText();

            Pacient pacient = findPacient(idPacient);
            if (pacient == null) return;

            Date data = parseDate(dataInput);
            if (data == null) return;

            service.adaugaProgramare(pacient, data, scop);
            clearProgramareFields();
            refreshProgramariTable();
            showAlert("Succes", "Programarea a fost adăugată.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Eroare de Intrare", "ID-ul Pacientului trebuie să fie un număr întreg valid.", Alert.AlertType.ERROR);
        } catch (ServiceException e) {
            showAlert("Eroare Serviciu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateProgramare() {
        try {
            int id = Integer.parseInt(idProgramareField.getText());
            int idPacient = Integer.parseInt(idPacientProgramareField.getText());
            String dataInput = dataProgramareField.getText();
            String scop = scopProgramareField.getText();

            Pacient pacient = findPacient(idPacient);
            if (pacient == null) return;

            Date data = parseDate(dataInput);
            if (data == null) return;

            service.updateProgramare(id, pacient, data, scop);
            clearProgramareFields();
            refreshProgramariTable();
            showAlert("Succes", "Programarea a fost actualizată.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Eroare de Intrare", "ID-urile trebuie să fie numere întregi valide.", Alert.AlertType.ERROR);
        } catch (ServiceException e) {
            showAlert("Eroare Serviciu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeletePorgramare() {
        try {
            int id = Integer.parseInt(idProgramareField.getText());
            service.deleteProgramare(id);
            clearProgramareFields();
            refreshProgramariTable();
            showAlert("Succes", "Programarea a fost ștearsă.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Eroare de Intrare", "ID-ul Programării trebuie să fie un număr întreg valid.", Alert.AlertType.ERROR);
        } catch (ServiceException e) {
            showAlert("Eroare Serviciu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshProgramariTable() {
        if (service != null) {
            ObservableList<Programare> programari = FXCollections.observableList(service.getProgramari());
            programariTable.setItems(programari);
        }
    }

    private void clearProgramareFields() {
        idProgramareField.clear();
        idPacientProgramareField.clear();
        dataProgramareField.clear();
        scopProgramareField.clear();
    }

    private void populateProgramareFields(Programare p) {
        idProgramareField.setText(String.valueOf(p.getId()));
        idPacientProgramareField.setText(String.valueOf(p.getPacient().getId()));
        dataProgramareField.setText(SDF.format(p.getData()));
        scopProgramareField.setText(p.getScop());
    }

    private Pacient findPacient(int idPacient) {
        Pacient pacient = service.getPacienti().stream()
                .filter(p -> p.getId() == idPacient)
                .findFirst()
                .orElse(null);

        if (pacient == null) {
            showAlert("Eroare Pacient", "Nu există niciun pacient cu ID-ul: " + idPacient, Alert.AlertType.WARNING);
        }
        return pacient;
    }

    private Date parseDate(String dataInput) {
        try {
            return SDF.parse(dataInput);
        } catch (ParseException e) {
            showAlert("Eroare Dată", "Format invalid pentru dată. Folosiți: dd.MM.yyyy HH:mm", Alert.AlertType.ERROR);
            return null;
        }
    }

    // rapoarte

    @FXML
    private void handleRaport1() {
        try {
            List<String> raport = service.getNumarProgramariPerPacient().stream()
                    .map(map -> String.format("Pacient: %s, Programări: %d", map.get("Pacient"), map.get("NumarProgramari")))
                    .collect(Collectors.toList());
            rapoarteListView.setItems(FXCollections.observableList(raport));
        } catch (Exception e) {
            showAlert("Eroare Raport 1", "Eroare la generarea raportului: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRaport2() {
        try {
            List<String> raport = service.getNumarProgramariPerLunaSortatDupaNumar().stream()
                    .map(map -> String.format("Luna: %s, Programări: %d", map.get("Luna"), map.get("NumarProgramari")))
                    .collect(Collectors.toList());
            rapoarteListView.setItems(FXCollections.observableList(raport));
        } catch (Exception e) {
            showAlert("Eroare Raport 2", "Eroare la generarea raportului: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRaport3() {
        try {
            List<String> raport = service.getZileTrecuteDeLaUltimaProgramare().stream()
                    .map(map -> String.format("Pacient: %s, Ultima programare: %s, Zile trecute: %d",
                            map.get("Pacient"),
                            SDF.format((Date) map.get("DataUltimaProgramare")),
                            map.get("ZileTrecute")))
                    .collect(Collectors.toList());
            rapoarteListView.setItems(FXCollections.observableList(raport));
        } catch (Exception e) {
            showAlert("Eroare Raport 3", "Eroare la generarea raportului: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRaport4() {
        // Raport 4 folosește aceeași logică ca Raport 2, deci putem apela metoda direct
        handleRaport2();
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}