//// src/main/java/br/unialfa/hackathon/service/ProvaService.java
//package br.unialfa.hackathon.service;
//
//import br.unialfa.hackathon.dto.ProvaResponse; // Importar o novo DTO
//import br.unialfa.hackathon.model.Disciplina;
//import br.unialfa.hackathon.model.Prova;
//import br.unialfa.hackathon.model.Turma;
//import br.unialfa.hackathon.repository.ProvaRepository;
//import br.unialfa.hackathon.repository.TurmaRepository; // Importar TurmaRepository
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ProvaService {
//
//    private final ProvaRepository repository;
//    private final TurmaRepository turmaRepository; // <--- INJETAR TurmaRepository
//
//    public Prova salvar(Prova prova) {
//        return repository.save(prova);
//    }
//
//    // Método existente
//    public List<Prova> listarPorTurma(Turma turma) {
//        return repository.findByTurma(turma);
//    }
//
//    // NOVO MÉTODO: Buscar provas por ID da turma e converter para DTO
//    public List<ProvaResponse> buscarProvasPorTurmaId(Long turmaId) {
//        // 1. Busca a Turma pelo ID
//        Turma turma = turmaRepository.findById(turmaId)
//                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + turmaId));
//
//        // 2. Busca as Provas associadas a essa Turma
//        List<Prova> provas = repository.findByTurma(turma);
//
//        // 3. Converte cada Prova para ProvaResponse DTO
//        return provas.stream()
//                .map(prova -> ProvaResponse.builder()
//                        .id(prova.getId())
//                        .nome(prova.getNome()) // Assumi que o 'nome' do JSON é 'nome' na entidade
//                        .disciplina(prova.getDisciplina()) // Assumi que 'disciplina' é String na entidade
//                        .turmaId(prova.getTurma().getId()) // Pega apenas o ID da turma
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//
//    public List<Prova> listarPorDisciplina(Disciplina disciplina) {
//        return repository.findByDisciplina(disciplina);
//    }
//
//    public List<Prova> listarPorData(LocalDate data) {
//        return repository.findByDataAplicacao(data);
//    }
//
//    public List<Prova> listarTodas() {
//        return repository.findAll();
//    }
//}