package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Disciplina;
import br.unialfa.hackathon.repository.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public List<Disciplina> findAll() {
        return disciplinaRepository.findByAtivaTrue();
    }

    public Disciplina findById(Long id) {
        return disciplinaRepository.findById(id).orElse(null);
    }

    public Disciplina findByCodigo(String codigo) {
        return disciplinaRepository.findByCodigo(codigo).orElse(null);
    }

    public Disciplina save(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    public void deleteById(Long id) {
        Disciplina disciplina = findById(id);
        if (disciplina != null) {
            disciplina.setAtiva(false);
            save(disciplina);
        }
    }

    public List<Disciplina> findByNome(String nome) {
        return disciplinaRepository.findByNomeContainingIgnoreCase(nome);
    }

    public boolean existsByCodigo(String codigo) {
        return disciplinaRepository.findByCodigo(codigo).isPresent();
    }
}