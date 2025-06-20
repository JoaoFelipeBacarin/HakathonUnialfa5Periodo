package br.unialfa.hackathon.service;


import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepo;

    public TurmaService(TurmaRepository turmaRepo) {
        this.turmaRepo = turmaRepo;
    }

    public List<Turma> listarTodas() {
        return turmaRepo.findAll();
    }
=======
public class TurmaService {
}
