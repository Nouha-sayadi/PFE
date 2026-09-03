package com.example.st2i.Services;

import com.example.st2i.DTO.FactureViewModel;
import com.example.st2i.Entities.Client;
import com.example.st2i.Entities.Contrat;
import com.example.st2i.Entities.EcheanceFacturation;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Exception.PdfGenerationException;
import com.example.st2i.Repositories.EcheanceFacturationRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Génère un PDF de facture à partir des données d'une Échéance de Facturation existante. */
@Service
public class FactureGenerationService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EcheanceFacturationRepository echeanceRepository;
    private final TemplateEngine templateEngine;

    public FactureGenerationService(EcheanceFacturationRepository echeanceRepository, TemplateEngine templateEngine) {
        this.echeanceRepository = echeanceRepository;
        this.templateEngine = templateEngine;
    }

    public byte[] generate(Long echeanceId) {
        EcheanceFacturation echeance = echeanceRepository.findById(echeanceId)
                .orElseThrow(() -> new RuntimeException("Échéance de facturation non trouvée (id=" + echeanceId + ")."));

        FactureViewModel vm = buildViewModel(echeance);

        Context context = new Context();
        context.setVariable("vm", vm);
        String html = templateEngine.process("facture-echeance", context);

        return renderPdf(html);
    }

    public String buildFileName(Long echeanceId) {
        EcheanceFacturation echeance = echeanceRepository.findById(echeanceId)
                .orElseThrow(() -> new RuntimeException("Échéance de facturation non trouvée (id=" + echeanceId + ")."));
        Contrat contrat = echeance.getContrat();
        String numeroContrat = contrat != null && contrat.getNumeroContrat() != null
                ? sanitize(contrat.getNumeroContrat()) : "SANS-CONTRAT";
        return "facture_%s_echeance%d.pdf".formatted(numeroContrat, echeance.getNumero());
    }

    private FactureViewModel buildViewModel(EcheanceFacturation echeance) {
        Contrat contrat = echeance.getContrat();
        Projet projet = echeance.getProjet() != null ? echeance.getProjet() : (contrat != null ? contrat.getProjet() : null);
        Client client = projet != null ? projet.getClient() : null;

        String numeroContrat = contrat != null ? blankToNull(contrat.getNumeroContrat()) : null;
        String intituleProjet = projet != null ? blankToNull(projet.getTitre()) : null;
        String contratLine = numeroContrat != null && intituleProjet != null
                ? numeroContrat + " — " + intituleProjet
                : (numeroContrat != null ? numeroContrat : (intituleProjet != null ? intituleProjet : "—"));

        return FactureViewModel.builder()
                .numeroFacture(buildNumeroFacture(echeance, contrat))
                .dateGeneration(LocalDate.now().format(DATE_FMT))
                .clientNom(client != null && blankToNull(client.getNom()) != null ? client.getNom() : "Client non renseigné")
                .clientAdresse(client != null ? blankToNull(client.getAdresse()) : null)
                .clientMatriculeFiscale(client != null ? blankToNull(client.getMatriculeFiscale()) : null)
                .numeroContrat(contratLine)
                .objet(blankToNull(echeance.getObjet()) != null ? echeance.getObjet() : "—")
                .pourcentage(formatPourcentage(echeance.getPourcentage()))
                .montantPrevu(formatMontant(echeance.getMontantPrevu()))
                .montantFacture(formatMontant(echeance.getMontantFacture()))
                .dateInitiale(formatDate(echeance.getDateInitiale()))
                .datePrevue(formatDate(echeance.getDatePrevActualisee()))
                .dateReelle(formatDate(echeance.getDateReelle()))
                .statutEmise(Boolean.TRUE.equals(echeance.getEmise()) ? "Émise" : "Non émise")
                .conditionsPaiement(contrat != null ? blankToNull(contrat.getConditionsPaiement()) : null)
                .build();
    }

    private String buildNumeroFacture(EcheanceFacturation echeance, Contrat contrat) {
        String base = contrat != null && contrat.getNumeroContrat() != null
                ? sanitize(contrat.getNumeroContrat())
                : "PROJET" + (echeance.getProjet() != null ? echeance.getProjet().getId() : "X");
        int numero = echeance.getNumero() != null ? echeance.getNumero() : 0;
        return "FACT-%s-%02d".formatted(base, numero);
    }

    private String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9-]", "");
    }

    private String blankToNull(String value) {
        return (value != null && !value.isBlank()) ? value : null;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "—";
    }

    private String formatMontant(Double montant) {
        return montant != null ? String.format(Locale.FRANCE, "%,.2f DT", montant) : "—";
    }

    private String formatPourcentage(Double pourcentage) {
        return pourcentage != null ? String.format(Locale.FRANCE, "%.0f%%", pourcentage * 100) : "—";
    }

    private byte[] renderPdf(String html) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new PdfGenerationException("Échec de la génération du PDF de facture.", e);
        }
    }
}
